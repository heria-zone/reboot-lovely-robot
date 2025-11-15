package net.msymbios.rlovelyr.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.common.network.NetworkHandler;
import net.msymbios.rlovelyr.common.network.messages.SetLevelCommandMessage;
import net.msymbios.rlovelyr.common.network.messages.SetColorCommandMessage;
import net.msymbios.rlovelyr.entity.internal.enums.EntityTexture;

import javax.annotation.Nonnull;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * Handles the setup of commands.
 */
@Mod.EventBusSubscriber(modid = LovelyRobot.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LovelyRobotCommands {

    // -- Methods --

    /**
     * Registers argument types.
     */
    public static void register() {
        ArgumentTypes.register(LovelyRobot.MODID + ":texture", EntityTextureArgument.class, new EntityTextureArgument.Serializer());
    } // register ()

    /**
     * Registers the custom commands.
     */
    @SubscribeEvent
    public static void onRegisterCommands(@Nonnull RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                literal(LovelyRobot.MODID).requires(source -> source.hasPermission(2))
                        .then(literal("set").then(literal("color").then(argument("texture", new EntityTextureArgument()).executes(LovelyRobotCommands::executeColorCommand))))
                        .then(literal("set").then(literal("level").then(argument("value", IntegerArgumentType.integer()).executes(LovelyRobotCommands::executeLevelCommand))))
        );
                        //.then(literal("xp").then(argument("value", IntegerArgumentType.integer(0)).executes(LovelyRobotCommands::executeXpCommand))));
    } // onRegisterCommands ()

    /**
     * Executes the set color commands.
     *
     */
    private static int executeColorCommand(@Nonnull CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Player player = (Player) source.getEntity();

        if (player == null) return 1;
        EntityTexture entityTexture = context.getArgument("texture", EntityTexture.class);
        NetworkHandler.sendToClient(player, new SetColorCommandMessage(entityTexture.getName()));

        return 0;
    } // executeColorCommand ()

    /**
     * Executes the set level commands.
     */
    private static int executeLevelCommand(@Nonnull CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Player player = (Player) source.getEntity();

        if (player == null) return 1;
        int value = context.getArgument("value", Integer.class);
        NetworkHandler.sendToClient(player, new SetLevelCommandMessage(value));

        return 0;
    } // executeLevelCommand ()

    /**
     * Executes the set xp commands.
     */
    private static int executeXpCommand(@Nonnull CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Player player = (Player) source.getEntity();

        if (player == null) return 1;
        int value = context.getArgument("value", Integer.class);
        //NetworkHandler.sendToClient(player, new SetXpCommandMessage(level, value));

        return 0;
    } // executeXpCommand ()

    // -- Classes --

    /**
     * Represents a command argument of an entity texture.
     */
    public static class EntityTextureArgument implements ArgumentType<EntityTexture> {

        /**
         * Examples of EntityTexture.
         */
        private static final Collection<String> EXAMPLES = Arrays.asList("yellow", "lime", "blue");

        @Override
        public EntityTexture parse(StringReader stringReader) throws CommandSyntaxException {
            String argString = stringReader.readUnquotedString();
            return EntityTexture.find(argString.toLowerCase());
        } // parse ()

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder suggestionsBuilder) {
            return SharedSuggestionProvider.suggest(EntityTexture.VALUES.stream().map(texture -> texture.getName().toLowerCase()), suggestionsBuilder);
        } // listSuggestions ()

        @Override
        public Collection<String> getExamples() {
            return EXAMPLES;
        } // getExamples ()

        public static class Serializer implements ArgumentSerializer<EntityTextureArgument> {
            @Override
            public void serializeToNetwork(EntityTextureArgument argument, FriendlyByteBuf buffer) {}

            @Override
            public EntityTextureArgument deserializeFromNetwork(FriendlyByteBuf buffer) {
                return new EntityTextureArgument();
            } // deserializeFromNetwork ()

            @Override
            public void serializeToJson(EntityTextureArgument argument, JsonObject json) {}

        } // class Serializer

    } // Class EntityTextureArgument

} // LovelyRobotCommands