package net.msymbios.llovelyr.lib.rendering;

import net.minecraft.resources.ResourceLocation;
import net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity;

/**
 * Headphone overlay layer for robot audio equipment visualization.
 * <p>
 * <b>Architecture:</b> Specialized overlay layer for rendering headphone accessories
 * on robot entities. Handles headphone visibility based on entity configuration
 * and provides consistent headphone rendering across all robot variants.
 * <p>
 * <b>Design Decision:</b> Separate layer for headphones allows easy toggling
 * and customization without modifying base textures. Headphones can be enabled/
 * disabled per entity or globally via configuration.
 */
public class HeadphoneOverlayLayer<T extends LovelyRobotEntity> extends BaseInternalRenderLayer<T> {

    // -- Constructor --

    /**
     * Creates headphone overlay layer with conditional rendering.
     *
     * @param headphoneTexture headphone overlay texture resource location
     */
    public HeadphoneOverlayLayer(ResourceLocation headphoneTexture) {
        super(headphoneTexture, entity -> shouldShowHeadphones(entity));
    } // Constructor: HeadphoneOverlayLayer()

    // -- Headphone Logic --

    /**
     * Determines if headphones should be visible on this entity.
     * <p>
     * <b>Logic:</b> Checks entity-specific headphone configuration and global
     * headphone settings. Headphones may be disabled for certain robot types
     * or based on player preferences.
     *
     * @param entity robot entity to check
     * @return true if headphones should render, false otherwise
     */
    private static boolean shouldShowHeadphones(LovelyRobotEntity entity) {
        // Check if entity supports headphones
        if (!entity.supportsHeadphones()) {
            return false;
        }

        // Check entity-specific headphone setting
        if (!entity.hasHeadphonesEnabled()) {
            return false;
        }

        // Additional conditions can be added here
        // (e.g., global config, specific robot variants, etc.)
        
        return true;
    } // shouldShowHeadphones()

} // Class: HeadphoneOverlayLayer