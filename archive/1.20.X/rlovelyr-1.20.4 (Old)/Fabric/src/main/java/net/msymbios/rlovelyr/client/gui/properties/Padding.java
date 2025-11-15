package net.msymbios.rlovelyr.client.gui.properties;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A padding.
 */
@Environment(EnvType.CLIENT)
public class Padding {

    // -- Variables --

    /**
     * The left padding.
     */
    public double left;

    /**
     * The right padding.
     */
    public double right;

    /**
     * The top padding.
     */
    public double top;

    /**
     * The bottom padding.
     */
    public double bottom;

    // -- Constructors --

    /**
     * @param left   the left padding.
     * @param top    the top padding.
     * @param right  the right padding.
     * @param bottom the bottom padding.
     */
    public Padding(double left, double top, double right, double bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    } // Constructor Padding ()

    /**
     * @param all the padding on all sides.
     */
    public Padding(double all) {
        this.left = all;
        this.right = all;
        this.top = all;
        this.bottom = all;
    } // Constructor Padding ()

    public Padding(Padding padding) {
        this(padding.left, padding.right, padding.top, padding.bottom);
    } // Constructor Padding ()

} // Class Padding