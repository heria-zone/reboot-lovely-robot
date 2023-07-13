package net.msymbios.rlovelyr.entity.enums;

import java.util.Arrays;
import java.util.Comparator;

public enum EntityAttribute {

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
    private static final EntityAttribute[] CODEC = Arrays.stream(values()).sorted(Comparator.comparingInt(EntityAttribute::getId)).toArray(EntityAttribute[]::new);

    private final int m_id;

    private final String m_name;


    // -- Constructor --
    EntityAttribute(int id, String name) {
        this.m_id = id;
        this.m_name = name;
    } // Constructor RobotAttribute


    // -- Methods --
    public static EntityAttribute byId(int id) {
        if (id < 0 || id >= CODEC.length) id = 0;
        return CODEC[id];
    } // byId ()

    public int getId() {
        return this.m_id;
    } // getId ()

    public static EntityAttribute byName(String name) {
        for (EntityAttribute item : CODEC) {
            if (item.getName().equals(name))
                return item;
        }
        return null; // or throw an exception if the name is not found
    } // byName ()

    public String getName() {
        return this.m_name;
    } // getName ()

} // Enum EntityAttribute