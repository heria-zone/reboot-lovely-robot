package net.msymbios.rlovelyr.entity.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.msymbios.rlovelyr.config.LovelyRobotResource;
import net.msymbios.rlovelyr.entity.custom.BunnyEntity;
import net.msymbios.rlovelyr.entity.enums.EntityState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class BunnyLayer extends GeoRenderLayer<BunnyEntity> {

    // -- Constructor --
    public BunnyLayer(GeoRenderer<BunnyEntity> entityRendererIn) {
        super(entityRendererIn);
    } // Constructor BunnyLayer ()

    // -- Inherited Methods --
    @Override
    public void render(PoseStack poseStack, BunnyEntity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType armorRenderType = RenderType.armorCutoutNoCull(LovelyRobotResource.BUNNY_LAYER_EMPTY);
        if(animatable.getAutoAttack()) {
            if(animatable.getCurrentState() == EntityState.BaseDefense) armorRenderType = RenderType.armorCutoutNoCull(LovelyRobotResource.BUNNY_LAYER_BASE_DEFENSE);
            else armorRenderType = RenderType.armorCutoutNoCull(LovelyRobotResource.BUNNY_LAYER_AUTO_ATTACK);
        }
        getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, armorRenderType, bufferSource.getBuffer(armorRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
    } // render ()

} // Class BunnyLayer