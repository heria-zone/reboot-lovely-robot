package net.heriazone.hzlib.api.entity.features.variants;

import net.heriazone.hzlib.api.entity.variants.interfaces.IVariant;
import java.util.Collection;

/**
 * <p>Feature interface for managing entity variant collections.<p>
 * <p>
 * <b>Architecture:</b> Provides type-safe variant management within the feature system,
 * enabling entities to have configurable variant collections without hardcoding
 * specific variant types. Supports dynamic variant registration and selection.
 * <p>
 * <b>Design Decision:</b> Generic type parameter ensures compile-time type safety
 * while allowing different variant types (texture, model, animator) to use the
 * same feature interface pattern.
 * <p>
 * <b>Integration:</b> Works with VariantRegistry system to provide entity-specific
 * variant filtering and selection from global variant registrations.
 *
 * @param <T> variant type extending IVariant
 */
public interface IVariantFeature<T extends IVariant> {

    // -- Variant Type Information --

    /**
     * Returns the variant type class managed by this feature.
     * <p>
     * <b>Usage:</b> Enables type-safe variant operations and registry lookups
     * without requiring explicit type casting.
     *
     * @return variant type class
     */
    Class<T> getVariantType();

    // -- Variant Collection Management --

    /**
     * Returns all variants available for the specified entity key.
     * <p>
     * <b>Filtering Logic:</b> Should return only variants that are both registered
     * in this feature and available for the given entity key.
     *
     * @param entityKey entity identifier for variant filtering
     * @return collection of available variants for entity
     */
    Collection<T> getAvailableVariants(String entityKey);

    /**
     * Returns the default variant for the specified entity key.
     * <p>
     * <b>Selection Logic:</b> Should return the most appropriate default variant
     * based on priority, availability, or feature-specific criteria.
     *
     * @param entityKey entity identifier for default selection
     * @return default variant for entity, or null if none available
     */
    T getDefaultVariant(String entityKey);

    /**
     * Returns a randomly selected variant for the specified entity key.
     * <p>
     * <b>Selection Logic:</b> Should select from available variants using
     * uniform or weighted random distribution.
     *
     * @param entityKey entity identifier for random selection
     * @return randomly selected variant for entity, or null if none available
     */
    T getRandomVariant(String entityKey);

    // -- Variant Queries --

    /**
     * Checks if the specified variant is available for the entity key.
     * <p>
     * <b>Availability Check:</b> Should verify both feature registration and
     * entity-specific availability conditions.
     *
     * @param entityKey entity identifier for availability check
     * @param variantKey variant key to check
     * @return true if variant is available for entity, false otherwise
     */
    boolean hasVariant(String entityKey, String variantKey);

    /**
     * Returns the number of variants available for the specified entity key.
     * <p>
     * <b>Count Logic:</b> Should count only variants that pass availability
     * checks for the given entity key.
     *
     * @param entityKey entity identifier for variant counting
     * @return number of available variants for entity
     */
    int getVariantCount(String entityKey);

} // Interface: IVariantFeature