package net.heriazone.hzlib.api.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.heriazone.hzlib.api.entity.InternalEntity;
import net.heriazone.hzlib.api.rendering.LayerRenderContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Forge wrapper for dynamic color layer rendering.
 * <p>
 * <b>Architecture:</b> Thin wrapper that delegates business logic to Common module
 * while maintaining GeckoLib-specific rendering calls in Forge module.
 * <p>
 * <b>Design Decision:</b> Preserves GeckoLib integration patterns while extracting
 * color calculation and rendering logic to shared Common implementation.
 */
public class DynamicColorLayer<T extends InternalEntity & GeoEntity> implements IInternalRenderLayer<T> {

    // -- Fields --

    private final GeoRenderer<T> renderer;
    private final ResourceLocation maskTexture;
    private final Function<T, Integer> colorProvider;
    private final Predicate<T> renderCondition;
    private final net.heriazone.hzlib.api.rendering.DynamicColorLayer commonLayer;

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
        this.commonLayer = new net.heriazone.hzlib.api.rendering.DynamicColorLayer(
                maskTexture.toString(),
                entity -> colorProvider.apply((T) entity),
                entity -> renderCondition.test((T) entity)
        );
    } // Constructor: DynamicColorLayer()

    // -- IInternalRenderLayer Implementation --

    @Override
    public boolean shouldRender(T entity, float partialTick) {
        // Delegate to Common layer logic
        return commonLayer.shouldRender(entity, partialTick);
    } // shouldRender()

    @Override
    public void render(PoseStack poseStack, T entity, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {

        // Delegate business logic to Common layer
        if (commonLayer.shouldRender(entity, partialTick)) {
            // Get color from Common layer
            LayerRenderContext context = LayerRenderContext.texture(null, entity);
            int color = commonLayer.calculateColor(context);

            // GeckoLib-specific rendering remains in Forge module
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
        }
    } // render()

    // -- Static Color Utility Methods (Delegated to Common) --

    /**
     * Creates health-based color gradient (delegated to Common layer).
     */
    public static int healthGradientColor(InternalEntity entity) {
        return net.heriazone.hzlib.api.rendering.DynamicColorLayer.healthGradientColor(entity);
    } // healthGradientColor()

    /**
     * Creates solid color from RGB values (delegated to Common layer).
     */
    public static int solidColor(int red, int green, int blue) {
        return net.heriazone.hzlib.api.rendering.DynamicColorLayer.solidColor(red, green, blue);
    } // solidColor()

    /**
     * Creates color with specified opacity (delegated to Common layer).
     */
    public static int colorWithAlpha(int red, int green, int blue, int alpha) {
        return net.heriazone.hzlib.api.rendering.DynamicColorLayer.colorWithAlpha(red, green, blue, alpha);
    } // colorWithAlpha()

} // Class: DynamicColorLayer