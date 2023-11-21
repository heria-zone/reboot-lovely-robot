package net.msymbios.rlovelyr.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.entity.enums.*;
import net.msymbios.rlovelyr.entity.goal.AiAutoAttackGoal;
import net.msymbios.rlovelyr.entity.goal.AiBaseDefenseGoal;
import net.msymbios.rlovelyr.entity.goal.AiFollowOwnerGoal;
import net.msymbios.rlovelyr.entity.internal.InternalAnimation;
import net.msymbios.rlovelyr.entity.internal.InternalEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import net.minecraft.world.DifficultyInstance;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.UUID;

public class HoneyEntity extends InternalEntity implements IAngerable, IAnimatable {

    // -- Variables --
    private final AnimationFactory cache = new AnimationFactory(this);

    // -- Properties --
    public static AttributeModifierMap  setAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, InternalMetric.getAttribute(EntityVariant.Honey, EntityAttribute.MAX_HEALTH))
                .add(Attributes.ATTACK_DAMAGE, InternalMetric.getAttribute(EntityVariant.Honey, EntityAttribute.ATTACK_DAMAGE))
                .add(Attributes.ATTACK_SPEED, InternalMetric.getAttribute(EntityVariant.Honey, EntityAttribute.ATTACK_SPEED))
                .add(Attributes.MOVEMENT_SPEED, InternalMetric.getAttribute(EntityVariant.Honey, EntityAttribute.MOVEMENT_SPEED))
                .add(Attributes.ARMOR, InternalMetric.getAttribute(EntityVariant.Honey, EntityAttribute.ARMOR))
                .add(Attributes.ARMOR_TOUGHNESS, InternalMetric.getAttribute(EntityVariant.Honey, EntityAttribute.ARMOR_TOUGHNESS)).build();
    } // setAttributes ()

    // -- Constructor --
    public HoneyEntity(EntityType<? extends TameableEntity> entityType, World level) {
        super(entityType, level);
        this.variant = EntityVariant.Honey;
    } // Constructor HoneyEntity ()

    // -- Animations --
    @Override
    public void registerControllers(AnimationData controllerRegister) {
        controllerRegister.addAnimationController(InternalAnimation.locomotionAnimation(this));
        controllerRegister.addAnimationController(InternalAnimation.attackAnimation(this));
    } // registerControllers ()

    @Override
    public AnimationFactory getFactory() { return cache; } // getFactory ()

    // -- Inherited Methods --
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
        this.variant = EntityVariant.Honey;
        this.setTexture(InternalMetric.getRandomTextureID(this.variant));
        this.setMaxLevel(getAttribute(EntityAttribute.MAX_LEVEL));
        return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
    } // finalizeSpawn ()

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new SitGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, InternalMetric.MOVEMENT_MELEE_ATTACK, true));
        this.goalSelector.addGoal(4, new AiFollowOwnerGoal(this, InternalMetric.MOVEMENT_FOLLOW_OWNER, InternalMetric.FOLLOW_DISTANCE_MAX, InternalMetric.FOLLOW_DISTANCE_MIN, false));
        this.goalSelector.addGoal(4, new AiBaseDefenseGoal(this, InternalMetric.MOVEMENT_FOLLOW_OWNER, InternalMetric.BaseDefenseRange, InternalMetric.BaseDefenseWarpRange));
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, InternalMetric.LOOK_RANGE));
        this.goalSelector.addGoal(5, new LookAtGoal(this, InternalEntity.class, InternalMetric.LOOK_RANGE));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new AiAutoAttackGoal<>(this, MobEntity.class, InternalMetric.ATTACK_CHANCE, true, false, InternalMetric.AvoidAttackingEntities));
        this.targetSelector.addGoal(5, new ResetAngerGoal<>(this, false));
    } // registerGoals ()

    // -- Inherited --
    @Override
    public int getRemainingPersistentAngerTime() { return 0; } // getRemainingPersistentAngerTime ()

    @Override
    public void setRemainingPersistentAngerTime(int p_21673_) {} // setRemainingPersistentAngerTime ()

    @Override
    public UUID getPersistentAngerTarget() {
        return null;
    } // getPersistentAngerTarget ()

    @Override
    public void setPersistentAngerTarget(UUID p_21672_) {} // setPersistentAngerTarget ()

    @Override
    public void startPersistentAngerTimer() {} // startPersistentAngerTimer ()

} // Class HoneyEntity