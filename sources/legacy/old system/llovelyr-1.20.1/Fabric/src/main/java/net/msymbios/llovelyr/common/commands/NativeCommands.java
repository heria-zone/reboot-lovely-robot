package net.msymbios.llovelyr.common.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
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

public abstract class NativeCommands {

    // -- Core Abstraction Layer --

    /**
     * Defines robot selection strategy for command execution.
     * <p>
     * <b>Design Pattern:</b> Strategy - encapsulates different robot selection
     * methods (crosshair, target list, owner registry) behind unified interface.
     */
    @FunctionalInterface
    private interface RobotSelector {
        /**
         * Selects robots for command execution.
         * 
         * @param ctx command context
         * @return list of robots to operate on
         * @throws CommandSyntaxException if selection fails
         */
        List<LovelyRobotEntity> selectRobots(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException;
    } // Interface: RobotSelector

    /**
     * Encapsulates robot operation with validation and result handling.
     * <p>
     * <b>Design Pattern:</b> Command - encapsulates operation as object,
     * enabling parameterization and validation.
     */
    @FunctionalInterface
    private interface RobotOperation {
        /**
         * Executes operation on robot with validation.
         * 
         * @param robot target robot
         * @param ctx command context for parameter extraction
         * @return operation result
         * @throws CommandSyntaxException if command execution fails
         */
        CommandResult execute(LovelyRobotEntity robot, CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException;
    } // Interface: RobotOperation

    // -- Robot Selectors --

    /**
     * Selects robot in player's crosshair.
     */
    private static final RobotSelector CROSSHAIR_SELECTOR = ctx -> {
        PlayerEntity player = ctx.getSource().getPlayerOrThrow();
        LovelyRobotEntity robot = (LovelyRobotEntity) findEntityInFront(player);
        return robot != null ? List.of(robot) : List.of();
    }; // CROSSHAIR_SELECTOR

    /**
     * Selects robots from target argument.
     */
    private static final RobotSelector TARGET_SELECTOR = ctx -> {
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
        return entities.stream()
                .filter(e -> e instanceof LovelyRobotEntity)
                .map(e -> (LovelyRobotEntity) e)
                .toList();
    }; // TARGET_SELECTOR

    /**
     * Selects robot by owner and index from registry.
     */
    private static final RobotSelector OWNER_SELECTOR = ctx -> {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "robot_index");
        LovelyRobotEntity robot = getOwnerRobotByIndex(player, index);
        return robot != null ? List.of(robot) : List.of();
    }; // OWNER_SELECTOR

    // -- Unified Command Executor --

    /**
     * Unified command executor handling robot selection, validation, and feedback.
     * <p>
     * <b>Architecture:</b> Eliminates duplication by centralizing execution flow
     * across crosshair, target, and owner command variants.
     */
    protected static int executeRobotCommand(
            CommandContext<ServerCommandSource> ctx,
            RobotSelector selector,
            RobotOperation operation,
            boolean requireOwnership
    ) throws CommandSyntaxException {
        List<LovelyRobotEntity> robots = selector.selectRobots(ctx);
        
        if (robots.isEmpty()) {
            ctx.getSource().sendError(Text.literal("No robots found"));
            return 0;
        }
        
        int successCount = 0;
        int failureCount = 0;
        List<String> errorMessages = new java.util.ArrayList<>();
        
        for (LovelyRobotEntity robot : robots) {
            // Ownership validation
            if (requireOwnership) {
                PlayerEntity player = ctx.getSource().getPlayerOrThrow();
                if (!robot.isOwner(player)) {
                    failureCount++;
                    errorMessages.add("Not owner of " + Utility.getEntityCustomName(robot));
                    continue;
                }
            }
            
            // Execute operation
            CommandResult result = operation.execute(robot, ctx);
            
            if (result.success) {
                successCount++;
                // For single robot operations, send individual feedback
                if (robots.size() == 1) {
                    ctx.getSource().sendFeedback(() -> Text.literal(result.message), true);
                }
            } else {
                failureCount++;
                errorMessages.add(result.message);
            }
        }
        
        // Send batch feedback for multiple robots
        if (robots.size() > 1) {
            int finalSuccess = successCount;
            int finalFailure = failureCount;
            ctx.getSource().sendFeedback(() -> 
                Text.literal("Operation completed: " + finalSuccess + " succeeded" + 
                    (finalFailure > 0 ? ", " + finalFailure + " failed" : "")), 
                true
            );
        }
        
        // Send error details if any
        if (!errorMessages.isEmpty() && errorMessages.size() <= 3) {
            for (String error : errorMessages) {
                ctx.getSource().sendError(Text.literal(error));
            }
        }
        
        return successCount;
    } // executeRobotCommand()

