package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.entity.client.layer.NekoLayer;
import net.msymbios.rlovelyr.entity.client.model.NekoModel;
import net.msymbios.rlovelyr.entity.custom.NekoEntity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class NekoRenderer extends GeoEntityRenderer<NekoEntity> {

    // -- Constructor --
    public NekoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new NekoModel());
        this.shadowRadius = LovelyRobotConfig.CLIENT.shadowRadius.get().floatValue();
        addLayer(new NekoLayer(this));
    } // Constructor Bunny2Renderer ()

    // -- Methods --
    @Override
    public @NotNull ResourceLocation getTextureLocation(NekoEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class NekoRenderer