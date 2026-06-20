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
 * Composable layer-based renderer for Bunny variant.
 * <p>
 * <b>Architecture:</b> Uses Bunny-specific mode indicator textures while maintaining
 * standard layer composition pattern.
 */
public class BunnyRenderer extends NativeRenderer<NativeRobotEntity> {

    // -- Constructor --

    public BunnyRenderer(EntityRendererProvider.Context context) {
        super(context, new NativeRobotModel(), SharedConfigs.Client.ShadowRadius);

        // Layer stack: Base texture → Health collar → Kitsune-specific headphone overlay
        addLayer(new BaseTextureLayer<>(this));

        // Health indicator collar (green → yellow → red based on health)
        addLayer(new DynamicColorLayer<>(
                this,
                LovelyResource.BUNNY_LAYER_COLLAR_DYE,
                DynamicColorLayer::healthGradientColor
        ));

        // Headphone overlay with conditional textures (Bunny variant - priority: defense > attack > idle)
        addLayer(new HeadphoneOverlayLayer<>(this, LovelyResource.BUNNY_LAYER_EMPTY)
                .addConditionalTexture(
                        entity -> entity.getAutoAttack() && entity.getCurrentState() == EntityState.Defense,
                        LovelyResource.BUNNY_LAYER_BASE_DEFENSE
                )
                .addConditionalTexture(
                        RobotEntity::getAutoAttack,
                        LovelyResource.BUNNY_LAYER_AUTO_ATTACK
                )
        );
    } // Constructor: BunnyRenderer()

} // Class: BunnyRenderer