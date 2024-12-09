package net.msymbios.rlovelyr.client.gui.properties.enums;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A direction.
 */
@Environment(EnvType.CLIENT)
public enum Direction {
    LEFT_TO_RIGHT(false),
    RIGHT_TO_LEFT(false),
    TOP_TO_BOTTOM(true),
    BOTTOM_TO_TOP(true);

    /**
     * Whether the direction is vertical.
     */
    public final boolean isVertical;

    /**
     * Whether the direction is horizontal.
     */
    public final boolean isHorizontal;

    /**
     * @param isVertical whether the direction is vertical.
     */
    Direction(boolean isVertical) {
        this.isVertical = isVertical;
        this.isHorizontal = !isVertical;
    }
} // Enum Direction