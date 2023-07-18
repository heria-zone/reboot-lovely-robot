package net.msymbios.rlovelyr.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.entity.internal.Utility;

import java.util.List;

public class EntityItemSpawn extends SpawnEggItem {

    // -- Constructor --
    public EntityItemSpawn(EntityType<? extends MobEntity> type, Settings settings) {
        super(type, 0xFFFFFF, 0xFFFFFF, settings);
    } // Constructor RobotSpawnItem ()

    // -- Build-In Methods --
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if(!stack.hasTag()) {
            CompoundTag compound = new CompoundTag();
            compound.putString("rlovelyr.type", getEntityType(new CompoundTag()).toString());
            compound.putString("rlovelyr.color", "Random");
            compound.putInt("rlovelyr.level", 0);
            stack.setTag(compound);
        }

        if(stack.hasTag()) {
            String[] type = stack.getTag().getString("rlovelyr.type").split("\\.");
            tooltip.add(Text.of("Type: " + Utility.FirstToUpperCase (type[type.length -1])));
            tooltip.add(Text.of("Color: " + stack.getTag().getString("rlovelyr.color")));
            tooltip.add(Text.of("Level: " + stack.getTag().getInt("rlovelyr.level")));
        }
    } // appendTooltip ()

} // Class EntityItemSpawn