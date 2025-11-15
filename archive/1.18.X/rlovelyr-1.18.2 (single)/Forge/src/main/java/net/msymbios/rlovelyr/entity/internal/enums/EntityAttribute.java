package net.msymbios.rlovelyr.entity.internal.enums;

import net.msymbios.rlovelyr.config.LovelyRobotID;

import java.util.Arrays;
import java.util.Comparator;

public enum EntityAttribute {

    // -- Enum --
    MAX_HEALTH(0, LovelyRobotID.ATTR_MAX_HEALTH),
    MAX_LEVEL(1, LovelyRobotID.ATTR_MAX_LEVEL),
    ATTACK_DAMAGE(2, LovelyRobotID.ATTR_ATTACK_DAMAGE),
    ATTACK_SPEED(3, LovelyRobotID.ATTR_ATTACK_SPEED),
    MOVEMENT_SPEED(4, LovelyRobotID.ATTR_MOVEMENT_SPEED),
    DEFENSE(5, LovelyRobotID.ATTR_DEFENSE),
    ARMOR(6, LovelyRobotID.ATTR_ARMOR),
    ARMOR_TOUGHNESS(7, LovelyRobotID.ATTR_ARMOR_TOUGHNESS),
    BASE_DEFENSE_RANGE(8, LovelyRobotID.ATTR_BASE_DEFENSE),
    BASE_DEFENSE_WARP_RANGE(9, LovelyRobotID.ATTR_BASE_DEFENSE_WARP);

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