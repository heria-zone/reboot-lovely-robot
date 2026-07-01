package net.heriazone.lovelylib.common.entity.goal;

import net.heriazone.hzlib.framework.entity.enums.EntityState;
import net.heriazone.lovelylib.common.entity.RobotEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import java.util.EnumSet;

/**
 * Tribute defense goal — returns robot to its saved base coordinates.
 * <p>
 * <b>Architecture:</b> Faithful translation of the original LovelyRobot
 * {@code AiBaseDefenseGoal} (1.16.5 archive, itself a port of
 * {@code EntityAIBunnyFollowPoint}) into the 1.21.1 goal API.
 * Intentionally does not implement the Legacy/Reboot PATROL→GUARD cycle
 * ({@link AiBaseDefenseGoal}) — that state machine is a Legacy/Reboot feature
 * (ADR 022 Change F, Tribute Isolation note).
 * <p>
 * <b>Activation:</b> Active in {@link EntityState#Defense} when the robot is not
 * ordered to sit and is farther from base than {@code minDistance}.
 * <p>
 * <b>Navigation contract:</b> {@code moveTo()} is called unconditionally every
 * 10 ticks (matching the original). The path is not gated behind a boolean return —
 * gating caused a dead zone where a failed path inside {@code warpDistance} left
 * the robot stationary. Beyond {@code warpDistance} the robot teleports instead.
 * <p>
 * <b>Goal exit:</b> {@code canContinueToUse()} checks {@code navigation.isDone()}
 * first — when pathfinding finishes (arrived or gave up), the goal exits cleanly
 * and {@code canUse()} re-evaluates on the next tick. This matches the original
 * and prevents the lock-in-place bug where a dead path kept the goal alive.
 */
public class AiTributeReturnToBaseGoal extends Goal {

    // -- Fields --

    private final RobotEntity entity;
    private final LevelReader world;
    private final PathNavigation navigation;
    private final double speed;
    private final float minDistance;
    private final float warpDistance;

    private float oldWaterCost;
    private int recalcCountdown = 0;

    // -- Constructor --

    /**
     * Creates the Tribute base-return goal.
     *
     * @param entity       robot entity to control
     * @param speed        movement speed multiplier when pathing to base
     * @param minDistance  distance (blocks) from base at which the goal activates
     * @param warpDistance distance (blocks) beyond which teleportation is attempted
     *                     if navigation fails
     */
    public AiTributeReturnToBaseGoal(RobotEntity entity, double speed,
                                     float minDistance, float warpDistance) {
        this.entity      = entity;
        this.world       = entity.level();
        this.navigation  = entity.getNavigation();
        this.speed       = speed;
        this.minDistance = minDistance;
        this.warpDistance = warpDistance;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    } // Constructor: AiTributeReturnToBaseGoal ()

    // -- Inherited Methods --

    /**
     * Activates when the robot is in Defense state, not sitting, and farther
     * from its base than {@code minDistance}.
     */
    @Override
    public boolean canUse() {
        if (entity.getCurrentState() != EntityState.Defense) return false;
        if (entity.isOrderedToSit()) return false;
        double distSq = entity.distanceToSqr(
                entity.getBaseX(), entity.getBaseY(), entity.getBaseZ());
        return distSq >= (double) (minDistance * minDistance);
    } // canUse ()

    /**
     * Continues as long as the robot is in Defense state, not sitting, and the
     * path is not yet complete.
     * <p>
     * <b>Origin:</b> The original {@code AiBaseDefenseGoal} checks
     * {@code navigation.isDone()} first — when pathfinding finishes (arrived or
     * gave up), the goal exits cleanly and {@code canUse()} re-evaluates on the
     * next tick. Without this check, a dead path leaves the goal running but doing
     * nothing, locking the robot in place.
     */
    @Override
    public boolean canContinueToUse() {
        if (navigation.isDone()) return false;
        if (entity.getCurrentState() != EntityState.Defense) return false;
        return !entity.isOrderedToSit();
    } // canContinueToUse ()

    /**
     * Stores current water pathfinding cost and resets the recalc counter so
     * navigation begins immediately on the first tick.
     */
    @Override
    public void start() {
        recalcCountdown = 0;
        oldWaterCost = entity.getPathfindingMalus(PathType.WATER);
        entity.setPathfindingMalus(PathType.WATER, 0.0F);
    } // start ()

