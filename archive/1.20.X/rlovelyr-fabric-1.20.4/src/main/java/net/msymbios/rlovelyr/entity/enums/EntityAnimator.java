package net.msymbios.rlovelyr.entity.enums;

import net.msymbios.rlovelyr.config.LovelyRobotID;

import java.util.Arrays;
import java.util.Comparator;

public enum EntityAnimator {

    // -- Enum --
    Default(0, LovelyRobotID.ANIM_DEFAULT);

    // -- Variables --
    private static final EntityAnimator[] CODEC = Arrays.stream(values()).sorted(Comparator.comparingInt(EntityAnimator::getId)).toArray(EntityAnimator[]::new);

    private final int m_id;

    private final String m_name;

    // -- Constructor --
    EntityAnimator(int id, String name) {
        this.m_id = id;
        this.m_name = name;
    } // Constructor RobotAnimation

    // -- Methods --
    /**
     * Retrieve an EntityAnimator by its ID.
     *
     * @param  id   the ID of the EntityAnimator to retrieve
     * @return      the retrieved EntityAnimator
     */
    public static EntityAnimator byId(int id) {
        if (id < 0 || id >= CODEC.length) id = 0;
        return CODEC[id];
    } // byId ()

    /**
     * Gets the ID of the object.
     *
     * @return         	the ID of the object
     */
    public int getId() {
        return this.m_id;
    } // getId ()

    /**
     * A description of the entire Java function.
     *
     * @param  name	description of parameter
     * @return         	description of return value
     */
    public static EntityAnimator byName(String name) {
        for (EntityAnimator item : CODEC) {
            if (item.getName().equals(name))
                return item;
        }
        return null; // or throw an exception if the name is not found
    } // byName ()

    /**
     * Gets the name of the object.
     *
     * @return         	the name of the object
     */
    public String getName() {
        return this.m_name;
    } // getName ()

} // Enum EntityAnimator