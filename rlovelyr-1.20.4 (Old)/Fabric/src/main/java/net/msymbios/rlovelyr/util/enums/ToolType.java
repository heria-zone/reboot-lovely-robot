package net.msymbios.rlovelyr.util.enums;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.msymbios.rlovelyr.util.ToolUtil;

public enum ToolType {

    // -- Values ''--
    WEAPON,
    PICKAXE,
    AXE,
    HOE;

    // -- Methods --

    public boolean is(ItemStack stack) {
        return isTooltype(this, stack.getItem());
    }

    public boolean is(Item item) {
        return isTooltype(this, item);
    }

    public static boolean isTooltype(ToolType type, Item item) {
        return switch (type) {
            case WEAPON -> ToolUtil.isWeapon(item);
            case PICKAXE -> ToolUtil.isPickaxe(item);
            case AXE -> ToolUtil.isAxe(item);
            case HOE -> ToolUtil.isHoe(item);
        };
    } // isTooltype ()

    public static ToolType getToolType(ItemStack stack) {
        return getToolType(stack.getItem());
    }

    public static ToolType getToolType(Item item) {
        for (ToolType type : values()) {
            if (isTooltype(type, item)) return type;
        }
        return null;
    } // getToolType ()

} // Enum ToolType