package net.msymbios.llovelyr.shared.entity.kitsune;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.llovelyr.common.Configs.SharedConfigs;
import net.msymbios.llovelyr.shared.entity.RobotEntity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * GeckoLib renderer for robot entities.
 * <p>
 * Texture resolution delegated to entity - allows runtime texture switching
 * based on variant and color without renderer changes.
 */
public class KitsuneRenderer extends GeoEntityRenderer<RobotEntity> {

    // -- Constructor --

    public KitsuneRenderer(EntityRendererProvider.Context context) {
        super(context, new KitsuneModel());
        this.shadowRadius = SharedConfigs.Client.ShadowRadius;
        addRenderLayer(new KitsuneLayer(this));
    } // Constructor: KitsuneRenderer()

    // -- Methods --

    @Override
    public @NotNull ResourceLocation getTextureLocation(RobotEntity entity) {
        return entity.getTexture();
    } // getTextureLocation()

} // Class: KitsuneRenderer