package net.heriazone.lovelylib.hzlib.api.entity;

import net.heriazone.lovelylib.Lovely;
import net.heriazone.lovelylib.api.entity.features.*;
import net.heriazone.lovelylib.api.registry.*;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.NativeEntityType;
import net.heriazone.lovelylib.common.entity.enums.*;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.lovelylib.common.shared.LovelyIdentifier;
import net.heriazone.lovelylib.hzlib.api.entity.data.*;
import net.heriazone.lovelylib.hzlib.api.entity.internal.*;
import net.heriazone.lovelylib.hzlib.framework.entity.data.*;
import net.heriazone.lovelylib.hzlib.framework.entity.enums.*;
import net.heriazone.lovelylib.hzlib.framework.utils.Version;
import net.heriazone.lovelylib.hzlib.utils.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.*;

import java.util.Objects;

import static net.heriazone.lovelylib.hzlib.utils.Utils.invertBoolean;


public abstract class InternalEntity extends TamableAnimal implements IReadWriteNBT {

    // -- Variables --

    protected static final EntityDataAccessor<Integer> TEXTURE_ID = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> NOTIFICATION = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> MODEL_ID = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);

    protected int waryTimer = 0, autoHealTimer = 0;
    protected boolean combatMode = false, autoHeal = false, canWander = false;
    public InternalEntityType<?> nativeEntity;
    protected EntityModel model = EntityModel.Default;

    // -- Stat Objects --

    /**
     * <p>Encapsulates combat-related statistics.<p>
     * <p>
     * <b>Architecture:</b> Replaces scattered level/exp/hp fields with cohesive
     * data object, enabling feature-based attribute calculations.
     */
    protected CombatStats combatStats = new CombatStats(); // TODO: Replace this for the EntityData

    /**
     * <p>Encapsulates protection statistics.<p>
     * <p>
     * <b>Architecture:</b> Centralizes fire/fall/blast/projectile protection
     * levels with validation support.
     */
    protected ProtectionStats protectionStats = new ProtectionStats();

    /**
     * <p>Encapsulates enchantment statistics.<p>
     * <p>
     * <b>Architecture:</b> Manages looting/sharpness/knockback enchantment
     * levels calculated from robot level.
     */
    protected EnchantmentStats enchantmentStats = new EnchantmentStats();

    // -- NBT Handlers --

    /**
     * <p>Handles NBT serialization for combat stats.<p>
     * <p>
     * <b>Design Decision:</b> Separates serialization logic from data model,
     * enabling format changes without modifying stat classes.
     */
    protected CombatStatsNBT combatStatsNBT = new CombatStatsNBT(combatStats);

    /**
     * <p>Handles NBT serialization for protection stats.<p>
     */
    protected ProtectionStatsNBT protectionStatsNBT = new ProtectionStatsNBT(protectionStats);

    /**
     * <p>Handles NBT serialization for enchantment stats.<p>
     */
    protected EnchantmentStatsNBT enchantmentStatsNBT = new EnchantmentStatsNBT(enchantmentStats);

    // -- Experience Tracking --

    /**
     * <p>Tracks pending experience from attacked entities.<p>
     * <p>
     * <b>Architecture:</b> Prevents infinite exp gain from immortal entities by
     * accumulating exp per entity and only awarding on death.
     * <p>
     * <b>Performance:</b> Automatically purges stale entries every 5 minutes to
     * prevent memory leaks.
     */
    protected ExperienceTracker expTracker = new ExperienceTracker();

    // -- Properties --

    // TEXTURE

    public ResourceLocation getTexture() {
        // Use NativeEntityType's color texture system
        if (nativeEntity instanceof NativeEntityType) {
            return ((NativeEntityType) nativeEntity).getColorTexture(EntityTexture.byId(getTextureID()));
        }
        // Fallback for non-robot entities
        return nativeEntity.getTextures().get(EntityVariantTexture.DEFAULT);
    } // getTexture ()

    public int getTextureID() {
        int value = 0;  // Default to WHITE
        // Use NativeEntityType's random color system
        if (nativeEntity instanceof NativeEntityType) {
            value = ((NativeEntityType) nativeEntity).getRandomColorId();
        }
        try {value = this.entityData.get(TEXTURE_ID);}
        catch (Exception ignored) {}
        return value;
    } // getTextureID ()

    public void setTexture(int value) {
        // Use NativeEntityType's color checking system
        if (nativeEntity instanceof NativeEntityType) {
            if (((NativeEntityType) nativeEntity).hasColor(EntityTexture.byId(value))) {
                this.entityData.set(TEXTURE_ID, value);
            }
            // Ignore invalid colors for this entity type
        } else {
            this.entityData.set(TEXTURE_ID, value);
        }
    } // setTexture ()

    public void setTexture(EntityTexture value) { setTexture(value.getId()); } // setTexture ()

    // MODEL

    /**
     * Retrieves the current model state with client-server synchronization.
     * <p>
     * <b>Architecture:</b> Uses EntityDataAccessor for automatic client-server sync,
     * ensuring model changes are immediately visible on all clients.
     * <p>
     * <b>Fallback:</b> Returns Default model if synchronization fails or data is corrupted.
     *
     * @return current EntityModel (Default or Armed)
     */
    public EntityModel getModel() {
        try {
            return EntityModel.byId(this.entityData.get(MODEL_ID));
        } catch (Exception ignored) {
            return EntityModel.Default;
        }
    } // getModel ()

    /**
     * Updates model state with automatic client-server synchronization.
     * <p>
     * <b>Architecture:</b> Sets both EntityDataAccessor (for network sync) and local
     * field (for server-side logic). This ensures immediate server-side availability
     * while triggering client updates.
     * <p>
     * <b>Network Impact:</b> Only syncs when model actually changes, minimizing
     * network traffic during combat mode transitions.
     *
     * @param model new EntityModel to apply
     */
    public void setModel(EntityModel model) {
        this.entityData.set(MODEL_ID, model.getId());
        this.model = model; // Keep local field in sync for server-side logic
    } // setModel ()

    public ResourceLocation getCurrentModel() {
        // Map EntityModel to EntityVariantModel using synchronized getter
        EntityVariantModel variant = (getModel() == EntityModel.Armed) ? EntityVariantModel.ARMED : EntityVariantModel.DEFAULT;
        return nativeEntity.getModels().get(variant);
    } // getCurrentModel ()

    // ANIMATOR

    public ResourceLocation getAnimator() {
        return nativeEntity.getAnimators().get(EntityVariantAnimator.DEFAULT);
    } // getAnimator ()

    // STATE

    public int getCurrentStateID() {
        int value = EntityState.Standby.getId();
        try {value = this.entityData.get(STATE);}
        catch (Exception ignored) {}
        return value;
    } // getCurrentStateID ()

    public EntityState getCurrentState() {
        EntityState value = EntityState.Standby;
        try {value = EntityState.byId(this.entityData.get(STATE));}
        catch (Exception ignored) {}
        return value;
    } // getCurrentState ()

    public void setCurrentState(EntityState value){
        this.entityData.set(STATE, value.getId());
    } // setCurrentMode ()

    public void setCurrentState(int value){
        this.entityData.set(STATE, value);
    } // setCurrentState ()

    // NOTIFICATION

    public boolean getNotification() {
        boolean value = true;
        try {value = this.entityData.get(NOTIFICATION);}
        catch (Exception ignored) {}
        return value;
    } // getNotification ()

    public void setNotification(boolean value) {
        this.entityData.set(NOTIFICATION, value);
    } // setNotification ()

    // COMBAT MODE

    /**
     * Checks if robot is in combat (wary) mode.
     * <p>
     * <b>Usage:</b> Determines if robot is actively engaged in combat or recently
     * was in combat. Combat mode is activated when robot attacks or is attacked,
     * and persists for WaryTime ticks after combat ends.
     * <p>
     * <b>Behavior Impact:</b> Affects follow distance, movement priorities, and
     * visual model (armed vs default stance).
     *
     * @return true if robot is in combat mode (wary)
     */
    public boolean isWary() {
        return combatMode;
    } // isWary ()

    // WANDER PERMISSION

    /**
     * Checks if robot is allowed to wander.
     * <p>
     * <b>Usage:</b> Controlled by AiConditionalWanderGoal to signal when wandering
     * is active. When true, AiFollowOwnerGoal yields priority to allow wandering.
     * <p>
     * <b>Behavior Impact:</b> Disables follow goal when owner is stationary and
     * robot is wandering nearby.
     *
     * @return true if robot can wander (owner stationary, conditions met)
     */
    public boolean canWander() {
        return canWander;
    } // canWander ()

    /**
     * Sets whether robot is allowed to wander.
     * <p>
     * <b>Usage:</b> Set by AiConditionalWanderGoal when wander conditions are met
     * or when owner starts moving again.
     *
     * @param value true to allow wandering, false to disable
     */
    public void setCanWander(boolean value) {
        canWander = value;
    } // setCanWander ()

    // -- Simplified Stat Accessors --

    /**
     * <p>Gets robot's current level.<p>
     * <p>
     * <b>Architecture:</b> Delegates to combatStats, providing clean interface
     * without exposing internal stat object structure.
     *
     * @return current robot level
     */
    public int getLevel() {
        return combatStats.getLevel();
    } // getLevel ()

    /**
     * <p>Sets robot's level and recalculates attributes.<p>
     * <p>
     * <b>State Impact:</b> Triggers full attribute recalculation including HP,
     * attack, defense, armor, enchantments, and optional protection upgrades.
     *
     * @param level new robot level
     */
    public void setLevel(int level) {
        combatStats.setLevel(level);
        recalculateAttributes();
    } // setLevel ()

    /**
     * <p>Gets robot's current experience points.<p>
     *
     * @return current experience
     */
    public int getExperience() {
        return combatStats.getExperience();
    } // getExperience ()

    /**
     * <p>Sets robot's experience and triggers auto-level-up if sufficient.<p>
     * <p>
     * <b>Behavior:</b> Automatically consumes experience and increments level
     * when sufficient exp is available, repeating until insufficient exp remains.
     *
     * @param experience new experience value
     */
    public void setExperience(int experience) {
        combatStats.setExperience(experience);
        // Auto-level-up logic will be handled by command system
    } // setExperience ()

    // Stats Accessors

    protected CombatLevelFeature getCombatFeature () {
        if (nativeEntity == null) return null;
        if (nativeEntity.getFeature(CombatLevelFeature.class).isPresent()) return nativeEntity.getFeature(CombatLevelFeature.class).get();
        else return null;
    } // getCombatFeature ()

    protected EnchantmentFeature getEnchantmentFeature () {
        if (nativeEntity == null) return null;
        if (nativeEntity.getFeature(EnchantmentFeature.class).isPresent()) return nativeEntity.getFeature(EnchantmentFeature.class).get();
        else return null;
    } // getEnchantmentFeature ()

    protected ProtectionFeature getProtectionFeature () {
        if (nativeEntity == null) return null;
        if (nativeEntity.getFeature(ProtectionFeature.class).isPresent()) return nativeEntity.getFeature(ProtectionFeature.class).get();
        else return null;
    } // getProtectionFeature ()

    public int getMaxLevel() {
        // Use NativeEntityType's convenience accessor which delegates to LevelFeature
        if (nativeEntity instanceof NativeEntityType) {
            return ((NativeEntityType) nativeEntity).getMaxLevel();
        }
        return 0;
    } // getMaxLevel ()

    public int getHp() {
        int value = 0;
        if (nativeEntity.hasFeature(CombatLevelFeature.class)) {
            CombatLevelFeature feature = getCombatFeature();
            value = feature.calculateHp(combatStats.getLevel());
        }
        return value;
    } // getHp ()

    public int getAttackDamage() {
        int value = 0;
        if (nativeEntity.hasFeature(CombatLevelFeature.class)) {
            CombatLevelFeature feature = getCombatFeature();
            value = feature.calculateAttack(combatStats.getLevel());
        }
        return value;
    } // getAttackDamage ()

    public int getArmorLevel() {
        int value = 0;
        if (nativeEntity.hasFeature(CombatLevelFeature.class)) {
            CombatLevelFeature feature = getCombatFeature();
            value = (int)feature.calculateArmor(combatStats.getLevel());
        }
        return value;
    } // getArmorLevel ()

    public int getArmorToughnessLevel() {
        int value = 0;
        if (nativeEntity.hasFeature(CombatLevelFeature.class)) {
            CombatLevelFeature feature = getCombatFeature();
            value = (int)feature.calculateArmorToughness(combatStats.getLevel());
        }
        return value;
    } // getArmorToughnessLevel ()

    public int getLooting() {
        int value = 0;
        if (nativeEntity.hasFeature(EnchantmentFeature.class)) {
            EnchantmentFeature feature = getEnchantmentFeature();
            value = feature.calculateLooting(combatStats.getLevel());
        }
        return value;
    } // getLooting ()

    // Protection accessors

    /**
     * <p>Gets fire protection level.<p>
     *
     * @return fire protection level
     */
    public int getFireProtection() {
        return protectionStats.getFireProtection();
    } // getFireProtection ()

    /**
     * <p>Sets fire protection level.<p>
     *
     * @param level new fire protection level
     */
    public void setFireProtection(int level) {
        protectionStats.setFireProtection(level);
    } // setFireProtection ()

    /**
     * <p>Gets fall protection level.<p>
     *
     * @return fall protection level
     */
    public int getFallProtection() {
        return protectionStats.getFallProtection();
    } // getFallProtection ()

    /**
     * <p>Sets fall protection level.<p>
     *
     * @param level new fall protection level
     */
    public void setFallProtection(int level) {
        protectionStats.setFallProtection(level);
    } // setFallProtection ()

    /**
     * <p>Gets blast protection level.<p>
     *
     * @return blast protection level
     */
    public int getBlastProtection() {
        return protectionStats.getBlastProtection();
    } // getBlastProtection ()

    /**
     * <p>Sets blast protection level.<p>
     *
     * @param level new blast protection level
     */
    public void setBlastProtection(int level) {
        protectionStats.setBlastProtection(level);
    } // setBlastProtection ()

    /**
     * <p>Gets projectile protection level.<p>
     *
     * @return projectile protection level
     */
    public int getProjectileProtection() {
        return protectionStats.getProjectileProtection();
    } // getProjectileProtection ()

    /**
     * <p>Sets projectile protection level.<p>
     *
     * @param level new projectile protection level
     */
    public void setProjectileProtection(int level) {
        protectionStats.setProjectileProtection(level);
    } // setProjectileProtection ()

    // Enchantment accessors

    /**
     * <p>Gets looting enchantment level.<p>
     *
     * @return looting level
     */
    public int getLootingLevel() {
        return enchantmentStats.getLootingLevel();
    } // getLootingLevel ()

    /**
     * <p>Sets looting enchantment level.<p>
     *
     * @param level new looting level
     */
    public void setLootingLevel(int level) {
        enchantmentStats.setLootingLevel(level);
    } // setLootingLevel ()

    /**
     * <p>Gets sharpness enchantment level.<p>
     *
     * @return sharpness level
     */
    public int getSharpnessLevel() {
        return enchantmentStats.getSharpnessLevel();
    } // getSharpnessLevel ()

    /**
     * <p>Sets sharpness enchantment level.<p>
     *
     * @param level new sharpness level
     */
    public void setSharpnessLevel(int level) {
        enchantmentStats.setSharpnessLevel(level);
    } // setSharpnessLevel ()

    /**
     * <p>Gets knockback enchantment level.<p>
     *
     * @return knockback level
     */
    public int getKnockbackLevel() {
        return enchantmentStats.getKnockbackLevel();
    } // getKnockbackLevel ()

    /**
     * <p>Sets knockback enchantment level.<p>
     *
     * @param level new knockback level
     */
    public void setKnockbackLevel(int level) {
        enchantmentStats.setKnockbackLevel(level);
    } // setKnockbackLevel ()

    // -- Attribute Recalculation --

    /**
     * <p>Recalculates all entity attributes based on current level and features.<p>
     * <p>
     * <b>Architecture:</b> Coordinates feature-based calculations for combat stats,
     * enchantments, and protections. Delegates to attached features on entity type.
     * <p>
     * <b>State Impact:</b> Updates HP, attack, defense, armor, armor toughness,
     * enchantment levels, and optionally auto-upgrades protections based on level.
     * <p>
     * <b>Design Decision:</b> Centralized recalculation ensures consistency across
     * level-ups, NBT loading, and command-based stat changes.
     */
    protected void recalculateAttributes() {
        // Get CombatLevelFeature from entity type
        if (nativeEntity != null) {
            // Calculate combat attributes if feature exists
            if (nativeEntity.hasFeature(CombatLevelFeature.class)) {
                CombatLevelFeature combatFeature = getCombatFeature();

                int level = combatStats.getLevel();

                // Calculate HP, attack, defense
                int maxHp = combatFeature.calculateHp(level);
                int attack = combatFeature.calculateAttack(level);
                int defense = combatFeature.calculateDefense(level);

                // Calculate armor and armor toughness
                double armor = combatFeature.calculateArmor(level);
                double calculatedToughness = combatFeature.calculateArmorToughness(level);

                // Add base toughness to calculated toughness (from surplus defense)
                double baseToughness = nativeEntity.getData().getArmorToughness();
                double armorToughness = baseToughness + calculatedToughness;

                // Update combat stats
                combatStats.setMaxHp(maxHp);
                combatStats.setAttack(attack);
                combatStats.setDefense(defense);

                // Update entity attributes
                InternalLogic.handleLevel(this, maxHp, attack, armor, armorToughness);

                // Ensure current HP doesn't exceed new max HP
                this.setHealth(maxHp);
                combatStats.setCurrentHp((int) this.getHealth());
            }

            // Calculate enchantment levels if feature exists
            if (nativeEntity.getFeature(EnchantmentFeature.class).isPresent()) {
                EnchantmentFeature enchantFeature = getEnchantmentFeature();

                int level = combatStats.getLevel();

                // Calculate enchantment levels
                int looting = enchantFeature.calculateLooting(level);
                int sharpness = enchantFeature.calculateSharpness(level);
                int knockback = enchantFeature.calculateKnockback(level);

                // Update enchantment stats
                enchantmentStats.setLootingLevel(looting);
                enchantmentStats.setSharpnessLevel(sharpness);
                enchantmentStats.setKnockbackLevel(knockback);
            }

            // Auto-upgrade protections if feature exists and enabled
            if (nativeEntity.getFeature(ProtectionFeature.class).isPresent()) {
                ProtectionFeature protectionFeature = getProtectionFeature();

                int level = combatStats.getLevel();

                // Calculate auto-protection levels (only if auto-upgrade enabled)
                int fireProtection = protectionFeature.calculateAutoFireProtection(level, protectionStats.getFireProtection());
                int fallProtection = protectionFeature.calculateAutoFallProtection(level, protectionStats.getFallProtection());
                int blastProtection = protectionFeature.calculateAutoBlastProtection(level, protectionStats.getBlastProtection());
                int projectileProtection = protectionFeature.calculateAutoProjectileProtection(level, protectionStats.getProjectileProtection());

                // Update protection stats (only if auto-upgrade increased them)
                if (fireProtection > protectionStats.getFireProtection())
                    protectionStats.setFireProtection(fireProtection);

                if (fallProtection > protectionStats.getFallProtection())
                    protectionStats.setFallProtection(fallProtection);

                if (blastProtection > protectionStats.getBlastProtection())
                    protectionStats.setBlastProtection(blastProtection);

                if (projectileProtection > protectionStats.getProjectileProtection())
                    protectionStats.setProjectileProtection(projectileProtection);
            }
        }
    } // recalculateAttributes ()

    // -- Registry Lifecycle --

    /**
     * <p>Registers robot in owner's registry.<p>
     * <p>
     * <b>Architecture:</b> Called when robot is tamed, establishing tracking
     * relationship between owner and robot for spawn limits and management commands.
     * <p>
     * <b>State Impact:</b> Creates registry entry with WeakReference to this entity,
     * enabling live position/health/stats access without cached data synchronization.
     * <p>
     * <b>Design Decision:</b> Only registers on server side to prevent client-side
     * registry pollution and ensure single source of truth.
     */
    protected void registerRobot() {
        // Only register on server side
        if (this.level().isClientSide || !this.isTame() || this.getOwnerUUID() == null) return;
        try {
            ServerLevel serverLevel = (ServerLevel) this.level();
            RobotRegistryManager.getRegistry(serverLevel)
                    .registerRobot(new RobotRegistryEntry(
                            this.getUUID(),
                            this.getOwnerUUID(),
                            this,
                            nativeEntity.getKey()
                    ));
        } catch (Exception e) {
            // Log error but don't fail taming
            Lovely.LOGGER.error("Failed to register robot {} for owner {}",
                    this.getUUID(), this.getOwnerUUID(), e);
        }
    } // registerRobot ()

    /**
     * Ensures robot is registered in owner registry.
     * <p>
     * <b>Use Case:</b> Called when entity loads from NBT (world reload, chunk load).
     * Handles two scenarios:
     * 1. Entry exists (loaded from SavedData) - updates entity reference
     * 2. Entry doesn't exist (new robot) - creates new entry
     * <p>
     * <b>Thread Safety:</b> Only runs on server side to avoid client-side issues.
     */
    protected void ensureRegistered() {
        if (this.level() instanceof ServerLevel serverLevel) {
            OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(serverLevel);

            RobotRegistryEntry existingEntry = registry.getRobotById(this.getUUID());

            if (existingEntry != null) {
                // Entry exists (loaded from disk) - update entity reference
                existingEntry.setEntity(this);
            } else {
                // Not registered - register now
                String robotType = this.getType().getDescriptionId();
                RobotRegistryEntry registryEntry =
                        new RobotRegistryEntry(
                                this.getUUID(),
                                this.getOwnerUUID(),
                                this,
                                robotType
                        );
                registry.registerRobot(registryEntry);

                // Mark dirty to save new registration
                RobotRegistryManager.markDirty(serverLevel);
            }
        }
    } // ensureRegistered()

    /**
     * <p>Unregisters robot from owner's registry.<p>
     * <p>
     * <b>Architecture:</b> Called when robot is removed or dies, cleaning up
     * tracking relationship and freeing registry slot for new robots.
     * <p>
     * <b>State Impact:</b> Removes registry entry. Robot will no longer appear
     * in owner list commands or count toward spawn limits.
     * <p>
     * <b>Persistence:</b> Marks registry as dirty to save removal to disk.
     */
    protected void unregisterRobot() {
        // Only unregister on server side
        if (this.level().isClientSide) return;
        try {
            ServerLevel serverLevel = (ServerLevel) this.level();
            boolean removed = RobotRegistryManager.getRegistry(serverLevel)
                    .unregisterRobot(this.getUUID());

            // Mark dirty if robot was actually removed
            if (removed) {
                RobotRegistryManager.markDirty(serverLevel);
            }
        } catch (Exception e) {
            // Log error but don't fail removal
            Lovely.LOGGER.error("Failed to unregister robot {}", this.getUUID(), e);
        }
    } // unregisterRobot ()

    /**
     * <p>Updates registry timestamp for this robot.<p>
     * <p>
     * <b>Architecture:</b> Called periodically (every 20 ticks) to maintain
     * "last seen" timestamp for offline detection and cleanup.
     * <p>
     * <b>Performance:</b> Lightweight operation (O(1) lookup + timestamp update).
     * Batched to once per second to minimize overhead.
     */
    protected void updateRegistryTimestamp() {
        // Only update on server side
        if (this.level().isClientSide || !this.isTame() || this.getOwnerUUID() == null) return;
        try {
            ServerLevel serverLevel = (ServerLevel) this.level();
            RobotRegistryEntry entry =
                    RobotRegistryManager.getRegistry(serverLevel).getRobotById(this.getUUID());

            if (entry != null) {
                entry.updateTimestamp();
            }
        } catch (Exception e) {
            // Silently fail - timestamp update is not critical
        }
    } // updateRegistryTimestamp ()

    // -- Constructor --

    protected InternalEntity(EntityType<? extends TamableAnimal> entityType, Level world, InternalEntityType<?> nativeEntityType) {
        super(entityType, world);
        this.nativeEntity = nativeEntityType;

        // Use NativeEntityType's random color system
        if (nativeEntity != null) {
            int randomColorId = ((NativeEntityType) nativeEntity).getRandomColorId();
            this.setTexture(randomColorId);
        } else {
            this.setTexture(0);  // Default to WHITE
        }

        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(nativeEntity.getData().getMaxHealth());
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(nativeEntity.getData().getAttackDamage());
        this.getAttribute(Attributes.ATTACK_SPEED).setBaseValue(nativeEntity.getData().getAttackSpeed());
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(nativeEntity.getData().getMoveSpeed());
        this.getAttribute(Attributes.ARMOR).setBaseValue(nativeEntity.getData().getArmor());
        this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(nativeEntity.getData().getArmorToughness());

        this.recalculateAttributes();

        // Initialize health to max health after all attributes are set
        float maxHealth = this.getMaxHealth();
        this.setHealth(maxHealth);
    } // Constructor: InternalEntity ()

    // -- Navigation Override --

    /**
     * Creates custom navigation for robots with improved edge traversal.
     * <p>
     * <b>Design Decision:</b> Overrides default GroundPathNavigation to configure
     * pathfinding behavior. Allows robots to confidently walk off block edges when
     * following owners, preventing spinning behavior at ledges.
     * <p>
     * <b>Behavior Impact:</b> Robots will walk off edges without hesitation,
     * similar to wolves and other tameable mobs. Improves follow responsiveness
     * on terrain with elevation changes.
     * <p>
     * <b>Implementation:</b> Uses custom GroundPathNavigation subclass that
     * prevents constant path recalculation on edges and allows walking off ledges.
     *
     * @param level the world level
     * @return configured ground path navigation
     */
    @Override
    protected PathNavigation createNavigation(Level level) {
        GroundPathNavigation navigation = new GroundPathNavigation(this, level);
        navigation.setCanFloat(true);
        navigation.setCanOpenDoors(true);
        // Allow walking off edges - prevents spinning on ledges
        navigation.setCanPassDoors(true);
        return navigation;
    } // createNavigation()

    // -- Entity Lifecycle --

    /**
     * Handles entity removal and ensures registry cleanup.
     * <p>
     * <b>Architecture:</b> Overrides Entity.remove() to intercept all removal scenarios
     * including death, despawn, chunk unload, and manual removal. Ensures robot is
     * unregistered from owner's registry to prevent memory leaks and incorrect spawn limits.
     * <p>
     * <b>Registry Cleanup:</b> Calls unregisterRobot() before super.remove() to ensure
     * registry entry is removed while entity data is still accessible.
     * <p>
     * <b>Thread Safety:</b> Only unregisters on server side to avoid client-side issues.
     * <p>
     * <b>Use Cases:</b>
     * - Robot dies (RemovalReason.KILLED)
     * - Robot picked up (RemovalReason.DISCARDED)
     * - Robot despawns (RemovalReason.DISCARDED)
     * - Chunk unloads (RemovalReason.UNLOADED_TO_CHUNK)
     * - Manual removal via commands (RemovalReason.DISCARDED)
     *
     * @param reason the reason for entity removal
     */
    @Override
    public void remove(RemovalReason reason) {
        // Unregister from owner's registry before removal
        // This ensures registry doesn't accumulate dead/removed robots
        if (!this.level().isClientSide && this.isTame() && this.getOwnerUUID() != null) {
            unregisterRobot();
        }

        super.remove(reason);
    } // remove()

    // -- Inherited Methods --

    // Sound

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundEvents.GENERIC_HURT;
    } // getHurtSound ()

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    } // getDeathSound ()

    // INITIALISE

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor levelAccessor, @NotNull DifficultyInstance instance, @NotNull MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData) {
        rotate(Rotation.getRandom(this.getRandom()));

        recalculateAttributes();

        // Refresh navigation to pick up new movement speed
        // AI goals cache the speed attribute, so we need to refresh after changing it
        this.getNavigation().stop();
        return super.finalizeSpawn(levelAccessor, instance, mobSpawnType, spawnGroupData);
    } // finalizeSpawn ()

    @Override
    protected void dropEquipment() {
        handleItemDrop();
        super.dropEquipment();
    } // dropEquipment ()

    @Nullable @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {
        return null;
    } // getBreedOffspring ()

    // COMBAT

    @Override
    public boolean doHurtTarget(@NotNull Entity target) {
        handleAttackTarget(target);
        return super.doHurtTarget(target);
    } // doHurtTarget ()

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        boolean result = handleDamage(source, amount);

        final Entity entity = source.getEntity();
        if (entity != null && !(entity instanceof Player) && !(entity instanceof Arrow)) amount = (amount + 1.0f) / 2.0f;

        return !result ? false : super.hurt(source, amount);
    } // hurt ()

    // INTERACTION

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();
        if (this.level().isClientSide) {
            boolean flag = this.isOwnedBy(player) || this.isTame() || canInteractWithItems(stack) && !this.isTame();
            return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            if (this.isTame() && this.isOwnedBy(player)) {
                if (!(item instanceof DyeItem)) {
                    InteractionResult interactionresult = super.mobInteract(player, hand);
                    if ((!interactionresult.consumesAction()) && this.isOwnedBy(player)) {
                        handleInteract(stack, player);
                        return InteractionResult.SUCCESS;
                    }

                    return interactionresult;
                }

                return handleItemInteraction (stack, player);
            }

            return super.mobInteract(player, hand);
        }
    } // mobInteract ()


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TEXTURE_ID, EntityTexture.RANDOM.getId());
        builder.define(STATE, EntityState.Follow.getId());
        builder.define(NOTIFICATION, true);
        builder.define(MODEL_ID, EntityModel.Default.getId());
    } // defineSynchedData ()

    @Override
    public void addAdditionalSaveData(CompoundTag dataNBT) {
        super.addAdditionalSaveData(dataNBT);

        dataNBT.putInt("TextureID", this.getTextureID());
        dataNBT.putInt("State", this.getCurrentStateID());
        dataNBT.putBoolean("Notification", this.getNotification());
        dataNBT.putInt("Model", this.getModel().getId());

        // Use new EntityData system for consolidated stat storage
        EntityData entityData = new EntityData(combatStats, protectionStats, enchantmentStats);
        entityData.toParentNBT(dataNBT);

        // Save entity-specific data
        CompoundTag customData = new CompoundTag();
        customData.putString("VersionNBT", Lovely.DATA_VERSION.toString());
        writeToNBT(customData);
        dataNBT.put("CustomEntityData", customData);
    } // addAdditionalSaveData ()

    @Override
    public void readAdditionalSaveData(CompoundTag dataNBT) {
        super.readAdditionalSaveData(dataNBT);

        // Use EntityDataMigration to handle both old and new formats
        EntityData loadedData = EntityDataMigration.migrate(dataNBT, Lovely.DATA_VERSION);

        // Update stat objects from loaded data
        this.combatStats = loadedData.getCombatStats();
        this.protectionStats = loadedData.getProtectionStats();
        this.enchantmentStats = loadedData.getEnchantmentStats();

        // Recreate NBT handlers with loaded stats
        this.combatStatsNBT = new CombatStatsNBT(combatStats);
        this.protectionStatsNBT = new ProtectionStatsNBT(protectionStats);
        this.enchantmentStatsNBT = new EnchantmentStatsNBT(enchantmentStats);

        // Read entity-specific custom data
        if (dataNBT.contains("CustomEntityData")) {
            CompoundTag customData = dataNBT.getCompound("CustomEntityData");

            Version version = dataNBT.contains("VersionNBT") && Version.isValidVersion(customData.getString("VersionNBT"))
                    ? new Version(customData.getString("VersionNBT"))
                    : Lovely.DATA_VERSION;
            readFromNBT(customData, version);
        } else {
            this.setTexture(dataNBT.getInt("TextureID"));
            this.setCurrentState(dataNBT.getInt("State"));
            this.setNotification(dataNBT.getBoolean("Notification"));
            this.setModel(EntityModel.byId(dataNBT.getInt("Model")));
        }

        // Recalculate attributes after loading to ensure consistency
        recalculateAttributes();
    } // readAdditionalSaveData ()

    @Override
    public CompoundTag writeToNBT(@NotNull CompoundTag dataNBT) {
        dataNBT.putInt("TextureID", this.getTextureID());
        dataNBT.putInt("State", this.getCurrentStateID());
        dataNBT.putBoolean("Notification", this.getNotification());
        dataNBT.putInt("Model", this.getModel().getId());
        return dataNBT;
    } // writeToNBT

    @Override
    public void readFromNBT(@NotNull CompoundTag dataNBT, @NotNull Version version) {
        this.setTexture(dataNBT.getInt("TextureID"));
        this.setCurrentState(dataNBT.getInt("State"));
        this.setNotification(dataNBT.getBoolean("Notification"));
        this.setModel(EntityModel.byId(dataNBT.getInt("Model")));
    } // readFromNBT ()

    // -- Custom Methods --

    public abstract ItemStack getDropItem();
    public abstract Item getPickupItem();

    protected abstract void handleItemDrop();

    protected abstract void handleAttackTarget(@NotNull Entity target);

    protected abstract boolean handleDamage (@NotNull DamageSource source, float amount);

    protected boolean canInteractWithItems(ItemStack stack){
        return false;
    } // canInteractWithItems ()

    protected void handleInteract (ItemStack stack, Player player) {
        handleSit(stack);
        handleState(stack);
    } // handleInteract ()

    protected InteractionResult handleItemInteraction (ItemStack stack, Player player) {
        return InteractionResult.PASS;
    } // handleItemInteraction ()

    protected void handleAutoHeal () {
        if(this.getHealth() < this.getMaxHealth()) autoHeal = true;
        if(this.level().isClientSide &&!autoHeal) return;

        if(autoHealTimer != 0) {
            autoHealTimer--;
        } else {
            final float healValue = this.getHealth() / 16.0F;
            this.heal(healValue);
            autoHeal = false;
            autoHealTimer = SharedConfigs.Common.HealInterval;
        }
    } // handleAutoHeal ()

    protected void handleActivateCombatMode () {
        if(!combatMode) combatMode = true;
        waryTimer = SharedConfigs.Common.WaryTime;
    } // handleActivateCombatMode ()

    protected void handleCombatMode() {
        // Clear target if it's dead or removed
        if(this.getTarget() != null && (!this.getTarget().isAlive() || this.getTarget().isRemoved())) {
            this.setTarget(null);
        }

        // Check if robot has a valid living target and activate combat mode
        // Only consider swinging if there's actually a target to swing at
        if((this.swinging && this.getTarget() != null) || (this.getTarget() != null && this.getTarget().isAlive())) {
            handleActivateCombatMode();
        }

        if(this.level().isClientSide && !combatMode) return;

        if(waryTimer != 0) {
            if(this.getModel() != EntityModel.Armed) this.setModel(EntityModel.Armed);
            waryTimer--;
        } else {
            if(this.getModel() != EntityModel.Default) this.setModel(EntityModel.Default);
            combatMode = false;
        }
    } // handleCombatMode ()

    public void handleTame(Player player) {
        // Set ownership without taming particles (spawning, not taming)
        this.tame(player);
        this.setTame(true, false);
        this.setOrderedToSit(false);

        // Spawn POOF particles (spawn effect)
        InternalParticle.Poof(this);

        // Play spawn sound effect (totem activation sound) - volume scales with entity size
        float volume = (float) Math.max(0.5F, Math.min(2.0F, this.getBbWidth() * this.getBbHeight()));
        this.level().playSound(
                null,
                this.blockPosition(),
                net.minecraft.sounds.SoundEvents.TOTEM_USE,
                net.minecraft.sounds.SoundSource.NEUTRAL,
                volume,
                1.2F
        );

        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyConstant.MSG_OWNER).append(Component.literal(": " + player.getName().getString())), true);

        // Register robot in owner's registry
        registerRobot();
    } // handleTame ()

    protected boolean handleTexture(ItemStack stack, Player player) {
        return false;
    } // handleTexture ()

    protected void handleSit(ItemStack stack) {
        if(!canInteractWithItems(stack)) return;
        setOrderedToSit(invertBoolean(isOrderedToSit()));
        this.jumping = false;
        this.navigation.stop();
        this.setTarget(null);
    } // handleSit ()

    protected void handleState(ItemStack stack) {
        if (handleFollowState(stack)) return;
        if (handleStandbyState(stack)) return;
    } // handleState ()

    protected boolean handleStandbyState(ItemStack stack){
        if(!canInteractWithItems(stack) || getCurrentState() == EntityState.Standby) return false;
        setCurrentState(EntityState.Standby);
        displayNotification(LovelyConstant.MSG_STANDBY, getNotification());
        return true;
    } // handleStandbyState ()

    protected boolean handleFollowState(ItemStack stack){
        if(!canInteractWithItems(stack) || getCurrentState() == EntityState.Follow) return false;
        setCurrentState(EntityState.Follow);
        displayNotification(LovelyConstant.MSG_FOLLOW, getNotification());
        return true;
    } // handleFollowState ()

    // DISPLAY

    protected void displayNotification(String notification, String message, boolean display) {
        if(!display) return;
        String customName = Utils.getEntityCustomName(this);
        if(!customName.isEmpty()) InternalLogic.displayInfo(this, Component.nullToEmpty(customName + " | ").copy().append(LovelyIdentifier.getMessageTranslation(notification).append(Component.nullToEmpty(": ").copy().append(LovelyIdentifier.getMessageTranslation(message)))), true);
        else InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(notification).append(Component.nullToEmpty(": ").copy().append(LovelyIdentifier.getMessageTranslation(message))), true);
    } // displayNotification ()

    protected void displayNotification(String message, boolean display) {
        if(!display) return;
        String customName = Utils.getEntityCustomName(this);
        if(!customName.isEmpty()) InternalLogic.displayInfo(this, Component.nullToEmpty(customName + " | ").copy().append(LovelyIdentifier.getMessageTranslation(message)), true);
        else InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(message), true);
    } // displayNotification ()

    protected void displayExtra() {
        Component debug = null;
        MutableComponent entityName = !Utils.getEntityCustomName(this).isEmpty() ? Component.literal(Utils.getEntityCustomName(this)) : LovelyIdentifier.getTranslation(Objects.requireNonNull(EntityVariant.byName(nativeEntity.getKey())));
        if(combatMode && getNotification()) {
            debug = entityName.append(Component.nullToEmpty(": ").copy().append(LovelyIdentifier.getMessageTranslation(LovelyConstant.MSG_WARY)));
            if(waryTimer < 10) debug = debug.copy().append(": 0" + waryTimer + " ");
            else debug = debug.copy().append(": " + waryTimer + " ");
        }

        if(autoHeal && getNotification()) {
            if(debug != null) debug = debug.copy().append(LovelyIdentifier.getMessageTranslation(LovelyConstant.MSG_HEAL));
            else debug = entityName.append(Component.nullToEmpty(": ").copy().append(LovelyIdentifier.getMessageTranslation(LovelyConstant.MSG_HEAL)));

            if(autoHealTimer < 10) debug = debug.copy().append(": 0" + autoHealTimer + " ");
            else debug = debug.copy().append(": " + autoHealTimer + " ");
            if(this.getHealth() < 10) debug = debug.copy().append("| 0" + this.getHealth());
            else debug = debug.copy().append("| " + (int)this.getHealth());
        }
        if(debug != null) InternalLogic.displayInfo(this, debug, true);
    } // displayExtra ()

} // Class InternalEntity