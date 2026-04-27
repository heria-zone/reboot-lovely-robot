package net.heriazone.hzlib.api.services;

/**
 * Service locator for platform-specific functionality.
 * <p>
 * <b>Architecture:</b> Provides static access to platform services through
 * dependency injection pattern. Each loader registers its implementation
 * during mod initialization, enabling common code to access platform services.
 * <p>
 * <b>Initialization Order:</b> Platform services must be registered before
 * any common code attempts to use them. Typically done in mod constructor
 * or early initialization phase.
 * <p>
 * <b>Thread Safety:</b> Service registration is expected to happen during
 * single-threaded mod loading phase. No synchronization needed for access
 * since instance is set once and never changed.
 */
public class Services {

    // -- Fields --

    private static IPlatformServices instance;

    // -- Service Registration --

    /**
     * Registers the platform-specific services implementation.
     * <p>
     * <b>Initialization:</b> Called once during mod loading by each loader's
     * main class. Must be called before any common code attempts to access
     * platform services.
     * <p>
     * <b>Error Handling:</b> Throws IllegalStateException if services are
     * already registered to prevent accidental double-registration.
     *
     * @param implementation the platform-specific services implementation
     * @throws IllegalArgumentException if implementation is null
     * @throws IllegalStateException if services are already registered
     */
    public static void setInstance(IPlatformServices implementation) {
        if (implementation == null) {
            throw new IllegalArgumentException("Platform services implementation cannot be null");
        }

        if (instance != null) {
            throw new IllegalStateException("Platform services already registered: " + instance.getPlatformName());
        }

        instance = implementation;
    } // setInstance()

    // -- Service Access --

    /**
     * Gets the registered platform services implementation.
     * <p>
     * <b>Access Pattern:</b> Common code uses this method to access platform-specific
     * functionality without direct loader dependencies. Provides consistent API
     * across all three loaders.
     * <p>
     * <b>Error Handling:</b> Throws IllegalStateException if services haven't
     * been registered yet, indicating initialization order problem.
     *
     * @return the platform services implementation
     * @throws IllegalStateException if services haven't been registered
     */
    public static IPlatformServices get() {
        if (instance == null) {
            throw new IllegalStateException(
                    "Platform services not registered. " +
                            "Ensure Services.setInstance() is called during mod initialization."
            );
        }

        return instance;
    } // get()

    // -- Utility Methods --

    /**
     * Checks if platform services have been registered.
     * <p>
     * <b>Initialization Check:</b> Allows common code to verify services
     * are available before attempting to use them. Useful for optional
     * platform-dependent features.
     *
     * @return true if services are registered, false otherwise
     */
    public static boolean isRegistered() {
        return instance != null;
    } // isRegistered()

    /**
     * Gets the platform name if services are registered.
     * <p>
     * <b>Safe Access:</b> Provides platform identification without throwing
     * exceptions if services aren't registered yet. Useful for early logging
     * and debugging.
     *
     * @return platform name if registered, "Unknown" otherwise
     */
    public static String getPlatformNameSafe() {
        return instance != null ? instance.getPlatformName() : "Unknown";
    } // getPlatformNameSafe()

} // Class: Services