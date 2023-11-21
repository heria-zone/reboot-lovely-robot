package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.msymbios.rlovelyr.entity.client.layer.VanillaLayer;
import net.msymbios.rlovelyr.entity.client.model.VanillaModel;
import net.msymbios.rlovelyr.entity.custom.VanillaEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class VanillaRenderer extends GeoEntityRenderer<VanillaEntity> {

    // -- Constructor --
    public VanillaRenderer(EntityRendererManager renderManager) {
        super(renderManager, new VanillaModel());
        this.shadowRadius = InternalMetric.SHADOW_RADIUS;
        addLayer(new VanillaLayer(this));
    } // Constructor VanillaRenderer ()

    // -- Methods --
    @Override
    public ResourceLocation getTextureLocation(VanillaEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class VanillaRenderer