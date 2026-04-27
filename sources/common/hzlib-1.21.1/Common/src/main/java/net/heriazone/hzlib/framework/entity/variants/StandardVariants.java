package net.heriazone.hzlib.framework.entity.variants;

import net.heriazone.hzlib.api.entity.variants.interfaces.*;

/**
 * <p>Pre-defined standard variants for common use cases.<p>
 * <p>
 * <b>Architecture:</b> Provides commonly used variant instances that can be registered
 * by mods without requiring custom implementations. Covers typical entity resource
 * patterns found in most Minecraft mods.
 * <p>
 * <b>Design Decision:</b> Static instances reduce boilerplate in mod initialization
 * while providing consistent naming and path conventions across different mods.
 * Higher priority values indicate more "default" variants.
 * <p>
 * <b>Usage Pattern:</b> Register desired variants during mod initialization:
 * <pre>
 * VariantRegistries.TEXTURES.register(StandardVariants.DEFAULT_TEXTURE);
 * VariantRegistries.MODELS.register(StandardVariants.DEFAULT_MODEL);
 * </pre>
 */
public class StandardVariants {

    // -- Texture Variants --

    /**
     * Default texture variant using standard entity texture path.
     * <p>
     * <b>Path Pattern:</b> "textures/entity/{entity}/default.png"
     * <b>Priority:</b> 100 (highest - preferred default)
     */
    public static final ITextureVariant DEFAULT_TEXTURE = new StandardTextureVariant(
            "default", 
            "Default", 
            "textures/entity/{entity}/default.png", 
            100
    );

    /**
     * Armed/combat texture variant for entities with weapon states.
     * <p>
     * <b>Path Pattern:</b> "textures/entity/{entity}/armed.png"
     * <b>Priority:</b> 50 (secondary option)
     */
    public static final ITextureVariant ARMED_TEXTURE = new StandardTextureVariant(
            "armed", 
            "Armed", 
            "textures/entity/{entity}/armed.png", 
            50
    );

    /**
     * Alternative texture variant for entities with multiple base appearances.
     * <p>
     * <b>Path Pattern:</b> "textures/entity/{entity}/alt.png"
     * <b>Priority:</b> 25 (tertiary option)
     */
    public static final ITextureVariant ALT_TEXTURE = new StandardTextureVariant(
            "alt", 
            "Alternative", 
            "textures/entity/{entity}/alt.png", 
            25
    );

    // -- Model Variants --

    /**
     * Default model variant using standard GeckoLib model path.
     * <p>
     * <b>Path Pattern:</b> "geo/{entity}.geo.json"
     * <b>Priority:</b> 100 (highest - preferred default)
     */
    public static final IModelVariant DEFAULT_MODEL = new StandardModelVariant(
            "default", 
            "Default", 
            "geo/{entity}.geo.json", 
            100
    );

    /**
     * Armed/combat model variant for entities with weapon states.
     * <p>
     * <b>Path Pattern:</b> "geo/{entity}.armed.geo.json"
     * <b>Priority:</b> 50 (secondary option)
     */
    public static final IModelVariant ARMED_MODEL = new StandardModelVariant(
            "armed", 
            "Armed", 
            "geo/{entity}.armed.geo.json", 
            50
    );

    /**
     * Alternative model variant for entities with multiple base models.
     * <p>
     * <b>Path Pattern:</b> "geo/{entity}.alt.geo.json"
     * <b>Priority:</b> 25 (tertiary option)
     */
    public static final IModelVariant ALT_MODEL = new StandardModelVariant(
            "alt", 
            "Alternative", 
            "geo/{entity}.alt.geo.json", 
            25
    );

    // -- Animator Variants --

    /**
     * Default animator variant using standard GeckoLib animation path.
     * <p>
     * <b>Path Pattern:</b> "animations/{entity}.animation.json"
     * <b>Priority:</b> 100 (highest - preferred default)
     */
    public static final IAnimatorVariant DEFAULT_ANIMATOR = new StandardAnimatorVariant(
            "default", 
            "Default", 
            "animations/{entity}.animation.json", 
            100
    );

    /**
     * Combat animator variant for entities with combat-specific animations.
     * <p>
     * <b>Path Pattern:</b> "animations/{entity}.combat.animation.json"
     * <b>Priority:</b> 50 (secondary option)
     */
    public static final IAnimatorVariant COMBAT_ANIMATOR = new StandardAnimatorVariant(
            "combat", 
            "Combat", 
            "animations/{entity}.combat.animation.json", 
            50
    );

