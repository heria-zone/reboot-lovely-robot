package net.msymbios.rlovelyr.item.custom;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.entity.enums.EntityTexture;
import net.msymbios.rlovelyr.entity.internal.Utility;

import javax.annotation.Nullable;
import java.util.List;

public class RobotCoreItem extends Item {

    // -- Constructor --
    public RobotCoreItem(Properties setting) {
        super(setting);
    } // Constructor RobotCoreItem

    // -- Build-In Methods --
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
            if(!stack.getTag().getString("type").isEmpty())         tooltip.add(new TranslationTextComponent("msg.item.type").append(": ").append(new TranslationTextComponent(stack.getTag().getString("type"))));
            tooltip.add(new TranslationTextComponent("msg.item.color").append(": ").append(new TranslationTextComponent(Utility.getTranslatable(EntityTexture.byId(stack.getTag().getInt("color"))))));
            if(stack.getTag().getInt("level") > 0)                  tooltip.add(new TranslationTextComponent("msg.item.level").append(": " + stack.getTag().getInt("level")));
        }
    } // appendHoverText ()

} // Class RobotCoreItem