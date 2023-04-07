package net.msymbios.rlovelyr.entity.custom;

import net.msymbios.rlovelyr.entity.enums.*;
import net.msymbios.rlovelyr.entity.utils.ModMetrics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Objects;
import java.util.UUID;

import static net.msymbios.rlovelyr.entity.utils.ModUtils.*;

public class HoneyEntity extends TamableAnimal implements NeutralMob, GeoEntity {

    // -- Variables --
    private static final EntityDataAccessor<String> VARIANT = SynchedEntityData.defineId(HoneyEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> TEXTURE_ID = SynchedEntityData.defineId(HoneyEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(HoneyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> AUTO_ATTACK = SynchedEntityData.defineId(HoneyEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> MAX_LEVEL = SynchedEntityData.defineId(HoneyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LEVEL = SynchedEntityData.defineId(HoneyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> EXP = SynchedEntityData.defineId(HoneyEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> FIRE_PROTECTION = SynchedEntityData.defineId(HoneyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FALL_PROTECTION = SynchedEntityData.defineId(HoneyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BLAST_PROTECTION = SynchedEntityData.defineId(HoneyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PROJECTILE_PROTECTION = SynchedEntityData.defineId(HoneyEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Float> BASE_X = SynchedEntityData.defineId(HoneyEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> BASE_Y = SynchedEntityData.defineId(HoneyEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> BASE_Z = SynchedEntityData.defineId(HoneyEntity.class, EntityDataSerializers.FLOAT);

    private static ResourceLocation currentModel;
    private static ResourceLocation currentAnimator;

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    private int autoHealCounter = 0;

    // -- Properties --
    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, ModMetrics.getAttributeValue(RobotVariant.Honey, RobotAttribute.MAX_HEALTH))
                .add(Attributes.ATTACK_DAMAGE, ModMetrics.getAttributeValue(RobotVariant.Honey, RobotAttribute.ATTACK_DAMAGE))
                .add(Attributes.ATTACK_SPEED, ModMetrics.getAttributeValue(RobotVariant.Honey, RobotAttribute.ATTACK_SPEED))
                .add(Attributes.MOVEMENT_SPEED, ModMetrics.getAttributeValue(RobotVariant.Honey, RobotAttribute.MOVEMENT_SPEED))
                .add(Attributes.ARMOR, ModMetrics.getAttributeValue(RobotVariant.Honey, RobotAttribute.ARMOR))
                .add(Attributes.ARMOR_TOUGHNESS, ModMetrics.getAttributeValue(RobotVariant.Honey, RobotAttribute.ARMOR_TOUGHNESS)).build();
    } // setAttributes ()


    // -- MODEL --
    public ResourceLocation getCurrentModel() {
        return currentModel;
    } // getCurrentModel ()

    public void setCurrentModel(String value) {
        currentModel = ModMetrics.getModel(RobotVariant.Honey, RobotModel.byName(value));
    } // setCurrentModel ()

    public void setCurrentModel(RobotModel value) {
        currentModel = ModMetrics.getModel(RobotVariant.Honey, value);
    } // setCurrentModel ()


    // -- ANIMATOR --
    public ResourceLocation getCurrentAnimator() {
        return currentAnimator;
    } // getCurrentAnimator ()

    public void setCurrentAnimator(String value) {
        currentAnimator = ModMetrics.ANIMATIONS.get(value);
    } // setCurrentAnimator ()

    public void setCurrentAnimator(RobotAnimation value) {
        currentAnimator = ModMetrics.ANIMATIONS.get(value.getName());
    } // setCurrentAnimator ()


    // -- TEXTURE --
    public ResourceLocation getTexture() {
        return getTextureByID(getTextureID());
    } // getTexture ()

    public int getTextureID() {
        int value = 0;
        try {value = this.entityData.get(TEXTURE_ID);}
        catch (Exception ignored) {}
        return value;
    } // getTextureID ()

    public ResourceLocation getTextureByID(int value) {
        return ModMetrics.getTexture(RobotVariant.Honey, RobotTexture.byId(value));
    } // getTextureByID ()

    public void setTexture(int value) {
        this.entityData.set(TEXTURE_ID, value);
    } // setTexture ()

    public void setTexture(RobotTexture value) {
        setTexture(value.getId());
    } // setTexture ()


    // -- VARIANT --
    public String getVariant() {
        String value = RobotVariant.Honey.getName();
        try {value = this.entityData.get(VARIANT);}
        catch (Exception ignored) {}
        return value;
    } // getVariant ()

    public void setVariant(String value) {
        this.entityData.set(VARIANT, value);
    } // setVariant ()


    // -- STATE --
    public int getCurrentStateID() {
        int value = RobotState.Standby.getId();
        try {value = this.entityData.get(STATE);}
        catch (Exception ignored) {}
        return value;
    } // getCurrentStateID ()

    public RobotState getCurrentState() {
        RobotState value = RobotState.Standby;
        try {value = RobotState.byId(this.entityData.get(STATE));}
        catch (Exception ignored) {}
        return value;
    } // getCurrentState ()

    public void setCurrentState(RobotState value){
        this.entityData.set(STATE, value.getId());
    } // setCurrentMode ()

    public void setCurrentState(int value){
        this.entityData.set(STATE, value);
    } // setCurrentMode ()


    // -- AUTO ATTACK --
    public boolean getAutoAttack() {
        boolean value = false;
        try {value = this.entityData.get(AUTO_ATTACK);}
        catch (Exception ignored) {}
        return value;
    } // getAutoAttack ()

    public void setAutoAttack(boolean value) {
        this.entityData.set(AUTO_ATTACK, value);
    } // setAutoAttack ()


    // -- STATS --
    public int getMaxLevel(){
        int value = ModMetrics.MaxLevel;
        try {value = this.entityData.get(MAX_LEVEL);}
        catch (Exception ignored) {}
        return value;
    } // getMaxLevel ()

    public void setMaxLevel(int value) {
        this.entityData.set(MAX_LEVEL, value);
    } // setMaxLevel ()

    public int getCurrentLevel(){
        int value = 0;
        try {value = this.entityData.get(LEVEL);}
        catch (Exception ignored){}
        return value;
    } // getLevel ()

    public void setLevel(int value){
        this.entityData.set(LEVEL, value);
        Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(getHpValue());
        Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(getAttackValue());
        Objects.requireNonNull(this.getAttribute(Attributes.ARMOR)).setBaseValue(getBaseArmorValue());
        Objects.requireNonNull(this.getAttribute(Attributes.ARMOR_TOUGHNESS)).setBaseValue(getArmorToughnessValue());
    } // setLevel ()

    public int getExp(){
        int value = 1;
        try {value = this.entityData.get(EXP);}
        catch (Exception ignored){}
        return value;
    } // getExp ()

    public void setExp(int value){
        this.entityData.set(EXP, value);
    } // setExp ()

    public int getHpValue() {
        var hp = (int)ModMetrics.getAttributeValue(RobotVariant.Honey, RobotAttribute.MAX_HEALTH);
        return (hp + this.getCurrentLevel() * hp / 50);
    } // getHpValue ()

    public int getAttackValue() {
        var attack = (int)ModMetrics.getAttributeValue(RobotVariant.Honey, RobotAttribute.ATTACK_DAMAGE);
        return (attack + this.getCurrentLevel() * attack / 50);
    } // getAttackValue ()

    public int getDefenseValue() {
        var defense = (int)ModMetrics.getAttributeValue(RobotVariant.Honey, RobotAttribute.DEFENSE);
        return (defense + this.getCurrentLevel() * defense / 50);
    } // getDefenseValue ()

    public int getLootingLevel() {
        int level = 0;
        if (ModMetrics.LootingEnchantment) {
            level = this.getCurrentLevel() / ModMetrics.LootingRequiredLevel;
            if (level > ModMetrics.MaxLootingLevel) {
                level = ModMetrics.MaxLootingLevel;
            }
        }
        return level;
    } // getLootingLevel ()

    public double getBaseArmorValue () {
        int armor = this.getDefenseValue();
        if (armor > 30) armor = 30;
        return armor;
    } // getArmorValue ()

    public double getArmorToughnessValue () {
        double armor = getArmorValue();
        double armor_tou = 0;
        if (armor > 30) armor_tou = armor - 30;
        return armor_tou;
    } // getArmorToughnessValue ()

    public int getFireProtection() {
        int value = 0;
        try {value = this.entityData.get(FIRE_PROTECTION);}
        catch (Exception ignored) {}
        return value;
    } // getFireProtection ()

    public void setFireProtection(int value) {
        this.entityData.set(FIRE_PROTECTION, value);
    } // setFireProtection ()

    public int getFallProtection() {
        int retValue = 0;
        try {retValue = this.entityData.get(FALL_PROTECTION);}
        catch (Exception ignored) {}
        return retValue;
    } // getFallProtection ()

    public void setFallProtection(int value) {
        this.entityData.set(FALL_PROTECTION, value);
    } // setFallProtection ()

    public int getBlastProtection() {
        int value = 0;
        try {value = this.entityData.get(BLAST_PROTECTION);}
        catch (Exception ignored) {}
        return value;
    } // getBlastProtection ()

    public void setBlastProtection(int value) {
        this.entityData.set(BLAST_PROTECTION, value);
    } // setBlastProtection ()

    public int getProjectileProtection() {
        int value = 0;
        try {value = this.entityData.get(PROJECTILE_PROTECTION);}
        catch (Exception ignored) {}
        return value;
    } // getProjectileProtection ()

    public void setProjectileProtection(int value) {
        this.entityData.set(PROJECTILE_PROTECTION, value);
    } // setProjectileProtection ()

    public float getBaseX() {
        float value = 0;
        try {value = this.entityData.get(BASE_X);}
        catch (Exception ignored) {}
        return value;
    } // getBaseX ()

    public void setBaseX(float value) {
        this.entityData.set(BASE_X, value);
    } // setBaseX ()

    public float getBaseY() {
        float value = 0;
        try {value = this.entityData.get(BASE_Y);}
        catch (Exception ignored) {}
        return value;
    } // getBaseY ()

    public void setBaseY(float value) {
        this.entityData.set(BASE_Y, value);
    } // setBaseY ()

    public float getBaseZ() {
        float value = 0;
        try {value = this.entityData.get(BASE_Z);}
        catch (Exception ignored) {}
        return value;
    } // getBaseZ ()

    public void setBaseZ(float value) {
        this.entityData.set(BASE_Z, value);
    } // setBaseZ ()


    // -- Constructor --
    public HoneyEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        setCurrentModel(RobotModel.Unarmed);
        setCurrentAnimator(RobotAnimation.Locomotion);
    } // Constructor HoneyEntity ()


    // -- Animations --
    private PlayState locomotionAnim(AnimationState animationState) {
        if(animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.lovelyrobot.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if(isOrderedToSit()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.lovelyrobot.sit", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        } else {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.lovelyrobot.idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
    } // locomotionAnim ()

    private PlayState attackAnim(AnimationState state) {
        if(this.swinging && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("animation.lovelyrobot.attack", Animation.LoopType.PLAY_ONCE));
            this.swinging = false;
        }

        return PlayState.CONTINUE;
    } // attackAnim ()

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController(this, "locomotionController", 0, this::locomotionAnim));
        controllerRegistrar.add(new AnimationController(this, "attackController", 0, this::attackAnim));
    } // registerControllers ()

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    } // getAnimatableInstanceCache ()


    // -- Inherited Methods --
    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor levelAccessor, @NotNull DifficultyInstance instance, @NotNull MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        this.setVariant(RobotVariant.Honey.name());
        this.setTexture(getRandomNumber(ModMetrics.getTextureCount(RobotVariant.Honey)));
        this.setMaxLevel(ModMetrics.MaxLevel);
        this.setOrderedToSit(true);
        return super.finalizeSpawn(levelAccessor, instance, mobSpawnType, spawnGroupData, compoundTag);
    } // finalizeSpawn ()

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(6, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, AbstractSkeleton.class, false));
        this.targetSelector.addGoal(7, new ResetUniversalAngerTargetGoal<>(this, true));
    } // registerGoals ()

    @Override
    public void tick() {
        super.tick();
        handleAutoHeal();
    } // tick ()


    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) return false;

        if (source.isFire() && amount >= 1.0f && this.getFireProtection() > 0)
            amount *= (100.0f - this.getFireProtection()) / 100.0f;

        if (source == DamageSource.FALL && amount >= 1.0f && this.getFallProtection() > 0)
            amount *= (100.0f - this.getFallProtection()) / 100.0f;

        if (source.isExplosion() && amount >= 1.0f && this.getBlastProtection() > 0)
            amount *= (100.0f - this.getBlastProtection()) / 100.0f;

        if (source.isProjectile() && amount >= 1.0f && this.getProjectileProtection() > 0)
            amount *= (100.0f - this.getProjectileProtection()) / 100.0f;

        if (amount < 1.0f) return false;

        if(!level.isClientSide) {
            if(source.isFire() && canLevelUpFireProtection()) this.setFireProtection(this.getFireProtection() + 1);
            if(source == DamageSource.FALL && canLevelUpFallProtection()) this.setFallProtection(this.getFallProtection() + 1);
            if(source.isExplosion() && canLevelUpBlastProtection()) this.setBlastProtection(this.getBlastProtection() + 1);
            if(source.isProjectile() && canLevelUpProjectileProtection()) this.setProjectileProtection(this.getProjectileProtection() + 1);
        }

        final Entity entity = source.getEntity();

        if (this.canLevelUp() && !(entity instanceof Player) && entity instanceof LivingEntity && !this.level.isClientSide) {
            final int maxHp = (int)((LivingEntity)entity).getMaxHealth();
            this.addExp(maxHp / 6);
        }

        if (entity != null && !(entity instanceof Player) && !(entity instanceof Arrow)) {
            amount = (amount + 1.0f) / 2.0f;
        }

        return super.hurt(source, amount);
    } // hurt ()

    @Override
    public boolean doHurtTarget(@NotNull Entity target) {
        if(this.canLevelUp() && !(target instanceof Player) && !this.level.isClientSide) {
            final int maxHp = (int)((LivingEntity)target).getMaxHealth();
            this.addExp(maxHp / 4);
        }
        this.level.broadcastEntityEvent(this, (byte)4);
        return super.doHurtTarget(target);
    } // doHurtTarget ()

    @Nullable @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {
        return null;
    } // getBreedOffspring ()


    @Override
    public void onEnterCombat() {
        setCurrentModel(RobotModel.Armed);
        super.onEnterCombat();
    } // onEnterCombat ()

    @Override
    public void onLeaveCombat() {
        setCurrentModel(RobotModel.Unarmed);
        super.onLeaveCombat();
    } // onLeaveCombat ()

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        switch (slot.getType()){
            case HAND: {
                final ItemStack tempSword = new ItemStack(Items.DIAMOND_SWORD,1);
                final int lootingLevel = this.getLootingLevel();
                if(lootingLevel > 0) {
                    tempSword.enchant(Enchantment.byId(21), lootingLevel);
                }
                return tempSword;
            }
            default: {
                return super.getItemBySlot(slot);
            }
        }
    } // getItemBySlot ()


    // -- Custom Methods --
    private void handleAutoHeal () {
        if(this.level.isClientSide && !ModMetrics.AutoHeal) return;
        if(this.getHealth() == this.getHpValue()) return;

        if(autoHealCounter >= ModMetrics.AutoHealInterval) {
            final float healValue = this.getHpValue() / 16.0f;
            this.heal(healValue);
            autoHealCounter = 0;
        }
        autoHealCounter++;
    } // handleAutoHeal ()

    private boolean canLevelUp() {
        return this.getCurrentLevel() < getMaxLevel();
    } // canLevelUp ()

    private boolean canLevelUpFireProtection() {
        return this.getFireProtection() < ModMetrics.FireProtectionLimit;
    } // canLevelUpFireProtection ()

    private boolean canLevelUpFallProtection() {
        return this.getFallProtection() < ModMetrics.FallProtectionLimit;
    } // canLevelUpFallProtection ()

    private boolean canLevelUpBlastProtection() {
        return this.getBlastProtection() < ModMetrics.BlastProtectionLimit;
    } // canLevelUpBlastProtection ()

    private boolean canLevelUpProjectileProtection() {
        return this.getProjectileProtection() < ModMetrics.ProjectileProtectionLimit;
    } // canLevelUpProjectileProtection ()

    private int getNextExp() {
        return ModMetrics.BaseExp + this.getCurrentLevel() * ModMetrics.UpExpValue;
    } // getNextExp ()

    private void addExp (int value) {
        int addExp = value;
        if(this.hasCustomName()) addExp = addExp * 3 / 2;

        int exp = this.getExp();
        exp += addExp;

        var oldLevel = getCurrentLevel();
        while (exp >= this.getNextExp()) {
            exp -= this.getNextExp();
            this.setLevel(this.getCurrentLevel() + 1);
        }

        this.setExp(exp);
        if(oldLevel != getCurrentLevel()) {
            if(!level.isClientSide) {
                try {
                    final LivingEntity entity = this.getOwner();
                    if (entity == null) return;
                    this.displayMessage((Player)entity);
                } catch (Exception ignored) {}
            }
        }
    } // addExp ()


    // -- Interact Methods --
    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        var itemStack = player.getItemInHand(hand);

        if(hand == InteractionHand.MAIN_HAND) {
            handleSit(itemStack);
            if (this.level.isClientSide) {
                return InteractionResult.PASS;
            } else {
                handleState(itemStack, player);
                handleAutoAttack(itemStack, player);
                handleTexture(itemStack);
                if(getOwner() == null) handleTame(player);

                if(itemStack.is(Items.STICK)) displayMessage(player);
                if(itemStack.is(Items.BOOK)) displayProtectionMessage(player);

                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, hand);
    } // interactMob ()

    public void handleTame(Player player) {
        this.setOwnerUUID(player.getUUID());
        this.setTame(true);
        this.setOrderedToSit(true);
        player.displayClientMessage(Component.literal("Owner: " + getOwner().getName().getString()), true);
    } // handleTame ()

    public void handleTexture(ItemStack item) {
        if(item.is(Items.WHITE_DYE)) setTexture(RobotTexture.WHITE);
        if(item.is(Items.ORANGE_DYE)) setTexture(RobotTexture.ORANGE);
        if(item.is(Items.MAGENTA_DYE)) setTexture(RobotTexture.MAGENTA);
        if(item.is(Items.LIGHT_BLUE_DYE)) setTexture(RobotTexture.LIGHT_BLUE);
        if(item.is(Items.YELLOW_DYE)) setTexture(RobotTexture.YELLOW);
        if(item.is(Items.LIME_DYE)) setTexture(RobotTexture.LIME);
        if(item.is(Items.PINK_DYE)) setTexture(RobotTexture.PINK);
        if(item.is(Items.GRAY_DYE)) setTexture(RobotTexture.GRAY);
        if(item.is(Items.LIGHT_GRAY_DYE)) setTexture(RobotTexture.LIGHT_GRAY);
        if(item.is(Items.CYAN_DYE)) setTexture(RobotTexture.CYAN);
        if(item.is(Items.PURPLE_DYE)) setTexture(RobotTexture.PURPLE);
        if(item.is(Items.BLUE_DYE)) setTexture(RobotTexture.BLUE);
        if(item.is(Items.BROWN_DYE)) setTexture(RobotTexture.BROWN);
        if(item.is(Items.GREEN_DYE)) setTexture(RobotTexture.GREEN);
        if(item.is(Items.RED_DYE)) setTexture(RobotTexture.RED);
        if(item.is(Items.BLACK_DYE)) setTexture(RobotTexture.BLACK);
    } // handleTexture ()

    public void handleSit(ItemStack itemStack) {
        if(!canInteract(itemStack)) return;
        setOrderedToSit(invertBoolean(isOrderedToSit()));
    } // handleSit ()

    public void handleAutoAttack(ItemStack itemStack, Player player){
        if (!canInteractAutoAttack(itemStack)) return;
        setAutoAttack(invertBoolean(getAutoAttack()));
        player.displayClientMessage(Component.literal("Auto Attack: " + this.getAutoAttack()), true);
    } // handleAutoAttack ()

    public void handleState(ItemStack itemStack, Player player) {
        var previousState = getCurrentState();
        StandbyState(itemStack);
        FollowState(itemStack);
        BaseDefenseState(itemStack);
        if(previousState != getCurrentState()) player.displayClientMessage(Component.literal("State: " + getCurrentState().name()), true);
    } // handleState

    public void StandbyState(ItemStack itemStack){
        if(!canInteract(itemStack)) return;
        if(isOrderedToSit()) setCurrentState(RobotState.Standby);
    } // StandbyState ()

    public void FollowState(ItemStack itemStack){
        if(!canInteract(itemStack)) return;
        if(!isOrderedToSit()) setCurrentState(RobotState.Follow);
    } // FollowState ()

    public void BaseDefenseState(ItemStack itemStack){
        if(!canInteractGuardMode(itemStack)) return;
        setOrderedToSit(false);
        setAutoAttack(true);

        var currentPosition = this.position();
        this.setBaseX((float)currentPosition.x);
        this.setBaseY((float)currentPosition.y);
        this.setBaseZ((float)currentPosition.z);
        setCurrentState(RobotState.BaseDefense);
    } // BaseDefenseState ()

    public void displayMessage (Player player) {
        player.displayClientMessage(Component.literal("|--------------------------"), false);
        player.displayClientMessage(Component.literal("MaxLevel: " + this.getMaxLevel()), false);
        player.displayClientMessage(Component.literal("Model: " + this.getVariant()), false);
        player.displayClientMessage(Component.literal("Health: " + this.getHealth() + "/" + this.getMaxHealth()), false);
        player.displayClientMessage(Component.literal("Attack: " + this.getAttackValue()), false);
        player.displayClientMessage(Component.literal("Auto Attack: " + this.getAutoAttack()), false);
        player.displayClientMessage(Component.literal("Level: " + this.getCurrentLevel()), false);
        player.displayClientMessage(Component.literal("Exp: " + this.getExp()), false);
        player.displayClientMessage(Component.literal("Looting: " + this.getLootingLevel()), false);
    } // displayMessage ()

    public void displayProtectionMessage (Player player) {
        player.displayClientMessage(Component.literal("|--------------------------"), false);
        player.displayClientMessage(Component.literal("Fire Protection: " + this.getFireProtection() + "/" + ModMetrics.FireProtectionLimit), false);
        player.displayClientMessage(Component.literal("Fall Protection: " + this.getFallProtection() + "/" + ModMetrics.FallProtectionLimit), false);
        player.displayClientMessage(Component.literal("Blast Protection: " + this.getBlastProtection() + "/" + ModMetrics.BlastProtectionLimit), false);
        player.displayClientMessage(Component.literal("Projectile Protection: " + this.getProjectileProtection() + "/" + ModMetrics.ProjectileProtectionLimit), false);
    } // displayProtectionMessage ()


    // -- Save Methods --
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, RobotVariant.Honey.getName());
        this.entityData.define(TEXTURE_ID, 0);

        this.entityData.define(STATE, 0);
        this.entityData.define(AUTO_ATTACK, false);

        this.entityData.define(MAX_LEVEL, ModMetrics.MaxLevel);
        this.entityData.define(LEVEL, 0);
        this.entityData.define(EXP, 0);

        this.entityData.define(FIRE_PROTECTION, 0);
        this.entityData.define(FALL_PROTECTION, 0);
        this.entityData.define(BLAST_PROTECTION, 0);
        this.entityData.define(PROJECTILE_PROTECTION, 0);

        this.entityData.define(BASE_X, 0F);
        this.entityData.define(BASE_Y, 0F);
        this.entityData.define(BASE_Z, 0F);
    } // defineSynchedData ()

    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putString("Variant", this.getVariant());
        nbt.putInt("TextureID", this.getTextureID());

