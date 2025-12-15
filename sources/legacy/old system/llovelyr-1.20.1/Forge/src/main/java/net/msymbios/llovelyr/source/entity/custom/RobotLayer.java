package net.msymbios.llovelyr.source.entity.custom;

import net.msymbios.llovelyr.common.entity.internal.InternalLayer;
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
