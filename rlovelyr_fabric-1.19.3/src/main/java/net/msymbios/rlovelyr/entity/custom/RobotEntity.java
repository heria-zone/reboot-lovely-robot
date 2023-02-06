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
import net.msymbios.rlovelyr.entity.utils.RobotMode;
import net.msymbios.rlovelyr.entity.utils.RobotVariant;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class RobotEntity extends TameableEntity implements VariantHolder<RobotVariant> {

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


    // -- Initialize --
    static {
        VARIANT = DataTracker.registerData(RobotEntity.class, TrackedDataHandlerRegistry.INTEGER);
        MODE = DataTracker.registerData(RobotEntity.class, TrackedDataHandlerRegistry.INTEGER);
        AUTO_ATTACK = DataTracker.registerData(RobotEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }


    // -- Properties --
    public Identifier getCurrentModel() {
        return currentModel;
    } // getCurrentTexture ()

    public void setCurrentModel() {
        if(currentMode == RobotMode.Follow) currentModel = MODELS.get("normal");
        if(currentMode == RobotMode.Guard) currentModel = MODELS.get("attack");
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

    public static Integer getTextureId() {
        return getRandomNumber(TEXTURES.size());
    } // getTextureId ()

    public void setTexture(ItemStack itemStack) {} // setTexture ()

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
    protected RobotEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        InitializeEntity();
    } // Constructor RobotEntity ()


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
        // RobotMode debugMode = currentMode;

        StandbyMode(itemStack);
        FollowMode(itemStack);
        GuardMode(itemStack);

        // if(debugMode != currentMode) player.sendMessage(Text.literal("Mode: " + this.currentMode.toString()));
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
        if(itemStack.isOf(Items.PINK_DYE) || itemStack.isOf(Items.YELLOW_DYE) || itemStack.isOf(Items.LIGHT_BLUE_DYE) || itemStack.isOf(Items.BLACK_DYE)) return false;
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
    /*@Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_;
    } // getAmbientSound ()*/

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_GENERIC_HURT;
    } // getHurtSound ()

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_GENERIC_DEATH;
    } // getDeathSound ()

    /*@Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_PIG_STEP, 0.15f, 1.0f);
    } // playStepSound ()*/


    // -- Utilities Methods --
    public static int getRandomNumber(int number) {
        return Random.createLocal().nextInt(number);
    } // getRandomNumber ()

    public boolean invertBoolean(boolean value) {
        return value = !value;
    } // invertBoolean ()

} // Class RobotEntity