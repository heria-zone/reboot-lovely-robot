package net.msymbios.llovelyr.source;

import net.fabricmc.api.ClientModInitializer;

public class LovelyClient implements ClientModInitializer {

    // -- Inherited Method --

    @Override
    public void onInitializeClient() {
        LovelyItems.registerModel();
        LovelyEntities.registerRender();
    } // onInitializeClient ()

} // Class: LovelyClient