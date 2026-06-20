package net.heriazone.hzlib.api.entity.features.emanation;

import net.minecraft.nbt.CompoundTag;

/**
 * <p>Per-entity mutable runtime state for {@link EmanationFeature}.<p>
 * <p>
 * <b>Architecture:</b> Intentionally minimal. Only the
 * {@link EmanationTrigger#ON_THRESHOLD} trigger needs per-entity tracking — a cooldown
 * so the threshold rule doesn't fire every tick while health stays below the threshold.
 * ON_ATTACK and ON_HURT fire freely per event (already rate-limited by combat).
 * ON_GIFT fires per player interaction (the give rate is the natural limit).
 * <p>
 * <b>NBT persistence:</b> Cooldown stored as an absolute game tick. Survives server
 * restarts — on reload the cooldown may already be expired, which is correct: the
 * entity's nature is always ready to express itself once enough time has passed.
 * <p>
 * Lives on {@code MonsterEntity} as {@code protected final EmanationState emanationState}.
 * Declared there rather than on {@code NativeEntity} since robots don't use
 * emanation mechanics yet.
 */
public final class EmanationState {

    // -- Fields --

    /** Absolute game tick after which the threshold trigger can fire again. 0 = ready. */
    private long thresholdCooldownUntil = 0L;

    // -- Threshold Cooldown --

    /**
     * Returns true if the threshold trigger is ready to fire.
     *
     * @param currentTick current level game time in ticks ({@code level.getGameTime()})
     * @return true if cooldown has expired or was never set
     */
    public boolean isThresholdReady(long currentTick) {
        return currentTick >= thresholdCooldownUntil;
    } // isThresholdReady ()

    /**
     * Sets the threshold cooldown after a successful fire.
     *
     * @param currentTick   current level game time in ticks
     * @param cooldownTicks duration of the cooldown in ticks
     */
    public void setThresholdCooldown(long currentTick, int cooldownTicks) {
        thresholdCooldownUntil = currentTick + cooldownTicks;
    } // setThresholdCooldown ()

    // -- NBT --

    /** Saves the cooldown state to the given NBT tag. */
    public void save(CompoundTag nbt) {
        if (thresholdCooldownUntil > 0L) {
            nbt.putLong("ThresholdCooldownUntil", thresholdCooldownUntil);
        }
    } // save ()

    /** Loads the cooldown state from the given NBT tag. */
    public void load(CompoundTag nbt) {
        if (nbt.contains("ThresholdCooldownUntil")) {
            thresholdCooldownUntil = nbt.getLong("ThresholdCooldownUntil");
        }
    } // load ()

} // Class: EmanationState
