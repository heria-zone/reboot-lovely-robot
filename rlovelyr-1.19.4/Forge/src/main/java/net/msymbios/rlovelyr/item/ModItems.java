package net.msymbios.rlovelyr.item;

import net.msymbios.rlovelyr.entity.ModEntities;
import net.msymbios.rlovelyr.item.custom.RobotCoreItem;
import net.msymbios.rlovelyr.item.custom.SpawnItem;
import net.msymbios.rlovelyr.LovelyRobotMod;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    // -- Variables --
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LovelyRobotMod.MODID);
    public static final RegistryObject<Item> BUNNY_SPAWN = ITEMS.register("bunny_spawn", () -> new SpawnItem(ModEntities.BUNNY, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BUNNY2_SPAWN = ITEMS.register("bunny2_spawn", () -> new SpawnItem(ModEntities.BUNNY2, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DRAGON_SPAWN = ITEMS.register("dragon_spawn", () -> new SpawnItem(ModEntities.DRAGON, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HONEY_SPAWN = ITEMS.register("honey_spawn", () -> new SpawnItem(ModEntities.HONEY, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> KITSUNE_SPAWN = ITEMS.register("kitsune_spawn", () -> new SpawnItem(ModEntities.KITSUNE, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> NEKO_SPAWN = ITEMS.register("neko_spawn", () -> new SpawnItem(ModEntities.NEKO, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> VANILLA_SPAWN = ITEMS.register("vanilla_spawn", () -> new SpawnItem(ModEntities.VANILLA, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ROBOT_CORE = ITEMS.register("robot_core", () -> new RobotCoreItem(new Item.Properties()));

    // -- Methods --
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    } // register ()

    public static void addCreativeModeTab(CreativeModeTabEvent.BuildContents event) {
        if(event.getTab() == ModItemsGroup.LOVELY_ROBOT){
            event.accept(ModItems.BUNNY_SPAWN);
            event.accept(ModItems.BUNNY2_SPAWN);
            event.accept(ModItems.DRAGON_SPAWN);
            event.accept(ModItems.HONEY_SPAWN);
            event.accept(ModItems.KITSUNE_SPAWN);
            event.accept(ModItems.NEKO_SPAWN);
            event.accept(ModItems.VANILLA_SPAWN);
            event.accept(ModItems.ROBOT_CORE);
        }

        if(event.getTab() == CreativeModeTabs.SPAWN_EGGS){
            event.accept(ModItems.BUNNY_SPAWN);
            event.accept(ModItems.BUNNY2_SPAWN);
            event.accept(ModItems.DRAGON_SPAWN);
            event.accept(ModItems.HONEY_SPAWN);
            event.accept(ModItems.KITSUNE_SPAWN);
            event.accept(ModItems.NEKO_SPAWN);
            event.accept(ModItems.VANILLA_SPAWN);
        }
    } // addCreativeModeTab ()

} // Class ModItems