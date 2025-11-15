package net.msymbios.rlovelyr.entity.internal.enums;

import net.msymbios.rlovelyr.config.LovelyRobotID;

import java.util.Arrays;
import java.util.Comparator;

public enum EntityModel {

    // -- Enum --
    Default(0, LovelyRobotID.MOD_DEFAULT),
    Armed(1, LovelyRobotID.MOD_ARMED);

    // -- Variables --
    private static final EntityModel[] CODEC = Arrays.stream(values()).sorted(Comparator.comparingInt(EntityModel::getId)).toArray(EntityModel[]::new);

    private final int m_id;

    private final String m_name;

    // -- Constructor --
    EntityModel(int id, String name) {
        this.m_id = id;
        this.m_name = name;
    } // Constructor RobotModel

    // -- Methods --
    public static EntityModel byId(int id) {
        if (id < 0 || id >= CODEC.length) id = 0;
        return CODEC[id];
    } // byId ()

    public int getId() {
        return this.m_id;
    } // getId ()

    public static EntityModel byName(String name) {
        for (EntityModel item : CODEC) {
            if (item.getName().equals(name))
                return item;
        }
        return null; // or throw an exception if the name is not found
    } // byName ()

    public String getName() {
        return this.m_name;
    } // getName ()

} // Enum EntityModel