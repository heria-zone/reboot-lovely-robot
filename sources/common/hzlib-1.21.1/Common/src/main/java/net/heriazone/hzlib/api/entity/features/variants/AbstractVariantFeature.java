package net.heriazone.hzlib.api.entity.features.variants;

import net.heriazone.hzlib.api.entity.variants.VariantRegistry;
import net.heriazone.hzlib.api.entity.variants.interfaces.IVariant;
import net.heriazone.hzlib.framework.entity.data.ResourceMap;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Shared implementation base for single-axis appearance variant features.
 * <p>
 * <b>Architecture:</b> Eliminates ~300 lines of identical logic that were copy-pasted
 * across {@link TextureVariantFeature}, {@link ModelVariantFeature}, and
 * {@link AnimatorVariantFeature}. Each subclass now provides only two things: the
 * registry accessor and the type token — all query and configuration logic lives here.
 * <p>
 * <b>Lane A only.</b> This base is for single-axis features that vary independently.
 * Entities whose appearance dimensions are fully coupled use
 * {@link CompositeAppearanceFeature} (Lane B) instead.
 * <p>
 * <b>Fluent return type:</b> Configuration methods return {@code AbstractVariantFeature<V>},
 * not the concrete subtype. This is intentional — configuration chains end before the
 * variable is used. Subclasses may override with covariant returns if needed.
 *
 * @param <V> the variant interface type ({@link net.heriazone.hzlib.api.entity.variants.interfaces.ITextureVariant},
 *            {@link net.heriazone.hzlib.api.entity.variants.interfaces.IModelVariant},
 *            {@link net.heriazone.hzlib.api.entity.variants.interfaces.IAnimatorVariant})
 */
public abstract class AbstractVariantFeature<V extends IVariant>
        implements IVariantFeature<V> {

    // -- Fields --

    private final Map<String, Set<String>> entityVariants  = new HashMap<>();
    private final Map<String, String>      defaultVariants = new HashMap<>();
    private final ResourceMap<String, ResourceLocation> additionalResources = new ResourceMap<>();

    // -- Abstract hook --

    /**
     * Returns the global registry used to resolve variant keys.
     * Each subclass points to its own registry ({@code TEXTURES}, {@code MODELS}, {@code ANIMATORS}).
     */
    protected abstract VariantRegistry<V> registry();

    // -- IVariantFeature (implemented once, inherited by all three subclasses) --

    @Override
    public Collection<V> getAvailableVariants(String entityKey) {
        Set<String> enabled = entityVariants.get(entityKey);
        if (enabled == null || enabled.isEmpty()) return Collections.emptyList();
        return enabled.stream()
                .map(key -> registry().get(key))
                .filter(Optional::isPresent).map(Optional::get)
                .filter(v -> v.isAvailable(entityKey))
                .collect(Collectors.toList());
    } // getAvailableVariants ()

    @Override
    public V getDefaultVariant(String entityKey) {
        String defaultKey = defaultVariants.get(entityKey);
        if (defaultKey != null) {
            Optional<V> found = registry().get(defaultKey);
            if (found.isPresent() && found.get().isAvailable(entityKey)) return found.get();
        }
        return getAvailableVariants(entityKey).stream()
                .max(Comparator.comparingInt(IVariant::getPriority))
                .orElse(null);
    } // getDefaultVariant ()

    @Override
    public V getRandomVariant(String entityKey) {
        List<V> available = new ArrayList<>(getAvailableVariants(entityKey));
        if (available.isEmpty()) return null;
        return available.get(new Random().nextInt(available.size()));
    } // getRandomVariant ()

    @Override
    public boolean hasVariant(String entityKey, String variantKey) {
        Set<String> enabled = entityVariants.get(entityKey);
        if (enabled == null || !enabled.contains(variantKey)) return false;
        return registry().get(variantKey).map(v -> v.isAvailable(entityKey)).orElse(false);
    } // hasVariant ()

    @Override
    public int getVariantCount(String entityKey) {
        return getAvailableVariants(entityKey).size();
    } // getVariantCount ()

    // -- Configuration --

    /** Enables a single variant for the given entity key. */
    public AbstractVariantFeature<V> withVariant(String entityKey, String variantKey) {
        if (entityKey != null && variantKey != null)
            entityVariants.computeIfAbsent(entityKey, k -> new HashSet<>()).add(variantKey);
        return this;
    } // withVariant ()

    /** Enables multiple variants for the given entity key in one call. */
    public AbstractVariantFeature<V> withVariants(String entityKey, String... variantKeys) {
        if (entityKey != null && variantKeys != null)
            Collections.addAll(entityVariants.computeIfAbsent(entityKey, k -> new HashSet<>()), variantKeys);
        return this;
    } // withVariants ()

    /** Sets the preferred default variant for the given entity key. */
    public AbstractVariantFeature<V> withDefault(String entityKey, String variantKey) {
        if (entityKey != null && variantKey != null) defaultVariants.put(entityKey, variantKey);
        return this;
    } // withDefault ()

    // -- Legacy resource map (used by withTexture / withModel / withAnimator aliases) --

    /** Returns the legacy resource map used by domain-specific convenience aliases. */
    protected ResourceMap<String, ResourceLocation> additionalResources() {
        return additionalResources;
    } // additionalResources ()

} // Class: AbstractVariantFeature
