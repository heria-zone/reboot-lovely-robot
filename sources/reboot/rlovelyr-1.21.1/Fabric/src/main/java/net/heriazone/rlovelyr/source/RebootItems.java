package net.heriazone.rlovelyr.source;

import net.heriazone.rlovelyr.Reboot;
import net.heriazone.rlovelyr.RebootIdentifier;
import net.heriazone.lovelylib.common.entity.definition.ModTarget;
import net.heriazone.lovelylib.common.entity.definition.RobotDefinitionRegistry;
import net.heriazone.lovelylib.common.entity.definition.RobotEntityDefinition;
import net.heriazone.lovelylib.common.entity.definition.RobotVariant;
import net.heriazone.lovelylib.common.items.LovelyCoreItem;
import net.heriazone.lovelylib.common.items.LovelySpawnItem;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.hzlib.api.items.InternalItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Centralizes item registration for Reboot variant robots (Fabric).
 * <p>
 * <b>Architecture:</b> Registry-driven map replaces 8 named static fields.
 * Mirrors LegacyItems Fabric — only namespace (RebootIdentifier) differs.
 */
public class RebootItems extends InternalItems {

    // -- State --

    private static final Map<RobotVariant, Item> spawnItems = new HashMap<>();

    // -- Static Items --

    public static final Item ROBOT_CORE = registerCoreItem(LovelyConstant.ROBOT_CORE, Rarity.UNCOMMON, 1);
    public static final Item ROBOT_CORE_ALDARIAN = registerCoreItem(LovelyConstant.ROBOT_CORE_ALDARIAN, Rarity.EPIC, 1);

    // -- Registration --

    public static void register() {
        registerAll();
        Reboot.LOGGER.info("Registering Items: " + Reboot.MODID);
    } // register()

    private static void registerAll() {
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.REBOOT)) {
            Item item = registerSpawnItem(def.getSpawnItemKey(),
                RebootEntities.getEntityType(def.getVariant()), Rarity.RARE, 1);
            spawnItems.put(def.getVariant(), item);
        }
    } // registerAll()

    // -- Access --

    public static Item getSpawnItem(RobotVariant variant) {
        return Objects.requireNonNull(spawnItems.get(variant),
            "No spawn item registered for Reboot variant: " + variant);
    } // getSpawnItem()

    // -- Client --

    public static void registerModel() {
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.REBOOT)) {
            registerModel(getSpawnItem(def.getVariant()),
                LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
        }
    } // registerModel()

    // -- Helpers --

    private static Item registerCoreItem(String name, Rarity rarity, int stack) {
        return register(RebootIdentifier.getId(name),
            new LovelyCoreItem(new Item.Properties().rarity(rarity).fireResistant().stacksTo(stack)));
    } // registerCoreItem()

    private static Item registerSpawnItem(String name, EntityType<? extends Mob> mob,
                                          Rarity rarity, int stack) {
        return register(RebootIdentifier.getId(name),
            new LovelySpawnItem(mob, new Item.Properties().rarity(rarity).fireResistant().stacksTo(stack)));
    } // registerSpawnItem()

} // Class: RebootItems
