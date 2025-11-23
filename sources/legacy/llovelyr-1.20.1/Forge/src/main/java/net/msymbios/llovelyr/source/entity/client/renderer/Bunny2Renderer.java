package net.msymbios.llovelyr.source.entity.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.llovelyr.source.configs.LovelyConfigs;
import net.msymbios.llovelyr.source.entity.client.layer.Bunny2Layer;
import net.msymbios.llovelyr.source.entity.client.model.Bunny2Model;
import net.msymbios.llovelyr.source.entity.custom.Bunny2Entity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Renderer for Bunny2 robot entity.
 * <p>
 * <b>Rendering:</b> Uses GeckoLib for 3D model rendering with custom layers.
 */
public class Bunny2Renderer extends GeoEntityRenderer<Bunny2Entity> {

    // -- Constructor --

    public Bunny2Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Bunny2Model());
        this.shadowRadius = LovelyConfigs.ShadowRadius;
        addRenderLayer(new Bunny2Layer(this));
    } // Bunny2Renderer

    // -- Methods --

    @Override
    public @NotNull ResourceLocation getTextureLocation(Bunny2Entity instance) {
        return instance.getTexture();
    } // getTextureLocation

} // Class: Bunny2Renderer
