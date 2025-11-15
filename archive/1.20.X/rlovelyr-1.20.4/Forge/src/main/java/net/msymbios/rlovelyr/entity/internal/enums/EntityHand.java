package net.msymbios.rlovelyr.entity.internal.enums;

/**
 * Enum used to identify the hands being used for actions and inventory slots.
 */
public enum EntityHand {

    // Values
    NONE,
    OFF,
    MAIN,
    BOTH;

    // -- Methods --

    /**
     * @param main whether to include the main hand.
     * @param off  whether to include the offhand.
     * @return the hand equivalent to the given booleans.
     */
    public static EntityHand fromBooleans(boolean main, boolean off) {
        if (main && off) return BOTH;
        else if (main) return MAIN;
        else if (off) return OFF;
        return NONE;
    } // fromBooleans ()

} // Class EntityHand