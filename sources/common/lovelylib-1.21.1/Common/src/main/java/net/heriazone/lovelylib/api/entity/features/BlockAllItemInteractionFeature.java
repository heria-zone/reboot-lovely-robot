package net.heriazone.lovelylib.api.entity.features;

/**
 * Marker — robot accepts no item interactions of any kind.
 * <p>
 * <b>Dispatch:</b> {@code RobotEntity.handleItemInteraction()} checks for this feature
 * as the first guard in its dispatch chain and returns {@code InteractionResult.PASS}
 * immediately, bypassing dye, Blaze Rod, food, and all other standard handlers.
 * The robot can still be tamed via command or spawn if a TamingFeature is configured.
 * <p>
 * <b>Usage:</b> Empyrium is the only current consumer — a single-colour apex robot
 * that is intentionally non-customisable by the player once spawned.
 */
public final class BlockAllItemInteractionFeature {
    // Marker — no fields required.
} // Class: BlockAllItemInteractionFeature
