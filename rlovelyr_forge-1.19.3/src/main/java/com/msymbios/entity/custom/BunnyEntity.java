package com.msymbios.entity.custom;

import com.msymbios.entity.utils.ModMetrics;
import com.msymbios.entity.utils.RobotMode;
import com.msymbios.entity.utils.RobotVariant;
import com.msymbios.rlovelyr.LovelyRobotMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.HashMap;
import java.util.UUID;

import static com.msymbios.entity.utils.ModUtils.*;

public class BunnyEntity extends TamableAnimal implements NeutralMob, VariantHolder<RobotVariant>, GeoEntity {

    // -- Variables --
    private static final HashMap<Integer, ResourceLocation> TEXTURES = new HashMap<>();
    private static final HashMap<String, ResourceLocation> MODELS = new HashMap<>();
    private static final HashMap<String, ResourceLocation> ANIMATIONS = new HashMap<>();

    private static final EntityDataAccessor<Integer> VARIANT;
    private static final EntityDataAccessor<Integer> MODE;
    private static final EntityDataAccessor<Boolean> AUTO_ATTACK;

    private static ResourceLocation currentModel;
    private static ResourceLocation currentAnimator;

    public boolean isAutoAttackOn;
    public RobotMode currentMode;

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);


    // -- Initialize --
    static {
        TEXTURES.put(0, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny/bunny_00.png"));
        TEXTURES.put(1, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny/bunny_01.png"));
        TEXTURES.put(2, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny/bunny_02.png"));
        TEXTURES.put(4, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny/bunny_03.png"));
        TEXTURES.put(5, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny/bunny_04.png"));

        MODELS.put("unarmed", new ResourceLocation(LovelyRobotMod.MODID, "geo/bunny.geo.json"));
        ANIMATIONS.put("locomotion", new ResourceLocation(LovelyRobotMod.MODID, "animations/lovelyrobot.animation.json"));

        VARIANT = SynchedEntityData.defineId(HoneyEntity.class, EntityDataSerializers.INT);
        MODE = SynchedEntityData.defineId(HoneyEntity.class, EntityDataSerializers.INT);
        AUTO_ATTACK = SynchedEntityData.defineId(HoneyEntity.class, EntityDataSerializers.BOOLEAN);
    }


    // -- Properties --
    public ResourceLocation getCurrentModel() {
        return currentModel;
    } // getCurrentTexture ()

    public ResourceLocation getCurrentAnimator() {
        return currentAnimator;
    } // getCurrentAnimator ()

    public ResourceLocation getCurrentTexture() {
        return getTexture(getEntityVariant());
    } // getCurrentTexture ()

    public ResourceLocation getTexture(int key) {
        return TEXTURES.containsKey(key) ? TEXTURES.get(key) : getCurrentTexture();
    } // getTexture ()

    public void setTexture(ItemStack itemStack) {
        if(itemStack.is(Items.PINK_DYE)) setVariant(RobotVariant.PINK);
        if(itemStack.is(Items.YELLOW_DYE)) setVariant(RobotVariant.YELLOW);
        if(itemStack.is(Items.LIGHT_BLUE_DYE)) setVariant(RobotVariant.LIGHT_BLUE);
        if(itemStack.is(Items.PURPLE_DYE)) setVariant(RobotVariant.PURPLE);
        if(itemStack.is(Items.RED_DYE)) setVariant(RobotVariant.RED);
    } // setTexture ()

    public void setEntityVariant(int variant) {
        this.entityData.set(VARIANT, variant);
    } // setVariant ()

    public int getEntityVariant() {
        return this.entityData.get(VARIANT);
    } // getVariant ()

    @Override
    public void setVariant(RobotVariant variant) {
        setEntityVariant(variant.getId());
    } // setVariant ()

    @Override
    public @NotNull RobotVariant getVariant() {
        return RobotVariant.byId(getEntityVariant());
    } // getVariant ()

    public int getCurrentMode() {
        this.entityData.set(MODE, currentMode.getId());
        return currentMode.getId();
    } // getMode ()

    public void setCurrentMode(RobotMode value){
        this.entityData.set(MODE, value.getId());
        currentMode = value;
    } // setCurrentMode ()

    public void setCurrentMode(int value){
        this.entityData.set(MODE, value);
        currentMode = RobotMode.byId(value);
    } // setCurrentMode ()

    public boolean getAutoAttack() {
        this.entityData.set(AUTO_ATTACK, isAutoAttackOn);
        return isAutoAttackOn;
    } // getAutoAttack ()

    public void setAutoAttack(boolean value) {
        this.entityData.set(AUTO_ATTACK, value);
        isAutoAttackOn = value;
    } // setAutoAttack ()


    // -- Constructor --
    public BunnyEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        currentModel = MODELS.get("unarmed");
        currentAnimator = ANIMATIONS.get("locomotion");
    } // Constructor BunnyEntity ()


    // -- Animations --
    private PlayState locomotionAnim(AnimationState animationState) {
        if(animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.lovelyrobot.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if(isOrderedToSit()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.lovelyrobot.sit", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        } else {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.lovelyrobot.idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
    } // locomotionAnim ()

    private PlayState attackAnim(AnimationState state) {
        if(this.swinging && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("animation.lovelyrobot.attack", Animation.LoopType.PLAY_ONCE));
            this.swinging = false;
        }

        return PlayState.CONTINUE;
    } // attackAnim ()


    // -- Inheritance --
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController(this, "locomotionController", 0, this::locomotionAnim));
        controllerRegistrar.add(new AnimationController(this, "attackController", 0, this::attackAnim));
    } // registerControllers ()

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    } // getAnimatableInstanceCache ()


    // -- Methods --
    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, ModMetrics.BunnyBaseHp)
                .add(Attributes.ATTACK_DAMAGE, ModMetrics.BunnyBaseAttack)
                .add(Attributes.ATTACK_SPEED, ModMetrics.AttackMoveSpeed)
                .add(Attributes.MOVEMENT_SPEED, ModMetrics.BunnyMovementSpeed).build();
    } // setAttributes ()

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor levelAccessor, @NotNull DifficultyInstance instance, @NotNull MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        this.setEntityVariant(0);
        // this.setEntityVariant(getRandomNumber(TEXTURES.size()));
        return super.finalizeSpawn(levelAccessor, instance, mobSpawnType, spawnGroupData, compoundTag);
    } // finalizeSpawn ()

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(6, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, AbstractSkeleton.class, false));
        this.targetSelector.addGoal(7, new ResetUniversalAngerTargetGoal<>(this, true));
    } // registerGoals ()

    @Nullable @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {
        return null;
    } // getBreedOffspring ()


    // -- Interact Methods --
    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        var itemStack = player.getItemInHand(hand);

        if(hand == InteractionHand.MAIN_HAND) {
            setSittingState(itemStack);

            if (this.level.isClientSide) {
                return InteractionResult.PASS;
            } else {
                setAutoAttackState(itemStack, player);
                setMode(itemStack);
                setTexture(itemStack);

                if(getOwner() == null){
                    this.setOwnerUUID(player.getUUID());
                    player.displayClientMessage(Component.literal("Owner: " + getOwner().getName()), true);
                }

                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, hand);
    } // interactMob ()

    public void setSittingState(ItemStack itemStack) {
        if(!canInteract(itemStack)) return;
        setOrderedToSit(invertBoolean(isOrderedToSit()));
    } // setSittingState ()

    public void setAutoAttackState(ItemStack itemStack, Player player){
        if(!canInteractAutoAttack(itemStack)) return;
        setAutoAttack(invertBoolean(isAutoAttackOn));
        player.displayClientMessage(Component.literal("Auto Attack: " + this.isAutoAttackOn), true);
    } // setAutoAttackState ()

    public void setMode(ItemStack itemStack) {
        StandbyMode(itemStack);
        FollowMode(itemStack);
        GuardMode(itemStack);
    } // setMode

    public void StandbyMode(ItemStack itemStack){
        if(!canInteract(itemStack)) return;
        if(isOrderedToSit()) setCurrentMode(RobotMode.Standby);
    } // StandbyMode ()

    public void FollowMode(ItemStack itemStack){
        if(!canInteract(itemStack)) return;
        if(!isOrderedToSit()) setCurrentMode(RobotMode.Follow);
    } // FollowMode ()

    public void GuardMode(ItemStack itemStack){
        if(!canInteractGuardMode(itemStack)) return;
        setOrderedToSit(false);
        setCurrentMode(RobotMode.Guard);
    } // GuardMode ()


    // -- Save Methods --
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, 0);
        this.entityData.define(MODE, 0);
        this.entityData.define(AUTO_ATTACK, false);
    } // defineSynchedData ()

    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("Variant", this.getEntityVariant());
        compoundTag.putInt("Mode", this.getCurrentMode());
        compoundTag.putBoolean("AutoAttack", this.getAutoAttack());
    } // addAdditionalSaveData ()

    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setEntityVariant(compoundTag.getInt("Variant"));
        this.setCurrentMode(compoundTag.getInt("Mode"));
        this.setAutoAttack(compoundTag.getBoolean("AutoAttack"));
    } // readAdditionalSaveData ()


    // -- Sound Methods --
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundEvents.GENERIC_HURT;
    } // getHurtSound ()

    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    } // getDeathSound ()


    // -- Inherited --
    @Override
    public int getRemainingPersistentAngerTime() {
        return 0;
    }

    @Override
    public void setRemainingPersistentAngerTime(int p_21673_) {}

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return null;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID p_21672_) {}

    @Override
    public void startPersistentAngerTimer() {}

} // Class BunnyEntity