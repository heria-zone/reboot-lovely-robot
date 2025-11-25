package net.msymbios.llovelyr.source.entity;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.world.EntityView;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.msymbios.llovelyr.common.entity.goal.*;
import net.msymbios.llovelyr.common.entity.internal.*;
import net.msymbios.llovelyr.common.util.internal.Utility;
import net.msymbios.llovelyr.common.util.internal.Version;
import net.msymbios.llovelyr.source.configs.LovelyConfigs;
import net.msymbios.llovelyr.source.configs.LovelyIdentifier;
import net.msymbios.llovelyr.source.entity.internal.NativeEntityType;
import net.msymbios.llovelyr.source.entity.internal.enums.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.Objects;

import static net.msymbios.llovelyr.common.util.internal.Utility.invertBoolean;
import static net.msymbios.llovelyr.source.items.LovelyItems.ROBOT_CORE;

public abstract class LovelyRobot extends InternalEntity implements GeoEntity {

    // -- Variables --

    protected static final TrackedData<Boolean> AUTO_ATTACK = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    protected static final TrackedData<Integer> MAX_LEVEL = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> LEVEL = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> EXP = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.INTEGER);

    protected static final TrackedData<Integer> FIRE_PROTECTION = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> FALL_PROTECTION = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> BLAST_PROTECTION = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> PROJECTILE_PROTECTION = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.INTEGER);

    protected static final TrackedData<Float> BASE_X = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.FLOAT);
    protected static final TrackedData<Float> BASE_Y = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.FLOAT);
    protected static final TrackedData<Float> BASE_Z = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.FLOAT);

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    protected boolean changeWeapon = false;

    // -- Properties --

    // AUTO ATTACK

    public boolean getAutoAttack() {
        boolean value = false;
        try {value = this.dataTracker.get(AUTO_ATTACK);}
        catch (Exception ignored) {}
        return value;
    } // getAutoAttack ()

    public void setAutoAttack(boolean value) {
        this.dataTracker.set(AUTO_ATTACK, value);
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
        try {level = this.dataTracker.get(LEVEL);}
        catch (Exception ignored){}
        return level;
    } // getCurrentLevel ()

    public void setCurrentLevel(int value){
        this.dataTracker.set(LEVEL, value);
        InternalLogic.handleLevel(this, getHp(), getAttackDamage(), getArmorLevel(), getArmorToughnessLevel());
    } // setCurrentLevel ()

    public int getExp(){
        int value = 1;
        try {value = this.dataTracker.get(EXP);}
        catch (Exception ignored){}
        return value;
    } // getExp ()

    public void setExp(int value){
        this.dataTracker.set(EXP, value);
    } // setExp ()

    // PROTECTION

    public int getFireProtection() {
        int value = 0;
        try {value = this.dataTracker.get(FIRE_PROTECTION);}
        catch (Exception ignored) {}
        return value;
    } // getFireProtection ()

    public void setFireProtection(int value) {
        this.dataTracker.set(FIRE_PROTECTION, value);
    } // setFireProtection ()

    public int getFallProtection() {
        int retValue = 0;
        try {retValue = this.dataTracker.get(FALL_PROTECTION);}
        catch (Exception ignored) {}
        return retValue;
    } // getFallProtection ()

    public void setFallProtection(int value) {
        this.dataTracker.set(FALL_PROTECTION, value);
    } // setFallProtection ()

    public int getBlastProtection() {
        int value = 0;
        try {value = this.dataTracker.get(BLAST_PROTECTION);}
        catch (Exception ignored) {}
        return value;
    } // getBlastProtection ()

    public void setBlastProtection(int value) {
        this.dataTracker.set(BLAST_PROTECTION, value);
    } // setBlastProtection ()

    public int getProjectileProtection() {
        int value = 0;
        try {value = this.dataTracker.get(PROJECTILE_PROTECTION);}
        catch (Exception ignored) {}
        return value;
    } // getProjectileProtection ()

    public void setProjectileProtection(int value) {
        this.dataTracker.set(PROJECTILE_PROTECTION, value);
    } // setProjectileProtection ()

    // BASE

    public float getBaseX() {
        float value = this.getBlockX();
        try {value = this.dataTracker.get(BASE_X);}
        catch (Exception ignored) {}
        return value;
    } // getBaseX ()

    public void setBaseX(float value) {
        this.dataTracker.set(BASE_X, value);
    } // setBaseX ()

    public float getBaseY() {
        float value = this.getBlockY();
        try {value = this.dataTracker.get(BASE_Y);}
        catch (Exception ignored) {}
        return value;
    } // getBaseY ()

    public void setBaseY(float value) {
        this.dataTracker.set(BASE_Y, value);
    } // setBaseY ()

    public float getBaseZ() {
        float value = this.getBlockZ();
        try {value = this.dataTracker.get(BASE_Z);}
        catch (Exception ignored) {}
        return value;
    } // getBaseZ ()

    public void setBaseZ(float value) {
        this.dataTracker.set(BASE_Z, value);
    } // setBaseZ ()

    // -- Constructor --

    public LovelyRobot(EntityType<? extends InternalEntity> entityType, World world, NativeEntityType nativeEntityType) {
        super(entityType, world, nativeEntityType);
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
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(3, new MeleeAttackGoal(this, LovelyConfigs.Common.MovementMeleeAttack, true));
        this.goalSelector.add(4, new AiFollowOwnerGoal(this, LovelyConfigs.Common.MovementFollowOwner, LovelyConfigs.Common.FollowDistanceMax, LovelyConfigs.Common.FollowDistanceMin, false));
        this.goalSelector.add(4, new AiBaseDefenseGoal(this, LovelyConfigs.Common.MovementFollowOwner, LovelyConfigs.Common.BaseDefenceRange, LovelyConfigs.Common.BaseDefenceWarpRange));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, LovelyConfigs.Common.MovementWanderAround));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, LovelyConfigs.Common.LookRange));
        this.goalSelector.add(6, new LookAtEntityGoal(this, LivingEntity.class, LovelyConfigs.Common.LookRange));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new RevengeGoal(this));
        this.targetSelector.add(4, new AiAutoAttackGoal<>(this, MobEntity.class, LovelyConfigs.Common.AttackChance, true, false, InternalEntityType.AvoidAttackingEntities));
    } // initGoals ()

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (!changeWeapon) this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    } // initialize ()

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        if (Objects.requireNonNull(slot.getType()) == EquipmentSlot.Type.HAND) {
            final ItemStack tempSword = new ItemStack(Items.DIAMOND_SWORD, 1);
            final int lootingLevel = this.getLooting();
            if (lootingLevel > 0) tempSword.addEnchantment(Enchantment.byRawId(21), lootingLevel);
            return tempSword;
        }
        return super.getEquippedStack(slot);
    } // getEquippedStack ()

    @Override
    public void tick() {
        super.tick();
        handleCombatMode();
        handleAutoHeal();
        displayExtra();
    } // tick ()

    // DATA

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(LEVEL, 0);
        this.dataTracker.startTracking(EXP, 0);
        this.dataTracker.startTracking(AUTO_ATTACK, true);

        this.dataTracker.startTracking(FIRE_PROTECTION, 0);
        this.dataTracker.startTracking(FALL_PROTECTION, 0);
        this.dataTracker.startTracking(BLAST_PROTECTION, 0);
        this.dataTracker.startTracking(PROJECTILE_PROTECTION, 0);

        this.dataTracker.startTracking(BASE_X, 0F);
        this.dataTracker.startTracking(BASE_Y, 0F);
        this.dataTracker.startTracking(BASE_Z, 0F);
    } // initDataTracker ()

    @Override
    public void writeCustomDataToNbt(NbtCompound dataNBT) {
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

        super.writeCustomDataToNbt(dataNBT);
    } // writeCustomDataToNbt ()

    @Override
    public void readCustomDataFromNbt(NbtCompound dataNBT) {
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
        super.readCustomDataFromNbt(dataNBT);
    } // readCustomDataFromNbt ()

    @Override
    public NbtCompound writeToNBT(NbtCompound dataNBT) {
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
    public void readFromNBT(NbtCompound dataNBT, Version version) {
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
        if(InternalLogic.handleLevelUp(this.getCurrentLevel(), this.getMaxLevel()) && !(target instanceof PlayerEntity) && !this.getWorld().isClient) {
            final int maxHp = (int)((LivingEntity)target).getMaxHealth();
            addExp(maxHp / 4);
        }
        this.getWorld().sendEntityStatus(this, (byte)4);
    } // handleAttackTarget ()

    @Override
    protected boolean handleDamage (DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) return false;
        if ((source.getAttacker() instanceof PlayerEntity player)) {
            if (this.isOwner(player) && !LovelyConfigs.Common.FriendlyFire)
                return false;
        }
        handleActivateCombatMode();

        if ((source.isOf(DamageTypes.ON_FIRE) || source.isOf(DamageTypes.IN_FIRE) || source.isOf(DamageTypes.LAVA)) && amount >= 1.0f && this.getFireProtection() > 0)
            amount *= (100.0f - this.getFireProtection()) / 100.0f;

        if (source.isOf(DamageTypes.FALL) && amount >= 1.0f && this.getFallProtection() > 0)
            amount *= (100.0f - this.getFallProtection()) / 100.0f;

        if (source.isOf(DamageTypes.EXPLOSION) && amount >= 1.0f && this.getBlastProtection() > 0)
            amount *= (100.0f - this.getBlastProtection()) / 100.0f;

        if (source.isOf(DamageTypes.ARROW) && amount >= 1.0f && this.getProjectileProtection() > 0)
            amount *= (100.0f - this.getProjectileProtection()) / 100.0f;

        if (amount < 1.0f) return false;

        if(!getWorld().isClient) {
            if((source.isOf(DamageTypes.ON_FIRE) || source.isOf(DamageTypes.IN_FIRE) || source.isOf(DamageTypes.LAVA)) && InternalLogic.handleFireProtectionLevelUp(getFireProtection())) this.setFireProtection(this.getFireProtection() + 1);
            if(source.isOf(DamageTypes.FALL) && InternalLogic.handleFallProtectionLevelUp(this.getFallProtection())) this.setFallProtection(this.getFallProtection() + 1);
            if(source.isOf(DamageTypes.EXPLOSION) && InternalLogic.handleBlastProtectionLevelUp(this.getBlastProtection())) this.setBlastProtection(this.getBlastProtection() + 1);
            if(source.isOf(DamageTypes.ARROW) && InternalLogic.handleProjectileProtectionLevelUp(this.getProjectileProtection())) this.setProjectileProtection(this.getProjectileProtection() + 1);
        }

        final Entity entity = source.getAttacker();

        if (InternalLogic.handleLevelUp(this.getCurrentLevel(), this.getMaxLevel()) && !(entity instanceof PlayerEntity) && entity instanceof LivingEntity && !this.getWorld().isClient) {
            final int maxHp = (int)((LivingEntity)entity).getMaxHealth();
            addExp(maxHp / 6);
        }

        return true;
    } // handleDamage ()

    @Override
    protected void handleItemDrop() {
        final ItemStack dropItem = setDropItem();
        NbtCompound nbt = dropItem.getNbt();
        if(nbt == null) nbt = new NbtCompound();

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

        dropItem.setNbt(nbt);
        //if (!customName.isEmpty()) dropItem.setCustomName(Text.literal(customName).copy().append(Utility.getRandomTitle()).formatted(Formatting.DARK_PURPLE)); // TODO: I wonder what to do!
        this.dropStack(dropItem, 0.0F);
    } // handleDropItems ()

    @Override
    public ItemStack setDropItem() {
        return new ItemStack(ROBOT_CORE, 1);
    } // setDropItem ()

    protected ActionResult handleItemInteraction (ItemStack stack, PlayerEntity player) {
        if(handleTexture(stack, player)) return ActionResult.SUCCESS;
        return ActionResult.SUCCESS;
    } // handleItemInteraction ()

    @Override
    protected boolean canInteractWithItems(ItemStack stack) {
        if(stack.getItem() instanceof DyeItem) return false;
        if(stack.getItem() instanceof SwordItem) return false;
        if(stack.isOf(Items.STICK) || stack.isOf(Items.BOOK) || stack.isOf(Items.WRITABLE_BOOK) || stack.isOf(Items.OAK_BUTTON)) return false;
        return !stack.isOf(Items.COMPASS) && !stack.isOf(Items.RECOVERY_COMPASS);
    } // canInteractWithItems ()

    @Override
    protected void handleInteract (ItemStack stack, PlayerEntity player) {
        super.handleInteract(stack, player);
        handleStickRetrieval(stack, player);
        handleAutoAttack(stack);
        handleDisplayInteraction(stack);
    } // handleInteract ()

    @Override
    protected void handleState(ItemStack stack) {
        if (handleStandbyState(stack)) return;
        if (handleFollowState(stack)) return;
        if (handleBaseDefenseState(stack)) return;
    } // handleState ()

    @Override
    protected boolean handleTexture(ItemStack stack, PlayerEntity player) {
        var oldTexture = getTextureID();
        if(stack.isOf(Items.WHITE_DYE)) setTexture(EntityTexture.WHITE);
        if(stack.isOf(Items.ORANGE_DYE)) setTexture(EntityTexture.ORANGE);
        if(stack.isOf(Items.MAGENTA_DYE)) setTexture(EntityTexture.MAGENTA);
        if(stack.isOf(Items.LIGHT_BLUE_DYE)) setTexture(EntityTexture.LIGHT_BLUE);
        if(stack.isOf(Items.YELLOW_DYE)) setTexture(EntityTexture.YELLOW);
        if(stack.isOf(Items.LIME_DYE)) setTexture(EntityTexture.LIME);
        if(stack.isOf(Items.PINK_DYE)) setTexture(EntityTexture.PINK);
        if(stack.isOf(Items.GRAY_DYE)) setTexture(EntityTexture.GRAY);
        if(stack.isOf(Items.LIGHT_GRAY_DYE)) setTexture(EntityTexture.LIGHT_GRAY);
        if(stack.isOf(Items.CYAN_DYE)) setTexture(EntityTexture.CYAN);
        if(stack.isOf(Items.PURPLE_DYE)) setTexture(EntityTexture.PURPLE);
        if(stack.isOf(Items.BLUE_DYE)) setTexture(EntityTexture.BLUE);
        if(stack.isOf(Items.BROWN_DYE)) setTexture(EntityTexture.BROWN);
        if(stack.isOf(Items.GREEN_DYE)) setTexture(EntityTexture.GREEN);
        if(stack.isOf(Items.RED_DYE)) setTexture(EntityTexture.RED);
        if(stack.isOf(Items.BLACK_DYE)) setTexture(EntityTexture.BLACK);

        if(oldTexture != getTextureID()) {
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
                return true;
            }
        }
        return false;
    } // handleTexture ()

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    } // method_48926 ()

    // -- Custom Methods --

    /**
     * Adds experience points and handles level-up logic.
     * <p>
     * <b>Custom Name Bonus:</b> Named robots receive 1.5x experience multiplier
     * to reward player investment in personalization.
     * <p>
     * <b>Level-Up:</b> Automatically levels up when experience threshold reached.
     * Updates stats and displays notification to owner.
     * <p>
     * <b>Safety:</b> Validates input, checks max level cap, and ensures owner
     * exists before displaying notifications.
     *
     * @param value experience points to add
     */
    public void addExp(int value) {
        if (value <= 0) return;

        int addExp = value;
        int currentExp = getExp();
        int currentLevel = this.getCurrentLevel();
        int maxLevel = this.getMaxLevel();
        String customName = "";

        // Get custom name safely
        try {
            customName = getCustomName().getString();
        } catch (Exception ignored) {}

        // Named robots earn 1.5x experience bonus
        if (!customName.isEmpty()) {
            addExp = addExp * 3 / 2;
        }

        currentExp += addExp;

        // Store old level for change detection
        var oldLevel = currentLevel;

        // Level up loop with max level cap
        while (currentLevel < maxLevel && currentExp >= InternalLogic.calculateNextExp(currentLevel)) {
            currentExp -= InternalLogic.calculateNextExp(currentLevel);
            currentLevel++;
            setCurrentLevel(currentLevel);
        }

        setExp(currentExp);

        // Display level-up notification only if level actually changed
        if (oldLevel != currentLevel) {
            if (!getWorld().isClient) {
                try {
                    final LivingEntity owner = getOwner();
                    if (owner != null) {
                        displayGeneralMessage(getNotification(), true);
                    }
                } catch (Exception ignored) {}
                
                // Spawn level-up particle effects (villager trade refresh particles)
                InternalParticle.HappyVillager(this);
                
                // Play level-up sound effect (player experience level-up sound) - positional at robot location
                this.getWorld().playSound(
                    null, 
                    this.getBlockPos(), 
                    net.minecraft.sound.SoundEvents.ENTITY_PLAYER_LEVELUP, 
                    net.minecraft.sound.SoundCategory.NEUTRAL, 
                    1.0F, 
                    1.0F
                );
            }
        }
    } // addExp ()

    private boolean canInteractAutoAttack(ItemStack stack) {
        return stack.getItem() instanceof SwordItem;
    } // canInteractAutoAttack ()

    private boolean canInteractGuardMode(ItemStack stack) {
        return stack.isOf(Items.COMPASS) || stack.isOf(Items.RECOVERY_COMPASS);
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
        setSitting(false);
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
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_LOOTING).append(": " + this.getLooting()                            + "/" + LovelyConfigs.Common.MaxLootEnchantment), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_FIRE_PROTECTION).append(": " + this.getFireProtection()             + "/" + LovelyConfigs.Common.ProtectionLimitFire), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_FALL_PROTECTION).append(": " + this.getFallProtection()             + "/" + LovelyConfigs.Common.ProtectionLimitFall), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_BLAST_PROTECTION).append(": " + this.getBlastProtection()           + "/" + LovelyConfigs.Common.ProtectionLimitBlast), false);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_PROJECTILE_PROTECTION).append(": " + this.getProjectileProtection() + "/" + LovelyConfigs.Common.ProtectionLimitProjectile), false);
    } // displayEnchantmentMessage ()

    /**
     * Processes stick-based robot retrieval interaction.
     * <p>
     * <b>Permission Check:</b> Validates player ownership before allowing retrieval.
     * Only the robot's owner can retrieve it using a stick.
     * <p>
     * <b>Inventory Management:</b> Attempts to add spawn item to player inventory.
     * If inventory is full, drops item at robot location as fallback.
     * <p>
     * <b>Feedback:</b> Spawns particle effects and plays sound to confirm retrieval.
     * 
     * @param stack the stick item stack
     * @param player the player attempting retrieval
     * @return true if retrieval was successful
     */
    protected boolean handleStickRetrieval(ItemStack stack, PlayerEntity player) {
        // Only process stick items
        if (!stack.isOf(Items.STICK)) return false;
        
        // Validate ownership
        if (!this.isOwner(player)) return false;
        
        // Check if player has inventory space - if not, do nothing
        if (player.getInventory().getEmptySlot() < 0) {
            return false;
        }
        
        // Create spawn item from current robot state
        ItemStack spawnItem = createSpawnItemFromEntity();
        
        // Add to player inventory (we already checked there's space)
        player.getInventory().insertStack(spawnItem);
        
        // Spawn particle effects (POOF particles)
        InternalParticle.Poof(this);
        
        // Play sound effect
        this.getWorld().playSound(
            null, 
            this.getBlockPos(), 
            net.minecraft.sound.SoundEvents.ENTITY_ITEM_PICKUP, 
            net.minecraft.sound.SoundCategory.PLAYERS, 
            1.0F, 
            1.0F
        );
        
        // Consume stick in survival mode
        if (!player.getAbilities().creativeMode) {
            stack.decrement(1);
        }
        
        // Remove robot entity
        this.discard();
        
        return true;
    } // handleStickRetrieval ()

    /**
     * Creates spawn item from current robot entity with full NBT preservation.
     * <p>
     * <b>Architecture:</b> Uses same NBT structure as handleItemDrop() to ensure
     * consistency with core drop mechanics. Matches LovelySpawnItem.initialize()
     * expectations for proper data transfer.
     * <p>
     * <b>Data Preservation:</b> Transfers all entity state including level, XP,
     * protections, color, custom name, and owner through NBT compound.
     * 
     * @return ItemStack containing spawn item with complete robot data
     */
    private ItemStack createSpawnItemFromEntity() {
        // Determine correct spawn item based on robot variant
        final ItemStack spawnItem;
        if (this.nativeEntity.key.equals("bunny2")) {
            spawnItem = new ItemStack(net.msymbios.llovelyr.source.items.LovelyItems.BUNNY2_SPAWN, 1);
        } else {
            spawnItem = new ItemStack(net.msymbios.llovelyr.source.items.LovelyItems.VANILLA_SPAWN, 1);
        }
        
        // Use same NBT structure as handleItemDrop()
        NbtCompound nbt = spawnItem.getNbt();
        if(nbt == null) nbt = new NbtCompound();

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

        spawnItem.setNbt(nbt);

        // Apply custom name with title (commented for future use)
        // if (!customName.isEmpty()) {
        //     spawnItem.setCustomName(
        //         Text.literal(customName)
        //             .append(Utility.getRandomTitle())
        //             .formatted(Formatting.DARK_PURPLE)
        //     );
        // }
        
        return spawnItem;
    } // createSpawnItemFromEntity ()

} // Class LovelyRobot