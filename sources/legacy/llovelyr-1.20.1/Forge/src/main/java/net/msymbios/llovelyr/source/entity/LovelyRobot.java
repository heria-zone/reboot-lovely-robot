package net.msymbios.llovelyr.source.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.msymbios.llovelyr.common.entity.goal.*;
import net.msymbios.llovelyr.common.entity.internal.*;
import net.msymbios.llovelyr.common.util.internal.Utility;
import net.msymbios.llovelyr.common.util.internal.Version;
import net.msymbios.llovelyr.source.configs.LovelyConfigs;
import net.msymbios.llovelyr.source.configs.LovelyIdentifier;
import net.msymbios.llovelyr.source.entity.internal.enums.*;
import net.msymbios.llovelyr.source.items.LovelyItems;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

import javax.annotation.Nonnull;

import static net.msymbios.llovelyr.common.util.internal.Utility.invertBoolean;

public abstract class LovelyRobot extends InternalEntity implements GeoEntity {

    // -- Variables --

    protected static final EntityDataAccessor<Boolean> AUTO_ATTACK = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.BOOLEAN);

    protected static final EntityDataAccessor<Integer> MAX_LEVEL = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> LEVEL = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> EXP = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);

    protected static final EntityDataAccessor<Integer> FIRE_PROTECTION = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> FALL_PROTECTION = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> BLAST_PROTECTION = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> PROJECTILE_PROTECTION = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);

    protected static final EntityDataAccessor<Float> BASE_X = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> BASE_Y = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> BASE_Z = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.FLOAT);

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    // -- Properties --

    // AUTO ATTACK

    public boolean getAutoAttack() {
        boolean value = false;
        try {value = this.entityData.get(AUTO_ATTACK);}
        catch (Exception ignored) {}
        return value;
    } // getAutoAttack ()

    public void setAutoAttack(boolean value) {
        this.entityData.set(AUTO_ATTACK, value);
    } // setAutoAttack ()

    // STATS

    public int getMaxLevel() { return nativeEntity.getMaxLevel(); } // getMaxLevel ()

    public int getHp() { return InternalLogic.calculateHp(this.getCurrentLevel(), (int)this.nativeEntity.getMaxHealth()); } // getHp ()

    public int getAttackDamage() { return InternalLogic.calculateAttack(this.getCurrentLevel(), (int)nativeEntity.getAttackDamage()); } // getAttackDamage ()

    public int getArmorLevel() {
        var defence = InternalLogic.calculateDefense(this.getCurrentLevel(), (int)nativeEntity.getArmour());
        return (int) InternalLogic.calculateArmor(defence);
    } // getArmorLevel ()

    public int getArmorToughnessLevel() { return (int) InternalLogic.calculateArmorToughness(getArmorLevel()); } // getArmorToughnessLevel ()

    public int getLooting() {return InternalLogic.calculateLooting(this.getCurrentLevel());} // getLooting ()

    public int getCurrentLevel() {
        var level = 0;
        try {level = this.entityData.get(LEVEL);}
        catch (Exception ignored){}
        return level;
    } // getCurrentLevel ()

    public void setCurrentLevel(int value){
        this.entityData.set(LEVEL, value);
        InternalLogic.handleLevel(this, getHp(), getAttackDamage(), getArmorLevel(), getArmorToughnessLevel());
    } // setCurrentLevel ()

    public int getExp(){
        int value = 1;
        try {value = this.entityData.get(EXP);}
        catch (Exception ignored){}
        return value;
    } // getExp ()

    public void setExp(int value){
        this.entityData.set(EXP, value);
    } // setExp ()

    // PROTECTION

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

    // BASE

    public float getBaseX() {
        float value = this.getBlockX();
        try {value = this.entityData.get(BASE_X);}
        catch (Exception ignored) {}
        return value;
    } // getBaseX ()

    public void setBaseX(float value) {
        this.entityData.set(BASE_X, value);
    } // setBaseX ()

    public float getBaseY() {
        float value = this.getBlockY();
        try {value = this.entityData.get(BASE_Y);}
        catch (Exception ignored) {}
        return value;
    } // getBaseY ()

    public void setBaseY(float value) {
        this.entityData.set(BASE_Y, value);
    } // setBaseY ()

    public float getBaseZ() {
        float value = this.getBlockZ();
        try {value = this.entityData.get(BASE_Z);}
        catch (Exception ignored) {}
        return value;
    } // getBaseZ ()

    public void setBaseZ(float value) {
        this.entityData.set(BASE_Z, value);
    } // setBaseZ ()

    // -- Constructor --

    public LovelyRobot(EntityType<? extends InternalEntity> entityType, Level level) {
        super(entityType, level);
        rotate(Rotation.getRandom(this.getRandom()));
    } // Constructor LovelyRobot ()

    // -- Inherited Methods --

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegister) {
        controllerRegister.add(InternalAnimation.locomotionAnimation(this));
        controllerRegister.add(InternalAnimation.attackAnimation(this));
    } // registerControllers ()

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; } // getAnimatableInstanceCache ()

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, LovelyConfigs.MovementMeleeAttack, true));
        this.goalSelector.addGoal(4, new AiFollowOwnerGoal(this, LovelyConfigs.MovementFollowOwner, LovelyConfigs.FollowDistanceMax, LovelyConfigs.FollowDistanceMin, false));
        this.goalSelector.addGoal(4, new AiBaseDefenseGoal(this, LovelyConfigs.MovementFollowOwner, LovelyConfigs.BaseDefenceRange, LovelyConfigs.BaseDefenceWarpRange));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, LovelyConfigs.MovementWanderAround));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, LovelyConfigs.LookRange));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, LivingEntity.class, LovelyConfigs.LookRange));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new AiAutoAttackGoal<>(this, Mob.class, LovelyConfigs.AttackChance, true, false, InternalEntityType.AvoidAttackingEntities));
    } // registerGoals ()

    @Override
    public void tick() {
        super.tick();
        handleCombatMode();
        handleAutoHeal();
        displayExtra();
    } // tick ()

    @Override
    public void onEnterCombat() {
        handleActivateCombatMode();
        super.onEnterCombat();
    } // onEnterCombat ()

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        switch (slot.getType()){
            case HAND: {
                final ItemStack tempSword = new ItemStack(Items.DIAMOND_SWORD,1);
                final int lootingLevel = this.getLooting();
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

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LEVEL, 0);
        this.entityData.define(EXP, 0);
        this.entityData.define(AUTO_ATTACK, true);

        this.entityData.define(FIRE_PROTECTION, 0);
        this.entityData.define(FALL_PROTECTION, 0);
        this.entityData.define(BLAST_PROTECTION, 0);
        this.entityData.define(PROJECTILE_PROTECTION, 0);

        this.entityData.define(BASE_X, 0F);
        this.entityData.define(BASE_Y, 0F);
        this.entityData.define(BASE_Z, 0F);
    } // defineSynchedData ()

    @Override
    public void addAdditionalSaveData(CompoundTag dataNBT) {
        dataNBT.putInt("Level", this.getCurrentLevel());
        dataNBT.putInt("Exp", this.getExp());
        dataNBT.putBoolean("AutoAttack", this.getAutoAttack());

        dataNBT.putInt("FireProtection", this.getFireProtection());
        dataNBT.putInt("FallProtection", this.getFallProtection());
        dataNBT.putInt("BlastProtection", this.getBlastProtection());
        dataNBT.putInt("ProjectileProtection", this.getProjectileProtection());

        dataNBT.putFloat("BaseX", this.getBaseX());
        dataNBT.putFloat("BaseY", this.getBaseY());
        dataNBT.putFloat("BaseZ", this.getBaseZ());

        super.addAdditionalSaveData(dataNBT);
    } // writeCustomDataToNbt ()

    @Override
    public void readAdditionalSaveData(CompoundTag dataNBT) {
        this.setCurrentLevel(dataNBT.getInt("Level"));
        this.setExp(dataNBT.getInt("Exp"));
        this.setAutoAttack(dataNBT.getBoolean("AutoAttack"));

        this.setFireProtection(dataNBT.getInt("FireProtection"));
        this.setFallProtection(dataNBT.getInt("FallProtection"));
        this.setBlastProtection(dataNBT.getInt("BlastProtection"));
        this.setProjectileProtection(dataNBT.getInt("ProjectileProtection"));

        this.setBaseY(dataNBT.getFloat("BaseY"));
        this.setBaseZ(dataNBT.getFloat("BaseZ"));
        this.setBaseX(dataNBT.getFloat("BaseX"));
        super.readAdditionalSaveData(dataNBT);
    } // readCustomDataFromNbt ()

    @Override
    public CompoundTag writeToNBT(@Nonnull CompoundTag dataNBT) {
        dataNBT = super.writeToNBT(dataNBT);
        dataNBT.putInt("Level", this.getCurrentLevel());
        dataNBT.putInt("Exp", this.getExp());
        dataNBT.putBoolean("AutoAttack", this.getAutoAttack());

        dataNBT.putInt("FireProtection", this.getFireProtection());
        dataNBT.putInt("FallProtection", this.getFallProtection());
        dataNBT.putInt("BlastProtection", this.getBlastProtection());
        dataNBT.putInt("ProjectileProtection", this.getProjectileProtection());

        dataNBT.putFloat("BaseX", this.getBaseX());
        dataNBT.putFloat("BaseY", this.getBaseY());
        dataNBT.putFloat("BaseZ", this.getBaseZ());
        return dataNBT;
    } // writeToNBT

    @Override
    public void readFromNBT(@Nonnull CompoundTag dataNBT, @Nonnull Version version) {
        super.readFromNBT(dataNBT, version);
        this.setCurrentLevel(dataNBT.getInt("Level"));
        this.setExp(dataNBT.getInt("Exp"));
        this.setAutoAttack(dataNBT.getBoolean("AutoAttack"));

        this.setFireProtection(dataNBT.getInt("FireProtection"));
        this.setFallProtection(dataNBT.getInt("FallProtection"));
        this.setBlastProtection(dataNBT.getInt("BlastProtection"));
        this.setProjectileProtection(dataNBT.getInt("ProjectileProtection"));

        this.setBaseY(dataNBT.getFloat("BaseY"));
        this.setBaseZ(dataNBT.getFloat("BaseZ"));
        this.setBaseX(dataNBT.getFloat("BaseX"));
    } // readFromNBT ()

    // HANDLERS

    @Override
    protected void handleAttackTarget(@NotNull Entity target) {
        handleActivateCombatMode();
        if(InternalLogic.handleLevelUp(this.getCurrentLevel(), this.getMaxLevel()) && !(target instanceof Player) && !this.level().isClientSide) {
            final int maxHp = (int)((LivingEntity)target).getMaxHealth();
            addExp(maxHp / 4);
        }
        this.level().broadcastEntityEvent(this, (byte)4);
    } // handleAttackTarget ()

    @Override
    protected boolean handleDamage (@NotNull DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) return false;
        if ((source.getEntity() instanceof Player player)) {
            if (this.isOwnedBy(player) && !LovelyConfigs.FriendlyFire)
                return false;
        }
        handleActivateCombatMode();

        if ((source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.LAVA)) && amount >= 1.0f && this.getFireProtection() > 0)
            amount *= (100.0f - this.getFireProtection()) / 100.0f;

        if (source.is(DamageTypes.FALL) && amount >= 1.0f && this.getFallProtection() > 0)
            amount *= (100.0f - this.getFallProtection()) / 100.0f;

        if (source.is(DamageTypes.EXPLOSION) && amount >= 1.0f && this.getBlastProtection() > 0)
            amount *= (100.0f - this.getBlastProtection()) / 100.0f;

        if (source.is(DamageTypes.ARROW) && amount >= 1.0f && this.getProjectileProtection() > 0)
            amount *= (100.0f - this.getProjectileProtection()) / 100.0f;

        if (amount < 1.0f) return false;

        if(!level().isClientSide) {
            if((source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.LAVA)) && InternalLogic.handleFireProtectionLevelUp(getFireProtection())) this.setFireProtection(this.getFireProtection() + 1);
            if(source.is(DamageTypes.FALL) && InternalLogic.handleFallProtectionLevelUp(this.getFallProtection())) this.setFallProtection(this.getFallProtection() + 1);
            if(source.is(DamageTypes.EXPLOSION) && InternalLogic.handleBlastProtectionLevelUp(this.getBlastProtection())) this.setBlastProtection(this.getBlastProtection() + 1);
            if(source.is(DamageTypes.ARROW) && InternalLogic.handleProjectileProtectionLevelUp(this.getProjectileProtection())) this.setProjectileProtection(this.getProjectileProtection() + 1);
        }

        final Entity entity = source.getEntity();

        if (InternalLogic.handleLevelUp(this.getCurrentLevel(), this.getMaxLevel()) && !(entity instanceof Player) && entity instanceof LivingEntity && !this.level().isClientSide) {
            final int maxHp = (int)((LivingEntity)entity).getMaxHealth();
            addExp(maxHp / 6);
        }

        return true;
    } // handleDamage ()

    @Override
    protected void handleItemDrop() {
        final ItemStack dropItem = setDropItem();
        CompoundTag nbt = dropItem.getTag();
        if(nbt == null) nbt = new CompoundTag();

        String customName = Utility.getEntityCustomName(this);
        if (!customName.isEmpty()) nbt.putString(LovelyIdentifier.STAT_CUSTOM_NAME, customName);

        String ownerName = Utility.getEntityOwnerName(this);
        if (!ownerName.isEmpty()) nbt.putString(LovelyIdentifier.STAT_OWNER, ownerName);

        nbt.putString(LovelyIdentifier.STAT_TYPE, this.nativeEntity.key);
        nbt.putInt(LovelyIdentifier.STAT_COLOR, this.getTextureID());

        nbt.putInt(LovelyIdentifier.STAT_MAX_LEVEL, this.getMaxLevel());
        nbt.putInt(LovelyIdentifier.STAT_LEVEL, this.getCurrentLevel());
        nbt.putInt(LovelyIdentifier.STAT_EXP, this.getExp());

        nbt.putInt(LovelyIdentifier.STAT_FIRE_PROTECTION, this.getFireProtection());
        nbt.putInt(LovelyIdentifier.STAT_FALL_PROTECTION, this.getFallProtection());
        nbt.putInt(LovelyIdentifier.STAT_BLAST_PROTECTION, this.getBlastProtection());
        nbt.putInt(LovelyIdentifier.STAT_PROJECTILE_PROTECTION, this.getProjectileProtection());

        dropItem.setTag(nbt);

        if (!customName.isEmpty()) dropItem.setHoverName(Component.nullToEmpty(customName).copy().append(Utility.getRandomTitle()).withStyle(ChatFormatting.DARK_PURPLE));

        ItemEntity itemEntity = new ItemEntity(this.level(), this.getX(), this.getY() + (double)0.0F, this.getZ(), dropItem);
        itemEntity.setDefaultPickUpDelay();
        this.level().addFreshEntity(itemEntity);
    } // handleDropItems ()

    @Override
    protected InteractionResult handleItemInteraction (ItemStack stack, Player player) {
        if(handleTexture(stack, player)) return InteractionResult.SUCCESS;
        return InteractionResult.PASS;
    } // handleItemInteraction ()

    @Override
    protected boolean canInteractWithItems(ItemStack stack) {
        if(stack.getItem() instanceof DyeItem) return false;
        if(stack.getItem() instanceof SwordItem) return false;
        if(stack.is(Items.STICK) || stack.is(Items.BOOK) || stack.is(Items.WRITABLE_BOOK) || stack.is(Items.OAK_BUTTON)) return false;
        return !stack.is(Items.COMPASS) && !stack.is(Items.RECOVERY_COMPASS);
    } // canInteractWithItems ()

    @Override
    protected void handleInteract (ItemStack stack, Player player) {
        super.handleInteract(stack, player);
        handleAutoAttack(stack);
        handleDisplayInteraction(stack);
    } // handleInteract

    @Override
    protected void handleState(ItemStack stack) {
        if (handleStandbyState(stack)) return;
        if (handleFollowState(stack)) return;
        if (handleBaseDefenseState(stack)) return;
    } // handleState

    @Override
    protected boolean handleTexture(ItemStack stack, Player player) {
        var oldTexture = getTextureID();
        if(stack.is(Items.WHITE_DYE)) setTexture(EntityTexture.WHITE);
        if(stack.is(Items.ORANGE_DYE)) setTexture(EntityTexture.ORANGE);
        if(stack.is(Items.MAGENTA_DYE)) setTexture(EntityTexture.MAGENTA);
        if(stack.is(Items.LIGHT_BLUE_DYE)) setTexture(EntityTexture.LIGHT_BLUE);
        if(stack.is(Items.YELLOW_DYE)) setTexture(EntityTexture.YELLOW);
        if(stack.is(Items.LIME_DYE)) setTexture(EntityTexture.LIME);
        if(stack.is(Items.PINK_DYE)) setTexture(EntityTexture.PINK);
        if(stack.is(Items.GRAY_DYE)) setTexture(EntityTexture.GRAY);
        if(stack.is(Items.LIGHT_GRAY_DYE)) setTexture(EntityTexture.LIGHT_GRAY);
        if(stack.is(Items.CYAN_DYE)) setTexture(EntityTexture.CYAN);
        if(stack.is(Items.PURPLE_DYE)) setTexture(EntityTexture.PURPLE);
        if(stack.is(Items.BLUE_DYE)) setTexture(EntityTexture.BLUE);
        if(stack.is(Items.BROWN_DYE)) setTexture(EntityTexture.BROWN);
        if(stack.is(Items.GREEN_DYE)) setTexture(EntityTexture.GREEN);
        if(stack.is(Items.RED_DYE)) setTexture(EntityTexture.RED);
        if(stack.is(Items.BLACK_DYE)) setTexture(EntityTexture.BLACK);

        if(oldTexture != getTextureID()) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    } // handleTexture ()

    // -- Custom Methods --

    public void addExp (int value) {
        int addExp = value;
        int exp = getExp();
        String customName = "";
        try {customName = getCustomName().getString();}
        catch (Exception ignored) {}

        // if they have a name they earn more exp
        if(!customName.isEmpty()) addExp = addExp * 3 / 2;
        exp += addExp;

        var oldLevel = this.getCurrentLevel();
        while (exp >= InternalLogic.calculateNextExp(this.getCurrentLevel())) {
            exp -= InternalLogic.calculateNextExp(this.getCurrentLevel());
            setCurrentLevel(this.getCurrentLevel() + 1);
        }

        setExp(exp);
        if(oldLevel != this.getCurrentLevel()) {
            if(!level().isClientSide) {
                try {
                    final LivingEntity owner = getOwner();
                    if (owner == null) return;
                    displayGeneralMessage(getNotification(), true);
                } catch (Exception ignored) {}
            }
        }
    } // addExp ()


    private boolean canInteractAutoAttack(ItemStack stack) {
        return stack.getItem() instanceof SwordItem;
    } // canInteractAutoAttack ()

    private boolean canInteractGuardMode(ItemStack stack) {
        return stack.is(Items.COMPASS) || stack.is(Items.RECOVERY_COMPASS);
    } // canInteractGuardMode ()


    protected boolean handleDisplayInteraction (ItemStack stack) {
        if(stack.getItem() == (Items.OAK_BUTTON)) {
            this.setNotification(invertBoolean(getNotification()));
            if(getNotification()) displayNotification(LovelyIdentifier.MSG_NOTIFICATION, LovelyIdentifier.MSG_ON, getNotification());
            else displayNotification(LovelyIdentifier.MSG_NOTIFICATION, LovelyIdentifier.MSG_OFF, true);
        }

        if(stack.getItem() == (Items.BOOK)) displayGeneralMessage(true, false);
        if(stack.getItem() == (Items.WRITABLE_BOOK)) displayEnchantmentMessage();
        return true;
    } // handleDisplayInteraction ()

    protected void handleAutoAttack(ItemStack stack){
        if (!canInteractAutoAttack(stack)) return;
        if (getCurrentState() == EntityState.Defense) return;
        setAutoAttack(invertBoolean(getAutoAttack()));

        if(getAutoAttack()) displayNotification(LovelyIdentifier.MSG_AUTO_ATTACK, LovelyIdentifier.MSG_ON, getNotification());
        else displayNotification(LovelyIdentifier.MSG_AUTO_ATTACK, LovelyIdentifier.MSG_OFF, getNotification());
    } // handleAutoAttack ()

    protected boolean handleBaseDefenseState(ItemStack stack){
        if(!canInteractGuardMode(stack) || getCurrentState() == EntityState.Defense) return false;
        setCurrentState(EntityState.Defense);
        setOrderedToSit(false);
        setAutoAttack(true);
        this.setBaseX((float)this.getBlockX());
        this.setBaseY((float)this.getBlockY());
        this.setBaseZ((float)this.getBlockZ());
        displayNotification(LovelyIdentifier.MSG_BASE_DEFENCE, getNotification());
        return true;
    } // handleBaseDefenseState ()

    public void displayGeneralMessage(boolean canShow, boolean showLevelUp) {
        if(!canShow) return;
        InternalLogic.displayInfo(this, (LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_BAR)), false);
        if(showLevelUp) InternalLogic.displayInfo(this, (LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_LEVEL_UP)), false);
        if(this.getCustomName() != null) InternalLogic.displayInfo(this, LovelyIdentifier.getVariantTranslation(nativeEntity.key).append(": " + this.getCustomName().getString()), false);
        else InternalLogic.displayInfo(this, LovelyIdentifier.getVariantTranslation(nativeEntity.key), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_LEVEL).append(": " + this.getCurrentLevel()             + "/" + this.getMaxLevel()), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_EXPERIENCE).append(": " + this.getExp()                 + "/" + InternalLogic.calculateNextExp(this.getExp())), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_HEALTH).append(": " + (int)Math.floor(this.getHealth()) + "/" + (int)this.getMaxHealth()), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_ATTACK).append(": " + this.getAttackDamage()), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_DEFENCE).append(": " + this.getArmorLevel()), false);
    } // displayGeneralMessage ()

    public void displayEnchantmentMessage() {
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_BAR), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_ENCHANTMENT), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_LOOTING).append(": " + this.getLooting()                            + "/" + LovelyConfigs.MaxLootEnchantment), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_FIRE_PROTECTION).append(": " + this.getFireProtection()             + "/" + LovelyConfigs.ProtectionLimitFire), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_FALL_PROTECTION).append(": " + this.getFallProtection()             + "/" + LovelyConfigs.ProtectionLimitFall), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_BLAST_PROTECTION).append(": " + this.getBlastProtection()           + "/" + LovelyConfigs.ProtectionLimitBlast), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_PROJECTILE_PROTECTION).append(": " + this.getProjectileProtection() + "/" + LovelyConfigs.ProtectionLimitProjectile), false);
    } // displayEnchantmentMessage ()

} // Class LovelyRobot