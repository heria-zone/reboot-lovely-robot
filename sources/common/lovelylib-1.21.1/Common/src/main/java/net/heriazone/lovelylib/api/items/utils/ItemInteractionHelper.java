package net.heriazone.lovelylib.api.items.utils;

import net.heriazone.lovelylib.common.entity.LovelyRobotEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static net.heriazone.lovelylib.api.items.utils.TooltipUtils.*;

/**
 * Provides common interaction logic for spawn items across all loaders.
 * <p>
 * <b>Architecture:</b> Extracts duplicated interaction patterns from loader-specific
 * spawn item implementations. Handles tooltip generation, liquid spawning, block
 * placement, and spawner configuration consistently across Fabric, Forge, and NeoForge.
 * <p>
 * <b>Design Decision:</b> Static utility methods avoid inheritance complexity while
 * enabling thin wrapper pattern in loader modules. Each method corresponds to a
 * specific interaction type with consistent behavior.
 * <p>
 * <b>Entity Type Abstraction:</b> Uses functional interface to retrieve entity type,
 * allowing each loader to provide its own mechanism (direct reference, supplier, etc.).
 */
public class ItemInteractionHelper {

    // -- Functional Interfaces --

    /**
     * Functional interface for retrieving entity type from ItemStack.
     * <p>
     * <b>Loader Abstraction:</b> Different loaders have different mechanisms
     * for storing and retrieving entity types from spawn eggs.
     */
    @FunctionalInterface
    public interface EntityTypeProvider {
        EntityType<?> getEntityType(ItemStack itemStack);
    }

    /**
     * Functional interface for performing player POV hit result.
     * <p>
     * <b>Access Abstraction:</b> Allows access to protected getPlayerPOVHitResult
     * method from the calling item context.
     */
    @FunctionalInterface
    public interface HitResultProvider {
        BlockHitResult getPlayerPOVHitResult(Level level, Player player, ClipContext.Fluid fluidMode);
    }

    // -- Tooltip Generation --

    /**
     * Generates tooltip text for spawn items with custom data display.
     * <p>
     * <b>Data Component Integration:</b> Extracts custom data using ItemSpawnHelper
     * and delegates to TooltipUtils for consistent formatting across loaders.
     * <p>
     * <b>Initialization:</b> Automatically initializes default data if not present,
     * ensuring spawn eggs always have valid component data.
     *
     * @param stack the ItemStack to generate tooltip for
     * @param context tooltip context (nullable)
     * @param tooltip mutable list to append tooltip lines to
     * @param flag tooltip flag indicating detail level
     */
    public static void appendHoverText(ItemStack stack, @Nullable Item.TooltipContext context,
                                       List<Component> tooltip, TooltipFlag flag) {
        // Check if stack has custom data component
        CompoundTag nbt = ItemSpawnHelper.extractCustomData(stack);
        if (nbt != null) {
            addNameTooltip(tooltip, nbt);
            addOwnerTooltip(tooltip, nbt);
            addColorTooltip(tooltip, nbt);
            addLevelTooltip(tooltip, nbt);
        } else {
            // Initialize default data if not present
            ItemSpawnHelper.initializeDefaultData(stack);
        }
    } // appendHoverText()

    // -- Liquid Spawning --

