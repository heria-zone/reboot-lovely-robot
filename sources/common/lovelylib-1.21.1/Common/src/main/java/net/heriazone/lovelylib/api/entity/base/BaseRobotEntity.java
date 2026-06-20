package net.heriazone.lovelylib.api.entity.base;

import net.heriazone.lovelylib.common.entity.RobotFamily;
import net.heriazone.lovelylib.common.entity.RobotEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * Base class for robot entity implementations across different loaders.
 * <p>
 * <b>Architecture:</b> Provides common robot behavior while allowing loader-specific
 * customization for GeckoLib integration, item handling, and platform-specific APIs.
 * <p>
 * <b>Design Decision:</b> Abstract class rather than interface to provide default
 * implementations while requiring loader-specific GeckoLib integration. Keeps
 * animation logic in loaders to respect GeckoLib isolation constraint.
 * <p>
 * <b>Loader Responsibilities:</b> Subclasses must implement GeckoLib interfaces
 * (GeoEntity), animation controllers, and platform-specific item resolution.
 */
public abstract class BaseRobotEntity extends RobotEntity {

    // -- Constructor --

    public BaseRobotEntity(EntityType<? extends RobotEntity> entityType, Level level, RobotFamily nativeEntity) {
        super(entityType, level, nativeEntity);
        handlePostSpawnInitialization();
    } // Constructor: BaseRobotEntity()

    // -- Common Behavior Methods --

} // Class: BaseRobotEntity