package net.msymbios.rlovelyr.entity.client.layer;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.msymbios.rlovelyr.config.LovelyRobotResource;
import net.msymbios.rlovelyr.entity.custom.NekoEntity;
import net.msymbios.rlovelyr.entity.enums.EntityState;
import software.bernie.geckolib3.renderer.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderer.geo.IGeoRenderer;

public class NekoLayer extends GeoLayerRenderer<NekoEntity> {

    // -- Constructor --
    public NekoLayer(IGeoRenderer<NekoEntity> entityRendererIn) {
        super(entityRendererIn);
    } // Constructor NekoLayer ()

    // -- Inherited Methods --
    @Override
    public void render(MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLightIn, NekoEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        RenderLayer armorRenderType = RenderLayer.getArmorCutoutNoCull(LovelyRobotResource.GENERAL_LAYER_EMPTY);
        if(entity.getAutoAttack()) {
            if(entity.getCurrentState() == EntityState.BaseDefense) armorRenderType = RenderLayer.getArmorCutoutNoCull(LovelyRobotResource.GENERAL_LAYER_BASE_DEFENSE);
            else armorRenderType = RenderLayer.getArmorCutoutNoCull(LovelyRobotResource.GENERAL_LAYER_AUTO_ATTACK);
        }
        getRenderer().render(getEntityModel().getModel(entity.getCurrentModel()), entity, partialTicks, armorRenderType, poseStack, bufferSource, bufferSource.getBuffer(armorRenderType), packedLightIn, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
    } // render ()

} // Class NekoLayer