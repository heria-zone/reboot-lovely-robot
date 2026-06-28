package net.heriazone.hzlib.api.entity.variants;

import net.heriazone.hzlib.api.entity.variants.interfaces.*;

/**
 * <p>Central registry collection for all variant types.<p>
 * <p>
 * <b>Architecture:</b> Provides static access to type-specific variant registries,
 * enabling centralized variant management while maintaining type safety through
 * separate registry instances per variant type.
 * <p>
 * <b>Two-lane architecture:</b> The three single-axis registries ({@code TEXTURES},
 * {@code MODELS}, {@code ANIMATORS}) serve Lane A — independent combinatorial axes.
 * {@code APPEARANCES} serves Lane B — fully-coupled composite entries. A family
 * registers in one lane only; {@code NativeEntity} enforces this at runtime.
 */
public class VariantRegistries {

    // -- Static Registry Instances --

    /** Lane A — texture palette variants (colors, seasonal swaps). */
    public static final VariantRegistry<ITextureVariant>      TEXTURES    = new VariantRegistry<>(ITextureVariant.class);

    /** Lane A — model state variants (default, armed, size). */
    public static final VariantRegistry<IModelVariant>        MODELS      = new VariantRegistry<>(IModelVariant.class);

    /** Lane A — animation file variants. */
    public static final VariantRegistry<IAnimatorVariant>     ANIMATORS   = new VariantRegistry<>(IAnimatorVariant.class);

    /**
     * Lane B — composite appearance bundles (texture + model + animator + optional size).
     * <p>
     * Previously typed as {@code IAppearanceVariant}; updated to {@link ICompositeAppearance}
     * following the Terminology alignment in ADR 021.
     */
    public static final VariantRegistry<ICompositeAppearance> APPEARANCES = new VariantRegistry<>(ICompositeAppearance.class);

    // -- Private Constructor --

    private VariantRegistries() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    } // Constructor: VariantRegistries ()

} // Class: VariantRegistries