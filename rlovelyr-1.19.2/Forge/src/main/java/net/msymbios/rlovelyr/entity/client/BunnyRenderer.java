package net.msymbios.rlovelyr.entity.client;

import net.msymbios.rlovelyr.entity.custom.BunnyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class BunnyRenderer extends GeoEntityRenderer<BunnyEntity> {

    // -- Constructor --
    public BunnyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BunnyModel());
        this.shadowRadius = InternalMetric.ShadowRadius;
    } // Constructor BunnyRenderer ()

    // -- Methods --
    @Override
    public @NotNull ResourceLocation getTextureLocation(BunnyEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class BunnyRenderer