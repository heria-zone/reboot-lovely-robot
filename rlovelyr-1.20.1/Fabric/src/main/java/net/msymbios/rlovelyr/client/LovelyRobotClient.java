package net.msymbios.rlovelyr.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.particles.LovelyRobotParticles;
import net.msymbios.rlovelyr.particles.custom.LevelUpParticle;

public class LovelyRobotClient implements ClientModInitializer {

    // -- Method --
    @Override
    public void onInitializeClient() {
        LovelyRobotEntities.registerRender();
        LovelyRobotParticles.registerRender();
    } // onInitializeClient ()

} // Class LovelyRobotClient
