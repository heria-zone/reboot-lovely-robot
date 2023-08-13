package net.msymbios.rlovelyr;

import net.fabricmc.api.ClientModInitializer;
import net.msymbios.rlovelyr.entity.ModEntities;

public class LovelyRobotModClient implements ClientModInitializer {

    // -- Method --
    @Override
    public void onInitializeClient() {
        ModEntities.registerRenderers();
    } // onInitializeClient ()

} // Class LovelyRobotClient
