package net.heriazone.llovelyr.entity;

import net.heriazone.lovelylib.hzlib.api.entity.InternalModel;

/**
 * GeckoLib model provider for robot entities.
 * <p>
 * Delegates model/animation resolution to InternalModel, which queries entity's
 * NativeEntityType for variant-specific resources.
 */
public class RobotModel extends InternalModel<RobotEntity> {

} // Class: RobotModel