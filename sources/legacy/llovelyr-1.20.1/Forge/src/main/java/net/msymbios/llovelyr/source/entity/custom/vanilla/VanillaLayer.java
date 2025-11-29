package net.msymbios.llovelyr.source.entity.client.layer;

import net.msymbios.llovelyr.common.entity.internal.InternalLayer;
import net.msymbios.llovelyr.source.entity.custom.VanillaEntity;
import software.bernie.geckolib.renderer.GeoRenderer;

/**
 * Render layer for Vanilla robot entity.
 * <p>
 * <b>Layer:</b> Handles additional rendering like held items and armor.
 */
public class VanillaLayer extends InternalLayer<VanillaEntity> {

    // -- Constructor --

    public VanillaLayer(GeoRenderer<VanillaEntity> entityRendererIn) {
        super(entityRendererIn);
    } // VanillaLayer

} // Class: VanillaLayer
