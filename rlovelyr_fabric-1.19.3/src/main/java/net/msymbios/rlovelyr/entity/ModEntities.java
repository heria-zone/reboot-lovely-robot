package net.msymbios.rlovelyr.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.LovelyRobotMod;
import net.msymbios.rlovelyr.entity.custom.VanillaEntity;

public class ModEntities {

    // -- Variables --
    public static final EntityType<VanillaEntity> VANILLA = Registry.register(Registries.ENTITY_TYPE, new Identifier(LovelyRobotMod.MODID, "vanilla"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, VanillaEntity::new).dimensions(EntityDimensions.fixed(0.4f, 1.5f)).build());

} // Class ModEntities
