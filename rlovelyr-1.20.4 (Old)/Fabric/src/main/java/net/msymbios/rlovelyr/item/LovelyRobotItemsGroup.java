package net.msymbios.rlovelyr.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.config.LovelyRobotID;

public class LovelyRobotItemsGroup {

    // -- Variables --
    public static ItemGroup LOVELY_ROBOT = register(LovelyRobotID.DEFAULT_GROUP, register(LovelyRobotID.TRANS_ITEM_GROUP));

    // -- Methods --

    /**
     * Registers an item group with the specified name.
     *
     * @param name The name of the item group
     * @param itemGroup The item group to register
     * @return The registered item group
     * */
    private static ItemGroup register(String name, ItemGroup itemGroup) {
        // Register the item group using the provided name and item group
        return Registry.register(Registries.ITEM_GROUP, LovelyRobotID.getId(name), itemGroup);
    } // register ()

    /**
     * Registers an item group with the specified translatable name.
     *
     * @param translatable The translatable name of the item group
     * @return The registered item group
     * */
    private static ItemGroup register(String translatable) {
        // Define the item group with the provided translatable name and icon
        return FabricItemGroup.builder().displayName(Text.translatable(translatable)).icon(() -> new ItemStack(LovelyRobotItems.BUNNY_SPAWN)).entries(((displayContext, entries) -> {
            // Add specific items to the item group entries
            entries.add(LovelyRobotItems.ROBOT_CORE);
            entries.add(LovelyRobotItems.BUNNY_SPAWN);
            entries.add(LovelyRobotItems.BUNNY2_SPAWN);
            entries.add(LovelyRobotItems.DRAGON_SPAWN);
            entries.add(LovelyRobotItems.HONEY_SPAWN);
            entries.add(LovelyRobotItems.KITSUNE_SPAWN);
            entries.add(LovelyRobotItems.NEKO_SPAWN);
            entries.add(LovelyRobotItems.VANILLA_SPAWN);
        })).build();
    } // register ()

    public static void register () {
        LovelyRobot.LOGGER.info("Registering ItemGroups for: " + LovelyRobot.MODID);
    } // register ()

} // Class LovelyRobotItemsGroup