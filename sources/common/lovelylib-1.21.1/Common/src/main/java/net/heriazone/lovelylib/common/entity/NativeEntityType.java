package net.heriazone.lovelylib.common.entity;

import net.heriazone.lovelylib.common.entity.enums.*;
import net.heriazone.lovelylib.common.shared.LovelyIdentifier;
import net.heriazone.lovelylib.hzlib.api.entity.InternalEntityType;
import net.heriazone.lovelylib.hzlib.api.entity.features.*;
import net.heriazone.lovelylib.hzlib.framework.entity.data.ResourceMap;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * <p>Robot-specific entity type with 16-color palette and level system integration.<p>
 * <p>
 * <b>Architecture:</b> Extends InternalEntityType to provide robot-specific functionality
 * including color palette management and automatic LevelFeature attachment. Bridges
 * framework layer (pure Java) with Minecraft's resource system.
 * <p>
 * <b>Design Decision:</b> Automatically attaches LevelFeature during construction to ensure
 * all robots have leveling capability. Color palette uses separate map from base textures
 * to support both variant textures (DEFAULT, ARMED) and color customization.
 * <p>
 * <b>Resource Management:</b> Populates textures, models, and animators based on EntityVariant.
 * Color textures are populated separately via withColorPalette() for 16-color customization.
 */
public class NativeEntityType extends InternalEntityType<NativeEntityType> {

    // -- Color Palette --

    private final Map<EntityTexture, ResourceLocation> colorTextures;
    private final EntityVariant variant;

    // -- Constructor --

    /**
     * Creates robot entity type with specified variant and attaches LevelFeature.
     * <p>
     * <b>State Impact:</b> Automatically attaches LevelFeature with maxLevel 0.
     * Caller should configure maxLevel via LevelFeature.setMaxLevel() after construction.
     * <p>
     * <b>Design Decision:</b> LevelFeature attached during construction ensures all
     * robots have leveling capability without requiring explicit feature attachment.
     *
     * @param key unique identifier for this robot type
     * @param variant entity variant determining resource paths
     */
    public NativeEntityType(String key, EntityVariant variant) {
        super(key);
        this.variant = variant;
        this.colorTextures = new HashMap<>();

        // Attach LevelFeature automatically
        withFeature(LevelFeature.class, new LevelFeature(0));
    } // Constructor: NativeEntityType ()

    // -- Abstract Method Implementations --

    /**
     * Populates base texture variants (DEFAULT, ARMED) for this robot type.
     * <p>
     * <b>Implementation:</b> Registers DEFAULT texture using variant-specific path.
     * Color textures are populated separately via withColorPalette().
     *
     * @param textures resource map to populate with texture identifiers
     */
    @Override
    protected void populateTextures(ResourceMap<EntityVariantTexture, ResourceLocation> textures) {
        String basePath = LovelyIdentifier.TEXTURE_ENTITY_PATH + key + "/";
        textures.put(EntityVariantTexture.DEFAULT, LovelyIdentifier.getId(basePath + "default.png"));
    } // populateTextures ()

    /**
     * Populates model variants (DEFAULT, ARMED) for this robot type.
     * <p>
     * <b>Implementation:</b> Registers DEFAULT and ARMED models using variant-specific paths.
     *
     * @param models resource map to populate with model identifiers
     */
    @Override
    protected void populateModels(ResourceMap<EntityVariantModel, ResourceLocation> models) {
        models.put(EntityVariantModel.DEFAULT, LovelyIdentifier.getId("geo/" + key + "." + LovelyIdentifier.MOD_DEFAULT + ".geo.json"));
        models.put(EntityVariantModel.ARMED, LovelyIdentifier.getId("geo/" + key + "." + LovelyIdentifier.MOD_ARMED + ".geo.json"));
    } // populateModels ()

