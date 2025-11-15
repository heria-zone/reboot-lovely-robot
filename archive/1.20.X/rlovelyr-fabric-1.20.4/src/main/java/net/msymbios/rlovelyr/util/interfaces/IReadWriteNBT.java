package net.msymbios.rlovelyr.util.interfaces;

import net.minecraft.nbt.CompoundTag;
import net.msymbios.rlovelyr.util.internal.Version;

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
    default CompoundTag writeToNBT()
    {
        return writeToNBT(new CompoundTag());
    }

    /**
     * Writes the object data to the given tag.
     *
     * @param tag the tag to write to (typically the tag to directly write unless otherwise specified).
     * @return the tag the object data was written to (should be the tag that was passed in).
     */
    CompoundTag writeToNBT(CompoundTag tag);

    /**
     * Reads the object data from the given tag.
     *
     * @param tag the tag to read from.
     * @param tagVersion the tagVersion of the tag (used to perform upgrade operations between versions).
     */
    void readFromNBT(CompoundTag tag, Version tagVersion);

} // Interface IReadWriteNBT