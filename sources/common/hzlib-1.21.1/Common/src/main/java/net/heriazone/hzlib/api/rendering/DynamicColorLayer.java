package net.heriazone.hzlib.api.rendering;

import net.heriazone.hzlib.api.entity.InternalEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Dynamic color layer for runtime-tinted visual indicators.
 * <p>
 * <b>Architecture:</b> Renders grayscale mask texture tinted with color calculated
 * from entity state. Enables dynamic visual feedback (health indicators, combat mode,
 * status effects) without texture file proliferation.
 * <p>
 * <b>Design Decision:</b> Single grayscale mask + runtime tinting vs separate colored
 * textures. Reduces texture count from N colors to 1 mask. Color calculation happens
 * per-frame but is typically simple (health percentage, state enum).
 * <p>
 * <b>Usage Examples:</b>
 * - Health collar: Green (full) → Yellow (half) → Red (critical)
 * - Combat mode: Blue (passive) → Orange (wary) → Red (aggressive)
 * - Status effects: Purple (poisoned), White (regenerating)
 * <p>
 * <b>Performance:</b> Color calculation runs every frame. Keep color functions
 * lightweight. Cache complex calculations in entity tick logic.
 */
public class DynamicColorLayer<T extends InternalEntity> implements IInternalRenderLayer<T> {

    // -- Fields --

    private final String maskTexturePath;
    private final Function<InternalEntity, Integer> colorProvider;
    private final Predicate<InternalEntity> renderCondition;

    // -- Constructors --

    /**
     * Creates dynamic color layer that always renders.
     *
     * @param maskTexture grayscale mask texture (white = tinted, transparent = no tint)
     * @param colorProvider function calculating ARGB color from entity state
     */
    public DynamicColorLayer(ResourceLocation maskTexture, Function<T, Integer> colorProvider) {
        this.maskTexturePath = maskTexture.toString();
        this.colorProvider = entity -> colorProvider.apply((T) entity);
        this.renderCondition = entity -> true;
    } // Constructor: DynamicColorLayer()

    /**
     * Creates dynamic color layer with conditional rendering.
     *
     * @param maskTexture grayscale mask texture (white = tinted, transparent = no tint)
     * @param colorProvider function calculating ARGB color from entity state
     * @param renderCondition predicate determining if layer should render
     */
    public DynamicColorLayer(ResourceLocation maskTexture, Function<T, Integer> colorProvider, Predicate<T> renderCondition) {
        this.maskTexturePath = maskTexture.toString();
        this.colorProvider = entity -> colorProvider.apply((T) entity);
        this.renderCondition = entity -> renderCondition.test((T) entity);
    } // Constructor: DynamicColorLayer()

    /**
     * Creates dynamic color layer with string texture path (for loader wrappers).
     *
     * @param maskTexturePath grayscale mask texture path
     * @param colorProvider function calculating ARGB color from entity state
     * @param renderCondition predicate determining if layer should render
     */
    public DynamicColorLayer(String maskTexturePath, Function<InternalEntity, Integer> colorProvider, Predicate<InternalEntity> renderCondition) {
        this.maskTexturePath = maskTexturePath;
        this.colorProvider = colorProvider;
        this.renderCondition = renderCondition;
    } // Constructor: DynamicColorLayer()

    // -- Color Utility Methods --

    /**
     * Creates health-based color gradient (bright green → yellow → red).
     * <p>
     * <b>Usage:</b> Health indicators on collar, eyes, or body parts.
     * <p>
     * <b>Color Palette:</b>
     * - 100% health: Bright lime green (RGB 50, 255, 100) - matches headphone green
     * - 50% health: Yellow (RGB 255, 255, 0)
     * - 0% health: Red (RGB 255, 0, 0)
     *
     * @param entity robot entity with health data
     * @return ARGB color representing health percentage
     */
    public static int healthGradientColor(InternalEntity entity) {
        float healthPercent = entity.getHealth() / entity.getMaxHealth();

        // Bright lime green (similar to headphone green)
        final int HEALTH_GREEN_RED = 50;
        final int HEALTH_GREEN_GREEN = 255;
        final int HEALTH_GREEN_BLUE = 100;

        int red, green, blue;

        if (healthPercent > 0.5f) {
            // Bright green to yellow (100% → 50%)
            // Interpolate from bright green to yellow
            float t = (1.0f - healthPercent) * 2.0f; // 0.0 at 100%, 1.0 at 50%
            red = (int) (HEALTH_GREEN_RED + (255 - HEALTH_GREEN_RED) * t);
            green = (int) (HEALTH_GREEN_GREEN + (255 - HEALTH_GREEN_GREEN) * t);
            blue = (int) (HEALTH_GREEN_BLUE + (0 - HEALTH_GREEN_BLUE) * t);
        } else {
            // Yellow to red (50% → 0%)
            red = 255;
            green = (int) (healthPercent * 2.0f * 255);
            blue = 0;
        }

        return FastColor.ARGB32.color(255, red, green, blue);
    } // healthGradientColor()

    /**
     * Creates solid color from RGB values.
     *
     * @param red red component (0-255)
     * @param green green component (0-255)
     * @param blue blue component (0-255)
     * @return ARGB color with full opacity
     */
    public static int solidColor(int red, int green, int blue) {
        return FastColor.ARGB32.color(255, red, green, blue);
    } // solidColor()

    /**
     * Creates color with specified opacity.
     *
     * @param red red component (0-255)
     * @param green green component (0-255)
     * @param blue blue component (0-255)
     * @param alpha opacity (0-255, 0 = transparent, 255 = opaque)
     * @return ARGB color with specified opacity
     */
    public static int colorWithAlpha(int red, int green, int blue, int alpha) {
        return FastColor.ARGB32.color(alpha, red, green, blue);
    } // colorWithAlpha()

    // -- Layer Logic Methods --

    /**
     * Calculates color for the given render context.
     *
     * @param context layer render context
     * @return ARGB color for rendering
     */
    public int calculateColor(LayerRenderContext context) {
        return colorProvider.apply(context.getEntity());
    } // calculateColor()

    /**
     * Helper method for backward compatibility with LayerRenderContext-based shouldRender.
     *
     * @param context layer render context
     * @return true if layer should render
     */
    public boolean shouldRender(LayerRenderContext context) {
        return shouldRender((T) context.getEntity(), 0.0f);
    } // shouldRender()

    /**
     * Checks if layer should render for the given entity.
     *
     * @param entity robot entity being rendered
     * @param partialTick sub-tick interpolation for smooth animation
     * @return true if layer should render
     */
    @Override
    public boolean shouldRender(T entity, float partialTick) {
        return maskTexturePath != null && renderCondition.test(entity);
    } // shouldRender()

    @Override
    public LayerRenderContext getRenderContext(T entity, float partialTick) {
        int color = colorProvider.apply(entity);

        // Convert string path to ResourceLocation
        ResourceLocation texture = null;
        if (maskTexturePath != null && !maskTexturePath.isEmpty()) {
            texture = ResourceLocation.parse(maskTexturePath);
        }

        return LayerRenderContext.coloredTexture(texture, color, entity);
    } // getRenderContext()

    /**
     * Gets the texture path for this layer.
     *
     * @return texture path string
     */
    public String getTexturePath() {
        return maskTexturePath;
    } // getTexturePath()

} // Class: DynamicColorLayer