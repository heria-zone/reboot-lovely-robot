package net.heriazone.hzlib.api.entity;

import net.heriazone.hzlib.api.entity.features.variants.AnimatorVariantFeature;
import net.heriazone.hzlib.api.entity.features.variants.ModelVariantFeature;
import net.heriazone.hzlib.api.entity.features.variants.TextureVariantFeature;
import net.heriazone.hzlib.api.entity.variants.interfaces.IAnimatorVariant;
import net.heriazone.hzlib.api.entity.variants.interfaces.IModelVariant;
import net.heriazone.hzlib.api.entity.variants.interfaces.ITextureVariant;
import net.heriazone.hzlib.framework.entity.data.CombatData;
import net.minecraft.network.chat.MutableComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>Abstract base class for all entity type definitions in the HZLib ecosystem.<p>
 * <p>
 * <b>Architecture:</b> Single shared base for both robot types ({@code NativeEntityType}
 * in lovelylib) and monster types ({@code NativeEntityType} in monsters_girls). Provides
 * the feature composition pattern, combat stats configuration, and the
 * {@link #configureVariants()} hook for variant registration.
 * <p>
 * <b>Design Decision:</b> Generic self-type parameter enables fluent builder pattern with
 * correct return types in subclasses. Feature system uses Class-keyed map for type-safe
 * retrieval without casting. Variant registration is delegated to subclasses via
 * {@link #configureVariants()} rather than abstract populate methods, allowing
 * string-keyed variant features ({@link TextureVariantFeature}, {@link ModelVariantFeature},
 * {@link AnimatorVariantFeature}) to be registered without enum coupling.
 * <p>
 * <b>Variant System:</b> All variant resolution goes through the feature system.
 * Subclasses register variants in {@link #configureVariants()} using
 * {@link #withFeature(Class, Object)}. Entities resolve variants at runtime via
 * {@link #getTextureVariant(String, String)}, {@link #getModelVariant(String, String)},
 * and {@link #getAnimatorVariant(String, String)}.
 * <p>
 * <b>Thread Safety:</b> Not thread-safe. Feature map modifications must be synchronized
 * if accessed across threads.
 *
 * @param <T> concrete entity type for fluent builder pattern
 */
public abstract class NativeEntityFamily<T extends NativeEntityFamily<T>> {

    // -- Identification Fields --

    protected final String key;
    protected final MutableComponent name;

    // -- Data Container --

    /**
     * <p>Base combat statistics for this entity type.<p>
     * <p>
     * <b>Architecture:</b> Contains HP, attack, speed, armor — the flat stats every
     * entity needs for {@code createAttributes()}. No leveling, no experience.
     * Robot-specific scaling (CombatLevelStats) lives in HZLib's RobotEntity tier.
     */
    protected final CombatData data;

    // -- Feature System --

    private final Map<Class<?>, Object> features;

    // -- Constructor --

    /**
     * Creates entity type with specified key, initializes combat data, and calls
     * {@link #configureVariants()} to allow subclasses to register variant features.
     * <p>
     * <b>State Impact:</b> {@link #configureVariants()} is called during construction.
     * Subclass implementations must not depend on subclass-level state that hasn't
     * been initialized yet.
     *
     * @param key unique identifier for this entity type (e.g., {@code "bunny"}, {@code "mushroom_brown"})
     * @throws NullPointerException if key is null
     */
    protected NativeEntityFamily(String key) {
        this.key      = Objects.requireNonNull(key, "Entity type key cannot be null");
        this.data     = new CombatData(key, "entity." + key);
        this.name     = createTranslation(key);
        this.features = new HashMap<>();

        // Allow subclasses to register variant features (TextureVariantFeature, etc.)
        configureVariants();
    } // Constructor: NativeEntityFamily ()

    // -- Abstract Methods --

    /**
     * Creates the translatable display name for this entity type.
     * <p>
     * <b>Implementation:</b> Typically returns a translation component using the
     * entity type key. Called during construction — must not depend on subclass state.
     *
     * @param key entity type key
     * @return translatable text component for display
     */
    protected abstract MutableComponent createTranslation(String key);

    // -- Variant Configuration Hook --

    /**
     * Registers variant features for this entity type.
     * <p>
     * <b>Override contract:</b> Override to register {@link TextureVariantFeature},
     * {@link ModelVariantFeature}, and {@link AnimatorVariantFeature} via
     * {@link #withFeature(Class, Object)}. Called during construction.
     * <p>
     * <b>Robot pattern:</b>
     * <pre>{@code
     * @Override
     * protected void configureVariants() {
     *     withFeature(ModelVariantFeature.class, new ModelVariantFeature()
     *         .withVariants(key, "default", "armed")
     *         .withDefault(key, "default"));
     *     withFeature(AnimatorVariantFeature.class, new AnimatorVariantFeature()
     *         .withVariants(key, "default")
     *         .withDefault(key, "default"));
     *     // Texture variants registered separately via withColorPalette()
     * }
     * }</pre>
     * <p>
     * <b>Monster pattern:</b>
     * <pre>{@code
     * @Override
     * protected void configureVariants() {
     *     withFeature(TextureVariantFeature.class, new TextureVariantFeature()
     *         .withVariants(key, "default", "tummy")
     *         .withDefault(key, "default"));
     *     withFeature(ModelVariantFeature.class, new ModelVariantFeature()
     *         .withVariants(key, "default")
     *         .withDefault(key, "default"));
     *     withFeature(AnimatorVariantFeature.class, new AnimatorVariantFeature()
     *         .withVariants(key, "default")
     *         .withDefault(key, "default"));
     * }
     * }</pre>
     */
    protected void configureVariants() {
        // Default: no variants registered. Subclasses override to register features.
    } // configureVariants ()

    // -- Public Accessors --

    /**
     * Returns the unique identifier for this entity type.
     *
     * @return entity type key
     */
    public String getKey() {
        return key;
    } // getKey ()

    /**
     * Returns the translatable display name for this entity type.
     *
     * @return translatable text component
     */
    public MutableComponent getName() {
        return name;
    } // getName ()

    /**
     * Returns the base combat statistics container.
     * <p>
     * <b>Usage:</b> Read by {@code NativeEntity.createAttributes()} to set
     * Minecraft entity attributes (MAX_HEALTH, ATTACK_DAMAGE, etc.).
     *
     * @return combat data with base HP, attack, speed, armor
     */
    public CombatData getData() {
        return data;
    } // getData ()

    // -- Variant Access Methods --

    /**
     * Returns the texture variant for the specified entity key and variant key.
     * <p>
     * <b>Null safety:</b> Returns {@code null} if no {@link TextureVariantFeature}
     * is registered or if the variant key is not found. Callers should handle null.
     *
     * @param entityKey  entity identifier for variant filtering
     * @param variantKey specific variant key to retrieve (e.g., {@code "white"}, {@code "default"})
     * @return texture variant if available, {@code null} otherwise
     */
    public ITextureVariant getTextureVariant(String entityKey, String variantKey) {
        return getFeature(TextureVariantFeature.class)
                .filter(feature -> feature.hasVariant(entityKey, variantKey))
                .map(feature -> feature.getAvailableVariants(entityKey).stream()
                        .filter(v -> v.getKey().equals(variantKey))
                        .findFirst()
                        .orElse(null))
                .orElse(null);
    } // getTextureVariant ()

    /**
     * Returns the default texture variant for the specified entity key.
     *
     * @param entityKey entity identifier
     * @return default texture variant, or {@code null} if no variants configured
     */
    public ITextureVariant getDefaultTextureVariant(String entityKey) {
        return getFeature(TextureVariantFeature.class)
                .map(feature -> feature.getDefaultVariant(entityKey))
                .orElse(null);
    } // getDefaultTextureVariant ()

    /**
     * Returns the model variant for the specified entity key and variant key.
     *
     * @param entityKey  entity identifier
     * @param variantKey specific variant key (e.g., {@code "default"}, {@code "armed"})
     * @return model variant if available, {@code null} otherwise
     */
    public IModelVariant getModelVariant(String entityKey, String variantKey) {
        return getFeature(ModelVariantFeature.class)
                .filter(feature -> feature.hasVariant(entityKey, variantKey))
                .map(feature -> feature.getAvailableVariants(entityKey).stream()
                        .filter(v -> v.getKey().equals(variantKey))
                        .findFirst()
                        .orElse(null))
                .orElse(null);
    } // getModelVariant ()

    /**
     * Returns the default model variant for the specified entity key.
     *
     * @param entityKey entity identifier
     * @return default model variant, or {@code null} if no variants configured
     */
    public IModelVariant getDefaultModelVariant(String entityKey) {
        return getFeature(ModelVariantFeature.class)
                .map(feature -> feature.getDefaultVariant(entityKey))
                .orElse(null);
    } // getDefaultModelVariant ()

    /**
     * Returns the animator variant for the specified entity key and variant key.
     *
     * @param entityKey  entity identifier
     * @param variantKey specific variant key (e.g., {@code "default"})
     * @return animator variant if available, {@code null} otherwise
     */
    public IAnimatorVariant getAnimatorVariant(String entityKey, String variantKey) {
        return getFeature(AnimatorVariantFeature.class)
                .filter(feature -> feature.hasVariant(entityKey, variantKey))
                .map(feature -> feature.getAvailableVariants(entityKey).stream()
                        .filter(v -> v.getKey().equals(variantKey))
                        .findFirst()
                        .orElse(null))
                .orElse(null);
    } // getAnimatorVariant ()

    /**
     * Returns the default animator variant for the specified entity key.
     *
     * @param entityKey entity identifier
     * @return default animator variant, or {@code null} if no variants configured
     */
    public IAnimatorVariant getDefaultAnimatorVariant(String entityKey) {
        return getFeature(AnimatorVariantFeature.class)
                .map(feature -> feature.getDefaultVariant(entityKey))
                .orElse(null);
    } // getDefaultAnimatorVariant ()

    // -- Combat Stats Configuration --

    /**
     * Configures all base combat statistics in a single fluent call.
     * <p>
     * <b>Design Decision:</b> Bulk setter reduces boilerplate in entity type definitions.
     * These are the flat base stats — no leveling, no scaling. Robot-specific stat
     * scaling (CombatLevelStats) is handled in HZLib's RobotEntity tier.
     *
     * @param health    maximum health points
     * @param attack    damage per attack
     * @param speed     attack speed (attacks per second)
     * @param armor     armor points
     * @param toughness armor toughness points
     * @param knockback knockback resistance (0.0–1.0)
     * @param moveSpeed movement speed multiplier
     * @return this instance for method chaining
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
     * Attaches a feature module to this entity type.
     * <p>
     * <b>Architecture:</b> Enables composition over inheritance for optional functionality.
     * Features are stored by class type, allowing type-safe retrieval without casting.
     * <p>
     * <b>Examples:</b> {@link TextureVariantFeature}, {@link ModelVariantFeature},
     * {@link AnimatorVariantFeature}, {@code LevelFeature}, {@code FoodFeature},
     * {@code DropFeature}, {@code PickupFeature}.
     *
     * @param <F>          feature type
     * @param featureClass class object for the feature type
     * @param feature      feature instance to attach
     * @return this instance for method chaining
     * @throws NullPointerException if featureClass or feature is null
     */
    @SuppressWarnings("unchecked")
    public <F> T withFeature(Class<F> featureClass, F feature) {
        Objects.requireNonNull(featureClass, "Feature class cannot be null");
        Objects.requireNonNull(feature, "Feature instance cannot be null");
        features.put(featureClass, feature);
        return (T) this;
    } // withFeature ()

    /**
     * Retrieves a feature module of the specified type.
     * <p>
     * <b>Type Safety:</b> Returns {@code Optional} with correct generic type,
     * eliminating the need for casting at call sites.
     *
     * @param <F>          feature type
     * @param featureClass class object for the feature type
     * @return {@code Optional} containing the feature if present, empty otherwise
     * @throws NullPointerException if featureClass is null
     */
    @SuppressWarnings("unchecked")
    public <F> Optional<F> getFeature(Class<F> featureClass) {
        Objects.requireNonNull(featureClass, "Feature class cannot be null");
        return Optional.ofNullable((F) features.get(featureClass));
    } // getFeature ()

    /**
     * Checks whether a feature module of the specified type is attached.
     *
     * @param featureClass class object for the feature type
     * @return {@code true} if the feature is present, {@code false} otherwise
     * @throws NullPointerException if featureClass is null
     */
    public boolean hasFeature(Class<?> featureClass) {
        Objects.requireNonNull(featureClass, "Feature class cannot be null");
        return features.containsKey(featureClass);
    } // hasFeature ()

} // Class: NativeEntityFamily