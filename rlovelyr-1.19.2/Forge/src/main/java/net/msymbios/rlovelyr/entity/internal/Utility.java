package net.msymbios.rlovelyr.entity.internal;

import net.msymbios.rlovelyr.entity.enums.EntityState;
import net.msymbios.rlovelyr.entity.enums.EntityTexture;
import net.msymbios.rlovelyr.entity.enums.EntityVariant;

public class Utility {

    // -- Variables --
    public static final double PI = 3.14159265358979323846;

    // -- Methods --
    public static boolean invertBoolean(boolean value) {
        return !value;
    } // invertBoolean ()

    // TRANSLATABLE
    public static String getTranslatable(EntityVariant variant){
        String value = "entity.rlovelyr.bunny";
        switch (variant) {
            case Bunny: value = "entity.rlovelyr.bunny"; break;
            case Bunny2: value = "entity.rlovelyr.bunny2"; break;
            case Dragon: value = "entity.rlovelyr.dragon"; break;
            case Honey: value = "entity.rlovelyr.honey"; break;
            case Kitsune: value = "entity.rlovelyr.kitsune"; break;
            case Neko: value = "entity.rlovelyr.neko"; break;
            case Vanilla: value = "entity.rlovelyr.vanilla"; break;
        }
        return value;
    } // getTranslatable ()

    public static String getTranslatable(EntityState state){
        String value = "msg.rlovelyr.follow";
        switch (state) {
            case Follow: value = "msg.rlovelyr.follow"; break;
            case BaseDefense: value = "msg.rlovelyr.base_defence"; break;
            case Standby: value = "msg.rlovelyr.standby"; break;
        }
        return value;
    } // getTranslatable ()

    public static String getTranslatable(EntityTexture translatable){
        String value = "msg.item.random";
        switch (translatable) {
            case RANDOM:  value = "msg.item.random"; break;
            case WHITE: value = "msg.item.white"; break;
            case ORANGE: value = "msg.item.orange"; break;
            case MAGENTA: value = "msg.item.magenta"; break;
            case LIGHT_BLUE: value = "msg.item.light_blue"; break;
            case YELLOW: value = "msg.item.yellow"; break;
            case LIME: value = "msg.item.lime"; break;
            case PINK: value = "msg.item.pink"; break;
            case GRAY: value = "msg.item.gray"; break;
            case LIGHT_GRAY: value = "msg.item.light_gray"; break;
            case CYAN: value = "msg.item.cyan"; break;
            case PURPLE: value = "msg.item.purple"; break;
            case BLUE: value = "msg.item.blue"; break;
            case BROWN: value = "msg.item.brown"; break;
            case GREEN: value = "msg.item.green"; break;
            case RED: value = "msg.item.red"; break;
            case BLACK: value = "msg.item.black"; break;
        }
        return value;
    } // getTranslatable ()

} // Class Utility