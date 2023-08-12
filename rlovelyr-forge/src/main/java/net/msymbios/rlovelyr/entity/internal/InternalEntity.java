package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.msymbios.rlovelyr.entity.custom.VanillaEntity;
import net.msymbios.rlovelyr.entity.enums.*;

import javax.annotation.Nullable;
import java.util.Objects;

import static net.msymbios.rlovelyr.entity.internal.Utility.invertBoolean;

public abstract class InternalEntity extends TameableEntity {

    // -- Variables --
    protected static DataParameter<Integer> TEXTURE_ID = EntityDataManager.defineId(VanillaEntity.class, DataSerializers.INT);
    protected static DataParameter<Integer> STATE = EntityDataManager.defineId(VanillaEntity.class, DataSerializers.INT);
    protected static DataParameter<Boolean> AUTO_ATTACK = EntityDataManager.defineId(VanillaEntity.class, DataSerializers.BOOLEAN);

    protected static DataParameter<Integer> MAX_LEVEL = EntityDataManager.defineId(VanillaEntity.class, DataSerializers.INT);
    protected static DataParameter<Integer> LEVEL = EntityDataManager.defineId(VanillaEntity.class, DataSerializers.INT);
    protected static DataParameter<Integer> EXP = EntityDataManager.defineId(VanillaEntity.class, DataSerializers.INT);

    protected static DataParameter<Integer> FIRE_PROTECTION = EntityDataManager.defineId(VanillaEntity.class, DataSerializers.INT);
    protected static DataParameter<Integer> FALL_PROTECTION = EntityDataManager.defineId(VanillaEntity.class, DataSerializers.INT);
    protected static DataParameter<Integer> BLAST_PROTECTION = EntityDataManager.defineId(VanillaEntity.class, DataSerializers.INT);
    protected static DataParameter<Integer> PROJECTILE_PROTECTION = EntityDataManager.defineId(VanillaEntity.class, DataSerializers.INT);

    protected static DataParameter<Float> BASE_X = EntityDataManager.defineId(VanillaEntity.class, DataSerializers.FLOAT);
    protected static DataParameter<Float> BASE_Y = EntityDataManager.defineId(VanillaEntity.class, DataSerializers.FLOAT);
    protected static DataParameter<Float> BASE_Z = EntityDataManager.defineId(VanillaEntity.class, DataSerializers.FLOAT);

    protected static DataParameter<Boolean> NOTIFICATION = EntityDataManager.defineId(VanillaEntity.class, DataSerializers.BOOLEAN);

    protected int waryTimer = 0, autoHealTimer = 0;
    protected boolean combatMode = false, autoHeal = false;
    protected EntityVariant variant;
    protected EntityModel model = EntityModel.Default;

    // -- Properties --

    // VARIANT
    public String getVariant() {
        return this.variant.getName();
    } // getVariant ()

    // TEXTURE
    public ResourceLocation getTexture() { return InternalMetric.getTexture(this.variant, EntityTexture.byId(getTextureID())); } // getTexture ()

    public int getTextureID() {
        int value = 0;
        try {value = this.entityData.get(TEXTURE_ID);}
        catch (Exception ignored) {}
        return value;
    } // getTextureID ()

    public void setTexture(int value) {
        if(InternalMetric.checkTextureID(this.variant, EntityTexture.byId(value)))
            this.entityData.set(TEXTURE_ID, value);
    } // setTexture ()

    public void setTexture(EntityTexture value) {
        setTexture(value.getId());
    } // setTexture ()

    // MODEL
    public ResourceLocation getCurrentModel() { return InternalMetric.getModel(this.variant, model); } // getCurrentModel ()

    // ANIMATOR
    public ResourceLocation getAnimatorByID(EntityAnimator value) {
        return InternalMetric.getAnimator(this.variant, value);
    } // getAnimatorByID ()

    public ResourceLocation getAnimator() {
        return InternalMetric.getAnimator(this.variant);
    } // getAnimator ()

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
    public int getAttribute(EntityAttribute attribute) { return (int) InternalMetric.getAttributeValue(this.variant, attribute); } // getAttribute ()

