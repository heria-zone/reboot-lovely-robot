package net.msymbios.rlovelyr.entity.enums;

import net.minecraft.util.StringIdentifiable;

import java.util.Arrays;
import java.util.Comparator;

public enum EntityAnimation implements StringIdentifiable {

    // -- Enum --
    Locomotion(0, "locomotion");


    // -- Variables --
    private static final EntityAnimation[] CODEC = Arrays.stream(values()).sorted(Comparator.comparingInt(EntityAnimation::getId)).toArray(EntityAnimation[]::new);
    private final int m_id;
    private final String m_name;


    // -- Constructor --
    EntityAnimation(int id, String name) {
        this.m_id = id;
        this.m_name = name;
    } // Constructor RobotAnimation


    // -- Methods --
    public static EntityAnimation byId(int id) {
        if (id < 0 || id >= CODEC.length) id = 0;
        return CODEC[id];
    } // byId ()

    public int getId() {
        return this.m_id;
    } // getId ()

    public String getName() {
        return this.m_name;
    } // getName ()

    @Override
    public String asString() {
        return this.m_name;
    } // asString ()

} // Enum RobotAnimation