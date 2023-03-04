package net.msymbios.rlovelyr.entity.enums;

import net.minecraft.util.StringIdentifiable;

import java.util.Arrays;
import java.util.Comparator;

public enum RobotVariant implements StringIdentifiable {

    // -- Enum --
    Bunny(0, "bunny"),
    Bunny2(1, "bunny2"),
    Honey(2, "honey"),
    Vanilla(3, "vanilla");


    // -- Variables --
    private static final RobotVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(RobotVariant::getId)).toArray(RobotVariant[]::new);
    private final int id;
    private final String name;


    // -- Constructor --
    RobotVariant(int id, String name) {
        this.id = id;
        this.name = name;
    } // Constructor VanillaVariant


    // -- Methods --
    public int getId() {
        return this.id;
    } // getId ()

    public static RobotVariant byId(int id) {
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

} // Enum RobotVariant