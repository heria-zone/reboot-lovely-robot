package net.heriazone.hzlib.api.entity.features.variants;

import net.heriazone.hzlib.api.entity.variants.VariantRegistries;
import net.heriazone.hzlib.api.entity.variants.interfaces.ICompositeAppearance;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Lane B appearance feature — bundles texture, model, animator, and optional size
 * configuration into named, explicitly-declared appearance entries.
 * <p>
 * <b>When to use (Lane B):</b> When all appearance dimensions are fully coupled —
 * no combination of texture × model × animator is valid unless explicitly declared.
 * Gourdragora (size × color tightly coupled) is the canonical case.
 * <p>
 * <b>When NOT to use:</b> When dimensions vary independently (robots — 16 colors ×
 * 2 model states). Use {@link TextureVariantFeature} + {@link ModelVariantFeature} +
 * {@link AnimatorVariantFeature} (Lane A) instead.
 * <p>
 * <b>Mutual exclusion:</b> A family declares either this feature OR the Lane A features.
 * {@code NativeEntity.initializeRandomVariants} checks this feature first and skips Lane A
 * entirely when it is present. Declaring both logs a warning and Lane B wins.
 * <p>
 * <b>Renamed from</b> {@code AppearanceVariantFeature}. All method signatures are identical
 * — callers update the type name only.
 */
public class CompositeAppearanceFeature implements IVariantFeature<ICompositeAppearance> {

    // -- Fields --

    private final Map<String, Set<String>> entityVariants  = new HashMap<>();
    private final Map<String, String>      defaultVariants = new HashMap<>();

    // -- Constructor --

    /** Creates an empty feature. Configure via fluent API before attaching to a family. */
    public CompositeAppearanceFeature() {} // Constructor: CompositeAppearanceFeature ()

    // -- IVariantFeature --

    @Override
    public Class<ICompositeAppearance> getVariantType() {
        return ICompositeAppearance.class;
    } // getVariantType ()

    @Override
    public Collection<ICompositeAppearance> getAvailableVariants(String entityKey) {
        Set<String> enabled = entityVariants.get(entityKey);
        if (enabled == null || enabled.isEmpty()) return Collections.emptyList();
        return enabled.stream()
                .map(key -> VariantRegistries.APPEARANCES.get(key))
                .filter(Optional::isPresent).map(Optional::get)
                .filter(v -> v.isAvailable(entityKey))
                .collect(Collectors.toList());
    } // getAvailableVariants ()

    @Override
    public ICompositeAppearance getDefaultVariant(String entityKey) {
        String defaultKey = defaultVariants.get(entityKey);
        if (defaultKey != null) {
            Optional<ICompositeAppearance> found = VariantRegistries.APPEARANCES.get(defaultKey);
            if (found.isPresent() && found.get().isAvailable(entityKey)) return found.get();
        }
        return getAvailableVariants(entityKey).stream()
                .max(Comparator.comparingInt(ICompositeAppearance::getPriority))
                .orElse(null);
    } // getDefaultVariant ()

    @Override
    public ICompositeAppearance getRandomVariant(String entityKey) {
        List<ICompositeAppearance> available = new ArrayList<>(getAvailableVariants(entityKey));
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

    // -- Explicit key lookup (Gourdragora weighted-spawn path) --

    /**
     * Returns the appearance registered under {@code variantKey} for {@code entityKey},
     * or {@code null} if the key is not registered or not available.
     * <p>
     * <b>Use case:</b> After {@link net.heriazone.hzlib.api.entity.features.SizeVariantFeature#pickWeightedRandom()}
     * determines the desired size, this method retrieves the matching composite by its
     * derived key (e.g. {@code "gourdragora_golden_big"}) — no string concatenation at
     * the call site once the family is wired correctly.
     */
    @Nullable
    public ICompositeAppearance getVariant(String entityKey, String variantKey) {
        if (!hasVariant(entityKey, variantKey)) return null;
        return VariantRegistries.APPEARANCES.get(variantKey).orElse(null);
    } // getVariant ()

    // -- Configuration --

    /** Enables a single composite appearance for the given entity key. */
    public CompositeAppearanceFeature withVariant(String entityKey, String variantKey) {
        if (entityKey != null && variantKey != null)
            entityVariants.computeIfAbsent(entityKey, k -> new HashSet<>()).add(variantKey);
        return this;
    } // withVariant ()

    /** Enables multiple composite appearances for the given entity key. */
    public CompositeAppearanceFeature withVariants(String entityKey, String... variantKeys) {
        if (entityKey != null && variantKeys != null)
            Collections.addAll(entityVariants.computeIfAbsent(entityKey, k -> new HashSet<>()), variantKeys);
        return this;
    } // withVariants ()

    /** Sets the default composite appearance for the given entity key. */
    public CompositeAppearanceFeature withDefault(String entityKey, String variantKey) {
        if (entityKey != null && variantKey != null) defaultVariants.put(entityKey, variantKey);
        return this;
    } // withDefault ()

} // Class: CompositeAppearanceFeature
