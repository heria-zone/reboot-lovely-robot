package net.msymbios.llovelyr.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.msymbios.llovelyr.common.entity.internal.InternalEntity;
import net.msymbios.llovelyr.common.entity.internal.InternalParticle;
import net.msymbios.llovelyr.framework.registry.OwnerRobotRegistry;
import net.msymbios.llovelyr.framework.registry.RobotRegistryEntry;
import net.msymbios.llovelyr.lib.entity.features.LevelFeature;
import net.msymbios.llovelyr.lib.entity.features.ProtectionFeature;
import net.msymbios.llovelyr.lib.registry.RobotRegistryManager;
import net.msymbios.llovelyr.source.entity.common.LovelyRobotEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * <p>Registry-based robot management commands with owner + index targeting.<p>
 * <p>
 * <b>Architecture:</b> Complements existing entity selector and crosshair commands
 * with registry-based targeting, enabling management of offline/distant robots.
 * <p>
 * <b>Command Structure:</b> Three targeting methods:
 * - Group: Entity selectors (@e[type=...])
 * - Owner: Registry-based (owner UUID + robot index)
 * - Robot: Raycast targeting (looking at robot)
 */
public class LovelyRobotCommands {

    // -- Public Methods --

    /**
     * Registers registry-based robot management commands.
     * <p>
     * <b>Command Structure:</b> Root literal "llovelyr" with owner-based subcommands.
     * 
     * @param dispatcher command dispatcher for registration
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("llovelyr")
                .requires(source -> source.hasPermission(2))
                .then(buildOwnerListCommand())
                .then(buildOwnerSetLevelCommand())
                .then(buildOwnerSetExpCommand())
                .then(buildOwnerSetProtectionCommand())
                .then(buildOwnerTeleportCommand())
                .then(buildOwnerRecallCommand())
                .then(buildOwnerHealAllCommand())
                .then(buildOwnerStatsCommand())
                .then(buildOwnerTransferCommand())
        );
    } // register()

    // -- Command Builders --

    /**
     * Builds owner list command to display all robots for a player.
     * <p>
     * <b>Usage:</b> /llovelyr owner list [player]
     * <p>
     * <b>Output:</b> Index, name, type, dimension, position, health for each robot.
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildOwnerListCommand() {
        return Commands.literal("owner")
            .then(Commands.literal("list")
                .executes(ctx -> executeOwnerList(ctx, null))
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(ctx -> executeOwnerList(ctx, EntityArgument.getPlayer(ctx, "player")))
                )
            );
    } // buildOwnerListCommand()

    /**
     * Builds owner set level command with validation.
     * <p>
     * <b>Usage:</b> /llovelyr owner set level <player> <index> <value>
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildOwnerSetLevelCommand() {
        return Commands.literal("owner")
            .then(Commands.literal("set")
                .then(Commands.literal("level")
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("index", IntegerArgumentType.integer(0))
                            .then(Commands.argument("value", IntegerArgumentType.integer(1, 200))
                                .executes(LovelyRobotCommands::executeOwnerSetLevel)
                            )
                        )
                    )
                )
            );
    } // buildOwnerSetLevelCommand()

    /**
     * Builds owner set exp command with auto-level-up.
     * <p>
     * <b>Usage:</b> /llovelyr owner set exp <player> <index> <value>
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildOwnerSetExpCommand() {
        return Commands.literal("owner")
            .then(Commands.literal("set")
                .then(Commands.literal("exp")
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("index", IntegerArgumentType.integer(0))
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(LovelyRobotCommands::executeOwnerSetExp)
                            )
                        )
                    )
                )
            );
    } // buildOwnerSetExpCommand()

    /**
     * Builds owner set protection command with validation.
     * <p>
     * <b>Usage:</b> /llovelyr owner set protection <type> <player> <index> <value>
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildOwnerSetProtectionCommand() {
        return Commands.literal("owner")
            .then(Commands.literal("set")
                .then(Commands.literal("protection")
                    .then(Commands.literal("fire")
                        .then(Commands.argument("player", EntityArgument.player())
                            .then(Commands.argument("index", IntegerArgumentType.integer(0))
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 80))
                                    .executes(ctx -> executeOwnerSetProtection(ctx, "fire"))
                                )
                            )
                        )
                    )
                    .then(Commands.literal("fall")
                        .then(Commands.argument("player", EntityArgument.player())
                            .then(Commands.argument("index", IntegerArgumentType.integer(0))
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 80))
                                    .executes(ctx -> executeOwnerSetProtection(ctx, "fall"))
                                )
                            )
                        )
                    )
                    .then(Commands.literal("blast")
                        .then(Commands.argument("player", EntityArgument.player())
                            .then(Commands.argument("index", IntegerArgumentType.integer(0))
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 80))
                                    .executes(ctx -> executeOwnerSetProtection(ctx, "blast"))
                                )
                            )
                        )
                    )
                    .then(Commands.literal("projectile")
                        .then(Commands.argument("player", EntityArgument.player())
                            .then(Commands.argument("index", IntegerArgumentType.integer(0))
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 80))
                                    .executes(ctx -> executeOwnerSetProtection(ctx, "projectile"))
                                )
                            )
                        )
                    )
                )
            );
    } // buildOwnerSetProtectionCommand()

    /**
     * Builds owner teleport command to teleport player to robot.
     * <p>
     * <b>Usage:</b> /llovelyr owner teleport <player> <index>
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildOwnerTeleportCommand() {
        return Commands.literal("owner")
            .then(Commands.literal("teleport")
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("index", IntegerArgumentType.integer(0))
                        .executes(LovelyRobotCommands::executeOwnerTeleport)
                    )
                )
            );
    } // buildOwnerTeleportCommand()

    /**
     * Builds owner recall command to teleport robot to player.
     * <p>
     * <b>Usage:</b> /llovelyr owner recall <player> <index>
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildOwnerRecallCommand() {
        return Commands.literal("owner")
            .then(Commands.literal("recall")
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("index", IntegerArgumentType.integer(0))
                        .executes(LovelyRobotCommands::executeOwnerRecall)
                    )
                )
            );
    } // buildOwnerRecallCommand()

    /**
     * Builds owner heal all command to heal all robots for a player.
     * <p>
     * <b>Usage:</b> /llovelyr owner healall <player>
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildOwnerHealAllCommand() {
        return Commands.literal("owner")
            .then(Commands.literal("healall")
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(LovelyRobotCommands::executeOwnerHealAll)
                )
            );
    } // buildOwnerHealAllCommand()

    /**
     * Builds owner stats command to display robot stats.
     * <p>
     * <b>Usage:</b> /llovelyr owner stats <player> <index>
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildOwnerStatsCommand() {
        return Commands.literal("owner")
            .then(Commands.literal("stats")
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("index", IntegerArgumentType.integer(0))
                        .executes(LovelyRobotCommands::executeOwnerStats)
                    )
                )
            );
    } // buildOwnerStatsCommand()

    /**
     * Builds owner transfer command to transfer robot ownership.
     * <p>
     * <b>Usage:</b> /llovelyr owner transfer <from_player> <index> <to_player>
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildOwnerTransferCommand() {
        return Commands.literal("owner")
            .then(Commands.literal("transfer")
                .then(Commands.argument("from_player", EntityArgument.player())
                    .then(Commands.argument("index", IntegerArgumentType.integer(0))
                        .then(Commands.argument("to_player", EntityArgument.player())
                            .executes(LovelyRobotCommands::executeOwnerTransfer)
                        )
                    )
                )
            );
    } // buildOwnerTransferCommand()

    // -- Command Executors --

    /**
     * Executes owner list command.
     * <p>
     * <b>Behavior:</b> Displays all robots owned by specified player with index,
     * name, type, dimension, position, and health information.
     */
    private static int executeOwnerList(CommandContext<CommandSourceStack> ctx, Player targetPlayer) throws CommandSyntaxException {
        Player player = targetPlayer != null ? targetPlayer : ctx.getSource().getPlayerOrException();
        ServerLevel level = (ServerLevel) player.level();
        OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(level);
        
        List<RobotRegistryEntry> robots = registry.getRobotsForOwner(player.getUUID());
        
        if (robots.isEmpty()) {
            ctx.getSource().sendSuccess(
                () -> Component.literal("No robots registered for " + player.getName().getString()),
                false
            );
            return 0;
        }
        
        ctx.getSource().sendSuccess(
            () -> Component.literal("=== Robots for " + player.getName().getString() + " ===").withStyle(ChatFormatting.GOLD),
            false
        );
        
        for (int i = 0; i < robots.size(); i++) {
            RobotRegistryEntry entry = robots.get(i);
            final int index = i;
            
            Object entityObj = entry.getEntity();
            if (entityObj instanceof InternalEntity robot) {
                String name = robot.hasCustomName() ? robot.getCustomName().getString() : "Unnamed";
                String type = entry.getRobotType();
                String dimension = robot.level().dimension().location().toString();
                BlockPos pos = robot.blockPosition();
                float health = robot.getHealth();
                float maxHealth = robot.getMaxHealth();
                
                ctx.getSource().sendSuccess(
                    () -> Component.literal(String.format("[%d] %s (%s) - %s @ (%d, %d, %d) - HP: %.1f/%.1f",
                        index, name, type, dimension, pos.getX(), pos.getY(), pos.getZ(), health, maxHealth))
                        .withStyle(ChatFormatting.WHITE),
                    false
                );
            } else {
                ctx.getSource().sendSuccess(
                    () -> Component.literal(String.format("[%d] Offline/Unloaded (%s)", index, entry.getRobotType()))
                        .withStyle(ChatFormatting.GRAY),
                    false
                );
            }
        }
        
        return robots.size();
    } // executeOwnerList()

