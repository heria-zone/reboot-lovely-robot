package net.heriazone.lovelylib.hzlib.api.entity.data;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Tracks pending experience from attacked entities to prevent infinite exp gain.</p>
 * <p>
 * <b>Architecture:</b> Accumulates exp per attacked entity UUID, only awarding
 * when entity dies. Prevents exp farming from immortal entities (e.g., Mumummum).
 * <p>
 * <b>Design Decision:</b> Uses time-based purging to prevent memory leaks from
 * entities that never die. Configurable timeout ensures old entries are cleaned.
 * <p>
 * <b>Thread Safety:</b> Uses ConcurrentHashMap for thread-safe access across
 * server ticks and async events.
 * <p>
 * <b>Use Cases:</b>
 * <ul>
 * <li>Prevent exp gain from hitting invulnerable entities repeatedly</li>
 * <li>Award accumulated exp only on entity death</li>
 * <li>Auto-cleanup stale entries to prevent memory bloat</li>
 * </ul>
 */
public class ExperienceTracker {

    // -- Constants --

    /**
     * Default timeout for purging stale entries (5 minutes in milliseconds).
     */
    public static final long DEFAULT_TIMEOUT_MS = 5 * 60 * 1000; // 5 minutes

    // -- Fields --

    private final Map<UUID, PendingExpEntry> pendingExp;
    private final long timeoutMs;
    private long lastPurgeTime;

    // -- Constructors --

    /**
     * Creates experience tracker with default timeout (5 minutes).
     */
    public ExperienceTracker() {
        this(DEFAULT_TIMEOUT_MS);
    } // Constructor: ExperienceTracker ()

    /**
     * Creates experience tracker with custom timeout.
     *
     * @param timeoutMs timeout in milliseconds for purging stale entries
     */
    public ExperienceTracker(long timeoutMs) {
        this.pendingExp = new ConcurrentHashMap<>();
        this.timeoutMs = Math.max(1000, timeoutMs); // Minimum 1 second
        this.lastPurgeTime = System.currentTimeMillis();
    } // Constructor: ExperienceTracker ()

    // -- Public Methods --

    /**
     * Accumulates experience for attacked entity.
     * <p>
     * <b>Behavior:</b> Adds exp to existing entry or creates new entry.
     * Updates last hit time to prevent premature purging.
     * <p>
     * <b>Thread Safety:</b> Safe to call from multiple threads.
     *
     * @param entityId UUID of attacked entity
     * @param exp experience to accumulate
     * @throws NullPointerException if entityId is null
     */
    public void accumulateExp(@NotNull UUID entityId, int exp) {
        Objects.requireNonNull(entityId, "Entity ID cannot be null");

        if (exp <= 0) {
            return;
        }

        pendingExp.compute(entityId, (key, existing) -> {
            if (existing == null) {
                return new PendingExpEntry(exp);
            } else {
                existing.addExp(exp);
                return existing;
            }
        });
    } // accumulateExp ()

    /**
     * Claims accumulated experience for entity and removes entry.
     * <p>
     * <b>Use Case:</b> Called when entity dies to award all accumulated exp.
     * <p>
     * <b>Thread Safety:</b> Safe to call from multiple threads.
     *
     * @param entityId UUID of entity that died
     * @return accumulated experience, or 0 if no entry exists
     * @throws NullPointerException if entityId is null
     */
    public int claimExp(@NotNull UUID entityId) {
        Objects.requireNonNull(entityId, "Entity ID cannot be null");

        PendingExpEntry entry = pendingExp.remove(entityId);
        return entry != null ? entry.getAccumulatedExp() : 0;
    } // claimExp ()

    /**
     * Gets accumulated experience for entity without claiming.
     * <p>
     * <b>Use Case:</b> Query pending exp for debugging or display purposes.
     *
     * @param entityId UUID of entity
     * @return accumulated experience, or 0 if no entry exists
     * @throws NullPointerException if entityId is null
     */
    public int getPendingExp(@NotNull UUID entityId) {
        Objects.requireNonNull(entityId, "Entity ID cannot be null");

        PendingExpEntry entry = pendingExp.get(entityId);
        return entry != null ? entry.getAccumulatedExp() : 0;
    } // getPendingExp ()

