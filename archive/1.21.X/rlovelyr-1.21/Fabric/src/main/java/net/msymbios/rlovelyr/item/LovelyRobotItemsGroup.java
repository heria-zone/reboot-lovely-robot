package net.msymbios.rlovelyr.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.config.LovelyRobotID;

public class LovelyRobotItemsGroup {

    // -- Variables --

    public static FabricItemGroup LOVELY_ROBOT = register(LovelyRobotID.DEFAULT_GROUP, register(LovelyRobotID.TRANS_ITEM_GROUP));

    // -- Methods --

    /**
     * Registers an item group with the specified name.
     *
     * @param name The name of the item group
     * @param itemGroup The item group to register
     * @return The registered item group
     * */
    private static FabricItemGroup register(String name, CreativeModeTab itemGroup) {
        // Register the item group using the provided name and item group
        return Registry.register(Registries.CREATIVE_MODE_TAB, LovelyRobotID.getId(name), itemGroup);
    } // register ()

    /**
     * Registers an item group with the specified translatable name.
     *
     * @param translatable The translatable name of the item group
     * @return The registered item group
     * */
    private static CreativeModeTab register(String translatable) {
        // Define the item group with the provided translatable name and icon
        return FabricItemGroup.builder().title(Component.translatable(translatable)).icon(() -> new ItemStack(LovelyRobotItems.BUNNY_SPAWN)).displayItems(((displayContext, entries) -> {
            // Add specific items to the item group entries
            entries.accept(LovelyRobotItems.ROBOT_CORE);
            entries.accept(LovelyRobotItems.BUNNY_SPAWN);
            entries.accept(LovelyRobotItems.BUNNY2_SPAWN);
            entries.accept(LovelyRobotItems.DRAGON_SPAWN);
            entries.accept(LovelyRobotItems.HONEY_SPAWN);
            entries.accept(LovelyRobotItems.KITSUNE_SPAWN);
            entries.accept(LovelyRobotItems.NEKO_SPAWN);
            entries.accept(LovelyRobotItems.VANILLA_SPAWN);
        })).build();
    } // register ()

    public static void register () {
        LovelyRobot.LOGGER.info("Registering Creative Tab for: " + LovelyRobot.MODID);
    } // register ()

} // Class LovelyRobotItemsGroup