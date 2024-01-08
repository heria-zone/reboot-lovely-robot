package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.client.layer.KitsuneLayer;
import net.msymbios.rlovelyr.entity.client.model.KitsuneModel;
import net.msymbios.rlovelyr.entity.custom.KitsuneEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class KitsuneRenderer extends GeoEntityRenderer<KitsuneEntity> {

    // -- Constructor --
    public KitsuneRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new KitsuneModel());
        this.shadowRadius = InternalMetric.SHADOW_RADIUS;
        addLayer(new KitsuneLayer(this));
    } // Constructor KitsuneRenderer ()

    // -- Methods --
    @Override
    public Identifier getTextureResource(KitsuneEntity instance) {
        return instance.getTexture();
    } // getTextureResource ()

} // Class KitsuneRenderer