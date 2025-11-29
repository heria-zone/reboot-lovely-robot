package net.msymbios.llovelyr.source.entity.custom.bunny2;

import net.msymbios.llovelyr.common.entity.internal.InternalLayer;
import software.bernie.geckolib.renderer.GeoRenderer;

/**
 * Render layer for Bunny2 robot entity.
 * <p>
 * <b>Layer:</b> Handles additional rendering like held items and armor.
 */
public class Bunny2Layer extends InternalLayer<Bunny2Entity> {

    // -- Constructor --

    public Bunny2Layer(GeoRenderer<Bunny2Entity> entityRendererIn) {
        super(entityRendererIn);
    } // Bunny2Layer

} // Class: Bunny2Layer
