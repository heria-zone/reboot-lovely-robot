package net.msymbios.llovelyr.source.items.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.world.World;
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
public class LovelyCoreItem extends Item {

    // -- Constructors --

    public LovelyCoreItem(Settings settings) {
        super(settings);
    } // Constructor: LovelyCoreItem()

    // -- Inherited Methods --

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (stack.hasNbt()) {
            NbtCompound nbt = stack.getOrCreateNbt();
            addNameTooltip(tooltip, nbt);
            addOwnerTooltip(tooltip, nbt);
            addTypeTooltip(tooltip, nbt);
            addColorTooltip(tooltip, nbt);
            addLevelTooltip(tooltip, nbt);
        } else {
            NbtCompound compound = stack.getOrCreateNbt();
            compound.putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.RANDOM.getId());
            compound.putInt(LovelyIdentifier.STAT_LEVEL, 0);
            stack.setNbt(compound);
        }
    } // appendTooltip()

} // Class: LovelyCoreItem
