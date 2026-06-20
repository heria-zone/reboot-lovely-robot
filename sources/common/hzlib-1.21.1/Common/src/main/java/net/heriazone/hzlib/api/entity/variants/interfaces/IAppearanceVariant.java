package net.heriazone.hzlib.api.entity.variants.interfaces;

import net.heriazone.hzlib.api.entity.variants.VariantRegistries;
import net.minecraft.resources.ResourceLocation;

/**
 * <p>Composite variant that bundles texture, model, and animator into one addressable unit.<p>
 * <p>
 * <b>Why composite:</b> Entities like Gourdragora require all three dimensions to change
 * together — picking them independently risks mismatched combinations (mini texture with
 * default animator). An {@link IAppearanceVariant} is the single key that guarantees
 * coherence across all three.
 * <p>
 * <b>Contract:</b> {@link #getResource} is intentionally a no-op — this variant has no
 * single representative resource. Use the typed accessors instead.
 */
public interface IAppearanceVariant extends IVariant {

    // -- Composite Accessors --

    /** Returns the texture variant key this appearance resolves to. */
    String getTextureKey();

    /** Returns the model variant key this appearance resolves to. */
    String getModelKey();

    /** Returns the animator variant key this appearance resolves to. */
    String getAnimatorKey();

    // -- Resource Resolution --

    /**
     * Resolves the texture {@link ResourceLocation} via {@code VariantRegistries.TEXTURES}.
     * Returns null if the texture key is not registered.
     */
    default ResourceLocation getTextureResource() {
        return VariantRegistries.TEXTURES.get(getTextureKey())
                .map(v -> v.getResource(getTextureKey()))
                .orElse(null);
    } // getTextureResource ()

    /**
     * Resolves the model {@link ResourceLocation} via {@code VariantRegistries.MODELS}.
     * Returns null if the model key is not registered.
     */
    default ResourceLocation getModelResource() {
        return VariantRegistries.MODELS.get(getModelKey())
                .map(v -> v.getResource(getModelKey()))
                .orElse(null);
    } // getModelResource ()

    /**
     * Resolves the animator {@link ResourceLocation} via {@code VariantRegistries.ANIMATORS}.
     * Returns null if the animator key is not registered.
     */
    default ResourceLocation getAnimatorResource() {
        return VariantRegistries.ANIMATORS.get(getAnimatorKey())
                .map(v -> v.getResource(getAnimatorKey()))
                .orElse(null);
    } // getAnimatorResource ()

    // -- IVariant no-op --

    /**
     * Not applicable for composite variants — returns null.
     * Use the typed resource methods instead.
     */
    @Override
    default ResourceLocation getResource(String key) {
        return null;
    } // getResource ()

} // Interface: IAppearanceVariant