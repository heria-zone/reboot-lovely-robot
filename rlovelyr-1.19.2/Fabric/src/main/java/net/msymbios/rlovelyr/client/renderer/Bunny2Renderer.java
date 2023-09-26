package net.msymbios.rlovelyr.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.client.model.Bunny2Model;
import net.msymbios.rlovelyr.entity.custom.Bunny2Entity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class Bunny2Renderer extends GeoEntityRenderer<Bunny2Entity> {

    // -- Constructor --
    public Bunny2Renderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new Bunny2Model());
        this.shadowRadius = InternalMetric.ShadowRadius;
    } // Constructor Bunny2Renderer ()

    // -- Methods --
    @Override
    public Identifier getTextureResource(Bunny2Entity instance) {
        return instance.getTexture();
    } // getTextureResource ()

} // Class Bunny2Renderer