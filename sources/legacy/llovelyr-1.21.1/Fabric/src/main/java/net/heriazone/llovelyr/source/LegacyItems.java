package net.heriazone.llovelyr.source;

import net.heriazone.llovelyr.Legacy;
import net.heriazone.llovelyr.LegacyIdentifier;
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
 * Centralizes item registration for Legacy variant robots (Fabric).
 * <p>
 * <b>Architecture:</b> Registry-driven map replaces 8 named static fields.
 * Adding a new Legacy entity requires no change here — the loop over
 * RobotDefinitionRegistry.getForMod(LEGACY) picks it up automatically.
 * <p>
 * <b>Fabric Pattern:</b> Items are registered immediately via Registry.register()
 * during class initialization; map values are unwrapped Item references (no
 * RegistryObject wrapper, unlike Forge/NeoForge).
 */
public class LegacyItems extends InternalItems {

    // -- State --

    private static final Map<RobotVariant, Item> spawnItems = new HashMap<>();

    // -- Static Items --

    public static final Item ROBOT_CORE = registerCoreItem(LovelyConstant.ROBOT_CORE, Rarity.UNCOMMON, 1);

    // -- Registration --

    /**
     * Populates spawn item map and logs. Called during Fabric onInitialize().
     * <p>
     * <b>Design:</b> Spawn items are registered during static field initialization
     * via the registerAll() loop triggered at class-load. This method exists to
     * surface the log line at the correct initialization stage and to match the
     * register() signature expected by LovelyLegacy.
     */
    public static void register() {
        registerAll();
        Legacy.LOGGER.info("Registering Items: " + Legacy.MODID);
    } // register()

    private static void registerAll() {
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
            Item item = registerSpawnItem(def.getSpawnItemKey(),
                LegacyEntities.getEntityType(def.getVariant()), Rarity.RARE, 1);
            spawnItems.put(def.getVariant(), item);
        }
    } // registerAll()

    // -- Access --

    /**
     * Returns the spawn item for a Legacy variant.
     *
     * @throws NullPointerException if the variant was never registered for LEGACY
     */
    public static Item getSpawnItem(RobotVariant variant) {
        return Objects.requireNonNull(spawnItems.get(variant),
            "No spawn item registered for Legacy variant: " + variant
            + ". Was LegacyItems.register() called?");
    } // getSpawnItem()

    // -- Client --

    /**
     * Registers NBT-driven model predicates so each spawn egg displays the
     * correct color texture based on its stored color NBT value.
     * Called during onInitializeClient().
     */
    public static void registerModel() {
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
            registerModel(getSpawnItem(def.getVariant()),
                LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
        }
    } // registerModel()

    // -- Helpers --

    private static Item registerCoreItem(String name, Rarity rarity, int stack) {
        return register(LegacyIdentifier.getId(name),
            new LovelyCoreItem(new Item.Properties().rarity(rarity).fireResistant().stacksTo(stack)));
    } // registerCoreItem()

    private static Item registerSpawnItem(String name, EntityType<? extends Mob> mob,
                                          Rarity rarity, int stack) {
        return register(LegacyIdentifier.getId(name),
            new LovelySpawnItem(mob, new Item.Properties().rarity(rarity).fireResistant().stacksTo(stack)));
    } // registerSpawnItem()

} // Class: LegacyItems