        nbt.putInt("State", this.getCurrentStateID());
        nbt.putBoolean("AutoAttack", this.getAutoAttack());

        nbt.putInt("MaxLevel", this.getMaxLevel());
        nbt.putInt("Level", this.getCurrentLevel());
        nbt.putInt("Exp", this.getExp());

        nbt.putInt("FireProtection", this.getFireProtection());
        nbt.putInt("FallProtection", this.getFallProtection());
        nbt.putInt("BlastProtection", this.getBlastProtection());
        nbt.putInt("ProjectileProtection", this.getProjectileProtection());

        nbt.putFloat("BaseX", this.getBaseX());
        nbt.putFloat("BaseY", this.getBaseY());
        nbt.putFloat("BaseZ", this.getBaseZ());
    } // addAdditionalSaveData ()

    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.setVariant(nbt.getString("Variant"));
        this.setTexture(nbt.getInt("TextureID"));

        this.setCurrentState(nbt.getInt("State"));
        this.setAutoAttack(nbt.getBoolean("AutoAttack"));

        this.setMaxLevel(nbt.getInt("MaxLevel"));
        this.setLevel(nbt.getInt("Level"));
        this.setExp(nbt.getInt("Exp"));

        this.setFireProtection(nbt.getInt("FireProtection"));
        this.setFallProtection(nbt.getInt("FallProtection"));
        this.setBlastProtection(nbt.getInt("BlastProtection"));
        this.setProjectileProtection(nbt.getInt("ProjectileProtection"));

        this.setBaseY(nbt.getFloat("BaseY"));
        this.setBaseZ(nbt.getFloat("BaseZ"));
        this.setBaseX(nbt.getFloat("BaseX"));
    } // readAdditionalSaveData ()


    // -- Sound Methods --
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundEvents.GENERIC_HURT;
    } // getHurtSound ()

    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    } // getDeathSound ()


    // -- Inherited --
    @Override
    public int getRemainingPersistentAngerTime() {
        return 0;
    } // getRemainingPersistentAngerTime ()

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

} // Class HoneyEntity