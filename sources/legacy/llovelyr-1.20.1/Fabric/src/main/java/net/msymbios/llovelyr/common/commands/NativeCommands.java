package net.msymbios.llovelyr.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.msymbios.llovelyr.common.entity.enums.EntityVariant;
import net.msymbios.llovelyr.common.entity.internal.InternalEntity;
import net.msymbios.llovelyr.common.shared.LovelyIdentifier;
import net.msymbios.llovelyr.common.utils.internal.Utility;
import net.msymbios.llovelyr.framework.registry.*;
import net.msymbios.llovelyr.lib.entity.features.LevelFeature;
import net.msymbios.llovelyr.lib.registry.RobotRegistryManager;
import net.msymbios.llovelyr.source.entity.common.LovelyRobotEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class NativeCommands {

    // -- API Methods --

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("llovely")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(buildCrossCommands())
                        .then(buildTargetCommands())
                        .then(buildOwnerCommands())
                        .then(buildReloadCommand())
        );
    } // register ()

    // -- Internal Methods --

    private static ArgumentBuilder<ServerCommandSource, ?> buildCrossCommands() {
        return CommandManager.literal("robot")
                .then(buildCrossAddCommands())
                .then(buildCrossGetCommands())
                .then(buildCrossSetCommands())
                .then(CommandManager.literal("recall")
                        .executes(NativeCommands::executeCrosshairRecall)
                )
                .then(CommandManager.literal("heal")
                        .executes(NativeCommands::executeCrosshairHeal)
                )
                .then(CommandManager.literal("stats")
                        .executes(NativeCommands::executeCrosshairStats)
                );
    } // buildCrossCommands()

    private static ArgumentBuilder<ServerCommandSource, ?> buildCrossAddCommands() {
        return CommandManager.literal("add")
                .then(buildCrossAddCombatCommands());
    } // buildCrossAddCommands()

    private static ArgumentBuilder<ServerCommandSource, ?> buildCrossSetCommands() {
        return CommandManager.literal("set")
                .then(buildCrossSetCombatCommands())
                .then(buildCrossSetAttributeCommands())
                .then(buildCrossSetProtectionCommands())
                .then(buildCrossSetAppearanceCommands())
                .then(buildCrossSetIdentifierCommands());
    } // buildCrossSetCommands ()


    private static ArgumentBuilder<ServerCommandSource, ?> buildTargetCommands() {
        return CommandManager.literal("target")
                .then(CommandManager.argument("targets", EntityArgumentType.entities())
                        .then(buildTargetAddCommands())
                        .then(buildTargetSetCommands())
                        .then(CommandManager.literal("heal").executes(NativeCommands::executeTargetHeal))
                        .then(CommandManager.literal("teleport")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .executes(NativeCommands::executeTargetTeleport)
                                )
                        )
                        .then(CommandManager.literal("recall")
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .executes(NativeCommands::executeTargetRecall)
                                )
                        )
                        .then(CommandManager.literal("ownership")
                                .then(CommandManager.argument("to_player", EntityArgumentType.player())
                                        .executes(NativeCommands::executeTargetTransfer)
                                )
                        )
                );
    } // buildTargetCommands()

    private static ArgumentBuilder<ServerCommandSource, ?> buildTargetAddCommands() {
        return CommandManager.literal("add")
                .then(buildTargetAddCombatCommands());
    } // buildTargetAddCommands()

    private static ArgumentBuilder<ServerCommandSource, ?> buildTargetSetCommands() {
        return CommandManager.literal("set")
                .then(buildTargetSetCombatCommands())
                .then(buildTargetSetAttributeCommands())
                .then(buildTargetSetProtectionCommands())
                .then(buildTargetSetAppearanceCommands())
                .then(buildTargetSetIdentifierCommands());
    } // buildTargetSetCommands ()


    private static ArgumentBuilder<ServerCommandSource, ?> buildOwnerCommands() {
        return CommandManager.literal("owner")
                .then(buildOwnerListCommands())
                .then(buildOwnerAddCommands())
                .then(buildOwnerSetCommands())
                .then(CommandManager.literal("teleport")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .suggests(NativeCommands::suggestPlayerNames)
                                .then(CommandManager.argument("robot_index", IntegerArgumentType.integer(0))
                                        .suggests(NativeCommands::suggestRobotIndices)
                                        .executes(NativeCommands::executeOwnerTeleport)
                                )
                        )
                )
                .then(CommandManager.literal("recall")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .suggests(NativeCommands::suggestPlayerNames)
                                .then(CommandManager.argument("robot_index", IntegerArgumentType.integer(0))
                                        .suggests(NativeCommands::suggestRobotIndices)
                                        .executes(NativeCommands::executeOwnerRecall)
                                )
                        )
                )
                .then(CommandManager.literal("heal")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .suggests(NativeCommands::suggestPlayerNames)
                                .then(CommandManager.argument("robot_index", IntegerArgumentType.integer(0))
                                        .suggests(NativeCommands::suggestRobotIndices)
                                        .executes(NativeCommands::executeOwnerHeal)
                                )
                        )
                )
                .then(CommandManager.literal("healall")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .suggests(NativeCommands::suggestPlayerNames)
                                .executes(NativeCommands::executeOwnerHealAll)
                        )
                )
                .then(CommandManager.literal("stats")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .suggests(NativeCommands::suggestPlayerNames)
                                .then(CommandManager.argument("robot_index", IntegerArgumentType.integer(0))
                                        .suggests(NativeCommands::suggestRobotIndices)
                                        .executes(NativeCommands::executeOwnerStats)
                                )
                        )
                )
                .then(CommandManager.literal("transfer")
                        .then(CommandManager.argument("from_player", EntityArgumentType.player())
                                .suggests(NativeCommands::suggestPlayerNames)
                                .then(CommandManager.argument("robot_index", IntegerArgumentType.integer(0))
                                        .suggests(NativeCommands::suggestRobotIndices)
                                        .then(CommandManager.argument("to_player", EntityArgumentType.player())
                                                .executes(NativeCommands::executeOwnerTransfer)
                                        )
                                )
                        )
                );
    } // buildOwnerCommands()

    private static ArgumentBuilder<ServerCommandSource, ?> buildOwnerListCommands() {
        return CommandManager.literal("list")
                .then(CommandManager.literal("player")
                        .executes(NativeCommands::executeListOwners)
                )
                .then(CommandManager.literal("robot")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(NativeCommands::executeListPlayerRobots)
                        )
                );
    } // buildOwnerListCommands ()

    private static ArgumentBuilder<ServerCommandSource, ?> buildOwnerAddCommands() {
        return CommandManager.literal("add")
                .then(buildOwnerAddCombatCommands());
    } // buildOwnerAddCommands ()

    private static ArgumentBuilder<ServerCommandSource, ?> buildOwnerSetCommands() {
        return CommandManager.literal("set")
                .then(buildOwnerSetCombatCommands())
                .then(buildOwnerSetAttributeCommands())
                .then(buildOwnerSetProtectionCommands())
                .then(buildOwnerSetAppearanceCommands())
                .then(buildOwnerSetIdentifierCommands());
    } // buildOwnerSetCommands ()

    // TARGET COMMANDS
    private static ArgumentBuilder<ServerCommandSource, ?> buildTargetAddCombatCommands () {
        return CommandManager.literal("combat")
                .then(CommandManager.literal("exp")
                        .then(CommandManager.argument("exp_value", IntegerArgumentType.integer(0))
                                .suggests(NativeCommands::suggestMinMaxExp)
                                .executes(NativeCommands::executeTargetAddXP)
                        )
                );
    } // buildTargetAddCombatCommands ()

    private static ArgumentBuilder<ServerCommandSource, ?> buildTargetSetCombatCommands () {
        return CommandManager.literal("combat")
                .then(CommandManager.literal("all")
                        .then(CommandManager.argument("level", IntegerArgumentType.integer(1))
                                .suggests(NativeCommands::suggestMinMaxLevel)
                                .then(CommandManager.argument("exp", IntegerArgumentType.integer(0))
                                        .suggests(NativeCommands::suggestMinMaxExpForLevel)
                                        .executes(NativeCommands::executeTargetSetAllCombat)
                                )
                        )
                )
                .then(CommandManager.literal("exp")
                        .then(CommandManager.argument("exp_value", IntegerArgumentType.integer(0))
                                .suggests(NativeCommands::suggestMinMaxExp)
                                .executes(NativeCommands::executeTargetSetXP)
                        )
                )
                .then(CommandManager.literal("level")
                        .then(CommandManager.argument("level_value", IntegerArgumentType.integer(1))
                                .suggests(NativeCommands::suggestMinMaxLevel)
                                .executes(NativeCommands::executeTargetSetLevel)
                        )
                );
    } // buildTargetSetCombatCommands ()

    private static ArgumentBuilder<ServerCommandSource, ?> buildTargetSetAttributeCommands() {
        return CommandManager.literal("attribute")
                .then(CommandManager.literal("all")
                        .then(CommandManager.argument("hp", IntegerArgumentType.integer(1))
                                .then(CommandManager.argument("attack", IntegerArgumentType.integer(1))
                                        .then(CommandManager.argument("defense", IntegerArgumentType.integer(0))
                                                .then(CommandManager.argument("speed", IntegerArgumentType.integer(1))
                                                        .executes(NativeCommands::executeTargetSetAllAttributes)
                                                )
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("hp")
                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                                .executes(NativeCommands::executeTargetSetHP)
                        )
                )
                .then(CommandManager.literal("attack")
                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                                .executes(NativeCommands::executeTargetSetAttack)
                        )
                )
                .then(CommandManager.literal("defense")
                        .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                .executes(NativeCommands::executeTargetSetDefense)
                        )
                )
                .then(CommandManager.literal("speed")
                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                                .executes(NativeCommands::executeTargetSetSpeed)
                        )
                );
    } // buildTargetSetAttributeCommands()

    private static ArgumentBuilder<ServerCommandSource, ?> buildTargetSetProtectionCommands() {
        return CommandManager.literal("protection")
                .then(CommandManager.literal("all")
                        .then(CommandManager.argument("fire", IntegerArgumentType.integer(0))
                                .suggests((ctx, builder) -> suggestMinMaxProtection(ctx, builder, "fire"))
                                .then(CommandManager.argument("fall", IntegerArgumentType.integer(0))
                                        .suggests((ctx, builder) -> suggestMinMaxProtection(ctx, builder, "fall"))
                                        .then(CommandManager.argument("blast", IntegerArgumentType.integer(0))
                                                .suggests((ctx, builder) -> suggestMinMaxProtection(ctx, builder, "blast"))
                                                .then(CommandManager.argument("projectile", IntegerArgumentType.integer(0))
                                                        .suggests((ctx, builder) -> suggestMinMaxProtection(ctx, builder, "projectile"))
                                                        .executes(NativeCommands::executeTargetSetAllProtections)
                                                )
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("fire")
                        .then(CommandManager.argument("level", IntegerArgumentType.integer(0))
                                .suggests((ctx, builder) -> suggestMinMaxProtection(ctx, builder, "fire"))
                                .executes(NativeCommands::executeTargetSetFireProtection)
                        )
                )
                .then(CommandManager.literal("fall")
                        .then(CommandManager.argument("level", IntegerArgumentType.integer(0))
                                .suggests((ctx, builder) -> suggestMinMaxProtection(ctx, builder, "fall"))
                                .executes(NativeCommands::executeTargetSetFallProtection)
                        )
                )
                .then(CommandManager.literal("blast")
                        .then(CommandManager.argument("level", IntegerArgumentType.integer(0))
                                .suggests((ctx, builder) -> suggestMinMaxProtection(ctx, builder, "blast"))
                                .executes(NativeCommands::executeTargetSetBlastProtection)
                        )
                )
                .then(CommandManager.literal("projectile")
                        .then(CommandManager.argument("level", IntegerArgumentType.integer(0))
                                .suggests((ctx, builder) -> suggestMinMaxProtection(ctx, builder, "projectile"))
                                .executes(NativeCommands::executeTargetSetProjectileProtection)
                        )
                );
    } // buildTargetSetProtectionCommands()

    private static ArgumentBuilder<ServerCommandSource, ?> buildTargetSetAppearanceCommands() {
        return CommandManager.literal("appearance")
                .then(CommandManager.argument("color", ColorArgumentType.color())
                        .executes(NativeCommands::executeTargetSetAppearance)
                );
    } // buildTargetSetAppearanceCommands()

    private static ArgumentBuilder<ServerCommandSource, ?> buildTargetSetIdentifierCommands() {
        return CommandManager.literal("identifier")
                .then(CommandManager.argument("name", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(NativeCommands::executeTargetSetIdentifier)
                );
    } // buildTargetSetIdentifierCommands()

    // CROSS-AIR COMMANDS
    private static ArgumentBuilder<ServerCommandSource, ?> buildCrossAddCombatCommands() {
        return CommandManager.literal("combat")
                .then(CommandManager.literal("exp")
                        .then(CommandManager.argument("exp_value", IntegerArgumentType.integer(0))
                                .suggests(NativeCommands::suggestMaxExp)
                                .executes(NativeCommands::executeCrosshairAddXP)
                        )
                );
    } // buildRobotCombatCommands()

    private static ArgumentBuilder<ServerCommandSource, ?> buildCrossSetCombatCommands () {
        return CommandManager.literal("combat")
                .then(CommandManager.literal("all")
                        .then(CommandManager.argument("level", IntegerArgumentType.integer(1))
                                .suggests(NativeCommands::suggestMaxLevel)
                                .then(CommandManager.argument("exp", IntegerArgumentType.integer(0))
                                        .suggests(NativeCommands::suggestMaxExpForLevel)
                                        .executes(NativeCommands::executeSetAllCombat)
                                )
                        )
                )
                .then(CommandManager.literal("exp")
                        .then(CommandManager.argument("exp_value", IntegerArgumentType.integer(0))
                                .suggests(NativeCommands::suggestMaxExp)
                                .executes(NativeCommands::executeCrosshairSetXP)
                        )
                )
                .then(CommandManager.literal("level")
                        .then(CommandManager.argument("level_value", IntegerArgumentType.integer(1))
                                .suggests(NativeCommands::suggestMaxLevel)
                                .executes(NativeCommands::executeCrosshairSetLevel)
                        )
                );
    } // buildCrossSetCombatCommands ()

    private static ArgumentBuilder<ServerCommandSource, ?> buildCrossSetAttributeCommands() {
        return CommandManager.literal("attribute")
                .then(CommandManager.literal("all")
                        .then(CommandManager.argument("hp", IntegerArgumentType.integer(1))
                                .then(CommandManager.argument("attack", IntegerArgumentType.integer(1))
                                        .then(CommandManager.argument("defense", IntegerArgumentType.integer(0))
                                                .then(CommandManager.argument("speed", IntegerArgumentType.integer(1))
                                                        .executes(NativeCommands::executeCrosshairSetAllAttributes)
                                                )
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("hp")
                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                                .executes(NativeCommands::executeCrosshairSetHP)
                        )
                )
                .then(CommandManager.literal("attack")
                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                                .executes(NativeCommands::executeCrosshairSetAttack)
                        )
                )
                .then(CommandManager.literal("defense")
                        .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                .executes(NativeCommands::executeCrosshairSetDefense)
                        )
                )
                .then(CommandManager.literal("speed")
                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                                .executes(NativeCommands::executeCrosshairSetSpeed)
                        )
                );
    } // buildCrossSetAttributeCommands()

    private static ArgumentBuilder<ServerCommandSource, ?> buildCrossSetProtectionCommands() {
        return CommandManager.literal("protection")
                .then(CommandManager.literal("all")
                        .then(CommandManager.argument("fire", IntegerArgumentType.integer(0))
                                .suggests(NativeCommands::suggestMaxFireProtection)
                                .then(CommandManager.argument("fall", IntegerArgumentType.integer(0))
                                        .suggests(NativeCommands::suggestMaxFallProtection)
                                        .then(CommandManager.argument("blast", IntegerArgumentType.integer(0))
                                                .suggests(NativeCommands::suggestMaxBlastProtection)
                                                .then(CommandManager.argument("projectile", IntegerArgumentType.integer(0))
                                                        .suggests(NativeCommands::suggestMaxProjectileProtection)
                                                        .executes(NativeCommands::executeCrosshairSetAllProtections)
                                                )
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("fire")
                        .then(CommandManager.argument("level", IntegerArgumentType.integer(0))
                                .suggests(NativeCommands::suggestMaxFireProtection)
                                .executes(NativeCommands::executeCrosshairSetFireProtection)
                        )
                )
                .then(CommandManager.literal("fall")
                        .then(CommandManager.argument("level", IntegerArgumentType.integer(0))
                                .suggests(NativeCommands::suggestMaxFallProtection)
                                .executes(NativeCommands::executeCrosshairSetFallProtection)
                        )
                )
                .then(CommandManager.literal("blast")
                        .then(CommandManager.argument("level", IntegerArgumentType.integer(0))
                                .suggests(NativeCommands::suggestMaxBlastProtection)
                                .executes(NativeCommands::executeCrosshairSetBlastProtection)
                        )
                )
                .then(CommandManager.literal("projectile")
                        .then(CommandManager.argument("level", IntegerArgumentType.integer(0))
                                .suggests(NativeCommands::suggestMaxProjectileProtection)
                                .executes(NativeCommands::executeCrosshairSetProjectileProtection)
                        )
                );
    } // buildCrossSetProtectionCommands()

    private static ArgumentBuilder<ServerCommandSource, ?> buildCrossSetAppearanceCommands() {
        return CommandManager.literal("appearance")
                .then(CommandManager.argument("color", ColorArgumentType.color())
                        .executes(NativeCommands::executeCrosshairSetAppearance)
                );
    } // buildCrossSetAppearanceCommands()

    private static ArgumentBuilder<ServerCommandSource, ?> buildCrossSetIdentifierCommands() {
        return CommandManager.literal("identifier")
                .then(CommandManager.argument("name", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(NativeCommands::executeCrosshairSetIdentifier)
                );
    } // buildCrossSetIdentifierCommands()

    private static ArgumentBuilder<ServerCommandSource, ?> buildCrossGetCommands() {
        return CommandManager.literal("get")
                .then(CommandManager.literal("owner")
                        .executes(NativeCommands::executeCrosshairGetOwner)
                );
    } // buildCrossGetCommands()

    // OWNER COMMANDS
    private static ArgumentBuilder<ServerCommandSource, ?> buildOwnerAddCombatCommands() {
        return CommandManager.literal("combat")
                .then(CommandManager.literal("exp")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .suggests(NativeCommands::suggestPlayerNames)
                                .then(CommandManager.argument("robot_index", IntegerArgumentType.integer(0))
                                        .suggests(NativeCommands::suggestRobotIndices)
                                        .then(CommandManager.argument("exp_value", IntegerArgumentType.integer(0))
                                                .suggests(NativeCommands::suggestOwnerMaxExp)
                                                .executes(NativeCommands::executeOwnerAddExp)
                                        )
                                )
                        )
                );
    } // buildOwnerAddCombatCommands ()

    private static ArgumentBuilder<ServerCommandSource, ?> buildOwnerSetCombatCommands () {
        return CommandManager.literal("combat")
                .then(CommandManager.literal("all")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .suggests(NativeCommands::suggestPlayerNames)
                                .then(CommandManager.argument("robot_index", IntegerArgumentType.integer(0))
                                        .suggests(NativeCommands::suggestRobotIndices)
                                        .then(CommandManager.argument("level", IntegerArgumentType.integer(1))
                                                .suggests(NativeCommands::suggestOwnerMaxLevel)
                                                .then(CommandManager.argument("exp", IntegerArgumentType.integer(0))
                                                        .suggests(NativeCommands::suggestOwnerMaxExpForLevel)
                                                        .executes(NativeCommands::executeOwnerSetAllCombat)
                                                )
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("exp")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .suggests(NativeCommands::suggestPlayerNames)
                                .then(CommandManager.argument("robot_index", IntegerArgumentType.integer(0))
                                        .suggests(NativeCommands::suggestRobotIndices)
                                        .then(CommandManager.argument("exp_value", IntegerArgumentType.integer(0))
                                                .suggests(NativeCommands::suggestOwnerMaxExp)
                                                .executes(NativeCommands::executeOwnerSetExp)
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("level")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .suggests(NativeCommands::suggestPlayerNames)
                                .then(CommandManager.argument("robot_index", IntegerArgumentType.integer(0))
                                        .suggests(NativeCommands::suggestRobotIndices)
                                        .then(CommandManager.argument("level_value", IntegerArgumentType.integer(1))
                                                .suggests(NativeCommands::suggestOwnerMaxLevel)
                                                .executes(NativeCommands::executeOwnerSetLevel)
                                        )
                                )
                        )
                );
    } // buildOwnerSetCombatCommands ()

    private static ArgumentBuilder<ServerCommandSource, ?> buildOwnerSetAttributeCommands() {
        return CommandManager.literal("attribute")
                .then(CommandManager.literal("all")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .suggests(NativeCommands::suggestPlayerNames)
                                .then(CommandManager.argument("robot_index", IntegerArgumentType.integer(0))
                                        .suggests(NativeCommands::suggestRobotIndices)
                                        .then(CommandManager.argument("hp", IntegerArgumentType.integer(1))
                                                .then(CommandManager.argument("attack", IntegerArgumentType.integer(1))
                                                        .then(CommandManager.argument("defense", IntegerArgumentType.integer(0))
                                                                .then(CommandManager.argument("speed", IntegerArgumentType.integer(1))
                                                                        .executes(NativeCommands::executeOwnerSetAllAttributes)
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("hp")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .suggests(NativeCommands::suggestPlayerNames)
                                .then(CommandManager.argument("robot_index", IntegerArgumentType.integer(0))
                                        .suggests(NativeCommands::suggestRobotIndices)
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                                                .executes(NativeCommands::executeOwnerSetHP)
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("attack")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .suggests(NativeCommands::suggestPlayerNames)
                                .then(CommandManager.argument("robot_index", IntegerArgumentType.integer(0))
                                        .suggests(NativeCommands::suggestRobotIndices)
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                                                .executes(NativeCommands::executeOwnerSetAttack)
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("defense")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .suggests(NativeCommands::suggestPlayerNames)
                                .then(CommandManager.argument("robot_index", IntegerArgumentType.integer(0))
                                        .suggests(NativeCommands::suggestRobotIndices)
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                                .executes(NativeCommands::executeOwnerSetDefense)
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("speed")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .suggests(NativeCommands::suggestPlayerNames)
                                .then(CommandManager.argument("robot_index", IntegerArgumentType.integer(0))
                                        .suggests(NativeCommands::suggestRobotIndices)
                                        .then(CommandManager.argument("value", IntegerArgumentType.integer(1))
                                                .executes(NativeCommands::executeOwnerSetSpeed)
                                        )
                                )
                        )
                );
    } // buildOwnerSetAttributeCommands()

    private static ArgumentBuilder<ServerCommandSource, ?> buildOwnerSetProtectionCommands() {
        return CommandManager.literal("protection")
                .then(CommandManager.literal("all")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .suggests(NativeCommands::suggestPlayerNames)
                                .then(CommandManager.argument("robot_index", IntegerArgumentType.integer(0))
                                        .suggests(NativeCommands::suggestRobotIndices)
                                        .then(CommandManager.argument("fire", IntegerArgumentType.integer(0))
                                                .suggests((ctx, builder) -> suggestOwnerMaxProtection(ctx, builder, "fire"))
                                                .then(CommandManager.argument("fall", IntegerArgumentType.integer(0))
                                                        .suggests((ctx, builder) -> suggestOwnerMaxProtection(ctx, builder, "fall"))
                                                        .then(CommandManager.argument("blast", IntegerArgumentType.integer(0))
                                                                .suggests((ctx, builder) -> suggestOwnerMaxProtection(ctx, builder, "blast"))
                                                                .then(CommandManager.argument("projectile", IntegerArgumentType.integer(0))
                                                                        .suggests((ctx, builder) -> suggestOwnerMaxProtection(ctx, builder, "projectile"))
                                                                        .executes(NativeCommands::executeOwnerSetAllProtections)
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("fire")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .suggests(NativeCommands::suggestPlayerNames)
                                .then(CommandManager.argument("robot_index", IntegerArgumentType.integer(0))
                                        .suggests(NativeCommands::suggestRobotIndices)
                                        .then(CommandManager.argument("level", IntegerArgumentType.integer(0))
                                                .suggests((ctx, builder) -> suggestOwnerMaxProtection(ctx, builder, "fire"))
                                                .executes(NativeCommands::executeOwnerSetFireProtection)
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("fall")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .suggests(NativeCommands::suggestPlayerNames)
                                .then(CommandManager.argument("robot_index", IntegerArgumentType.integer(0))
                                        .suggests(NativeCommands::suggestRobotIndices)
                                        .then(CommandManager.argument("level", IntegerArgumentType.integer(0))
                                                .suggests((ctx, builder) -> suggestOwnerMaxProtection(ctx, builder, "fall"))
                                                .executes(NativeCommands::executeOwnerSetFallProtection)
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("blast")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .suggests(NativeCommands::suggestPlayerNames)
                                .then(CommandManager.argument("robot_index", IntegerArgumentType.integer(0))
                                        .suggests(NativeCommands::suggestRobotIndices)
                                        .then(CommandManager.argument("level", IntegerArgumentType.integer(0))
                                                .suggests((ctx, builder) -> suggestOwnerMaxProtection(ctx, builder, "blast"))
                                                .executes(NativeCommands::executeOwnerSetBlastProtection)
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("projectile")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .suggests(NativeCommands::suggestPlayerNames)
                                .then(CommandManager.argument("robot_index", IntegerArgumentType.integer(0))
                                        .suggests(NativeCommands::suggestRobotIndices)
                                        .then(CommandManager.argument("level", IntegerArgumentType.integer(0))
                                                .suggests((ctx, builder) -> suggestOwnerMaxProtection(ctx, builder, "projectile"))
                                                .executes(NativeCommands::executeOwnerSetProjectileProtection)
                                        )
                                )
                        )
                );
    } // buildOwnerSetProtectionCommands()

    private static ArgumentBuilder<ServerCommandSource, ?> buildOwnerSetAppearanceCommands() {
        return CommandManager.literal("appearance")
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .suggests(NativeCommands::suggestPlayerNames)
                        .then(CommandManager.argument("robot_index", IntegerArgumentType.integer(0))
                                .suggests(NativeCommands::suggestRobotIndices)
                                .then(CommandManager.argument("color", ColorArgumentType.color())
                                        .executes(NativeCommands::executeOwnerSetAppearance)
                                )
                        )
                );
    } // buildOwnerSetAppearanceCommands()

    private static ArgumentBuilder<ServerCommandSource, ?> buildOwnerSetIdentifierCommands() {
        return CommandManager.literal("identifier")
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .suggests(NativeCommands::suggestPlayerNames)
                        .then(CommandManager.argument("robot_index", IntegerArgumentType.integer(0))
                                .suggests(NativeCommands::suggestRobotIndices)
                                .then(CommandManager.argument("name", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                                        .executes(NativeCommands::executeOwnerSetIdentifier)
                                )
                        )
                );
    } // buildOwnerSetIdentifierCommands()

    private static ArgumentBuilder<ServerCommandSource, ?> buildReloadCommand() {
        return CommandManager.literal("reload")
                .executes(NativeCommands::executeReload);
    } // buildReloadCommand()

    // -- Commands Suggestions --

    /**
     * Suggests max level for targeted robot.
     * <p>
     * Displays robot's max level as suggestion, providing context-aware
     * command completion.
     */
    private static CompletableFuture<Suggestions> suggestMaxLevel(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            PlayerEntity player = ctx.getSource().getPlayerOrThrow();
            LovelyRobotEntity robot = (LovelyRobotEntity) findEntityInFront(player);
            
            if (robot != null) {
                int maxLevel = robot.getLevelSystem()
                        .map(LevelFeature::getMaxLevel)
                        .orElse(200);
                builder.suggest(maxLevel, Text.literal("Maximum allowed level for this robot"));
            }
        } catch (CommandSyntaxException ignored) {}
        
        return builder.buildFuture();
    } // suggestMaxLevel()

    /**
     * Suggests max XP for targeted robot's current level.
     * <p>
     * Displays XP required for next level, providing context-aware
     * command completion.
     */
    private static CompletableFuture<Suggestions> suggestMaxExp(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            PlayerEntity player = ctx.getSource().getPlayerOrThrow();
            LovelyRobotEntity robot = (LovelyRobotEntity) findEntityInFront(player);
            
            if (robot != null) {
                int maxExp = robot.getLevelSystem()
                        .map(feature -> feature.getExpForLevel(robot.getCurrentLevel()))
                        .orElse(Integer.MAX_VALUE);
                builder.suggest(maxExp, Text.literal("Maximum XP for current level (surplus will level up)"));
            }
        } catch (CommandSyntaxException ignored) {}
        return builder.buildFuture();
    } // suggestMaxExp()

    /**
     * Suggests max XP for the level being typed in the "all" command.
     * <p>
     * Dynamically calculates max XP based on the level argument value,
     * providing accurate suggestions for the target level.
     */
    private static CompletableFuture<Suggestions> suggestMaxExpForLevel(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            PlayerEntity player = ctx.getSource().getPlayerOrThrow();
            LovelyRobotEntity robot = (LovelyRobotEntity) findEntityInFront(player);
            
            if (robot != null) {
                // Try to get the level argument that was just typed
                int targetLevel;
                try {
                    targetLevel = IntegerArgumentType.getInteger(ctx, "level");
                } catch (IllegalArgumentException e) {
                    // If level not parsed yet, use current level
                    targetLevel = robot.getCurrentLevel();
                }

                int finalTargetLevel = targetLevel;
                int maxExp = robot.getLevelSystem()
                        .map(feature -> feature.getExpForLevel(finalTargetLevel))
                        .orElse(Integer.MAX_VALUE);
                builder.suggest(maxExp, Text.literal("Maximum XP allowed for level " + targetLevel));
            }
        } catch (CommandSyntaxException ignored) {}
        return builder.buildFuture();
    } // suggestMaxExpForLevel()

    /**
     * Suggests minimum max level among all targeted robots.
     * <p>
     * Finds the lowest max level across selected entities - ensures suggested
     * value works for all robots.
     */
    private static CompletableFuture<Suggestions> suggestMinMaxLevel(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
            int minMaxLevel = Integer.MAX_VALUE;
            int robotCount = 0;

            for (Entity entity : entities) {
                if (entity instanceof LovelyRobotEntity robot) {
                    int maxLevel = robot.getLevelSystem()
                            .map(LevelFeature::getMaxLevel)
                            .orElse(200);
                    minMaxLevel = Math.min(minMaxLevel, maxLevel);
                    robotCount++;
                }
            }

            if (robotCount > 0 && minMaxLevel != Integer.MAX_VALUE) {
                builder.suggest(minMaxLevel, Text.literal("Safe max level for all " + robotCount + " robot(s)"));
            }
        } catch (Exception ignored) {}
        return builder.buildFuture();
    } // suggestMinMaxLevel()

    /**
     * Suggests minimum max XP among all targeted robots for their current levels.
     * <p>
     * Finds the lowest max XP across selected entities - ensures suggested
     * value works for all robots at their current levels.
     */
    private static CompletableFuture<Suggestions> suggestMinMaxExp(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
            int minMaxExp = Integer.MAX_VALUE;
            int robotCount = 0;

            for (Entity entity : entities) {
                if (entity instanceof LovelyRobotEntity robot) {
                    int maxExp = robot.getLevelSystem()
                            .map(feature -> feature.getExpForLevel(robot.getCurrentLevel()))
                            .orElse(Integer.MAX_VALUE);
                    minMaxExp = Math.min(minMaxExp, maxExp);
                    robotCount++;
                }
            }

            if (robotCount > 0 && minMaxExp != Integer.MAX_VALUE) {
                builder.suggest(minMaxExp, Text.literal("Safe max XP for all " + robotCount + " robot(s) (surplus will level up)"));
            }
        } catch (Exception ignored) {}
        return builder.buildFuture();
    } // suggestMinMaxExp()

    /**
     * Suggests minimum max XP for the target level being typed in "all" command.
     * <p>
     * Finds the lowest max XP across selected entities for the specified level -
     * ensures suggested value works for all robots at that level.
     */
    private static CompletableFuture<Suggestions> suggestMinMaxExpForLevel(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
            int targetLevel;
            try {
                targetLevel = IntegerArgumentType.getInteger(ctx, "level");
            } catch (IllegalArgumentException e) {
                targetLevel = 1; // Default if not parsed yet
            }

            int minMaxExp = Integer.MAX_VALUE;
            int robotCount = 0;
            int finalTargetLevel = targetLevel;

            for (Entity entity : entities) {
                if (entity instanceof LovelyRobotEntity robot) {
                    int maxExp = robot.getLevelSystem()
                            .map(feature -> feature.getExpForLevel(finalTargetLevel))
                            .orElse(Integer.MAX_VALUE);
                    minMaxExp = Math.min(minMaxExp, maxExp);
                    robotCount++;
                }
            }

            if (robotCount > 0 && minMaxExp != Integer.MAX_VALUE) {
                builder.suggest(minMaxExp, Text.literal("Safe max XP for level " + finalTargetLevel + " (" + robotCount + " robot(s))"));
            }
        } catch (Exception ignored) {}
        return builder.buildFuture();
    } // suggestMinMaxExpForLevel()

    /**
     * Suggests player names who own robots.
     * <p>
     * Queries registry for all owners and suggests their names.
     */
    private static CompletableFuture<Suggestions> suggestPlayerNames(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            ServerWorld world = ctx.getSource().getWorld();
            OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(world);
            var ownerMap = registry.getAllOwners();

            for (var ownerUuid : ownerMap.keySet()) {
                PlayerEntity player = world.getPlayerByUuid(ownerUuid);
                if (player != null) {
                    builder.suggest(player.getName().getString());
                }
            }
        } catch (Exception ignored) {}
        return builder.buildFuture();
    } // suggestPlayerNames()

    /**
     * Suggests robot indices for selected player.
     * <p>
     * Displays robot count and suggests valid indices based on player's robots.
     */
    private static CompletableFuture<Suggestions> suggestRobotIndices(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
            ServerWorld world = (ServerWorld) player.getWorld();
            OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(world);
            
            List<RobotRegistryEntry> robots = registry.getRobotsForOwner(player.getUuid());
            
            if (!robots.isEmpty()) {
                // Suggest first few indices with robot names
                for (int i = 0; i < Math.min(robots.size(), 5); i++) {
                    RobotRegistryEntry entry = robots.get(i);
                    Object entityObj = entry.getEntity();
                    
                    String robotInfo;
                    if (entityObj instanceof InternalEntity robot && robot.hasCustomName()) {
                        robotInfo = robot.getCustomName().getString();
                    } else {
                        robotInfo = entry.getRobotType().replace("entity.llovelyr.", "");
                    }
                    
                    builder.suggest(i, Text.literal(robotInfo));
                }
                
                // If more robots exist, suggest the count
                if (robots.size() > 5) {
                    builder.suggest(robots.size() - 1, Text.literal("Last robot (total: " + robots.size() + ")"));
                }
            }
        } catch (Exception ignored) {}
        return builder.buildFuture();
    } // suggestRobotIndices()

    /**
     * Suggests max level for owner's robot at specified index.
     * <p>
     * Retrieves robot from registry and displays its max level.
     */
    private static CompletableFuture<Suggestions> suggestOwnerMaxLevel(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
            int index = IntegerArgumentType.getInteger(ctx, "robot_index");
            
            LovelyRobotEntity robot = getOwnerRobotByIndex(player, index);
            if (robot != null) {
                int maxLevel = robot.getLevelSystem()
                        .map(LevelFeature::getMaxLevel)
                        .orElse(200);
                builder.suggest(maxLevel, Text.literal("Maximum level for this robot"));
            }
        } catch (Exception ignored) {}
        return builder.buildFuture();
    } // suggestOwnerMaxLevel()

    /**
     * Suggests max XP for owner's robot at specified index.
     * <p>
     * Retrieves robot from registry and displays max XP for current level.
     */
    private static CompletableFuture<Suggestions> suggestOwnerMaxExp(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
            int index = IntegerArgumentType.getInteger(ctx, "robot_index");
            
            LovelyRobotEntity robot = getOwnerRobotByIndex(player, index);
            if (robot != null) {
                int maxExp = robot.getLevelSystem()
                        .map(feature -> feature.getExpForLevel(robot.getCurrentLevel()))
                        .orElse(Integer.MAX_VALUE);
                builder.suggest(maxExp, Text.literal("Maximum XP for current level (surplus will level up)"));
            }
        } catch (Exception ignored) {}
        return builder.buildFuture();
    } // suggestOwnerMaxExp()

    /**
     * Suggests max XP for the level being typed in owner "all" command.
     * <p>
     * Dynamically calculates max XP based on level argument and robot type.
     */
    private static CompletableFuture<Suggestions> suggestOwnerMaxExpForLevel(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
            int index = IntegerArgumentType.getInteger(ctx, "robot_index");
            
            int targetLevel;
            try {
                targetLevel = IntegerArgumentType.getInteger(ctx, "level");
            } catch (IllegalArgumentException e) {
                targetLevel = 1;
            }
            
            LovelyRobotEntity robot = getOwnerRobotByIndex(player, index);
            if (robot != null) {
                int finalTargetLevel = targetLevel;
                int maxExp = robot.getLevelSystem()
                        .map(feature -> feature.getExpForLevel(finalTargetLevel))
                        .orElse(Integer.MAX_VALUE);
                builder.suggest(maxExp, Text.literal("Maximum XP for level " + targetLevel));
            }
        } catch (Exception ignored) {}
        return builder.buildFuture();
    } // suggestOwnerMaxExpForLevel()

    /**
     * Suggests max fire protection for crosshair robot.
     */
    private static CompletableFuture<Suggestions> suggestMaxFireProtection(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            PlayerEntity player = ctx.getSource().getPlayerOrThrow();
            LovelyRobotEntity robot = (LovelyRobotEntity) findEntityInFront(player);
            
            if (robot != null) {
                int maxLevel = robot.getProtectionSystem()
                        .map(net.msymbios.llovelyr.lib.entity.features.ProtectionFeature::getMaxFireProtection)
                        .orElse(80);
                builder.suggest(maxLevel, Text.literal("Maximum fire protection for this robot"));
            }
        } catch (CommandSyntaxException ignored) {}
        return builder.buildFuture();
    } // suggestMaxFireProtection()

    /**
     * Suggests max fall protection for crosshair robot.
     */
    private static CompletableFuture<Suggestions> suggestMaxFallProtection(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            PlayerEntity player = ctx.getSource().getPlayerOrThrow();
            LovelyRobotEntity robot = (LovelyRobotEntity) findEntityInFront(player);
            
            if (robot != null) {
                int maxLevel = robot.getProtectionSystem()
                        .map(net.msymbios.llovelyr.lib.entity.features.ProtectionFeature::getMaxFallProtection)
                        .orElse(80);
                builder.suggest(maxLevel, Text.literal("Maximum fall protection for this robot"));
            }
        } catch (CommandSyntaxException ignored) {}
        return builder.buildFuture();
    } // suggestMaxFallProtection()

    /**
     * Suggests max blast protection for crosshair robot.
     */
    private static CompletableFuture<Suggestions> suggestMaxBlastProtection(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            PlayerEntity player = ctx.getSource().getPlayerOrThrow();
            LovelyRobotEntity robot = (LovelyRobotEntity) findEntityInFront(player);
            
            if (robot != null) {
                int maxLevel = robot.getProtectionSystem()
                        .map(net.msymbios.llovelyr.lib.entity.features.ProtectionFeature::getMaxBlastProtection)
                        .orElse(80);
                builder.suggest(maxLevel, Text.literal("Maximum blast protection for this robot"));
            }
        } catch (CommandSyntaxException ignored) {}
        return builder.buildFuture();
    } // suggestMaxBlastProtection()

    /**
     * Suggests max projectile protection for crosshair robot.
     */
    private static CompletableFuture<Suggestions> suggestMaxProjectileProtection(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            PlayerEntity player = ctx.getSource().getPlayerOrThrow();
            LovelyRobotEntity robot = (LovelyRobotEntity) findEntityInFront(player);
            
            if (robot != null) {
                int maxLevel = robot.getProtectionSystem()
                        .map(net.msymbios.llovelyr.lib.entity.features.ProtectionFeature::getMaxProjectileProtection)
                        .orElse(80);
                builder.suggest(maxLevel, Text.literal("Maximum projectile protection for this robot"));
            }
        } catch (CommandSyntaxException ignored) {}
        return builder.buildFuture();
    } // suggestMaxProjectileProtection()

    /**
     * Suggests minimum max protection among all targeted robots.
     */
    private static CompletableFuture<Suggestions> suggestMinMaxFireProtection(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
            int minMaxLevel = Integer.MAX_VALUE;
            int robotCount = 0;

            for (Entity entity : entities) {
                if (entity instanceof LovelyRobotEntity robot) {
                    int maxLevel = robot.getProtectionSystem()
                            .map(net.msymbios.llovelyr.lib.entity.features.ProtectionFeature::getMaxFireProtection)
                            .orElse(80);
                    minMaxLevel = Math.min(minMaxLevel, maxLevel);
                    robotCount++;
                }
            }

            if (robotCount > 0 && minMaxLevel != Integer.MAX_VALUE) {
                builder.suggest(minMaxLevel, Text.literal("Safe max fire protection for all " + robotCount + " robot(s)"));
            }
        } catch (Exception ignored) {}
        return builder.buildFuture();
    } // suggestMinMaxFireProtection()

    /**
     * Suggests minimum max fall protection among all targeted robots.
     */
    private static CompletableFuture<Suggestions> suggestMinMaxFallProtection(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
            int minMaxLevel = Integer.MAX_VALUE;
            int robotCount = 0;

            for (Entity entity : entities) {
                if (entity instanceof LovelyRobotEntity robot) {
                    int maxLevel = robot.getProtectionSystem()
                            .map(net.msymbios.llovelyr.lib.entity.features.ProtectionFeature::getMaxFallProtection)
                            .orElse(80);
                    minMaxLevel = Math.min(minMaxLevel, maxLevel);
                    robotCount++;
                }
            }

            if (robotCount > 0 && minMaxLevel != Integer.MAX_VALUE) {
                builder.suggest(minMaxLevel, Text.literal("Safe max fall protection for all " + robotCount + " robot(s)"));
            }
        } catch (Exception ignored) {}
        return builder.buildFuture();
    } // suggestMinMaxFallProtection()

    /**
     * Suggests minimum max blast protection among all targeted robots.
     */
    private static CompletableFuture<Suggestions> suggestMinMaxBlastProtection(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
            int minMaxLevel = Integer.MAX_VALUE;
            int robotCount = 0;

            for (Entity entity : entities) {
                if (entity instanceof LovelyRobotEntity robot) {
                    int maxLevel = robot.getProtectionSystem()
                            .map(net.msymbios.llovelyr.lib.entity.features.ProtectionFeature::getMaxBlastProtection)
                            .orElse(80);
                    minMaxLevel = Math.min(minMaxLevel, maxLevel);
                    robotCount++;
                }
            }

            if (robotCount > 0 && minMaxLevel != Integer.MAX_VALUE) {
                builder.suggest(minMaxLevel, Text.literal("Safe max blast protection for all " + robotCount + " robot(s)"));
            }
        } catch (Exception ignored) {}
        return builder.buildFuture();
    } // suggestMinMaxBlastProtection()

    /**
     * Suggests minimum max projectile protection among all targeted robots.
     */
    private static CompletableFuture<Suggestions> suggestMinMaxProjectileProtection(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
            int minMaxLevel = Integer.MAX_VALUE;
            int robotCount = 0;

            for (Entity entity : entities) {
                if (entity instanceof LovelyRobotEntity robot) {
                    int maxLevel = robot.getProtectionSystem()
                            .map(net.msymbios.llovelyr.lib.entity.features.ProtectionFeature::getMaxProjectileProtection)
                            .orElse(80);
                    minMaxLevel = Math.min(minMaxLevel, maxLevel);
                    robotCount++;
                }
            }

            if (robotCount > 0 && minMaxLevel != Integer.MAX_VALUE) {
                builder.suggest(minMaxLevel, Text.literal("Safe max projectile protection for all " + robotCount + " robot(s)"));
            }
        } catch (Exception ignored) {}
        return builder.buildFuture();
    } // suggestMinMaxProjectileProtection()

    /**
     * Suggests max fire protection for owner's robot.
     */
    private static CompletableFuture<Suggestions> suggestOwnerMaxFireProtection(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
            int index = IntegerArgumentType.getInteger(ctx, "robot_index");
            
            LovelyRobotEntity robot = getOwnerRobotByIndex(player, index);
            if (robot != null) {
                int maxLevel = robot.getProtectionSystem()
                        .map(net.msymbios.llovelyr.lib.entity.features.ProtectionFeature::getMaxFireProtection)
                        .orElse(80);
                builder.suggest(maxLevel, Text.literal("Maximum fire protection for this robot"));
            }
        } catch (Exception ignored) {}
        return builder.buildFuture();
    } // suggestOwnerMaxFireProtection()

    /**
     * Suggests max fall protection for owner's robot.
     */
    private static CompletableFuture<Suggestions> suggestOwnerMaxFallProtection(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
            int index = IntegerArgumentType.getInteger(ctx, "robot_index");
            
            LovelyRobotEntity robot = getOwnerRobotByIndex(player, index);
            if (robot != null) {
                int maxLevel = robot.getProtectionSystem()
                        .map(net.msymbios.llovelyr.lib.entity.features.ProtectionFeature::getMaxFallProtection)
                        .orElse(80);
                builder.suggest(maxLevel, Text.literal("Maximum fall protection for this robot"));
            }
        } catch (Exception ignored) {}
        return builder.buildFuture();
    } // suggestOwnerMaxFallProtection()

    /**
     * Suggests max blast protection for owner's robot.
     */
    private static CompletableFuture<Suggestions> suggestOwnerMaxBlastProtection(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
            int index = IntegerArgumentType.getInteger(ctx, "robot_index");
            
            LovelyRobotEntity robot = getOwnerRobotByIndex(player, index);
            if (robot != null) {
                int maxLevel = robot.getProtectionSystem()
                        .map(net.msymbios.llovelyr.lib.entity.features.ProtectionFeature::getMaxBlastProtection)
                        .orElse(80);
                builder.suggest(maxLevel, Text.literal("Maximum blast protection for this robot"));
            }
        } catch (Exception ignored) {}
        return builder.buildFuture();
    } // suggestOwnerMaxBlastProtection()

    /**
     * Suggests max projectile protection for owner's robot.
     */
    private static CompletableFuture<Suggestions> suggestOwnerMaxProjectileProtection(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        try {
            PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
            int index = IntegerArgumentType.getInteger(ctx, "robot_index");
            
            LovelyRobotEntity robot = getOwnerRobotByIndex(player, index);
            if (robot != null) {
                int maxLevel = robot.getProtectionSystem()
                        .map(net.msymbios.llovelyr.lib.entity.features.ProtectionFeature::getMaxProjectileProtection)
                        .orElse(80);
                builder.suggest(maxLevel, Text.literal("Maximum projectile protection for this robot"));
            }
        } catch (Exception ignored) {}
        return builder.buildFuture();
    } // suggestOwnerMaxProjectileProtection()

    /**
     * Suggests max protection level for targeted robot.
     * <p>
     * Displays robot's max protection level based on protection type.
     */
    private static CompletableFuture<Suggestions> suggestMaxProtection(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder, String protectionType) {
        try {
            PlayerEntity player = ctx.getSource().getPlayerOrThrow();
            LovelyRobotEntity robot = (LovelyRobotEntity) findEntityInFront(player);
            
            if (robot != null) {
                int maxLevel = robot.nativeEntity.getFeature(net.msymbios.llovelyr.lib.entity.features.ProtectionFeature.class)
                        .map(feature -> switch (protectionType) {
                            case "fire" -> feature.getMaxFireProtection();
                            case "fall" -> feature.getMaxFallProtection();
                            case "blast" -> feature.getMaxBlastProtection();
                            case "projectile" -> feature.getMaxProjectileProtection();
                            default -> 80;
                        })
                        .orElse(80);
                builder.suggest(maxLevel, Text.literal("Maximum " + protectionType + " protection for this robot"));
            }
        } catch (Exception ignored) {}
        return builder.buildFuture();
    } // suggestMaxProtection()

    /**
     * Suggests minimum max protection level among all targeted robots.
     */
    private static CompletableFuture<Suggestions> suggestMinMaxProtection(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder, String protectionType) {
        try {
            Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
            int minMaxLevel = Integer.MAX_VALUE;
            int robotCount = 0;

            for (Entity entity : entities) {
                if (entity instanceof LovelyRobotEntity robot) {
                    int maxLevel = robot.nativeEntity.getFeature(net.msymbios.llovelyr.lib.entity.features.ProtectionFeature.class)
                            .map(feature -> switch (protectionType) {
                                case "fire" -> feature.getMaxFireProtection();
                                case "fall" -> feature.getMaxFallProtection();
                                case "blast" -> feature.getMaxBlastProtection();
                                case "projectile" -> feature.getMaxProjectileProtection();
                                default -> 80;
                            })
                            .orElse(80);
                    minMaxLevel = Math.min(minMaxLevel, maxLevel);
                    robotCount++;
                }
            }

            if (robotCount > 0 && minMaxLevel != Integer.MAX_VALUE) {
                builder.suggest(minMaxLevel, Text.literal("Safe max " + protectionType + " protection for all " + robotCount + " robot(s)"));
            }
        } catch (Exception ignored) {}
        return builder.buildFuture();
    } // suggestMinMaxProtection()

    /**
     * Suggests max protection level for owner's robot at specified index.
     */
    private static CompletableFuture<Suggestions> suggestOwnerMaxProtection(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder, String protectionType) {
        try {
            PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
            int index = IntegerArgumentType.getInteger(ctx, "robot_index");
            
            LovelyRobotEntity robot = getOwnerRobotByIndex(player, index);
            if (robot != null) {
                int maxLevel = robot.nativeEntity.getFeature(net.msymbios.llovelyr.lib.entity.features.ProtectionFeature.class)
                        .map(feature -> switch (protectionType) {
                            case "fire" -> feature.getMaxFireProtection();
                            case "fall" -> feature.getMaxFallProtection();
                            case "blast" -> feature.getMaxBlastProtection();
                            case "projectile" -> feature.getMaxProjectileProtection();
                            default -> 80;
                        })
                        .orElse(80);
                builder.suggest(maxLevel, Text.literal("Maximum " + protectionType + " protection for this robot"));
            }
        } catch (Exception ignored) {}
        return builder.buildFuture();
    } // suggestOwnerMaxProtection()

    // -- Command Executors --

    /**
     * Adds experience points to robot in crosshair.
     * <p>
     * No validation - allows adding any amount. Useful for rewarding robots
     * without level restrictions.
     */
    private static int executeCrosshairAddXP(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int xp = IntegerArgumentType.getInteger(ctx, "exp_value");

        return executeOnRobot(ctx, robot -> {
            robot.addExp(xp);
            return new CommandResult(true, "Add " + xp + "xp to " + Utility.getEntityCustomName(robot));
        });
    } // executeCrosshairAddXP()

    /**
     * Sets exact experience value for robot in crosshair.
     * <p>
     * Validates against max XP for current level to prevent overflow.
     * Use for precise XP control during testing or balancing.
     */
    private static int executeCrosshairSetXP(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int xp = IntegerArgumentType.getInteger(ctx, "exp_value");
        
        return executeOnRobot(ctx, robot -> {
            int maxXp = robot.getLevelSystem()
                    .map(feature -> feature.getExpForLevel(robot.getCurrentLevel()))
                    .orElse(Integer.MAX_VALUE);

            if (xp > maxXp) {
                return new CommandResult(false, "Exp " + xp + " exceeds maximum requirement of " + maxXp + "xp for current level!");
            }

            robot.setExp(xp);
            return new CommandResult(true, "Set " + xp + "xp to " + Utility.getEntityCustomName(robot));
        });
    } // executeCrosshairSetXP()

    /**
     * Sets robot level directly, bypassing XP progression.
     * <p>
     * Validates against robot type's max level from LevelFeature.
     * Useful for testing high-level robot behavior or quick progression.
     */
    private static int executeCrosshairSetLevel(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int level = IntegerArgumentType.getInteger(ctx, "level_value");
        
        return executeOnRobot(ctx, robot -> {
            int maxLevel = robot.getLevelSystem()
                    .map(LevelFeature::getMaxLevel)
                    .orElse(200);

            if (level > maxLevel) {
                return new CommandResult(false, "Level " + level + " exceeds maximum requirement of " + maxLevel + " for this robot!");
            }

            robot.setCurrentLevel(level);
            return new CommandResult(true, "Set level " + level + " to " + Utility.getEntityCustomName(robot));
        });
    } // executeCrosshairSetLevel()

    /**
     * Sets HP attribute for robot in crosshair.
     * <p>
     * Sets max health and heals robot to full HP.
     */
    private static int executeCrosshairSetHP(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int hp = IntegerArgumentType.getInteger(ctx, "value");

        return executeOnRobot(ctx, robot -> {
            robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(hp);
            robot.setHealth(hp);
            return new CommandResult(true, "Set HP to " + hp + " for " + Utility.getEntityCustomName(robot));
        });
    } // executeCrosshairSetHP()

    /**
     * Sets attack attribute for robot in crosshair.
     */
    private static int executeCrosshairSetAttack(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int attack = IntegerArgumentType.getInteger(ctx, "value");

        return executeOnRobot(ctx, robot -> {
            robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(attack);
            return new CommandResult(true, "Set attack to " + attack + " for " + Utility.getEntityCustomName(robot));
        });
    } // executeCrosshairSetAttack()

    /**
     * Sets defense attribute for robot in crosshair.
     */
    private static int executeCrosshairSetDefense(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int defense = IntegerArgumentType.getInteger(ctx, "value");

        return executeOnRobot(ctx, robot -> {
            robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ARMOR).setBaseValue(defense);
            return new CommandResult(true, "Set defense to " + defense + " for " + Utility.getEntityCustomName(robot));
        });
    } // executeCrosshairSetDefense()

    /**
     * Sets speed attribute for robot in crosshair.
     * <p>
     * Speed value is divided by 10 for Minecraft's movement speed scale.
     */
    private static int executeCrosshairSetSpeed(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int speed = IntegerArgumentType.getInteger(ctx, "value");

        return executeOnRobot(ctx, robot -> {
            robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(speed / 10.0);
            return new CommandResult(true, "Set speed to " + speed + " for " + Utility.getEntityCustomName(robot));
        });
    } // executeCrosshairSetSpeed()

    /**
     * Sets all attributes in one command for robot in crosshair.
     * <p>
     * Batch operation for efficient attribute configuration.
     */
    private static int executeCrosshairSetAllAttributes(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int hp = IntegerArgumentType.getInteger(ctx, "hp");
        int attack = IntegerArgumentType.getInteger(ctx, "attack");
        int defense = IntegerArgumentType.getInteger(ctx, "defense");
        int speed = IntegerArgumentType.getInteger(ctx, "speed");

        return executeOnRobot(ctx, robot -> {
            robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(hp);
            robot.setHealth(hp);
            robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(attack);
            robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ARMOR).setBaseValue(defense);
            robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(speed / 10.0);

            String name = Utility.getEntityCustomName(robot);
            return new CommandResult(true, "Set attributes (HP:" + hp + " ATK:" + attack + " DEF:" + defense + " SPD:" + speed + ") for " + name);
        });
    } // executeCrosshairSetAllAttributes()

    /**
     * Sets fire protection for robot in crosshair.
     * <p>
     * Fire protection reduces damage from fire, lava, and burning.
     */
    private static int executeCrosshairSetFireProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int level = IntegerArgumentType.getInteger(ctx, "level");

        return executeOnRobot(ctx, robot -> {
            robot.setFireProtection(level);
            return new CommandResult(true, "Set fire protection to " + level + " for " + Utility.getEntityCustomName(robot));
        });
    } // executeCrosshairSetFireProtection()

    /**
     * Sets fall protection for robot in crosshair.
     * <p>
     * Fall protection reduces damage from falling.
     */
    private static int executeCrosshairSetFallProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int level = IntegerArgumentType.getInteger(ctx, "level");

        return executeOnRobot(ctx, robot -> {
            robot.setFallProtection(level);
            return new CommandResult(true, "Set fall protection to " + level + " for " + Utility.getEntityCustomName(robot));
        });
    } // executeCrosshairSetFallProtection()

    /**
     * Sets blast protection for robot in crosshair.
     * <p>
     * Blast protection reduces damage from explosions.
     */
    private static int executeCrosshairSetBlastProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int level = IntegerArgumentType.getInteger(ctx, "level");

        return executeOnRobot(ctx, robot -> {
            robot.setBlastProtection(level);
            return new CommandResult(true, "Set blast protection to " + level + " for " + Utility.getEntityCustomName(robot));
        });
    } // executeCrosshairSetBlastProtection()

    /**
     * Sets projectile protection for robot in crosshair.
     * <p>
     * Projectile protection reduces damage from arrows and other projectiles.
     */
    private static int executeCrosshairSetProjectileProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int level = IntegerArgumentType.getInteger(ctx, "level");

        return executeOnRobot(ctx, robot -> {
            robot.setProjectileProtection(level);
            return new CommandResult(true, "Set projectile protection to " + level + " for " + Utility.getEntityCustomName(robot));
        });
    } // executeCrosshairSetProjectileProtection()

    /**
     * Sets all protections in one command for robot in crosshair.
     * <p>
     * Batch operation for efficient protection configuration.
     */
    private static int executeCrosshairSetAllProtections(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int fire = IntegerArgumentType.getInteger(ctx, "fire");
        int fall = IntegerArgumentType.getInteger(ctx, "fall");
        int blast = IntegerArgumentType.getInteger(ctx, "blast");
        int projectile = IntegerArgumentType.getInteger(ctx, "projectile");

        return executeOnRobot(ctx, robot -> {
            robot.setFireProtection(fire);
            robot.setFallProtection(fall);
            robot.setBlastProtection(blast);
            robot.setProjectileProtection(projectile);

            String name = Utility.getEntityCustomName(robot);
            return new CommandResult(true, "Set protections (Fire:" + fire + " Fall:" + fall + " Blast:" + blast + " Projectile:" + projectile + ") for " + name);
        });
    } // executeCrosshairSetAllProtections()

    /**
     * Sets appearance (color/texture) for robot in crosshair.
     * <p>
     * Changes robot's visual appearance immediately.
     */
    private static int executeCrosshairSetAppearance(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        net.msymbios.llovelyr.framework.entity.enums.EntityTexture color = ctx.getArgument("color", net.msymbios.llovelyr.framework.entity.enums.EntityTexture.class);

        return executeOnRobot(ctx, robot -> {
            robot.setTexture(color);
            String colorName = color.Name().toLowerCase();
            return new CommandResult(true, "Set color to " + colorName + " for " + Utility.getEntityCustomName(robot));
        });
    } // executeCrosshairSetAppearance()

    /**
     * Sets identifier (custom name) for robot in crosshair.
     * <p>
     * Sets custom name and makes it visible above the robot.
     */
    private static int executeCrosshairSetIdentifier(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        String nameString = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "name");

        return executeOnRobot(ctx, robot -> {
            Text name = Text.literal(nameString);
            robot.setCustomName(name);
            robot.setCustomNameVisible(true);
            return new CommandResult(true, "Set name to '" + nameString + "' for " + Utility.getEntityCustomName(robot));
        });
    } // executeCrosshairSetIdentifier()

    /**
     * Gets owner of robot in crosshair.
     * <p>
     * Displays current owner name or "No owner" if untamed.
     */
    private static int executeCrosshairGetOwner(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeOnRobot(ctx, robot -> {
            PlayerEntity owner = (PlayerEntity) robot.getOwner();
            String ownerName = owner != null ? owner.getName().getString() : "No owner";
            String robotName = Utility.getEntityCustomName(robot);
            return new CommandResult(true, robotName + " owner: " + ownerName);
        });
    } // executeCrosshairGetOwner()

    /**
     * Displays comprehensive stats for robot in crosshair.
     */
    private static int executeCrosshairStats(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = ctx.getSource().getPlayerOrThrow();
        LovelyRobotEntity robot = (LovelyRobotEntity) findEntityInFront(player);

        if (robot == null) {
            ctx.getSource().sendError(Text.literal("No robot found in crosshair"));
            return 0;
        }

        robot.displayGeneralMessage(true, false);
        return 1;
    } // executeCrosshairStats()

    /**
     * Heals robot in crosshair to full health.
     */
    private static int executeCrosshairHeal(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeOnRobot(ctx, robot -> {
            robot.setHealth(robot.getMaxHealth());
            String robotName = Utility.getEntityCustomName(robot);
            return new CommandResult(true, "Healed " + robotName);
        });
    } // executeCrosshairHeal()

    /**
     * Recalls robot in crosshair to command source location.
     */
    private static int executeCrosshairRecall(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        net.minecraft.server.network.ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        
        return executeOnRobot(ctx, robot -> {
            robot.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
            String robotName = Utility.getEntityCustomName(robot);
            return new CommandResult(true, "Recalled " + robotName);
        });
    } // executeCrosshairRecall()

    /**
     * Sets both level and experience in one command.
     * <p>
     * Validates both values against robot's limits. Convenient for quickly
     * configuring robot combat stats during testing or setup.
     */
    private static int executeSetAllCombat(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int level = IntegerArgumentType.getInteger(ctx, "level");
        int exp = IntegerArgumentType.getInteger(ctx, "exp");
        
        return executeOnRobot(ctx, robot -> {
            // Validate level
            int maxLevel = robot.getLevelSystem()
                    .map(LevelFeature::getMaxLevel)
                    .orElse(200);

            if (level > maxLevel) {
                return new CommandResult(false, "Level " + level + " exceeds maximum requirement of " + maxLevel + " for this robot!");
            }

            // Validate exp (after setting level, check against new level's max)
            int maxExp = robot.getLevelSystem()
                    .map(feature -> feature.getExpForLevel(level))
                    .orElse(Integer.MAX_VALUE);

            if (exp > maxExp) {
                return new CommandResult(false, "Exp " + exp + " exceeds maximum requirement of " + maxExp + "xp for level " + level + "!");
            }

            // Set both values
            robot.setCurrentLevel(level);
            robot.setExp(exp);
            
            String name = Utility.getEntityCustomName(robot);
            return new CommandResult(true, "Set " + name + " to level " + level + " with " + exp + "xp");
        });
    } // executeSetAllCombat()

    // -- Target Command Executors --

    /**
     * Adds experience to multiple targeted robots.
     * <p>
     * No validation - surplus XP triggers level ups automatically.
     */
    private static int executeTargetAddXP(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
        int xp = IntegerArgumentType.getInteger(ctx, "exp_value");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobotEntity robot) {
                robot.addExp(xp);
                count++;
            }
        }

        int finalCount = count;
        ctx.getSource().sendFeedback(
                () -> Text.literal("Added " + xp + "xp to " + finalCount + " robot(s)"), true
        );
        return count;
    } // executeTargetAddXP()

    /**
     * Sets exact XP for multiple targeted robots.
     * <p>
     * Validates against each robot's current level max XP.
     */
    private static int executeTargetSetXP(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
        int xp = IntegerArgumentType.getInteger(ctx, "exp_value");
        int count = 0;
        int skipped = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobotEntity robot) {
                int maxExp = robot.getLevelSystem()
                        .map(feature -> feature.getExpForLevel(robot.getCurrentLevel()))
                        .orElse(Integer.MAX_VALUE);

                if (xp > maxExp) {
                    skipped++;
                    continue;
                }

                robot.setExp(xp);
                count++;
            }
        }

        int finalCount = count;
        int finalSkipped = skipped;
        ctx.getSource().sendFeedback(
                () -> Text.literal("Set " + xp + "xp for " + finalCount + " robot(s)" + 
                        (finalSkipped > 0 ? " (" + finalSkipped + " skipped - XP too high)" : "")), 
                true
        );
        return count;
    } // executeTargetSetXP()

    /**
     * Sets level for multiple targeted robots.
     * <p>
     * Validates against each robot's max level.
     */
    private static int executeTargetSetLevel(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
        int level = IntegerArgumentType.getInteger(ctx, "level_value");
        int count = 0;
        int skipped = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobotEntity robot) {
                int maxLevel = robot.getLevelSystem()
                        .map(LevelFeature::getMaxLevel)
                        .orElse(200);

                if (level > maxLevel) {
                    skipped++;
                    continue;
                }

                robot.setCurrentLevel(level);
                count++;
            }
        }

        int finalCount = count;
        int finalSkipped = skipped;
        ctx.getSource().sendFeedback(
                () -> Text.literal("Set level " + level + " for " + finalCount + " robot(s)" + 
                        (finalSkipped > 0 ? " (" + finalSkipped + " skipped - level too high)" : "")), 
                true
        );
        return count;
    } // executeTargetSetLevel()

    /**
     * Sets both level and XP for multiple targeted robots.
     * <p>
     * Validates both values against each robot's limits.
     */
    private static int executeTargetSetAllCombat(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
        int level = IntegerArgumentType.getInteger(ctx, "level");
        int exp = IntegerArgumentType.getInteger(ctx, "exp");
        int count = 0;
        int skipped = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobotEntity robot) {
                int maxLevel = robot.getLevelSystem()
                        .map(LevelFeature::getMaxLevel)
                        .orElse(200);

                if (level > maxLevel) {
                    skipped++;
                    continue;
                }

                int maxExp = robot.getLevelSystem()
                        .map(feature -> feature.getExpForLevel(level))
                        .orElse(Integer.MAX_VALUE);

                if (exp > maxExp) {
                    skipped++;
                    continue;
                }

                robot.setCurrentLevel(level);
                robot.setExp(exp);
                count++;
            }
        }

        int finalCount = count;
        int finalSkipped = skipped;
        ctx.getSource().sendFeedback(
                () -> Text.literal("Set level " + level + " with " + exp + "xp for " + finalCount + " robot(s)" + 
                        (finalSkipped > 0 ? " (" + finalSkipped + " skipped - values too high)" : "")), 
                true
        );
        return count;
    } // executeTargetSetAllCombat()

    /**
     * Sets HP for multiple targeted robots.
     */
    private static int executeTargetSetHP(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
        int hp = IntegerArgumentType.getInteger(ctx, "value");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobotEntity robot) {
                robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(hp);
                robot.setHealth(hp);
                count++;
            }
        }

        int finalCount = count;
        ctx.getSource().sendFeedback(
                () -> Text.literal("Set HP to " + hp + " for " + finalCount + " robot(s)"),
                true
        );
        return count;
    } // executeTargetSetHP()

    /**
     * Sets attack for multiple targeted robots.
     */
    private static int executeTargetSetAttack(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
        int attack = IntegerArgumentType.getInteger(ctx, "value");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobotEntity robot) {
                robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(attack);
                count++;
            }
        }

        int finalCount = count;
        ctx.getSource().sendFeedback(
                () -> Text.literal("Set attack to " + attack + " for " + finalCount + " robot(s)"),
                true
        );
        return count;
    } // executeTargetSetAttack()

    /**
     * Sets defense for multiple targeted robots.
     */
    private static int executeTargetSetDefense(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
        int defense = IntegerArgumentType.getInteger(ctx, "value");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobotEntity robot) {
                robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ARMOR).setBaseValue(defense);
                count++;
            }
        }

        int finalCount = count;
        ctx.getSource().sendFeedback(
                () -> Text.literal("Set defense to " + defense + " for " + finalCount + " robot(s)"),
                true
        );
        return count;
    } // executeTargetSetDefense()

    /**
     * Sets speed for multiple targeted robots.
     * <p>
     * Speed value is divided by 10 for Minecraft's movement speed scale.
     */
    private static int executeTargetSetSpeed(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
        int speed = IntegerArgumentType.getInteger(ctx, "value");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobotEntity robot) {
                robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(speed / 10.0);
                count++;
            }
        }

        int finalCount = count;
        ctx.getSource().sendFeedback(
                () -> Text.literal("Set speed to " + speed + " for " + finalCount + " robot(s)"),
                true
        );
        return count;
    } // executeTargetSetSpeed()

    /**
     * Sets all attributes for multiple targeted robots.
     * <p>
     * Batch operation for efficient attribute configuration.
     */
    private static int executeTargetSetAllAttributes(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
        int hp = IntegerArgumentType.getInteger(ctx, "hp");
        int attack = IntegerArgumentType.getInteger(ctx, "attack");
        int defense = IntegerArgumentType.getInteger(ctx, "defense");
        int speed = IntegerArgumentType.getInteger(ctx, "speed");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobotEntity robot) {
                robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(hp);
                robot.setHealth(hp);
                robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(attack);
                robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ARMOR).setBaseValue(defense);
                robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(speed / 10.0);
                count++;
            }
        }

        int finalCount = count;
        ctx.getSource().sendFeedback(
                () -> Text.literal("Set attributes (HP:" + hp + " ATK:" + attack + " DEF:" + defense + " SPD:" + speed + ") for " + finalCount + " robot(s)"),
                true
        );
        return count;
    } // executeTargetSetAllAttributes()

    /**
     * Sets fire protection for multiple targeted robots.
     * <p>
     * Fire protection reduces damage from fire, lava, and burning.
     */
    private static int executeTargetSetFireProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
        int level = IntegerArgumentType.getInteger(ctx, "level");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobotEntity robot) {
                robot.setFireProtection(level);
                count++;
            }
        }

        int finalCount = count;
        ctx.getSource().sendFeedback(
                () -> Text.literal("Set fire protection to " + level + " for " + finalCount + " robot(s)"),
                true
        );
        return count;
    } // executeTargetSetFireProtection()

    /**
     * Sets fall protection for multiple targeted robots.
     * <p>
     * Fall protection reduces damage from falling.
     */
    private static int executeTargetSetFallProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
        int level = IntegerArgumentType.getInteger(ctx, "level");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobotEntity robot) {
                robot.setFallProtection(level);
                count++;
            }
        }

        int finalCount = count;
        ctx.getSource().sendFeedback(
                () -> Text.literal("Set fall protection to " + level + " for " + finalCount + " robot(s)"),
                true
        );
        return count;
    } // executeTargetSetFallProtection()

    /**
     * Sets blast protection for multiple targeted robots.
     * <p>
     * Blast protection reduces damage from explosions.
     */
    private static int executeTargetSetBlastProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
        int level = IntegerArgumentType.getInteger(ctx, "level");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobotEntity robot) {
                robot.setBlastProtection(level);
                count++;
            }
        }

        int finalCount = count;
        ctx.getSource().sendFeedback(
                () -> Text.literal("Set blast protection to " + level + " for " + finalCount + " robot(s)"),
                true
        );
        return count;
    } // executeTargetSetBlastProtection()

    /**
     * Sets projectile protection for multiple targeted robots.
     * <p>
     * Projectile protection reduces damage from arrows and other projectiles.
     */
    private static int executeTargetSetProjectileProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
        int level = IntegerArgumentType.getInteger(ctx, "level");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobotEntity robot) {
                robot.setProjectileProtection(level);
                count++;
            }
        }

        int finalCount = count;
        ctx.getSource().sendFeedback(
                () -> Text.literal("Set projectile protection to " + level + " for " + finalCount + " robot(s)"),
                true
        );
        return count;
    } // executeTargetSetProjectileProtection()

    /**
     * Sets all protections for multiple targeted robots.
     * <p>
     * Batch operation for efficient protection configuration.
     */
    private static int executeTargetSetAllProtections(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
        int fire = IntegerArgumentType.getInteger(ctx, "fire");
        int fall = IntegerArgumentType.getInteger(ctx, "fall");
        int blast = IntegerArgumentType.getInteger(ctx, "blast");
        int projectile = IntegerArgumentType.getInteger(ctx, "projectile");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobotEntity robot) {
                robot.setFireProtection(fire);
                robot.setFallProtection(fall);
                robot.setBlastProtection(blast);
                robot.setProjectileProtection(projectile);
                count++;
            }
        }

        int finalCount = count;
        ctx.getSource().sendFeedback(
                () -> Text.literal("Set protections (Fire:" + fire + " Fall:" + fall + " Blast:" + blast + " Projectile:" + projectile + ") for " + finalCount + " robot(s)"),
                true
        );
        return count;
    } // executeTargetSetAllProtections()

    /**
     * Sets appearance for multiple targeted robots.
     * <p>
     * Changes visual appearance for all selected robots.
     */
    private static int executeTargetSetAppearance(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
        net.msymbios.llovelyr.framework.entity.enums.EntityTexture color = ctx.getArgument("color", net.msymbios.llovelyr.framework.entity.enums.EntityTexture.class);
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobotEntity robot) {
                robot.setTexture(color);
                count++;
            }
        }

        int finalCount = count;
        String colorName = color.Name().toLowerCase();
        ctx.getSource().sendFeedback(
                () -> Text.literal("Set color to " + colorName + " for " + finalCount + " robot(s)"),
                true
        );
        return count;
    } // executeTargetSetAppearance()

    /**
     * Sets identifier (custom name) for multiple targeted robots.
     * <p>
     * Sets custom name and makes it visible for all selected robots.
     */
    private static int executeTargetSetIdentifier(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
        String nameString = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "name");
        Text name = Text.literal(nameString);
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobotEntity robot) {
                robot.setCustomName(name);
                robot.setCustomNameVisible(true);
                count++;
            }
        }

        int finalCount = count;
        ctx.getSource().sendFeedback(
                () -> Text.literal("Set name to '" + nameString + "' for " + finalCount + " robot(s)"),
                true
        );
        return count;
    } // executeTargetSetIdentifier()

    /**
     * Heals all targeted robots to full health.
     * <p>
     * Batch operation for efficient healing of multiple robots.
     */
    private static int executeTargetHeal(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobotEntity robot) {
                robot.setHealth(robot.getMaxHealth());
                count++;
            }
        }

        int finalCount = count;
        ctx.getSource().sendFeedback(
                () -> Text.literal("Healed " + finalCount + " robot(s)"),
                true
        );
        return count;
    } // executeTargetHeal()

    /**
     * Teleports command source to first targeted robot.
     * <p>
     * If multiple robots targeted, teleports to the first one.
     */
    private static int executeTargetTeleport(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
        
        for (Entity entity : entities) {
            if (entity instanceof LovelyRobotEntity robot) {
                net.minecraft.server.network.ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                player.teleport(
                        (ServerWorld) robot.getWorld(),
                        robot.getX(),
                        robot.getY(),
                        robot.getZ(),
                        robot.getYaw(),
                        robot.getPitch()
                );

                String robotName = Utility.getEntityCustomName(robot);
                ctx.getSource().sendFeedback(() -> Text.literal("Teleported to " + robotName), false);
                return 1;
            }
        }

        ctx.getSource().sendError(Text.literal("No valid robot found in targets"));
        return 0;
    } // executeTargetTeleport()

    /**
     * Recalls all targeted robots to command source location.
     * <p>
     * Batch operation for efficient robot repositioning.
     */
    private static int executeTargetRecall(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
        net.minecraft.server.network.ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobotEntity robot) {
                robot.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
                count++;
            }
        }

        int finalCount = count;
        ctx.getSource().sendFeedback(
                () -> Text.literal("Recalled " + finalCount + " robot(s)"),
                true
        );
        return count;
    } // executeTargetRecall()

    /**
     * Transfers ownership of all targeted robots to specified player.
     * <p>
     * Batch operation for efficient ownership transfer.
     */
    private static int executeTargetTransfer(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
        PlayerEntity toPlayer = EntityArgumentType.getPlayer(ctx, "to_player");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobotEntity robot) {
                // Unregister from old owner
                ServerWorld world = (ServerWorld) robot.getWorld();
                OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(world);
                registry.unregisterRobot(robot.getUuid());

                // Set new owner (registration happens automatically)
                robot.setOwner(toPlayer);
                count++;
            }
        }

        int finalCount = count;
        ctx.getSource().sendFeedback(
                () -> Text.literal("Transferred " + finalCount + " robot(s) to " + toPlayer.getName().getString()),
                true
        );
        return count;
    } // executeTargetTransfer()

    // -- Owner Command Executors --

    /**
     * Adds experience to robot selected by owner and index.
     * <p>
     * No validation - surplus XP triggers level ups automatically.
     */
    private static int executeOwnerAddExp(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "robot_index");
        int xp = IntegerArgumentType.getInteger(ctx, "exp_value");

        return executeOnOwnerRobot(ctx, player, index, robot -> {
            robot.addExp(xp);
            return new CommandResult(true, "Added " + xp + "xp to " + Utility.getEntityCustomName(robot));
        });
    } // executeOwnerAddExp()

    /**
     * Sets exact XP for robot selected by owner and index.
     * <p>
     * Validates against robot's current level max XP.
     */
    private static int executeOwnerSetExp(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "robot_index");
        int xp = IntegerArgumentType.getInteger(ctx, "exp_value");

        return executeOnOwnerRobot(ctx, player, index, robot -> {
            int maxExp = robot.getLevelSystem()
                    .map(feature -> feature.getExpForLevel(robot.getCurrentLevel()))
                    .orElse(Integer.MAX_VALUE);

            if (xp > maxExp) {
                return new CommandResult(false, "Exp " + xp + " exceeds maximum requirement of " + maxExp + "xp for current level!");
            }

            robot.setExp(xp);
            return new CommandResult(true, "Set " + xp + "xp to " + Utility.getEntityCustomName(robot));
        });
    } // executeOwnerSetExp()

    /**
     * Sets level for robot selected by owner and index.
     * <p>
     * Validates against robot's max level.
     */
    private static int executeOwnerSetLevel(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "robot_index");
        int level = IntegerArgumentType.getInteger(ctx, "level_value");

        return executeOnOwnerRobot(ctx, player, index, robot -> {
            int maxLevel = robot.getLevelSystem()
                    .map(LevelFeature::getMaxLevel)
                    .orElse(200);

            if (level > maxLevel) {
                return new CommandResult(false, "Level " + level + " exceeds maximum requirement of " + maxLevel + " for this robot!");
            }

            robot.setCurrentLevel(level);
            return new CommandResult(true, "Set level " + level + " to " + Utility.getEntityCustomName(robot));
        });
    } // executeOwnerSetLevel()

    /**
     * Sets both level and XP for robot selected by owner and index.
     * <p>
     * Validates both values against robot's limits.
     */
    private static int executeOwnerSetAllCombat(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "robot_index");
        int level = IntegerArgumentType.getInteger(ctx, "level");
        int exp = IntegerArgumentType.getInteger(ctx, "exp");

        return executeOnOwnerRobot(ctx, player, index, robot -> {
            // Validate level
            int maxLevel = robot.getLevelSystem()
                    .map(LevelFeature::getMaxLevel)
                    .orElse(200);

            if (level > maxLevel) {
                return new CommandResult(false, "Level " + level + " exceeds maximum requirement of " + maxLevel + " for this robot!");
            }

            // Validate exp for target level
            int maxExp = robot.getLevelSystem()
                    .map(feature -> feature.getExpForLevel(level))
                    .orElse(Integer.MAX_VALUE);

            if (exp > maxExp) {
                return new CommandResult(false, "Exp " + exp + " exceeds maximum requirement of " + maxExp + "xp for level " + level + "!");
            }

            // Set both values
            robot.setCurrentLevel(level);
            robot.setExp(exp);

            String name = Utility.getEntityCustomName(robot);
            return new CommandResult(true, "Set " + name + " to level " + level + " with " + exp + "xp");
        });
    } // executeOwnerSetAllCombat()

    /**
     * Sets HP for robot selected by owner and index.
     */
    private static int executeOwnerSetHP(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "robot_index");
        int hp = IntegerArgumentType.getInteger(ctx, "value");

        return executeOnOwnerRobot(ctx, player, index, robot -> {
            robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(hp);
            robot.setHealth(hp);
            return new CommandResult(true, "Set HP to " + hp + " for " + Utility.getEntityCustomName(robot));
        });
    } // executeOwnerSetHP()

    /**
     * Sets attack for robot selected by owner and index.
     */
    private static int executeOwnerSetAttack(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "robot_index");
        int attack = IntegerArgumentType.getInteger(ctx, "value");

        return executeOnOwnerRobot(ctx, player, index, robot -> {
            robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(attack);
            return new CommandResult(true, "Set attack to " + attack + " for " + Utility.getEntityCustomName(robot));
        });
    } // executeOwnerSetAttack()

    /**
     * Sets defense for robot selected by owner and index.
     */
    private static int executeOwnerSetDefense(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "robot_index");
        int defense = IntegerArgumentType.getInteger(ctx, "value");

        return executeOnOwnerRobot(ctx, player, index, robot -> {
            robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ARMOR).setBaseValue(defense);
            return new CommandResult(true, "Set defense to " + defense + " for " + Utility.getEntityCustomName(robot));
        });
    } // executeOwnerSetDefense()

    /**
     * Sets speed for robot selected by owner and index.
     * <p>
     * Speed value is divided by 10 for Minecraft's movement speed scale.
     */
    private static int executeOwnerSetSpeed(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "robot_index");
        int speed = IntegerArgumentType.getInteger(ctx, "value");

        return executeOnOwnerRobot(ctx, player, index, robot -> {
            robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(speed / 10.0);
            return new CommandResult(true, "Set speed to " + speed + " for " + Utility.getEntityCustomName(robot));
        });
    } // executeOwnerSetSpeed()

    /**
     * Sets all attributes for robot selected by owner and index.
     * <p>
     * Batch operation for efficient attribute configuration.
     */
    private static int executeOwnerSetAllAttributes(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "robot_index");
        int hp = IntegerArgumentType.getInteger(ctx, "hp");
        int attack = IntegerArgumentType.getInteger(ctx, "attack");
        int defense = IntegerArgumentType.getInteger(ctx, "defense");
        int speed = IntegerArgumentType.getInteger(ctx, "speed");

        return executeOnOwnerRobot(ctx, player, index, robot -> {
            robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(hp);
            robot.setHealth(hp);
            robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(attack);
            robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ARMOR).setBaseValue(defense);
            robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(speed / 10.0);

            String name = Utility.getEntityCustomName(robot);
            return new CommandResult(true, "Set attributes (HP:" + hp + " ATK:" + attack + " DEF:" + defense + " SPD:" + speed + ") for " + name);
        });
    } // executeOwnerSetAllAttributes()

    /**
     * Sets fire protection for robot selected by owner and index.
     * <p>
     * Fire protection reduces damage from fire, lava, and burning.
     */
    private static int executeOwnerSetFireProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "robot_index");
        int level = IntegerArgumentType.getInteger(ctx, "level");

        return executeOnOwnerRobot(ctx, player, index, robot -> {
            robot.setFireProtection(level);
            return new CommandResult(true, "Set fire protection to " + level + " for " + Utility.getEntityCustomName(robot));
        });
    } // executeOwnerSetFireProtection()

    /**
     * Sets fall protection for robot selected by owner and index.
     * <p>
     * Fall protection reduces damage from falling.
     */
    private static int executeOwnerSetFallProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "robot_index");
        int level = IntegerArgumentType.getInteger(ctx, "level");

        return executeOnOwnerRobot(ctx, player, index, robot -> {
            robot.setFallProtection(level);
            return new CommandResult(true, "Set fall protection to " + level + " for " + Utility.getEntityCustomName(robot));
        });
    } // executeOwnerSetFallProtection()

    /**
     * Sets blast protection for robot selected by owner and index.
     * <p>
     * Blast protection reduces damage from explosions.
     */
    private static int executeOwnerSetBlastProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "robot_index");
        int level = IntegerArgumentType.getInteger(ctx, "level");

        return executeOnOwnerRobot(ctx, player, index, robot -> {
            robot.setBlastProtection(level);
            return new CommandResult(true, "Set blast protection to " + level + " for " + Utility.getEntityCustomName(robot));
        });
    } // executeOwnerSetBlastProtection()

    /**
     * Sets projectile protection for robot selected by owner and index.
     * <p>
     * Projectile protection reduces damage from arrows and other projectiles.
     */
    private static int executeOwnerSetProjectileProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "robot_index");
        int level = IntegerArgumentType.getInteger(ctx, "level");

        return executeOnOwnerRobot(ctx, player, index, robot -> {
            robot.setProjectileProtection(level);
            return new CommandResult(true, "Set projectile protection to " + level + " for " + Utility.getEntityCustomName(robot));
        });
    } // executeOwnerSetProjectileProtection()

    /**
     * Sets all protections for robot selected by owner and index.
     * <p>
     * Batch operation for efficient protection configuration.
     */
    private static int executeOwnerSetAllProtections(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "robot_index");
        int fire = IntegerArgumentType.getInteger(ctx, "fire");
        int fall = IntegerArgumentType.getInteger(ctx, "fall");
        int blast = IntegerArgumentType.getInteger(ctx, "blast");
        int projectile = IntegerArgumentType.getInteger(ctx, "projectile");

        return executeOnOwnerRobot(ctx, player, index, robot -> {
            robot.setFireProtection(fire);
            robot.setFallProtection(fall);
            robot.setBlastProtection(blast);
            robot.setProjectileProtection(projectile);

            String name = Utility.getEntityCustomName(robot);
            return new CommandResult(true, "Set protections (Fire:" + fire + " Fall:" + fall + " Blast:" + blast + " Projectile:" + projectile + ") for " + name);
        });
    } // executeOwnerSetAllProtections()

    /**
     * Sets appearance for robot selected by owner and index.
     * <p>
     * Changes robot's visual appearance.
     */
    private static int executeOwnerSetAppearance(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "robot_index");
        net.msymbios.llovelyr.framework.entity.enums.EntityTexture color = ctx.getArgument("color", net.msymbios.llovelyr.framework.entity.enums.EntityTexture.class);

        return executeOnOwnerRobot(ctx, player, index, robot -> {
            robot.setTexture(color);
            String colorName = color.Name().toLowerCase();
            return new CommandResult(true, "Set color to " + colorName + " for " + Utility.getEntityCustomName(robot));
        });
    } // executeOwnerSetAppearance()

    /**
     * Sets identifier (custom name) for robot selected by owner and index.
     * <p>
     * Sets custom name and makes it visible.
     */
    private static int executeOwnerSetIdentifier(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "robot_index");
        String nameString = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "name");

        return executeOnOwnerRobot(ctx, player, index, robot -> {
            Text name = Text.literal(nameString);
            robot.setCustomName(name);
            robot.setCustomNameVisible(true);
            return new CommandResult(true, "Set name to '" + nameString + "' for " + Utility.getEntityCustomName(robot));
        });
    } // executeOwnerSetIdentifier()

    // -- Owner Utility Command Executors --

    /**
     * Teleports player to robot selected by owner and index.
     */
    private static int executeOwnerTeleport(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "robot_index");

        LovelyRobotEntity robot = getOwnerRobotByIndex(player, index);
        if (robot == null) {
            ctx.getSource().sendError(Text.literal("Robot not found or offline"));
            return 0;
        }

        net.minecraft.server.network.ServerPlayerEntity serverPlayer = (net.minecraft.server.network.ServerPlayerEntity) player;
        serverPlayer.teleport(
                (ServerWorld) robot.getWorld(),
                robot.getX(),
                robot.getY(),
                robot.getZ(),
                robot.getYaw(),
                robot.getPitch()
        );

        String robotName = Utility.getEntityCustomName(robot);
        ctx.getSource().sendFeedback(() -> Text.literal("Teleported to " + robotName), false);
        return 1;
    } // executeOwnerTeleport()

    /**
     * Recalls robot to player location by owner and index.
     */
    private static int executeOwnerRecall(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "robot_index");

        return executeOnOwnerRobot(ctx, player, index, robot -> {
            robot.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
            String robotName = Utility.getEntityCustomName(robot);
            return new CommandResult(true, "Recalled " + robotName + " to " + player.getName().getString());
        });
    } // executeOwnerRecall()

    /**
     * Heals robot selected by owner and index to full health.
     */
    private static int executeOwnerHeal(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "robot_index");

        return executeOnOwnerRobot(ctx, player, index, robot -> {
            robot.setHealth(robot.getMaxHealth());
            String robotName = Utility.getEntityCustomName(robot);
            return new CommandResult(true, "Healed " + robotName);
        });
    } // executeOwnerHeal()

    /**
     * Heals all robots owned by specified player.
     */
    private static int executeOwnerHealAll(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        ServerWorld world = (ServerWorld) player.getWorld();
        OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(world);

        List<RobotRegistryEntry> robots = registry.getRobotsForOwner(player.getUuid());
        int healedCount = 0;

        for (RobotRegistryEntry entry : robots) {
            Object entityObj = entry.getEntity();
            if (entityObj instanceof LovelyRobotEntity robot && entry.isEntityValid()) {
                robot.setHealth(robot.getMaxHealth());
                healedCount++;
            }
        }

        int finalCount = healedCount;
        ctx.getSource().sendFeedback(
                () -> Text.literal("Healed " + finalCount + " robot(s) for " + player.getName().getString()),
                true
        );
        return healedCount;
    } // executeOwnerHealAll()

    /**
     * Displays comprehensive stats for robot selected by owner and index.
     */
    private static int executeOwnerStats(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "robot_index");

        LovelyRobotEntity robot = getOwnerRobotByIndex(player, index);
        if (robot == null) {
            ctx.getSource().sendError(Text.literal("Robot not found or offline"));
            return 0;
        }

        robot.displayGeneralMessage(true, false);
        return 1;
    } // executeOwnerStats()

    /**
     * Transfers robot ownership from one player to another.
     */
    private static int executeOwnerTransfer(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity fromPlayer = EntityArgumentType.getPlayer(ctx, "from_player");
        int index = IntegerArgumentType.getInteger(ctx, "robot_index");
        PlayerEntity toPlayer = EntityArgumentType.getPlayer(ctx, "to_player");

        LovelyRobotEntity robot = getOwnerRobotByIndex(fromPlayer, index);
        if (robot == null) {
            ctx.getSource().sendError(Text.literal("Robot not found or offline"));
            return 0;
        }

        // Unregister from old owner
        ServerWorld world = (ServerWorld) fromPlayer.getWorld();
        OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(world);
        registry.unregisterRobot(robot.getUuid());

        // Set new owner (registration happens automatically)
        robot.setOwner(toPlayer);

        String robotName = Utility.getEntityCustomName(robot);
        ctx.getSource().sendFeedback(
                () -> Text.literal("Transferred " + robotName + " from " + fromPlayer.getName().getString() + " to " + toPlayer.getName().getString()),
                true
        );
        return 1;
    } // executeOwnerTransfer()

    // -- Reload Command Executor --

    /**
     * Reloads configuration from disk.
     * <p>
     * Triggers config reload without server restart.
     */
    private static int executeReload(CommandContext<ServerCommandSource> ctx) {
        try {
            net.msymbios.llovelyr.source.LovelyConfigs.reload();
            ctx.getSource().sendFeedback(
                    () -> Text.literal("Configuration reloaded successfully").formatted(Formatting.GREEN),
                    true
            );
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendError(Text.literal("Failed to reload configuration: " + e.getMessage()));
            return 0;
        }
    } // executeReload()

    // -- List Command Executors --

    /**
     * Lists all players who own robots with robot counts.
     * <p>
     * Displays player names with robot counts in format "player_name (count)".
     * Useful for server administration and ownership overview.
     */
    private static int executeListOwners(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerWorld world = ctx.getSource().getWorld();
        OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(world);
        
        var ownerMap = registry.getAllOwners();
        
        if (ownerMap.isEmpty()) {
            ctx.getSource().sendFeedback(
                    () -> Text.literal("No robot owners found").formatted(Formatting.GRAY),
                    false
            );
            return 0;
        }

        ctx.getSource().sendFeedback(() -> LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_BAR).formatted(Formatting.WHITE), false);
        
        ownerMap.forEach((ownerUuid, robots) -> {
            String ownerName = world.getPlayerByUuid(ownerUuid) != null 
                    ? world.getPlayerByUuid(ownerUuid).getName().getString()
                    : ownerUuid.toString();
            int count = robots.size();
            
            ctx.getSource().sendFeedback(
                    () -> Text.literal("- " + ownerName + " (" + count + ")").formatted(Formatting.WHITE),
                    false
            );
        });
        
        return ownerMap.size();
    } // executeListOwners()

    /**
     * Lists all robots owned by specified player with detailed information.
     * <p>
     * Displays index, type, name, and dimension for each robot.
     * Index values can be used with owner-based commands for robot management.
     */
    private static int executeListPlayerRobots(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        ServerWorld world = (ServerWorld) player.getWorld();
        OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(world);
        
        List<RobotRegistryEntry> robots = registry.getRobotsForOwner(player.getUuid());
        
        if (robots.isEmpty()) {
            ctx.getSource().sendFeedback(
                    () -> Text.literal("No robots found for " + player.getName().getString()).formatted(Formatting.GRAY),
                    false
            );
            return 0;
        }

        // Header with separator
        ctx.getSource().sendFeedback(() -> LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_BAR).formatted(Formatting.WHITE), false);

        // Player name in brackets
        ctx.getSource().sendFeedback(() -> Text.literal("Player: (" + player.getName().getString() + ")").formatted(Formatting.WHITE), false);

        for (int i = 0; i < robots.size(); i++) {
            RobotRegistryEntry entry = robots.get(i);
            final int index = i;
            
            Object entityObj = entry.getEntity();
            if (entityObj instanceof InternalEntity robot) {
                // Get robot type name using LovelyIdentifier translation (same as displayExtra)
                Text robotTypeName = LovelyIdentifier.getTranslation(java.util.Objects.requireNonNull(EntityVariant.byName(robot.nativeEntity.getKey())));

                // Build display: [index] Type (CustomName) - dimension
                // If has custom name, show: Type (CustomName), otherwise just Type
                Text display = Text.literal("[" + index + "] ");
                
                if (robot.hasCustomName()) {
                    display = display.copy().append(robotTypeName).append(Text.literal(" (" + Utility.getEntityCustomName(robot) + ")"));
                } else {
                    display = display.copy().append(robotTypeName);
                }

                Text finalDisplay = display;
                ctx.getSource().sendFeedback(() -> finalDisplay.copy().formatted(Formatting.WHITE), false);
            } else {
                // For offline robots, use translation key
                EntityVariant variant = EntityVariant.byName(entry.getRobotType().replace("entity.llovelyr.", ""));
                Text robotTypeName = variant != null 
                    ? LovelyIdentifier.getTranslation(variant)
                    : Text.literal(entry.getRobotType());
                
                ctx.getSource().sendFeedback(
                        () -> Text.literal("[" + index + "] Offline/Unloaded (")
                                .append(robotTypeName)
                                .append(Text.literal(")"))
                                .formatted(Formatting.GRAY),
                        false
                );
            }
        }
        
        return robots.size();
    } // executeListPlayerRobots()

    // -- Helper Methods --

    /**
     * Retrieves robot by owner and index from registry.
     * <p>
     * <b>Validation:</b> Checks index bounds and entity validity.
     *
     * @param player owner player
     * @param index robot index
     * @return robot entity or null if not found/invalid
     */
    private static LovelyRobotEntity getOwnerRobotByIndex(PlayerEntity player, int index) {
        try {
            ServerWorld world = (ServerWorld) player.getWorld();
            OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(world);
            
            List<RobotRegistryEntry> robots = registry.getRobotsForOwner(player.getUuid());
            
            if (index < 0 || index >= robots.size()) {
                return null;
            }
            
            RobotRegistryEntry entry = robots.get(index);
            Object entityObj = entry.getEntity();
            
            if (entityObj instanceof LovelyRobotEntity robot && entry.isEntityValid()) {
                return robot;
            }
        } catch (Exception ignored) {}
        return null;
    } // getOwnerRobotByIndex()

    /**
     * Executes command action on robot selected by owner and index.
     * <p>
     * <b>Design Pattern:</b> Template method - handles registry lookup and validation
     * while delegating specific action to provided function.
     *
     * @param context command context
     * @param player owner player
     * @param index robot index
     * @param action function to execute on validated robot
     * @return 1 if successful, 0 if failed
     */
    private static int executeOnOwnerRobot(CommandContext<ServerCommandSource> context, PlayerEntity player, int index, IRobotCommandAction action) throws CommandSyntaxException {
        ServerWorld world = (ServerWorld) player.getWorld();
        OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(world);
        
        List<RobotRegistryEntry> robots = registry.getRobotsForOwner(player.getUuid());
        
        if (robots.isEmpty()) {
            context.getSource().sendError(Text.literal("Player " + player.getName().getString() + " has no registered robots"));
            return 0;
        }
        
        if (index < 0 || index >= robots.size()) {
            context.getSource().sendError(Text.literal("Invalid robot index " + index + " (player has " + robots.size() + " robot(s))"));
            return 0;
        }
        
        RobotRegistryEntry entry = robots.get(index);
        Object entityObj = entry.getEntity();
        
        if (!(entityObj instanceof LovelyRobotEntity robot) || !entry.isEntityValid()) {
            context.getSource().sendError(Text.literal("Robot is offline or unloaded"));
            return 0;
        }

        CommandResult result = action.execute(robot);
        
        if (result.success) {
            context.getSource().sendFeedback(() -> Text.literal(result.message), true);
            return 1;
        } else {
            context.getSource().sendError(Text.literal(result.message));
            return 0;
        }
    } // executeOnOwnerRobot()

    /**
     * Executes command action on robot in crosshair with common validation.
     * <p>
     * <b>Design Pattern:</b> Template method - handles common validation (robot presence,
     * ownership) while delegating specific action to provided function.
     *
     * @param context command context
     * @param action function to execute on validated robot
     * @return 1 if successful, 0 if failed
     */
    private static int executeOnRobot(CommandContext<ServerCommandSource> context, IRobotCommandAction action) throws CommandSyntaxException {
        PlayerEntity player = context.getSource().getPlayerOrThrow();
        LovelyRobotEntity robot = (LovelyRobotEntity) findEntityInFront(player);

        if (robot == null) {
            context.getSource().sendError(Text.literal("No robot found in crosshair"));
            return 0;
        }

        if (!robot.isOwner(player)) {
            context.getSource().sendError(Text.literal("You don't own " + Utility.getEntityCustomName(robot)));
            return 0;
        }

        CommandResult result = action.execute(robot);
        
        if (result.success) {
            context.getSource().sendFeedback(() -> Text.literal(result.message), true);
            return 1;
        } else {
            context.getSource().sendError(Text.literal(result.message));
            return 0;
        }
    } // executeOnRobot()

    /**
     * Finds robot in front of PlayerEntity using raycast.
     * <p>
     * <b>Range:</b> 5 block reach distance
     * <p>
     * <b>Implementation:</b> Creates search box along look vector and finds
     * closest InternalEntity.
     *
     * @param player player performing raycast
     * @return robot entity or null if none found
     */
    public static InternalEntity findEntityInFront(PlayerEntity player) {
        // Get player's look vector
        Vec3d eyePos = player.getEyePos();
        Vec3d lookVec = player.getRotationVec(1.0F);
        Vec3d endPos = eyePos.add(lookVec.multiply(5.0)); // 5 block reach

        // Perform entity raycast
        Box searchBox = new Box(eyePos, endPos).expand(1.0);
        List<Entity> entities = player.getWorld().getOtherEntities(player, searchBox);

        LovelyRobotEntity closestRobot = null;
        double closestDistance = Double.MAX_VALUE;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobotEntity robot) {
                // Check if ray intersects with entity bounding box
                Optional<Vec3d> hit = entity.getBoundingBox().raycast(eyePos, endPos);
                if (hit.isPresent()) {
                    double distance = eyePos.distanceTo(hit.get());
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestRobot = robot;
                    }
                }
            }
        }

        return closestRobot;
    } // findEntityInFront()

    // -- Nested Classes & Interfaces --

    /**
     * Command execution result with success status and message.
     */
    private record CommandResult(boolean success, String message) {} // Record: CommandResult

    /**
     * Functional interface for robot command actions.
     */
    @FunctionalInterface
    private interface IRobotCommandAction {
        // -- Methods --
        CommandResult execute(LovelyRobotEntity robot);
    } // Interfaces: IRobotCommandAction

} // Class: NativeCommands