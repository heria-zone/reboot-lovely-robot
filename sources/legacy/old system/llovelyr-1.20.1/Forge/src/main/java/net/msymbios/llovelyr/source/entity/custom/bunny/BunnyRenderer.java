package net.msymbios.llovelyr.source.entity.custom.bunny;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.llovelyr.source.LovelyConfigs;
import net.msymbios.llovelyr.source.entity.custom.RobotEntity;
import net.msymbios.llovelyr.source.entity.custom.RobotModel;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * GeckoLib renderer for robot entities.
 * <p>
 * Texture resolution delegated to entity - allows runtime texture switching
 * based on variant and color without renderer changes.
 */
public class BunnyRenderer extends GeoEntityRenderer<RobotEntity> {

    // -- Constructor --

    public BunnyRenderer(EntityRendererProvider.Context context) {
        super(context, new RobotModel());
        this.shadowRadius = LovelyConfigs.ShadowRadius;
        addRenderLayer(new BunnyLayer(this));
    } // Constructor: BunnyRenderer()

    // -- Methods --

    @Override
    public @NotNull ResourceLocation getTextureLocation(RobotEntity entity) {
        return entity.getTexture();
    } // getTextureLocation()

} // Class: BunnyRenderer
