package net.heriazone.hzlib.api.entity.features.overlay;

/**
 * <p>Determines how an {@link OverlaySlot} selects its active texture each frame.<p>
 * <p>
 * <b>Persistence contract:</b>
 * <ul>
 *   <li>{@link #RANDOM} and {@link #INTERACTIVE} — selection is persistent. The entity stores
 *       the chosen texture path in {@code SynchedEntityData} and saves it to NBT.</li>
 *   <li>{@link #CONDITIONAL} and {@link #ALWAYS} — selection is stateless. No entity data
 *       needed; the predicate or constant is evaluated every render tick.</li>
 * </ul>
 */
public enum SlotMode {

    /**
     * Chosen once at {@code finalizeSpawn}, persisted in synced entity data and NBT.
     * Picks uniformly from the slot's texture pool.
     * <p>Example: Mandragora hairstyle — straight, curly, short, long.
     */
    RANDOM,

    /**
     * Evaluated every render tick. First matching entry wins; no entity state required.
     * <p>Example: belly overlay (entity stat check), seasonal costume (month check).
     */
    CONDITIONAL,

    /**
     * Cycled by explicit player interaction (e.g., shears). Persistent in synced data + NBT.
     * Empty string at index 0 is a valid "none active" sentinel.
     * <p>Example: Gourdragora carving patterns.
     */
    INTERACTIVE,

    /**
     * Always renders; no condition, no state. Unconditional overlay.
     * <p>Example: always-on emissive glow, permanent detail overlay.
     */
    ALWAYS

} // Enum: SlotMode
