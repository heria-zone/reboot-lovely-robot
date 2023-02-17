package com.msymbios.rlovelyr.entity.custom;

import com.msymbios.rlovelyr.entity.enums.*;
import com.msymbios.rlovelyr.LovelyRobotMod;
import com.msymbios.rlovelyr.entity.utils.ModMetrics;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static com.msymbios.rlovelyr.entity.utils.ModUtils.*;

public class Bunny2Entity extends TamableAnimal implements NeutralMob, GeoEntity {

    // -- Variables --
    private static final HashMap<RobotTexture, ResourceLocation> TEXTURES = new HashMap<>(){{
        put(RobotTexture.ORANGE, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_01.png")); // Orange
        put(RobotTexture.MAGENTA, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_02.png")); // Magenta
        put(RobotTexture.YELLOW, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_04.png")); // Yellow
        put(RobotTexture.LIME, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_05.png")); // Lime
        put(RobotTexture.PINK, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_06.png")); // Pink
        put(RobotTexture.LIGHT_BLUE, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_08.png")); // Light Blue
        put(RobotTexture.PURPLE, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_10.png")); // Purple
        put(RobotTexture.BLUE, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_11.png")); // Blue
        put(RobotTexture.RED, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_14.png")); // Red
        put(RobotTexture.BLACK, new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/bunny2/bunny2_15.png")); // Black
    }};
    private static final HashMap<RobotModel, ResourceLocation> MODELS = new HashMap<>(){{
        put(RobotModel.Unarmed, new ResourceLocation(LovelyRobotMod.MODID, "geo/bunny2.geo.json"));
        put(RobotModel.Armed, new ResourceLocation(LovelyRobotMod.MODID, "geo/bunny2.attack.geo.json"));
    }};
    private static final HashMap<RobotAnimation, ResourceLocation> ANIMATIONS = new HashMap<>(){{
        put(RobotAnimation.Locomotion, new ResourceLocation(LovelyRobotMod.MODID, "animations/lovelyrobot.animation.json"));
    }};

    private static final EntityDataAccessor<String> VARIANT = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> TEXTURE_ID = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> AUTO_ATTACK = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> MAX_LEVEL = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LEVEL = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> EXP = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> FIRE_PROTECTION = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FALL_PROTECTION = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BLAST_PROTECTION = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PROJECTILE_PROTECTION = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Float> BASE_X = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> BASE_Y = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> BASE_Z = SynchedEntityData.defineId(Bunny2Entity.class, EntityDataSerializers.FLOAT);

    private static ResourceLocation currentModel;
    private static ResourceLocation currentAnimator;

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);


    // -- Properties --
    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, ModMetrics.Bunny2BaseHp)
                .add(Attributes.ATTACK_DAMAGE, ModMetrics.Bunny2BaseAttack)
                .add(Attributes.ATTACK_SPEED, ModMetrics.AttackMoveSpeed)
                .add(Attributes.MOVEMENT_SPEED, ModMetrics.Bunny2MovementSpeed)
                .add(Attributes.ARMOR, 0F)
                .add(Attributes.ARMOR_TOUGHNESS, 0F).build();
    } // setAttributes ()


    // -- MODEL --
    public ResourceLocation getCurrentModel() {
        return currentModel;
    } // getCurrentModel ()

    public void setCurrentModel(String value) {
        currentModel = MODELS.get(RobotModel.byName(value));
    } // setCurrentModel ()

    public void setCurrentModel(RobotModel value) {
        currentModel = MODELS.get(value);
    } // setCurrentModel ()


    // -- ANIMATION --
    public ResourceLocation getCurrentAnimator() {
        return currentAnimator;
    } // getCurrentAnimator ()

    public void setCurrentAnimator(String value) {
        currentAnimator = ANIMATIONS.get(RobotAnimation.byName(value));
    } // setCurrentAnimator ()

    public void setCurrentAnimator(RobotAnimation value) {
        currentAnimator = ANIMATIONS.get(value);
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

    public ResourceLocation getTextureByID(int key) {
        return TEXTURES.containsKey(RobotTexture.byId(key)) ? TEXTURES.get(RobotTexture.byId(key)) : getTexture();
    } // getTextureByID ()

    public void setTexture(ItemStack itemStack) {
        if(itemStack.is(Items.ORANGE_DYE)) setTexture(RobotTexture.ORANGE);
        if(itemStack.is(Items.MAGENTA_DYE)) setTexture(RobotTexture.MAGENTA);
        if(itemStack.is(Items.YELLOW_DYE)) setTexture(RobotTexture.YELLOW);
        if(itemStack.is(Items.LIME_DYE)) setTexture(RobotTexture.LIME);
        if(itemStack.is(Items.PINK_DYE)) setTexture(RobotTexture.PINK);
        if(itemStack.is(Items.LIGHT_BLUE_DYE)) setTexture(RobotTexture.LIGHT_BLUE);
        if(itemStack.is(Items.PURPLE_DYE)) setTexture(RobotTexture.PURPLE);
        if(itemStack.is(Items.BLUE_DYE)) setTexture(RobotTexture.BLUE);
        if(itemStack.is(Items.RED_DYE)) setTexture(RobotTexture.RED);
        if(itemStack.is(Items.BLACK_DYE)) setTexture(RobotTexture.BLACK);
    } // setTexture ()

    public void setTexture(int variant) {
        this.entityData.set(TEXTURE_ID, variant);
    } // setTexture ()

    public void setTexture(RobotTexture variant) {
        setTexture(variant.getId());
    } // setTexture ()


    // -- VARIANT --
    public String getVariant() {
        String value = RobotVariant.Bunny2.getName();
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

    public int getBaseLevel(){
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
        return (int)(ModMetrics.Bunny2BaseHp + this.getBaseLevel() * ModMetrics.Bunny2BaseHp / 50);
    } // getHpValue ()

    public int getAttackValue() {
        return (int)(ModMetrics.Bunny2BaseAttack + this.getBaseLevel() * ModMetrics.Bunny2BaseAttack / 50);
    } // getAttackValue ()

    public int getDefenseValue() {
        return (int)(ModMetrics.Bunny2BaseDefense + this.getBaseLevel() * ModMetrics.Bunny2BaseDefense / 50);
    } // getDefenseValue ()

    public int getLootingLevel() {
        int level = 0;
        if (ModMetrics.LootingEnchantment) {
            level = this.getBaseLevel() / ModMetrics.LootingRequiredLevel;
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
    public Bunny2Entity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        setCurrentModel(RobotModel.Unarmed);
        setCurrentAnimator(RobotAnimation.Locomotion);
    } // Constructor Bunny2Entity ()


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
        this.setVariant(RobotVariant.Bunny2.getName());
        this.setTexture(getRandomNumber(TEXTURES.size()));
        this.setMaxLevel(ModMetrics.MaxLevel);

        EquipmentSlot slot = EquipmentSlot.MAINHAND;
        ItemStack diamondSword = new ItemStack(Items.DIAMOND_SWORD);
        this.setItemSlot(slot, diamondSword);
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
        handleModelTransition();
        handleAutoHeal();
    } // tick ()

    @Nullable @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {
        return null;
    } // getBreedOffspring ()


    // -- Custom Methods --
    private void handleModelTransition () {
        if(this.swinging) {
            setCurrentModel(RobotModel.Armed);
        } else {
            setCurrentModel(RobotModel.Unarmed);
        }
    } // handleModelTransition ()

    private void handleAutoHeal () {
        if (!this.level.isClientSide && ModMetrics.AutoHeal && this.age % ModMetrics.AutoHealInterval == 0 && this.getHealth() < this.getHpValue()) {
            final float healValue = this.getHpValue() / 16.0f;
            this.heal(healValue);
        }
    } // handleAutoHeal ()


    // -- Interact Methods --
    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        var itemStack = player.getItemInHand(hand);

        if(hand == InteractionHand.MAIN_HAND) {
            setSittingState(itemStack);

            if (this.level.isClientSide) {
                return InteractionResult.PASS;
            } else {
                setAutoAttackState(itemStack, player);
                setMode(itemStack);
                setTexture(itemStack);

                if(getOwner() == null){
                    this.setOwnerUUID(player.getUUID());
                    player.displayClientMessage(Component.literal("Owner: " + getOwner().getName()), true);
                }

                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, hand);
    } // interactMob ()

    public void setSittingState(ItemStack itemStack) {
        if(!canInteract(itemStack)) return;
        setOrderedToSit(invertBoolean(isOrderedToSit()));
    } // setSittingState ()

    public void setAutoAttackState(ItemStack itemStack, Player player){
        if(!canInteractAutoAttack(itemStack)) return;
        setAutoAttack(invertBoolean(getAutoAttack()));
        player.displayClientMessage(Component.literal("Auto Attack: " + this.getAutoAttack()), true);
    } // setAutoAttackState ()

    public void setMode(ItemStack itemStack) {
        StandbyMode(itemStack);
        FollowMode(itemStack);
        GuardMode(itemStack);
    } // setMode

    public void StandbyMode(ItemStack itemStack){
        if(!canInteract(itemStack)) return;
        if(isOrderedToSit()) setCurrentState(RobotState.Standby);
    } // StandbyMode ()

    public void FollowMode(ItemStack itemStack){
        if(!canInteract(itemStack)) return;
        if(!isOrderedToSit()) setCurrentState(RobotState.Follow);
    } // FollowMode ()

    public void GuardMode(ItemStack itemStack){
        if(!canInteractGuardMode(itemStack)) return;
        setOrderedToSit(false);
        setCurrentState(RobotState.BaseDefense);
    } // GuardMode ()


    // -- Save Methods --
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TEXTURE_ID, 0);

        this.entityData.define(STATE, 0);
        this.entityData.define(AUTO_ATTACK, false);
    } // defineSynchedData ()

    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("TextureID", this.getTextureID());
        compoundTag.putInt("State", this.getCurrentStateID());
        compoundTag.putBoolean("AutoAttack", this.getAutoAttack());
    } // addAdditionalSaveData ()

    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setTexture(compoundTag.getInt("TextureID"));
        this.setCurrentState(compoundTag.getInt("State"));
        this.setAutoAttack(compoundTag.getBoolean("AutoAttack"));
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
    }

    @Override
    public void setRemainingPersistentAngerTime(int p_21673_) {}

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return null;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID p_21672_) {}

    @Override
    public void startPersistentAngerTimer() {}

} // Class Bunny2Entity