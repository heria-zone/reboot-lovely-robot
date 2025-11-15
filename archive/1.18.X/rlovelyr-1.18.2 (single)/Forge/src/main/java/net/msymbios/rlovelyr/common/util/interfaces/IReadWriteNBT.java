package net.msymbios.rlovelyr.common.util.interfaces;

import net.minecraft.nbt.CompoundTag;
import net.msymbios.rlovelyr.common.util.internal.Version;

import javax.annotation.Nonnull;

/**
 * Defines an object that should read/write NBT data.
 */
public interface IReadWriteNBT {

    // -- Methods --

    /**
     * Writes the object data to a new tag.
     *
     * @return the tag the object data was written to.
     */
    @Nonnull
    default CompoundTag writeToNBT() {
        return writeToNBT(new CompoundTag());
    } // writeToNBT ()

    /**
     * Writes the object data to the given tag.
     *
     * @param tag the tag to write to (typically the tag to directly write unless otherwise specified).
     * @return the tag the object data was written to (should be the tag that was passed in).
     */
    @Nonnull
    CompoundTag writeToNBT(@Nonnull CompoundTag tag);

    /**
     * Reads the object data from the given tag.
     *
     * @param tag the tag to read from.
     * @param tagVersion the tagVersion of the tag (used to perform upgrade operations between versions).
     */
    void readFromNBT(@Nonnull CompoundTag tag, @Nonnull Version tagVersion);

} // Interface IReadWriteNBT