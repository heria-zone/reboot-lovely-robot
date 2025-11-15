package net.msymbios.rlovelyr.config.internal;

import eu.midnightdust.lib.config.MidnightConfig;

/**
 * Config options only available to each client.
 */
public class Client extends MidnightConfig {

    // -- RENDERER --

    @Comment public static final String commentShadowRadius = "Entity shadow cast size on the ground.";
    @Entry(category = "Renderer", name = "Entity Shadow Radius", isSlider = true, min = 0.1D, max = 1D)
    public final double shadowRadius = 0.4D;



} // Class Client