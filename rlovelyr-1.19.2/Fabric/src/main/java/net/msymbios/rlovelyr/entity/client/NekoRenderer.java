package net.msymbios.rlovelyr.entity.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.custom.NekoEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class NekoRenderer extends GeoEntityRenderer<NekoEntity> {

    // -- Constructor --
    public NekoRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new NekoModel());
        this.shadowRadius = InternalMetric.ShadowRadius;
    } // Constructor VanillaRenderer ()

    // -- Methods --
    @Override
    public Identifier getTextureResource(NekoEntity instance) {
        return instance.getTexture();
    } // getTextureResource ()

} // Class NekoRenderer