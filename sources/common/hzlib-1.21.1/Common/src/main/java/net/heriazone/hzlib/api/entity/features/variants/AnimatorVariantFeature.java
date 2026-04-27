package net.heriazone.hzlib.api.entity.features.variants;

import net.heriazone.hzlib.api.entity.variants.VariantRegistries;
import net.heriazone.hzlib.api.entity.variants.interfaces.IAnimatorVariant;
import net.heriazone.hzlib.framework.entity.data.ResourceMap;
import net.minecraft.resources.ResourceLocation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Enhanced animator variant feature with registry integration and entity-specific filtering.<p>
 * <p>
 * <b>Architecture:</b> Bridges entity-specific animator configuration with global variant registry,
 * enabling entities to selectively use subsets of registered variants while maintaining
 * consistent resource resolution patterns.
 * <p>
 * <b>Design Decision:</b> Combines local entity configuration (which variants are enabled)
 * with global variant registry (how variants resolve resources), providing flexibility
 * without duplicating variant definitions across entities.
 * <p>
 * <b>Enhanced Features:</b> Supports default variant specification, random selection,
 * and availability filtering based on entity-specific requirements.
 */
public class AnimatorVariantFeature implements IVariantFeature<IAnimatorVariant> {

    // -- Fields --

    private final Map<String, Set<String>> entityVariants;
    private final Map<String, String> defaultVariants;
    private final ResourceMap<String, ResourceLocation> additionalAnimators;

    // -- Constructor --

    /**
     * Creates empty animator variant feature with no configured variants.
     * <p>
     * <b>State Impact:</b> Feature starts empty and requires configuration through
     * fluent API methods before providing useful functionality.
     */
    public AnimatorVariantFeature() {
        this.entityVariants = new HashMap<>();
        this.defaultVariants = new HashMap<>();
        this.additionalAnimators = new ResourceMap<>();
    } // Constructor: AnimatorVariantFeature ()

    // -- IVariantFeature Implementation --

    @Override
    public Class<IAnimatorVariant> getVariantType() {
        return IAnimatorVariant.class;
    } // getVariantType ()

    @Override
    public Collection<IAnimatorVariant> getAvailableVariants(String entityKey) {
        Set<String> enabledVariants = entityVariants.get(entityKey);
        if (enabledVariants == null || enabledVariants.isEmpty()) {
            return Collections.emptyList();
        }

        return enabledVariants.stream()
                .map(variantKey -> VariantRegistries.ANIMATORS.get(variantKey))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(variant -> variant.isAvailable(entityKey))
                .collect(Collectors.toList());
    } // getAvailableVariants ()

    @Override
    public IAnimatorVariant getDefaultVariant(String entityKey) {
        String defaultKey = defaultVariants.get(entityKey);
        if (defaultKey != null) {
            Optional<IAnimatorVariant> defaultVariant = VariantRegistries.ANIMATORS.get(defaultKey);
            if (defaultVariant.isPresent() && defaultVariant.get().isAvailable(entityKey)) {
                return defaultVariant.get();
            }
        }

        // Fallback to highest priority available variant
        return getAvailableVariants(entityKey).stream()
                .max(Comparator.comparingInt(IAnimatorVariant::getPriority))
                .orElse(null);
    } // getDefaultVariant ()

    @Override
    public IAnimatorVariant getRandomVariant(String entityKey) {
        Collection<IAnimatorVariant> available = getAvailableVariants(entityKey);
        if (available.isEmpty()) {
            return null;
        }

        List<IAnimatorVariant> variantList = new ArrayList<>(available);
        return variantList.get(new Random().nextInt(variantList.size()));
    } // getRandomVariant ()

    @Override
    public boolean hasVariant(String entityKey, String variantKey) {
        Set<String> enabledVariants = entityVariants.get(entityKey);
        if (enabledVariants == null || !enabledVariants.contains(variantKey)) {
            return false;
        }

        Optional<IAnimatorVariant> variant = VariantRegistries.ANIMATORS.get(variantKey);
        return variant.isPresent() && variant.get().isAvailable(entityKey);
    } // hasVariant ()

    @Override
    public int getVariantCount(String entityKey) {
        return getAvailableVariants(entityKey).size();
    } // getVariantCount ()

    // -- Configuration Methods --

    /**
     * Enables specified variant for entity key.
     * <p>
     * <b>State Impact:</b> Adds variant to entity's enabled variant set.
     * Variant must be registered in global registry to be usable.
     * <p>
     * <b>Fluent API:</b> Returns this instance for method chaining.
     *
     * @param entityKey entity identifier
     * @param variantKey variant key to enable
     * @return this feature instance for chaining
     */
    public AnimatorVariantFeature withVariant(String entityKey, String variantKey) {
        if (entityKey != null && variantKey != null) {
            entityVariants.computeIfAbsent(entityKey, k -> new HashSet<>()).add(variantKey);
        }
        return this;
    } // withVariant ()

