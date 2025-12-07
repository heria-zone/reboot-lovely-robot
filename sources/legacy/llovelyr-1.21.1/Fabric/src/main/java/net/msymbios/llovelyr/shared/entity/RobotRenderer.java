package net.msymbios.llovelyr.shared.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.msymbios.llovelyr.common.Configs.SharedConfigs;
import net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity;
import net.msymbios.llovelyr.common.shared.LovelyResource;
import net.msymbios.llovelyr.framework.entity.enums.EntityState;
import net.msymbios.llovelyr.lib.entity.InternalLayerRenderer;
import net.msymbios.llovelyr.lib.entity.layer.BaseTextureLayer;
import net.msymbios.llovelyr.lib.entity.layer.DynamicColorLayer;
import net.msymbios.llovelyr.lib.entity.layer.HeadphoneOverlayLayer;

/**
 * Composable layer-based renderer for standard robot variants.
 * <p>
 * <b>Architecture:</b> Uses layer composition for visual effects. Base texture +
 * health collar + headphone overlay with conditional textures for different states.
 * <p>
 * <b>Design Decision:</b> Single renderer class handles multiple variants by
 * configuring layer stack. HeadphoneOverlayLayer replaces multiple DetailOverlayLayers
 * with intelligent texture swapping.
 */
public class RobotRenderer extends InternalLayerRenderer<RobotEntity> {

    // -- Constructor --

    public RobotRenderer(EntityRendererProvider.Context context) {
        super(context, new RobotModel(), SharedConfigs.Client.ShadowRadius);

        // Layer stack: Base texture → Health collar → Headphone overlay
        addLayer(new BaseTextureLayer<>(this));

        // Health indicator collar (green → yellow → red based on health)
        addLayer(new DynamicColorLayer<>(
                this,
                LovelyResource.GENERAL_LAYER_COLLAR_DYE,
                DynamicColorLayer::healthGradientColor
        ));

        // Headphone overlay with conditional textures (priority order: defense > attack > idle)
        addLayer(new HeadphoneOverlayLayer<>(this, LovelyResource.GENERAL_LAYER_EMPTY)
                .addConditionalTexture(
                        entity -> entity.getCurrentState() == EntityState.Defense,
                        LovelyResource.GENERAL_LAYER_BASE_DEFENSE
                )
                .addConditionalTexture(
                        LovelyRobotEntity::getAutoAttack,
                        LovelyResource.GENERAL_LAYER_AUTO_ATTACK
                )
        );
    } // Constructor: RobotRenderer()

} // Class: RobotRenderer