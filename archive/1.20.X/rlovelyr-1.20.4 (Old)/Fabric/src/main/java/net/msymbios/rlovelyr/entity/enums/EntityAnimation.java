package net.msymbios.rlovelyr.entity.enums;

import java.util.Arrays;
import java.util.Comparator;

public enum EntityAnimation {

    // -- Enum --
    Idle(0, "idle"),
    Walk(1, "walk"),
    Rest(1, "rest"),
    Sit(1, "sit"),
    Attack(1, "attack");

    // -- Variables --
    private static final EntityAnimation[] CODEC = Arrays.stream(values()).sorted(Comparator.comparingInt(EntityAnimation::getId)).toArray(EntityAnimation[]::new);

    private final int m_id;

    private final String m_name;

    // -- Constructor --
    EntityAnimation(int id, String name) {
        this.m_id = id;
        this.m_name = name;
    } // Constructor EntityAnimation

    // -- Methods --
    public static EntityAnimation byId(int id) {
        if (id < 0 || id >= CODEC.length) id = 0;
        return CODEC[id];
    } // byId ()

    public int getId() {
        return this.m_id;
    } // getId ()

    public static EntityAnimation byName(String name) {
        for (EntityAnimation item : CODEC) {
            if (item.getName().equals(name))
                return item;
        }
        return null; // or throw an exception if the name is not found
    } // byName ()

    public String getName() {
        return this.m_name;
    } // getName ()

} // Enum EntityAnimation