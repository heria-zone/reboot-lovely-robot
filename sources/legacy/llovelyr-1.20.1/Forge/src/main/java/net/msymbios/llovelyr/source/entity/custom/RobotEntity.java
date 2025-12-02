package net.msymbios.llovelyr.source.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.msymbios.llovelyr.common.entity.type.RobotEntityType;
import net.msymbios.llovelyr.source.entity.common.LovelyRobot;

/**
 * Unified entity implementation for all robot variants.
 * <p>
 * <b>Design Decision:</b> Composition over inheritance - single entity class with
 * behavior configured via RobotEntityType instead of separate classes per variant.
 * Reduces code duplication and simplifies variant addition.
 */
public class RobotEntity extends LovelyRobot {

    // -- Constructor --

    public RobotEntity(EntityType<? extends LovelyRobot> entityType, Level level, RobotEntityType nativeEntity) {
        super(entityType, level, nativeEntity);
    } // Constructor: RobotEntity()

} // Class: RobotEntity
