package net.msymbios.rlovelyr.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.msymbios.rlovelyr.entity.custom.BunnyEntity;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class BunnyRenderer extends GeoEntityRenderer<BunnyEntity> {

    // -- Constructor --
    public BunnyRenderer(EntityRendererManager renderManager) {
        super(renderManager, new BunnyModel());
        this.shadowRadius = 0.4f;
    } // Constructor Bunny2Renderer ()

    // -- Methods --
    @Override
    public ResourceLocation getTextureLocation(BunnyEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class BunnyRenderer