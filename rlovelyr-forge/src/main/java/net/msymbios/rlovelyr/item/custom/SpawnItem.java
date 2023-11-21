package net.msymbios.rlovelyr.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.AbstractSpawner;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.msymbios.rlovelyr.entity.enums.EntityTexture;
import net.msymbios.rlovelyr.entity.internal.InternalEntity;
import net.msymbios.rlovelyr.entity.internal.Utility;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class SpawnItem extends ForgeSpawnEggItem {

    // -- Constructor --
    public SpawnItem(Supplier<? extends EntityType<? extends MobEntity>> type, Properties props) {
        super(type, 0xFFFFFF, 0xFFFFFF, props);
    } // Constructor SpawnItem ()

    // -- Inherited Methods --
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        if(!stack.hasTag()) {
            CompoundNBT compound = new CompoundNBT();
            compound.putInt("color", 16);
            compound.putInt("level", 0);
            stack.setTag(compound);
        }

        if(stack.hasTag()) {
            if(!stack.getTag().getString("custom_name").isEmpty())  tooltip.add(new TranslationTextComponent("msg.item.name").append(": " + stack.getTag().getString("custom_name")));
            tooltip.add(new TranslationTextComponent("msg.item.color").append(": ").append(new TranslationTextComponent(Utility.getTranslatable(EntityTexture.byId(stack.getTag().getInt("color"))))));
            if(stack.getTag().getInt("level") > 0)                  tooltip.add(new TranslationTextComponent("msg.item.level").append(": " + stack.getTag().getInt("level")));
        }
    } // appendHoverText ()

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getItemInHand(hand);
        RayTraceResult raytraceresult = getPlayerPOVHitResult(world, user, RayTraceContext.FluidMode.SOURCE_ONLY);
        if (raytraceresult.getType() != RayTraceResult.Type.BLOCK) {
            return ActionResult.pass(stack);
        } else if (!(world instanceof ServerWorld)) {
            return ActionResult.success(stack);
        } else {
            BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)raytraceresult;
            BlockPos blockpos = blockraytraceresult.getBlockPos();
            if (!(world.getBlockState(blockpos).getBlock() instanceof FlowingFluidBlock)) {
                return ActionResult.pass(stack);
            } else if (world.mayInteract(user, blockpos) && user.mayUseItemAt(blockpos, blockraytraceresult.getDirection(), stack)) {
                EntityType<?> entitytype = this.getType(stack.getTag());
                InternalEntity entity = (InternalEntity) entitytype.spawn((ServerWorld)world, stack, user, blockpos, SpawnReason.SPAWN_EGG, false, false);
                if (entity == null) {
                    return ActionResult.pass(stack);
                } else {
                    if (!user.abilities.instabuild) {
                        stack.shrink(1);
                    }

                    user.awardStat(Stats.ITEM_USED.get(this));
                    entity.handleTame(user);
                    initialize(stack.getTag(), entity);
                    return ActionResult.consume(stack);
                }
            } else {
                return ActionResult.fail(stack);
            }
        }
    } // use ()

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        if (!(world instanceof ServerWorld)) {
            return ActionResultType.SUCCESS;
        } else {
            ItemStack stack = context.getItemInHand();
            BlockPos blockpos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockState blockstate = world.getBlockState(blockpos);
            if (blockstate.is(Blocks.SPAWNER)) {
                TileEntity tileentity = world.getBlockEntity(blockpos);
                if (tileentity instanceof MobSpawnerTileEntity) {
                    AbstractSpawner abstractspawner = ((MobSpawnerTileEntity)tileentity).getSpawner();
                    EntityType<?> entitytype1 = this.getType(stack.getTag());
                    abstractspawner.setEntityId(entitytype1);
                    tileentity.setChanged();
                    world.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
                    stack.shrink(1);
                    return ActionResultType.CONSUME;
                }
            }

            BlockPos blockpos1;
            if (blockstate.getCollisionShape(world, blockpos).isEmpty()) {
                blockpos1 = blockpos;
            } else {
                blockpos1 = blockpos.relative(direction);
            }

            EntityType<?> entitytype = this.getType(stack.getTag());
            InternalEntity entity = (InternalEntity) entitytype.spawn((ServerWorld)world, stack, context.getPlayer(), blockpos1, SpawnReason.SPAWN_EGG, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP);
            if (entity != null) {
                stack.shrink(1);
                PlayerEntity player = context.getPlayer();
                entity.handleTame(player);
                initialize(stack.getTag(), entity);
            }

            return ActionResultType.CONSUME;
        }
    } // useOn ()

    // -- Methods --
    private void initialize (CompoundNBT dataNBT, InternalEntity entity) {
        if(!dataNBT.getString("custom_name").isEmpty()) entity.setCustomName(ITextComponent.nullToEmpty(dataNBT.getString("custom_name")));
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