package net.msymbios.llovelyr.shared.entity;

import net.msymbios.llovelyr.lib.entity.InternalLayer;
import software.bernie.geckolib.renderer.GeoRenderer;

/**
 * GeckoLib render layer for additional visual effects.
 * <p>
 * Delegates to InternalLayer for variant-specific overlay rendering (glowing eyes,
 * damage tints, etc.). Actual rendering logic is in the parent class.
 */
public class RobotLayer extends InternalLayer<RobotEntity> {

    // -- Constructor --

    public RobotLayer(GeoRenderer<RobotEntity> renderer) {
        super(renderer);
    } // Constructor: RobotLayer()

} // Class: RobotLayer