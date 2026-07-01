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
 * {@code EntityAIBunnyFollowPoint} into the 1.21.1 goal API.
 * Intentionally does not implement the Legacy/Reboot PATROL→GUARD cycle
 * ({@link AiBaseDefenseGoal}) — that state machine is a Legacy/Reboot feature
 * (ADR 022 Change F, Tribute Isolation note).
 * <p>
 * <b>Activation:</b> Active in {@link EntityState#Defense} when the robot is not
 * ordered to sit and is farther from base than {@code minDistance}.
 * <p>
 * <b>Navigation:</b> Recalculates the path to base every 10 ticks. When the
 * distance to base exceeds {@code warpDistance} and navigation cannot find a path,
 * teleports the robot to the nearest valid adjacent block (5×5 scan from original).
 * <p>
 * <b>Look control:</b> Keeps the robot's head facing base during travel.
 * <p>
 * <b>Why not {@link AiBaseDefenseGoal}:</b> Its PATROL→GUARD state machine with
 * scan patterns and 30–45 s cycles is a Legacy/Reboot feature. Tribute gets
 * point-return only, matching original mod behavior.
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
     * Continues as long as the robot remains in Defense state and not sitting.
     * Distance threshold is re-checked in {@link #tick()} so canContinueToUse
     * stays lean.
     */
    @Override
    public boolean canContinueToUse() {
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
     * Keeps the robot's head facing base, then every 10 ticks recalculates the
     * path. If the robot is beyond {@code warpDistance} and navigation fails,
     * attempts a teleport using a 5×5 floor scan — the same heuristic used in
     * the original {@code EntityAIBunnyFollowPoint}.
     */
    @Override
    public void tick() {
        // Keep head aimed at base while traveling
        entity.getLookControl().setLookAt(
                entity.getBaseX(), entity.getBaseY(), entity.getBaseZ(),
                10.0F, (float) entity.getMaxHeadXRot());

        if (--recalcCountdown > 0) return;
        recalcCountdown = 10;

        double distSq = entity.distanceToSqr(
                entity.getBaseX(), entity.getBaseY(), entity.getBaseZ());

        boolean pathOk = navigation.moveTo(
                entity.getBaseX(), entity.getBaseY(), entity.getBaseZ(), speed);

        if (!pathOk && distSq >= (double) (warpDistance * warpDistance)) {
            tryTeleportToBase();
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
