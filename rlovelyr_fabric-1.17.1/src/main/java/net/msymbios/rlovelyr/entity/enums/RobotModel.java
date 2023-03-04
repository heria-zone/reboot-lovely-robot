package net.msymbios.rlovelyr.entity.enums;

import net.minecraft.util.StringIdentifiable;

import java.util.Arrays;
import java.util.Comparator;

public enum RobotModel implements StringIdentifiable {

    // -- Enum --
    Unarmed(0, "unarmed"),
    Armed(1, "armed");


    // -- Variables --
    private static final RobotModel[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(RobotModel::getId)).toArray(RobotModel[]::new);
    private final int id;
    private final String name;


    // -- Constructor --
    RobotModel(int id, String name) {
        this.id = id;
        this.name = name;
    } // Constructor RobotModel


    // -- Methods --
    public int getId() {
        return this.id;
    } // getId ()

    public static RobotModel byId(int id) {
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

} // Enum RobotModel