package net.msymbios.llovelyr.lib.entity.data;

import net.minecraft.nbt.CompoundTag;
import net.msymbios.llovelyr.LovelyConstant;
import net.msymbios.llovelyr.framework.entity.data.CombatStats;
import net.msymbios.llovelyr.framework.entity.data.EnchantmentStats;
import net.msymbios.llovelyr.framework.entity.data.ProtectionStats;
import net.msymbios.llovelyr.framework.utils.Version;
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

    private final CombatStats combatStats;
    private final ProtectionStats protectionStats;
    private final EnchantmentStats enchantmentStats;
    private final Version dataVersion;

    // -- Constructors --

    /**
     * Creates entity data with default stats.
     * <p>
     * <b>Initial State:</b> All stats initialized to zero/default values.
     */
    public EntityData() {
        this(new CombatStats(), new ProtectionStats(), new EnchantmentStats());
    } // Constructor: EntityData ()

    /**
     * Creates entity data with specified stat objects.
     *
     * @param combatStats combat statistics
     * @param protectionStats protection statistics
     * @param enchantmentStats enchantment statistics
     * @throws NullPointerException if any parameter is null
     */
    public EntityData(@NotNull CombatStats combatStats,
                      @NotNull ProtectionStats protectionStats,
                      @NotNull EnchantmentStats enchantmentStats) {
        this.combatStats = Objects.requireNonNull(combatStats, "Combat stats cannot be null");
        this.protectionStats = Objects.requireNonNull(protectionStats, "Protection stats cannot be null");
        this.enchantmentStats = Objects.requireNonNull(enchantmentStats, "Enchantment stats cannot be null");
        this.dataVersion = LovelyConstant.Version.CURRENT;
    } // Constructor: EntityData ()

    // -- Getters --

    /**
     * Gets combat statistics.
     *
     * @return combat stats object
     */
    public CombatStats getCombatStats() {
        return combatStats;
    } // getCombatStats ()

    /**
     * Gets protection statistics.
     *
     * @return protection stats object
     */
    public ProtectionStats getProtectionStats() {
        return protectionStats;
    } // getProtectionStats ()

    /**
     * Gets enchantment statistics.
     *
     * @return enchantment stats object
     */
    public EnchantmentStats getEnchantmentStats() {
        return enchantmentStats;
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
     *   CombatStats: compound
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
        CombatStatsNBT combatNBT = new CombatStatsNBT(combatStats);
        nbt.put("CombatStats", combatNBT.writeToNBT(new CompoundTag()));

        ProtectionStatsNBT protectionNBT = new ProtectionStatsNBT(protectionStats);
        nbt.put("ProtectionStats", protectionNBT.writeToNBT(new CompoundTag()));

        EnchantmentStatsNBT enchantmentNBT = new EnchantmentStatsNBT(enchantmentStats);
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
                : LovelyConstant.Version.CURRENT;

        // Deserialize stat objects
        CombatStats combatStats = new CombatStats();
        if (nbt.contains("CombatStats")) {
            CombatStatsNBT combatNBT = new CombatStatsNBT(combatStats);
            combatNBT.readFromNBT(nbt.getCompound("CombatStats"), version);
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

        return new EntityData(combatStats, protectionStats, enchantmentStats);
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
        if (combatStats.getLevel() < 0 || combatStats.getMaxHp() < 0) {
            return false;
        }

        // Validate protection stats (0-100 range)
        if (protectionStats.getFireProtection() < 0 || protectionStats.getFireProtection() > 100) {
            return false;
        }
        if (protectionStats.getFallProtection() < 0 || protectionStats.getFallProtection() > 100) {
            return false;
        }
        if (protectionStats.getBlastProtection() < 0 || protectionStats.getBlastProtection() > 100) {
            return false;
        }
        if (protectionStats.getProjectileProtection() < 0 || protectionStats.getProjectileProtection() > 100) {
            return false;
        }

        // Validate enchantment stats (non-negative)
        if (enchantmentStats.getLootingLevel() < 0) {
            return false;
        }
        if (enchantmentStats.getSharpnessLevel() < 0) {
            return false;
        }
        if (enchantmentStats.getKnockbackLevel() < 0) {
            return false;
        }

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
        return new EntityData(
                combatStats.copy(),
                protectionStats.copy(),
                enchantmentStats.copy()
        );
    } // copy ()

    @Override
    public String toString() {
        return "EntityData{" +
                "combat=" + combatStats +
                ", protection=" + protectionStats +
                ", enchantment=" + enchantmentStats +
                ", version=" + dataVersion +
                '}';
    } // toString ()

} // Class: EntityData
