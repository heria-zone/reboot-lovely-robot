package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.entity.client.layer.HoneyLayer;
import net.msymbios.rlovelyr.entity.client.model.HoneyModel;
import net.msymbios.rlovelyr.entity.custom.HoneyEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HoneyRenderer extends GeoEntityRenderer<HoneyEntity>  {

    // -- Constructor --
    public HoneyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HoneyModel());
        this.shadowRadius = InternalMetric.SHADOW_RADIUS.get();
        addRenderLayer(new HoneyLayer(this));
    } // Constructor HoneyRenderer ()

    // -- Methods --
    @Override
    public @NotNull ResourceLocation getTextureLocation(HoneyEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class HoneyRenderer