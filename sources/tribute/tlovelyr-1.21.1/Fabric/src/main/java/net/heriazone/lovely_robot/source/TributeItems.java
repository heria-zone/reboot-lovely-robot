package net.heriazone.lovely_robot.source;

import net.heriazone.lovely_robot.Tribute;
import net.heriazone.lovely_robot.TributeIdentifier;
import net.heriazone.lovelylib.common.items.LovelyCoreItem;
import net.heriazone.lovelylib.common.items.LovelySpawnItem;
import net.heriazone.lovelylib.common.entity.definition.RobotVariant;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.hzlib.api.items.InternalItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * Centralizes item registration for Tribute variant robots.
 * <p>
 * <b>Architecture:</b> Uses Fabric's direct Registry system to register items
 * during mod initialization. Separates robot cores from spawn items while
 * maintaining consistent registration flow.
 * <p>
 * <b>Item Categories:</b> Robot cores (crafting materials) and spawn eggs
 * (entity summoning) with NBT-based customization support.
 */
public class TributeItems extends InternalItems {

    // -- Variables --

    // MISCELLANEOUS
    public static final Item ROBOT_CORE = registerItem(LovelyConstant.ROBOT_CORE, Rarity.UNCOMMON, 1);

    // SPAWNS
    public static final Item BUNNY_SPAWN   = registerItem(RobotVariant.Bunny.getName()   + "_spawn", TributeEntities.BUNNY,   Rarity.RARE, 1);
    public static final Item BUNNY2_SPAWN  = registerItem(RobotVariant.Bunny2.getName()  + "_spawn", TributeEntities.BUNNY2,  Rarity.RARE, 1);
    public static final Item HONEY_SPAWN   = registerItem(RobotVariant.Honey.getName()   + "_spawn", TributeEntities.HONEY,   Rarity.RARE, 1);
    public static final Item VANILLA_SPAWN = registerItem(RobotVariant.Vanilla.getName() + "_spawn", TributeEntities.VANILLA, Rarity.RARE, 1);

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
        return register(TributeIdentifier.getId(name), new LovelyCoreItem(new Item.Properties().rarity(rarity).fireResistant().stacksTo(stack)));
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
        return register(TributeIdentifier.getId(name), new LovelySpawnItem(mob, new Item.Properties().rarity(rarity).fireResistant().stacksTo(stack)));
    } // registerItem()

    /**
     * Registers items and adds them to creative tabs.
     * <p>
     * <b>Timing:</b> Must be called during mod initialization.
     */
    public static void register() {
        Tribute.LOGGER.info("Registering Items: " + Tribute.MODID);
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
        registerModel(TributeItems.BUNNY_SPAWN, LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
        registerModel(TributeItems.BUNNY2_SPAWN, LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
        registerModel(TributeItems.HONEY_SPAWN, LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
        registerModel(TributeItems.VANILLA_SPAWN, LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
    } // registerModel()

} // Class: TributeItems