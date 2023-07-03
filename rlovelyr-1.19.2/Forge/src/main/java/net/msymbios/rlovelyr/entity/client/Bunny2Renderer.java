package net.msymbios.rlovelyr.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.msymbios.rlovelyr.entity.custom.Bunny2Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class Bunny2Renderer extends GeoEntityRenderer<Bunny2Entity> {

    // -- Constructor --
    public Bunny2Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Bunny2Model());
        this.shadowRadius = 0.4f;
    } // Constructor Bunny2Renderer ()

    // -- Methods --
    @Override
    public @NotNull ResourceLocation getTextureLocation(Bunny2Entity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

    @Override
    public RenderType getRenderType(Bunny2Entity animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return super.getRenderType(animatable, partialTick, poseStack, bufferSource, buffer, packedLight, texture);
    } // getRenderType ()

} // Class Bunny2Renderer