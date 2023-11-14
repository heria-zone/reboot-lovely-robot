package net.msymbios.rlovelyr.entity.client.renderer;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.client.layer.BunnyLayer;
import net.msymbios.rlovelyr.entity.client.model.BunnyModel;
import net.msymbios.rlovelyr.entity.custom.BunnyEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import software.bernie.geckolib3.renderer.geo.GeoEntityRenderer;

public class BunnyRenderer extends GeoEntityRenderer<BunnyEntity> {

    // -- Constructor --
    public BunnyRenderer(EntityRenderDispatcher renderManager, EntityRendererRegistry.Context context) {
        super(renderManager, new BunnyModel());
        this.shadowRadius = InternalMetric.ShadowRadius;
        addLayer(new BunnyLayer(this));
    } // Constructor BunnyRenderer ()

    // -- Inherited Methods --
    @Override
    public Identifier getTextureLocation(BunnyEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class BunnyRenderer