    /**
     * Populates animator variants for this robot type.
     * <p>
     * <b>Implementation:</b> Registers DEFAULT animator using variant-specific path.
     *
     * @param animators resource map to populate with animator identifiers
     */
    @Override
    protected void populateAnimators(ResourceMap<EntityVariantAnimator, ResourceLocation> animators) {
        animators.put(EntityVariantAnimator.DEFAULT, LovelyIdentifier.getId("animations/" + LovelyIdentifier.ANIM_DEFAULT + ".animation.json"));
    } // populateAnimators ()

    /**
     * Creates translatable text component for robot type name.
     * <p>
     * <b>Implementation:</b> Uses variant-specific translation key.
     *
     * @param key entity type key for translation
     * @return translatable text component for robot name
     */
    @Override
    protected MutableComponent createTranslation(String key) {
        return LovelyIdentifier.getTranslation("entity.", key);
    } // createTranslation ()

    // -- Color Palette System --

    /**
     * Populates 16-color texture palette for this robot type.
     * <p>
     * <b>Architecture:</b> Enables per-robot color customization using Minecraft's
     * 16-color dye system. Color textures are separate from variant textures to
     * support independent customization.
     * <p>
     * <b>Performance:</b> Populates all 16 colors at once to avoid repeated path
     * construction during gameplay.
     *
     * @param variant entity variant determining color texture paths
     * @return this instance for method chaining
     */
    public NativeEntityType withColorPalette(EntityVariant variant) {
        String basePath = LovelyIdentifier.TEXTURE_ENTITY_PATH + variant.getName() + "/";

        // Populate all 16 colors using variant_ID naming pattern
        for (EntityTexture color : EntityTexture.values()) {
            if (color != EntityTexture.RANDOM) {
                String colorId = String.format("%02d", color.getId());
                colorTextures.put(color, LovelyIdentifier.getId(basePath + variant.getName() + "_" + colorId + ".png"));
            }
        }

        return this;
    } // withColorPalette ()

    /**
     * Retrieves texture identifier for specified color.
     * <p>
     * <b>Failure Mode:</b> Returns WHITE texture if requested color not available.
     * Ensures rendering always has valid texture even with incomplete palettes.
     *
     * @param color desired color variant
     * @return texture identifier for color, or WHITE if not available
     */
    public ResourceLocation getColorTexture(EntityTexture color) {
        if (hasColor(color)) {
            return colorTextures.get(color);
        }
        return colorTextures.getOrDefault(EntityTexture.WHITE,
                textures.get(EntityVariantTexture.DEFAULT));
    } // getColorTexture ()

    /**
     * Checks if color texture is available for this robot type.
     *
     * @param color color variant to check
     * @return true if color texture exists, false otherwise
     */
    public boolean hasColor(EntityTexture color) {
        return colorTextures.containsKey(color);
    } // hasColor ()

    /**
     * Generates random color ID from available color palette.
     * <p>
     * <b>Performance:</b> Creates new list on each call. Consider caching if
     * called frequently during gameplay.
     *
     * @return ID of randomly selected color, or 0 (WHITE) if palette empty
     */
    public int getRandomColorId() {
        if (colorTextures.isEmpty()) {
            return 0;  // WHITE
        }

        var colors = colorTextures.keySet().stream().toList();
        EntityTexture randomColor = colors.get(new Random().nextInt(colors.size()));
        return randomColor.getId();
    } // getRandomColorId ()

    // -- Convenience Accessors --

    /**
     * Retrieves maximum level from attached LevelFeature.
     * <p>
     * <b>Design Decision:</b> Convenience method eliminates boilerplate Optional
     * handling at call sites. Returns 0 if LevelFeature not present (defensive).
     *
     * @return maximum level from LevelFeature, or 0 if feature not present
     */
    public int getMaxLevel() {
        return getFeature(LevelFeature.class)
                .map(LevelFeature::getMaxLevel)
                .orElse(0);
    } // getMaxLevel ()

} // Class: NativeEntityType