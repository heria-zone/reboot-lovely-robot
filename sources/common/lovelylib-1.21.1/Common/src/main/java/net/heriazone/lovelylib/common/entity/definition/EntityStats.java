package net.heriazone.lovelylib.common.entity.definition;

import net.heriazone.lovelylib.common.configs.SharedConfigs;

/**
 * Immutable value object carrying the seven stat defaults for one robot entity.
 * <p>
 * <b>Architecture:</b> Replaces the per-entity static fields in SharedConfigs.Common
 * (e.g. BunnyMaxLevel, BunnyBaseHp, ...). Stats now travel with the entity definition
 * rather than living in a separate flat class, eliminating the structural decoupling
 * that caused Bug 2 in ADR-023.
 * <p>
 * <i>Note:</i> knockbackResistance is intentionally excluded — it is not a stored
 * config value and is passed as a literal 0F directly to RobotFamily.withCombatStats()
 * at the call site. EntityConfigData has no knockbackResistance field.
 */
public final class EntityStats {

    // -- Fields --

    public final int   maxLevel;
    public final int   baseHp;
    public final int   baseAttack;
    public final float attackSpeed;
    public final int   baseDefense;
    public final float baseToughness;
    public final float movementSpeed;

    // -- Constructor --

    public EntityStats(int maxLevel, int baseHp, int baseAttack,
                       float attackSpeed, int baseDefense,
                       float baseToughness, float movementSpeed) {
        this.maxLevel      = maxLevel;
        this.baseHp        = baseHp;
        this.baseAttack    = baseAttack;
        this.attackSpeed   = attackSpeed;
        this.baseDefense   = baseDefense;
        this.baseToughness = baseToughness;
        this.movementSpeed = movementSpeed;
    } // Constructor: EntityStats()

    // -- Methods --

    /**
     * Converts to the config data shape consumed by ConfigAccessLayer and RobotFamilies.
     * <p>
     * EntityConfigData takes exactly these 7 parameters in this order. knockbackResistance
     * is not included; callers pass 0F directly when invoking RobotFamily.withCombatStats().
     */
    public SharedConfigs.EntityConfigData toEntityConfigData() {
        return new SharedConfigs.EntityConfigData(
            maxLevel, baseHp, baseAttack, attackSpeed,
            baseDefense, baseToughness, movementSpeed
        );
    } // toEntityConfigData()

} // Class: EntityStats
