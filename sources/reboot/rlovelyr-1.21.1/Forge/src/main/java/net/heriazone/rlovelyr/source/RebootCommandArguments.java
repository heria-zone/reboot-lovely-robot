package net.heriazone.rlovelyr.source;

import net.heriazone.rlovelyr.Reboot;
import net.heriazone.lovelylib.api.commands.ColorArgumentType;
import net.heriazone.lovelylib.api.commands.ColorArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class RebootCommandArguments {

    // -- Variables --

    /**
     * Deferred register for command argument types.
     * <p>
     * <b>Architecture:</b> Registers custom argument types with Minecraft's command
     * system, enabling client-server synchronization of command arguments.
     */
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES =
            DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, Reboot.MODID);

    /**
     * Registered ColorArgumentType for robot color selection commands.
     * <p>
     * <b>Registration:</b> Enables ColorArgumentType to be used in commands with
     * proper client-server synchronization.
     */
    public static final RegistryObject<ArgumentTypeInfo<?, ?>> COLOR_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("color", ColorArgumentTypeInfo::new);

    // -- Methods --

    public static void register (IEventBus events) {
        COMMAND_ARGUMENT_TYPES.register(events);
    } // register ()

    public static void register (FMLCommonSetupEvent event) {
        // Register ColorArgumentType with ArgumentTypeInfos for client-server sync
        event.enqueueWork(() -> {
            ArgumentTypeInfos.registerByClass(
                    ColorArgumentType.class,
                    (ArgumentTypeInfo<ColorArgumentType, ?>) COLOR_ARGUMENT_TYPE.get()
            );
            Reboot.LOGGER.info("Registered ColorArgumentType");
        });
    } // register ()

} // Class: RebootCommandArguments