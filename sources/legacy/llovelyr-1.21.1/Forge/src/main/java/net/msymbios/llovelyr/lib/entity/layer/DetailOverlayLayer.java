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

import java.util.function.Predicate;

/**
 * Forge wrapper for detail overlay layer rendering.
 * <p>
 * <b>Architecture:</b> Thin wrapper around Common BaseTextureLayer that handles
 * GeckoLib-specific rendering calls. Delegates business logic to Common module
 * while keeping GeckoLib dependencies in Forge loader.
 * <p>
 * <b>Design Decision:</b> Wrapper pattern maintains GeckoLib isolation while
 * extracting reusable overlay rendering logic to Common module.
 */
public class DetailOverlayLayer<T extends LovelyRobotEntity & GeoEntity> implements IInternalRenderLayer<T> {

    // -- Fields --

    private final GeoRenderer<T> renderer;
    private final net.msymbios.llovelyr.lib.rendering.BaseTextureLayer<T> commonLayer;

    // -- Constructors --

    /**
     * Creates Forge detail overlay layer wrapper that always renders.
     *
     * @param renderer parent GeoRenderer managing this entity
     * @param overlayTexture texture containing detail overlay
     */
    public DetailOverlayLayer(GeoRenderer<T> renderer, ResourceLocation overlayTexture) {
        this.renderer = renderer;
        this.commonLayer = new net.msymbios.llovelyr.lib.rendering.BaseTextureLayer<>(overlayTexture.toString());
    } // Constructor: DetailOverlayLayer()

    /**
     * Creates Forge detail overlay layer wrapper with conditional rendering.
     *
     * @param renderer parent GeoRenderer managing this entity
     * @param overlayTexture texture containing detail overlay
     * @param renderCondition predicate determining if overlay should render
     */
    public DetailOverlayLayer(GeoRenderer<T> renderer, ResourceLocation overlayTexture, Predicate<T> renderCondition) {
        this.renderer = renderer;
        this.commonLayer = new net.msymbios.llovelyr.lib.rendering.BaseTextureLayer<>(overlayTexture.toString(), renderCondition);
    } // Constructor: DetailOverlayLayer()

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

        // Get texture from common layer
        ResourceLocation overlayTexture = ResourceLocation.parse(commonLayer.getTexturePath());

        // Render overlay with cutout for transparency
        RenderType overlayRenderType = RenderType.armorCutoutNoCull(overlayTexture);

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

} // Class: DetailOverlayLayer
