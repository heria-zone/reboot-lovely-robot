package net.msymbios.llovelyr.shared.entity;

import net.msymbios.llovelyr.lib.entity.InternalModel;

/**
 * GeckoLib model provider for robot entities.
 * <p>
 * Delegates model/animation resolution to InternalModel, which queries entity's
 * NativeEntityType for variant-specific resources.
 */
public class RobotModel extends InternalModel<RobotEntity> {

} // Class: RobotModel