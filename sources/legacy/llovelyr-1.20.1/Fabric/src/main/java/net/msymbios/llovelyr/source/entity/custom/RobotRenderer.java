package net.msymbios.llovelyr.source.entity.custom;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.msymbios.llovelyr.source.LovelyConfigs;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * GeckoLib renderer for robot entities.
 * <p>
 * Texture resolution delegated to entity - allows runtime texture switching
 * based on variant and color without renderer changes.
 */
public class RobotRenderer extends GeoEntityRenderer<RobotEntity> {

    // -- Constructor --

    public RobotRenderer(EntityRendererFactory.Context context) {
        super(context, new RobotModel());
        this.shadowRadius = LovelyConfigs.Client.ShadowRadius;
        addRenderLayer(new RobotLayer(this));
    } // Constructor: RobotRenderer()

    // -- Methods --

    @Override
    public @NotNull Identifier getTextureLocation(RobotEntity entity) {
        return entity.getTexture();
    } // getTextureLocation()

} // Class: RobotRenderer