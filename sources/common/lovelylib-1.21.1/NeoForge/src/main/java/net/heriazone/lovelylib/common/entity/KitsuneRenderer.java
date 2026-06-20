package net.heriazone.lovelylib.common.entity;

import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.shared.LovelyResource;
import net.heriazone.hzlib.api.entity.NativeRenderer;
import net.heriazone.hzlib.api.layer.BaseTextureLayer;
import net.heriazone.hzlib.api.layer.DynamicColorLayer;
import net.heriazone.hzlib.api.layer.HeadphoneOverlayLayer;
import net.heriazone.hzlib.framework.entity.enums.EntityState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * Composable layer-based renderer for Kitsune variant.
 * <p>
 * <b>Architecture:</b> Uses KitsuneModel for tail visibility logic while maintaining
 * layer composition for visual effects. Kitsune-specific mode indicator textures.
 */
public class KitsuneRenderer extends NativeRenderer<NativeRobotEntity> {

    // -- Constructor --

    public KitsuneRenderer(EntityRendererProvider.Context context) {
        super(context, new KitsuneModel(), SharedConfigs.Client.ShadowRadius);

        // Layer stack: Base texture → Health collar → Kitsune-specific headphone overlay
        addLayer(new BaseTextureLayer<>(this));

        // Health indicator collar (green → yellow → red based on health)
        addLayer(new DynamicColorLayer<>(
                this,
                LovelyResource.KITSUNE_LAYER_COLLAR_DYE,
                DynamicColorLayer::healthGradientColor
        ));

        // Headphone overlay with conditional textures (Kitsune variant - priority: defense > attack > idle)
        addLayer(new HeadphoneOverlayLayer<>(this, LovelyResource.GENERAL_LAYER_EMPTY)
                .addConditionalTexture(
                        entity -> entity.getCurrentState() == EntityState.Defense,
                        LovelyResource.KITSUNE_LAYER_BASE_DEFENSE
                )
                .addConditionalTexture(
                        RobotEntity::getAutoAttack,
                        LovelyResource.KITSUNE_LAYER_AUTO_ATTACK
                )
        );
    } // Constructor: KitsuneRenderer()

} // Class: KitsuneRenderer