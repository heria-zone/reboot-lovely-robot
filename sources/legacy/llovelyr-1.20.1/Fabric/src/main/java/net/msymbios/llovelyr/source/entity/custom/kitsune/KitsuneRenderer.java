package net.msymbios.llovelyr.source.entity.custom.kitsune;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.msymbios.llovelyr.source.LovelyConfigs;
import net.msymbios.llovelyr.source.entity.custom.RobotEntity;
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

    public KitsuneRenderer(EntityRendererFactory.Context context) {
        super(context, new KitsuneModel());
        this.shadowRadius = LovelyConfigs.Client.ShadowRadius;
        addRenderLayer(new KitsuneLayer(this));
    } // Constructor: KitsuneRenderer()

    // -- Methods --

    @Override
    public @NotNull Identifier getTextureLocation(RobotEntity entity) {
        return entity.getTexture();
    } // getTextureLocation()

} // Class: KitsuneRenderer