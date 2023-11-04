package net.msymbios.rlovelyr.client;

import net.fabricmc.api.ClientModInitializer;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;

public class LovelyRobotClient implements ClientModInitializer {

    // -- Method --
    @Override
    public void onInitializeClient() {
        LovelyRobotEntities.registerRender();
    } // onInitializeClient ()

} // Class LovelyRobotClient
