package net.msymbios.rlovelyr.item.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.SpawnEggItem;

public class EntityItemSpawn extends SpawnEggItem {

    // -- Constructor --
    public EntityItemSpawn(EntityType<? extends MobEntity> type, Settings settings) {
        super(type, 0xFFFFFF, 0xFFFFFF, settings);
    } // Constructor RobotSpawnItem ()

} // Class EntityItemSpawn