    /**
     * Stops navigation and restores the original water pathfinding cost.
     */
    @Override
    public void stop() {
        navigation.stop();
        entity.setPathfindingMalus(PathType.WATER, oldWaterCost);
    } // stop ()

    /**
     * Updates the goal each tick.
     * <p>
     * Keeps the robot's head facing base. Every 10 ticks, if not leashed or
     * riding, moves toward base. If beyond {@code warpDistance}, teleports instead.
     * <p>
     * <b>Origin:</b> The original {@code AiBaseDefenseGoal} calls
     * {@code navigation.moveTo()} unconditionally — it does not gate movement
     * behind a boolean return value. Gating caused a dead zone where a failed
     * path inside {@code warpDistance} left the robot stationary with no recovery.
     */
    @Override
    public void tick() {
        entity.getLookControl().setLookAt(
                entity.getBaseX(), entity.getBaseY(), entity.getBaseZ(),
                10.0F, (float) entity.getMaxHeadXRot());

        if (--recalcCountdown > 0) return;
        recalcCountdown = 10;

        if (entity.isLeashed() || entity.isPassenger()) return;

        double distSq = entity.distanceToSqr(
                entity.getBaseX(), entity.getBaseY(), entity.getBaseZ());

        if (distSq >= (double) (warpDistance * warpDistance)) {
            tryTeleportToBase();
        } else {
            navigation.moveTo(entity.getBaseX(), entity.getBaseY(), entity.getBaseZ(), speed);
        }
    } // tick ()

    // -- Teleportation --

    /**
     * Scans a 5×5 grid around the base position for a walkable solid-floor
     * landing spot and teleports the robot there.
     * <p>
     * <b>Origin:</b> Direct translation of the {@code EntityAIBunnyFollowPoint}
     * scan loop. Tries up to 25 candidate positions (skipping the centre 3×3
     * to avoid placing the robot on top of the base block) before giving up.
     */
    private void tryTeleportToBase() {
        for (int i = 0; i < 10; i++) {
            int dx = entity.getRandom().nextInt(5) - 2;
            int dy = entity.getRandom().nextInt(3) - 1;
            int dz = entity.getRandom().nextInt(5) - 2;
            boolean ok = tryTeleportTo(
                    (int) entity.getBaseX() + dx,
                    (int) entity.getBaseY() + dy,
                    (int) entity.getBaseZ() + dz);
            if (ok) return;
        }
    } // tryTeleportToBase ()

    /**
     * Attempts to teleport to the given coordinates.
     * <p>
     * Rejects destinations within 2 blocks of base (micro-teleport guard),
     * non-walkable positions, and positions on leaves blocks.
     *
     * @param x target X
     * @param y target Y
     * @param z target Z
     * @return {@code true} if teleportation succeeded
     */
    private boolean tryTeleportTo(int x, int y, int z) {
        if (Math.abs(x - entity.getBaseX()) < 2.0
                && Math.abs(z - entity.getBaseZ()) < 2.0) {
            return false;
        }
        if (!canTeleportTo(new BlockPos(x, y, z))) return false;

        entity.moveTo(x + 0.5, y, z + 0.5, entity.getYRot(), entity.getXRot());
        navigation.stop();
        return true;
    } // tryTeleportTo ()

    /**
     * Validates a candidate teleport destination for walkability, non-leaves
     * floor, and bounding-box clearance.
     *
     * @param pos candidate position
     * @return {@code true} if safe to teleport to
     */
    private boolean canTeleportTo(BlockPos pos) {
        PathType pathType = WalkNodeEvaluator.getPathTypeStatic(entity, pos);
        if (pathType != PathType.WALKABLE) return false;

        BlockState floorState = world.getBlockState(pos.below());
        if (floorState.getBlock() instanceof LeavesBlock) return false;

        BlockPos relative = pos.subtract(entity.blockPosition());
        return world.noCollision(entity, entity.getBoundingBox().move(relative));
    } // canTeleportTo ()

} // Class: AiTributeReturnToBaseGoal
