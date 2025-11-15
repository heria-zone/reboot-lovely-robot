package net.msymbios.rlovelyr.entity.goal;

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
import net.msymbios.rlovelyr.entity.internal.RobotEntity;
import net.msymbios.rlovelyr.common.entity.enums.EntityState;

import java.util.EnumSet;

public class AiBaseDefenseGoal extends Goal {

    // -- Variables --
    private LevelReader level;
    private RobotEntity entity;
    private PathNavigation navigation;
    private float speed, maxDistance, minDistance, oldWaterCost;
    private int updateCountdownTicks;

    // -- Constructors --
    public AiBaseDefenseGoal(RobotEntity mob, float speed, float minDistance, float maxDistance) {
        this.entity = mob;
        this.level = mob.level();
        this.speed = speed;
        this.navigation = mob.getNavigation();
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(mob.getNavigation() instanceof GroundPathNavigation) && !(mob.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    } // Constructor AiBaseDefenseGoal ()

    // -- Methods --
    public boolean canUse() {
        if(this.entity.getCurrentState() != EntityState.Defense) return false;
        if(this.entity.isOrderedToSit()) this.entity.setOrderedToSit(false);
        if(!this.entity.getAutoAttack()) this.entity.setAutoAttack(true);
        return this.entity.distanceToSqr(this.entity.getBaseX(), this.entity.getBaseY(), this.entity.getBaseZ()) >= (this.minDistance * this.minDistance);
    } // canUse ()

    public void start() {
        if(this.entity.getCurrentState() != EntityState.Defense) return;
        this.updateCountdownTicks = 0;
        this.oldWaterCost = this.entity.getPathfindingMalus(BlockPathTypes.WATER);
        this.entity.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    } // start ()

    public void stop() {
        this.navigation.stop();
        this.entity.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    } // stop ()

    public void tick() {
        this.entity.getLookControl().setLookAt(this.entity.getBaseX(), this.entity.getBaseY(), this.entity.getBaseZ(), 10.0F, (float)this.entity.getMaxHeadXRot());
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = this.adjustedTickDelay(10);
            if (!this.entity.isLeashed() && !this.entity.isPassenger()) {
                if (this.entity.distanceToSqr(this.entity.getBaseX(), this.entity.getBaseY(), this.entity.getBaseZ()) >= 144.0D) this.teleportToOwner();
                else this.navigation.moveTo(this.entity.getBaseX(), this.entity.getBaseY(), this.entity.getBaseZ(), this.speed);
            }
        }
    } // tick ()

    // -- Custom Methods --
    public boolean canContinueToUse() {
        if (this.navigation.isDone()) return false;
        if(this.entity.isOrderedToSit()) this.entity.setOrderedToSit(false);
        return !(this.entity.distanceToSqr(this.entity.getBaseX(), this.entity.getBaseY(), this.entity.getBaseZ()) > (this.maxDistance * this.maxDistance));
    } // canContinueToUse ()

    private void teleportToOwner() {
        for(int i = 0; i < 10; ++i) {
            int j = this.randomIntInclusive(-3, 3);
            int k = this.randomIntInclusive(-1, 1);
            int l = this.randomIntInclusive(-3, 3);
            boolean flag = this.maybeTeleportTo((int)this.entity.getBaseX() + j, (int)this.entity.getBaseY() + k, (int)this.entity.getBaseZ() + l);
            if (flag) return;
        }
    } // teleportToOwner ()

    private boolean maybeTeleportTo(int x, int y, int z) {
        if (Math.abs((double)x - this.entity.getBaseX()) < 2.0D && Math.abs((double)z - this.entity.getBaseZ()) < 2.0D) {
            return false;
        } else if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.entity.moveTo((double)x + 0.5D, (double)y, (double)z + 0.5D, this.entity.getYRot(), this.entity.getXRot());
            this.navigation.stop();
            return true;
        }
    } // maybeTeleportTo ()

    private boolean canTeleportTo(BlockPos pos) {
        BlockPathTypes blockpathtypes = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, pos.mutable());
        if (blockpathtypes != BlockPathTypes.WALKABLE) {
            return false;
        } else {
            BlockState blockstate = this.level.getBlockState(pos.below());
            if (blockstate.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockpos = pos.subtract(this.entity.blockPosition());
                return this.level.noCollision(this.entity, this.entity.getBoundingBox().move(blockpos));
            }
        }
    } // canTeleportTo ()

    private int randomIntInclusive(int min, int max) {
        return this.entity.getRandom().nextInt(max - min + 1) + min;
    } // randomIntInclusive ()

} // Class AiBaseDefenseGoal