package net.msymbios.rlovelyr.entity.skill;

import net.msymbios.rlovelyr.entity.internal.InterfaceEntity;
import net.msymbios.rlovelyr.entity.skill.info.SkillGroupInfo;
import net.msymbios.rlovelyr.entity.skill.info.SkillInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Contains a list of skills.
 */
public class SkillGroup {

    // -- Variables --

    /**
     * The blockling.
     */
    public final InterfaceEntity blockling;

    /**
     * The skill group's info.
     */
    public final SkillGroupInfo info;

    /**
     * The list og skills in the group.
     */
    private List<Skill> skills = new ArrayList<>();

    // -- Constructors --

    /**
     * @param blockling the blockling.
     * @param info      the group's info.
     */
    public SkillGroup(InterfaceEntity blockling, SkillGroupInfo info) {
        this.blockling = blockling;
        this.info = info;
    } // Constructor SkillGroup ()

    // -- Methods --

    /**
     * @return true if the given ability is part of the group.
     */
    public boolean contains(Skill ability) {
        return skills.contains(ability);
    } // contains ()

    /**
     * @return the list of skills in the group.
     */
    public List<Skill> getSkills() {
        return skills;
    } // getSkills ()

    /**
     * @return the skill for the given skill info if it exists in the group, else null.
     */
    public Skill getSkill(SkillInfo skillInfo) {
        return getSkill(skillInfo.id);
    } // getSkill ()

    /**
     * @return the skill for the given skill id if it exists in the group, else null.
     */
    public Skill getSkill(UUID skillId) {
        for (Skill skill : skills) {
            if (skill.info.id.equals(skillId)) {
                return skill;
            }
        }
        return null;
    } // getSkill ()

    /**
     * Adds the given skill to the group.
     *
     * @param skill the skill to add.
     */
    public void addSkill(Skill skill) {
        skills.add(skill);
    } // addSkill ()

    /**
     * Adds a list of skills to the group.
     *
     * @param skills the list of skills to add.
     */
    public void addSkills(List<Skill> skills) {
        for (Skill skill : skills)
            addSkill(skill);
    } // addSkills ()

} // Class SkillGroup