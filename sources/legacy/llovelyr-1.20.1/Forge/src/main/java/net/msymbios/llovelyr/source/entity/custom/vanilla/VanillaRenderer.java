package net.msymbios.llovelyr.source.entity.custom.vanilla;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.llovelyr.source.LovelyConfigs;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Renderer for Vanilla robot entity.
 * <p>
 * <b>Rendering:</b> Uses GeckoLib for 3D model rendering with custom layers.
 */
public class VanillaRenderer extends GeoEntityRenderer<VanillaEntity> {

    // -- Constructor --

    public VanillaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new VanillaModel());
        this.shadowRadius = LovelyConfigs.ShadowRadius;
        addRenderLayer(new VanillaLayer(this));
    } // VanillaRenderer

    // -- Methods --

    @Override
    public @NotNull ResourceLocation getTextureLocation(VanillaEntity instance) {
        return instance.getTexture();
    } // getTextureLocation

} // Class: VanillaRenderer
