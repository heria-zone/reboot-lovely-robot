package net.heriazone.lovelylib.common.entity;

import net.heriazone.hzlib.api.entity.NativeRenderer;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.shared.LovelyResource;
import net.heriazone.hzlib.framework.entity.enums.EntityState;
import net.heriazone.hzlib.api.layer.BaseTextureLayer;
import net.heriazone.hzlib.api.layer.DynamicColorLayer;
import net.heriazone.hzlib.api.layer.HeadphoneOverlayLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

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
public class NativeRobotRenderer extends NativeRenderer<NativeRobotEntity> {

    // -- Constructor --

    public NativeRobotRenderer(EntityRendererProvider.Context context) {
        super(context, new NativeRobotModel(), SharedConfigs.Client.ShadowRadius);

        // Layer stack: Base texture → Health collar → Kitsune-specific headphone overlay
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
                        RobotEntity::getAutoAttack,
                        LovelyResource.GENERAL_LAYER_AUTO_ATTACK
                )
        );
    } // Constructor: NativeRobotRenderer()

} // Class: NativeRobotRenderer