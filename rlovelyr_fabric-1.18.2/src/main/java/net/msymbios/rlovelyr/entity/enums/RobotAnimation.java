package net.msymbios.rlovelyr.entity.enums;

import net.minecraft.util.StringIdentifiable;

import java.util.Arrays;
import java.util.Comparator;

public enum RobotAnimation implements StringIdentifiable {

    // -- Enum --
    Locomotion(0, "locomotion");


    // -- Variables --
    private static final RobotAnimation[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(RobotAnimation::getId)).toArray(RobotAnimation[]::new);
    private final int id;
    private final String name;


    // -- Constructor --
    RobotAnimation(int id, String name) {
        this.id = id;
        this.name = name;
    } // Constructor RobotAnimation


    // -- Methods --
    public int getId() {
        return this.id;
    } // getId ()

    public static RobotAnimation byId(int id) {
        if (id < 0 || id >= BY_ID.length) id = 0;
        return BY_ID[id];
    } // byId ()

    public String getName() {
        return this.name;
    } // getId ()

    @Override
    public String asString() {
        return this.name;
    } // asString ()

} // Enum RobotAnimation