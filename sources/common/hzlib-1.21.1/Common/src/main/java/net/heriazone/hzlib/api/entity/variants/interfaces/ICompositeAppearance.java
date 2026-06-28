package net.heriazone.hzlib.api.entity.variants.interfaces;

import net.heriazone.hzlib.api.entity.features.SizeVariantFeature;
import net.heriazone.hzlib.api.entity.variants.VariantRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * Composite variant that bundles texture, model, and animator — plus an optional
 * size configuration — into one fully-coupled, named appearance unit.
 * <p>
 * <b>Architecture (Lane B):</b> Used by entities where appearance dimensions are
 * inseparable. {@code gourdragora_golden_big} is one specific thing — one texture,
 * one model, one scale. Independent-axis features ({@link ITextureVariant} etc.)
 * cannot express this without risking mismatched combinations.
 * <p>
 * <b>{@link #getSizeConfig()}:</b> Present when this appearance corresponds to a
 * specific physical size (Gourdragora big/mini/default). The entity calls
 * {@code sizeConfig.applyTo(entity)} at spawn and on NBT load — no hand-matched
 * string keys needed. Returns empty for flat-palette appearances with no size coupling.
 * <p>
 * <b>Renamed from</b> {@code IAppearanceVariant} — "Composite" reflects the pattern;
 * "Appearance" reflects the tier. The old name confused the Variant tier above.
 */
public interface ICompositeAppearance extends IVariant {

    // -- Composite accessors --

    /** Returns the texture variant key this appearance resolves to. */
    String getTextureKey();

    /** Returns the model variant key this appearance resolves to. */
    String getModelKey();

    /** Returns the animator variant key this appearance resolves to. */
    String getAnimatorKey();

    /**
     * Returns the size configuration tied to this appearance, if any.
     * <p>
     * Default returns empty — existing implementations compile without change.
     * Override in size-driven appearances (e.g. Gourdragora) to return the
     * matching {@link SizeVariantFeature.SizeConfig}.
     */
    default Optional<SizeVariantFeature.SizeConfig> getSizeConfig() {
        return Optional.empty();
    } // getSizeConfig ()

    // -- Resource resolution --

    /** Resolves the texture {@link ResourceLocation} from {@code VariantRegistries.TEXTURES}. */
    default ResourceLocation getTextureResource() {
        return VariantRegistries.TEXTURES.get(getTextureKey())
                .map(v -> v.getResource(getTextureKey()))
                .orElse(null);
    } // getTextureResource ()

    /** Resolves the model {@link ResourceLocation} from {@code VariantRegistries.MODELS}. */
    default ResourceLocation getModelResource() {
        return VariantRegistries.MODELS.get(getModelKey())
                .map(v -> v.getResource(getModelKey()))
                .orElse(null);
    } // getModelResource ()

    /** Resolves the animator {@link ResourceLocation} from {@code VariantRegistries.ANIMATORS}. */
    default ResourceLocation getAnimatorResource() {
        return VariantRegistries.ANIMATORS.get(getAnimatorKey())
                .map(v -> v.getResource(getAnimatorKey()))
                .orElse(null);
    } // getAnimatorResource ()

    /**
     * Not applicable for composite variants — returns {@code null}.
     * Use the typed resource methods above.
     */
    @Override
    default ResourceLocation getResource(String key) {
        return null;
    } // getResource ()

} // Interface: ICompositeAppearance
