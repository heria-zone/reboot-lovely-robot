package net.msymbios.rlovelyr.client.gui.properties.enums;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A flow.
 */
@Environment(EnvType.CLIENT)
public enum Flow {
    TOP_LEFT_LEFT_TO_RIGHT(Corner.TOP_LEFT, Direction.LEFT_TO_RIGHT, Direction.TOP_TO_BOTTOM),
    TOP_LEFT_TOP_TO_BOTTOM(Corner.TOP_LEFT, Direction.TOP_TO_BOTTOM, Direction.LEFT_TO_RIGHT),
    TOP_RIGHT_RIGHT_TO_LEFT(Corner.TOP_RIGHT, Direction.RIGHT_TO_LEFT, Direction.TOP_TO_BOTTOM),
    TOP_RIGHT_TOP_TO_BOTTOM(Corner.TOP_RIGHT, Direction.TOP_TO_BOTTOM, Direction.RIGHT_TO_LEFT),
    BOTTOM_LEFT_LEFT_TO_RIGHT(Corner.BOTTOM_LEFT, Direction.LEFT_TO_RIGHT, Direction.BOTTOM_TO_TOP),
    BOTTOM_LEFT_BOTTOM_TO_TOP(Corner.BOTTOM_LEFT, Direction.BOTTOM_TO_TOP, Direction.LEFT_TO_RIGHT),
    BOTTOM_RIGHT_RIGHT_TO_LEFT(Corner.BOTTOM_RIGHT, Direction.RIGHT_TO_LEFT, Direction.BOTTOM_TO_TOP),
    BOTTOM_RIGHT_BOTTOM_TO_TOP(Corner.BOTTOM_RIGHT, Direction.BOTTOM_TO_TOP, Direction.RIGHT_TO_LEFT);

    /**
     * The corner to start in.
     */
    public final Corner startCorner;

    /**
     * The primary flow direction.
     */
    public final Direction direction;

    /**
     * The overflow direction.
     */
    public final Direction overflowDirection;

    /**
     * @param startCorner       the corner to start in.
     * @param direction         the primary flow direction.
     * @param overflowDirection the overflow direction.
     */
    Flow(Corner startCorner, Direction direction, Direction overflowDirection) {
        this.startCorner = startCorner;
        this.direction = direction;
        this.overflowDirection = overflowDirection;
    }

} // Enum Flow