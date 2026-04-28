package net.heriazone.lovelylib.common.entity;

import net.heriazone.hzlib.api.entity.InternalLayerRenderer;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.shared.LovelyResource;
import net.heriazone.hzlib.api.layer.BaseTextureLayer;
import net.heriazone.hzlib.api.layer.DynamicColorLayer;
import net.heriazone.hzlib.api.layer.HeadphoneOverlayLayer;
import net.heriazone.hzlib.framework.entity.enums.EntityState;
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
public class NativeRobotRenderer extends InternalLayerRenderer<NativeRobotEntity> {

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