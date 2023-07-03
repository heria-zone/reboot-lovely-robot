package net.msymbios.rlovelyr.item;

import net.msymbios.rlovelyr.entity.ModEntities;
import net.msymbios.rlovelyr.item.custom.EntityItemSpawn;
import net.msymbios.rlovelyr.LovelyRobotMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    // -- Variables --
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LovelyRobotMod.MODID);
    public static final RegistryObject<Item> BUNNY_SPAWN = ITEMS.register("bunny_spawn", () -> new EntityItemSpawn(ModEntities.BUNNY, new Item.Properties().tab(ModItemsTab.LOVELY_ROBOT).stacksTo(1)));
    public static final RegistryObject<Item> BUNNY2_SPAWN = ITEMS.register("bunny2_spawn", () -> new EntityItemSpawn(ModEntities.BUNNY2, new Item.Properties().tab(ModItemsTab.LOVELY_ROBOT).stacksTo(1)));
    public static final RegistryObject<Item> HONEY_SPAWN = ITEMS.register("honey_spawn", () -> new EntityItemSpawn(ModEntities.HONEY, new Item.Properties().tab(ModItemsTab.LOVELY_ROBOT).stacksTo(1)));
    public static final RegistryObject<Item> VANILLA_SPAWN = ITEMS.register("vanilla_spawn", () -> new EntityItemSpawn(ModEntities.VANILLA, new Item.Properties().tab(ModItemsTab.LOVELY_ROBOT).stacksTo(1)));
    public static final RegistryObject<Item> ROBOT_CORE = ITEMS.register("robot_core", () -> new Item(new Item.Properties().tab(ModItemsTab.LOVELY_ROBOT)));

    // -- Methods --
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    } // register ()

} // Class ModItems