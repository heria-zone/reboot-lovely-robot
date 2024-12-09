package net.msymbios.rlovelyr.entity.skill.info;

import net.msymbios.rlovelyr.entity.attribute.enums.Level;

import java.util.Map;

/**
 * Info regarding a skill's requirements.
 */
public class SkillRequirementsInfo {

    // -- Variables --

    /**
     * The levels required to buy a skill.
     */
    public final Map<Level, Integer> levels;

    // -- Constructors --

    /**
     * @param levels the levels required to buy a skill.
     */
    public SkillRequirementsInfo(Map<Level, Integer> levels) {
        this.levels = levels;
    }

} // Class SkillRequirementsInfo