package net.msymbios.llovelyr.config;

import net.minecraft.resources.ResourceLocation;

/**
 * Registry for commonly used resource locations in rendering and UI systems.
 * <p>
 * <b>Architecture:</b> Pre-creates ResourceLocation instances to avoid repeated
 * string parsing during rendering. Particularly important for layer textures that
 * are referenced every frame.
 * <p>
 * <b>Performance:</b> ResourceLocation construction involves string parsing and
 * validation. Caching these as constants eliminates overhead in hot rendering paths.
 */
public class LovelyRobotResource {

    // -- Layer Textures --

    /** Overlay texture indicating auto-attack mode is enabled. */
    public static final ResourceLocation GENERAL_LAYER_AUTO_ATTACK = 
            LovelyIdentifier.getId(LovelyIdentifier.TEXTURE_LAYER_PATH + "general_auto_attack.png");
    
    /** Overlay texture indicating base defense mode is active. */
    public static final ResourceLocation GENERAL_LAYER_BASE_DEFENSE = 
            LovelyIdentifier.getId(LovelyIdentifier.TEXTURE_LAYER_PATH + "general_base_defence.png");
    
    /** Empty/transparent layer texture for conditional rendering. */
    public static final ResourceLocation GENERAL_LAYER_EMPTY = 
            LovelyIdentifier.getId(LovelyIdentifier.TEXTURE_LAYER_PATH + "general_empty.png");

} // Class: LovelyRobotResource
