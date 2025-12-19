package net.heriazone.lovelylib.hzlib.api.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.heriazone.lovelylib.common.entity.LovelyRobotEntity;
import net.heriazone.lovelylib.hzlib.api.rendering.LayerRenderContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.util.function.Predicate;

/**
 * Forge wrapper for headphone overlay layer rendering.
 * <p>
 * <b>Architecture:</b> Thin wrapper around Common HeadphoneOverlayLayer that handles
 * GeckoLib-specific rendering calls. Delegates business logic to Common module
 * while keeping GeckoLib dependencies in Forge loader.
 * <p>
 * <b>Design Decision:</b> Wrapper pattern maintains GeckoLib isolation while
 * extracting reusable conditional overlay logic to Common module.
 */
public class HeadphoneOverlayLayer<T extends LovelyRobotEntity & GeoEntity> implements IInternalRenderLayer<T> {

    // -- Fields --

    private final GeoRenderer<T> renderer;
    private final net.heriazone.lovelylib.hzlib.api.rendering.HeadphoneOverlayLayer<T> commonLayer;

    // -- Constructor --

    /**
     * Creates Forge headphone overlay layer wrapper.
     *
     * @param renderer parent GeoRenderer managing this entity
     * @param defaultTexture texture to render when no conditions match
     */
    public HeadphoneOverlayLayer(GeoRenderer<T> renderer, ResourceLocation defaultTexture) {
        this.renderer = renderer;
        this.commonLayer = new net.heriazone.lovelylib.hzlib.api.rendering.HeadphoneOverlayLayer<>(defaultTexture.toString());
    } // Constructor: HeadphoneOverlayLayer()

    // -- Configuration --

    /**
     * Adds conditional texture that renders when condition is met.
     *
     * @param condition predicate determining if this texture should render
     * @param texture texture to render when condition is true
     * @return this layer for method chaining
     */
    public HeadphoneOverlayLayer<T> addConditionalTexture(Predicate<T> condition, ResourceLocation texture) {
        commonLayer.addConditionalTexture(condition, texture.toString());
        return this;
    } // addConditionalTexture()

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

        // Get selected texture from common layer
        LayerRenderContext context = LayerRenderContext.texture(null, entity);
        String selectedTexturePath = commonLayer.getSelectedTexture(context);
        ResourceLocation selectedTexture = ResourceLocation.parse(selectedTexturePath);

        // Render with selected texture
        RenderType overlayRenderType = RenderType.armorCutoutNoCull(selectedTexture);

        renderer.reRender(
                bakedModel,
                poseStack,
                bufferSource,
                entity,
                overlayRenderType,
                bufferSource.getBuffer(overlayRenderType),
                partialTick,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                -1 // White tint
        );
    } // render()

} // Class: HeadphoneOverlayLayer