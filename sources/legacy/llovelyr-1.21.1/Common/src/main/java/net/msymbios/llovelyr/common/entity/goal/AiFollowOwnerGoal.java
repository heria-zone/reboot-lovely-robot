package net.msymbios.llovelyr.common.entity.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.msymbios.llovelyr.common.Configs.SharedConfigs;
import net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity;
import net.msymbios.llovelyr.framework.entity.enums.EntityState;

import java.util.EnumSet;

/**
 * Wolf-style owner following goal with state-based execution.
 * <p>
 * <b>Architecture:</b> Reimplements following behavior based on Wolf's approach,
 * which doesn't suffer from spinning issues on edges. Uses direct navigation
 * without excessive look control interference.
 * <p>
 * <b>Design Decision:</b> Instead of extending FollowOwnerGoal (which has look
 * control issues), we implement the core following logic directly, similar to
 * how Wolf does it in vanilla Minecraft.
 * <p>
 * <b>State Dependency:</b> Only executes when robot is in Follow state, enabling
 * clean separation between behavioral modes without goal priority conflicts.
 */
public class AiFollowOwnerGoal extends Goal {

    // -- Fields --

    private final LovelyRobotEntity entity;
    private final LevelReader level;
    private final PathNavigation navigation;
    private final double speedModifier;
    private final float stopDistance;
    private final float startDistance;
    private final boolean canFly;
    
    private LivingEntity owner;
    private int timeToRecalcPath;
    private float oldWaterCost;

    // -- Constructor --

    /**
     * Creates Wolf-style owner-following goal.
     *
     * @param entity robot entity
     * @param speed movement speed multiplier when following
     * @param startDistance distance at which to start following (blocks)
     * @param stopDistance distance at which to stop following (blocks)
     */
    public AiFollowOwnerGoal(LovelyRobotEntity entity, double speed, float startDistance, float stopDistance) {
        this.entity = entity;
        this.level = entity.level();
        this.navigation = entity.getNavigation();
        this.speedModifier = speed;
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.canFly = false;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    } // Constructor: AiFollowOwnerGoal ()

    // -- Inherited Methods --

    /**
     * Determines if goal can start executing.
     * <p>
     * <b>Wolf-style Logic:</b> Simple distance check without complex state management.
     * <p>
     * <b>Conditions:</b>
     * - Robot in Follow state
     * - Owner exists and is alive
     * - Not sitting
     * - Distance to owner exceeds start distance
     *
     * @return true if should start following
     */
    @Override
    public boolean canUse() {
        // State gate - only follow in Follow state
        if (entity.getCurrentState() != EntityState.Follow) return false;
        
        // Don't follow if allowed to wander
        if (entity.canWander()) return false;

        LivingEntity owner = entity.getOwner();
        if (owner == null) return false;
        if (owner.isSpectator()) return false;
        if (entity.isOrderedToSit()) return false;
        
        // Check distance - use appropriate threshold based on combat state
        float distance = entity.distanceTo(owner);
        float threshold = entity.isWary() ? SharedConfigs.Common.FollowDistanceMax : SharedConfigs.Common.FollowDistanceMin;
        
        if (distance < threshold) return false;

        this.owner = owner;
        return true;
    } // canUse ()

    /**
     * Determines if goal should continue executing.
     * <p>
     * <b>Wolf-style Logic:</b> Continue until close enough or navigation fails.
     *
     * @return true if should continue following
     */
    @Override
    public boolean canContinueToUse() {
        // Stop if state changes
        if (entity.getCurrentState() != EntityState.Follow) return false;
        
        if (this.navigation.isDone()) return false;
        if (entity.isOrderedToSit()) return false;
        
        // Check distance - use appropriate threshold based on combat state
        float distance = entity.distanceTo(this.owner);
        float threshold = entity.isWary() ? SharedConfigs.Common.FollowDistanceMax : SharedConfigs.Common.FollowDistanceMin;
        
        return distance > threshold;
    } // canContinueToUse ()

