package net.heriazone.hzlib.api.entity.variants;

import net.heriazone.hzlib.api.entity.variants.interfaces.*;
import java.util.*;

/**
 * <p>Registry for managing variant instances with type-safe operations.<p>
 * <p>
 * <b>Architecture:</b> Provides centralized registration and retrieval of variant instances
 * with support for default selection, random selection, and priority-based ordering.
 * Uses LinkedHashMap to preserve registration order while enabling efficient lookups.
 * <p>
 * <b>Design Decision:</b> Generic type parameter ensures compile-time type safety while
 * allowing different variant types (texture, model, animator) to use the same registry
 * infrastructure without casting.
 * <p>
 * <b>Thread Safety:</b> Not thread-safe. Registration should occur during initialization
 * phase before concurrent access begins.
 *
 * @param <T> variant type extending IVariant
 */
public class VariantRegistry<T extends IVariant> {

    // -- Fields --

    private final Map<String, T> variants = new LinkedHashMap<>();
    private final Class<T> variantType;

    // -- Constructor --

    /**
     * Creates variant registry for specified variant type.
     *
     * @param variantType class object for variant type
     */
    public VariantRegistry(Class<T> variantType) {
        this.variantType = Objects.requireNonNull(variantType, "Variant type cannot be null");
    } // Constructor: VariantRegistry ()

    // -- Registration Methods --

    /**
     * Registers variant instance with its key.
     * <p>
     * <b>State Impact:</b> Adds or replaces variant mapping. Duplicate keys
     * will overwrite previous registrations without warning.
     *
     * @param variant variant instance to register
     * @throws NullPointerException if variant is null
     * @throws IllegalArgumentException if variant key is null or empty
     */
    public void register(T variant) {
        Objects.requireNonNull(variant, "Variant cannot be null");
        String key = variant.getKey();
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Variant key cannot be null or empty");
        }
        variants.put(key, variant);
    } // register ()

    // -- Retrieval Methods --

    /**
     * Retrieves variant by key.
     *
     * @param key variant key to retrieve
     * @return Optional containing variant if found, empty otherwise
     */
    public Optional<T> get(String key) {
        return Optional.ofNullable(variants.get(key));
    } // get ()

    /**
     * Returns all registered variants.
     * <p>
     * <b>Performance:</b> Returns view of internal collection. Modifications
     * to returned collection will not affect registry.
     *
     * @return collection of all registered variants
     */
    public Collection<T> getAll() {
        return new ArrayList<>(variants.values());
    } // getAll ()

    /**
     * Returns default variant (first registered or highest priority).
     * <p>
     * <b>Selection Logic:</b> Returns variant with highest priority value.
     * If multiple variants have same priority, returns first registered.
     * If no variants registered, returns null.
     *
     * @return default variant or null if registry empty
     */
    public T getDefault() {
        return variants.values().stream()
                .max(Comparator.comparingInt(IVariant::getPriority))
                .orElse(null);
    } // getDefault ()

    /**
     * Returns randomly selected variant.
     * <p>
     * <b>Selection Logic:</b> Uses uniform random distribution across all
     * registered variants. Returns null if registry empty.
     *
     * @return randomly selected variant or null if registry empty
     */
    public T getRandom() {
        if (variants.isEmpty()) {
            return null;
        }
        List<T> variantList = new ArrayList<>(variants.values());
        return variantList.get(new Random().nextInt(variantList.size()));
    } // getRandom ()

    // -- Query Methods --

    /**
     * Checks if variant is registered for specified key.
     *
     * @param key variant key to check
     * @return true if variant exists for key, false otherwise
     */
    public boolean contains(String key) {
        return variants.containsKey(key);
    } // contains ()

    /**
     * Returns number of registered variants.
     *
     * @return variant count
     */
    public int size() {
        return variants.size();
    } // size ()

    /**
     * Checks if registry is empty.
     *
     * @return true if no variants registered, false otherwise
     */
    public boolean isEmpty() {
        return variants.isEmpty();
    } // isEmpty ()

    /**
     * Returns variant type class.
     *
     * @return variant type class
     */
    public Class<T> getVariantType() {
        return variantType;
    } // getVariantType ()

} // Class: VariantRegistry