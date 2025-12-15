package net.msymbios.llovelyr.shared.entity.kitsune;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.msymbios.llovelyr.common.Configs.SharedConfigs;
import net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity;
import net.msymbios.llovelyr.common.shared.LovelyResource;
import net.msymbios.llovelyr.framework.entity.enums.EntityState;
import net.msymbios.llovelyr.lib.entity.InternalLayerRenderer;
import net.msymbios.llovelyr.lib.entity.layer.BaseTextureLayer;
import net.msymbios.llovelyr.lib.entity.layer.DynamicColorLayer;
import net.msymbios.llovelyr.lib.entity.layer.HeadphoneOverlayLayer;
import net.msymbios.llovelyr.shared.entity.RobotEntity;

/**
 * Composable layer-based renderer for Kitsune variant.
 * <p>
 * <b>Architecture:</b> Uses KitsuneModel for tail visibility logic while maintaining
 * layer composition for visual effects. Kitsune-specific mode indicator textures.
 */
public class KitsuneRenderer extends InternalLayerRenderer<RobotEntity> {

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
                        LovelyRobotEntity::getAutoAttack,
                        LovelyResource.KITSUNE_LAYER_AUTO_ATTACK
                )
        );
    } // Constructor: KitsuneRenderer()

} // Class: KitsuneRenderer