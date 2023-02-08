package com.msymbios.entity.custom;

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
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.HashMap;

public class HoneyEntity extends TamableAnimal implements VariantHolder<RobotVariant>, GeoEntity {

    // -- Variables --
    protected static final HashMap<Integer, ResourceLocation> TEXTURES = new HashMap<>();
    protected static final HashMap<String, ResourceLocation> MODELS = new HashMap<>();
    protected static final HashMap<String, ResourceLocation> ANIMATIONS = new HashMap<>();

    protected static final EntityDataAccessor<Integer> VARIANT;
    protected static final EntityDataAccessor<Integer> MODE;
    protected static final EntityDataAccessor<Boolean> AUTO_ATTACK;

    protected static ResourceLocation currentModel;
    protected static ResourceLocation currentAnimator;

    public boolean isAutoAttackOn;
    public RobotMode currentMode;

    private AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    // -- Initialize --
    static {
        TEXTURES.put(1, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/honey/honey_00.png"));
        TEXTURES.put(2, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/honey/honey_01.png"));
        TEXTURES.put(0, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/honey/honey_02.png"));

        MODELS.put("normal", new ResourceLocation(LovelyRobotMod.MODID, "geo/honey.geo.json"));

        ANIMATIONS.put("locomotion", new ResourceLocation(LovelyRobotMod.MODID, "animations/lovelyrobot.animation.json"));

        VARIANT = SynchedEntityData.defineId(HoneyEntity.class, EntityDataSerializers.INT);
        MODE = SynchedEntityData.defineId(HoneyEntity.class, EntityDataSerializers.INT);
        AUTO_ATTACK = SynchedEntityData.defineId(HoneyEntity.class, EntityDataSerializers.BOOLEAN);
    }

    // -- Properties --
    public ResourceLocation getCurrentModel() {
        return currentModel;
    } // getCurrentTexture ()

    public void setCurrentModel() {
        if(currentMode == RobotMode.Follow) currentModel = MODELS.get("normal");
        if(currentMode == RobotMode.Guard) currentModel = MODELS.get("normal");
        if(currentMode == RobotMode.Standby) currentModel = MODELS.get("normal");
    } // setCurrentModel ()

    public void setModel(String model) {
        currentModel = MODELS.get(model);
    } // setCurrentModel ()

    public static ResourceLocation getCurrentAnimator() {
        return currentAnimator;
    } // getCurrentAnimator ()

    public ResourceLocation getCurrentTexture() {
        return getTexture(getEntityVariant());
    } // getCurrentTexture ()


    public static ResourceLocation getTexture(int key) {
        if(TEXTURES.containsKey(key))
            return TEXTURES.get(key);
        return null;
    } // getTexture ()

    public void setTexture(ItemStack itemStack) {
        if(itemStack.is(Items.PINK_DYE)) setVariant(RobotVariant.PINK);
        if(itemStack.is(Items.YELLOW_DYE)) setVariant(RobotVariant.YELLOW);
        if(itemStack.is(Items.LIGHT_BLUE_DYE)) setVariant(RobotVariant.LIGHT_BLUE);
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
    public RobotVariant getVariant() {
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

    // -- Constructors --
    public HoneyEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        InitializeEntity();
    } // Constructor RobotEntity ()

    // -- Animations --
    private PlayState locomotionAnim(AnimationState animationState) {
        if(animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.lovelyrobot.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if(isInSittingPose()) {
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
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.4f).build();
    } // setAttributes ()

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(7, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, AbstractSkeleton.class, false));
    } // registerGoals ()

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance instance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        this.setEntityVariant(0);
        return super.finalizeSpawn(levelAccessor, instance, mobSpawnType, spawnGroupData, compoundTag);
    } // finalizeSpawn ()

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    } // getBreedOffspring ()

    protected void InitializeEntity(){
        currentModel = MODELS.get("normal");
        currentAnimator = ANIMATIONS.get("locomotion");
        setCurrentModel();
    } // SetupEntity ()

    // -- Interact Methods --
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if(hand == InteractionHand.MAIN_HAND) {
            setSittingState(itemStack);

            if (this.level.isClientSide) {
                return InteractionResult.PASS;
            } else {
                setAutoAttackState(itemStack, player);
                setMode(itemStack, player);
                setTexture(itemStack);
                setCurrentModel();

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
        setInSittingPose(invertBoolean(isInSittingPose()));
    } // setSittingState ()

    public void setAutoAttackState(ItemStack itemStack, Player player){
        if(itemStack.is(Items.WOODEN_SWORD) || itemStack.is(Items.STONE_SWORD) || itemStack.is(Items.IRON_SWORD) || itemStack.is(Items.GOLDEN_SWORD) || itemStack.is(Items.DIAMOND_SWORD) || itemStack.is(Items.NETHERITE_SWORD)) {
            setAutoAttack(invertBoolean(isAutoAttackOn));
            player.displayClientMessage(Component.literal("Auto Attack: " + this.isAutoAttackOn), true);
        }
    } // setAutoAttackState ()

    public void setMode(ItemStack itemStack, Player player) {
        StandbyMode(itemStack);
        FollowMode(itemStack);
        GuardMode(itemStack);
    } // setMode

    public void StandbyMode(ItemStack itemStack){
        if(!canInteract(itemStack)) return;
        if(isInSittingPose()) setCurrentMode(RobotMode.Standby);
    } // StandbyMode ()

    public void FollowMode(ItemStack itemStack){
        if(!canInteract(itemStack)) return;
        if(!isInSittingPose()) setCurrentMode(RobotMode.Follow);
    } // FollowMode ()

    public void GuardMode(ItemStack itemStack){
        if(!itemStack.is(Items.COMPASS) && !itemStack.is(Items.RECOVERY_COMPASS)) return;
        setInSittingPose(false);
        setCurrentMode(RobotMode.Guard);
    } // GuardMode ()

    private boolean canInteract(ItemStack itemStack){
        if(itemStack.is(Items.PINK_DYE) || itemStack.is(Items.YELLOW_DYE) || itemStack.is(Items.LIGHT_BLUE_DYE) || itemStack.is(Items.BLACK_DYE) || itemStack.is(Items.RED_DYE) || itemStack.is(Items.PURPLE_DYE) || itemStack.is(Items.BLUE_DYE) || itemStack.is(Items.LIME_DYE) || itemStack.is(Items.ORANGE_DYE)) return false;
        if(itemStack.is(Items.WOODEN_SWORD) || itemStack.is(Items.STONE_SWORD) || itemStack.is(Items.IRON_SWORD) || itemStack.is(Items.GOLDEN_SWORD) || itemStack.is(Items.DIAMOND_SWORD) || itemStack.is(Items.NETHERITE_SWORD)) return false;
        if(itemStack.is(Items.COMPASS) || itemStack.is(Items.RECOVERY_COMPASS)) return false;
        return true;
    } // canInteract ()

    // -- Save Methods --
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, 0);
        this.entityData.define(MODE, 0);
        this.entityData.define(AUTO_ATTACK, false);
    } // defineSynchedData ()

    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("Variant", this.getEntityVariant());
        compoundTag.putInt("Mode", this.getCurrentMode());
        compoundTag.putBoolean("AutoAttack", this.getAutoAttack());
    } // addAdditionalSaveData ()

    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setEntityVariant(compoundTag.getInt("Variant"));
        this.setCurrentMode(compoundTag.getInt("Mode"));
        this.setAutoAttack(compoundTag.getBoolean("AutoAttack"));
    } // readAdditionalSaveData ()

    // -- Sound Methods --
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.GENERIC_HURT;
    } // getHurtSound ()

    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    } // getDeathSound ()

    // -- Utilities Methods --
    public boolean invertBoolean(boolean value) {
        return value = !value;
    } // invertBoolean ()

} // Class HoneyEntity