package net.msymbios.rlovelyr.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.msymbios.rlovelyr.entity.custom.VanillaEntity;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class VanillaRenderer extends GeoEntityRenderer<VanillaEntity> {

    // -- Constructor --
    public VanillaRenderer(EntityRendererManager renderManager) {
        super(renderManager, new VanillaModel());
        this.shadowRadius = 0.4f;
    } // Constructor Bunny2Renderer ()

    // -- Methods --
    @Override
    public ResourceLocation getTextureLocation(VanillaEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class VanillaRenderer