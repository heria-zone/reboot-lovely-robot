package net.msymbios.rlovelyr.entity.enums;

import java.util.Arrays;
import java.util.Comparator;

public enum EntityTexture {

    // -- Enum --
    WHITE(0, "white"),
    ORANGE(1, "orange"),
    MAGENTA(2, "magenta"),
    LIGHT_BLUE(3, "light_blue"),
    YELLOW(4, "yellow"),
    LIME(5, "lime"),
    PINK(6, "pink"),
    GRAY(7, "gray"),
    LIGHT_GRAY(8, "light_gray"),
    CYAN(9, "cyan"),
    PURPLE(10, "purple"),
    BLUE(11, "blue"),
    BROWN(12, "brown"),
    GREEN(13, "green"),
    RED(14, "red"),
    BLACK(15, "black"),
    RANDOM(16, "random");


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