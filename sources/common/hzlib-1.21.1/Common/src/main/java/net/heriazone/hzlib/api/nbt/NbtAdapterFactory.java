package net.heriazone.hzlib.api.nbt;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * <p>Singleton registry for the MC-version-specific {@link DataCompound} factory.<p>
 * <p>
 * <b>Architecture:</b> Each MC-version module registers one {@link Factory}
 * implementation during the earliest mod-init hook. All subsequent pipeline calls
 * route through here, keeping {@code CompoundTag} (or its version equivalent) out
 * of HZLib Common entirely.
 * <p>
 * <b>Registration timing:</b> Must be called before any entity is loaded or
 * deserialized — register in the loader constructor / {@code onInitialize}, not in
 * {@code enqueueWork} or post-init hooks.
 * <p>
 * <b>Failure mode:</b> Calling {@link #wrap} or {@link #createEmpty} before
 * registration throws {@link IllegalStateException} with a clear message identifying
 * which MC-version module failed to register.
 */
public final class NbtAdapterFactory {

    // -- Fields --

    private static Factory instance;

    // -- Constructor --

    private NbtAdapterFactory() {} // Non-instantiable

    // -- Registration --

    /**
     * Registers the MC-version-specific factory. Call exactly once, at mod init.
     *
     * @param factory the version-specific implementation
     * @throws NullPointerException if factory is null
     */
    public static void register(@NotNull Factory factory) {
        instance = Objects.requireNonNull(factory, "Factory cannot be null");
    } // register ()

    // -- Factory Methods --

    /**
     * Wraps a MC-native compound tag object in the {@link DataCompound} abstraction.
     * The concrete type of {@code nativeCompound} is determined by the registered factory.
     *
     * @param nativeCompound the MC-native tag (e.g. {@code CompoundTag} in 1.21.1)
     * @return wrapped {@link DataCompound}
     * @throws IllegalStateException if no factory has been registered
     * @throws IllegalArgumentException if the native compound type is not supported
     */
    @NotNull
    public static DataCompound wrap(@NotNull Object nativeCompound) {
        ensureRegistered();
        return instance.wrap(nativeCompound);
    } // wrap ()

    /**
     * Creates a new, empty {@link DataCompound} backed by the registered factory.
     *
     * @return empty compound
     * @throws IllegalStateException if no factory has been registered
     */
    @NotNull
    public static DataCompound createEmpty() {
        ensureRegistered();
        return instance.createEmpty();
    } // createEmpty ()

    // -- Private Helpers --

    private static void ensureRegistered() {
        if (instance == null) {
            throw new IllegalStateException(
                "[HZLib] NbtAdapterFactory has not been registered. " +
                "The MC-version-specific module must call NbtAdapterFactory.register() " +
                "during mod initialisation before any entity is loaded.");
        }
    } // ensureRegistered ()

    // -- Factory Interface --

    /**
     * Implemented once per supported MC version in the version-scoped source set.
     */
    public interface Factory {

        /**
         * Wraps a MC-native compound tag in a {@link DataCompound}.
         *
         * @param nativeCompound the MC-native tag; cast internally to the expected type
         * @return wrapped compound
         * @throws IllegalArgumentException if the type is not the expected MC-native class
         */
        @NotNull DataCompound wrap(@NotNull Object nativeCompound);

        /**
         * Creates a new, empty {@link DataCompound}.
         *
         * @return fresh empty compound
         */
        @NotNull DataCompound createEmpty();

    } // Interface: Factory

} // Class: NbtAdapterFactory
