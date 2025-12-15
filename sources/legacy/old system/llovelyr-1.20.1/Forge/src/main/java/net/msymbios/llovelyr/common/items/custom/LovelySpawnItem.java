package net.msymbios.llovelyr.common.items.custom;

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
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.msymbios.llovelyr.common.entity.internal.InternalParticle;
import net.msymbios.llovelyr.common.shared.LovelyIdentifier;
import net.msymbios.llovelyr.source.entity.common.LovelyRobotEntity;
import net.msymbios.llovelyr.framework.entity.enums.EntityTexture;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static net.msymbios.llovelyr.common.items.utils.TooltipUtils.*;

/**
 * Spawn egg for summoning robots with NBT-stored customization.
 * <p>
 * <b>Architecture:</b> Extends ForgeSpawnEggItem to leverage Minecraft's spawn
 * egg mechanics while adding robot-specific initialization from NBT data.
 * <p>
 * <b>Spawn Behavior:</b> Right-click block to place robot, right-click water
 * to spawn in liquid, or use on spawner to configure it. Transfers NBT data
 * (name, color, level, protections) to spawned entity.
 */
public class LovelySpawnItem extends ForgeSpawnEggItem {

    // -- Constructor --

    public LovelySpawnItem(Supplier<? extends EntityType<? extends Mob>> type, Properties properties) {
        super(type, 0xFFFFFF, 0xFFFFFF, properties);
    } // Constructor: LovelySpawnItem()

