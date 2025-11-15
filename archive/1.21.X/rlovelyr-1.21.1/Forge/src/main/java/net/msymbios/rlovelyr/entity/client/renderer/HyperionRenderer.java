package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.entity.client.layer.HyperionLayer;
import net.msymbios.rlovelyr.entity.client.model.HyperionModel;
import net.msymbios.rlovelyr.entity.custom.HyperionEntity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HyperionRenderer extends GeoEntityRenderer<HyperionEntity> {

    // -- Constructor --

    public HyperionRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HyperionModel());
        this.shadowRadius = LovelyRobotConfig.Client.ShadowRadius;
        addRenderLayer(new HyperionLayer(this));
    } // Constructor HyperionRenderer ()

    // -- Methods --

    @Override
    public @NotNull ResourceLocation getTextureLocation(HyperionEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class HyperionRenderer