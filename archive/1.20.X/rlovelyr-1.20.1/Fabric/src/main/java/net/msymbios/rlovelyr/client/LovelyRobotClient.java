package net.msymbios.rlovelyr.client;

import net.fabricmc.api.ClientModInitializer;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.particles.LovelyRobotParticles;

public class LovelyRobotClient implements ClientModInitializer {

    // -- Method --
    @Override
    public void onInitializeClient() {
        LovelyRobotEntities.registerRender();
        LovelyRobotParticles.registerRender();
    } // onInitializeClient ()

} // Class LovelyRobotClient
