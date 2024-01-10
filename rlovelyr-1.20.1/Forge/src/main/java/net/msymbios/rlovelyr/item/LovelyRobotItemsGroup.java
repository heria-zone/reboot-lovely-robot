package net.msymbios.rlovelyr.item;

import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.msymbios.rlovelyr.LovelyRobot;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.msymbios.rlovelyr.config.LovelyRobotID;

@Mod.EventBusSubscriber(modid = LovelyRobot.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LovelyRobotItemsGroup {

    // -- Variables --
    public static DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LovelyRobot.MODID);

    public static RegistryObject<CreativeModeTab> LOVELY_ROBOT = CREATIVE_MODE_TABS.register(LovelyRobotID.TAB_GROUP, () ->
            CreativeModeTab.builder().icon(() -> new ItemStack(LovelyRobotItems.BUNNY2_SPAWN.get())).title(Component.translatable("itemGroup.lovely_robot")).build());

    // -- Methods --
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    } // register ()

} // Class ModItemsTab