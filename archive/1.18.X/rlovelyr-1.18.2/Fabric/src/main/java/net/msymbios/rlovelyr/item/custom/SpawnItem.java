package net.msymbios.rlovelyr.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.MobSpawnerLogic;
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

    // -- Build-In Methods --
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(!stack.hasNbt()) {
            NbtCompound compound = new NbtCompound();
            compound.putInt("color", 16);
            compound.putInt("level", 0);
            stack.setNbt(compound);
        }

        if(stack.hasNbt()) {
            if(!stack.getNbt().getString("custom_name").isEmpty()) tooltip.add(new TranslatableText("msg.item.name").append(": " + stack.getNbt().getString("custom_name")));
            tooltip.add(new TranslatableText("msg.item.color").append(": ").append(new TranslatableText(Utility.getTranslatable(EntityTexture.byId(stack.getNbt().getInt("color"))))));
            if(stack.getNbt().getInt("level") > 0) tooltip.add(new TranslatableText("msg.item.level").append(": " + stack.getNbt().getInt("level")));
        }
    } // appendTooltip ()

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        HitResult hitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return TypedActionResult.pass(stack);
        } else if (!(world instanceof ServerWorld)) {
            return TypedActionResult.success(stack);
        } else {
            BlockHitResult blockHitResult = (BlockHitResult)hitResult;
            BlockPos blockPos = blockHitResult.getBlockPos();
            if (!(world.getBlockState(blockPos).getBlock() instanceof FluidBlock)) {
                return TypedActionResult.pass(stack);
            } else if (world.canPlayerModifyAt(user, blockPos) && user.canPlaceOn(blockPos, blockHitResult.getSide(), stack)) {
                EntityType<?> entityType = this.getEntityType(stack.getNbt());
                InternalEntity entity = (InternalEntity) entityType.spawnFromItemStack((ServerWorld)world, stack, user, blockPos, SpawnReason.SPAWN_EGG, false, false);
                if (entity == null) {
                    return TypedActionResult.pass(stack);
                } else {
                    if (!user.getAbilities().creativeMode) {
                        stack.decrement(1);
                    }

                    user.incrementStat(Stats.USED.getOrCreateStat(this));
                    world.emitGameEvent(GameEvent.ENTITY_PLACE, user);
                    entity.handleTame(user);
                    initialize(stack.getNbt(), entity);
                    return TypedActionResult.consume(stack);
                }
            } else {
                return TypedActionResult.fail(stack);
            }
        }
    } // use ()

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!(world instanceof ServerWorld)) {
            return ActionResult.SUCCESS;
        } else {
            ItemStack stack = context.getStack();
            BlockPos blockPos = context.getBlockPos();
            Direction direction = context.getSide();
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.isOf(Blocks.SPAWNER)) {
                BlockEntity blockEntity = world.getBlockEntity(blockPos);
                if (blockEntity instanceof MobSpawnerBlockEntity) {
                    MobSpawnerLogic mobSpawnerLogic = ((MobSpawnerBlockEntity)blockEntity).getLogic();
                    EntityType<?> entityType = this.getEntityType(stack.getNbt());
                    mobSpawnerLogic.setEntityId(entityType);
                    blockEntity.markDirty();
                    world.updateListeners(blockPos, blockState, blockState, 3);
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
            InternalEntity entity = (InternalEntity) entityType2.spawnFromItemStack((ServerWorld)world, stack, context.getPlayer(), blockPos2, SpawnReason.SPAWN_EGG, true, !Objects.equals(blockPos, blockPos2) && direction == Direction.UP);
            if (entity != null) {
                stack.decrement(1);
                world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
                PlayerEntity player = context.getPlayer();
                entity.handleTame(player);
                initialize(stack.getNbt(), entity);
            }

            return ActionResult.CONSUME;
        }
    } // useOnBlock ()

    // -- Methods --
    private void initialize (NbtCompound dataNBT, InternalEntity entity) {
        if(!dataNBT.getString("custom_name").isEmpty()) entity.setCustomName(Text.of(dataNBT.getString("custom_name")));
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