package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.util.math.random.Random;

public class Utility {

    // -- Methods --
    public static int getRandomNumber(int number) {
        return Random.createLocal().nextInt(number);
    } // getRandomNumber ()

    public static boolean invertBoolean(boolean value) {
        return value = !value;
    } // invertBoolean ()

} // Class ModUtils