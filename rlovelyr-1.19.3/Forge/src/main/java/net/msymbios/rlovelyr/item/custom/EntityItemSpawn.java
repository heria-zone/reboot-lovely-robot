package net.msymbios.rlovelyr.item.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.msymbios.rlovelyr.entity.internal.Utility;

import java.util.List;
import java.util.function.Supplier;

public class EntityItemSpawn extends ForgeSpawnEggItem {

    // -- Constructor --
    public EntityItemSpawn(Supplier<? extends EntityType<? extends Mob>> type, Properties props) {
        super(type, 0xFFFFFF, 0xFFFFFF, props);
    } // Constructor RobotSpawnItem ()

    // -- Build-In Methods --
    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        if(!stack.hasTag()) {
            CompoundTag compound = new CompoundTag();
            compound.putString("rlovelyr.type", getType(new CompoundTag()).toString());
            compound.putString("rlovelyr.color", "Random");
            compound.putInt("rlovelyr.level", 0);
            stack.setTag(compound);
        }

        if(stack.hasTag()) {
            String[] type = stack.getTag().getString("rlovelyr.type").split("\\.");
            tooltip.add(Component.literal("Type: " + Utility.FirstToUpperCase (type[type.length -1])));
            tooltip.add(Component.literal("Color: " + stack.getTag().getString("rlovelyr.color")));
            tooltip.add(Component.literal("Level: " + stack.getTag().getInt("rlovelyr.level")));
        }
    } // appendHoverText ()

} // Class EntityItemSpawn