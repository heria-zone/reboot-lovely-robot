package net.msymbios.rlovelyr.item.custom;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.msymbios.rlovelyr.entity.internal.Utility;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class SpawnItem extends ForgeSpawnEggItem {

    // -- Constructor --
    public SpawnItem(Supplier<? extends EntityType<? extends MobEntity>> type, Properties props) {
        super(type, 0xFFFFFF, 0xFFFFFF, props);
    } // Constructor SpawnItem ()

    // -- Build-In Methods --
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        if(!stack.hasTag()) {
            CompoundNBT compound = new CompoundNBT();
            compound.putString("rlovelyr.type", getType(new CompoundNBT()).toString());
            compound.putString("rlovelyr.color", "Random");
            compound.putInt("rlovelyr.level", 0);
            stack.setTag(compound);
        }

        if(stack.hasTag()) {
            String[] type = stack.getTag().getString("rlovelyr.type").split("\\.");
            tooltip.add(ITextComponent.nullToEmpty("Type: " + Utility.FirstToUpperCase (type[type.length -1])));
            tooltip.add(ITextComponent.nullToEmpty("Color: " + stack.getTag().getString("rlovelyr.color")));
            tooltip.add(ITextComponent.nullToEmpty("Level: " + stack.getTag().getInt("rlovelyr.level")));
        }
    } // appendHoverText ()

} // Class SpawnItem