    // -- Operation Factories --

    /**
     * Combat operations factory.
     * <p>
     * Provides reusable operations for XP, level, and combined combat management.
     */
    private static class CombatOperations {
        
        static RobotOperation addXP() {
            return (robot, ctx) -> {
                int xp = IntegerArgumentType.getInteger(ctx, "exp_value");
                robot.addExp(xp);
                return new CommandResult(true, "Added " + xp + "xp to " + Utility.getEntityCustomName(robot));
            };
        } // addXP()
        
        static RobotOperation setXP() {
            return (robot, ctx) -> {
                int xp = IntegerArgumentType.getInteger(ctx, "exp_value");
                int maxXp = robot.getLevelSystem()
                        .map(feature -> feature.getExpForLevel(robot.getCurrentLevel()))
                        .orElse(Integer.MAX_VALUE);
                
                if (xp > maxXp) {
                    return new CommandResult(false, "XP " + xp + " exceeds max " + maxXp + " for " + Utility.getEntityCustomName(robot));
                }
                
                robot.setExp(xp);
                return new CommandResult(true, "Set " + xp + "xp to " + Utility.getEntityCustomName(robot));
            };
        } // setXP()
        
        static RobotOperation setLevel() {
            return (robot, ctx) -> {
                int level = IntegerArgumentType.getInteger(ctx, "level_value");
                int maxLevel = robot.getLevelSystem()
                        .map(LevelFeature::getMaxLevel)
                        .orElse(200);
                
                if (level > maxLevel) {
                    return new CommandResult(false, "Level " + level + " exceeds max " + maxLevel + " for " + Utility.getEntityCustomName(robot));
                }
                
                robot.setCurrentLevel(level);
                return new CommandResult(true, "Set level " + level + " to " + Utility.getEntityCustomName(robot));
            };
        } // setLevel()
        
        static RobotOperation setAllCombat() {
            return (robot, ctx) -> {
                int level = IntegerArgumentType.getInteger(ctx, "level");
                int exp = IntegerArgumentType.getInteger(ctx, "exp");
                
                // Validate level
                int maxLevel = robot.getLevelSystem()
                        .map(LevelFeature::getMaxLevel)
                        .orElse(200);
                if (level > maxLevel) {
                    return new CommandResult(false, "Level " + level + " exceeds max " + maxLevel + " for " + Utility.getEntityCustomName(robot));
                }
                
                // Validate exp
                int maxExp = robot.getLevelSystem()
                        .map(feature -> feature.getExpForLevel(level))
                        .orElse(Integer.MAX_VALUE);
                if (exp > maxExp) {
                    return new CommandResult(false, "XP " + exp + " exceeds max " + maxExp + " for level " + level + " on " + Utility.getEntityCustomName(robot));
                }
                
                robot.setCurrentLevel(level);
                robot.setExp(exp);
                return new CommandResult(true, "Set " + Utility.getEntityCustomName(robot) + " to level " + level + " with " + exp + "xp");
            };
        } // setAllCombat()
        
    } // Class: CombatOperations

    /**
     * Attribute operations factory.
     * <p>
     * Provides reusable operations for HP, attack, defense, speed, and combined attributes.
     */
    private static class AttributeOperations {
        
        static RobotOperation setHP() {
            return (robot, ctx) -> {
                int hp = IntegerArgumentType.getInteger(ctx, "value");
                robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(hp);
                robot.setHealth(hp);
                return new CommandResult(true, "Set HP to " + hp + " for " + Utility.getEntityCustomName(robot));
            };
        } // setHP()
        
        static RobotOperation setAttack() {
            return (robot, ctx) -> {
                int attack = IntegerArgumentType.getInteger(ctx, "value");
                robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(attack);
                return new CommandResult(true, "Set attack to " + attack + " for " + Utility.getEntityCustomName(robot));
            };
        } // setAttack()
        
        static RobotOperation setDefense() {
            return (robot, ctx) -> {
                int defense = IntegerArgumentType.getInteger(ctx, "value");
                robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ARMOR).setBaseValue(defense);
                return new CommandResult(true, "Set defense to " + defense + " for " + Utility.getEntityCustomName(robot));
            };
        } // setDefense()
        
        static RobotOperation setSpeed() {
            return (robot, ctx) -> {
                int speed = IntegerArgumentType.getInteger(ctx, "value");
                robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(speed / 10.0);
                return new CommandResult(true, "Set speed to " + speed + " for " + Utility.getEntityCustomName(robot));
            };
        } // setSpeed()
        
