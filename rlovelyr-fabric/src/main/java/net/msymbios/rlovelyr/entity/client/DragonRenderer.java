package net.msymbios.rlovelyr.entity.client;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.entity.custom.DragonEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import software.bernie.geckolib3.renderer.geo.GeoEntityRenderer;

public class DragonRenderer extends GeoEntityRenderer<DragonEntity> {

    // -- Constructor --
    public DragonRenderer(EntityRenderDispatcher renderManager, EntityRendererRegistry.Context context) {
        super(renderManager, new DragonModel());
        this.shadowRadius = InternalMetric.ShadowRadius;
    } // Constructor DragonRenderer ()

    // -- Inherited Methods --
    @Override
    public Identifier getTextureLocation(DragonEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class DragonRenderer