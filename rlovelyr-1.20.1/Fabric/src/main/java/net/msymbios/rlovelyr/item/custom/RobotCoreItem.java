package net.msymbios.rlovelyr.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.entity.enums.EntityTexture;
import net.msymbios.rlovelyr.entity.internal.Utility;
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
            compound.putInt("color", 16);
            compound.putInt("level", 0);
            stack.setNbt(compound);
        }

        if(stack.hasNbt()) {
            if(!stack.getNbt().getString("custom_name").isEmpty()) tooltip.add(Text.translatable("msg.item.name").append(Text.literal(": " + stack.getNbt().getString("custom_name"))));
            if(!stack.getNbt().getString("type").isEmpty()) tooltip.add(Text.translatable("msg.item.type").append(Text.literal(": ")).append(Text.translatable(stack.getNbt().getString("type"))));
            tooltip.add(Text.translatable("msg.item.color").append(Text.literal(": ").append(Text.translatable(Utility.getTranslatable(EntityTexture.byId(stack.getNbt().getInt("color")))))));
            if(stack.getNbt().getInt("level") > 0) tooltip.add(Text.translatable("msg.item.level").append(Text.literal(": " + stack.getNbt().getInt("level"))));
        }
    } // appendTooltip ()

} // Class RobotCoreItem