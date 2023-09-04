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
            case Bunny -> value = "entity.rlovelyr.bunny";
            case Bunny2 -> value = "entity.rlovelyr.bunny2";
            case Dragon -> value = "entity.rlovelyr.dragon";
            case Honey -> value = "entity.rlovelyr.honey";
            case Kitsune -> value = "entity.rlovelyr.kitsune";
            case Neko -> value = "entity.rlovelyr.neko";
            case Vanilla -> value = "entity.rlovelyr.vanilla";
        }
        return value;
    } // getTranslatableEntity ()

    public static String getTranslatableState(EntityState state){
        String value = "msg.rlovelyr.follow";
        switch (state) {
            case Follow -> value = "msg.rlovelyr.follow";
            case BaseDefense -> value = "msg.rlovelyr.base_defence";
            case Standby -> value = "msg.rlovelyr.standby";
        }
        return value;
    } // getTranslatableEntity ()

} // Class Utility