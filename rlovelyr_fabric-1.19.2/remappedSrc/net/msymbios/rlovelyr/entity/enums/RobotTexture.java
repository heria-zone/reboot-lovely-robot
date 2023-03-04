package net.msymbios.rlovelyr.entity.enums;

import net.minecraft.util.StringIdentifiable;

import java.util.Arrays;
import java.util.Comparator;

public enum RobotTexture implements StringIdentifiable {

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
    public static final Codec<RobotTexture> CODEC = StringIdentifiable.createCodec(RobotTexture::values);
    private static final RobotTexture[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(RobotTexture::getId)).toArray(RobotTexture[]::new);
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
        if (id < 0 || id >= BY_ID.length) id = 0;
        return BY_ID[id];
    } // byId ()

    public String getName() {
        return this.name;
    } // getId ()

    public static RobotTexture byName(String name) {
        return CODEC.byId(name);
    } // byId ()

    @Override
    public String asString() {
        return this.name;
    } // asString ()

} // Enum RobotTexture