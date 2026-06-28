package net.heriazone.hzlib.api.entity;

import net.heriazone.hzlib.api.entity.features.FoodFeature;
import net.heriazone.hzlib.api.entity.features.SizeVariantFeature;
import net.heriazone.hzlib.api.entity.features.exchange.ExchangeFeature;
import net.heriazone.hzlib.api.entity.features.exchange.ExchangeState;
import net.heriazone.hzlib.api.entity.features.overlay.OverlayFeature;
import net.heriazone.hzlib.api.entity.features.overlay.OverlaySlot;
import net.heriazone.hzlib.api.entity.features.variants.AnimatorVariantFeature;
import net.heriazone.hzlib.api.entity.features.variants.ModelVariantFeature;
import net.heriazone.hzlib.api.entity.features.variants.TextureVariantFeature;
import net.heriazone.hzlib.api.entity.variants.VariantRegistries;
import net.heriazone.hzlib.api.entity.variants.interfaces.IAnimatorVariant;
import net.heriazone.hzlib.api.entity.variants.interfaces.IModelVariant;
import net.heriazone.hzlib.api.entity.variants.interfaces.ITextureVariant;
import net.heriazone.hzlib.api.nbt.DataCompound;
import net.heriazone.hzlib.api.nbt.MigrationChain;
import net.heriazone.hzlib.api.nbt.NbtAdapterFactory;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Root entity base for all HZLib-managed tameable entities.
 * <p>
 * <b>Architecture:</b> Sits directly below {@code TamableAnimal} and above all
 * HZLib entity tiers. Owns the concerns every HZLib entity shares:
 * <ul>
 *   <li>Three string-keyed appearance dimensions — {@code TEXTURE_VARIANT},
 *       {@code MODEL_VARIANT}, {@code ANIMATOR_VARIANT} — resolved at runtime
 *       via {@link NativeEntityFamily}'s feature system</li>
 *   <li>Base combat attributes applied from {@link CombatData} on the entity's
 *       {@link NativeEntityFamily}</li>
 *   <li>Combat mode and auto-heal lifecycle — wary timer, heal timer, model
 *       variant toggling between default and armed states</li>
 *   <li>Spawn variant initialisation — random and biome-aware selection via
 *       {@link #initializeSpawnVariants}</li>
 *   <li>Persistent overlay slot state — maps {@link OverlayFeature} RANDOM and
 *       INTERACTIVE slots to {@code SynchedEntityData} accessors declared by
 *       the concrete subclass</li>
 *   <li>Interaction pipeline — {@link ExchangeFeature} first, then
 *       {@link #handleSpecificInteractions}</li>
 *   <li>Registry lifecycle hooks — concrete no-ops, overridden by subclasses
 *       that implement a registry</li>
 * </ul>
 * <p>
 * <b>Abstract contracts:</b> {@link #handleSpecificInteractions},
 * {@link #recalculateAttributes}, {@link #handleItemDrop},
 * {@link #handleAttackTarget}, {@link #handleDamage}.
 * <p>
 * <b>Thread safety:</b> {@code EntityDataAccessor} fields are synced automatically.
 * NBT and registry operations must run on the server thread.
 */
public abstract class NativeEntity extends TamableAnimal {

    // -- Entity Data Accessors --

    /** Whether the entity sends status messages to its owner. */
    protected static final EntityDataAccessor<Boolean> NOTIFICATION_ENABLED = SynchedEntityData.defineId(NativeEntity.class, EntityDataSerializers.BOOLEAN);

    /** Behavioral state — drives AI goal selection independently of visual variants. */
    protected static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(NativeEntity.class, EntityDataSerializers.INT);

    /** Active texture variant key — resolved to a resource path via {@link NativeEntityFamily}. */
    protected static final EntityDataAccessor<String> TEXTURE_VARIANT = SynchedEntityData.defineId(NativeEntity.class, EntityDataSerializers.STRING);

    /** Active model variant key — controls which geo model the renderer loads. */
    protected static final EntityDataAccessor<String> MODEL_VARIANT = SynchedEntityData.defineId(NativeEntity.class, EntityDataSerializers.STRING);

    /** Active animator variant key — controls which animation file the renderer loads. */
    protected static final EntityDataAccessor<String> ANIMATOR_VARIANT = SynchedEntityData.defineId(NativeEntity.class, EntityDataSerializers.STRING);

    // -- Family Reference --

    /**
     * This entity's family descriptor — the source of variant features, combat data,
     * and all feature composition. Set by the concrete subclass before calling
     * {@link #applyBaseAttributes()} and {@link #registerOverlayData()}.
     */
    public NativeEntityFamily<?> nativeEntity;

    // -- Exchange State --

    /**
     * Per-instance runtime state for {@link ExchangeFeature}.
     * <p>
     * Holds per-player sequence buffers and per-rule cooldown expiry ticks.
     * Created eagerly so interaction code never needs a null check — both internal
     * maps start empty and cost nothing at rest.
     */
    protected final ExchangeState exchangeState = new ExchangeState();

    // -- Overlay Slot State --

    /**
     * Maps slot keys to the pre-declared {@code SynchedEntityData} accessors used
     * to persist RANDOM and INTERACTIVE {@link OverlayFeature} slots.
     * <p>
     * <b>Why not declared here:</b> {@code SynchedEntityData} accessors must be
     * registered in {@link #defineSynchedData} before construction completes — the
     * data map locks immediately after. Subclasses that use {@link OverlayFeature}
     * declare a static accessor pool and register it in their own
     * {@code defineSynchedData} override. {@link #registerOverlayData()} then maps
     * slot keys to those pool entries at entity construction time.
     */
    private final Map<String, EntityDataAccessor<String>> overlaySlotAccessors = new HashMap<>();

    /**
     * Returns the ordered pool of pre-declared accessors available for persistent
     * overlay slots. Index 0 maps to the first persistent slot, index 1 to the second,
     * and so on.
     * <p>
     * Override in subclasses that declare {@link OverlayFeature} slots. The base
     * implementation returns an empty list — overlay slots are silently skipped for
     * subclasses that do not override.
     */
    protected List<EntityDataAccessor<String>> getOverlaySlotPool() {
        return List.of();
    } // getOverlaySlotPool ()

    // -- Combat State --

    /** Ticks remaining in combat mode. Counts down after each combat event. */
    protected int waryTimer = 0;

    /** Ticks remaining until the next auto-heal pulse. */
    protected int autoHealTimer = 0;

    /** Whether combat mode is active. Set by attacks, cleared when {@link #waryTimer} reaches 0. */
    protected boolean combatMode = false;

    /** Whether auto-heal is active. Set when health falls below max; cleared after healing. */
    protected boolean autoHeal = false;

    // -- Constructor --

    protected NativeEntity(EntityType<? extends TamableAnimal> entityType, Level world, NativeEntityFamily<? extends NativeEntityFamily<?>> entityFamily) {
        super(entityType, world);
        this.nativeEntity = entityFamily;
    } // Constructor: NativeEntity ()

    // -- Custom Methods --

    /**
     * Returns the entity's display name, incorporating the custom name if set.
     * Format: {@code "CustomName (Key)"} when named, {@code "Key"} otherwise.
     */
    protected String getEntityName() {
        String customName = Utils.getEntityCustomName(this);
        String typeName = this.nativeEntity.getKey().substring(0, 1).toUpperCase() + this.nativeEntity.getKey().substring(1);
        if (!customName.isEmpty()) return customName + " (" + typeName + ")";
        return typeName;
    } // getEntityName ()

    /** Drops an item at the given coordinates with the default pickup delay. */
    protected void dropItemAtLocation(ItemStack itemStack, double x, double y, double z) {
        ItemEntity itemEntity = new ItemEntity(this.level(), x, y, z, itemStack);
        itemEntity.setDefaultPickUpDelay();
        this.level().addFreshEntity(itemEntity);
    } // dropItemAtLocation ()

    // -- Overlay Slot API --

    /**
     * Maps {@link OverlayFeature} slot keys to the pre-declared
     * {@link #getOverlaySlotPool()} accessors and initialises each to its default.
     * <p>
     * Must be called after {@link #nativeEntity} is set. Does not call
     * {@code defineId} — accessors are already registered in
     * {@link #defineSynchedData}. Logs a warning if the feature declares more
     * persistent slots than the pool provides.
     */
    protected void registerOverlayData() {
        if (nativeEntity == null) return;
        List<EntityDataAccessor<String>> pool = getOverlaySlotPool();
        nativeEntity.getFeature(OverlayFeature.class).ifPresent(feature -> {
            List<OverlaySlot> persistent = feature.getPersistentSlots();
            for (int i = 0; i < persistent.size(); i++) {
                if (i >= pool.size()) {
                    System.err.println("[HZLib] WARNING: OverlayFeature on " + nativeEntity.getKey()
                            + " declares " + persistent.size() + " persistent slots but pool only has "
                            + pool.size() + ". Slot '" + persistent.get(i).getKey() + "' will not be synced.");
                    break;
                }
                String key = persistent.get(i).getKey();
                EntityDataAccessor<String> accessor = pool.get(i);
                overlaySlotAccessors.put(key, accessor);
                entityData.set(accessor, persistent.get(i).getDefault());
            }
        });
    } // registerOverlayData ()

    /**
     * Returns the active texture path for a persistent overlay slot.
     * Returns {@code ""} for unknown keys and for CONDITIONAL / ALWAYS slots
     * which carry no persistent state.
     *
     * @param slotKey slot key declared on the entity's {@link OverlayFeature}
     * @return active texture path, or empty string
     */
    public String getOverlaySlot(String slotKey) {
        EntityDataAccessor<String> accessor = overlaySlotAccessors.get(slotKey);
        if (accessor == null) return "";
        try { return entityData.get(accessor); }
        catch (Exception ignored) { return ""; }
    } // getOverlaySlot ()

    /**
     * Sets the active texture path for a persistent overlay slot.
     * Silently ignores unknown slot keys.
     *
     * @param slotKey     slot key declared on the entity's {@link OverlayFeature}
     * @param texturePath new active texture path; empty string = no render pass
     */
    public void setOverlaySlot(String slotKey, String texturePath) {
        EntityDataAccessor<String> accessor = overlaySlotAccessors.get(slotKey);
        if (accessor == null) return;
        entityData.set(accessor, texturePath != null ? texturePath : "");
    } // setOverlaySlot ()

    /**
     * Advances an INTERACTIVE overlay slot to the next entry in its declared pool,
     * cycling at the end. No-op if the key is unknown or no {@link OverlayFeature}
     * is registered.
     *
     * @param slotKey slot key of the INTERACTIVE slot to cycle
     */
    public void cycleOverlaySlot(String slotKey) {
        if (nativeEntity == null) return;
        nativeEntity.getFeature(OverlayFeature.class).ifPresent(feature -> {
            OverlaySlot slot = feature.getSlot(slotKey);
            if (slot == null) return;
            String current = getOverlaySlot(slotKey);
            setOverlaySlot(slotKey, slot.cycleNext(current));
        });
    } // cycleOverlaySlot ()

    // -- Belly Level Hook --

    /**
     * Returns the entity's current belly level as an integer (0 = none / SLIM).
     * <p>
     * <b>Architecture:</b> Thin seam that lets {@link net.heriazone.hzlib.api.rendering.RenderConditions#bellyAtLeast}
     * predicate on belly level without any mod-specific type reference entering HZLib.
     * Override in mod entity classes that implement a belly progression system.
     * The base implementation returns {@code 0} — entities with no belly system always
     * evaluate as having the minimum level, so {@code bellyAtLeast(1)} correctly returns
     * {@code false} for them.
     *
     * @return current belly level, 0 by default
     */
    public int getBellyLevel() {
        return 0;
    } // getBellyLevel ()

    // -- Friendly Fire Prevention --

    /**
     * Prevents tamed entities from targeting their owner's other tamed companions.
     * <p>
     * <b>Architecture:</b> This single override covers every AI goal that calls
     * {@code wantsToAttack()} internally — {@code NearestAttackableTargetGoal},
     * {@code OwnerHurtTargetGoal}, and {@code HurtByTargetGoal} all route through
     * this method when evaluating a candidate target. Returning {@code false} here
     * is the targeting-side layer of the two-layer friendly fire prevention system;
     * the projectile-side layer lives in each projectile's {@code onHitEntity()}.
     * <p>
     * <b>Rule:</b> If this entity is tamed, has a known owner, and the candidate target
     * is also a {@link TamableAnimal} with the same owner UUID — return {@code false}.
     * All other target candidates are evaluated normally by the parent implementation.
     * <p>
     * <b>Scope:</b> Only fires when {@code isTame()} is true — untamed entities are
     * completely unaffected. The UUID equality check is O(1) and runs in the AI goal
     * tick; negligible performance cost.
     *
     * @param target    candidate attack target being evaluated by an AI goal
     * @param attacker  the entity that caused this entity to consider attacking
     *                  (may differ from {@code target} for retaliation goals)
     * @return {@code false} if target is a same-owner tamed peer, otherwise delegates
     *         to {@link TamableAnimal#wantsToAttack(LivingEntity, LivingEntity)}
     */
    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity attacker) {
        if (isTame() && getOwnerUUID() != null
                && target instanceof TamableAnimal peer
                && getOwnerUUID().equals(peer.getOwnerUUID())) {
            return false; // same-owner companion — never attack
        }
        return super.wantsToAttack(target, attacker);
    } // wantsToAttack ()

    // -- Attribute Initialization --

    /**
     * Applies {@link CombatData} attributes from {@link #nativeEntity} to this entity
     * and resets all three variant keys to their family-specific defaults.
     * <p>
     * Must be called after {@link #nativeEntity} is set. {@link #defineSynchedData}
     * initialises variant keys to the placeholder {@code "default"} because
     * {@code nativeEntity} is not yet available at that point — this corrects them.
     * Also called in {@link #finalizeSpawn} to re-apply after spawn variant selection.
     */
    protected void applyBaseAttributes() {
        if (nativeEntity == null) return;
        createAttributes(nativeEntity, this);

        // Correct model variant — defineSynchedData sets "default" before nativeEntity is available
        String defaultModelKey = getDefaultModelVariantKey();
        if (!defaultModelKey.equals("default")) setModelVariant(defaultModelKey);

        // Correct animator variant for the same reason
        nativeEntity.getFeature(net.heriazone.hzlib.api.entity.features.variants.AnimatorVariantFeature.class)
                .ifPresent(f -> {
                    var defaultAnim = f.getDefaultVariant(nativeEntity.getKey());
                    if (defaultAnim != null && !defaultAnim.getKey().equals("default")) {
                        setAnimatorVariant(defaultAnim.getKey());
                    }
                });

        // Correct texture variant — initializeRandomVariants() will override at spawn,
        // but NBT-loaded entities need a valid key before that hook runs
        nativeEntity.getFeature(net.heriazone.hzlib.api.entity.features.variants.TextureVariantFeature.class)
                .ifPresent(f -> {
                    var defaultTex = f.getDefaultVariant(nativeEntity.getKey());
                    if (defaultTex != null && "default".equals(getTextureVariant())) {
                        setTextureVariant(defaultTex.getKey());
                    }
                });
    } // applyBaseAttributes ()

    /**
     * Applies {@link CombatData} from a {@link NativeEntityFamily} to a live entity.
     * <p>
     * Static overload used when attributes must be pushed onto an already-constructed
     * entity rather than built via the registration-time {@link AttributeSupplier}.
     *
     * @param entityType family descriptor providing {@link CombatData}
     * @param entity     target entity to receive the attribute values
     */
    public static void createAttributes(NativeEntityFamily<?> entityType, LivingEntity entity) {
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
     * Enters combat mode and switches the model variant to armed immediately,
     * without waiting for the next {@link #handleCombatMode()} tick.
     */
    protected void handleActivateCombatMode() {
        combatMode = true;
        waryTimer = getCombatWaryTime();
        // Switch model immediately — same tick as activation, not next handleCombatMode() cycle
        if (nativeEntity != null && !level().isClientSide) {
            String armedKey = getArmedModelVariantKey();
            if (!armedKey.equals(getModelVariant())) setModelVariant(armedKey);
        }
    } // handleActivateCombatMode ()

    /**
     * Ticks the wary timer and drives {@code MODEL_VARIANT} between the armed and
     * default model variants. Resolves variant keys through {@link ModelVariantFeature}
     * rather than hardcoded strings.
     * <p>
     * Call from {@code tick()} on the server side.
     */
    protected void handleCombatMode() {
        // Drop stale targets — dead or removed entities should not sustain combat mode
        if (getTarget() != null && (!getTarget().isAlive() || getTarget().isRemoved())) {
            setTarget(null);
        }

        // Re-enter combat mode whenever a live target is being engaged
        if ((swinging && getTarget() != null) || (getTarget() != null && getTarget().isAlive())) {
            handleActivateCombatMode();
        }

        if (level().isClientSide && !combatMode) return;
        if (nativeEntity == null) return;

        if (waryTimer > 0) {
            // Hold armed model while the wary timer counts down
            String armedKey = getArmedModelVariantKey();
            if (!armedKey.equals(getModelVariant())) setModelVariant(armedKey);
            waryTimer--;
        } else if (combatMode) {
            // Timer expired — return to default model and clear combat mode
            combatMode = false;
            String defaultKey = getDefaultModelVariantKey();
            if (!defaultKey.equals(getModelVariant())) setModelVariant(defaultKey);
        }
    } // handleCombatMode ()

    /**
     * Returns the key of the default (unarmed) model variant from {@link ModelVariantFeature}.
     * Falls back to {@code "default"} if no feature is registered.
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
     * Returns the key of the armed model variant from {@link ModelVariantFeature} —
     * the first registered variant whose key contains {@code "armed"}.
     * Falls back to {@code "armed"} if none is found.
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

    /** Returns {@code true} if this entity is currently in combat mode. */
    public boolean isWary() {
        return combatMode;
    } // isWary ()

    // -- Auto-Heal --

    /**
     * Ticks the auto-heal timer and heals the entity by 1/16 of current health
     * once the interval elapses. Call from {@code tick()} on the server side.
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

    // -- Configurable Timers --

    /**
     * Ticks the entity remains in combat mode after the last combat event.
     * Override to read from a config value.
     */
    protected int getCombatWaryTime() {
        return 100; // 5 seconds default
    } // getCombatWaryTime ()

    /**
     * Ticks between auto-heal pulses.
     * Override to read from a config value.
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
     * Hook called when the behavioral state changes.
     * Handles the built-in Standby / Follow transitions. Override to add further
     * state-specific behavior such as AI goal changes or animation triggers.
     */
    protected void onStateChanged(EntityState newState) {
        if (!isTame()) return;
        switch (newState) {
            case Standby -> {
                setOrderedToSit(true);
                setInSittingPose(true);
                setTarget(null);
                getNavigation().stop();
            }
            case Follow -> {
                setOrderedToSit(false);
                setInSittingPose(false);
            }
            default -> {}
        }
    } // onStateChanged ()

    // -- Variant Accessors --

    /** Returns the active texture variant key. */
    public String getTextureVariant() {
        try { return entityData.get(TEXTURE_VARIANT); }
        catch (Exception ignored) { return "default"; }
    } // getTextureVariant ()

    /** Sets the texture variant key. Silently ignores keys not registered on this entity's family. */
    public void setTextureVariant(String variantKey) {
        if (nativeEntity != null && isValidTextureVariant(variantKey)) {
            entityData.set(TEXTURE_VARIANT, variantKey);
        }
    } // setTextureVariant ()

    /** Returns the active model variant key. */
    public String getModelVariant() {
        try { return entityData.get(MODEL_VARIANT); }
        catch (Exception ignored) { return "default"; }
    } // getModelVariant ()

    /** Sets the model variant key. Silently ignores unregistered keys. */
    public void setModelVariant(String variantKey) {
        if (nativeEntity != null && isValidModelVariant(variantKey)) {
            entityData.set(MODEL_VARIANT, variantKey);
        }
    } // setModelVariant ()

    /** Returns the active animator variant key. */
    public String getAnimatorVariant() {
        try { return entityData.get(ANIMATOR_VARIANT); }
        catch (Exception ignored) { return "default"; }
    } // getAnimatorVariant ()

    /** Sets the animator variant key. Silently ignores unregistered keys. */
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
     * Resolves the active texture variant to a {@link ResourceLocation}.
     * Checks the family's {@link TextureVariantFeature} first, then falls back to
     * the global {@link VariantRegistries}. Returns {@code null} if unresolvable.
     */
    public ResourceLocation getCurrentTexture() {
        if (nativeEntity == null) return null;
        ITextureVariant variant = nativeEntity.getTextureVariant(nativeEntity.getKey(), getTextureVariant());
        if (variant != null) return variant.getResource(nativeEntity.getKey());
        return VariantRegistries.TEXTURES.get(getTextureVariant())
                .map(v -> v.getResource(getTextureVariant()))
                .orElse(null);
    } // getCurrentTexture ()

    /**
     * Resolves the active model variant to a {@link ResourceLocation}.
     * Checks the family's {@link ModelVariantFeature} first, then falls back to
     * the global {@link VariantRegistries}. Returns {@code null} if unresolvable.
     */
    public ResourceLocation getCurrentModel() {
        if (nativeEntity == null) return null;
        IModelVariant variant = nativeEntity.getModelVariant(nativeEntity.getKey(), getModelVariant());
        if (variant != null) return variant.getResource(nativeEntity.getKey());
        return VariantRegistries.MODELS.get(getModelVariant())
                .map(v -> v.getResource(getModelVariant()))
                .orElse(null);
    } // getCurrentModel ()

    /**
     * Resolves the active animator variant to a {@link ResourceLocation}.
     * Checks the family's {@link AnimatorVariantFeature} first, then falls back to
     * the global {@link VariantRegistries}. Returns {@code null} if unresolvable.
     */
    public ResourceLocation getCurrentAnimator() {
        if (nativeEntity == null) return null;
        IAnimatorVariant variant = nativeEntity.getAnimatorVariant(nativeEntity.getKey(), getAnimatorVariant());
        if (variant != null) return variant.getResource(nativeEntity.getKey());
        return VariantRegistries.ANIMATORS.get(getAnimatorVariant())
                .map(v -> v.getResource(getAnimatorVariant()))
                .orElse(null);
    } // getCurrentAnimator ()

    /**
     * Returns the render scale for the active model variant via {@link SizeVariantFeature}.
     * Returns {@code 1.0} if the feature is not present.
     */
    public float getCurrentScale() {
        if (nativeEntity == null || !nativeEntity.hasFeature(SizeVariantFeature.class)) return 1.0f;
        return nativeEntity.getFeature(SizeVariantFeature.class)
                .map(f -> f.getConfig(getModelVariant()).getScale())
                .orElse(1.0f);
    } // getCurrentScale ()

    // -- Variant Validation --

    // Guards setters — ensures only keys registered on the family's features are accepted,
    // preventing client-server desync from unknown variant keys.

    protected boolean isValidTextureVariant(String variantKey) {
        if (nativeEntity == null || variantKey == null) return false;
        return nativeEntity.getFeature(TextureVariantFeature.class)
                .map(f -> f.hasVariant(nativeEntity.getKey(), variantKey))
                .orElseGet(() -> VariantRegistries.TEXTURES.contains(variantKey));
    } // isValidTextureVariant ()

    protected boolean isValidModelVariant(String variantKey) {
        if (nativeEntity == null || variantKey == null) return false;
        return nativeEntity.getFeature(ModelVariantFeature.class)
                .map(f -> f.hasVariant(nativeEntity.getKey(), variantKey))
                .orElseGet(() -> VariantRegistries.MODELS.contains(variantKey));
    } // isValidModelVariant ()

    protected boolean isValidAnimatorVariant(String variantKey) {
        if (nativeEntity == null || variantKey == null) return false;
        return nativeEntity.getFeature(AnimatorVariantFeature.class)
                .map(f -> f.hasVariant(nativeEntity.getKey(), variantKey))
                .orElseGet(() -> VariantRegistries.ANIMATORS.contains(variantKey));
    } // isValidAnimatorVariant ()

    // -- Navigation --

    /** Ground navigation with floating, door-opening, and edge-traversal enabled. */
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
     * Hooks into all removal scenarios to give subclasses a chance to clean up
     * registry state. Only fires on the server side.
     */
    @Override
    public void remove(RemovalReason reason) {
        if (!this.level().isClientSide && this.isTame() && this.getOwnerUUID() != null) {
            unregisterRobot();
        }
        super.remove(reason);
    } // remove ()

    /** Randomises spawn orientation, initialises variants, and applies base attributes. */
    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty,
                                        MobSpawnType spawnReason, @Nullable SpawnGroupData entityData) {
        // Random orientation prevents all fresh spawns facing the same direction
        rotate(Rotation.getRandom(this.getRandom()));
        initializeSpawnVariants(world, spawnReason);
        applyBaseAttributes();
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
    } // finalizeSpawn ()

    /**
     * Selects spawn-time variants with access to world context.
     * <p>
     * Default delegates to {@link #initializeRandomVariants()}. Override to make
     * selection context-aware — biome, dimension, spawn reason, etc.
     */
    protected void initializeSpawnVariants(ServerLevelAccessor world, MobSpawnType reason) {
        initializeRandomVariants();
    } // initializeSpawnVariants ()

    /**
     * Randomly selects texture, model, and animator variants from the family's registered
     * features, then seeds all RANDOM {@link OverlayFeature} slots.
     * <p>
     * INTERACTIVE slots start at their declared default. CONDITIONAL and ALWAYS slots
     * carry no persistent state and are skipped.
     */
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

        // Seed RANDOM overlay slots once at spawn — persisted via SynchedEntityData
        nativeEntity.getFeature(net.heriazone.hzlib.api.entity.features.overlay.OverlayFeature.class)
                .ifPresent(feature -> feature.getSlots().stream()
                        .filter(s -> s.getMode() == net.heriazone.hzlib.api.entity.features.overlay.SlotMode.RANDOM)
                        .forEach(s -> setOverlaySlot(s.getKey(), s.pickRandom())));
    } // initializeRandomVariants ()

    // -- NBT Serialization --

    /**
     * Writes root-level synced fields and, if a schema is declared on the family,
     * the {@code "EntityData"} compound driven by that schema.
     * <p>
     * <b>Layer contract:</b> {@code CompoundTag} is touched only here at the entry
     * point. Everything below operates on {@link DataCompound} — no MC-native tag
     * types appear in the pipeline.
     * <p>
     * <b>Root-level fields:</b> Written directly from their {@code SynchedEntityData}
     * accessors. These must be at root so the migration chain can detect format version
     * before it reads the {@code EntityData} sub-compound.
     * <p>
     * <b>EntityData compound:</b> Written by {@link net.heriazone.hzlib.api.nbt.EntityDataSchema#writeTo}
     * if the family declares a schema. The entity provides values via
     * {@link #provideFieldValue} — no field key strings at call sites.
     */
    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);

        // Wrap immediately — only this line touches CompoundTag
        DataCompound root = NbtAdapterFactory.wrap(nbt);

        // Root-level synced fields: SynchedEntityData is authoritative at runtime
        root.putInt    ("StateId",             getCurrentStateID());
        root.putString ("TextureVariant",      getTextureVariant());
        root.putString ("ModelVariant",        getModelVariant());
        root.putString ("AnimatorVariant",     getAnimatorVariant());
        root.putBoolean("NotificationEnabled", isNotificationEnabled());

        // Exchange cooldowns — sequence buffers are transient
        DataCompound exchangeCompound = NbtAdapterFactory.createEmpty();
        exchangeState.save(nbt); // ExchangeState still uses CompoundTag internally; bridge via nbt
        // (full DataCompound migration for ExchangeState deferred to Phase 2 — not a published field)

        // OverlaySlots — RANDOM and INTERACTIVE slots carry persistent state
        if (!overlaySlotAccessors.isEmpty()) {
            DataCompound overlayCompound = NbtAdapterFactory.createEmpty();
            overlaySlotAccessors.keySet().forEach(key ->
                    overlayCompound.putString(key, getOverlaySlot(key)));
            root.put("OverlaySlots", overlayCompound);
        }

        // EntityData compound — schema-driven, no field key strings in this method
        if (nativeEntity != null && nativeEntity.getSchema() != null) {
            DataCompound entityData = root.getOrCreate("EntityData");
            nativeEntity.getSchema().writeTo(entityData, this::provideFieldValue);
        }
    } // addAdditionalSaveData ()

    /**
     * Loads root-level synced fields and schema-declared fields, running the family's
     * {@link MigrationChain} first if the format is not current.
     * <p>
     * <b>Five-stage read pipeline (ADR 019):</b>
     * <ol>
     *   <li>Wrap — convert MC-native tag to {@link DataCompound}</li>
     *   <li>Migrate — {@link MigrationChain#migrate} upgrades legacy format if needed</li>
     *   <li>Root synced fields — push into {@code SynchedEntityData} (NBT is authoritative on load)</li>
     *   <li>OverlaySlots — restore persistent slot state into their accessors</li>
     *   <li>EntityData — schema reads fields and delivers typed values to {@link #consumeFieldValue}</li>
     * </ol>
     * <b>{@code SynchedEntityData} protocol:</b> Stage 3 pushes values from NBT into the
     * synced accessors before the entity ticks. The register may hold stale pre-load
     * values — always use the NBT value, not the current accessor value, as the source.
     */
    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);

        // Stage 1: wrap — only this line touches CompoundTag
        DataCompound root = NbtAdapterFactory.wrap(nbt);

        // Stage 2: migration — upgrades legacy format to current before schema read
        if (nativeEntity != null) {
            root = nativeEntity.getMigrationChain().migrate(root);
        }

        // Stage 3: push root-level fields into SynchedEntityData
        // NBT is authoritative on load — do not read from entityData register here
        if (root.has("StateId"))             setCurrentState(root.getInt("StateId", EntityState.Follow.getId()));
        if (root.has("NotificationEnabled")) setNotificationEnabled(root.getBoolean("NotificationEnabled", true));

        if (root.has("TextureVariant")) {
            setTextureVariant(root.getString("TextureVariant", "default"));
        } else if (root.has("TextureID")) {
            // Legacy int migration — handled by MigrationChain in Phase 2; fallback guard here
            migrateTextureId(root.getInt("TextureID", 0));
        }

        if (root.has("ModelVariant"))    setModelVariant(root.getString("ModelVariant", "default"));
        if (root.has("AnimatorVariant")) setAnimatorVariant(root.getString("AnimatorVariant", "default"));

        // ExchangeState — still uses CompoundTag bridge (full migration deferred to Phase 2)
        if (nbt.contains("ExchangeState")) exchangeState.load(nbt.getCompound("ExchangeState"));

        // Stage 4: overlay slots — restore RANDOM/INTERACTIVE slot state into their accessors
        if (root.hasCompound("OverlaySlots")) {
            DataCompound overlayCompound = root.getCompound("OverlaySlots");
            overlayCompound.keys().forEach(key -> setOverlaySlot(key, overlayCompound.getString(key, "")));
        }

        // Stage 5: EntityData compound — schema read delivers typed values to consumeFieldValue
        if (nativeEntity != null && nativeEntity.getSchema() != null
                && root.hasCompound("EntityData")) {
            nativeEntity.getSchema().readFrom(root.getCompound("EntityData"), this::consumeFieldValue);
        }
    } // readAdditionalSaveData ()

    /**
     * Returns the current value of {@code field} from this entity's live state.
     * <p>
     * Called by {@link net.heriazone.hzlib.api.nbt.EntityDataSchema#writeTo} once per
     * registered field. Override in subclasses that declare a schema to return the
     * correct live value for each field handle. The base returns the field's default
     * value — entities with no schema do not need to override this.
     * <p>
     * <b>No string keys:</b> match on the field constant identity, not the key string.
     * <pre>{@code
     * protected <T> T provideFieldValue(DataField<T> field) {
     *     if (field == RobotFields.LEVEL)   return field.getType().getType().cast(getCurrentLevel());
     *     if (field == RobotFields.EXP)     return field.getType().getType().cast(getExp());
     *     return super.provideFieldValue(field);
     * }
     * }</pre>
     *
     * @param <T>   the field's value type
     * @param field typed handle identifying which field to provide
     * @return current live value for the field; must not be null
     */
    protected <T> T provideFieldValue(net.heriazone.hzlib.api.nbt.DataField<T> field) {
        return field.getDefaultValue();
    } // provideFieldValue ()

    /**
     * Applies a value loaded from NBT to this entity's state.
     * <p>
     * Called by {@link net.heriazone.hzlib.api.nbt.EntityDataSchema#readFrom} once per
     * registered field. Override in subclasses that declare a schema. For fields that
     * also have a {@code SynchedEntityData} accessor, push the value there too —
     * NBT is authoritative on load, not the synced register.
     * <p>
     * The base implementation is a no-op — entities with no schema do not need to
     * override this.
     *
     * @param <T>   the field's value type
     * @param field typed handle identifying which field was loaded
     * @param value validated value read from NBT (never null; invalid values use field default)
     */
    protected <T> void consumeFieldValue(net.heriazone.hzlib.api.nbt.DataField<T> field, T value) {
        // No-op — override in subclasses that declare a schema
    } // consumeFieldValue ()

    /**
     * Migrates a legacy int-based {@code TextureID} to a string variant key.
     * <p>
     * Only reached on first load of saves that predate both the string variant system
     * and the {@link MigrationChain}. The chain (Phase 2) will handle this properly
     * for published formats — this guard exists for direct int-key loads that slip
     * past the chain. The base falls back to {@code "default"}; override in subclasses
     * that maintain a color palette.
     *
     * @param oldTextureId legacy int texture ID (0–15)
     */
    protected void migrateTextureId(int oldTextureId) {
        setTextureVariant("default");
    } // migrateTextureId ()

    // Base overrides are no-ops — subclasses wire in their sounds via SoundFeature
    // or by overriding these methods directly.

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
     * Runs the shared interaction pipeline in guaranteed order and returns the first
     * non-PASS result. Subclasses extend common logic via {@link #onCommonInteraction},
     * not by overriding this method.
     * <p>
     * Order: {@link ExchangeFeature} → {@link #onCommonInteraction}.
     */
    protected final InteractionResult handleCommonInteractions(Player player, InteractionHand hand, ItemStack stack) {
        // ExchangeFeature always runs first — before taming food or any other logic
        InteractionResult exchangeResult = handleExchangeInteraction(player, stack);
        if (exchangeResult != InteractionResult.PASS) return exchangeResult;

        return onCommonInteraction(player, hand, stack);
    } // handleCommonInteractions ()

    /**
     * Override point for common interaction logic that runs after {@link ExchangeFeature}.
     * <p>
     * Default behavior: attempts taming via {@link FoodFeature} if the entity type
     * declares one, the held item is a registered food, and the entity is untamed.
     * The item is always consumed on a taming attempt; feedback is provided by
     * {@link FoodFeature.TamingFeedback}.
     */
    protected InteractionResult onCommonInteraction(Player player, InteractionHand hand, ItemStack stack) {
        if (nativeEntity == null || level().isClientSide) return InteractionResult.PASS;

        // Taming food — probabilistic attempt, item consumed regardless of outcome
        nativeEntity.getFeature(FoodFeature.class).ifPresent(food -> {
            if (!isTame() && food.isFood(stack)) {
                boolean tamed = food.attemptTame(this, player, stack);
                if (tamed) handleTame(player);
            }
        });

        return InteractionResult.PASS;
    } // onCommonInteraction ()

    /** Entity-type-specific interactions. Implement for all entity-specific input handling. */
    protected abstract InteractionResult handleSpecificInteractions(Player player, InteractionHand hand, ItemStack stack);

    /**
     * Delegates to {@link ExchangeFeature#tryExchange} if the feature is registered.
     * Server-side only — returns PASS immediately on the client.
     *
     * @return SUCCESS if an exchange fired, CONSUME if item entered a partial sequence,
     *         PASS if no rule matched or feature is absent
     */
    protected InteractionResult handleExchangeInteraction(Player player, ItemStack stack) {
        if (level().isClientSide) return InteractionResult.PASS;
        if (nativeEntity == null)  return InteractionResult.PASS;

        return nativeEntity.getFeature(ExchangeFeature.class)
                .map(feature -> feature.tryExchange(this, player, stack, exchangeState))
                .orElse(InteractionResult.PASS);
    } // handleExchangeInteraction ()

    /**
     * Override to handle item-driven texture changes via {@link #setTextureVariant}.
     * Returns {@code false} by default — item is not consumed.
     */
    protected boolean handleTexture(ItemStack stack, Player player) {
        return false;
    } // handleTexture ()

    /**
     * Override to whitelist item types that should reach the interaction pipeline.
     * Returns {@code false} by default.
     */
    protected boolean canInteractWithItems(ItemStack stack) {
        return false;
    } // canInteractWithItems ()

    /**
     * Completes the taming flow: assigns ownership, clears sitting pose, displays a
     * confirmation message, and calls {@link #registerRobot()}.
     * <p>
     * No visual or audio feedback is played here — the base implementation is
     * intentionally silent so subclasses can control presentation independently.
     */
    public void handleTame(Player player) {
        this.tame(player);
        this.setTame(true, false);
        this.setOrderedToSit(false);

        displayTameMessage(player);
        registerRobot();
    } // handleTame ()

    /**
     * Override to display a tame confirmation message to the new owner.
     * No-op by default.
     */
    protected void displayTameMessage(Player player) {
        // No-op — override to show ownership message
    } // displayTameMessage ()

    // -- Registry Lifecycle --

    // Concrete no-ops. Subclasses that maintain an entity registry override these
    // to integrate with their registry system.

    /** Called when this entity is tamed. Override to register in the owner's registry. */
    protected void registerRobot() {
        // No-op — override in subclasses with a registry
    } // registerRobot ()

    /** Called to ensure this entity has a valid registry entry. Override as needed. */
    protected void ensureRegistered() {
        // No-op — override in subclasses with a registry
    } // ensureRegistered ()

    /** Called on removal to clean up the registry entry. Override as needed. */
    protected void unregisterRobot() {
        // No-op — override in subclasses with a registry
    } // unregisterRobot ()

    /** Called periodically to keep the registry "last seen" timestamp current. Override as needed. */
    protected void updateRegistryTimestamp() {
        // No-op — override in subclasses with a registry
    } // updateRegistryTimestamp ()

    // -- Abstract Contracts --

    /** Recalculates all live attributes. Call after level-up, NBT load, or config reload. */
    protected abstract void recalculateAttributes();

    /** Handles item drops on death. */
    protected abstract void handleItemDrop();

    /** Called when this entity attacks a target. Use for combat mode activation and stat tracking. */
    protected abstract void handleAttackTarget(Entity target);

    /**
     * Called when this entity takes damage.
     *
     * @return {@code true} to apply damage normally, {@code false} to cancel it
     */
    protected abstract boolean handleDamage(DamageSource source, float amount);

    // -- Child Creation --

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob otherParent) {
        return null; // HZLib entities do not breed
    } // getBreedOffspring ()

    // -- Utility --

    /** Returns {@code true} if this entity's owner UUID matches the given player. */
    public boolean isOwnedBy(@Nullable Player player) {
        if (player == null) return false;
        return Objects.equals(getOwnerUUID(), player.getUUID());
    } // isOwnedBy ()

    /**
     * Returns the family key used for variant resolution.
     * Returns {@code "unknown"} if {@link #nativeEntity} is not yet set.
     */
    public String getEntityTypeKey() {
        return nativeEntity != null ? nativeEntity.getKey() : "unknown";
    } // getEntityTypeKey ()

} // Class: NativeEntity