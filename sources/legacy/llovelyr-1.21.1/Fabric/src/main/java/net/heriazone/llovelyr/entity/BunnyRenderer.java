package net.heriazone.llovelyr.entity;

import net.heriazone.lovelylib.common.entity.LovelyRobotEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.shared.LovelyResource;
import net.heriazone.lovelylib.hzlib.api.entity.InternalLayerRenderer;
import net.heriazone.lovelylib.hzlib.api.layer.*;
import net.heriazone.lovelylib.hzlib.framework.entity.enums.EntityState;

/**
 * Composable layer-based renderer for Bunny variant.
 * <p>
 * <b>Architecture:</b> Uses Bunny-specific mode indicator textures while maintaining
 * standard layer composition pattern.
 */
public class BunnyRenderer extends InternalLayerRenderer<RobotEntity> {

    // -- Constructor --

    public BunnyRenderer(EntityRendererProvider.Context context) {
        super(context, new RobotModel(), SharedConfigs.Client.ShadowRadius);
        // Layer stack: Base texture → Health collar → Bunny-specific headphone overlay

        // Health indicator collar (green → yellow → red based on health)
        addLayer(new DynamicColorLayer<>(
                this,
                LovelyResource.BUNNY_LAYER_COLLAR_DYE,
                DynamicColorLayer::healthGradientColor
        ));

        // Headphone overlay with conditional textures (Bunny variant - priority: defense > attack > idle)
        addLayer(new HeadphoneOverlayLayer<>(this, LovelyResource.BUNNY_LAYER_EMPTY)
                .addConditionalTexture(
                        entity -> entity.getCurrentState() == EntityState.Defense,
                        LovelyResource.BUNNY_LAYER_BASE_DEFENSE
                )
                .addConditionalTexture(
                        LovelyRobotEntity::getAutoAttack,
                        LovelyResource.BUNNY_LAYER_AUTO_ATTACK
                )
        );
    } // Constructor: BunnyRenderer()

} // Class: BunnyRenderer