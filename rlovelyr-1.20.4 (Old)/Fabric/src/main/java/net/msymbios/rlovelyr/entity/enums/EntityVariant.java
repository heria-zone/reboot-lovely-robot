package net.msymbios.rlovelyr.entity.enums;

import net.msymbios.rlovelyr.config.LovelyRobotID;

import java.util.Arrays;
import java.util.Comparator;

public enum EntityVariant {

    // -- Enum --
    Bunny(0, LovelyRobotID.BUNNY),
    Bunny2(1, LovelyRobotID.BUNNY2),
    Dragon(2, LovelyRobotID.DRAGON),
    Honey(3, LovelyRobotID.HONEY),
    Kitsune(4, LovelyRobotID.KITSUNE),
    Neko(5, LovelyRobotID.NEKO),
    Vanilla(6, LovelyRobotID.VANILLA);

    // -- Variables --
    private static final EntityVariant[] CODEC = Arrays.stream(values()).sorted(Comparator.comparingInt(EntityVariant::getId)).toArray(EntityVariant[]::new);

    private final int m_id;

    private final String m_name;

    // -- Constructor --
    EntityVariant(int id, String name) {
        this.m_id = id;
        this.m_name = name;
    } // Constructor VanillaVariant

    // -- Methods --
    public static EntityVariant byId(int id) {
        if (id < 0 || id >= CODEC.length) id = 0;
        return CODEC[id];
    } // byId ()

    public int getId() {
        return this.m_id;
    } // getId ()

    public static EntityVariant byName(String name) {
        for (EntityVariant item : CODEC) {
            if (item.getName().equals(name))
                return item;
        }
        return null; // or throw an exception if the name is not found
    } // byName ()

    public String getName() {
        return this.m_name;
    } // getId ()

} // Enum EntityVariant