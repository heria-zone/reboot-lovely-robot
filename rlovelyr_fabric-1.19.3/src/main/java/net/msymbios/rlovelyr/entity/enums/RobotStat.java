package net.msymbios.rlovelyr.entity.enums;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

import java.util.function.IntFunction;

public enum RobotStat implements StringIdentifiable {

    // -- Enum --
    MAX_HEALTH(0, "max_health"),
    MAX_LEVEL(1, "max_level"),
    ATTACK_DAMAGE(2, "attack_damage"),
    ATTACK_SPEED(3, "attack_speed"),
    MOVEMENT_SPEED(4, "movement_speed"),
    DEFENSE(5, "defense"),
    ARMOR(6, "armor"),
    ARMOR_TOUGHNESS(7, "armor_toughness");


    // -- Variables --
    public static final Codec<RobotStat> CODEC = StringIdentifiable.createCodec(RobotStat::values);
    private static final IntFunction<RobotStat> BY_ID = ValueLists.createIdToValueFunction(RobotStat::getId, RobotStat.values(), ValueLists.OutOfBoundsHandling.WRAP);;
    private final int id;
    private final String name;


    // -- Constructor --
    RobotStat(int id, String name) {
        this.id = id;
        this.name = name;
    } // Constructor RobotStat


    // -- Methods --
    public int getId() {
        return this.id;
    } // getId ()

    public static RobotStat byId(int id) {
        return BY_ID.apply(id);
    } // byId ()

    public String getName() {
        return this.name;
    } // getId ()

    public static RobotStat byName(String name) {
        return CODEC.byId(name);
    } // byId ()

    @Override
    public String asString() {
        return this.name;
    } // asString ()

} // Enum RobotStat