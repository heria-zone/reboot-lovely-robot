package net.msymbios.rlovelyr.client.gui.properties.enums;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Represents a way in which a row/column in a grid can be defined.
 */
@Environment(EnvType.CLIENT)
public enum GridDefinition {
    /**
     * The row/column will be defined by a fixed size.
     */
    FIXED,

    /**
     * The row/column will be defined by the size of the largest child.
     */
    AUTO,

    /**
     * The row/column will be defined by a ratio of the remaining size.
     */
    RATIO,
} // Enum GridDefinition