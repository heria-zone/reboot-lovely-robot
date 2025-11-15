package net.msymbios.rlovelyr.entity.goal;

import java.util.EnumSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.entity.enums.EntityState;
import net.msymbios.rlovelyr.entity.internal.InternalEntity;

import java.util.EnumSet;

public class AiBaseDefenseGoal extends Goal {

    // -- Variables --
    private IWorldReader level;
    private InternalEntity entity;
    private PathNavigator navigation;
    private float speed, maxDistance, minDistance, oldWaterCost;
    private int updateCountdownTicks;

    // -- Constructors --
    public AiBaseDefenseGoal(InternalEntity mob, float speed, float minDistance, float maxDistance) {
        this.entity = mob;
        this.level = mob.level;
        this.speed = speed;
        this.navigation = mob.getNavigation();
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(mob.getNavigation() instanceof GroundPathNavigator) && !(mob.getNavigation() instanceof FlyingPathNavigator)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    } // Constructor AiBaseDefenseGoal ()

    // -- Methods --
    public boolean canUse() {
        if(this.entity.getCurrentState() != EntityState.BaseDefense) return false;
        if(this.entity.isOrderedToSit()) this.entity.setOrderedToSit(false);
        if(!this.entity.getAutoAttack()) this.entity.setAutoAttack(true);
        return this.entity.distanceToSqr(this.entity.getBaseX(), this.entity.getBaseY(), this.entity.getBaseZ()) >= (this.minDistance * this.minDistance);
    } // canUse ()

    public void start() {
        if(this.entity.getCurrentState() != EntityState.BaseDefense) return;
        this.updateCountdownTicks = 0;
        this.oldWaterCost = this.entity.getPathfindingMalus(PathNodeType.WATER);
        this.entity.setPathfindingMalus(PathNodeType.WATER, 0.0F);
    } // start ()

    public void stop() {
        this.navigation.stop();
        this.entity.setPathfindingMalus(PathNodeType.WATER, this.oldWaterCost);
    } // stop ()

    public void tick() {
        this.entity.getLookControl().setLookAt(this.entity.getBaseX(), this.entity.getBaseY(), this.entity.getBaseZ(), 10.0F, (float)this.entity.getMaxHeadXRot());
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = 10;
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
            this.entity.moveTo((double)x + 0.5D, (double)y, (double)z + 0.5D, this.entity.yRot, this.entity.xRot);
            this.navigation.stop();
            return true;
        }
    } // maybeTeleportTo ()

    private boolean canTeleportTo(BlockPos pos) {
        PathNodeType blockpathtypes = WalkNodeProcessor.getBlockPathTypeStatic(this.level, pos.mutable());
        if (blockpathtypes != PathNodeType.WALKABLE) {
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