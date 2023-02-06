package net.msymbios.rlovelyr.entity.custom;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.LovelyRobotMod;
import net.msymbios.rlovelyr.entity.utils.RobotMode;
import net.msymbios.rlovelyr.entity.utils.RobotVariant;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.HashMap;

public class Bunny2Entity extends TameableEntity implements VariantHolder<RobotVariant>, GeoEntity {

    // -- Variables --
    protected static final HashMap<Integer, Identifier> TEXTURES = new HashMap<>();
    protected static final HashMap<String, Identifier> MODELS = new HashMap<>();
    protected static final HashMap<String, Identifier> ANIMATIONS = new HashMap<>();

    protected static final TrackedData<Integer> VARIANT;
    protected static final TrackedData<Integer> MODE;
    protected static final TrackedData<Boolean> AUTO_ATTACK;

    protected static Identifier currentModel;
    protected static Identifier currentAnimator;

    public boolean isAutoAttackOn;
    public RobotMode currentMode;

    private AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);


    // -- Initialize --
    static {
        TEXTURES.put(0, new Identifier(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_00.png"));
        TEXTURES.put(6, new Identifier(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_01.png"));
        TEXTURES.put(8, new Identifier(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_02.png"));
        TEXTURES.put(7, new Identifier(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_03.png"));

        MODELS.put("normal", new Identifier(LovelyRobotMod.MODID, "geo/bunny2.geo.json"));
        //MODELS.put("normal", new Identifier(LovelyRobotMod.MODID, "geo/vanilla.geo.json"));

        ANIMATIONS.put("locomotion", new Identifier(LovelyRobotMod.MODID, "animations/lovelyrobot.animation.json"));

        VARIANT = DataTracker.registerData(Bunny2Entity.class, TrackedDataHandlerRegistry.INTEGER);
        MODE = DataTracker.registerData(Bunny2Entity.class, TrackedDataHandlerRegistry.INTEGER);
        AUTO_ATTACK = DataTracker.registerData(Bunny2Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }


    // -- Properties --
    public Identifier getCurrentModel() {
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

    public static Identifier getCurrentAnimator() {
        return currentAnimator;
    } // getCurrentAnimator ()

    public Identifier getCurrentTexture() {
        return getTexture(getEntityVariant());
    } // getCurrentTexture ()

    public static Identifier getTexture() {
        return getTexture(getRandomNumber(TEXTURES.size()));
    } // getTexture ()

    public static Identifier getTexture(int key) {
        if(TEXTURES.containsKey(key))
            return TEXTURES.get(key);
        return null;
    } // getTexture ()


    public void setTexture(ItemStack itemStack) {
        if(itemStack.isOf(Items.PINK_DYE)) setVariant(RobotVariant.PINK);
        if(itemStack.isOf(Items.BLUE_DYE)) setVariant(RobotVariant.BLUE);
        if(itemStack.isOf(Items.LIME_DYE)) setVariant(RobotVariant.LIME);
        if(itemStack.isOf(Items.ORANGE_DYE)) setVariant(RobotVariant.ORANGE);
    } // setTexture ()

    public void setEntityVariant(int variant) {
        this.dataTracker.set(VARIANT, variant);
    } // setVariant ()

    public int getEntityVariant() {
        return (Integer)this.dataTracker.get(VARIANT);
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
        dataTracker.set(MODE, currentMode.getId());
        return currentMode.getId();
    } // getMode ()

    public void setCurrentMode(RobotMode value){
        this.dataTracker.set(MODE, value.getId());
        currentMode = value;
    } // setCurrentMode ()

    public void setCurrentMode(int value){
        this.dataTracker.set(MODE, value);
        currentMode = RobotMode.byId(value);
    } // setCurrentMode ()

    public boolean getAutoAttack() {
        dataTracker.set(AUTO_ATTACK, isAutoAttackOn);
        return isAutoAttackOn;
    } // getAutoAttack ()

    public void setAutoAttack(boolean value) {
        this.dataTracker.set(AUTO_ATTACK, value);
        isAutoAttackOn = value;
    } // setAutoAttack ()


    // -- Constructor --
    public Bunny2Entity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        InitializeEntity();
    } // Constructor RobotEntity ()


    // -- Animations --
    private PlayState locomotionAnim(AnimationState animationState) {
        if(animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.lovelyrobot.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if(isSitting()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.lovelyrobot.sit", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        } else {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.lovelyrobot.idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
    } // locomotionAnim ()

    private PlayState attackAnim(AnimationState state) {
        if(this.handSwinging && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("animation.lovelyrobot.attack", Animation.LoopType.PLAY_ONCE));
            this.handSwinging = false;
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
    public static DefaultAttributeContainer.Builder setAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 2.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4f);
    } // setAttributes ()

    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setEntityVariant(0);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    } // initialize ()

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(3, new PounceAtTargetGoal(this, 0.4F));
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(5, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F, false));
        this.goalSelector.add(6, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(9, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, (new RevengeGoal(this, new Class[0])).setGroupRevenge(new Class[0]));
        this.targetSelector.add(4, new UniversalAngerGoal(this, true));
    } // initGoals ()

    @Nullable @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    } // createChild

    protected void InitializeEntity(){
        currentModel = MODELS.get("normal");
        currentAnimator = ANIMATIONS.get("locomotion");
        setCurrentModel();
    } // SetupEntity ()


    // -- Interact Methods --
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if(hand == Hand.MAIN_HAND) {
            setSittingState(itemStack);

            if (this.world.isClient) {
                return ActionResult.PASS;
            } else {
                setAutoAttackState(itemStack, player);
                setMode(itemStack, player);
                setTexture(itemStack);
                setCurrentModel();

                if(getOwner() == null){
                    this.setOwner(player);
                    player.sendMessage(Text.literal("Owner: " + getOwner().getEntityName()));
                }

                return ActionResult.SUCCESS;
            }
        }

        return super.interactMob(player, hand);
    } // interactMob ()

    public void setSittingState(ItemStack itemStack) {
        if(!canInteract(itemStack)) return;
        setSitting(invertBoolean(isSitting()));
    } // setSittingState ()

    public void setAutoAttackState(ItemStack itemStack, PlayerEntity player){
        if(itemStack.isOf(Items.WOODEN_SWORD) || itemStack.isOf(Items.STONE_SWORD) || itemStack.isOf(Items.IRON_SWORD) || itemStack.isOf(Items.GOLDEN_SWORD) || itemStack.isOf(Items.DIAMOND_SWORD) || itemStack.isOf(Items.NETHERITE_SWORD)) {
            setAutoAttack(invertBoolean(isAutoAttackOn));
            player.sendMessage(Text.literal("Auto Attack: " + this.isAutoAttackOn));
        }
    } // setAutoAttackState ()

    public void setMode(ItemStack itemStack, PlayerEntity player) {
        StandbyMode(itemStack);
        FollowMode(itemStack);
        GuardMode(itemStack);
    } // setMode

    public void StandbyMode(ItemStack itemStack){
        if(!canInteract(itemStack)) return;
        if(isSitting()) setCurrentMode(RobotMode.Standby);
    } // StandbyMode ()

    public void FollowMode(ItemStack itemStack){
        if(!canInteract(itemStack)) return;
        if(!isSitting()) setCurrentMode(RobotMode.Follow);
    } // FollowMode ()

    public void GuardMode(ItemStack itemStack){
        if(!itemStack.isOf(Items.COMPASS) && !itemStack.isOf(Items.RECOVERY_COMPASS)) return;
        setSitting(false);
        setCurrentMode(RobotMode.Guard);
    } // GuardMode ()

    private boolean canInteract(ItemStack itemStack){
        if(itemStack.isOf(Items.PINK_DYE) || itemStack.isOf(Items.YELLOW_DYE) || itemStack.isOf(Items.LIGHT_BLUE_DYE) || itemStack.isOf(Items.BLACK_DYE) || itemStack.isOf(Items.RED_DYE) || itemStack.isOf(Items.PURPLE_DYE) || itemStack.isOf(Items.BLUE_DYE) || itemStack.isOf(Items.LIME_DYE) || itemStack.isOf(Items.ORANGE_DYE)) return false;
        if(itemStack.isOf(Items.WOODEN_SWORD) || itemStack.isOf(Items.STONE_SWORD) || itemStack.isOf(Items.IRON_SWORD) || itemStack.isOf(Items.GOLDEN_SWORD) || itemStack.isOf(Items.DIAMOND_SWORD) || itemStack.isOf(Items.NETHERITE_SWORD)) return false;
        if(itemStack.isOf(Items.COMPASS) || itemStack.isOf(Items.RECOVERY_COMPASS)) return false;
        return true;
    } // canInteract ()


    // -- Save Methods --
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, 0);
        this.dataTracker.startTracking(MODE, 0);
        this.dataTracker.startTracking(AUTO_ATTACK, false);
    } // initDataTracker ()

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getEntityVariant());
        nbt.putInt("Mode", this.getCurrentMode());
        nbt.putBoolean("AutoAttack", this.getAutoAttack());
    } // writeCustomDataToNbt ()

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setEntityVariant(nbt.getInt("Variant"));
        this.setCurrentMode(nbt.getInt("Mode"));
        this.setAutoAttack(nbt.getBoolean("AutoAttack"));
    } // readCustomDataFromNbt ()

    // -- Sound Methods --
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_GENERIC_HURT;
    } // getHurtSound ()

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_GENERIC_DEATH;
    } // getDeathSound ()


    // -- Utilities Methods --
    public static int getRandomNumber(int number) {
        return Random.createLocal().nextInt(number);
    } // getRandomNumber ()

    public boolean invertBoolean(boolean value) {
        return value = !value;
    } // invertBoolean ()

} // Class RobotEntity