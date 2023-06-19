package net.msymbios.rlovelyr;

import net.fabricmc.api.ClientModInitializer;
import net.msymbios.rlovelyr.entity.ModEntity;

public class LovelyRobotModClient implements ClientModInitializer {

    // -- Method --
    @Override
    public void onInitializeClient() {
        ModEntity.registerEntityRenderer();
    } // onInitializeClient ()

} // Class LovelyRobotClient
