package net.msymbios.llovelyr.lib.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity;
import net.msymbios.llovelyr.lib.rendering.LayerRenderContext;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

/**
 * Adapter that bridges common layer system with GeckoLib-specific rendering.
 * <p>
 * <b>Architecture:</b> Thin wrapper that delegates business logic to common layer
 * implementations while handling GeckoLib-specific rendering calls. Maintains
 * the GeckoLib boundary by keeping all GeckoLib imports in loader modules.
 * <p>
 * <b>Design Decision:</b> Adapter pattern allows reusing common layer logic
 * across all loaders while preserving loader-specific rendering integration.
 * Each loader can have its own adapter implementation if needed.
 */
public class CommonLayerAdapter<T extends LovelyRobotEntity & GeoEntity> implements IInternalRenderLayer<T> {

    // -- Fields --

    private final net.msymbios.llovelyr.lib.rendering.IInternalRenderLayer<T> commonLayer;
    private final GeoRenderer<T> renderer;

    // -- Constructor --

    /**
     * Creates adapter for common layer with GeckoLib renderer.
     *
     * @param commonLayer common layer implementation with business logic
     * @param renderer GeoRenderer for this entity type
     */
    public CommonLayerAdapter(net.msymbios.llovelyr.lib.rendering.IInternalRenderLayer<T> commonLayer, GeoRenderer<T> renderer) {
        this.commonLayer = commonLayer;
        this.renderer = renderer;
    } // Constructor: CommonLayerAdapter()

    // -- IInternalRenderLayer Implementation --

    @Override
    public boolean shouldRender(T entity, float partialTick) {
        return commonLayer.shouldRender(entity, partialTick);
    } // shouldRender()

    @Override
    public void render(PoseStack poseStack, T entity, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {

        // Get render context from common layer
        LayerRenderContext context = commonLayer.getRenderContext(entity, partialTick);
        
        if (!context.shouldRender()) {
            return;
        }

        // Handle different layer types based on context
        if (context.texture() != null) {
            // Render textured layer
            RenderType layerRenderType = RenderType.armorCutoutNoCull(context.texture());
            
            int overlayCoords = context.useOverlay() ? packedOverlay : OverlayTexture.NO_OVERLAY;
            
            renderer.reRender(
                    bakedModel,
                    poseStack,
                    bufferSource,
                    entity,
                    layerRenderType,
                    bufferSource.getBuffer(layerRenderType),
                    partialTick,
                    packedLight,
                    overlayCoords,
                    context.color()
            );
        }
    } // render()

} // Class: CommonLayerAdapter