package net.heriazone.hzlib.api.entity.features.variants;

import net.heriazone.hzlib.api.entity.variants.VariantRegistry;
import net.heriazone.hzlib.api.entity.variants.VariantRegistries;
import net.heriazone.hzlib.api.entity.variants.interfaces.ITextureVariant;
import net.minecraft.resources.ResourceLocation;

/**
 * Lane A feature that manages texture variant selection for an entity type.
 * <p>
 * <b>Architecture:</b> Extends {@link AbstractVariantFeature} — all query and
 * configuration logic lives in the base. Only the registry hook and type token
 * are specific to this class.
 * <p>
 * Use {@link #withTexture} / {@link #getTexture} for legacy resource-map entries
 * that pre-date the typed variant registry.
 */
public class TextureVariantFeature extends AbstractVariantFeature<ITextureVariant> {

    // -- AbstractVariantFeature --

    @Override
    protected VariantRegistry<ITextureVariant> registry() {
        return VariantRegistries.TEXTURES;
    } // registry ()

    @Override
    public Class<ITextureVariant> getVariantType() {
        return ITextureVariant.class;
    } // getVariantType ()

    // -- Covariant fluent overrides --
    // See ModelVariantFeature for rationale.

    @Override public TextureVariantFeature withVariant(String entityKey, String variantKey) {
        super.withVariant(entityKey, variantKey); return this;
    }
    @Override public TextureVariantFeature withVariants(String entityKey, String... variantKeys) {
        super.withVariants(entityKey, variantKeys); return this;
    }
    @Override public TextureVariantFeature withDefault(String entityKey, String variantKey) {
        super.withDefault(entityKey, variantKey); return this;
    }

    // -- Legacy resource aliases --

    /**
     * Registers a texture directly by variant name, bypassing the typed registry.
     * <p>
     * <i>Note:</i> Legacy path for textures not yet migrated to {@link ITextureVariant}.
     * Prefer registry-based variants for new implementations.
     */
    public TextureVariantFeature withTexture(String variant, ResourceLocation texture) {
        if (variant != null && texture != null) additionalResources().put(variant, texture);
        return this;
    } // withTexture ()

    /** Returns the legacy texture resource for {@code variant}, or {@code null} if absent. */
    public ResourceLocation getTexture(String variant) {
        return additionalResources().get(variant);
    } // getTexture ()

    /** Returns {@code true} if a legacy texture or registry entry exists for {@code variant}. */
    public boolean hasTexture(String variant) {
        return additionalResources().has(variant) || VariantRegistries.TEXTURES.contains(variant);
    } // hasTexture ()

    /** Returns a random legacy texture resource, or {@code null} if none are registered. */
    public ResourceLocation getRandomTexture() {
        return additionalResources().getRandom();
    } // getRandomTexture ()

    /**
     * Convenience batch method — adds sequentially-named {@code "body_N"} legacy texture entries.
     * <p>
     * <i>Note:</i> Maintained for compatibility with existing family declarations.
     */
    public TextureVariantFeature withBodyVariants(String basePath, String... variants) {
        if (basePath == null || variants == null) return this;
        for (int i = 0; i < variants.length; i++) {
            if (variants[i] != null)
                additionalResources().put("body_" + (i + 1),
                        ResourceLocation.parse(basePath + "/" + variants[i]));
        }
        return this;
    } // withBodyVariants ()

} // Class: TextureVariantFeature
