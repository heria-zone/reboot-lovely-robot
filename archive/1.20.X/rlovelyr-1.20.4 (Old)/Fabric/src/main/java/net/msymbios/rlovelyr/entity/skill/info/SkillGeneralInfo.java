package net.msymbios.rlovelyr.entity.skill.info;

import net.minecraft.text.MutableText;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.skill.Skill;

/**
 * Info regarding the general properties of a skill.
 */
public class SkillGeneralInfo {

    // -- Variables --

    /**
     * The skill's type.
     */
    public final Skill.Type type;

    /**
     * The skill's name's translation text component.
     */
    public final MutableText name;

    /**
     * The skill's description's translation text component.
     */
    public final MutableText desc;

    // -- Constructors --

    /**
     * @param type the skill's type.
     * @param key  the skill's key.
     */
    public SkillGeneralInfo(Skill.Type type, String key) {
        this.type = type;
        this.name = LovelyRobotID.getSkillTranslation(key + ".name");
        this.desc = LovelyRobotID.getSkillTranslation(key + ".desc");
    } // Constructor SkillGeneralInfo ()

} // Class SkillGeneralInfo