package net.msymbios.rlovelyr.entity.utils;

import net.minecraft.util.function.ValueLists;

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
        BY_ID = ValueLists.createIdToValueFunction(RobotMode::getId, RobotMode.values(), ValueLists.OutOfBoundsHandling.WRAP);
    }

} // Enum RobotMode