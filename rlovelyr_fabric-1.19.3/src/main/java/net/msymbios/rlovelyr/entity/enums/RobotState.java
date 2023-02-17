package net.msymbios.rlovelyr.entity.enums;

import net.minecraft.util.function.ValueLists;

import java.util.function.IntFunction;

public enum RobotState {

    // -- Enum --
    Follow(0), Defense(1), Standby(2);

    // -- Variables --
    private final int id;
    private static final IntFunction<RobotState> BY_ID = ValueLists.createIdToValueFunction(RobotState::getId, RobotState.values(), ValueLists.OutOfBoundsHandling.WRAP);;


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

} // Enum RobotState