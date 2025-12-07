package net.msymbios.llovelyr.source;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.msymbios.llovelyr.LovelyConstant;
import net.msymbios.llovelyr.common.commands.ColorArgumentType;
import net.msymbios.llovelyr.common.commands.ColorArgumentTypeInfo;

/**
 * Registry for custom command argument types (NeoForge).
 * <p>
 * <b>Architecture:</b> Registers custom argument types with Minecraft's command
 * system, enabling client-server synchronization of command arguments.
 */
public class LovelyCommandArguments {

    // -- Variables --

    /**
     * Deferred register for command argument types.
     * <p>
     * <b>Architecture:</b> Registers custom argument types with Minecraft's command
     * system, enabling client-server synchronization of command arguments.
     */
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES =
            DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, LovelyConstant.MODID);

    /**
     * Registered ColorArgumentType for robot color selection commands.
     * <p>
     * <b>Registration:</b> Enables ColorArgumentType to be used in commands with
     * proper client-server synchronization.
     */
    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<?, ?>> COLOR_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("color", () -> new ColorArgumentTypeInfo());

    // -- Methods --

    /**
     * Registers command argument types with event bus.
     *
     * @param events NeoForge event bus
     */
    public static void register (IEventBus events) {
        COMMAND_ARGUMENT_TYPES.register(events);
        LovelyConstant.LOGGER.info("Registering Command Arguments: " + LovelyConstant.MODID);
    } // register ()

    /**
     * Registers ColorArgumentType with ArgumentTypeInfos for client-server sync.
     * <p>
     * <b>Thread Safety:</b> Uses enqueueWork to ensure registration happens
     * on the main thread.
     *
     * @param event common setup event
     */
    public static void register (FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ArgumentTypeInfos.registerByClass(
                    ColorArgumentType.class,
                    (ArgumentTypeInfo<ColorArgumentType, ?>) COLOR_ARGUMENT_TYPE.get()
            );
            LovelyConstant.LOGGER.info("Registered ColorArgumentType");
        });
    } // register ()

} // Class: LovelyCommandArguments
