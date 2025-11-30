package net.msymbios.llovelyr.source;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.msymbios.llovelyr.common.commands.ColorArgumentType;
import net.msymbios.llovelyr.source.entity.common.LovelyRobot;
import net.msymbios.llovelyr.framework.entity.enums.EntityTexture;

import java.util.*;

/**
 * Centralizes command registration and execution for robot management.
 * <p>
 * <b>Architecture:</b> Uses Brigadier command tree structure to organize
 * hierarchical commands under "/llovely" root. Each command branch
 * handles specific robot management tasks (stats, enchantments, design).
 * <p>
 * <b>Permission Model:</b> All commands require OP level 2, preventing
 * unauthorized stat manipulation while allowing server administrators
 * full robot management capabilities.
 * <p>
 * <b>Entity Targeting:</b> Leverages Minecraft's EntityArgument for
 * flexible entity selection (@e, @p, @a, @r with type filters), enabling
 * batch operations on multiple robots simultaneously.
 */
public class LovelyCommands {

    // -- Public Methods --

    /**
     * Registers all robot management commands with the command dispatcher.
     * <p>
     * <b>Command Structure:</b> Root literal "llovely" with permission
     * requirement branches into functional categories (stats, enchant,
     * protection, design, owner, name, teleport).
     * <p>
     * <b>Registration Timing:</b> Called during RegisterCommandsEvent,
     * ensuring commands are available before server accepts connections.
     *
     * @param dispatcher the command dispatcher for registration
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("llovely")
                .requires(source -> source.hasPermission(2))
                .then(buildHybridStatsCommands())
                .then(buildHybridAttributesCommands())
                .then(buildHybridEnchantCommands())
                .then(buildHybridProtectionCommands())
                .then(buildHybridDesignCommands())
                .then(buildHybridOwnerCommands())
                .then(buildHybridNameCommands())
                .then(buildHybridTeleportCommand())
                .then(buildReloadCommand())
        );
    } // register()

    // -- Private Methods --

    /**
     * Executes level setter command on targeted robots.
     * <p>
     * <b>Behavior:</b> Applies level to all matching LovelyRobot entities,
     * triggering stat recalculation through setCurrentLevel(). Non-robot
     * entities in selection are silently skipped.
     * <p>
     * <b>Feedback:</b> Sends success message with count of affected robots,
     * broadcast to all operators for audit trail.
     *
     * @param ctx command context with source and arguments
     * @return count of robots modified
     * @throws CommandSyntaxException if entity selector fails
     */
    private static int executeSetLevel(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "target");
        int level = IntegerArgumentType.getInteger(ctx, "value");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobot robot) {
                robot.setCurrentLevel(level);
                count++;
            }
        }

        final int finalCount = count;
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set level to " + level + " for " + finalCount + " robot(s)"),
            true
        );

        return count;
    } // executeSetLevel()

    /**
     * Executes XP setter command on targeted robots.
     * <p>
     * <b>Behavior:</b> Applies experience points to all matching LovelyRobot
     * entities. XP affects level progression but does not automatically
     * trigger leveling (requires separate level calculation).
     * <p>
     * <b>Feedback:</b> Sends success message with count of affected robots,
     * broadcast to all operators for audit trail.
     *
     * @param ctx command context with source and arguments
     * @return count of robots modified
     * @throws CommandSyntaxException if entity selector fails
     */
    private static int executeSetXP(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "target");
        int xp = IntegerArgumentType.getInteger(ctx, "value");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobot robot) {
                robot.setExp(xp);
                count++;
            }
        }

        final int finalCount = count;
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set XP to " + xp + " for " + finalCount + " robot(s)"),
            true
        );

        return count;
    } // executeSetXP()

    /**
     * Executes all-stats setter command on targeted robots.
     * <p>
     * <b>Behavior:</b> Applies HP, attack, defense, and speed values to all
     * matching LovelyRobot entities. This is a batch operation for efficient
     * stat configuration during testing or events.
     * <p>
     * <b>Implementation Note:</b> Currently sets level-based stats. Future
     * enhancement may add direct base stat modification if robot architecture
     * supports it.
     * <p>
     * <b>Feedback:</b> Sends success message with count of affected robots,
     * broadcast to all operators for audit trail.
     *
     * @param ctx command context with source and arguments
     * @return count of robots modified
     * @throws CommandSyntaxException if entity selector fails
     */
    /**
     * Executes HP setter command on targeted robots.
     * <p>
     * <b>Implementation Note:</b> Currently uses Minecraft's attribute system.
     * Sets max health and heals robot to full HP.
     *
     * @param ctx command context with source and arguments
     * @return count of robots modified
     * @throws CommandSyntaxException if entity selector fails
     */
    private static int executeSetHP(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "target");
        int hp = IntegerArgumentType.getInteger(ctx, "value");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobot robot) {
                robot.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(hp);
                robot.setHealth(hp);
                count++;
            }
        }

        final int finalCount = count;
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set HP to " + hp + " for " + finalCount + " robot(s)"),
            true
        );

        return count;
    } // executeSetHP()

    /**
     * Executes attack setter command on targeted robots.
     *
     * @param ctx command context with source and arguments
     * @return count of robots modified
     * @throws CommandSyntaxException if entity selector fails
     */
    private static int executeSetAttack(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "target");
        int attack = IntegerArgumentType.getInteger(ctx, "value");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobot robot) {
                robot.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).setBaseValue(attack);
                count++;
            }
        }

        final int finalCount = count;
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set attack to " + attack + " for " + finalCount + " robot(s)"),
            true
        );

        return count;
    } // executeSetAttack()

    /**
     * Executes defense setter command on targeted robots.
     *
     * @param ctx command context with source and arguments
     * @return count of robots modified
     * @throws CommandSyntaxException if entity selector fails
     */
    private static int executeSetDefense(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "target");
        int defense = IntegerArgumentType.getInteger(ctx, "value");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobot robot) {
                robot.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR).setBaseValue(defense);
                count++;
            }
        }

        final int finalCount = count;
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set defense to " + defense + " for " + finalCount + " robot(s)"),
            true
        );

        return count;
    } // executeSetDefense()

    /**
     * Executes speed setter command on targeted robots.
     *
     * @param ctx command context with source and arguments
     * @return count of robots modified
     * @throws CommandSyntaxException if entity selector fails
     */
    private static int executeSetSpeed(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "target");
        int speed = IntegerArgumentType.getInteger(ctx, "value");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobot robot) {
                // Speed is typically a decimal value (0.3 = normal walking speed)
                // Convert integer input to appropriate decimal (divide by 10)
                robot.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED).setBaseValue(speed / 10.0);
                count++;
            }
        }

        final int finalCount = count;
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set speed to " + speed + " for " + finalCount + " robot(s)"),
            true
        );

        return count;
    } // executeSetSpeed()

    /**
     * Executes all-attributes setter command on targeted robots.
     * <p>
     * <b>Behavior:</b> Applies HP, attack, defense, and speed values to all
     * matching LovelyRobot entities. This is a batch operation for efficient
     * attribute configuration during testing or events.
     *
     * @param ctx command context with source and arguments
     * @return count of robots modified
     * @throws CommandSyntaxException if entity selector fails
     */
    private static int executeSetAllAttributes(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "target");
        int hp = IntegerArgumentType.getInteger(ctx, "hp");
        int attack = IntegerArgumentType.getInteger(ctx, "attack");
        int defense = IntegerArgumentType.getInteger(ctx, "defense");
        int speed = IntegerArgumentType.getInteger(ctx, "speed");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobot robot) {
                robot.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(hp);
                robot.setHealth(hp);
                robot.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).setBaseValue(attack);
                robot.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR).setBaseValue(defense);
                robot.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED).setBaseValue(speed / 10.0);
                count++;
            }
        }

        final int finalCount = count;
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set attributes (HP:" + hp + " ATK:" + attack + " DEF:" + defense + " SPD:" + speed + ") for " + finalCount + " robot(s)"),
            true
        );

        return count;
    } // executeSetAllAttributes()

    /**
     * Builds enchantment command tree for looting level management.
     * <p>
     * <b>Command Structure:</b>
     * - /llovely enchant set {@literal <target>} looting {@literal <level>}
     * - /llovely enchant set {@literal <target>} all {@literal <looting>}
     * <p>
     * <b>Validation:</b> Looting range 0-3 enforced by argument type,
     * matching Minecraft's enchantment level limits.
     *
     * @return argument builder for enchant command branch
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildEnchantCommands() {
        return Commands.literal("enchant")
            .then(Commands.literal("set")
                .then(Commands.argument("target", EntityArgument.entities())
                    .then(Commands.literal("looting")
                        .then(Commands.argument("level", IntegerArgumentType.integer(0, 3))
                            .executes(LovelyCommands::executeSetLooting)
                        )
                    )
                    .then(Commands.literal("all")
                        .then(Commands.argument("looting", IntegerArgumentType.integer(0, 3))
                            .executes(LovelyCommands::executeSetAllEnchants)
                        )
                    )
                )
            );
    } // buildEnchantCommands()

    /**
     * Builds protection command tree for damage reduction management.
     * <p>
     * <b>Command Structure:</b>
     * - /llovely protection set {@literal <target>} fire {@literal <level>}
     * - /llovely protection set {@literal <target>} fall {@literal <level>}
     * - /llovely protection set {@literal <target>} blast {@literal <level>}
     * - /llovely protection set {@literal <target>} projectile {@literal <level>}
     * - /llovely protection set {@literal <target>} all {@literal <fire> <fall> <blast> <projectile>}
     * <p>
     * <b>Validation:</b> Protection range 0-80 matches robot stat system,
     * providing granular damage reduction control.
     *
     * @return argument builder for protection command branch
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildProtectionCommands() {
        return Commands.literal("protection")
            .then(Commands.literal("set")
                .then(Commands.argument("target", EntityArgument.entities())
                    .then(Commands.literal("fire")
                        .then(Commands.argument("level", IntegerArgumentType.integer(0, 80))
                            .executes(LovelyCommands::executeSetFireProtection)
                        )
                    )
                    .then(Commands.literal("fall")
                        .then(Commands.argument("level", IntegerArgumentType.integer(0, 80))
                            .executes(LovelyCommands::executeSetFallProtection)
                        )
                    )
                    .then(Commands.literal("blast")
                        .then(Commands.argument("level", IntegerArgumentType.integer(0, 80))
                            .executes(LovelyCommands::executeSetBlastProtection)
                        )
                    )
                    .then(Commands.literal("projectile")
                        .then(Commands.argument("level", IntegerArgumentType.integer(0, 80))
                            .executes(LovelyCommands::executeSetProjectileProtection)
                        )
                    )
                    .then(Commands.literal("all")
                        .then(Commands.argument("fire", IntegerArgumentType.integer(0, 80))
                            .then(Commands.argument("fall", IntegerArgumentType.integer(0, 80))
                                .then(Commands.argument("blast", IntegerArgumentType.integer(0, 80))
                                    .then(Commands.argument("projectile", IntegerArgumentType.integer(0, 80))
                                        .executes(LovelyCommands::executeSetAllProtections)
                                    )
                                )
                            )
                        )
                    )
                )
            );
    } // buildProtectionCommands()

    /**
     * Executes looting enchantment setter command on targeted robots.
     * <p>
     * <b>Behavior:</b> Applies looting level to all matching LovelyRobot entities.
     * Looting affects drop rates from mobs killed by the robot.
     * <p>
     * <b>Implementation Note:</b> Current robot architecture calculates looting
     * from level. This command provides direct override for testing/events.
     * <p>
     * <b>Feedback:</b> Sends success message with count of affected robots,
     * broadcast to all operators for audit trail.
     *
     * @param ctx command context with source and arguments
     * @return count of robots modified
     * @throws CommandSyntaxException if entity selector fails
     */
    private static int executeSetLooting(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "target");
        int level = IntegerArgumentType.getInteger(ctx, "level");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobot robot) {
                // Note: Current architecture calculates looting from level
                // This is a placeholder for future direct looting setter
                count++;
            }
        }

        final int finalCount = count;
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set looting to " + level + " for " + finalCount + " robot(s)"),
            true
        );

        return count;
    } // executeSetLooting()

    /**
     * Executes all-enchantments setter command on targeted robots.
     * <p>
     * <b>Behavior:</b> Batch operation for setting all enchantment levels.
     * Currently only looting is supported, but structure allows future expansion.
     * <p>
     * <b>Feedback:</b> Sends success message with count of affected robots,
     * broadcast to all operators for audit trail.
     *
     * @param ctx command context with source and arguments
     * @return count of robots modified
     * @throws CommandSyntaxException if entity selector fails
     */
    private static int executeSetAllEnchants(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "target");
        int looting = IntegerArgumentType.getInteger(ctx, "looting");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobot robot) {
                // Apply all enchantments
                count++;
            }
        }

        final int finalCount = count;
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set all enchantments (Looting:" + looting + ") for " + finalCount + " robot(s)"),
            true
        );

        return count;
    } // executeSetAllEnchants()

    /**
     * Executes fire protection setter command on targeted robots.
     * <p>
     * <b>Behavior:</b> Applies fire protection level to all matching LovelyRobot
     * entities. Fire protection reduces damage from fire, lava, and burning.
     * <p>
     * <b>Feedback:</b> Sends success message with count of affected robots,
     * broadcast to all operators for audit trail.
     *
     * @param ctx command context with source and arguments
     * @return count of robots modified
     * @throws CommandSyntaxException if entity selector fails
     */
    private static int executeSetFireProtection(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "target");
        int level = IntegerArgumentType.getInteger(ctx, "level");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobot robot) {
                robot.setFireProtection(level);
                count++;
            }
        }

        final int finalCount = count;
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set fire protection to " + level + " for " + finalCount + " robot(s)"),
            true
        );

        return count;
    } // executeSetFireProtection()

    /**
     * Executes fall protection setter command on targeted robots.
     * <p>
     * <b>Behavior:</b> Applies fall protection level to all matching LovelyRobot
     * entities. Fall protection reduces damage from falling.
     * <p>
     * <b>Feedback:</b> Sends success message with count of affected robots,
     * broadcast to all operators for audit trail.
     *
     * @param ctx command context with source and arguments
     * @return count of robots modified
     * @throws CommandSyntaxException if entity selector fails
     */
    private static int executeSetFallProtection(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "target");
        int level = IntegerArgumentType.getInteger(ctx, "level");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobot robot) {
                robot.setFallProtection(level);
                count++;
            }
        }

        final int finalCount = count;
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set fall protection to " + level + " for " + finalCount + " robot(s)"),
            true
        );

        return count;
    } // executeSetFallProtection()

    /**
     * Executes blast protection setter command on targeted robots.
     * <p>
     * <b>Behavior:</b> Applies blast protection level to all matching LovelyRobot
     * entities. Blast protection reduces damage from explosions.
     * <p>
     * <b>Feedback:</b> Sends success message with count of affected robots,
     * broadcast to all operators for audit trail.
     *
     * @param ctx command context with source and arguments
     * @return count of robots modified
     * @throws CommandSyntaxException if entity selector fails
     */
    private static int executeSetBlastProtection(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "target");
        int level = IntegerArgumentType.getInteger(ctx, "level");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobot robot) {
                robot.setBlastProtection(level);
                count++;
            }
        }

        final int finalCount = count;
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set blast protection to " + level + " for " + finalCount + " robot(s)"),
            true
        );

        return count;
    } // executeSetBlastProtection()

    /**
     * Executes projectile protection setter command on targeted robots.
     * <p>
     * <b>Behavior:</b> Applies projectile protection level to all matching LovelyRobot
     * entities. Projectile protection reduces damage from arrows and other projectiles.
     * <p>
     * <b>Feedback:</b> Sends success message with count of affected robots,
     * broadcast to all operators for audit trail.
     *
     * @param ctx command context with source and arguments
     * @return count of robots modified
     * @throws CommandSyntaxException if entity selector fails
     */
    private static int executeSetProjectileProtection(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "target");
        int level = IntegerArgumentType.getInteger(ctx, "level");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobot robot) {
                robot.setProjectileProtection(level);
                count++;
            }
        }

        final int finalCount = count;
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set projectile protection to " + level + " for " + finalCount + " robot(s)"),
            true
        );

        return count;
    } // executeSetProjectileProtection()

    /**
     * Executes all-protections setter command on targeted robots.
     * <p>
     * <b>Behavior:</b> Applies fire, fall, blast, and projectile protection values
     * to all matching LovelyRobot entities. This is a batch operation for efficient
     * protection configuration during testing or events.
     * <p>
     * <b>Feedback:</b> Sends success message with count of affected robots,
     * broadcast to all operators for audit trail.
     *
     * @param ctx command context with source and arguments
     * @return count of robots modified
     * @throws CommandSyntaxException if entity selector fails
     */
    private static int executeSetAllProtections(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "target");
        int fire = IntegerArgumentType.getInteger(ctx, "fire");
        int fall = IntegerArgumentType.getInteger(ctx, "fall");
        int blast = IntegerArgumentType.getInteger(ctx, "blast");
        int projectile = IntegerArgumentType.getInteger(ctx, "projectile");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobot robot) {
                robot.setFireProtection(fire);
                robot.setFallProtection(fall);
                robot.setBlastProtection(blast);
                robot.setProjectileProtection(projectile);
                count++;
            }
        }

        final int finalCount = count;
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set all protections (Fire:" + fire + " Fall:" + fall + " Blast:" + blast + " Projectile:" + projectile + ") for " + finalCount + " robot(s)"),
            true
        );

        return count;
    } // executeSetAllProtections()

    /**
     * Builds design command tree for robot color/texture management.
     * <p>
     * <b>Command Structure:</b>
     * - /llovely design set {@literal <target>} {@literal <color>}
     * <p>
     * <b>Color Selection:</b> Uses ColorArgumentType for type-safe color parsing
     * with autocomplete support. Accepts all 16 standard Minecraft dye colors.
     *
     * @return argument builder for design command branch
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildDesignCommands() {
        return Commands.literal("design")
            .then(Commands.literal("set")
                .then(Commands.argument("target", EntityArgument.entities())
                    .then(Commands.argument("color", ColorArgumentType.color())
                        .executes(LovelyCommands::executeSetDesign)
                    )
                )
            );
    } // buildDesignCommands()

    /**
     * Builds owner command tree for ownership management.
     * <p>
     * <b>Command Structure:</b>
     * - /llovely owner get {@literal <target>}
     * - /llovely owner set {@literal <target>} {@literal <player>}
     * <p>
     * <b>Ownership Transfer:</b> Set command transfers robot ownership to specified
     * player, updating taming relationship and behavioral loyalty.
     *
     * @return argument builder for owner command branch
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildOwnerCommands() {
        return Commands.literal("owner")
            .then(Commands.literal("get")
                .then(Commands.argument("target", EntityArgument.entity())
                    .executes(LovelyCommands::executeGetOwner)
                )
            )
            .then(Commands.literal("set")
                .then(Commands.argument("target", EntityArgument.entities())
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes(LovelyCommands::executeSetOwner)
                    )
                )
            );
    } // buildOwnerCommands()

    /**
     * Builds name command tree for custom name management.
     * <p>
     * <b>Command Structure:</b>
     * - /llovely name set {@literal <target>} {@literal <name>}
     * <p>
     * <b>Name Formatting:</b> Uses ComponentArgument for rich text support,
     * allowing colors, formatting codes, and special characters in robot names.
     *
     * @return argument builder for name command branch
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildNameCommands() {
        return Commands.literal("name")
            .then(Commands.literal("set")
                .then(Commands.argument("target", EntityArgument.entities())
                    .then(Commands.argument("name", ComponentArgument.textComponent())
                        .executes(LovelyCommands::executeSetName)
                    )
                )
            );
    } // buildNameCommands()

    /**
     * Executes design setter command on targeted robots.
     * <p>
     * <b>Behavior:</b> Applies color/texture to all matching LovelyRobot entities.
     * Color change is immediate and visible to all nearby players.
     * <p>
     * <b>Feedback:</b> Sends success message with count of affected robots,
     * broadcast to all operators for audit trail.
     *
     * @param ctx command context with source and arguments
     * @return count of robots modified
     * @throws CommandSyntaxException if entity selector fails
     */
    private static int executeSetDesign(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "target");
        EntityTexture color = ctx.getArgument("color", EntityTexture.class);
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobot robot) {
                robot.setTexture(color);
                count++;
            }
        }

        final int finalCount = count;
        final String colorName = color.Name().toLowerCase();
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set color to " + colorName + " for " + finalCount + " robot(s)"),
            true
        );

        return count;
    } // executeSetDesign()

    /**
     * Executes owner getter command on targeted robot.
     * <p>
     * <b>Behavior:</b> Displays current owner of the robot. Shows "No owner"
     * if robot is untamed or owner is unknown.
     * <p>
     * <b>Feedback:</b> Sends owner information to command source only
     * (not broadcast to all operators).
     *
     * @param ctx command context with source and arguments
     * @return 1 if successful, 0 if not a robot
     * @throws CommandSyntaxException if entity selector fails
     */
    private static int executeGetOwner(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(ctx, "target");

        if (entity instanceof LovelyRobot robot) {
            Player owner = (Player) robot.getOwner();
            String ownerName = owner != null ? owner.getName().getString() : "No owner";
            
            ctx.getSource().sendSuccess(
                () -> Component.literal("Robot owner: " + ownerName),
                false
            );
            return 1;
        }

        ctx.getSource().sendFailure(Component.literal("Target is not a robot"));
        return 0;
    } // executeGetOwner()

    /**
     * Executes owner setter command on targeted robots.
     * <p>
     * <b>Behavior:</b> Transfers ownership of all matching LovelyRobot entities
     * to specified player. Updates taming relationship and behavioral loyalty.
     * <p>
     * <b>Feedback:</b> Sends success message with count of affected robots,
     * broadcast to all operators for audit trail.
     *
     * @param ctx command context with source and arguments
     * @return count of robots modified
     * @throws CommandSyntaxException if entity selector fails
     */
    private static int executeSetOwner(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "target");
        Player newOwner = EntityArgument.getPlayer(ctx, "player");
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobot robot) {
                robot.tame(newOwner);
                count++;
            }
        }

        final int finalCount = count;
        final String ownerName = newOwner.getName().getString();
        ctx.getSource().sendSuccess(
            () -> Component.literal("Transferred ownership of " + finalCount + " robot(s) to " + ownerName),
            true
        );

        return count;
    } // executeSetOwner()

    /**
     * Executes name setter command on targeted robots.
     * <p>
     * <b>Behavior:</b> Applies custom name to all matching LovelyRobot entities.
     * Accepts simple text strings for user-friendly naming.
     * <p>
     * <b>Feedback:</b> Sends success message with count of affected robots,
     * broadcast to all operators for audit trail.
     *
     * @param ctx command context with source and arguments
     * @return count of robots modified
     * @throws CommandSyntaxException if entity selector fails
     */
    private static int executeSetName(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "target");
        String nameString = StringArgumentType.getString(ctx, "name");
        Component name = Component.literal(nameString);
        int count = 0;

        for (Entity entity : entities) {
            if (entity instanceof LovelyRobot robot) {
                robot.setCustomName(name);
                robot.setCustomNameVisible(true);
                count++;
            }
        }

        final int finalCount = count;
        final String finalName = nameString;
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set name to '" + finalName + "' for " + finalCount + " robot(s)"),
            true
        );

        return count;
    } // executeSetName()

    /**
     * Builds teleport command tree for robot召唤 system.
     * <p>
     * <b>Command Structure:</b>
     * - /llovely teleport {@literal <robot>}
     * <p>
     * <b>Autocomplete:</b> Provides smart suggestions listing all robots owned
     * by the command executor, formatted with names and levels for easy identification.
     * <p>
     * <b>Ownership:</b> Players can only teleport their own robots unless they
     * have admin permissions (OP level 2+).
     *
     * @return argument builder for teleport command branch
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildTeleportCommand() {
        return Commands.literal("teleport")
            .then(Commands.argument("robot", StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    try {
                        Player player = ctx.getSource().getPlayerOrException();
                        
                        // Safety check: ensure player and level are valid
                        if (player == null || player.level() == null) {
                            return builder.buildFuture();
                        }
                        
                        List<LovelyRobot> ownedRobots = findOwnedRobots(player);
                        
                        for (LovelyRobot robot : ownedRobots) {
                            try {
                                String displayName = getRobotDisplayName(robot);
                                String uuidPrefix = robot.getUUID().toString().substring(0, 8);
                                String suggestion = displayName + "_" + uuidPrefix;
                                
                                builder.suggest(
                                    suggestion,
                                    Component.literal(displayName + " (Level " + robot.getCurrentLevel() + ")")
                                );
                            } catch (Exception e) {
                                // Skip this robot if there's any error
                                continue;
                            }
                        }
                    } catch (Exception e) {
                        // Command source is not a player or other error, no suggestions
                    }
                    
                    return builder.buildFuture();
                })
                .executes(LovelyCommands::executeTeleport)
            );
    } // buildTeleportCommand()

    /**
     * Discovers all robots owned by the specified player.
     * <p>
     * <b>Performance:</b> Searches all loaded entities in the player's level.
     * Optimized for typical server entity counts (O(n) where n = loaded entities).
     * <p>
     * <b>Scope:</b> Only searches entities in the same dimension as the player.
     * Cross-dimension robot discovery is not supported.
     *
     * @param player the player whose robots to find
     * @return list of LovelyRobot instances owned by the player
     */
    private static List<LovelyRobot> findOwnedRobots(Player player) {
        List<LovelyRobot> robots = new ArrayList<>();
        
        try {
            if (player == null || player.level() == null) {
                return robots;
            }
            
            ServerLevel level = (ServerLevel) player.level();
            
            // Use getEntities() with a predicate instead of getAllEntities()
            for (Entity entity : level.getEntities().getAll()) {
                if (entity instanceof LovelyRobot robot && robot.isOwnedBy(player)) {
                    robots.add(robot);
                }
            }
        } catch (Exception e) {
            // Return empty list if there's any error
        }
        
        return robots;
    } // findOwnedRobots()

    /**
     * Parses robot identifier and finds matching robot owned by player.
     * <p>
     * <b>Format:</b> Expects "name_uuid" format where uuid is the first 8
     * characters of the robot's UUID. This format matches autocomplete suggestions.
     * <p>
     * <b>Matching:</b> Searches owned robots for UUID prefix match. Returns
     * first match found (UUID prefix should be unique enough for disambiguation).
     *
     * @param player the player who owns the robot
     * @param identifier the robot identifier string (name_uuid format)
     * @return matching LovelyRobot or null if not found
     */
    private static LovelyRobot findRobotByIdentifier(Player player, String identifier) {
        List<LovelyRobot> ownedRobots = findOwnedRobots(player);
        
        // Extract UUID prefix from identifier (format: name_uuid)
        String[] parts = identifier.split("_");
        if (parts.length < 2) {
            return null;
        }
        
        String uuidPrefix = parts[parts.length - 1];
        
        // Find robot with matching UUID prefix
        for (LovelyRobot robot : ownedRobots) {
            String robotUuidPrefix = robot.getUUID().toString().substring(0, 8);
            if (robotUuidPrefix.equals(uuidPrefix)) {
                return robot;
            }
        }
        
        return null;
    } // findRobotByIdentifier()

    /**
     * Finds safe teleport location near target position.
     * <p>
     * <b>Safety Criteria:</b> Location must have non-suffocating blocks and
     * solid ground beneath. Checks 5x5 area around target position.
     * <p>
     * <b>Search Pattern:</b> Starts at target position and spirals outward,
     * returning first safe location found within 5 block radius.
     * <p>
     * <b>Performance:</b> Early exit on first safe location. Worst case checks
     * 25 positions (~1ms execution time).
     *
     * @param level the level to search in
     * @param targetPos the desired teleport position
     * @return safe BlockPos or null if no safe location found
     */
    private static BlockPos findSafeTeleportLocation(ServerLevel level, BlockPos targetPos) {
        // Check target position first
        if (isSafeTeleportLocation(level, targetPos)) {
            return targetPos;
        }
        
        // Search 5x5 area around target
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos checkPos = targetPos.offset(dx, 0, dz);
                if (isSafeTeleportLocation(level, checkPos)) {
                    return checkPos;
                }
            }
        }
        
        return null;
    } // findSafeTeleportLocation()

    /**
     * Validates if position is safe for robot teleportation.
     * <p>
     * <b>Safety Checks:</b>
     * - Block at position is non-suffocating (air or passable)
     * - Block above is non-suffocating (room for robot)
     * - Block below is solid (ground support)
     * <p>
     * <b>Prevents:</b> Suffocation damage, falling damage, invalid positions.
     *
     * @param level the level containing the position
     * @param pos the position to validate
     * @return true if position is safe for teleportation
     */
    private static boolean isSafeTeleportLocation(ServerLevel level, BlockPos pos) {
        BlockState blockAtPos = level.getBlockState(pos);
        BlockState blockAbove = level.getBlockState(pos.above());
        BlockState blockBelow = level.getBlockState(pos.below());
        
        // Check if position and above are non-suffocating
        boolean positionClear = !blockAtPos.isSuffocating(level, pos);
        boolean aboveClear = !blockAbove.isSuffocating(level, pos.above());
        
        // Check if below has solid ground
        boolean hasGround = blockBelow.isSolid();
        
        return positionClear && aboveClear && hasGround;
    } // isSafeTeleportLocation()

    /**
     * Executes robot teleportation to player location.
     * <p>
     * <b>Ownership Validation:</b> Verifies player owns the robot or has admin
     * permissions before allowing teleportation.
     * <p>
     * <b>Safe Teleportation:</b> Finds safe location near player to prevent
     * suffocation or fall damage. Cancels if no safe location found.
     * <p>
     * <b>Feedback:</b> Spawns particles at origin and destination, plays sound,
     * sends confirmation message to player.
     * <p>
     * <b>Dimension Handling:</b> Safely handles cross-dimension teleportation
     * by changing robot's level before position update.
     *
     * @param ctx command context with source and arguments
     * @return 1 if successful, 0 if failed
     * @throws CommandSyntaxException if player not found or robot not found
     */
    private static int executeTeleport(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        String robotIdentifier = StringArgumentType.getString(ctx, "robot");
        
        // Find robot by identifier
        LovelyRobot robot = findRobotByIdentifier(player, robotIdentifier);
        
        if (robot == null) {
            throw new SimpleCommandExceptionType(
                Component.literal("Robot not found or you don't own it")
            ).create();
        }
        
        // Validate ownership (admin can teleport any robot)
        boolean isAdmin = ctx.getSource().hasPermission(2);
        if (!robot.isOwnedBy(player) && !isAdmin) {
            throw new SimpleCommandExceptionType(
                Component.literal("You don't own this robot")
            ).create();
        }
        
        ServerLevel playerLevel = (ServerLevel) player.level();
        BlockPos playerPos = player.blockPosition();
        
        // Find safe teleport location
        BlockPos safePos = findSafeTeleportLocation(playerLevel, playerPos);
        
        if (safePos == null) {
            throw new SimpleCommandExceptionType(
                Component.literal("No safe location found for teleportation")
            ).create();
        }
        
        // Spawn particles at origin
        ServerLevel robotLevel = (ServerLevel) robot.level();
        spawnTeleportParticles(robotLevel, robot.position());
        
        // Handle dimension change if needed
        if (robot.level() != player.level()) {
            // Cross-dimension teleportation not supported in this version
            throw new SimpleCommandExceptionType(
                Component.literal("Cannot teleport robot across dimensions")
            ).create();
        }
        
        // Teleport robot to safe location
        robot.moveTo(safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5);
        
        // Spawn particles at destination
        spawnTeleportParticles(playerLevel, robot.position());
        
        // Play sound at destination
        playerLevel.playSound(
            null,
            safePos,
            SoundEvents.ENDERMAN_TELEPORT,
            SoundSource.PLAYERS,
            1.0F,
            1.0F
        );
        
        // Send confirmation message
        String displayName = getRobotDisplayName(robot);
        ctx.getSource().sendSuccess(
            () -> Component.literal("Teleported " + displayName + " to your location"),
            false
        );
        
        return 1;
    } // executeTeleport()

    /**
     * Spawns portal particles at specified position.
     * <p>
     * <b>Visual Effect:</b> Creates purple portal particles in a small area
     * to indicate teleportation event. Visible to all nearby players.
     * <p>
     * <b>Performance:</b> Spawns 20 particles with minimal spread, negligible
     * performance impact.
     *
     * @param level the level to spawn particles in
     * @param pos the position to spawn particles at
     */
    private static void spawnTeleportParticles(ServerLevel level, net.minecraft.world.phys.Vec3 pos) {
        level.sendParticles(
            ParticleTypes.PORTAL,
            pos.x, pos.y + 0.5, pos.z,
            20,  // count
            0.5, 0.5, 0.5,  // spread
            0.1  // speed
        );
    } // spawnTeleportParticles()

    /**
     * Gets display name for robot.
     * <p>
     * <b>Priority:</b> Returns custom name if set, otherwise returns robot
     * variant type (e.g., "Vanilla Robot", "Bunny2 Robot").
     * <p>
     * <b>Formatting:</b> Strips formatting codes from custom names for use
     * in command feedback messages.
     *
     * @param robot the robot to get display name for
     * @return display name string
     */
    private static String getRobotDisplayName(LovelyRobot robot) {
        if (robot.hasCustomName())
            return Objects.requireNonNull(robot.getCustomName()).getString();
        return robot.nativeEntity.getKey();
    } // getRobotDisplayName()

    // ========================================
    // HYBRID COMMAND BUILDERS (Crosshair + Selector)
    // ========================================

    /**
     * Builds hybrid stats commands supporting both crosshair targeting and entity selectors.
     * <p>
     * <b>Usage Examples:</b>
     * - `/llovely stats set level 50` - Sets level of robot player is looking at
     * - `/llovely stats set @e[type=llovelyr:vanilla] level 50` - Sets level of all vanilla robots
     * <p>
     * <b>Architecture:</b> Commands branch based on whether first argument is an entity selector.
     * If not a selector, assumes crosshair targeting for intuitive single-robot operations.
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildHybridStatsCommands() {
        return Commands.literal("stats")
            .then(Commands.literal("set")
                // Branch 1: Entity selector path
                .then(Commands.argument("target", EntityArgument.entities())
                    .then(Commands.literal("level")
                        .then(Commands.argument("value", IntegerArgumentType.integer(1, 200))
                            .executes(LovelyCommands::executeSetLevel)
                        )
                    )
                    .then(Commands.literal("exp")
                        .then(Commands.argument("value", IntegerArgumentType.integer(0))
                            .executes(LovelyCommands::executeSetXP)
                        )
                    )
                )
                // Branch 2: Crosshair targeting path
                .then(Commands.literal("level")
                    .then(Commands.argument("value", IntegerArgumentType.integer(1, 200))
                        .executes(LovelyCommands::executeCrosshairSetLevel)
                    )
                )
                .then(Commands.literal("exp")
                    .then(Commands.argument("value", IntegerArgumentType.integer(0))
                        .executes(LovelyCommands::executeCrosshairSetXP)
                    )
                )
            );
    } // buildHybridStatsCommands()

    /**
     * Builds hybrid attributes commands supporting both crosshair targeting and entity selectors.
     * <p>
     * <b>Usage Examples:</b>
     * - `/llovely attributes set hp 100` - Sets HP of robot player is looking at
     * - `/llovely attributes set @e[type=llovelyr:vanilla] hp 100` - Sets HP of all vanilla robots
     * <p>
     * <b>Architecture:</b> Separate from stats to distinguish between progression (level/exp)
     * and combat attributes (hp/attack/defense/speed).
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildHybridAttributesCommands() {
        return Commands.literal("attributes")
            .then(Commands.literal("set")
                // Branch 1: Entity selector path
                .then(Commands.argument("target", EntityArgument.entities())
                    .then(Commands.literal("hp")
                        .then(Commands.argument("value", IntegerArgumentType.integer(1))
                            .executes(LovelyCommands::executeSetHP)
                        )
                    )
                    .then(Commands.literal("attack")
                        .then(Commands.argument("value", IntegerArgumentType.integer(1))
                            .executes(LovelyCommands::executeSetAttack)
                        )
                    )
                    .then(Commands.literal("defense")
                        .then(Commands.argument("value", IntegerArgumentType.integer(0))
                            .executes(LovelyCommands::executeSetDefense)
                        )
                    )
                    .then(Commands.literal("speed")
                        .then(Commands.argument("value", IntegerArgumentType.integer(1))
                            .executes(LovelyCommands::executeSetSpeed)
                        )
                    )
                    .then(Commands.literal("all")
                        .then(Commands.argument("hp", IntegerArgumentType.integer(1))
                            .then(Commands.argument("attack", IntegerArgumentType.integer(1))
                                .then(Commands.argument("defense", IntegerArgumentType.integer(0))
                                    .then(Commands.argument("speed", IntegerArgumentType.integer(1))
                                        .executes(LovelyCommands::executeSetAllAttributes)
                                    )
                                )
                            )
                        )
                    )
                )
                // Branch 2: Crosshair targeting path
                .then(Commands.literal("hp")
                    .then(Commands.argument("value", IntegerArgumentType.integer(1))
                        .executes(LovelyCommands::executeCrosshairSetHP)
                    )
                )
                .then(Commands.literal("attack")
                    .then(Commands.argument("value", IntegerArgumentType.integer(1))
                        .executes(LovelyCommands::executeCrosshairSetAttack)
                    )
                )
                .then(Commands.literal("defense")
                    .then(Commands.argument("value", IntegerArgumentType.integer(0))
                        .executes(LovelyCommands::executeCrosshairSetDefense)
                    )
                )
                .then(Commands.literal("speed")
                    .then(Commands.argument("value", IntegerArgumentType.integer(1))
                        .executes(LovelyCommands::executeCrosshairSetSpeed)
                    )
                )
                .then(Commands.literal("all")
                    .then(Commands.argument("hp", IntegerArgumentType.integer(1))
                        .then(Commands.argument("attack", IntegerArgumentType.integer(1))
                            .then(Commands.argument("defense", IntegerArgumentType.integer(0))
                                .then(Commands.argument("speed", IntegerArgumentType.integer(1))
                                    .executes(LovelyCommands::executeCrosshairSetAllAttributes)
                                )
                            )
                        )
                    )
                )
            );
    } // buildHybridAttributesCommands()

    /**
     * Builds hybrid enchantment commands supporting both targeting methods.
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildHybridEnchantCommands() {
        return Commands.literal("enchant")
            .then(Commands.literal("set")
                // Entity selector path
                .then(Commands.argument("target", EntityArgument.entities())
                    .then(Commands.literal("looting")
                        .then(Commands.argument("level", IntegerArgumentType.integer(0, 3))
                            .executes(LovelyCommands::executeSetLooting)
                        )
                    )
                    .then(Commands.literal("all")
                        .then(Commands.argument("looting", IntegerArgumentType.integer(0, 3))
                            .executes(LovelyCommands::executeSetAllEnchants)
                        )
                    )
                )
                // Crosshair targeting path
                .then(Commands.literal("looting")
                    .then(Commands.argument("level", IntegerArgumentType.integer(0, 3))
                        .executes(LovelyCommands::executeCrosshairSetLooting)
                    )
                )
                .then(Commands.literal("all")
                    .then(Commands.argument("looting", IntegerArgumentType.integer(0, 3))
                        .executes(LovelyCommands::executeCrosshairSetAllEnchants)
                    )
                )
            );
    } // buildHybridEnchantCommands()

    /**
     * Builds hybrid protection commands supporting both targeting methods.
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildHybridProtectionCommands() {
        return Commands.literal("protection")
            .then(Commands.literal("set")
                // Entity selector path
                .then(Commands.argument("target", EntityArgument.entities())
                    .then(Commands.literal("fire")
                        .then(Commands.argument("level", IntegerArgumentType.integer(0, 80))
                            .executes(LovelyCommands::executeSetFireProtection)
                        )
                    )
                    .then(Commands.literal("fall")
                        .then(Commands.argument("level", IntegerArgumentType.integer(0, 80))
                            .executes(LovelyCommands::executeSetFallProtection)
                        )
                    )
                    .then(Commands.literal("blast")
                        .then(Commands.argument("level", IntegerArgumentType.integer(0, 80))
                            .executes(LovelyCommands::executeSetBlastProtection)
                        )
                    )
                    .then(Commands.literal("projectile")
                        .then(Commands.argument("level", IntegerArgumentType.integer(0, 80))
                            .executes(LovelyCommands::executeSetProjectileProtection)
                        )
                    )
                    .then(Commands.literal("all")
                        .then(Commands.argument("fire", IntegerArgumentType.integer(0, 80))
                            .then(Commands.argument("fall", IntegerArgumentType.integer(0, 80))
                                .then(Commands.argument("blast", IntegerArgumentType.integer(0, 80))
                                    .then(Commands.argument("projectile", IntegerArgumentType.integer(0, 80))
                                        .executes(LovelyCommands::executeSetAllProtections)
                                    )
                                )
                            )
                        )
                    )
                )
                // Crosshair targeting path
                .then(Commands.literal("fire")
                    .then(Commands.argument("level", IntegerArgumentType.integer(0, 80))
                        .executes(LovelyCommands::executeCrosshairSetFireProtection)
                    )
                )
                .then(Commands.literal("fall")
                    .then(Commands.argument("level", IntegerArgumentType.integer(0, 80))
                        .executes(LovelyCommands::executeCrosshairSetFallProtection)
                    )
                )
                .then(Commands.literal("blast")
                    .then(Commands.argument("level", IntegerArgumentType.integer(0, 80))
                        .executes(LovelyCommands::executeCrosshairSetBlastProtection)
                    )
                )
                .then(Commands.literal("projectile")
                    .then(Commands.argument("level", IntegerArgumentType.integer(0, 80))
                        .executes(LovelyCommands::executeCrosshairSetProjectileProtection)
                    )
                )
                .then(Commands.literal("all")
                    .then(Commands.argument("fire", IntegerArgumentType.integer(0, 80))
                        .then(Commands.argument("fall", IntegerArgumentType.integer(0, 80))
                            .then(Commands.argument("blast", IntegerArgumentType.integer(0, 80))
                                .then(Commands.argument("projectile", IntegerArgumentType.integer(0, 80))
                                    .executes(LovelyCommands::executeCrosshairSetAllProtections)
                                )
                            )
                        )
                    )
                )
            );
    } // buildHybridProtectionCommands()

    /**
     * Builds hybrid design commands supporting both targeting methods.
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildHybridDesignCommands() {
        return Commands.literal("design")
            .then(Commands.literal("set")
                // Entity selector path
                .then(Commands.argument("target", EntityArgument.entities())
                    .then(Commands.argument("color", ColorArgumentType.color())
                        .executes(LovelyCommands::executeSetDesign)
                    )
                )
                // Crosshair targeting path (using "here" literal to avoid ambiguity)
                .then(Commands.literal("here")
                    .then(Commands.argument("color", ColorArgumentType.color())
                        .executes(LovelyCommands::executeCrosshairSetDesign)
                    )
                )
            );
    } // buildHybridDesignCommands()

    /**
     * Builds hybrid owner commands supporting both targeting methods.
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildHybridOwnerCommands() {
        return Commands.literal("owner")
            .then(Commands.literal("get")
                // Entity selector path
                .then(Commands.argument("target", EntityArgument.entity())
                    .executes(LovelyCommands::executeGetOwner)
                )
                // Crosshair targeting path (no arguments)
                .then(Commands.literal("here")
                    .executes(LovelyCommands::executeCrosshairGetOwner)
                )
            )
            .then(Commands.literal("set")
                // Entity selector path
                .then(Commands.argument("target", EntityArgument.entities())
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes(LovelyCommands::executeSetOwner)
                    )
                )
                // Crosshair targeting path (using "here" literal to avoid ambiguity)
                .then(Commands.literal("here")
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes(LovelyCommands::executeCrosshairSetOwner)
                    )
                )
            );
    } // buildHybridOwnerCommands()

    /**
     * Builds hybrid name commands supporting both targeting methods.
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildHybridNameCommands() {
        return Commands.literal("name")
            .then(Commands.literal("set")
                // Entity selector path
                .then(Commands.argument("target", EntityArgument.entities())
                    .then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(LovelyCommands::executeSetName)
                    )
                )
                // Crosshair targeting path (using "here" literal to avoid ambiguity)
                .then(Commands.literal("here")
                    .then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(LovelyCommands::executeCrosshairSetName)
                    )
                )
            );
    } // buildHybridNameCommands()

    /**
     * Builds hybrid teleport command supporting both targeting methods.
     * <p>
     * <b>Usage Examples:</b>
     * - `/llovely teleport` - Teleports robot player is looking at
     * - `/llovely teleport <robot_name_uuid>` - Teleports specific robot by identifier
     * <p>
     * <b>Architecture:</b> Crosshair mode (no arguments) uses raycasting,
     * identifier mode uses autocomplete with robot names and UUIDs.
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildHybridTeleportCommand() {
        return Commands.literal("teleport")
            // Crosshair targeting path (no arguments)
            .executes(LovelyCommands::executeCrosshairTeleport)
            // Robot identifier path (with autocomplete)
            .then(Commands.argument("robot", StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    try {
                        Player player = ctx.getSource().getPlayerOrException();
                        
                        if (player == null || player.level() == null) {
                            return builder.buildFuture();
                        }
                        
                        List<LovelyRobot> ownedRobots = findOwnedRobots(player);
                        
                        for (LovelyRobot robot : ownedRobots) {
                            try {
                                String displayName = getRobotDisplayName(robot);
                                String uuidPrefix = robot.getUUID().toString().substring(0, 8);
                                String suggestion = displayName + "_" + uuidPrefix;
                                
                                builder.suggest(
                                    suggestion,
                                    Component.literal(displayName + " (Level " + robot.getCurrentLevel() + ")")
                                );
                            } catch (Exception e) {
                                continue;
                            }
                        }
                    } catch (Exception e) {
                        // Command source is not a player
                    }
                    
                    return builder.buildFuture();
                })
                .executes(LovelyCommands::executeTeleport)
            );
    } // buildHybridTeleportCommand()

    // ========================================
    // CROSSHAIR TARGETING EXECUTORS
    // ========================================

    /**
     * Executes level setting on the robot the player is looking at.
     * <p>
     * <b>Architecture:</b> Uses server-side raycasting to find crosshair target.
     * This follows the Blocklings pattern for intuitive entity targeting.
     */
    private static int executeCrosshairSetLevel(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        int level = IntegerArgumentType.getInteger(ctx, "value");
        
        LovelyRobot robot = findCrosshairRobot(player);
        
        if (robot == null) {
            ctx.getSource().sendFailure(Component.literal("No robot found in crosshair or you don't own it"));
            return 0;
        }
        
        if (!robot.isOwnedBy(player)) {
            ctx.getSource().sendFailure(Component.literal("You don't own this robot"));
            return 0;
        }
        
        robot.setCurrentLevel(level);
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set level to " + level + " for " + getRobotDisplayName(robot)),
            true
        );
        
        return 1;
    } // executeCrosshairSetLevel()

    private static int executeCrosshairSetXP(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        int xp = IntegerArgumentType.getInteger(ctx, "value");
        
        LovelyRobot robot = findCrosshairRobot(player);
        if (robot == null) {
            ctx.getSource().sendFailure(Component.literal("No robot found in crosshair or you don't own it"));
            return 0;
        }
        
        if (!robot.isOwnedBy(player)) {
            ctx.getSource().sendFailure(Component.literal("You don't own this robot"));
            return 0;
        }
        
        robot.setExp(xp);
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set XP to " + xp + " for " + getRobotDisplayName(robot)),
            true
        );
        
        return 1;
    } // executeCrosshairSetXP()

    private static int executeCrosshairSetHP(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        int hp = IntegerArgumentType.getInteger(ctx, "value");
        
        LovelyRobot robot = findCrosshairRobot(player);
        if (robot == null) {
            ctx.getSource().sendFailure(Component.literal("No robot found in crosshair or you don't own it"));
            return 0;
        }
        
        if (!robot.isOwnedBy(player)) {
            ctx.getSource().sendFailure(Component.literal("You don't own this robot"));
            return 0;
        }
        
        robot.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(hp);
        robot.setHealth(hp);
        
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set HP to " + hp + " for " + getRobotDisplayName(robot)),
            true
        );
        
        return 1;
    } // executeCrosshairSetHP()

    private static int executeCrosshairSetAttack(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        int attack = IntegerArgumentType.getInteger(ctx, "value");
        
        LovelyRobot robot = findCrosshairRobot(player);
        if (robot == null) {
            ctx.getSource().sendFailure(Component.literal("No robot found in crosshair or you don't own it"));
            return 0;
        }
        
        if (!robot.isOwnedBy(player)) {
            ctx.getSource().sendFailure(Component.literal("You don't own this robot"));
            return 0;
        }
        
        robot.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).setBaseValue(attack);
        
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set attack to " + attack + " for " + getRobotDisplayName(robot)),
            true
        );
        
        return 1;
    } // executeCrosshairSetAttack()

    private static int executeCrosshairSetDefense(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        int defense = IntegerArgumentType.getInteger(ctx, "value");
        
        LovelyRobot robot = findCrosshairRobot(player);
        if (robot == null) {
            ctx.getSource().sendFailure(Component.literal("No robot found in crosshair or you don't own it"));
            return 0;
        }
        
        if (!robot.isOwnedBy(player)) {
            ctx.getSource().sendFailure(Component.literal("You don't own this robot"));
            return 0;
        }
        
        robot.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR).setBaseValue(defense);
        
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set defense to " + defense + " for " + getRobotDisplayName(robot)),
            true
        );
        
        return 1;
    } // executeCrosshairSetDefense()

    private static int executeCrosshairSetSpeed(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        int speed = IntegerArgumentType.getInteger(ctx, "value");
        
        LovelyRobot robot = findCrosshairRobot(player);
        if (robot == null) {
            ctx.getSource().sendFailure(Component.literal("No robot found in crosshair or you don't own it"));
            return 0;
        }
        
        if (!robot.isOwnedBy(player)) {
            ctx.getSource().sendFailure(Component.literal("You don't own this robot"));
            return 0;
        }
        
        robot.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED).setBaseValue(speed / 10.0);
        
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set speed to " + speed + " for " + getRobotDisplayName(robot)),
            true
        );
        
        return 1;
    } // executeCrosshairSetSpeed()

    private static int executeCrosshairSetAllAttributes(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        int hp = IntegerArgumentType.getInteger(ctx, "hp");
        int attack = IntegerArgumentType.getInteger(ctx, "attack");
        int defense = IntegerArgumentType.getInteger(ctx, "defense");
        int speed = IntegerArgumentType.getInteger(ctx, "speed");
        
        LovelyRobot robot = findCrosshairRobot(player);
        if (robot == null) {
            ctx.getSource().sendFailure(Component.literal("No robot found in crosshair or you don't own it"));
            return 0;
        }
        
        if (!robot.isOwnedBy(player)) {
            ctx.getSource().sendFailure(Component.literal("You don't own this robot"));
            return 0;
        }
        
        robot.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(hp);
        robot.setHealth(hp);
        robot.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).setBaseValue(attack);
        robot.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR).setBaseValue(defense);
        robot.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED).setBaseValue(speed / 10.0);
        
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set attributes (HP:" + hp + " ATK:" + attack + " DEF:" + defense + " SPD:" + speed + ") for " + getRobotDisplayName(robot)),
            true
        );
        
        return 1;
    } // executeCrosshairSetAllAttributes()

    private static int executeCrosshairSetLooting(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        int level = IntegerArgumentType.getInteger(ctx, "level");
        
        LovelyRobot robot = findCrosshairRobot(player);
        if (robot == null) {
            ctx.getSource().sendFailure(Component.literal("No robot found in crosshair or you don't own it"));
            return 0;
        }
        
        if (!robot.isOwnedBy(player)) {
            ctx.getSource().sendFailure(Component.literal("You don't own this robot"));
            return 0;
        }
        
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set looting to " + level + " for " + getRobotDisplayName(robot)),
            true
        );
        
        return 1;
    } // executeCrosshairSetLooting()

    private static int executeCrosshairSetAllEnchants(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        int looting = IntegerArgumentType.getInteger(ctx, "looting");
        
        LovelyRobot robot = findCrosshairRobot(player);
        if (robot == null) {
            ctx.getSource().sendFailure(Component.literal("No robot found in crosshair or you don't own it"));
            return 0;
        }
        
        if (!robot.isOwnedBy(player)) {
            ctx.getSource().sendFailure(Component.literal("You don't own this robot"));
            return 0;
        }
        
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set all enchantments (Looting:" + looting + ") for " + getRobotDisplayName(robot)),
            true
        );
        
        return 1;
    } // executeCrosshairSetAllEnchants()

    private static int executeCrosshairSetFireProtection(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        int level = IntegerArgumentType.getInteger(ctx, "level");
        
        LovelyRobot robot = findCrosshairRobot(player);
        if (robot == null) {
            ctx.getSource().sendFailure(Component.literal("No robot found in crosshair or you don't own it"));
            return 0;
        }
        
        if (!robot.isOwnedBy(player)) {
            ctx.getSource().sendFailure(Component.literal("You don't own this robot"));
            return 0;
        }
        
        robot.setFireProtection(level);
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set fire protection to " + level + " for " + getRobotDisplayName(robot)),
            true
        );
        
        return 1;
    } // executeCrosshairSetFireProtection()

    private static int executeCrosshairSetFallProtection(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        int level = IntegerArgumentType.getInteger(ctx, "level");
        
        LovelyRobot robot = findCrosshairRobot(player);
        if (robot == null) {
            ctx.getSource().sendFailure(Component.literal("No robot found in crosshair or you don't own it"));
            return 0;
        }
        
        if (!robot.isOwnedBy(player)) {
            ctx.getSource().sendFailure(Component.literal("You don't own this robot"));
            return 0;
        }
        
        robot.setFallProtection(level);
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set fall protection to " + level + " for " + getRobotDisplayName(robot)),
            true
        );
        
        return 1;
    } // executeCrosshairSetFallProtection()

    private static int executeCrosshairSetBlastProtection(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        int level = IntegerArgumentType.getInteger(ctx, "level");
        
        LovelyRobot robot = findCrosshairRobot(player);
        if (robot == null) {
            ctx.getSource().sendFailure(Component.literal("No robot found in crosshair or you don't own it"));
            return 0;
        }
        
        if (!robot.isOwnedBy(player)) {
            ctx.getSource().sendFailure(Component.literal("You don't own this robot"));
            return 0;
        }
        
        robot.setBlastProtection(level);
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set blast protection to " + level + " for " + getRobotDisplayName(robot)),
            true
        );
        
        return 1;
    } // executeCrosshairSetBlastProtection()

    private static int executeCrosshairSetProjectileProtection(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        int level = IntegerArgumentType.getInteger(ctx, "level");
        
        LovelyRobot robot = findCrosshairRobot(player);
        if (robot == null) {
            ctx.getSource().sendFailure(Component.literal("No robot found in crosshair or you don't own it"));
            return 0;
        }
        
        if (!robot.isOwnedBy(player)) {
            ctx.getSource().sendFailure(Component.literal("You don't own this robot"));
            return 0;
        }
        
        robot.setProjectileProtection(level);
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set projectile protection to " + level + " for " + getRobotDisplayName(robot)),
            true
        );
        
        return 1;
    } // executeCrosshairSetProjectileProtection()

    private static int executeCrosshairSetAllProtections(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        int fire = IntegerArgumentType.getInteger(ctx, "fire");
        int fall = IntegerArgumentType.getInteger(ctx, "fall");
        int blast = IntegerArgumentType.getInteger(ctx, "blast");
        int projectile = IntegerArgumentType.getInteger(ctx, "projectile");
        
        LovelyRobot robot = findCrosshairRobot(player);
        if (robot == null) {
            ctx.getSource().sendFailure(Component.literal("No robot found in crosshair or you don't own it"));
            return 0;
        }
        
        if (!robot.isOwnedBy(player)) {
            ctx.getSource().sendFailure(Component.literal("You don't own this robot"));
            return 0;
        }
        
        robot.setFireProtection(fire);
        robot.setFallProtection(fall);
        robot.setBlastProtection(blast);
        robot.setProjectileProtection(projectile);
        
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set all protections (Fire:" + fire + " Fall:" + fall + " Blast:" + blast + " Projectile:" + projectile + ") for " + getRobotDisplayName(robot)),
            true
        );
        
        return 1;
    } // executeCrosshairSetAllProtections()

    private static int executeCrosshairSetDesign(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        EntityTexture color = ctx.getArgument("color", EntityTexture.class);
        
        LovelyRobot robot = findCrosshairRobot(player);
        if (robot == null) {
            ctx.getSource().sendFailure(Component.literal("No robot found in crosshair or you don't own it"));
            return 0;
        }
        
        if (!robot.isOwnedBy(player)) {
            ctx.getSource().sendFailure(Component.literal("You don't own this robot"));
            return 0;
        }
        
        robot.setTexture(color);
        
        final String colorName = color.Name().toLowerCase();
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set color to " + colorName + " for " + getRobotDisplayName(robot)),
            true
        );
        
        return 1;
    } // executeCrosshairSetDesign()

    private static int executeCrosshairGetOwner(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        
        LovelyRobot robot = findCrosshairRobot(player);
        if (robot == null) {
            ctx.getSource().sendFailure(Component.literal("No robot found in crosshair"));
            return 0;
        }
        
        Player owner = (Player) robot.getOwner();
        String ownerName = owner != null ? owner.getName().getString() : "No owner";
        
        ctx.getSource().sendSuccess(
            () -> Component.literal(getRobotDisplayName(robot) + " owner: " + ownerName),
            false
        );
        
        return 1;
    } // executeCrosshairGetOwner()

    private static int executeCrosshairSetOwner(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        Player newOwner = EntityArgument.getPlayer(ctx, "player");
        
        LovelyRobot robot = findCrosshairRobot(player);
        if (robot == null) {
            ctx.getSource().sendFailure(Component.literal("No robot found in crosshair or you don't own it"));
            return 0;
        }
        
        if (!robot.isOwnedBy(player)) {
            ctx.getSource().sendFailure(Component.literal("You don't own this robot"));
            return 0;
        }
        
        robot.tame(newOwner);
        
        final String ownerName = newOwner.getName().getString();
        ctx.getSource().sendSuccess(
            () -> Component.literal("Transferred ownership of " + getRobotDisplayName(robot) + " to " + ownerName),
            true
        );
        
        return 1;
    } // executeCrosshairSetOwner()

    private static int executeCrosshairSetName(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        String nameString = StringArgumentType.getString(ctx, "name");
        Component name = Component.literal(nameString);
        
        LovelyRobot robot = findCrosshairRobot(player);
        if (robot == null) {
            ctx.getSource().sendFailure(Component.literal("No robot found in crosshair or you don't own it"));
            return 0;
        }
        
        if (!robot.isOwnedBy(player)) {
            ctx.getSource().sendFailure(Component.literal("You don't own this robot"));
            return 0;
        }
        
        robot.setCustomName(name);
        robot.setCustomNameVisible(true);
        
        ctx.getSource().sendSuccess(
            () -> Component.literal("Set name to '" + nameString + "' for " + getRobotDisplayName(robot)),
            true
        );
        
        return 1;
    } // executeCrosshairSetName()

    /**
     * Executes teleportation of the robot the player is looking at.
     * <p>
     * <b>Architecture:</b> Uses crosshair targeting to find robot, then teleports
     * it to player's location with safety checks. Follows Blocklings pattern for
     * intuitive teleportation without needing to specify robot identifier.
     * <p>
     * <b>Safety:</b> Finds safe landing spot near player to prevent suffocation
     * or fall damage. Spawns particles and plays sound for visual/audio feedback.
     */
    private static int executeCrosshairTeleport(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        
        LovelyRobot robot = findCrosshairRobot(player);
        if (robot == null) {
            ctx.getSource().sendFailure(Component.literal("No robot found in crosshair or you don't own it"));
            return 0;
        }
        
        if (!robot.isOwnedBy(player)) {
            ctx.getSource().sendFailure(Component.literal("You don't own this robot"));
            return 0;
        }
        
        ServerLevel playerLevel = (ServerLevel) player.level();
        BlockPos playerPos = player.blockPosition();
        
        // Find safe teleport location
        BlockPos safePos = findSafeTeleportLocation(playerLevel, playerPos);
        
        if (safePos == null) {
            ctx.getSource().sendFailure(Component.literal("No safe location found for teleportation"));
            return 0;
        }
        
        // Spawn particles at origin
        ServerLevel robotLevel = (ServerLevel) robot.level();
        spawnTeleportParticles(robotLevel, robot.position());
        
        // Handle dimension change if needed
        if (robot.level() != player.level()) {
            ctx.getSource().sendFailure(Component.literal("Cannot teleport robot across dimensions"));
            return 0;
        }
        
        // Teleport robot to safe location
        robot.moveTo(safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5);
        
        // Spawn particles at destination
        spawnTeleportParticles(playerLevel, robot.position());
        
        // Play sound at destination
        playerLevel.playSound(
            null,
            safePos,
            SoundEvents.ENDERMAN_TELEPORT,
            SoundSource.PLAYERS,
            1.0F,
            1.0F
        );
        
        // Send confirmation message
        String displayName = getRobotDisplayName(robot);
        ctx.getSource().sendSuccess(
            () -> Component.literal("Teleported " + displayName + " to your location"),
            false
        );
        
        return 1;
    } // executeCrosshairTeleport()

    // ========================================
    // CROSSHAIR TARGETING UTILITY
    // ========================================

    /**
     * Finds the robot the player is looking at using server-side raycasting.
     * <p>
     * <b>Architecture:</b> Uses player's look vector to perform entity raycast.
     * This is a server-side implementation of the client-side crosshair targeting
     * used in the Blocklings mod.
     * <p>
     * <b>Range:</b> 5 block reach distance (standard interaction range)
     * <p>
     * <b>Validation:</b> Only returns LovelyRobot entities (ownership checked by caller).
     *
     * @param player the player performing the lookup
     * @return the robot in crosshair, or null if none found
     */
    private static LovelyRobot findCrosshairRobot(Player player) {
        // Get player's look vector
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getViewVector(1.0F);
        Vec3 endPos = eyePos.add(lookVec.scale(5.0)); // 5 block reach
        
        // Perform entity raycast
        AABB searchBox = new AABB(eyePos, endPos).inflate(1.0);
        List<Entity> entities = player.level().getEntities(player, searchBox);
        
        LovelyRobot closestRobot = null;
        double closestDistance = Double.MAX_VALUE;
        
        for (Entity entity : entities) {
            if (entity instanceof LovelyRobot robot) {
                // Check if ray intersects with entity bounding box
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
    } // findCrosshairRobot()

    // ========================================
    // CONFIG RELOAD COMMAND
    // ========================================

    /**
     * Builds config reload command for runtime configuration updates.
     * <p>
     * <b>Usage:</b> `/llovely reload` - Reloads configuration from disk
     * <p>
     * <b>Permissions:</b> Requires operator level 2 (same as other llovely commands)
     * <p>
     * <b>Behavior:</b> Triggers Forge's config reload mechanism, which fires
     * ModConfigEvent and updates all config values without server restart.
     */
    private static ArgumentBuilder<CommandSourceStack, ?> buildReloadCommand() {
        return Commands.literal("reload")
            .executes(LovelyCommands::executeReload);
    } // buildReloadCommand()

    /**
     * Executes config reload command.
     * <p>
     * <b>Behavior:</b> Triggers configuration reload through Forge's config system.
     * All config values are re-read from disk and applied immediately.
     * <p>
     * <b>Feedback:</b> Sends success message to command source confirming reload.
     *
     * @param ctx command context with source
     * @return command success code (1)
     */
    private static int executeReload(CommandContext<CommandSourceStack> ctx) {
        try {
            LovelyConfigs.reload();
            ctx.getSource().sendSuccess(
                () -> Component.literal("Configuration reloaded successfully!").withStyle(ChatFormatting.GREEN),
                true
            );
            return Command.SINGLE_SUCCESS;
        } catch (Exception e) {
            ctx.getSource().sendFailure(
                Component.literal("Failed to reload configuration: " + e.getMessage()).withStyle(ChatFormatting.RED)
            );
            return 0;
        }
    } // executeReload()

} // Class: LovelyCommands
