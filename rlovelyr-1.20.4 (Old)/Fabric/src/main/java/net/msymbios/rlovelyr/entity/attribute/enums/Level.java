package net.msymbios.rlovelyr.entity.attribute.enums;

import net.msymbios.rlovelyr.client.gui.texture.Texture;
import net.msymbios.rlovelyr.client.gui.texture.Textures;
import net.msymbios.rlovelyr.client.gui.util.Colour;

/**
 * An enum used to identify each level.
 */
public enum Level {

    COMBAT, MINING, WOODCUTTING, FARMING, TOTAL;

    /**
     * The minimum value a level can be.
     */
    public static final int MIN = 1;

    /**
     * The maximum value a level can be.
     */
    public static final int MAX = 100;

    /**
     * @return the background texture for the level's xp bar.
     */
    public Texture getXpBarBackgroundTexture() {
        switch (this) {
            case COMBAT: return Textures.Stats.COMBAT_BAR_BACKGROUND;
            case MINING: return Textures.Stats.MINING_BAR_BACKGROUND;
            case WOODCUTTING: return Textures.Stats.WOODCUTTING_BAR_BACKGROUND;
            case FARMING: return Textures.Stats.FARMING_BAR_BACKGROUND;
        }
        return Textures.Stats.COMBAT_BAR_BACKGROUND;
    }

    /**
     * @return the foreground texture for the level's xp bar.
     */
    public Texture getXpBarForegroundTexture() {
        switch (this) {
            case COMBAT: return Textures.Stats.COMBAT_BAR_FOREGROUND;
            case MINING: return Textures.Stats.MINING_BAR_FOREGROUND;
            case WOODCUTTING: return Textures.Stats.WOODCUTTING_BAR_FOREGROUND;
            case FARMING: return Textures.Stats.FARMING_BAR_FOREGROUND;
        }
        return Textures.Stats.COMBAT_BAR_FOREGROUND;
    }

    /**
     * @return the level icons texture for the level.
     */
    public Texture getLevelIconsTexture() {
        switch (this) {
            case COMBAT: return Textures.Stats.COMBAT_LEVEL_ICONS;
            case MINING: return Textures.Stats.MINING_LEVEL_ICONS;
            case WOODCUTTING: return Textures.Stats.WOODCUTTING_LEVEL_ICONS;
            case FARMING: return Textures.Stats.FARMING_LEVEL_ICONS;
        }
        return Textures.Stats.COMBAT_LEVEL_ICONS;
    } // getLevelIconsTexture ()

    /**
     * @return the level's colour.
     */
    public Colour getLevelColour() {
        switch (this) {
            case COMBAT: return Colour.fromRGBInt(0xe03434);
            case MINING: return Colour.fromRGBInt(0x4870d4);
            case WOODCUTTING: return Colour.fromRGBInt(0x4db83d);
            case FARMING: return Colour.fromRGBInt(0xedcf24);
        }
        return new Colour(0xffffffff);
    } // getLevelColour ()

} // Enum Level