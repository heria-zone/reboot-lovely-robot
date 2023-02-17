package com.msymbios.rlovelyr.entity.enums;

import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum RobotTexture implements StringRepresentable {

    // -- Enum --
    ORANGE(7, "orange"),
    MAGENTA(9, "magenta"),
    YELLOW(1, "yellow"),
    LIME(8, "lime"),
    PINK(0, "pink"),
    LIGHT_BLUE(2, "light_blue"),
    PURPLE(4, "purple"),
    BLUE(6, "blue"),
    RED(5, "red"),
    BLACK(3, "black");


    // -- Variables --
    public static final StringRepresentable.EnumCodec<RobotTexture> CODEC = StringRepresentable.fromEnum(RobotTexture::values);
    private static final IntFunction<RobotTexture> BY_ID = ByIdMap.continuous(RobotTexture::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    private final int id;
    private final String name;


    // -- Constructor --
    RobotTexture(int id, String name) {
        this.id = id;
        this.name = name;
    } // Constructor RobotTexture


    // -- Methods --
    public int getId() {
        return this.id;
    } // getId ()

    public static RobotTexture byId(int id) {
        return BY_ID.apply(id);
    } // byId ()

    public String getName() {
        return this.name;
    } // getId ()

    public static RobotTexture byName(String name) {
        return CODEC.byName(name);
    } // byId ()

    @Override
    public String getSerializedName() {
        return this.name;
    } // getSerializedName ()

} // Enum RobotTexture