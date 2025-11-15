package net.msymbios.rlovelyr.entity.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.entity.client.layer.KitsuneLayer;
import net.msymbios.rlovelyr.entity.client.model.KitsuneModel;
import net.msymbios.rlovelyr.entity.custom.KitsuneEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class KitsuneRenderer extends GeoEntityRenderer<KitsuneEntity> {

    // -- Constructor --
    public KitsuneRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new KitsuneModel());
        this.shadowRadius = InternalMetric.SHADOW_RADIUS.get();
        addLayer(new KitsuneLayer(this));
    } // Constructor KitsuneRenderer ()

    // -- Methods --
    @Override
    public @NotNull ResourceLocation getTextureLocation(KitsuneEntity instance) {
        return instance.getTexture();
    } // getTextureLocation ()

} // Class KitsuneRenderer