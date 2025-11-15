package net.msymbios.rlovelyr.common.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.config.LovelyRobotResource;
import net.msymbios.rlovelyr.entity.internal.RobotEntity;
import net.msymbios.rlovelyr.common.entity.enums.EntityState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public abstract class InternalLayer<T extends RobotEntity & GeoEntity> extends GeoRenderLayer<T> {

    // -- Variables --

    protected ResourceLocation layerIdle = LovelyRobotResource.GENERAL_LAYER_EMPTY;
    protected ResourceLocation layerBaseDefence = LovelyRobotResource.GENERAL_LAYER_BASE_DEFENSE;
    protected ResourceLocation layerAutoAttack = LovelyRobotResource.GENERAL_LAYER_AUTO_ATTACK;

    // -- Constructor --

    public InternalLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    } // Constructor InternalLayer ()

    // -- Inherited Methods --

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType armorRenderType = RenderType.armorCutoutNoCull(layerIdle);
        if(animatable.getAutoAttack()) {
            if(animatable.getCurrentState() == EntityState.Defense) armorRenderType = RenderType.armorCutoutNoCull(layerBaseDefence);
            else armorRenderType = RenderType.armorCutoutNoCull(layerAutoAttack);
        }
        getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, armorRenderType, bufferSource.getBuffer(armorRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
    } // render ()

} // Class InternalLayer