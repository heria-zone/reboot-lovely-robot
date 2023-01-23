package net.msymbios.rlovelyr.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RobotSpawnItem extends SpawnEggItem {

    // -- Constructor --
    public RobotSpawnItem(EntityType<? extends MobEntity> type, Settings settings) {
        super(type, 0xFFFFFF, 0xFFFFFF, settings);
    } // Constructor RobotSpawnItem ()

    // -- Methods --
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Type: Unknown").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Color: Random").formatted(Formatting.GRAY));
        super.appendTooltip(stack, world, tooltip, context);
    } // appendTooltip ()

} // Class RobotSpawnItem