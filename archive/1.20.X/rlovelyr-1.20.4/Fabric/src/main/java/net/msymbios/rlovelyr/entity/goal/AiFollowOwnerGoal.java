package net.msymbios.rlovelyr.entity.goal;

import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.msymbios.rlovelyr.common.entity.enums.EntityState;
import net.msymbios.rlovelyr.entity.internal.RobotEntity;

public class AiFollowOwnerGoal extends FollowOwnerGoal {

    // -- Variable --
    private RobotEntity entity;

    // -- Constructor --
    public AiFollowOwnerGoal(RobotEntity tameable, double speed, float minDistance, float maxDistance, boolean leavesAllowed) {
        super((TameableEntity)tameable, speed, minDistance, maxDistance, leavesAllowed);
        entity = tameable;
    } // Constructor FollowOwnerGoal ()

    // -- Inherited Methods --
    @Override
    public boolean canStart() {
        if(CheckEntityState()) return super.canStart();
        return false;
    } // canStart ()

    @Override
    public void start() {
        if(CheckEntityState()) super.start();
    } // start ()

    @Override
    public void stop() {
        if(CheckEntityState()) super.stop();
    } // stop ()

    @Override
    public void tick() {
        if(CheckEntityState()) super.tick();
    } // tick ()

    // -- Custom Methods --
    public boolean CheckEntityState () {
        return entity.getCurrentState() == EntityState.Follow;
    } // CheckEntityState ()

} // Class AiFollowOwnerGoal