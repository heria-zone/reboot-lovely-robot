package net.msymbios.rlovelyr.entity.internal;

import net.msymbios.rlovelyr.entity.enums.EntityState;
import net.msymbios.rlovelyr.entity.enums.EntityVariant;

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

    public static String getTranslatableEntity(EntityVariant variant){
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
    } // getTranslatableEntity ()

    public static String getTranslatableState(EntityState state){
        String value = "msg.rlovelyr.follow";
        switch (state) {
            case Follow: value = "msg.rlovelyr.follow"; break;
            case BaseDefense: value = "msg.rlovelyr.base_defence"; break;
            case Standby: value = "msg.rlovelyr.standby"; break;
        }
        return value;
    } // getTranslatableEntity ()

} // Class Utility