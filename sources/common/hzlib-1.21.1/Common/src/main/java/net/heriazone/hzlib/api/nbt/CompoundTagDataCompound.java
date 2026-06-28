package net.heriazone.hzlib.api.nbt;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * <p>{@link DataCompound} implementation backed by MC 1.18+ {@link CompoundTag}.<p>
 * <p>
 * <b>UUID encoding:</b> Uses {@code CompoundTag.putUUID} / {@code getUUID}
 * (available since 1.18 — stores as two {@code IntArray} entries). Pre-1.18
 * ports supply their own implementation using the two-long format.
 * <p>
 * <b>Nesting:</b> {@link #getOrCreate} and {@link #getCompound} always return
 * a live view — mutations to the returned compound are reflected in the parent.
 * <p>
 * <b>Registration:</b> Provided to {@link NbtAdapterFactory} as an anonymous
 * {@link NbtAdapterFactory.Factory} in each loader entry point. Not constructed
 * directly by pipeline code.
 */
public final class CompoundTagDataCompound implements DataCompound {

    // -- Fields --

    private final CompoundTag tag;

    // -- Constructors --

    /**
     * Wraps an existing {@link CompoundTag}.
     *
     * @param tag the tag to wrap; must not be null
     */
    public CompoundTagDataCompound(@NotNull CompoundTag tag) {
        this.tag = Objects.requireNonNull(tag, "CompoundTag cannot be null");
    } // Constructor: CompoundTagDataCompound ()

    /** Creates a wrapper around a new, empty {@link CompoundTag}. */
    public CompoundTagDataCompound() {
        this(new CompoundTag());
    } // Constructor: CompoundTagDataCompound ()

    // -- Package-private accessor for serialisation --

    /**
     * Returns the underlying {@link CompoundTag}.
     * Used by the pipeline entry point ({@code NativeEntity.addAdditionalSaveData})
     * to hand the tag back to Minecraft after writing — not for use in pipeline logic.
     *
     * @return the wrapped tag
     */
    @NotNull
    CompoundTag unwrap() {
        return tag;
    } // unwrap ()

    // -- DataCompound -- Primitives --

    @Override
    public void putInt(@NotNull String key, int value) {
        tag.putInt(key, value);
    } // putInt ()

    @Override
    public int getInt(@NotNull String key, int defaultValue) {
        return tag.contains(key) ? tag.getInt(key) : defaultValue;
    } // getInt ()

    @Override
    public void putFloat(@NotNull String key, float value) {
        tag.putFloat(key, value);
    } // putFloat ()

    @Override
    public float getFloat(@NotNull String key, float defaultValue) {
        return tag.contains(key) ? tag.getFloat(key) : defaultValue;
    } // getFloat ()

    @Override
    public void putBoolean(@NotNull String key, boolean value) {
        tag.putBoolean(key, value);
    } // putBoolean ()

    @Override
    public boolean getBoolean(@NotNull String key, boolean defaultValue) {
        return tag.contains(key) ? tag.getBoolean(key) : defaultValue;
    } // getBoolean ()

    @Override
    public void putString(@NotNull String key, @NotNull String value) {
        tag.putString(key, value);
    } // putString ()

    @Override
    @NotNull
    public String getString(@NotNull String key, @NotNull String defaultValue) {
        return tag.contains(key) ? tag.getString(key) : defaultValue;
    } // getString ()

    // -- DataCompound -- UUID --

    @Override
    public void putUUID(@NotNull String key, @NotNull UUID value) {
        tag.putUUID(key, value);
    } // putUUID ()

    @Override
    @Nullable
    public UUID getUUID(@NotNull String key) {
        return tag.hasUUID(key) ? tag.getUUID(key) : null;
    } // getUUID ()

    // -- DataCompound -- Nesting --

    @Override
    @NotNull
    public DataCompound getOrCreate(@NotNull String key) {
        if (!tag.contains(key)) {
            tag.put(key, new CompoundTag());
        }
        return new CompoundTagDataCompound(tag.getCompound(key));
    } // getOrCreate ()

    @Override
    @NotNull
    public DataCompound getCompound(@NotNull String key) {
        return new CompoundTagDataCompound(tag.getCompound(key));
    } // getCompound ()

    @Override
    public void put(@NotNull String key, @NotNull DataCompound value) {
        if (value instanceof CompoundTagDataCompound wrapper) {
            tag.put(key, wrapper.tag);
        } else {
            throw new IllegalArgumentException(
                "[HZLib] CompoundTagDataCompound.put() received an incompatible DataCompound " +
                "implementation: " + value.getClass().getName() +
                ". All DataCompound instances in 1.21.1 must be CompoundTagDataCompound.");
        }
    } // put ()

    // -- DataCompound -- Introspection --

    @Override
    public boolean has(@NotNull String key) {
        return tag.contains(key);
    } // has ()

    @Override
    public boolean hasCompound(@NotNull String key) {
        return tag.contains(key, 10); // NBT type 10 = TAG_Compound
    } // hasCompound ()

    @Override
    @NotNull
    public Set<String> keys() {
        return tag.getAllKeys();
    } // keys ()

    @Override
    public void remove(@NotNull String key) {
        tag.remove(key);
    } // remove ()

} // Class: CompoundTagDataCompound
