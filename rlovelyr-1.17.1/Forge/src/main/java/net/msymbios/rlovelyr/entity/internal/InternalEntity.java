package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.msymbios.rlovelyr.entity.custom.VanillaEntity;
import net.msymbios.rlovelyr.entity.enums.*;

import java.util.Objects;

import static net.msymbios.rlovelyr.entity.internal.Utility.invertBoolean;

public abstract class InternalEntity extends TamableAnimal {

    // -- Variables --
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

    protected int waryTimer = 0, autoHealTimer = 0;
    protected boolean combatMode = false, autoHeal = false, log = false;
    protected EntityVariant variant;
    protected EntityModel model = EntityModel.Default;

    // -- Properties --

    // VARIANT
    public String getVariant() { return this.variant.getName(); } // getVariant ()

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
        return this.log;
    } // getNotification ()

    public void setNotification(boolean value) {
        this.log = value;
    } // setNotification ()

    // -- Constructor --
    protected InternalEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
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
    public void tick() {
        super.tick();
        handleCombatMode();
        handleAutoHeal();
        commandDebugExtra();
    } // tick ()

    @Override
    public boolean doHurtTarget(Entity target) {
        handleActivateCombatMode();
        if(this.canLevelUp() && !(target instanceof Player) && !this.level.isClientSide) {
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
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
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

    @Override
    public void onEnterCombat() {
        handleActivateCombatMode();
        super.onEnterCombat();
    } // onEnterCombat ()

    // -- Interact Methods --
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        var itemStack = player.getItemInHand(hand);
        if(hand == InteractionHand.MAIN_HAND) {
            if(getOwner() != null)  handleSit(itemStack);
            if (this.level.isClientSide) {
                boolean bl = this.isOwnedBy(player) || this.isTame() || itemStack.is(Items.BONE) && !this.isTame();
                return bl ? InteractionResult.CONSUME : InteractionResult.PASS;
            } else {
                if(getOwner() == null) handleTame(player);
                if(getOwner() != null) {
                    handleState(itemStack, player);
                    handleAutoAttack(itemStack, player);
                    handleTexture(itemStack, player);

                    if(itemStack.getItem() == (Items.OAK_BUTTON)) {
                        this.setNotification(Utility.invertBoolean(getNotification()));
                        if(getNotification()) commandDebug(new TranslatableComponent("msg.rlovelyr.notification").append(Component.nullToEmpty(" ")).append(new TranslatableComponent("msg.rlovelyr.on")), true);
                        else commandDebug(new TranslatableComponent("msg.rlovelyr.notification").append(Component.nullToEmpty(" ")).append(new TranslatableComponent("msg.rlovelyr.off")), true);
                    }

                    if(itemStack.is(Items.BOOK)) displayMessage(player, true, false);
                    if(itemStack.is(Items.WRITABLE_BOOK)) displayProtectionMessage(player);
                }

                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    } // interactMob ()

    public static boolean canInteract(ItemStack itemStack){
        if(itemStack.is(Items.WHITE_DYE) ||itemStack.is(Items.ORANGE_DYE) || itemStack.is(Items.MAGENTA_DYE) || itemStack.is(Items.LIGHT_BLUE_DYE) ||
                itemStack.is(Items.YELLOW_DYE) || itemStack.is(Items.LIME_DYE) || itemStack.is(Items.PINK_DYE) || itemStack.is(Items.GRAY_DYE) ||
                itemStack.is(Items.LIGHT_GRAY_DYE) || itemStack.is(Items.CYAN_DYE) || itemStack.is(Items.PURPLE_DYE) || itemStack.is(Items.BLUE_DYE) ||
                itemStack.is(Items.BROWN_DYE) || itemStack.is(Items.GREEN_DYE) || itemStack.is(Items.RED_DYE) || itemStack.is(Items.BLACK_DYE)) return false;
        if(itemStack.is(Items.WOODEN_SWORD) || itemStack.is(Items.STONE_SWORD) || itemStack.is(Items.IRON_SWORD) || itemStack.is(Items.GOLDEN_SWORD) || itemStack.is(Items.DIAMOND_SWORD) || itemStack.is(Items.NETHERITE_SWORD)) return false;
        if(itemStack.is(Items.STICK) || itemStack.is(Items.BOOK) || itemStack.is(Items.WRITABLE_BOOK) || itemStack.is(Items.OAK_BUTTON)) return false;
        return !itemStack.is(Items.COMPASS);
    } // canInteract ()

    public static boolean canInteractGuardMode(ItemStack itemStack){
        return itemStack.is(Items.COMPASS);
    } // canInteractGuardMode ()

    public static boolean canInteractAutoAttack(ItemStack itemStack) {
        return itemStack.is(Items.WOODEN_SWORD) || itemStack.is(Items.STONE_SWORD) || itemStack.is(Items.IRON_SWORD) || itemStack.is(Items.GOLDEN_SWORD) || itemStack.is(Items.DIAMOND_SWORD) || itemStack.is(Items.NETHERITE_SWORD);
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

        var oldLevel = getCurrentLevel();
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
                    this.displayMessage((Player)entity, getNotification(), true);
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
        if(this.level.isClientSide && !combatMode) return;

        if(waryTimer != 0) {
            if(model != EntityModel.Armed) model = EntityModel.Armed;
            waryTimer--;
        } else {
            if(model != EntityModel.Default) model = EntityModel.Default;
            combatMode = false;
        }
    } // handleCombatMode ()

    public void handleTame(Player player) {
        this.setOwnerUUID(player.getUUID());
        this.setTame(true);
        this.setOrderedToSit(true);
        player.displayClientMessage(new TranslatableComponent("msg.rlovelyr.owner").append(": " + getOwner().getName().getString()), true);
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
            if (!player.getAbilities().instabuild) item.shrink(1);
        }
    } // handleTexture ()

    public void handleSit(ItemStack itemStack) {
        if(!canInteract(itemStack)) return;
        setOrderedToSit(invertBoolean(isOrderedToSit()));
    } // handleSit ()

    public void handleAutoAttack(ItemStack itemStack, Player player){
        if (!canInteractAutoAttack(itemStack)) return;
        setAutoAttack(invertBoolean(getAutoAttack()));
        String msgAutoAttack;
        if(this.getAutoAttack()) msgAutoAttack = "msg.rlovelyr.on";
        else msgAutoAttack = "msg.rlovelyr.off";
        player.displayClientMessage(new TranslatableComponent("msg.rlovelyr.auto_attack").append(Component.nullToEmpty(": ")).append(new TranslatableComponent(msgAutoAttack)), true);
    } // handleAutoAttack ()

    public void handleState(ItemStack itemStack, Player player) {
        var previousState = getCurrentState();
        StandbyState(itemStack);
        FollowState(itemStack);
        BaseDefenseState(itemStack);
        if(previousState != getCurrentState()) player.displayClientMessage(new TranslatableComponent("msg.rlovelyr.state").append(Component.nullToEmpty(": ")).append(new TranslatableComponent(Utility.getTranslatableState(this.getCurrentState()))), true);
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

    // -- Debug Methods --
    public void commandDebug(String message, boolean overlay) {
        if(this.getOwner() != null) {
            Player player = (Player)this.getOwner();
            player.displayClientMessage(Component.nullToEmpty(message), overlay);
        }
    } // commandDebug ()

    public void commandDebug(MutableComponent message, boolean overlay) {
        if(this.getOwner() != null) {
            Player player = (Player)this.getOwner();
            player.displayClientMessage(message, overlay);
        }
    } // commandDebug ()

    public void commandDebugExtra() {
        MutableComponent debug = null;
        if(combatMode && getNotification()) {
            debug = new TranslatableComponent(Utility.getTranslatableEntity(this.variant)).append(Component.nullToEmpty(": ")).append(new TranslatableComponent("msg.rlovelyr.wary"));
            if(waryTimer < 10) debug = debug.append(": 0" + waryTimer + " ");
            else debug = debug.append(": " + waryTimer + " ");
        }

        if(autoHeal && getNotification()) {
            if(debug != null) debug = debug.append(new TranslatableComponent("msg.rlovelyr.heal"));
            else debug = new TranslatableComponent(Utility.getTranslatableEntity(this.variant)).append(Component.nullToEmpty(": ")).append(new TranslatableComponent("msg.rlovelyr.heal"));

            if(autoHealTimer < 10) debug = debug.append(": 0" + autoHealTimer + " ");
            else debug = debug.append(": " + autoHealTimer + " ");
            if(this.getHealth() < 10) debug = debug.append("| 0" + this.getHealth());
            else debug = debug.append("| " + (int)Math.floor(this.getHealth()));
        }
        if(debug != null) commandDebug(debug, true);
    } // commandDebugExtra ()

    public void displayMessage (Player player, boolean canShow, boolean showLevelUp) {
        if(!canShow) return;
        player.displayClientMessage(new TranslatableComponent("msg.rlovelyr.bar"), false);
        if(showLevelUp) player.displayClientMessage(new TranslatableComponent("msg.rlovelyr.level_up"), false);
        if(this.getCustomName() != null) player.displayClientMessage(new TranslatableComponent(Utility.getTranslatableEntity(this.variant)).append(": " + this.getCustomName().getString()), false);
        else player.displayClientMessage(new TranslatableComponent(Utility.getTranslatableEntity(this.variant)), false);
        player.displayClientMessage(new TranslatableComponent("msg.rlovelyr.level").append(": " + this.getCurrentLevel()             + "/" + this.getMaxLevel()), false);
        player.displayClientMessage(new TranslatableComponent("msg.rlovelyr.experience").append(": " + this.getExp()                 + "/" + this.getNextExp()), false);
        player.displayClientMessage(new TranslatableComponent("msg.rlovelyr.health").append(": " + (int)Math.floor(this.getHealth()) + "/" + (int)this.getMaxHealth()), false);
        player.displayClientMessage(new TranslatableComponent("msg.rlovelyr.attack").append(": " + this.getAttackValue()), false);
        player.displayClientMessage(new TranslatableComponent("msg.rlovelyr.defence").append(": " + this.getDefenseValue()), false);
    } // displayMessage ()

    public void displayProtectionMessage (Player player) {
        player.displayClientMessage(new TranslatableComponent("msg.rlovelyr.bar"), false);
        player.displayClientMessage(new TranslatableComponent("msg.rlovelyr.enchantment"), false);
        player.displayClientMessage(new TranslatableComponent("msg.rlovelyr.looting").append(": " + this.getLootingLevel()                       + "/" + InternalMetric.MaxLootingLevel), false);
        player.displayClientMessage(new TranslatableComponent("msg.rlovelyr.fire_protection").append(": " + this.getFireProtection()             + "/" + InternalMetric.FireProtectionLimit), false);
        player.displayClientMessage(new TranslatableComponent("msg.rlovelyr.fall_protection").append(": " + this.getFallProtection()             + "/" + InternalMetric.FallProtectionLimit), false);
        player.displayClientMessage(new TranslatableComponent("msg.rlovelyr.blast_protection").append(": " + this.getBlastProtection()           + "/" + InternalMetric.BlastProtectionLimit), false);
        player.displayClientMessage(new TranslatableComponent("msg.rlovelyr.projectile_protection").append(": " + this.getProjectileProtection() + "/" + InternalMetric.ProjectileProtectionLimit), false);
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
    } // defineSynchedData ()

    public void addAdditionalSaveData(CompoundTag nbt) {
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
    } // addAdditionalSaveData ()

    public void readAdditionalSaveData(CompoundTag nbt) {
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
    } // readAdditionalSaveData ()

} // Class InternalEntity