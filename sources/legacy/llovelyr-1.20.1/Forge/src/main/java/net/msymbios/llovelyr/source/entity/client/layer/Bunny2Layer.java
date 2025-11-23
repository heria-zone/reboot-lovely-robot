package net.msymbios.llovelyr.source.entity.client.layer;

import net.msymbios.llovelyr.common.entity.internal.InternalLayer;
import net.msymbios.llovelyr.source.entity.custom.Bunny2Entity;
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
