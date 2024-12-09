package net.msymbios.rlovelyr.entity.enums;

/**
 * Enum used to identify the hands being used for actions and inventory slots.
 */
public enum BlocklingHand {

    // Values
    NONE,
    OFF,
    MAIN,
    BOTH;

    // -- Methods --

    /**
     * @param main whether to include the main hand.
     * @param off  whether to include the off hand.
     * @return the hand equivalent to the given booleans.
     */
    public static BlocklingHand fromBooleans(boolean main, boolean off) {
        if (main && off) return BOTH;
        else if (main) return MAIN;
        else if (off) return OFF;
        return NONE;
    } // fromBooleans ()

} // Class BlocklingHand