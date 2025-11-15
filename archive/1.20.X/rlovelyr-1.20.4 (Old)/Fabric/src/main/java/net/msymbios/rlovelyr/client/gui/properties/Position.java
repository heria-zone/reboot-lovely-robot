package net.msymbios.rlovelyr.client.gui.properties;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Represents a position.
 */
@Environment(EnvType.CLIENT)
public class Position {

    // -- Variables --

    /**
     * The x position.
     */
    public double x;

    /**
     * The y position.
     */
    public double y;

    // -- Constructors --

    /**
     * @param x the x position.
     * @param y the y position.
     */
    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    } // Constructor Position ()

    /**
     * Copy constructor.
     *
     * @param position the position.
     */
    public Position(Position position) {
        this(position.x, position.y);
    } // Constructor Position ()


} // Class Position