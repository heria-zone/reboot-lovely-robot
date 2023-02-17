package net.msymbios.rlovelyr.entity.enums;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

import java.util.function.IntFunction;

public enum RobotModel implements StringIdentifiable {

    // -- Enum --
    Unarmed(0, "unarmed"),
    Armed(1, "armed");


    // -- Variables --
    public static final Codec<RobotModel> CODEC = StringIdentifiable.createCodec(RobotModel::values);
    private static final IntFunction<RobotModel> BY_ID = ValueLists.createIdToValueFunction(RobotModel::getId, RobotModel.values(), ValueLists.OutOfBoundsHandling.WRAP);;
    private final int id;
    private final String name;


    // -- Properties --
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
        return CODEC.byId(name);
    } // byId ()


    // -- Constructor --
    RobotModel(int id, String name) {
        this.id = id;
        this.name = name;
    } // Constructor RobotModel


    // -- Methods --
    @Override
    public String asString() {
        return this.name;
    } // asString ()

} // Enum RobotTexture