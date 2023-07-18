package net.msymbios.rlovelyr.item.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.msymbios.rlovelyr.entity.internal.Utility;

import java.util.List;

public class EntityItemRobotCore extends Item {

    // -- Constructor --
    public EntityItemRobotCore(Properties setting) {
        super(setting);
    } // Constructor EntityItemRobotCore

    // -- Build-In Methods --
    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        if(!stack.hasTag()) {
            CompoundTag compound = new CompoundTag();
            compound.putString("rlovelyr.type", "??");
            compound.putString("rlovelyr.color", "Random");
            compound.putInt("rlovelyr.level", 0);
            stack.setTag(compound);
        }

        if(stack.hasTag()) {
            String[] type = stack.getTag().getString("rlovelyr.type").split("\\.");
            tooltip.add(Component.nullToEmpty("Type: " + Utility.FirstToUpperCase (type[type.length -1])));
            tooltip.add(Component.nullToEmpty("Color: " + stack.getTag().getString("rlovelyr.color")));
            tooltip.add(Component.nullToEmpty("Level: " + stack.getTag().getInt("rlovelyr.level")));
        }
    } // appendHoverText ()

} // Class EntityItemRobotCore