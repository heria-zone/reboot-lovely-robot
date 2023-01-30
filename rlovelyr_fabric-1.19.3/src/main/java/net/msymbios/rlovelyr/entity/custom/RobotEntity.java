package net.msymbios.rlovelyr.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.*;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.entity.varient.RobotVariant;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class RobotEntity extends TameableEntity implements VariantHolder<RobotVariant> {

    // -- Variables --
    protected static final HashMap<Integer, Identifier> TEXTURES = new HashMap<>();
    protected static final HashMap<String, Identifier> MODELS = new HashMap<>();
    protected static final HashMap<String, Identifier> ANIMATIONS = new HashMap<>();

    protected static final TrackedData<Integer> VARIANT;
    protected static final TrackedData<Boolean> SITTING;

    protected static Identifier currentModel;
    protected static Identifier currentAnimator;

    public boolean isAutoAttackOn;
    public RobotMode currentMode = RobotMode.Standby;

    public RobotMode debugMode = RobotMode.Standby;
    public boolean debugAutoAttackOn;

    // -- Initialize --
    static {
        VARIANT = DataTracker.registerData(RobotEntity.class, TrackedDataHandlerRegistry.INTEGER);
        SITTING = DataTracker.registerData(RobotEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }

    // -- Properties --
    public Identifier getCurrentTexture() {
        return getTexture(getEntityVariant());
    } // getCurrentTexture ()

    public static Identifier getCurrentModel() {
        return currentModel;
    } // getCurrentTexture ()

    public static Identifier getCurrentAnimator() {
        return currentAnimator;
    } // getCurrentAnimator ()

    // -- Constructor --
    protected RobotEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        InitializeEntity();
    } // Constructor RobotEntity ()

    // -- Appearance --
    public static Identifier getTexture() {
        return getTexture(getRandomNumber(TEXTURES.size()));
    } // getTexture ()

    public static Identifier getTexture(int key) {
        if(TEXTURES.containsKey(key))
            return TEXTURES.get(key);
        return null;
    } // getTexture ()

    public static Integer getTextureId() {
        return getRandomNumber(TEXTURES.size());
    } // getTextureId ()

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

    public void setColor(ItemStack itemStack) {
        if(itemStack.isOf(Items.PINK_DYE)) setVariant(RobotVariant.PINK);
        if(itemStack.isOf(Items.YELLOW_DYE)) setVariant(RobotVariant.YELLOW);
        if(itemStack.isOf(Items.LIGHT_BLUE_DYE)) setVariant(RobotVariant.LIGHT_BLUE);
        if(itemStack.isOf(Items.BLACK_DYE)) setVariant(RobotVariant.BLACK);
    } // setColor ()

    public void setModel() {
        if(currentMode == RobotMode.Follow) currentModel = MODELS.get("normal");
        if(currentMode == RobotMode.Guard) currentModel = MODELS.get("attack");
        if(currentMode == RobotMode.Standby) currentModel = MODELS.get("normal");
    } // setModel ()

    // -- Methods --
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setEntityVariant(getTextureId());
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

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (this.world.isClient) {
            if(hand == Hand.MAIN_HAND) {
                setSittingState(itemStack);
                setAutoAttackState(itemStack);

                StandbyMode(itemStack);
                FollowMode(itemStack);
                GuardMode(itemStack);

                setColor(itemStack);
                setModel();
            }
        }

        if (!this.world.isClient) {
            if(hand == Hand.MAIN_HAND) {
                setSittingState(itemStack);
                setAutoAttackState(itemStack);

                StandbyMode(itemStack);
                FollowMode(itemStack);
                GuardMode(itemStack);

                setColor(itemStack);
                setModel();


                if(getOwner() == null) {
                    this.setTamed(true);
                    setOwner(player);
                    player.sendMessage(Text.literal("Owner: " + getOwner().getEntityName()));
                    player.sendMessage(Text.literal("Tame: " + isTamed()));
                }

                if(debugMode != currentMode) {
                    player.sendMessage(Text.literal("Mode: " + this.currentMode.toString()));
                    debugMode = currentMode;
                }

                if(debugAutoAttackOn != isAutoAttackOn) {
                    player.sendMessage(Text.literal("Auto Attack: " + this.isAutoAttackOn));
                    debugAutoAttackOn = isAutoAttackOn;
                }
            }
        }

        return ActionResult.SUCCESS;
    } // interactMob ()

    @Nullable @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    } // createChild

    // -- Sound Methods --
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_DOLPHIN_AMBIENT;
    } // getAmbientSound ()

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_DOLPHIN_HURT;
    } // getHurtSound ()

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PIG_DEATH;
    } // getDeathSound ()

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_PIG_STEP, 0.15f, 1.0f);
    } // playStepSound ()

    // -- Initialize --
    protected void InitializeEntity(){
        currentModel = MODELS.get("normal");
        currentAnimator = ANIMATIONS.get("locomotion");
    } // SetupEntity ()

    // -- State --
    public void setSittingState(ItemStack itemStack) {
        if(!canInteract(itemStack)) return;
        boolean value = invertBoolean(isSitting());
        this.dataTracker.set(SITTING, this.isSitting());
        setSitting(value);
    } // setSittingState ()

    public void setAutoAttackState(ItemStack itemStack){
        if(itemStack.isOf(Items.WOODEN_SWORD) || itemStack.isOf(Items.STONE_SWORD) || itemStack.isOf(Items.IRON_SWORD) || itemStack.isOf(Items.GOLDEN_SWORD) || itemStack.isOf(Items.DIAMOND_SWORD) || itemStack.isOf(Items.NETHERITE_SWORD))
            isAutoAttackOn = invertBoolean(isAutoAttackOn);
    } // setAutoAttackState ()

    // -- Mode --
    public void StandbyMode(ItemStack itemStack){
        if(!canInteract(itemStack)) return;
        if(isSitting()) currentMode = RobotMode.Standby;
    } // StandbyMode ()

    public void FollowMode(ItemStack itemStack){
        if(!canInteract(itemStack)) return;
        if(!isSitting()) currentMode = RobotMode.Follow;
    } // FollowMode ()

    public void GuardMode(ItemStack itemStack){
        if(!itemStack.isOf(Items.COMPASS) && !itemStack.isOf(Items.RECOVERY_COMPASS)) return;
        setSitting(false);
        currentMode = RobotMode.Guard;
    } // GuardMode ()

    // -- Utilities --
    private boolean canInteract(ItemStack itemStack){
        if(itemStack.isOf(Items.PINK_DYE) || itemStack.isOf(Items.YELLOW_DYE) || itemStack.isOf(Items.LIGHT_BLUE_DYE) || itemStack.isOf(Items.BLACK_DYE)) return false;
        if(itemStack.isOf(Items.WOODEN_SWORD) || itemStack.isOf(Items.STONE_SWORD) || itemStack.isOf(Items.IRON_SWORD) || itemStack.isOf(Items.GOLDEN_SWORD) || itemStack.isOf(Items.DIAMOND_SWORD) || itemStack.isOf(Items.NETHERITE_SWORD)) return false;
        if(itemStack.isOf(Items.COMPASS) || itemStack.isOf(Items.RECOVERY_COMPASS)) return false;
        return true;
    } // canInteract ()

    public static int getRandomNumber(int number) {
        return Random.createLocal().nextInt(number);
    } // getRandomNumber ()

    public boolean invertBoolean(boolean value) {
        return value = !value;
    } // invertBoolean ()

    // -- Save --
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, 0);
        this.dataTracker.startTracking(SITTING, false);
    } // initDataTracker ()

    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("Variant", this.getEntityVariant());
        nbt.putBoolean("Sitting", this.isSitting());
        super.writeCustomDataToNbt(nbt);
    } // writeCustomDataToNbt ()

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setEntityVariant(nbt.getInt("Variant"));
        this.setSitting(nbt.getBoolean("Sitting"));
    } // readCustomDataFromNbt ()

    // -- Enum --
    public enum RobotMode {
        Follow,
        Guard,
        Standby
    } // Enum RobotMode

} // Class RobotEntity