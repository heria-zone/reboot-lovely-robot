package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.entity.client.layer.DragonLayer;
import net.msymbios.rlovelyr.entity.client.model.DragonModel;
import net.msymbios.rlovelyr.entity.custom.DragonEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class DragonRenderer extends GeoEntityRenderer<DragonEntity> {

    // -- Constructor --
    public DragonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DragonModel());
        this.shadowRadius = InternalMetric.SHADOW_RADIUS.get();
        addLayer(new DragonLayer(this));
    } // Constructor DragonRenderer ()

    // -- Methods --
    @Override
    public @NotNull ResourceLocation getTextureLocation(DragonEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class DragonRenderer