package net.msymbios.llovelyr.source.entity.custom;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.msymbios.llovelyr.common.shared.LovelyResource;
import net.msymbios.llovelyr.framework.entity.enums.EntityState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

/**
 * GeckoLib render layer for additional visual effects.
 * <p>
 * Delegates to InternalLayer for variant-specific overlay rendering (glowing eyes,
 * damage tints, etc.). Actual rendering logic is in the parent class.
 */
public class BunnyLayer extends RobotLayer {

    // -- Constructor --

    public BunnyLayer(GeoRenderer<RobotEntity> renderer) {
        super(renderer);
    } // Constructor: BunnyLayer ()

    // -- Methods --

    @Override
    public void render(MatrixStack poseStack, RobotEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderLayer armorRenderType = RenderLayer.getArmorCutoutNoCull(LovelyResource.BUNNY_LAYER_EMPTY);
        if(animatable.getAutoAttack()) {
            if(animatable.getCurrentState() == EntityState.Defense) armorRenderType = RenderLayer.getArmorCutoutNoCull(LovelyResource.BUNNY_LAYER_BASE_DEFENSE);
            else armorRenderType = RenderLayer.getArmorCutoutNoCull(LovelyResource.BUNNY_LAYER_AUTO_ATTACK);
        }
        getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, armorRenderType, bufferSource.getBuffer(armorRenderType), partialTick, packedLight, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
    } // render ()

} // Class: RobotLayer