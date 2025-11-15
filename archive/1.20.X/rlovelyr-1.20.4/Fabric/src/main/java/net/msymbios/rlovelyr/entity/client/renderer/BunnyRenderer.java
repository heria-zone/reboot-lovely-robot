package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.entity.client.layer.BunnyLayer;
import net.msymbios.rlovelyr.entity.client.model.BunnyModel;
import net.msymbios.rlovelyr.entity.custom.BunnyEntity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BunnyRenderer extends GeoEntityRenderer<BunnyEntity> {

    // -- Constructor --

    public BunnyRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BunnyModel());
        this.shadowRadius = LovelyRobotConfig.Client.ShadowRadius;
        addRenderLayer(new BunnyLayer(this));
    } // Constructor BunnyRenderer ()

    // -- Methods --

    @Override
    public @NotNull Identifier getTextureLocation(BunnyEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class BunnyRenderer