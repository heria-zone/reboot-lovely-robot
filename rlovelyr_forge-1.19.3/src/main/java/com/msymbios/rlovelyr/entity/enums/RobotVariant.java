package com.msymbios.rlovelyr.entity.enums;

import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum RobotVariant implements StringRepresentable {

    // -- Enum --
    Bunny(0, "bunny"),
    Bunny2(1, "bunny2"),
    Honey(2, "honey"),
    Vanilla(3, "vanilla");


    // -- Variables --
    public static final StringRepresentable.EnumCodec<RobotVariant> CODEC = StringRepresentable.fromEnum(RobotVariant::values);
    private static final IntFunction<RobotVariant> BY_ID = ByIdMap.continuous(RobotVariant::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    private final int id;
    private final String name;


    // -- Constructor --
    RobotVariant(int id, String name) {
        this.id = id;
        this.name = name;
    } // Constructor RobotVariant


    // -- Methods --
    public int getId() {
        return this.id;
    } // getId ()

    public static RobotVariant byId(int id) {
        return BY_ID.apply(id);
    } // byId ()

    public String getName() {
        return this.name;
    } // getId ()

    public static RobotVariant byName(String name) {
        return CODEC.byName(name);
    } // byId ()

    @Override
    public String getSerializedName() {
        return this.name;
    } // getSerializedName ()

} // Enum RobotVariant