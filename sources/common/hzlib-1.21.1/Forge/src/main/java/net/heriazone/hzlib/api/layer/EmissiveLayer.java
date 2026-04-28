package net.heriazone.hzlib.api.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.heriazone.hzlib.api.entity.InternalEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

/**
 * Forge wrapper for emissive layer rendering.
 * <p>
 * <b>Architecture:</b> Thin wrapper around Common EmissiveLayer that handles
 * GeckoLib-specific rendering calls. Delegates business logic to Common module
 * while keeping GeckoLib dependencies in Forge loader.
 * <p>
 * <b>Design Decision:</b> Wrapper pattern maintains GeckoLib isolation while
 * extracting reusable emissive rendering logic to Common module.
 */
public class EmissiveLayer<T extends InternalEntity & GeoEntity> implements IInternalRenderLayer<T> {

    // -- Fields --

    private final GeoRenderer<T> renderer;
    private final net.heriazone.hzlib.api.rendering.EmissiveLayer<T> commonLayer;

    // -- Constructor --

    /**
     * Creates Forge emissive layer wrapper.
     *
     * @param renderer parent GeoRenderer managing this entity
     * @param emissiveTexture texture containing glowing parts
     */
    public EmissiveLayer(GeoRenderer<T> renderer, ResourceLocation emissiveTexture) {
        this.renderer = renderer;
        this.commonLayer = new net.heriazone.hzlib.api.rendering.EmissiveLayer<>(emissiveTexture);
    } // Constructor: EmissiveLayer()

    // -- IInternalRenderLayer Implementation --

    @Override
    public boolean shouldRender(T entity, float partialTick) {
        return commonLayer.shouldRender(entity, partialTick);
    } // shouldRender()

    @Override
    public void render(PoseStack poseStack, T entity, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {

        // Get texture from common layer
        ResourceLocation emissiveTexture = ResourceLocation.parse(commonLayer.getTexturePath());

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