package net.msymbios.rlovelyr.entity.skill.info;

import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.client.gui.texture.Texture;

import java.awt.*;

/**
 * Info regarding how to display a skill in a gui.
 */
public class SkillGuiInfo {

    // -- Variables --

    /**
     * The x position.
     */
    public final int x;

    /**
     * The y position.
     */
    public final int y;

    /**
     * The connection type used to connect a skill with its parent.
     */
    public final ConnectionType connectionType;

    /**
     * The skill's icon texture.
     */
    public final SkillIconTexture iconTexture;

    /**
     * The skill's colour.
     */
    public final Color colour;

    // -- Constructors --

    /**
     * @param x              the x position.
     * @param y              the y position.
     * @param connectionType the connection type used to connect a skill with its parent.
     * @param colour         the skill's icon texture.
     * @param texture        the skill's colour.
     */
    public SkillGuiInfo(int x, int y, ConnectionType connectionType, int colour, SkillIconTexture texture) {
        this.x = x;
        this.y = y;
        this.connectionType = connectionType;
        this.colour = new Color(colour);
        this.iconTexture = texture;
    } // Constructor SkillGuiInfo

    // -- Classes --

    /**
     * A skill's icon texture.
     */
    public static class SkillIconTexture extends Texture {

        // -- Variables --
        /**
         * The width and height of an icon.
         */
        public static final int ICON_SIZE = 24;

        // -- Constructors --

        /**
         * @param texture the texture location.
         * @param x       the texture x index.
         * @param y       the texture y index.
         */
        public SkillIconTexture(Identifier texture, int x, int y) {
            super(texture, x * ICON_SIZE, y * ICON_SIZE, ICON_SIZE, ICON_SIZE);
        } // Constructor SkillIconTexture

    } // Class SkillIconTexture

    // -- Enums --

    /**
     * The connection type used to connect a skill with its parent.
     */
    public enum ConnectionType {
        SINGLE_LONGEST_FIRST,
        SINGLE_SHORTEST_FIRST,
        DOUBLE_LONGEST_SPLIT,
        DOUBLE_SHORTEST_SPLIT;
    } // Enum ConnectionType

} // Class SkillGuiInfo