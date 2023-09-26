package net.msymbios.rlovelyr.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.client.model.KitsuneModel;
import net.msymbios.rlovelyr.entity.custom.KitsuneEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class KitsuneRenderer extends GeoEntityRenderer<KitsuneEntity> {

    // -- Constructor --
    public KitsuneRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new KitsuneModel());
        this.shadowRadius = InternalMetric.ShadowRadius;
    } // Constructor KitsuneRenderer ()

    // -- Methods --
    @Override
    public Identifier getTextureResource(KitsuneEntity instance) {
        return instance.getTexture();
    } // getTextureResource ()

} // Class KitsuneRenderer