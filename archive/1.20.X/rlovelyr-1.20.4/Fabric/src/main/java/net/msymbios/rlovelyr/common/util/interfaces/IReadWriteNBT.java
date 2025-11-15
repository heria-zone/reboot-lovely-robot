package net.msymbios.rlovelyr.common.util.interfaces;

import net.minecraft.nbt.NbtCompound;
import net.msymbios.rlovelyr.common.util.internal.Version;

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
    default NbtCompound writeToNBT() {
        return writeToNBT(new NbtCompound());
    } // writeToNBT ()

    /**
     * Writes the object data to the given tag.
     *
     * @param tag the tag to write to (typically the tag to directly write unless otherwise specified).
     * @return the tag the object data was written to (should be the tag that was passed in).
     */
    NbtCompound writeToNBT(NbtCompound tag);

    /**
     * Reads the object data from the given tag.
     *
     * @param tag the tag to read from.
     * @param tagVersion the tagVersion of the tag (used to perform upgrade operations between versions).
     */
    void readFromNBT(NbtCompound tag, Version tagVersion);

} // Interface IReadWriteNBT