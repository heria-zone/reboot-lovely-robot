package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.entity.client.layer.VanillaLayer;
import net.msymbios.rlovelyr.entity.client.model.VanillaModel;
import net.msymbios.rlovelyr.entity.custom.VanillaEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class VanillaRenderer extends GeoEntityRenderer<VanillaEntity> {

    // -- Constructor --
    public VanillaRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new VanillaModel());
        this.shadowRadius = InternalMetric.SHADOW_RADIUS;
        addRenderLayer(new VanillaLayer(this));
    } // Constructor VanillaRenderer ()

    // -- Methods --
    @Override
    public Identifier getTextureLocation(VanillaEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class VanillaRenderer