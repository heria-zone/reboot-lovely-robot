package net.msymbios.llovelyr.lib.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity;
import net.msymbios.llovelyr.lib.rendering.LayerRenderContext;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * NeoForge wrapper for dynamic color layer rendering.
 * <p>
 * <b>Architecture:</b> Thin wrapper around Common DynamicColorLayer that handles
 * NeoForge-specific GeckoLib rendering calls. Delegates business logic to Common module
 * while keeping GeckoLib dependencies in NeoForge loader.
 * <p>
 * <b>Design Decision:</b> Wrapper pattern maintains GeckoLib isolation while
 * extracting reusable dynamic color logic to Common module.
 */
public class DynamicColorLayer<T extends LovelyRobotEntity & GeoEntity> implements IInternalRenderLayer<T> {

    // -- Fields --

    private final GeoRenderer<T> renderer;
    private final net.msymbios.llovelyr.lib.rendering.DynamicColorLayer<T> commonLayer;

    // -- Constructors --

    /**
     * Creates NeoForge dynamic color layer wrapper that always renders.
     *
     * @param renderer parent GeoRenderer managing this entity
     * @param maskTexture grayscale mask texture (white = tinted, transparent = no tint)
     * @param colorProvider function calculating ARGB color from entity state
     */
    public DynamicColorLayer(GeoRenderer<T> renderer, ResourceLocation maskTexture, Function<T, Integer> colorProvider) {
        this.renderer = renderer;
        this.commonLayer = new net.msymbios.llovelyr.lib.rendering.DynamicColorLayer<>(maskTexture, colorProvider);
    } // Constructor: DynamicColorLayer()

    /**
     * Creates NeoForge dynamic color layer wrapper with conditional rendering.
     *
     * @param renderer parent GeoRenderer managing this entity
     * @param maskTexture grayscale mask texture (white = tinted, transparent = no tint)
     * @param colorProvider function calculating ARGB color from entity state
     * @param renderCondition predicate determining if layer should render
     */
    public DynamicColorLayer(GeoRenderer<T> renderer, ResourceLocation maskTexture,
                             Function<T, Integer> colorProvider, Predicate<T> renderCondition) {
        this.renderer = renderer;
        this.commonLayer = new net.msymbios.llovelyr.lib.rendering.DynamicColorLayer<>(maskTexture, colorProvider, renderCondition);
    } // Constructor: DynamicColorLayer()

    // -- IInternalRenderLayer Implementation --

    @Override
    public boolean shouldRender(T entity, float partialTick) {
        return commonLayer.shouldRender(entity, partialTick);
    } // shouldRender()

    @Override
    public void render(PoseStack poseStack, T entity, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {

        // Get color and texture from common layer
        LayerRenderContext context = LayerRenderContext.texture(null, entity);
        int color = commonLayer.calculateColor(context);
        ResourceLocation maskTexture = ResourceLocation.parse(commonLayer.getTexturePath());

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

    // -- Color Utility Methods (Delegated to Common) --

    /**
     * Creates health-based color gradient (bright green → yellow → red).
     *
     * @param entity robot entity with health data
     * @return ARGB color representing health percentage
     */
    public static int healthGradientColor(LovelyRobotEntity entity) {
        return net.msymbios.llovelyr.lib.rendering.DynamicColorLayer.healthGradientColor(entity);
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
        return net.msymbios.llovelyr.lib.rendering.DynamicColorLayer.solidColor(red, green, blue);
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
        return net.msymbios.llovelyr.lib.rendering.DynamicColorLayer.colorWithAlpha(red, green, blue, alpha);
    } // colorWithAlpha()

} // Class: DynamicColorLayer
