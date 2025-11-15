package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.entity.client.layer.KitsuneLayer;
import net.msymbios.rlovelyr.entity.client.model.KitsuneModel;
import net.msymbios.rlovelyr.entity.custom.KitsuneEntity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class KitsuneRenderer extends GeoEntityRenderer<KitsuneEntity> {

    // -- Constructor --
    public KitsuneRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new KitsuneModel());
        this.shadowRadius = LovelyRobotConfig.CLIENT.shadowRadius.get().floatValue();
        addLayer(new KitsuneLayer(this));
    } // Constructor Bunny2Renderer ()

    // -- Methods --
    @Override
    public @NotNull ResourceLocation getTextureLocation(KitsuneEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class KitsuneRenderer