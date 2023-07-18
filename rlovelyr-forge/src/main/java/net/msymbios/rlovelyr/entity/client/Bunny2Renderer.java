package net.msymbios.rlovelyr.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.msymbios.rlovelyr.entity.custom.Bunny2Entity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class Bunny2Renderer extends GeoEntityRenderer<Bunny2Entity> {

    // -- Constructor --
    public Bunny2Renderer(EntityRendererManager renderManager) {
        super(renderManager, new Bunny2Model());
        this.shadowRadius = InternalMetric.ShadowRadius;
    } // Constructor Bunny2Renderer ()

    // -- Methods --
    @Override
    public ResourceLocation getTextureLocation(Bunny2Entity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class Bunny2Renderer