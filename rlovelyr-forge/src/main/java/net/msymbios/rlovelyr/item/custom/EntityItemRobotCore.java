package net.msymbios.rlovelyr.item.custom;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EntityItemRobotCore extends Item {

    // -- Constructor --
    public EntityItemRobotCore(Properties setting) {
        super(setting);
    } // Constructor EntityItemRobotCore

    // -- Build-In Methods --
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        if(!stack.hasTag()) {
            CompoundNBT compound = new CompoundNBT();
            compound.putString("rlovelyr.type", "??");
            compound.putString("rlovelyr.color", "Random");
            compound.putInt("rlovelyr.level", 0);
            stack.setTag(compound);
        }

        if(stack.hasTag()) {
            tooltip.add(ITextComponent.nullToEmpty("Type: " + stack.getTag().getString("rlovelyr.type")));
            tooltip.add(ITextComponent.nullToEmpty("Color: " + stack.getTag().getString("rlovelyr.color")));
            tooltip.add(ITextComponent.nullToEmpty("Level: " + stack.getTag().getInt("rlovelyr.level")));
        }
    } // appendHoverText ()

} // Class EntityItemRobotCore