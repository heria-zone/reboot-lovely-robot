package net.heriazone.lovelylib.common.entity;

import net.heriazone.hzlib.api.entity.InternalEntity;
import net.heriazone.hzlib.api.entity.data.ExperienceTracker;
import net.heriazone.hzlib.api.entity.features.DropFeature;
import net.heriazone.hzlib.api.entity.features.LevelFeature;
import net.heriazone.hzlib.api.entity.features.PickupFeature;
import net.heriazone.hzlib.api.entity.internal.InternalLogic;
import net.heriazone.hzlib.api.entity.internal.InternalParticle;
import net.heriazone.hzlib.framework.entity.data.CombatLevelStats;
import net.heriazone.hzlib.framework.entity.data.EnchantmentStats;
import net.heriazone.hzlib.framework.entity.data.ProtectionStats;
import net.heriazone.hzlib.framework.entity.enums.EntityState;
import net.heriazone.hzlib.utils.Utils;
import net.heriazone.lovelylib.api.entity.features.ProtectionFeature;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.enums.EntityTexture;
import net.heriazone.lovelylib.common.entity.goal.*;
import net.heriazone.lovelylib.common.entity.utils.EnchantmentProtectionCalculator;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.lovelylib.common.shared.LovelyIdentifier;
import net.heriazone.lovelylib.utils.EntitySpawnHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.heriazone.hzlib.utils.Utils.invertBoolean;

/**
 * <p>Robot-specific entity tier — level progression, protection, enchantments, and registry.<p>
 * <p>
 * <b>Architecture:</b> Extends {@link InternalEntity} with all robot-specific fields.
 * Sits between the shared {@link InternalEntity} base and lovelylib's {@code RobotEntity}
 * (renamed from {@code LovelyRobotEntity}) which adds AI goals and robot behaviors.
 * <p>
 * <b>Field ownership:</b>
 * <ul>
 *   <li>Level, experience, max level — robot leveling system</li>
 *   <li>Fire/fall/blast/projectile protection — enchantment-based damage reduction</li>
 *   <li>Auto-attack, base coordinates, sitting pose, health sync — robot behavior state</li>
 *   <li>{@link CombatLevelStats}, {@link ProtectionStats}, {@link EnchantmentStats} — stat objects</li>
 *   <li>{@link ExperienceTracker} — prevents infinite exp from immortal entities</li>
 * </ul>
 * <p>
 * <b>Registry lifecycle:</b> {@link #registerRobot()}, {@link #ensureRegistered()},
 * {@link #unregisterRobot()} are abstract — implemented in lovelylib's {@code RobotEntity}
 * which has access to the registry infrastructure.
 * <p>
 * <b>recalculateAttributes():</b> Abstract — implemented in lovelylib's {@code RobotEntity}
 * which has access to {@code CombatLevelFeature}, {@code EnchantmentFeature}, and
 * {@code ProtectionFeature} from lovelylib's feature package.
 * <p>
 * <b>Thread Safety:</b> {@code EntityDataAccessor} fields are synchronized automatically.
 * Stat object mutations should occur on the server thread.
 */
public abstract class RobotEntity extends InternalEntity {

    // -- Entity Data Accessors (robot-specific) --

