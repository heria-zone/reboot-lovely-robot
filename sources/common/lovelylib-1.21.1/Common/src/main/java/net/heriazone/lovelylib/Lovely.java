package net.heriazone.lovelylib;

import net.heriazone.hzlib.framework.utils.Version;
import net.heriazone.lovelylib.common.entity.definition.RobotDefinitionRegistry;
import net.heriazone.lovelylib.source.SharedRobotDefinitions;
import net.heriazone.lovelylib.source.legacy.LegacyRobotDefinitions;
import net.heriazone.lovelylib.source.reboot.RebootRobotDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for lovelylib — robot entity management and AI systems.
 * <p>
 * <b>Architecture:</b> Provides a loader-agnostic initialization contract via
 * onInitialize(). Every loader entry point (Forge constructor, Fabric onInitialize,
 * NeoForge constructor) calls this as its very first statement, before any
 * DeferredRegister or event-bus wiring, to guarantee RobotDefinitionRegistry is
 * sealed before the first deferred supplier fires.
 */
public class Lovely {

    // -- Constants --

    public static final String  VERSION      = "1.0.0";
    public static final Version DATA_VERSION = new Version("1.0.0");
    public static final String  MODID        = "lovelylib";
    public static final Logger  LOGGER       = LoggerFactory.getLogger("Lovely Lib");

    // -- State --

    private static boolean initialized = false;

    // -- Public Methods --

    /**
     * Registers all robot entity definitions and seals RobotDefinitionRegistry.
     * <p>
     * <b>Timing:</b> Must be the very first call in every loader entry point,
     * before Items.register(eventBus), Entities.register(eventBus), or any
     * equivalent Fabric registry call. On Forge/NeoForge, DeferredRegister
     * defers supplier resolution to RegisterEvent — but LegacyRobotFamilies
     * (initialized in Phase 2) must have already run before those suppliers fire.
     * <p>
     * <b>Idempotent:</b> Safe to call multiple times; initialization runs only once.
     * This prevents issues if two loaders share a classloader in test environments.
     */
    public static void onInitialize() {
        if (initialized) {
            LOGGER.warn("Lovely Lib already initialized, skipping duplicate call");
            return;
        }

        LOGGER.info("Initializing Lovely Lib version {}", VERSION);

        SharedRobotDefinitions.register();
        LegacyRobotDefinitions.register();
        RebootRobotDefinitions.register();
        RobotDefinitionRegistry.seal();

        initialized = true;
        LOGGER.info("Lovely Lib initialization complete — {} definitions registered",
            RobotDefinitionRegistry.getAll().size());
    } // onInitialize()

    /**
     * @deprecated Use onInitialize(). This stub remains to avoid breaking any
     *             call sites from before Sprint 14 until they are migrated.
     */
    @Deprecated
    public static void initialize() {
        onInitialize();
    } // initialize()

    /**
     * Get the current library version for runtime checks and logging.
     * 
     * @return library version string
     */
    public static String getVersion() {
        return VERSION;
    }

    /**
     * Check if the library has been initialized.
     * <p>
     * <b>Usage:</b> Useful for conditional logic that depends on initialization state.
     * 
     * @return true if initialize() has been called successfully
     */
    public static boolean isInitialized() {
        return initialized;
    }

    /**
     * Get the library identifier.
     * 
     * @return library identifier string
     */
    public static String getModid() {
        return MODID;
    }

} // Class: Lovely