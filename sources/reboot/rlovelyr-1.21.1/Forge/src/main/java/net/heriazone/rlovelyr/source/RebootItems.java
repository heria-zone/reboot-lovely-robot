package net.heriazone.rlovelyr.source;

import net.heriazone.rlovelyr.Reboot;
import net.heriazone.lovelylib.common.items.LovelyCoreItem;
import net.heriazone.lovelylib.common.items.LovelySpawnItem;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.lovelylib.hzlib.api.items.InternalItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * Centralizes item registration for Reboot variant robots.
 * <p>
 * <b>Architecture:</b> Uses Forge's DeferredRegister pattern to safely register
 * items during mod initialization. Separates robot cores from spawn items while
 * maintaining consistent registration flow.
 * <p>
 * <b>Item Categories:</b> Robot cores (crafting materials) and spawn eggs
 * (entity summoning) with NBT-based customization support.
 */
public class RebootItems extends InternalItems {

    // -- Variables --

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reboot.MODID);

    // MISCELLANEOUS
    public static final RegistryObject<Item> ROBOT_CORE = registerItem(LovelyConstant.ROBOT_CORE, Rarity.UNCOMMON, 1);

    // SPAWNS
    public static final RegistryObject<Item> BUNNY_SPAWN = registerItem(LovelyConstant.BUNNY_SPAWN, RebootEntities.BUNNY::get, Rarity.RARE, 1);
    public static final RegistryObject<Item> BUNNY2_SPAWN = registerItem(LovelyConstant.BUNNY2_SPAWN, RebootEntities.BUNNY2::get, Rarity.RARE, 1);
    public static final RegistryObject<Item> DRAGON_SPAWN = registerItem(LovelyConstant.DRAGON_SPAWN, RebootEntities.DRAGON::get, Rarity.RARE, 1);
    public static final RegistryObject<Item> HONEY_SPAWN = registerItem(LovelyConstant.HONEY_SPAWN, RebootEntities.HONEY::get, Rarity.RARE, 1);
    public static final RegistryObject<Item> KITSUNE_SPAWN = registerItem(LovelyConstant.KITSUNE_SPAWN, RebootEntities.KITSUNE::get, Rarity.RARE, 1);
    public static final RegistryObject<Item> NEKO_SPAWN = registerItem(LovelyConstant.NEKO_SPAWN, RebootEntities.NEKO::get, Rarity.RARE, 1);
    public static final RegistryObject<Item> VANILLA_SPAWN = registerItem(LovelyConstant.VANILLA_SPAWN, RebootEntities.VANILLA::get, Rarity.RARE, 1);

    // -- Methods --

    /**
     * Registers robot core item with specified properties.
     * <p>
     * <b>Usage:</b> For crafting materials and robot storage items.
     *
     * @param name the registry name
     * @param rarity the item rarity level
     * @param stack the maximum stack size
     * @return registry object wrapping the item
     */
    private static RegistryObject<Item> registerItem(String name, Rarity rarity, int stack) {
        return ITEMS.register(name, () -> new LovelyCoreItem(new Item.Properties().rarity(rarity).fireResistant().stacksTo(stack)));
    } // registerItem()

    /**
     * Registers spawn egg item for robot entity.
     * <p>
     * <b>NBT Support:</b> Spawn eggs can store robot customization data (color,
     * level, name) which transfers to spawned entity.
     *
     * @param name the registry name
     * @param entity the entity type supplier
     * @param rarity the item rarity level
     * @param stack the maximum stack size
     * @return registry object wrapping the spawn egg
     */
    private static RegistryObject<Item> registerItem(String name, Supplier<EntityType<? extends Mob>> entity, Rarity rarity, int stack) {
        return ITEMS.register(name, () -> new LovelySpawnItem(entity, new Item.Properties().rarity(rarity).fireResistant().stacksTo(stack)));
    } // registerItem()

    /**
     * Registers all items with Forge event bus.
     * <p>
     * <b>Timing:</b> Must be called during mod construction before registry events fire.
     *
     * @param eventBus the mod event bus
     */
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        Reboot.LOGGER.info("Registering Items: " + Reboot.MODID);
    } // register()

    /**
     * Registers client-side model predicates for dynamic item appearance.
     * <p>
     * <b>Architecture:</b> Enables NBT-based model switching for robot color
     * variants. Each spawn egg can display different textures based on stored
     * color value in item NBT.
     * <p>
     * <b>Thread Safety:</b> Uses enqueueWork to ensure model registration occurs
     * on main thread during client setup phase, preventing concurrent modification.
     * <p>
     * <b>Timing:</b> Must be called during FMLClientSetupEvent after items are
     * registered but before client rendering begins.
     *
     * @param event the client setup event providing thread-safe work queue
     */
    public static void registerModel(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            registerModel(RebootItems.BUNNY_SPAWN.get(), LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
            registerModel(RebootItems.BUNNY2_SPAWN.get(), LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
            registerModel(RebootItems.DRAGON_SPAWN.get(), LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
            registerModel(RebootItems.HONEY_SPAWN.get(), LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
            registerModel(RebootItems.KITSUNE_SPAWN.get(), LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
            registerModel(RebootItems.NEKO_SPAWN.get(), LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
            registerModel(RebootItems.VANILLA_SPAWN.get(), LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
        });
    } // registerModel()

} // Class: RebootItems