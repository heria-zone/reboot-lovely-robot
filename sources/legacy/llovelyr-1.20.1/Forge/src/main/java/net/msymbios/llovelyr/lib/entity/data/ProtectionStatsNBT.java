package net.msymbios.llovelyr.lib.entity.data;

import net.minecraft.nbt.CompoundTag;
import net.msymbios.llovelyr.framework.entity.data.ProtectionStats;
import net.msymbios.llovelyr.framework.utils.Version;
import net.msymbios.llovelyr.lib.utils.interfaces.IReadWriteNBT;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * <p>Handles NBT serialization for ProtectionStats with version-aware migration support.</p>
 * <p>
 * <b>Architecture:</b> Encapsulates NBT read/write logic, keeping serialization concerns
 * separate from data model. Enables backward compatibility when protection stat structure
 * evolves across mod versions.
 * <p>
 * <b>Design Decision:</b> Wrapper pattern rather than implementing IReadWriteNBT directly
 * on ProtectionStats maintains framework layer purity (zero Minecraft dependencies).
 * <p>
 * <b>Migration Strategy:</b> Writes current version tag, uses version comparison in
 * readFromNBT to apply transformation logic for legacy data formats.
 */
public class ProtectionStatsNBT implements IReadWriteNBT {

    // -- Constants --
    
    private static final String TAG_VERSION = "Version";
    private static final String TAG_FIRE_PROTECTION = "FireProtection";
    private static final String TAG_FALL_PROTECTION = "FallProtection";
    private static final String TAG_BLAST_PROTECTION = "BlastProtection";
    private static final String TAG_PROJECTILE_PROTECTION = "ProjectileProtection";
    
    private static final String CURRENT_VERSION = "1.0.0";

    // -- Fields --
    
    private final ProtectionStats stats;

    // -- Constructors --
    
    /**
     * Creates NBT handler for specified protection stats instance.
     * <p>
     * <b>Design Decision:</b> Handler holds reference to stats object rather than
     * copying data, enabling direct mutation during deserialization.
     * 
     * @param stats protection stats instance to serialize/deserialize (must not be null)
     * @throws NullPointerException if stats is null
     */
    public ProtectionStatsNBT(@Nonnull ProtectionStats stats) {
        this.stats = Objects.requireNonNull(stats, "ProtectionStats cannot be null");
    }

    // -- IReadWriteNBT Implementation --
    
    @Nonnull
    @Override
    public CompoundTag writeToNBT(@Nonnull CompoundTag tag) {
        Objects.requireNonNull(tag, "CompoundTag cannot be null");
        
        // Write version for migration support
        tag.putString(TAG_VERSION, CURRENT_VERSION);
        
        // Write all protection stat fields
        tag.putInt(TAG_FIRE_PROTECTION, stats.getFireProtection());
        tag.putInt(TAG_FALL_PROTECTION, stats.getFallProtection());
        tag.putInt(TAG_BLAST_PROTECTION, stats.getBlastProtection());
        tag.putInt(TAG_PROJECTILE_PROTECTION, stats.getProjectileProtection());
        
        return tag;
    }

    @Override
    public void readFromNBT(@Nonnull CompoundTag tag, @Nonnull Version tagVersion) {
        Objects.requireNonNull(tag, "CompoundTag cannot be null");
        Objects.requireNonNull(tagVersion, "Version cannot be null");
        
        // Read all protection stat fields with defaults for missing data
        stats.setFireProtection(tag.contains(TAG_FIRE_PROTECTION) ? tag.getInt(TAG_FIRE_PROTECTION) : 0);
        stats.setFallProtection(tag.contains(TAG_FALL_PROTECTION) ? tag.getInt(TAG_FALL_PROTECTION) : 0);
        stats.setBlastProtection(tag.contains(TAG_BLAST_PROTECTION) ? tag.getInt(TAG_BLAST_PROTECTION) : 0);
        stats.setProjectileProtection(tag.contains(TAG_PROJECTILE_PROTECTION) ? tag.getInt(TAG_PROJECTILE_PROTECTION) : 0);
        
        // Apply version-specific migrations if needed
        // Example: if (tagVersion.isOlderThan(new Version("1.1.0"))) { /* migration logic */ }
    }

    // -- Getters --
    
    /**
     * @return the protection stats instance being serialized
     */
    @Nonnull
    public ProtectionStats getStats() {
        return stats;
    }

} // Class: ProtectionStatsNBT
