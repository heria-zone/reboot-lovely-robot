package net.msymbios.rlovelyr.item.custom;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.internal.enums.EntityTexture;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static net.msymbios.rlovelyr.item.util.TooltipUtils.*;

public class RobotCoreItem extends Item {

    // -- Constructor --

    public RobotCoreItem(Properties properties) {
        super(properties);
    } // Constructor RobotCoreItem ()

    // -- Inherited Methods --

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag nbt = Objects.requireNonNull(stack.get(DataComponents.CUSTOM_DATA)).copyTag();
            addNameTooltip(tooltip, nbt);
            addOwnerTooltip(tooltip, nbt);
            addTypeTooltip(tooltip, nbt);
            addColorTooltip(tooltip, nbt);
            addLevelTooltip(tooltip, nbt);
        } else {
            CompoundTag compound = new CompoundTag();
            compound.putInt(LovelyRobotID.STAT_COLOR, EntityTexture.RANDOM.getId());
            compound.putInt(LovelyRobotID.STAT_LEVEL, 0);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(compound));
        }
    } // appendHoverText ()

} // Class RobotCoreItem