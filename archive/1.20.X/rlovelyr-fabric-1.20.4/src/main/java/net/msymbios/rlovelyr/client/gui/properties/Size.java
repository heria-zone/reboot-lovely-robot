package net.msymbios.rlovelyr.client.gui.properties;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Represents a size.
 */
@Environment(EnvType.CLIENT)
public class Size {

    // -- Variables --

    /**
     * The width.
     */
    public double width;

    /**
     * The height.
     */
    public double height;

    // -- Constructors --

    /**
     * @param width  the width.
     * @param height the height.
     */
    public Size(double width, double height) {
        this.width = width;
        this.height = height;
    } // Constructor Size ()

    /**
     * Copy constructor.
     *
     * @param size the size.
     */
    public Size(Size size) {
        this(size.width, size.height);
    } // Constructor Size ()

} // Class Size