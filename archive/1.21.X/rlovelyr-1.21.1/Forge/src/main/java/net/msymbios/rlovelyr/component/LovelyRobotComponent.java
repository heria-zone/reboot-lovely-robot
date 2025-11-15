package net.msymbios.rlovelyr.component;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.msymbios.rlovelyr.LovelyRobot;

import java.util.function.UnaryOperator;

public class LovelyRobotComponent {

    // -- Variables --

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, LovelyRobot.MODID);

    // -- Methods --

    private static <T> RegistryObject<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    } // register ()

    public static void register(IEventBus event) {
        DATA_COMPONENT_TYPES.register(event);
    } // register ()

} // Class LovelyRobotComponent