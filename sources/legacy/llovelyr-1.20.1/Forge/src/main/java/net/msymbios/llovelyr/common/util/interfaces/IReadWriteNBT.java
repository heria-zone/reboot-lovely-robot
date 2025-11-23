package net.msymbios.llovelyr.common.util.interfaces;

import net.minecraft.nbt.CompoundTag;
import net.msymbios.llovelyr.common.util.internal.Version;

import javax.annotation.Nonnull;

/**
 * Establishes NBT serialization contract with version-aware upgrade support.
 * <p>
 * <b>Architecture:</b> Enables entity persistence across world saves while
 * maintaining backward compatibility. Version parameter allows data migration
 * when entity structure changes between mod releases.
 * <p>
 * <b>Implementation Pattern:</b> Implementers should write current version tag,
 * then use version comparison in readFromNBT to handle legacy data formats.
 */
public interface IReadWriteNBT {

    // -- Methods --

    /**
     * Convenience method creating new tag for serialization.
     *
     * @return the tag the object data was written to
     */
    @Nonnull
    default CompoundTag writeToNBT() {
        return writeToNBT(new CompoundTag());
    } // writeToNBT()

    /**
     * Serializes object state to NBT for world persistence.
     * <p>
     * <b>Contract:</b> Must return the same tag instance passed in, allowing
     * chained writes and parent tag population.
     *
     * @param tag the tag to write to
     * @return the tag the object data was written to (same instance as parameter)
     */
    @Nonnull
    CompoundTag writeToNBT(@Nonnull CompoundTag tag);

    /**
     * Deserializes object state from NBT with version-aware upgrade logic.
     * <p>
     * <b>Migration Strategy:</b> Compare tagVersion against current version to
     * apply transformation logic for legacy data formats.
     *
     * @param tag the tag to read from
     * @param tagVersion the version of the tag (enables upgrade operations)
     */
    void readFromNBT(@Nonnull CompoundTag tag, @Nonnull Version tagVersion);

} // Interface: IReadWriteNBT
