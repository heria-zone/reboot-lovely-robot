package net.msymbios.rlovelyr.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.msymbios.rlovelyr.entity.enums.EntityAttribute;
import net.msymbios.rlovelyr.entity.enums.EntityVariant;
import net.msymbios.rlovelyr.entity.goal.AiAutoAttackGoal;
import net.msymbios.rlovelyr.entity.goal.AiBaseDefenseGoal;
import net.msymbios.rlovelyr.entity.goal.AiFollowOwnerGoal;
import net.msymbios.rlovelyr.entity.internal.InternalAnimation;
import net.msymbios.rlovelyr.entity.internal.InternalEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.core.manager.SingletonAnimationFactory;

import java.util.UUID;

import static net.msymbios.rlovelyr.item.LovelyRobotItems.VANILLA_SPAWN;

public class VanillaEntity extends InternalEntity implements NeutralMob, IAnimatable {

    // -- Variables --
    private final AnimationFactory cache = new SingletonAnimationFactory(this);

    // -- Properties --
    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, InternalMetric.getAttribute(EntityVariant.Vanilla, EntityAttribute.MAX_HEALTH))
                .add(Attributes.ATTACK_DAMAGE, InternalMetric.getAttribute(EntityVariant.Vanilla, EntityAttribute.ATTACK_DAMAGE))
                .add(Attributes.ATTACK_SPEED, InternalMetric.getAttribute(EntityVariant.Vanilla, EntityAttribute.ATTACK_SPEED))
                .add(Attributes.MOVEMENT_SPEED, InternalMetric.getAttribute(EntityVariant.Vanilla, EntityAttribute.MOVEMENT_SPEED))
                .add(Attributes.ARMOR, InternalMetric.getAttribute(EntityVariant.Vanilla, EntityAttribute.ARMOR))
                .add(Attributes.ARMOR_TOUGHNESS, InternalMetric.getAttribute(EntityVariant.Vanilla, EntityAttribute.ARMOR_TOUGHNESS)).build();
    } // setAttributes ()

    // -- Constructor --
    public VanillaEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.variant = EntityVariant.Vanilla;
    } // Constructor VanillaEntity ()

    // -- Animations --
    @Override
    public void registerControllers(AnimationData controllerRegister) {
        controllerRegister.addAnimationController(InternalAnimation.locomotionAnimation(this));
        controllerRegister.addAnimationController(InternalAnimation.attackAnimation(this));
    } // registerControllers ()

    @Override
    public AnimationFactory getFactory() { return cache; } // getFactory ()

    @Override
    public ItemStack setDropItem() {
        return new ItemStack(VANILLA_SPAWN.get(), 1);
    } // setDropItem ()

    // -- Inherited Methods --
    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor levelAccessor, @NotNull DifficultyInstance instance, @NotNull MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        this.variant = EntityVariant.Vanilla;
        this.setTexture(InternalMetric.getRandomTextureID(this.variant));
        this.setMaxLevel(getAttribute(EntityAttribute.MAX_LEVEL));
        return super.finalizeSpawn(levelAccessor, instance, mobSpawnType, spawnGroupData, compoundTag);
    } // finalizeSpawn ()

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, InternalMetric.MOVEMENT_MELEE_ATTACK.get(), true));
        this.goalSelector.addGoal(4, new AiFollowOwnerGoal(this, InternalMetric.MOVEMENT_FOLLOW_OWNER.get(), InternalMetric.FOLLOW_DISTANCE_MAX.get(), InternalMetric.FOLLOW_DISTANCE_MIN.get(), false));
        this.goalSelector.addGoal(4, new AiBaseDefenseGoal(this, InternalMetric.MOVEMENT_FOLLOW_OWNER.get(), InternalMetric.BASE_DEFENCE_RANGE.get(), InternalMetric.BASE_DEFENCE_WARP_RANGE.get()));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, InternalMetric.MOVEMENT_WANDER_AROUND.get()));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, InternalMetric.LOOK_RANGE.get()));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, InternalEntity.class, InternalMetric.LOOK_RANGE.get()));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new AiAutoAttackGoal<>(this, Mob.class, InternalMetric.ATTACK_CHANCE.get(), true, false, InternalMetric.AvoidAttackingEntities));
        this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<>(this, false));
    } // registerGoals ()

    // -- Inherited --
    @Override
    public int getRemainingPersistentAngerTime() { return 0; } // getRemainingPersistentAngerTime ()

    @Override
    public void setRemainingPersistentAngerTime(int p_21673_) {} // setRemainingPersistentAngerTime ()

    @Nullable @Override
    public UUID getPersistentAngerTarget() {
        return null;
    } // getPersistentAngerTarget ()

    @Override
    public void setPersistentAngerTarget(@Nullable UUID p_21672_) {} // setPersistentAngerTarget ()

    @Override
    public void startPersistentAngerTimer() {} // startPersistentAngerTimer ()

} // Class VanillaEntity