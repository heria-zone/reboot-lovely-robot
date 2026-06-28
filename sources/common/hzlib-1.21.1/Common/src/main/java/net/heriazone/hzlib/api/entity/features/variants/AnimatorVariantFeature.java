package net.heriazone.hzlib.api.entity.features.variants;

import net.heriazone.hzlib.api.entity.variants.VariantRegistry;
import net.heriazone.hzlib.api.entity.variants.VariantRegistries;
import net.heriazone.hzlib.api.entity.variants.interfaces.IAnimatorVariant;
import net.minecraft.resources.ResourceLocation;

/**
 * Lane A feature that manages animator variant selection for an entity type.
 * <p>
 * <b>Architecture:</b> Extends {@link AbstractVariantFeature} — all query and
 * configuration logic lives in the base. Only the registry hook and type token
 * are specific to this class.
 * <p>
 * Use {@link #withAnimator} / {@link #getAnimator} for legacy resource-map entries
 * that pre-date the typed variant registry.
 */
public class AnimatorVariantFeature extends AbstractVariantFeature<IAnimatorVariant> {

    // -- AbstractVariantFeature --

    @Override
    protected VariantRegistry<IAnimatorVariant> registry() {
        return VariantRegistries.ANIMATORS;
    } // registry ()

    @Override
    public Class<IAnimatorVariant> getVariantType() {
        return IAnimatorVariant.class;
    } // getVariantType ()

    // -- Covariant fluent overrides --
    // See ModelVariantFeature for rationale.

    @Override public AnimatorVariantFeature withVariant(String entityKey, String variantKey) {
        super.withVariant(entityKey, variantKey); return this;
    }
    @Override public AnimatorVariantFeature withVariants(String entityKey, String... variantKeys) {
        super.withVariants(entityKey, variantKeys); return this;
    }
    @Override public AnimatorVariantFeature withDefault(String entityKey, String variantKey) {
        super.withDefault(entityKey, variantKey); return this;
    }

    // -- Legacy resource aliases --

    /**
     * Registers an animator directly by variant name, bypassing the typed registry.
     * <p>
     * <i>Note:</i> Legacy path for animators not yet migrated to {@link IAnimatorVariant}.
     * Prefer registry-based variants for new implementations.
     */
    public AnimatorVariantFeature withAnimator(String variant, ResourceLocation animator) {
        if (variant != null && animator != null) additionalResources().put(variant, animator);
        return this;
    } // withAnimator ()

    /** Returns the legacy animator resource for {@code variant}, or {@code null} if absent. */
    public ResourceLocation getAnimator(String variant) {
        return additionalResources().get(variant);
    } // getAnimator ()

    /** Returns {@code true} if a legacy animator or registry entry exists for {@code variant}. */
    public boolean hasAnimator(String variant) {
        return additionalResources().has(variant) || VariantRegistries.ANIMATORS.contains(variant);
    } // hasAnimator ()

    /** Returns a random legacy animator resource, or {@code null} if none are registered. */
    public ResourceLocation getRandomAnimator() {
        return additionalResources().getRandom();
    } // getRandomAnimator ()

    /**
     * Convenience batch method — adds sequentially-named {@code "body_N"} legacy animator entries.
     * <p>
     * <i>Note:</i> Maintained for compatibility with existing family declarations.
     */
    public AnimatorVariantFeature withBodyVariants(String basePath, String... variants) {
        if (basePath == null || variants == null) return this;
        for (int i = 0; i < variants.length; i++) {
            if (variants[i] != null)
                additionalResources().put("body_" + (i + 1),
                        ResourceLocation.parse(basePath + "/" + variants[i]));
        }
        return this;
    } // withBodyVariants ()

} // Class: AnimatorVariantFeature
