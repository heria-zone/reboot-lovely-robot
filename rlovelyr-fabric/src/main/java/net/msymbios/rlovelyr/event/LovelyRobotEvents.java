package net.msymbios.rlovelyr.event;

public class LovelyRobotEvents {

    // -- Method --
    public static void register () {
        ItemCraftCallback.EVENT.register(new CraftHandler());
    } // register ()

} // Class LovelyRobotEvents