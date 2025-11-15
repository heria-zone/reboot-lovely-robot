package net.msymbios.rlovelyr.client;

import net.fabricmc.api.ClientModInitializer;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.entity.animation.AnimationRegister;

public class LovelyRobotClient implements ClientModInitializer {

    // -- Method --
    @Override
    public void onInitializeClient() {
        LovelyRobotEntities.registerRender();
        AnimationRegister.registerAnimationState();
    } // onInitializeClient ()

} // Class LovelyRobotClient