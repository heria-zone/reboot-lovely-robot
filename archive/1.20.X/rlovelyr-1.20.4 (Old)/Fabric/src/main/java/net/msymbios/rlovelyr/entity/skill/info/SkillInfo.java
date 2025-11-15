package net.msymbios.rlovelyr.entity.skill.info;

import net.msymbios.rlovelyr.entity.skill.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A container for all the info for a skill.
 */
public class SkillInfo {

    // -- Variables --

    /**
     * The skill type's id.
     */
    public final UUID id;

    /**
     * The general info.
     */
    public final SkillGeneralInfo general;

    /**
     * The defaults info.
     */
    public final SkillDefaultsInfo defaults;

    /**
     * The requirements info.
     */
    public final SkillRequirementsInfo requirements;

    /**
     * The gui info.
     */
    public final SkillGuiInfo gui;

    // -- Constructors --

    /**
     * @param id           the skill type's id in string form.
     * @param generalInfo  the general info.
     * @param defaultsInfo the defaults info.
     * @param requirements the requirements info.
     * @param guiInfo      the gui info.
     */
    public SkillInfo(String id, SkillGeneralInfo generalInfo, SkillDefaultsInfo defaultsInfo, SkillRequirementsInfo requirements, SkillGuiInfo guiInfo) {
        this.id = UUID.fromString(id);
        this.general = generalInfo;
        this.defaults = defaultsInfo;
        this.requirements = requirements;
        this.gui = guiInfo;
    } // Constructor SkillInfo ()

    // -- Methods --

    /**
     * Called when the given skill is first initialised.
     */
    public void init(Skill skill) {}

    /**
     * Called when the given skill is bought.
     */
    public void onBuy(Skill skill) {}

    /**
     * Called once every tick for a bought skill.
     */
    public void tick(Skill skill) {}

    /**
     * Returns an array of all parent skill infos.
     */
    public List<SkillInfo> parents() {
        return new ArrayList<>();
    }

    /**
     * Returns an array of all conflict skill infos.
     */
    public List<SkillInfo> conflicts() {
        return new ArrayList<>();
    }

} // Class SkillInfo