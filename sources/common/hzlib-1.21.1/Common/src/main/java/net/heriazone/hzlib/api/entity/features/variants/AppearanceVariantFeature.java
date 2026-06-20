package net.heriazone.hzlib.api.entity.features.variants;

import net.heriazone.hzlib.api.entity.variants.VariantRegistries;
import net.heriazone.hzlib.api.entity.variants.interfaces.IAppearanceVariant;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Manages pools of {@link IAppearanceVariant} per entity key.<p>
 * <p>
 * <b>Purpose:</b> Replaces independent texture/model/animator selection with a single
 * coordinated pick — guaranteeing that all three dimensions are always coherent.
 * Entities like Gourdragora declare their full appearance pool here; at spawn time
 * a single {@link #getRandomVariant} call returns a complete, matched set.
 * <p>
 * <b>Pattern:</b> Follows the same structure as {@link TextureVariantFeature},
 * {@link ModelVariantFeature}, and {@link AnimatorVariantFeature} for consistency.
 */
public class AppearanceVariantFeature implements IVariantFeature<IAppearanceVariant> {

    // -- Fields --

    private final Map<String, Set<String>> entityVariants;
    private final Map<String, String> defaultVariants;

    // -- Constructor --

    /**
     * Creates an empty entity appearance feature.
     * Configure via fluent API before attaching to an entity type.
     */
    public AppearanceVariantFeature() {
        this.entityVariants = new HashMap<>();
        this.defaultVariants = new HashMap<>();
    } // Constructor: AppearanceVariantFeature ()

    // -- IVariantFeature --

    @Override
    public Class<IAppearanceVariant> getVariantType() {
        return IAppearanceVariant.class;
    } // getVariantType ()

    @Override
    public Collection<IAppearanceVariant> getAvailableVariants(String entityKey) {
        Set<String> enabled = entityVariants.get(entityKey);
        if (enabled == null || enabled.isEmpty()) return Collections.emptyList();

        return enabled.stream()
                .map(key -> VariantRegistries.APPEARANCES.get(key))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(v -> v.isAvailable(entityKey))
                .collect(Collectors.toList());
    } // getAvailableVariants ()

    @Override
    public IAppearanceVariant getDefaultVariant(String entityKey) {
        String defaultKey = defaultVariants.get(entityKey);
        if (defaultKey != null) {
            Optional<IAppearanceVariant> found = VariantRegistries.APPEARANCES.get(defaultKey);
            if (found.isPresent() && found.get().isAvailable(entityKey)) return found.get();
        }

        return getAvailableVariants(entityKey).stream()
                .max(Comparator.comparingInt(IAppearanceVariant::getPriority))
                .orElse(null);
    } // getDefaultVariant ()

    @Override
    public IAppearanceVariant getRandomVariant(String entityKey) {
        List<IAppearanceVariant> available = new ArrayList<>(getAvailableVariants(entityKey));
        if (available.isEmpty()) return null;
        return available.get(new Random().nextInt(available.size()));
    } // getRandomVariant ()

    @Override
    public boolean hasVariant(String entityKey, String variantKey) {
        Set<String> enabled = entityVariants.get(entityKey);
        if (enabled == null || !enabled.contains(variantKey)) return false;
        return VariantRegistries.APPEARANCES.get(variantKey)
                .map(v -> v.isAvailable(entityKey))
                .orElse(false);
    } // hasVariant ()

    @Override
    public int getVariantCount(String entityKey) {
        return getAvailableVariants(entityKey).size();
    } // getVariantCount ()

    // -- Configuration --

    /**
     * Enables a single entity appearance for the given entity key.
     *
     * @param entityKey  entity identifier
     * @param variantKey entity appearance key to enable
     * @return this feature for chaining
     */
    public AppearanceVariantFeature withVariant(String entityKey, String variantKey) {
        if (entityKey != null && variantKey != null)
            entityVariants.computeIfAbsent(entityKey, k -> new HashSet<>()).add(variantKey);
        return this;
    } // withVariant ()

    /**
     * Enables multiple entity appearances for the given entity key.
     *
     * @param entityKey   entity identifier
     * @param variantKeys entity appearance keys to enable
     * @return this feature for chaining
     */
    public AppearanceVariantFeature withVariants(String entityKey, String... variantKeys) {
        if (entityKey != null && variantKeys != null)
            Collections.addAll(entityVariants.computeIfAbsent(entityKey, k -> new HashSet<>()), variantKeys);
        return this;
    } // withVariants ()

    /**
     * Sets the default entity appearance for the given entity key.
     * <p>
     * The variant should also be registered via {@link #withVariant} to be selectable.
     *
     * @param entityKey  entity identifier
     * @param variantKey entity appearance key to use as default
     * @return this feature for chaining
     */
    public AppearanceVariantFeature withDefault(String entityKey, String variantKey) {
        if (entityKey != null && variantKey != null)
            defaultVariants.put(entityKey, variantKey);
        return this;
    } // withDefault ()

} // Class: AppearanceVariantFeature