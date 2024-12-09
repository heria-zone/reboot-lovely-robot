package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.entity.client.layer.DragonLayer;
import net.msymbios.rlovelyr.entity.client.model.DragonModel;
import net.msymbios.rlovelyr.entity.custom.DragonEntity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DragonRenderer extends GeoEntityRenderer<DragonEntity> {

    // -- Constructor --

    public DragonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DragonModel());
        this.shadowRadius = LovelyRobotConfig.Client.ShadowRadius;
        addRenderLayer(new DragonLayer(this));
    } // Constructor DragonRenderer ()

    // -- Methods --

    @Override
    public @NotNull ResourceLocation getTextureLocation(DragonEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class DragonRenderer