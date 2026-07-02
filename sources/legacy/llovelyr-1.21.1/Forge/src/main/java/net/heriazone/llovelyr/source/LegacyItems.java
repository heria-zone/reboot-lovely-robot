package net.heriazone.llovelyr.source;

import net.heriazone.llovelyr.Legacy;
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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Centralizes item registration for Legacy variant robots (Forge).
 * <p>
 * <b>Architecture:</b> Registry-driven map replaces 8 named static fields.
 * Adding a new Legacy entity requires no change here.
 * <p>
 * <b>Forge Pattern:</b> DeferredRegister defers supplier resolution to
 * RegisterEvent. Map values are RegistryObject wrappers; call .get() after
 * the event fires to access the resolved Item.
 */
public class LegacyItems extends InternalItems {

    // -- State --

    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, Legacy.MODID);

    private static final Map<RobotVariant, RegistryObject<Item>> spawnItems = new HashMap<>();

    // -- Static Items --

    public static final RegistryObject<Item> ROBOT_CORE =
        ITEMS.register(LovelyConstant.ROBOT_CORE,
            () -> new LovelyCoreItem(new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant().stacksTo(1)));

    // -- Registration --

    /**
     * Registers the DeferredRegister with the event bus and populates the spawn
     * item map. Must be called in the mod constructor before RegisterEvent fires.
     */
    public static void register(IEventBus eventBus) {
        registerAll();
        ITEMS.register(eventBus);
        Legacy.LOGGER.info("Registering Items: " + Legacy.MODID);
    } // register()

    private static void registerAll() {
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
            RobotVariant variant = def.getVariant();
            RegistryObject<Item> item = registerSpawnItem(
                def.getSpawnItemKey(),
                () -> LegacyEntities.getEntityType(variant).get(),
                Rarity.RARE, 1);
            spawnItems.put(variant, item);
        }
    } // registerAll()

    // -- Access --

    /**
     * Returns the RegistryObject for the spawn item of a Legacy variant.
     * Call .get() only after RegisterEvent has fired.
     *
     * @throws NullPointerException if the variant was never registered for LEGACY
     */
    public static RegistryObject<Item> getSpawnItem(RobotVariant variant) {
        return Objects.requireNonNull(spawnItems.get(variant),
            "No spawn item registered for Legacy variant: " + variant);
    } // getSpawnItem()

    // -- Client --

    /**
     * Registers NBT-driven model predicates for all spawn eggs.
     * Must be enqueued via FMLClientSetupEvent.enqueueWork() to run on the
     * main thread after items are resolved.
     */
    public static void registerModel(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
                registerModel(getSpawnItem(def.getVariant()).get(),
                    LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
            }
        });
    } // registerModel()

    // -- Helpers --

    private static RegistryObject<Item> registerSpawnItem(String name,
            Supplier<EntityType<? extends Mob>> entity, Rarity rarity, int stack) {
        return ITEMS.register(name,
            () -> new LovelySpawnItem(entity,
                new Item.Properties().rarity(rarity).fireResistant().stacksTo(stack)));
    } // registerSpawnItem()

} // Class: LegacyItems
