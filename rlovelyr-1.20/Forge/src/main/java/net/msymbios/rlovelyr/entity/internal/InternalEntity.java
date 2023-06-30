package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.msymbios.rlovelyr.entity.custom.VanillaEntity;
import net.msymbios.rlovelyr.entity.enums.EntityState;
import net.msymbios.rlovelyr.entity.enums.EntityTexture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.msymbios.rlovelyr.entity.internal.Utility.*;

public abstract class InternalEntity extends TamableAnimal {

    // -- Variables --
    protected static final EntityDataAccessor<String> VARIANT = SynchedEntityData.defineId(VanillaEntity.class, EntityDataSerializers.STRING);
    protected static final EntityDataAccessor<Integer> TEXTURE_ID = SynchedEntityData.defineId(VanillaEntity.class, EntityDataSerializers.INT);

    protected static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(VanillaEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> AUTO_ATTACK = SynchedEntityData.defineId(VanillaEntity.class, EntityDataSerializers.BOOLEAN);

    protected static final EntityDataAccessor<Integer> MAX_LEVEL = SynchedEntityData.defineId(VanillaEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> LEVEL = SynchedEntityData.defineId(VanillaEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> EXP = SynchedEntityData.defineId(VanillaEntity.class, EntityDataSerializers.INT);

    protected static final EntityDataAccessor<Integer> FIRE_PROTECTION = SynchedEntityData.defineId(VanillaEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> FALL_PROTECTION = SynchedEntityData.defineId(VanillaEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> BLAST_PROTECTION = SynchedEntityData.defineId(VanillaEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> PROJECTILE_PROTECTION = SynchedEntityData.defineId(VanillaEntity.class, EntityDataSerializers.INT);

    protected static final EntityDataAccessor<Float> BASE_X = SynchedEntityData.defineId(VanillaEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> BASE_Y = SynchedEntityData.defineId(VanillaEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> BASE_Z = SynchedEntityData.defineId(VanillaEntity.class, EntityDataSerializers.FLOAT);

    protected int autoHealCounter = 0;

    // -- Properties --

    // TEXTURE
    public abstract ResourceLocation getTextureByID(int value);

    public ResourceLocation getTexture() {
        return getTextureByID(getTextureID());
    } // getTexture ()

    public int getTextureID() {
        int value = 0;
        try {value = this.entityData.get(TEXTURE_ID);}
        catch (Exception ignored) {}
        return value;
    } // getTextureID ()

    public void setTexture(int value) {
        this.entityData.set(TEXTURE_ID, value);
    } // setTexture ()

    public void setTexture(EntityTexture value) {
        setTexture(value.getId());
    } // setTexture ()

    // VARIANT
    public abstract String getVariant();

    public String getVariant(String value ) {
        try {value = this.entityData.get(VARIANT);}
        catch (Exception ignored) {}
        return value;
    } // getVariant ()

    public void setVariant(String value) {
        this.entityData.set(VARIANT, value);
    } // setVariant ()

    // STATE
    public int getCurrentStateID() {
        int value = EntityState.Standby.getId();
        try {value = this.entityData.get(STATE);}
        catch (Exception ignored) {}
        return value;
    } // getCurrentStateID ()

    public EntityState getCurrentState() {
        EntityState value = EntityState.Standby;
        try {value = EntityState.byId(this.entityData.get(STATE));}
        catch (Exception ignored) {}
        return value;
    } // getCurrentState ()

    public void setCurrentState(EntityState value){
        this.entityData.set(STATE, value.getId());
    } // setCurrentMode ()

    public void setCurrentState(int value){
        this.entityData.set(STATE, value);
    } // setCurrentMode ()

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
    public abstract int getMaxLevel();

    public int getMaxLevel(int value) {
        try {value = this.entityData.get(MAX_LEVEL);}
        catch (Exception ignored) {}
        return value;
    } // getMaxLevel ()

    public void setMaxLevel(int value) {
        this.entityData.set(MAX_LEVEL, value);
    } // setMaxLevel ()

    public int getCurrentLevel() {
        int value = 0;
        try {value = this.entityData.get(LEVEL);}
        catch (Exception ignored){}
        return value;
    } // getCurrentLevel ()

    public void setCurrentLevel(int value) {
        this.entityData.set(LEVEL, value);
        Objects.requireNonNull(this.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(getHpValue());
        Objects.requireNonNull(this.getAttribute(Attributes.ATTACK_DAMAGE)).setBaseValue(getAttackValue());
        Objects.requireNonNull(this.getAttribute(Attributes.ARMOR)).setBaseValue(getBaseArmorValue());
        Objects.requireNonNull(this.getAttribute(Attributes.ARMOR_TOUGHNESS)).setBaseValue(getArmorToughnessValue());
    } // setCurrentLevel ()

    public int getExp() {
        int value = 1;
        try {value = this.entityData.get(EXP);}
        catch (Exception ignored){}
        return value;
    } // getExp ()

    public void setExp(int value){
        this.entityData.set(EXP, value);
    } // setExp ()

    public abstract int getHpValue();

    public int getHpValue(int value) {
        return (value + this.getCurrentLevel() * value / 50);
    } // getHpValue ()

    public abstract int getAttackValue();

    public int getAttackValue(int value) {
        return (value + this.getCurrentLevel() * value / 50);
    } // getAttackValue ()

    public abstract int getDefenseValue();

    public int getDefenseValue(int value) {
        return (value + this.getCurrentLevel() * value / 50);
    } // getDefenseValue ()

    public int getLootingLevel() {
        int level = 0;
        if (InternalMetric.LootingEnchantment) {
            level = this.getCurrentLevel() / InternalMetric.LootingRequiredLevel;
            if (level > InternalMetric.MaxLootingLevel) {
                level = InternalMetric.MaxLootingLevel;
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
    protected InternalEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    } // Constructor InternalEntity ()

    // -- Sound Methods --
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundEvents.GENERIC_HURT;
    } // getHurtSound ()

    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    } // getDeathSound ()

    // -- Built-In Methods --
    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) return false;

        if ((source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.LAVA)) && amount >= 1.0f && this.getFireProtection() > 0)
            amount *= (100.0f - this.getFireProtection()) / 100.0f;

        if (source.is(DamageTypes.FALL) && amount >= 1.0f && this.getFallProtection() > 0)
            amount *= (100.0f - this.getFallProtection()) / 100.0f;

        if (source.is(DamageTypes.EXPLOSION) && amount >= 1.0f && this.getBlastProtection() > 0)
            amount *= (100.0f - this.getBlastProtection()) / 100.0f;

        if (source.is(DamageTypes.ARROW) && amount >= 1.0f && this.getProjectileProtection() > 0)
            amount *= (100.0f - this.getProjectileProtection()) / 100.0f;

        if (amount < 1.0f) return false;

        if(!this.level().isClientSide) {
            if((source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.LAVA)) && canLevelUpFireProtection()) this.setFireProtection(this.getFireProtection() + 1);
            if(source.is(DamageTypes.FALL) && canLevelUpFallProtection()) this.setFallProtection(this.getFallProtection() + 1);
            if(source.is(DamageTypes.EXPLOSION) && canLevelUpBlastProtection()) this.setBlastProtection(this.getBlastProtection() + 1);
            if(source.is(DamageTypes.ARROW) && canLevelUpProjectileProtection()) this.setProjectileProtection(this.getProjectileProtection() + 1);
        }

        final Entity entity = source.getEntity();

        if (this.canLevelUp() && !(entity instanceof Player) && entity instanceof LivingEntity && !this.level().isClientSide) {
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
        if(this.canLevelUp() && !(target instanceof Player) && !this.level().isClientSide) {
            final int maxHp = (int)((LivingEntity)target).getMaxHealth();
            this.addExp(maxHp / 4);
        }
        this.level().broadcastEntityEvent(this, (byte)4);
        return super.doHurtTarget(target);
    } // doHurtTarget ()

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {
        return null;
    } // getBreedOffspring ()

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

    // -- Interact Methods --
    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        var itemStack = player.getItemInHand(hand);

        if(hand == InteractionHand.MAIN_HAND) {
            handleSit(itemStack);
            if (this.level().isClientSide) {
                boolean bl = this.isOwnedBy(player) || this.isTame() || itemStack.is(Items.BONE) && !this.isTame();
                return bl ? InteractionResult.CONSUME : InteractionResult.PASS;
            } else {
                handleState(itemStack, player);
                handleAutoAttack(itemStack, player);
                handleTexture(itemStack, player);
                if(getOwner() == null) handleTame(player);

                if(itemStack.is(Items.STICK)) displayMessage(player);
                if(itemStack.is(Items.BOOK)) displayProtectionMessage(player);

                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, hand);
    } // interactMob ()

    // -- Custom Methods --
    protected void handleAutoHeal () {
        if(this.level().isClientSide && !InternalMetric.AutoHeal) return;
        if(this.getHealth() == this.getHpValue()) return;

        if(autoHealCounter >= InternalMetric.AutoHealInterval) {
            final float healValue = this.getHpValue() / 16.0f;
            this.heal(healValue);
            autoHealCounter = 0;
        }
        autoHealCounter++;
    } // handleAutoHeal ()

    protected boolean canLevelUp() {
        return this.getCurrentLevel() < getMaxLevel();
    } // canLevelUp ()

    protected boolean canLevelUpFireProtection() {
        return this.getFireProtection() < InternalMetric.FireProtectionLimit;
    } // canLevelUpFireProtection ()

    protected boolean canLevelUpFallProtection() {
        return this.getFallProtection() < InternalMetric.FallProtectionLimit;
    } // canLevelUpFallProtection ()

    protected boolean canLevelUpBlastProtection() {
        return this.getBlastProtection() < InternalMetric.BlastProtectionLimit;
    } // canLevelUpBlastProtection ()

    protected boolean canLevelUpProjectileProtection() {
        return this.getProjectileProtection() < InternalMetric.ProjectileProtectionLimit;
    } // canLevelUpProjectileProtection ()

    protected int getNextExp() {
        return InternalMetric.BaseExp + this.getCurrentLevel() * InternalMetric.UpExpValue;
    } // getNextExp ()

    protected void addExp (int value) {
        int addExp = value;
        if(this.hasCustomName()) addExp = addExp * 3 / 2;

        int exp = this.getExp();
        exp += addExp;

        var oldLevel = getCurrentLevel();
        while (exp >= this.getNextExp()) {
            exp -= this.getNextExp();
            this.setCurrentLevel(this.getCurrentLevel() + 1);
        }

        this.setExp(exp);
        if(oldLevel != getCurrentLevel()) {
            if(!level().isClientSide) {
                try {
                    final LivingEntity entity = this.getOwner();
                    if (entity == null) return;
                    this.displayMessage((Player)entity);
                } catch (Exception ignored) {}
            }
        }
    } // addExp ()

    public void handleTame(Player player) {
        this.setOwnerUUID(player.getUUID());
        this.setTame(true);
        this.setOrderedToSit(true);
        player.displayClientMessage(Component.literal("Owner: " + getOwner().getName().getString()), true);
    } // handleTame ()

    public void handleTexture(ItemStack item, Player player) {
        var oldTexture = getTextureID();
        if(item.is(Items.WHITE_DYE)) setTexture(EntityTexture.WHITE);
        if(item.is(Items.ORANGE_DYE)) setTexture(EntityTexture.ORANGE);
        if(item.is(Items.MAGENTA_DYE)) setTexture(EntityTexture.MAGENTA);
        if(item.is(Items.LIGHT_BLUE_DYE)) setTexture(EntityTexture.LIGHT_BLUE);
        if(item.is(Items.YELLOW_DYE)) setTexture(EntityTexture.YELLOW);
        if(item.is(Items.LIME_DYE)) setTexture(EntityTexture.LIME);
        if(item.is(Items.PINK_DYE)) setTexture(EntityTexture.PINK);
        if(item.is(Items.GRAY_DYE)) setTexture(EntityTexture.GRAY);
        if(item.is(Items.LIGHT_GRAY_DYE)) setTexture(EntityTexture.LIGHT_GRAY);
        if(item.is(Items.CYAN_DYE)) setTexture(EntityTexture.CYAN);
        if(item.is(Items.PURPLE_DYE)) setTexture(EntityTexture.PURPLE);
        if(item.is(Items.BLUE_DYE)) setTexture(EntityTexture.BLUE);
        if(item.is(Items.BROWN_DYE)) setTexture(EntityTexture.BROWN);
        if(item.is(Items.GREEN_DYE)) setTexture(EntityTexture.GREEN);
        if(item.is(Items.RED_DYE)) setTexture(EntityTexture.RED);
        if(item.is(Items.BLACK_DYE)) setTexture(EntityTexture.BLACK);
        if(oldTexture != getTextureID()) {
            if (!player.getAbilities().instabuild)
                item.shrink(1);
        }
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
        if(isOrderedToSit()) setCurrentState(EntityState.Standby);
    } // StandbyState ()

    public void FollowState(ItemStack itemStack){
        if(!canInteract(itemStack)) return;
        if(!isOrderedToSit()) setCurrentState(EntityState.Follow);
    } // FollowState ()

    public void BaseDefenseState(ItemStack itemStack){
        if(!canInteractGuardMode(itemStack)) return;
        setOrderedToSit(false);
        setAutoAttack(true);

        var currentPosition = this.position();
        this.setBaseX((float)currentPosition.x);
        this.setBaseY((float)currentPosition.y);
        this.setBaseZ((float)currentPosition.z);
        setCurrentState(EntityState.BaseDefense);
    } // BaseDefenseState ()

    public void displayMessage (Player player) {
        if(!InternalMetric.LevelUpLog) return;
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
        if(!InternalMetric.LevelUpLog) return;
        player.displayClientMessage(Component.literal("|--------------------------"), false);
        player.displayClientMessage(Component.literal("Fire Protection: " + this.getFireProtection() + "/" + InternalMetric.FireProtectionLimit), false);
        player.displayClientMessage(Component.literal("Fall Protection: " + this.getFallProtection() + "/" + InternalMetric.FallProtectionLimit), false);
        player.displayClientMessage(Component.literal("Blast Protection: " + this.getBlastProtection() + "/" + InternalMetric.BlastProtectionLimit), false);
        player.displayClientMessage(Component.literal("Projectile Protection: " + this.getProjectileProtection() + "/" + InternalMetric.ProjectileProtectionLimit), false);
    } // displayProtectionMessage ()

    // -- Save Methods --
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TEXTURE_ID, 0);

        this.entityData.define(STATE, 0);
        this.entityData.define(AUTO_ATTACK, false);

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
        this.setCurrentLevel(nbt.getInt("Level"));
        this.setExp(nbt.getInt("Exp"));

        this.setFireProtection(nbt.getInt("FireProtection"));
        this.setFallProtection(nbt.getInt("FallProtection"));
        this.setBlastProtection(nbt.getInt("BlastProtection"));
        this.setProjectileProtection(nbt.getInt("ProjectileProtection"));

        this.setBaseY(nbt.getFloat("BaseY"));
        this.setBaseZ(nbt.getFloat("BaseZ"));
        this.setBaseX(nbt.getFloat("BaseX"));
    } // readAdditionalSaveData ()

} // Class InternalEntity