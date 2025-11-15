package net.msymbios.rlovelyr.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.Rarity;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.common.item.InternalItems;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.item.custom.RobotCoreItem;
import net.msymbios.rlovelyr.item.custom.SpawnItem;

public class LovelyRobotItems extends InternalItems {

    // -- Variables --

    // MISCELLANEOUS
    public static final Item ROBOT_CORE = registerItem(LovelyRobotID.ROBOT_CORE, Rarity.UNCOMMON, 1);
    public static final Item ROBOT_CORE_ALDARIAN = registerItem(LovelyRobotID.ROBOT_CORE_ALDARIAN, Rarity.UNCOMMON, 1);

    // SPAWNS
    public static final Item BUNNY_SPAWN = registerItem(LovelyRobotID.BUNNY_SPAWN, LovelyRobotEntities.BUNNY, Rarity.RARE, 1);
    public static final Item BUNNY2_SPAWN = registerItem(LovelyRobotID.BUNNY2_SPAWN, LovelyRobotEntities.BUNNY2, Rarity.RARE, 1);
    public static final Item DRAGON_SPAWN = registerItem(LovelyRobotID.DRAGON_SPAWN, LovelyRobotEntities.DRAGON, Rarity.RARE, 1);
    public static final Item HONEY_SPAWN = registerItem(LovelyRobotID.HONEY_SPAWN, LovelyRobotEntities.HONEY, Rarity.RARE, 1);
    public static final Item KITSUNE_SPAWN = registerItem(LovelyRobotID.KITSUNE_SPAWN, LovelyRobotEntities.KITSUNE, Rarity.RARE, 1);
    public static final Item NEKO_SPAWN = registerItem(LovelyRobotID.NEKO_SPAWN, LovelyRobotEntities.NEKO, Rarity.RARE, 1);
    public static final Item VANILLA_SPAWN = registerItem(LovelyRobotID.VANILLA_SPAWN, LovelyRobotEntities.VANILLA, Rarity.RARE, 1);

    public static final Item PRIME_SPAWN = registerItem(LovelyRobotID.PRIME_SPAWN, LovelyRobotEntities.PRIME, Rarity.RARE, 1);
    public static final Item HYPERION_SPAWN = registerItem(LovelyRobotID.HYPERION_SPAWN, LovelyRobotEntities.HYPERION, Rarity.RARE, 1);
    public static final Item EMPYRIUM_SPAWN = registerItem(LovelyRobotID.EMPYRIUM_SPAWN, LovelyRobotEntities.EMPYRIUM, Rarity.RARE, 1);

    public static final Item STINGER_SPAWN = registerItem(LovelyRobotID.STINGER_SPAWN, LovelyRobotEntities.STINGER, Rarity.RARE, 1);


    // -- Methods --

    public static Item registerItem (String name, Rarity rarity, int stack) {
        return register(LovelyRobotID.getId(name), new RobotCoreItem(new FabricItemSettings().rarity(rarity).fireproof().maxCount(stack)));
    } // registerItem ()

    public static Item registerItem (String name, EntityType<? extends MobEntity> mob, Rarity rarity, int stack) {
        return register(LovelyRobotID.getId(name), new SpawnItem(mob, new FabricItemSettings().rarity(rarity).fireproof().maxCount(stack)));
    } // registerItem ()

    public static void register() {
        LovelyRobot.LOGGER.info("Registering Items: " + LovelyRobot.MODID );

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(LovelyRobotItems::spawnEggItemsEntry);
        ItemGroupEvents.modifyEntriesEvent(LovelyRobotItemsGroup.DEFAULT_KEY).register(LovelyRobotItems::allItemsEntry);
        ItemGroupEvents.modifyEntriesEvent(LovelyRobotItemsGroup.ALDARIAN_TECH_KEY).register(LovelyRobotItems::addItemsToAldarianTechTab);
    } // register ()

    private static void spawnEggItemsEntry(FabricItemGroupEntries entries) {
        entries.add(BUNNY_SPAWN);
        entries.add(BUNNY2_SPAWN);
        entries.add(DRAGON_SPAWN);
        entries.add(HONEY_SPAWN);
        entries.add(KITSUNE_SPAWN);
        entries.add(NEKO_SPAWN);
        entries.add(VANILLA_SPAWN);
    } // spawnEggItemsEntry ()

    private static void allItemsEntry(FabricItemGroupEntries entries) {
        entries.add(ROBOT_CORE);

        entries.add(BUNNY_SPAWN);
        entries.add(BUNNY2_SPAWN);
        entries.add(DRAGON_SPAWN);
        entries.add(HONEY_SPAWN);
        entries.add(KITSUNE_SPAWN);
        entries.add(NEKO_SPAWN);
        entries.add(VANILLA_SPAWN);
        entries.add(STINGER_SPAWN);
    } // allItemsEntry ()

    protected static void addItemsToAldarianTechTab(FabricItemGroupEntries entries) {
        entries.add(PRIME_SPAWN);
        entries.add(HYPERION_SPAWN);
        entries.add(EMPYRIUM_SPAWN);
    } // addItemsToAldarianTechTab ()

    public static void registerModels() {
        ModelPredicateProviderRegistry.register(LovelyRobotID.getId(LovelyRobotID.ITEM_TAG_VARIANT), (stack, world, entity, seed) -> stack.getNbt() != null && stack.getNbt().contains(LovelyRobotID.STAT_COLOR) ? stack.getNbt().getInt(LovelyRobotID.STAT_COLOR) : 16);

        // Register the model for each item
        registerModel(LovelyRobotItems.BUNNY_SPAWN, LovelyRobotID.getId(LovelyRobotID.ITEM_TAG_VARIANT), LovelyRobotID.STAT_COLOR);
        registerModel(LovelyRobotItems.BUNNY2_SPAWN, LovelyRobotID.getId(LovelyRobotID.ITEM_TAG_VARIANT), LovelyRobotID.STAT_COLOR);
        registerModel(LovelyRobotItems.DRAGON_SPAWN, LovelyRobotID.getId(LovelyRobotID.ITEM_TAG_VARIANT), LovelyRobotID.STAT_COLOR);
        registerModel(LovelyRobotItems.HONEY_SPAWN, LovelyRobotID.getId(LovelyRobotID.ITEM_TAG_VARIANT), LovelyRobotID.STAT_COLOR);
        registerModel(LovelyRobotItems.KITSUNE_SPAWN, LovelyRobotID.getId(LovelyRobotID.ITEM_TAG_VARIANT), LovelyRobotID.STAT_COLOR);
        registerModel(LovelyRobotItems.NEKO_SPAWN, LovelyRobotID.getId(LovelyRobotID.ITEM_TAG_VARIANT), LovelyRobotID.STAT_COLOR);
        registerModel(LovelyRobotItems.VANILLA_SPAWN, LovelyRobotID.getId(LovelyRobotID.ITEM_TAG_VARIANT), LovelyRobotID.STAT_COLOR);
    } // registerModels ()

} // Class LovelyRobotItems