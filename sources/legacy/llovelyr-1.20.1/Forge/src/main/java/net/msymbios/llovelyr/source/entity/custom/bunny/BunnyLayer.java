package net.msymbios.llovelyr.source.entity.custom.bunny;

import net.msymbios.llovelyr.common.shared.LovelyResource;
import net.msymbios.llovelyr.source.entity.custom.RobotEntity;
import net.msymbios.llovelyr.source.entity.custom.RobotLayer;
import software.bernie.geckolib.renderer.GeoRenderer;

/**
 * GeckoLib render layer for additional visual effects.
 * <p>
 * Delegates to InternalLayer for variant-specific overlay rendering (glowing eyes,
 * damage tints, etc.). Actual rendering logic is in the parent class.
 */
public class BunnyLayer extends RobotLayer {

    // -- Constructor --

    public BunnyLayer(GeoRenderer<RobotEntity> renderer) {
        super(renderer);
        layerIdle = LovelyResource.BUNNY_LAYER_EMPTY;
        layerBaseDefence = LovelyResource.BUNNY_LAYER_BASE_DEFENSE;
        layerAutoAttack = LovelyResource.BUNNY_LAYER_AUTO_ATTACK;
    } // Constructor: BunnyLayer ()

} // Class: BunnyLayer
