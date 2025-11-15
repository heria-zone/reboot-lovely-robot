package net.msymbios.rlovelyr.client.gui.properties;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Represents an offset.
 */
@Environment(EnvType.CLIENT)
public class Offset {

    // -- Variables --

    /**
     * The x offset.
     */
    public double x;

    /**
     * The y offset.
     */
    public double y;

    // -- Constructors --

    /**
     * @param x the offset.
     * @param y the offset.
     */
    public Offset(double x, double y) {
        this.x = x;
        this.y = y;
    } // Constructor Offset ()

    /**
     * Copy constructor.
     *
     * @param offset the offset.
     */
    public Offset(Offset offset) {
        this(offset.x, offset.y);
    } // Constructor Offset ()

} // Class Offset