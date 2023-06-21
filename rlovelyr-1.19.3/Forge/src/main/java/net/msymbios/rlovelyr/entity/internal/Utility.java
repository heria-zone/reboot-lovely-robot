package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Random;

public class Utility {

    // -- Variables --
    private static final Random rand = new Random();

    // -- Interact --
    public static boolean canInteract(ItemStack itemStack){
        if(itemStack.is(Items.WHITE_DYE) ||itemStack.is(Items.ORANGE_DYE) || itemStack.is(Items.MAGENTA_DYE) || itemStack.is(Items.LIGHT_BLUE_DYE) ||
                itemStack.is(Items.YELLOW_DYE) || itemStack.is(Items.LIME_DYE) || itemStack.is(Items.PINK_DYE) || itemStack.is(Items.GRAY_DYE) ||
                itemStack.is(Items.LIGHT_GRAY_DYE) || itemStack.is(Items.CYAN_DYE) || itemStack.is(Items.PURPLE_DYE) || itemStack.is(Items.BLUE_DYE) ||
                itemStack.is(Items.BROWN_DYE) || itemStack.is(Items.GREEN_DYE) || itemStack.is(Items.RED_DYE) || itemStack.is(Items.BLACK_DYE)) return false;
        if(itemStack.is(Items.WOODEN_SWORD) || itemStack.is(Items.STONE_SWORD) || itemStack.is(Items.IRON_SWORD) || itemStack.is(Items.GOLDEN_SWORD) || itemStack.is(Items.DIAMOND_SWORD) || itemStack.is(Items.NETHERITE_SWORD)) return false;
        if(itemStack.is(Items.STICK) || itemStack.is(Items.BOOK)) return false;
        return !itemStack.is(Items.COMPASS) && !itemStack.is(Items.RECOVERY_COMPASS);
    } // canInteract ()

    public static boolean canInteractGuardMode(ItemStack itemStack){
        return itemStack.is(Items.COMPASS) || itemStack.is(Items.RECOVERY_COMPASS);
    } // canInteractGuardMode ()

    public static boolean canInteractAutoAttack(ItemStack itemStack) {
        return itemStack.is(Items.WOODEN_SWORD) || itemStack.is(Items.STONE_SWORD) || itemStack.is(Items.IRON_SWORD) || itemStack.is(Items.GOLDEN_SWORD) || itemStack.is(Items.DIAMOND_SWORD) || itemStack.is(Items.NETHERITE_SWORD);
    } // canInteractAutoAttack ()

    // -- Methods --
    public static int getRandomNumber(int number) {
        return rand.nextInt(number);
    } // getRandomNumber ()

    public static boolean invertBoolean(boolean value) {
        return value = !value;
    } // invertBoolean ()

} // Class Utility