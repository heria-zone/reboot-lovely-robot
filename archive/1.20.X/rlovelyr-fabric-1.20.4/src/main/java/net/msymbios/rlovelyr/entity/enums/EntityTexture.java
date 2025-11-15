package net.msymbios.rlovelyr.entity.enums;

import net.msymbios.rlovelyr.config.LovelyRobotID;

import java.util.Arrays;
import java.util.Comparator;

public enum EntityTexture {

    // -- Enum --
    WHITE(0, LovelyRobotID.TEX_WHITE),
    ORANGE(1, LovelyRobotID.TEX_ORANGE),
    MAGENTA(2, LovelyRobotID.TEX_MAGENTA),
    LIGHT_BLUE(3, LovelyRobotID.TEX_LIGHT_BLUE),
    YELLOW(4, LovelyRobotID.TEX_YELLOW),
    LIME(5, LovelyRobotID.TEX_LIME),
    PINK(6, LovelyRobotID.TEX_PINK),
    GRAY(7, LovelyRobotID.TEX_GRAY),
    LIGHT_GRAY(8, LovelyRobotID.TEX_LIGHT_GRAY),
    CYAN(9, LovelyRobotID.TEX_CYAN),
    PURPLE(10, LovelyRobotID.TEX_PURPLE),
    BLUE(11, LovelyRobotID.TEX_BLUE),
    BROWN(12, LovelyRobotID.TEX_BROWN),
    GREEN(13, LovelyRobotID.TEX_GREEN),
    RED(14, LovelyRobotID.TEX_RED),
    BLACK(15, LovelyRobotID.TEX_BLACK),
    RANDOM(16, LovelyRobotID.TEX_RANDOM);

    // -- Variables --
    private static final EntityTexture[] CODEC = Arrays.stream(values()).sorted(Comparator.comparingInt(EntityTexture::getId)).toArray(EntityTexture[]::new);

    private final int m_id;

    private final String m_name;

    // -- Constructor --
    EntityTexture(int id, String name) {
        this.m_id = id;
        this.m_name = name;
    } // Constructor RobotTexture

    // -- Methods --
    public static EntityTexture byId(int id) {
        if (id < 0 || id >= CODEC.length) id = 0;
        return CODEC[id];
    } // byId ()

    public int getId() {
        return this.m_id;
    } // getId ()

    public static EntityTexture byName(String name) {
        for (EntityTexture item : CODEC) {
            if (item.getName().equals(name))
                return item;
        }
        return null; // or throw an exception if the name is not found
    } // byName ()

    public String getName() {
        return this.m_name;
    } // getName ()

} // Enum EntityTexture