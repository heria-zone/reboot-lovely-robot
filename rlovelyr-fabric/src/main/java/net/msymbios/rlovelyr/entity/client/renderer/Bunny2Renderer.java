package net.msymbios.rlovelyr.entity.client.renderer;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.client.layer.Bunny2Layer;
import net.msymbios.rlovelyr.entity.client.model.Bunny2Model;
import net.msymbios.rlovelyr.entity.custom.Bunny2Entity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import software.bernie.geckolib3.renderer.geo.GeoEntityRenderer;

import java.util.function.Function;

public class Bunny2Renderer extends GeoEntityRenderer<Bunny2Entity> {

    // -- Constructor --
    public Bunny2Renderer(EntityRenderDispatcher renderManager, EntityRendererRegistry.Context context) {
        super(renderManager, new Bunny2Model());
        this.shadowRadius = InternalMetric.ShadowRadius;
        addLayer(new Bunny2Layer(this));
    } // Constructor Bunny2Renderer ()

    // -- Inherited Methods --
    @Override
    public Identifier getTextureLocation(Bunny2Entity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class Bunny2Renderer