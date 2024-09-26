package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.entity.client.layer.HyperionLayer;
import net.msymbios.rlovelyr.entity.client.model.HyperionModel;
import net.msymbios.rlovelyr.entity.custom.HyperionEntity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HyperionRenderer extends GeoEntityRenderer<HyperionEntity> {

    // -- Constructor --

    public HyperionRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HyperionModel());
        this.shadowRadius = LovelyRobotConfig.Client.ShadowRadius;
        addRenderLayer(new HyperionLayer(this));
    } // Constructor HyperionRenderer ()

    // -- Methods --

    @Override
    public @NotNull Identifier getTextureLocation(HyperionEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class HyperionRenderer