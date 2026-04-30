package net.heriazone.hzlib.api.entity;

import net.heriazone.hzlib.api.entity.features.variants.AnimatorVariantFeature;
import net.heriazone.hzlib.api.entity.features.variants.ModelVariantFeature;
import net.heriazone.hzlib.api.entity.features.variants.TextureVariantFeature;
import net.heriazone.hzlib.api.entity.internal.InternalLogic;
import net.heriazone.hzlib.api.entity.internal.InternalParticle;
import net.heriazone.hzlib.api.entity.variants.interfaces.IAnimatorVariant;
import net.heriazone.hzlib.api.entity.variants.interfaces.IModelVariant;
import net.heriazone.hzlib.api.entity.variants.interfaces.ITextureVariant;
import net.heriazone.hzlib.framework.entity.data.CombatData;
import net.heriazone.hzlib.framework.entity.enums.EntityState;
import net.heriazone.hzlib.utils.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * <p>Shared base entity class for all HZLib-based mods — robots and monsters alike.<p>
 * <p>
 * <b>Architecture:</b> Single shared root below {@code TamableAnimal} for both the
 * robot hierarchy (via HZLib {@code RobotEntity} → lovelylib {@code RobotEntity}) and
 * the monster hierarchy (via monsters_girls {@code MonsterEntity}). Provides:
 * <ul>
 *   <li>String-based variant system ({@code TEXTURE_VARIANT}, {@code MODEL_VARIANT},
 *       {@code ANIMATOR_VARIANT}) — the new system from ADR_011</li>
 *   <li>Base combat stats ({@link CombatData}) — HP, attack, speed, armor for
 *       {@link #createAttributes(InternalEntityType)}</li>
 *   <li>Combat mode and auto-heal timers — shared by both robots and monsters</li>
 *   <li>Complete NBT with backward compatibility migration
 *       ({@code TextureID} int → {@code TextureVariant} string)</li>
 *   <li>Context-aware spawn variant hook ({@link #initializeSpawnVariants})</li>
 *   <li>Generic interaction framework — taming, item handling, display hooks</li>
 *   <li>Registry lifecycle hooks — concrete no-ops, overridden in lovelylib's
 *       {@code RobotEntity} (robot-specific, not propagated to monsters yet)</li>
 * </ul>
 * <p>
 * <b>Abstract contracts subclasses must implement:</b>
 * {@link #handleSpecificInteractions}, {@link #recalculateAttributes},
 * {@link #handleItemDrop}, {@link #handleAttackTarget}, {@link #handleDamage}.
 * <p>
 * <b>What does NOT belong here:</b> Robot leveling ({@code CombatLevelStats}),
 * protection stats, enchantment stats, experience tracking — all of these live in
 * HZLib's {@code RobotEntity} tier.
 * <p>
 * <b>Thread Safety:</b> {@code EntityDataAccessor} fields are synchronized automatically.
 * NBT operations should be performed on the server thread.
 */
public abstract class InternalEntity extends TamableAnimal {

    // -- Entity Data Accessors --

    /**
     * Behavioral state (Follow, Standby, Defense, etc.).
     * Controls AI behavior patterns — distinct from texture/model variants.
     */
    protected static final EntityDataAccessor<Integer> STATE =
            SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);

    /**
     * String-keyed texture variant (e.g., {@code "white"}, {@code "default"}).
     * Replaces the old int-based {@code TEXTURE_ID} system (ADR_012).
     */
    protected static final EntityDataAccessor<String> TEXTURE_VARIANT =
            SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.STRING);

    /**
     * String-keyed model variant (e.g., {@code "default"}, {@code "armed"}).
     * Replaces the old int-based {@code MODEL_ID} system (ADR_012).
     */
    protected static final EntityDataAccessor<String> MODEL_VARIANT =
            SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.STRING);

    /**
     * String-keyed animator variant (e.g., {@code "default"}).
     * New field — lovelylib's old system had no animator variant tracking.
     */
    protected static final EntityDataAccessor<String> ANIMATOR_VARIANT =
            SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.STRING);

    /**
     * Notification preference — whether the entity shows status messages to its owner.
     */
    protected static final EntityDataAccessor<Boolean> NOTIFICATION_ENABLED =
            SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.BOOLEAN);

    // -- Entity Type Reference --

    /**
     * Reference to this entity's type configuration.
     * Provides access to variant features, combat data, and feature composition.
     */
    public InternalEntityType<?> nativeEntity;

    // -- Combat State (shared by robots and monsters) --

    /**
     * Ticks remaining in combat (wary) mode.
     * Counts down from {@code WaryTime} after last combat event.
     */
    protected int waryTimer = 0;

    /**
     * Ticks remaining until next auto-heal tick.
     * Counts down from {@code HealInterval}.
     */
    protected int autoHealTimer = 0;

    /**
     * Whether this entity is currently in combat (wary) mode.
     * Activated by attacks, cleared when {@link #waryTimer} reaches 0.
     */
    protected boolean combatMode = false;

    /**
     * Whether auto-heal is currently active.
     * Set when health drops below max; cleared after a heal tick.
     */
    protected boolean autoHeal = false;

    // -- Constructor --

    /**
     * Creates the entity. Subclasses must set {@link #nativeEntity} and call
     * {@link #applyBaseAttributes()} after construction.
     *
     * @param entityType Minecraft entity type
     * @param world      world instance
     */
    protected InternalEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
    } // Constructor: InternalEntity ()

    // -- Custom Methods --

    /**
     * Gets display name for entity including type and custom name if available.
     * <p>
     * <b>Format:</b> Returns "CustomName (Type)" if named, otherwise just "Type"
     *
     * @return formatted robot display name
     */
    protected String getEntityName() {
        String customName = Utils.getEntityCustomName(this);
        String typeName = this.nativeEntity.getKey().substring(0, 1).toUpperCase() + this.nativeEntity.getKey().substring(1);
        if (!customName.isEmpty()) return customName + " (" + typeName + ")";
        return typeName;
    } // getEntityName ()

    /**
     * Drops item at specified location with default pickup delay.
     * <p>
     * <b>Helper Method:</b> Centralizes item dropping logic for consistency.
     *
     * @param itemStack the item to drop
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     */
    protected void dropItemAtLocation(ItemStack itemStack, double x, double y, double z) {
        ItemEntity itemEntity = new ItemEntity(this.level(), x, y, z, itemStack);
        itemEntity.setDefaultPickUpDelay();
        this.level().addFreshEntity(itemEntity);
    } // dropItemAtLocation ()

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
    public static AttributeSupplier createAttributes(InternalEntityType<?> entity) {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, entity.getData().getMaxHealth())
                .add(Attributes.ATTACK_DAMAGE, entity.getData().getAttackDamage())
                .add(Attributes.ATTACK_SPEED, entity.getData().getAttackSpeed())
                .add(Attributes.MOVEMENT_SPEED, entity.getData().getMoveSpeed())
                .add(Attributes.ARMOR, entity.getData().getArmor())
                .add(Attributes.ARMOR_TOUGHNESS, entity.getData().getArmorToughness())
                .build();
    } // createAttributes ()

    // -- Attribute Initialization --

    /**
     * Applies base combat attributes from {@link #nativeEntity}'s {@link CombatData}.
     * <p>
     * <b>Call site:</b> Must be called after {@link #nativeEntity} is set — typically
     * at the end of the subclass constructor. Also called in {@link #finalizeSpawn}
     * to ensure attributes are correct after spawn initialization.
     * <p>
     * <b>Variant initialization:</b> Also resets {@code MODEL_VARIANT} and
     * {@code ANIMATOR_VARIANT} to the entity-type-specific defaults (e.g.,
     * {@code "bunny_default"} instead of the generic {@code "default"} set in
     * {@link #defineSynchedData}).
     * <p>
     * <b>Attributes set:</b> MAX_HEALTH, ATTACK_DAMAGE, ATTACK_SPEED, MOVEMENT_SPEED,
     * ARMOR, ARMOR_TOUGHNESS, KNOCKBACK_RESISTANCE.
     */
    protected void applyBaseAttributes() {
        if (nativeEntity == null) return;
        createAttributes(nativeEntity, this);

        // Reset model/animator variants to entity-specific defaults now that nativeEntity is set.
        // defineSynchedData() initializes them to "default" before nativeEntity is available.
        String defaultModelKey = getDefaultModelVariantKey();
        if (!defaultModelKey.equals("default")) setModelVariant(defaultModelKey);

        nativeEntity.getFeature(net.heriazone.hzlib.api.entity.features.variants.AnimatorVariantFeature.class)
                .ifPresent(f -> {
                    var defaultAnim = f.getDefaultVariant(nativeEntity.getKey());
                    if (defaultAnim != null && !defaultAnim.getKey().equals("default")) {
                        setAnimatorVariant(defaultAnim.getKey());
                    }
                });

        // Reset TEXTURE_VARIANT to entity-specific default if still at the generic "default"
        // placeholder set by defineSynchedData(). initializeRandomVariants() will override
        // this at spawn time, but this ensures a valid key is set for NBT-loaded entities.
        nativeEntity.getFeature(net.heriazone.hzlib.api.entity.features.variants.TextureVariantFeature.class)
                .ifPresent(f -> {
                    var defaultTex = f.getDefaultVariant(nativeEntity.getKey());
                    if (defaultTex != null && "default".equals(getTextureVariant())) {
                        setTextureVariant(defaultTex.getKey());
                    }
                });
    } // applyBaseAttributes ()

    /**
     * Applies base combat attributes from the given entity type to the given entity.
     * <p>
     * <b>Static utility:</b> Can be called from {@code createAttributes()} static
     * methods in entity registration (e.g., {@code LovelyRobotEntity.createAttributes()}).
     *
     * @param entityType the entity type providing {@link CombatData}
     * @param entity     the living entity to apply attributes to
     */
    public static void createAttributes(InternalEntityType<?> entityType, LivingEntity entity) {
        if (entityType == null || entity == null) return;
        CombatData data = entityType.getData();

        safeSetAttribute(entity, Attributes.MAX_HEALTH,          data.getMaxHealth());
        safeSetAttribute(entity, Attributes.ATTACK_DAMAGE,       data.getAttackDamage());
        safeSetAttribute(entity, Attributes.ATTACK_SPEED,        data.getAttackSpeed());
        safeSetAttribute(entity, Attributes.MOVEMENT_SPEED,      data.getMoveSpeed());
        safeSetAttribute(entity, Attributes.ARMOR,               data.getArmor());
        safeSetAttribute(entity, Attributes.ARMOR_TOUGHNESS,     data.getArmorToughness());
        safeSetAttribute(entity, Attributes.KNOCKBACK_RESISTANCE, data.getKnockbackResistance());
    } // createAttributes ()

    protected static void safeSetAttribute(LivingEntity entity,
            net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute,
            double value) {
        var instance = entity.getAttribute(attribute);
        if (instance != null) instance.setBaseValue(value);
    } // safeSetAttribute ()

    // -- Combat Mode --

    /**
     * Activates combat mode and resets the wary timer.
     * Also immediately switches to the armed model variant so the visual
     * change happens on the same tick as combat activation.
     */
    protected void handleActivateCombatMode() {
        combatMode = true;
        waryTimer = getCombatWaryTime();
        // Immediately switch to armed model — don't wait for next handleCombatMode() tick
        if (nativeEntity != null && !level().isClientSide) {
            String armedKey = getArmedModelVariantKey();
            if (!armedKey.equals(getModelVariant())) setModelVariant(armedKey);
        }
    } // handleActivateCombatMode ()

    /**
     * Ticks the combat mode timer and clears combat mode when the timer expires.
     * Drives the {@code MODEL_VARIANT} between the armed and default model variants
     * as the wary timer ticks.
     * <p>
     * <b>Variant keys:</b> Uses the entity type's registered default and armed model
     * variants rather than hardcoded strings, so entity-specific keys like
     * {@code "bunny_default"} / {@code "bunny_armed"} are resolved correctly.
     * <p>
     * <b>Call site:</b> Call from {@code tick()} on the server side.
     */
    protected void handleCombatMode() {
        // Clear stale targets
        if (getTarget() != null && (!getTarget().isAlive() || getTarget().isRemoved())) {
            setTarget(null);
        }

        // Activate combat mode if swinging at a live target
        if ((swinging && getTarget() != null) || (getTarget() != null && getTarget().isAlive())) {
            handleActivateCombatMode();
        }

        if (level().isClientSide && !combatMode) return;

        // Guard: nativeEntity must be set for model variant resolution
        if (nativeEntity == null) return;

        if (waryTimer > 0) {
            // Switch to armed model variant while in combat
            String armedKey = getArmedModelVariantKey();
            if (!armedKey.equals(getModelVariant())) setModelVariant(armedKey);
            waryTimer--;
        } else if (combatMode) {
            // Timer expired — exit combat mode and return to default model
            combatMode = false;
            String defaultKey = getDefaultModelVariantKey();
            if (!defaultKey.equals(getModelVariant())) setModelVariant(defaultKey);
        }
        // If combatMode is already false, model is already default — nothing to do
    } // handleCombatMode ()

    /**
     * Returns the key for the default (unarmed) model variant.
     * Resolves from the registered {@link ModelVariantFeature} default, falling back
     * to {@code "default"} if no feature is configured.
     */
    protected String getDefaultModelVariantKey() {
        if (nativeEntity == null) return "default";
        var feature = nativeEntity.getFeature(
                net.heriazone.hzlib.api.entity.features.variants.ModelVariantFeature.class);
        if (feature.isEmpty()) return "default";
        var defaultVariant = feature.get().getDefaultVariant(nativeEntity.getKey());
        return defaultVariant != null ? defaultVariant.getKey() : "default";
    } // getDefaultModelVariantKey ()

    /**
     * Returns the key for the armed model variant.
     * Looks for a variant whose key contains {@code "armed"} in the registered
     * {@link ModelVariantFeature}, falling back to {@code "armed"} if not found.
     */
    protected String getArmedModelVariantKey() {
        if (nativeEntity == null) return "armed";
        var feature = nativeEntity.getFeature(
                net.heriazone.hzlib.api.entity.features.variants.ModelVariantFeature.class);
        if (feature.isEmpty()) return "armed";
        return feature.get().getAvailableVariants(nativeEntity.getKey()).stream()
                .map(net.heriazone.hzlib.api.entity.variants.interfaces.IModelVariant::getKey)
                .filter(k -> k.contains("armed"))
                .findFirst()
                .orElse("armed");
    } // getArmedModelVariantKey ()

    /**
     * Returns whether this entity is currently in combat (wary) mode.
     *
     * @return true if in combat mode
     */
    public boolean isWary() {
        return combatMode;
    } // isWary ()

    // -- Auto-Heal --

    /**
     * Ticks the auto-heal system, healing the entity periodically when below max health.
     * <p>
     * <b>Call site:</b> Call from {@code tick()} on the server side.
     */
    protected void handleAutoHeal() {
        if (getHealth() < getMaxHealth()) autoHeal = true;
        if (level().isClientSide && !autoHeal) return;

        if (autoHealTimer > 0) {
            autoHealTimer--;
        } else {
            heal(getHealth() / 16.0F);
            autoHeal = false;
            autoHealTimer = getAutoHealInterval();
        }
    } // handleAutoHeal ()

    // -- Configurable Timers (override in subclasses or read from config) --

    /**
     * Returns the number of ticks to remain in combat mode after last combat event.
     * Override to read from config (e.g., {@code SharedConfigs.Common.WaryTime}).
     *
     * @return wary timer duration in ticks
     */
    protected int getCombatWaryTime() {
        return 100; // 5 seconds default
    } // getCombatWaryTime ()

    /**
     * Returns the number of ticks between auto-heal ticks.
     * Override to read from config (e.g., {@code SharedConfigs.Common.HealInterval}).
     *
     * @return heal interval in ticks
     */
    protected int getAutoHealInterval() {
        return 50; // 2.5 seconds default
    } // getAutoHealInterval ()

    // -- Entity Data Initialization --

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(STATE,              EntityState.Follow.getId());
        builder.define(TEXTURE_VARIANT,    "default");
        builder.define(MODEL_VARIANT,      "default");
        builder.define(ANIMATOR_VARIANT,   "default");
        builder.define(NOTIFICATION_ENABLED, true);
    } // defineSynchedData ()

    // -- State Accessors --

    /** Returns the current behavioral state ID. */
    public int getCurrentStateID() {
        try { return entityData.get(STATE); }
        catch (Exception ignored) { return EntityState.Standby.getId(); }
    } // getCurrentStateID ()

    /** Returns the current behavioral state. */
    public EntityState getCurrentState() {
        try { return EntityState.byId(entityData.get(STATE)); }
        catch (Exception ignored) { return EntityState.Standby; }
    } // getCurrentState ()

    /** Sets the current behavioral state and fires {@link #onStateChanged}. */
    public void setCurrentState(EntityState state) {
        entityData.set(STATE, state.getId());
        onStateChanged(state);
    } // setCurrentState ()

    /** Sets the current behavioral state by ID and fires {@link #onStateChanged}. */
    public void setCurrentState(int stateId) {
        entityData.set(STATE, stateId);
        onStateChanged(EntityState.byId(stateId));
    } // setCurrentState ()

    /**
     * Called when the behavioral state changes.
     * Override to add state-specific behaviors (AI changes, animation triggers, etc.).
     *
     * @param newState the new state
     */
    protected void onStateChanged(EntityState newState) {
        switch (newState) {
            case Standby -> {
                if (isTame()) {
                    setOrderedToSit(true);
                    setInSittingPose(true);
                    setTarget(null);
                    getNavigation().stop();
                }
            }
            case Follow -> {
                if (isTame()) {
                    setOrderedToSit(false);
                    setInSittingPose(false);
                }
            }
            default -> {}
        }
    } // onStateChanged ()

    // -- Variant Accessors --

    /** Returns the current texture variant key (e.g., {@code "white"}, {@code "default"}). */
    public String getTextureVariant() {
        try { return entityData.get(TEXTURE_VARIANT); }
        catch (Exception ignored) { return "default"; }
    } // getTextureVariant ()

    /**
     * Sets the texture variant key if it is valid for this entity type.
     * Invalid keys are silently ignored to prevent client-server desync.
     */
    public void setTextureVariant(String variantKey) {
        if (nativeEntity != null && isValidTextureVariant(variantKey)) {
            entityData.set(TEXTURE_VARIANT, variantKey);
        }
    } // setTextureVariant ()

    /** Returns the current model variant key (e.g., {@code "default"}, {@code "armed"}). */
    public String getModelVariant() {
        try { return entityData.get(MODEL_VARIANT); }
        catch (Exception ignored) { return "default"; }
    } // getModelVariant ()

    /** Sets the model variant key if it is valid for this entity type. */
    public void setModelVariant(String variantKey) {
        if (nativeEntity != null && isValidModelVariant(variantKey)) {
            entityData.set(MODEL_VARIANT, variantKey);
        }
    } // setModelVariant ()

    /** Returns the current animator variant key (e.g., {@code "default"}). */
    public String getAnimatorVariant() {
        try { return entityData.get(ANIMATOR_VARIANT); }
        catch (Exception ignored) { return "default"; }
    } // getAnimatorVariant ()

    /** Sets the animator variant key if it is valid for this entity type. */
    public void setAnimatorVariant(String variantKey) {
        if (nativeEntity != null && isValidAnimatorVariant(variantKey)) {
            entityData.set(ANIMATOR_VARIANT, variantKey);
        }
    } // setAnimatorVariant ()

    // -- Notification --

    /** Returns whether status notifications are enabled for this entity. */
    public boolean isNotificationEnabled() {
        try { return entityData.get(NOTIFICATION_ENABLED); }
        catch (Exception ignored) { return true; }
    } // isNotificationEnabled ()

    /** Sets whether status notifications are enabled. */
    public void setNotificationEnabled(boolean enabled) {
        entityData.set(NOTIFICATION_ENABLED, enabled);
    } // setNotificationEnabled ()

    // -- Resource Resolution --

    /**
     * Returns the current texture {@link ResourceLocation} resolved via
     * {@link TextureVariantFeature}. Returns {@code null} if not available.
     */
    public ResourceLocation getCurrentTexture() {
        if (nativeEntity == null) return null;
        ITextureVariant variant = nativeEntity.getTextureVariant(nativeEntity.getKey(), getTextureVariant());
        return variant != null ? variant.getResource(nativeEntity.getKey()) : null;
    } // getCurrentTexture ()

    /**
     * Returns the current model {@link ResourceLocation} resolved via
     * {@link ModelVariantFeature}. Returns {@code null} if not available.
     */
    public ResourceLocation getCurrentModel() {
        if (nativeEntity == null) return null;
        IModelVariant variant = nativeEntity.getModelVariant(nativeEntity.getKey(), getModelVariant());
        return variant != null ? variant.getResource(nativeEntity.getKey()) : null;
    } // getCurrentModel ()

    /**
     * Returns the current animator {@link ResourceLocation} resolved via
     * {@link AnimatorVariantFeature}. Returns {@code null} if not available.
     */
    public ResourceLocation getCurrentAnimator() {
        if (nativeEntity == null) return null;
        IAnimatorVariant variant = nativeEntity.getAnimatorVariant(nativeEntity.getKey(), getAnimatorVariant());
        return variant != null ? variant.getResource(nativeEntity.getKey()) : null;
    } // getCurrentAnimator ()

    // -- Variant Validation --

    protected boolean isValidTextureVariant(String variantKey) {
        if (nativeEntity == null || variantKey == null) return false;
        return nativeEntity.getFeature(TextureVariantFeature.class)
                .map(f -> f.hasVariant(nativeEntity.getKey(), variantKey))
                .orElse(false);
    } // isValidTextureVariant ()

    protected boolean isValidModelVariant(String variantKey) {
        if (nativeEntity == null || variantKey == null) return false;
        return nativeEntity.getFeature(ModelVariantFeature.class)
                .map(f -> f.hasVariant(nativeEntity.getKey(), variantKey))
                .orElse(false);
    } // isValidModelVariant ()

    protected boolean isValidAnimatorVariant(String variantKey) {
        if (nativeEntity == null || variantKey == null) return false;
        return nativeEntity.getFeature(AnimatorVariantFeature.class)
                .map(f -> f.hasVariant(nativeEntity.getKey(), variantKey))
                .orElse(false);
    } // isValidAnimatorVariant ()

    // -- Navigation --

    /**
     * Configures ground path navigation with edge traversal and door handling.
     * Prevents spinning at ledges when following owners.
     */
    @Override
    protected PathNavigation createNavigation(Level level) {
        GroundPathNavigation nav = new GroundPathNavigation(this, level);
        nav.setCanFloat(true);
        nav.setCanOpenDoors(true);
        nav.setCanPassDoors(true);
        return nav;
    } // createNavigation ()

    // -- Entity Lifecycle --

    /**
     * Handles entity removal and ensures registry cleanup.
     * <p>
     * <b>Architecture:</b> Intercepts all removal scenarios (death, despawn, chunk
     * unload, manual removal) to ensure the robot is unregistered from the owner's
     * registry. Prevents registry leaks and incorrect spawn limit counts.
     * <p>
     * <b>Thread Safety:</b> Only unregisters on server side.
     *
     * @param reason the reason for entity removal
     */
    @Override
    public void remove(RemovalReason reason) {
        if (!this.level().isClientSide && this.isTame() && this.getOwnerUUID() != null) {
            unregisterRobot();
        }
        super.remove(reason);
    } // remove ()

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty,
                                        MobSpawnType spawnReason, @Nullable SpawnGroupData entityData) {
        // Randomize spawn orientation — prevents all entities facing the same direction
        rotate(Rotation.getRandom(this.getRandom()));
        initializeSpawnVariants(world, spawnReason);
        applyBaseAttributes();
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
    } // finalizeSpawn ()

    /**
     * Initializes entity variants at spawn time with access to world context.
     * Default calls {@link #initializeRandomVariants()}. Override for biome-aware
     * or coordinated variant selection (see ADR_011).
     *
     * @param world  server level accessor providing biome and world context
     * @param reason spawn reason
     */
    protected void initializeSpawnVariants(ServerLevelAccessor world, MobSpawnType reason) {
        initializeRandomVariants();
    } // initializeSpawnVariants ()

    /** Selects random variants from all registered variant features. */
    protected void initializeRandomVariants() {
        if (nativeEntity == null) return;

        nativeEntity.getFeature(TextureVariantFeature.class).ifPresent(f -> {
            ITextureVariant v = f.getRandomVariant(nativeEntity.getKey());
            if (v != null) setTextureVariant(v.getKey());
        });

        nativeEntity.getFeature(ModelVariantFeature.class).ifPresent(f -> {
            IModelVariant v = f.getRandomVariant(nativeEntity.getKey());
            if (v != null) setModelVariant(v.getKey());
        });

        nativeEntity.getFeature(AnimatorVariantFeature.class).ifPresent(f -> {
            IAnimatorVariant v = f.getRandomVariant(nativeEntity.getKey());
            if (v != null) setAnimatorVariant(v.getKey());
        });
    } // initializeRandomVariants ()

    // -- NBT Serialization --

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("StateId",              getCurrentStateID());
        nbt.putString("TextureVariant",    getTextureVariant());
        nbt.putString("ModelVariant",      getModelVariant());
        nbt.putString("AnimatorVariant",   getAnimatorVariant());
        nbt.putBoolean("NotificationEnabled", isNotificationEnabled());
    } // addAdditionalSaveData ()

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);

        if (nbt.contains("StateId"))            setCurrentState(nbt.getInt("StateId"));
        if (nbt.contains("NotificationEnabled")) setNotificationEnabled(nbt.getBoolean("NotificationEnabled"));

        // TextureVariant — new string system (ADR_012)
        if (nbt.contains("TextureVariant")) {
            setTextureVariant(nbt.getString("TextureVariant"));
        } else if (nbt.contains("TextureID")) {
            // Backward compat: migrate old int-based TextureID to string key (ADR_012)
            migrateTextureId(nbt.getInt("TextureID"));
        }

        if (nbt.contains("ModelVariant"))    setModelVariant(nbt.getString("ModelVariant"));
        if (nbt.contains("AnimatorVariant")) setAnimatorVariant(nbt.getString("AnimatorVariant"));
    } // readAdditionalSaveData ()

    /**
     * Migrates an old int-based {@code TextureID} to the new string-keyed
     * {@code TextureVariant} system. Called once on first load of old saves.
     * <p>
     * <b>Subclass override:</b> Override in robot entities to use
     * {@code EntityTexture.byId(id).Name()} for the 16-color palette mapping.
     * The base implementation sets {@code "default"} as a safe fallback.
     *
     * @param oldTextureId the old int texture ID (0–15 for robots)
     */
    protected void migrateTextureId(int oldTextureId) {
        // Base fallback — robot subclass overrides with EntityTexture.byId(id).Name()
        setTextureVariant("default");
    } // migrateTextureId ()

    // -- Sound System --

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return super.getHurtSound(damageSource);
    } // getHurtSound ()

    @Override
    protected SoundEvent getDeathSound() {
        return super.getDeathSound();
    } // getDeathSound ()

    @Override
    protected SoundEvent getAmbientSound() {
        return super.getAmbientSound();
    } // getAmbientSound ()

    // -- Interaction Framework --

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        InteractionResult result = handleCommonInteractions(player, hand, stack);
        if (result != InteractionResult.PASS) return result;
        return handleSpecificInteractions(player, hand, stack);
    } // mobInteract ()

    /**
     * Handles interactions common to all entity types (e.g., taming food).
     * Override to add shared interaction logic before type-specific handling.
     */
    protected InteractionResult handleCommonInteractions(Player player, InteractionHand hand, ItemStack stack) {
        return InteractionResult.PASS;
    } // handleCommonInteractions ()

    /**
     * Handles entity-type-specific interactions.
     * Subclasses must implement (robot commands, monster feeding, etc.).
     */
    protected abstract InteractionResult handleSpecificInteractions(Player player, InteractionHand hand, ItemStack stack);

    /**
     * Handles dye/item texture interaction. Override in robot entities to apply
     * color changes via {@link #setTextureVariant(String)}.
     *
     * @param stack  item stack used
     * @param player interacting player
     * @return true if texture was changed and item should be consumed
     */
    protected boolean handleTexture(ItemStack stack, Player player) {
        return false;
    } // handleTexture ()

    /**
     * Returns whether the given item stack can trigger interaction handling.
     * Override to whitelist specific items (e.g., dyes, books, swords).
     *
     * @param stack item stack to check
     * @return true if interactions should be processed for this item
     */
    protected boolean canInteractWithItems(ItemStack stack) {
        return false;
    } // canInteractWithItems ()

    /**
     * Handles taming — sets ownership, spawns particles, plays sound, registers entity.
     * <p>
     * <b>Architecture:</b> Generic taming flow shared by all tameable entity types.
     * Calls the abstract {@link #registerRobot()} hook so subclasses can register
     * in their respective registry systems.
     * <p>
     * <b>Display:</b> Override {@link #displayTameMessage(Player)} to show a
     * mod-specific owner message after taming.
     *
     * @param player the player taming this entity
     */
    public void handleTame(Player player) {
        this.tame(player);
        this.setTame(true, false);
        this.setOrderedToSit(false);

        // Spawn POOF particles (spawn effect)
        InternalParticle.Poof(this);

        // Play spawn sound — volume scales with entity size
        float volume = (float) Math.max(0.5F, Math.min(2.0F, this.getBbWidth() * this.getBbHeight()));
        this.level().playSound(null, this.blockPosition(),
                SoundEvents.TOTEM_USE, SoundSource.NEUTRAL, volume, 1.2F);

        displayTameMessage(player);
        registerRobot();
    } // handleTame ()

    /**
     * Displays the tame confirmation message to the player.
     * Override in lovelylib's {@code RobotEntity} to show the owner name using
     * {@code LovelyIdentifier} and {@code LovelyConstant.MSG_OWNER}.
     *
     * @param player the player who tamed this entity
     */
    protected void displayTameMessage(Player player) {
        // Default no-op — override in lovelylib RobotEntity with mod-specific message
    } // displayTameMessage ()

    // -- Registry Lifecycle (concrete no-ops — override in lovelylib RobotEntity) --

    /**
     * Registers this entity in the owner's registry when tamed.
     * <p>
     * <b>Design Decision:</b> Concrete no-op here rather than abstract — registry
     * is a robot-specific concern. {@code MonsterEntity} and future entity types
     * that don't use a registry don't need to implement this.
     * Override in lovelylib's {@code RobotEntity} to register in {@code RobotRegistryManager}.
     */
    protected void registerRobot() {
        // No-op — override in lovelylib RobotEntity
    } // registerRobot ()

    /**
     * Ensures this entity is registered, creating or updating the registry entry.
     * <p>
     * <b>Design Decision:</b> Concrete no-op — registry is robot-specific.
     * Override in lovelylib's {@code RobotEntity}.
     */
    protected void ensureRegistered() {
        // No-op — override in lovelylib RobotEntity
    } // ensureRegistered ()

    /**
     * Unregisters this entity from the owner's registry.
     * <p>
     * <b>Design Decision:</b> Concrete no-op — registry is robot-specific.
     * Override in lovelylib's {@code RobotEntity}.
     */
    protected void unregisterRobot() {
        // No-op — override in lovelylib RobotEntity
    } // unregisterRobot ()

    /**
     * Updates the registry timestamp for this entity.
     * <p>
     * <b>Architecture:</b> Called every 20 ticks to maintain "last seen" timestamp.
     * Override in lovelylib's {@code RobotEntity} to call {@code RobotRegistryManager}.
     */
    protected void updateRegistryTimestamp() {
        // No-op — override in lovelylib RobotEntity
    } // updateRegistryTimestamp ()

    // -- Abstract Contracts --

    /**
     * Recalculates all entity attributes based on current level and attached features.
     * <p>
     * <b>Call sites:</b> After level-up, after NBT load, after config reload.
     * Implemented in lovelylib's {@code RobotEntity} using feature system.
     */
    protected abstract void recalculateAttributes();

    /**
     * Handles item drop on entity death.
     * Implemented in lovelylib's {@code RobotEntity} to drop the robot core.
     */
    protected abstract void handleItemDrop();

    /**
     * Handles attack target event — exp accumulation, combat mode activation.
     * Implemented in lovelylib's {@code RobotEntity}.
     *
     * @param target the entity being attacked
     */
    protected abstract void handleAttackTarget(Entity target);

    /**
     * Handles incoming damage — protection reduction, exp accumulation.
     * Implemented in lovelylib's {@code RobotEntity}.
     *
     * @param source damage source
     * @param amount raw damage amount
     * @return true if damage should be applied, false to cancel
     */
    protected abstract boolean handleDamage(DamageSource source, float amount);

    // -- Child Creation --

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob otherParent) {
        return null; // HZLib entities don't breed by default
    } // getBreedOffspring ()

    // -- Utility --

    /**
     * Returns whether this entity is owned by the specified player.
     * Handles null player and owner references gracefully.
     */
    public boolean isOwnedBy(@Nullable Player player) {
        if (player == null) return false;
        return Objects.equals(getOwnerUUID(), player.getUUID());
    } // isOwnedBy ()

    /**
     * Returns the entity type key for variant resolution.
     * Returns {@code "unknown"} if {@link #nativeEntity} is not set.
     */
    public String getEntityTypeKey() {
        return nativeEntity != null ? nativeEntity.getKey() : "unknown";
    } // getEntityTypeKey ()

} // Class: InternalEntity