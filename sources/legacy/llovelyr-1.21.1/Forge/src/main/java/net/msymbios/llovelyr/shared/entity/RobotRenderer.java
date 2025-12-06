package net.msymbios.llovelyr.shared.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.llovelyr.common.Configs.SharedConfigs;
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

    public RobotRenderer(EntityRendererProvider.Context context) {
        super(context, new RobotModel());
        this.shadowRadius = SharedConfigs.Client.ShadowRadius;
        addRenderLayer(new RobotLayer(this));
    } // Constructor: RobotRenderer()

    // -- Methods --

    @Override
    public @NotNull ResourceLocation getTextureLocation(RobotEntity entity) {
        return entity.getTexture();
    } // getTextureLocation()

} // Class: RobotRenderer