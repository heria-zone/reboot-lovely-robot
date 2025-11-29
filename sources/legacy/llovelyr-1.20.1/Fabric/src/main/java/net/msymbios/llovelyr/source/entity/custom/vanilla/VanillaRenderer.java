package net.msymbios.llovelyr.source.entity.custom.vanilla;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.msymbios.llovelyr.source.LovelyConfigs;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Renderer for Vanilla robot entity (Fabric).
 * <p>
 * <b>Rendering:</b> Uses GeckoLib for 3D model rendering with custom layers.
 */
public class VanillaRenderer extends GeoEntityRenderer<VanillaEntity> {

    // -- Constructor --

    public VanillaRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new VanillaModel());
        this.shadowRadius = LovelyConfigs.Client.ShadowRadius;
        addRenderLayer(new VanillaLayer(this));
    } // VanillaRenderer

    // -- Methods --

    @Override
    public @NotNull Identifier getTextureLocation(VanillaEntity instance) {
        return instance.getTexture();
    } // getTextureLocation

} // Class: VanillaRenderer
