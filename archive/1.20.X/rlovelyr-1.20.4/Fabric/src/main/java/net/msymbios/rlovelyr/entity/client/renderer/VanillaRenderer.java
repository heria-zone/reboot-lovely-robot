package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.entity.client.layer.VanillaLayer;
import net.msymbios.rlovelyr.entity.client.model.VanillaModel;
import net.msymbios.rlovelyr.entity.custom.VanillaEntity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class VanillaRenderer extends GeoEntityRenderer<VanillaEntity> {

    // -- Constructor --

    public VanillaRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new VanillaModel());
        this.shadowRadius = LovelyRobotConfig.Client.ShadowRadius;
        addRenderLayer(new VanillaLayer(this));
    } // Constructor VanillaRenderer ()

    // -- Methods --

    @Override
    public @NotNull Identifier getTextureLocation(VanillaEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class VanillaRenderer