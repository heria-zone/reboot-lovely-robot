package net.msymbios.rlovelyr.client.gui.properties;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A margin.
 */
@Environment(EnvType.CLIENT)
public class Margin {

    // -- Variables --

    /**
     * The left margin.
     */
    public double left;

    /**
     * The right margin.
     */
    public double right;

    /**
     * The top margin.
     */
    public double top;

    /**
     * The bottom margin.
     */
    public double bottom;

    // -- Constructors --

    /**
     * @param left   the left margin.
     * @param right  the right margin.
     * @param top    the top margin.
     * @param bottom the bottom margin.
     */
    public Margin(double left, double right, double top, double bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    } // Constructor Margin ()

    /**
     * @param all the margin on all sides.
     */
    public Margin(double all) {
        this.left = all;
        this.right = all;
        this.top = all;
        this.bottom = all;
    } // Constructor Margin ()

    public Margin(Margin margin) {
        this(margin.left, margin.right, margin.top, margin.bottom);
    } // Constructor Margin ()

} // Class Margin