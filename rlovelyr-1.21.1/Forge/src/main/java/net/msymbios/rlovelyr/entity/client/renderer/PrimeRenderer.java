package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.entity.client.layer.PrimeLayer;
import net.msymbios.rlovelyr.entity.client.model.PrimeModel;
import net.msymbios.rlovelyr.entity.custom.PrimeEntity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PrimeRenderer extends GeoEntityRenderer<PrimeEntity> {

    // -- Constructor --

    public PrimeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PrimeModel());
        this.shadowRadius = LovelyRobotConfig.Client.ShadowRadius;
        addRenderLayer(new PrimeLayer(this));
    } // Constructor PrimeRenderer ()

    // -- Methods --

    @Override
    public @NotNull ResourceLocation getTextureLocation(PrimeEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class PrimeRenderer