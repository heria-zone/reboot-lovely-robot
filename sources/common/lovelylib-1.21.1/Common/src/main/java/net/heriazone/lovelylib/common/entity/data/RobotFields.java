package net.heriazone.lovelylib.common.entity.data;

import net.heriazone.hzlib.api.nbt.DataField;
import net.heriazone.hzlib.api.nbt.DataType;

/**
 * <p>Single source of truth for all robot entity NBT field handles.<p>
 * <p>
 * <b>Design:</b> Every field an {@link net.heriazone.lovelylib.common.entity.RobotEntity}
 * owns is declared here as a typed {@link DataField} constant. The NBT key is encapsulated
 * inside each constant — no string literals appear at call sites.
 * Rename a field key by changing one constant; the compiler rejects every stale reference.
 * <p>
 * <b>Usage:</b> {@link net.heriazone.lovelylib.common.entity.RobotFamily#configureSchema()}
 * registers these constants into the family's {@link net.heriazone.hzlib.api.nbt.EntityDataSchema}.
 * {@link net.heriazone.lovelylib.common.entity.RobotEntity#provideFieldValue} and
 * {@link net.heriazone.lovelylib.common.entity.RobotEntity#consumeFieldValue} dispatch
 * on these constants by identity — zero string keys at those call sites.
 */
public final class RobotFields {

    private RobotFields() {} // Non-instantiable

    // -- Combat / Level --

    /** Current robot level (1–maxLevel). */
    public static final DataField<Integer> LEVEL =
            DataField.of("Level", DataType.INT, 1, v -> v >= 1);

    /** Accumulated experience points. */
    public static final DataField<Integer> EXP =
            DataField.of("Exp", DataType.INT, 0, v -> v >= 0);

    /** Maximum level cap configured on the robot type. */
    public static final DataField<Integer> MAX_LEVEL =
            DataField.of("MaxLevel", DataType.INT, 0, v -> v >= 0);

    // -- Protection --

    /** Fire damage reduction level (0–100). */
    public static final DataField<Integer> FIRE_PROT =
            DataField.of("FireProtection", DataType.INT, 0, v -> v >= 0 && v <= 100);

    /** Fall damage reduction level (0–100). */
    public static final DataField<Integer> FALL_PROT =
            DataField.of("FallProtection", DataType.INT, 0, v -> v >= 0 && v <= 100);

    /** Blast damage reduction level (0–100). */
    public static final DataField<Integer> BLAST_PROT =
            DataField.of("BlastProtection", DataType.INT, 0, v -> v >= 0 && v <= 100);

    /** Projectile damage reduction level (0–100). */
    public static final DataField<Integer> PROJ_PROT =
            DataField.of("ProjectileProtection", DataType.INT, 0, v -> v >= 0 && v <= 100);

    // -- Behaviour State --

    /** Whether auto-attack mode is active. */
    public static final DataField<Boolean> AUTO_ATTACK =
            DataField.of("AutoAttack", DataType.BOOLEAN, true);

    // -- Base / Home Coordinates --

    /** Home base X for base-defense mode. */
    public static final DataField<Float> BASE_X =
            DataField.of("BaseX", DataType.FLOAT, 0f);

    /** Home base Y for base-defense mode. */
    public static final DataField<Float> BASE_Y =
            DataField.of("BaseY", DataType.FLOAT, 0f);

    /** Home base Z for base-defense mode. */
    public static final DataField<Float> BASE_Z =
            DataField.of("BaseZ", DataType.FLOAT, 0f);

    // -- Pose / Health --

    /** Whether the robot is in the smaller sitting hitbox pose. */
    public static final DataField<Boolean> SITTING =
            DataField.of("IsInSittingPose", DataType.BOOLEAN, false);

    /** Current health — persisted because Minecraft does not auto-restore it on reload. */
    public static final DataField<Float> HEALTH =
            DataField.of("CurrentHealth", DataType.FLOAT, 20f, v -> v > 0f);

    // -- Idle Animation State --

    /**
     * Ticks the entity has been stationary since entering or remaining in Standby.
     * <p>
     * <b>Purpose:</b> Persisted so {@code AnimationStateManager.resolveIdleSlot()}
     * correctly resolves the active idle animation (rest vs sit) on the first tick
     * after a world reload — without waiting for the sit-delay threshold to elapse
     * again. A robot left in the sit pose before the world was closed will resume
     * the sit animation immediately on next load rather than resetting to rest.
     * <p>
     * <b>Lifecycle:</b> Incremented every tick the entity is stationary and not
     * in a vehicle ({@code NativeEntity.tickIdleCounter()}). Reset to zero when the
     * entity moves or the state transitions away from Standby
     * ({@code NativeEntity.onStateChanged()}).
     */
    public static final DataField<Integer> IDLE_STATIONARY_TICKS =
            DataField.of("IdleStationaryTicks", DataType.INT, 0, v -> v >= 0);

    // -- Standby Animation State (deprecated — retained for save compatibility) --

    /**
     * Ticks spent stationary since entering standby. Restored so the sit-down
     * timer continues from where it left off after a world reload rather than
     * restarting from zero (which would cause an immediate pose reset visible to the player).
     *
     * @deprecated ADR 022 Change D — the {@link IdleSlot} system replaces this timer.
     *     This field is retained as a load-only NBT entry so existing saves do not produce
     *     schema errors. It is read but never written after migration.
     */
    @Deprecated
    public static final DataField<Integer> STANDBY_TICKS =
            DataField.of("StandbyTicks", DataType.INT, 0);

    /**
     * Randomised tick threshold at which the robot transitions from REST to SIT.
     * Restored alongside {@link #STANDBY_TICKS} for the same reason.
     *
     * @deprecated ADR 022 Change D — replaced by the sit slot's
     *     {@code activationThresholdTicks} ({@code SharedConfigs.Common.StandbyToSitDelayMin}).
     *     Retained as a load-only NBT entry for save compatibility.
     */
    @Deprecated
    public static final DataField<Integer> STANDBY_TARGET_TICKS =
            DataField.of("StandbyTargetTicks", DataType.INT, 0);

} // Class: RobotFields
