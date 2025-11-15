package net.msymbios.rlovelyr.entity.internal;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.config.LovelyRobotResource;
import net.msymbios.rlovelyr.entity.internal.enums.EntityState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public abstract class InternalLayer<T extends InternalEntity & IAnimatable> extends GeoLayerRenderer<T> {

    // -- Variables --

    protected ResourceLocation layerIdle = LovelyRobotResource.GENERAL_LAYER_EMPTY;
    protected ResourceLocation layerBaseDefence = LovelyRobotResource.GENERAL_LAYER_BASE_DEFENSE;
    protected ResourceLocation layerAutoAttack = LovelyRobotResource.GENERAL_LAYER_AUTO_ATTACK;

    // -- Constructor --

    public InternalLayer(IGeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    } // Constructor InternalLayer ()

    // -- Inherited Methods --

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        RenderType armorRenderType = RenderType.armorCutoutNoCull(layerIdle);
        if(entity.getAutoAttack()) {
            if(entity.getCurrentState() == EntityState.Defense) armorRenderType = RenderType.armorCutoutNoCull(layerBaseDefence);
            else armorRenderType = RenderType.armorCutoutNoCull(layerAutoAttack);
        }
        getRenderer().render(getEntityModel().getModel(entity.getCurrentModel()), entity, partialTicks, armorRenderType, poseStack, bufferSource, bufferSource.getBuffer(armorRenderType), packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
    } // render ()

} // Class InternalLayer