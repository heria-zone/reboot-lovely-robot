package net.msymbios.llovelyr.common.items.custom;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.msymbios.llovelyr.common.shared.LovelyIdentifier;
import net.msymbios.llovelyr.framework.entity.enums.EntityTexture;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.msymbios.llovelyr.common.items.utils.TooltipUtils.*;

/**
 * Robot core item storing entity data for crafting and storage.
 * <p>
 * <b>Architecture:</b> Acts as portable robot storage, preserving entity state
 * (name, owner, type, color, level) in data components for later reconstruction.
 * <p>
 * <b>Migration Note:</b> Minecraft 1.21.1 replaced NBT tags with typed data components.
 * Custom data is now stored in DataComponents.CUSTOM_DATA as a CustomData object.
 * <p>
 * <b>Usage Pattern:</b> Created when robot is "captured" or crafted, can be
 * used to spawn robot with preserved attributes.
 */
public class LovelyCoreItem extends Item {

    // -- Constructors --

    public LovelyCoreItem(Properties properties) {
        super(properties);
    } // Constructor: LovelyCoreItem()

    // -- Inherited Methods --

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // Check if stack has custom data component
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
                CompoundTag nbt = customData.copyTag();
                addNameTooltip(tooltip, nbt);
                addOwnerTooltip(tooltip, nbt);
                addTypeTooltip(tooltip, nbt);
                addColorTooltip(tooltip, nbt);
                addLevelTooltip(tooltip, nbt);
            }
        } else {
            // Initialize default data if not present
            CompoundTag compound = new CompoundTag();
            compound.putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.RANDOM.getId());
            compound.putInt(LovelyIdentifier.STAT_LEVEL, 0);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(compound));
        }
    } // appendHoverText()

} // Class: LovelyCoreItem