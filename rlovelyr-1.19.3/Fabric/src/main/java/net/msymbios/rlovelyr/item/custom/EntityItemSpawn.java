package net.msymbios.rlovelyr.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.entity.internal.Utility;

import javax.annotation.Nullable;
import java.util.List;

public class EntityItemSpawn extends SpawnEggItem {

    // -- Constructor --
    public EntityItemSpawn(EntityType<? extends MobEntity> type, Settings settings) {
        super(type, 0xFFFFFF, 0xFFFFFF, settings);
    } // Constructor RobotSpawnItem ()

    // -- Build-In Methods --
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(!stack.hasNbt()) {
            NbtCompound compound = new NbtCompound();
            compound.putString("rlovelyr.type", getEntityType(new NbtCompound()).toString());
            compound.putString("rlovelyr.color", "Random");
            compound.putInt("rlovelyr.level", 0);
            stack.setNbt(compound);
        }

        if(stack.hasNbt()) {
            String[] type = stack.getNbt().getString("rlovelyr.type").split("\\.");
            tooltip.add(Text.literal("Type: " + Utility.FirstToUpperCase (type[type.length -1])));
            tooltip.add(Text.literal("Color: " + stack.getNbt().getString("rlovelyr.color")));
            tooltip.add(Text.literal("Level: " + stack.getNbt().getInt("rlovelyr.level")));
        }
    } // appendTooltip ()

} // Class EntityItemSpawn