package net.msymbios.llovelyr.source;

import net.fabricmc.api.ClientModInitializer;

/**
 * Client-side initialization for Legacy variant (Fabric loader).
 * <p>
 * <b>Architecture:</b> Fabric's client-only entry point for rendering system
 * registration (item models, entity renderers). Separate from common initialization
 * in {@link net.msymbios.llovelyr.LovelyLegacy}.
 * <p>
 * <b>Registration Order:</b> Item models before entity renderers to ensure item
 * properties exist before entities reference them.
 * <p>
 * <i>Note:</i> Client-only class. Must be declared in fabric.mod.json under "client"
 * entrypoint. Never reference from server-side code.
 */
public class LovelyClient implements ClientModInitializer {

    // -- Inherited Methods --

    /**
     * Registers client-side rendering systems.
     * <p>
     * <b>Order:</b> Item models → Entity renderers (ensures item properties exist
     * before entity renderers query them).
     */
    @Override
    public void onInitializeClient() {
        LovelyItems.registerModel();
        LovelyEntities.registerRender();
    } // onInitializeClient()

} // Class: LovelyClient