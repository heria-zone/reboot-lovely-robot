package net.msymbios.rlovelyr.client.gui.properties;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Represents a scale.
 */
@Environment(EnvType.CLIENT)
public class Scale {

    // -- Variables --

    /**
     * The scale in the x direction.
     */
    public double x;

    /**
     * The scale in the y direction.
     */
    public double y;

    // -- Constructors --

    /**
     * @param x the scale in the x direction.
     * @param y the scale in the y direction.
     */
    public Scale(double x, double y) {
        this.x = x;
        this.y = y;
    } // Constructor Scale ()

    /**
     * Copy constructor.
     *
     * @param scale the scale.
     */
    public Scale(Scale scale) {
        this(scale.x, scale.y);
    } // Constructor Scale ()

} // Class Scale