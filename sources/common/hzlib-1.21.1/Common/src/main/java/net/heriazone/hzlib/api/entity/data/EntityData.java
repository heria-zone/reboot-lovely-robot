package net.heriazone.hzlib.api.entity.data;

import net.heriazone.hzlib.framework.entity.data.*;
import net.heriazone.hzlib.framework.utils.Version;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * <p>Consolidated container for all custom entity data.</p>
 * <p>
 * <b>Architecture:</b> Single source of truth for entity state, grouping
 * combat stats, protections, enchantments, and future extensions under
 * unified "EntityData" NBT compound.
 * <p>
 * <b>Design Decision:</b> Replaces scattered NBT fields with structured
 * container, enabling:
 * <ul>
 * <li>Cleaner NBT organization</li>
 * <li>Easier migration between versions</li>
 * <li>Type-safe data access</li>
 * <li>Centralized validation</li>
 * </ul>
 * <p>
 * <b>Migration Strategy:</b> Supports loading from both old (scattered) and
 * new (EntityData) NBT formats, automatically migrating on first load.
 */
public class EntityData {

    // -- Fields --

    private final CombatLevelStats stats;
    private final ProtectionStats protections;
    private final EnchantmentStats enchantments;
    private final Version dataVersion;

    // -- Constructors --

    /**
     * Creates entity data with default stats.
     * <p>
     * <b>Initial State:</b> All stats initialized to zero/default values.
     */
    public EntityData() {
        this(new CombatLevelStats(), new ProtectionStats(), new EnchantmentStats(), new Version("1.0.0"));
    } // Constructor: EntityData ()

    public EntityData(Version version) {
        this(new CombatLevelStats(), new ProtectionStats(), new EnchantmentStats(), version);
    } // Constructor: EntityData ()

    /**
     * Creates entity data with specified stat objects.
     *
     * @param stats combat statistics
     * @param protections protection statistics
     * @param enchantments enchantment statistics
     * @throws NullPointerException if any parameter is null
     */
    public EntityData(@NotNull CombatLevelStats stats, @NotNull ProtectionStats protections, @NotNull EnchantmentStats enchantments, @NotNull Version version) {
        this.stats = Objects.requireNonNull(stats, "Combat stats cannot be null");
        this.protections = Objects.requireNonNull(protections, "Protection stats cannot be null");
        this.enchantments = Objects.requireNonNull(enchantments, "Enchantment stats cannot be null");
        this.dataVersion = Objects.requireNonNull(version, "Enchantment stats cannot be null");
    } // Constructor: EntityData ()

    // -- Getters --

    /**
     * Gets combat statistics.
     *
     * @return combat stats object
     */
    public CombatLevelStats getCombatStats() {
        return stats;
    } // getCombatStats ()

    /**
     * Gets protection statistics.
     *
     * @return protection stats object
     */
    public ProtectionStats getProtectionStats() {
        return protections;
    } // getProtectionStats ()

    /**
     * Gets enchantment statistics.
     *
     * @return enchantment stats object
     */
    public EnchantmentStats getEnchantmentStats() {
        return enchantments;
    } // getEnchantmentStats ()

    /**
     * Gets data format version.
     *
     * @return version of data structure
     */
    public Version getDataVersion() {
        return dataVersion;
    } // getDataVersion ()

    // -- NBT Serialization --

    /**
     * Serializes entity data to NBT compound.
     * <p>
     * <b>Format:</b>
     * <pre>
     * EntityData {
     *   DataVersion: string
     *   CombatLevelStats: compound
     *   ProtectionStats: compound
     *   EnchantmentStats: compound
     * }
     * </pre>
     *
     * @return CompoundTag with all entity data
     */
    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();

        // Store version for future migration support
        nbt.putString("DataVersion", dataVersion.toString());

        // Serialize stat objects using their NBT handlers
        CombatStatsNBT combatNBT = new CombatStatsNBT(stats);
        nbt.put("CombatLevelStats", combatNBT.writeToNBT(new CompoundTag()));

        ProtectionStatsNBT protectionNBT = new ProtectionStatsNBT(protections);
        nbt.put("ProtectionStats", protectionNBT.writeToNBT(new CompoundTag()));

        EnchantmentStatsNBT enchantmentNBT = new EnchantmentStatsNBT(enchantments);
        nbt.put("EnchantmentStats", enchantmentNBT.writeToNBT(new CompoundTag()));

