package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.entity.client.layer.BunnyLayer;
import net.msymbios.rlovelyr.entity.client.model.BunnyModel;
import net.msymbios.rlovelyr.entity.custom.BunnyEntity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class BunnyRenderer extends GeoEntityRenderer<BunnyEntity> {

    // -- Constructor --
    public BunnyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BunnyModel());
        this.shadowRadius = LovelyRobotConfig.CLIENT.shadowRadius.get().floatValue();
        addLayer(new BunnyLayer(this));
    } // Constructor Bunny2Renderer ()

    // -- Methods --
    @Override
    public @NotNull ResourceLocation getTextureLocation(BunnyEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class BunnyRenderer