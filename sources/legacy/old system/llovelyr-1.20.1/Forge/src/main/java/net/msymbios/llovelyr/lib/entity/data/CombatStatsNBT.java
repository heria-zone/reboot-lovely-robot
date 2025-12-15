package net.msymbios.llovelyr.lib.entity.data;

import net.minecraft.nbt.CompoundTag;
import net.msymbios.llovelyr.framework.entity.data.CombatStats;
import net.msymbios.llovelyr.framework.utils.Version;
import net.msymbios.llovelyr.lib.utils.interfaces.IReadWriteNBT;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * <p>Handles NBT serialization for CombatStats with version-aware migration support.</p>
 * <p>
 * <b>Architecture:</b> Encapsulates NBT read/write logic, keeping serialization concerns
 * separate from data model. Enables backward compatibility when combat stat structure
 * evolves across mod versions.
 * <p>
 * <b>Design Decision:</b> Wrapper pattern rather than implementing IReadWriteNBT directly
 * on CombatStats maintains framework layer purity (zero Minecraft dependencies).
 * <p>
 * <b>Migration Strategy:</b> Writes current version tag, uses version comparison in
 * readFromNBT to apply transformation logic for legacy data formats.
 */
public class CombatStatsNBT implements IReadWriteNBT {

    // -- Constants --
    
    private static final String TAG_VERSION = "Version";
    private static final String TAG_LEVEL = "Level";
    private static final String TAG_EXPERIENCE = "Experience";
    private static final String TAG_CURRENT_HP = "CurrentHp";
    private static final String TAG_MAX_HP = "MaxHp";
    private static final String TAG_ATTACK = "Attack";
    private static final String TAG_DEFENSE = "Defense";
    
    private static final String CURRENT_VERSION = "1.0.0";

    // -- Fields --
    
    private final CombatStats stats;

    // -- Constructors --
    
    /**
     * Creates NBT handler for specified combat stats instance.
     * <p>
     * <b>Design Decision:</b> Handler holds reference to stats object rather than
     * copying data, enabling direct mutation during deserialization.
     * 
     * @param stats combat stats instance to serialize/deserialize (must not be null)
     * @throws NullPointerException if stats is null
     */
    public CombatStatsNBT(@Nonnull CombatStats stats) {
        this.stats = Objects.requireNonNull(stats, "CombatStats cannot be null");
    }

    // -- IReadWriteNBT Implementation --
    
    @Nonnull
    @Override
    public CompoundTag writeToNBT(@Nonnull CompoundTag tag) {
        Objects.requireNonNull(tag, "CompoundTag cannot be null");
        
        // Write version for migration support
        tag.putString(TAG_VERSION, CURRENT_VERSION);
        
        // Write all combat stat fields
        tag.putInt(TAG_LEVEL, stats.getLevel());
        tag.putInt(TAG_EXPERIENCE, stats.getExperience());
        tag.putInt(TAG_CURRENT_HP, stats.getCurrentHp());
        tag.putInt(TAG_MAX_HP, stats.getMaxHp());
        tag.putInt(TAG_ATTACK, stats.getAttack());
        tag.putInt(TAG_DEFENSE, stats.getDefense());
        
        return tag;
    }

    @Override
    public void readFromNBT(@Nonnull CompoundTag tag, @Nonnull Version tagVersion) {
        Objects.requireNonNull(tag, "CompoundTag cannot be null");
        Objects.requireNonNull(tagVersion, "Version cannot be null");
        
        // Read all combat stat fields with defaults for missing data
        stats.setLevel(tag.contains(TAG_LEVEL) ? tag.getInt(TAG_LEVEL) : 0);
        stats.setExperience(tag.contains(TAG_EXPERIENCE) ? tag.getInt(TAG_EXPERIENCE) : 0);
        stats.setCurrentHp(tag.contains(TAG_CURRENT_HP) ? tag.getInt(TAG_CURRENT_HP) : 20);
        stats.setMaxHp(tag.contains(TAG_MAX_HP) ? tag.getInt(TAG_MAX_HP) : 20);
        stats.setAttack(tag.contains(TAG_ATTACK) ? tag.getInt(TAG_ATTACK) : 2);
        stats.setDefense(tag.contains(TAG_DEFENSE) ? tag.getInt(TAG_DEFENSE) : 0);
        
        // Apply version-specific migrations if needed
        // Example: if (tagVersion.isOlderThan(new Version("1.1.0"))) { /* migration logic */ }
    }

    // -- Getters --
    
    /**
     * @return the combat stats instance being serialized
     */
    @Nonnull
    public CombatStats getStats() {
        return stats;
    }

} // Class: CombatStatsNBT
