package net.heriazone.hzlib.api.nbt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

/**
 * <p>Version-agnostic compound NBT wrapper — the sole point of contact between
 * the data pipeline and the MC NBT API.<p>
 * <p>
 * <b>Architecture:</b> All pipeline code ({@code DataField}, {@code EntityDataSchema},
 * {@code MigrationChain}) operates exclusively on this interface. MC-version-specific
 * implementations live in version-scoped source sets, never in Common. This isolates
 * the pipeline from NBT API changes across MC versions (1.7.10–1.21.x).
 * <p>
 * <b>UUID storage:</b> Implementations handle the version-specific storage format
 * ({@code putUUID} in 1.18+, two-long encoding before that). Callers never branch on
 * MC version to persist a UUID.
 * <p>
 * <b>Obtain instances:</b> via {@link NbtAdapterFactory#wrap(Object)} or
 * {@link NbtAdapterFactory#createEmpty()}.
 */
public interface DataCompound {

    // -- Primitives --

    void    putInt(@NotNull String key, int value);
    int     getInt(@NotNull String key, int defaultValue);

    void    putFloat(@NotNull String key, float value);
    float   getFloat(@NotNull String key, float defaultValue);

    void    putBoolean(@NotNull String key, boolean value);
    boolean getBoolean(@NotNull String key, boolean defaultValue);

    void    putString(@NotNull String key, @NotNull String value);
    @NotNull String getString(@NotNull String key, @NotNull String defaultValue);

    // -- UUID --

    /**
     * Persists a UUID using the MC-version-appropriate encoding.
     * Implementations select between {@code tag.putUUID()} (1.18+) and
     * the two-long {@code UUIDMost}/{@code UUIDLeast} format (pre-1.18).
     */
    void    putUUID(@NotNull String key, @NotNull UUID value);

    /**
     * @return the stored UUID, or {@code null} if the key is absent.
     */
    @Nullable UUID getUUID(@NotNull String key);

    // -- Nesting --

    /**
     * Returns the sub-compound at {@code key}, creating it if absent.
     * Never returns {@code null}.
     */
    @NotNull DataCompound getOrCreate(@NotNull String key);

    /**
     * Returns the sub-compound at {@code key}, or an empty compound if absent.
     * Never returns {@code null}.
     */
    @NotNull DataCompound getCompound(@NotNull String key);

    void put(@NotNull String key, @NotNull DataCompound value);

    // -- Introspection --

    boolean has(@NotNull String key);
    boolean hasCompound(@NotNull String key);

    /** Returns all top-level keys present in this compound. */
    @NotNull Set<String> keys();

    void remove(@NotNull String key);

} // Interface: DataCompound
