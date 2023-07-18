package net.msymbios.rlovelyr.entity.enums;

import java.util.Arrays;
import java.util.Comparator;

public enum EntityAnimator {

    // -- Enum --
    Locomotion(0, "locomotion");


    // -- Variables --
    private static final EntityAnimator[] CODEC = Arrays.stream(values()).sorted(Comparator.comparingInt(EntityAnimator::getId)).toArray(EntityAnimator[]::new);

    private final int m_id;

    private final String m_name;


    // -- Constructor --
    EntityAnimator(int id, String name) {
        this.m_id = id;
        this.m_name = name;
    } // Constructor EntityAnimator


    // -- Methods --
    public static EntityAnimator byId(int id) {
        if (id < 0 || id >= CODEC.length) id = 0;
        return CODEC[id];
    } // byId ()

    public int getId() {
        return this.m_id;
    } // getId ()

    public static EntityAnimator byName(String name) {
        for (EntityAnimator item : CODEC) {
            if (item.getName().equals(name))
                return item;
        }
        return null; // or throw an exception if the name is not found
    } // byName ()

    public String getName() {
        return this.m_name;
    } // getName ()

} // Enum EntityAnimator