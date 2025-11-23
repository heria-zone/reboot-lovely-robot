package net.msymbios.llovelyr.source.items.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.msymbios.llovelyr.source.configs.LovelyIdentifier;
import net.msymbios.llovelyr.source.entity.internal.enums.EntityTexture;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.msymbios.llovelyr.source.items.util.TooltipUtils.*;

/**
 * Robot core item storing entity data for crafting and storage.
 * <p>
 * <b>Architecture:</b> Acts as portable robot storage, preserving entity state
 * (name, owner, type, color, level) in NBT for later reconstruction.
 * <p>
 * <b>Usage Pattern:</b> Created when robot is "captured" or crafted, can be
 * used to spawn robot with preserved attributes.
 */
public class LovelyCore extends Item {

    // -- Constructors --

    public LovelyCore(Properties properties) {
        super(properties);
    } // Constructor: LovelyCore()

    // -- Inherited Methods --

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (stack.hasTag()) {
            CompoundTag nbt = stack.getOrCreateTag();
            addNameTooltip(tooltip, nbt);
            addOwnerTooltip(tooltip, nbt);
            addTypeTooltip(tooltip, nbt);
            addColorTooltip(tooltip, nbt);
            addLevelTooltip(tooltip, nbt);
        } else {
            CompoundTag compound = stack.getOrCreateTag();
            compound.putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.RANDOM.getId());
            compound.putInt(LovelyIdentifier.STAT_LEVEL, 0);
            stack.setTag(compound);
        }
    } // appendHoverText()

} // Class: LovelyCore
