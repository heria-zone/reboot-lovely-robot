package net.msymbios.llovelyr.source.entity;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.world.EntityView;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.msymbios.llovelyr.common.entity.goal.*;
import net.msymbios.llovelyr.common.entity.internal.*;
import net.msymbios.llovelyr.common.utils.internal.Utility;
import net.msymbios.llovelyr.framework.entity.enums.EntityState;
import net.msymbios.llovelyr.framework.entity.enums.EntityTexture;
import net.msymbios.llovelyr.framework.utils.Version;
import net.msymbios.llovelyr.source.LovelyConfigs;
import net.msymbios.llovelyr.common.shared.LovelyIdentifier;
import net.msymbios.llovelyr.source.LovelyItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.Objects;

import static net.msymbios.llovelyr.common.utils.internal.Utility.invertBoolean;
import static net.msymbios.llovelyr.source.LovelyItems.ROBOT_CORE;

public abstract class LovelyRobot extends InternalEntity implements GeoEntity {

    // -- Variables --

    protected static final TrackedData<Boolean> AUTO_ATTACK = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    protected static final TrackedData<Integer> MAX_LEVEL = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> LEVEL = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> EXP = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.INTEGER);

    protected static final TrackedData<Integer> FIRE_PROTECTION = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> FALL_PROTECTION = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> BLAST_PROTECTION = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> PROJECTILE_PROTECTION = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.INTEGER);

    protected static final TrackedData<Float> BASE_X = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.FLOAT);
    protected static final TrackedData<Float> BASE_Y = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.FLOAT);
    protected static final TrackedData<Float> BASE_Z = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.FLOAT);

    protected static final TrackedData<Boolean> IS_IN_SITTING_POSE = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    protected static final TrackedData<Float> CURRENT_HEALTH = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.FLOAT);

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    protected boolean changeWeapon = false;
    
    // -- Standby Animation State (not persisted) --
    private int standbyTicks = 0;
    private int standbyTargetTicks = 0;

    // -- Properties --

    // AUTO ATTACK

    public boolean getAutoAttack() {
        boolean value = false;
        try {value = this.dataTracker.get(AUTO_ATTACK);}
        catch (Exception ignored) {}
        return value;
    } // getAutoAttack ()

    public void setAutoAttack(boolean value) {
        this.dataTracker.set(AUTO_ATTACK, value);
    } // setAutoAttack ()

    // STATS

    public int getMaxLevel() { return nativeEntity.getMaxLevel(); } // getMaxLevel ()

    public int getHp() { return InternalLogic.calculateHp(this.getCurrentLevel(), (int)this.nativeEntity.getMaxHealth()); } // getHp ()

    public int getAttackDamage() { return InternalLogic.calculateAttack(this.getCurrentLevel(), (int)nativeEntity.getAttackDamage()); } // getAttackDamage ()

    public int getArmorLevel() {
        var defence = InternalLogic.calculateDefense(this.getCurrentLevel(), (int)nativeEntity.getArmour());
        return (int) InternalLogic.calculateArmor(defence);
    } // getArmorLevel ()

    public int getArmorToughnessLevel() { return (int) InternalLogic.calculateArmorToughness(getArmorLevel()); } // getArmorToughnessLevel ()

    public int getLooting() {return InternalLogic.calculateLooting(this.getCurrentLevel());} // getLooting ()

    public int getCurrentLevel() {
        var level = 0;
        try {level = this.dataTracker.get(LEVEL);}
        catch (Exception ignored){}
        return level;
    } // getCurrentLevel ()

    public void setCurrentLevel(int value){
        this.dataTracker.set(LEVEL, value);
        InternalLogic.handleLevel(this, getHp(), getAttackDamage(), getArmorLevel(), getArmorToughnessLevel());
    } // setCurrentLevel ()

    public int getExp(){
        int value = 1;
        try {value = this.dataTracker.get(EXP);}
        catch (Exception ignored){}
        return value;
    } // getExp ()

    public void setExp(int value){
        this.dataTracker.set(EXP, value);
    } // setExp ()

    // PROTECTION

    public int getFireProtection() {
        int value = 0;
        try {value = this.dataTracker.get(FIRE_PROTECTION);}
        catch (Exception ignored) {}
        return value;
    } // getFireProtection ()

    public void setFireProtection(int value) {
        this.dataTracker.set(FIRE_PROTECTION, value);
    } // setFireProtection ()

    public int getFallProtection() {
        int retValue = 0;
        try {retValue = this.dataTracker.get(FALL_PROTECTION);}
        catch (Exception ignored) {}
        return retValue;
    } // getFallProtection ()

    public void setFallProtection(int value) {
        this.dataTracker.set(FALL_PROTECTION, value);
    } // setFallProtection ()

    public int getBlastProtection() {
        int value = 0;
        try {value = this.dataTracker.get(BLAST_PROTECTION);}
        catch (Exception ignored) {}
        return value;
    } // getBlastProtection ()

    public void setBlastProtection(int value) {
        this.dataTracker.set(BLAST_PROTECTION, value);
    } // setBlastProtection ()

    public int getProjectileProtection() {
        int value = 0;
        try {value = this.dataTracker.get(PROJECTILE_PROTECTION);}
        catch (Exception ignored) {}
        return value;
    } // getProjectileProtection ()

    public void setProjectileProtection(int value) {
        this.dataTracker.set(PROJECTILE_PROTECTION, value);
    } // setProjectileProtection ()

    // BASE

    public float getBaseX() {
        float value = this.getBlockX();
        try {value = this.dataTracker.get(BASE_X);}
        catch (Exception ignored) {}
        return value;
    } // getBaseX ()

    public void setBaseX(float value) {
        this.dataTracker.set(BASE_X, value);
    } // setBaseX ()

    public float getBaseY() {
        float value = this.getBlockY();
        try {value = this.dataTracker.get(BASE_Y);}
        catch (Exception ignored) {}
        return value;
    } // getBaseY ()

    public void setBaseY(float value) {
        this.dataTracker.set(BASE_Y, value);
    } // setBaseY ()

    public float getBaseZ() {
        float value = this.getBlockZ();
        try {value = this.dataTracker.get(BASE_Z);}
        catch (Exception ignored) {}
        return value;
    } // getBaseZ ()

    public void setBaseZ(float value) {
        this.dataTracker.set(BASE_Z, value);
    } // setBaseZ ()

    // -- Constructor --

    public LovelyRobot(EntityType<? extends InternalEntity> entityType, World world, NativeEntityType nativeEntityType) {
        super(entityType, world, nativeEntityType);
    } // Constructor LovelyRobot ()

    // -- Inherited Methods --

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegister) {
        controllerRegister.add(InternalAnimation.locomotionAnimation(this));
        controllerRegister.add(InternalAnimation.attackAnimation(this));
    } // registerControllers ()

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; } // getAnimatableInstanceCache ()

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(3, new MeleeAttackGoal(this, LovelyConfigs.Common.MovementMeleeAttack, true));
        this.goalSelector.add(4, new AiFollowOwnerGoal(this, LovelyConfigs.Common.MovementFollowOwner, LovelyConfigs.Common.FollowDistanceMin, LovelyConfigs.Common.FollowDistanceMax, false));
        this.goalSelector.add(4, new AiBaseDefenseGoal(this, LovelyConfigs.Common.MovementFollowOwner, LovelyConfigs.Common.BaseDefenceRange, LovelyConfigs.Common.BaseDefenceWarpRange));
        this.goalSelector.add(6, new AiConditionalWanderGoal(this, LovelyConfigs.Common.MovementWanderAround));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, LovelyConfigs.Common.LookRange));
        this.goalSelector.add(7, new LookAtEntityGoal(this, LivingEntity.class, LovelyConfigs.Common.LookRange));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new RevengeGoal(this));
        this.targetSelector.add(4, new AiAutoAttackGoal<>(this, MobEntity.class, LovelyConfigs.Common.AttackChance, true, false, InternalEntityType.AvoidAttackingEntities));
    } // initGoals ()

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (!changeWeapon) this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    } // initialize ()

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        if (Objects.requireNonNull(slot.getType()) == EquipmentSlot.Type.HAND) {
            final ItemStack tempSword = new ItemStack(Items.DIAMOND_SWORD, 1);
            final int lootingLevel = this.getLooting();
            if (lootingLevel > 0) tempSword.addEnchantment(Enchantment.byRawId(21), lootingLevel);
            return tempSword;
        }
        return super.getEquippedStack(slot);
    } // getEquippedStack ()

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        if (isInSittingPose()) {
            return EntityDimensions.changing(
                LovelyConfigs.EntityDimensions.SITTING_WIDTH,
                LovelyConfigs.EntityDimensions.SITTING_HEIGHT
            );
        }
        return EntityDimensions.changing(
            LovelyConfigs.EntityDimensions.DEFAULT_WIDTH,
            LovelyConfigs.EntityDimensions.DEFAULT_HEIGHT
        );
    } // getDimensions ()

    @Override
    public void tick() {
        super.tick();
        handleStandbyAnimation();
        handleCombatMode();
        handleAutoHeal();
        handleHealthSync();
        displayExtra();
    } // tick ()
    
    /**
     * Synchronizes current health to data tracker for proper persistence.
     * <p>
     * <b>Architecture:</b> Minecraft's health system doesn't automatically sync to
     * EntityData, causing health to reset on world reload. This method ensures
     * current health is stored in the data tracker every tick.
     * <p>
     * <b>Performance:</b> Only updates data tracker when health value changes,
     * minimizing network traffic.
     */
    private void handleHealthSync() {
        float currentHealth = this.getHealth();
        float storedHealth = getCurrentHealthValue();
        
        // Only update if health changed
        if (Math.abs(currentHealth - storedHealth) > 0.01F) {
            setCurrentHealthValue(currentHealth);
        }
    } // handleHealthSync ()
    
    /**
     * Manages standby animation transitions between REST and SIT poses.
     * <p>
     * <b>Architecture:</b> Tracks time spent stationary in standby mode. After
     * random delay (between min/max config values), transitions robot from REST
     * (standing idle) to SIT (sitting pose with smaller hitbox). Movement resets
     * timer and exits sitting.
     * <p>
     * <b>State Flow:</b>
     * - Enter Standby → REST animation, timer starts, random target set
     * - Timer reaches random threshold → SIT animation, hitbox shrinks
     * - Start moving → WALK animation, timer resets, hitbox restores
     * - Stop moving → REST animation, timer restarts with new random target
     * - Exit Standby → IDLE animation, timer resets, hitbox restores
     * <p>
     * <b>Performance:</b> Runs every tick but only performs calculations when in
     * standby mode. Hitbox refresh is called only on state transitions.
     */
    private void handleStandbyAnimation() {
        if (getCurrentState() == EntityState.Standby) {
            // Check movement with velocity for extra safety
            boolean isMoving = this.getVelocity().lengthSquared() > 0.0001;

            if (!isMoving) {
                // Set random target on first tick or when target is 0
                if (standbyTargetTicks == 0) {
                    standbyTargetTicks = LovelyConfigs.Common.StandbyToSitDelayMin + 
                        this.random.nextInt(LovelyConfigs.Common.StandbyToSitDelayMax - LovelyConfigs.Common.StandbyToSitDelayMin + 1);
                }
                
                // Stationary in standby - increment timer
                standbyTicks++;
                
                // Transition to sitting pose after random delay
                if (standbyTicks >= standbyTargetTicks && !isInSittingPose()) {
                    enterSittingPose();
                }
            } else {
                // Moving - exit sitting pose and reset timer
                if (isInSittingPose()) {
                    exitSittingPose();
                }
                standbyTicks = 0;
                standbyTargetTicks = 0;
            }
        } else {
            // Not in standby - reset everything
            if (isInSittingPose()) {
                exitSittingPose();
            }
            standbyTicks = 0;
            standbyTargetTicks = 0;
        }
    } // handleStandbyAnimation ()
    
    /**
     * Transitions robot into sitting pose with smaller hitbox.
     * <p>
     * <b>Architecture:</b> Sets sitting flag and triggers hitbox refresh. Animation
     * controller detects flag change and switches to SIT animation with 5-tick blend.
     * <p>
     * <b>Hitbox Change:</b> Shrinks from default (0.6 x 1.8) to sitting (0.6 x 0.9).
     * Prevents collision issues when robot is in low-profile sitting animation.
     */
    private void enterSittingPose() {
        setInSittingPose(true);
        calculateDimensions();
    } // enterSittingPose ()
    
    /**
     * Exits sitting pose and restores default hitbox.
     * <p>
     * <b>Architecture:</b> Clears sitting flag, resets timer, and triggers hitbox
     * refresh. Animation controller switches back to REST or WALK based on movement.
     * <p>
     * <b>Hitbox Restoration:</b> Returns to default dimensions (0.6 x 1.8).
     */
    private void exitSittingPose() {
        setInSittingPose(false);
        standbyTicks = 0;
        calculateDimensions();
    } // exitSittingPose ()
    
    /**
     * Checks if robot is currently in sitting pose.
     * <p>
     * <b>Usage:</b> Called by InternalAnimation.locomotionAnimation() to determine
     * which animation to play in standby mode.
     * 
     * @return true if robot is in sitting pose (smaller hitbox)
     */
    public boolean isInSittingPose() {
        boolean value = false;
        try {
            value = this.dataTracker.get(IS_IN_SITTING_POSE);
        } catch (Exception ignored) {}
        return value;
    } // isInSittingPose ()

    public void setInSittingPose(boolean value) {
        this.dataTracker.set(IS_IN_SITTING_POSE, value);
    } // setInSittingPose ()

    public float getCurrentHealthValue() {
        float value = 1.0F;
        try {
            value = this.dataTracker.get(CURRENT_HEALTH);
        } catch (Exception ignored) {}
        return value;
    } // getCurrentHealthValue ()

    public void setCurrentHealthValue(float value) {
        this.dataTracker.set(CURRENT_HEALTH, value);
        this.setHealth(value);
    } // setCurrentHealthValue ()

    // DATA

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(LEVEL, 0);
        this.dataTracker.startTracking(EXP, 0);
        this.dataTracker.startTracking(AUTO_ATTACK, true);

        this.dataTracker.startTracking(FIRE_PROTECTION, 0);
        this.dataTracker.startTracking(FALL_PROTECTION, 0);
        this.dataTracker.startTracking(BLAST_PROTECTION, 0);
        this.dataTracker.startTracking(PROJECTILE_PROTECTION, 0);

        this.dataTracker.startTracking(BASE_X, 0F);
        this.dataTracker.startTracking(BASE_Y, 0F);
        this.dataTracker.startTracking(BASE_Z, 0F);
        
        this.dataTracker.startTracking(IS_IN_SITTING_POSE, false);
        this.dataTracker.startTracking(CURRENT_HEALTH, 1.0F);
    } // initDataTracker ()

    @Override
    public void writeCustomDataToNbt(NbtCompound dataNBT) {
        dataNBT.putInt("Level", this.getCurrentLevel());
        dataNBT.putInt("Exp", this.getExp());
        dataNBT.putBoolean("AutoAttack", this.getAutoAttack());

        dataNBT.putInt("FireProtection", this.getFireProtection());
        dataNBT.putInt("FallProtection", this.getFallProtection());
        dataNBT.putInt("BlastProtection", this.getBlastProtection());
        dataNBT.putInt("ProjectileProtection", this.getProjectileProtection());

        dataNBT.putFloat("BaseX", this.getBaseX());
        dataNBT.putFloat("BaseY", this.getBaseY());
        dataNBT.putFloat("BaseZ", this.getBaseZ());
        
        dataNBT.putBoolean("IsInSittingPose", isInSittingPose());
        dataNBT.putFloat("CurrentHealth", this.getHealth());

        super.writeCustomDataToNbt(dataNBT);
    } // writeCustomDataToNbt ()

    @Override
    public void readCustomDataFromNbt(NbtCompound dataNBT) {
        this.setCurrentLevel(dataNBT.getInt("Level"));
        this.setExp(dataNBT.getInt("Exp"));
        this.setAutoAttack(dataNBT.getBoolean("AutoAttack"));

        this.setFireProtection(dataNBT.getInt("FireProtection"));
        this.setFallProtection(dataNBT.getInt("FallProtection"));
        this.setBlastProtection(dataNBT.getInt("BlastProtection"));
        this.setProjectileProtection(dataNBT.getInt("ProjectileProtection"));

        this.setBaseY(dataNBT.getFloat("BaseY"));
        this.setBaseZ(dataNBT.getFloat("BaseZ"));
        this.setBaseX(dataNBT.getFloat("BaseX"));
        
        setInSittingPose(dataNBT.getBoolean("IsInSittingPose"));
        if (!isInSittingPose()) enterSittingPose();
        
        // Restore health
        float savedHealth = dataNBT.getFloat("CurrentHealth");
        if (savedHealth > 0) {
            this.setHealth(savedHealth);
            setCurrentHealthValue(savedHealth);
        }
        
        // Refresh dimensions on next tick to ensure proper hitbox after world load
        if (isInSittingPose() && !this.getWorld().isClient) {
            // Schedule dimension refresh for next tick
            this.getWorld().getServer().execute(() -> {
                if (this.isAlive()) {
                    calculateDimensions();
                }
            });
        }
        
        super.readCustomDataFromNbt(dataNBT);
    } // readCustomDataFromNbt ()

    @Override
    public NbtCompound writeToNBT(NbtCompound dataNBT) {
        dataNBT = super.writeToNBT(dataNBT);
        dataNBT.putInt("Level", this.getCurrentLevel());
        dataNBT.putInt("Exp", this.getExp());
        dataNBT.putBoolean("AutoAttack", this.getAutoAttack());

        dataNBT.putInt("FireProtection", this.getFireProtection());
        dataNBT.putInt("FallProtection", this.getFallProtection());
        dataNBT.putInt("BlastProtection", this.getBlastProtection());
        dataNBT.putInt("ProjectileProtection", this.getProjectileProtection());

        dataNBT.putFloat("BaseX", this.getBaseX());
        dataNBT.putFloat("BaseY", this.getBaseY());
        dataNBT.putFloat("BaseZ", this.getBaseZ());
        
        dataNBT.putBoolean("IsInSittingPose", isInSittingPose());
        dataNBT.putFloat("CurrentHealth", this.getHealth());
        
        return dataNBT;
    } // writeToNBT

    @Override
    public void readFromNBT(NbtCompound dataNBT, Version version) {
        super.readFromNBT(dataNBT, version);
        this.setCurrentLevel(dataNBT.getInt("Level"));
        this.setExp(dataNBT.getInt("Exp"));
        this.setAutoAttack(dataNBT.getBoolean("AutoAttack"));

        this.setFireProtection(dataNBT.getInt("FireProtection"));
        this.setFallProtection(dataNBT.getInt("FallProtection"));
        this.setBlastProtection(dataNBT.getInt("BlastProtection"));
        this.setProjectileProtection(dataNBT.getInt("ProjectileProtection"));

        this.setBaseY(dataNBT.getFloat("BaseY"));
        this.setBaseZ(dataNBT.getFloat("BaseZ"));
        this.setBaseX(dataNBT.getFloat("BaseX"));
        
        setInSittingPose(dataNBT.getBoolean("IsInSittingPose"));
        
        // Restore health
        float savedHealth = dataNBT.getFloat("CurrentHealth");
        if (savedHealth > 0) {
            this.setHealth(savedHealth);
            setCurrentHealthValue(savedHealth);
        }
        
        // Refresh dimensions on next tick to ensure proper hitbox after world load
        if (isInSittingPose() && !this.getWorld().isClient) {
            // Schedule dimension refresh for next tick
            this.getWorld().getServer().execute(() -> {
                if (this.isAlive()) calculateDimensions();
            });
        }
    } // readFromNBT ()

    // HANDLERS

    @Override
    protected void handleAttackTarget(@NotNull Entity target) {
        handleActivateCombatMode();
        if(InternalLogic.handleLevelUp(this.getCurrentLevel(), this.getMaxLevel()) && !(target instanceof PlayerEntity) && !this.getWorld().isClient) {
            final int maxHp = (int)((LivingEntity)target).getMaxHealth();
            addExp(maxHp / 4);
        }
        this.getWorld().sendEntityStatus(this, (byte)4);
    } // handleAttackTarget ()

    @Override
    protected boolean handleDamage (DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) return false;
        if ((source.getAttacker() instanceof PlayerEntity player)) {
            if (this.isOwner(player) && !LovelyConfigs.Common.FriendlyFire)
                return false;
        }
        handleActivateCombatMode();

        if ((source.isOf(DamageTypes.ON_FIRE) || source.isOf(DamageTypes.IN_FIRE) || source.isOf(DamageTypes.LAVA)) && amount >= 1.0f && this.getFireProtection() > 0)
            amount *= (100.0f - this.getFireProtection()) / 100.0f;

        if (source.isOf(DamageTypes.FALL) && amount >= 1.0f && this.getFallProtection() > 0)
            amount *= (100.0f - this.getFallProtection()) / 100.0f;

        if (source.isOf(DamageTypes.EXPLOSION) && amount >= 1.0f && this.getBlastProtection() > 0)
            amount *= (100.0f - this.getBlastProtection()) / 100.0f;

        if (source.isOf(DamageTypes.ARROW) && amount >= 1.0f && this.getProjectileProtection() > 0)
            amount *= (100.0f - this.getProjectileProtection()) / 100.0f;

        if (amount < 1.0f) return false;

        if(!getWorld().isClient) {
            if((source.isOf(DamageTypes.ON_FIRE) || source.isOf(DamageTypes.IN_FIRE) || source.isOf(DamageTypes.LAVA)) && InternalLogic.handleFireProtectionLevelUp(getFireProtection())) this.setFireProtection(this.getFireProtection() + 1);
            if(source.isOf(DamageTypes.FALL) && InternalLogic.handleFallProtectionLevelUp(this.getFallProtection())) this.setFallProtection(this.getFallProtection() + 1);
            if(source.isOf(DamageTypes.EXPLOSION) && InternalLogic.handleBlastProtectionLevelUp(this.getBlastProtection())) this.setBlastProtection(this.getBlastProtection() + 1);
            if(source.isOf(DamageTypes.ARROW) && InternalLogic.handleProjectileProtectionLevelUp(this.getProjectileProtection())) this.setProjectileProtection(this.getProjectileProtection() + 1);
        }

        final Entity entity = source.getAttacker();

        if (InternalLogic.handleLevelUp(this.getCurrentLevel(), this.getMaxLevel()) && !(entity instanceof PlayerEntity) && entity instanceof LivingEntity && !this.getWorld().isClient) {
            final int maxHp = (int)((LivingEntity)entity).getMaxHealth();
            addExp(maxHp / 6);
        }

        return true;
    } // handleDamage ()

    @Override
    protected void handleItemDrop() {
        final ItemStack dropItem = setDropItem();
        NbtCompound nbt = dropItem.getNbt();
        if(nbt == null) nbt = new NbtCompound();

        String customName = Utility.getEntityCustomName(this);
        if (!customName.isEmpty()) nbt.putString(LovelyIdentifier.STAT_CUSTOM_NAME, customName);

        String ownerName = Utility.getEntityOwnerName(this);
        if (!ownerName.isEmpty()) nbt.putString(LovelyIdentifier.STAT_OWNER, ownerName);

        nbt.putString(LovelyIdentifier.STAT_TYPE, this.nativeEntity.key);
        nbt.putInt(LovelyIdentifier.STAT_COLOR, this.getTextureID());

        nbt.putInt(LovelyIdentifier.STAT_MAX_LEVEL, this.getMaxLevel());
        nbt.putInt(LovelyIdentifier.STAT_LEVEL, this.getCurrentLevel());
        nbt.putInt(LovelyIdentifier.STAT_EXP, this.getExp());

        nbt.putInt(LovelyIdentifier.STAT_FIRE_PROTECTION, this.getFireProtection());
        nbt.putInt(LovelyIdentifier.STAT_FALL_PROTECTION, this.getFallProtection());
        nbt.putInt(LovelyIdentifier.STAT_BLAST_PROTECTION, this.getBlastProtection());
        nbt.putInt(LovelyIdentifier.STAT_PROJECTILE_PROTECTION, this.getProjectileProtection());

        dropItem.setNbt(nbt);
        //if (!customName.isEmpty()) dropItem.setCustomName(Text.literal(customName).copy().append(Utility.getRandomTitle()).formatted(Formatting.DARK_PURPLE)); // TODO: I wonder what to do!
        
        // Create ItemEntity manually to apply glowing effect
        net.minecraft.entity.ItemEntity itemEntity = new net.minecraft.entity.ItemEntity(
            this.getWorld(), 
            this.getX(), 
            this.getY() + 0.0F, 
            this.getZ(), 
            dropItem
        );
        itemEntity.setToDefaultPickupDelay();
        
        // Apply glowing effect to make core visible through walls
        itemEntity.setGlowing(true);
        
        // Apply custom glow color based on robot variant
        applyGlowColor(itemEntity, this.getTextureID());
        
        this.getWorld().spawnEntity(itemEntity);
    } // handleDropItems ()

    /**
     * Applies custom glow color to dropped core based on robot texture variant.
     * <p>
     * <b>Architecture:</b> Uses Minecraft's scoreboard team system to assign glow colors.
     * Each texture variant gets a dedicated team with matching color.
     * <p>
     * <b>Performance:</b> Teams are created once and reused. Lookup is O(1) hash map access.
     * <p>
     * <b>Fallback:</b> If team creation fails, entity retains default white glow.
     * 
     * @param itemEntity the dropped core item entity
     * @param textureId the robot's texture variant ID
     */
    private void applyGlowColor(net.minecraft.entity.ItemEntity itemEntity, int textureId) {
        try {
            net.minecraft.scoreboard.Scoreboard scoreboard = this.getWorld().getScoreboard();
            String teamName = "robot_core_" + textureId;
            
            net.minecraft.scoreboard.Team team = scoreboard.getTeam(teamName);
            if (team == null) {
                team = scoreboard.addTeam(teamName);
                team.setColor(getColorForTexture(textureId));
            }
            
            scoreboard.addPlayerToTeam(itemEntity.getUuidAsString(), team);
        } catch (Exception e) {
            // Fallback to default glow if team creation fails
            // Entity will still glow, just with default white color
        }
    } // applyGlowColor ()

    /**
     * Maps robot texture variant to Minecraft formatting color.
     * <p>
     * <b>Design Decision:</b> Uses Formatting enum for color consistency with
     * Minecraft's existing color system. Provides 16 distinct colors matching dye palette.
     * 
     * @param textureId the robot's texture variant ID
     * @return corresponding Formatting color
     */
    private Formatting getColorForTexture(int textureId) {
        EntityTexture texture = EntityTexture.byId(textureId);
        return switch (texture) {
            case WHITE -> Formatting.WHITE;
            case ORANGE -> Formatting.GOLD;
            case MAGENTA -> Formatting.LIGHT_PURPLE;
            case LIGHT_BLUE -> Formatting.AQUA;
            case YELLOW -> Formatting.YELLOW;
            case LIME -> Formatting.GREEN;
            case PINK -> Formatting.LIGHT_PURPLE;
            case GRAY -> Formatting.DARK_GRAY;
            case LIGHT_GRAY -> Formatting.GRAY;
            case CYAN -> Formatting.DARK_AQUA;
            case PURPLE -> Formatting.DARK_PURPLE;
            case BLUE -> Formatting.BLUE;
            case BROWN -> Formatting.GOLD;
            case GREEN -> Formatting.DARK_GREEN;
            case RED -> Formatting.RED;
            case BLACK -> Formatting.BLACK;
            default -> Formatting.WHITE;
        };
    } // getColorForTexture ()

    @Override
    public ItemStack setDropItem() {
        return new ItemStack(ROBOT_CORE, 1);
    } // setDropItem ()

    protected ActionResult handleItemInteraction (ItemStack stack, PlayerEntity player) {
        if(handleTexture(stack, player)) return ActionResult.SUCCESS;
        return ActionResult.SUCCESS;
    } // handleItemInteraction ()

    @Override
    protected boolean canInteractWithItems(ItemStack stack) {
        if(stack.getItem() instanceof DyeItem) return false;
        if(stack.getItem() instanceof SwordItem) return false;
        if(stack.isOf(Items.BOOK) || stack.isOf(Items.WRITABLE_BOOK) || stack.isOf(Items.OAK_BUTTON)) return false;
        return !stack.isOf(Items.COMPASS) && !stack.isOf(Items.RECOVERY_COMPASS);
    } // canInteractWithItems ()

    @Override
    protected void handleInteract (ItemStack stack, PlayerEntity player) {
        super.handleInteract(stack, player);
        handlePickupRetrieval(stack, player);
        handleAutoAttack(stack);
        handleDisplayInteraction(stack);
    } // handleInteract ()

    @Override
    protected void handleState(ItemStack stack) {
        if (handleStandbyState(stack)) return;
        if (handleFollowState(stack)) return;
        if (handleBaseDefenseState(stack)) return;
    } // handleState ()

    @Override
    protected boolean handleTexture(ItemStack stack, PlayerEntity player) {
        var oldTexture = getTextureID();
        if(stack.isOf(Items.WHITE_DYE)) setTexture(EntityTexture.WHITE);
        if(stack.isOf(Items.ORANGE_DYE)) setTexture(EntityTexture.ORANGE);
        if(stack.isOf(Items.MAGENTA_DYE)) setTexture(EntityTexture.MAGENTA);
        if(stack.isOf(Items.LIGHT_BLUE_DYE)) setTexture(EntityTexture.LIGHT_BLUE);
        if(stack.isOf(Items.YELLOW_DYE)) setTexture(EntityTexture.YELLOW);
        if(stack.isOf(Items.LIME_DYE)) setTexture(EntityTexture.LIME);
        if(stack.isOf(Items.PINK_DYE)) setTexture(EntityTexture.PINK);
        if(stack.isOf(Items.GRAY_DYE)) setTexture(EntityTexture.GRAY);
        if(stack.isOf(Items.LIGHT_GRAY_DYE)) setTexture(EntityTexture.LIGHT_GRAY);
        if(stack.isOf(Items.CYAN_DYE)) setTexture(EntityTexture.CYAN);
        if(stack.isOf(Items.PURPLE_DYE)) setTexture(EntityTexture.PURPLE);
        if(stack.isOf(Items.BLUE_DYE)) setTexture(EntityTexture.BLUE);
        if(stack.isOf(Items.BROWN_DYE)) setTexture(EntityTexture.BROWN);
        if(stack.isOf(Items.GREEN_DYE)) setTexture(EntityTexture.GREEN);
        if(stack.isOf(Items.RED_DYE)) setTexture(EntityTexture.RED);
        if(stack.isOf(Items.BLACK_DYE)) setTexture(EntityTexture.BLACK);

        if(oldTexture != getTextureID()) {
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
                return true;
            }
        }
        return false;
    } // handleTexture ()

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    } // method_48926 ()

    // -- Custom Methods --

    /**
     * Adds experience points and handles level-up logic.
     * <p>
     * <b>Custom Name Bonus:</b> Named robots receive 1.5x experience multiplier
     * to reward player investment in personalization.
     * <p>
     * <b>Level-Up:</b> Automatically levels up when experience threshold reached.
     * Updates stats and displays notification to owner.
     * <p>
     * <b>Safety:</b> Validates input, checks max level cap, and ensures owner
     * exists before displaying notifications.
     *
     * @param value experience points to add
     */
    public void addExp(int value) {
        if (value <= 0) return;

        int addExp = value;
        int currentExp = getExp();
        int currentLevel = this.getCurrentLevel();
        int maxLevel = this.getMaxLevel();
        String customName = "";

        // Get custom name safely
        try {
            customName = getCustomName().getString();
        } catch (Exception ignored) {}

        // Named robots earn 1.5x experience bonus
        if (!customName.isEmpty()) {
            addExp = addExp * 3 / 2;
        }

        currentExp += addExp;

        // Store old level for change detection
        var oldLevel = currentLevel;

        // Level up loop with max level cap
        while (currentLevel < maxLevel && currentExp >= InternalLogic.calculateNextExp(currentLevel)) {
            currentExp -= InternalLogic.calculateNextExp(currentLevel);
            currentLevel++;
            setCurrentLevel(currentLevel);
        }

        setExp(currentExp);

        // Display level-up notification only if level actually changed
        if (oldLevel != currentLevel) {
            if (!getWorld().isClient) {
                try {
                    final LivingEntity owner = getOwner();
                    if (owner != null) {
                        displayGeneralMessage(getNotification(), true);
                    }
                } catch (Exception ignored) {}
                
                // Spawn level-up particle effects (villager trade refresh particles)
                InternalParticle.HappyVillager(this);
                
                // Play level-up sound effect (player experience level-up sound) - positional at robot location
                this.getWorld().playSound(
                    null, 
                    this.getBlockPos(), 
                    net.minecraft.sound.SoundEvents.ENTITY_PLAYER_LEVELUP, 
                    net.minecraft.sound.SoundCategory.NEUTRAL, 
                    1.0F, 
                    1.0F
                );
            }
        }
    } // addExp ()

    private boolean canInteractAutoAttack(ItemStack stack) {
        return stack.getItem() instanceof SwordItem;
    } // canInteractAutoAttack ()

    private boolean canInteractGuardMode(ItemStack stack) {
        return stack.isOf(Items.COMPASS) || stack.isOf(Items.RECOVERY_COMPASS);
    } // canInteractGuardMode ()

    protected boolean handleDisplayInteraction (ItemStack stack) {
        if(stack.getItem() == (Items.OAK_BUTTON)) {
            this.setNotification(invertBoolean(getNotification()));
            if(getNotification()) displayNotification(LovelyIdentifier.MSG_NOTIFICATION, LovelyIdentifier.MSG_ON, getNotification());
            else displayNotification(LovelyIdentifier.MSG_NOTIFICATION, LovelyIdentifier.MSG_OFF, true);
        }

        if(stack.getItem() == (Items.BOOK)) displayGeneralMessage(true, false);
        if(stack.getItem() == (Items.WRITABLE_BOOK)) displayEnchantmentMessage();
        return true;
    } // handleDisplayInteraction ()

    protected void handleAutoAttack(ItemStack stack){
        if (!canInteractAutoAttack(stack)) return;
        if (getCurrentState() == EntityState.Defense) return;
        setAutoAttack(invertBoolean(getAutoAttack()));

        if(getAutoAttack()) displayNotification(LovelyIdentifier.MSG_AUTO_ATTACK, LovelyIdentifier.MSG_ON, getNotification());
        else displayNotification(LovelyIdentifier.MSG_AUTO_ATTACK, LovelyIdentifier.MSG_OFF, getNotification());
    } // handleAutoAttack ()

    protected boolean handleBaseDefenseState(ItemStack stack){
        if(!canInteractGuardMode(stack) || getCurrentState() == EntityState.Defense) return false;
        setCurrentState(EntityState.Defense);
        setSitting(false);
        setAutoAttack(true);
        this.setBaseX((float)this.getBlockX());
        this.setBaseY((float)this.getBlockY());
        this.setBaseZ((float)this.getBlockZ());
        displayNotification(LovelyIdentifier.MSG_BASE_DEFENCE, getNotification());
        return true;
    } // handleBaseDefenseState ()

    /**
     * Handles robot death and core dropping with smart retrieval logic.
     * <p>
     * <b>Architecture:</b> Overrides parent dropEquipment() to intercept core drop
     * and implement distance-based auto-retrieval. Falls back to normal drop with
     * glow effect when retrieval conditions aren't met.
     * <p>
     * <b>Smart Retrieval:</b> When enabled, checks owner distance and attempts to
     * add core directly to owner inventory. Provides feedback via particles, sound,
     * and chat messages.
     * <p>
     * <b>Fallback Logic:</b> Drops core with glow effect when: config disabled,
     * owner offline, owner too far, or inventory full.
     */
    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        // Check if smart retrieval is enabled
        if (LovelyConfigs.Common.EnableSmartCoreRetrieval && !this.getWorld().isClient) {
            PlayerEntity owner = (PlayerEntity) this.getOwner();
            
            if (owner != null && owner.isAlive()) {
                double distance = this.distanceTo(owner);
                double maxDistance = LovelyConfigs.Common.SmartCoreRetrievalDistance;
                
                if (distance <= maxDistance) {
                    // Attempt auto-retrieval
                    if (attemptAutoRetrieval(owner)) {
                        // Success - core retrieved, don't drop anything
                        return;
                    }
                    // Inventory full - drop core with glow effect
                    handleItemDrop();
                    return;
                } else {
                    // Beyond range - notify owner of drop location
                    String robotName = getRobotDisplayName();
                    owner.sendMessage(
                        Text.literal(robotName + " core dropped at ")
                            .append(Text.literal(this.getBlockPos().toShortString())
                            .formatted(Formatting.YELLOW)),
                        false
                    );
                }
            }
        }
        
        // Fallback: normal drop with glow effect (parent calls handleItemDrop)
        super.dropEquipment(source, lootingMultiplier, allowDrops);
    } // dropEquipment ()

    public void displayGeneralMessage(boolean canShow, boolean showLevelUp) {
        if(!canShow) return;
        InternalLogic.displayInfo(this, (LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_BAR)), false);
        if(showLevelUp) InternalLogic.displayInfo(this, (LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_LEVEL_UP)), false);
        if(this.getCustomName() != null) InternalLogic.displayInfo(this, LovelyIdentifier.getVariantTranslation(nativeEntity.key).append(": " + this.getCustomName().getString()), false);
        else InternalLogic.displayInfo(this, LovelyIdentifier.getVariantTranslation(nativeEntity.key), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_LEVEL).append(": " + this.getCurrentLevel()             + "/" + this.getMaxLevel()), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_EXPERIENCE).append(": " + this.getExp()                 + "/" + InternalLogic.calculateNextExp(this.getExp())), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_HEALTH).append(": " + (int)Math.floor(this.getHealth()) + "/" + (int)this.getMaxHealth()), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_ATTACK).append(": " + this.getAttackDamage()), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_DEFENCE).append(": " + this.getArmorLevel()), false);
    } // displayGeneralMessage ()

    public void displayEnchantmentMessage() {
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_BAR), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_ENCHANTMENT), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_LOOTING).append(": " + this.getLooting()                            + "/" + LovelyConfigs.Common.MaxLootEnchantment), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_FIRE_PROTECTION).append(": " + this.getFireProtection()             + "/" + LovelyConfigs.Common.ProtectionLimitFire), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_FALL_PROTECTION).append(": " + this.getFallProtection()             + "/" + LovelyConfigs.Common.ProtectionLimitFall), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_BLAST_PROTECTION).append(": " + this.getBlastProtection()           + "/" + LovelyConfigs.Common.ProtectionLimitBlast), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_PROJECTILE_PROTECTION).append(": " + this.getProjectileProtection() + "/" + LovelyConfigs.Common.ProtectionLimitProjectile), false);
    } // displayEnchantmentMessage ()

    /**
     * Processes robot retrieval interaction using Ctrl+Shift with empty hand.
     * <p>
     * <b>Input Requirements:</b> Player must hold Ctrl (crouch) and Shift while
     * interacting with empty hand. This prevents accidental pickups during normal
     * interaction.
     * <p>
     * <b>Permission Check:</b> Validates player ownership before allowing retrieval.
     * Only the robot's owner can retrieve it.
     * <p>
     * <b>Inventory Management:</b> Attempts to add spawn item to player inventory.
     * If inventory is full, does nothing (no retrieval). Creative mode always succeeds.
     * <p>
     * <b>Feedback:</b> Spawns particle effects and plays sound to confirm retrieval.
     * 
     * @param stack the item stack in player's hand (must be empty)
     * @param player the player attempting retrieval
     * @return true if retrieval was successful
     */
    protected boolean handlePickupRetrieval(ItemStack stack, PlayerEntity player) {
        // Only process empty hand
        if (!stack.isEmpty()) return false;
        
        // Require Ctrl+Shift (crouch + shift)
        if (!player.isSneaking() || !player.isSprinting()) return false;
        
        // Validate ownership
        if (!this.isOwner(player)) return false;
        
        // Check inventory space (creative mode always has space)
        if (!player.getAbilities().creativeMode && player.getInventory().getEmptySlot() < 0) {
            // Inventory full in survival - notify player
            if (!this.getWorld().isClient) {
                player.sendMessage(
                    Text.literal("Inventory full - cannot retrieve robot")
                        .formatted(Formatting.RED),
                    true
                );
            }
            return false;
        }
        
        // Create spawn item from current robot state
        ItemStack spawnItem = createSpawnItemFromEntity();
        
        // Try to add to player inventory
        boolean added = player.getInventory().insertStack(spawnItem);
        
        // If item wasn't fully added (shouldn't happen in creative, but safety check)
        if (!spawnItem.isEmpty()) {
            // Drop the item at robot location
            net.minecraft.entity.ItemEntity itemEntity = new net.minecraft.entity.ItemEntity(
                this.getWorld(), 
                this.getX(), 
                this.getY(), 
                this.getZ(), 
                spawnItem
            );
            itemEntity.setToDefaultPickupDelay();
            this.getWorld().spawnEntity(itemEntity);
        }
        
        // Spawn particle effects (POOF particles)
        InternalParticle.Poof(this);
        
        // Play sound effect
        this.getWorld().playSound(
            null, 
            this.getBlockPos(), 
            net.minecraft.sound.SoundEvents.ENTITY_ITEM_PICKUP, 
            net.minecraft.sound.SoundCategory.PLAYERS, 
            1.0F, 
            1.0F
        );
        
        // Remove robot entity
        this.discard();
        
        return true;
    } // handlePickupRetrieval ()

    /**
     * Creates spawn item from current robot entity with full NBT preservation.
     * <p>
     * <b>Architecture:</b> Uses same NBT structure as handleItemDrop() to ensure
     * consistency with core drop mechanics. Matches LovelySpawnItem.initialize()
     * expectations for proper data transfer.
     * <p>
     * <b>Data Preservation:</b> Transfers all entity state including level, XP,
     * protections, color, custom name, and owner through NBT compound.
     * 
     * @return ItemStack containing spawn item with complete robot data
     */
    private ItemStack createSpawnItemFromEntity() {
        // Determine correct spawn item based on robot variant
        final ItemStack spawnItem;
        if (this.nativeEntity.key.equals("bunny2")) {
            spawnItem = new ItemStack(LovelyItems.BUNNY2_SPAWN, 1);
        } else {
            spawnItem = new ItemStack(LovelyItems.VANILLA_SPAWN, 1);
        }
        
        // Use same NBT structure as handleItemDrop()
        NbtCompound nbt = spawnItem.getNbt();
        if(nbt == null) nbt = new NbtCompound();

        String customName = Utility.getEntityCustomName(this);
        if (!customName.isEmpty()) nbt.putString(LovelyIdentifier.STAT_CUSTOM_NAME, customName);

        String ownerName = Utility.getEntityOwnerName(this);
        if (!ownerName.isEmpty()) nbt.putString(LovelyIdentifier.STAT_OWNER, ownerName);

        nbt.putString(LovelyIdentifier.STAT_TYPE, this.nativeEntity.key);
        nbt.putInt(LovelyIdentifier.STAT_COLOR, this.getTextureID());

        nbt.putInt(LovelyIdentifier.STAT_MAX_LEVEL, this.getMaxLevel());
        nbt.putInt(LovelyIdentifier.STAT_LEVEL, this.getCurrentLevel());
        nbt.putInt(LovelyIdentifier.STAT_EXP, this.getExp());
        nbt.putFloat(LovelyIdentifier.STAT_HP, this.getCurrentHealthValue());

        nbt.putInt(LovelyIdentifier.STAT_FIRE_PROTECTION, this.getFireProtection());
        nbt.putInt(LovelyIdentifier.STAT_FALL_PROTECTION, this.getFallProtection());
        nbt.putInt(LovelyIdentifier.STAT_BLAST_PROTECTION, this.getBlastProtection());
        nbt.putInt(LovelyIdentifier.STAT_PROJECTILE_PROTECTION, this.getProjectileProtection());

        spawnItem.setNbt(nbt);

        // Apply custom name with title (commented for future use)
        // if (!customName.isEmpty()) {
        //     spawnItem.setCustomName(
        //         Text.literal(customName)
        //             .append(Utility.getRandomTitle())
        //             .formatted(Formatting.DARK_PURPLE)
        //     );
        // }
        
        return spawnItem;
    } // createSpawnItemFromEntity ()

    /**
     * Attempts to automatically retrieve robot core to owner's inventory.
     * <p>
     * <b>Architecture:</b> Creates core item with full NBT data matching handleItemDrop()
     * format. Attempts inventory addition and provides feedback on success.
     * <p>
     * <b>Data Preservation:</b> Uses same NBT structure as handleItemDrop() to ensure
     * consistency. Core can be used to respawn robot with all stats intact.
     * <p>
     * <b>Feedback:</b> On success, spawns particles, plays sound, and sends confirmation
     * message to owner.
     * <p>
     * <b>Creative Mode:</b> In creative mode, always succeeds regardless of inventory state.
     * 
     * @param owner the robot's owner player
     * @return true if core was successfully added to inventory, false if inventory full
     */
    private boolean attemptAutoRetrieval(PlayerEntity owner) {
        // Creative mode always succeeds (items go to creative inventory)
        if (owner.getAbilities().creativeMode) {
            spawnRetrievalParticles();
            playRetrievalSound(owner);
            
            String robotName = getRobotDisplayName();
            owner.sendMessage(
                Text.literal(robotName + " core retrieved")
                    .formatted(Formatting.GREEN),
                true
            );
            return true;
        }
        
        // Survival/Adventure mode - check inventory space
        // Create core item with full NBT data
        final ItemStack coreStack = setDropItem();
        NbtCompound nbt = coreStack.getNbt();
        if(nbt == null) nbt = new NbtCompound();
        
        // Populate NBT (same as handleItemDrop)
        String customName = Utility.getEntityCustomName(this);
        if (!customName.isEmpty()) nbt.putString(LovelyIdentifier.STAT_CUSTOM_NAME, customName);
        
        String ownerName = Utility.getEntityOwnerName(this);
        if (!ownerName.isEmpty()) nbt.putString(LovelyIdentifier.STAT_OWNER, ownerName);
        
        nbt.putString(LovelyIdentifier.STAT_TYPE, this.nativeEntity.key);
        nbt.putInt(LovelyIdentifier.STAT_COLOR, this.getTextureID());
        nbt.putInt(LovelyIdentifier.STAT_MAX_LEVEL, this.getMaxLevel());
        nbt.putInt(LovelyIdentifier.STAT_LEVEL, this.getCurrentLevel());
        nbt.putInt(LovelyIdentifier.STAT_EXP, this.getExp());
        nbt.putInt(LovelyIdentifier.STAT_FIRE_PROTECTION, this.getFireProtection());
        nbt.putInt(LovelyIdentifier.STAT_FALL_PROTECTION, this.getFallProtection());
        nbt.putInt(LovelyIdentifier.STAT_BLAST_PROTECTION, this.getBlastProtection());
        nbt.putInt(LovelyIdentifier.STAT_PROJECTILE_PROTECTION, this.getProjectileProtection());
        
        coreStack.setNbt(nbt);
        
        // Apply custom name with title (commented for future use)
        // if (!customName.isEmpty()) {
        //     coreStack.setCustomName(Text.literal(customName)
        //         .append(Utility.getRandomTitle())
        //         .formatted(Formatting.DARK_PURPLE));
        // }
        
        // Try to add to inventory
        boolean added = owner.getInventory().insertStack(coreStack);
        
        // Check if the entire stack was added (stack should be empty)
        if (added && coreStack.isEmpty()) {
            // Success - spawn particles and send message
            spawnRetrievalParticles();
            playRetrievalSound(owner);
            
            String robotName = getRobotDisplayName();
            owner.sendMessage(
                Text.literal(robotName + " core retrieved")
                    .formatted(Formatting.GREEN),
                true
            );
            
            return true;
        }
        
        // Inventory full or couldn't add full stack - will drop normally
        return false;
    } // attemptAutoRetrieval ()

    /**
     * Gets display name for robot including type and custom name if available.
     * <p>
     * <b>Format:</b> Returns "CustomName (Type)" if named, otherwise just "Type"
     * 
     * @return formatted robot display name
     */
    private String getRobotDisplayName() {
        String customName = Utility.getEntityCustomName(this);
        String typeName = this.nativeEntity.key.substring(0, 1).toUpperCase() + this.nativeEntity.key.substring(1);
        
        if (!customName.isEmpty()) {
            return customName + " (" + typeName + ")";
        }
        return typeName;
    } // getRobotDisplayName ()

    /**
     * Spawns particle effects at robot location to indicate successful core retrieval.
     * <p>
     * <b>Visual Feedback:</b> Uses HAPPY_VILLAGER particles (green sparkles) to
     * indicate positive outcome. Particles spawn in a small area around robot.
     * <p>
     * <b>Performance:</b> Spawns 10 particles with moderate spread. Minimal
     * performance impact.
     */
    private void spawnRetrievalParticles() {
        net.minecraft.server.world.ServerWorld serverWorld = (net.minecraft.server.world.ServerWorld) this.getWorld();
        serverWorld.spawnParticles(
            net.minecraft.particle.ParticleTypes.HAPPY_VILLAGER,
            this.getX(), this.getY() + 0.5, this.getZ(),
            10, // count
            0.5, 0.5, 0.5, // spread
            0.0 // speed
        );
    } // spawnRetrievalParticles ()

    /**
     * Plays sound effect at owner location to indicate successful core retrieval.
     * <p>
     * <b>Audio Feedback:</b> Uses ENTITY_ITEM_PICKUP sound (same as picking up items)
     * to provide familiar audio cue. Plays at owner's location for immediate feedback.
     * <p>
     * <b>Volume:</b> Standard volume (1.0) and pitch (1.0) for clear audibility.
     * 
     * @param owner the robot's owner player
     */
    private void playRetrievalSound(PlayerEntity owner) {
        this.getWorld().playSound(
            null,
            owner.getBlockPos(),
            net.minecraft.sound.SoundEvents.ENTITY_ITEM_PICKUP,
            net.minecraft.sound.SoundCategory.PLAYERS,
            1.0F,
            1.0F
        );
    } // playRetrievalSound ()

} // Class LovelyRobot