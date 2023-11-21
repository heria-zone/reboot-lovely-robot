package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.msymbios.rlovelyr.entity.client.layer.DragonLayer;
import net.msymbios.rlovelyr.entity.client.model.DragonModel;
import net.msymbios.rlovelyr.entity.custom.DragonEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class DragonRenderer extends GeoEntityRenderer<DragonEntity> {

    // -- Constructor --
    public DragonRenderer(EntityRendererManager renderManager) {
        super(renderManager, new DragonModel());
        this.shadowRadius = InternalMetric.SHADOW_RADIUS;
        addLayer(new DragonLayer(this));
    } // Constructor DragonRenderer ()

    // -- Methods --
    @Override
    public ResourceLocation getTextureLocation(DragonEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class DragonRenderer