package net.heriazone.hzlib;

import net.fabricmc.api.ModInitializer;
import net.heriazone.hzlib.api.nbt.CompoundTagDataCompound;
import net.heriazone.hzlib.api.nbt.McVersionProvider;
import net.heriazone.hzlib.api.nbt.NbtAdapterFactory;
import net.minecraft.SharedConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>HZ Lib Fabric entry point providing foundation utilities for Heria Zone ecosystem.</p>
 * <p>
 * <b>Architecture:</b> Serves as the Fabric-specific initialization point for HZ Lib,
 * establishing core services and utilities that other mods can depend upon.
 * <p>
 * <b>Design Intent:</b> Minimal initialization focused on setting up library infrastructure
 * without game-specific functionality, maintaining clean separation between library
 * utilities and mod-specific features.
 * 
 * @version 1.0.0
 * @since 1.21.1
 * @author MSymbios
 */
public class HZLibFabric implements ModInitializer {
    
    // -- Constants --
    public static final String MOD_ID = "hzlib";
    public static final Logger LOGGER = LoggerFactory.getLogger("HZ Lib");
    
    // -- ModInitializer Implementation --
    
    @Override
    public void onInitialize() {
        LOGGER.info("HZ Lib {} initializing for Fabric", getClass().getPackage().getImplementationVersion());
        
        // Initialize core library services
        initializeLibraryServices();
        
        LOGGER.info("HZ Lib initialization complete");
    }
    
    // -- Private Methods --
    
    /**
     * Registers NBT pipeline services — {@link NbtAdapterFactory} and {@link McVersionProvider}.
     * <p>
     * Called synchronously during {@code onInitialize}, before any entity is loaded or deserialized.
     * Both registrations must precede any {@code DataCompound} usage downstream.
     */
    private void initializeLibraryServices() {
        NbtAdapterFactory.register(new NbtAdapterFactory.Factory() {
            @Override
            public net.heriazone.hzlib.api.nbt.DataCompound wrap(Object nativeCompound) {
                if (!(nativeCompound instanceof net.minecraft.nbt.CompoundTag tag)) {
                    throw new IllegalArgumentException(
                        "[HZLib] Expected CompoundTag, got: " + nativeCompound.getClass().getName());
                }
                return new CompoundTagDataCompound(tag);
            }

            @Override
            public net.heriazone.hzlib.api.nbt.DataCompound createEmpty() {
                return new CompoundTagDataCompound();
            }
        });

        McVersionProvider.register(() -> SharedConstants.getCurrentVersion().getName());

        LOGGER.debug("NBT pipeline services registered (Fabric)");
    }
    
} // Class: HZLibFabric