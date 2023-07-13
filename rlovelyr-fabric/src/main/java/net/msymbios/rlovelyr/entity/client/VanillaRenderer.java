package net.msymbios.rlovelyr.entity.client;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.custom.VanillaEntity;
import software.bernie.geckolib3.renderer.geo.GeoEntityRenderer;

public class VanillaRenderer extends GeoEntityRenderer<VanillaEntity> {

    // -- Constructor --
    public VanillaRenderer(EntityRenderDispatcher renderManager, EntityRendererRegistry.Context context) {
        super(renderManager, new VanillaModel());
        this.shadowRadius = 0.4f;
    } // Constructor VanillaRenderer ()

    // -- Methods --
    @Override
    public Identifier getTextureLocation(VanillaEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

    @Override
    public RenderLayer getRenderType(VanillaEntity instance, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, VertexConsumer buffer, int packedLight, Identifier texture) {
        return super.getRenderType(instance, partialTick, poseStack, bufferSource, buffer, packedLight, texture);
    } // getRenderType ()

} // Class VanillaRenderer