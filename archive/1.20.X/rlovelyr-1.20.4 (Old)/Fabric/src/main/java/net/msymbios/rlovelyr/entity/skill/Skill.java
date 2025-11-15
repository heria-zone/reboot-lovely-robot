package net.msymbios.rlovelyr.entity.skill;

import net.msymbios.rlovelyr.client.gui.texture.Texture;
import net.msymbios.rlovelyr.client.gui.texture.Textures;
import net.msymbios.rlovelyr.entity.internal.InterfaceEntity;
import net.msymbios.rlovelyr.entity.skill.info.SkillInfo;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Buyable to provide bonus stats, ai tasks and abilities.
 */
public class Skill {

    // -- Variables --

    /**
     * The blockling.
     */
    public final InterfaceEntity blockling;

    /**
     * The skill's info.
     */
    public final SkillInfo info;

    /**
     * The skill's skill group.
     */
    public final SkillGroup group;

    /**
     * The current state of the skill.
     */
    private State state;

    /**
     * Invoked when the skill's state changes.
     */
    //public final EventHandler<StateChangedEvent> onStateChanged = new EventHandler<>();

    /**
     * @param info  the skill's info.
     * @param group the skill's skill group.
     */
    public Skill(SkillInfo info, SkillGroup group) {
        this.blockling = group.blockling;
        this.info = info;
        this.group = group;
        this.state = info.defaults.defaultState;

        info.init(this);
    }

    /**
     * @return true if the skill can be bought.
     */
    public boolean canBuy() {
        if (state != State.UNLOCKED) {
            return false;
        }

        if (info.requirements.levels.keySet().stream().anyMatch(level -> group.blockling.getStats().getLevelAttribute(level).getValue() < info.requirements.levels.get(level))) {
            return false;
        }

        if (!areParentsBought()) {
            return false;
        }

        if (hasConflict()) {
            return false;
        }

        return true;
    }

    /**
     * Attempts to buy the skill.
     *
     * @return true if the skill was successfully bought.
     */
    public boolean tryBuy() {
        return tryBuy(true);
    }

    /**
     * Attempts to buy the skill.
     *
     * @param sync whether to sync to the client/server.
     * @return true if the skill was successfully bought.
     */
    public boolean tryBuy(boolean sync) {
        if (!canBuy()) {
            return false;
        }

        buy();

        info.onBuy(this);

        if (sync) {
            //new SkillTryBuyMessage(blockling, this).sync();
        }

        return true;
    }

    /**
     * Buys the skill.
     */
    private void buy() {
        setState(State.BOUGHT, false);
    }

    /**
     * @return true if the skill is bought.
     */
    public boolean isBought() {
        return state == State.BOUGHT;
    }

    /**
     * @return the current state of the skill.
     */
    public State getState() {
        return this.state;
    }

    /**
     * Sets the current state of the skill.
     * Syncs to the client/server.
     *
     * @param state the new state.
     */
    public void setState(State state) {
        setState(state, true);
    }

    /**
     * Sets the current state of the skill.
     * Syncs to the client/server if sync is true.
     *
     * @param state the new state.
     * @param sync  whether to sync to the client/server.
     */
    public void setState(State state, boolean sync) {
        this.state = state;

        if (sync) {
            //new SkillStateMessage(blockling, this).sync();
        }

        if (state == State.BOUGHT) {
            for (Skill child : children()) {
                if (child.state == State.BOUGHT || child.state == State.UNLOCKED) {
                    continue;
                }

                if (child.parents().stream().noneMatch(skill -> skill.state == State.LOCKED)) {
                    child.setState(State.UNLOCKED, sync);
                }
            }
        }

        //onStateChanged.handle(new StateChangedEvent());
    }

    /**
     * @return a list containing all the child skills.
     */
    public List<Skill> children() {
        return group.getSkills().stream().filter(skill -> skill.info.parents().contains(info)).collect(Collectors.toList());
    }

    /**
     * @return a list containing all the parent skills.
     */
    public List<Skill> parents() {
        return group.getSkills().stream().filter(skill -> info.parents().contains(skill.info)).collect(Collectors.toList());
    }

    /**
     * @return true if all the parents are bought.
     */
    public boolean areParentsBought() {
        return parents().stream().noneMatch(skill -> skill.getState() != State.BOUGHT);
    }

    /**
     * @return a list of all the conflicting skills.
     */
    public List<Skill> conflicts() {
        return group.getSkills().stream().filter(skill -> info.conflicts().contains(skill.info)).collect(Collectors.toList());
    }

    /**
     * @return true if any conflicting skill has been bought.
     */
    public boolean hasConflict() {
        return conflicts().stream().anyMatch(skill -> skill.getState() == State.BOUGHT);
    }

    /**
     * Represents the type of a skill.
     */
    public enum Type {
        STAT(0),
        AI(1),
        UTILITY(2),
        OTHER(3);

        /**
         * The background texture for the skill type.
         */
        public final Texture texture;

        /**
         * @param textureX the texture x index.
         */
        Type(int textureX) {
            this.texture = new Texture(Textures.Skills.SKILLS, textureX * 24, 217, 24, 24);
        }
    }

    /**
     * Represents the state of a skill.
     */
    public enum State {
        LOCKED(0x343434),
        UNLOCKED(0xf4f4f4),
        BOUGHT(0xffc409);

        /**
         * The colour to use in the gui.
         */
        public final Color colour;

        /**
         * @param colour the colour to use in the gui.
         */
        State(int colour) {
            this.colour = new Color(colour);
        }
    }

    /**
     * Called when the skill's state changes.
     */
    //public static class StateChangedEvent implements IEvent {}

} // Class Skill