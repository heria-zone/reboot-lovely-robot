package net.msymbios.llovelyr.common.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
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
     * Registers registry-based robot management CommandManager.
     * <p>
     * <b>Command Structure:</b> Root literal "llovelyr" with owner-based subCommandManager.
     * 
     * @param dispatcher command dispatcher for registration
     */
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            CommandManager.literal("llovelyr")
                .requires(source -> source.hasPermissionLevel(2))
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
     * Builds owner list command to display all robots for a PlayerEntity.
     * <p>
     * <b>Usage:</b> /llovelyr owner list [PlayerEntity]
     * <p>
     * <b>Output:</b> Index, name, type, dimension, position, health for each robot.
     */
    private static ArgumentBuilder<ServerCommandSource, ?> buildOwnerListCommand() {
        return CommandManager.literal("owner")
            .then(CommandManager.literal("list")
                .executes(ctx -> executeOwnerList(ctx, null))
                .then(CommandManager.argument("PlayerEntity", EntityArgumentType.player())
                    .executes(ctx -> executeOwnerList(ctx, EntityArgumentType.getPlayer(ctx, "player")))
                )
            );
    } // buildOwnerListCommand()

    /**
     * Builds owner set level command with validation.
     * <p>
     * <b>Usage:</b> /llovelyr owner set level <PlayerEntity> <index> <value>
     */
    private static ArgumentBuilder<ServerCommandSource, ?> buildOwnerSetLevelCommand() {
        return CommandManager.literal("owner")
            .then(CommandManager.literal("set")
                .then(CommandManager.literal("level")
                    .then(CommandManager.argument("PlayerEntity", EntityArgumentType.player())
                        .then(CommandManager.argument("index", IntegerArgumentType.integer(0))
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(1, 200))
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
     * <b>Usage:</b> /llovelyr owner set exp <PlayerEntity> <index> <value>
     */
    private static ArgumentBuilder<ServerCommandSource, ?> buildOwnerSetExpCommand() {
        return CommandManager.literal("owner")
            .then(CommandManager.literal("set")
                .then(CommandManager.literal("exp")
                    .then(CommandManager.argument("PlayerEntity", EntityArgumentType.player())
                        .then(CommandManager.argument("index", IntegerArgumentType.integer(0))
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
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
     * <b>Usage:</b> /llovelyr owner set protection <type> <PlayerEntity> <index> <value>
     */
    private static ArgumentBuilder<ServerCommandSource, ?> buildOwnerSetProtectionCommand() {
        return CommandManager.literal("owner")
            .then(CommandManager.literal("set")
                .then(CommandManager.literal("protection")
                    .then(CommandManager.literal("fire")
                        .then(CommandManager.argument("PlayerEntity", EntityArgumentType.player())
                            .then(CommandManager.argument("index", IntegerArgumentType.integer(0))
                                .then(CommandManager.argument("value", IntegerArgumentType.integer(0, 80))
                                    .executes(ctx -> executeOwnerSetProtection(ctx, "fire"))
                                )
                            )
                        )
                    )
                    .then(CommandManager.literal("fall")
                        .then(CommandManager.argument("PlayerEntity", EntityArgumentType.player())
                            .then(CommandManager.argument("index", IntegerArgumentType.integer(0))
                                .then(CommandManager.argument("value", IntegerArgumentType.integer(0, 80))
                                    .executes(ctx -> executeOwnerSetProtection(ctx, "fall"))
                                )
                            )
                        )
                    )
                    .then(CommandManager.literal("blast")
                        .then(CommandManager.argument("PlayerEntity", EntityArgumentType.player())
                            .then(CommandManager.argument("index", IntegerArgumentType.integer(0))
                                .then(CommandManager.argument("value", IntegerArgumentType.integer(0, 80))
                                    .executes(ctx -> executeOwnerSetProtection(ctx, "blast"))
                                )
                            )
                        )
                    )
                    .then(CommandManager.literal("projectile")
                        .then(CommandManager.argument("PlayerEntity", EntityArgumentType.player())
                            .then(CommandManager.argument("index", IntegerArgumentType.integer(0))
                                .then(CommandManager.argument("value", IntegerArgumentType.integer(0, 80))
                                    .executes(ctx -> executeOwnerSetProtection(ctx, "projectile"))
                                )
                            )
                        )
                    )
                )
            );
    } // buildOwnerSetProtectionCommand()

    /**
     * Builds owner teleport command to teleport PlayerEntity to robot.
     * <p>
     * <b>Usage:</b> /llovelyr owner teleport <PlayerEntity> <index>
     */
    private static ArgumentBuilder<ServerCommandSource, ?> buildOwnerTeleportCommand() {
        return CommandManager.literal("owner")
            .then(CommandManager.literal("teleport")
                .then(CommandManager.argument("PlayerEntity", EntityArgumentType.player())
                    .then(CommandManager.argument("index", IntegerArgumentType.integer(0))
                        .executes(LovelyRobotCommands::executeOwnerTeleport)
                    )
                )
            );
    } // buildOwnerTeleportCommand()

    /**
     * Builds owner recall command to teleport robot to PlayerEntity.
     * <p>
     * <b>Usage:</b> /llovelyr owner recall <PlayerEntity> <index>
     */
    private static ArgumentBuilder<ServerCommandSource, ?> buildOwnerRecallCommand() {
        return CommandManager.literal("owner")
            .then(CommandManager.literal("recall")
                .then(CommandManager.argument("PlayerEntity", EntityArgumentType.player())
                    .then(CommandManager.argument("index", IntegerArgumentType.integer(0))
                        .executes(LovelyRobotCommands::executeOwnerRecall)
                    )
                )
            );
    } // buildOwnerRecallCommand()

    /**
     * Builds owner heal all command to heal all robots for a PlayerEntity.
     * <p>
     * <b>Usage:</b> /llovelyr owner healall <PlayerEntity>
     */
    private static ArgumentBuilder<ServerCommandSource, ?> buildOwnerHealAllCommand() {
        return CommandManager.literal("owner")
            .then(CommandManager.literal("healall")
                .then(CommandManager.argument("PlayerEntity", EntityArgumentType.player())
                    .executes(LovelyRobotCommands::executeOwnerHealAll)
                )
            );
    } // buildOwnerHealAllCommand()

    /**
     * Builds owner stats command to display robot stats.
     * <p>
     * <b>Usage:</b> /llovelyr owner stats <PlayerEntity> <index>
     */
    private static ArgumentBuilder<ServerCommandSource, ?> buildOwnerStatsCommand() {
        return CommandManager.literal("owner")
            .then(CommandManager.literal("stats")
                .then(CommandManager.argument("PlayerEntity", EntityArgumentType.player())
                    .then(CommandManager.argument("index", IntegerArgumentType.integer(0))
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
    private static ArgumentBuilder<ServerCommandSource, ?> buildOwnerTransferCommand() {
        return CommandManager.literal("owner")
            .then(CommandManager.literal("transfer")
                .then(CommandManager.argument("from_player", EntityArgumentType.player())
                    .then(CommandManager.argument("index", IntegerArgumentType.integer(0))
                        .then(CommandManager.argument("to_player", EntityArgumentType.player())
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
     * <b>Behavior:</b> Displays all robots owned by specified PlayerEntity with index,
     * name, type, dimension, position, and health information.
     */
    private static int executeOwnerList(CommandContext<ServerCommandSource> ctx, PlayerEntity targetPlayer) throws CommandSyntaxException {
        PlayerEntity player = targetPlayer != null ? targetPlayer : ctx.getSource().getPlayerOrThrow();
        ServerWorld level = (ServerWorld) player.getWorld();
        OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(level);
        
        List<RobotRegistryEntry> robots = registry.getRobotsForOwner(player.getUuid());
        
        if (robots.isEmpty()) {
            ctx.getSource().sendFeedback(
                () -> Text.literal("No robots registered for " + player.getName().getString()),
                false
            );
            return 0;
        }
        
        ctx.getSource().sendFeedback(
            () -> Text.literal("=== Robots for " + player.getName().getString() + " ===").formatted(Formatting.GOLD),
            false
        );
        
        for (int i = 0; i < robots.size(); i++) {
            RobotRegistryEntry entry = robots.get(i);
            final int index = i;
            
            Object entityObj = entry.getEntity();
            if (entityObj instanceof InternalEntity robot) {
                String name = robot.hasCustomName() ? robot.getCustomName().getString() : "Unnamed";
                String type = entry.getRobotType();
                String dimension = robot.getWorld().getDimensionKey().getValue().toString();
                BlockPos pos = robot.getBlockPos();
                float health = robot.getHealth();
                float maxHealth = robot.getMaxHealth();
                
                ctx.getSource().sendFeedback(
                    () -> Text.literal(String.format("[%d] %s (%s) - %s @ (%d, %d, %d) - HP: %.1f/%.1f",
                        index, name, type, dimension, pos.getX(), pos.getY(), pos.getZ(), health, maxHealth))
                        .formatted(Formatting.WHITE),
                    false
                );
            } else {
                ctx.getSource().sendFeedback(
                    () -> Text.literal(String.format("[%d] Offline/Unloaded (%s)", index, entry.getRobotType()))
                        .formatted(Formatting.GRAY),
                    false
                );
            }
        }
        
        return robots.size();
    } // executeOwnerList()

    /**
     * Executes owner set level command with validation.
     */
    private static int executeOwnerSetLevel(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "index");
        int newLevel = IntegerArgumentType.getInteger(ctx, "value");
        
        InternalEntity robot = getRobotByIndex(ctx, player, index);
        
        // Get max level from robot's entity type
        Optional<LevelFeature> featureOpt = robot.nativeEntity.getFeature(LevelFeature.class);
        if (featureOpt.isPresent()) {
            int maxLevel = featureOpt.get().getMaxLevel();
            if (newLevel > maxLevel) {
                throw new SimpleCommandExceptionType(
                    Text.literal("Level " + newLevel + " exceeds maximum " + maxLevel + " for " + robot.nativeEntity.getKey())
                ).create();
            }
        }
        
        robot.setLevel(newLevel);
        
        String robotName = robot.hasCustomName() ? robot.getCustomName().getString() : "Robot";
        ctx.getSource().sendFeedback(
            () -> Text.literal("Set level to " + newLevel + " for " + robotName),
            true
        );
        
        return Command.SINGLE_SUCCESS;
    } // executeOwnerSetLevel()

    /**
     * Executes owner set exp command with auto-level-up.
     */
    private static int executeOwnerSetExp(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
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
            ctx.getSource().sendFeedback(
                () -> Text.literal(robotName + " leveled up from " + oldLevel + " to " + newLevel + "!"),
                true
            );
        } else {
            ctx.getSource().sendFeedback(
                () -> Text.literal("Set XP to " + newExp + " for " + robotName),
                true
            );
        }
        
        return Command.SINGLE_SUCCESS;
    } // executeOwnerSetExp()

    /**
     * Executes owner set protection command with validation.
     */
    private static int executeOwnerSetProtection(CommandContext<ServerCommandSource> ctx, String protectionType) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
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
                    Text.literal(protectionType + " protection level " + newLevel + " exceeds maximum " + maxLevel)
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
        ctx.getSource().sendFeedback(
            () -> Text.literal("Set " + protectionType + " protection to " + newLevel + " for " + robotName),
            true
        );
        
        return Command.SINGLE_SUCCESS;
    } // executeOwnerSetProtection()

    /**
     * Executes owner teleport command to teleport PlayerEntity to robot.
     */
    private static int executeOwnerTeleport(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "index");
        
        InternalEntity robot = getRobotByIndex(ctx, player, index);
        
        // Teleport PlayerEntity to robot's position
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        serverPlayer.teleport(
            (ServerWorld) robot.getWorld(),
            robot.getX(),
            robot.getY(),
            robot.getZ(),
            robot.getYaw(),
            robot.getPitch()
        );
        
        String robotName = robot.hasCustomName() ? robot.getCustomName().getString() : "Robot";
        ctx.getSource().sendFeedback(
            () -> Text.literal("Teleported to " + robotName),
            false
        );
        
        return Command.SINGLE_SUCCESS;
    } // executeOwnerTeleport()

    /**
     * Executes owner recall command to teleport robot to PlayerEntity.
     */
    private static int executeOwnerRecall(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "index");
        
        InternalEntity robot = getRobotByIndex(ctx, player, index);
        
        // Teleport robot to PlayerEntity's position
        robot.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
        
        String robotName = robot.hasCustomName() ? robot.getCustomName().getString() : "Robot";
        ctx.getSource().sendFeedback(
            () -> Text.literal("Recalled " + robotName + " to " + player.getName().getString()),
            false
        );
        
        return Command.SINGLE_SUCCESS;
    } // executeOwnerRecall()

    /**
     * Executes owner heal all command to heal all robots for a PlayerEntity.
     */
    private static int executeOwnerHealAll(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        ServerWorld level = (ServerWorld) player.getWorld();
        OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(level);
        
        List<RobotRegistryEntry> robots = registry.getRobotsForOwner(player.getUuid());
        int healedCount = 0;
        
        for (RobotRegistryEntry entry : robots) {
            Object entityObj = entry.getEntity();
            if (entityObj instanceof InternalEntity robot && entry.isEntityValid()) {
                robot.setHealth(robot.getMaxHealth());
                healedCount++;
            }
        }
        
        final int finalCount = healedCount;
        ctx.getSource().sendFeedback(
            () -> Text.literal("Healed " + finalCount + " robot(s) for " + player.getName().getString()),
            true
        );
        
        return healedCount;
    } // executeOwnerHealAll()

    /**
     * Executes owner stats command to display comprehensive robot stats.
     */
    private static int executeOwnerStats(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "index");

        LovelyRobotEntity robot = (LovelyRobotEntity)getRobotByIndex(ctx, player, index);
        
        String name = robot.hasCustomName() ? robot.getCustomName().getString() : "Unnamed";
        BlockPos pos = robot.getBlockPos();
        String dimension = robot.getWorld().getDimensionKey().getValue().toString();
        
        ctx.getSource().sendFeedback(
            () -> Text.literal("=== Stats for " + name + " ===").formatted(Formatting.GOLD),
            false
        );
        
        ctx.getSource().sendFeedback(
            () -> Text.literal("Level: " + robot.getLevel() + " | XP: " + robot.getExp()).formatted(Formatting.WHITE),
            false
        );
        
        ctx.getSource().sendFeedback(
            () -> Text.literal(String.format("Health: %.1f/%.1f", robot.getHealth(), robot.getMaxHealth())).formatted(Formatting.WHITE),
            false
        );
        
        ctx.getSource().sendFeedback(
            () -> Text.literal("Position: " + dimension + " @ (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")").formatted(Formatting.WHITE),
            false
        );
        
        ctx.getSource().sendFeedback(
            () -> Text.literal(String.format("Protections: Fire=%d Fall=%d Blast=%d Projectile=%d",
                robot.getFireProtection(), robot.getFallProtection(), robot.getBlastProtection(), robot.getProjectileProtection()))
                .formatted(Formatting.WHITE),
            false
        );
        
        return Command.SINGLE_SUCCESS;
    } // executeOwnerStats()

    /**
     * Executes owner transfer command to transfer robot ownership.
     */
    private static int executeOwnerTransfer(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity fromPlayer = EntityArgumentType.getPlayer(ctx, "from_player");
        int index = IntegerArgumentType.getInteger(ctx, "index");
        PlayerEntity toPlayer = EntityArgumentType.getPlayer(ctx, "to_player");
        
        InternalEntity robot = getRobotByIndex(ctx, fromPlayer, index);
        
        // Unregister from old owner
        ServerWorld level = (ServerWorld) fromPlayer.getWorld();
        OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(level);
        registry.unregisterRobot(robot.getUuid());
        
        // Set new owner (registration happens automatically in setTame)
        robot.setOwner(toPlayer);
        InternalParticle.HappyVillager(robot);
        
        String robotName = robot.hasCustomName() ? robot.getCustomName().getString() : "Robot";
        ctx.getSource().sendFeedback(
            () -> Text.literal("Transferred " + robotName + " from " + fromPlayer.getName().getString() + " to " + toPlayer.getName().getString()),
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
    private static InternalEntity getRobotByIndex(CommandContext<ServerCommandSource> ctx, PlayerEntity player, int index) throws CommandSyntaxException {
        ServerWorld level = (ServerWorld) player.getWorld();
        OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(level);
        
        List<RobotRegistryEntry> robots = registry.getRobotsForOwner(player.getUuid());
        
        if (robots.isEmpty()) {
            throw new SimpleCommandExceptionType(
                Text.literal("PlayerEntity " + player.getName().getString() + " has no registered robots")
            ).create();
        }
        
        if (index < 0 || index >= robots.size()) {
            throw new SimpleCommandExceptionType(
                Text.literal("Invalid robot index " + index + " (PlayerEntity has " + robots.size() + " robot(s))")
            ).create();
        }
        
        RobotRegistryEntry entry = robots.get(index);
        Object entityObj = entry.getEntity();
        
        if (!(entityObj instanceof InternalEntity)) {
            throw new SimpleCommandExceptionType(
                Text.literal("Robot is offline or unloaded")
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
     * 3. Raycast (no arguments, uses PlayerEntity look vector)
     * 
     * @param ctx command context
     * @return collection of target robots
     * @throws CommandSyntaxException if no valid targets found
     */
    private static Collection<? extends Entity> getTargetRobots(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        // Try entity selector first
        try {
            return EntityArgumentType.getEntities(ctx, "targets");
        } catch (IllegalArgumentException e) {
            // Argument not present, try next method
        }
        
        // Try owner + index
        try {
            PlayerEntity owner = EntityArgumentType.getPlayer(ctx, "owner");
            int robotIndex = IntegerArgumentType.getInteger(ctx, "robot_index");
            InternalEntity robot = getRobotByIndex(ctx, owner, robotIndex);
            return List.of(robot);
        } catch (IllegalArgumentException e) {
            // Arguments not present, try next method
        }
        
        // Try raycast
        PlayerEntity player = ctx.getSource().getPlayerOrThrow();
        InternalEntity robot = findRobotInFront(player);
        
        if (robot == null) {
            throw new SimpleCommandExceptionType(
                Text.literal("No robot found. Use entity selector, owner+index, or look at a robot.")
            ).create();
        }
        
        return List.of(robot);
    } // getTargetRobots()

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
    private static InternalEntity findRobotInFront(PlayerEntity player) {
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
    } // findRobotInFront()

} // Class: LovelyRobotCommands

