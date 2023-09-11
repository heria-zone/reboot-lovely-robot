package net.msymbios.rlovelyr.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.client.model.DragonModel;
import net.msymbios.rlovelyr.entity.custom.DragonEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DragonRenderer extends GeoEntityRenderer<DragonEntity> {

    // -- Constructor --
    public DragonRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DragonModel());
        this.shadowRadius = InternalMetric.ShadowRadius;
    } // Constructor DragonRenderer ()

    // -- Methods --
    @Override
    public Identifier getTextureLocation(DragonEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class DragonRenderer