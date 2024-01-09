package net.msymbios.rlovelyr.item;

import net.minecraft.world.item.Rarity;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.item.custom.RobotCoreItem;
import net.msymbios.rlovelyr.item.custom.SpawnItem;
import net.msymbios.rlovelyr.LovelyRobot;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LovelyRobotItems {

    // -- Variables --
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LovelyRobot.MODID);
    public static final RegistryObject<Item> ROBOT_CORE = register(LovelyRobotID.ROBOT_CORE, new RobotCoreItem(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1)));
    public static final RegistryObject<Item> BUNNY_SPAWN = register(LovelyRobotID.BUNNY_SPAWN, new SpawnItem(LovelyRobotEntities.BUNNY, new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));
    public static final RegistryObject<Item> BUNNY2_SPAWN = register(LovelyRobotID.BUNNY2_SPAWN, new SpawnItem(LovelyRobotEntities.BUNNY2, new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));
    public static final RegistryObject<Item> DRAGON_SPAWN = register(LovelyRobotID.DRAGON_SPAWN, new SpawnItem(LovelyRobotEntities.DRAGON, new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));
    public static final RegistryObject<Item> HONEY_SPAWN = register(LovelyRobotID.HONEY_SPAWN, new SpawnItem(LovelyRobotEntities.HONEY, new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));
    public static final RegistryObject<Item> KITSUNE_SPAWN = register(LovelyRobotID.KITSUNE_SPAWN, new SpawnItem(LovelyRobotEntities.KITSUNE, new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));
    public static final RegistryObject<Item> NEKO_SPAWN = register(LovelyRobotID.NEKO_SPAWN, new SpawnItem(LovelyRobotEntities.NEKO, new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));
    public static final RegistryObject<Item> VANILLA_SPAWN = register(LovelyRobotID.VANILLA_SPAWN, new SpawnItem(LovelyRobotEntities.VANILLA, new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));

    // -- Methods --
    private static RegistryObject<Item> register(String name, Item item) {
        return ITEMS.register(name, () -> item);
    } // register ()

    public static void register(IEventBus event) {
        ITEMS.register(event);
    } // register ()

    public static void addCreativeModeTab(CreativeModeTabEvent.BuildContents event) {
        if(event.getTab() == LovelyRobotItemsGroup.LOVELY_ROBOT){
            event.accept(LovelyRobotItems.BUNNY_SPAWN);
            event.accept(LovelyRobotItems.BUNNY2_SPAWN);
            event.accept(LovelyRobotItems.DRAGON_SPAWN);
            event.accept(LovelyRobotItems.HONEY_SPAWN);
            event.accept(LovelyRobotItems.KITSUNE_SPAWN);
            event.accept(LovelyRobotItems.NEKO_SPAWN);
            event.accept(LovelyRobotItems.VANILLA_SPAWN);
            event.accept(LovelyRobotItems.ROBOT_CORE);
        }

        if(event.getTab() == CreativeModeTabs.SPAWN_EGGS){
            event.accept(LovelyRobotItems.BUNNY_SPAWN);
            event.accept(LovelyRobotItems.BUNNY2_SPAWN);
            event.accept(LovelyRobotItems.DRAGON_SPAWN);
            event.accept(LovelyRobotItems.HONEY_SPAWN);
            event.accept(LovelyRobotItems.KITSUNE_SPAWN);
            event.accept(LovelyRobotItems.NEKO_SPAWN);
            event.accept(LovelyRobotItems.VANILLA_SPAWN);
        }
    } // addCreativeModeTab ()

} // Class LovelyRobotItems