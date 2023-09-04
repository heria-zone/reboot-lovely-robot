package net.msymbios.rlovelyr.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RobotCoreItem extends Item {

    public RobotCoreItem(Settings settings) {
        super(settings);
    }

    // -- Build-In Methods --
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(!stack.hasNbt()) {
            NbtCompound compound = new NbtCompound();
            compound.putString("rlovelyr.type", "??");
            compound.putString("rlovelyr.color", "Random");
            compound.putInt("rlovelyr.level", 0);
            stack.setNbt(compound);
        }

        if(stack.hasNbt()) {
            tooltip.add(Text.literal("Type: " + stack.getNbt().getString("rlovelyr.type")));
            tooltip.add(Text.literal("Color: " + stack.getNbt().getString("rlovelyr.color")));
            tooltip.add(Text.literal("Level: " + stack.getNbt().getInt("rlovelyr.level")));
        }
    } // appendTooltip ()

} // Class RobotCoreItem