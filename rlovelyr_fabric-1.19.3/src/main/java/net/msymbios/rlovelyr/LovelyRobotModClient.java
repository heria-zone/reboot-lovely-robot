package net.msymbios.rlovelyr;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.msymbios.rlovelyr.entity.ModEntities;
import net.msymbios.rlovelyr.entity.client.VanillaRenderer;

public class LovelyRobotModClient implements ClientModInitializer {

    // -- Method --
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.VANILLA, VanillaRenderer::new);
    } // onInitializeClient ()

} // Class LovelyRobotClient
