package net.heriazone.hzlib.api.entity;

import net.heriazone.hzlib.api.entity.dynamic.*;
import net.heriazone.hzlib.framework.entity.data.*;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import net.heriazone.hzlib.api.entity.features.variants.AnimatorVariantFeature;
import net.heriazone.hzlib.api.entity.features.variants.ModelVariantFeature;
import net.heriazone.hzlib.api.entity.features.variants.TextureVariantFeature;
import net.heriazone.hzlib.api.entity.variants.interfaces.IAnimatorVariant;
import net.heriazone.hzlib.api.entity.variants.interfaces.IModelVariant;
import net.heriazone.hzlib.api.entity.variants.interfaces.ITextureVariant;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>Abstract base class for entity type definitions with Minecraft integration.<p>
 * <p>
 * <b>Architecture:</b> Bridges pure Java framework layer with Minecraft's resource system.
 * Provides feature composition pattern allowing optional modules (TextureVariantFeature, 
 * ModelVariantFeature, etc.) to be attached without inheritance coupling.
 * <p>
 * <b>Design Decision:</b> Generic self-type parameter enables fluent builder pattern with
 * correct return types in subclasses. Feature system uses Class-keyed map for type-safe
 * retrieval without casting. Variant system uses registry-based approach for flexibility.
 * <p>
 * <b>Resource Management:</b> Delegates variant configuration to feature classes,
 * allowing entity-specific variant selection while maintaining consistent access patterns
 * through the global variant registry system.
 * <p>
 * <b>Thread Safety:</b> Not thread-safe. Feature map modifications must be synchronized
 * if accessed across threads.
 *
 * @param <T> concrete entity type for fluent builder pattern
 */
public abstract class InternalEntityType<T extends InternalEntityType<T>> {

    // -- Identification Fields --

    protected final String key;
    protected final MutableComponent name;

    // -- Data Container --

    protected final CombatData data;

    // -- Resource Maps --

    protected final ResourceMap<String, ResourceLocation> textures;
    protected final ResourceMap<String, ResourceLocation> models;
    protected final ResourceMap<String, ResourceLocation> animators;
    //protected final ResourceMap<IInternalVariant, ResourceLocation> textures;
    //protected final ResourceMap<IInternalVariant, ResourceLocation> models;
    //protected final ResourceMap<IInternalVariant, ResourceLocation> animators;

    // -- Feature System --

    private final Map<Class<?>, Object> features;

    // -- Constructors --

    /**
     * Creates entity type with specified identification and initializes feature system.
     * <p>
     * <b>State Impact:</b> Calls abstract configuration method to initialize variant features.
     * Subclasses must ensure configuration methods are safe to call during construction.
     *
     * @param key unique identifier for this entity type
     */
    protected InternalEntityType(String key) {
        this.key = Objects.requireNonNull(key, "Entity type key cannot be null");
        this.data = new CombatData(key, "entity." + key);
        this.name = createTranslation(key);
        this.features = new HashMap<>();

        this.textures = new ResourceMap<>(); // Replace later with a TextureFeature
        this.models = new ResourceMap<>();  // Replace later with a ModelFeature
        this.animators = new ResourceMap<>();  // Replace later with a AnimatorFeature

        // Configure variant features through subclass implementation
        configureVariants();
    } // Constructor: InternalEntityType ()

    // -- Abstract Methods --

    /**
     * Populates texture resource map with variant-specific textures.
     * <p>
     * <b>Implementation Requirements:</b> Must register at least DEFAULT variant.
     * Called during construction, so must not depend on subclass state.
     *
     * @param textures resource map to populate with texture identifiers
     */
    protected abstract void populateTextures(ResourceMap<InternalTextureVariant<?>, ResourceLocation> textures);

    /**
     * Populates model resource map with variant-specific models.
     * <p>
     * <b>Implementation Requirements:</b> Must register at least DEFAULT variant.
     * Called during construction, so must not depend on subclass state.
     *
     * @param models resource map to populate with model identifiers
     */
    protected abstract void populateModels(ResourceMap<InternalModelVariant<?>, ResourceLocation> models);

    /**
     * Populates animator resource map with variant-specific animators.
     * <p>
     * <b>Implementation Requirements:</b> Must register at least DEFAULT variant.
     * Called during construction, so must not depend on subclass state.
     *
     * @param animators resource map to populate with animator identifiers
     */
    protected abstract void populateAnimators(ResourceMap<InternalAnimatorVariant<?>, ResourceLocation> animators);

    /**
     * Creates translatable text component for entity type name.
     * <p>
     * <b>Implementation Requirements:</b> Must return valid MutableComponent for display.
     * Typically, creates translation key from entity type key.
     *
     * @param key entity type key for translation
     * @return translatable text component for entity name
     */
    protected abstract MutableComponent createTranslation(String key);

