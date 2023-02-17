package com.msymbios.rlovelyr.entity.custom;

import com.msymbios.rlovelyr.entity.enums.RobotAnimation;
import com.msymbios.rlovelyr.entity.enums.RobotModel;
import com.msymbios.rlovelyr.entity.enums.RobotState;
import com.msymbios.rlovelyr.entity.enums.RobotTexture;
import com.msymbios.rlovelyr.LovelyRobotMod;
import com.msymbios.rlovelyr.entity.utils.ModMetrics;
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
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import java.util.HashMap;
import java.util.UUID;

import static com.msymbios.rlovelyr.entity.utils.ModUtils.*;

public class Bunny2Entity extends TamableAnimal implements NeutralMob, VariantHolder<RobotTexture>, GeoEntity {

    // -- Variables --
    private static final HashMap<RobotTexture, ResourceLocation> TEXTURES = new HashMap<>(){{
        put(RobotTexture.ORANGE, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_01.png")); // Orange
        put(RobotTexture.MAGENTA, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_02.png")); // Magenta
        put(RobotTexture.YELLOW, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_04.png")); // Yellow
        put(RobotTexture.LIME, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_05.png")); // Lime
        put(RobotTexture.PINK, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_06.png")); // Pink
        put(RobotTexture.LIGHT_BLUE, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_08.png")); // Light Blue
        put(RobotTexture.PURPLE, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_10.png")); // Purple
        put(RobotTexture.BLUE, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_11.png")); // Blue
        put(RobotTexture.RED, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_14.png")); // Red
        put(RobotTexture.BLACK, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_15.png")); // Black
    }};
    private static final HashMap<RobotModel, ResourceLocation> MODELS = new HashMap<>(){{
        put(RobotModel.Unarmed, new ResourceLocation(LovelyRobotMod.MODID, "geo/bunny2.geo.json"));
        put(RobotModel.Armed, new ResourceLocation(LovelyRobotMod.MODID, "geo/bunny2.attack.geo.json"));
    }};
    private static final HashMap<RobotAnimation, ResourceLocation> ANIMATIONS = new HashMap<>(){{
        put(RobotAnimation.Locomotion, new ResourceLocation(LovelyRobotMod.MODID, "animations/lovelyrobot.animation.json"));
    }};


    // private static final EntityDataAccessor<String> VARIANT = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> TEXTURE_ID = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> AUTO_ATTACK = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> MAX_LEVEL = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LEVEL = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> EXP = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> FIRE_PROTECTION = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FALL_PROTECTION = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BLAST_PROTECTION = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PROJECTILE_PROTECTION = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Float> BASE_X = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> BASE_Y = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> BASE_Z = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.FLOAT);

    private static ResourceLocation currentModel;
    private static ResourceLocation currentAnimator;

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);


    // -- Properties --
    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, ModMetrics.Bunny2BaseHp)
                .add(Attributes.ATTACK_DAMAGE, ModMetrics.Bunny2BaseAttack)
                .add(Attributes.ATTACK_SPEED, ModMetrics.AttackMoveSpeed)
                .add(Attributes.MOVEMENT_SPEED, ModMetrics.Bunny2MovementSpeed)
                .add(Attributes.ARMOR, 0F)
                .add(Attributes.ARMOR_TOUGHNESS, 0F).build();
    } // setAttributes ()


    // -- MODEL --
    public ResourceLocation getCurrentModel() {
        return currentModel;
    } // getCurrentModel ()

    public void setCurrentModel(String value) {
        currentModel = MODELS.get(RobotModel.byName(value));
    } // setCurrentModel ()

    public void setCurrentModel(RobotModel value) {
        currentModel = MODELS.get(value);
    } // setCurrentModel ()


    // -- ANIMATION --
    public ResourceLocation getCurrentAnimator() {
        return currentAnimator;
    } // getCurrentAnimator ()

    public void setCurrentAnimator(String value) {
        currentAnimator = ANIMATIONS.get(RobotAnimation.byName(value));
    } // setCurrentAnimator ()

    public void setCurrentAnimator(RobotAnimation value) {
        currentAnimator = ANIMATIONS.get(value);
    } // setCurrentAnimator ()


    // -- TEXTURE --
    public ResourceLocation getTextureByID(int key) {
        return TEXTURES.containsKey(RobotTexture.byId(key)) ? TEXTURES.get(RobotTexture.byId(key)) : getCurrentTexture();
    } // getTextureByID ()

    public ResourceLocation getCurrentTexture() {
        return getTextureByID(getEntityVariant());
    } // getCurrentTexture ()

    public void setCurrentTexture(ItemStack itemStack) {
        if(itemStack.is(Items.ORANGE_DYE)) setVariant(RobotTexture.ORANGE);
        if(itemStack.is(Items.MAGENTA_DYE)) setVariant(RobotTexture.MAGENTA);
        if(itemStack.is(Items.YELLOW_DYE)) setVariant(RobotTexture.YELLOW);
        if(itemStack.is(Items.LIME_DYE)) setVariant(RobotTexture.LIME);
        if(itemStack.is(Items.PINK_DYE)) setVariant(RobotTexture.PINK);
        if(itemStack.is(Items.LIGHT_BLUE_DYE)) setVariant(RobotTexture.LIGHT_BLUE);
        if(itemStack.is(Items.PURPLE_DYE)) setVariant(RobotTexture.PURPLE);
        if(itemStack.is(Items.BLUE_DYE)) setVariant(RobotTexture.BLUE);
        if(itemStack.is(Items.RED_DYE)) setVariant(RobotTexture.RED);
        if(itemStack.is(Items.BLACK_DYE)) setVariant(RobotTexture.BLACK);
    } // setTexture ()


    // -- VARIANT --
    public void setEntityVariant(int variant) {
        this.entityData.set(TEXTURE_ID, variant);
    } // setVariant ()

    public int getEntityVariant() {
        return this.entityData.get(TEXTURE_ID);
    } // getVariant ()

    @Override
    public void setVariant(RobotTexture variant) {
        setEntityVariant(variant.getId());
    } // setVariant ()

    @Override
    public @NotNull RobotTexture getVariant() {
        return RobotTexture.byId(getEntityVariant());
    } // getVariant ()


    // -- STATE --
    public int getCurrentState() {
        int value = 0;
        try {value = this.entityData.get(STATE);}
        catch (Exception ignored) {}
        return value;
    } // getCurrentState ()

    public void setCurrentState(RobotState value){
        this.entityData.set(STATE, value.getId());
    } // setCurrentMode ()

    public void setCurrentState(int value){
        this.entityData.set(STATE, value);
    } // setCurrentMode ()


    // -- AUTO ATTACK --
    public boolean getAutoAttack() {
        boolean value = false;
        try {value = this.entityData.get(AUTO_ATTACK);}
        catch (Exception ignored) {}
        return value;
    } // getAutoAttack ()

    public void setAutoAttack(boolean value) {
        this.entityData.set(AUTO_ATTACK, value);
    } // setAutoAttack ()


    // -- Constructor --
    public Bunny2Entity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        currentModel = MODELS.get(RobotModel.Unarmed);
        currentAnimator = ANIMATIONS.get(RobotAnimation.Locomotion);
    } // Constructor Bunny2Entity ()


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
    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor levelAccessor, @NotNull DifficultyInstance instance, @NotNull MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        this.setEntityVariant(getRandomNumber(TEXTURES.size()));
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
                setCurrentTexture(itemStack);

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
        setAutoAttack(invertBoolean(getAutoAttack()));
        player.displayClientMessage(Component.literal("Auto Attack: " + this.getAutoAttack()), true);
    } // setAutoAttackState ()

    public void setMode(ItemStack itemStack) {
        StandbyMode(itemStack);
        FollowMode(itemStack);
        GuardMode(itemStack);
    } // setMode

    public void StandbyMode(ItemStack itemStack){
        if(!canInteract(itemStack)) return;
        if(isOrderedToSit()) setCurrentState(RobotState.Standby);
    } // StandbyMode ()

    public void FollowMode(ItemStack itemStack){
        if(!canInteract(itemStack)) return;
        if(!isOrderedToSit()) setCurrentState(RobotState.Follow);
    } // FollowMode ()

    public void GuardMode(ItemStack itemStack){
        if(!canInteractGuardMode(itemStack)) return;
        setOrderedToSit(false);
        setCurrentState(RobotState.BaseDefense);
    } // GuardMode ()


    // -- Save Methods --
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TEXTURE_ID, 0);
        this.entityData.define(STATE, 0);
        this.entityData.define(AUTO_ATTACK, false);
    } // defineSynchedData ()

    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("Variant", this.getEntityVariant());
        compoundTag.putInt("Mode", this.getCurrentState());
        compoundTag.putBoolean("AutoAttack", this.getAutoAttack());
    } // addAdditionalSaveData ()

    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setEntityVariant(compoundTag.getInt("Variant"));
        this.setCurrentState(compoundTag.getInt("Mode"));
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

} // Class Bunny2Entity