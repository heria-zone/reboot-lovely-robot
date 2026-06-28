package net.heriazone.hzlib;

import net.heriazone.hzlib.api.nbt.CompoundTagDataCompound;
import net.heriazone.hzlib.api.nbt.McVersionProvider;
import net.heriazone.hzlib.api.nbt.NbtAdapterFactory;
import net.minecraft.SharedConstants;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>HZ Lib Forge entry point providing foundation utilities for Heria Zone ecosystem.</p>
 * <p>
 * <b>Architecture:</b> Serves as the Forge-specific initialization point for HZ Lib,
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
@Mod("hzlib")
public class HZLibForge {
    
    // -- Constants --
    public static final String MOD_ID = "hzlib";
    public static final Logger LOGGER = LoggerFactory.getLogger("HZ Lib");
    
    // -- Constructor --
    
    public HZLibForge() {
        LOGGER.info("HZ Lib {} initializing for Forge", getClass().getPackage().getImplementationVersion());

        // Register NBT pipeline services synchronously — must complete before enqueueWork
        // and before any entity is loaded during world join.
        initializeLibraryServices();

        // Get the mod event bus
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onCommonSetup);

        LOGGER.info("HZ Lib Forge constructor complete");
    }
    
    // -- Event Handlers --
    
    /**
     * Handles common setup phase for library initialization.
     * <p>
     * <b>Design Intent:</b> Establishes foundation services that other mods
     * can rely upon, maintaining minimal footprint and clean initialization.
     * 
     * @param event the common setup event
     */
    private void onCommonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HZ Lib common setup phase starting");
        
        event.enqueueWork(() -> {
            LOGGER.debug("HZ Lib Forge common setup work enqueued");
        });
        
        LOGGER.info("HZ Lib common setup phase complete");
    }
    
    // -- Private Methods --
    
    /**
     * Registers NBT pipeline services — {@link NbtAdapterFactory} and {@link McVersionProvider}.
     * <p>
     * Called in the constructor body (synchronous, before {@code enqueueWork}) so both
     * registrations are complete before any entity is loaded during world join.
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

        LOGGER.debug("NBT pipeline services registered (Forge)");
    }
    
} // Class: HZLibForge