    /**
     * Configures variant features for this entity type.
     * <p>
     * <b>Implementation Requirements:</b> Must configure at least texture variants.
     * Called during construction, so must not depend on subclass state.
     * <p>
     * <b>Typical Implementation:</b>
     * <pre>{@code
     * protected void configureVariants() {
     *     withFeature(TextureVariantFeature.class, new TextureVariantFeature()
     *         .withVariants(key, "default", "seasonal", "special")
     *         .withDefault(key, "default"));
     * }
     * }</pre>
     */
    protected void configureVariants() {} // TODO: STUDY THIS LATER

    // -- Public Accessors --

    /**
     * Returns unique identifier for this entity type.
     *
     * @return entity type key
     */
    public String getKey() {
        return key;
    } // getKey ()

    /**
     * Returns translatable name for this entity type.
     *
     * @return translatable text component
     */
    public MutableComponent getName() {
        return name;
    } // getName ()

    /**
     * Returns combat statistics and metadata container.
     *
     * @return entity type data
     */
    public CombatData getData() {
        return data;
    } // getData ()

    /**
     * Returns texture resource map.
     *
     * @return texture variant to resource location mapping
     */
    public ResourceMap<String, ResourceLocation> getTextures() {
        return textures;
    } // getTextures ()

    /**
     * Returns model resource map.
     *
     * @return model variant to resource location mapping
     */
    public ResourceMap<String, ResourceLocation> getModels() {
        return models;
    } // getModels ()

    /**
     * Returns animator resource map.
     *
     * @return animator variant to resource location mapping
     */
    public ResourceMap<String, ResourceLocation> getAnimators() {
        return animators;
    } // getAnimators ()

    // -- Variant Access Methods --

    /**
     * Returns texture variant for specified entity key and variant key.
     * <p>
     * <b>Registry Integration:</b> Queries TextureVariantFeature if present,
     * otherwise returns null. Callers should handle null gracefully.
     *
     * @param entityKey entity identifier for variant filtering
     * @param variantKey specific variant to retrieve
     * @return texture variant if available, null otherwise
     */
    public ITextureVariant getTextureVariant(String entityKey, String variantKey) {
        return getFeature(TextureVariantFeature.class)
                .filter(feature -> feature.hasVariant(entityKey, variantKey))
                .map(feature -> feature.getAvailableVariants(entityKey).stream()
                        .filter(variant -> variant.getKey().equals(variantKey))
                        .findFirst()
                        .orElse(null))
                .orElse(null);
    } // getTextureVariant ()

    /**
     * Returns default texture variant for specified entity key.
     * <p>
     * <b>Fallback Behavior:</b> Returns highest priority variant if no default configured.
     *
     * @param entityKey entity identifier for variant filtering
     * @return default texture variant, or null if no variants configured
     */
    public ITextureVariant getDefaultTextureVariant(String entityKey) {
        return getFeature(TextureVariantFeature.class)
                .map(feature -> feature.getDefaultVariant(entityKey))
                .orElse(null);
    } // getDefaultTextureVariant ()

    /**
     * Returns model variant for specified entity key and variant key.
     * <p>
     * <b>Registry Integration:</b> Queries ModelVariantFeature if present,
     * otherwise returns null. Callers should handle null gracefully.
     *
     * @param entityKey entity identifier for variant filtering
     * @param variantKey specific variant to retrieve
     * @return model variant if available, null otherwise
     */
    public IModelVariant getModelVariant(String entityKey, String variantKey) {
        return getFeature(ModelVariantFeature.class)
                .filter(feature -> feature.hasVariant(entityKey, variantKey))
                .map(feature -> feature.getAvailableVariants(entityKey).stream()
                        .filter(variant -> variant.getKey().equals(variantKey))
                        .findFirst()
                        .orElse(null))
                .orElse(null);
    } // getModelVariant ()

    /**
     * Returns default model variant for specified entity key.
     * <p>
     * <b>Fallback Behavior:</b> Returns highest priority variant if no default configured.
     *
     * @param entityKey entity identifier for variant filtering
     * @return default model variant, or null if no variants configured
     */
    public IModelVariant getDefaultModelVariant(String entityKey) {
        return getFeature(ModelVariantFeature.class)
                .map(feature -> feature.getDefaultVariant(entityKey))
                .orElse(null);
    } // getDefaultModelVariant ()

    /**
     * Returns animator variant for specified entity key and variant key.
     * <p>
     * <b>Registry Integration:</b> Queries AnimatorVariantFeature if present,
     * otherwise returns null. Callers should handle null gracefully.
     *
     * @param entityKey entity identifier for variant filtering
     * @param variantKey specific variant to retrieve
     * @return animator variant if available, null otherwise
     */
    public IAnimatorVariant getAnimatorVariant(String entityKey, String variantKey) {
        return getFeature(AnimatorVariantFeature.class)
                .filter(feature -> feature.hasVariant(entityKey, variantKey))
                .map(feature -> feature.getAvailableVariants(entityKey).stream()
                        .filter(variant -> variant.getKey().equals(variantKey))
                        .findFirst()
                        .orElse(null))
                .orElse(null);
    } // getAnimatorVariant ()

