package net.msymbios.llovelyr.shared.entity.kitsune;

import net.msymbios.llovelyr.common.shared.LovelyResource;
import net.msymbios.llovelyr.shared.entity.RobotEntity;
import net.msymbios.llovelyr.shared.entity.RobotLayer;
import software.bernie.geckolib.renderer.GeoRenderer;

/**
 * GeckoLib render layer for additional visual effects.
 * <p>
 * Delegates to InternalLayer for variant-specific overlay rendering (glowing eyes,
 * damage tints, etc.). Actual rendering logic is in the parent class.
 */
public class KitsuneLayer extends RobotLayer {

    // -- Constructor --

    public KitsuneLayer(GeoRenderer<RobotEntity> renderer) {
        super(renderer);
        layerIdle = LovelyResource.GENERAL_LAYER_EMPTY;
        layerBaseDefence = LovelyResource.KITSUNE_LAYER_BASE_DEFENSE;
        layerAutoAttack = LovelyResource.KITSUNE_LAYER_AUTO_ATTACK;
    } // Constructor: KitsuneLayer ()

} // Class: KitsuneLayer