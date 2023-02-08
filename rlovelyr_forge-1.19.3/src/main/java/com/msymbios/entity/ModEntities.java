package com.msymbios.entity;

import com.msymbios.entity.custom.Bunny2Entity;
import com.msymbios.entity.custom.BunnyEntity;
import com.msymbios.entity.custom.HoneyEntity;
import com.msymbios.entity.custom.VanillaEntity;
import com.msymbios.rlovelyr.LovelyRobotMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    // -- Variables --
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LovelyRobotMod.MODID);

    public static final RegistryObject<EntityType<BunnyEntity>> BUNNY = ENTITY_TYPES.register("bunny",
            () -> EntityType.Builder.of(BunnyEntity::new, MobCategory.CREATURE).sized(0.4f, 1.5f)
                    .build(new ResourceLocation(LovelyRobotMod.MODID, "bunny").toString()));
    public static final RegistryObject<EntityType<Bunny2Entity>> BUNNY2 = ENTITY_TYPES.register("bunny2",
            () -> EntityType.Builder.of(Bunny2Entity::new, MobCategory.CREATURE).sized(0.4f, 1.5f)
                    .build(new ResourceLocation(LovelyRobotMod.MODID, "bunny2").toString()));
    public static final RegistryObject<EntityType<HoneyEntity>> HONEY = ENTITY_TYPES.register("honey",
            () -> EntityType.Builder.of(HoneyEntity::new, MobCategory.CREATURE).sized(0.4f, 1.5f)
                    .build(new ResourceLocation(LovelyRobotMod.MODID, "honey").toString()));
    public static final RegistryObject<EntityType<VanillaEntity>> VANILLA = ENTITY_TYPES.register("vanilla",
            () -> EntityType.Builder.of(VanillaEntity::new, MobCategory.CREATURE).sized(0.4f, 1.5f)
                    .build(new ResourceLocation(LovelyRobotMod.MODID, "vanilla").toString()));

    // -- Methods --
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    } // register ()

} // Class ModEntities