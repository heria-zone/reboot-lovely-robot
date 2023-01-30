package net.msymbios.rlovelyr.item.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.SpawnEggItem;

import java.util.List;

public class RobotSpawnItem extends SpawnEggItem {

    // -- Constructor --
    public RobotSpawnItem(EntityType<? extends MobEntity> type, Settings settings) {
        super(type, 0xFFFFFF, 0xFFFFFF, settings);
    } // Constructor RobotSpawnItem ()

} // Class RobotSpawnItem