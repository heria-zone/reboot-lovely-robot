package net.msymbios.rlovelyr.entity.enums;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

import java.util.function.IntFunction;

public enum RobotVariant implements StringIdentifiable {

    // -- Enum --
    Bunny(0, "bunny"),
    Bunny2(1, "bunny2"),
    Honey(2, "honey"),
    Vanilla(3, "vanilla");


    // -- Variables --
    public static final Codec<RobotVariant> CODEC = StringIdentifiable.createCodec(RobotVariant::values);
    private static final IntFunction<RobotVariant> BY_ID = ValueLists.createIdToValueFunction(RobotVariant::getId, RobotVariant.values(), ValueLists.OutOfBoundsHandling.WRAP);;
    private final int id;
    private final String name;


    // -- Properties --
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
        return CODEC.byId(name);
    } // byId ()


    // -- Constructor --
    RobotVariant(int id, String name) {
        this.id = id;
        this.name = name;
    } // Constructor VanillaVariant


    // -- Methods --
    @Override
    public String asString() {
        return this.name;
    } // asString ()

} // Enum RobotTexture