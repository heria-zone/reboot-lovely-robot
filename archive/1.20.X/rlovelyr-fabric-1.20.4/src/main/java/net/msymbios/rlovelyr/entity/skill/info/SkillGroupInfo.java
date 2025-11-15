package net.msymbios.rlovelyr.entity.skill.info;

import net.minecraft.text.MutableText;
import net.msymbios.rlovelyr.client.gui.texture.Textures;
import net.msymbios.rlovelyr.config.LovelyRobotID;

import java.util.UUID;

/**
 * Info regarding a skill group.
 */
public class SkillGroupInfo {

    // -- Variables --

    /**
     * The skill group's id.
     */
    public final UUID id;

    /**
     * The skill group's key.
     */
    public final String key;

    /**
     * The skill group's background texture.
     */
    public final Textures.Skills.Tiles backgroundTexture;

    /**
     * The skill group's gui title.
     */
    public final MutableText guiTitle;

    // -- Constructors --

    /**
     * @param id                the skill group's id in string form.
     * @param key               the skill group's key.
     * @param backgroundTexture the skill group's background texture.
     */
    public SkillGroupInfo(String id, String key, Textures.Skills.Tiles backgroundTexture) {
        this.id = UUID.fromString(id);
        this.key = key;
        this.backgroundTexture = backgroundTexture;
        this.guiTitle = LovelyRobotID.getTranslation("skill_group." + key + ".gui_title");
    } // Constructor SkillGroupInfo ()

} // Class SkillGroupInfo