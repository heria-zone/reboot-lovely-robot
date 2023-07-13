package net.msymbios.rlovelyr.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
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

import static net.msymbios.rlovelyr.entity.internal.Utility.*;

public class Bunny2Entity extends InternalEntity implements IAngerable, IAnimatable {

    // -- Variables --
    private static ResourceLocation currentModel;
    private static ResourceLocation currentAnimator;
    private final AnimationFactory cache = new AnimationFactory(this);

    // -- Properties --
    public static AttributeModifierMap setAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, InternalMetric.getAttributeValue(EntityVariant.Bunny2, EntityAttribute.MAX_HEALTH))
                .add(Attributes.ATTACK_DAMAGE, InternalMetric.getAttributeValue(EntityVariant.Bunny2, EntityAttribute.ATTACK_DAMAGE))
                .add(Attributes.ATTACK_SPEED, InternalMetric.getAttributeValue(EntityVariant.Bunny2, EntityAttribute.ATTACK_SPEED))
                .add(Attributes.MOVEMENT_SPEED, InternalMetric.getAttributeValue(EntityVariant.Bunny2, EntityAttribute.MOVEMENT_SPEED))
                .add(Attributes.ARMOR, InternalMetric.getAttributeValue(EntityVariant.Bunny2, EntityAttribute.ARMOR))
                .add(Attributes.ARMOR_TOUGHNESS, InternalMetric.getAttributeValue(EntityVariant.Bunny2, EntityAttribute.ARMOR_TOUGHNESS)).build();
    } // setAttributes ()

    // -- MODEL --
    public ResourceLocation getCurrentModel() {
        return currentModel;
    } // getCurrentModel ()

    public void setCurrentModel(EntityModel value) {
        currentModel = InternalMetric.getModel(EntityVariant.Bunny2, value);
    } // setCurrentModel ()

    // -- ANIMATOR --
    public ResourceLocation getCurrentAnimator() {
        return currentAnimator;
    } // getCurrentAnimator ()

    public void setCurrentAnimator(EntityAnimation value) {
        currentAnimator = InternalMetric.ANIMATIONS.get(value);
    } // setCurrentAnimator ()

    // TEXTURE
    @Override
    public ResourceLocation getTextureByID(int value) {
        return InternalMetric.getTexture(EntityVariant.Bunny2, EntityTexture.byId(value));
    } // getTextureByID ()

    // VARIANT
    @Override
    public String getVariant() {
        return this.getVariant(EntityVariant.Bunny2.getName());
    } // getVariant ()

    // STATS
    @Override
    public int getMaxLevel() {
        return this.getMaxLevel((int)InternalMetric.getAttributeValue(EntityVariant.Bunny2, EntityAttribute.MAX_LEVEL));
    } // getMaxLevel ()

    @Override
    public int getCurrentLevel() {
        int level = (int)(InternalMetric.getAttributeValue(EntityVariant.Bunny2, EntityAttribute.MAX_LEVEL));
        if(level != getMaxLevel()) setMaxLevel(level);
        return super.getCurrentLevel();
    } // getCurrentLevel ()

    @Override
    public int getHpValue() {
        return this.getHpValue((int) InternalMetric.getAttributeValue(EntityVariant.Bunny2, EntityAttribute.MAX_HEALTH));
    } // getHpValue ()

    @Override
    public int getAttackValue() {
        return this.getHpValue((int) InternalMetric.getAttributeValue(EntityVariant.Bunny2, EntityAttribute.ATTACK_DAMAGE));
    } // getAttackValue ()

    @Override
    public int getDefenseValue() {
        return this.getHpValue((int) InternalMetric.getAttributeValue(EntityVariant.Bunny2, EntityAttribute.DEFENSE));
    } // getDefenseValue ()

    // -- Constructor --
    public Bunny2Entity(EntityType<? extends TameableEntity> entityType, World level) {
        super(entityType, level);
        setCurrentModel(EntityModel.Unarmed);
        setCurrentAnimator(EntityAnimation.Locomotion);
    } // Constructor Bunny2Entity ()

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
    public ILivingEntityData finalizeSpawn(IServerWorld levelAccessor, DifficultyInstance instance, SpawnReason mobSpawnType, @Nullable ILivingEntityData spawnGroupData, @Nullable CompoundNBT compoundTag) {
        this.setVariant(EntityVariant.Bunny2.name());
        this.setTexture(getRandomNumber(InternalMetric.getTextureCount(EntityVariant.Bunny2)));
        this.setMaxLevel((int) InternalMetric.getAttributeValue(EntityVariant.Bunny2, EntityAttribute.MAX_LEVEL));
        return super.finalizeSpawn(levelAccessor, instance, mobSpawnType, spawnGroupData, compoundTag);
    } // finalizeSpawn ()

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new SitGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, InternalMetric.MeleeAttackMovement, true));
        this.goalSelector.addGoal(4, new AiFollowOwnerGoal(this, InternalMetric.FollowOwnerMovement, InternalMetric.FollowBehindDistance, InternalMetric.FollowCloseDistance, false));
        this.goalSelector.addGoal(4, new AiBaseDefenseGoal(this, InternalMetric.FollowOwnerMovement, InternalMetric.BaseDefenseRange, InternalMetric.BaseDefenseWarpRange));
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, InternalMetric.LookAtRange));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new AiAutoAttackGoal<>(this, MobEntity.class, 5, true, false, InternalMetric.AvoidAttackingEntities));
        this.targetSelector.addGoal(5, new ResetAngerGoal<>(this, false));
    } // registerGoals ()

    @Override
    public void tick() {
        super.tick();
        handleAutoHeal();
    } // tick ()

    @Override
    public void onEnterCombat() {
        setCurrentModel(EntityModel.Armed);
        super.onEnterCombat();
    } // onEnterCombat ()

    @Override
    public void onLeaveCombat() {
        setCurrentModel(EntityModel.Unarmed);
        super.onLeaveCombat();
    } // onLeaveCombat ()

    // -- Save Methods --
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, EntityVariant.Vanilla.getName());
        this.entityData.define(MAX_LEVEL, (int) InternalMetric.getAttributeValue(EntityVariant.Bunny2, EntityAttribute.MAX_LEVEL));
    } // defineSynchedData ()

    // -- Inherited --
    @Override
    public int getRemainingPersistentAngerTime() {
        return 0;
    } // getRemainingPersistentAngerTime ()

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

} // Class Bunny2Entity