package net.msymbios.rlovelyr.entity.client;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.custom.BunnyEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import javax.annotation.Nullable;

public class BunnyRenderer extends GeoEntityRenderer<BunnyEntity> {

    // -- Constructor --
    public BunnyRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BunnyModel());
        this.shadowRadius = 0.4f;
    } // Constructor VanillaRenderer ()

    // -- Methods --
    @Override
    public Identifier getTextureLocation(BunnyEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

    @Override
    public RenderLayer getRenderType(BunnyEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    } // getRenderType ()

} // Class BunnyRenderer