    /**
     * Checks if entity has pending experience.
     *
     * @param entityId UUID of entity
     * @return true if entity has accumulated exp
     * @throws NullPointerException if entityId is null
     */
    public boolean hasPendingExp(@NotNull UUID entityId) {
        Objects.requireNonNull(entityId, "Entity ID cannot be null");
        return pendingExp.containsKey(entityId);
    } // hasPendingExp ()

    /**
     * Purges stale entries older than configured timeout.
     * <p>
     * <b>Performance:</b> O(n) where n is number of tracked entities.
     * Should be called periodically (e.g., every 20 ticks) to prevent
     * memory buildup.
     * <p>
     * <b>Behavior:</b> Only purges if sufficient time has passed since
     * last purge to avoid excessive iteration.
     *
     * @return number of entries purged
     */
    public int purgeStaleEntries() {
        long currentTime = System.currentTimeMillis();

        // Only purge if timeout has passed since last purge
        if (currentTime - lastPurgeTime < timeoutMs) {
            return 0;
        }

        lastPurgeTime = currentTime;
        int purgedCount = 0;

        Iterator<Map.Entry<UUID, PendingExpEntry>> iterator = pendingExp.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, PendingExpEntry> entry = iterator.next();
            if (entry.getValue().isStale(currentTime, timeoutMs)) {
                iterator.remove();
                purgedCount++;
            }
        }

        return purgedCount;
    } // purgeStaleEntries ()

    /**
     * Clears all pending experience entries.
     * <p>
     * <b>Use Case:</b> Reset tracker or cleanup on world unload.
     */
    public void clear() {
        pendingExp.clear();
        lastPurgeTime = System.currentTimeMillis();
    } // clear ()

    /**
     * Gets number of entities with pending experience.
     *
     * @return count of tracked entities
     */
    public int size() {
        return pendingExp.size();
    } // size ()

    /**
     * Checks if tracker is empty.
     *
     * @return true if no pending experience entries
     */
    public boolean isEmpty() {
        return pendingExp.isEmpty();
    } // isEmpty ()

    // -- Pending Experience Entry --

    /**
     * <p>Represents accumulated experience for single entity.</p>
     * <p>
     * <b>Thread Safety:</b> Mutable but only accessed through ConcurrentHashMap
     * compute operations, ensuring thread-safe updates.
     */
    private static class PendingExpEntry {

        // -- Fields --

        private int accumulatedExp;
        private long lastHitTime;

        // -- Constructor --

        /**
         * Creates pending exp entry with initial experience.
         *
         * @param initialExp starting experience value
         */
        public PendingExpEntry(int initialExp) {
            this.accumulatedExp = Math.max(0, initialExp);
            this.lastHitTime = System.currentTimeMillis();
        } // Constructor: PendingExpEntry ()

        // -- Methods --

        /**
         * Adds experience to accumulated total.
         *
         * @param exp experience to add
         */
        public void addExp(int exp) {
            if (exp > 0) {
                this.accumulatedExp += exp;
                this.lastHitTime = System.currentTimeMillis();
            }
        } // addExp ()

        /**
         * Gets accumulated experience.
         *
         * @return total accumulated exp
         */
        public int getAccumulatedExp() {
            return accumulatedExp;
        } // getAccumulatedExp ()

        /**
         * Gets last hit timestamp.
         *
         * @return timestamp in milliseconds
         */
        public long getLastHitTime() {
            return lastHitTime;
        } // getLastHitTime ()

        /**
         * Checks if entry is stale based on timeout.
         *
         * @param currentTime current timestamp in milliseconds
         * @param timeoutMs timeout duration in milliseconds
         * @return true if entry should be purged
         */
        public boolean isStale(long currentTime, long timeoutMs) {
            return (currentTime - lastHitTime) > timeoutMs;
        } // isStale ()

    } // Class: PendingExpEntry

} // Class: ExperienceTracker