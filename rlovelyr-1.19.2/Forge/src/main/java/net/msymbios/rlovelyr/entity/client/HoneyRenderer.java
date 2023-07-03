package net.msymbios.rlovelyr.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.msymbios.rlovelyr.entity.custom.HoneyEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class HoneyRenderer extends GeoEntityRenderer<HoneyEntity> {

    // -- Constructor --
    public HoneyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HoneyModel());
        this.shadowRadius = 0.4f;
    } // Constructor Bunny2Renderer ()

    // -- Methods --
    @Override
    public @NotNull ResourceLocation getTextureLocation(HoneyEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

    @Override
    public RenderType getRenderType(HoneyEntity animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return super.getRenderType(animatable, partialTick, poseStack, bufferSource, buffer, packedLight, texture);
    } // getRenderType ()

} // Class HoneyRenderer