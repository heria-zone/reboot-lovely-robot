package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.entity.client.layer.EmpyriumLayer;
import net.msymbios.rlovelyr.entity.client.model.EmpyriumModel;
import net.msymbios.rlovelyr.entity.custom.EmpyriumEntity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EmpyriumRenderer extends GeoEntityRenderer<EmpyriumEntity> {

    // -- Constructor --

    public EmpyriumRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new EmpyriumModel());
        this.shadowRadius = LovelyRobotConfig.Client.ShadowRadius;
        addRenderLayer(new EmpyriumLayer(this));
    } // Constructor EmpyriumRenderer ()

    // -- Methods --

    @Override
    public @NotNull Identifier getTextureLocation(EmpyriumEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class EmpyriumRenderer