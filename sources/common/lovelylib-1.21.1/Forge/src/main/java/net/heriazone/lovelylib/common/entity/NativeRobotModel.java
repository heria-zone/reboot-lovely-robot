package net.heriazone.lovelylib.common.entity;

import net.heriazone.hzlib.api.entity.InternalModel;

/**
 * GeckoLib model provider for robot entities.
 * <p>
 * Delegates model/animation resolution to InternalModel, which queries entity's
 * NativeEntityType for variant-specific resources.
 */
public class NativeRobotModel extends InternalModel<NativeRobotEntity> { } // Class: NativeRobotModel