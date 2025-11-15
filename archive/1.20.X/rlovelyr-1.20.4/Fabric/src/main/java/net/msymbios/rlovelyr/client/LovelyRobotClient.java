package net.msymbios.rlovelyr.client;

import net.fabricmc.api.ClientModInitializer;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.item.LovelyRobotItems;

public class LovelyRobotClient implements ClientModInitializer {

    // -- Inherited Method --

    @Override
    public void onInitializeClient() {
        LovelyRobotItems.registerModels();
        LovelyRobotEntities.registerRender();
    } // onInitializeClient ()

} // Class LovelyRobotClient