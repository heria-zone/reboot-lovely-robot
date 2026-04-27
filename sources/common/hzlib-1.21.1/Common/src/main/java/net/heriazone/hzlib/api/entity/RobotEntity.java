package net.heriazone.hzlib.api.entity;

import net.heriazone.hzlib.framework.entity.data.CombatLevelStats;
import net.heriazone.hzlib.framework.entity.data.EnchantmentStats;
import net.heriazone.hzlib.framework.entity.data.ProtectionStats;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * <p>Specialized entity class for robot-based mods with level progression and combat systems.<p>
 * <p>
 * <b>Architecture:</b> Extends InternalEntity with robot-specific features including
 * level progression, experience tracking, enchantment systems, and protection mechanics.
 * Serves as the base class for InternalEntity and other robot implementations.
 * <p>
 * <b>Design Decision:</b> Uses composition with data classes (CombatLevelStats,
 * ProtectionStats, EnchantmentStats) rather than direct field storage, enabling
 * feature-based attribute calculations and consistent stat management.
 * <p>
 * <b>Level System:</b> Implements experience-based leveling with automatic stat
 * scaling and level-up rewards. Experience formula: Base (50) × Multiplier (2) × Level.
 * <p>
 * <b>Thread Safety:</b> EntityDataAccessor fields are synchronized automatically.
 * Combat stats modifications should occur on server thread.
 */
public abstract class RobotEntity extends InternalEntity {

    // -- Entity Data Accessors --

