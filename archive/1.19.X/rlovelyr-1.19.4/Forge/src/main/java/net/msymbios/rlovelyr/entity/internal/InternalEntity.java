package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.msymbios.rlovelyr.entity.custom.VanillaEntity;
import net.msymbios.rlovelyr.entity.enums.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    protected static final EntityDataAccessor<Boolean> NOTIFICATION = SynchedEntityData.defineId(VanillaEntity.class, EntityDataSerializers.BOOLEAN);

    protected int waryTimer = 0, autoHealTimer = 0;
    protected boolean combatMode = false, autoHeal = false;
    protected EntityVariant variant;
    protected EntityModel model = EntityModel.Default;

    // -- Properties --

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
    public int getAttribute(EntityAttribute attribute) { return (int) InternalMetric.getAttribute(this.variant, attribute); } // getAttribute ()

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
        InternalLogic.handleSetLevel(this);
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

    public int getHp() { return InternalLogic.getHpValue(this, getAttribute(EntityAttribute.MAX_HEALTH)); } // getHp ()

    public int getAttack() { return InternalLogic.getAttackValue(this, getAttribute(EntityAttribute.ATTACK_DAMAGE)); } // getAttack ()

    public int getDefense() { return InternalLogic.getDefenseValue(this, getAttribute(EntityAttribute.DEFENSE)); } // getDefenseValue ()

    public int getLooting() {return InternalLogic.getLootingLevel(this);} // getLooting ()

    public double getArmorLevel() {return InternalLogic.getArmorValue(this);} // getArmorLevel ()

    public double getArmorToughnessLevel() {return InternalLogic.getArmorToughnessValue(this);} // getArmorToughnessLevel ()

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

    // NOTIFICATION
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
    public void tick() {
        super.tick();
        handleCombatMode();
        handleAutoHeal();
    } // tick ()

    @Override
    public boolean doHurtTarget(@NotNull Entity target) {
        handleActivateCombatMode();
        if(InternalLogic.canLevelUp(this) && !(target instanceof Player) && !this.level.isClientSide) {
            final int maxHp = (int)((LivingEntity)target).getMaxHealth();
            InternalLogic.addExp(this, maxHp / 4);
        }
        this.level.broadcastEntityEvent(this, (byte)4);
        return super.doHurtTarget(target);
    } // doHurtTarget ()

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) return false;
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

        if(!level.isClientSide) {
            if((source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.LAVA)) && InternalLogic.canLevelUpFireProtection(this)) this.setFireProtection(this.getFireProtection() + 1);
            if(source.is(DamageTypes.FALL) && InternalLogic.canLevelUpFallProtection(this)) this.setFallProtection(this.getFallProtection() + 1);
            if(source.is(DamageTypes.EXPLOSION) && InternalLogic.canLevelUpBlastProtection(this)) this.setBlastProtection(this.getBlastProtection() + 1);
            if(source.is(DamageTypes.ARROW) && InternalLogic.canLevelUpProjectileProtection(this)) this.setProjectileProtection(this.getProjectileProtection() + 1);
        }

        final Entity entity = source.getEntity();

        if (InternalLogic.canLevelUp(this) && !(entity instanceof Player) && entity instanceof LivingEntity && !this.level.isClientSide) {
            final int maxHp = (int)((LivingEntity)entity).getMaxHealth();
            InternalLogic.addExp(this, maxHp / 6);
        }

        if (entity != null && !(entity instanceof Player) && !(entity instanceof Arrow)) {
            amount = (amount + 1.0f) / 2.0f;
        }

        return super.hurt(source, amount);
    } // hurt ()

    @Override
    protected void dropEquipment() {
        final ItemStack dropItem = setDropItem();//new ItemStack(ROBOT_CORE.get(), 1);
        CompoundTag nbt = dropItem.getTag();
        if(nbt == null) nbt = new CompoundTag();

        String customName = "";
        try {customName = getCustomName().getString();}
        catch (Exception ignored) {}
        if (!customName.isEmpty()) nbt.putString("custom_name", customName);

        nbt.putString("type", Utility.getTranslatable(this.variant));
        nbt.putInt("color", this.getTextureID());

        nbt.putInt("max_level", this.getMaxLevel());
        nbt.putInt("level", this.getCurrentLevel());
        nbt.putInt("exp", this.getExp());

        nbt.putInt("fire_protection", this.getFireProtection());
        nbt.putInt("fall_protection", this.getFallProtection());
        nbt.putInt("blast_protection", this.getBlastProtection());
        nbt.putInt("projectile_protection", this.getProjectileProtection());

        dropItem.setTag(nbt);
        ItemEntity itemEntity = new ItemEntity(this.level, this.getX(), this.getY() + (double)0.0F, this.getZ(), dropItem);
        itemEntity.setDefaultPickUpDelay();
        this.level.addFreshEntity(itemEntity);
    } // dropEquipment ()

    @Nullable @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {
        return null;
    } // getBreedOffspring ()

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
    public void onEnterCombat() {
        handleActivateCombatMode();
        super.onEnterCombat();
    } // onEnterCombat ()

    // -- Interact Methods --
    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(hand == InteractionHand.MAIN_HAND) {
            if(getOwner() != null) handleSit(stack);
            if(this.level.isClientSide) {
                boolean bl = this.isOwnedBy(player) || this.isTame() || stack.getItem() == (Items.BONE) && !this.isTame();
                return bl ? InteractionResult.CONSUME : InteractionResult.PASS;
            } else {
                if(getOwner() == null) handleTame(player);
                if(getOwner() != null) {
                    handleState(stack);
                    handleAutoAttack(stack);
                    handleTexture(stack, player);
                    handleDisplayInteraction(stack);
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
        return !itemStack.is(Items.COMPASS) && !itemStack.is(Items.RECOVERY_COMPASS);
    } // canInteract ()

    public static boolean canInteractGuardMode(ItemStack itemStack){
        return itemStack.is(Items.COMPASS) || itemStack.is(Items.RECOVERY_COMPASS);
    } // canInteractGuardMode ()

    public static boolean canInteractAutoAttack(ItemStack itemStack) {
        return itemStack.is(Items.WOODEN_SWORD) || itemStack.is(Items.STONE_SWORD) || itemStack.is(Items.IRON_SWORD) || itemStack.is(Items.GOLDEN_SWORD) || itemStack.is(Items.DIAMOND_SWORD) || itemStack.is(Items.NETHERITE_SWORD);
    } // canInteractAutoAttack ()

    // -- Custom Methods --
    public abstract ItemStack setDropItem();

    // -- Logic Methods --
    protected void handleAutoHeal () {
        if(this.getHealth() < this.getHp()) autoHeal = true;
        if(this.level.isClientSide && !autoHeal) return;

        if(autoHealTimer != 0) {
            autoHealTimer--;
        } else {
            final float healValue = this.getHp() / 16.0F;
            this.heal(healValue);
            autoHeal = false;
            autoHealTimer = InternalMetric.HEAL_INTERVAL.get();
        }
    } // handleAutoHeal ()

    protected void handleActivateCombatMode () {
        if(!combatMode) combatMode = true;
        waryTimer = InternalMetric.WARY_TIME.get();
    } // handleActivateCombatMode ()

    protected void handleCombatMode() {
        if(this.level.isClientSide && !combatMode) return;
        if(waryTimer != 0) {
            if(this.model != EntityModel.Armed) this.model = EntityModel.Armed;
            waryTimer--;
        } else {
            if(this.model != EntityModel.Default) this.model = EntityModel.Default;
            combatMode = false;
        }
    } // handleCombatMode ()

    public void handleTame(Player player) {
        this.setOwnerUUID(player.getUUID());
        this.setTame(true);
        InternalParticle.Heart(this);
        //player.displayClientMessage(Component.translatable("msg.rlovelyr.owner").append(": " + getOwner().getName().getString()), true);
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

    public void handleSit(ItemStack stack) {
        if(!canInteract(stack)) return;
        setOrderedToSit(invertBoolean(isOrderedToSit()));
    } // handleSit ()

    public void handleAutoAttack(ItemStack stack){
        if (!canInteractAutoAttack(stack)) return;
        setAutoAttack(invertBoolean(getAutoAttack()));
        /*String msgAutoAttack;
        if(this.getAutoAttack()) msgAutoAttack = "msg.rlovelyr.on";
        else msgAutoAttack = "msg.rlovelyr.off";
        player.displayClientMessage(Component.translatable("msg.rlovelyr.auto_attack").append(Component.literal(": ").append(Component.translatable(msgAutoAttack))), true);*/
    } // handleAutoAttack ()

    public void handleState(ItemStack stack) {
        //var previousState = getCurrentState();
        StandbyState(stack);
        FollowState(stack);
        BaseDefenseState(stack);
       // if(previousState != getCurrentState()) player.displayClientMessage(Component.translatable("msg.rlovelyr.state").append(Component.literal(": ").append(Component.translatable(Utility.getTranslatable(this.getCurrentState())))), true);
    } // handleState

    protected void handleDisplayInteraction (ItemStack stack) {
        if(stack.getItem() == (Items.OAK_BUTTON)) {
            this.setNotification(Utility.invertBoolean(getNotification()));
            if(getNotification()) InternalLogic.commandDebug(this, Component.translatable("msg.rlovelyr.notification").append(" ").append(Component.translatable("msg.rlovelyr.on")), true);
            else InternalLogic.commandDebug(this, Component.translatable("msg.rlovelyr.notification").append(" ").append(Component.translatable("msg.rlovelyr.off")), true);
        }

        if(stack.getItem() == (Items.BOOK)) displayGeneralMessage(true, false);
        if(stack.getItem() == (Items.WRITABLE_BOOK)) displayEnchantmentMessage();
    } // handleDisplayInteraction ()

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
    public void displayExtra() {
        MutableComponent debug = null;
        if(combatMode && getNotification()) {
            debug = Component.translatable(Utility.getTranslatable(this.variant)).append(Component.literal(": ").append(Component.translatable("msg.rlovelyr.wary")));
            if(waryTimer < 10) debug = debug.append(": 0" + waryTimer + " ");
            else debug = debug.append(": " + waryTimer + " ");
        }

        if(autoHeal && getNotification()) {
            if(debug != null) debug = debug.append(Component.translatable("msg.rlovelyr.heal"));
            else debug = Component.translatable(Utility.getTranslatable(this.variant)).append(Component.literal(": ").append(Component.translatable("msg.rlovelyr.heal")));

            if(autoHealTimer < 10) debug = debug.append(": 0" + autoHealTimer + " ");
            else debug = debug.append(": " + autoHealTimer + " ");
            if(this.getHealth() < 10) debug = debug.append("| 0" + this.getHealth());
            else debug = debug.append("| " + (int)Math.floor(this.getHealth()));
        }
        if(debug != null) InternalLogic.commandDebug(this, debug, true);
    } // displayExtra ()

    public void displayGeneralMessage (boolean canShow, boolean showLevelUp) {
        if(!canShow) return;
        InternalLogic.commandDebug(this, (Component.translatable("msg.rlovelyr.bar")), false);
        if(showLevelUp) InternalLogic.commandDebug(this, (Component.translatable("msg.rlovelyr.level_up")), false);
        if(this.getCustomName() != null) InternalLogic.commandDebug(this, Component.translatable(Utility.getTranslatable(this.variant)).append(": " + this.getCustomName().getString()), false);
        else InternalLogic.commandDebug(this, Component.translatable(Utility.getTranslatable(this.variant)), false);
        InternalLogic.commandDebug(this, Component.translatable("msg.rlovelyr.level").append(": " + this.getCurrentLevel()             + "/" + this.getMaxLevel()), false);
        InternalLogic.commandDebug(this, Component.translatable("msg.rlovelyr.experience").append(": " + this.getExp()                 + "/" + InternalLogic.getNextExp(this)), false);
        InternalLogic.commandDebug(this, Component.translatable("msg.rlovelyr.health").append(": " + (int)Math.floor(this.getHealth()) + "/" + (int)this.getMaxHealth()), false);
        InternalLogic.commandDebug(this, Component.translatable("msg.rlovelyr.attack").append(": " + this.getAttack()), false);
        InternalLogic.commandDebug(this, Component.translatable("msg.rlovelyr.defence").append(": " + this.getDefense()), false);
    } // displayGeneralMessage ()

    public void displayEnchantmentMessage () {
        InternalLogic.commandDebug(this, Component.translatable("msg.rlovelyr.bar"), false);
        InternalLogic.commandDebug(this, Component.translatable("msg.rlovelyr.enchantment"), false);
        InternalLogic.commandDebug(this, Component.translatable("msg.rlovelyr.looting").append(": " + this.getLooting()                             + "/" + InternalMetric.MAX_LOOT_ENCHANTMENT.get()), false);
        InternalLogic.commandDebug(this, Component.translatable("msg.rlovelyr.fire_protection").append(": " + this.getFireProtection()              + "/" + InternalMetric.PROTECTION_LIMIT_FIRE.get()), false);
        InternalLogic.commandDebug(this, Component.translatable("msg.rlovelyr.fall_protection").append(": " + this.getFallProtection()              + "/" + InternalMetric.PROTECTION_LIMIT_FALL.get()), false);
        InternalLogic.commandDebug(this, Component.translatable("msg.rlovelyr.blast_protection").append(": " + this.getBlastProtection()            + "/" + InternalMetric.PROTECTION_LIMIT_BLAST.get()), false);
        InternalLogic.commandDebug(this, Component.translatable("msg.rlovelyr.projectile_protection").append(": " + this.getProjectileProtection()  + "/" + InternalMetric.PROTECTION_LIMIT_PROJECTILE.get()), false);
    } // displayEnchantmentMessage ()

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

        this.entityData.define(NOTIFICATION, true);
    } // defineSynchedData ()

    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
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

    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
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