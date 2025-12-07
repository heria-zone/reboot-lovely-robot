package net.msymbios.llovelyr.lib.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

/**
 * Base texture layer rendering the primary robot appearance.
 * <p>
 * <b>Architecture:</b> Foundation layer that renders the main textured model.
 * Always renders first in layer stack. Texture is resolved from entity's
 * variant and color configuration.
 * <p>
 * <b>Design Decision:</b> Separated from renderer to allow texture swapping
 * without renderer changes. Entity controls texture selection based on state.
 */
public class BaseTextureLayer<T extends LovelyRobotEntity & GeoEntity> implements IInternalRenderLayer<T> {

    // -- Fields --

    private final GeoRenderer<T> renderer;

    // -- Constructor --

    /**
     * Creates base texture layer with parent renderer reference.
     *
     * @param renderer parent GeoRenderer managing this entity
     */
    public BaseTextureLayer(GeoRenderer<T> renderer) {
        this.renderer = renderer;
    } // Constructor: BaseTextureLayer()

    // -- IInternalRenderLayer Implementation --

    @Override
    public boolean shouldRender(T entity, float partialTick) {
        return true; // Base texture always renders
    } // shouldRender()

    @Override
    public void render(PoseStack poseStack, T entity, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {

        // Render base model with entity's texture - this is the primary render
        // No need to call anything here as the base render happens in GeoEntityRenderer
        // This layer exists for consistency in the layer system
    } // render()

} // Class: BaseTextureLayer
