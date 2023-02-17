package com.msymbios.rlovelyr.entity.enums;

import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public enum RobotState {

    // -- Enum --
    Follow(0), BaseDefense(1), Standby(2);

    // -- Variables --
    private final int id;
    private static final IntFunction<RobotState> BY_ID = ByIdMap.continuous(RobotState::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);;


    // -- Properties --
    public int getId() {
        return this.id;
    } // getId ()

    public static RobotState byId(int id) {
        return BY_ID.apply(id);
    } // byId ()


    // -- Constructor --
    RobotState(int id) {
        this.id = id;
    } // Constructor RobotMode

} // enum RobotState