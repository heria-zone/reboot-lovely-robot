package net.msymbios.rlovelyr.entity;

import net.msymbios.rlovelyr.entity.custom.Bunny2Entity;
import net.msymbios.rlovelyr.entity.custom.BunnyEntity;
import net.msymbios.rlovelyr.entity.custom.HoneyEntity;
import net.msymbios.rlovelyr.entity.custom.VanillaEntity;
import net.msymbios.rlovelyr.LovelyRobotMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;

public class ModEntities {

    // -- Variables --
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LovelyRobotMod.MODID);

    public static final RegistryObject<EntityType<BunnyEntity>> BUNNY = ENTITY_TYPES.register("bunny",
            () -> EntityType.Builder.of(BunnyEntity::new, MobCategory.CREATURE).sized(InternalMetric.Width, InternalMetric.Height)
                    .build(new ResourceLocation(LovelyRobotMod.MODID, "bunny").toString()));
    public static final RegistryObject<EntityType<Bunny2Entity>> BUNNY2 = ENTITY_TYPES.register("bunny2",
            () -> EntityType.Builder.of(Bunny2Entity::new, MobCategory.CREATURE).sized(InternalMetric.Width, InternalMetric.Height)
                    .build(new ResourceLocation(LovelyRobotMod.MODID, "bunny2").toString()));
    public static final RegistryObject<EntityType<HoneyEntity>> HONEY = ENTITY_TYPES.register("honey",
            () -> EntityType.Builder.of(HoneyEntity::new, MobCategory.CREATURE).sized(InternalMetric.Width, InternalMetric.Height)
                    .build(new ResourceLocation(LovelyRobotMod.MODID, "honey").toString()));
    public static final RegistryObject<EntityType<VanillaEntity>> VANILLA = ENTITY_TYPES.register("vanilla",
            () -> EntityType.Builder.of(VanillaEntity::new, MobCategory.CREATURE).sized(InternalMetric.Width, InternalMetric.Height)
                    .build(new ResourceLocation(LovelyRobotMod.MODID, "vanilla").toString()));

    // -- Methods --
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    } // register ()

} // Class ModEntities