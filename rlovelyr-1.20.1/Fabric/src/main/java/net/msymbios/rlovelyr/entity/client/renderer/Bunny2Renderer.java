package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.client.model.Bunny2Model;
import net.msymbios.rlovelyr.entity.custom.Bunny2Entity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class Bunny2Renderer extends GeoEntityRenderer<Bunny2Entity> {

    // -- Constructor --
    public Bunny2Renderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new Bunny2Model());
        this.shadowRadius = InternalMetric.SHADOW_RADIUS;
    } // Constructor VanillaRenderer ()

    // -- Methods --
    @Override
    public Identifier getTextureLocation(Bunny2Entity instance) {
        return instance.getTexture();
    } // getTextureResource ()

} // Class Bunny2Renderer