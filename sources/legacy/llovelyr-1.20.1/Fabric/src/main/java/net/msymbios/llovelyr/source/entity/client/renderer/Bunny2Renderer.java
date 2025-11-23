package net.msymbios.llovelyr.source.entity.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.msymbios.llovelyr.source.configs.LovelyConfigs;
import net.msymbios.llovelyr.source.entity.client.layer.Bunny2Layer;
import net.msymbios.llovelyr.source.entity.client.model.Bunny2Model;
import net.msymbios.llovelyr.source.entity.custom.Bunny2Entity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Renderer for Bunny2 robot entity (Fabric).
 * <p>
 * <b>Rendering:</b> Uses GeckoLib for 3D model rendering with custom layers.
 */
public class Bunny2Renderer extends GeoEntityRenderer<Bunny2Entity> {

    // -- Constructor --

    public Bunny2Renderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new Bunny2Model());
        this.shadowRadius = LovelyConfigs.Common.ShadowRadius;
        addRenderLayer(new Bunny2Layer(this));
    } // Bunny2Renderer

    // -- Methods --

    @Override
    public @NotNull Identifier getTextureLocation(Bunny2Entity instance) {
        return instance.getTexture();
    } // getTextureLocation

} // Class: Bunny2Renderer
