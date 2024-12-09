package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.entity.client.layer.StingerLayer;
import net.msymbios.rlovelyr.entity.client.model.StingerModel;
import net.msymbios.rlovelyr.entity.custom.StingerEntity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class StingerRenderer extends GeoEntityRenderer<StingerEntity> {

    // -- Constructor --

    public StingerRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new StingerModel());
        this.shadowRadius = LovelyRobotConfig.Client.ShadowRadius;
        addRenderLayer(new StingerLayer(this));
    } // Constructor StingerRenderer ()

    // -- Methods --

    @Override
    public @NotNull Identifier getTextureLocation(StingerEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class StingerRenderer