    /**
     * Sets default variant for entity key.
     * <p>
     * <b>State Impact:</b> Configures preferred variant for default selection.
     * Variant should also be enabled via withVariant() to be available.
     * <p>
     * <b>Fluent API:</b> Returns this instance for method chaining.
     *
     * @param entityKey entity identifier
     * @param variantKey variant key to set as default
     * @return this feature instance for chaining
     */
    public AnimatorVariantFeature withDefault(String entityKey, String variantKey) {
        if (entityKey != null && variantKey != null) {
            defaultVariants.put(entityKey, variantKey);
        }
        return this;
    } // withDefault ()

    /**
     * Enables multiple variants for entity key.
     * <p>
     * <b>Convenience Method:</b> Equivalent to calling withVariant() for each key.
     * <p>
     * <b>Fluent API:</b> Returns this instance for method chaining.
     *
     * @param entityKey entity identifier
     * @param variantKeys variant keys to enable
     * @return this feature instance for chaining
     */
    public AnimatorVariantFeature withVariants(String entityKey, String... variantKeys) {
        if (entityKey != null && variantKeys != null) {
            Set<String> variants = entityVariants.computeIfAbsent(entityKey, k -> new HashSet<>());
            Collections.addAll(variants, variantKeys);
        }
        return this;
    } // withVariants ()

    // -- Legacy Compatibility Methods --

    /**
     * Associates animator identifier with specified variant name.
     * <p>
     * <b>Legacy Support:</b> Maintains compatibility with direct animator registration.
     * Consider using registry-based variants for new implementations.
     * <p>
     * <b>Fluent API:</b> Returns this instance for method chaining.
     *
     * @param variant variant name (e.g., "body_1", "seasonal_winter")
     * @param animator animator identifier to associate with variant
     * @return this feature instance for chaining
     */
    public AnimatorVariantFeature withAnimator(String variant, ResourceLocation animator) {
        if (variant != null && animator != null) {
            this.additionalAnimators.put(variant, animator);
        }
        return this;
    } // withAnimator ()

    /**
     * Convenience method for adding multiple body variants with sequential naming.
     * <p>
     * <b>Legacy Support:</b> Maintains compatibility with existing body variant patterns.
     * <p>
     * <b>Naming Pattern:</b> Creates variants named "body_1", "body_2", etc. using provided base path.
     * <p>
     * <b>Fluent API:</b> Returns this instance for method chaining.
     *
     * @param basePath base path for animator identifiers (e.g., "entity/robot")
     * @param variants variant names to append to base path
     * @return this feature instance for chaining
     */
    public AnimatorVariantFeature withBodyVariants(String basePath, String... variants) {
        if (basePath == null || variants == null) {
            return this;
        }

        for (int i = 0; i < variants.length; i++) {
            if (variants[i] != null) {
                String variantKey = "body_" + (i + 1);
                String animatorPath = basePath + "/" + variants[i];
                ResourceLocation animatorId = ResourceLocation.parse(animatorPath);
                this.additionalAnimators.put(variantKey, animatorId);
            }
        }

        return this;
    } // withBodyVariants ()

    // -- Legacy Animator Queries --

    /**
     * Returns animator identifier for specified variant.
     * <p>
     * <b>Legacy Support:</b> Checks additional animators first, then registry variants.
     * <p>
     * <b>Fallback Behavior:</b> Returns null if variant not configured.
     * Callers should handle null or provide default animator as fallback.
     *
     * @param variant variant name to retrieve
     * @return animator identifier for variant, or null if not configured
     */
    public ResourceLocation getAnimator(String variant) {
        // Check additional animators first (legacy support)
        ResourceLocation additionalAnimator = this.additionalAnimators.get(variant);
        if (additionalAnimator != null) {
            return additionalAnimator;
        }

        // Check registry variants (new system)
        Optional<IAnimatorVariant> registryVariant = VariantRegistries.ANIMATORS.get(variant);
        if (registryVariant.isPresent()) {
            // Note: This requires an entity key, but legacy method doesn't provide one
            // Return null to maintain compatibility - callers should use new methods
            return null;
        }

        return null;
    } // getAnimator ()

    /**
     * Checks if animator is configured for specified variant.
     *
     * @param variant variant name to check
     * @return true if animator is configured for variant, false otherwise
     */
    public boolean hasAnimator(String variant) {
        return this.additionalAnimators.has(variant) || VariantRegistries.ANIMATORS.contains(variant);
    } // hasAnimator ()

    /**
     * Returns randomly selected animator identifier from configured variants.
     * <p>
     * <b>Legacy Support:</b> Selects from additional animators only.
     * <p>
     * <b>Performance:</b> O(1) after initial cache build due to ResourceMap caching.
     *
     * @return randomly selected animator identifier, or null if no variants configured
     */
    public ResourceLocation getRandomAnimator() {
        return this.additionalAnimators.getRandom();
    } // getRandomAnimator ()

} // Class: AnimatorVariantFeature