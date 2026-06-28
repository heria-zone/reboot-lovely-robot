package net.heriazone.hzlib.api.entity.features.variants;

import net.heriazone.hzlib.api.entity.variants.VariantRegistry;
import net.heriazone.hzlib.api.entity.variants.VariantRegistries;
import net.heriazone.hzlib.api.entity.variants.interfaces.IModelVariant;
import net.minecraft.resources.ResourceLocation;

/**
 * Lane A feature that manages model variant selection for an entity type.
 * <p>
 * <b>Architecture:</b> Extends {@link AbstractVariantFeature} — all query and
 * configuration logic lives in the base. Only the registry hook and type token
 * are specific to this class.
 * <p>
 * Use {@link #withModel} / {@link #getModel} for legacy resource-map entries
 * that pre-date the typed variant registry.
 */
public class ModelVariantFeature extends AbstractVariantFeature<IModelVariant> {

    // -- AbstractVariantFeature --

    @Override
    protected VariantRegistry<IModelVariant> registry() {
        return VariantRegistries.MODELS;
    } // registry ()

    @Override
    public Class<IModelVariant> getVariantType() {
        return IModelVariant.class;
    } // getVariantType ()

    // -- Covariant fluent overrides --
    // AbstractVariantFeature methods return AbstractVariantFeature<V>. These overrides
    // restore the concrete return type so withFeature(ModelVariantFeature.class, new ModelVariantFeature().withVariants(...))
    // compiles correctly — the value must match the Class<F> key exactly.

    @Override public ModelVariantFeature withVariant(String entityKey, String variantKey) {
        super.withVariant(entityKey, variantKey); return this;
    }
    @Override public ModelVariantFeature withVariants(String entityKey, String... variantKeys) {
        super.withVariants(entityKey, variantKeys); return this;
    }
    @Override public ModelVariantFeature withDefault(String entityKey, String variantKey) {
        super.withDefault(entityKey, variantKey); return this;
    }

    // -- Legacy resource aliases --

    /**
     * Registers a model directly by variant name, bypassing the typed registry.
     * <p>
     * <i>Note:</i> Legacy path for models not yet migrated to {@link IModelVariant}.
     * Prefer registry-based variants for new implementations.
     */
    public ModelVariantFeature withModel(String variant, ResourceLocation model) {
        if (variant != null && model != null) additionalResources().put(variant, model);
        return this;
    } // withModel ()

    /** Returns the legacy model resource for {@code variant}, or {@code null} if absent. */
    public ResourceLocation getModel(String variant) {
        return additionalResources().get(variant);
    } // getModel ()

    /** Returns {@code true} if a legacy model or registry entry exists for {@code variant}. */
    public boolean hasModel(String variant) {
        return additionalResources().has(variant) || VariantRegistries.MODELS.contains(variant);
    } // hasModel ()

    /** Returns a random legacy model resource, or {@code null} if none are registered. */
    public ResourceLocation getRandomModel() {
        return additionalResources().getRandom();
    } // getRandomModel ()

    /**
     * Convenience batch method — adds sequentially-named {@code "body_N"} legacy model entries.
     * <p>
     * <i>Note:</i> Maintained for compatibility with existing family declarations.
     */
    public ModelVariantFeature withBodyVariants(String basePath, String... variants) {
        if (basePath == null || variants == null) return this;
        for (int i = 0; i < variants.length; i++) {
            if (variants[i] != null)
                additionalResources().put("body_" + (i + 1),
                        ResourceLocation.parse(basePath + "/" + variants[i]));
        }
        return this;
    } // withBodyVariants ()

} // Class: ModelVariantFeature
