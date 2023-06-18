package net.msymbios.rlovelyr.entity.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.msymbios.rlovelyr.entity.custom.VanillaEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class AutoAttackGoal<T extends LivingEntity> extends ActiveTargetGoal {

    // -- Variables --
    private boolean autoAttack;

    // -- Inherited Methods --
    @Override
    public boolean canStart() {
        if(autoAttack) return super.canStart();
        else return false;
    } // canStart ()

    @Override
    public void start() {
        if(autoAttack) super.start();
    } // start ()

    // -- Constructors --
    public AutoAttackGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility, boolean autoAttack) {
        super(mob, targetClass, checkVisibility);
        this.autoAttack = autoAttack;
    } // Constructor AutoAttackGoal ()

    public AutoAttackGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility, Predicate<LivingEntity> targetPredicate, boolean autoAttack) {
        super(mob, targetClass, checkVisibility, targetPredicate);
        this.autoAttack = autoAttack;
    } // Constructor AutoAttackGoal ()

    public AutoAttackGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility, boolean checkCanNavigate, boolean autoAttack) {
        super(mob, targetClass, checkVisibility, checkCanNavigate);
        this.autoAttack = autoAttack;
    } // Constructor AutoAttackGoal ()

    public AutoAttackGoal(MobEntity mob, Class<T> targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, @Nullable Predicate<LivingEntity> targetPredicate, boolean autoAttack) {
        super(mob, targetClass, reciprocalChance, checkVisibility, checkCanNavigate, targetPredicate);
        this.autoAttack = autoAttack;
    } // Constructor AutoAttackGoal ()

} // Class AutoAttackGoal