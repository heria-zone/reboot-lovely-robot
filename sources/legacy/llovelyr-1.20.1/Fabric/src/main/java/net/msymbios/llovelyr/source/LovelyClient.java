package net.msymbios.llovelyr.source.client;

import net.fabricmc.api.ClientModInitializer;
import net.msymbios.llovelyr.source.entity.LovelyEntities;
import net.msymbios.llovelyr.source.items.LovelyItems;

public class LovelyClient implements ClientModInitializer {

    // -- Inherited Method --

    @Override
    public void onInitializeClient() {
        LovelyItems.registerModel();
        LovelyEntities.registerRender();
    } // onInitializeClient ()

} // Class: LovelyClient