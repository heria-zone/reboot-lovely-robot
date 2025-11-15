package net.msymbios.rlovelyr.entity.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.msymbios.rlovelyr.entity.internal.InternalEntity;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class AiAutoAttackGoal<T extends LivingEntity> extends NearestAttackableTargetGoal {

    // -- Variables --
    private InternalEntity entity;

    // -- Constructors --
    public AiAutoAttackGoal(InternalEntity mob, Class<T> p_26061_, boolean p_26062_) {
        this(mob, p_26061_, 10, p_26062_, false, (Predicate<LivingEntity>)null);
        entity = mob;
    } // Constructor AiAutoAttackGoal ()

    public AiAutoAttackGoal(InternalEntity mob, Class<T> p_199892_, boolean p_199893_, Predicate<LivingEntity> p_199894_) {
        this(mob, p_199892_, 10, p_199893_, false, p_199894_);
        entity = mob;
    } // Constructor AiAutoAttackGoal ()

    public AiAutoAttackGoal(InternalEntity mob, Class<T> p_26065_, boolean p_26066_, boolean p_26067_) {
        this(mob, p_26065_, 10, p_26066_, p_26067_, (Predicate<LivingEntity>)null);
        entity = mob;
    } // Constructor AiAutoAttackGoal ()

    public AiAutoAttackGoal(InternalEntity mob, Class<T> targetClass, int p_26055_, boolean p_26056_, boolean p_26057_, @Nullable Predicate<LivingEntity> p_26058_) {
        super(mob, targetClass, p_26055_, p_26056_, p_26057_, p_26058_);
        entity = mob;
    } // Constructor AiAutoAttackGoal ()

    // -- Inherited Methods --
    @Override
    public boolean canUse() {
        if(entity.getAutoAttack()) return super.canUse();
        return false;
    } // canUse ()

    @Override
    public void start() {
        if(entity.getAutoAttack()) super.start();
    } // start ()

} // Class AiAutoAttackGoal