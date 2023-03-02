package com.msymbios.rlovelyr.entity.enums;

import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum RobotAttribute implements StringRepresentable {

    // -- Enum --
    MAX_HEALTH(0, "max_health"),
    MAX_LEVEL(1, "max_level"),
    ATTACK_DAMAGE(2, "attack_damage"),
    ATTACK_SPEED(3, "attack_speed"),
    MOVEMENT_SPEED(4, "movement_speed"),
    DEFENSE(5, "defense"),
    ARMOR(6, "armor"),
    ARMOR_TOUGHNESS(7, "armor_toughness"),
    BASE_DEFENSE_RANGE(8, "base_defense_range"),
    BASE_DEFENSE_WARP_RANGE(9, "base_defense_warp_range");


    // -- Variables --
    public static final StringRepresentable.EnumCodec<RobotAttribute> CODEC = StringRepresentable.fromEnum(RobotAttribute::values);
    private static final IntFunction<RobotAttribute> BY_ID = ByIdMap.continuous(RobotAttribute::getId, RobotAttribute.values(), ByIdMap.OutOfBoundsStrategy.WRAP);;
    private final int id;
    private final String name;


    // -- Constructor --
    RobotAttribute(int id, String name) {
        this.id = id;
        this.name = name;
    } // Constructor RobotAttribute


    // -- Methods --
    public int getId() {
        return this.id;
    } // getId ()

    public static RobotAttribute byId(int id) {
        return BY_ID.apply(id);
    } // byId ()

    public String getName() {
        return this.name;
    } // getId ()

    public static RobotAttribute byName(String name) {
        return CODEC.byName(name);
    } // byId ()

    @Override
    public String getSerializedName() {
        return this.name;
    } // getSerializedName ()

} // Enum RobotAttribute