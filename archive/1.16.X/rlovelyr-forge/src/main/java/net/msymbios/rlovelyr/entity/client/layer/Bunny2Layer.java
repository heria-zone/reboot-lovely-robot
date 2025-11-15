package net.msymbios.rlovelyr.entity.client.layer;

import net.msymbios.rlovelyr.entity.custom.Bunny2Entity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.msymbios.rlovelyr.config.LovelyRobotResource;
import net.msymbios.rlovelyr.entity.enums.EntityState;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class Bunny2Layer extends GeoLayerRenderer<Bunny2Entity> {

    // -- Constructor --
    public Bunny2Layer(IGeoRenderer<Bunny2Entity> entityRendererIn) {
        super(entityRendererIn);
    } // Constructor Bunny2Layer ()

    // -- Inherited Methods --
    @Override
    public void render(MatrixStack poseStack, IRenderTypeBuffer bufferSource, int packedLightIn, Bunny2Entity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        RenderType armorRenderType = RenderType.armorCutoutNoCull(LovelyRobotResource.GENERAL_LAYER_EMPTY);
        if(entity.getAutoAttack()) {
            if(entity.getCurrentState() == EntityState.BaseDefense) armorRenderType = RenderType.armorCutoutNoCull(LovelyRobotResource.GENERAL_LAYER_BASE_DEFENSE);
            else armorRenderType = RenderType.armorCutoutNoCull(LovelyRobotResource.GENERAL_LAYER_AUTO_ATTACK);
        }
        getRenderer().render(getEntityModel().getModel(entity.getCurrentModel()), entity, partialTicks, armorRenderType, poseStack, bufferSource, bufferSource.getBuffer(armorRenderType), packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
    } // render ()

} // Class Bunny2Layer