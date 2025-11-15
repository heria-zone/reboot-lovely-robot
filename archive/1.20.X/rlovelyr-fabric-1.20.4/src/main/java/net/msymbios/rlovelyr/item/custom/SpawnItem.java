package net.msymbios.rlovelyr.item.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.enums.EntityTexture;
import net.msymbios.rlovelyr.entity.internal.InternalEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static net.msymbios.rlovelyr.util.internal.Utility.*;

public class SpawnItem extends SpawnEggItem {

    // -- Constructor --
    public SpawnItem(EntityType<? extends Mob> type, Properties settings) {
        super(type, 0xFFFFFF, 0xFFFFFF, settings);
    } // Constructor SpawnItem ()

    // -- Inherited Methods --

    /**
     * Appends tooltip information to the given ItemStack in the specified world, using the provided list of Text elements and TooltipContext.
     **/
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        if (stack.hasTag()) {
            CompoundTag nbt = stack.getOrCreateTag();
            addNameTooltip(tooltip, nbt);
            addColorTooltip(tooltip, nbt);
            addLevelTooltip(tooltip, nbt);
         } else {
            CompoundTag compound = stack.getOrCreateTag();
            compound.putInt(LovelyRobotID.STAT_COLOR, EntityTexture.RANDOM.getId());
            compound.putInt(LovelyRobotID.STAT_LEVEL, 0);
            stack.setTag(compound);
        }
    } // appendHoverText ()

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult blockHitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
        if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return TypedActionResult.pass(itemStack);
        } else if (!(world instanceof ServerWorld)) {
            return TypedActionResult.success(itemStack);
        } else {
            BlockHitResult blockHitResult2 = blockHitResult;
            BlockPos blockPos = blockHitResult2.getBlockPos();
            if (!(world.getBlockState(blockPos).getBlock() instanceof FluidBlock)) {
                return TypedActionResult.pass(itemStack);
            } else if (world.canPlayerModifyAt(user, blockPos) && user.canPlaceOn(blockPos, blockHitResult2.getSide(), itemStack)) {
                EntityType<?> entityType = this.getEntityType(itemStack.getNbt());
                InternalEntity entity = (InternalEntity)entityType.spawnFromItemStack((ServerWorld)world, itemStack, user, blockPos, SpawnReason.SPAWN_EGG, false, false);
                if (entity == null) {
                    return TypedActionResult.pass(itemStack);
                } else {
                    if (!user.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                    }

                    user.incrementStat(Stats.USED.getOrCreateStat(this));
                    world.emitGameEvent(user, GameEvent.ENTITY_PLACE, entity.getPos());
                    entity.handleTame(user);
                    initialize(itemStack.getNbt(), entity);
                    return TypedActionResult.consume(itemStack);
                }
            } else {
                return TypedActionResult.fail(itemStack);
            }
        }
    } // use ()


    @Override
    public InteractionResult useOn(UseOnContext context) {
        World world = context.getWorld();
        if (!(world instanceof ServerWorld)) {
            return ActionResult.SUCCESS;
        } else {
            ItemStack itemStack = context.getStack();
            BlockPos blockPos = context.getBlockPos();
            Direction direction = context.getSide();
            BlockState blockState = world.getBlockState(blockPos);
            BlockEntity var8 = world.getBlockEntity(blockPos);
            EntityType entityType;
            if (var8 instanceof Spawner) {
                Spawner spawner = (Spawner)var8;
                entityType = this.getEntityType(itemStack.getNbt());
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

                entityType = this.getEntityType(itemStack.getNbt());
                InternalEntity entity = (InternalEntity) entityType.spawnFromItemStack((ServerWorld)world, itemStack, context.getPlayer(), blockPos2, SpawnReason.SPAWN_EGG, true, !Objects.equals(blockPos, blockPos2) && direction == Direction.UP);
                if (entity != null) {
                    itemStack.decrement(1);
                    world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);

                    PlayerEntity player = context.getPlayer();
                    entity.handleTame(player);
                    initialize(itemStack.getNbt(), entity);
                }

                return ActionResult.CONSUME;
            }
        }
    } // useOnBlock ()

    // -- Custom Methods --

    /**
     * Initializes the internal entity based on the data stored in the NBT compound.
     *
     * @param dataNBT The NBT compound containing the entity data
     * @param entity The internal entity to initialize
     * */
    private void initialize (CompoundTag dataNBT, InternalEntity entity) {
        // Set custom name if it is not empty
        if(!dataNBT.getString(LovelyRobotID.STAT_CUSTOM_NAME).isEmpty()) entity.setCustomName(Component.literal(dataNBT.getString(LovelyRobotID.STAT_CUSTOM_NAME)));
        // Set texture if it is not the default random texture
        if(dataNBT.getInt(LovelyRobotID.STAT_COLOR) != EntityTexture.RANDOM.getId()) entity.setTexture(dataNBT.getInt(LovelyRobotID.STAT_COLOR));

        // Set max level, current level, and experience if they are greater than 0
        if(dataNBT.getInt(LovelyRobotID.STAT_MAX_LEVEL) > 0) entity.setMaxLevel(dataNBT.getInt(LovelyRobotID.STAT_MAX_LEVEL));
        if(dataNBT.getInt(LovelyRobotID.STAT_LEVEL) > 0) entity.setCurrentLevel(dataNBT.getInt(LovelyRobotID.STAT_LEVEL));
        if(dataNBT.getInt(LovelyRobotID.STAT_EXP) > 0) entity.setExp(dataNBT.getInt(LovelyRobotID.STAT_EXP));

        // Set various protection levels if they are greater than 0
        if(dataNBT.getInt(LovelyRobotID.STAT_FIRE_PROTECTION) > 0) entity.setFireProtection(dataNBT.getInt(LovelyRobotID.STAT_FIRE_PROTECTION));
        if(dataNBT.getInt(LovelyRobotID.STAT_FALL_PROTECTION) > 0) entity.setFallProtection(dataNBT.getInt(LovelyRobotID.STAT_FALL_PROTECTION));
        if(dataNBT.getInt(LovelyRobotID.STAT_BLAST_PROTECTION) > 0) entity.setBlastProtection(dataNBT.getInt(LovelyRobotID.STAT_BLAST_PROTECTION));
        if(dataNBT.getInt(LovelyRobotID.STAT_PROJECTILE_PROTECTION) > 0) entity.setProjectileProtection(dataNBT.getInt(LovelyRobotID.STAT_PROJECTILE_PROTECTION));
    } // initialize ()

} // Class SpawnItem