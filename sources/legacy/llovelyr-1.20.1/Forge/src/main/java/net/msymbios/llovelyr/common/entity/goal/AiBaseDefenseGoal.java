package net.msymbios.llovelyr.common.entity.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.msymbios.llovelyr.source.entity.LovelyRobot;
import net.msymbios.llovelyr.source.entity.internal.enums.EntityState;

import java.util.EnumSet;

/**
 * Defense mode goal that returns robot to designated base position.
 * <p>
 * <b>Architecture:</b> Implements stationary defense behavior where robot
 * patrols back to base coordinates when in Defense state. Combines navigation,
 * teleportation, and state management for area defense mechanics.
 * <p>
 * <b>Design Decision:</b> Uses base coordinates (stored in entity) rather than
 * owner position, allowing robots to guard specific locations independently.
 * Enables "stay and defend" behavior distinct from following.
 * <p>
 * <b>State Dependency:</b> Only executes in Defense state. Automatically enables
 * auto-attack and disables sitting when activated, ensuring combat readiness.
 * <p>
 * <b>Teleportation:</b> If robot exceeds 12-block distance from base, attempts
 * teleportation to prevent getting permanently stuck. Validates teleport
 * destinations for walkability and space.
 * <p>
 * <b>Performance:</b> Updates pathfinding every 10 ticks (0.5s) rather than
 * every tick to reduce computational load during defense patrol.
 */
public class AiBaseDefenseGoal extends Goal {

    // -- Fields --

    private final LevelReader world;
    private final LovelyRobot entity;
    private final PathNavigation navigation;
    private final float speed;
    private final float maxDistance;
    private final float minDistance;
    private float oldWaterPathfindingPenalty;
    private int updateCountdownTicks;

    // -- Constructor --

    /**
     * Creates base defense goal with distance thresholds.
     * <p>
     * <b>Navigation Validation:</b> Requires GroundPathNavigation or FlyingPathNavigation
     * to ensure compatible pathfinding. Throws exception for unsupported types
     * (e.g., water-based navigation).
     *
     * @param mob robot entity to control
     * @param speed movement speed multiplier when returning to base
     * @param minDistance minimum distance from base before goal activates (blocks)
     * @param maxDistance maximum distance before teleportation triggers (blocks)
     * @throws IllegalArgumentException if navigation type unsupported
     */
    public AiBaseDefenseGoal(LovelyRobot mob, float speed, float minDistance, float maxDistance) {
        this.entity = mob;
        this.world = mob.level();
        this.speed = speed;
        this.navigation = mob.getNavigation();
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        
        if (!(mob.getNavigation() instanceof GroundPathNavigation) && !(mob.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for AiBaseDefenseGoal");
        }
    } // Constructor: AiBaseDefenseGoal

    // -- Inherited Methods --

    /**
     * Determines if goal can start executing.
     * <p>
     * <b>State Validation:</b> Only activates in Defense state. Automatically
     * disables sitting and enables auto-attack to prepare for combat.
     * <p>
     * <b>Distance Check:</b> Activates when robot is beyond minimum distance
     * from base, preventing constant micro-adjustments when already at base.
     *
     * @return true if in Defense state and beyond minimum distance from base
     */
    @Override
    public boolean canUse() {
        if (this.entity.getCurrentState() != EntityState.Defense) return false;
        
        if (this.entity.isInSittingPose()) this.entity.setInSittingPose(false);
        if (!this.entity.getAutoAttack()) this.entity.setAutoAttack(true);
        
        return this.entity.distanceToSqr(this.entity.getBaseX(), this.entity.getBaseY(), this.entity.getBaseZ()) >= (this.minDistance * this.minDistance);
    } // canUse

    /**
     * Initiates goal execution after canUse approval.
     * <p>
     * <b>Pathfinding Setup:</b> Resets update countdown and adjusts water
     * pathfinding penalty to 0, allowing robots to path through water when
     * returning to base (prevents getting stuck at water boundaries).
     */
    @Override
    public void start() {
        if (this.entity.getCurrentState() != EntityState.Defense) return;
        
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.entity.getPathfindingMalus(BlockPathTypes.WATER);
        this.entity.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    } // start

    /**
     * Halts goal execution when conditions no longer met.
     * <p>
     * <b>Cleanup:</b> Stops navigation and restores original water pathfinding
     * penalty to prevent affecting other goals' pathfinding behavior.
     */
    @Override
    public void stop() {
        this.navigation.stop();
        this.entity.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterPathfindingPenalty);
    } // stop

