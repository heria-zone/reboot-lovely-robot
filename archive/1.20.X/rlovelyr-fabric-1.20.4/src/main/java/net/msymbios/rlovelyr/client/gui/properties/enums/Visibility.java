package net.msymbios.rlovelyr.client.gui.properties.enums;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Represents the types of visibility for a control.
 */
@Environment(EnvType.CLIENT)
public enum Visibility {
    /**
     * The control is visible.
     */
    VISIBLE,

    /**
     * The control is invisible but will still take up space.
     */
    INVISIBLE,

    /**
     * The control is invisible and will not take up space.
     */
    COLLAPSED,
} // Enum Visibility