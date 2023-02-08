package com.msymbios.entity.utils;

import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public enum RobotMode {

    // -- Enum --
    Follow(0), Guard(1), Standby(2);

    // -- Variables --
    private final int id;
    private static final IntFunction<RobotMode> BY_ID;

    // -- Properties --
    public int getId() {
        return this.id;
    } // getId ()

    public static RobotMode byId(int id) {
        return BY_ID.apply(id);
    } // byId ()

    // -- Constructor --
    RobotMode(int id) {
        this.id = id;
    } // Constructor RobotMode

    // -- Initialize --
    static {
        BY_ID = ByIdMap.continuous(RobotMode::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    }

} // enum RobotMode