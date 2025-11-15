package net.msymbios.rlovelyr.item.custom;

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
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.internal.InternalEntity;
import net.msymbios.rlovelyr.entity.internal.enums.EntityTexture;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static net.msymbios.rlovelyr.item.utils.Utility.*;

public class SpawnItem extends ForgeSpawnEggItem {

    // -- Constructor --

    public SpawnItem(Supplier<? extends EntityType<? extends Mob>> mob, Properties props) {
        super(mob, 0xFFFFFF, 0xFFFFFF, props);
    } // Constructor SpawnItem ()

    // -- Inherited Methods --

    /**
     * Appends tooltip information to the given ItemStack in the specified world, using the provided list of Text elements and TooltipContext.
     **/
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag context) {
        if (stack.hasTag()) {
            CompoundTag nbt = stack.getOrCreateTag();
            addNameTooltip(tooltip, nbt);
            addOwnerTooltip(tooltip, nbt);
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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        HitResult hitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hitresult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemstack);
        } else if (!(level instanceof ServerLevel)) {
            return InteractionResultHolder.success(itemstack);
        } else {
            BlockHitResult blockhitresult = (BlockHitResult)hitresult;
            BlockPos blockpos = blockhitresult.getBlockPos();
            if (!(level.getBlockState(blockpos).getBlock() instanceof LiquidBlock)) {
                return InteractionResultHolder.pass(itemstack);
            } else if (level.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos, blockhitresult.getDirection(), itemstack)) {
                EntityType<?> entitytype = this.getType(itemstack.getTag());
                InternalEntity entity = (InternalEntity)entitytype.spawn((ServerLevel)level, itemstack, player, blockpos, MobSpawnType.SPAWN_EGG, false, false);
                if (entity == null) {
                    return InteractionResultHolder.pass(itemstack);
                } else {
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                    level.gameEvent(GameEvent.ENTITY_PLACE, player);

                    entity.handleTame(player);
                    initialize(itemstack.getTag(), entity);
                    return InteractionResultHolder.consume(itemstack);
                }
            } else {
                return InteractionResultHolder.fail(itemstack);
            }
        }
    } // use ()

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        } else {
            ItemStack itemstack = context.getItemInHand();
            BlockPos blockpos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(Blocks.SPAWNER)) {
                BlockEntity blockentity = level.getBlockEntity(blockpos);
                if (blockentity instanceof SpawnerBlockEntity) {
                    BaseSpawner basespawner = ((SpawnerBlockEntity)blockentity).getSpawner();
                    EntityType<?> entitytype1 = this.getType(itemstack.getTag());
                    basespawner.setEntityId(entitytype1);
                    blockentity.setChanged();
                    level.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
                    itemstack.shrink(1);
                    return InteractionResult.CONSUME;
                }
            }

            BlockPos blockpos1;
            if (blockstate.getCollisionShape(level, blockpos).isEmpty()) {
                blockpos1 = blockpos;
            } else {
                blockpos1 = blockpos.relative(direction);
            }

            EntityType<?> entitytype = this.getType(itemstack.getTag());
            InternalEntity entity = (InternalEntity)entitytype.spawn((ServerLevel)level, itemstack, context.getPlayer(), blockpos1, MobSpawnType.SPAWN_EGG, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP);
            if (entity != null) {
                itemstack.shrink(1);
                level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);

                entity.handleTame(context.getPlayer());
                initialize(itemstack.getTag(), entity);
            }

            return InteractionResult.CONSUME;
        }
    } // useOn ()

    // -- Custom Methods --

    /**
     * Initializes the internal entity based on the data stored in the NBT compound.
     *
     * @param dataNBT The NBT compound containing the entity data
     * @param entity The internal entity to initialize
     * */
    private void initialize (CompoundTag dataNBT, InternalEntity entity) {
        // Set custom name if it is not empty
        if(!dataNBT.getString(LovelyRobotID.STAT_CUSTOM_NAME).isEmpty()) entity.setCustomName(Component.nullToEmpty(dataNBT.getString(LovelyRobotID.STAT_CUSTOM_NAME)));
        // Set texture if it is not the default random texture
        if(dataNBT.getInt(LovelyRobotID.STAT_COLOR) != EntityTexture.RANDOM.getId()) entity.setTexture(dataNBT.getInt(LovelyRobotID.STAT_COLOR));

        // Set max level, current level, and experience if they are greater than 0
        if(dataNBT.getInt(LovelyRobotID.STAT_LEVEL) > 0) entity.setCurrentLevel(dataNBT.getInt(LovelyRobotID.STAT_LEVEL));
        if(dataNBT.getInt(LovelyRobotID.STAT_EXP) > 0) entity.setExp(dataNBT.getInt(LovelyRobotID.STAT_EXP));

        // Set various protection levels if they are greater than 0
        if(dataNBT.getInt(LovelyRobotID.STAT_FIRE_PROTECTION) > 0) entity.setFireProtection(dataNBT.getInt(LovelyRobotID.STAT_FIRE_PROTECTION));
        if(dataNBT.getInt(LovelyRobotID.STAT_FALL_PROTECTION) > 0) entity.setFallProtection(dataNBT.getInt(LovelyRobotID.STAT_FALL_PROTECTION));
        if(dataNBT.getInt(LovelyRobotID.STAT_BLAST_PROTECTION) > 0) entity.setBlastProtection(dataNBT.getInt(LovelyRobotID.STAT_BLAST_PROTECTION));
        if(dataNBT.getInt(LovelyRobotID.STAT_PROJECTILE_PROTECTION) > 0) entity.setProjectileProtection(dataNBT.getInt(LovelyRobotID.STAT_PROJECTILE_PROTECTION));
    } // initialize ()

} // Class SpawnItem