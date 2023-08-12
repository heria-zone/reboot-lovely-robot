package net.msymbios.rlovelyr.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.msymbios.rlovelyr.entity.custom.KitsuneEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class KitsuneRenderer extends GeoEntityRenderer<KitsuneEntity> {

    // -- Constructor --
    public KitsuneRenderer(EntityRendererManager renderManager) {
        super(renderManager, new KitsuneModel());
        this.shadowRadius = InternalMetric.ShadowRadius;
    } // Constructor KitsuneRenderer ()

    // -- Methods --
    @Override
    public ResourceLocation getTextureLocation(KitsuneEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class KitsuneRenderer