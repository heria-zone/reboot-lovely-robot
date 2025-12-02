package net.msymbios.llovelyr.source.entity.common;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.msymbios.llovelyr.common.entity.NativeEntityType;
import net.msymbios.llovelyr.framework.entity.enums.EntityTexture;
import net.msymbios.llovelyr.common.entity.goal.*;
import net.msymbios.llovelyr.common.entity.internal.*;
import net.msymbios.llovelyr.common.utils.internal.Utility;
import net.msymbios.llovelyr.framework.entity.enums.EntityState;
import net.msymbios.llovelyr.framework.utils.Version;
import net.msymbios.llovelyr.lib.entity.type.features.LevelFeature;
import net.msymbios.llovelyr.source.LovelyConfigs;
import net.msymbios.llovelyr.common.shared.LovelyIdentifier;
import net.msymbios.llovelyr.source.LovelyItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

import javax.annotation.Nonnull;

import static net.msymbios.llovelyr.common.utils.internal.Utility.invertBoolean;

public abstract class LovelyRobot extends InternalEntity implements GeoEntity {

    // -- Variables --

    protected static final EntityDataAccessor<Boolean> AUTO_ATTACK = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.BOOLEAN);

    protected static final EntityDataAccessor<Integer> MAX_LEVEL = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> LEVEL = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> EXP = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);

    protected static final EntityDataAccessor<Integer> FIRE_PROTECTION = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> FALL_PROTECTION = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> BLAST_PROTECTION = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> PROJECTILE_PROTECTION = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);

    protected static final EntityDataAccessor<Float> BASE_X = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> BASE_Y = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> BASE_Z = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.FLOAT);

    protected static final EntityDataAccessor<Boolean> IS_IN_SITTING_POSE = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Float> CURRENT_HEALTH = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.FLOAT);

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    
    // -- Standby Animation State (not persisted) --
    private int standbyTicks = 0;
    private int standbyTargetTicks = 0;

    // -- Properties --

    // AUTO ATTACK

    public boolean getAutoAttack() {
        boolean value = false;
        try {value = this.entityData.get(AUTO_ATTACK);}
        catch (Exception ignored) {}
        return value;
    } // getAutoAttack ()

    public void setAutoAttack(boolean value) {
        this.entityData.set(AUTO_ATTACK, value);
    } // setAutoAttack ()

    // STATS

    public int getMaxLevel() { 
        // Use NativeEntityType's convenience accessor which delegates to LevelFeature
        if (nativeEntity instanceof NativeEntityType) {
            return ((NativeEntityType) nativeEntity).getMaxLevel();
        }
        return 0;
    } // getMaxLevel ()

    public int getHp() { return InternalLogic.calculateHp(this.getCurrentLevel(), (int)this.nativeEntity.getData().getMaxHealth()); } // getHp ()

    public int getAttackDamage() { return InternalLogic.calculateAttack(this.getCurrentLevel(), (int)nativeEntity.getData().getAttackDamage()); } // getAttackDamage ()

    public int getArmorLevel() {
        var defence = InternalLogic.calculateDefense(this.getCurrentLevel(), (int)nativeEntity.getData().getArmor());
        return (int) InternalLogic.calculateArmor(defence);
    } // getArmorLevel ()

    public int getArmorToughnessLevel() { return (int) InternalLogic.calculateArmorToughness(getArmorLevel()); } // getArmorToughnessLevel ()

    public int getLooting() {return InternalLogic.calculateLooting(this.getCurrentLevel());} // getLooting ()

    public int getCurrentLevel() {
        var level = 0;
        try {level = this.entityData.get(LEVEL);}
        catch (Exception ignored){}
        return level;
    } // getCurrentLevel ()

    public void setCurrentLevel(int value){
        this.entityData.set(LEVEL, value);
        InternalLogic.handleLevel(this, getHp(), getAttackDamage(), getArmorLevel(), getArmorToughnessLevel());
    } // setCurrentLevel ()

    public int getExp(){
        int value = 1;
        try {value = this.entityData.get(EXP);}
        catch (Exception ignored){}
        return value;
    } // getExp ()

    public void setExp(int value){
        this.entityData.set(EXP, value);
    } // setExp ()

    // PROTECTION

    public int getFireProtection() {
        int value = 0;
        try {value = this.entityData.get(FIRE_PROTECTION);}
        catch (Exception ignored) {}
        return value;
    } // getFireProtection ()

    public void setFireProtection(int value) {
        this.entityData.set(FIRE_PROTECTION, value);
    } // setFireProtection ()

    public int getFallProtection() {
        int retValue = 0;
        try {retValue = this.entityData.get(FALL_PROTECTION);}
        catch (Exception ignored) {}
        return retValue;
    } // getFallProtection ()

    public void setFallProtection(int value) {
        this.entityData.set(FALL_PROTECTION, value);
    } // setFallProtection ()

    public int getBlastProtection() {
        int value = 0;
        try {value = this.entityData.get(BLAST_PROTECTION);}
        catch (Exception ignored) {}
        return value;
    } // getBlastProtection ()

    public void setBlastProtection(int value) {
        this.entityData.set(BLAST_PROTECTION, value);
    } // setBlastProtection ()

    public int getProjectileProtection() {
        int value = 0;
        try {value = this.entityData.get(PROJECTILE_PROTECTION);}
        catch (Exception ignored) {}
        return value;
    } // getProjectileProtection ()

    public void setProjectileProtection(int value) {
        this.entityData.set(PROJECTILE_PROTECTION, value);
    } // setProjectileProtection ()

    // BASE

    public float getBaseX() {
        float value = this.getBlockX();
        try {value = this.entityData.get(BASE_X);}
        catch (Exception ignored) {}
        return value;
    } // getBaseX ()

    public void setBaseX(float value) {
        this.entityData.set(BASE_X, value);
    } // setBaseX ()

    public float getBaseY() {
        float value = this.getBlockY();
        try {value = this.entityData.get(BASE_Y);}
        catch (Exception ignored) {}
        return value;
    } // getBaseY ()

    public void setBaseY(float value) {
        this.entityData.set(BASE_Y, value);
    } // setBaseY ()

    public float getBaseZ() {
        float value = this.getBlockZ();
        try {value = this.entityData.get(BASE_Z);}
        catch (Exception ignored) {}
        return value;
    } // getBaseZ ()

    public void setBaseZ(float value) {
        this.entityData.set(BASE_Z, value);
    } // setBaseZ ()

    // -- Constructor --

    public LovelyRobot(EntityType<? extends InternalEntity> entityType, Level level, NativeEntityType nativeEntity) {
        super(entityType, level, nativeEntity);
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
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, LovelyConfigs.MovementMeleeAttack, true));
        this.goalSelector.addGoal(4, new AiFollowOwnerGoal(this, LovelyConfigs.MovementFollowOwner, LovelyConfigs.FollowDistanceMin, LovelyConfigs.FollowDistanceMax, false));
        this.goalSelector.addGoal(4, new AiBaseDefenseGoal(this, LovelyConfigs.MovementFollowOwner, LovelyConfigs.BaseDefenceRange, LovelyConfigs.BaseDefenceWarpRange));
        this.goalSelector.addGoal(6, new AiConditionalWanderGoal(this, LovelyConfigs.MovementWanderAround));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, LovelyConfigs.LookRange));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, LivingEntity.class, LovelyConfigs.LookRange));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new AiAutoAttackGoal<>(this, Mob.class, LovelyConfigs.AttackChance, true, false,
                entity -> entity instanceof Monster &&
                !(entity instanceof Creeper) &&
                !(entity instanceof InternalEntity)));
    } // registerGoals ()

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
            boolean isMoving = this.getDeltaMovement().lengthSqr() > 0.0001;
            
            if (!isMoving) {
                // Set random target on first tick or when target is 0
                if (standbyTargetTicks == 0) {
                    standbyTargetTicks = LovelyConfigs.StandbyToSitDelayMin + 
                        this.random.nextInt(LovelyConfigs.StandbyToSitDelayMax - LovelyConfigs.StandbyToSitDelayMin + 1);
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
        refreshDimensions();
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
        refreshDimensions();
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
            value = this.entityData.get(IS_IN_SITTING_POSE);
        } catch (Exception ignored) {}
        return value;
    } // isInSittingPose ()
    
    public void setInSittingPose(boolean value) {
        this.entityData.set(IS_IN_SITTING_POSE, value);
    } // setInSittingPose ()

    public float getCurrentHealthValue() {
        float value = 1.0F;
        try {
            value = this.entityData.get(CURRENT_HEALTH);
        } catch (Exception ignored) {}
        return value;
    } // getCurrentHealthValue ()
    
    public void setCurrentHealthValue(float value) {
        this.entityData.set(CURRENT_HEALTH, value);
        this.setHealth(value);
    } // setCurrentHealthValue ()

    @Override
    public void onEnterCombat() {
        handleActivateCombatMode();
        super.onEnterCombat();
    } // onEnterCombat ()
    
    @Override
    public void setTarget(@Nullable LivingEntity target) {
        // Trigger combat mode when acquiring a target (matches Fabric's onAttacking behavior)
        if (target != null) handleActivateCombatMode();
        super.setTarget(target);
    } // setTarget ()

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        switch (slot.getType()){
            case HAND: {
                final ItemStack tempSword = new ItemStack(Items.DIAMOND_SWORD,1);
                final int lootingLevel = this.getLooting();
                if(lootingLevel > 0) {
                    tempSword.enchant(Enchantment.byId(21), lootingLevel);
                }
                return tempSword;
            }
            default: {
                return super.getItemBySlot(slot);
            }
        }
    } // getItemBySlot ()

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        if (isInSittingPose()) {
            return EntityDimensions.scalable(
                LovelyConfigs.EntityDimensions.SITTING_WIDTH,
                LovelyConfigs.EntityDimensions.SITTING_HEIGHT
            );
        }
        return EntityDimensions.scalable(
            LovelyConfigs.EntityDimensions.DEFAULT_WIDTH,
            LovelyConfigs.EntityDimensions.DEFAULT_HEIGHT
        );
    } // getDimensions ()

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LEVEL, 0);
        this.entityData.define(EXP, 0);
        this.entityData.define(AUTO_ATTACK, true);

        this.entityData.define(FIRE_PROTECTION, 0);
        this.entityData.define(FALL_PROTECTION, 0);
        this.entityData.define(BLAST_PROTECTION, 0);
        this.entityData.define(PROJECTILE_PROTECTION, 0);

        this.entityData.define(BASE_X, 0F);
        this.entityData.define(BASE_Y, 0F);
        this.entityData.define(BASE_Z, 0F);
        
        this.entityData.define(IS_IN_SITTING_POSE, false);
        this.entityData.define(CURRENT_HEALTH, 1.0F);
    } // defineSynchedData ()

    @Override
    public void addAdditionalSaveData(CompoundTag dataNBT) {
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

        super.addAdditionalSaveData(dataNBT);
    } // writeCustomDataToNbt ()

    @Override
    public void readAdditionalSaveData(CompoundTag dataNBT) {
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
        if (isInSittingPose() && !this.level().isClientSide) {
            // Schedule dimension refresh for next tick
            this.level().getServer().execute(() -> {
                if (this.isAlive()) {
                    refreshDimensions();
                }
            });
        }
        
        super.readAdditionalSaveData(dataNBT);
    } // readCustomDataFromNbt ()

    @Override
    public CompoundTag writeToNBT(@Nonnull CompoundTag dataNBT) {
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
    public void readFromNBT(@Nonnull CompoundTag dataNBT, @Nonnull Version version) {
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
        if (isInSittingPose() && !this.level().isClientSide) {
            // Schedule dimension refresh for next tick
            this.level().getServer().execute(() -> {
                if (this.isAlive()) refreshDimensions();
            });
        }
    } // readFromNBT ()

    // HANDLERS

    @Override
    protected void handleAttackTarget(@NotNull Entity target) {
        handleActivateCombatMode();
        if(this.getCurrentLevel() < this.getMaxLevel() && !(target instanceof Player) && !this.level().isClientSide) {
            final int maxHp = (int)((LivingEntity)target).getMaxHealth();
            addExp(maxHp / 4);
        }
        this.level().broadcastEntityEvent(this, (byte)4);
    } // handleAttackTarget ()

    @Override
    protected boolean handleDamage (@NotNull DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) return false;
        if ((source.getEntity() instanceof Player player)) {
            if (this.isOwnedBy(player) && !LovelyConfigs.FriendlyFire)
                return false;
        }
        handleActivateCombatMode();

        if ((source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.LAVA)) && amount >= 1.0f && this.getFireProtection() > 0)
            amount *= (100.0f - this.getFireProtection()) / 100.0f;

        if (source.is(DamageTypes.FALL) && amount >= 1.0f && this.getFallProtection() > 0)
            amount *= (100.0f - this.getFallProtection()) / 100.0f;

        if (source.is(DamageTypes.EXPLOSION) && amount >= 1.0f && this.getBlastProtection() > 0)
            amount *= (100.0f - this.getBlastProtection()) / 100.0f;

        if (source.is(DamageTypes.ARROW) && amount >= 1.0f && this.getProjectileProtection() > 0)
            amount *= (100.0f - this.getProjectileProtection()) / 100.0f;

        if (amount < 1.0f) return false;

        if(!level().isClientSide) {
            if((source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.LAVA)) && InternalLogic.handleFireProtectionLevelUp(getFireProtection())) this.setFireProtection(this.getFireProtection() + 1);
            if(source.is(DamageTypes.FALL) && InternalLogic.handleFallProtectionLevelUp(this.getFallProtection())) this.setFallProtection(this.getFallProtection() + 1);
            if(source.is(DamageTypes.EXPLOSION) && InternalLogic.handleBlastProtectionLevelUp(this.getBlastProtection())) this.setBlastProtection(this.getBlastProtection() + 1);
            if(source.is(DamageTypes.ARROW) && InternalLogic.handleProjectileProtectionLevelUp(this.getProjectileProtection())) this.setProjectileProtection(this.getProjectileProtection() + 1);
        }

        final Entity entity = source.getEntity();

        if (this.getCurrentLevel() < this.getMaxLevel() && !(entity instanceof Player) && entity instanceof LivingEntity && !this.level().isClientSide) {
            final int maxHp = (int)((LivingEntity)entity).getMaxHealth();
            addExp(maxHp / 6);
        }

        return true;
    } // handleDamage ()

    @Override
    protected void handleItemDrop() {
        final ItemStack dropItem = setDropItem();
        CompoundTag nbt = dropItem.getTag();
        if(nbt == null) nbt = new CompoundTag();

        String customName = Utility.getEntityCustomName(this);
        if (!customName.isEmpty()) nbt.putString(LovelyIdentifier.STAT_CUSTOM_NAME, customName);

        String ownerName = Utility.getEntityOwnerName(this);
        if (!ownerName.isEmpty()) nbt.putString(LovelyIdentifier.STAT_OWNER, ownerName);

        nbt.putString(LovelyIdentifier.STAT_TYPE, this.nativeEntity.getKey());
        nbt.putInt(LovelyIdentifier.STAT_COLOR, this.getTextureID());

        nbt.putInt(LovelyIdentifier.STAT_MAX_LEVEL, this.getMaxLevel());
        nbt.putInt(LovelyIdentifier.STAT_LEVEL, this.getCurrentLevel());
        nbt.putInt(LovelyIdentifier.STAT_EXP, this.getExp());

        nbt.putInt(LovelyIdentifier.STAT_FIRE_PROTECTION, this.getFireProtection());
        nbt.putInt(LovelyIdentifier.STAT_FALL_PROTECTION, this.getFallProtection());
        nbt.putInt(LovelyIdentifier.STAT_BLAST_PROTECTION, this.getBlastProtection());
        nbt.putInt(LovelyIdentifier.STAT_PROJECTILE_PROTECTION, this.getProjectileProtection());

        dropItem.setTag(nbt);

        if (!customName.isEmpty()) dropItem.setHoverName(Component.nullToEmpty(customName).copy().append(Utility.getRandomTitle()).withStyle(ChatFormatting.DARK_PURPLE));

        ItemEntity itemEntity = new ItemEntity(this.level(), this.getX(), this.getY() + (double)0.0F, this.getZ(), dropItem);
        itemEntity.setDefaultPickUpDelay();
        
        // Apply glowing effect to make core visible through walls
        itemEntity.setGlowingTag(true);
        
        // Apply custom glow color based on robot variant
        applyGlowColor(itemEntity, this.getTextureID());
        
        this.level().addFreshEntity(itemEntity);
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
    private void applyGlowColor(ItemEntity itemEntity, int textureId) {
        try {
            net.minecraft.world.scores.Scoreboard scoreboard = this.level().getScoreboard();
            String teamName = "robot_core_" + textureId;
            
            net.minecraft.world.scores.PlayerTeam team = scoreboard.getPlayerTeam(teamName);
            if (team == null) {
                team = scoreboard.addPlayerTeam(teamName);
                team.setColor(getColorForTexture(textureId));
            }
            
            scoreboard.addPlayerToTeam(itemEntity.getStringUUID(), team);
        } catch (Exception e) {
            // Fallback to default glow if team creation fails
            // Entity will still glow, just with default white color
        }
    } // applyGlowColor ()

    /**
     * Maps robot texture variant to Minecraft chat formatting color.
     * <p>
     * <b>Design Decision:</b> Uses ChatFormatting enum for color consistency with
     * Minecraft's existing color system. Provides 16 distinct colors matching dye palette.
     * 
     * @param textureId the robot's texture variant ID
     * @return corresponding ChatFormatting color
     */
    private ChatFormatting getColorForTexture(int textureId) {
        EntityTexture texture = EntityTexture.byId(textureId);
        return switch (texture) {
            case WHITE -> ChatFormatting.WHITE;
            case ORANGE -> ChatFormatting.GOLD;
            case MAGENTA -> ChatFormatting.LIGHT_PURPLE;
            case LIGHT_BLUE -> ChatFormatting.AQUA;
            case YELLOW -> ChatFormatting.YELLOW;
            case LIME -> ChatFormatting.GREEN;
            case PINK -> ChatFormatting.LIGHT_PURPLE;
            case GRAY -> ChatFormatting.DARK_GRAY;
            case LIGHT_GRAY -> ChatFormatting.GRAY;
            case CYAN -> ChatFormatting.DARK_AQUA;
            case PURPLE -> ChatFormatting.DARK_PURPLE;
            case BLUE -> ChatFormatting.BLUE;
            case BROWN -> ChatFormatting.GOLD;
            case GREEN -> ChatFormatting.DARK_GREEN;
            case RED -> ChatFormatting.RED;
            case BLACK -> ChatFormatting.BLACK;
            default -> ChatFormatting.WHITE;
        };
    } // getColorForTexture ()

    @Override
    protected InteractionResult handleItemInteraction (ItemStack stack, Player player) {
        if(handleTexture(stack, player)) return InteractionResult.SUCCESS;
        return InteractionResult.PASS;
    } // handleItemInteraction ()

    @Override
    protected boolean canInteractWithItems(ItemStack stack) {
        if(stack.getItem() instanceof DyeItem) return false;
        if(stack.getItem() instanceof SwordItem) return false;
        if(stack.is(Items.BOOK) || stack.is(Items.WRITABLE_BOOK) || stack.is(Items.OAK_BUTTON)) return false;
        return !stack.is(Items.COMPASS) && !stack.is(Items.RECOVERY_COMPASS);
    } // canInteractWithItems ()

    @Override
    protected void handleInteract (ItemStack stack, Player player) {
        super.handleInteract(stack, player);
        handlePickupRetrieval(stack, player);
        handleAutoAttack(stack);
        handleDisplayInteraction(stack);
    } // handleInteract

    @Override
    protected void handleState(ItemStack stack) {
        if (handleStandbyState(stack)) return;
        if (handleFollowState(stack)) return;
        if (handleBaseDefenseState(stack)) return;
    } // handleState

    @Override
    protected boolean handleTexture(ItemStack stack, Player player) {
        var oldTexture = getTextureID();
        if(stack.is(Items.WHITE_DYE)) setTexture(EntityTexture.WHITE);
        if(stack.is(Items.ORANGE_DYE)) setTexture(EntityTexture.ORANGE);
        if(stack.is(Items.MAGENTA_DYE)) setTexture(EntityTexture.MAGENTA);
        if(stack.is(Items.LIGHT_BLUE_DYE)) setTexture(EntityTexture.LIGHT_BLUE);
        if(stack.is(Items.YELLOW_DYE)) setTexture(EntityTexture.YELLOW);
        if(stack.is(Items.LIME_DYE)) setTexture(EntityTexture.LIME);
        if(stack.is(Items.PINK_DYE)) setTexture(EntityTexture.PINK);
        if(stack.is(Items.GRAY_DYE)) setTexture(EntityTexture.GRAY);
        if(stack.is(Items.LIGHT_GRAY_DYE)) setTexture(EntityTexture.LIGHT_GRAY);
        if(stack.is(Items.CYAN_DYE)) setTexture(EntityTexture.CYAN);
        if(stack.is(Items.PURPLE_DYE)) setTexture(EntityTexture.PURPLE);
        if(stack.is(Items.BLUE_DYE)) setTexture(EntityTexture.BLUE);
        if(stack.is(Items.BROWN_DYE)) setTexture(EntityTexture.BROWN);
        if(stack.is(Items.GREEN_DYE)) setTexture(EntityTexture.GREEN);
        if(stack.is(Items.RED_DYE)) setTexture(EntityTexture.RED);
        if(stack.is(Items.BLACK_DYE)) setTexture(EntityTexture.BLACK);

        if(oldTexture != getTextureID()) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    } // handleTexture ()

    // -- Custom Methods --

    /**
     * Calculates total experience after applying bonuses and adding to current XP.
     * <p>
     * <b>Custom Name Bonus:</b> Named robots receive 1.5x experience multiplier
     * (50% bonus) to reward player investment in personalization. This encourages
     * players to name their robots and creates stronger attachment.
     * <p>
     * <b>Formula:</b> 
     * <ul>
     * <li>Unnamed: currentExp + value</li>
     * <li>Named: currentExp + (value * 3 / 2)</li>
     * </ul>
     * <p>
     * <b>Design Decision:</b> Bonus is multiplicative rather than additive to
     * maintain consistent scaling across all XP gains. Integer division (3/2)
     * avoids floating-point precision issues.
     * <p>
     * <b>Safety:</b> Handles missing custom names gracefully with try-catch to
     * prevent crashes from null or malformed name data.
     * 
     * @param value base experience points to add (before bonuses)
     * @return total experience after applying bonuses and adding to current XP
     */
    private int calculateEarnedExp (int value) {
        int addExp = value;
        int currentExp = getExp();
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

        return currentExp;
    } // calculateEarnedExp ()

    /**
     * Checks if this robot has a level system attached.
     * <p>
     * <b>Architecture:</b> Verifies that the robot's entity type is a NativeEntityType
     * with an attached LevelFeature. This check is essential before accessing level
     * system functionality to prevent ClassCastException or NullPointerException.
     * <p>
     * <b>Use Case:</b> Call this before using getLevelSystem() or any level-related
     * operations that depend on LevelFeature being present.
     * <p>
     * <b>Performance:</b> Lightweight check - only performs instanceof and Optional.isPresent().
     * Safe to call frequently.
     * 
     * @return true if robot has NativeEntityType with LevelFeature attached, false otherwise
     */
    private boolean hasLevelSystem() {
        if (!(nativeEntity instanceof NativeEntityType)) {
            return false;
        }
        NativeEntityType robotType =
            (NativeEntityType) nativeEntity;
        return robotType.getFeature(LevelFeature.class).isPresent();
    } // hasLevelSystem ()

    /**
     * Retrieves the LevelFeature for this robot.
     * <p>
     * <b>Architecture:</b> Provides convenient access to the robot's LevelFeature
     * without requiring repeated instanceof checks and Optional handling at call sites.
     * Centralizes the feature retrieval logic.
     * <p>
     * <b>Safety:</b> Returns Optional.empty() if robot doesn't have NativeEntityType
     * or if LevelFeature is not attached. Callers should check with hasLevelSystem()
     * first or handle empty Optional appropriately.
     * <p>
     * <b>Usage Pattern:</b>
     * <pre>
     * getLevelSystem().ifPresent(levelFeature -> {
     *     int expRequired = levelFeature.getExpForNextLevel();
     *     // ... use levelFeature
     * });
     * </pre>
     * 
     * @return Optional containing LevelFeature if present, empty Optional otherwise
     */
    private java.util.Optional<LevelFeature> getLevelSystem() {
        if (!(nativeEntity instanceof NativeEntityType)) {
            return java.util.Optional.empty();
        }
        NativeEntityType robotType =
            (NativeEntityType) nativeEntity;
        return robotType.getFeature(LevelFeature.class);
    } // getLevelSystem ()

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

        // Store old level for change detection
        var oldLevel = this.getCurrentLevel();

        // Level up loop with max level cap - using LevelFeature for XP calculation
        getLevelSystem().ifPresent(levelFeature -> {
            // Sync current state to LevelFeature
            levelFeature.setCurrentLevel(this.getCurrentLevel());
            levelFeature.setExperience(this.calculateEarnedExp(value));
            
            // Use LevelFeature's level-up logic
            while (levelFeature.tryLevelUp()) {
                setCurrentLevel(levelFeature.getCurrentLevel());
            }
            
            // Sync back the final experience
            setExp(levelFeature.getExperience());
        });

        // Display level-up notification only if level actually changed
        if (oldLevel != this.getCurrentLevel()) {
            if (!level().isClientSide) {
                try {
                    final LivingEntity owner = getOwner();
                    if (owner != null) {
                        displayGeneralMessage(getNotification(), true);
                    }
                } catch (Exception ignored) {}
                
                // Spawn level-up particle effects (villager trade refresh particles)
                InternalParticle.HappyVillager(this);
                
                // Play level-up sound effect (player experience level-up sound) - positional at robot location
                this.level().playSound(
                    null, 
                    this.getX(), 
                    this.getY(), 
                    this.getZ(), 
                    SoundEvents.PLAYER_LEVELUP,
                    net.minecraft.sounds.SoundSource.NEUTRAL, 
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
        return stack.is(Items.COMPASS) || stack.is(Items.RECOVERY_COMPASS);
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
        setOrderedToSit(false);
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
    protected void dropEquipment() {
        // Check if smart retrieval is enabled
        if (LovelyConfigs.EnableSmartCoreRetrieval && !this.level().isClientSide) {
            Player owner = (Player) this.getOwner();
            
            if (owner != null && owner.isAlive()) {
                double distance = this.distanceTo(owner);
                double maxDistance = LovelyConfigs.SmartCoreRetrievalDistance;
                
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
                    owner.displayClientMessage(
                        Component.literal(robotName + " core dropped at ")
                            .append(Component.literal(this.blockPosition().toShortString())
                            .withStyle(ChatFormatting.YELLOW)),
                        false
                    );
                }
            }
        }
        
        // Fallback: normal drop with glow effect (parent calls handleItemDrop)
        super.dropEquipment();
    } // dropEquipment ()

    public void displayGeneralMessage(boolean canShow, boolean showLevelUp) {
        if(!canShow) return;
        InternalLogic.displayInfo(this, (LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_BAR)), false);
        if(showLevelUp) InternalLogic.displayInfo(this, (LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_LEVEL_UP)), false);
        if(this.getCustomName() != null) InternalLogic.displayInfo(this, LovelyIdentifier.getVariantTranslation(nativeEntity.getKey()).append(": " + this.getCustomName().getString()), false);
        else InternalLogic.displayInfo(this, LovelyIdentifier.getVariantTranslation(nativeEntity.getKey()), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_LEVEL).append(": " + this.getCurrentLevel()             + "/" + this.getMaxLevel()), false);
        getLevelSystem().ifPresent(feature -> InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_EXPERIENCE).append(": " + this.getExp()                 + "/" + feature.getExpForLevel(this.getCurrentLevel())), false));
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_HEALTH).append(": " + (int)Math.floor(this.getHealth()) + "/" + (int)this.getMaxHealth()), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_ATTACK).append(": " + this.getAttackDamage()), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_DEFENCE).append(": " + this.getArmorLevel()), false);
    } // displayGeneralMessage ()

    public void displayEnchantmentMessage() {
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_BAR), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_ENCHANTMENT), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_LOOTING).append(": " + this.getLooting()                            + "/" + LovelyConfigs.MaxLootEnchantment), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_FIRE_PROTECTION).append(": " + this.getFireProtection()             + "/" + LovelyConfigs.ProtectionLimitFire), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_FALL_PROTECTION).append(": " + this.getFallProtection()             + "/" + LovelyConfigs.ProtectionLimitFall), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_BLAST_PROTECTION).append(": " + this.getBlastProtection()           + "/" + LovelyConfigs.ProtectionLimitBlast), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_PROJECTILE_PROTECTION).append(": " + this.getProjectileProtection() + "/" + LovelyConfigs.ProtectionLimitProjectile), false);
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
    protected boolean handlePickupRetrieval(ItemStack stack, Player player) {
        // Only process empty hand
        if (!stack.isEmpty()) return false;
        
        // Require Ctrl+Shift (crouch + shift)
        if (!player.isCrouching() || !player.isShiftKeyDown()) return false;

        // Validate ownership
        if (!this.isOwnedBy(player)) return false;

        // Check inventory space (creative mode always has space)
        if (!player.getAbilities().instabuild && player.getInventory().getFreeSlot() < 0) {
            // Inventory full in survival - notify player
            if (!this.level().isClientSide) {
                player.displayClientMessage(
                    Component.literal("Inventory full - cannot retrieve robot")
                        .withStyle(ChatFormatting.RED),
                    true
                );
            }
            return false;
        }

        // Create spawn item from current robot state
        ItemStack spawnItem = createSpawnItemFromEntity();

        // Try to add to player inventory
        boolean added = player.getInventory().add(spawnItem); // TODO: Inspect this later

        // If item wasn't fully added (shouldn't happen in creative, but safety check)
        if (!spawnItem.isEmpty()) {
            // Drop the item at robot location
            ItemEntity itemEntity = new ItemEntity(
                this.level(),
                this.getX(),
                this.getY(),
                this.getZ(),
                spawnItem
            );
            itemEntity.setDefaultPickUpDelay();
            this.level().addFreshEntity(itemEntity);
        }

        // Spawn particle effects (POOF particles)
        InternalParticle.Poof(this);

        // Play sound effect
        this.level().playSound(
            null,
            this.blockPosition(),
            SoundEvents.ITEM_PICKUP,
            net.minecraft.sounds.SoundSource.PLAYERS,
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
        if (this.nativeEntity.getKey().equals("bunny2")) {
            spawnItem = new ItemStack(LovelyItems.BUNNY2_SPAWN.get(), 1);
        } else {
            spawnItem = new ItemStack(LovelyItems.VANILLA_SPAWN.get(), 1);
        }
        
        // Use same NBT structure as handleItemDrop()
        CompoundTag nbt = spawnItem.getTag();
        if(nbt == null) nbt = new CompoundTag();

        String customName = Utility.getEntityCustomName(this);
        if (!customName.isEmpty()) nbt.putString(LovelyIdentifier.STAT_CUSTOM_NAME, customName);

        String ownerName = Utility.getEntityOwnerName(this);
        if (!ownerName.isEmpty()) nbt.putString(LovelyIdentifier.STAT_OWNER, ownerName);

        nbt.putString(LovelyIdentifier.STAT_TYPE, this.nativeEntity.getKey());
        nbt.putInt(LovelyIdentifier.STAT_COLOR, this.getTextureID());

        nbt.putInt(LovelyIdentifier.STAT_MAX_LEVEL, this.getMaxLevel());
        nbt.putInt(LovelyIdentifier.STAT_LEVEL, this.getCurrentLevel());
        nbt.putInt(LovelyIdentifier.STAT_EXP, this.getExp());
        nbt.putFloat(LovelyIdentifier.STAT_HP, this.getCurrentHealthValue());

        nbt.putInt(LovelyIdentifier.STAT_FIRE_PROTECTION, this.getFireProtection());
        nbt.putInt(LovelyIdentifier.STAT_FALL_PROTECTION, this.getFallProtection());
        nbt.putInt(LovelyIdentifier.STAT_BLAST_PROTECTION, this.getBlastProtection());
        nbt.putInt(LovelyIdentifier.STAT_PROJECTILE_PROTECTION, this.getProjectileProtection());

        spawnItem.setTag(nbt);

        // Apply custom name with title (commented for future use)
        // if (!customName.isEmpty()) {
        //     spawnItem.setHoverName(
        //         Component.nullToEmpty(customName)
        //             .copy()
        //             .append(Utility.getRandomTitle())
        //             .withStyle(ChatFormatting.DARK_PURPLE)
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
    private boolean attemptAutoRetrieval(Player owner) {
        // Creative mode always succeeds (items go to creative inventory)
        if (owner.getAbilities().instabuild) {
            spawnRetrievalParticles();
            playRetrievalSound(owner);
            
            String robotName = getRobotDisplayName();
            owner.displayClientMessage(
                Component.literal(robotName + " core retrieved")
                    .withStyle(ChatFormatting.GREEN),
                true
            );
            return true;
        }
        
        // Survival/Adventure mode - check inventory space
        // Create core item with full NBT data
        final ItemStack coreStack = setDropItem();
        CompoundTag nbt = coreStack.getTag();
        if(nbt == null) nbt = new CompoundTag();
        
        // Populate NBT (same as handleItemDrop)
        String customName = Utility.getEntityCustomName(this);
        if (!customName.isEmpty()) nbt.putString(LovelyIdentifier.STAT_CUSTOM_NAME, customName);
        
        String ownerName = Utility.getEntityOwnerName(this);
        if (!ownerName.isEmpty()) nbt.putString(LovelyIdentifier.STAT_OWNER, ownerName);
        
        nbt.putString(LovelyIdentifier.STAT_TYPE, this.nativeEntity.getKey());
        nbt.putInt(LovelyIdentifier.STAT_COLOR, this.getTextureID());
        nbt.putInt(LovelyIdentifier.STAT_MAX_LEVEL, this.getMaxLevel());
        nbt.putInt(LovelyIdentifier.STAT_LEVEL, this.getCurrentLevel());
        nbt.putInt(LovelyIdentifier.STAT_EXP, this.getExp());
        nbt.putInt(LovelyIdentifier.STAT_FIRE_PROTECTION, this.getFireProtection());
        nbt.putInt(LovelyIdentifier.STAT_FALL_PROTECTION, this.getFallProtection());
        nbt.putInt(LovelyIdentifier.STAT_BLAST_PROTECTION, this.getBlastProtection());
        nbt.putInt(LovelyIdentifier.STAT_PROJECTILE_PROTECTION, this.getProjectileProtection());
        
        coreStack.setTag(nbt);
        
        // Apply custom name with title (commented for future use)
        // if (!customName.isEmpty()) {
        //     coreStack.setHoverName(Component.nullToEmpty(customName)
        //         .copy().append(Utility.getRandomTitle())
        //         .withStyle(ChatFormatting.DARK_PURPLE));
        // }
        
        // Try to add to inventory
        boolean added = owner.getInventory().add(coreStack);
        
        // Check if the entire stack was added (stack should be empty)
        if (added && coreStack.isEmpty()) {
            // Success - spawn particles and send message
            spawnRetrievalParticles();
            playRetrievalSound(owner);
            
            String robotName = getRobotDisplayName();
            owner.displayClientMessage(
                Component.literal(robotName + " core retrieved")
                    .withStyle(ChatFormatting.GREEN),
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
        String typeName = this.nativeEntity.getKey().substring(0, 1).toUpperCase() + this.nativeEntity.getKey().substring(1);
        
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
        net.minecraft.server.level.ServerLevel serverLevel = (net.minecraft.server.level.ServerLevel) this.level();
        serverLevel.sendParticles(
            net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
            this.getX(), this.getY() + 0.5, this.getZ(),
            10, // count
            0.5, 0.5, 0.5, // spread
            0.0 // speed
        );
    } // spawnRetrievalParticles ()

    /**
     * Plays sound effect at owner location to indicate successful core retrieval.
     * <p>
     * <b>Audio Feedback:</b> Uses ITEM_PICKUP sound (same as picking up items)
     * to provide familiar audio cue. Plays at owner's location for immediate feedback.
     * <p>
     * <b>Volume:</b> Standard volume (1.0) and pitch (1.0) for clear audibility.
     * 
     * @param owner the robot's owner player
     */
    private void playRetrievalSound(Player owner) {
        this.level().playSound(
            null,
            owner.blockPosition(),
            SoundEvents.ITEM_PICKUP,
            net.minecraft.sounds.SoundSource.PLAYERS,
            1.0F,
            1.0F
        );
    } // playRetrievalSound ()

    // -- Attribute Creation --

    /**
     * Creates entity attributes from NativeEntityType configuration.
     * <p>
     * Retrieves combat stats (health, attack, armor, speed) from entity's CombatData,
     * which is populated from config during mod initialization.
     *
     * @param entity robot entity type containing configured stats
     * @return attribute supplier with configured values
     */
    public static AttributeSupplier createAttributes(NativeEntityType entity) {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, entity.getData().getMaxHealth())
                .add(Attributes.ATTACK_DAMAGE, entity.getData().getAttackDamage())
                .add(Attributes.ATTACK_SPEED, entity.getData().getAttackSpeed())
                .add(Attributes.MOVEMENT_SPEED, entity.getData().getMoveSpeed())
                .add(Attributes.ARMOR, entity.getData().getArmor())
                .add(Attributes.ARMOR_TOUGHNESS, entity.getData().getArmorToughness())
                .build();
    } // createAttributes ()

} // Class LovelyRobot
