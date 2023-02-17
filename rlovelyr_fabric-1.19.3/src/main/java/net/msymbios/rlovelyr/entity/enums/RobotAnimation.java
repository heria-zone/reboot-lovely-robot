package net.msymbios.rlovelyr.entity.enums;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

import java.util.function.IntFunction;

public enum RobotAnimation implements StringIdentifiable {

    // -- Enum --
    Locomotion(0, "locomotion");


    // -- Variables --
    public static final Codec<RobotAnimation> CODEC = StringIdentifiable.createCodec(RobotAnimation::values);
    private static final IntFunction<RobotAnimation> BY_ID = ValueLists.createIdToValueFunction(RobotAnimation::getId, RobotAnimation.values(), ValueLists.OutOfBoundsHandling.WRAP);;
    private final int id;
    private final String name;


    // -- Constructor --
    RobotAnimation(int id, String name) {
        this.id = id;
        this.name = name;
    } // Constructor RobotAnimation


    // -- Methods --
    public int getId() {
        return this.id;
    } // getId ()

    public static RobotAnimation byId(int id) {
        return BY_ID.apply(id);
    } // byId ()

    public String getName() {
        return this.name;
    } // getId ()

    public static RobotAnimation byName(String name) {
        return CODEC.byId(name);
    } // byId ()

    @Override
    public String asString() {
        return this.name;
    } // asString ()

} // Enum RobotAnimation