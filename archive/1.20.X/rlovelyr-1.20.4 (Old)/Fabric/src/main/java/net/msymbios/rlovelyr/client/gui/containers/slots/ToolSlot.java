package net.msymbios.rlovelyr.client.gui.containers.slots;


import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.msymbios.rlovelyr.util.ToolUtil;

/**
 * A container slot for a tool.
 */
public class ToolSlot extends Slot {

    // -- Constructors --

    /**
     * @param inventory      the corresponding inventory.
     * @param inventoryIndex the inventory index represented by the slot.
     * @param x              x location in the gui.
     * @param y              y location in the gui.
     */
    public ToolSlot(Inventory inventory, int inventoryIndex, int x, int y) {
        super(inventory, inventoryIndex, x, y);
    } // Constructor ToolSlot ()

    // -- Inherited Methods --

    @Override
    public boolean canInsert(ItemStack stack) {
        return ToolUtil.isTool(stack);
    } // canInsert ()

} // Class ToolSlot