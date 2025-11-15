package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.entity.client.layer.Bunny2Layer;
import net.msymbios.rlovelyr.entity.client.model.Bunny2Model;
import net.msymbios.rlovelyr.entity.custom.Bunny2Entity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class Bunny2Renderer extends GeoEntityRenderer<Bunny2Entity> {

    // -- Constructor --
    public Bunny2Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Bunny2Model());
        this.shadowRadius = LovelyRobotConfig.CLIENT.shadowRadius.get().floatValue();
        addLayer(new Bunny2Layer(this));
    } // Constructor Bunny2Renderer ()

    // -- Methods --
    @Override
    public @NotNull ResourceLocation getTextureLocation(Bunny2Entity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class Bunny2Renderer