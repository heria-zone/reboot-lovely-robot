package net.heriazone.hzlib.api.entity.variants;

import net.heriazone.hzlib.api.entity.variants.interfaces.*;

/**
 * <p>Central registry collection for all variant types.<p>
 * <p>
 * <b>Architecture:</b> Provides static access to type-specific variant registries,
 * enabling centralized variant management while maintaining type safety through
 * separate registry instances per variant type.
 * <p>
 * <b>Design Decision:</b> Static registries simplify access patterns and ensure
 * single source of truth for variant registration across the entire application.
 * Each variant type has its own registry to prevent type confusion.
 * <p>
 * <b>Usage Pattern:</b> Register variants during initialization, then use throughout
 * application lifecycle for variant resolution and selection.
 */
public class VariantRegistries {

    // -- Static Registry Instances --

    /**
     * Registry for texture variants.
     * <p>
     * <b>Usage:</b> Register texture variants during mod initialization.
     * Used by entity types to resolve texture resources.
     */
    public static final VariantRegistry<ITextureVariant> TEXTURES = new VariantRegistry<>(ITextureVariant.class);

    /**
     * Registry for model variants.
     * <p>
     * <b>Usage:</b> Register model variants during mod initialization.
     * Used by entity types to resolve model resources.
     */
    public static final VariantRegistry<IModelVariant> MODELS = new VariantRegistry<>(IModelVariant.class);

    /**
     * Registry for animator variants.
     * <p>
     * <b>Usage:</b> Register animator variants during mod initialization.
     * Used by entity types to resolve animator resources.
     */
    public static final VariantRegistry<IAnimatorVariant> ANIMATORS = new VariantRegistry<>(IAnimatorVariant.class);

    /**
     * Registry for entity appearances.
     * <p>
     * <b>Usage:</b> Register entity appearances during mod initialization.
     * Used by entity types to resolve appearance resources.
     */
    public static final VariantRegistry<IAppearanceVariant> APPEARANCES = new VariantRegistry<>(IAppearanceVariant.class);

    // -- Private Constructor --

    /**
     * Prevents instantiation of utility class.
     */
    private VariantRegistries() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    } // Constructor: VariantRegistries ()

} // Class: VariantRegistries