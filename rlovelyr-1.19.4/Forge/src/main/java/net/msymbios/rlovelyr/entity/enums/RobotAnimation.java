package net.msymbios.rlovelyr.entity.enums;

import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum RobotAnimation implements StringRepresentable {

    // -- Enum --
    Locomotion(0, "locomotion");


    // -- Variables --
    public static final StringRepresentable.EnumCodec<RobotAnimation> CODEC = StringRepresentable.fromEnum(RobotAnimation::values);
    private static final IntFunction<RobotAnimation> BY_ID = ByIdMap.continuous(RobotAnimation::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
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
        return CODEC.byName(name);
    } // byId ()

    @Override
    public String getSerializedName() {
        return this.name;
    } // getSerializedName ()

} // Enum RobotAnimation