    /**
     * <p>Synchronized robot level (1-200).<p>
     */
    protected static final EntityDataAccessor<Integer> ROBOT_LEVEL = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);

    /**
     * <p>Synchronized experience points.<p>
     */
    protected static final EntityDataAccessor<Float> EXPERIENCE_POINTS = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.FLOAT);

    /**
     * <p>Synchronized maximum health points.<p>
     */
    protected static final EntityDataAccessor<Float> MAX_HEALTH_POINTS = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.FLOAT);

    /**
     * <p>Synchronized attack damage.<p>
     */
    protected static final EntityDataAccessor<Float> ATTACK_DAMAGE = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.FLOAT);

    /**
     * <p>Synchronized defense value.<p>
     */
    protected static final EntityDataAccessor<Float> DEFENSE_VALUE = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.FLOAT);

    // Protection Stats
    protected static final EntityDataAccessor<Integer> FIRE_PROTECTION = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> FALL_PROTECTION = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> BLAST_PROTECTION = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> PROJECTILE_PROTECTION = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);

    // Enchantment Stats
    protected static final EntityDataAccessor<Integer> LOOTING_LEVEL = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> SHARPNESS_LEVEL = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> KNOCKBACK_LEVEL = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.INT);

    // Robot Preferences
    protected static final EntityDataAccessor<Boolean> AUTO_HEAL_ENABLED = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> COMBAT_MODE_ENABLED = SynchedEntityData.defineId(RobotEntity.class, EntityDataSerializers.BOOLEAN);

    // -- Combat Data Objects --

    /**
     * <p>Combat level statistics container.<p>
     * <p>
     * <b>Design Decision:</b> Cached locally for performance, synchronized with
     * EntityDataAccessor fields during updates to maintain client-server consistency.
     */
    protected CombatLevelStats combatStats;

    /**
     * <p>Protection statistics container.<p>
     */
    protected ProtectionStats protectionStats;

    /**
     * <p>Enchantment statistics container.<p>
     */
    protected EnchantmentStats enchantmentStats;

    // -- Constructor --

    /**
     * Creates robot entity with specified entity type and world.
     * <p>
     * <b>State Impact:</b> Initializes combat data objects and entity data accessors
     * with default robot values.
     *
     * @param entityType Minecraft entity type for registration
     * @param world world instance where entity exists
     */
    protected RobotEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
        
        // Initialize combat data objects
        this.combatStats = new CombatLevelStats();
        this.protectionStats = new ProtectionStats();
        this.enchantmentStats = new EnchantmentStats();
    } // Constructor: RobotEntity ()

    // -- Entity Data Initialization --

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        
        // Combat stats
        builder.define(ROBOT_LEVEL, 1);
        builder.define(EXPERIENCE_POINTS, 0.0f);
        builder.define(MAX_HEALTH_POINTS, 20.0f);
        builder.define(ATTACK_DAMAGE, 5.0f);
        builder.define(DEFENSE_VALUE, 5.0f);
        
        // Protection stats
        builder.define(FIRE_PROTECTION, 0);
        builder.define(FALL_PROTECTION, 0);
        builder.define(BLAST_PROTECTION, 0);
        builder.define(PROJECTILE_PROTECTION, 0);
        
        // Enchantment stats
        builder.define(LOOTING_LEVEL, 0);
        builder.define(SHARPNESS_LEVEL, 0);
        builder.define(KNOCKBACK_LEVEL, 0);
        
        // Robot preferences
        builder.define(AUTO_HEAL_ENABLED, true);
        builder.define(COMBAT_MODE_ENABLED, false);
    } // defineSynchedData ()

    // -- Level and Experience System --

    /**
     * Returns current robot level.
     *
     * @return level (1-200)
     */
    public int getRobotLevel() {
        try {
            return this.entityData.get(ROBOT_LEVEL);
        } catch (Exception e) {
            return 1;
        }
    } // getRobotLevel ()

    /**
     * Sets robot level with validation and stat updates.
     * <p>
     * <b>State Impact:</b> Updates cached combat stats and synchronizes with
     * EntityDataAccessor for client-server consistency.
     *
     * @param level new level (clamped to 1-200)
     */
    public void setRobotLevel(int level) {
        int clampedLevel = Math.max(1, Math.min(200, level));
        this.entityData.set(ROBOT_LEVEL, clampedLevel);
        this.combatStats.setLevel(clampedLevel);
        updateStatsFromLevel();
    } // setRobotLevel ()

    /**
     * Returns current experience points.
     *
     * @return experience points
     */
    public float getExperiencePoints() {
        try {
            return this.entityData.get(EXPERIENCE_POINTS);
        } catch (Exception e) {
            return 0.0f;
        }
    } // getExperiencePoints ()

    /**
     * Sets experience points with validation.
     *
     * @param experience new experience points (minimum 0.0)
     */
    public void setExperiencePoints(float experience) {
        float clampedExp = Math.max(0.0f, experience);
        this.entityData.set(EXPERIENCE_POINTS, clampedExp);
        this.combatStats.setExperience((int) clampedExp);
    } // setExperiencePoints ()

    /**
     * Adds experience points and handles level-up progression.
     * <p>
     * <b>Level-Up Logic:</b> Uses standard robot experience formula and automatically
     * increases level when thresholds are reached. Triggers stat recalculation.
     *
     * @param expGain experience points to add
     * @return true if level increased, false otherwise
     */
    public boolean addExperience(float expGain) {
        if (expGain <= 0 || getRobotLevel() >= 200) return false;

        float currentExp = getExperiencePoints() + expGain;
        int oldLevel = getRobotLevel();
        int newLevel = oldLevel;

        // Check for level-up using standard formula: Base (50) × Multiplier (2) × Level
        while (newLevel < 200) {
            float requiredExp = 50.0f * 2.0f * newLevel;
            if (currentExp >= requiredExp) {
                currentExp -= requiredExp;
                newLevel++;
            } else {
                break;
            }
        }

        setExperiencePoints(currentExp);
        if (newLevel > oldLevel) {
            setRobotLevel(newLevel);
            onLevelUp(oldLevel, newLevel);
            return true;
        }

        return false;
    } // addExperience ()

    /**
     * Called when robot levels up.
     * <p>
     * <b>Extensibility:</b> Subclasses can override to add level-up rewards,
     * sound effects, or other level-up behaviors.
     *
     * @param oldLevel previous level
     * @param newLevel new level
     */
    protected void onLevelUp(int oldLevel, int newLevel) {
        // Subclasses can override for level-up effects
        updateStatsFromLevel();
    } // onLevelUp ()

    /**
     * Updates combat stats based on current level.
     * <p>
     * <b>Scaling Formula:</b> Base stats + (level - 1) * scaling factor.
     * Health: 20 + (level - 1) * 2, Attack: 5 + (level - 1) * 1, Defense: 5 + (level - 1) * 1.
     */
    protected void updateStatsFromLevel() {
        int level = getRobotLevel();
        
        // Calculate level-based stats
        float newMaxHealth = 20.0f + (level - 1) * 2.0f;
        float newAttack = 5.0f + (level - 1) * 1.0f;
        float newDefense = 5.0f + (level - 1) * 1.0f;
        
        // Update EntityDataAccessor values
        this.entityData.set(MAX_HEALTH_POINTS, newMaxHealth);
        this.entityData.set(ATTACK_DAMAGE, newAttack);
        this.entityData.set(DEFENSE_VALUE, newDefense);
        
        // Update cached combat stats
        this.combatStats.setMaxHp((int) newMaxHealth);
        this.combatStats.setAttack((int) newAttack);
        this.combatStats.setDefense((int) newDefense);
        
        // Update entity max health attribute
        if (this.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH) != null) {
            this.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(newMaxHealth);
        }
    } // updateStatsFromLevel ()

    // -- Combat Stats Access --

    /**
     * Returns current combat level statistics.
     * <p>
     * <b>Thread Safety:</b> Returns defensive copy to prevent external modification.
     *
     * @return copy of combat stats
     */
    public CombatLevelStats getCombatStats() {
        return this.combatStats.copy();
    } // getCombatStats ()

    /**
     * Returns current protection statistics.
     *
     * @return copy of protection stats
     */
    public ProtectionStats getProtectionStats() {
        return this.protectionStats.copy();
    } // getProtectionStats ()

    /**
     * Returns current enchantment statistics.
     *
     * @return copy of enchantment stats
     */
    public EnchantmentStats getEnchantmentStats() {
        return this.enchantmentStats.copy();
    } // getEnchantmentStats ()

    // -- Protection System --

    /**
     * Returns fire protection level.
     *
     * @return fire protection level
     */
    public int getFireProtection() {
        try {
            return this.entityData.get(FIRE_PROTECTION);
        } catch (Exception e) {
            return 0;
        }
    } // getFireProtection ()

    /**
     * Sets fire protection level with validation.
     *
     * @param level new fire protection level (minimum 0)
     */
    public void setFireProtection(int level) {
        int clampedLevel = Math.max(0, level);
        this.entityData.set(FIRE_PROTECTION, clampedLevel);
        this.protectionStats.setFireProtection(clampedLevel);
    } // setFireProtection ()

    /**
     * Returns fall protection level.
     *
     * @return fall protection level
     */
    public int getFallProtection() {
        try {
            return this.entityData.get(FALL_PROTECTION);
        } catch (Exception e) {
            return 0;
        }
    } // getFallProtection ()

    /**
     * Sets fall protection level with validation.
     *
     * @param level new fall protection level (minimum 0)
     */
    public void setFallProtection(int level) {
        int clampedLevel = Math.max(0, level);
        this.entityData.set(FALL_PROTECTION, clampedLevel);
        this.protectionStats.setFallProtection(clampedLevel);
    } // setFallProtection ()

    /**
     * Returns blast protection level.
     *
     * @return blast protection level
     */
    public int getBlastProtection() {
        try {
            return this.entityData.get(BLAST_PROTECTION);
        } catch (Exception e) {
            return 0;
        }
    } // getBlastProtection ()

    /**
     * Sets blast protection level with validation.
     *
     * @param level new blast protection level (minimum 0)
     */
    public void setBlastProtection(int level) {
        int clampedLevel = Math.max(0, level);
        this.entityData.set(BLAST_PROTECTION, clampedLevel);
        this.protectionStats.setBlastProtection(clampedLevel);
    } // setBlastProtection ()

    /**
     * Returns projectile protection level.
     *
     * @return projectile protection level
     */
    public int getProjectileProtection() {
        try {
            return this.entityData.get(PROJECTILE_PROTECTION);
        } catch (Exception e) {
            return 0;
        }
    } // getProjectileProtection ()

    /**
     * Sets projectile protection level with validation.
     *
     * @param level new projectile protection level (minimum 0)
     */
    public void setProjectileProtection(int level) {
        int clampedLevel = Math.max(0, level);
        this.entityData.set(PROJECTILE_PROTECTION, clampedLevel);
        this.protectionStats.setProjectileProtection(clampedLevel);
    } // setProjectileProtection ()

    // -- Enchantment System --

    /**
     * Returns looting enchantment level.
     *
     * @return looting level
     */
    public int getLootingLevel() {
        try {
            return this.entityData.get(LOOTING_LEVEL);
        } catch (Exception e) {
            return 0;
        }
    } // getLootingLevel ()

    /**
     * Sets looting enchantment level with validation.
     *
     * @param level new looting level (minimum 0)
     */
    public void setLootingLevel(int level) {
        int clampedLevel = Math.max(0, level);
        this.entityData.set(LOOTING_LEVEL, clampedLevel);
        this.enchantmentStats.setLootingLevel(clampedLevel);
    } // setLootingLevel ()

    /**
     * Returns sharpness enchantment level.
     *
     * @return sharpness level
     */
    public int getSharpnessLevel() {
        try {
            return this.entityData.get(SHARPNESS_LEVEL);
        } catch (Exception e) {
            return 0;
        }
    } // getSharpnessLevel ()

    /**
     * Sets sharpness enchantment level with validation.
     *
     * @param level new sharpness level (minimum 0)
     */
    public void setSharpnessLevel(int level) {
        int clampedLevel = Math.max(0, level);
        this.entityData.set(SHARPNESS_LEVEL, clampedLevel);
        this.enchantmentStats.setSharpnessLevel(clampedLevel);
    } // setSharpnessLevel ()

    /**
     * Returns knockback enchantment level.
     *
     * @return knockback level
     */
    public int getKnockbackLevel() {
        try {
            return this.entityData.get(KNOCKBACK_LEVEL);
        } catch (Exception e) {
            return 0;
        }
    } // getKnockbackLevel ()

    /**
     * Sets knockback enchantment level with validation.
     *
     * @param level new knockback level (minimum 0)
     */
    public void setKnockbackLevel(int level) {
        int clampedLevel = Math.max(0, level);
        this.entityData.set(KNOCKBACK_LEVEL, clampedLevel);
        this.enchantmentStats.setKnockbackLevel(clampedLevel);
    } // setKnockbackLevel ()

    // -- Robot Preferences --

    /**
     * Returns auto-heal preference.
     *
     * @return true if auto-heal is enabled
     */
    public boolean isAutoHealEnabled() {
        try {
            return this.entityData.get(AUTO_HEAL_ENABLED);
        } catch (Exception e) {
            return true;
        }
    } // isAutoHealEnabled ()

    /**
     * Sets auto-heal preference.
     *
     * @param enabled true to enable auto-heal
     */
    public void setAutoHealEnabled(boolean enabled) {
        this.entityData.set(AUTO_HEAL_ENABLED, enabled);
    } // setAutoHealEnabled ()

    /**
     * Returns combat mode preference.
     *
     * @return true if combat mode is enabled
     */
    public boolean isCombatModeEnabled() {
        try {
            return this.entityData.get(COMBAT_MODE_ENABLED);
        } catch (Exception e) {
            return false;
        }
    } // isCombatModeEnabled ()

    /**
     * Sets combat mode preference.
     *
     * @param enabled true to enable combat mode
     */
    public void setCombatModeEnabled(boolean enabled) {
        this.entityData.set(COMBAT_MODE_ENABLED, enabled);
    } // setCombatModeEnabled ()

    // -- Robot-Specific Interactions --

    @Override
    protected InteractionResult handleSpecificInteractions(Player player, InteractionHand hand, ItemStack stack) {
        // Handle robot-specific interactions
        if (handleLevelingInteractions(player, hand, stack) != InteractionResult.PASS) {
            return InteractionResult.SUCCESS;
        }
        
        if (handlePreferenceInteractions(player, hand, stack) != InteractionResult.PASS) {
            return InteractionResult.SUCCESS;
        }
        
        if (handleEnchantmentInteractions(player, hand, stack) != InteractionResult.PASS) {
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    } // handleSpecificInteractions ()

    /**
     * Handles leveling-related interactions.
     * <p>
     * <b>Experience Items:</b> Experience bottles, emeralds, and other experience-giving items.
     *
     * @param player interacting player
     * @param hand interaction hand
     * @param stack item stack in hand
     * @return interaction result
     */
    protected InteractionResult handleLevelingInteractions(Player player, InteractionHand hand, ItemStack stack) {
        // Experience bottle gives experience
        if (stack.is(Items.EXPERIENCE_BOTTLE)) {
            if (!this.level().isClientSide) {
                addExperience(10.0f);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }
        
        // Emerald gives small experience boost
        if (stack.is(Items.EMERALD)) {
            if (!this.level().isClientSide) {
                addExperience(5.0f);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    } // handleLevelingInteractions ()

    /**
     * Handles preference-related interactions.
     * <p>
     * <b>Preference Items:</b> Buttons for notifications, note blocks for sounds, etc.
     *
     * @param player interacting player
     * @param hand interaction hand
     * @param stack item stack in hand
     * @return interaction result
     */
    protected InteractionResult handlePreferenceInteractions(Player player, InteractionHand hand, ItemStack stack) {
        if (!isOwnedBy(player)) return InteractionResult.PASS;
        
        // Button toggles notifications
        if (stack.is(Items.OAK_BUTTON)) {
            if (!this.level().isClientSide) {
                setNotificationEnabled(!isNotificationEnabled());
            }
            return InteractionResult.SUCCESS;
        }
        
        // Note block toggles auto-heal
        if (stack.is(Items.NOTE_BLOCK)) {
            if (!this.level().isClientSide) {
                setAutoHealEnabled(!isAutoHealEnabled());
            }
            return InteractionResult.SUCCESS;
        }
        
        // Redstone toggles combat mode
        if (stack.is(Items.REDSTONE)) {
            if (!this.level().isClientSide) {
                setCombatModeEnabled(!isCombatModeEnabled());
            }
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    } // handlePreferenceInteractions ()

    /**
     * Handles enchantment-related interactions.
     * <p>
     * <b>Enchantment Items:</b> Books for enchantments, specific items for upgrades.
     *
     * @param player interacting player
     * @param hand interaction hand
     * @param stack item stack in hand
     * @return interaction result
     */
    protected InteractionResult handleEnchantmentInteractions(Player player, InteractionHand hand, ItemStack stack) {
        if (!isOwnedBy(player)) return InteractionResult.PASS;
        
        // Enchanted book can upgrade enchantments
        if (stack.is(Items.ENCHANTED_BOOK)) {
            // Implementation would check book enchantments and upgrade robot
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    } // handleEnchantmentInteractions ()

    // -- NBT Serialization --

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        
        // Combat stats
        nbt.putInt("RobotLevel", getRobotLevel());
        nbt.putFloat("ExperiencePoints", getExperiencePoints());
        nbt.putFloat("MaxHealthPoints", this.entityData.get(MAX_HEALTH_POINTS));
        nbt.putFloat("AttackDamage", this.entityData.get(ATTACK_DAMAGE));
        nbt.putFloat("DefenseValue", this.entityData.get(DEFENSE_VALUE));
        
        // Protection stats
        nbt.putInt("FireProtection", getFireProtection());
        nbt.putInt("FallProtection", getFallProtection());
        nbt.putInt("BlastProtection", getBlastProtection());
        nbt.putInt("ProjectileProtection", getProjectileProtection());
        
        // Enchantment stats
        nbt.putInt("LootingLevel", getLootingLevel());
        nbt.putInt("SharpnessLevel", getSharpnessLevel());
        nbt.putInt("KnockbackLevel", getKnockbackLevel());
        
        // Robot preferences
        nbt.putBoolean("AutoHealEnabled", isAutoHealEnabled());
        nbt.putBoolean("CombatModeEnabled", isCombatModeEnabled());
    } // addAdditionalSaveData ()

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        
        // Combat stats
        if (nbt.contains("RobotLevel")) {
            setRobotLevel(nbt.getInt("RobotLevel"));
        }
        if (nbt.contains("ExperiencePoints")) {
            setExperiencePoints(nbt.getFloat("ExperiencePoints"));
        }
        if (nbt.contains("MaxHealthPoints")) {
            this.entityData.set(MAX_HEALTH_POINTS, nbt.getFloat("MaxHealthPoints"));
        }
        if (nbt.contains("AttackDamage")) {
            this.entityData.set(ATTACK_DAMAGE, nbt.getFloat("AttackDamage"));
        }
        if (nbt.contains("DefenseValue")) {
            this.entityData.set(DEFENSE_VALUE, nbt.getFloat("DefenseValue"));
        }
        
        // Protection stats
        if (nbt.contains("FireProtection")) {
            setFireProtection(nbt.getInt("FireProtection"));
        }
        if (nbt.contains("FallProtection")) {
            setFallProtection(nbt.getInt("FallProtection"));
        }
        if (nbt.contains("BlastProtection")) {
            setBlastProtection(nbt.getInt("BlastProtection"));
        }
        if (nbt.contains("ProjectileProtection")) {
            setProjectileProtection(nbt.getInt("ProjectileProtection"));
        }
        
        // Enchantment stats
        if (nbt.contains("LootingLevel")) {
            setLootingLevel(nbt.getInt("LootingLevel"));
        }
        if (nbt.contains("SharpnessLevel")) {
            setSharpnessLevel(nbt.getInt("SharpnessLevel"));
        }
        if (nbt.contains("KnockbackLevel")) {
            setKnockbackLevel(nbt.getInt("KnockbackLevel"));
        }
        
        // Robot preferences
        if (nbt.contains("AutoHealEnabled")) {
            setAutoHealEnabled(nbt.getBoolean("AutoHealEnabled"));
        }
        if (nbt.contains("CombatModeEnabled")) {
            setCombatModeEnabled(nbt.getBoolean("CombatModeEnabled"));
        }
        
        // Sync cached data objects with loaded values
        syncCachedDataObjects();
    } // readAdditionalSaveData ()

    /**
     * Synchronizes cached data objects with EntityDataAccessor values.
     * <p>
     * <b>Design Decision:</b> Called after NBT loading to ensure cached objects
     * reflect the loaded state.
     */
    protected void syncCachedDataObjects() {
        // Update combat stats
        this.combatStats.setLevel(getRobotLevel());
        this.combatStats.setExperience((int) getExperiencePoints());
        //this.combatStats.setMaxHp((int) this.entityData.get(MAX_HEALTH_POINTS));
        //this.combatStats.setAttack((int) this.entityData.get(ATTACK_DAMAGE));
        //this.combatStats.setDefense((int) this.entityData.get(DEFENSE_VALUE));
        
        // Update protection stats
        this.protectionStats.setFireProtection(getFireProtection());
        this.protectionStats.setFallProtection(getFallProtection());
        this.protectionStats.setBlastProtection(getBlastProtection());
        this.protectionStats.setProjectileProtection(getProjectileProtection());
        
        // Update enchantment stats
        this.enchantmentStats.setLootingLevel(getLootingLevel());
        this.enchantmentStats.setSharpnessLevel(getSharpnessLevel());
        this.enchantmentStats.setKnockbackLevel(getKnockbackLevel());
    } // syncCachedDataObjects ()

    // -- Combat System Integration --

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        // Apply protection-based damage reduction
        float reducedDamage = applyProtectionReduction(damageSource, amount);
        return super.hurt(damageSource, reducedDamage);
    } // hurt ()

    /**
     * Applies protection-based damage reduction.
     * <p>
     * <b>Protection Logic:</b> Different protection types reduce specific damage sources.
     * Fire protection reduces fire damage, blast protection reduces explosion damage, etc.
     *
     * @param damageSource source of damage
     * @param amount original damage amount
     * @return reduced damage amount
     */
    protected float applyProtectionReduction(DamageSource damageSource, float amount) {
        float reduction = 0.0f;
        
        // Apply appropriate protection based on damage source
        if (damageSource.is(net.minecraft.tags.DamageTypeTags.IS_FIRE)) {
            reduction = getFireProtection() * 0.1f; // 10% reduction per level
        } else if (damageSource.is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION)) {
            reduction = getBlastProtection() * 0.1f;
        } else if (damageSource.is(net.minecraft.tags.DamageTypeTags.IS_PROJECTILE)) {
            reduction = getProjectileProtection() * 0.1f;
        } else if (damageSource.is(net.minecraft.tags.DamageTypeTags.IS_FALL)) {
            reduction = getFallProtection() * 0.1f;
        }
        
        // Cap reduction at 80%
        reduction = Math.min(0.8f, reduction);
        return amount * (1.0f - reduction);
    } // applyProtectionReduction ()

    // -- Child Creation --

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob otherParent) {
        // Robots don't breed naturally
        return null;
    } // getBreedOffspring ()

} // Class: RobotEntity