    // -- Inherited Methods --

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (stack.hasTag()) {
            CompoundTag nbt = stack.getOrCreateTag();
            addNameTooltip(tooltip, nbt);
            addOwnerTooltip(tooltip, nbt);
            addColorTooltip(tooltip, nbt);
            addLevelTooltip(tooltip, nbt);
        } else {
            CompoundTag compound = stack.getOrCreateTag();
            compound.putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.RANDOM.getId());
            compound.putInt(LovelyIdentifier.STAT_LEVEL, 0);
            stack.setTag(compound);
        }
    } // appendHoverText()

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemStack);
        } else if (!(level instanceof ServerLevel)) {
            return InteractionResultHolder.success(itemStack);
        } else {
            BlockPos blockPos = blockHitResult.getBlockPos();
            if (!(level.getBlockState(blockPos).getBlock() instanceof LiquidBlock)) {
                return InteractionResultHolder.pass(itemStack);
            } else if (level.mayInteract(player, blockPos) && player.mayUseItemAt(blockPos, blockHitResult.getDirection(), itemStack)) {
                // Check registry spawn limit before spawning
                net.msymbios.llovelyr.framework.registry.OwnerRobotRegistry registry = 
                    net.msymbios.llovelyr.lib.registry.RobotRegistryManager.getRegistry((ServerLevel) level);
                
                if (!registry.canSpawnRobot(player.getUUID(), net.msymbios.llovelyr.source.LovelyConfigs.OwnerMaxRobotNum)) {
                    // Display error message to player
                    player.displayClientMessage(
                        Component.literal("Cannot spawn robot: limit of " + net.msymbios.llovelyr.source.LovelyConfigs.OwnerMaxRobotNum + " reached (" +
                            registry.getRobotsForOwner(player.getUUID()).size() + " active)"),
                        true
                    );
                    return InteractionResultHolder.fail(itemStack);
                }
                
                EntityType<?> entityType = this.getType(itemStack.getTag());
                Mob entity = (Mob) entityType.spawn((ServerLevel) level, itemStack, player, blockPos, MobSpawnType.SPAWN_EGG, false, false);
                if (entity == null) {
                    return InteractionResultHolder.pass(itemStack);
                } else {
                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                    level.gameEvent(player, GameEvent.ENTITY_PLACE, entity.position());

                    if (entity instanceof LovelyRobotEntity robotEntity) {
                        robotEntity.handleTame(player);
                        initialize(itemStack.getOrCreateTag(), robotEntity);
                    }
                    return InteractionResultHolder.consume(itemStack);
                }
            } else {
                return InteractionResultHolder.fail(itemStack);
            }
        }
    } // use()

    @Override
    public InteractionResult useOn(UseOnContext context) {
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
                EntityType<?> entityType = this.getType(itemStack.getTag());
                spawner.setEntityId(entityType, level.getRandom());
                level.sendBlockUpdated(blockPos, blockState, blockState, 3);
                level.gameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, blockPos);
                itemStack.shrink(1);
                return InteractionResult.CONSUME;
            } else {
                BlockPos blockPos2;
                if (blockState.getCollisionShape(level, blockPos).isEmpty()) {
                    blockPos2 = blockPos;
                } else {
                    blockPos2 = blockPos.relative(direction);
                }

                // Check registry spawn limit before spawning
                Player player = context.getPlayer();
                if (player != null) {
                    net.msymbios.llovelyr.framework.registry.OwnerRobotRegistry registry = 
                        net.msymbios.llovelyr.lib.registry.RobotRegistryManager.getRegistry((ServerLevel) level);
                    
                    if (!registry.canSpawnRobot(player.getUUID(), net.msymbios.llovelyr.source.LovelyConfigs.OwnerMaxRobotNum)) {
                        // Display error message to player
                        player.displayClientMessage(
                            Component.literal("Cannot spawn robot: limit of " + net.msymbios.llovelyr.source.LovelyConfigs.OwnerMaxRobotNum + " reached (" +
                                registry.getRobotsForOwner(player.getUUID()).size() + " active)"),
                            true
                        );
                        return InteractionResult.FAIL;
                    }
                }
                
                EntityType<?> entityType = this.getType(itemStack.getTag());
                Mob entity = (Mob) entityType.spawn((ServerLevel) level, itemStack, context.getPlayer(), blockPos2, MobSpawnType.SPAWN_EGG, true, !Objects.equals(blockPos, blockPos2) && direction == Direction.UP);
                if (entity != null) {
                    itemStack.shrink(1);
                    level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);

                    if (entity instanceof LovelyRobotEntity robotEntity) {
                        robotEntity.handleTame(context.getPlayer());
                        initialize(itemStack.getOrCreateTag(), robotEntity);
                    }
                }

                return InteractionResult.CONSUME;
            }
        }
    } // useOn()

    // -- Custom Methods --

    /**
     * Transfers NBT data from spawn egg to spawned robot entity.
     * <p>
     * <b>Data Transfer:</b> Applies custom name, texture, level, experience,
     * and protection enchantments from item NBT to entity state.
     *
     * @param dataNBT the NBT compound from spawn egg
     * @param entity the spawned robot entity to initialize
     */
    private void initialize(CompoundTag dataNBT, LovelyRobotEntity entity) {
        if (!dataNBT.getString(LovelyIdentifier.STAT_CUSTOM_NAME).isEmpty()) entity.setCustomName(Component.literal(dataNBT.getString(LovelyIdentifier.STAT_CUSTOM_NAME)));
        if (dataNBT.getInt(LovelyIdentifier.STAT_COLOR) != EntityTexture.RANDOM.getId()) entity.setTexture(dataNBT.getInt(LovelyIdentifier.STAT_COLOR));

        if (dataNBT.getInt(LovelyIdentifier.STAT_LEVEL) > 0) entity.setCurrentLevel(dataNBT.getInt(LovelyIdentifier.STAT_LEVEL));
        if (dataNBT.getInt(LovelyIdentifier.STAT_EXP) > 0) entity.setExp(dataNBT.getInt(LovelyIdentifier.STAT_EXP));
        if (dataNBT.contains(LovelyIdentifier.STAT_HP)) entity.setCurrentHealthValue(dataNBT.getFloat(LovelyIdentifier.STAT_HP));

        if (dataNBT.getInt(LovelyIdentifier.STAT_FIRE_PROTECTION) > 0) entity.setFireProtection(dataNBT.getInt(LovelyIdentifier.STAT_FIRE_PROTECTION));
        if (dataNBT.getInt(LovelyIdentifier.STAT_FALL_PROTECTION) > 0) entity.setFallProtection(dataNBT.getInt(LovelyIdentifier.STAT_FALL_PROTECTION));
        if (dataNBT.getInt(LovelyIdentifier.STAT_BLAST_PROTECTION) > 0) entity.setBlastProtection(dataNBT.getInt(LovelyIdentifier.STAT_BLAST_PROTECTION));
        if (dataNBT.getInt(LovelyIdentifier.STAT_PROJECTILE_PROTECTION) > 0) entity.setProjectileProtection(dataNBT.getInt(LovelyIdentifier.STAT_PROJECTILE_PROTECTION));
    } // initialize()

} // Class: LovelySpawnItem