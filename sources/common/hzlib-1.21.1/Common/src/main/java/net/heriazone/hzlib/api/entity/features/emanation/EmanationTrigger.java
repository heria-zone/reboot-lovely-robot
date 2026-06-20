package net.heriazone.hzlib.api.entity.features.emanation;

/**
 * <p>The four moments when an emanation can occur.<p>
 * <p>
 * <b>ON_ATTACK</b>    — the entity strikes a target.<br>
 * <b>ON_HURT</b>      — the entity takes damage from an attacker.<br>
 * <b>ON_GIFT</b>      — a player gives the entity an item.<br>
 * <b>ON_THRESHOLD</b> — the entity's health crosses a declared threshold.
 * <p>
 * <b>Design:</b> Triggers map directly to overrideable lifecycle methods in
 * {@code MonsterEntity} — {@code doHurtTarget()}, {@code hurt()},
 * {@code onCommonInteraction()}, and {@code aiStep()}. The enum makes
 * the mapping explicit and eliminates any string-keyed dispatch.
 */
public enum EmanationTrigger {
    ON_ATTACK,
    ON_HURT,
    ON_GIFT,
    ON_THRESHOLD
} // Enum: EmanationTrigger