    /**
     * Initiates goal execution.
     * <p>
     * <b>Wolf-style Setup:</b> Configures pathfinding for following behavior.
     */
    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = entity.getPathfindingMalus(PathType.WATER);
        entity.setPathfindingMalus(PathType.WATER, 0.0F);
    } // start ()

    /**
     * Halts goal execution.
     * <p>
     * <b>Wolf-style Cleanup:</b> Restores pathfinding settings and stops navigation.
     */
    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        entity.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
    } // stop ()

    /**
     * Updates goal state each tick.
     * <p>
     * <b>Wolf-style Behavior:</b> Periodically recalculates path, teleports if too far.
     * Navigation system handles body orientation naturally - no manual look control needed.
     */
    @Override
    public void tick() {
        // Recalculate path periodically
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            
            if (!entity.isLeashed() && !entity.isPassenger()) {
                // Teleport if too far
                if (entity.distanceToSqr(this.owner) >= 144.0) {
                    this.teleportToOwner();
                } else {
                    // Navigate to owner - navigation will handle body orientation
                    this.navigation.moveTo(this.owner, this.speedModifier);
                }
            }
        }
    } // tick ()

    // -- Helper Methods --

    /**
     * Teleports robot to owner when too far away.
     * <p>
     * <b>Wolf-style Teleport:</b> Finds safe position near owner and teleports.
     */
    private void teleportToOwner() {
        net.minecraft.core.BlockPos ownerPos = this.owner.blockPosition();
        
        for (int i = 0; i < 10; i++) {
            int x = this.randomIntInclusive(-3, 3);
            int y = this.randomIntInclusive(-1, 1);
            int z = this.randomIntInclusive(-3, 3);
            
            if (this.maybeTeleportTo(ownerPos.getX() + x, ownerPos.getY() + y, ownerPos.getZ() + z)) {
                return;
            }
        }
    } // teleportToOwner ()

    /**
     * Attempts to teleport to specific coordinates.
     *
     * @param x target x coordinate
     * @param y target y coordinate
     * @param z target z coordinate
     * @return true if teleport successful
     */
    private boolean maybeTeleportTo(int x, int y, int z) {
        if (Math.abs((double)x - this.owner.getX()) < 2.0 && Math.abs((double)z - this.owner.getZ()) < 2.0) {
            return false;
        }
        
        if (!this.canTeleportTo(new net.minecraft.core.BlockPos(x, y, z))) {
            return false;
        }
        
        entity.moveTo((double)x + 0.5, (double)y, (double)z + 0.5, entity.getYRot(), entity.getXRot());
        this.navigation.stop();
        return true;
    } // maybeTeleportTo ()

    /**
     * Checks if position is safe for teleporting.
     *
     * @param pos target position
     * @return true if position is safe
     */
    private boolean canTeleportTo(net.minecraft.core.BlockPos pos) {
        PathType pathType = WalkNodeEvaluator.getPathTypeStatic(
            entity, pos
        );
        
        if (pathType != PathType.WALKABLE) {
            return false;
        }
        
        net.minecraft.world.level.block.state.BlockState blockState = this.level.getBlockState(pos.below());
        if (!this.canFly && blockState.getBlock() instanceof net.minecraft.world.level.block.LeavesBlock) {
            return false;
        }
        
        net.minecraft.core.BlockPos relativePos = pos.subtract(entity.blockPosition());
        return this.level.noCollision(entity, entity.getBoundingBox().move(relativePos));
    } // canTeleportTo ()

    /**
     * Generates random integer in range (inclusive).
     *
     * @param min minimum value
     * @param max maximum value
     * @return random integer
     */
    private int randomIntInclusive(int min, int max) {
        return entity.getRandom().nextInt(max - min + 1) + min;
    } // randomIntInclusive ()

} // Class: AiFollowOwnerGoal