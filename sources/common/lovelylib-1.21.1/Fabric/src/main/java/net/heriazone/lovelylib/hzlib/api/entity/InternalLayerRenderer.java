package net.heriazone.lovelylib.hzlib.api.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.heriazone.lovelylib.common.entity.LovelyRobotEntity;
import net.heriazone.lovelylib.hzlib.api.layer.IInternalRenderLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Composable layer-based renderer for robot entities.
 * <p>
 * <b>Architecture:</b> Thin wrapper around common InternalLayerRenderer that handles
 * GeckoLib-specific rendering integration. Delegates layer management to common
 * implementation while maintaining GeckoLib boundary.
 * <p>
 * <b>Design Decision:</b> Wrapper pattern preserves existing API while enabling
 * code reuse through common layer system. Layer composition logic is shared
 * across all loaders.
 */
public class InternalLayerRenderer<T extends LovelyRobotEntity & GeoEntity> extends GeoEntityRenderer<T> {

    // -- Fields --

    private final List<IInternalRenderLayer<T>> renderLayers = new ArrayList<>();
    private boolean isRenderingLayers = false; // Prevent recursion

    // -- Constructors --

    /**
     * Creates renderer with specified model and default shadow radius.
     *
     * @param context renderer provider context
     * @param model GeoModel for entity geometry and animations
     */
    public InternalLayerRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
    } // Constructor: InternalLayerRenderer()

    /**
     * Creates renderer with specified model and shadow radius.
     *
     * @param context renderer provider context
     * @param model GeoModel for entity geometry and animations
     * @param shadowRadius shadow size (0.0 = no shadow, 0.5 = default)
     */
    public InternalLayerRenderer(EntityRendererProvider.Context context, GeoModel<T> model, float shadowRadius) {
        super(context, model);
        this.shadowRadius = shadowRadius;
    } // Constructor: InternalLayerRenderer()

    // -- Layer Management --

    /**
     * Adds render layer to the layer stack.
     * <p>
     * <b>Architecture:</b> Layers render in order added. Base texture should be first,
     * followed by emissives, then overlays, then dynamic effects.
     * <p>
     * <b>Design Decision:</b> Builder pattern allows fluent layer configuration during
     * renderer construction.
     *
     * @param layer render layer to add
     * @return this renderer for method chaining
     */
    public InternalLayerRenderer<T> addLayer(IInternalRenderLayer<T> layer) {
        renderLayers.add(layer);
        return this;
    } // addLayer()

    /**
     * Removes all render layers.
     * <p>
     * <i>Note:</i> Rarely needed. Primarily for testing or dynamic layer reconfiguration.
     *
     * @return this renderer for method chaining
     */
    public InternalLayerRenderer<T> clearLayers() {
        renderLayers.clear();
        return this;
    } // clearLayers()

    // -- Rendering --

    @Override
    public void actuallyRender(PoseStack poseStack, T animatable, BakedGeoModel model, RenderType renderType,
                               MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                               float partialTick, int packedLight, int packedOverlay, int color) {

        // Skip layer rendering if we're already rendering layers (prevent recursion from reRender calls)
        if (isRenderingLayers) {
            super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer,
                    isReRender, partialTick, packedLight, packedOverlay, color);
            return;
        }

        // First, render the base model normally
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay, color);

        // Set flag to prevent recursion when layers call reRender
        isRenderingLayers = true;

        try {
            // Then render additional layers on top (skip first layer as it's the base texture)
            for (int i = 1; i < renderLayers.size(); i++) {
                IInternalRenderLayer<T> layer = renderLayers.get(i);
                if (layer.shouldRender(animatable, partialTick)) {
                    layer.render(
                            poseStack,
                            animatable,
                            model,
                            renderType,
                            bufferSource,
                            buffer,
                            partialTick,
                            packedLight,
                            packedOverlay
                    );
                }
            }
        } finally {
            // Always reset flag
            isRenderingLayers = false;
        }
    } // actuallyRender()

    @Override
    public @NotNull ResourceLocation getTextureLocation(T entity) {
        return entity.getTexture();
    } // getTextureLocation()

} // Class: InternalLayerRenderer