    public int getMaxLevel() { return getMaxLevel (getAttribute(EntityAttribute.MAX_LEVEL)); } // getMaxLevel ()

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

    public int getHpValue() { return getHpValue(getAttribute(EntityAttribute.MAX_HEALTH)); } // getHpValue ()

    public int getHpValue(int value) {
        return (value + this.getCurrentLevel() * value / 50);
    } // getHpValue ()

    public int getAttackValue() { return getAttackValue(getAttribute(EntityAttribute.ATTACK_DAMAGE)); } // getAttackValue ()

    public int getAttackValue(int value) {
        return (value + this.getCurrentLevel() * value / 50);
    } // getAttackValue ()

    public int getDefenseValue() { return getDefenseValue(getAttribute(EntityAttribute.DEFENSE)); } // getDefenseValue ()

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

    // INFO
    public boolean getNotification() {
        boolean value = false;
        try {value = this.entityData.get(NOTIFICATION);}
        catch (Exception ignored) {}
        return value;
    } // getNotification ()

    public void setNotification(boolean value) {
        this.entityData.set(NOTIFICATION, value);
    } // setNotification ()

    // -- Constructor --
    protected InternalEntity(EntityType<? extends TameableEntity> entityType, World level) {
        super(entityType, level);
    } // Constructor InternalEntity ()

