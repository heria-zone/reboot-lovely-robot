package net.msymbios.rlovelyr.entity.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.custom.DragonEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class DragonRenderer extends GeoEntityRenderer<DragonEntity> {

    // -- Constructor --
    public DragonRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DragonModel());
        this.shadowRadius = InternalMetric.ShadowRadius;
    } // Constructor DragonRenderer ()

    // -- Inherited Methods --
    @Override
    public Identifier getTextureLocation(DragonEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class DragonRenderer