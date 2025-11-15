package net.msymbios.rlovelyr.entity.client.layer;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.msymbios.rlovelyr.config.LovelyRobotResource;
import net.msymbios.rlovelyr.entity.enums.EntityState;
import net.msymbios.rlovelyr.entity.enums.EntityVariant;
import net.msymbios.rlovelyr.entity.internal.InternalEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class InternalEntityLayer extends GeoRenderLayer<GeoEntity> {

    // -- Constructor --
    public InternalEntityLayer(GeoRenderer<GeoEntity> entityRendererIn) {
        super(entityRendererIn);
    } // Constructor Bunny2Layer ()

    // -- Inherited Methods --
    @Override
    public void render(MatrixStack poseStack, GeoEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        InternalEntity entity = (InternalEntity) animatable;
        RenderLayer armorRenderType;
        
        if(entity.variant == EntityVariant.Bunny) armorRenderType = RenderLayer.getArmorCutoutNoCull(LovelyRobotResource.BUNNY_LAYER_EMPTY);
        else armorRenderType = RenderLayer.getArmorCutoutNoCull(LovelyRobotResource.GENERAL_LAYER_EMPTY);

        if(entity.getAutoAttack()) {
            if(entity.getCurrentState() == EntityState.Defense) {
                if(entity.variant == EntityVariant.Bunny) RenderLayer.getArmorCutoutNoCull(LovelyRobotResource.BUNNY_LAYER_BASE_DEFENSE);
                else if (entity.variant == EntityVariant.Kitsune) RenderLayer.getArmorCutoutNoCull(LovelyRobotResource.KITSUNE_LAYER_BASE_DEFENSE);
                else armorRenderType = RenderLayer.getArmorCutoutNoCull(LovelyRobotResource.GENERAL_LAYER_BASE_DEFENSE);
            } else {
                if(entity.variant == EntityVariant.Bunny) RenderLayer.getArmorCutoutNoCull(LovelyRobotResource.BUNNY_LAYER_AUTO_ATTACK);
                else if (entity.variant == EntityVariant.Kitsune) RenderLayer.getArmorCutoutNoCull(LovelyRobotResource.KITSUNE_LAYER_AUTO_ATTACK);
                else armorRenderType = RenderLayer.getArmorCutoutNoCull(LovelyRobotResource.GENERAL_LAYER_AUTO_ATTACK);
            }
        }
        getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, armorRenderType, bufferSource.getBuffer(armorRenderType), partialTick, packedLight, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
    } // render ()

} // Class InternalEntityLayer