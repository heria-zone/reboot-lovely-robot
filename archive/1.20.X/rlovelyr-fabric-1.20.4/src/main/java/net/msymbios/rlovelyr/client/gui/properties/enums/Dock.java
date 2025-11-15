package net.msymbios.rlovelyr.client.gui.properties.enums;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Possible docking options.
 */
@Environment(EnvType.CLIENT)
public enum Dock {
    LEFT,
    TOP,
    RIGHT,
    BOTTOM,
    FILL,
} // Enum Dock