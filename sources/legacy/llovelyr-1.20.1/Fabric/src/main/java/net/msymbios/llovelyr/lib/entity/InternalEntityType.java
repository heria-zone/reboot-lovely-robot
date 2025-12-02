package net.msymbios.llovelyr.lib.entity.type;

import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;
import net.msymbios.llovelyr.framework.entity.enums.EntityVariantAnimator;
import net.msymbios.llovelyr.framework.entity.enums.EntityVariantModel;
import net.msymbios.llovelyr.framework.entity.enums.EntityVariantTexture;
import net.msymbios.llovelyr.framework.entity.type.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>Abstract base class for entity type definitions with Minecraft integration.<p>
 * <p>
 * <b>Architecture:</b> Bridges pure Java framework layer with Minecraft's resource system.
 * Provides feature composition pattern allowing optional modules (LevelFeature, FoodFeature, etc.)
 * to be attached without inheritance coupling.
 * <p>
 * <b>Design Decision:</b> Generic self-type parameter enables fluent builder pattern with
 * correct return types in subclasses. Feature system uses Class-keyed map for type-safe
 * retrieval without casting.
 * <p>
 * <b>Resource Management:</b> Delegates texture/model/animator population to subclasses
 * through abstract methods, allowing variant-specific resource organization while maintaining
 * consistent access patterns.
 * <p>
 * <b>Thread Safety:</b> Not thread-safe. Feature map modifications must be synchronized
 * if accessed across threads.
 * 
 * @param <T> concrete entity type for fluent builder pattern
 */
public abstract class InternalEntityType<T extends InternalEntityType<T>> {

    // -- Identification Fields --
    
    protected final String key;
    protected final MutableText name;
    
    // -- Data Container --
    
    protected final CombatData data;
    
    // -- Resource Maps --
    
    protected final ResourceMap<EntityVariantTexture, Identifier> textures;
    protected final ResourceMap<EntityVariantModel, Identifier> models;
    protected final ResourceMap<EntityVariantAnimator, Identifier> animators;
    
    // -- Feature System --
    
    private final Map<Class<?>, Object> features;
    
    // -- Constructors --
    
    /**
     * Creates entity type with specified identification and initializes resource maps.
     * <p>
     * <b>State Impact:</b> Calls abstract population methods to initialize resource maps.
     * Subclasses must ensure population methods are safe to call during construction.
     * 
     * @param key unique identifier for this entity type
     */
    protected InternalEntityType(String key) {
        this.key = Objects.requireNonNull(key, "Entity type key cannot be null");
        this.data = new CombatData(key, "entity." + key);
        this.name = createTranslation(key);
        
        this.textures = new ResourceMap<>();
        this.models = new ResourceMap<>();
        this.animators = new ResourceMap<>();
        this.features = new HashMap<>();
        
        // Populate resources through subclass implementations
        populateTextures(this.textures);
        populateModels(this.models);
        populateAnimators(this.animators);
    }
    
    // -- Abstract Methods --
    
    /**
     * Populates texture resource map with variant-specific textures.
     * <p>
     * <b>Implementation Requirements:</b> Must register at least DEFAULT variant.
     * Called during construction, so must not depend on subclass state.
     * 
     * @param textures resource map to populate with texture identifiers
     */
    protected abstract void populateTextures(ResourceMap<EntityVariantTexture, Identifier> textures);
    
    /**
     * Populates model resource map with variant-specific models.
     * <p>
     * <b>Implementation Requirements:</b> Must register at least DEFAULT variant.
     * Called during construction, so must not depend on subclass state.
     * 
     * @param models resource map to populate with model identifiers
     */
    protected abstract void populateModels(ResourceMap<EntityVariantModel, Identifier> models);
    
    /**
     * Populates animator resource map with variant-specific animators.
     * <p>
     * <b>Implementation Requirements:</b> Must register at least DEFAULT variant.
     * Called during construction, so must not depend on subclass state.
     * 
     * @param animators resource map to populate with animator identifiers
     */
    protected abstract void populateAnimators(ResourceMap<EntityVariantAnimator, Identifier> animators);
    
    /**
     * Creates translatable text component for entity type name.
     * <p>
     * <b>Implementation Requirements:</b> Must return valid MutableText for display.
     * Typically creates translation key from entity type key.
     * 
     * @param key entity type key for translation
     * @return translatable text component for entity name
     */
    protected abstract MutableText createTranslation(String key);
    
    // -- Public Accessors --
    
    /**
     * Returns unique identifier for this entity type.
     * 
     * @return entity type key
     */
    public String getKey() {
        return key;
    }
    
    /**
     * Returns translatable name for this entity type.
     * 
     * @return translatable text component
     */
    public MutableText getName() {
        return name;
    }
    
    /**
     * Returns combat statistics and metadata container.
     * 
     * @return entity type data
     */
    public CombatData getData() {
        return data;
    }
    
    /**
     * Returns texture resource map.
     * 
     * @return texture variant to identifier mapping
     */
    public ResourceMap<EntityVariantTexture, Identifier> getTextures() {
        return textures;
    }
    
    /**
     * Returns model resource map.
     * 
     * @return model variant to identifier mapping
     */
    public ResourceMap<EntityVariantModel, Identifier> getModels() {
        return models;
    }
    
    /**
     * Returns animator resource map.
     * 
     * @return animator variant to identifier mapping
     */
    public ResourceMap<EntityVariantAnimator, Identifier> getAnimators() {
        return animators;
    }
    
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
    }
    
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
    }
    
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
    }
    
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
    }

} // Class: InternalEntityType
