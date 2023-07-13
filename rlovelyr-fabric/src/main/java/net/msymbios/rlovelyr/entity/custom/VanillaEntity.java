package net.msymbios.rlovelyr.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.entity.enums.*;
import net.msymbios.rlovelyr.entity.goal.AiFollowOwnerGoal;
import net.msymbios.rlovelyr.entity.goal.AiAutoAttackGoal;
import net.msymbios.rlovelyr.entity.goal.AiBaseDefenseGoal;
import net.msymbios.rlovelyr.entity.internal.*;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import static net.msymbios.rlovelyr.entity.internal.Utility.*;

public class VanillaEntity extends InternalEntity implements IAnimatable {

    // -- Variables --
    private Identifier currentModel;
    private Identifier currentAnimator;
    private final AnimationFactory cache = new AnimationFactory(this);

    // -- Properties --
    public static DefaultAttributeContainer.Builder setAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, InternalMetric.getAttributeValue(EntityVariant.Vanilla, EntityAttribute.MAX_HEALTH))
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, InternalMetric.getAttributeValue(EntityVariant.Vanilla, EntityAttribute.ATTACK_DAMAGE))
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, InternalMetric.getAttributeValue(EntityVariant.Vanilla, EntityAttribute.ATTACK_SPEED))
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, InternalMetric.getAttributeValue(EntityVariant.Vanilla, EntityAttribute.MOVEMENT_SPEED))
                .add(EntityAttributes.GENERIC_ARMOR, InternalMetric.getAttributeValue(EntityVariant.Vanilla, EntityAttribute.ARMOR))
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, InternalMetric.getAttributeValue(EntityVariant.Vanilla, EntityAttribute.ARMOR_TOUGHNESS));
    } // setAttributes ()

    // -- MODEL --
    public Identifier getCurrentModel() {
        return currentModel;
    } // getCurrentTexture ()

    public void setCurrentModel(EntityModel value) {
        currentModel = InternalMetric.getModel(EntityVariant.Vanilla, value);
    } // setCurrentAnimator ()

    // -- ANIMATOR --
    public Identifier getCurrentAnimator() {
        return currentAnimator;
    } // getCurrentAnimator ()

    public void setCurrentAnimator(EntityAnimation value) {
        currentAnimator = InternalMetric.ANIMATIONS.get(value);
    } // setCurrentAnimator ()

    // TEXTURE
    @Override
    public Identifier getTextureByID(int value) { return InternalMetric.getTexture(EntityVariant.Vanilla, EntityTexture.byId(value)); } // getTextureByID ()

    // VARIANT
    @Override
    public String getVariant() {
        return this.getVariant(EntityVariant.Vanilla.getName());
    } // getVariant ()

    // STATS
    @Override
    public int getMaxLevel(){ return getMaxLevel ((int) InternalMetric.getAttributeValue(EntityVariant.Vanilla, EntityAttribute.MAX_LEVEL)); } // getMaxLevel ()

    @Override
    public int getCurrentLevel(){
        int level = (int)(InternalMetric.getAttributeValue(EntityVariant.Vanilla, EntityAttribute.MAX_LEVEL));
        if(level != getMaxLevel()) setMaxLevel(level);
        return super.getCurrentLevel();
    } // getLevel ()

    @Override
    public int getHpValue() {
        return getHpValue((int) InternalMetric.getAttributeValue(EntityVariant.Vanilla, EntityAttribute.MAX_HEALTH));
    } // getHpValue ()

    @Override
    public int getAttackValue() {
        return getAttackValue((int) InternalMetric.getAttributeValue(EntityVariant.Vanilla, EntityAttribute.ATTACK_DAMAGE));
    } // getAttackValue ()

    @Override
    public int getDefenseValue() {
        return getDefenseValue((int) InternalMetric.getAttributeValue(EntityVariant.Vanilla, EntityAttribute.DEFENSE));
    } // getDefenseValue ()

    // -- Constructor --
    public VanillaEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        setCurrentModel(EntityModel.Unarmed);
        setCurrentAnimator(EntityAnimation.Locomotion);
    } // Constructor RobotEntity ()

    // -- Animations --
    @Override
    public void registerControllers(AnimationData controllerRegister) {
        controllerRegister.addAnimationController(InternalAnimation.locomotionAnimation(this));
        controllerRegister.addAnimationController(InternalAnimation.attackAnimation(this));
    } // registerControllers ()

    @Override
    public AnimationFactory getFactory() {
        return cache;
    } // getFactory ()

    // -- Inherited Methods --
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityNbt) {
        this.setVariant(EntityVariant.Vanilla.getName());
        this.setTexture(getRandomNumber(InternalMetric.getTextureCount(EntityVariant.Vanilla)));
        this.setMaxLevel((int) InternalMetric.getAttributeValue(EntityVariant.Vanilla, EntityAttribute.MAX_LEVEL));

        EquipmentSlot slot = EquipmentSlot.MAINHAND;
        ItemStack diamondSword = new ItemStack(Items.DIAMOND_SWORD);
        this.equipStack(slot, diamondSword);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    } // initialize ()

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(3, new MeleeAttackGoal(this, InternalMetric.MeleeAttackMovement, false));
        this.goalSelector.add(4, new AiFollowOwnerGoal(this, InternalMetric.FollowOwnerMovement, InternalMetric.FollowBehindDistance, InternalMetric.FollowCloseDistance, false));
        this.goalSelector.add(4, new AiBaseDefenseGoal(this, InternalMetric.FollowOwnerMovement, InternalMetric.BaseDefenseRange, InternalMetric.BaseDefenseWarpRange));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, InternalMetric.WanderAroundMovement));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, InternalMetric.LookAtRange));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new RevengeGoal(this));
        this.targetSelector.add(4, new AiAutoAttackGoal(this, MobEntity.class, 5, false, false, InternalMetric.AvoidAttackingEntities));
        this.targetSelector.add(5, new UniversalAngerGoal(this, true));
    } // initGoals ()

    @Override
    public void tick() {
        super.tick();
        handleModelTransition();
        handleAutoHeal();
    } // tick ()

    // -- Custom Methods --
    private void handleModelTransition () {
        if(this.isAttacking()) setCurrentModel(EntityModel.Armed);
        else setCurrentModel(EntityModel.Unarmed);
    } // handleModelTransition ()

    // -- Save Methods --
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, EntityVariant.Vanilla.getName());
        this.dataTracker.startTracking(MAX_LEVEL, (int) InternalMetric.getAttributeValue(EntityVariant.Vanilla, EntityAttribute.MAX_LEVEL));
    } // initDataTracker ()

} // Class VanillaEntity