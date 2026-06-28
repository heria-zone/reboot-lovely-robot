package net.heriazone.hzlib.api.nbt;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * <p>Singleton registry for the current MC version string, written into every entity save.<p>
 * <p>
 * <b>Why two version fields:</b> {@code SchemaVersion} tracks data layout changes (field renames,
 * additions). {@code McVersion} records which MC release wrote the save — migration steps that must
 * behave differently per MC release (e.g. UUID encoding before/after 1.18) need this second axis.
 * Merging the two would conflate data evolution with platform evolution.
 * <p>
 * <b>Registration timing:</b> Same constraint as {@link NbtAdapterFactory} — call during the
 * earliest mod-init hook, before any entity is saved. Must return the actual running MC version,
 * never a hardcoded constant.
 */
public final class McVersionProvider {

    // -- Fields --

    private static Provider instance;

    // -- Constructor --

    private McVersionProvider() {} // Non-instantiable

    // -- Registration --

    /**
     * Registers the MC-version-specific provider. Call exactly once, at mod init.
     *
     * @param provider the version-specific implementation
     * @throws NullPointerException if provider is null
     */
    public static void register(@NotNull Provider provider) {
        instance = Objects.requireNonNull(provider, "Provider cannot be null");
    } // register ()

    // -- Query --

    /**
     * Returns the current Minecraft version string (e.g. {@code "1.21.1"}).
     * Written into {@code EntityData.McVersion} on every entity save.
     *
     * @return MC version string
     * @throws IllegalStateException if no provider has been registered
     */
    @NotNull
    public static String current() {
        if (instance == null) {
            throw new IllegalStateException(
                "[HZLib] McVersionProvider has not been registered. " +
                "The MC-version-specific module must call McVersionProvider.register() " +
                "during mod initialisation.");
        }
        return instance.getMcVersion();
    } // current ()

    // -- Provider Interface --

    /**
     * Implemented once per supported MC version in the version-scoped source set.
     * Must return the actual running MC version — not a hardcoded constant.
     */
    public interface Provider {

        /**
         * @return the current Minecraft version string, e.g. {@code "1.21.1"}
         */
        @NotNull String getMcVersion();

    } // Interface: Provider

} // Class: McVersionProvider
