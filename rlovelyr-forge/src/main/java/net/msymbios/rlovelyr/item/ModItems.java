package net.msymbios.rlovelyr.item;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.msymbios.rlovelyr.entity.ModEntities;
import net.msymbios.rlovelyr.item.custom.RobotCoreItem;
import net.msymbios.rlovelyr.item.custom.SpawnItem;
import net.msymbios.rlovelyr.LovelyRobotMod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {

    // -- Variables --
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LovelyRobotMod.MODID);
    public static final RegistryObject<Item> BUNNY_SPAWN = ITEMS.register("bunny_spawn", () -> new SpawnItem(ModEntities.BUNNY, new Item.Properties().tab(ModItemsGroup.LOVELY_ROBOT).stacksTo(1)));
    public static final RegistryObject<Item> BUNNY2_SPAWN = ITEMS.register("bunny2_spawn", () -> new SpawnItem(ModEntities.BUNNY2, new Item.Properties().tab(ModItemsGroup.LOVELY_ROBOT).stacksTo(1)));
    public static final RegistryObject<Item> DRAGON_SPAWN = ITEMS.register("dragon_spawn", () -> new SpawnItem(ModEntities.DRAGON, new Item.Properties().tab(ModItemsGroup.LOVELY_ROBOT).stacksTo(1)));
    public static final RegistryObject<Item> HONEY_SPAWN = ITEMS.register("honey_spawn", () -> new SpawnItem(ModEntities.HONEY, new Item.Properties().tab(ModItemsGroup.LOVELY_ROBOT).stacksTo(1)));
    public static final RegistryObject<Item> KITSUNE_SPAWN = ITEMS.register("kitsune_spawn", () -> new SpawnItem(ModEntities.KITSUNE, new Item.Properties().tab(ModItemsGroup.LOVELY_ROBOT).stacksTo(1)));
    public static final RegistryObject<Item> NEKO_SPAWN = ITEMS.register("neko_spawn", () -> new SpawnItem(ModEntities.NEKO, new Item.Properties().tab(ModItemsGroup.LOVELY_ROBOT).stacksTo(1)));
    public static final RegistryObject<Item> VANILLA_SPAWN = ITEMS.register("vanilla_spawn", () -> new SpawnItem(ModEntities.VANILLA, new Item.Properties().tab(ModItemsGroup.LOVELY_ROBOT).stacksTo(1)));
    public static final RegistryObject<Item> ROBOT_CORE = ITEMS.register("robot_core", () -> new RobotCoreItem(new Item.Properties().tab(ModItemsGroup.LOVELY_ROBOT)));

    // -- Methods --
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    } // register ()

} // Class ModItems