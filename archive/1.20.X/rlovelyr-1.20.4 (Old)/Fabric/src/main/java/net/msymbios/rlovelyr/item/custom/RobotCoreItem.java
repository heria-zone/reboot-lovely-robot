package net.msymbios.rlovelyr.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.enums.EntityTexture;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.msymbios.rlovelyr.util.internal.Utility.*;

public class RobotCoreItem extends Item {

    // -- Constructor --
    public RobotCoreItem(Settings settings) {
        super(settings);
    } // Constructor RobotCoreItem ()

    // -- Inherited Methods --

    /**
     * Appends tooltip information to the given ItemStack in the specified world, using the provided list of Text elements and TooltipContext.
     **/
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.hasNbt()) {
            NbtCompound nbt = stack.getOrCreateNbt();
            addNameTooltip(tooltip, nbt);
            addTypeTooltip(tooltip, nbt);
            addColorTooltip(tooltip, nbt);
            addLevelTooltip(tooltip, nbt);
        } else {
            NbtCompound compound = stack.getOrCreateNbt();
            compound.putInt(LovelyRobotID.STAT_COLOR, EntityTexture.RANDOM.getId());
            compound.putInt(LovelyRobotID.STAT_LEVEL, 0);
            stack.setNbt(compound);
        }
    } // appendTooltip ()

} // Class RobotCoreItem