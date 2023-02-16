package com.msymbios.entity.utils;

import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum RobotVariant implements StringRepresentable {

    // -- Enum --
    PINK(0, "pink"),
    YELLOW(1, "yellow"),
    LIGHT_BLUE(2, "light_blue"),
    BLACK(3, "black"),
    PURPLE(4, "purple"),
    RED(5, "red"),
    BLUE(6, "blue"),
    ORANGE(7, "orange"),
    LIME(8, "lime"),
    MAGENTA(9, "magenta");

    // -- Variables --
    public static final StringRepresentable.EnumCodec<RobotVariant> CODEC;
    private static final IntFunction<RobotVariant> BY_ID;
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
        return BY_ID.apply(id);
    } // byId ()

    @Override
    public String getSerializedName() {
        return this.name;
    } // getSerializedName ()

    // -- Initialize --
    static {
        CODEC = StringRepresentable.fromEnum(RobotVariant::values);
        BY_ID = ByIdMap.continuous(RobotVariant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    }

} // Enum RobotVariant