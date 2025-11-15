package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.entity.client.layer.HoneyLayer;
import net.msymbios.rlovelyr.entity.client.model.HoneyModel;
import net.msymbios.rlovelyr.entity.custom.HoneyEntity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HoneyRenderer extends GeoEntityRenderer<HoneyEntity> {

    // -- Constructor --

    public HoneyRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HoneyModel());
        this.shadowRadius = LovelyRobotConfig.Client.ShadowRadius;
        addRenderLayer(new HoneyLayer(this));
    } // Constructor HoneyRenderer ()

    // -- Methods --

    @Override
    public @NotNull Identifier getTextureLocation(HoneyEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class HoneyRenderer