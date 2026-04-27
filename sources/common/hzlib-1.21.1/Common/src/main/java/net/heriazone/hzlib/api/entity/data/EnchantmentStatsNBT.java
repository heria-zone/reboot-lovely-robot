package net.heriazone.hzlib.api.entity.data;

import net.heriazone.hzlib.framework.entity.data.EnchantmentStats;
import net.heriazone.hzlib.framework.utils.Version;
import net.heriazone.hzlib.utils.IReadWriteNBT;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * <p>Handles NBT serialization for EnchantmentStats with version-aware migration support.</p>
 * <p>
 * <b>Architecture:</b> Encapsulates NBT read/write logic, keeping serialization concerns
 * separate from data model. Enables backward compatibility when enchantment stat structure
 * evolves across mod versions.
 * <p>
 * <b>Design Decision:</b> Wrapper pattern rather than implementing IReadWriteNBT directly
 * on EnchantmentStats maintains framework layer purity (zero Minecraft dependencies).
 * <p>
 * <b>Migration Strategy:</b> Writes current version tag, uses version comparison in
 * readFromNBT to apply transformation logic for legacy data formats.
 */
public class EnchantmentStatsNBT implements IReadWriteNBT {

    // -- Constants --

    private static final String TAG_VERSION = "Version";
    private static final String TAG_LOOTING_LEVEL = "LootingLevel";
    private static final String TAG_SHARPNESS_LEVEL = "SharpnessLevel";
    private static final String TAG_KNOCKBACK_LEVEL = "KnockbackLevel";

    private static final String CURRENT_VERSION = "1.0.0";

    // -- Variables --

    private final EnchantmentStats stats;

    // -- Constructors --

    /**
     * Creates NBT handler for specified enchantment stats instance.
     * <p>
     * <b>Design Decision:</b> Handler holds reference to stats object rather than
     * copying data, enabling direct mutation during deserialization.
     *
     * @param stats enchantment stats instance to serialize/deserialize (must not be null)
     * @throws NullPointerException if stats is null
     */
    public EnchantmentStatsNBT(@NotNull EnchantmentStats stats) {
        this.stats = Objects.requireNonNull(stats, "EnchantmentStats cannot be null");
    } // Constructor: EnchantmentStatsNBT ()

    // -- IReadWriteNBT Implementation --

    @NotNull
    @Override
    public CompoundTag writeToNBT(@NotNull CompoundTag tag) {
        Objects.requireNonNull(tag, "CompoundTag cannot be null");

        // Write version for migration support
        tag.putString(TAG_VERSION, CURRENT_VERSION);

        // Write all enchantment stat fields
        tag.putInt(TAG_LOOTING_LEVEL, stats.getLootingLevel());
        tag.putInt(TAG_SHARPNESS_LEVEL, stats.getSharpnessLevel());
        tag.putInt(TAG_KNOCKBACK_LEVEL, stats.getKnockbackLevel());

        return tag;
    } // writeToNBT ()

    @Override
    public void readFromNBT(@NotNull CompoundTag tag, @NotNull Version tagVersion) {
        Objects.requireNonNull(tag, "CompoundTag cannot be null");
        Objects.requireNonNull(tagVersion, "Version cannot be null");

        // Read all enchantment stat fields with defaults for missing data
        stats.setLootingLevel(tag.contains(TAG_LOOTING_LEVEL) ? tag.getInt(TAG_LOOTING_LEVEL) : 0);
        stats.setSharpnessLevel(tag.contains(TAG_SHARPNESS_LEVEL) ? tag.getInt(TAG_SHARPNESS_LEVEL) : 0);
        stats.setKnockbackLevel(tag.contains(TAG_KNOCKBACK_LEVEL) ? tag.getInt(TAG_KNOCKBACK_LEVEL) : 0);

        // Apply version-specific migrations if needed
        // Example: if (tagVersion.isOlderThan(new Version("1.1.0"))) { /* migration logic */ }
    } // readFromNBT ()

    // -- Getters --

    /**
     * @return the enchantment stats instance being serialized
     */
    @NotNull
    public EnchantmentStats getStats() {
        return stats;
    } // getStats ()

} // Class: EnchantmentStatsNBT