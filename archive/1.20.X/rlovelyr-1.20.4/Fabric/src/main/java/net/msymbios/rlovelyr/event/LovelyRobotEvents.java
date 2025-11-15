package net.msymbios.rlovelyr.event;

import net.msymbios.rlovelyr.event.interfaces.ItemCraftCallback;

public class LovelyRobotEvents {

    // -- Method --

    /**
     * Register the CraftHandler with the ItemCraftCallback event.
     **/
    public static void register () {
        ItemCraftCallback.EVENT.register(new CraftHandler());
    } // register ()

} // Class LovelyRobotEvents