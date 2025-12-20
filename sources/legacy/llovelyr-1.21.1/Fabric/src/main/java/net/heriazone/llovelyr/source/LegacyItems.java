package net.heriazone.llovelyr.source;

import net.heriazone.llovelyr.Legacy;
import net.heriazone.llovelyr.LegacyIdentifier;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.lovelylib.common.items.LovelyCoreItem;
import net.heriazone.lovelylib.common.items.LovelySpawnItem;
import net.heriazone.lovelylib.hzlib.api.items.InternalItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * Centralizes item registration for Legacy variant robots.
 * <p>
 * <b>Architecture:</b> Uses Fabric's direct Registry system to register items
 * during mod initialization. Separates robot cores from spawn items while
 * maintaining consistent registration flow.
 * <p>
 * <b>Item Categories:</b> Robot cores (crafting materials) and spawn eggs
 * (entity summoning) with NBT-based customization support.
 */
public class LegacyItems extends InternalItems {

    // -- Variables --

    // MISCELLANEOUS
    public static final Item ROBOT_CORE = registerItem(LovelyConstant.ROBOT_CORE, Rarity.UNCOMMON, 1);

    // SPAWNS
    public static final Item BUNNY_SPAWN = registerItem(LovelyConstant.BUNNY_SPAWN, LegacyEntities.BUNNY, Rarity.RARE, 1);
    public static final Item BUNNY2_SPAWN = registerItem(LovelyConstant.BUNNY2_SPAWN, LegacyEntities.BUNNY2, Rarity.RARE, 1);
    public static final Item DRAGON_SPAWN = registerItem(LovelyConstant.DRAGON_SPAWN, LegacyEntities.DRAGON, Rarity.RARE, 1);
    public static final Item HONEY_SPAWN = registerItem(LovelyConstant.HONEY_SPAWN, LegacyEntities.HONEY, Rarity.RARE, 1);
    public static final Item KITSUNE_SPAWN = registerItem(LovelyConstant.KITSUNE_SPAWN, LegacyEntities.KITSUNE, Rarity.RARE, 1);
    public static final Item NEKO_SPAWN = registerItem(LovelyConstant.NEKO_SPAWN, LegacyEntities.NEKO, Rarity.RARE, 1);
    public static final Item VANILLA_SPAWN = registerItem(LovelyConstant.VANILLA_SPAWN, LegacyEntities.VANILLA, Rarity.RARE, 1);

    // -- Methods --

    /**
     * Registers robot core item with specified properties.
     * <p>
     * <b>Usage:</b> For crafting materials and robot storage items.
     *
     * @param name the registry name
     * @param rarity the item rarity level
     * @param stack the maximum stack size
     * @return the registered item instance
     */
    private static Item registerItem(String name, Rarity rarity, int stack) {
        return register(LegacyIdentifier.getId(name), new LovelyCoreItem(new Item.Properties().rarity(rarity).fireResistant().stacksTo(stack)));
    } // registerItem()

    /**
     * Registers spawn egg item for robot entity.
     * <p>
     * <b>NBT Support:</b> Spawn eggs can store robot customization data (color,
     * level, name) which transfers to spawned entity.
     *
     * @param name the registry name
     * @param mob the entity type
     * @param rarity the item rarity level
     * @param stack the maximum stack size
     * @return the registered spawn egg instance
     */
    private static Item registerItem(String name, EntityType<? extends Mob> mob, Rarity rarity, int stack) {
        return register(LegacyIdentifier.getId(name), new LovelySpawnItem(mob, new Item.Properties().rarity(rarity).fireResistant().stacksTo(stack)));
    } // registerItem()

    /**
     * Registers items and adds them to creative tabs.
     * <p>
     * <b>Timing:</b> Must be called during mod initialization.
     */
    public static void register() {
        Legacy.LOGGER.info("Registering Items: " + Legacy.MODID);
    } // register()

    /**
     * Registers client-side model predicates for dynamic item appearance.
     * <p>
     * <b>Architecture:</b> Enables NBT-based model switching for robot color
     * variants. Each spawn egg can display different textures based on stored
     * color value in item NBT.
     * <p>
     * <b>Timing:</b> Must be called during client initialization after items
     * are registered.
     */
    public static void registerModel() {
        registerModel(LegacyItems.BUNNY_SPAWN, LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
        registerModel(LegacyItems.BUNNY2_SPAWN, LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
        registerModel(LegacyItems.DRAGON_SPAWN, LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
        registerModel(LegacyItems.HONEY_SPAWN, LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
        registerModel(LegacyItems.KITSUNE_SPAWN, LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
        registerModel(LegacyItems.NEKO_SPAWN, LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
        registerModel(LegacyItems.VANILLA_SPAWN, LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
    } // registerModel()

} // Class: LegacyItems