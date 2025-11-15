package net.msymbios.rlovelyr.item;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.*;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.item.custom.RobotCoreItem;
import net.msymbios.rlovelyr.item.custom.SpawnItem;

import java.util.function.Supplier;

public class LovelyRobotItems {

    // -- Variables --

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LovelyRobot.MODID);

    public static final RegistryObject<Item> ROBOT_CORE = registerItem(LovelyRobotID.ROBOT_CORE, Rarity.UNCOMMON, 1);
    public static final RegistryObject<Item> ROBOT_CORE_ALDARIAN = registerItem(LovelyRobotID.ROBOT_CORE_ALDARIAN, Rarity.UNCOMMON, 1);

    public static final RegistryObject<Item> BUNNY_SPAWN = registerItem(LovelyRobotID.BUNNY_SPAWN, LovelyRobotEntities.BUNNY, Rarity.RARE, 1);
    public static final RegistryObject<Item> BUNNY2_SPAWN = registerItem(LovelyRobotID.BUNNY2_SPAWN, LovelyRobotEntities.BUNNY2, Rarity.RARE, 1);
    public static final RegistryObject<Item> DRAGON_SPAWN = registerItem(LovelyRobotID.DRAGON_SPAWN, LovelyRobotEntities.DRAGON, Rarity.RARE, 1);
    public static final RegistryObject<Item> HONEY_SPAWN = registerItem(LovelyRobotID.HONEY_SPAWN, LovelyRobotEntities.HONEY, Rarity.RARE, 1);
    public static final RegistryObject<Item> KITSUNE_SPAWN = registerItem(LovelyRobotID.KITSUNE_SPAWN, LovelyRobotEntities.KITSUNE, Rarity.RARE, 1);
    public static final RegistryObject<Item> NEKO_SPAWN = registerItem(LovelyRobotID.NEKO_SPAWN, LovelyRobotEntities.NEKO, Rarity.RARE, 1);
    public static final RegistryObject<Item> VANILLA_SPAWN = registerItem(LovelyRobotID.VANILLA_SPAWN, LovelyRobotEntities.VANILLA, Rarity.RARE, 1);

    public static final RegistryObject<Item> PRIME_SPAWN = registerItem(LovelyRobotID.PRIME_SPAWN, LovelyRobotEntities.PRIME, Rarity.RARE, 1);
    public static final RegistryObject<Item> HYPERION_SPAWN = registerItem(LovelyRobotID.HYPERION_SPAWN, LovelyRobotEntities.HYPERION, Rarity.RARE, 1);
    public static final RegistryObject<Item> EMPYRIUM_SPAWN = registerItem(LovelyRobotID.EMPYRIUM_SPAWN, LovelyRobotEntities.EMPYRIUM, Rarity.RARE, 1);

    // -- Methods --

    public static RegistryObject<Item> registerItem (String name, Rarity rarity, int stack) {
        return ITEMS.register(name, () -> new RobotCoreItem(new Item.Properties().rarity(rarity).stacksTo(stack).fireResistant()));
    } // registerItem ()

    public static RegistryObject<Item> registerItem (String name, Supplier<? extends EntityType<? extends Mob>> mob, Rarity rarity, int stack) {
        return ITEMS.register(name, () -> new SpawnItem(mob, new Item.Properties().rarity(rarity).stacksTo(stack).fireResistant()));
    } // registerItem ()

    public static void register(IEventBus event) {
        LovelyRobot.LOGGER.info("Registering Items for: " + LovelyRobot.MODID);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(event);
    } // register ()

    public static void registerCreative(IEventBus event) {
        // Register the item to a creative tab
        event.addListener(LovelyRobotItems::addCreative);
    } // registerCreative ()

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        addItemsToDefaultTab(event);
        addItemsToAldarianTechTab(event);
    } // addCreative ()

    protected static void addItemsToDefaultTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() != LovelyRobotItemsGroup.DEFAULT_KEY) return;
        event.accept(ROBOT_CORE);
        event.accept(BUNNY_SPAWN);
        event.accept(BUNNY2_SPAWN);
        event.accept(DRAGON_SPAWN);
        event.accept(HONEY_SPAWN);
        event.accept(KITSUNE_SPAWN);
        event.accept(NEKO_SPAWN);
        event.accept(VANILLA_SPAWN);
    } // addItemsToDefaultTab ()

    protected static void addItemsToAldarianTechTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() != LovelyRobotItemsGroup.ALDARIAN_TECH_KEY) return;
        event.accept(PRIME_SPAWN);
        event.accept(HYPERION_SPAWN);
        event.accept(EMPYRIUM_SPAWN);
    } // addItemsToAldarianTechTab ()

    @SafeVarargs
    protected static void addItemsToCreativeTab(BuildCreativeModeTabContentsEvent event, ResourceKey<CreativeModeTab> creativeTab, RegistryObject<Item>... items) {
        if (event.getTabKey() != creativeTab) return;
        for (RegistryObject<Item> item : items) event.accept(item);
    } // addItemsToCreativeTab ()

    protected static void addItemToCreativeTab(BuildCreativeModeTabContentsEvent event, ResourceKey<CreativeModeTab> creativeTab, RegistryObject<Item> item) {
        if (event.getTabKey() != creativeTab) return;
        event.accept(item);
    } // addItemToCreativeTab ()

    public static void registerModel(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            registerModel(LovelyRobotItems.BUNNY_SPAWN, LovelyRobotID.ITEM_TAG_VARIANT, LovelyRobotID.STAT_COLOR);
            registerModel(LovelyRobotItems.BUNNY2_SPAWN, LovelyRobotID.ITEM_TAG_VARIANT, LovelyRobotID.STAT_COLOR);
            registerModel(LovelyRobotItems.DRAGON_SPAWN, LovelyRobotID.ITEM_TAG_VARIANT, LovelyRobotID.STAT_COLOR);
            registerModel(LovelyRobotItems.HONEY_SPAWN, LovelyRobotID.ITEM_TAG_VARIANT, LovelyRobotID.STAT_COLOR);
            registerModel(LovelyRobotItems.KITSUNE_SPAWN, LovelyRobotID.ITEM_TAG_VARIANT, LovelyRobotID.STAT_COLOR);
            registerModel(LovelyRobotItems.NEKO_SPAWN, LovelyRobotID.ITEM_TAG_VARIANT, LovelyRobotID.STAT_COLOR);
            registerModel(LovelyRobotItems.VANILLA_SPAWN, LovelyRobotID.ITEM_TAG_VARIANT, LovelyRobotID.STAT_COLOR);
        });
    } // registerModel ()

    private static void registerModel(RegistryObject<Item> item, String name, String key) {
        ItemProperties.register(item.get(), LovelyRobotID.getId(name), (stack, world, entity, id) -> stack.getTag() != null ? stack.getTag().getInt(key) : 16);
    } // registerModel ()

} // Class LovelyRobotItems