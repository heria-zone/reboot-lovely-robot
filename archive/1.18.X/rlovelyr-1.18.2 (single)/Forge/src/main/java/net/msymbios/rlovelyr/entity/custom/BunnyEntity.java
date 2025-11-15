package net.msymbios.rlovelyr.entity.custom;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.entity.goal.AiAutoAttackGoal;
import net.msymbios.rlovelyr.entity.goal.AiBaseDefenseGoal;
import net.msymbios.rlovelyr.entity.goal.AiFollowOwnerGoal;
import net.msymbios.rlovelyr.entity.internal.InternalAnimation;
import net.msymbios.rlovelyr.entity.internal.InternalEntity;
import net.msymbios.rlovelyr.entity.internal.InternalEntityType;
import net.msymbios.rlovelyr.entity.internal.NativeEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.core.manager.SingletonAnimationFactory;

import static net.msymbios.rlovelyr.item.LovelyRobotItems.BUNNY_SPAWN;

public class BunnyEntity extends InternalEntity implements IAnimatable {

    // -- Variables --

    private final AnimationFactory cache = new SingletonAnimationFactory(this);

    // -- Constructor --

    public BunnyEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        rotate(Rotation.getRandom(this.getRandom()));
        this.nativeEntity = NativeEntity.BUNNY;
    } // Constructor BunnyEntity ()

    // -- Inherited Methods --

    @Override
    public void registerControllers(AnimationData controllerRegister) {
        controllerRegister.addAnimationController(InternalAnimation.locomotionAnimation(this));
        controllerRegister.addAnimationController(InternalAnimation.attackAnimation(this));
    } // registerControllers ()

    @Override
    public AnimationFactory getFactory() { return cache; } // getFactory ()

    @Override
    public ItemStack setDropItem() {
        return new ItemStack(BUNNY_SPAWN.get(), 1);
    } // setDropItem ()

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, LovelyRobotConfig.COMMON.movementMeleeAttack.get(), true));
        this.goalSelector.addGoal(4, new AiFollowOwnerGoal(this, LovelyRobotConfig.COMMON.movementFollowOwner.get(), LovelyRobotConfig.COMMON.followDistanceMax.get().floatValue(), LovelyRobotConfig.COMMON.followDistanceMin.get().floatValue(), false));
        this.goalSelector.addGoal(4, new AiBaseDefenseGoal(this, LovelyRobotConfig.COMMON.movementFollowOwner.get().floatValue(), LovelyRobotConfig.COMMON.baseDefenceRange.get().floatValue(), LovelyRobotConfig.COMMON.baseDefenceWarpRange.get().floatValue()));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, LovelyRobotConfig.COMMON.movementWanderAround.get()));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, LovelyRobotConfig.COMMON.lookRange.get().floatValue()));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, InternalEntity.class, LovelyRobotConfig.COMMON.lookRange.get().floatValue()));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new AiAutoAttackGoal<>(this, Mob.class, LovelyRobotConfig.COMMON.attackChance.get(), true, false, InternalEntityType.AvoidAttackingEntities));
    } // registerGoals ()

    // -- Custom Methods --

    /**
     * @return the additional attributes to add to the entity.
     */
    public static AttributeSupplier createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, NativeEntity.BUNNY.getMaxHealth())
                .add(Attributes.ATTACK_DAMAGE, NativeEntity.BUNNY.getAttackDamage())
                .add(Attributes.ATTACK_SPEED, NativeEntity.BUNNY.getAttackSpeed())
                .add(Attributes.MOVEMENT_SPEED, NativeEntity.BUNNY.getMoveSpeed())
                .add(Attributes.ARMOR, NativeEntity.BUNNY.getArmour())
                .add(Attributes.ARMOR_TOUGHNESS, NativeEntity.BUNNY.getArmourToughness())
                .build();
    } // createAttributes ()

} // Class BunnyEntity