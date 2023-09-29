package net.msymbios.rlovelyr.entity.internal;

import net.msymbios.rlovelyr.entity.enums.*;

public class Utility {

    // -- Variables --
    public static final double PI = 3.14159265358979323846;

    // -- Methods --
    public static boolean invertBoolean(boolean value) {
        return !value;
    } // invertBoolean ()

    public static String FirstToUpperCase(String value) {
        String letterToUpper = value.substring(0, 1);  // Get the first character
        return letterToUpper.toUpperCase() + value.substring(1);
    } // FirstToUpperCase ()

    // TRANSLATABLE
    public static String getTranslatable(EntityVariant translatable){
        String value = "entity.rlovelyr.bunny";
        switch (translatable) {
            case Bunny -> value = "entity.rlovelyr.bunny";
            case Bunny2 -> value = "entity.rlovelyr.bunny2";
            case Dragon -> value = "entity.rlovelyr.dragon";
            case Honey -> value = "entity.rlovelyr.honey";
            case Kitsune -> value = "entity.rlovelyr.kitsune";
            case Neko -> value = "entity.rlovelyr.neko";
            case Vanilla -> value = "entity.rlovelyr.vanilla";
        }
        return value;
    } // getTranslatable ()

    public static String getTranslatable(EntityState translatable){
        String value = "msg.rlovelyr.follow";
        switch (translatable) {
            case Follow -> value = "msg.rlovelyr.follow";
            case BaseDefense -> value = "msg.rlovelyr.base_defence";
            case Standby -> value = "msg.rlovelyr.standby";
        }
        return value;
    } // getTranslatable ()

    public static String getTranslatable(EntityTexture translatable){
        String value = "msg.item.pink";
        switch (translatable) {
            case RANDOM -> value = "msg.item.random";
            case WHITE -> value = "msg.item.white";
            case ORANGE -> value = "msg.item.orange";
            case MAGENTA -> value = "msg.item.magenta";
            case LIGHT_BLUE -> value = "msg.item.light_blue";
            case YELLOW -> value = "msg.item.yellow";
            case LIME -> value = "msg.item.lime";
            case PINK -> value = "msg.item.pink";
            case GRAY -> value = "msg.item.gray";
            case LIGHT_GRAY -> value = "msg.item.light_gray";
            case CYAN -> value = "msg.item.cyan";
            case PURPLE -> value = "msg.item.purple";
            case BLUE -> value = "msg.item.blue";
            case BROWN -> value = "msg.item.brown";
            case GREEN -> value = "msg.item.green";
            case RED -> value = "msg.item.red";
            case BLACK -> value = "msg.item.black";
        }
        return value;
    } // getTranslatable ()

} // Class Utility