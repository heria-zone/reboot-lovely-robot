package net.msymbios.llovelyr.framework.entity.enums;

/**
 * Identifies which hands are active for robot actions and inventory management.
 * <p>
 * <b>Architecture:</b> Provides type-safe hand identification for equipment rendering,
 * item usage, and combat systems. Replaces boolean pairs with clearer semantic meaning.
 * <p>
 * <b>Design Decision:</b> Four states (NONE, OFF, MAIN, BOTH) cover all hand combinations
 * without requiring bitwise operations or multiple boolean parameters.
 */
public enum EntityHand {

    // -- Hand States --

    /** No hands active. Used when robot is unarmed or hands are disabled. */
    NONE,
    
    /** Only offhand active. Used for shields, torches, or secondary tools. */
    OFF,
    
    /** Only main hand active. Default for most tools and weapons. */
    MAIN,
    
    /** Both hands active. Used for two-handed weapons or dual-wielding. */
    BOTH;

    // -- Utility Methods --

    /**
     * Converts boolean hand flags to semantic hand state.
     * <p>
     * <b>Usage:</b> Simplifies hand state determination from separate boolean checks,
     * improving code readability and reducing conditional complexity.
     * 
     * @param main whether main hand is active
     * @param off whether offhand is active
     * @return corresponding EntityHand state
     */
    public static EntityHand fromBooleans(boolean main, boolean off) {
        if (main && off) {
            return BOTH;
        } else if (main) {
            return MAIN;
        } else if (off) {
            return OFF;
        }
        return NONE;
    } // fromBooleans ()

} // Enum: EntityHand
