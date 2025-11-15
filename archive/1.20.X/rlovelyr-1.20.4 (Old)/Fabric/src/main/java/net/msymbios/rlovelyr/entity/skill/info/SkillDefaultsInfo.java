package net.msymbios.rlovelyr.entity.skill.info;

import net.msymbios.rlovelyr.entity.skill.Skill;

/**
 * Info regarding the default values for a skill.
 */
public class SkillDefaultsInfo {

    // -- Variables --

    /**
     * The default state of a skill.
     */
    public final Skill.State defaultState;

    // -- Constructors --

    /**
     * @param defaultState the default state of a skill.
     */
    public SkillDefaultsInfo(Skill.State defaultState) {
        this.defaultState = defaultState;
    } // Constructor SkillDefaultsInfo ()

} // Class SkillDefaultsInfo