package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.entity.client.layer.NekoLayer;
import net.msymbios.rlovelyr.entity.client.model.NekoModel;
import net.msymbios.rlovelyr.entity.custom.NekoEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class NekoRenderer extends GeoEntityRenderer<NekoEntity>  {

    // -- Constructor --
    public NekoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new NekoModel());
        this.shadowRadius = InternalMetric.SHADOW_RADIUS.get();
        addRenderLayer(new NekoLayer(this));
    } // Constructor NekoRenderer ()

    // -- Methods --
    @Override
    public @NotNull ResourceLocation getTextureLocation(NekoEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class NekoRenderer