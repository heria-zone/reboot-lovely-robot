package net.msymbios.rlovelyr.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
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
import net.msymbios.rlovelyr.entity.enums.EntityTexture;
import net.msymbios.rlovelyr.entity.internal.InternalEntity;
import net.msymbios.rlovelyr.entity.internal.Utility;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class SpawnItem extends SpawnEggItem {

    // -- Constructor --
    public SpawnItem(EntityType<? extends MobEntity> type, Settings settings) {
        super(type, 0xFFFFFF, 0xFFFFFF, settings);
    } // Constructor SpawnItem ()

    // -- Inherited Methods --
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(!stack.hasNbt()) {
            NbtCompound compound = new NbtCompound();
            compound.putInt("color", 16);
            compound.putInt("level", 0);
            stack.setNbt(compound);
        }

        if(stack.hasNbt()) {
            if(!stack.getNbt().getString("custom_name").isEmpty()) tooltip.add(Text.literal(stack.getNbt().getString("custom_name")));
            tooltip.add(Text.translatable("msg.item.color").append(Text.literal(": ").append(Text.translatable(Utility.getTranslatable(EntityTexture.byId(stack.getNbt().getInt("color")))))));
            if(stack.getNbt().getInt("level") > 0) tooltip.add(Text.translatable("msg.item.level").append(Text.literal(": " + stack.getNbt().getInt("level"))));
        }
    } // appendTooltip ()

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack stack = playerEntity.getStackInHand(hand);
        BlockHitResult blockHitResult = raycast(world, playerEntity, RaycastContext.FluidHandling.SOURCE_ONLY);
        if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return TypedActionResult.pass(stack);
        } else if (!(world instanceof ServerWorld)) {
            return TypedActionResult.success(stack);
        } else {
            BlockPos blockPos = blockHitResult.getBlockPos();
            if (!(world.getBlockState(blockPos).getBlock() instanceof FluidBlock)) {
                return TypedActionResult.pass(stack);
            } else if (world.canPlayerModifyAt(playerEntity, blockPos) && playerEntity.canPlaceOn(blockPos, blockHitResult.getSide(), stack)) {
                EntityType<?> entityType = this.getEntityType(stack.getNbt());
                InternalEntity entity = (InternalEntity)entityType.spawnFromItemStack((ServerWorld)world, stack, playerEntity, blockPos, SpawnReason.SPAWN_EGG, false, false);
                if (entity == null) {
                    return TypedActionResult.pass(stack);
                } else {
                    if (!playerEntity.getAbilities().creativeMode) {
                        stack.decrement(1);
                    }

                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                    world.emitGameEvent(playerEntity, GameEvent.ENTITY_PLACE, entity.getPos());
                    entity.handleTame(playerEntity);
                    initialize(stack.getNbt(), entity);
                    return TypedActionResult.consume(stack);
                }
            } else {
                return TypedActionResult.fail(stack);
            }
        }
    } // use ()

    @Override
    public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
        World world = itemUsageContext.getWorld();
        if (!(world instanceof ServerWorld)) {
            return ActionResult.SUCCESS;
        } else {
            ItemStack stack = itemUsageContext.getStack();
            BlockPos blockPos = itemUsageContext.getBlockPos();
            Direction direction = itemUsageContext.getSide();
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.isOf(Blocks.SPAWNER)) {
                BlockEntity blockEntity = world.getBlockEntity(blockPos);
                if (blockEntity instanceof MobSpawnerBlockEntity) {
                    MobSpawnerBlockEntity mobSpawnerBlockEntity = (MobSpawnerBlockEntity)blockEntity;
                    EntityType<?> entityType = this.getEntityType(stack.getNbt());
                    mobSpawnerBlockEntity.setEntityType(entityType, world.getRandom());
                    blockEntity.markDirty();
                    world.updateListeners(blockPos, blockState, blockState, 3);
                    world.emitGameEvent(itemUsageContext.getPlayer(), GameEvent.BLOCK_CHANGE, blockPos);
                    stack.decrement(1);
                    return ActionResult.CONSUME;
                }
            }

            BlockPos blockPos2;
            if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
                blockPos2 = blockPos;
            } else {
                blockPos2 = blockPos.offset(direction);
            }

            EntityType<?> entityType2 = this.getEntityType(stack.getNbt());
            InternalEntity entity = (InternalEntity) entityType2.spawnFromItemStack((ServerWorld)world, stack, itemUsageContext.getPlayer(), blockPos2, SpawnReason.SPAWN_EGG, true, !Objects.equals(blockPos, blockPos2) && direction == Direction.UP);
            if (entity != null) {
                stack.decrement(1);
                world.emitGameEvent(itemUsageContext.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);

                PlayerEntity player = itemUsageContext.getPlayer();
                entity.handleTame(player);
                initialize(stack.getNbt(), entity);
            }

            return ActionResult.CONSUME;
        }
    }

    // -- Methods --
    private void initialize (NbtCompound dataNBT, InternalEntity entity) {
        if(!dataNBT.getString("custom_name").isEmpty()) entity.setCustomName(Text.literal(dataNBT.getString("custom_name")));
        if(dataNBT.getInt("color") != 16) entity.setTexture(dataNBT.getInt("color"));

        if(dataNBT.getInt("max_level") > 0) entity.setMaxLevel(dataNBT.getInt("max_level"));
        if(dataNBT.getInt("level") > 0) entity.setCurrentLevel(dataNBT.getInt("level"));
        if(dataNBT.getInt("exp") > 0) entity.setExp(dataNBT.getInt("exp"));

        if(dataNBT.getInt("fire_protection") > 0) entity.setFireProtection(dataNBT.getInt("fire_protection"));
        if(dataNBT.getInt("fall_protection") > 0) entity.setFallProtection(dataNBT.getInt("fall_protection"));
        if(dataNBT.getInt("blast_protection") > 0) entity.setBlastProtection(dataNBT.getInt("blast_protection"));
        if(dataNBT.getInt("projectile_protection") > 0) entity.setProjectileProtection(dataNBT.getInt("projectile_protection"));
    } // initialize ()

} // Class SpawnItem