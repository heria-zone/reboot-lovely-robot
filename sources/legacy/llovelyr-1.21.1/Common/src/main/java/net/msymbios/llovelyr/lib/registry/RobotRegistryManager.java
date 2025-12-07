package net.msymbios.llovelyr.lib.registry;

import net.minecraft.server.level.ServerLevel;
import net.msymbios.llovelyr.framework.registry.OwnerRobotRegistry;

import org.jetbrains.annotations.*;
import java.util.Objects;

/**
 * <p>Manages server-side robot registries with disk persistence.</p>
 * <p>
 * <b>Architecture:</b> Uses Minecraft's SavedData system to persist registry
 * across server restarts and world reloads. Registry data is automatically
 * loaded from disk when accessed.
 * <p>
 * <b>Design Decision:</b> Delegates to SavedData for persistence rather than
 * maintaining in-memory cache. This ensures registry is always synchronized
 * with disk and survives world reloads.
 * <p>
 * <b>Thread Safety:</b> SavedData is accessed only from server thread, ensuring
 * thread-safe registry operations without additional synchronization.
 */
public class RobotRegistryManager {

    // -- Private Constructor --

    /**
     * Private constructor prevents instantiation of utility class.
     */
    private RobotRegistryManager() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    } // Constructor: RobotRegistryManager ()

    // -- Public Methods --

    /**
     * Retrieves robot registry for specified server level.
     * <p>
     * <b>Persistence:</b> Registry is automatically loaded from disk if exists,
     * or created new if first access. Changes are automatically saved to disk.
     * <p>
     * <b>Thread Safety:</b> Must be called from server thread only.
     * <p>
     * <b>Performance:</b> O(1) lookup via SavedData system.
     *
     * @param level server level to get registry for (must not be null)
     * @return robot registry for the server (loaded from disk or newly created)
     * @throws NullPointerException if level is null
     */
    @NotNull
    public static OwnerRobotRegistry getRegistry(@NotNull ServerLevel level) {
        Objects.requireNonNull(level, "ServerLevel cannot be null");
        
        // Get or create saved data (automatically loads from disk)
        RobotRegistrySavedData savedData = RobotRegistrySavedData.get(level);
        return savedData.getRegistry();
    } // getRegistry ()

    /**
     * Marks registry as dirty to trigger save to disk.
     * <p>
     * <b>Use Case:</b> Called after registry modifications (register/unregister)
     * to ensure changes are persisted to disk.
     * <p>
     * <b>Performance:</b> Actual save happens asynchronously, this just marks dirty.
     *
     * @param level server level whose registry was modified
     */
    public static void markDirty(@NotNull ServerLevel level) {
        Objects.requireNonNull(level, "ServerLevel cannot be null");
        
        RobotRegistrySavedData savedData = RobotRegistrySavedData.get(level);
        savedData.setDirty();
    } // markDirty ()

} // Class: RobotRegistryManager