    /**
     * Handles right-click interaction for spawning entities in liquid blocks.
     * <p>
     * <b>Spawn Validation:</b> Checks registry limits before spawning and provides
     * user feedback on failure. Validates liquid block placement and player permissions.
     * <p>
     * <b>Entity Initialization:</b> Transfers custom data from spawn item to entity
     * and handles taming for robot entities.
     *
     * @param level the level where interaction occurs
     * @param player the player performing the interaction
     * @param hand the hand used for interaction
     * @param itemStack the spawn item being used
     * @param entityTypeProvider function to get entity type from ItemStack
     * @param hitResultProvider function to get player POV hit result
     * @return interaction result indicating success, failure, or pass
     */
    public static InteractionResultHolder<ItemStack> handleLiquidSpawn(Level level, Player player,
                                                                       InteractionHand hand, ItemStack itemStack,
                                                                       EntityTypeProvider entityTypeProvider,
                                                                       HitResultProvider hitResultProvider) {
        BlockHitResult blockHitResult = hitResultProvider.getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemStack);
        } else if (!(level instanceof ServerLevel)) {
            return InteractionResultHolder.success(itemStack);
        } else {
            BlockPos blockPos = blockHitResult.getBlockPos();
            if (!(level.getBlockState(blockPos).getBlock() instanceof LiquidBlock)) {
                return InteractionResultHolder.pass(itemStack);
            } else if (level.mayInteract(player, blockPos) &&
                    player.mayUseItemAt(blockPos, blockHitResult.getDirection(), itemStack)) {

                // Check registry spawn limit before spawning
                if (!ItemSpawnHelper.canSpawnRobot((ServerLevel) level, player)) {
                    return InteractionResultHolder.fail(itemStack);
                }

                // Get entity type from ItemStack
                EntityType<?> entityType = entityTypeProvider.getEntityType(itemStack);

                // Get custom data for entity initialization
                CompoundTag customTag = ItemSpawnHelper.extractCustomData(itemStack);

                Mob entity = (Mob) entityType.spawn((ServerLevel) level, itemStack, player, blockPos,
                        MobSpawnType.SPAWN_EGG, false, false);
                if (entity == null) {
                    return InteractionResultHolder.pass(itemStack);
                } else {
                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }

                    player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
                    level.gameEvent(player, GameEvent.ENTITY_PLACE, entity.position());

                    if (entity instanceof LovelyRobotEntity robotEntity) {
                        robotEntity.handleTame(player);
                        ItemSpawnHelper.initializeEntityFromData(customTag, robotEntity);
                    }
                    return InteractionResultHolder.consume(itemStack);
                }
            } else {
                return InteractionResultHolder.fail(itemStack);
            }
        }
    } // handleLiquidSpawn()

    // -- Block Placement --

    /**
     * Handles right-click on block interaction for entity spawning and spawner configuration.
     * <p>
     * <b>Spawner Integration:</b> Configures mob spawners when used on spawner blocks,
     * setting entity type and triggering block updates.
     * <p>
     * <b>Surface Spawning:</b> Places entities on solid surfaces with collision detection
     * and proper positioning relative to clicked face.
     *
     * @param context use context containing level, position, player, and item information
     * @param entityTypeProvider function to get entity type from ItemStack
     * @return interaction result indicating success, failure, or consumption
     */
    public static InteractionResult handleBlockPlacement(UseOnContext context,
                                                         EntityTypeProvider entityTypeProvider) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        } else {
            ItemStack itemStack = context.getItemInHand();
            BlockPos blockPos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockState blockState = level.getBlockState(blockPos);
            BlockEntity blockEntity = level.getBlockEntity(blockPos);

            if (blockEntity instanceof SpawnerBlockEntity spawner) {
                // Configure spawner with entity type
                EntityType<?> entityType = entityTypeProvider.getEntityType(itemStack);
                spawner.setEntityId(entityType, level.getRandom());
                level.sendBlockUpdated(blockPos, blockState, blockState, 3);
                level.gameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, blockPos);
                itemStack.shrink(1);
                return InteractionResult.CONSUME;
            } else {
                // Determine spawn position based on collision
                BlockPos spawnPos;
                if (blockState.getCollisionShape(level, blockPos).isEmpty()) {
                    spawnPos = blockPos;
                } else {
                    spawnPos = blockPos.relative(direction);
                }

                // Check registry spawn limit before spawning
                Player player = context.getPlayer();
                if (player != null && !ItemSpawnHelper.canSpawnRobot((ServerLevel) level, player)) {
                    return InteractionResult.FAIL;
                }

                // Get entity type from ItemStack
                EntityType<?> entityType = entityTypeProvider.getEntityType(itemStack);

                // Get custom data for entity initialization
                CompoundTag customTag = ItemSpawnHelper.extractCustomData(itemStack);

                Mob entity = (Mob) entityType.spawn((ServerLevel) level, itemStack, context.getPlayer(),
                        spawnPos, MobSpawnType.SPAWN_EGG, true,
                        !Objects.equals(blockPos, spawnPos) && direction == Direction.UP);
                if (entity != null) {
                    itemStack.shrink(1);
                    level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);

                    if (entity instanceof LovelyRobotEntity robotEntity) {
                        robotEntity.handleTame(context.getPlayer());
                        ItemSpawnHelper.initializeEntityFromData(customTag, robotEntity);
                    }
                }

                return InteractionResult.CONSUME;
            }
        }
    } // handleBlockPlacement()

} // Class: ItemInteractionHelper