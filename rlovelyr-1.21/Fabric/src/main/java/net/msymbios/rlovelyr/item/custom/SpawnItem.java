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
import net.minecraft.world.phys.BlockHitResult;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.internal.enums.EntityTexture;

import java.util.List;

import static net.msymbios.rlovelyr.item.utils.Utility.*;
import static net.msymbios.rlovelyr.item.utils.Utility.addLevelTooltip;

public class SpawnItem extends SpawnEggItem {

    // -- Constructor --

    public SpawnItem(EntityType<? extends Mob> type, Properties settings) {
        super(type, 0xFFFFFF, 0xFFFFFF, settings);
    } // Constructor SpawnItem ()

    // -- Inherited Methods --

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
        if (stack.getItem().has) {
            CompoundTag nbt = stack.tag();
            addNameTooltip(tooltip, nbt);
            addOwnerTooltip(tooltip, nbt);
            addColorTooltip(tooltip, nbt);
            addLevelTooltip(tooltip, nbt);
        } else {
            CompoundTag compound = stack.getOrCreateTag();
            compound.putInt(LovelyRobotID.STAT_COLOR, EntityTexture.RANDOM.getId());
            compound.putInt(LovelyRobotID.STAT_LEVEL, 0);
            stack.set(compound);
        }
    }

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
        if(dataNBT.getInt(LovelyRobotID.STAT_LEVEL) > 0) entity.setCurrentLevel(dataNBT.getInt(LovelyRobotID.STAT_LEVEL));
        if(dataNBT.getInt(LovelyRobotID.STAT_EXP) > 0) entity.setExp(dataNBT.getInt(LovelyRobotID.STAT_EXP));

        // Set various protection levels if they are greater than 0
        if(dataNBT.getInt(LovelyRobotID.STAT_FIRE_PROTECTION) > 0) entity.setFireProtection(dataNBT.getInt(LovelyRobotID.STAT_FIRE_PROTECTION));
        if(dataNBT.getInt(LovelyRobotID.STAT_FALL_PROTECTION) > 0) entity.setFallProtection(dataNBT.getInt(LovelyRobotID.STAT_FALL_PROTECTION));
        if(dataNBT.getInt(LovelyRobotID.STAT_BLAST_PROTECTION) > 0) entity.setBlastProtection(dataNBT.getInt(LovelyRobotID.STAT_BLAST_PROTECTION));
        if(dataNBT.getInt(LovelyRobotID.STAT_PROJECTILE_PROTECTION) > 0) entity.setProjectileProtection(dataNBT.getInt(LovelyRobotID.STAT_PROJECTILE_PROTECTION));
    } // initialize ()

} // Class SpawnItem