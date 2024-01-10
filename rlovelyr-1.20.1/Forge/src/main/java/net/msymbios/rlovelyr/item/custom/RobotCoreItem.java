package net.msymbios.rlovelyr.item.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.msymbios.rlovelyr.entity.enums.EntityTexture;
import net.msymbios.rlovelyr.entity.internal.Utility;

import java.util.List;

public class RobotCoreItem extends Item {

    // -- Constructor --
    public RobotCoreItem(Properties setting) {
        super(setting);
    } // Constructor RobotCoreItem

    // -- Build-In Methods --
    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        if(!stack.hasTag()) {
            CompoundTag compound = new CompoundTag();
            compound.putInt("color", 16);
            compound.putInt("level", 0);
            stack.setTag(compound);
        }

        if(stack.hasTag()) {
            if(!stack.getTag().getString("custom_name").isEmpty())  tooltip.add(Component.translatable("msg.item.name").append(": " + stack.getTag().getString("custom_name")));
            if(!stack.getTag().getString("type").isEmpty())         tooltip.add(Component.translatable("msg.item.type").append(": ").append(Component.translatable(stack.getTag().getString("type"))));
            tooltip.add(Component.translatable("msg.item.color").append(": ").append(Component.translatable(Utility.getTranslatable(EntityTexture.byId(stack.getTag().getInt("color"))))));
            if(stack.getTag().getInt("level") > 0)                  tooltip.add(Component.translatable("msg.item.level").append(": " + stack.getTag().getInt("level")));
        }
    } // appendHoverText ()

} // Class RobotCoreItem