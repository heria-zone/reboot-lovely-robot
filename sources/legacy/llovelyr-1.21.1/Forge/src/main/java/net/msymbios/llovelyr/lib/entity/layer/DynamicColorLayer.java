package net.msymbios.llovelyr.lib.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

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
public class DynamicColorLayer<T extends LovelyRobotEntity & GeoEntity> implements IInternalRenderLayer<T> {

    // -- Fields --

    private final GeoRenderer<T> renderer;
    private final ResourceLocation maskTexture;
    private final Function<T, Integer> colorProvider;
    private final Predicate<T> renderCondition;

    // -- Constructors --

    /**
     * Creates dynamic color layer that always renders.
     *
     * @param renderer parent GeoRenderer managing this entity
     * @param maskTexture grayscale mask texture (white = tinted, transparent = no tint)
     * @param colorProvider function calculating ARGB color from entity state
     */
    public DynamicColorLayer(GeoRenderer<T> renderer, ResourceLocation maskTexture, Function<T, Integer> colorProvider) {
        this(renderer, maskTexture, colorProvider, entity -> true);
    } // Constructor: DynamicColorLayer()

    /**
     * Creates dynamic color layer with conditional rendering.
     *
     * @param renderer parent GeoRenderer managing this entity
     * @param maskTexture grayscale mask texture (white = tinted, transparent = no tint)
     * @param colorProvider function calculating ARGB color from entity state
     * @param renderCondition predicate determining if layer should render
     */
    public DynamicColorLayer(GeoRenderer<T> renderer, ResourceLocation maskTexture,
                             Function<T, Integer> colorProvider, Predicate<T> renderCondition) {
        this.renderer = renderer;
        this.maskTexture = maskTexture;
        this.colorProvider = colorProvider;
        this.renderCondition = renderCondition;
    } // Constructor: DynamicColorLayer()

    // -- IInternalRenderLayer Implementation --

    @Override
    public boolean shouldRender(T entity, float partialTick) {
        return maskTexture != null && renderCondition.test(entity);
    } // shouldRender()

    @Override
    public void render(PoseStack poseStack, T entity, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {

        // Calculate color from entity state
        int color = colorProvider.apply(entity);

        // Render mask with calculated tint
        RenderType colorRenderType = RenderType.armorCutoutNoCull(maskTexture);

        renderer.reRender(
                bakedModel,
                poseStack,
                bufferSource,
                entity,
                colorRenderType,
                bufferSource.getBuffer(colorRenderType),
                partialTick,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                color
        );
    } // render()

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
    public static int healthGradientColor(LovelyRobotEntity entity) {
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

} // Class: DynamicColorLayer
