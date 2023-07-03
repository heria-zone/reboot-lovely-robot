package net.msymbios.rlovelyr.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.msymbios.rlovelyr.entity.custom.VanillaEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class VanillaRenderer extends GeoEntityRenderer<VanillaEntity> {

    // -- Constructor --
    public VanillaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new VanillaModel());
        this.shadowRadius = 0.4f;
    } // Constructor Bunny2Renderer ()

    // -- Methods --
    @Override
    public @NotNull ResourceLocation getTextureLocation(VanillaEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

    @Override
    public RenderType getRenderType(VanillaEntity animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return super.getRenderType(animatable, partialTick, poseStack, bufferSource, buffer, packedLight, texture);
    } // getRenderType ()

} // Class VanillaRenderer