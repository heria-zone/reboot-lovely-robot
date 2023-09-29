package net.msymbios.rlovelyr.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.config.InternalMetrics;
import net.msymbios.rlovelyr.entity.enums.EntityAttribute;
import net.msymbios.rlovelyr.entity.enums.EntityVariant;
import net.msymbios.rlovelyr.goal.AiAutoAttackGoal;
import net.msymbios.rlovelyr.goal.AiBaseDefenseGoal;
import net.msymbios.rlovelyr.goal.AiFollowOwnerGoal;
import net.msymbios.rlovelyr.entity.internal.InternalAnimation;
import net.msymbios.rlovelyr.entity.internal.InternalEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.core.manager.SingletonAnimationFactory;

public class Bunny2Entity extends InternalEntity implements IAnimatable {

    // -- Variables --
    private final AnimationFactory cache = new SingletonAnimationFactory(this);

    // -- Properties --
    public static DefaultAttributeContainer.Builder setAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, InternalMetrics.BUNNY2_MAX_HEALTH)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, InternalMetrics.BUNNY2_ATTACK_DAMAGE)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, InternalMetrics.BUNNY2_ATTACK_SPEED)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, InternalMetrics.BUNNY2_MOVEMENT_SPEED)
                .add(EntityAttributes.GENERIC_ARMOR, InternalMetrics.GENERAL_ARMOR)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, InternalMetrics.GENERAL_ARMOR_TOUGHNESS);
    } // setAttributes ()

    // -- Constructor --
    public Bunny2Entity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.variant = EntityVariant.Bunny2;
    } // Constructor Bunny2Entity ()

    // -- Inherited Methods --
    @Override
    public void registerControllers(AnimationData controllerRegister) {
        controllerRegister.addAnimationController(InternalAnimation.locomotionAnimation(this));
        controllerRegister.addAnimationController(InternalAnimation.attackAnimation(this));
    } // registerControllers ()

    @Override
    public AnimationFactory getFactory() {
        return cache;
    } // getFactory ()

    // -- Built-In Methods --
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.variant = EntityVariant.Bunny2;
        this.setTexture(net.msymbios.rlovelyr.entity.internal.InternalMetric.getRandomTextureID(this.variant));
        this.setMaxLevel(getAttribute(EntityAttribute.MAX_LEVEL));
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    } // initialize ()

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(3, new MeleeAttackGoal(this, InternalMetrics.MOVEMENT_MELEE_ATTACK, false));
        this.goalSelector.add(4, new AiFollowOwnerGoal(this, InternalMetrics.MOVEMENT_FOLLOW_OWNER, InternalMetrics.FOLLOW_DISTANCE_MAX, InternalMetrics.FOLLOW_DISTANCE_MIN, false));
        this.goalSelector.add(4, new AiBaseDefenseGoal(this, InternalMetrics.MOVEMENT_FOLLOW_OWNER, InternalMetrics.BUNNY2_BASE_DEFENCE_RANGE, InternalMetrics.BUNNY2_BASE_DEFENCE_WARP_RANGE));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, InternalMetrics.MOVEMENT_WANDER_AROUND));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, InternalMetrics.LOOK_RANGE));
        this.goalSelector.add(6, new LookAtEntityGoal(this, InternalEntity.class, InternalMetrics.LOOK_RANGE));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new RevengeGoal(this));
        this.targetSelector.add(4, new AiAutoAttackGoal(this, MobEntity.class, InternalMetrics.ATTACK_CHANCE, false, false, net.msymbios.rlovelyr.entity.internal.InternalMetric.AvoidAttackingEntities));
        this.targetSelector.add(5, new UniversalAngerGoal(this, true));
    } // initGoals ()

} // Class Bunny2Entity