package net.msymbios.rlovelyr.entity.utils;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

import java.util.function.IntFunction;

public enum RobotTexture implements StringIdentifiable {

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
    public static final StringIdentifiable.Codec<RobotTexture> CODEC = StringIdentifiable.createCodec(RobotTexture::values);
    private static final IntFunction<RobotTexture> BY_ID = ValueLists.createIdToValueFunction(RobotTexture::getId, RobotTexture.values(), ValueLists.OutOfBoundsHandling.WRAP);;
    private final int id;
    private final String name;


    // -- Properties --
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
        return CODEC.byId(name);
    } // byId ()


    // -- Constructor --
    RobotTexture(int id, String name) {
        this.id = id;
        this.name = name;
    } // Constructor VanillaVariant


    // -- Methods --
    @Override
    public String asString() {
        return this.name;
    } // asString ()

} // Enum RobotTexture