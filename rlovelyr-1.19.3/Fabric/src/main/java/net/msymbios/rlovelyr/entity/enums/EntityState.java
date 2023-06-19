package net.msymbios.rlovelyr.entity.enums;

import java.util.Arrays;
import java.util.Comparator;

public enum EntityState {

    // -- Enum --
    Follow(0), BaseDefense(1), Standby(2);

    // -- Variables --
    private final int m_id;
    private static final EntityState[] CODEC = Arrays.stream(values()).sorted(Comparator.comparingInt(EntityState::getId)).toArray(EntityState[]::new);


    // -- Constructor --
    EntityState(int id) {
        this.m_id = id;
    } // Constructor RobotMode


    // -- Methods --
    public static EntityState byId(int id) {
        if (id < 0 || id >= CODEC.length) id = 0;
        return CODEC[id];
    } // byId ()

    public int getId() {
        return this.m_id;
    } // getId ()

} // Enum RobotState