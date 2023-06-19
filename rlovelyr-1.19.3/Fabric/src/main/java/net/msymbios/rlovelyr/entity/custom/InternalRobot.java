package net.msymbios.rlovelyr.entity.custom;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.entity.enums.RobotAttribute;
import net.msymbios.rlovelyr.entity.enums.RobotState;
import net.msymbios.rlovelyr.entity.enums.RobotTexture;
import net.msymbios.rlovelyr.entity.enums.RobotVariant;
import net.msymbios.rlovelyr.entity.utils.ModMetrics;
import org.jetbrains.annotations.Nullable;

import static net.msymbios.rlovelyr.entity.utils.ModUtils.*;
import static net.msymbios.rlovelyr.item.ModItems.ROBOT_CORE;

public abstract class InternalRobot extends TameableEntity {

    // -- Variables --
    protected static final TrackedData<String> VARIANT = DataTracker.registerData(InternalRobot.class, TrackedDataHandlerRegistry.STRING);
    protected static final TrackedData<Integer> TEXTURE_ID = DataTracker.registerData(InternalRobot.class, TrackedDataHandlerRegistry.INTEGER);

    protected static final TrackedData<Integer> STATE = DataTracker.registerData(InternalRobot.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Boolean> AUTO_ATTACK = DataTracker.registerData(InternalRobot.class, TrackedDataHandlerRegistry.BOOLEAN);

    protected static final TrackedData<Integer> MAX_LEVEL = DataTracker.registerData(InternalRobot.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> LEVEL = DataTracker.registerData(InternalRobot.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> EXP = DataTracker.registerData(InternalRobot.class, TrackedDataHandlerRegistry.INTEGER);

    protected static final TrackedData<Integer> FIRE_PROTECTION = DataTracker.registerData(InternalRobot.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> FALL_PROTECTION = DataTracker.registerData(InternalRobot.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> BLAST_PROTECTION = DataTracker.registerData(InternalRobot.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> PROJECTILE_PROTECTION = DataTracker.registerData(InternalRobot.class, TrackedDataHandlerRegistry.INTEGER);

    protected static final TrackedData<Float> BASE_X = DataTracker.registerData(InternalRobot.class, TrackedDataHandlerRegistry.FLOAT);
    protected static final TrackedData<Float> BASE_Y = DataTracker.registerData(InternalRobot.class, TrackedDataHandlerRegistry.FLOAT);
    protected static final TrackedData<Float> BASE_Z = DataTracker.registerData(InternalRobot.class, TrackedDataHandlerRegistry.FLOAT);;

    // -- Properties --

    // VARIANT
    public abstract String getVariant();

    public String getVariant(String value) {
        try {value = this.dataTracker.get(VARIANT);}
        catch (Exception ignored) {}
        return value;
    } // getVariant ()

    public void setVariant(String value) {
        this.dataTracker.set(VARIANT, value);
    } // setVariant ()

    // TEXTURE
    public Identifier getTexture() {
        return getTextureByID(getTextureID());
    } // getTexture ()

    public int getTextureID() {
        int value = 0;
        try {value = this.dataTracker.get(TEXTURE_ID);}
        catch (Exception ignored) {}
        return value;
    } // getTextureID ()

    public abstract Identifier getTextureByID(int value);

    public void setTexture(RobotTexture value) {
        setTexture(value.getId());
    } // setTexture ()

    public void setTexture(int value) {
        this.dataTracker.set(TEXTURE_ID, value);
    } // setTexture ()

    // STATE
    public int getCurrentStateID() {
        int value = RobotState.Standby.getId();
        try {value = this.dataTracker.get(STATE);}
        catch (Exception ignored) {}
        return value;
    } // getCurrentStateID ()

    public RobotState getCurrentState() {
        RobotState value = RobotState.Standby;
        try {value = RobotState.byId(this.dataTracker.get(STATE));}
        catch (Exception ignored) {}
        return value;
    } // getCurrentState ()

    public void setCurrentState(RobotState value){
        this.dataTracker.set(STATE, value.getId());
    } // setCurrentMode ()

    public void setCurrentState(int value){
        this.dataTracker.set(STATE, value);
    } // setCurrentState ()

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
    public abstract int getMaxLevel();

    public int getMaxLevel(int value){
        var oldValue = value;
        try {value = this.dataTracker.get(MAX_LEVEL);}
        catch (Exception ignored) {}
        if(value != oldValue) setMaxLevel(oldValue);
        return value;
    } // getMaxLevel ()

    public void setMaxLevel(int value) {
        this.dataTracker.set(MAX_LEVEL, value);
    } // setMaxLevel ()

    public int getLevel() {
        int value = 0;
        try {value = this.dataTracker.get(LEVEL);}
        catch (Exception ignored){}
        return value;
    } // getLevel ()

    public void setLevel(int value){
        this.dataTracker.set(LEVEL, value);

        EntityAttributeInstance maxHealthAttribute = this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        assert maxHealthAttribute != null;
        maxHealthAttribute.setBaseValue(getHpValue());

        EntityAttributeInstance damageAttribute = this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        assert damageAttribute != null;
        damageAttribute.setBaseValue(getAttackValue());

        EntityAttributeInstance armorAttribute = this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
        assert armorAttribute != null;
        armorAttribute.setBaseValue(getArmorValue());

        EntityAttributeInstance armorToughnessAttribute = this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
        assert armorToughnessAttribute != null;
        armorToughnessAttribute.setBaseValue(getArmorToughnessValue());
    } // setLevel ()

    public int getExp(){
        int value = 1;
        try {value = this.dataTracker.get(EXP);}
        catch (Exception ignored){}
        return value;
    } // getExp ()

    public void setExp(int value){
        this.dataTracker.set(EXP, value);
    } // setExp ()

    public abstract int getHpValue();
    public int getHpValue(int value) {
        return (value + this.getLevel() * value / 50);
    } // getHpValue ()

    public abstract int getAttackValue();
    public int getAttackValue(int value) {
        return (value + this.getLevel() * value / 50);
    } // getAttackValue ()

    public abstract int getDefenseValue();

    public int getDefenseValue(int value) {
        return (value + this.getLevel() * value / 50);
    } // getDefenseValue ()

    public int getLootingLevel() {
        int level = 0;
        if (ModMetrics.LootingEnchantment) {
            level = this.getLevel() / ModMetrics.LootingRequiredLevel;
            if (level > ModMetrics.MaxLootingLevel) {
                level = ModMetrics.MaxLootingLevel;
            }
        }
        return level;
    } // getLootingLevel ()

    public double getArmorValue () {
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
        float value = 0;
        try {value = this.dataTracker.get(BASE_X);}
        catch (Exception ignored) {}
        return value;
    } // getBaseX ()

    public void setBaseX(float value) {
        this.dataTracker.set(BASE_X, value);
    } // setBaseX ()

    public float getBaseY() {
        float value = 0;
        try {value = this.dataTracker.get(BASE_Y);}
        catch (Exception ignored) {}
        return value;
    } // getBaseY ()

    public void setBaseY(float value) {
        this.dataTracker.set(BASE_Y, value);
    } // setBaseY ()

    public float getBaseZ() {
        float value = 0;
        try {value = this.dataTracker.get(BASE_Z);}
        catch (Exception ignored) {}
        return value;
    } // getBaseZ ()

    public void setBaseZ(float value) {
        this.dataTracker.set(BASE_Z, value);
    } // setBaseZ ()

    // -- Construct --
    protected InternalRobot(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    } // Construct InternalRobot

    // -- Sound Methods --
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_GENERIC_HURT;
    } // getHurtSound ()

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_GENERIC_DEATH;
    } // getDeathSound ()

    // -- Built-In Methods --
    @Override
    public void onAttacking(Entity target) {
        if(this.canLevelUp() && !(target instanceof PlayerEntity) && target != null && !this.world.isClient) {
            final int maxHp = (int)((LivingEntity)target).getMaxHealth();
            this.addExp(maxHp / 4);
        }
        this.world.sendEntityStatus(this, (byte)4);
        super.onAttacking(target);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) return false;

        if (source.isFire() && amount >= 1.0f && this.getFireProtection() > 0)
            amount *= (100.0f - this.getFireProtection()) / 100.0f;

        if (source == DamageSource.FALL && amount >= 1.0f && this.getFallProtection() > 0)
            amount *= (100.0f - this.getFallProtection()) / 100.0f;

        if (source.isExplosive() && amount >= 1.0f && this.getBlastProtection() > 0)
            amount *= (100.0f - this.getBlastProtection()) / 100.0f;

        if (source.isProjectile() && amount >= 1.0f && this.getProjectileProtection() > 0)
            amount *= (100.0f - this.getProjectileProtection()) / 100.0f;

        if (amount < 1.0f) return false;

        if(!world.isClient) {
            if(source.isFire() && canLevelUpFireProtection()) this.setFireProtection(this.getFireProtection() + 1);
            if(source == DamageSource.FALL && canLevelUpFallProtection()) this.setFallProtection(this.getFallProtection() + 1);
            if(source.isExplosive() && canLevelUpBlastProtection()) this.setBlastProtection(this.getBlastProtection() + 1);
            if(source.isProjectile() && canLevelUpProjectileProtection()) this.setProjectileProtection(this.getProjectileProtection() + 1);
        }

        final Entity entity = source.getSource();

        if (this.canLevelUp() && !(entity instanceof PlayerEntity) && entity instanceof LivingEntity && !this.world.isClient) {
            final int maxHp = (int)((LivingEntity)entity).getMaxHealth();
            this.addExp(maxHp / 6);
        }

        if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof ArrowEntity)) {
            amount = (amount + 1.0f) / 2.0f;
        }

        return super.damage(source, amount);
    } // damage ()

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        final ItemStack craftPurchaseOrder = new ItemStack(ROBOT_CORE, 1);

        try {
            final NbtCompound nbt = new NbtCompound();
            final String customName = this.getEntityName();
            if (customName != null && !customName.trim().equals("")) {
                nbt.putString("CustomName", customName);
            }

            nbt.putString("Variant", this.getVariant());
            nbt.putInt("TextureID", this.getTextureID());

            nbt.putInt("State", this.getCurrentStateID());
            nbt.putBoolean("AutoAttack", this.getAutoAttack());

            nbt.putInt("MaxLevel", this.getMaxLevel());
            nbt.putInt("Level", this.getLevel());
            nbt.putInt("Exp", this.getExp());

            craftPurchaseOrder.setNbt(nbt);
        } catch (Exception ignored) {}
        this.dropStack(craftPurchaseOrder, 0.0f);
    } // dropEquipment ()

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        switch (slot.getType()){
            case HAND: {
                final ItemStack tempSword = new ItemStack(Items.DIAMOND_SWORD,1);
                final int lootingLevel = this.getLootingLevel();
                if(lootingLevel > 0) {
                    tempSword.addEnchantment(Enchantment.byRawId(21), lootingLevel);
                }
                return tempSword;
            }
            default: {
                return super.getEquippedStack(slot);
            }
        }
    } // getEquippedStack ()

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    } // createChild

    // -- Interact Methods --
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        var itemStack = player.getStackInHand(hand);

        if(hand == Hand.MAIN_HAND) {
            handleSit(itemStack);
            if(this.world.isClient) {
                return ActionResult.PASS;
            } else {
                handleState(itemStack, player);
                handleAutoAttack(itemStack, player);
                handleTexture(itemStack);
                if(getOwner() == null) handleTame(player);

                if(itemStack.isOf(Items.STICK)) displayMessage(player);
                if(itemStack.isOf(Items.BOOK)) displayProtectionMessage(player);

                return ActionResult.SUCCESS;
            }
        }

        return super.interactMob(player, hand);
    } // interactMob ()

    // -- Custom Methods --
    protected boolean canLevelUp() {
        return this.getLevel() < getMaxLevel();
    } // canLevelUp ()

    protected boolean canLevelUpFireProtection() {
        return this.getFireProtection() < ModMetrics.FireProtectionLimit;
    } // canLevelUpFireProtection ()

    protected boolean canLevelUpFallProtection() {
        return this.getFallProtection() < ModMetrics.FallProtectionLimit;
    } // canLevelUpFallProtection ()

    protected boolean canLevelUpBlastProtection() {
        return this.getBlastProtection() < ModMetrics.BlastProtectionLimit;
    } // canLevelUpBlastProtection ()

    protected boolean canLevelUpProjectileProtection() {
        return this.getProjectileProtection() < ModMetrics.ProjectileProtectionLimit;
    } // canLevelUpProjectileProtection ()

    protected int getNextExp() {
        return ModMetrics.BaseExp + this.getLevel() * ModMetrics.UpExpValue;
    } // getNextExp ()

    protected void addExp (int value) {
        var addExp = value;

        final String customName = this.getEntityName();
        if(customName != null && !customName.trim().equals(""))
            addExp = addExp * 3 / 2;

        int exp = this.getExp();
        exp += addExp;

        var oldLevel = getLevel();
        while (exp >= this.getNextExp()) {
            exp -= this.getNextExp();
            this.setLevel(this.getLevel() + 1);
        }

        this.setExp(exp);

        if(oldLevel != getLevel()) {
            if(!world.isClient) {
                try {
                    final LivingEntity entity = this.getOwner();
                    if (entity == null) return;
                    this.displayMessage((PlayerEntity)entity);
                } catch (Exception ignored) {}
            }
        }
    } // addExp ()

    protected void handleAutoHeal () {
        if (!this.world.isClient && ModMetrics.AutoHeal && this.age % ModMetrics.AutoHealInterval == 0 && this.getHealth() < this.getHpValue()) {
            final float healValue = this.getHpValue() / 16.0f;
            this.heal(healValue);
        }
    } // handleAutoHeal ()

    public void handleTame(PlayerEntity player) {
        this.setOwner(player);
        this.setTamed(true);
        player.sendMessage(Text.literal("Owner: " + getOwner().getEntityName()), true);
    } // handleTame ()

    public void handleTexture(ItemStack item) {
        if(item.isOf(Items.WHITE_DYE)) setTexture(RobotTexture.WHITE);
        if(item.isOf(Items.ORANGE_DYE)) setTexture(RobotTexture.ORANGE);
        if(item.isOf(Items.MAGENTA_DYE)) setTexture(RobotTexture.MAGENTA);
        if(item.isOf(Items.LIGHT_BLUE_DYE)) setTexture(RobotTexture.LIGHT_BLUE);
        if(item.isOf(Items.YELLOW_DYE)) setTexture(RobotTexture.YELLOW);
        if(item.isOf(Items.LIME_DYE)) setTexture(RobotTexture.LIME);
        if(item.isOf(Items.PINK_DYE)) setTexture(RobotTexture.PINK);
        if(item.isOf(Items.GRAY_DYE)) setTexture(RobotTexture.GRAY);
        if(item.isOf(Items.LIGHT_GRAY_DYE)) setTexture(RobotTexture.LIGHT_GRAY);
        if(item.isOf(Items.CYAN_DYE)) setTexture(RobotTexture.CYAN);
        if(item.isOf(Items.PURPLE_DYE)) setTexture(RobotTexture.PURPLE);
        if(item.isOf(Items.BLUE_DYE)) setTexture(RobotTexture.BLUE);
        if(item.isOf(Items.BROWN_DYE)) setTexture(RobotTexture.BROWN);
        if(item.isOf(Items.GREEN_DYE)) setTexture(RobotTexture.GREEN);
        if(item.isOf(Items.RED_DYE)) setTexture(RobotTexture.RED);
        if(item.isOf(Items.BLACK_DYE)) setTexture(RobotTexture.BLACK);
    } // handleTexture ()

    public void handleSit(ItemStack itemStack) {
        if(!canInteract(itemStack)) return;
        setSitting(invertBoolean(isSitting()));
    } // handleSit ()

    public void handleAutoAttack(ItemStack itemStack, PlayerEntity player){
        if (!canInteractAutoAttack(itemStack)) return;
        setAutoAttack(invertBoolean(getAutoAttack()));
        player.sendMessage(Text.literal("Auto Attack: " + this.getAutoAttack()), true);
    } // handleAutoAttack ()

    public void handleState(ItemStack itemStack, PlayerEntity player) {
        var previousState = getCurrentState();
        StandbyState(itemStack);
        FollowState(itemStack);
        BaseDefenseState(itemStack);
        if(previousState != getCurrentState()) player.sendMessage(Text.literal("State: " + getCurrentState().name()), true);
    } // handleState

    public void StandbyState(ItemStack itemStack){
        if(!canInteract(itemStack)) return;
        if(isSitting()) setCurrentState(RobotState.Standby);
    } // StandbyState ()

    public void FollowState(ItemStack itemStack){
        if(!canInteract(itemStack)) return;
        if(!isSitting()) setCurrentState(RobotState.Follow);
    } // FollowState ()

    public void BaseDefenseState(ItemStack itemStack){
        if(!canInteractGuardMode(itemStack)) return;
        setSitting(false);
        setAutoAttack(true);

        var currentPosition = this.getPos();
        this.setBaseX((float)currentPosition.x);
        this.setBaseY((float)currentPosition.y);
        this.setBaseZ((float)currentPosition.z);
        setCurrentState(RobotState.BaseDefense);
    } // BaseDefenseState ()

    public void displayMessage (PlayerEntity player) {
        player.sendMessage(Text.literal("|--------------------------"));
        player.sendMessage(Text.literal("MaxLevel: " + this.getMaxLevel()));
        player.sendMessage(Text.literal("Model: " + this.getVariant()));
        player.sendMessage(Text.literal("Health: " + this.getHealth() + "/" + this.getMaxHealth()));
        player.sendMessage(Text.literal("Attack: " + this.getAttackValue()));
        player.sendMessage(Text.literal("Auto Attack: " + this.getAutoAttack()));
        player.sendMessage(Text.literal("Level: " + this.getLevel()));
        player.sendMessage(Text.literal("Exp: " + this.getExp()));
        player.sendMessage(Text.literal("Looting: " + this.getLootingLevel()));
    } // displayMessage ()

    public void displayProtectionMessage (PlayerEntity player) {
        player.sendMessage(Text.literal("|--------------------------"));
        player.sendMessage(Text.literal("Fire Protection: " + this.getFireProtection() + "/" + ModMetrics.FireProtectionLimit));
        player.sendMessage(Text.literal("Fall Protection: " + this.getFallProtection() + "/" + ModMetrics.FallProtectionLimit));
        player.sendMessage(Text.literal("Blast Protection: " + this.getBlastProtection() + "/" + ModMetrics.BlastProtectionLimit));
        player.sendMessage(Text.literal("Projectile Protection: " + this.getProjectileProtection() + "/" + ModMetrics.ProjectileProtectionLimit));
    } // displayProtectionMessage ()

} // Class InternalRobot