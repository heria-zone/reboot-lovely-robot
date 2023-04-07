package net.msymbios.rlovelyr.entity.enums;

import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum RobotTexture implements StringRepresentable {

    // -- Enum --
    WHITE(0, "white"),
    ORANGE(1, "orange"),
    MAGENTA(2, "magenta"),
    LIGHT_BLUE(3, "light_blue"),
    YELLOW(4, "yellow"),
    LIME(5, "lime"),
    PINK(6, "pink"),
    GRAY(7, "gray"),
    LIGHT_GRAY(8, "light_gray"),
    CYAN(9, "cyan"),
    PURPLE(10, "purple"),
    BLUE(11, "blue"),
    BROWN(12, "brown"),
    GREEN(13, "green"),
    RED(14, "red"),
    BLACK(15, "black");


    // -- Variables --
    public static final StringRepresentable.EnumCodec<RobotTexture> CODEC = StringRepresentable.fromEnum(RobotTexture::values);
    private static final IntFunction<RobotTexture> BY_ID = ByIdMap.continuous(RobotTexture::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
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