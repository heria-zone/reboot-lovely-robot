package net.heriazone.hzlib.api.entity.features.exchange;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;

import java.util.*;

/**
 * <p>Mutable per-entity runtime state for {@link ExchangeFeature}.<p>
 * <p>
 * <b>Architecture:</b> Separates stateless feature declaration ({@link ExchangeFeature})
 * from stateful runtime tracking. One {@link ExchangeState} instance lives on each
 * entity that has an {@link ExchangeFeature}. Stored and restored via NBT.
 * <p>
 * <b>Sequence buffer:</b> One independent buffer per player UUID. Each buffer is a
 * growing list of items given in order. Buffers expire automatically if no new item
 * is given within a configurable timeout.
 * <p>
 * <b>Cooldowns:</b> Indexed by rule position within the feature's rule list. Stored
 * as absolute game tick values — the tick when the cooldown expires.
 * <p>
 * <b>Thread Safety:</b> Not thread-safe. All access must be on the server thread.
 */
public final class ExchangeState {

    // -- NBT Keys --

    private static final String NBT_COOLDOWNS  = "ExchangeCooldowns";
    private static final String NBT_RULE_INDEX = "RuleIndex";
    private static final String NBT_EXPIRES_AT = "ExpiresAt";

    // -- Fields --

    /** Per-player sequence buffer: UUID → ordered list of items given. */
    private final Map<UUID, List<Item>> sequenceBuffers = new HashMap<>();

    /** Per-player buffer timestamp: UUID → game tick when last item was added. */
    private final Map<UUID, Long> bufferTimestamps = new HashMap<>();

    /** Per-rule cooldowns: ruleIndex → absolute game tick when cooldown ends. */
    private final Map<Integer, Long> cooldowns = new HashMap<>();

    // -- Sequence Buffer --

    /**
     * Returns the sequence buffer for the given player, creating an empty one if absent.
     *
     * @param playerUUID player UUID
     * @return mutable buffer list (caller may modify)
     */
    public List<Item> getBuffer(UUID playerUUID) {
        return sequenceBuffers.computeIfAbsent(playerUUID, k -> new ArrayList<>());
    } // getBuffer ()

    /**
     * Clears the sequence buffer for the given player.
     *
     * @param playerUUID player UUID
     */
    public void clearBuffer(UUID playerUUID) {
        sequenceBuffers.remove(playerUUID);
        bufferTimestamps.remove(playerUUID);
    } // clearBuffer ()

    /**
     * Updates the timestamp for the given player's buffer to the current tick.
     *
     * @param playerUUID  player UUID
     * @param currentTick current game time in ticks
     */
    public void updateBufferTimestamp(UUID playerUUID, long currentTick) {
        bufferTimestamps.put(playerUUID, currentTick);
    } // updateBufferTimestamp ()

    /**
     * Expires any buffer whose last update was more than {@code timeoutTicks} ago.
     * <p>
     * Call once per interaction, before advancing the buffer.
     *
     * @param playerUUID    player UUID to check
     * @param currentTick   current game time
     * @param timeoutTicks  ticks of inactivity before a buffer expires
     */
    public void expireBuffer(UUID playerUUID, long currentTick, long timeoutTicks) {
        Long last = bufferTimestamps.get(playerUUID);
        if (last != null && (currentTick - last) > timeoutTicks) {
            clearBuffer(playerUUID);
        }
    } // expireBuffer ()

    /**
     * Expires all buffers that have timed out. Call this periodically (e.g., every
     * 20 ticks from the entity's {@code tick()} method) to clean up stale state.
     *
     * @param currentTick  current game time
     * @param timeoutTicks ticks of inactivity before expiry
     */
    public void expireAllStaleBuffers(long currentTick, long timeoutTicks) {
        List<UUID> toRemove = new ArrayList<>();
        for (Map.Entry<UUID, Long> entry : bufferTimestamps.entrySet()) {
            if ((currentTick - entry.getValue()) > timeoutTicks) {
                toRemove.add(entry.getKey());
            }
        }
        toRemove.forEach(this::clearBuffer);
    } // expireAllStaleBuffers ()

    // -- Cooldowns --

    /**
     * Returns whether the rule at the given index is currently on cooldown.
     *
     * @param ruleIndex   index of the rule in the feature's rule list
     * @param currentTick current game time
     * @return true if the rule cannot fire yet
     */
    public boolean isOnCooldown(int ruleIndex, long currentTick) {
        Long expiresAt = cooldowns.get(ruleIndex);
        return expiresAt != null && currentTick < expiresAt;
    } // isOnCooldown ()

    /**
     * Sets the cooldown expiry tick for the given rule.
     *
     * @param ruleIndex rule index
     * @param expiresAt absolute tick when the cooldown ends
     */
    public void setCooldown(int ruleIndex, long expiresAt) {
        cooldowns.put(ruleIndex, expiresAt);
    } // setCooldown ()

    /**
     * Clears the cooldown for the given rule, allowing it to fire immediately.
     *
     * @param ruleIndex rule index
     */
    public void clearCooldown(int ruleIndex) {
        cooldowns.remove(ruleIndex);
    } // clearCooldown ()

    // -- NBT Serialization --

    /**
     * Writes cooldown state to NBT.
     * <p>
     * <b>Buffers:</b> Sequence buffers are intentionally not persisted — they represent
     * in-progress player interactions that would be meaningless after a server restart.
     *
     * @param nbt compound tag to write into
     */
    public void save(CompoundTag nbt) {
        if (cooldowns.isEmpty()) return;

        ListTag list = new ListTag();
        for (Map.Entry<Integer, Long> entry : cooldowns.entrySet()) {
            CompoundTag entry_tag = new CompoundTag();
            entry_tag.putInt(NBT_RULE_INDEX, entry.getKey());
            entry_tag.putLong(NBT_EXPIRES_AT, entry.getValue());
            list.add(entry_tag);
        }
        nbt.put(NBT_COOLDOWNS, list);
    } // save ()

    /**
     * Reads cooldown state from NBT.
     *
     * @param nbt compound tag to read from
     */
    public void load(CompoundTag nbt) {
        cooldowns.clear();
        if (!nbt.contains(NBT_COOLDOWNS, Tag.TAG_LIST)) return;

        ListTag list = nbt.getList(NBT_COOLDOWNS, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry_tag = list.getCompound(i);
            int  ruleIndex = entry_tag.getInt(NBT_RULE_INDEX);
            long expiresAt = entry_tag.getLong(NBT_EXPIRES_AT);
            cooldowns.put(ruleIndex, expiresAt);
        }
    } // load ()

} // Class: ExchangeState
