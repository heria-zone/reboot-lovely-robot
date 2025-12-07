package net.msymbios.llovelyr.lib.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.util.function.Predicate;

/**
 * Static detail overlay layer for accessories and decorative elements.
 * <p>
 * <b>Architecture:</b> Renders additional details (headphones, armor, accessories)
 * as texture overlays. Supports conditional rendering based on entity state.
 * <p>
 * <b>Design Decision:</b> Overlays as separate textures avoid texture file explosion
 * when combining multiple accessories. Each accessory is independent layer.
 * <p>
 * <b>Usage Example:</b> Headphones overlay, armor plating, decorative patterns.
 */
public class DetailOverlayLayer<T extends LovelyRobotEntity & GeoEntity> implements IInternalRenderLayer<T> {

    // -- Fields --

    private final GeoRenderer<T> renderer;
    private final ResourceLocation overlayTexture;
    private final Predicate<T> renderCondition;

    // -- Constructors --

    /**
     * Creates detail overlay that always renders.
     *
     * @param renderer parent GeoRenderer managing this entity
     * @param overlayTexture texture containing detail overlay
     */
    public DetailOverlayLayer(GeoRenderer<T> renderer, ResourceLocation overlayTexture) {
        this(renderer, overlayTexture, entity -> true);
    } // Constructor: DetailOverlayLayer()

    /**
     * Creates detail overlay with conditional rendering.
     *
     * @param renderer parent GeoRenderer managing this entity
     * @param overlayTexture texture containing detail overlay
     * @param renderCondition predicate determining if overlay should render
     */
    public DetailOverlayLayer(GeoRenderer<T> renderer, ResourceLocation overlayTexture, Predicate<T> renderCondition) {
        this.renderer = renderer;
        this.overlayTexture = overlayTexture;
        this.renderCondition = renderCondition;
    } // Constructor: DetailOverlayLayer()

    // -- IInternalRenderLayer Implementation --

    @Override
    public boolean shouldRender(T entity, float partialTick) {
        return overlayTexture != null && renderCondition.test(entity);
    } // shouldRender()

    @Override
    public void render(PoseStack poseStack, T entity, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {

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