    /** Current robot level (1–200). Synced for client display. */
    protected static final EntityDataAccessor<Integer> LEVEL =
            SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);

    /** Current experience points. Synced for client display. */
    protected static final EntityDataAccessor<Integer> EXP =
            SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);

    /** Maximum level cap for this robot type. Synced for client display. */
    protected static final EntityDataAccessor<Integer> MAX_LEVEL =
            SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);

    /** Fire protection level (0–max). Synced for client display. */
    protected static final EntityDataAccessor<Integer> FIRE_PROTECTION =
            SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);

    /** Fall protection level (0–max). Synced for client display. */
    protected static final EntityDataAccessor<Integer> FALL_PROTECTION =
            SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);

    /** Blast protection level (0–max). Synced for client display. */
    protected static final EntityDataAccessor<Integer> BLAST_PROTECTION =
            SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);

    /** Projectile protection level (0–max). Synced for client display. */
    protected static final EntityDataAccessor<Integer> PROJECTILE_PROTECTION =
            SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);

    /** Whether auto-attack mode is enabled. Synced for client display. */
    protected static final EntityDataAccessor<Boolean> AUTO_ATTACK =
            SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.BOOLEAN);

    /** Home base X coordinate for base-defense mode. */
    protected static final EntityDataAccessor<Float> BASE_X =
            SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.FLOAT);

    /** Home base Y coordinate for base-defense mode. */
    protected static final EntityDataAccessor<Float> BASE_Y =
            SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.FLOAT);

    /** Home base Z coordinate for base-defense mode. */
    protected static final EntityDataAccessor<Float> BASE_Z =
            SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.FLOAT);

    /**
     * Whether the robot is in the sitting pose (smaller hitbox).
     * Synced so the client can render the correct hitbox.
     */
    protected static final EntityDataAccessor<Boolean> IS_IN_SITTING_POSE =
            SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.BOOLEAN);

    /**
     * Current health value, synced for persistence.
     * <p>
     * <b>Architecture:</b> Minecraft's health system doesn't automatically sync to
     * {@code EntityData}, causing health to reset on world reload. This field ensures
     * current health is stored and restored correctly.
     */
    protected static final EntityDataAccessor<Float> CURRENT_HEALTH =
            SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.FLOAT);

    // -- Stat Objects --

    /**
     * Combat level statistics — level, experience, HP, attack, defense.
     * <p>
     * <b>Architecture:</b> Replaces scattered level/exp/hp fields with a cohesive
     * data object, enabling feature-based attribute calculations.
     */
    protected CombatLevelStats combatStats = new CombatLevelStats();

    /**
     * Protection statistics — fire, fall, blast, projectile protection levels.
     */
    protected ProtectionStats protectionStats = new ProtectionStats();

    /**
     * Enchantment statistics — looting, sharpness, knockback levels.
     */
    protected EnchantmentStats enchantmentStats = new EnchantmentStats();

    /**
     * Tracks pending experience from attacked entities.
     * <p>
     * <b>Architecture:</b> Prevents infinite exp gain from immortal entities by
     * accumulating exp per entity UUID and only awarding on death.
     */
    protected ExperienceTracker expTracker = new ExperienceTracker();

    // -- Constructor --

    /**
     * Creates robot entity. Subclasses must set {@link #nativeEntity} and call
     * {@link #applyBaseAttributes()} after construction.
     *
     * @param entityType Minecraft's entity type
     * @param world      world instance
     */
    protected RobotEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
        handlePostSpawnInitialization();
    } // Constructor: RobotEntity ()

    // -- Inherited Methods --

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, SharedConfigs.Common.MovementMeleeAttack, true));
        this.goalSelector.addGoal(4, new AiFollowOwnerGoal(this, SharedConfigs.Common.MovementFollowOwner, SharedConfigs.Common.FollowDistanceMin, SharedConfigs.Common.FollowDistanceMax));
        this.goalSelector.addGoal(4, new AiBaseDefenseGoal(this, SharedConfigs.Common.MovementFollowOwner, SharedConfigs.Common.BaseDefenceRange, SharedConfigs.Common.BaseDefenceWarpRange));
        this.goalSelector.addGoal(6, new AiConditionalWanderGoal(this, SharedConfigs.Common.MovementWanderAround));
        this.goalSelector.addGoal(7, new AiConditionalLookGoal(this, Player.class, SharedConfigs.Common.LookRange));
        this.goalSelector.addGoal(7, new AiConditionalLookGoal(this, LivingEntity.class, SharedConfigs.Common.LookRange));
        this.goalSelector.addGoal(8, new AiConditionalRandomLookGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new AiAutoAttackGoal<>(this, Mob.class, SharedConfigs.Common.AttackChance, true, false, entity -> entity instanceof Monster && !(entity instanceof Creeper)));
    } // registerGoals ()

    @Override
    public void tick() {
        super.tick();

        handleStandbyAnimation();
        handleCombatMode();
        handleAutoHeal();
        handleHealthSync();
        displayExtra();

        // Purge stale exp entries every 20 ticks (once per second) to prevent memory leaks
        if (!this.level().isClientSide && this.tickCount % 20 == 0) {
            expTracker.purgeStaleEntries();
        }
    } // tick ()

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);

        builder.define(LEVEL,                1);
        builder.define(EXP,                  0);
        builder.define(MAX_LEVEL,            0);

        builder.define(FIRE_PROTECTION,      0);
        builder.define(FALL_PROTECTION,      0);
        builder.define(BLAST_PROTECTION,     0);
        builder.define(PROJECTILE_PROTECTION, 0);

        builder.define(AUTO_ATTACK,          true);

        builder.define(BASE_X,               0F);
        builder.define(BASE_Y,               0F);
        builder.define(BASE_Z,               0F);

        builder.define(IS_IN_SITTING_POSE,   false);
        builder.define(CURRENT_HEALTH,       1.0F);
    } // defineSynchedData ()

    // -- Custom Methods --

    // -- Level / Experience Accessors --

    /** Returns the current robot level. */
    public int getCurrentLevel() {
        try { return entityData.get(LEVEL); }
        catch (Exception ignored) { return 1; }
    } // getCurrentLevel ()

    /** Sets the current robot level and syncs to {@link CombatLevelStats}. */
    public void setCurrentLevel(int level) {
        entityData.set(LEVEL, Math.max(1, level));
        combatStats.setLevel(Math.max(1, level));
    } // setCurrentLevel ()

    /** Returns the current experience points. */
    public int getExp() {
        try { return entityData.get(EXP); }
        catch (Exception ignored) { return 0; }
    } // getExp ()

    /** Sets the current experience points and syncs to {@link CombatLevelStats}. */
    public void setExp(int exp) {
        int clamped = Math.max(0, exp);
        entityData.set(EXP, clamped);
        combatStats.setExperience(clamped);
    } // setExp ()

    /** Returns the maximum level cap for this robot type. */
    public int getMaxLevel() {
        try { return entityData.get(MAX_LEVEL); }
        catch (Exception ignored) { return 0; }
    } // getMaxLevel ()

    /** Sets the maximum level cap. */
    public void setMaxLevel(int maxLevel) {
        entityData.set(MAX_LEVEL, Math.max(0, maxLevel));
    } // setMaxLevel ()

    // -- Protection Accessors --

    /** Returns the fire protection level. */
    public int getFireProtection() {
        try { return entityData.get(FIRE_PROTECTION); }
        catch (Exception ignored) { return 0; }
    } // getFireProtection ()

    /** Sets the fire protection level and syncs to {@link ProtectionStats}. */
    public void setFireProtection(int level) {
        int v = Math.max(0, level);
        entityData.set(FIRE_PROTECTION, v);
        protectionStats.setFireProtection(v);
    } // setFireProtection ()

    /** Returns the fall protection level. */
    public int getFallProtection() {
        try { return entityData.get(FALL_PROTECTION); }
        catch (Exception ignored) { return 0; }
    } // getFallProtection ()

    /** Sets the fall protection level and syncs to {@link ProtectionStats}. */
    public void setFallProtection(int level) {
        int v = Math.max(0, level);
        entityData.set(FALL_PROTECTION, v);
        protectionStats.setFallProtection(v);
    } // setFallProtection ()

    /** Returns the blast protection level. */
    public int getBlastProtection() {
        try { return entityData.get(BLAST_PROTECTION); }
        catch (Exception ignored) { return 0; }
    } // getBlastProtection ()

    /** Sets the blast protection level and syncs to {@link ProtectionStats}. */
    public void setBlastProtection(int level) {
        int v = Math.max(0, level);
        entityData.set(BLAST_PROTECTION, v);
        protectionStats.setBlastProtection(v);
    } // setBlastProtection ()

    /** Returns the projectile protection level. */
    public int getProjectileProtection() {
        try { return entityData.get(PROJECTILE_PROTECTION); }
        catch (Exception ignored) { return 0; }
    } // getProjectileProtection ()

    /** Sets the projectile protection level and syncs to {@link ProtectionStats}. */
    public void setProjectileProtection(int level) {
        int v = Math.max(0, level);
        entityData.set(PROJECTILE_PROTECTION, v);
        protectionStats.setProjectileProtection(v);
    } // setProjectileProtection ()

    // -- Auto-Attack --

    /** Returns whether auto-attack mode is enabled. */
    public boolean getAutoAttack() {
        try { return entityData.get(AUTO_ATTACK); }
        catch (Exception ignored) { return true; }
    } // getAutoAttack ()

    /** Sets whether auto-attack mode is enabled. */
    public void setAutoAttack(boolean value) {
        entityData.set(AUTO_ATTACK, value);
    } // setAutoAttack ()

    // -- Base Coordinates --

    /** Returns the home base X coordinate. */
    public float getBaseX() {
        try { return entityData.get(BASE_X); }
        catch (Exception ignored) { return getBlockX(); }
    } // getBaseX ()

    /** Sets the home base X coordinate. */
    public void setBaseX(float value) { entityData.set(BASE_X, value); }

    /** Returns the home base Y coordinate. */
    public float getBaseY() {
        try { return entityData.get(BASE_Y); }
        catch (Exception ignored) { return getBlockY(); }
    } // getBaseY ()

    /** Sets the home base Y coordinate. */
    public void setBaseY(float value) { entityData.set(BASE_Y, value); }

    /** Returns the home base Z coordinate. */
    public float getBaseZ() {
        try { return entityData.get(BASE_Z); }
        catch (Exception ignored) { return getBlockZ(); }
    } // getBaseZ ()

    /** Sets the home base Z coordinate. */
    public void setBaseZ(float value) { entityData.set(BASE_Z, value); }

    // -- Sitting Pose --

    /**
     * Returns whether the robot is in the sitting pose (smaller hitbox).
     * Used by {@code getDimensions(Pose)} and the animation controller.
     */
    public boolean isInSittingPose() {
        try { return entityData.get(IS_IN_SITTING_POSE); }
        catch (Exception ignored) { return false; }
    } // isInSittingPose ()

    /** Sets the sitting pose flag and triggers a dimension refresh. */
    public void setInSittingPose(boolean value) {
        entityData.set(IS_IN_SITTING_POSE, value);
    } // setInSittingPose ()

    // -- Health Sync --

    /** Returns the persisted current health value. */
    public float getCurrentHealthValue() {
        try { return entityData.get(CURRENT_HEALTH); }
        catch (Exception ignored) { return 1.0F; }
    } // getCurrentHealthValue ()

    /** Sets the persisted current health value and applies it to the entity. */
    public void setCurrentHealthValue(float value) {
        entityData.set(CURRENT_HEALTH, value);
        setHealth(value);
    } // setCurrentHealthValue ()

    // -- Stat Object Accessors --

    /**
     * Returns a defensive copy of the combat level statistics.
     *
     * @return copy of {@link CombatLevelStats}
     */
    public CombatLevelStats getCombatStats()       { return combatStats.copy();       }

    /**
     * Returns a defensive copy of the protection statistics.
     *
     * @return copy of {@link ProtectionStats}
     */
    public ProtectionStats getProtectionStats()    { return protectionStats.copy();   }

    /**
     * Returns a defensive copy of the enchantment statistics.
     *
     * @return copy of {@link EnchantmentStats}
     */
    public EnchantmentStats getEnchantmentStats()  { return enchantmentStats.copy();  }

    // -- Attribute Recalculation (abstract — implemented in lovelylib RobotEntity) --

    /**
     * Recalculates all entity attributes based on current level and attached features.
     * <p>
     * <b>Implementation:</b> Implemented in lovelylib's {@code RobotEntity} which has
     * access to {@code CombatLevelFeature}, {@code EnchantmentFeature}, and
     * {@code ProtectionFeature} from lovelylib's feature package.
     * <p>
     * <b>Call sites:</b> After level-up, after NBT load, after config reload.
     */
    protected abstract void recalculateAttributes();

    // -- Registry Lifecycle (abstract — implemented in lovelylib RobotEntity) --

    /**
     * Registers this robot in the owner's registry.
     * Implemented in lovelylib's {@code RobotEntity} which has access to
     * {@code RobotRegistryManager}.
     */
    protected abstract void registerRobot();

    /**
     * Ensures this robot is registered, creating or updating the registry entry.
     * Called on world load / chunk load.
     */
    protected abstract void ensureRegistered();

    /**
     * Unregisters this robot from the owner's registry.
     * Called on death or removal.
     */
    protected abstract void unregisterRobot();

    // -- NBT Serialization --

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);

        // Level / experience
        nbt.putInt("Level",    getCurrentLevel());
        nbt.putInt("Exp",      getExp());
        nbt.putInt("MaxLevel", getMaxLevel());

        // Protection
        nbt.putInt("FireProtection",       getFireProtection());
        nbt.putInt("FallProtection",       getFallProtection());
        nbt.putInt("BlastProtection",      getBlastProtection());
        nbt.putInt("ProjectileProtection", getProjectileProtection());

        // Robot state
        nbt.putBoolean("AutoAttack",      getAutoAttack());
        nbt.putFloat("BaseX",             getBaseX());
        nbt.putFloat("BaseY",             getBaseY());
        nbt.putFloat("BaseZ",             getBaseZ());
        nbt.putBoolean("IsInSittingPose", isInSittingPose());
        nbt.putFloat("CurrentHealth",     getHealth());
    } // addAdditionalSaveData ()

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt); // handles TextureVariant, ModelVariant, etc.

        // Level / experience
        if (nbt.contains("Level"))    setCurrentLevel(nbt.getInt("Level"));
        if (nbt.contains("Exp"))      setExp(nbt.getInt("Exp"));
        if (nbt.contains("MaxLevel")) setMaxLevel(nbt.getInt("MaxLevel"));

        // Protection
        if (nbt.contains("FireProtection"))       setFireProtection(nbt.getInt("FireProtection"));
        if (nbt.contains("FallProtection"))       setFallProtection(nbt.getInt("FallProtection"));
        if (nbt.contains("BlastProtection"))      setBlastProtection(nbt.getInt("BlastProtection"));
        if (nbt.contains("ProjectileProtection")) setProjectileProtection(nbt.getInt("ProjectileProtection"));

        // Robot state
        if (nbt.contains("AutoAttack"))      setAutoAttack(nbt.getBoolean("AutoAttack"));
        if (nbt.contains("BaseX"))           setBaseX(nbt.getFloat("BaseX"));
        if (nbt.contains("BaseY"))           setBaseY(nbt.getFloat("BaseY"));
        if (nbt.contains("BaseZ"))           setBaseZ(nbt.getFloat("BaseZ"));
        if (nbt.contains("IsInSittingPose")) setInSittingPose(nbt.getBoolean("IsInSittingPose"));

        // Restore health
        if (nbt.contains("CurrentHealth")) {
            float savedHealth = nbt.getFloat("CurrentHealth");
            if (savedHealth > 0) {
                setHealth(savedHealth);
                entityData.set(CURRENT_HEALTH, savedHealth);
            }
        }

        // Recalculate attributes after loading — ensures stats are correct
        recalculateAttributes();
    } // readAdditionalSaveData ()

    // -- Child Creation --

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob otherParent) {
        return null; // Robots don't breed
    } // getBreedOffspring ()



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
    protected abstract void handleStandbyAnimation();

    /**
     * Transitions robot into sitting pose with smaller hitbox.
     * <p>
     * <b>Architecture:</b> Sets sitting flag and triggers hitbox refresh. Animation
     * controller detects flag change and switches to SIT animation with 5-tick blend.
     * <p>
     * <b>Hitbox Change:</b> Shrinks from default (0.6 x 1.8) to sitting (0.6 x 0.9).
     * Prevents collision issues when robot is in low-profile sitting animation.
     */
    protected void enterSittingPose() {
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
    protected void exitSittingPose() {
        setInSittingPose(false);
        refreshDimensions();
    } // exitSittingPose ()

    @Override
    public boolean attackable() {
        if (super.attackable()) handleActivateCombatMode();
        return super.attackable();
    } // attackable ()

    @Override
    public void handleDamageEvent(@NotNull DamageSource source) {
        handleActivateCombatMode();
        super.handleDamageEvent(source);
    } // handleDamageEvent ()

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
    public boolean isFood(ItemStack itemStack) {
        return false;
    } // isFood ()

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        switch (slot.getType()){
            case HAND: {
                final ItemStack tempSword = new ItemStack(Items.DIAMOND_SWORD,1);
                final int lootingLevel = this.getLooting();
                if(lootingLevel > 0 && this.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(Enchantments.LOOTING).isPresent()) {
                    tempSword.enchant(
                            this.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.LOOTING),
                            lootingLevel
                    );
                }
                return tempSword;
            }
            default: {
                return super.getItemBySlot(slot);
            }
        }
    } // getItemBySlot ()

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        if (isInSittingPose()) {
            return EntityDimensions.scalable(
                    SharedConfigs.EntityDimensions.SITTING_WIDTH,
                    SharedConfigs.EntityDimensions.SITTING_HEIGHT
            );
        }
        return EntityDimensions.scalable(
                SharedConfigs.EntityDimensions.DEFAULT_WIDTH,
                SharedConfigs.EntityDimensions.DEFAULT_HEIGHT
        );
    } // getDimensions ()

    // HANDLERS

    @Override
    protected void handleAttackTarget(@NotNull Entity target) {
        handleActivateCombatMode();
        if(this.getCurrentLevel() < this.getMaxLevel() && !(target instanceof Player) && !this.level().isClientSide) {
            final int maxHp = (int)((LivingEntity)target).getMaxHealth();
            int expAmount = maxHp / 4;

            // Accumulate exp instead of immediate award to prevent farming immortal entities
            expTracker.accumulateExp(target.getUUID(), expAmount);
        }
        this.level().broadcastEntityEvent(this, (byte)4);
    } // handleAttackTarget ()

    @Override
    protected boolean handleDamage (@NotNull DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) return false;
        if ((source.getEntity() instanceof Player player)) {
            if (this.isOwnedBy(player) && !SharedConfigs.Common.FriendlyFire)
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
            ProtectionFeature protection = getProtectionFeature();
            if((source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.LAVA)) && protection.canUpgradeFireProtection(getFireProtection())) this.setFireProtection(this.getFireProtection() + 1);
            if(source.is(DamageTypes.FALL) && protection.canUpgradeFallProtection(getFallProtection())) this.setFallProtection(this.getFallProtection() + 1);
            if(source.is(DamageTypes.EXPLOSION) && protection.canUpgradeBlastProtection(getBlastProtection())) this.setBlastProtection(this.getBlastProtection() + 1);
            if(source.is(DamageTypes.ARROW) && protection.canUpgradeProjectileProtection(getProjectileProtection())) this.setProjectileProtection(this.getProjectileProtection() + 1);
        }

        final Entity entity = source.getEntity();

        if (this.getCurrentLevel() < this.getMaxLevel() && !(entity instanceof Player) && entity instanceof LivingEntity && !this.level().isClientSide) {
            final int maxHp = (int)((LivingEntity)entity).getMaxHealth();
            int expAmount = maxHp / 6;

            // Accumulate exp instead of immediate award to prevent farming immortal entities
            expTracker.accumulateExp(entity.getUUID(), expAmount);
        }

        return true;
    } // handleDamage ()

    @Override
    protected void handleItemDrop() {
        final ItemStack dropItem = getDropItem();

        // Use Data Components instead of NBT tags (1.21.1)
        CompoundTag nbt = new CompoundTag();

        String customName = Utils.getEntityCustomName(this);
        if (!customName.isEmpty()) nbt.putString(LovelyConstant.STAT_CUSTOM_NAME, customName);

        String ownerName = Utils.getEntityOwnerName(this);
        if (!ownerName.isEmpty()) nbt.putString(LovelyConstant.STAT_OWNER, ownerName);

        nbt.putString(LovelyConstant.STAT_TYPE, this.nativeEntity.getKey());
        nbt.putInt(LovelyConstant.STAT_COLOR, this.getTextureID());

        nbt.putInt(LovelyConstant.STAT_MAX_LEVEL, this.getMaxLevel());
        nbt.putInt(LovelyConstant.STAT_LEVEL, this.getCurrentLevel());
        nbt.putInt(LovelyConstant.STAT_EXP, this.getExp());

        // Note: Current health is intentionally NOT included so crafted robots spawn at max health

        nbt.putInt(LovelyConstant.STAT_FIRE_PROTECTION, this.getFireProtection());
        nbt.putInt(LovelyConstant.STAT_FALL_PROTECTION, this.getFallProtection());
        nbt.putInt(LovelyConstant.STAT_BLAST_PROTECTION, this.getBlastProtection());
        nbt.putInt(LovelyConstant.STAT_PROJECTILE_PROTECTION, this.getProjectileProtection());

        // Store custom data in DataComponents.CUSTOM_DATA
        dropItem.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(nbt));

        // Set custom name using DataComponents.CUSTOM_NAME
        // Title generation disabled - feature not ready yet
        // if (!customName.isEmpty()) {
        //     dropItem.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
        //         Component.nullToEmpty(customName).copy().append(Utils.getRandomTitle()).withStyle(ChatFormatting.DARK_PURPLE));
        // }

        // Smart core recovery: Try to send to owner's inventory if nearby and in survival
        Player owner = (Player) this.getOwner();
        boolean sentToInventory = false;

        if (owner != null && !owner.getAbilities().instabuild) {
            // Survival mode only - check if owner is nearby
            double distanceToOwner = this.distanceTo(owner);
            if (distanceToOwner <= SharedConfigs.Common.SmartCoreRetrievalDistance) {
                // Owner is nearby - try to add to inventory if there's space
                if (owner.getInventory().getFreeSlot() >= 0) {
                    sentToInventory = owner.getInventory().add(dropItem);

                    if (sentToInventory && dropItem.isEmpty()) {
                        // Successfully sent to inventory - play sound at owner location
                        playCoreRecoverySound(owner);
                        return; // Don't drop on floor
                    }
                }
            }
        }

        // Creative mode OR inventory full OR owner not nearby OR not sent to inventory - drop on floor
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
        if(stack.is(Items.ENCHANTED_BOOK)) return false;
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
        handleProtectionLevelUpInteraction(stack, player);
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

    /**
     * Handles enchanted book feeding to increase robot protection values.
     * <p>
     * <b>Architecture:</b> Extracts protection enchantments from enchanted books
     * and converts them to robot protection points using percentage-based contribution.
     * <p>
     * <b>Supported Enchantments:</b>
     * <ul>
     * <li>Fire Protection → Fire Protection stat</li>
     * <li>Blast Protection → Blast Protection stat</li>
     * <li>Feather Falling → Fall Protection stat</li>
     * <li>Projectile Protection → Projectile Protection stat</li>
     * <li>Protection (generic) → Random protection type</li>
     * </ul>
     * <p>
     * <b>Formula:</b> protectionGain = enchantmentLevel × 0.25 × maxProtection
     * <p>
     * <b>Design Decision:</b> Books are fully consumed on success. Multi-enchanted
     * books apply all valid protections. Feeding is prevented if already at max.
     *
     * @param stack item stack to check
     * @param player player feeding the book
     */
    protected void handleProtectionLevelUpInteraction(ItemStack stack, Player player) {
        // Check if feature is enabled
        if (!SharedConfigs.Common.EnableEnchantedBookProtection) return;

        // Check if item is enchanted book
        if (!stack.is(Items.ENCHANTED_BOOK)) return;

        // Server-side only
        if (level().isClientSide) return;

        // Extract and apply enchantments
        processEnchantedBook(stack, player);
    } // handleProtectionLevelUpInteraction()

    /**
     * Processes enchanted book and applies protection enchantments.
     * <p>
     * <b>Implementation:</b> Extracts enchantments using 1.21.1 DataComponents API,
     * calculates protection gains, updates robot stats, and provides feedback.
     *
     * @param stack enchanted book stack
     * @param player player feeding the book
     */
    protected void processEnchantedBook(ItemStack stack, Player player) {
        // Get enchantments from book using 1.21.1 API
        var enchantments = stack.get(DataComponents.STORED_ENCHANTMENTS);
        if (enchantments == null || enchantments.isEmpty()) {
            player.displayClientMessage(
                    Component.literal("This book has no enchantments").withStyle(ChatFormatting.RED),
                    true
            );
            return;
        }

        boolean appliedAny = false;
        StringBuilder feedbackMessage = new StringBuilder();

        // Get enchantment registry
        var enchantmentRegistry = level().registryAccess().registryOrThrow(Registries.ENCHANTMENT);

        // Process each enchantment
        for (var entry : enchantments.entrySet()) {
            var enchantmentHolder = entry.getKey();
            int level = entry.getIntValue();

            // Get the enchantment key
            var enchantmentKey = enchantmentRegistry.getKey(enchantmentHolder.value());
            if (enchantmentKey == null) continue;

            String enchantmentPath = enchantmentKey.getPath();
            boolean applied = false;
            String protectionName = "";
            int gainedPoints = 0;

            // Check enchantment type and apply
            if (enchantmentPath.equals("fire_protection")) {
                if (EnchantmentProtectionCalculator.canApplyProtection(getFireProtection(), SharedConfigs.Common.ProtectionLimitFire)) {
                    gainedPoints = EnchantmentProtectionCalculator.calculateProtectionGain(level, SharedConfigs.Common.ProtectionLimitFire);
                    int oldValue = getFireProtection();
                    int newValue = EnchantmentProtectionCalculator.applyProtectionGain(oldValue, gainedPoints, SharedConfigs.Common.ProtectionLimitFire);
                    setFireProtection(newValue);
                    protectionName = "Fire Protection";
                    applied = true;
                }
            } else if (enchantmentPath.equals("blast_protection")) {
                if (EnchantmentProtectionCalculator.canApplyProtection(getBlastProtection(), SharedConfigs.Common.ProtectionLimitBlast)) {
                    gainedPoints = EnchantmentProtectionCalculator.calculateProtectionGain(level, SharedConfigs.Common.ProtectionLimitBlast);
                    int oldValue = getBlastProtection();
                    int newValue = EnchantmentProtectionCalculator.applyProtectionGain(oldValue, gainedPoints, SharedConfigs.Common.ProtectionLimitBlast);
                    setBlastProtection(newValue);
                    protectionName = "Blast Protection";
                    applied = true;
                }
            } else if (enchantmentPath.equals("feather_falling")) {
                if (EnchantmentProtectionCalculator.canApplyProtection(getFallProtection(), SharedConfigs.Common.ProtectionLimitFall)) {
                    gainedPoints = EnchantmentProtectionCalculator.calculateProtectionGain(level, SharedConfigs.Common.ProtectionLimitFall);
                    int oldValue = getFallProtection();
                    int newValue = EnchantmentProtectionCalculator.applyProtectionGain(oldValue, gainedPoints, SharedConfigs.Common.ProtectionLimitFall);
                    setFallProtection(newValue);
                    protectionName = "Fall Protection";
                    applied = true;
                }
            } else if (enchantmentPath.equals("projectile_protection")) {
                if (EnchantmentProtectionCalculator.canApplyProtection(getProjectileProtection(), SharedConfigs.Common.ProtectionLimitProjectile)) {
                    gainedPoints = EnchantmentProtectionCalculator.calculateProtectionGain(level, SharedConfigs.Common.ProtectionLimitProjectile);
                    int oldValue = getProjectileProtection();
                    int newValue = EnchantmentProtectionCalculator.applyProtectionGain(oldValue, gainedPoints, SharedConfigs.Common.ProtectionLimitProjectile);
                    setProjectileProtection(newValue);
                    protectionName = "Projectile Protection";
                    applied = true;
                }
            } else if (enchantmentPath.equals("protection")) {
                // Generic protection - apply to random non-maxed type
                // Build list of available (non-maxed) protection types
                java.util.List<EnchantmentProtectionCalculator.ProtectionType> availableTypes = new java.util.ArrayList<>();

                if (EnchantmentProtectionCalculator.canApplyProtection(getFireProtection(), SharedConfigs.Common.ProtectionLimitFire)) {
                    availableTypes.add(EnchantmentProtectionCalculator.ProtectionType.FIRE);
                }
                if (EnchantmentProtectionCalculator.canApplyProtection(getFallProtection(), SharedConfigs.Common.ProtectionLimitFall)) {
                    availableTypes.add(EnchantmentProtectionCalculator.ProtectionType.FALL);
                }
                if (EnchantmentProtectionCalculator.canApplyProtection(getBlastProtection(), SharedConfigs.Common.ProtectionLimitBlast)) {
                    availableTypes.add(EnchantmentProtectionCalculator.ProtectionType.BLAST);
                }
                if (EnchantmentProtectionCalculator.canApplyProtection(getProjectileProtection(), SharedConfigs.Common.ProtectionLimitProjectile)) {
                    availableTypes.add(EnchantmentProtectionCalculator.ProtectionType.PROJECTILE);
                }

                // Only apply if there are available types
                if (!availableTypes.isEmpty()) {
                    var randomType = availableTypes.get(random.nextInt(availableTypes.size()));

                    switch (randomType) {
                        case FIRE:
                            gainedPoints = EnchantmentProtectionCalculator.calculateProtectionGain(level, SharedConfigs.Common.ProtectionLimitFire);
                            int fireOldValue = getFireProtection();
                            int fireNewValue = EnchantmentProtectionCalculator.applyProtectionGain(fireOldValue, gainedPoints, SharedConfigs.Common.ProtectionLimitFire);
                            setFireProtection(fireNewValue);
                            protectionName = "Fire Protection";
                            applied = true;
                            break;
                        case FALL:
                            gainedPoints = EnchantmentProtectionCalculator.calculateProtectionGain(level, SharedConfigs.Common.ProtectionLimitFall);
                            int fallOldValue = getFallProtection();
                            int fallNewValue = EnchantmentProtectionCalculator.applyProtectionGain(fallOldValue, gainedPoints, SharedConfigs.Common.ProtectionLimitFall);
                            setFallProtection(fallNewValue);
                            protectionName = "Fall Protection";
                            applied = true;
                            break;
                        case BLAST:
                            gainedPoints = EnchantmentProtectionCalculator.calculateProtectionGain(level, SharedConfigs.Common.ProtectionLimitBlast);
                            int blastOldValue = getBlastProtection();
                            int blastNewValue = EnchantmentProtectionCalculator.applyProtectionGain(blastOldValue, gainedPoints, SharedConfigs.Common.ProtectionLimitBlast);
                            setBlastProtection(blastNewValue);
                            protectionName = "Blast Protection";
                            applied = true;
                            break;
                        case PROJECTILE:
                            gainedPoints = EnchantmentProtectionCalculator.calculateProtectionGain(level, SharedConfigs.Common.ProtectionLimitProjectile);
                            int projectileOldValue = getProjectileProtection();
                            int projectileNewValue = EnchantmentProtectionCalculator.applyProtectionGain(projectileOldValue, gainedPoints, SharedConfigs.Common.ProtectionLimitProjectile);
                            setProjectileProtection(projectileNewValue);
                            protectionName = "Projectile Protection";
                            applied = true;
                            break;
                    }
                }
            }

            if (applied) {
                appliedAny = true;
                if (feedbackMessage.length() > 0) feedbackMessage.append(", ");
                feedbackMessage.append(protectionName).append(" +").append(gainedPoints);
            }
        }

        if (!appliedAny) {
            player.displayClientMessage(
                    Component.literal("All protections are already at maximum!").withStyle(ChatFormatting.YELLOW),
                    true
            );
            return;
        }

        // Consume book
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        // Visual and audio feedback
        level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENCHANTMENT_TABLE_USE, this.getSoundSource(), 1.0F, 1.0F);

        // Show feedback message
        player.displayClientMessage(
                Component.literal(feedbackMessage.toString()).withStyle(ChatFormatting.GREEN),
                true
        );
    } // processEnchantedBook()

    // -- Custom Methods --

    /**
     * Claims accumulated experience from a killed entity.
     * <p>
     * <b>Architecture:</b> Called when an entity dies to award all accumulated exp
     * from previous attacks. Prevents exp farming from immortal entities.
     * <p>
     * <b>Use Case:</b> Should be called from entity death event handlers or when
     * detecting entity death in combat.
     *
     * @param entityId UUID of the entity that died
     */
    public void claimAccumulatedExp(@NotNull java.util.UUID entityId) {
        if (this.level().isClientSide) return;

        int accumulatedExp = expTracker.claimExp(entityId);
        if (accumulatedExp > 0) {
            addExp(accumulatedExp);
        }
    } // claimAccumulatedExp ()

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
    public java.util.Optional<LevelFeature> getLevelSystem() {
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
                        displayGeneralMessage(isNotificationEnabled(), true);
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
            this.setNotificationEnabled(invertBoolean(isNotificationEnabled()));
            if(isNotificationEnabled()) displayNotification(LovelyConstant.MSG_NOTIFICATION, LovelyConstant.MSG_ON, isNotificationEnabled());
            else displayNotification(LovelyConstant.MSG_NOTIFICATION, LovelyConstant.MSG_OFF, true);
        }

        if(stack.getItem() == (Items.BOOK)) displayGeneralMessage(true, false);
        if(stack.getItem() == (Items.WRITABLE_BOOK)) displayEnchantmentMessage();
        return true;
    } // handleDisplayInteraction ()

    protected void handleAutoAttack(ItemStack stack){
        if (!canInteractAutoAttack(stack)) return;
        if (getCurrentState() == EntityState.Defense) return;
        setAutoAttack(invertBoolean(getAutoAttack()));

        if(getAutoAttack()) displayNotification(LovelyConstant.MSG_AUTO_ATTACK, LovelyConstant.MSG_ON, isNotificationEnabled());
        else displayNotification(LovelyConstant.MSG_AUTO_ATTACK, LovelyConstant.MSG_OFF, isNotificationEnabled());
    } // handleAutoAttack ()

    protected boolean handleBaseDefenseState(ItemStack stack){
        if(!canInteractGuardMode(stack) || getCurrentState() == EntityState.Defense) return false;
        setCurrentState(EntityState.Defense);
        setOrderedToSit(false);
        setAutoAttack(true);
        this.setBaseX((float)this.getBlockX());
        this.setBaseY((float)this.getBlockY());
        this.setBaseZ((float)this.getBlockZ());
        displayNotification(LovelyConstant.MSG_BASE_DEFENCE, isNotificationEnabled());
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
        if (SharedConfigs.Common.EnableSmartCoreRetrieval && !this.level().isClientSide) {
            Player owner = (Player) this.getOwner();

            if (owner != null && owner.isAlive()) {
                double distance = this.distanceTo(owner);
                double maxDistance = SharedConfigs.Common.SmartCoreRetrievalDistance;

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
                    String robotName = getEntityName();
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
        InternalLogic.displayInfo(this, (LovelyIdentifier.getMessageTranslation(LovelyConstant.MSG_BAR)), false);
        if(showLevelUp) InternalLogic.displayInfo(this, (LovelyIdentifier.getMessageTranslation(LovelyConstant.MSG_LEVEL_UP)), false);
        if(this.getCustomName() != null) InternalLogic.displayInfo(this, LovelyIdentifier.getVariantTranslation(nativeEntity.getKey()).append(": " + this.getCustomName().getString()), false);
        else InternalLogic.displayInfo(this, LovelyIdentifier.getVariantTranslation(nativeEntity.getKey()), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyConstant.MSG_LEVEL).append(": " + this.getCurrentLevel()             + "/" + this.getMaxLevel()), false);
        getLevelSystem().ifPresent(feature -> InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyConstant.MSG_EXPERIENCE).append(": " + this.getExp()                 + "/" + feature.getExpForLevel(this.getCurrentLevel())), false));
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyConstant.MSG_HEALTH).append(": " + (int)Math.floor(this.getHealth()) + "/" + (int)this.getMaxHealth()), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyConstant.MSG_ATTACK).append(": " + this.getAttackDamage()), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyConstant.MSG_DEFENCE).append(": " + this.getArmorLevel()), false);
    } // displayGeneralMessage ()

    public void displayEnchantmentMessage() {
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyConstant.MSG_BAR), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyConstant.MSG_ENCHANTMENT), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyConstant.MSG_LOOTING).append(": " + this.getLooting()                            + "/" + SharedConfigs.Common.MaxLootEnchantment), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyConstant.MSG_FIRE_PROTECTION).append(": " + this.getFireProtection()             + "/" + SharedConfigs.Common.ProtectionLimitFire), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyConstant.MSG_FALL_PROTECTION).append(": " + this.getFallProtection()             + "/" + SharedConfigs.Common.ProtectionLimitFall), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyConstant.MSG_BLAST_PROTECTION).append(": " + this.getBlastProtection()           + "/" + SharedConfigs.Common.ProtectionLimitBlast), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyConstant.MSG_PROJECTILE_PROTECTION).append(": " + this.getProjectileProtection() + "/" + SharedConfigs.Common.ProtectionLimitProjectile), false);
    } // displayEnchantmentMessage ()

    /**
     * Gets the item that should be dropped when robot dies.
     * <p>
     * <b>Loader Variation:</b> Different loaders may have different item access
     * patterns (direct reference vs Supplier.get()) or different core items.
     *
     * @return ItemStack to drop on death
     */
    public ItemStack getDropItem() {
        if (this.nativeEntity.getFeature(DropFeature.class).isPresent())
            return this.nativeEntity.getFeature(DropFeature.class).get().createDropStack();
        return null;
    } // getDropItem ()

    /**
     * Gets the pickup item for this robot variant.
     * <p>
     * <b>Loader Variation:</b> Fabric uses constructor-injected item, while
     * Forge/NeoForge use variant-based lookup with Supplier.get() calls.
     *
     * @return Item that can be used to pick up this robot
     */
    public Item getPickupItem() {
        if (this.nativeEntity.getFeature(PickupFeature.class).isPresent())
            return this.nativeEntity.getFeature(PickupFeature.class).get().getPickupItem();
        else return getDropItem().getItem();
    } // getPickupItem ()

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

    /**
     * Plays sound effect at owner location when core is sent to inventory on death.
     * <p>
     * <b>Audio Feedback:</b> Uses ITEM_PICKUP sound to indicate successful smart
     * core recovery. Provides immediate feedback that core was automatically retrieved.
     * <p>
     * <b>Usage:</b> Called from handleItemDrop() when core is sent to owner's
     * inventory instead of dropping on floor.
     *
     * @param owner the robot's owner player
     */
    private void playCoreRecoverySound(Player owner) {
        this.level().playSound(
                null,
                owner.blockPosition(),
                SoundEvents.ITEM_PICKUP,
                net.minecraft.sounds.SoundSource.PLAYERS,
                1.0F,
                1.0F
        );
    } // playCoreRecoverySound ()

    // -- Headphone Support Methods --

    /**
     * Checks if this robot type supports headphone rendering.
     * <p>
     * <b>Architecture:</b> Allows robot variants to opt-out of headphone rendering
     * if the model doesn't support it or if it conflicts with the design.
     *
     * @return true if headphones can be rendered on this robot type
     */
    public boolean supportsHeadphones() {
        // Default implementation - most robots support headphones
        // Override in specific robot classes to disable if needed
        return true;
    } // supportsHeadphones()

    /**
     * Checks if headphones are currently enabled for this entity.
     * <p>
     * <b>Architecture:</b> Entity-specific headphone setting that can be toggled
     * per robot instance. Allows players to customize individual robots.
     *
     * @return true if headphones should be rendered on this entity
     */
    public boolean hasHeadphonesEnabled() {
        // Default implementation - headphones enabled by default
        // This could be extended to read from entity NBT data or config
        return true;
    } // hasHeadphonesEnabled()

    // -- Pickup Mechanics --

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
     * <b>Inventory Management:</b>
     * - Creative mode: Always drops on floor (no inventory check)
     * - Survival mode with space: Adds to inventory
     * - Survival mode full: Drops on floor
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

        // Create spawn item from current robot state
        ItemStack spawnItem = createSpawnItemFromEntity();

        // Creative mode: Always drop on floor, don't check inventory
        if (player.getAbilities().instabuild) {
            dropItemAtLocation(spawnItem, this.getX(), this.getY(), this.getZ());
        } else {
            // Survival mode: Try to add to inventory if there's space
            if (player.getInventory().getFreeSlot() >= 0) {
                // Has space - add to inventory
                boolean added = player.getInventory().add(spawnItem);

                // If somehow not fully added, drop remainder
                if (!spawnItem.isEmpty()) {
                    dropItemAtLocation(spawnItem, this.getX(), this.getY(), this.getZ());
                }
            } else {
                // Inventory full - drop on floor
                dropItemAtLocation(spawnItem, this.getX(), this.getY(), this.getZ());
            }
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

        unregisterRobot();

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
        // Use the spawn item provided during entity construction, or fall back to setDropItem()
        Item item = this.getPickupItem();
        final ItemStack spawnItemStack = new ItemStack(item, 1);

        // Use Data Components instead of NBT tags (1.21.1)
        CompoundTag nbt = new CompoundTag();

        String customName = Utils.getEntityCustomName(this);
        if (!customName.isEmpty()) nbt.putString(LovelyConstant.STAT_CUSTOM_NAME, customName);

        String ownerName = Utils.getEntityOwnerName(this);
        if (!ownerName.isEmpty()) nbt.putString(LovelyConstant.STAT_OWNER, ownerName);

        nbt.putString(LovelyConstant.STAT_TYPE, this.nativeEntity.getKey());
        nbt.putInt(LovelyConstant.STAT_COLOR, this.getTextureID());

        nbt.putInt(LovelyConstant.STAT_MAX_LEVEL, this.getMaxLevel());
        nbt.putInt(LovelyConstant.STAT_LEVEL, this.getCurrentLevel());
        nbt.putInt(LovelyConstant.STAT_EXP, this.getExp());

        // Note: Current health is intentionally NOT included so crafted robots spawn at max health

        nbt.putInt(LovelyConstant.STAT_FIRE_PROTECTION, this.getFireProtection());
        nbt.putInt(LovelyConstant.STAT_FALL_PROTECTION, this.getFallProtection());
        nbt.putInt(LovelyConstant.STAT_BLAST_PROTECTION, this.getBlastProtection());
        nbt.putInt(LovelyConstant.STAT_PROJECTILE_PROTECTION, this.getProjectileProtection());

        // Store custom data in DataComponents.CUSTOM_DATA
        spawnItemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));

        // Apply custom name with title (commented for future use)
        // if (!customName.isEmpty()) {
        //     spawnItemStack.setHoverName(
        //         Component.nullToEmpty(customName)
        //             .copy()
        //             .append(Utils.getRandomTitle())
        //             .withStyle(ChatFormatting.DARK_PURPLE)
        //     );
        // }

        return spawnItemStack;
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
     * <b>Creative Mode:</b> In creative mode, returns false to skip auto-retrieval
     * and drop core on ground instead. This allows creative players to see and
     * collect cores normally without them disappearing into the void.
     *
     * @param owner the robot's owner player
     * @return true if core was successfully added to inventory, false if inventory full or creative mode
     */
    private boolean attemptAutoRetrieval(Player owner) {
        // Creative mode - skip auto-retrieval and drop on ground instead
        if (owner.getAbilities().instabuild) {
            return false; // Return false to trigger normal drop behavior
        }

        // Survival/Adventure mode - check inventory space
        // Create core item with full data using Data Components (1.21.1)
        final ItemStack coreStack = getDropItem();
        CompoundTag nbt = new CompoundTag();

        // Populate NBT (same as handleItemDrop)
        String customName = Utils.getEntityCustomName(this);
        if (!customName.isEmpty()) nbt.putString(LovelyConstant.STAT_CUSTOM_NAME, customName);

        String ownerName = Utils.getEntityOwnerName(this);
        if (!ownerName.isEmpty()) nbt.putString(LovelyConstant.STAT_OWNER, ownerName);

        nbt.putString(LovelyConstant.STAT_TYPE, this.nativeEntity.getKey());
        nbt.putInt(LovelyConstant.STAT_COLOR, this.getTextureID());
        nbt.putInt(LovelyConstant.STAT_MAX_LEVEL, this.getMaxLevel());
        nbt.putInt(LovelyConstant.STAT_LEVEL, this.getCurrentLevel());
        nbt.putInt(LovelyConstant.STAT_EXP, this.getExp());

        // Note: Current health is intentionally NOT included so crafted robots spawn at max health

        nbt.putInt(LovelyConstant.STAT_FIRE_PROTECTION, this.getFireProtection());
        nbt.putInt(LovelyConstant.STAT_FALL_PROTECTION, this.getFallProtection());
        nbt.putInt(LovelyConstant.STAT_BLAST_PROTECTION, this.getBlastProtection());
        nbt.putInt(LovelyConstant.STAT_PROJECTILE_PROTECTION, this.getProjectileProtection());

        // Store custom data in DataComponents.CUSTOM_DATA
        coreStack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(nbt));

        // Apply custom name with title (commented for future use)
        // if (!customName.isEmpty()) {
        //     coreStack.setHoverName(Component.nullToEmpty(customName)
        //         .copy().append(Utils.getRandomTitle())
        //         .withStyle(ChatFormatting.DARK_PURPLE));
        // }

        // Try to add to inventory
        boolean added = owner.getInventory().add(coreStack);

        // Check if the entire stack was added (stack should be empty)
        if (added && coreStack.isEmpty()) {
            // Success - spawn particles and send message
            InternalParticle.HappyVillager(this);
            playRetrievalSound(owner);

            String robotName = getEntityName();
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

    // -- Spawn Mechanics --

    /**
     * Handles robot initialization after spawning.
     * <p>
     * <b>Common Logic:</b> Performs standard robot setup that applies to all
     * loaders: registry registration, data validation, and initial state setup.
     * <p>
     * <b>Usage:</b> Call from loader-specific spawn handling after entity creation
     * and before applying custom data.
     */
    protected void handlePostSpawnInitialization() {
        // Registry registration is handled by the base entity during normal lifecycle
        // ensureRegistered() is called automatically during tick() and data loading

        // Validate entity data consistency
        EntitySpawnHelper.validateEntityData(this);
    } // handlePostSpawnInitialization()

    /**
     * Handles robot cleanup before removal.
     * <p>
     * <b>Common Logic:</b> Performs standard cleanup that applies to all loaders:
     * registry cleanup, resource cleanup, and state finalization.
     * <p>
     * <b>Usage:</b> Call from loader-specific removal handling or death events.
     */
    protected void handlePreRemovalCleanup() {
        // Registry cleanup is handled by the base LovelyRobotEntity
        // Additional cleanup can be added here if needed
    } // handlePreRemovalCleanup()

} // Class: RobotEntity
