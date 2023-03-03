package net.msymbios.rlovelyr.entity.enums;

import java.util.Arrays;
import java.util.Comparator;

public enum RobotState {

    // -- Enum --
    Follow(0), BaseDefense(1), Standby(2);

    // -- Variables --
    private final int id;
    private static final RobotState[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(RobotState::getId)).toArray(RobotState[]::new);


    // -- Constructor --
    RobotState(int id) {
        this.id = id;
    } // Constructor RobotMode


    // -- Methods --
    public int getId() {
        return this.id;
    } // getId ()

    public static RobotState byId(int id) {
        if (id < 0 || id >= BY_ID.length) id = 0;
        return BY_ID[id];
    } // byId ()

} // Enum RobotState