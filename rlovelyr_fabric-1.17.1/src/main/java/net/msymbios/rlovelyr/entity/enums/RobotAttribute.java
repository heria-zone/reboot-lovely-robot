package net.msymbios.rlovelyr.entity.enums;

import net.minecraft.util.StringIdentifiable;

import java.util.Arrays;
import java.util.Comparator;

public enum RobotAttribute implements StringIdentifiable {

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
    private static final RobotAttribute[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(RobotAttribute::getId)).toArray(RobotAttribute[]::new);
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
        if (id < 0 || id >= BY_ID.length) id = 0;
        return BY_ID[id];
    } // byId ()

    public String getName() {
        return this.name;
    } // getId ()

    @Override
    public String asString() {
        return this.name;
    } // asString ()

} // Enum RobotAttribute