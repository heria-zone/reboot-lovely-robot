package net.msymbios.rlovelyr.entity.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;

public class ModUtils {

    // -- Interact --
    public static boolean canInteract(ItemStack itemStack){
        if(itemStack.isOf(Items.WHITE_DYE) ||itemStack.isOf(Items.ORANGE_DYE) || itemStack.isOf(Items.MAGENTA_DYE) || itemStack.isOf(Items.LIGHT_BLUE_DYE) ||
                itemStack.isOf(Items.YELLOW_DYE) || itemStack.isOf(Items.LIME_DYE) || itemStack.isOf(Items.PINK_DYE) || itemStack.isOf(Items.GRAY_DYE) ||
                itemStack.isOf(Items.LIGHT_GRAY_DYE) || itemStack.isOf(Items.CYAN_DYE) || itemStack.isOf(Items.PURPLE_DYE) || itemStack.isOf(Items.BLUE_DYE) ||
                itemStack.isOf(Items.BROWN_DYE) || itemStack.isOf(Items.GREEN_DYE) || itemStack.isOf(Items.RED_DYE) || itemStack.isOf(Items.BLACK_DYE)) return false;
        if(itemStack.isOf(Items.WOODEN_SWORD) || itemStack.isOf(Items.STONE_SWORD) || itemStack.isOf(Items.IRON_SWORD) || itemStack.isOf(Items.GOLDEN_SWORD) || itemStack.isOf(Items.DIAMOND_SWORD) || itemStack.isOf(Items.NETHERITE_SWORD)) return false;
        if(itemStack.isOf(Items.STICK) || itemStack.isOf(Items.BOOK)) return false;
        return !itemStack.isOf(Items.COMPASS) && !itemStack.isOf(Items.RECOVERY_COMPASS);
    } // canInteract ()

    public static boolean canInteractGuardMode(ItemStack itemStack){
        return itemStack.isOf(Items.COMPASS) || itemStack.isOf(Items.RECOVERY_COMPASS);
    } // canInteractGuardMode ()

    public static boolean canInteractAutoAttack(ItemStack itemStack) {
        return itemStack.isOf(Items.WOODEN_SWORD) || itemStack.isOf(Items.STONE_SWORD) || itemStack.isOf(Items.IRON_SWORD) || itemStack.isOf(Items.GOLDEN_SWORD) || itemStack.isOf(Items.DIAMOND_SWORD) || itemStack.isOf(Items.NETHERITE_SWORD);
    } // canInteractAutoAttack ()


    // -- Functions --
    public static int getRandomNumber(int number) {
        return Random.createLocal().nextInt(number);
    } // getRandomNumber ()

    public static boolean invertBoolean(boolean value) {
        return value = !value;
    } // invertBoolean ()

} // Class ModUtils