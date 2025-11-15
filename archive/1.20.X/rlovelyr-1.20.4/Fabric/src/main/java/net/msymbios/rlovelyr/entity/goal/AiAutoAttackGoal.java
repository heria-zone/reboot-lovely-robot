package net.msymbios.rlovelyr.entity.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.msymbios.rlovelyr.entity.internal.RobotEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class AiAutoAttackGoal<T extends LivingEntity> extends ActiveTargetGoal {

    // -- Variable --
    private RobotEntity m_entity;

    // -- Constructors --
    public AiAutoAttackGoal(RobotEntity mob, Class<T> targetClass, boolean checkVisibility) {
        super(mob, targetClass, 10, checkVisibility, false, null);
        m_entity = mob;
    } // Constructors AutoActiveTargetGoal ()

    public AiAutoAttackGoal(RobotEntity mob, Class<T> targetClass, boolean checkVisibility, Predicate<LivingEntity> targetPredicate) {
        super(mob, targetClass, 10, checkVisibility, false, targetPredicate);
        m_entity = mob;
    } // Constructors AutoActiveTargetGoal ()

    public AiAutoAttackGoal(RobotEntity mob, Class<T> targetClass, boolean checkVisibility, boolean checkCanNavigate) {
        super(mob, targetClass, 10, checkVisibility, checkCanNavigate, null);
        m_entity = mob;
    } // Constructors AutoActiveTargetGoal ()

    public AiAutoAttackGoal(RobotEntity mob, Class<T> targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, @Nullable Predicate<LivingEntity> targetPredicate) {
        super(mob, targetClass, reciprocalChance, checkVisibility, checkCanNavigate, targetPredicate);
        m_entity = mob;
    } // Constructors AutoActiveTargetGoal ()

    // -- Inherited Methods --
    @Override
    public boolean canStart() {
        if(CheckAutoAttack()) return super.canStart();
        return false;
    } // canStart ()

    @Override
    public void start() {
        if(CheckAutoAttack()) super.start();
    } // start ()

    // -- Custom Method --
    public boolean CheckAutoAttack() {
        if(m_entity == null) return false;
        return m_entity.getAutoAttack();
    } // CheckAutoAttack ()

} // Class AiAutoAttackGoal