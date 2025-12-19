package net.heriazone.llovelyr.entity;

import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.LovelyRobotEntity;
import net.heriazone.lovelylib.common.shared.LovelyResource;
import net.heriazone.lovelylib.hzlib.api.entity.InternalLayerRenderer;
import net.heriazone.lovelylib.hzlib.api.layer.BaseTextureLayer;
import net.heriazone.lovelylib.hzlib.api.layer.DynamicColorLayer;
import net.heriazone.lovelylib.hzlib.api.layer.HeadphoneOverlayLayer;
import net.heriazone.lovelylib.hzlib.framework.entity.enums.EntityState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * Composable layer-based renderer for standard robot variants.
 * <p>
 * <b>Architecture:</b> Uses layer composition for visual effects. Base texture +
 * mode indicator overlays (auto-attack, base defense) without texture proliferation.
 * <p>
 * <b>Design Decision:</b> Single renderer class handles multiple variants by
 * configuring layer stack. Variant-specific renderers only needed for unique
 * visual requirements (e.g., Kitsune tail system).
 */
public class RobotRenderer extends InternalLayerRenderer<RobotEntity> {

    // -- Constructor --

    public RobotRenderer(EntityRendererProvider.Context context) {
        super(context, new RobotModel(), SharedConfigs.Client.ShadowRadius);

        // Layer stack: Base texture → Health collar → Mode indicators
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