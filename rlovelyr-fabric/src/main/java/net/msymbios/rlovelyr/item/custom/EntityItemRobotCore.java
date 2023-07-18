package net.msymbios.rlovelyr.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EntityItemRobotCore extends Item {

    public EntityItemRobotCore(Settings settings) {
        super(settings);
    }

    // -- Build-In Methods --
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(!stack.hasTag()) {
            CompoundTag compound = new CompoundTag();
            compound.putString("rlovelyr.type", "??");
            compound.putString("rlovelyr.color", "Random");
            compound.putInt("rlovelyr.level", 0);
            stack.setTag(compound);
        }

        if(stack.hasTag()) {
            tooltip.add(Text.of("Type: " + stack.getTag().getString("rlovelyr.type")));
            tooltip.add(Text.of("Color: " + stack.getTag().getString("rlovelyr.color")));
            tooltip.add(Text.of("Level: " + stack.getTag().getInt("rlovelyr.level")));
        }
    } // appendTooltip ()

} // Class