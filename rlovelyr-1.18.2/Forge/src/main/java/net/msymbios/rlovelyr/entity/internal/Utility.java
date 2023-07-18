package net.msymbios.rlovelyr.entity.internal;

import java.util.Random;

public class Utility {

    // -- Variables --
    private static final Random rand = new Random();
    public static final double PI = 3.14159265358979323846;

    // -- Methods --
    public static int getRandomNumber(int number) {
        return rand.nextInt(number);
    } // getRandomNumber ()

    public static boolean invertBoolean(boolean value) {
        return !value;
    } // invertBoolean ()

    public static String FirstToUpperCase(String value) {
        String letterToUpper = value.substring(0, 1);  // Get the first character
        return letterToUpper.toUpperCase() + value.substring(1);
    } // FirstToUpperCase ()

} // Class Utility