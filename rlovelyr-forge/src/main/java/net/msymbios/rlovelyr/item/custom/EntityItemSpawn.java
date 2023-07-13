package net.msymbios.rlovelyr.item.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraftforge.common.ForgeSpawnEggItem;

import java.util.function.Supplier;

public class EntityItemSpawn extends ForgeSpawnEggItem {

    // -- Constructor --
    public EntityItemSpawn(Supplier<? extends EntityType<? extends MobEntity>> type, Properties props) {
        super(type, 0xFFFFFF, 0xFFFFFF, props);
    } // Constructor RobotSpawnItem ()

} // Class EntityItemSpawn