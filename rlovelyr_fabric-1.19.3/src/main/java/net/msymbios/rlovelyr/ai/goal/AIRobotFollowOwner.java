package net.msymbios.rlovelyr.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.msymbios.rlovelyr.entity.custom.RobotEntity;

import java.util.EnumSet;

public class AIRobotFollowOwner extends Goal {

    //-- Variables --
    private final RobotEntity tameable;
    private LivingEntity owner;
    private final WorldView world;
    private final double speed;
    private final EntityNavigation navigation;
    private int updateCountdownTicks;
    private final float maxDistance;
    private final float minDistance;
    private float oldWaterPathfindingPenalty;
    private final boolean leavesAllowed;

    // -- Constructor --
    public AIRobotFollowOwner(RobotEntity tamable, double speed, float minDistance, float maxDistance, boolean leavesAllowed) {
        this.tameable = tamable;
        this.world = tamable.world;
        this.speed = speed;
        this.navigation = tamable.getNavigation();
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        tamable.getNavigation();
        this.leavesAllowed = leavesAllowed;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        if (!(tamable.getNavigation() instanceof MobNavigation) && !(tamable.getNavigation() instanceof BirdNavigation))
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
    } // Constructor AIRobotFollowOwner ()

    // -- Methods --
    @Override
    public boolean canStart() {
        LivingEntity livingEntity = this.tameable.getOwner();

        if (livingEntity == null) return false;
        if (livingEntity.isSpectator()) return false;
        if (this.tameable.isSitting()) return false;
        if (this.tameable.squaredDistanceTo(livingEntity) < (double)(this.minDistance * this.minDistance)) return false;

        this.owner = livingEntity;
        return true;
    } // canStart ()

    @Override
    public boolean shouldContinue() {
        if (this.navigation.isIdle()) return false;
        if (this.tameable.isSitting()) return false;
        return !(this.tameable.squaredDistanceTo(this.owner) <= (double)(this.maxDistance * this.maxDistance));
    } // shouldContinue ()

    @Override
    public void start() {
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.tameable.getPathfindingPenalty(PathNodeType.WATER);
        this.tameable.setPathfindingPenalty(PathNodeType.WATER, 0.0f);
    } // start ()

    @Override
    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.tameable.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
    } // stop ()

    @Override
    public void tick() {
        this.tameable.getLookControl().lookAt(this.owner, 10.0f, this.tameable.getMaxLookPitchChange());
        if (--this.updateCountdownTicks > 0) return;

        this.updateCountdownTicks = this.getTickCount(10);
        if (this.tameable.isLeashed() || this.tameable.hasVehicle()) return;

        if (this.tameable.squaredDistanceTo(this.owner) >= 144.0) this.tryTeleport();
        else this.navigation.startMovingTo(this.owner, this.speed);
    } // tick ()

    private void tryTeleport() {
        BlockPos blockPos = this.owner.getBlockPos();
        for (int i = 0; i < 10; ++i) {
            int j = this.getRandomInt(-3, 3);
            int k = this.getRandomInt(-1, 1);
            int l = this.getRandomInt(-3, 3);
            boolean bl = this.tryTeleportTo(blockPos.getX() + j, blockPos.getY() + k, blockPos.getZ() + l);
            if (!bl) continue;
            return;
        }
    } // tryTeleport ()

    private boolean tryTeleportTo(int x, int y, int z) {
        if (Math.abs((double)x - this.owner.getX()) < 2.0 && Math.abs((double)z - this.owner.getZ()) < 2.0) return false;
        if (!this.canTeleportTo(new BlockPos(x, y, z))) return false;

        this.tameable.refreshPositionAndAngles((double)x + 0.5, y, (double)z + 0.5, this.tameable.getYaw(), this.tameable.getPitch());
        this.navigation.stop();
        return true;
    } // tryTeleportTo ()

    private boolean canTeleportTo(BlockPos pos) {
        PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType(this.world, pos.mutableCopy());
        if (pathNodeType != PathNodeType.WALKABLE) return false;

        BlockState blockState = this.world.getBlockState(pos.down());
        if (!this.leavesAllowed && blockState.getBlock() instanceof LeavesBlock) return false;

        BlockPos blockPos = pos.subtract(this.tameable.getBlockPos());
        return this.world.isSpaceEmpty(this.tameable, this.tameable.getBoundingBox().offset(blockPos));
    } // canTeleportTo ()

    private int getRandomInt(int min, int max) {
        return this.tameable.getRandom().nextInt(max - min + 1) + min;
    } // getRandomInt ()

} // Class AIRobotFollowOwner