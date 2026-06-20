package net.heriazone.lovelylib.common.entity;

import net.heriazone.hzlib.api.entity.NativeModel;

/**
 * GeckoLib model provider for robot entities.
 * <p>
 * Delegates model/animation resolution to NativeModel, which queries entity's
 * RobotFamily for variant-specific resources.
 */
public class NativeRobotModel extends NativeModel<NativeRobotEntity> { } // Class: NativeRobotModel