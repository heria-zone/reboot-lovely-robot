package net.heriazone.hzlib.api.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.heriazone.hzlib.api.layer.IInternalRenderLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Composable layer-based renderer for robot entities.
 * <p>
 * <b>Architecture:</b> Replaces monolithic renderer with flexible layer system.
 * Layers are rendered in order added, allowing visual effect composition without
 * texture file explosion or renderer subclassing.
 * <p>
 * <b>Design Decision:</b> Layer composition over inheritance. Single renderer class
 * handles all robot variants by configuring different layer stacks. New visual effects
 * require new layer class, not new renderer.
 * <p>
 * <b>Performance:</b> Each layer adds one render pass. Typical robot uses 2-4 layers
 * (base, emissive, 1-2 overlays). Layers with shouldRender() returning false are
 * skipped with minimal overhead.
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * new LayerRenderPipeline(context, model)
 *     .addLayer(new BaseTextureLayer<>(this))
 *     .addLayer(new EmissiveLayer<>(this, EMISSIVE_TEXTURE))
 *     .addLayer(new DetailOverlayLayer<>(this, HEADPHONES_TEXTURE))
 *     .addLayer(new DynamicColorLayer<>(this, COLLAR_MASK, DynamicColorLayer::healthGradientColor));
 * }</pre>
 */
public class NativeRenderer<T extends NativeEntity & GeoEntity> extends GeoEntityRenderer<T> {

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
    public NativeRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
    } // Constructor: LayerRenderPipeline()

    /**
     * Creates renderer with specified model and shadow radius.
     *
     * @param context renderer provider context
     * @param model GeoModel for entity geometry and animations
     * @param shadowRadius shadow size (0.0 = no shadow, 0.5 = default)
     */
    public NativeRenderer(EntityRendererProvider.Context context, GeoModel<T> model, float shadowRadius) {
        super(context, model);
        this.shadowRadius = shadowRadius;
    } // Constructor: LayerRenderPipeline()

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
    public NativeRenderer<T> addLayer(IInternalRenderLayer<T> layer) {
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
    public NativeRenderer<T> clearLayers() {
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
        return entity.getCurrentTexture();
    } // getTextureLocation()

} // Class: LayerRenderPipeline