    /**
     * Executes owner set level command with validation.
     */
    private static int executeOwnerSetLevel(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = EntityArgument.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "index");
        int newLevel = IntegerArgumentType.getInteger(ctx, "value");
        
        InternalEntity robot = getRobotByIndex(ctx, player, index);
        
        // Get max level from robot's entity type
        Optional<LevelFeature> featureOpt = robot.nativeEntity.getFeature(LevelFeature.class);
        if (featureOpt.isPresent()) {
            int maxLevel = featureOpt.get().getMaxLevel();
            if (newLevel > maxLevel) {
                throw new SimpleCommandExceptionType(
                    Component.literal("Level " + newLevel + " exceeds maximum " + maxLevel + " for " + robot.nativeEntity.getKey())
                ).create();
            }
        }
        
        robot.setLevel(newLevel);
        
        String robotName = robot.hasCustomName() ? robot.getCustomName().getString() : "Robot";
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set level to " + newLevel + " for " + robotName),
            true
        );
        
        return Command.SINGLE_SUCCESS;
    } // executeOwnerSetLevel()

    /**
     * Executes owner set exp command with auto-level-up.
     */
    private static int executeOwnerSetExp(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = EntityArgument.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "index");
        int newExp = IntegerArgumentType.getInteger(ctx, "value");

        LovelyRobotEntity robot = (LovelyRobotEntity)getRobotByIndex(ctx, player, index);
        
        int oldLevel = robot.getLevel();
        robot.addExp(newExp);
        
        // Auto-level-up logic would be in setExp or we trigger it here
        // For now, just set the exp
        int newLevel = robot.getLevel();
        
        String robotName = robot.hasCustomName() ? robot.getCustomName().getString() : "Robot";
        if (newLevel > oldLevel) {
            ctx.getSource().sendSuccess(
                () -> Component.literal(robotName + " leveled up from " + oldLevel + " to " + newLevel + "!"),
                true
            );
        } else {
            ctx.getSource().sendSuccess(
                () -> Component.literal("Set XP to " + newExp + " for " + robotName),
                true
            );
        }
        
        return Command.SINGLE_SUCCESS;
    } // executeOwnerSetExp()

    /**
     * Executes owner set protection command with validation.
     */
    private static int executeOwnerSetProtection(CommandContext<CommandSourceStack> ctx, String protectionType) throws CommandSyntaxException {
        Player player = EntityArgument.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "index");
        int newLevel = IntegerArgumentType.getInteger(ctx, "value");
        
        InternalEntity robot = getRobotByIndex(ctx, player, index);
        
        // Get max protection level from robot's entity type
        Optional<ProtectionFeature> featureOpt = robot.nativeEntity.getFeature(ProtectionFeature.class);
        if (featureOpt.isPresent()) {
            ProtectionFeature feature = featureOpt.get();
            int maxLevel = switch (protectionType) {
                case "fire" -> feature.getMaxFireProtection();
                case "fall" -> feature.getMaxFallProtection();
                case "blast" -> feature.getMaxBlastProtection();
                case "projectile" -> feature.getMaxProjectileProtection();
                default -> 80;
            };
            
            if (newLevel > maxLevel) {
                throw new SimpleCommandExceptionType(
                    Component.literal(protectionType + " protection level " + newLevel + " exceeds maximum " + maxLevel)
                ).create();
            }
        }
        
        // Set protection based on type
        switch (protectionType) {
            case "fire" -> robot.setFireProtection(newLevel);
            case "fall" -> robot.setFallProtection(newLevel);
            case "blast" -> robot.setBlastProtection(newLevel);
            case "projectile" -> robot.setProjectileProtection(newLevel);
        }
        
        String robotName = robot.hasCustomName() ? robot.getCustomName().getString() : "Robot";
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set " + protectionType + " protection to " + newLevel + " for " + robotName),
            true
        );
        
        return Command.SINGLE_SUCCESS;
    } // executeOwnerSetProtection()

    /**
     * Executes owner teleport command to teleport player to robot.
     */
    private static int executeOwnerTeleport(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = EntityArgument.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "index");
        
        InternalEntity robot = getRobotByIndex(ctx, player, index);
        
        // Teleport player to robot's position
        ServerPlayer serverPlayer = (ServerPlayer) player;
        serverPlayer.teleportTo(
            (ServerLevel) robot.level(),
            robot.getX(),
            robot.getY(),
            robot.getZ(),
            robot.getYRot(),
            robot.getXRot()
        );
        
        String robotName = robot.hasCustomName() ? robot.getCustomName().getString() : "Robot";
        ctx.getSource().sendSuccess(
            () -> Component.literal("Teleported to " + robotName),
            false
        );
        
        return Command.SINGLE_SUCCESS;
    } // executeOwnerTeleport()

    /**
     * Executes owner recall command to teleport robot to player.
     */
    private static int executeOwnerRecall(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = EntityArgument.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "index");
        
        InternalEntity robot = getRobotByIndex(ctx, player, index);
        
        // Teleport robot to player's position
        robot.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
        
        String robotName = robot.hasCustomName() ? robot.getCustomName().getString() : "Robot";
        ctx.getSource().sendSuccess(
            () -> Component.literal("Recalled " + robotName + " to " + player.getName().getString()),
            false
        );
        
        return Command.SINGLE_SUCCESS;
    } // executeOwnerRecall()

    /**
     * Executes owner heal all command to heal all robots for a player.
     */
    private static int executeOwnerHealAll(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = EntityArgument.getPlayer(ctx, "player");
        ServerLevel level = (ServerLevel) player.level();
        OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(level);
        
        List<RobotRegistryEntry> robots = registry.getRobotsForOwner(player.getUUID());
        int healedCount = 0;
        
        for (RobotRegistryEntry entry : robots) {
            Object entityObj = entry.getEntity();
            if (entityObj instanceof InternalEntity robot && entry.isEntityValid()) {
                robot.setHealth(robot.getMaxHealth());
                healedCount++;
            }
        }
        
        final int finalCount = healedCount;
        ctx.getSource().sendSuccess(
            () -> Component.literal("Healed " + finalCount + " robot(s) for " + player.getName().getString()),
            true
        );
        
        return healedCount;
    } // executeOwnerHealAll()

    /**
     * Executes owner stats command to display comprehensive robot stats.
     */
    private static int executeOwnerStats(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = EntityArgument.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "index");

        LovelyRobotEntity robot = (LovelyRobotEntity)getRobotByIndex(ctx, player, index);
        
        String name = robot.hasCustomName() ? robot.getCustomName().getString() : "Unnamed";
        BlockPos pos = robot.blockPosition();
        String dimension = robot.level().dimension().location().toString();
        
        ctx.getSource().sendSuccess(
            () -> Component.literal("=== Stats for " + name + " ===").withStyle(ChatFormatting.GOLD),
            false
        );
        
        ctx.getSource().sendSuccess(
            () -> Component.literal("Level: " + robot.getLevel() + " | XP: " + robot.getExp()).withStyle(ChatFormatting.WHITE),
            false
        );
        
        ctx.getSource().sendSuccess(
            () -> Component.literal(String.format("Health: %.1f/%.1f", robot.getHealth(), robot.getMaxHealth())).withStyle(ChatFormatting.WHITE),
            false
        );
        
        ctx.getSource().sendSuccess(
            () -> Component.literal("Position: " + dimension + " @ (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")").withStyle(ChatFormatting.WHITE),
            false
        );
        
        ctx.getSource().sendSuccess(
            () -> Component.literal(String.format("Protections: Fire=%d Fall=%d Blast=%d Projectile=%d",
                robot.getFireProtection(), robot.getFallProtection(), robot.getBlastProtection(), robot.getProjectileProtection()))
                .withStyle(ChatFormatting.WHITE),
            false
        );
        
        return Command.SINGLE_SUCCESS;
    } // executeOwnerStats()

    /**
     * Executes owner transfer command to transfer robot ownership.
     */
    private static int executeOwnerTransfer(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player fromPlayer = EntityArgument.getPlayer(ctx, "from_player");
        int index = IntegerArgumentType.getInteger(ctx, "index");
        Player toPlayer = EntityArgument.getPlayer(ctx, "to_player");
        
        InternalEntity robot = getRobotByIndex(ctx, fromPlayer, index);
        
        // Unregister from old owner
        ServerLevel level = (ServerLevel) fromPlayer.level();
        OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(level);
        registry.unregisterRobot(robot.getUUID());
        
        // Set new owner (registration happens automatically in setTame)
        robot.setOwnerUUID(toPlayer.getUUID());
        InternalParticle.HappyVillager(robot);
        
        String robotName = robot.hasCustomName() ? robot.getCustomName().getString() : "Robot";
        ctx.getSource().sendSuccess(
            () -> Component.literal("Transferred " + robotName + " from " + fromPlayer.getName().getString() + " to " + toPlayer.getName().getString()),
            true
        );
        
        return Command.SINGLE_SUCCESS;
    } // executeOwnerTransfer()

    // -- Helper Methods --

    /**
     * Gets robot by owner and index from registry.
     * <p>
     * <b>Validation:</b> Checks index bounds, entity validity, and throws
     * appropriate exceptions with descriptive messages.
     * 
     * @param ctx command context
     * @param player owner player
     * @param index robot index
     * @return robot entity
     * @throws CommandSyntaxException if robot not found or invalid
     */
    private static InternalEntity getRobotByIndex(CommandContext<CommandSourceStack> ctx, Player player, int index) throws CommandSyntaxException {
        ServerLevel level = (ServerLevel) player.level();
        OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(level);
        
        List<RobotRegistryEntry> robots = registry.getRobotsForOwner(player.getUUID());
        
        if (robots.isEmpty()) {
            throw new SimpleCommandExceptionType(
                Component.literal("Player " + player.getName().getString() + " has no registered robots")
            ).create();
        }
        
        if (index < 0 || index >= robots.size()) {
            throw new SimpleCommandExceptionType(
                Component.literal("Invalid robot index " + index + " (player has " + robots.size() + " robot(s))")
            ).create();
        }
        
        RobotRegistryEntry entry = robots.get(index);
        Object entityObj = entry.getEntity();
        
        if (!(entityObj instanceof InternalEntity)) {
            throw new SimpleCommandExceptionType(
                Component.literal("Robot is offline or unloaded")
            ).create();
        }
        
        return (InternalEntity) entityObj;
    } // getRobotByIndex()

    /**
     * Gets target robots using multiple targeting methods.
     * <p>
     * <b>Priority:</b>
     * 1. Entity selector ("targets" argument)
     * 2. Owner + index (owner + robot_index arguments)
     * 3. Raycast (no arguments, uses player look vector)
     * 
     * @param ctx command context
     * @return collection of target robots
     * @throws CommandSyntaxException if no valid targets found
     */
    private static Collection<? extends Entity> getTargetRobots(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        // Try entity selector first
        try {
            return EntityArgument.getEntities(ctx, "targets");
        } catch (IllegalArgumentException e) {
            // Argument not present, try next method
        }
        
        // Try owner + index
        try {
            Player owner = EntityArgument.getPlayer(ctx, "owner");
            int robotIndex = IntegerArgumentType.getInteger(ctx, "robot_index");
            InternalEntity robot = getRobotByIndex(ctx, owner, robotIndex);
            return List.of(robot);
        } catch (IllegalArgumentException e) {
            // Arguments not present, try next method
        }
        
        // Try raycast
        Player player = ctx.getSource().getPlayerOrException();
        InternalEntity robot = findRobotInFront(player);
        
        if (robot == null) {
            throw new SimpleCommandExceptionType(
                Component.literal("No robot found. Use entity selector, owner+index, or look at a robot.")
            ).create();
        }
        
        return List.of(robot);
    } // getTargetRobots()

    /**
     * Finds robot in front of player using raycast.
     * <p>
     * <b>Range:</b> 5 block reach distance
     * <p>
     * <b>Implementation:</b> Creates search box along look vector and finds
     * closest InternalEntity.
     * 
     * @param player player performing raycast
     * @return robot entity or null if none found
     */
    private static InternalEntity findRobotInFront(Player player) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getViewVector(1.0F);
        Vec3 endPos = eyePos.add(lookVec.scale(5.0));
        
        AABB searchBox = new AABB(eyePos, endPos).inflate(1.0);
        List<Entity> entities = player.level().getEntities(player, searchBox);
        
        InternalEntity closestRobot = null;
        double closestDistance = Double.MAX_VALUE;
        
        for (Entity entity : entities) {
            if (entity instanceof InternalEntity robot) {
                Optional<Vec3> hit = entity.getBoundingBox().clip(eyePos, endPos);
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
    } // findRobotInFront()

} // Class: LovelyRobotCommands

