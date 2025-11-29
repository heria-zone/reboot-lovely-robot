package net.msymbios.llovelyr.common.entity.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.msymbios.llovelyr.source.entity.common.LovelyRobot;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Conditional targeting goal that respects robot's auto-attack configuration.
 * <p>
 * <b>Architecture:</b> Wraps Minecraft's NearestAttackableTargetGoal with
 * auto-attack state checking, preventing robots from acquiring targets when
 * auto-attack is disabled by owner.
 * <p>
 * <b>Design Decision:</b> Extends rather than delegates to leverage Minecraft's
 * optimized target selection (visibility checks, distance calculations, predicate
 * filtering) while adding single behavioral gate.
 * <p>
 * <b>State Dependency:</b> Queries robot's auto-attack flag before allowing
 * goal execution. Enables dynamic combat behavior toggling without goal
 * reconstruction.
 * <p>
 * <i>Note:</i> Generic type T allows targeting specific entity types (e.g.,
 * Monster.class for hostile mobs, Player.class for PvP).
 *
 * @param <T> target entity type constraint
 */
public class AiAutoAttackGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

    // -- Fields --

    /**
     * Robot entity reference for auto-attack state queries.
     */
    private final LovelyRobot m_entity;

    // -- Constructors --

    /**
     * Creates targeting goal with basic visibility checking.
     *
     * @param mob robot entity to control
     * @param targetClass entity type to target
     * @param checkVisibility whether to require line-of-sight
     */
    public AiAutoAttackGoal(LovelyRobot mob, Class<T> targetClass, boolean checkVisibility) {
        super(mob, targetClass, 10, checkVisibility, false, null);
        m_entity = mob;
    } // Constructor: AiAutoAttackGoal

    /**
     * Creates targeting goal with custom target filtering.
     *
     * @param mob robot entity to control
     * @param targetClass entity type to target
     * @param checkVisibility whether to require line-of-sight
     * @param targetPredicate additional target validation (e.g., health checks)
     */
    public AiAutoAttackGoal(LovelyRobot mob, Class<T> targetClass, boolean checkVisibility, Predicate<LivingEntity> targetPredicate) {
        super(mob, targetClass, 10, checkVisibility, false, targetPredicate);
        m_entity = mob;
    } // Constructor: AiAutoAttackGoal

    /**
     * Creates targeting goal with navigation validation.
     *
     * @param mob robot entity to control
     * @param targetClass entity type to target
     * @param checkVisibility whether to require line-of-sight
     * @param checkCanNavigate whether to verify pathfinding feasibility
     */
    public AiAutoAttackGoal(LovelyRobot mob, Class<T> targetClass, boolean checkVisibility, boolean checkCanNavigate) {
        super(mob, targetClass, 10, checkVisibility, checkCanNavigate, null);
        m_entity = mob;
    } // Constructor: AiAutoAttackGoal

    /**
     * Creates targeting goal with full configuration control.
     *
     * @param mob robot entity to control
     * @param targetClass entity type to target
     * @param reciprocalChance inverse probability (10 = 10% chance per tick)
     * @param checkVisibility whether to require line-of-sight
     * @param checkCanNavigate whether to verify pathfinding feasibility
     * @param targetPredicate additional target validation
     */
    public AiAutoAttackGoal(LovelyRobot mob, Class<T> targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, @Nullable Predicate<LivingEntity> targetPredicate) {
        super(mob, targetClass, reciprocalChance, checkVisibility, checkCanNavigate, targetPredicate);
        m_entity = mob;
    } // Constructor: AiAutoAttackGoal

    // -- Inherited Methods --

    /**
     * Determines if goal can start executing.
     * <p>
     * <b>Behavioral Gate:</b> Only allows target acquisition when auto-attack
     * enabled. Prevents combat initiation when robot is in passive mode.
     *
     * @return true if auto-attack enabled and valid target found
     */
    @Override
    public boolean canUse() {
        if (!checkAutoAttack()) return false;
        return super.canUse();
    } // canUse

    /**
     * Initiates goal execution after canUse approval.
     * <p>
     * <b>Safety Check:</b> Re-validates auto-attack state before starting
     * to handle race conditions from concurrent state changes.
     */
    @Override
    public void start() {
        if (checkAutoAttack()) super.start();
    } // start

    // -- Custom Methods --

    /**
     * Validates robot's auto-attack configuration.
     * <p>
     * <b>Null Safety:</b> Returns false if entity reference lost (should never
     * occur but prevents NPE if goal outlives entity).
     *
     * @return true if robot has auto-attack enabled
     */
    private boolean checkAutoAttack() {
        if (m_entity == null) return false;
        return m_entity.getAutoAttack();
    } // checkAutoAttack

} // Class: AiAutoAttackGoal