    /**
     * Alternative animator variant for entities with multiple animation sets.
     * <p>
     * <b>Path Pattern:</b> "animations/{entity}.alt.animation.json"
     * <b>Priority:</b> 25 (tertiary option)
     */
    public static final IAnimatorVariant ALT_ANIMATOR = new StandardAnimatorVariant(
            "alt", 
            "Alternative", 
            "animations/{entity}.alt.animation.json", 
            25
    );

    // -- Minecraft Color Variants --

    /**
     * Creates standard 16-color palette variants for Minecraft dye colors.
     * <p>
     * <b>Color Order:</b> Follows Minecraft DyeColor enum ordering (WHITE=0, BLACK=15)
     * <b>Path Pattern:</b> "textures/entity/{entity}/{entity}_{id}.png"
     * <b>Priority:</b> All colors have priority 10 (lower than base variants)
     *
     * @return array of 16 color texture variants
     */
    public static ITextureVariant[] createMinecraftColorPalette() {
        String[] colorNames = {
                "White", "Orange", "Magenta", "Light Blue",
                "Yellow", "Lime", "Pink", "Gray",
                "Light Gray", "Cyan", "Purple", "Blue",
                "Brown", "Green", "Red", "Black"
        };
        
        String[] colorKeys = {
                "white", "orange", "magenta", "light_blue",
                "yellow", "lime", "pink", "gray",
                "light_gray", "cyan", "purple", "blue",
                "brown", "green", "red", "black"
        };

        ITextureVariant[] variants = new ITextureVariant[16];
        for (int i = 0; i < 16; i++) {
            variants[i] = new ColorTextureVariant(
                    colorKeys[i], 
                    colorNames[i], 
                    i, 
                    "textures/entity/{entity}/{entity}_{id}.png", 
                    10
            );
        }
        
        return variants;
    } // createMinecraftColorPalette ()

    // -- Utility Methods --

    /**
     * Registers all standard texture variants to the texture registry.
     * <p>
     * <b>Registered Variants:</b> DEFAULT_TEXTURE, ARMED_TEXTURE, ALT_TEXTURE
     */
    public static void registerStandardTextures() {
        net.heriazone.hzlib.api.entity.variants.VariantRegistries.TEXTURES.register(DEFAULT_TEXTURE);
        net.heriazone.hzlib.api.entity.variants.VariantRegistries.TEXTURES.register(ARMED_TEXTURE);
        net.heriazone.hzlib.api.entity.variants.VariantRegistries.TEXTURES.register(ALT_TEXTURE);
    } // registerStandardTextures ()

    /**
     * Registers all standard model variants to the model registry.
     * <p>
     * <b>Registered Variants:</b> DEFAULT_MODEL, ARMED_MODEL, ALT_MODEL
     */
    public static void registerStandardModels() {
        net.heriazone.hzlib.api.entity.variants.VariantRegistries.MODELS.register(DEFAULT_MODEL);
        net.heriazone.hzlib.api.entity.variants.VariantRegistries.MODELS.register(ARMED_MODEL);
        net.heriazone.hzlib.api.entity.variants.VariantRegistries.MODELS.register(ALT_MODEL);
    } // registerStandardModels ()

    /**
     * Registers all standard animator variants to the animator registry.
     * <p>
     * <b>Registered Variants:</b> DEFAULT_ANIMATOR, COMBAT_ANIMATOR, ALT_ANIMATOR
     */
    public static void registerStandardAnimators() {
        net.heriazone.hzlib.api.entity.variants.VariantRegistries.ANIMATORS.register(DEFAULT_ANIMATOR);
        net.heriazone.hzlib.api.entity.variants.VariantRegistries.ANIMATORS.register(COMBAT_ANIMATOR);
        net.heriazone.hzlib.api.entity.variants.VariantRegistries.ANIMATORS.register(ALT_ANIMATOR);
    } // registerStandardAnimators ()

    /**
     * Registers Minecraft color palette to the texture registry.
     * <p>
     * <b>Registered Variants:</b> All 16 Minecraft dye colors
     */
    public static void registerMinecraftColors() {
        ITextureVariant[] colors = createMinecraftColorPalette();
        for (ITextureVariant color : colors) {
            net.heriazone.hzlib.api.entity.variants.VariantRegistries.TEXTURES.register(color);
        }
    } // registerMinecraftColors ()

    /**
     * Registers all standard variants (textures, models, animators, colors).
     * <p>
     * <b>Convenience Method:</b> Calls all individual registration methods.
     */
    public static void registerAll() {
        registerStandardTextures();
        registerStandardModels();
        registerStandardAnimators();
        registerMinecraftColors();
    } // registerAll ()

    // -- Private Constructor --

    /**
     * Prevents instantiation of utility class.
     */
    private StandardVariants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    } // Constructor: StandardVariants ()

} // Class: StandardVariants