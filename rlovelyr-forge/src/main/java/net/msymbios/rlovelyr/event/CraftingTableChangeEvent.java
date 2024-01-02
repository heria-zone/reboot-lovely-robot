package net.msymbios.rlovelyr.event;

import net.minecraftforge.eventbus.api.Event;
import net.minecraft.world.World;
import net.minecraft.inventory.CraftingInventory;

public class CraftingTableChangeEvent extends Event {

    // -- Variables --
    private final World world;
    private final CraftingInventory inventory;

    // -- Constructor --
    public CraftingTableChangeEvent(World world, CraftingInventory inventory) {
        this.world = world;
        this.inventory = inventory;
    } // Constructor CraftingTableChangeEvent ()

    // -- Methods --
    public World getWorld() {
        return world;
    } // getWorld ()

    public CraftingInventory getInventory() {
        return inventory;
    } // getInventory ()

} // Class CraftingTableChangeEvent