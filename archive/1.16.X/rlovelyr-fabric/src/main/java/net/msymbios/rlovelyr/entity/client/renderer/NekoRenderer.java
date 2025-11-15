package net.msymbios.rlovelyr.entity.client.renderer;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.client.layer.NekoLayer;
import net.msymbios.rlovelyr.entity.client.model.NekoModel;
import net.msymbios.rlovelyr.entity.custom.NekoEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import software.bernie.geckolib3.renderer.geo.GeoEntityRenderer;

public class NekoRenderer extends GeoEntityRenderer<NekoEntity> {

    // -- Constructor --
    public NekoRenderer(EntityRenderDispatcher renderManager, EntityRendererRegistry.Context context) {
        super(renderManager, new NekoModel());
        this.shadowRadius = InternalMetric.SHADOW_RADIUS;
        addLayer(new NekoLayer(this));
    } // Constructor NekoRenderer ()

    // -- Inherited Methods --
    @Override
    public Identifier getTextureLocation(NekoEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class NekoRenderer