package net.msymbios.llovelyr.shared.entity.bunny;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.msymbios.llovelyr.common.Configs.SharedConfigs;
import net.msymbios.llovelyr.common.shared.LovelyResource;
import net.msymbios.llovelyr.framework.entity.enums.EntityState;
import net.msymbios.llovelyr.lib.entity.InternalLayerRenderer;
import net.msymbios.llovelyr.lib.entity.layer.BaseTextureLayer;
import net.msymbios.llovelyr.lib.entity.layer.DynamicColorLayer;
import net.msymbios.llovelyr.lib.entity.layer.HeadphoneOverlayLayer;
import net.msymbios.llovelyr.shared.entity.RobotEntity;
import net.msymbios.llovelyr.shared.entity.RobotModel;

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

        // Layer stack: Base texture → Health collar → Bunny-specific mode indicators
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
                        entity -> entity.getAutoAttack(),
                        LovelyResource.BUNNY_LAYER_AUTO_ATTACK
                )
        );
    } // Constructor: BunnyRenderer()

} // Class: BunnyRenderer
