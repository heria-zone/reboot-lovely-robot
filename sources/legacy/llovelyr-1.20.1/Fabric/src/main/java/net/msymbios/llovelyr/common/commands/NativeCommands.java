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
import net.msymbios.llovelyr.framework.registry.OwnerRobotRegistry;
import net.msymbios.llovelyr.framework.registry.RobotRegistryEntry;
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
                        .then(buildRobotCombatCommands())
                        .then(buildTargetCombatCommands())
                        .then(buildListCommands())
        );
    } // register ()

    // -- Internal Methods --

    private static ArgumentBuilder<ServerCommandSource, ?> buildRobotCombatCommands() {
        return CommandManager.literal("robot")
                .then(CommandManager.literal("add")
                        .then(CommandManager.literal("combat")
                                .then(CommandManager.literal("exp")
                                        .then(CommandManager.argument("exp_value", IntegerArgumentType.integer(0))
                                                .suggests(NativeCommands::suggestMaxExp)
                                                .executes(NativeCommands::executeCrosshairAddXP)
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("set")
                        .then(CommandManager.literal("combat")
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
                                )
                        )
                );
    } // buildRobotCombatCommands()

    private static ArgumentBuilder<ServerCommandSource, ?> buildTargetCombatCommands() {
        return CommandManager.literal("target")
                .then(CommandManager.argument("targets", EntityArgumentType.entities())
                        .then(CommandManager.literal("add")
                                .then(CommandManager.literal("combat")
                                        .then(CommandManager.literal("exp")
                                                .then(CommandManager.argument("exp_value", IntegerArgumentType.integer(0))
                                                        .suggests(NativeCommands::suggestMinMaxExp)
                                                        .executes(NativeCommands::executeTargetAddXP)
                                                )
                                        )
                                )
                        )
                        .then(CommandManager.literal("set")
                                .then(CommandManager.literal("combat")
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
                                        )
                                )
                        )
                );
    } // buildTargetCombatCommands()

    private static ArgumentBuilder<ServerCommandSource, ?> buildListCommands() {
        return CommandManager.literal("list")
                .then(CommandManager.literal("owner")
                        .executes(NativeCommands::executeListOwners)
                )
                .then(CommandManager.literal("robot")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(NativeCommands::executeListPlayerRobots)
                        )
                );
    } // buildListCommands()



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