package net.msymbios.llovelyr.common.items.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.msymbios.llovelyr.common.shared.LovelyIdentifier;
import net.msymbios.llovelyr.source.entity.common.LovelyRobot;
import net.msymbios.llovelyr.framework.entity.enums.EntityTexture;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static net.msymbios.llovelyr.common.items.utils.TooltipUtils.*;

/**
 * Spawn egg for summoning robots with NBT-stored customization.
 * <p>
 * <b>Architecture:</b> Extends SpawnEggItem to leverage Minecraft's spawn
 * egg mechanics while adding robot-specific initialization from NBT data.
 * <p>
 * <b>Spawn Behavior:</b> Right-click block to place robot, right-click water
 * to spawn in liquid, or use on spawner to configure it. Transfers NBT data
 * (name, color, level, protections) to spawned entity.
 */
public class LovelySpawnItem extends SpawnEggItem {

    // -- Constructor --

    public LovelySpawnItem(EntityType<? extends MobEntity> type, Settings settings) {
        super(type, 0xFFFFFF, 0xFFFFFF, settings);
    } // Constructor: LovelySpawnItem()

    // -- Inherited Methods --

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (stack.hasNbt()) {
            NbtCompound nbt = stack.getOrCreateNbt();
            addNameTooltip(tooltip, nbt);
            addOwnerTooltip(tooltip, nbt);
            addColorTooltip(tooltip, nbt);
            addLevelTooltip(tooltip, nbt);
        } else {
            NbtCompound compound = stack.getOrCreateNbt();
            compound.putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.RANDOM.getId());
            compound.putInt(LovelyIdentifier.STAT_LEVEL, 0);
            stack.setNbt(compound);
        }
    } // appendTooltip()

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult blockHitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
        if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return TypedActionResult.pass(itemStack);
        } else if (!(world instanceof ServerWorld)) {
            return TypedActionResult.success(itemStack);
        } else {
            BlockPos blockPos = blockHitResult.getBlockPos();
            if (!(world.getBlockState(blockPos).getBlock() instanceof FluidBlock)) {
                return TypedActionResult.pass(itemStack);
            } else if (world.canPlayerModifyAt(user, blockPos) && user.canPlaceOn(blockPos, blockHitResult.getSide(), itemStack)) {
                EntityType<?> entityType = this.getEntityType(itemStack.getNbt());
                MobEntity entity = (MobEntity) entityType.spawnFromItemStack((ServerWorld) world, itemStack, user, blockPos, SpawnReason.SPAWN_EGG, false, false);
                if (entity == null) {
                    return TypedActionResult.pass(itemStack);
                } else {
                    if (!user.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                    }

                    user.incrementStat(Stats.USED.getOrCreateStat(this));
                    world.emitGameEvent(user, GameEvent.ENTITY_PLACE, entity.getPos());

                    if (entity instanceof LovelyRobot robotEntity) {
                        // Set ownership without taming particles (spawning, not taming)
                        robotEntity.setOwner(user);
                        robotEntity.setSitting(false);
                        initialize(itemStack.getOrCreateNbt(), robotEntity);
                        
                        // Spawn POOF particles (spawn effect)
                        net.msymbios.llovelyr.common.entity.internal.InternalParticle.Poof(robotEntity);
                        
                        // Play spawn sound effect (totem activation sound) - volume scales with entity size
                        float volume = (float) Math.max(0.5F, Math.min(2.0F, entity.getWidth() * entity.getHeight()));
                        world.playSound(
                            null, 
                            entity.getBlockPos(), 
                            net.minecraft.sound.SoundEvents.ITEM_TOTEM_USE, 
                            net.minecraft.sound.SoundCategory.NEUTRAL, 
                            volume, 
                            1.2F
                        );
                    }
                    return TypedActionResult.consume(itemStack);
                }
            } else {
                return TypedActionResult.fail(itemStack);
            }
        }
    } // use()

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!(world instanceof ServerWorld)) {
            return ActionResult.SUCCESS;
        } else {
            ItemStack itemStack = context.getStack();
            BlockPos blockPos = context.getBlockPos();
            Direction direction = context.getSide();
            BlockState blockState = world.getBlockState(blockPos);
            BlockEntity blockEntity = world.getBlockEntity(blockPos);

            if (blockEntity instanceof MobSpawnerBlockEntity spawner) {
                EntityType<?> entityType = this.getEntityType(itemStack.getNbt());
                spawner.setEntityType(entityType, world.getRandom());
                world.updateListeners(blockPos, blockState, blockState, 3);
                world.emitGameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, blockPos);
                itemStack.decrement(1);
                return ActionResult.CONSUME;
            } else {
                BlockPos blockPos2;
                if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
                    blockPos2 = blockPos;
                } else {
                    blockPos2 = blockPos.offset(direction);
                }

                EntityType<?> entityType = this.getEntityType(itemStack.getNbt());
                MobEntity entity = (MobEntity) entityType.spawnFromItemStack((ServerWorld) world, itemStack, context.getPlayer(), blockPos2, SpawnReason.SPAWN_EGG, true, !Objects.equals(blockPos, blockPos2) && direction == Direction.UP);
                if (entity != null) {
                    itemStack.decrement(1);
                    world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);

                    if (entity instanceof LovelyRobot robotEntity) {
                        // Set ownership without taming particles (spawning, not taming)
                        robotEntity.setOwner(context.getPlayer());
                        robotEntity.setSitting(false);
                        initialize(itemStack.getOrCreateNbt(), robotEntity);
                        
                        // Spawn POOF particles (spawn effect)
                        net.msymbios.llovelyr.common.entity.internal.InternalParticle.Poof(robotEntity);
                        
                        // Play spawn sound effect (totem activation sound) - volume scales with entity size
                        float volume = (float) Math.max(0.5F, Math.min(2.0F, entity.getWidth() * entity.getHeight()));
                        world.playSound(
                            null, 
                            entity.getBlockPos(), 
                            net.minecraft.sound.SoundEvents.ITEM_TOTEM_USE, 
                            net.minecraft.sound.SoundCategory.NEUTRAL, 
                            volume, 
                            1.2F
                        );
                    }
                }

                return ActionResult.CONSUME;
            }
        }
    } // useOnBlock()

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
    private void initialize(NbtCompound dataNBT, LovelyRobot entity) {
        if (!dataNBT.getString(LovelyIdentifier.STAT_CUSTOM_NAME).isEmpty()) entity.setCustomName(Text.literal(dataNBT.getString(LovelyIdentifier.STAT_CUSTOM_NAME)));
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