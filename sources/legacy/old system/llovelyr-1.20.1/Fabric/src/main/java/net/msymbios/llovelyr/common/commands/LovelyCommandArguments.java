package net.msymbios.llovelyr.common.commands;

import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.msymbios.llovelyr.common.shared.LovelyIdentifier;

public class LovelyCommandArguments {

    // -- Methods --

    public static void register () {

        ArgumentTypeRegistry.registerArgumentType(LovelyIdentifier.getId(LovelyIdentifier.STAT_COLOR),
                ColorArgumentType.class,
                ConstantArgumentSerializer.of(ColorArgumentType::color)
        );

    } // register ()

} // Class: LovelyCommandArguments