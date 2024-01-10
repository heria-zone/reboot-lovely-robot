package net.msymbios.rlovelyr.item;

import net.minecraft.world.item.Rarity;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.item.custom.RobotCoreItem;
import net.msymbios.rlovelyr.item.custom.SpawnItem;
import net.msymbios.rlovelyr.LovelyRobot;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LovelyRobotItems {

    // -- Variables --
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LovelyRobot.MODID);
    public static final RegistryObject<Item> ROBOT_CORE = ITEMS.register(LovelyRobotID.ROBOT_CORE, () -> new RobotCoreItem(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> BUNNY_SPAWN = ITEMS.register(LovelyRobotID.BUNNY_SPAWN, () -> new SpawnItem(LovelyRobotEntities.BUNNY, new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> BUNNY2_SPAWN = ITEMS.register(LovelyRobotID.BUNNY2_SPAWN, () -> new SpawnItem(LovelyRobotEntities.BUNNY2, new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> DRAGON_SPAWN = ITEMS.register(LovelyRobotID.DRAGON_SPAWN, () -> new SpawnItem(LovelyRobotEntities.DRAGON, new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> HONEY_SPAWN = ITEMS.register(LovelyRobotID.HONEY_SPAWN, () -> new SpawnItem(LovelyRobotEntities.HONEY, new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> KITSUNE_SPAWN = ITEMS.register(LovelyRobotID.KITSUNE_SPAWN, () -> new SpawnItem(LovelyRobotEntities.KITSUNE, new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> NEKO_SPAWN = ITEMS.register(LovelyRobotID.NEKO_SPAWN, () -> new SpawnItem(LovelyRobotEntities.NEKO, new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> VANILLA_SPAWN = ITEMS.register(LovelyRobotID.VANILLA_SPAWN, () -> new SpawnItem(LovelyRobotEntities.VANILLA, new Item.Properties().rarity(Rarity.RARE)));

    // -- Methods --
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    } // register ()

    public static void addCreativeModeTab(BuildCreativeModeTabContentsEvent event) {
        if(event.getTab() == LovelyRobotItemsGroup.LOVELY_ROBOT.get()){
            event.accept(LovelyRobotItems.BUNNY_SPAWN);
            event.accept(LovelyRobotItems.BUNNY2_SPAWN);
            event.accept(LovelyRobotItems.DRAGON_SPAWN);
            event.accept(LovelyRobotItems.HONEY_SPAWN);
            event.accept(LovelyRobotItems.KITSUNE_SPAWN);
            event.accept(LovelyRobotItems.NEKO_SPAWN);
            event.accept(LovelyRobotItems.VANILLA_SPAWN);
            event.accept(LovelyRobotItems.ROBOT_CORE);
        }

        if(event.getTabKey() == CreativeModeTabs.SPAWN_EGGS){
            event.accept(LovelyRobotItems.BUNNY_SPAWN);
            event.accept(LovelyRobotItems.BUNNY2_SPAWN);
            event.accept(LovelyRobotItems.DRAGON_SPAWN);
            event.accept(LovelyRobotItems.HONEY_SPAWN);
            event.accept(LovelyRobotItems.KITSUNE_SPAWN);
            event.accept(LovelyRobotItems.NEKO_SPAWN);
            event.accept(LovelyRobotItems.VANILLA_SPAWN);
            event.accept(LovelyRobotItems.ROBOT_CORE);
        }
    } // addCreativeModeTab ()

} // Class LovelyRobotItems