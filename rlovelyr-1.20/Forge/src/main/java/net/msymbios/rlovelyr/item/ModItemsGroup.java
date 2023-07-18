package net.msymbios.rlovelyr.item;

import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.msymbios.rlovelyr.LovelyRobotMod;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LovelyRobotMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItemsGroup {

    // -- Variables --
    public static DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LovelyRobotMod.MODID);

    public static RegistryObject<CreativeModeTab> LOVELY_ROBOT = CREATIVE_MODE_TABS.register("lovely_robot", () ->
            CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.BUNNY2_SPAWN.get())).title(Component.translatable("itemGroup.lovely_robot")).build());

    // -- Methods --
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    } // register ()

} // Class ModItemsTab