package net.heriazone.hzlib.api.entity;

import net.heriazone.hzlib.api.entity.features.variants.*;
import net.heriazone.hzlib.api.entity.variants.interfaces.*;
import net.heriazone.hzlib.api.nbt.EntityDataSchema;
import net.heriazone.hzlib.api.nbt.MigrationChain;
import net.heriazone.hzlib.framework.entity.data.CombatData;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.animal.Animal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Root family descriptor for all HZLib-managed entity types.
 * <p>
 * <b>Architecture:</b> Each concrete instance represents one registered {@code EntityType<>}
 * — its key, display name, base combat stats, and the full set of feature modules that
 * govern its appearance and behaviour. Subclasses extend this to add domain-specific
 * defaults and variant registration patterns.
 * <p>
 * <b>Feature composition:</b> Optional capabilities ({@link TextureVariantFeature},
 * {@link ModelVariantFeature}, {@link AnimatorVariantFeature}, and any framework feature)
 * are attached via {@link #withFeature} and retrieved via {@link #getFeature}. This avoids
 * inheritance hierarchies for optional concerns.
 * <p>
 * <b>Variant registration:</b> Subclasses declare their variants by overriding
 * {@link #configureVariants()}, which is called during construction. The three standard
 * appearance dimensions — texture, model, animator — each have a dedicated feature class.
 * <p>
 * <b>Self-type parameter:</b> {@code T} enables the fluent builder methods
 * ({@link #withFeature}, {@link #withCombatStats}) to return the concrete subtype,
 * preserving method chaining without unchecked casts at call sites.
 *
 * @param <T> the concrete subtype, used for fluent builder return types
 */
public abstract class NativeEntityFamily<T extends NativeEntityFamily<T>> {

    // -- Identity --

    protected final String key;
    protected final MutableComponent name;

    // -- Animation --

    /**
     * Name of the head bone used by {@code NativeModel.setCustomAnimations()} for
     * look-direction tracking. Defaults to {@code "head"}, which covers every existing
     * family without any migration — only families whose {@code .geo.json} uses a
     * different bone name need to call {@link #headBone(String)}.
     */
    private String headBoneName = "head";

    // -- Combat Data --

    /**
     * Base flat stats for this family — health, attack, speed, armor.
     * Consumed by {@link NativeEntity#createAttributes} to set Minecraft entity attributes.
     * Extended tiers may layer additional scaling on top via their own data containers.
     */
    protected final CombatData data;

    // -- Data Pipeline --

    /**
     * Schema governing this family's entity data fields — declared by {@link #configureSchema()},
     * used by {@code NativeEntity} save/load pipeline. Null until {@link #configureSchema()} runs.
     */
    protected EntityDataSchema schema;

    /**
     * Migration chain applied when loading legacy saves for this family.
     * Null until {@link #configureSchema()} runs; defaults to {@link MigrationChain#empty()}.
     */
    protected MigrationChain migrationChain;

    // Class-keyed so getFeature() returns the correct generic type without casting.
    private final Map<Class<?>, Object> features;

    // -- Constructor --

    /**
     * Initialises the family with the given key, creates its {@link CombatData},
     * resolves the display name, and calls {@link #configureVariants()}.
     * <p>
     * {@link #configureVariants()} fires during construction — subclass overrides
     * must not reference subclass-level fields that have not yet been assigned.
     *
     * @param key unique registry identifier for this family
     * @throws NullPointerException if key is null
     */
    protected NativeEntityFamily(String key) {
        this.key      = Objects.requireNonNull(key, "Family key cannot be null");
        this.data     = new CombatData(key, "entity." + key);
        this.name     = createTranslation(key);
        this.features = new HashMap<>();

        // Variant features are registered here so the family is fully configured
        // immediately after construction without a separate init call.
        configureVariants();

        // Schema and migration chain are configured after variants — safe to reference
        // static DataField constants declared in subclasses at this point.
        configureSchema();
        if (migrationChain == null) migrationChain = MigrationChain.empty();
    } // Constructor: NativeEntityFamily ()

    // -- Abstract Methods --

    /**
     * Returns the translatable display name for this family.
     * Called during construction — must not depend on subclass state.
     *
     * @param key the family's registry key
     * @return translatable text component for in-game display
     */
    protected abstract MutableComponent createTranslation(String key);

    // -- Variant Configuration --

    /**
     * Override point for declaring the entity data schema and migration chain.
     * <p>
     * <b>When it runs:</b> Called from the constructor after {@link #configureVariants()},
     * so static {@link net.heriazone.hzlib.api.nbt.DataField} constants are safe to
     * reference. Do not reference subclass <em>instance</em> fields assigned after
     * {@code super()} — they have not yet been set.
     * <p>
     * <b>Typical implementation:</b>
     * <pre>{@code
     * protected void configureSchema() {
     *     schema = EntityDataSchema.builder()
     *         .register(RobotFields.LEVEL)
     *         .register(RobotFields.EXP)
     *         .version("1.0.0")
     *         .build();
     *     migrationChain = MigrationChain.builder()
     *         .addStep(new MigrationStep_V0_Fabric())
     *         .build();
     * }
     * }</pre>
     * Families with no persisted fields and no legacy saves do not need to override this.
     */
    protected void configureSchema() {
        // No-op — override to declare schema and migration chain
    } // configureSchema ()

    /**
     * Override point for registering variant features at construction time.
     * <p>
     * Use {@link #withFeature} to attach {@link TextureVariantFeature},
     * {@link ModelVariantFeature}, and {@link AnimatorVariantFeature} as needed.
     * The base implementation is a no-op — families with no appearance variants
     * do not need to override this.
     */
    protected void configureVariants() {
        // No-op — override to register TextureVariantFeature, ModelVariantFeature, etc.
    } // configureVariants ()

    // -- Schema / Migration Accessors --

    /**
     * Returns the entity data schema declared by {@link #configureSchema()}.
     * Returns {@code null} if the family declared no schema — the entity save path
     * skips the {@code EntityData} compound write when this is null.
     */
    public EntityDataSchema getSchema() { return schema; } // getSchema ()

    /**
     * Returns the migration chain for this family. Never null after construction —
     * defaults to {@link MigrationChain#empty()} when {@link #configureSchema()}
     * does not assign one.
     */
    public MigrationChain getMigrationChain() { return migrationChain; } // getMigrationChain ()

    // -- Identity Accessors --

    /** Returns the unique registry key for this family. */
    public String getKey() {
        return key;
    } // getKey ()

    // -- Animation Accessors --

    /**
     * Returns the head bone name used for look-direction tracking in
     * {@code NativeModel.setCustomAnimations()}. Defaults to {@code "head"}.
     *
     * @return bone name as declared in the family's {@code .geo.json}; never {@code null}
     */
    public String getHeadBoneName() {
        return headBoneName;
    } // getHeadBoneName ()

    /**
     * Sets the head bone name for look-direction tracking.
     * <p>
     * Only needed when a family's {@code .geo.json} uses a bone name other than
     * {@code "head"}. All existing families implicitly use the default — zero migration.
     *
     * @param boneName exact bone name as declared in the {@code .geo.json} file
     * @return this instance for chaining
     * @throws IllegalArgumentException if {@code boneName} is null or blank
     */
    @SuppressWarnings("unchecked")
    public T headBone(String boneName) {
        if (boneName == null || boneName.isBlank()) {
            throw new IllegalArgumentException("headBoneName must not be null or blank");
        }
        this.headBoneName = boneName;
        return (T) this;
    } // headBone ()

    /** Returns the translatable display name for this family. */
    public MutableComponent getName() {
        return name;
    } // getName ()

    /**
     * Returns the flat combat stats for this family.
     * Read by {@link NativeEntity#createAttributes} to populate Minecraft entity attributes.
     */
    public CombatData getData() {
        return data;
    } // getData ()

    // -- Variant Accessors --

    /**
     * Returns the texture variant registered under {@code variantKey} for {@code entityKey},
     * or {@code null} if no {@link TextureVariantFeature} is present or the key is unknown.
     */
    public ITextureVariant getTextureVariant(String entityKey, String variantKey) {
        return getFeature(TextureVariantFeature.class)
                .filter(f -> f.hasVariant(entityKey, variantKey))
                .map(f -> f.getAvailableVariants(entityKey).stream()
                        .filter(v -> v.getKey().equals(variantKey))
                        .findFirst()
                        .orElse(null))
                .orElse(null);
    } // getTextureVariant ()

    /** Returns the default texture variant for {@code entityKey}, or {@code null} if none is configured. */
    public ITextureVariant getDefaultTextureVariant(String entityKey) {
        return getFeature(TextureVariantFeature.class)
                .map(f -> f.getDefaultVariant(entityKey))
                .orElse(null);
    } // getDefaultTextureVariant ()

    /**
     * Returns the model variant registered under {@code variantKey} for {@code entityKey},
     * or {@code null} if no {@link ModelVariantFeature} is present or the key is unknown.
     */
    public IModelVariant getModelVariant(String entityKey, String variantKey) {
        return getFeature(ModelVariantFeature.class)
                .filter(f -> f.hasVariant(entityKey, variantKey))
                .map(f -> f.getAvailableVariants(entityKey).stream()
                        .filter(v -> v.getKey().equals(variantKey))
                        .findFirst()
                        .orElse(null))
                .orElse(null);
    } // getModelVariant ()

    /** Returns the default model variant for {@code entityKey}, or {@code null} if none is configured. */
    public IModelVariant getDefaultModelVariant(String entityKey) {
        return getFeature(ModelVariantFeature.class)
                .map(f -> f.getDefaultVariant(entityKey))
                .orElse(null);
    } // getDefaultModelVariant ()

    /**
     * Returns the animator variant registered under {@code variantKey} for {@code entityKey},
     * or {@code null} if no {@link AnimatorVariantFeature} is present or the key is unknown.
     */
    public IAnimatorVariant getAnimatorVariant(String entityKey, String variantKey) {
        return getFeature(AnimatorVariantFeature.class)
                .filter(f -> f.hasVariant(entityKey, variantKey))
                .map(f -> f.getAvailableVariants(entityKey).stream()
                        .filter(v -> v.getKey().equals(variantKey))
                        .findFirst()
                        .orElse(null))
                .orElse(null);
    } // getAnimatorVariant ()

    /** Returns the default animator variant for {@code entityKey}, or {@code null} if none is configured. */
    public IAnimatorVariant getDefaultAnimatorVariant(String entityKey) {
        return getFeature(AnimatorVariantFeature.class)
                .map(f -> f.getDefaultVariant(entityKey))
                .orElse(null);
    } // getDefaultAnimatorVariant ()

    // -- Combat Stats --

    /**
     * Sets all flat combat stats in a single fluent call.
     * These are base values with no level scaling — extended tiers add scaling on top.
     *
     * @param health    maximum health
     * @param attack    attack damage
     * @param speed     attack speed
     * @param armor     armor points
     * @param toughness armor toughness
     * @param knockback knockback resistance (0.0–1.0)
     * @param moveSpeed movement speed multiplier
     * @return this instance for chaining
     */
    @SuppressWarnings("unchecked")
    public T withCombatStats(float health, float attack, float speed,
                             float armor, float toughness, float knockback, float moveSpeed) {
        data.setMaxHealth(health);
        data.setAttackDamage(attack);
        data.setAttackSpeed(speed);
        data.setArmor(armor);
        data.setArmorToughness(toughness);
        data.setKnockbackResistance(knockback);
        data.setMoveSpeed(moveSpeed);
        return (T) this;
    } // withCombatStats ()

    // -- Feature System --

    /**
     * Attaches a feature module, replacing any previously registered module of the same type.
     * <p>
     * The feature map is Class-keyed so each feature type has at most one instance.
     * Calling this twice with the same class overwrites the first — intentional for
     * subclass overrides that refine a parent's default feature configuration.
     *
     * @param <F>          the feature type
     * @param featureClass class token used as the map key
     * @param feature      feature instance to attach
     * @return this instance for chaining
     */
    @SuppressWarnings("unchecked")
    public <F> T withFeature(Class<F> featureClass, F feature) {
        Objects.requireNonNull(featureClass, "Feature class cannot be null");
        Objects.requireNonNull(feature, "Feature instance cannot be null");
        features.put(featureClass, feature);
        return (T) this;
    } // withFeature ()

    /**
     * Returns the feature registered under {@code featureClass}, or empty if none is attached.
     * The {@code Optional} carries the correct generic type — no cast needed at call sites.
     *
     * @param <F>          the feature type
     * @param featureClass class token used as the map key
     * @return the attached feature, or {@link Optional#empty()}
     */
    @SuppressWarnings("unchecked")
    public <F> Optional<F> getFeature(Class<F> featureClass) {
        Objects.requireNonNull(featureClass, "Feature class cannot be null");
        return Optional.ofNullable((F) features.get(featureClass));
    } // getFeature ()

    /** Returns {@code true} if a feature of the given type is attached. */
    public boolean hasFeature(Class<?> featureClass) {
        Objects.requireNonNull(featureClass, "Feature class cannot be null");
        return features.containsKey(featureClass);
    } // hasFeature ()

    // -- Attribute Factory Methods --

    /**
     * Builds a finished {@link AttributeSupplier} from this family's {@link CombatData}.
     * Used at entity type registration time when a completed supplier is required.
     */
    public static AttributeSupplier createAttributes(NativeEntityFamily<?> entity) {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH,          entity.getData().getMaxHealth())
                .add(Attributes.ATTACK_DAMAGE,        entity.getData().getAttackDamage())
                .add(Attributes.ATTACK_SPEED,         entity.getData().getAttackSpeed())
                .add(Attributes.MOVEMENT_SPEED,       entity.getData().getMoveSpeed())
                .add(Attributes.ARMOR,                entity.getData().getArmor())
                .add(Attributes.ARMOR_TOUGHNESS,      entity.getData().getArmorToughness())
                .add(Attributes.KNOCKBACK_RESISTANCE, entity.getData().getKnockbackResistance())
                .build();
    } // createAttributes ()

    /**
     * Returns an open {@link AttributeSupplier.Builder} for ground entities.
     * Callers can add further attributes before calling {@code build()}.
     */
    public static AttributeSupplier.Builder createGroundAttributes(NativeEntityFamily<?> type) {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH,          type.getData().getMaxHealth())
                .add(Attributes.ATTACK_DAMAGE,        type.getData().getAttackDamage())
                .add(Attributes.ATTACK_SPEED,         type.getData().getAttackSpeed())
                .add(Attributes.MOVEMENT_SPEED,       type.getData().getMoveSpeed())
                .add(Attributes.ARMOR,                type.getData().getArmor())
                .add(Attributes.ARMOR_TOUGHNESS,      type.getData().getArmorToughness())
                .add(Attributes.KNOCKBACK_RESISTANCE, type.getData().getKnockbackResistance());
    } // createGroundAttributes ()

    /**
     * Returns an open {@link AttributeSupplier.Builder} for flying entities.
     * Extends {@link #createGroundAttributes} with {@code FLYING_SPEED} set to the
     * family's movement speed value.
     */
    public static AttributeSupplier.Builder createFlyingAttributes(NativeEntityFamily<?> type) {
        return createGroundAttributes(type).add(Attributes.FLYING_SPEED, type.getData().getMoveSpeed());
    } // createFlyingAttributes ()

} // Class: NativeEntityFamily