    /**
     * Returns default animator variant for specified entity key.
     * <p>
     * <b>Fallback Behavior:</b> Returns highest priority variant if no default configured.
     *
     * @param entityKey entity identifier for variant filtering
     * @return default animator variant, or null if no variants configured
     */
    public IAnimatorVariant getDefaultAnimatorVariant(String entityKey) {
        return getFeature(AnimatorVariantFeature.class)
                .map(feature -> feature.getDefaultVariant(entityKey))
                .orElse(null);
    } // getDefaultAnimatorVariant ()

    // -- Legacy Compatibility Methods --

    /**
     * Returns texture resource location for specified variant.
     * <p>
     * <b>Legacy Support:</b> Maintains compatibility with direct texture access.
     * Uses entity key as both entity and variant identifier for backward compatibility.
     *
     * @param variantKey variant identifier
     * @return texture resource location, or null if not available
     */
    public ResourceLocation getTexture(String variantKey) {
        return textures.get(variantKey);
    } // getTexture ()

    /**
     * Returns model resource location for specified variant.
     * <p>
     * <b>Legacy Support:</b> Maintains compatibility with direct model access.
     * Uses entity key as both entity and variant identifier for backward compatibility.
     *
     * @param variantKey variant identifier
     * @return model resource location, or null if not available
     */
    public ResourceLocation getModel(String variantKey) {
        return models.get(variantKey);
    } // getModel ()

    /**
     * Returns animator resource location for specified variant.
     * <p>
     * <b>Legacy Support:</b> Maintains compatibility with direct animator access.
     * Uses entity key as both entity and variant identifier for backward compatibility.
     *
     * @param variantKey variant identifier
     * @return animator resource location, or null if not available
     */
    public ResourceLocation getAnimator(String variantKey) {
        return animators.get(variantKey);
    } // getAnimator ()

    // -- Visual --

    /**
     * Adds textures to the InternalEntityType.
     *
     * @param overrideDefault indicates whether to override the default texture.
     * @param textureMap      the set of textures to add.
     * @return the updated InternalEntityType instance.
     */
    protected abstract T addTextures(boolean overrideDefault, InternalTextureVariant<?>... textureMap);

    /**
     * Adds animations to the InternalEntityType entity based on the provided animation set.
     *
     * @param animationMap the set of animations to add
     * @return the updated InternalEntityType instance with animations added
     */
    protected abstract T addAnimations(InternalAnimationVariant<?>... animationMap);



    // -- Combat Stats Configuration --

    /**
     * Configures all combat statistics in single fluent call.
     * <p>
     * <b>Design Decision:</b> Bulk setter rather than individual setters reduces
     * boilerplate in entity type definitions while maintaining type safety.
     * <p>
     * <b>State Impact:</b> Updates all combat stat fields in CombatData.
     * Values are clamped to valid ranges by CombatData setters.
     *
     * @param health maximum health points
     * @param attack damage per attack
     * @param speed attacks per second
     * @param armor armor points
     * @param toughness armor toughness points
     * @param knockback knockback resistance (0.0-1.0)
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
     * Attaches feature module to this entity type.
     * <p>
     * <b>Architecture:</b> Enables composition over inheritance for optional functionality.
     * Features are stored by class type, allowing type-safe retrieval without casting.
     * <p>
     * <b>Design Decision:</b> Class-keyed map rather than string keys provides compile-time
     * type safety and eliminates string constant management.
     *
     * @param <F> feature type
     * @param featureClass class object for feature type
     * @param feature feature instance to attach
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
     * Retrieves feature module of specified type.
     * <p>
     * <b>Type Safety:</b> Returns Optional with correct generic type, eliminating
     * need for casting at call sites.
     *
     * @param <F> feature type
     * @param featureClass class object for feature type
     * @return Optional containing feature if present, empty otherwise
     * @throws NullPointerException if featureClass is null
     */
    @SuppressWarnings("unchecked")
    public <F> Optional<F> getFeature(Class<F> featureClass) {
        Objects.requireNonNull(featureClass, "Feature class cannot be null");
        return Optional.ofNullable((F) features.get(featureClass));
    } // getFeature ()

    /**
     * Checks if feature module of specified type is attached.
     *
     * @param featureClass class object for feature type
     * @return true if feature is present, false otherwise
     * @throws NullPointerException if featureClass is null
     */
    public boolean hasFeature(Class<?> featureClass) {
        Objects.requireNonNull(featureClass, "Feature class cannot be null");
        return features.containsKey(featureClass);
    } // hasFeature ()

} // Class: InternalEntityType