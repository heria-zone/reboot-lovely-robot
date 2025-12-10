package net.msymbios.llovelyr.lib.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity;
import net.msymbios.llovelyr.lib.rendering.LayerRenderContext;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

/**
 * NeoForge wrapper for base texture layer rendering.
 * <p>
 * <b>Architecture:</b> Thin wrapper around Common BaseTextureLayer that handles
 * NeoForge-specific GeckoLib rendering calls. Delegates business logic to Common module
 * while keeping GeckoLib dependencies in NeoForge loader.
 * <p>
 * <b>Design Decision:</b> Wrapper pattern maintains GeckoLib isolation while
 * extracting reusable base texture logic to Common module.
 */
public class BaseTextureLayer<T extends LovelyRobotEntity & GeoEntity> implements IInternalRenderLayer<T> {

    // -- Fields --

    private final GeoRenderer<T> renderer;
    private final net.msymbios.llovelyr.lib.rendering.BaseTextureLayer<T> commonLayer;

    // -- Constructor --

    /**
     * Creates NeoForge base texture layer wrapper.
     *
     * @param renderer parent GeoRenderer managing this entity
     */
    public BaseTextureLayer(GeoRenderer<T> renderer) {
        this.renderer = renderer;
        this.commonLayer = new net.msymbios.llovelyr.lib.rendering.BaseTextureLayer<>();
    } // Constructor: BaseTextureLayer()

    // -- IInternalRenderLayer Implementation --

    @Override
    public boolean shouldRender(T entity, float partialTick) {
        LayerRenderContext context = LayerRenderContext.texture(null, entity);
        return commonLayer.shouldRender(context);
    } // shouldRender()

    @Override
    public void render(PoseStack poseStack, T entity, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {

        // Base texture rendering is handled by the primary GeoEntityRenderer
        // This layer exists for consistency in the layer system
        // No additional rendering needed here
    } // render()

} // Class: BaseTextureLayer
