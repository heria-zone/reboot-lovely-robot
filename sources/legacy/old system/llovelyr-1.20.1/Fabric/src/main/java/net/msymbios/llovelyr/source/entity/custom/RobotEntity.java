package net.msymbios.llovelyr.source.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.msymbios.llovelyr.common.entity.NativeEntityType;
import net.msymbios.llovelyr.source.entity.common.LovelyRobotEntity;

/**
 * Unified entity implementation for all robot variants.
 * <p>
 * <b>Design Decision:</b> Composition over inheritance - single entity class with
 * behavior configured via NativeEntityType instead of separate classes per variant.
 * Reduces code duplication and simplifies variant addition.
 */
public class RobotEntity extends LovelyRobotEntity {

    // -- Constructor --

    public RobotEntity(EntityType<? extends LovelyRobotEntity> entityType, World world, NativeEntityType nativeEntity, Item spawnItem) {
        super(entityType, world, nativeEntity, spawnItem);
    } // Constructor: RobotEntity()

} // Class: RobotEntity