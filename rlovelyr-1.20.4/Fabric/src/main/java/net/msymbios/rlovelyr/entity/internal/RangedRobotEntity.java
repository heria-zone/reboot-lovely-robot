package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.common.entity.InternalEntity;
import net.msymbios.rlovelyr.common.entity.InternalEntityType;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.entity.goal.AIRangedAttackGoal;
import net.msymbios.rlovelyr.entity.goal.AiAutoAttackGoal;
import net.msymbios.rlovelyr.entity.goal.AiBaseDefenseGoal;
import net.msymbios.rlovelyr.entity.goal.AiFollowOwnerGoal;
import org.jetbrains.annotations.Nullable;

public abstract class RangedRobotEntity extends RobotEntity implements RangedAttackMob {

    // -- Variables --

    private final AIRangedAttackGoal<RangedRobotEntity> bowAttackGoal = new AIRangedAttackGoal(this, 1.0, 20, 15.0F);

    // -- Constructors --

    public RangedRobotEntity(EntityType<? extends InternalEntity> entityType, World world) {
        super(entityType, world);
        //this.updateAttackType();
    }

    // -- Inherited Methods --

    @Override
    protected void initGoals() {
        this.goalSelector.add(3, new AIRangedAttackGoal<>(this, 1.0, 20, 15.0F));
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(4, new AiFollowOwnerGoal(this, LovelyRobotConfig.Common.MovementFollowOwner, LovelyRobotConfig.Common.FollowDistanceMax, LovelyRobotConfig.Common.FollowDistanceMin, false));
        this.goalSelector.add(4, new AiBaseDefenseGoal(this, LovelyRobotConfig.Common.MovementFollowOwner, LovelyRobotConfig.Common.BaseDefenceRange, LovelyRobotConfig.Common.BaseDefenceWarpRange));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, LovelyRobotConfig.Common.MovementWanderAround));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, LovelyRobotConfig.Common.LookRange));
        this.goalSelector.add(6, new LookAtEntityGoal(this, LivingEntity.class, LovelyRobotConfig.Common.LookRange));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new RevengeGoal(this));
        this.targetSelector.add(4, new AiAutoAttackGoal<>(this, MobEntity.class, LovelyRobotConfig.Common.AttackChance, true, false, InternalEntityType.AvoidAttackingEntities));
    } // initGoals ()

    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        ItemStack itemStack = this.getProjectileType(this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW)));
        PersistentProjectileEntity persistentProjectileEntity = this.createArrowProjectile(itemStack, pullProgress);
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333) - persistentProjectileEntity.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        persistentProjectileEntity.setVelocity(d, e + g * 0.20000000298023224, f, 1.6F, (float)(14 - this.getWorld().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.getWorld().spawnEntity(persistentProjectileEntity);
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        super.initEquipment(random, localDifficulty);
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        changeWeapon = true;
        this.initEquipment(random, difficulty);
        this.updateEnchantments(random, difficulty);
        //this.updateAttackType();
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    // -- Custom Methods --

    public void updateAttackType() {
        if (this.getWorld() != null && !this.getWorld().isClient) {
            this.goalSelector.remove(this.bowAttackGoal);
            ItemStack itemStack = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW));
            if (itemStack.isOf(Items.BOW)) {
                int i = 20;
                if (this.getWorld().getDifficulty() != Difficulty.HARD) {
                    i = 40;
                }

                this.bowAttackGoal.setAttackInterval(i);
                this.goalSelector.add(4, this.bowAttackGoal);
            }
        }
    }

    protected PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float damageModifier) {
        return ProjectileUtil.createArrowProjectile(this, arrow, damageModifier);
    }

    public boolean canUseRangedWeapon(RangedWeaponItem weapon) {
        return weapon == Items.BOW;
    }

    public void equipStack(EquipmentSlot slot, ItemStack stack) {
        super.equipStack(slot, stack);
        //if (!this.getWorld().isClient) this.updateAttackType();
    }

    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 1.74F;
    }

} // Class: RangedRobotEntity