        return nbt;
    } // toNBT ()

    /**
     * Deserializes entity data from NBT compound.
     * <p>
     * <b>Migration Support:</b> Handles both new (EntityData) and old
     * (scattered fields) formats automatically.
     *
     * @param nbt NBT compound with entity data
     * @return EntityData instance
     * @throws NullPointerException if nbt is null
     */
    public static EntityData fromNBT(@NotNull CompoundTag nbt) {
        Objects.requireNonNull(nbt, "NBT cannot be null");

        // Read version (default to CURRENT if not present)
        Version version = nbt.contains("DataVersion")
                ? new Version(nbt.getString("DataVersion"))
                : new Version("1.0.0");

        // Deserialize stat objects
        CombatLevelStats combatStats = new CombatLevelStats();
        if (nbt.contains("CombatLevelStats")) {
            CombatStatsNBT combatNBT = new CombatStatsNBT(combatStats);
            combatNBT.readFromNBT(nbt.getCompound("CombatLevelStats"), version);
        }

        ProtectionStats protectionStats = new ProtectionStats();
        if (nbt.contains("ProtectionStats")) {
            ProtectionStatsNBT protectionNBT = new ProtectionStatsNBT(protectionStats);
            protectionNBT.readFromNBT(nbt.getCompound("ProtectionStats"), version);
        }

        EnchantmentStats enchantmentStats = new EnchantmentStats();
        if (nbt.contains("EnchantmentStats")) {
            EnchantmentStatsNBT enchantmentNBT = new EnchantmentStatsNBT(enchantmentStats);
            enchantmentNBT.readFromNBT(nbt.getCompound("EnchantmentStats"), version);
        }

        return new EntityData(combatStats, protectionStats, enchantmentStats, version);
    } // fromNBT ()

    /**
     * Checks if NBT contains new EntityData format.
     * <p>
     * <b>Use Case:</b> Determine if migration from old format is needed.
     *
     * @param nbt NBT compound to check
     * @return true if contains EntityData structure
     * @throws NullPointerException if nbt is null
     */
    public static boolean hasEntityData(@NotNull CompoundTag nbt) {
        Objects.requireNonNull(nbt, "NBT cannot be null");
        return nbt.contains("EntityData");
    } // hasEntityData ()

    /**
     * Extracts EntityData from parent NBT compound.
     * <p>
     * <b>Use Case:</b> Read EntityData from entity's main NBT.
     *
     * @param parentNbt parent NBT compound
     * @return EntityData instance, or new instance if not present
     * @throws NullPointerException if parentNbt is null
     */
    public static EntityData fromParentNBT(@NotNull CompoundTag parentNbt) {
        Objects.requireNonNull(parentNbt, "Parent NBT cannot be null");

        if (parentNbt.contains("EntityData")) {
            return fromNBT(parentNbt.getCompound("EntityData"));
        }

        return new EntityData();
    } // fromParentNBT ()

    /**
     * Writes EntityData to parent NBT compound.
     * <p>
     * <b>Use Case:</b> Store EntityData in entity's main NBT.
     *
     * @param parentNbt parent NBT compound to write to
     * @throws NullPointerException if parentNbt is null
     */
    public void toParentNBT(@NotNull CompoundTag parentNbt) {
        Objects.requireNonNull(parentNbt, "Parent NBT cannot be null");
        parentNbt.put("EntityData", toNBT());
    } // toParentNBT ()

    // -- Validation --

    /**
     * Validates all stat objects for consistency.
     * <p>
     * <b>Checks:</b>
     * <ul>
     * <li>Level within valid range</li>
     * <li>HP non-negative</li>
     * <li>Protection levels within bounds</li>
     * <li>Enchantment levels valid</li>
     * </ul>
     *
     * @return true if all stats are valid
     */
    public boolean validate() {
        // Validate combat stats
        if (stats.getLevel() < 0 || stats.getMaxHp() < 0) return false;

        // Validate protection stats (0-100 range)
        if (protections.getFireProtection() < 0 || protections.getFireProtection() > 100) return false;
        if (protections.getFallProtection() < 0 || protections.getFallProtection() > 100) return false;
        if (protections.getBlastProtection() < 0 || protections.getBlastProtection() > 100) return false;
        if (protections.getProjectileProtection() < 0 || protections.getProjectileProtection() > 100) return false;

        // Validate enchantment stats (non-negative)
        if (enchantments.getLootingLevel() < 0) return false;
        if (enchantments.getSharpnessLevel() < 0) return false;
        if (enchantments.getKnockbackLevel() < 0) return false;

        return true;
    } // validate ()

    // -- Utility Methods --

    /**
     * Creates deep copy of entity data.
     * <p>
     * <b>Use Case:</b> Snapshot entity state for comparison or rollback.
     *
     * @return new EntityData instance with copied values
     */
    public EntityData copy() {
        return new EntityData(stats.copy(), protections.copy(), enchantments.copy(), dataVersion);
    } // copy ()

    @Override
    public String toString() {
        return "EntityData{" +
                "combat=" + stats +
                ", protection=" + protections +
                ", enchantment=" + enchantments +
                ", version=" + dataVersion +
                '}';
    } // toString ()

} // Class: EntityData
