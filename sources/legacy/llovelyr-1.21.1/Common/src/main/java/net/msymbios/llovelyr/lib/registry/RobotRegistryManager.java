package net.msymbios.llovelyr.lib.registry;

import net.minecraft.server.level.ServerLevel;
import net.msymbios.llovelyr.framework.registry.OwnerRobotRegistry;

import org.jetbrains.annotations.*;
import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Manages server-side robot registries with per-server isolation.</p>
 * <p>
 * <b>Architecture:</b> Provides singleton access to robot registries keyed by server
 * directory path. Enables multiple servers in same JVM to maintain independent robot
 * tracking without cross-contamination.
 * <p>
 * <b>Design Decision:</b> Server directory as key rather than ServerLevel instance
 * prevents memory leaks from holding server references and enables registry persistence
 * across server restarts.
 * <p>
 * <b>Thread Safety:</b> Uses ConcurrentHashMap for thread-safe registry access from
 * multiple server threads. Registry creation is synchronized to prevent duplicate
 * instances.
 */
public class RobotRegistryManager {

    // -- Static Fields --

    private static final Map<String, OwnerRobotRegistry> serverRegistries = new ConcurrentHashMap<>();

    // -- Private Constructor --

    /**
     * Private constructor prevents instantiation of utility class.
     */
    private RobotRegistryManager() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    } // Constructor: RobotRegistryManager ()

    // -- Public Methods --

    /**
     * Retrieves or creates robot registry for specified server level.
     * <p>
     * <b>Thread Safety:</b> Registry creation is synchronized per server directory.
     * Multiple threads requesting same server's registry will receive same instance.
     * <p>
     * <b>Performance:</b> O(1) lookup after initial creation. Synchronization only
     * occurs during first access per server.
     *
     * @param level server level to get registry for (must not be null)
     * @return robot registry for the server
     * @throws NullPointerException if level is null
     */
    @NotNull
    public static OwnerRobotRegistry getRegistry(@NotNull ServerLevel level) {
        Objects.requireNonNull(level, "ServerLevel cannot be null");

        String serverId = getServerId(level);

        // Use computeIfAbsent for atomic get-or-create
        return serverRegistries.computeIfAbsent(serverId, key -> new OwnerRobotRegistry());
    } // getRegistry ()

    /**
     * Clears robot registry for specified server.
     * <p>
     * <b>Use Case:</b> Called on server shutdown to release resources and prevent
     * stale data from persisting across server restarts.
     * <p>
     * <b>State Impact:</b> Removes registry from manager. All robot tracking for
     * that server is lost. Entities should unregister before server shutdown.
     *
     * @param serverId server identifier (typically server directory path)
     */
    public static void clearRegistry(@NotNull String serverId) {
        Objects.requireNonNull(serverId, "Server ID cannot be null");

        OwnerRobotRegistry registry = serverRegistries.remove(serverId);
        if (registry != null) {
            // Optional: perform cleanup on registry if needed
            registry.cleanupInvalidEntries();
        }
    } // clearRegistry ()

    /**
     * Clears robot registry for specified server level.
     * <p>
     * <b>Use Case:</b> Convenience method for clearing registry using ServerLevel.
     *
     * @param level server level to clear registry for (must not be null)
     * @throws NullPointerException if level is null
     */
    public static void clearRegistry(@NotNull ServerLevel level) {
        Objects.requireNonNull(level, "ServerLevel cannot be null");
        clearRegistry(getServerId(level));
    } // clearRegistry ()

    /**
     * Clears all registries across all servers.
     * <p>
     * <b>Use Case:</b> Called on mod shutdown or for testing cleanup.
     * <p>
     * <i>Note:</i> Use with caution - this affects all servers in the JVM.
     */
    public static void clearAllRegistries() {
        serverRegistries.values().forEach(OwnerRobotRegistry::cleanupInvalidEntries);
        serverRegistries.clear();
    } // clearAllRegistries ()

    // -- Private Helper Methods --

    /**
     * Extracts server identifier from server level.
     * <p>
     * <b>Implementation:</b> Uses server directory path as unique identifier.
     * This ensures different servers (even on same machine) have separate registries.
     * <p>
     * <b>Fallback:</b> If server directory unavailable, uses server instance hash.
     *
     * @param level server level to extract ID from
     * @return unique server identifier
     */
    @NotNull
    private static String getServerId(@NotNull ServerLevel level) {
        try {
            // Get server directory path as unique identifier
            File serverDir = level.getServer().getServerDirectory().toFile();
            return serverDir.getAbsolutePath();
        } catch (Exception e) {
            // Fallback to server instance hash if directory unavailable
            return "server_" + level.getServer().hashCode();
        }
    } // getServerId ()

} // Class: RobotRegistryManager
