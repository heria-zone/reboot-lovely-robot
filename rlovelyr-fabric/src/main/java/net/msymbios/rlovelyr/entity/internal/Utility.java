package net.msymbios.rlovelyr.entity.internal;

import java.util.Random;

public class Utility {

    // -- Variables --
    private static final Random rand = new Random();

    // -- Methods --
    public static int getRandomNumber(int number) {
        return rand.nextInt(number);
    } // getRandomNumber ()

    public static boolean invertBoolean(boolean value) {
        return value = !value;
    } // invertBoolean ()

} // Class Utility