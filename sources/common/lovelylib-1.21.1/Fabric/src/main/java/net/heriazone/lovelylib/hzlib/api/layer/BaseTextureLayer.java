package net.heriazone.lovelylib.hzlib.api.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.heriazone.lovelylib.common.entity.LovelyRobotEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

/**
 * Fabric wrapper for base texture layer rendering.
 * <p>
 * <b>Architecture:</b> Thin wrapper that delegates business logic to Common module
 * while maintaining GeckoLib-specific rendering calls in Fabric module.
 * <p>
 * <b>Design Decision:</b> Preserves GeckoLib integration patterns while extracting
 * layer rendering logic to shared Common implementation.
 */
public class BaseTextureLayer<T extends LovelyRobotEntity & GeoEntity> implements IInternalRenderLayer<T> {

    // -- Fields --

    private final GeoRenderer<T> renderer;
    private final net.heriazone.lovelylib.hzlib.api.rendering.BaseTextureLayer commonLayer;

    // -- Constructor --

    /**
     * Creates base texture layer with parent renderer reference.
     *
     * @param renderer parent GeoRenderer managing this entity
     */
    public BaseTextureLayer(GeoRenderer<T> renderer) {
        this.renderer = renderer;
        this.commonLayer = new net.heriazone.lovelylib.hzlib.api.rendering.BaseTextureLayer<>();
    } // Constructor: BaseTextureLayer()

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
            // GeckoLib-specific rendering remains in Fabric module
            // Base texture rendering is handled by the main renderer
            // This layer exists for consistency in the layer system
        }
    } // render()

} // Class: BaseTextureLayer