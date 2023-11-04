package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.client.model.KitsuneModel;
import net.msymbios.rlovelyr.entity.custom.KitsuneEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class KitsuneRenderer extends GeoEntityRenderer<KitsuneEntity> {

    // -- Constructor --
    public KitsuneRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new KitsuneModel());
        this.shadowRadius = InternalMetric.ShadowRadius;
    } // Constructor VanillaRenderer ()

    // -- Methods --
    @Override
    public Identifier getTextureLocation(KitsuneEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class KitsuneRenderer