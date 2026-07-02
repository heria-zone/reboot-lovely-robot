package net.heriazone.lovelylib.common.entity.definition;

/**
 * Identifies which mod(s) a robot entity ships in.
 * <p>
 * Used as a filter key in RobotDefinitionRegistry.getForMod() so that Legacy
 * and Reboot each iterate only their own entities. A single entity can carry
 * multiple targets (e.g. Bunny3 uses LEGACY and REBOOT via forMods()).
 * <p>
 * <i>Note:</i> TRIBUTE is defined for completeness but is never registered with
 * RobotDefinitionRegistry — Tribute has a fixed roster, uses TributeRobotEntity,
 * and is never extended. Its config loop reads LovelyConstant.TRIBUTE_VARIANTS
 * directly and is unaffected by this ADR.
 */
public enum ModTarget {
    LEGACY,
    REBOOT,
    TRIBUTE
} // Enum: ModTarget
