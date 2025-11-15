package net.msymbios.rlovelyr.entity.goal;

import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.msymbios.rlovelyr.common.entity.InternalEntity;
import net.msymbios.rlovelyr.common.entity.enums.EntityState;

public class AiFollowOwnerGoal extends FollowOwnerGoal {

    // -- Variable --
    private InternalEntity entity;

    // -- Constructor --
    public AiFollowOwnerGoal(InternalEntity mob, double p_25295_, float p_25296_, float p_25297_, boolean p_25298_) {
        super(mob, p_25295_, p_25296_, p_25297_, p_25298_);
        entity = mob;
    } // Constructor AiFollowOwnerGoal ()

    // -- Inherited Methods --
    @Override
    public boolean canUse() {
        if(CheckEntityState()) return super.canUse();
        return false;
    } // canUse ()

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