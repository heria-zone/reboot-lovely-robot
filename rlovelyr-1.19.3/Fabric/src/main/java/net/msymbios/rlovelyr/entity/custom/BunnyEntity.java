package net.msymbios.rlovelyr.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.entity.enums.*;
import net.msymbios.rlovelyr.entity.utils.InternalAnimation;
import net.msymbios.rlovelyr.entity.utils.ModMetrics;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;

import static net.msymbios.rlovelyr.entity.utils.ModUtils.*;

public class BunnyEntity extends InternalRobot implements GeoEntity {

    // -- Variables --
    private Identifier currentModel;
    private Identifier currentAnimator;
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    // -- Properties --
    public static DefaultAttributeContainer.Builder setAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, ModMetrics.getAttributeValue(RobotVariant.Bunny, RobotAttribute.MAX_HEALTH))
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, ModMetrics.getAttributeValue(RobotVariant.Bunny, RobotAttribute.ATTACK_DAMAGE))
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, ModMetrics.getAttributeValue(RobotVariant.Bunny, RobotAttribute.ATTACK_SPEED))
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, ModMetrics.getAttributeValue(RobotVariant.Bunny, RobotAttribute.MOVEMENT_SPEED))
                .add(EntityAttributes.GENERIC_ARMOR, ModMetrics.getAttributeValue(RobotVariant.Bunny, RobotAttribute.ARMOR))
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, ModMetrics.getAttributeValue(RobotVariant.Bunny, RobotAttribute.ARMOR_TOUGHNESS));
    } // setAttributes ()

    // -- MODEL --
    public Identifier getCurrentModel() {
        return currentModel;
    } // getCurrentTexture ()

    public void setCurrentModel(String value) {
        currentModel = ModMetrics.getModel(RobotVariant.Bunny, RobotModel.byName(value));
    } // setCurrentAnimator ()

    public void setCurrentModel(RobotModel value) {
        currentModel = ModMetrics.getModel(RobotVariant.Bunny, value);
    } // setCurrentAnimator ()

    // -- ANIMATOR --
    public Identifier getCurrentAnimator() {
        return currentAnimator;
    } // getCurrentAnimator ()

    public void setCurrentAnimator(String value) {
        currentAnimator = ModMetrics.ANIMATIONS.get(value);
    } // setCurrentAnimator ()

    public void setCurrentAnimator(RobotAnimation value) {
        currentAnimator = ModMetrics.ANIMATIONS.get(value.getName());
    } // setCurrentAnimator ()

    // TEXTURE
    @Override
    public Identifier getTextureByID(int value) { return ModMetrics.getTexture(RobotVariant.Bunny, RobotTexture.byId(value)); } // getTextureByID ()

    // VARIANT
    @Override
    public String getVariant() {
        return this.getVariant(RobotVariant.Bunny.getName());
    } // getVariant ()

    // STATS
    @Override
    public int getMaxLevel(){ return getMaxLevel ((int)ModMetrics.getAttributeValue(RobotVariant.Bunny, RobotAttribute.MAX_LEVEL)); } // getMaxLevel ()

    @Override
    public int getLevel(){
        var level = (int)(ModMetrics.getAttributeValue(RobotVariant.Bunny, RobotAttribute.MAX_LEVEL));
        if(level != getMaxLevel()) setMaxLevel(level);
        return super.getLevel();
    } // getLevel ()

    @Override
    public int getHpValue() {
        return getHpValue((int)ModMetrics.getAttributeValue(RobotVariant.Bunny, RobotAttribute.MAX_HEALTH));
    } // getHpValue ()

    @Override
    public int getAttackValue() {
        return getAttackValue((int)ModMetrics.getAttributeValue(RobotVariant.Bunny, RobotAttribute.ATTACK_DAMAGE));
    } // getAttackValue ()

    @Override
    public int getDefenseValue() {
        return getDefenseValue((int)ModMetrics.getAttributeValue(RobotVariant.Bunny, RobotAttribute.DEFENSE));
    } // getDefenseValue ()

    // -- Constructor --
    public BunnyEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        setCurrentModel(RobotModel.Unarmed);
        setCurrentAnimator(RobotAnimation.Locomotion);
    } // Constructor RobotEntity ()

    // -- Animations --
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(
                InternalAnimation.locomotionController(this),
                InternalAnimation.attackAnimation(this)
        );
    } // registerControllers ()

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    } // getAnimatableInstanceCache ()

    // -- Inherited Methods --
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setVariant(RobotVariant.Bunny.getName());
        this.setTexture(getRandomNumber(ModMetrics.getTextureCount(RobotVariant.Bunny)));
        this.setMaxLevel((int)ModMetrics.getAttributeValue(RobotVariant.Bunny, RobotAttribute.MAX_LEVEL));

        EquipmentSlot slot = EquipmentSlot.MAINHAND;
        ItemStack diamondSword = new ItemStack(Items.DIAMOND_SWORD);
        this.equipStack(slot, diamondSword);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    } // initialize ()

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(3, new MeleeAttackGoal(this, ModMetrics.MeleeAttackMovement, true));
        this.goalSelector.add(4, new FollowOwnerGoal(this, ModMetrics.FollowOwnerMovement, ModMetrics.FollowBehindDistance, ModMetrics.FollowCloseDistance, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, ModMetrics.WanderAroundMovement));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, ModMetrics.LookAtRange));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, (new RevengeGoal(this)).setGroupRevenge());
        this.targetSelector.add(4, new ActiveTargetGoal(this, MobEntity.class, 5, false, false, (entity) -> entity instanceof Monster && !(entity instanceof CreeperEntity) && !(entity instanceof InternalRobot)));
        this.targetSelector.add(5, new UniversalAngerGoal(this, true));
    } // initGoals ()

    @Override
    public void tick() {
        super.tick();
        handleModelTransition();
        handleAutoHeal();
    } // tick ()

    // -- Custom Methods --
    private void handleModelTransition () {
        if(this.isAttacking()) setCurrentModel(RobotModel.Armed);
        else setCurrentModel(RobotModel.Unarmed);
    } // handleModelTransition ()

    // -- Save Methods --
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, RobotVariant.Bunny.getName());
        this.dataTracker.startTracking(TEXTURE_ID, 0);

        this.dataTracker.startTracking(STATE, 0);
        this.dataTracker.startTracking(AUTO_ATTACK, false);

        this.dataTracker.startTracking(MAX_LEVEL, (int)ModMetrics.getAttributeValue(RobotVariant.Bunny, RobotAttribute.MAX_LEVEL));
        this.dataTracker.startTracking(LEVEL, 0);
        this.dataTracker.startTracking(EXP, 0);

        this.dataTracker.startTracking(FIRE_PROTECTION, 0);
        this.dataTracker.startTracking(FALL_PROTECTION, 0);
        this.dataTracker.startTracking(BLAST_PROTECTION, 0);
        this.dataTracker.startTracking(PROJECTILE_PROTECTION, 0);

        this.dataTracker.startTracking(BASE_X, 0F);
        this.dataTracker.startTracking(BASE_Y, 0F);
        this.dataTracker.startTracking(BASE_Z, 0F);
    } // initDataTracker ()

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("Variant", this.getVariant());
        nbt.putInt("TextureID", this.getTextureID());

        nbt.putInt("State", this.getCurrentStateID());
        nbt.putBoolean("AutoAttack", this.getAutoAttack());

        nbt.putInt("MaxLevel", this.getMaxLevel());
        nbt.putInt("Level", this.getLevel());
        nbt.putInt("Exp", this.getExp());

        nbt.putInt("FireProtection", this.getFireProtection());
        nbt.putInt("FallProtection", this.getFallProtection());
        nbt.putInt("BlastProtection", this.getBlastProtection());
        nbt.putInt("ProjectileProtection", this.getProjectileProtection());

        nbt.putFloat("BaseX", this.getBaseX());
        nbt.putFloat("BaseY", this.getBaseY());
        nbt.putFloat("BaseZ", this.getBaseZ());
    } // writeCustomDataToNbt ()

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setVariant(nbt.getString("Variant"));
        this.setTexture(nbt.getInt("TextureID"));

        this.setCurrentState(nbt.getInt("State"));
        this.setAutoAttack(nbt.getBoolean("AutoAttack"));

        this.setMaxLevel(nbt.getInt("MaxLevel"));
        this.setLevel(nbt.getInt("Level"));
        this.setExp(nbt.getInt("Exp"));

        this.setFireProtection(nbt.getInt("FireProtection"));
        this.setFallProtection(nbt.getInt("FallProtection"));
        this.setBlastProtection(nbt.getInt("BlastProtection"));
        this.setProjectileProtection(nbt.getInt("ProjectileProtection"));

        this.setBaseY(nbt.getFloat("BaseY"));
        this.setBaseZ(nbt.getFloat("BaseZ"));
        this.setBaseX(nbt.getFloat("BaseX"));
    } // readCustomDataFromNbt ()

} // Class BunnyEntity