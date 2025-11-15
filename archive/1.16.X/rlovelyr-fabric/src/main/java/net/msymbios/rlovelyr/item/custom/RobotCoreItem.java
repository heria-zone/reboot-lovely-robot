package net.msymbios.rlovelyr.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.entity.enums.EntityTexture;
import net.msymbios.rlovelyr.entity.internal.InternalLogic;
import net.msymbios.rlovelyr.entity.internal.Utility;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RobotCoreItem extends Item {

    // -- Constructor --
    public RobotCoreItem(Settings settings) {
        super(settings);
    } // Constructor RobotCoreItem ()

    // -- Build-In Methods --
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(!stack.hasTag()) {
            CompoundTag compound = new CompoundTag();
            compound.putInt("color", 16);
            compound.putInt("level", 0);
            stack.setTag(compound);
        }

        if(stack.hasTag()) {
            if(!stack.getTag().getString("custom_name").isEmpty()) tooltip.add(new TranslatableText("msg.item.name").append(": " + stack.getTag().getString("custom_name")));
            if(!stack.getTag().getString("type").isEmpty()) tooltip.add(new TranslatableText("msg.item.type").append(": ").append(new TranslatableText(stack.getTag().getString("type"))));
            tooltip.add(new TranslatableText("msg.item.color").append(": ").append(new TranslatableText(Utility.getTranslatable(EntityTexture.byId(stack.getTag().getInt("color"))))));
            if(stack.getTag().getInt("level") > 0) tooltip.add(new TranslatableText("msg.item.level").append(": " + stack.getTag().getInt("level")));
        }
    } // appendTooltip ()

} // Class RobotCoreItem