package net.heriazone.lovelylib.common.commands;

import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.heriazone.lovelylib.api.commands.ColorArgumentType;
import net.heriazone.lovelylib.common.shared.LovelyIdentifier;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;

/**
 * Fabric-specific command argument type registration.
 * <p>
 * <b>Architecture:</b> Registers custom ColorArgumentType with Fabric's command
 * system, enabling client-server synchronization of robot color arguments.
 * <p>
 * <b>Design Decision:</b> Uses SingletonArgumentInfo since ColorArgumentType
 * has no parameters - each instance is functionally identical.
 */
public class LovelyCommandArguments {

    // -- Methods --

    /**
     * Registers ColorArgumentType with Fabric's argument registry.
     * <p>
     * <b>Usage:</b> Call during mod initialization phase.
     * <p>
     * <b>Synchronization:</b> SingletonArgumentInfo handles network serialization
     * for parameterless argument types, creating new instances as needed.
     */
    public static void register() {
        ArgumentTypeRegistry.registerArgumentType(
                LovelyIdentifier.getId(LovelyIdentifier.STAT_COLOR),
                ColorArgumentType.class,
                SingletonArgumentInfo.contextFree(ColorArgumentType::color)
        );
    } // register()

} // Class: LovelyCommandArguments