    // -- Sound Methods --
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.GENERIC_HURT;
    } // getHurtSound ()

    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    } // getDeathSound ()

    // -- Built-In Methods --
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
        this.setOrderedToSit(true);
        this.setCurrentState(EntityState.Standby);
        return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
    } // finalizeSpawn ()

    @Override
    public void tick() {
        super.tick();
        handleCombatMode();
        handleAutoHeal();
        commandDebugExtra();
    } // tick ()

    @Override
    public boolean doHurtTarget(Entity target) {
        handleActivateCombatMode();
        if(this.canLevelUp() && !(target instanceof PlayerEntity) && !this.level.isClientSide) {
            final int maxHp = (int)((LivingEntity)target).getMaxHealth();
            this.addExp(maxHp / 4);
        }
        this.level.broadcastEntityEvent(this, (byte)4);
        return super.doHurtTarget(target);
    } // doHurtTarget ()

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) return false;
        handleActivateCombatMode();

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

        if (this.canLevelUp() && !(entity instanceof PlayerEntity) && entity instanceof LivingEntity && !this.level.isClientSide) {
            final int maxHp = (int)((LivingEntity)entity).getMaxHealth();
            this.addExp(maxHp / 6);
        }

        if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof ArrowEntity)) {
            amount = (amount + 1.0f) / 2.0f;
        }

        return super.hurt(source, amount);
    } // hurt ()

    @Nullable @Override
    public AgeableEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return null;
    } // getBreedOffspring ()

    @Override
    public ItemStack getItemBySlot(EquipmentSlotType slot) {
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

    @Override
    public void onEnterCombat() {
        handleActivateCombatMode();
        super.onEnterCombat();
    } // onEnterCombat ()

    // -- Interact Methods --
    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if(hand == Hand.MAIN_HAND) {
            if(getOwner() != null) handleSit(itemStack);
            if (this.level.isClientSide) {
                boolean bl = this.isOwnedBy(player) || this.isTame() || itemStack.getItem() == (Items.BONE) && !this.isTame();
                return bl ? ActionResultType.CONSUME : ActionResultType.PASS;
            } else {
                handleState(itemStack, player);
                handleAutoAttack(itemStack, player);
                handleTexture(itemStack, player);
                if(getOwner() == null) handleTame(player);

                if(getOwner() != null) {
                    if(itemStack.getItem() == (Items.OAK_BUTTON)) {
                        this.setNotification(Utility.invertBoolean(getNotification()));
                        if(getNotification()) commandDebug("InfoLog ON", true);
                        else commandDebug("InfoLog Log OFF", true);
                    }

                    if(itemStack.getItem() == (Items.BOOK)) displayMessage(player, true, false);
                    if(itemStack.getItem() == (Items.WRITABLE_BOOK)) displayProtectionMessage(player);
                }

                return ActionResultType.SUCCESS;
            }
        }

        return super.mobInteract(player, hand);
    } // mobInteract ()

    public static boolean canInteract(ItemStack itemStack){
        if(itemStack.getItem() == (Items.WHITE_DYE) ||itemStack.getItem() == (Items.ORANGE_DYE) || itemStack.getItem() == (Items.MAGENTA_DYE) || itemStack.getItem() == (Items.LIGHT_BLUE_DYE) ||
                itemStack.getItem() == (Items.YELLOW_DYE) || itemStack.getItem() == (Items.LIME_DYE) || itemStack.getItem() == (Items.PINK_DYE) || itemStack.getItem() == (Items.GRAY_DYE) ||
                itemStack.getItem() == (Items.LIGHT_GRAY_DYE) || itemStack.getItem() == (Items.CYAN_DYE) || itemStack.getItem() == (Items.PURPLE_DYE) || itemStack.getItem() == (Items.BLUE_DYE) ||
                itemStack.getItem() == (Items.BROWN_DYE) || itemStack.getItem() == (Items.GREEN_DYE) || itemStack.getItem() == (Items.RED_DYE) || itemStack.getItem() == (Items.BLACK_DYE)) return false;
        if(itemStack.getItem() == (Items.WOODEN_SWORD) || itemStack.getItem() == (Items.STONE_SWORD) || itemStack.getItem() == (Items.IRON_SWORD) || itemStack.getItem() == (Items.GOLDEN_SWORD) || itemStack.getItem() == (Items.DIAMOND_SWORD) || itemStack.getItem() == (Items.NETHERITE_SWORD)) return false;
        if(itemStack.getItem() == (Items.STICK) || itemStack.getItem() == (Items.BOOK) || itemStack.getItem() == (Items.WRITABLE_BOOK) || itemStack.getItem() == (Items.OAK_BUTTON)) return false;
        return itemStack.getItem() != (Items.COMPASS);
    } // canInteract ()

    public static boolean canInteractGuardMode(ItemStack itemStack){
        return itemStack.getItem() == (Items.COMPASS);
    } // canInteractGuardMode ()

    public static boolean canInteractAutoAttack(ItemStack itemStack) {
        return itemStack.getItem() == (Items.WOODEN_SWORD) || itemStack.getItem() == (Items.STONE_SWORD) || itemStack.getItem() == (Items.IRON_SWORD) || itemStack.getItem() == (Items.GOLDEN_SWORD) || itemStack.getItem() == (Items.DIAMOND_SWORD) || itemStack.getItem() == (Items.NETHERITE_SWORD);
    } // canInteractAutoAttack ()

    // -- Custom Methods --
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

        int oldLevel = getCurrentLevel();
        while (exp >= this.getNextExp()) {
            exp -= this.getNextExp();
            this.setCurrentLevel(this.getCurrentLevel() + 1);
        }

        this.setExp(exp);
        if(oldLevel != getCurrentLevel()) {
            if(!level.isClientSide) {
                try {
                    final LivingEntity entity = this.getOwner();
                    if (entity == null) return;
                    this.displayMessage((PlayerEntity)entity, getNotification(), true);
                } catch (Exception ignored) {}
            }
        }
    } // addExp ()

    // -- Logic Methods --
    protected void handleAutoHeal () {
        if(this.getHealth() < this.getHpValue()) autoHeal = true;
        if(this.level.isClientSide && !autoHeal) return;

        if(autoHealTimer != 0) {
            autoHealTimer--;
        } else {
            final float healValue = this.getHpValue() / 16.0F;
            this.heal(healValue);
            autoHeal = false;
            autoHealTimer = InternalMetric.AutoHealInterval;
        }
    } // handleAutoHeal ()

    protected void handleActivateCombatMode () {
        if(!combatMode) combatMode = true;
        waryTimer = InternalMetric.WaryTime;
    } // handleActivateCombatMode ()

    protected void handleCombatMode() {
        //if(this.attackable()) handleActivateCombatMode();
        if(this.level.isClientSide && !combatMode) return;

        if(waryTimer != 0) {
            if(model != EntityModel.Armed) model = EntityModel.Armed;
            waryTimer--;
        } else {
            if(model != EntityModel.Default) model = EntityModel.Default;
            combatMode = false;
        }
    } // handleCombatMode ()

    public void handleTame(PlayerEntity player) {
        this.setOwnerUUID(player.getUUID());
        this.setTame(true);
        this.setOrderedToSit(true);
        player.displayClientMessage(ITextComponent.nullToEmpty("Owner: " + getOwner().getName().getString()), true);
    } // handleTame ()

    public void handleTexture(ItemStack item, PlayerEntity player) {
        int oldTexture = getTextureID();
        if(item.getItem() == (Items.WHITE_DYE)) setTexture(EntityTexture.WHITE);
        if(item.getItem() == (Items.ORANGE_DYE)) setTexture(EntityTexture.ORANGE);
        if(item.getItem() == (Items.MAGENTA_DYE)) setTexture(EntityTexture.MAGENTA);
        if(item.getItem() == (Items.LIGHT_BLUE_DYE)) setTexture(EntityTexture.LIGHT_BLUE);
        if(item.getItem() == (Items.YELLOW_DYE)) setTexture(EntityTexture.YELLOW);
        if(item.getItem() == (Items.LIME_DYE)) setTexture(EntityTexture.LIME);
        if(item.getItem() == (Items.PINK_DYE)) setTexture(EntityTexture.PINK);
        if(item.getItem() == (Items.GRAY_DYE)) setTexture(EntityTexture.GRAY);
        if(item.getItem() == (Items.LIGHT_GRAY_DYE)) setTexture(EntityTexture.LIGHT_GRAY);
        if(item.getItem() == (Items.CYAN_DYE)) setTexture(EntityTexture.CYAN);
        if(item.getItem() == (Items.PURPLE_DYE)) setTexture(EntityTexture.PURPLE);
        if(item.getItem() == (Items.BLUE_DYE)) setTexture(EntityTexture.BLUE);
        if(item.getItem() == (Items.BROWN_DYE)) setTexture(EntityTexture.BROWN);
        if(item.getItem() == (Items.GREEN_DYE)) setTexture(EntityTexture.GREEN);
        if(item.getItem() == (Items.RED_DYE)) setTexture(EntityTexture.RED);
        if(item.getItem() == (Items.BLACK_DYE)) setTexture(EntityTexture.BLACK);
        if(oldTexture != getTextureID()) {
            if (!player.abilities.instabuild)
                item.shrink(1);
        }
    } // handleTexture ()

    public void handleSit(ItemStack itemStack) {
        if(!canInteract(itemStack)) return;
        setOrderedToSit(invertBoolean(isOrderedToSit()));
    } // handleSit ()

    public void handleAutoAttack(ItemStack itemStack, PlayerEntity player){
        if (!canInteractAutoAttack(itemStack)) return;
        setAutoAttack(invertBoolean(getAutoAttack()));
        player.displayClientMessage(ITextComponent.nullToEmpty("Auto Attack: " + this.getAutoAttack()), true);
    } // handleAutoAttack ()

    public void handleState(ItemStack itemStack, PlayerEntity player) {
        EntityState previousState = getCurrentState();
        StandbyState(itemStack);
        FollowState(itemStack);
        BaseDefenseState(itemStack);
        if(previousState != getCurrentState()) player.displayClientMessage(ITextComponent.nullToEmpty("State: " + getCurrentState().name()), true);
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

        Vector3d currentPosition = this.position();
        this.setBaseX((float)currentPosition.x);
        this.setBaseY((float)currentPosition.y);
        this.setBaseZ((float)currentPosition.z);
        setCurrentState(EntityState.BaseDefense);
    } // BaseDefenseState ()

    // -- Debug Methods --
    public void commandDebug(String message, boolean overlay) {
        if(this.getOwner() != null) {
            PlayerEntity player = (PlayerEntity) this.getOwner();
            player.displayClientMessage(ITextComponent.nullToEmpty(message), overlay);
        }
    } // commandDebug ()

    public void commandDebugExtra() {
        String debug = "";
        if(combatMode && getNotification()) {
            if(waryTimer < 10) debug += "Wary: 0" + waryTimer + " ";
            else debug += "Wary: " + waryTimer + " ";
        }

        if(autoHeal && getNotification()) {
            if(autoHealTimer < 10) debug += "Heal: 0" + autoHealTimer + " ";
            else debug += "Heal: " + autoHealTimer + " ";
            if(this.getHealth() < 10) debug += "| 0" + this.getHealth();
            else debug += "| " + (int)Math.floor(this.getHealth());
        }
        if(!debug.equals("")) commandDebug(debug, true);
    } // commandDebugExtra ()

    public void displayMessage (PlayerEntity player, boolean canShow, boolean showLevelUp) {
        if(!canShow) return;
        player.displayClientMessage(ITextComponent.nullToEmpty("|--------------------------"), false);
        if(showLevelUp) player.displayClientMessage(ITextComponent.nullToEmpty("[LevelUp]"), false);
        if(this.getCustomName() != null) player.displayClientMessage(ITextComponent.nullToEmpty(Utility.FirstToUpperCase (this.getVariant()) + ": " + this.getCustomName().getString()), false);
        else player.displayClientMessage(ITextComponent.nullToEmpty(Utility.FirstToUpperCase (this.getVariant())), false);
        player.displayClientMessage(ITextComponent.nullToEmpty("Level: " + this.getCurrentLevel() + "/" + this.getMaxLevel()), false);
        player.displayClientMessage(ITextComponent.nullToEmpty("Exp: " + this.getExp() + "/" + this.getNextExp()), false);
        player.displayClientMessage(ITextComponent.nullToEmpty("HP: " + (int)Math.floor(this.getHealth()) + "/" + (int)this.getMaxHealth()), false);
        player.displayClientMessage(ITextComponent.nullToEmpty("Attack: " + this.getAttackValue()), false);
        player.displayClientMessage(ITextComponent.nullToEmpty("Defense: " + this.getDefenseValue()), false);
    } // displayMessage ()

    public void displayProtectionMessage (PlayerEntity player) {
        player.displayClientMessage(ITextComponent.nullToEmpty("|--------------------------"), false);
        player.displayClientMessage(ITextComponent.nullToEmpty("[Enchantment]"), false);
        player.displayClientMessage(ITextComponent.nullToEmpty("Fire Protection: " + this.getFireProtection() + "/" + InternalMetric.FireProtectionLimit), false);
        player.displayClientMessage(ITextComponent.nullToEmpty("Fall Protection: " + this.getFallProtection() + "/" + InternalMetric.FallProtectionLimit), false);
        player.displayClientMessage(ITextComponent.nullToEmpty("Blast Protection: " + this.getBlastProtection() + "/" + InternalMetric.BlastProtectionLimit), false);
        player.displayClientMessage(ITextComponent.nullToEmpty("Projectile Protection: " + this.getProjectileProtection() + "/" + InternalMetric.ProjectileProtectionLimit), false);
    } // displayProtectionMessage ()

    // -- Save Methods --
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TEXTURE_ID, EntityTexture.PINK.getId());

        this.entityData.define(STATE, EntityState.Follow.getId());
        this.entityData.define(AUTO_ATTACK, true);

        this.entityData.define(MAX_LEVEL, 200);
        this.entityData.define(LEVEL, 0);
        this.entityData.define(EXP, 0);

        this.entityData.define(FIRE_PROTECTION, 0);
        this.entityData.define(FALL_PROTECTION, 0);
        this.entityData.define(BLAST_PROTECTION, 0);
        this.entityData.define(PROJECTILE_PROTECTION, 0);

        this.entityData.define(BASE_X, 0F);
        this.entityData.define(BASE_Y, 0F);
        this.entityData.define(BASE_Z, 0F);

        this.entityData.define(NOTIFICATION, false);
    } // defineSynchedData ()

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
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

        nbt.putBoolean("Notification", this.getNotification());
    } // addAdditionalSaveData ()

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
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

        this.setNotification(nbt.getBoolean("Notification"));
    } // readAdditionalSaveData ()

} // Class InternalEntity