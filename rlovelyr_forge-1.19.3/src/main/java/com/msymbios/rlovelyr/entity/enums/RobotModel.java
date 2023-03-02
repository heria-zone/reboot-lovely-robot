package com.msymbios.rlovelyr.entity.enums;

import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum RobotModel implements StringRepresentable {

    // -- Enum --
    Unarmed(0, "unarmed"),
    Armed(1, "armed");


    // -- Variables --
    public static final StringRepresentable.EnumCodec<RobotModel> CODEC = StringRepresentable.fromEnum(RobotModel::values);
    private static final IntFunction<RobotModel> BY_ID = ByIdMap.continuous(RobotModel::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
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
        return BY_ID.apply(id);
    } // byId ()

    public String getName() {
        return this.name;
    } // getId ()

    public static RobotModel byName(String name) {
        return CODEC.byName(name);
    } // byId ()

    @Override
    public String getSerializedName() {
        return this.name;
    } // getSerializedName ()

} // Enum RobotModel