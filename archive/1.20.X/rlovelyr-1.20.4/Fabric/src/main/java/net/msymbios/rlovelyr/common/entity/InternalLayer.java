package net.msymbios.rlovelyr.common.entity;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.common.entity.enums.EntityState;
import net.msymbios.rlovelyr.config.LovelyRobotResource;
import net.msymbios.rlovelyr.entity.internal.RobotEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public abstract class InternalLayer<T extends RobotEntity & GeoEntity> extends GeoRenderLayer<T> {

    // -- Variables --

    protected Identifier layerIdle = LovelyRobotResource.GENERAL_LAYER_EMPTY;
    protected Identifier layerBaseDefence = LovelyRobotResource.GENERAL_LAYER_BASE_DEFENSE;
    protected Identifier layerAutoAttack = LovelyRobotResource.GENERAL_LAYER_AUTO_ATTACK;

    // -- Constructor --

    public InternalLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    } // Constructor InternalLayer ()

    // -- Inherited Methods --

    @Override
    public void render(MatrixStack poseStack, T animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderLayer armorRenderType = RenderLayer.getArmorCutoutNoCull(layerIdle);
        if(animatable.getAutoAttack()) {
            if(animatable.getCurrentState() == EntityState.Defense) armorRenderType = RenderLayer.getArmorCutoutNoCull(layerBaseDefence);
            else armorRenderType = RenderLayer.getArmorCutoutNoCull(layerAutoAttack);
        }
        getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, armorRenderType, bufferSource.getBuffer(armorRenderType), partialTick, packedLight, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
    } // render ()

} // Class InternalLayer