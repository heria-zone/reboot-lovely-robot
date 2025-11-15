package net.msymbios.rlovelyr.entity.goal;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.msymbios.rlovelyr.entity.enums.EntityState;
import net.msymbios.rlovelyr.entity.internal.InternalEntity;

import java.util.EnumSet;

public class AiBaseDefenseGoal extends Goal {

    // -- Variables --
    private WorldView world;
    private InternalEntity entity;
    private EntityNavigation navigation;
    private float speed, maxDistance, minDistance, oldWaterPathfindingPenalty;
    private int updateCountdownTicks;

    // -- Constructors --
    public AiBaseDefenseGoal(InternalEntity mob, float speed, float minDistance, float maxDistance) {
        this.entity = mob;
        this.world = mob.getWorld();
        this.speed = speed;
        this.navigation = mob.getNavigation();
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        if (!(mob.getNavigation() instanceof MobNavigation) && !(mob.getNavigation() instanceof BirdNavigation))
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
    } // Constructor StandGuardGoal ()

    // -- Inherited Methods --
    public boolean canStart() {
        if(this.entity.getCurrentState() != EntityState.Defense) return false;
        if(this.entity.isSitting()) this.entity.setSitting(false);
        if(!this.entity.getAutoAttack()) this.entity.setAutoAttack(true);
        return this.entity.squaredDistanceTo(this.entity.getBaseX(), this.entity.getBaseY(), this.entity.getBaseZ()) >= (this.minDistance * this.minDistance);
    } // canStart ()

    public void start() {
        if(this.entity.getCurrentState() != EntityState.Defense) return;
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.entity.getPathfindingPenalty(PathNodeType.WATER);
        this.entity.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
    } // start ()

    public void stop() {
        this.navigation.stop();
        this.entity.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
    } // stop ()

    public void tick() {
        this.entity.getLookControl().lookAt(this.entity.getBaseX(), this.entity.getBaseY(), this.entity.getBaseZ(), 10.0F, (float)this.entity.getMaxLookPitchChange());
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = this.getTickCount(10);
            if (!this.entity.isLeashed() && !this.entity.hasVehicle()) {
                if (this.entity.squaredDistanceTo(this.entity.getBaseX(), this.entity.getBaseY(), this.entity.getBaseZ()) >= 144.0) {
                    this.tryTeleport();
                } else {
                    this.navigation.startMovingTo(this.entity.getBaseX(), this.entity.getBaseY(), this.entity.getBaseZ(), this.speed);
                }

            }
        }
    } // tick ()

    // -- Custom Methods --
    public boolean shouldContinue() {
        if (this.navigation.isIdle()) return false;
        if(this.entity.isSitting()) this.entity.setSitting(false);
        return !(this.entity.squaredDistanceTo(this.entity.getBaseX(), this.entity.getBaseY(), this.entity.getBaseZ()) > (this.maxDistance * this.maxDistance));
    } // shouldContinue ()

    private void tryTeleport() {
        for(int i = 0; i < 10; ++i) {
            int j = this.getRandomInt(-3, 3);
            int k = this.getRandomInt(-1, 1);
            int l = this.getRandomInt(-3, 3);
            boolean bl = this.tryTeleportTo((int)this.entity.getBaseX() + j, (int)this.entity.getBaseY() + k, (int)this.entity.getBaseZ() + l);
            if (bl) return;
        }
    } // tryTeleport ()

    private boolean tryTeleportTo(int x, int y, int z) {
        if (Math.abs((double)x - this.entity.getBaseX()) < 2.0 && Math.abs((double)z - this.entity.getBaseZ()) < 2.0) {
            return false;
        } else if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.entity.refreshPositionAndAngles((double)x + 0.5, (double)y, (double)z + 0.5, this.entity.getYaw(), this.entity.getPitch());
            this.navigation.stop();
            return true;
        }
    } // tryTeleportTo ()

    private boolean canTeleportTo(BlockPos pos) {
        PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType(this.world, pos.mutableCopy());
        if (pathNodeType != PathNodeType.WALKABLE) {
            return false;
        } else {
            BlockState blockState = this.world.getBlockState(pos.down());
            if (blockState.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockPos = pos.subtract(this.entity.getBlockPos());
                return this.world.isSpaceEmpty(this.entity, this.entity.getBoundingBox().offset(blockPos));
            }
        }
    } // canTeleportTo ()

    private int getRandomInt(int min, int max) {
        return this.entity.getRandom().nextInt(max - min + 1) + min;
    } // getRandomInt ()

} // Class AiBaseDefenseGoal