    /**
     * Updates goal state each tick while executing.
     * <p>
     * <b>Look Behavior:</b> Continuously faces base position for visual feedback
     * that robot is in defense mode.
     * <p>
     * <b>Throttled Updates:</b> Only recalculates pathfinding every 10 ticks
     * (0.5s) to reduce CPU usage. Checks for teleportation need or starts
     * navigation to base.
     * <p>
     * <b>Teleportation:</b> If distance exceeds 144 blocks² (12 blocks), attempts
     * teleportation to prevent permanent separation from base.
     */
    @Override
    public void tick() {
        this.entity.getLookControl().setLookAt(this.entity.getBaseX(), this.entity.getBaseY(), this.entity.getBaseZ(), 10.0F, (float) this.entity.getMaxHeadXRot());
        
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = this.adjustedTickDelay(10);
            
            if (!this.entity.isLeashed() && !this.entity.isPassenger()) {
                if (this.entity.distanceToSqr(this.entity.getBaseX(), this.entity.getBaseY(), this.entity.getBaseZ()) >= 144.0) {
                    this.tryTeleport();
                } else {
                    this.navigation.moveTo(this.entity.getBaseX(), this.entity.getBaseY(), this.entity.getBaseZ(), this.speed);
                }
            }
        }
    } // tick

    // -- Custom Methods --

    /**
     * Determines if goal should continue executing.
     * <p>
     * <b>Navigation Check:</b> Stops if navigation is idle (reached destination
     * or pathfinding failed).
     * <p>
     * <b>Distance Check:</b> Stops if robot exceeds maximum distance from base,
     * allowing teleportation logic to take over.
     * <p>
     * <b>Sitting Override:</b> Disables sitting if somehow enabled during execution.
     *
     * @return true if should continue navigating to base
     */
    @Override
    public boolean canContinueToUse() {
        if (this.navigation.isDone()) return false;
        if (this.entity.isInSittingPose()) this.entity.setInSittingPose(false);
        return !(this.entity.distanceToSqr(this.entity.getBaseX(), this.entity.getBaseY(), this.entity.getBaseZ()) > (this.maxDistance * this.maxDistance));
    } // canContinueToUse

    /**
     * Attempts to teleport robot near base position.
     * <p>
     * <b>Search Pattern:</b> Tries 10 random positions within 3-block radius
     * of base, checking each for teleportation validity.
     * <p>
     * <b>Early Exit:</b> Returns immediately on first successful teleport to
     * avoid unnecessary position checks.
     */
    private void tryTeleport() {
        for (int i = 0; i < 10; ++i) {
            int j = this.getRandomInt(-3, 3);
            int k = this.getRandomInt(-1, 1);
            int l = this.getRandomInt(-3, 3);
            boolean bl = this.tryTeleportTo((int) this.entity.getBaseX() + j, (int) this.entity.getBaseY() + k, (int) this.entity.getBaseZ() + l);
            if (bl) return;
        }
    } // tryTeleport

    /**
     * Attempts to teleport robot to specific coordinates.
     * <p>
     * <b>Proximity Check:</b> Rejects teleports within 2 blocks of base to
     * prevent micro-teleports when already near destination.
     * <p>
     * <b>Validity Check:</b> Validates destination is walkable and has space
     * for robot's bounding box.
     * <p>
     * <b>Execution:</b> If valid, teleports robot and stops navigation to
     * prevent pathfinding conflicts.
     *
     * @param x target X coordinate
     * @param y target Y coordinate
     * @param z target Z coordinate
     * @return true if teleportation succeeded
     */
    private boolean tryTeleportTo(int x, int y, int z) {
        if (Math.abs((double) x - this.entity.getBaseX()) < 2.0 && Math.abs((double) z - this.entity.getBaseZ()) < 2.0) {
            return false;
        } else if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.entity.moveTo((double) x + 0.5, (double) y, (double) z + 0.5, this.entity.getYRot(), this.entity.getXRot());
            this.navigation.stop();
            return true;
        }
    } // tryTeleportTo

    /**
     * Validates if position is safe for teleportation.
     * <p>
     * <b>Walkability Check:</b> Uses WalkNodeEvaluator to determine if position
     * is walkable terrain (not lava, void, etc.).
     * <p>
     * <b>Leaves Rejection:</b> Prevents teleporting onto leaves blocks to avoid
     * robots getting stuck in trees.
     * <p>
     * <b>Space Check:</b> Verifies robot's bounding box fits at destination
     * without colliding with blocks or entities.
     *
     * @param pos target position to validate
     * @return true if position is safe for teleportation
     */
    private boolean canTeleportTo(BlockPos pos) {
        BlockPathTypes pathNodeType = WalkNodeEvaluator.getBlockPathTypeStatic(this.world, pos.mutable());
        if (pathNodeType != BlockPathTypes.WALKABLE) {
            return false;
        } else {
            BlockState blockState = this.world.getBlockState(pos.below());
            if (blockState.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockPos = pos.subtract(this.entity.blockPosition());
                return this.world.noCollision(this.entity, this.entity.getBoundingBox().move(blockPos));
            }
        }
    } // canTeleportTo

    /**
     * Generates random integer within inclusive range.
     *
     * @param min minimum value (inclusive)
     * @param max maximum value (inclusive)
     * @return random integer in [min, max]
     */
    private int getRandomInt(int min, int max) {
        return this.entity.getRandom().nextInt(max - min + 1) + min;
    } // getRandomInt

} // Class: AiBaseDefenseGoal
