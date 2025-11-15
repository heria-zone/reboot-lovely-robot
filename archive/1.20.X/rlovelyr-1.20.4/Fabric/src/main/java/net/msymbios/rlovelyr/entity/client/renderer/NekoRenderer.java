package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.entity.client.layer.NekoLayer;
import net.msymbios.rlovelyr.entity.client.model.NekoModel;
import net.msymbios.rlovelyr.entity.custom.NekoEntity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class NekoRenderer extends GeoEntityRenderer<NekoEntity> {

    // -- Constructor --

    public NekoRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new NekoModel());
        this.shadowRadius = LovelyRobotConfig.Client.ShadowRadius;
        addRenderLayer(new NekoLayer(this));
    } // Constructor NekoRenderer ()

    // -- Methods --

    @Override
    public @NotNull Identifier getTextureLocation(NekoEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class NekoRenderer