        static RobotOperation setAllAttributes() {
            return (robot, ctx) -> {
                int hp = IntegerArgumentType.getInteger(ctx, "hp");
                int attack = IntegerArgumentType.getInteger(ctx, "attack");
                int defense = IntegerArgumentType.getInteger(ctx, "defense");
                int speed = IntegerArgumentType.getInteger(ctx, "speed");
                
                robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(hp);
                robot.setHealth(hp);
                robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(attack);
                robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ARMOR).setBaseValue(defense);
                robot.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(speed / 10.0);
                
                String name = Utility.getEntityCustomName(robot);
                return new CommandResult(true, "Set attributes (HP:" + hp + " ATK:" + attack + " DEF:" + defense + " SPD:" + speed + ") for " + name);
            };
        } // setAllAttributes()
        
    } // Class: AttributeOperations

    /**
     * Protection operations factory.
     * <p>
     * Provides reusable operations for fire, fall, blast, projectile, and combined protections.
     */
    private static class ProtectionOperations {
        
        static RobotOperation setFireProtection() {
            return (robot, ctx) -> {
                int level = IntegerArgumentType.getInteger(ctx, "level");
                robot.setFireProtection(level);
                return new CommandResult(true, "Set fire protection to " + level + " for " + Utility.getEntityCustomName(robot));
            };
        } // setFireProtection()
        
        static RobotOperation setFallProtection() {
            return (robot, ctx) -> {
                int level = IntegerArgumentType.getInteger(ctx, "level");
                robot.setFallProtection(level);
                return new CommandResult(true, "Set fall protection to " + level + " for " + Utility.getEntityCustomName(robot));
            };
        } // setFallProtection()
        
        static RobotOperation setBlastProtection() {
            return (robot, ctx) -> {
                int level = IntegerArgumentType.getInteger(ctx, "level");
                robot.setBlastProtection(level);
                return new CommandResult(true, "Set blast protection to " + level + " for " + Utility.getEntityCustomName(robot));
            };
        } // setBlastProtection()
        
        static RobotOperation setProjectileProtection() {
            return (robot, ctx) -> {
                int level = IntegerArgumentType.getInteger(ctx, "level");
                robot.setProjectileProtection(level);
                return new CommandResult(true, "Set projectile protection to " + level + " for " + Utility.getEntityCustomName(robot));
            };
        } // setProjectileProtection()
        
        static RobotOperation setAllProtections() {
            return (robot, ctx) -> {
                int fire = IntegerArgumentType.getInteger(ctx, "fire");
                int fall = IntegerArgumentType.getInteger(ctx, "fall");
                int blast = IntegerArgumentType.getInteger(ctx, "blast");
                int projectile = IntegerArgumentType.getInteger(ctx, "projectile");
                
                robot.setFireProtection(fire);
                robot.setFallProtection(fall);
                robot.setBlastProtection(blast);
                robot.setProjectileProtection(projectile);
                
                String name = Utility.getEntityCustomName(robot);
                return new CommandResult(true, "Set protections (Fire:" + fire + " Fall:" + fall + " Blast:" + blast + " Projectile:" + projectile + ") for " + name);
            };
        } // setAllProtections()
        
    } // Class: ProtectionOperations

    /**
     * Utility operations factory.
     * <p>
     * Provides reusable operations for heal, recall, appearance, and identifier.
     */
    private static class UtilityOperations {
        
        static RobotOperation heal() {
            return (robot, ctx) -> {
                robot.setHealth(robot.getMaxHealth());
                return new CommandResult(true, "Healed " + Utility.getEntityCustomName(robot));
            };
        } // heal()
        
        static RobotOperation recall() {
            return (robot, ctx) -> {
                PlayerEntity player = ctx.getSource().getPlayerOrThrow();
                robot.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
                return new CommandResult(true, "Recalled " + Utility.getEntityCustomName(robot));
            };
        } // recall()
        
        static RobotOperation setAppearance() {
            return (robot, ctx) -> {
                net.msymbios.llovelyr.framework.entity.enums.EntityTexture color = ctx.getArgument("color", net.msymbios.llovelyr.framework.entity.enums.EntityTexture.class);
                robot.setTexture(color);
                String colorName = color.Name().toLowerCase();
                return new CommandResult(true, "Set color to " + colorName + " for " + Utility.getEntityCustomName(robot));
            };
        } // setAppearance()
        
        static RobotOperation setIdentifier() {
            return (robot, ctx) -> {
                String nameString = com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "name");
                Text name = Text.literal(nameString);
                robot.setCustomName(name);
                robot.setCustomNameVisible(true);
                return new CommandResult(true, "Set name to '" + nameString + "' for " + Utility.getEntityCustomName(robot));
            };
        } // setIdentifier()
        
    } // Class: UtilityOperations

    // -- Commands Suggestions --

    /**
     * Suggests max level for targeted robot.
     * <p>
     * Displays robot's max level as suggestion, providing context-aware
     * command completion.
     */
    protected static CompletableFuture<Suggestions> suggestMaxLevel(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
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
    protected static CompletableFuture<Suggestions> suggestMaxExp(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
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
    protected static CompletableFuture<Suggestions> suggestMaxExpForLevel(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
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
    protected static CompletableFuture<Suggestions> suggestMinMaxLevel(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
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
    protected static CompletableFuture<Suggestions> suggestMinMaxExp(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
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
    protected static CompletableFuture<Suggestions> suggestMinMaxExpForLevel(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
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
    protected static CompletableFuture<Suggestions> suggestPlayerNames(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
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
    protected static CompletableFuture<Suggestions> suggestRobotIndices(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
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
    protected static CompletableFuture<Suggestions> suggestOwnerMaxLevel(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
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
    protected static CompletableFuture<Suggestions> suggestOwnerMaxExp(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
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
    protected static CompletableFuture<Suggestions> suggestOwnerMaxExpForLevel(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
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
    protected static CompletableFuture<Suggestions> suggestMaxFireProtection(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
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
    protected static CompletableFuture<Suggestions> suggestMaxFallProtection(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
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
    protected static CompletableFuture<Suggestions> suggestMaxBlastProtection(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
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
    protected static CompletableFuture<Suggestions> suggestMaxProjectileProtection(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
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
     * Suggests minimum max protection level among all targeted robots.
     */
    protected static CompletableFuture<Suggestions> suggestMinMaxProtection(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder, String protectionType) {
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
    protected static CompletableFuture<Suggestions> suggestOwnerMaxProtection(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder, String protectionType) {
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

    // -- Crosshair Commands --

    /**
     * Adds XP to crosshair robot with ownership validation.
     * <p>
     * <b>Selection:</b> Raycast-based targeting within 5 blocks.
     */
    protected static int executeCrosshairAddXP(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, CROSSHAIR_SELECTOR, CombatOperations.addXP(), true);
    } // executeCrosshairAddXP()

    /**
     * Sets exact XP for crosshair robot with validation against level cap.
     * <p>
     * <b>Selection:</b> Raycast-based targeting within 5 blocks.
     */
    protected static int executeCrosshairSetXP(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, CROSSHAIR_SELECTOR, CombatOperations.setXP(), true);
    } // executeCrosshairSetXP()

    /**
     * Sets level for crosshair robot with validation against max level.
     * <p>
     * <b>Selection:</b> Raycast-based targeting within 5 blocks.
     */
    protected static int executeCrosshairSetLevel(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, CROSSHAIR_SELECTOR, CombatOperations.setLevel(), true);
    } // executeCrosshairSetLevel()

    /**
     * Sets both level and XP for crosshair robot atomically.
     * <p>
     * <b>Selection:</b> Raycast-based targeting within 5 blocks.
     */
    protected static int executeSetAllCombat(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, CROSSHAIR_SELECTOR, CombatOperations.setAllCombat(), true);
    } // executeSetAllCombat()

    /**
     * Sets HP attribute for crosshair robot, healing to full.
     * <p>
     * <b>Selection:</b> Raycast-based targeting within 5 blocks.
     */
    protected static int executeCrosshairSetHP(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, CROSSHAIR_SELECTOR, AttributeOperations.setHP(), true);
    } // executeCrosshairSetHP()

    /**
     * Sets attack damage attribute for crosshair robot.
     * <p>
     * <b>Selection:</b> Raycast-based targeting within 5 blocks.
     */
    protected static int executeCrosshairSetAttack(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, CROSSHAIR_SELECTOR, AttributeOperations.setAttack(), true);
    } // executeCrosshairSetAttack()

    /**
     * Sets armor defense attribute for crosshair robot.
     * <p>
     * <b>Selection:</b> Raycast-based targeting within 5 blocks.
     */
    protected static int executeCrosshairSetDefense(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, CROSSHAIR_SELECTOR, AttributeOperations.setDefense(), true);
    } // executeCrosshairSetDefense()

    /**
     * Sets movement speed attribute for crosshair robot.
     * <p>
     * <b>Selection:</b> Raycast-based targeting within 5 blocks.
     * <i>Note:</i> Value divided by 10 for Minecraft's speed scale.
     */
    protected static int executeCrosshairSetSpeed(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, CROSSHAIR_SELECTOR, AttributeOperations.setSpeed(), true);
    } // executeCrosshairSetSpeed()

    /**
     * Sets all attributes atomically for crosshair robot.
     * <p>
     * <b>Selection:</b> Raycast-based targeting within 5 blocks.
     */
    protected static int executeCrosshairSetAllAttributes(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, CROSSHAIR_SELECTOR, AttributeOperations.setAllAttributes(), true);
    } // executeCrosshairSetAllAttributes()

    /**
     * Sets fire protection level for crosshair robot.
     * <p>
     * <b>Selection:</b> Raycast-based targeting within 5 blocks.
     */
    protected static int executeCrosshairSetFireProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, CROSSHAIR_SELECTOR, ProtectionOperations.setFireProtection(), true);
    } // executeCrosshairSetFireProtection()

    /**
     * Sets fall protection level for crosshair robot.
     * <p>
     * <b>Selection:</b> Raycast-based targeting within 5 blocks.
     */
    protected static int executeCrosshairSetFallProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, CROSSHAIR_SELECTOR, ProtectionOperations.setFallProtection(), true);
    } // executeCrosshairSetFallProtection()

    /**
     * Sets blast protection level for crosshair robot.
     * <p>
     * <b>Selection:</b> Raycast-based targeting within 5 blocks.
     */
    protected static int executeCrosshairSetBlastProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, CROSSHAIR_SELECTOR, ProtectionOperations.setBlastProtection(), true);
    } // executeCrosshairSetBlastProtection()

    /**
     * Sets projectile protection level for crosshair robot.
     * <p>
     * <b>Selection:</b> Raycast-based targeting within 5 blocks.
     */
    protected static int executeCrosshairSetProjectileProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, CROSSHAIR_SELECTOR, ProtectionOperations.setProjectileProtection(), true);
    } // executeCrosshairSetProjectileProtection()

    /**
     * Sets all protection levels atomically for crosshair robot.
     * <p>
     * <b>Selection:</b> Raycast-based targeting within 5 blocks.
     */
    protected static int executeCrosshairSetAllProtections(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, CROSSHAIR_SELECTOR, ProtectionOperations.setAllProtections(), true);
    } // executeCrosshairSetAllProtections()

    /**
     * Sets texture/color appearance for crosshair robot.
     * <p>
     * <b>Selection:</b> Raycast-based targeting within 5 blocks.
     */
    protected static int executeCrosshairSetAppearance(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, CROSSHAIR_SELECTOR, UtilityOperations.setAppearance(), true);
    } // executeCrosshairSetAppearance()

    /**
     * Sets custom name for crosshair robot with visibility enabled.
     * <p>
     * <b>Selection:</b> Raycast-based targeting within 5 blocks.
     */
    protected static int executeCrosshairSetIdentifier(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, CROSSHAIR_SELECTOR, UtilityOperations.setIdentifier(), true);
    } // executeCrosshairSetIdentifier()

    /**
     * Retrieves owner information for crosshair robot.
     * <p>
     * <b>Selection:</b> Raycast-based targeting within 5 blocks.
     */
    protected static int executeCrosshairGetOwner(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, CROSSHAIR_SELECTOR, (robot, c) -> {
            PlayerEntity owner = (PlayerEntity) robot.getOwner();
            String ownerName = owner != null ? owner.getName().getString() : "No owner";
            String robotName = Utility.getEntityCustomName(robot);
            return new CommandResult(true, robotName + " owner: " + ownerName);
        }, true);
    } // executeCrosshairGetOwner()

    /**
     * Displays comprehensive stats for crosshair robot.
     * <p>
     * <b>Selection:</b> Raycast-based targeting within 5 blocks.
     */
    protected static int executeCrosshairStats(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
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
     * Heals crosshair robot to full health.
     * <p>
     * <b>Selection:</b> Raycast-based targeting within 5 blocks.
     */
    protected static int executeCrosshairHeal(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, CROSSHAIR_SELECTOR, UtilityOperations.heal(), true);
    } // executeCrosshairHeal()

    /**
     * Teleports crosshair robot to command source location.
     * <p>
     * <b>Selection:</b> Raycast-based targeting within 5 blocks.
     */
    protected static int executeCrosshairRecall(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, CROSSHAIR_SELECTOR, UtilityOperations.recall(), true);
    } // executeCrosshairRecall()

    // -- Target Commands --

    /**
     * Adds XP to multiple targeted robots without ownership validation.
     * <p>
     * <b>Selection:</b> Entity selector argument supports multiple robots.
     */
    protected static int executeTargetAddXP(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, TARGET_SELECTOR, CombatOperations.addXP(), false);
    } // executeTargetAddXP()

    /**
     * Sets exact XP for multiple targeted robots with per-robot validation.
     * <p>
     * <b>Selection:</b> Entity selector argument supports multiple robots.
     */
    protected static int executeTargetSetXP(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, TARGET_SELECTOR, CombatOperations.setXP(), false);
    } // executeTargetSetXP()

    /**
     * Sets level for multiple targeted robots with per-robot validation.
     * <p>
     * <b>Selection:</b> Entity selector argument supports multiple robots.
     */
    protected static int executeTargetSetLevel(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, TARGET_SELECTOR, CombatOperations.setLevel(), false);
    } // executeTargetSetLevel()

    /**
     * Sets both level and XP atomically for multiple targeted robots.
     * <p>
     * <b>Selection:</b> Entity selector argument supports multiple robots.
     */
    protected static int executeTargetSetAllCombat(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, TARGET_SELECTOR, CombatOperations.setAllCombat(), false);
    } // executeTargetSetAllCombat()

    /**
     * Sets HP attribute for multiple targeted robots.
     * <p>
     * <b>Selection:</b> Entity selector argument supports multiple robots.
     */
    protected static int executeTargetSetHP(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, TARGET_SELECTOR, AttributeOperations.setHP(), false);
    } // executeTargetSetHP()

    /**
     * Sets attack damage attribute for multiple targeted robots.
     * <p>
     * <b>Selection:</b> Entity selector argument supports multiple robots.
     */
    protected static int executeTargetSetAttack(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, TARGET_SELECTOR, AttributeOperations.setAttack(), false);
    } // executeTargetSetAttack()

    /**
     * Sets armor defense attribute for multiple targeted robots.
     * <p>
     * <b>Selection:</b> Entity selector argument supports multiple robots.
     */
    protected static int executeTargetSetDefense(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, TARGET_SELECTOR, AttributeOperations.setDefense(), false);
    } // executeTargetSetDefense()

    /**
     * Sets movement speed attribute for multiple targeted robots.
     * <p>
     * <b>Selection:</b> Entity selector argument supports multiple robots.
     */
    protected static int executeTargetSetSpeed(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, TARGET_SELECTOR, AttributeOperations.setSpeed(), false);
    } // executeTargetSetSpeed()

    /**
     * Sets all attributes atomically for multiple targeted robots.
     * <p>
     * <b>Selection:</b> Entity selector argument supports multiple robots.
     */
    protected static int executeTargetSetAllAttributes(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, TARGET_SELECTOR, AttributeOperations.setAllAttributes(), false);
    } // executeTargetSetAllAttributes()

    /**
     * Sets fire protection level for multiple targeted robots.
     * <p>
     * <b>Selection:</b> Entity selector argument supports multiple robots.
     */
    protected static int executeTargetSetFireProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, TARGET_SELECTOR, ProtectionOperations.setFireProtection(), false);
    } // executeTargetSetFireProtection()

    /**
     * Sets fall protection level for multiple targeted robots.
     * <p>
     * <b>Selection:</b> Entity selector argument supports multiple robots.
     */
    protected static int executeTargetSetFallProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, TARGET_SELECTOR, ProtectionOperations.setFallProtection(), false);
    } // executeTargetSetFallProtection()

    /**
     * Sets blast protection level for multiple targeted robots.
     * <p>
     * <b>Selection:</b> Entity selector argument supports multiple robots.
     */
    protected static int executeTargetSetBlastProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, TARGET_SELECTOR, ProtectionOperations.setBlastProtection(), false);
    } // executeTargetSetBlastProtection()

    /**
     * Sets projectile protection level for multiple targeted robots.
     * <p>
     * <b>Selection:</b> Entity selector argument supports multiple robots.
     */
    protected static int executeTargetSetProjectileProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, TARGET_SELECTOR, ProtectionOperations.setProjectileProtection(), false);
    } // executeTargetSetProjectileProtection()

    /**
     * Sets all protection levels atomically for multiple targeted robots.
     * <p>
     * <b>Selection:</b> Entity selector argument supports multiple robots.
     */
    protected static int executeTargetSetAllProtections(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, TARGET_SELECTOR, ProtectionOperations.setAllProtections(), false);
    } // executeTargetSetAllProtections()

    /**
     * Sets texture/color appearance for multiple targeted robots.
     * <p>
     * <b>Selection:</b> Entity selector argument supports multiple robots.
     */
    protected static int executeTargetSetAppearance(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, TARGET_SELECTOR, UtilityOperations.setAppearance(), false);
    } // executeTargetSetAppearance()

    /**
     * Sets custom name for multiple targeted robots with visibility enabled.
     * <p>
     * <b>Selection:</b> Entity selector argument supports multiple robots.
     */
    protected static int executeTargetSetIdentifier(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, TARGET_SELECTOR, UtilityOperations.setIdentifier(), false);
    } // executeTargetSetIdentifier()

    /**
     * Heals all targeted robots to full health.
     * <p>
     * <b>Selection:</b> Entity selector argument supports multiple robots.
     */
    protected static int executeTargetHeal(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, TARGET_SELECTOR, UtilityOperations.heal(), false);
    } // executeTargetHeal()

    /**
     * Teleports command source to first targeted robot.
     * <p>
     * <b>Selection:</b> Only first robot in selector is used.
     */
    protected static int executeTargetTeleport(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgumentType.getEntities(ctx, "targets");
        
        for (Entity entity : entities) {
            if (entity instanceof LovelyRobotEntity robot) {
                ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
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
     * Teleports all targeted robots to command source location.
     * <p>
     * <b>Selection:</b> Entity selector argument supports multiple robots.
     */
    protected static int executeTargetRecall(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, TARGET_SELECTOR, UtilityOperations.recall(), false);
    } // executeTargetRecall()

    /**
     * Transfers ownership of all targeted robots to specified player.
     * <p>
     * <b>Selection:</b> Entity selector argument supports multiple robots.
     * <i>Note:</i> Unregisters from old owner and re-registers automatically.
     */
    protected static int executeTargetTransfer(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, TARGET_SELECTOR, (robot, c) -> {
            PlayerEntity toPlayer = EntityArgumentType.getPlayer(c, "to_player");
            
            // Unregister from old owner
            ServerWorld world = (ServerWorld) robot.getWorld();
            OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(world);
            registry.unregisterRobot(robot.getUuid());

            // Set new owner (registration happens automatically)
            robot.setOwner(toPlayer);
            
            return new CommandResult(true, "Transferred " + Utility.getEntityCustomName(robot) + " to " + toPlayer.getName().getString());
        }, false);
    } // executeTargetTransfer()

    // -- Owner Commands --

    /**
     * Adds XP to robot selected by owner and index from registry.
     * <p>
     * <b>Selection:</b> Registry lookup by player UUID and robot index.
     */
    protected static int executeOwnerAddExp(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, OWNER_SELECTOR, CombatOperations.addXP(), false);
    } // executeOwnerAddExp()

    /**
     * Sets exact XP for robot selected by owner and index with validation.
     * <p>
     * <b>Selection:</b> Registry lookup by player UUID and robot index.
     */
    protected static int executeOwnerSetExp(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, OWNER_SELECTOR, CombatOperations.setXP(), false);
    } // executeOwnerSetExp()

    /**
     * Sets level for robot selected by owner and index with validation.
     * <p>
     * <b>Selection:</b> Registry lookup by player UUID and robot index.
     */
    protected static int executeOwnerSetLevel(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, OWNER_SELECTOR, CombatOperations.setLevel(), false);
    } // executeOwnerSetLevel()

    /**
     * Sets both level and XP atomically for robot selected by owner and index.
     * <p>
     * <b>Selection:</b> Registry lookup by player UUID and robot index.
     */
    protected static int executeOwnerSetAllCombat(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, OWNER_SELECTOR, CombatOperations.setAllCombat(), false);
    } // executeOwnerSetAllCombat()

    /**
     * Sets HP attribute for robot selected by owner and index.
     * <p>
     * <b>Selection:</b> Registry lookup by player UUID and robot index.
     */
    protected static int executeOwnerSetHP(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, OWNER_SELECTOR, AttributeOperations.setHP(), false);
    } // executeOwnerSetHP()

    /**
     * Sets attack damage attribute for robot selected by owner and index.
     * <p>
     * <b>Selection:</b> Registry lookup by player UUID and robot index.
     */
    protected static int executeOwnerSetAttack(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, OWNER_SELECTOR, AttributeOperations.setAttack(), false);
    } // executeOwnerSetAttack()

    /**
     * Sets armor defense attribute for robot selected by owner and index.
     * <p>
     * <b>Selection:</b> Registry lookup by player UUID and robot index.
     */
    protected static int executeOwnerSetDefense(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, OWNER_SELECTOR, AttributeOperations.setDefense(), false);
    } // executeOwnerSetDefense()

    /**
     * Sets movement speed attribute for robot selected by owner and index.
     * <p>
     * <b>Selection:</b> Registry lookup by player UUID and robot index.
     */
    protected static int executeOwnerSetSpeed(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, OWNER_SELECTOR, AttributeOperations.setSpeed(), false);
    } // executeOwnerSetSpeed()

    /**
     * Sets all attributes atomically for robot selected by owner and index.
     * <p>
     * <b>Selection:</b> Registry lookup by player UUID and robot index.
     */
    protected static int executeOwnerSetAllAttributes(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, OWNER_SELECTOR, AttributeOperations.setAllAttributes(), false);
    } // executeOwnerSetAllAttributes()

    /**
     * Sets fire protection level for robot selected by owner and index.
     * <p>
     * <b>Selection:</b> Registry lookup by player UUID and robot index.
     */
    protected static int executeOwnerSetFireProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, OWNER_SELECTOR, ProtectionOperations.setFireProtection(), false);
    } // executeOwnerSetFireProtection()

    /**
     * Sets fall protection level for robot selected by owner and index.
     * <p>
     * <b>Selection:</b> Registry lookup by player UUID and robot index.
     */
    protected static int executeOwnerSetFallProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, OWNER_SELECTOR, ProtectionOperations.setFallProtection(), false);
    } // executeOwnerSetFallProtection()

    /**
     * Sets blast protection level for robot selected by owner and index.
     * <p>
     * <b>Selection:</b> Registry lookup by player UUID and robot index.
     */
    protected static int executeOwnerSetBlastProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, OWNER_SELECTOR, ProtectionOperations.setBlastProtection(), false);
    } // executeOwnerSetBlastProtection()

    /**
     * Sets projectile protection level for robot selected by owner and index.
     * <p>
     * <b>Selection:</b> Registry lookup by player UUID and robot index.
     */
    protected static int executeOwnerSetProjectileProtection(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, OWNER_SELECTOR, ProtectionOperations.setProjectileProtection(), false);
    } // executeOwnerSetProjectileProtection()

    /**
     * Sets all protection levels atomically for robot selected by owner and index.
     * <p>
     * <b>Selection:</b> Registry lookup by player UUID and robot index.
     */
    protected static int executeOwnerSetAllProtections(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, OWNER_SELECTOR, ProtectionOperations.setAllProtections(), false);
    } // executeOwnerSetAllProtections()

    /**
     * Sets texture/color appearance for robot selected by owner and index.
     * <p>
     * <b>Selection:</b> Registry lookup by player UUID and robot index.
     */
    protected static int executeOwnerSetAppearance(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, OWNER_SELECTOR, UtilityOperations.setAppearance(), false);
    } // executeOwnerSetAppearance()

    /**
     * Sets custom name for robot selected by owner and index with visibility enabled.
     * <p>
     * <b>Selection:</b> Registry lookup by player UUID and robot index.
     */
    protected static int executeOwnerSetIdentifier(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, OWNER_SELECTOR, UtilityOperations.setIdentifier(), false);
    } // executeOwnerSetIdentifier()

    /**
     * Teleports command source to robot selected by owner and index.
     * <p>
     * <b>Selection:</b> Registry lookup by player UUID and robot index.
     * <i>Note:</i> Fails gracefully if robot is offline or unloaded.
     */
    protected static int executeOwnerTeleport(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        PlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        int index = IntegerArgumentType.getInteger(ctx, "robot_index");

        LovelyRobotEntity robot = getOwnerRobotByIndex(player, index);
        if (robot == null) {
            ctx.getSource().sendError(Text.literal("Robot not found or offline"));
            return 0;
        }

        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
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
     * Teleports robot to owner's location by registry lookup.
     * <p>
     * <b>Selection:</b> Registry lookup by player UUID and robot index.
     */
    protected static int executeOwnerRecall(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, OWNER_SELECTOR, (robot, c) -> {
            PlayerEntity player = EntityArgumentType.getPlayer(c, "player");
            robot.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
            return new CommandResult(true, "Recalled " + Utility.getEntityCustomName(robot) + " to " + player.getName().getString());
        }, false);
    } // executeOwnerRecall()

    /**
     * Heals robot selected by owner and index to full health.
     * <p>
     * <b>Selection:</b> Registry lookup by player UUID and robot index.
     */
    protected static int executeOwnerHeal(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return executeRobotCommand(ctx, OWNER_SELECTOR, UtilityOperations.heal(), false);
    } // executeOwnerHeal()

    /**
     * Heals all robots owned by specified player to full health.
     * <p>
     * <b>Selection:</b> All robots in registry for player UUID.
     * <i>Note:</i> Skips offline or unloaded robots automatically.
     */
    protected static int executeOwnerHealAll(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
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
     * <p>
     * <b>Selection:</b> Registry lookup by player UUID and robot index.
     * <i>Note:</i> Fails gracefully if robot is offline or unloaded.
     */
    protected static int executeOwnerStats(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
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
     * Transfers robot ownership from one player to another by registry manipulation.
     * <p>
     * <b>Selection:</b> Registry lookup by source player UUID and robot index.
     * <i>Note:</i> Unregisters from old owner and re-registers automatically.
     */
    protected static int executeOwnerTransfer(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
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
    protected static int executeReload(CommandContext<ServerCommandSource> ctx) {
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
    protected static int executeListOwners(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
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
    protected static int executeListPlayerRobots(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
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

} // Class: NativeCommands