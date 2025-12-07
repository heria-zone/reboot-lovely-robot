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

/**
 * Emissive layer for glowing parts that ignore lighting.
 * <p>
 * <b>Architecture:</b> Renders specific model parts with full brightness regardless
 * of world lighting. Uses eyes render type for consistent glow effect.
 * <p>
 * <b>Design Decision:</b> Separate emissive texture allows glowing elements (eyes,
 * power indicators) without affecting main texture. Grayscale emissive texture
 * can be tinted for color variation.
 * <p>
 * <b>Performance:</b> Additional render pass, but emissive textures are typically
 * small (mostly transparent). Negligible performance impact.
 */
public class EmissiveLayer<T extends LovelyRobotEntity & GeoEntity> implements IInternalRenderLayer<T> {

    // -- Fields --

    private final GeoRenderer<T> renderer;
    private final ResourceLocation emissiveTexture;

    // -- Constructor --

    /**
     * Creates emissive layer with specified glow texture.
     *
     * @param renderer parent GeoRenderer managing this entity
     * @param emissiveTexture texture containing glowing parts (white = glow, transparent = no glow)
     */
    public EmissiveLayer(GeoRenderer<T> renderer, ResourceLocation emissiveTexture) {
        this.renderer = renderer;
        this.emissiveTexture = emissiveTexture;
    } // Constructor: EmissiveLayer()

    // -- IInternalRenderLayer Implementation --

    @Override
    public boolean shouldRender(T entity, float partialTick) {
        return emissiveTexture != null; // Only render if texture is provided
    } // shouldRender()

    @Override
    public void render(PoseStack poseStack, T entity, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {

        // Render with eyes type for full brightness
        RenderType emissiveRenderType = RenderType.eyes(emissiveTexture);

        renderer.reRender(
                bakedModel,
                poseStack,
                bufferSource,
                entity,
                emissiveRenderType,
                bufferSource.getBuffer(emissiveRenderType),
                partialTick,
                15728880, // Maximum brightness (0xF000F0)
                OverlayTexture.NO_OVERLAY,
                -1 // White tint
        );
    } // render()

} // Class: EmissiveLayer
