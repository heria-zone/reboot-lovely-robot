package net.msymbios.rlovelyr.entity.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.custom.BunnyEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BunnyRenderer extends GeoEntityRenderer<BunnyEntity> {

    // -- Constructor --
    public BunnyRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BunnyModel());
        this.shadowRadius = InternalMetric.ShadowRadius;
    } // Constructor VanillaRenderer ()

    // -- Methods --
    @Override
    public Identifier getTextureLocation(BunnyEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class BunnyRenderer