package net.heriazone.lovelylib.common.entity;

import net.heriazone.hzlib.api.entity.internal.EntityParticles;
import net.heriazone.hzlib.api.entity.NativeAnimation;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.goal.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

/**
 * Tribute-specific robot entity with heart particles and no spawn sound.
 * <p>
 * <b>Architecture:</b> Extends {@link NativeRobotEntity} and overrides only
 * {@link #onTameEffect(Player)} — the protected hook extracted in {@link RobotEntity}
 * specifically for this purpose. The ownership, registry, and display-message flow
 * in {@link RobotEntity#handleTame(Player)} is untouched.
 * <p>
 * <b>Tribute behavior:</b> Spawning produces {@link EntityParticles#Heart Heart}
 * particles (tame event byte 7) with no sound, evoking a gentler, nostalgic feel
 * that matches the Tribute mod's faithful-recreation identity.
 * <p>
 * <b>Contrast with legacy/reboot:</b> Those mods use the default {@link RobotEntity}
 * implementation — {@link EntityParticles#Poof Poof} particles + TOTEM_USE sound.
 */
public class TributeRobotEntity extends NativeRobotEntity implements GeoEntity {

    // -- Variables --

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    // -- Constructor --

    /**
     * NeoForge constructor — does not call {@code handlePostSpawnInitialization()}
     * to match the NeoForge {@link NativeRobotEntity} contract.
     */
    public TributeRobotEntity(EntityType<? extends RobotEntity> entityType, Level level, RobotFamily nativeEntity) {
        super(entityType, level, nativeEntity);
    } // Constructor: TributeRobotEntity ()

    // -- Tame Effect Override --

    /**
     * Tribute spawn effect — heart particles only, no sound.
     * <p>
     * Overrides {@link RobotEntity#onTameEffect(Player)} so that only the
     * visual feedback changes; all ownership and registry logic in
     * {@link RobotEntity#handleTame(Player)} remains unmodified.
     *
     * @param player the player taming this robot
     */
    @Override
    protected void onTameEffect(Player player) {
        EntityParticles.Heart(this);
        // Intentionally no sound — Tribute spawn is silent
    } // onTameEffect ()

    // -- AI Goals --

    /**
     * Tribute-specific goal set — faithful translation of the original LovelyRobot
     * {@code EntityAIBunnyFollowPoint} goal stack.
     * <p>
     * <b>Differences from Legacy/Reboot ({@link RobotEntity#registerGoals()}):</b>
     * <ul>
     *   <li>Priority 4 defense goal is {@link AiTributeReturnToBaseGoal} (point-return
     *       only) instead of {@link AiBaseDefenseGoal} (PATROL→GUARD state machine).</li>
     *   <li>Priority 6 wander goal is vanilla {@link WaterAvoidingRandomStrollGoal}
     *       instead of {@link AiConditionalWanderGoal} — no owner-stationary detection,
     *       matching the original mod's simpler wander behavior.</li>
     * </ul>
     */
    @Override
    protected void registerGoals() {
        // Movement / survival
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this,
                SharedConfigs.Common.MovementMeleeAttack, true));

        // Companion behavior — Follow and Defense run at equal priority (4)
        this.goalSelector.addGoal(4, new AiFollowOwnerGoal(this,
                SharedConfigs.Common.MovementFollowOwner,
                SharedConfigs.Common.FollowDistanceMin,
                SharedConfigs.Common.FollowDistanceMax));
        this.goalSelector.addGoal(4, new AiTributeReturnToBaseGoal(this,
                SharedConfigs.Common.MovementFollowOwner,
                SharedConfigs.Common.BaseDefenceRange,
                SharedConfigs.Common.BaseDefenceWarpRange));

        // Vanilla wander — no owner-stationary detection (original mod behavior)
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this,
                SharedConfigs.Common.MovementWanderAround));

        // Look goals
        this.goalSelector.addGoal(7, new AiConditionalLookGoal(this,
                Player.class, SharedConfigs.Common.LookRange));
        this.goalSelector.addGoal(7, new AiConditionalLookGoal(this,
                LivingEntity.class, SharedConfigs.Common.LookRange));
        this.goalSelector.addGoal(8, new AiConditionalRandomLookGoal(this));

        // Target selectors
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new AiAutoAttackGoal<>(this, Mob.class,
                SharedConfigs.Common.AttackChance, true, false,
                entity -> entity instanceof Monster && !(entity instanceof Creeper)));
    } // registerGoals ()

    // -- GeckoLib --

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegister) {
        controllerRegister.add(NativeAnimation.locomotionAnimation(this));
        controllerRegister.add(NativeAnimation.attackAnimation(this));
    } // registerControllers ()

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; } // getAnimatableInstanceCache ()

} // Class: TributeRobotEntity
