package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.*;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.entity.enums.*;
import org.jetbrains.annotations.Nullable;

import static net.msymbios.rlovelyr.entity.internal.Utility.invertBoolean;
import static net.msymbios.rlovelyr.item.LovelyRobotItems.ROBOT_CORE;

public abstract class InternalEntity extends TameableEntity {

    // -- Variables --
    protected static final TrackedData<Integer> TEXTURE_ID = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> STATE = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.INTEGER);
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

    protected static final TrackedData<Boolean> NOTIFICATION = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    protected int waryTimer = 0, autoHealTimer = 0;
    protected boolean combatMode = false, autoHeal = false;
    protected EntityVariant variant;
    protected EntityModel model = EntityModel.Default;

    // -- Properties --

    // TEXTURE
    public Identifier getTexture() { return InternalMetric.getTexture(this.variant, EntityTexture.byId(getTextureID())); } // getTexture ()

    public int getTextureID() {
        int value = InternalMetric.getRandomTextureID(this.variant);
        try {value = this.dataTracker.get(TEXTURE_ID);}
        catch (Exception ignored) {}
        return value;
    } // getTextureID ()

    public void setTexture(int value) { if(InternalMetric.checkTextureID(this.variant, EntityTexture.byId(value))) this.dataTracker.set(TEXTURE_ID, value); } // setTexture ()

    public void setTexture(EntityTexture value) { setTexture(value.getId()); } // setTexture ()

    // MODEL
    public Identifier getCurrentModel() { return InternalMetric.getModel(this.variant, model); } // getCurrentModel ()

    // ANIMATOR
    public Identifier getAnimator() { return InternalMetric.getAnimator(this.variant); } // getAnimator ()

    // STATE
    public int getCurrentStateID() {
        int value = EntityState.Standby.getId();
        try {value = this.dataTracker.get(STATE);}
        catch (Exception ignored) {}
        return value;
    } // getCurrentStateID ()

    public EntityState getCurrentState() {
        EntityState value = EntityState.Standby;
        try {value = EntityState.byId(this.dataTracker.get(STATE));}
        catch (Exception ignored) {}
        return value;
    } // getCurrentState ()

    public void setCurrentState(EntityState value){
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
    public int getAttribute(EntityAttribute attribute) { return (int) InternalMetric.getAttribute(this.variant, attribute); } // getAttribute ()

    public int getMaxLevel() { return getMaxLevel (getAttribute(EntityAttribute.MAX_LEVEL)); } // getMaxLevel ()

    public int getMaxLevel(int value){
        int oldValue = value;
        try {value = this.dataTracker.get(MAX_LEVEL);}
        catch (Exception ignored) {}
        if(value != oldValue) setMaxLevel(oldValue);
        return value;
    } // getMaxLevel ()

    public void setMaxLevel(int value) {
        this.dataTracker.set(MAX_LEVEL, value);
    } // setMaxLevel ()

    public int getCurrentLevel() {
        int level = getAttribute(EntityAttribute.MAX_LEVEL);
        if(level != getMaxLevel()) setMaxLevel(level);
        try {level = this.dataTracker.get(LEVEL);}
        catch (Exception ignored){}
        return level;
    } // getCurrentLevel ()

    public void setCurrentLevel(int value){
        this.dataTracker.set(LEVEL, value);
        InternalLogic.handleSetLevel(this);
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

    public int getHp() { return InternalLogic.getHpValue(this, getAttribute(EntityAttribute.MAX_HEALTH)); } // getHp ()

    public int getAttack() { return InternalLogic.getAttackValue(this, getAttribute(EntityAttribute.ATTACK_DAMAGE)); } // getAttack ()

    public int getDefense() { return InternalLogic.getDefenseValue(this, getAttribute(EntityAttribute.DEFENSE)); } // getDefenseValue ()

    public int getLooting() {
        return InternalLogic.getLootingLevel(this);
    } // getLooting ()

    public double getArmorLevel() {
        return InternalLogic.getArmorValue(this);
    } // getArmorLevel ()

    public double getArmorToughnessLevel() {
        return InternalLogic.getArmorToughnessValue(this);
    } // getArmorToughnessLevel ()

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
        float value = this.getBlockPos().getX();
        try {value = this.dataTracker.get(BASE_X);}
        catch (Exception ignored) {}
        return value;
    } // getBaseX ()

    public void setBaseX(float value) {
        this.dataTracker.set(BASE_X, value);
    } // setBaseX ()

    public float getBaseY() {
        float value = this.getBlockPos().getY();
        try {value = this.dataTracker.get(BASE_Y);}
        catch (Exception ignored) {}
        return value;
    } // getBaseY ()

    public void setBaseY(float value) {
        this.dataTracker.set(BASE_Y, value);
    } // setBaseY ()

    public float getBaseZ() {
        float value = this.getBlockPos().getZ();
        try {value = this.dataTracker.get(BASE_Z);}
        catch (Exception ignored) {}
        return value;
    } // getBaseZ ()

    public void setBaseZ(float value) {
        this.dataTracker.set(BASE_Z, value);
    } // setBaseZ ()

    // INFO
    public boolean getNotification() {
        boolean value = true;
        try {value = this.dataTracker.get(NOTIFICATION);}
        catch (Exception ignored) {}
        return value;
    } // getNotification ()

    public void setNotification(boolean value) {
        this.dataTracker.set(NOTIFICATION, value);
    } // setNotification ()

    // -- Construct --
    protected InternalEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    } // Construct InternalEntity

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
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityNbt) {
        EquipmentSlot slot = EquipmentSlot.MAINHAND;
        ItemStack diamondSword = new ItemStack(Items.DIAMOND_SWORD);
        this.equipStack(slot, diamondSword);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    } // initialize ()

    @Override
    public void tick() {
        super.tick();
        handleCombatMode();
        handleAutoHeal();
    } // tick ()

    @Override
    public void onAttacking(Entity target) {
        handleActivateCombatMode();
        if(this.canLevelUp() && !(target instanceof PlayerEntity) && target != null && !this.world.isClient) {
            final int maxHp = (int)((LivingEntity)target).getMaxHealth();
            InternalLogic.addExp(this, maxHp / 4);
        }
        this.world.sendEntityStatus(this, (byte)4);
        super.onAttacking(target);
    } // onAttacking ()

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) return false;
        handleActivateCombatMode();

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
            if(source.isFire() && InternalLogic.canLevelUpFireProtection(this)) this.setFireProtection(this.getFireProtection() + 1);
            if(source == DamageSource.FALL && InternalLogic.canLevelUpFallProtection(this)) this.setFallProtection(this.getFallProtection() + 1);
            if(source.isExplosive() && InternalLogic.canLevelUpBlastProtection(this)) this.setBlastProtection(this.getBlastProtection() + 1);
            if(source.isProjectile() && InternalLogic.canLevelUpProjectileProtection(this)) this.setProjectileProtection(this.getProjectileProtection() + 1);
        }

        final Entity entity = source.getSource();

        if (this.canLevelUp() && !(entity instanceof PlayerEntity) && entity instanceof LivingEntity && !this.world.isClient) {
            final int maxHp = (int)((LivingEntity)entity).getMaxHealth();
            InternalLogic.addExp(this, maxHp / 6);
        }

        if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof ArrowEntity)) {
            amount = (amount + 1.0f) / 2.0f;
        }

        return super.damage(source, amount);
    } // damage ()

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        final ItemStack dropItem = new ItemStack(ROBOT_CORE, 1);
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
        this.dropStack(dropItem, 0.0f);
    } // dropEquipment ()

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        switch (slot.getType()){
            case HAND: {
                final ItemStack tempSword = new ItemStack(Items.DIAMOND_SWORD,1);
                final int lootingLevel = this.getLooting();
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

    @Nullable @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    } // createChild

    // -- Interact Methods --
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if(hand == Hand.MAIN_HAND) {
            if(getOwner() != null) handleSit(stack);
            if(this.world.isClient) {
                boolean bl = this.isOwner(player) || this.isTamed() || stack.getItem() == (Items.BONE) && !this.isTamed();
                return bl ? ActionResult.CONSUME : ActionResult.PASS;
            } else {
                if(getOwner() == null) handleTame(player);
                if(getOwner() != null) {
                    handleState(stack);
                    handleAutoAttack(stack);
                    handleTexture(stack, player);
                    handleDisplayInteraction(stack);
                }
                return ActionResult.SUCCESS;
            }
        }
        return super.interactMob(player, hand);
    } // interactMob ()

    public boolean canInteract(ItemStack itemStack){
        if(itemStack.getItem() == (Items.WHITE_DYE) ||itemStack.getItem() == (Items.ORANGE_DYE) || itemStack.getItem() == (Items.MAGENTA_DYE) || itemStack.getItem() == (Items.LIGHT_BLUE_DYE) ||
                itemStack.getItem() == (Items.YELLOW_DYE) || itemStack.getItem() == (Items.LIME_DYE) || itemStack.getItem() == (Items.PINK_DYE) || itemStack.getItem() == (Items.GRAY_DYE) ||
                itemStack.getItem() == (Items.LIGHT_GRAY_DYE) || itemStack.getItem() == (Items.CYAN_DYE) || itemStack.getItem() == (Items.PURPLE_DYE) || itemStack.getItem() == (Items.BLUE_DYE) ||
                itemStack.getItem() == (Items.BROWN_DYE) || itemStack.getItem() == (Items.GREEN_DYE) || itemStack.getItem() == (Items.RED_DYE) || itemStack.getItem() == (Items.BLACK_DYE)) return false;
        if(itemStack.getItem() == (Items.WOODEN_SWORD) || itemStack.getItem() == (Items.STONE_SWORD) || itemStack.getItem() == (Items.IRON_SWORD) || itemStack.getItem() == (Items.GOLDEN_SWORD) || itemStack.getItem() == (Items.DIAMOND_SWORD) || itemStack.getItem() == (Items.NETHERITE_SWORD)) return false;
        if(itemStack.getItem() == (Items.STICK) || itemStack.getItem() == (Items.BOOK) || itemStack.getItem() == (Items.WRITABLE_BOOK) || itemStack.getItem() == (Items.OAK_BUTTON)) return false;
        return itemStack.getItem() != (Items.COMPASS);
    } // canInteract ()

    public boolean canInteractGuardMode(ItemStack itemStack){
        return itemStack.getItem() == (Items.COMPASS);
    } // canInteractGuardMode ()

    public boolean canInteractAutoAttack(ItemStack itemStack) {
        return itemStack.getItem() == (Items.WOODEN_SWORD) || itemStack.getItem() == (Items.STONE_SWORD) || itemStack.getItem() == (Items.IRON_SWORD) || itemStack.getItem() == (Items.GOLDEN_SWORD) || itemStack.getItem() == (Items.DIAMOND_SWORD) || itemStack.getItem() == (Items.NETHERITE_SWORD);
    } // canInteractAutoAttack ()

    // -- Custom Methods --
    protected boolean canLevelUp() {
        return this.getCurrentLevel() < getMaxLevel();
    } // canLevelUp ()

    // -- Logic Methods --
    protected void handleAutoHeal () {
        if(this.getHealth() < this.getHp()) autoHeal = true;
        if(this.world.isClient && !autoHeal) return;

        if(autoHealTimer != 0) {
            autoHealTimer--;
        } else {
            final float healValue = this.getHp() / 16.0F;
            this.heal(healValue);
            autoHeal = false;
            autoHealTimer = InternalMetric.HEAL_INTERVAL;
        }
    } // handleAutoHeal ()

    protected void handleActivateCombatMode () {
        if(!combatMode) combatMode = true;
        waryTimer = InternalMetric.WARY_TIME;
    } // handleActivateCombatMode ()

    protected void handleCombatMode() {
        if(this.isAttacking()) handleActivateCombatMode();
        if(this.world.isClient && !combatMode) return;

        if(waryTimer != 0) {
            if(this.model != EntityModel.Armed) this.model = EntityModel.Armed;
            waryTimer--;
        } else {
            if(this.model != EntityModel.Default) this.model = EntityModel.Default;
            combatMode = false;
        }
    } // handleCombatMode ()

    public void handleTame(PlayerEntity player) {
        this.setOwner(player);
        this.setTamed(true);
        InternalParticle.Heart(this);
        this.world.sendEntityStatus(this, (byte) 4);
    } // handleTame ()

    public void handleTexture(ItemStack items, PlayerEntity player) {
        int oldTexture = getTextureID();
        if(items.getItem() == (Items.WHITE_DYE)) setTexture(EntityTexture.WHITE);
        if(items.getItem() == (Items.ORANGE_DYE)) setTexture(EntityTexture.ORANGE);
        if(items.getItem() == (Items.MAGENTA_DYE)) setTexture(EntityTexture.MAGENTA);
        if(items.getItem() == (Items.LIGHT_BLUE_DYE)) setTexture(EntityTexture.LIGHT_BLUE);
        if(items.getItem() == (Items.YELLOW_DYE)) setTexture(EntityTexture.YELLOW);
        if(items.getItem() == (Items.LIME_DYE)) setTexture(EntityTexture.LIME);
        if(items.getItem() == (Items.PINK_DYE)) setTexture(EntityTexture.PINK);
        if(items.getItem() == (Items.GRAY_DYE)) setTexture(EntityTexture.GRAY);
        if(items.getItem() == (Items.LIGHT_GRAY_DYE)) setTexture(EntityTexture.LIGHT_GRAY);
        if(items.getItem() == (Items.CYAN_DYE)) setTexture(EntityTexture.CYAN);
        if(items.getItem() == (Items.PURPLE_DYE)) setTexture(EntityTexture.PURPLE);
        if(items.getItem() == (Items.BLUE_DYE)) setTexture(EntityTexture.BLUE);
        if(items.getItem() == (Items.BROWN_DYE)) setTexture(EntityTexture.BROWN);
        if(items.getItem() == (Items.GREEN_DYE)) setTexture(EntityTexture.GREEN);
        if(items.getItem() == (Items.RED_DYE)) setTexture(EntityTexture.RED);
        if(items.getItem() == (Items.BLACK_DYE)) setTexture(EntityTexture.BLACK);

        if(oldTexture != getTextureID()) {
            if (!player.abilities.creativeMode) items.decrement(1);
        }
    } // handleTexture ()

    public void handleSit(ItemStack itemStack) {
        if(!canInteract(itemStack)) return;
        setSitting(invertBoolean(isSitting()));
    } // handleSit ()

    public void handleAutoAttack(ItemStack itemStack){
        if (!canInteractAutoAttack(itemStack)) return;
        setAutoAttack(invertBoolean(getAutoAttack()));
    } // handleAutoAttack ()

    public void handleState(ItemStack itemStack) {
        StandbyState(itemStack);
        FollowState(itemStack);
        BaseDefenseState(itemStack);
    } // handleState

    protected void handleDisplayInteraction (ItemStack stack) {
        if(stack.getItem() == (Items.OAK_BUTTON)) {
            this.setNotification(Utility.invertBoolean(getNotification()));
            if(getNotification()) InternalLogic.commandDebug(this, new TranslatableText("msg.rlovelyr.notification").append(" ").append(new TranslatableText("msg.rlovelyr.on")), true);
            else InternalLogic.commandDebug(this, new TranslatableText("msg.rlovelyr.notification").append(" ").append(new TranslatableText("msg.rlovelyr.off")), true);
        }

        if(stack.getItem() == (Items.BOOK)) displayGeneralMessage(true, false);
        if(stack.getItem() == (Items.WRITABLE_BOOK)) displayEnchantmentMessage();
    } // handleDisplayInteraction ()

    public void StandbyState(ItemStack itemStack){
        if(!canInteract(itemStack)) return;
        if(isSitting()) setCurrentState(EntityState.Standby);
    } // StandbyState ()

    public void FollowState(ItemStack itemStack){
        if(!canInteract(itemStack)) return;
        if(!isSitting()) setCurrentState(EntityState.Follow);
    } // FollowState ()

    public void BaseDefenseState(ItemStack itemStack){
        if(!canInteractGuardMode(itemStack)) return;
        setSitting(false);
        setAutoAttack(true);

        Vec3d currentPosition = this.getPos();
        this.setBaseX((float)currentPosition.x);
        this.setBaseY((float)currentPosition.y);
        this.setBaseZ((float)currentPosition.z);
        setCurrentState(EntityState.BaseDefense);
    } // BaseDefenseState ()

    // -- Debug Methods --
    public void displayExtra() {
        MutableText debug = null;
        if(combatMode && getNotification()) {
            debug = new TranslatableText(Utility.getTranslatable(this.variant)).append(Text.of(": ")).append(new TranslatableText("msg.rlovelyr.wary"));
            if(waryTimer < 10) debug = debug.append(": 0" + waryTimer + " ");
            else debug = debug.append(": " + waryTimer + " ");
        }

        if(autoHeal && getNotification()) {
            if(debug != null) debug = debug.append(new TranslatableText("msg.rlovelyr.heal"));
            else debug = new TranslatableText(Utility.getTranslatable(this.variant)).append(Text.of(": ")).append(new TranslatableText("msg.rlovelyr.heal"));

            if(autoHealTimer < 10) debug = debug.append(": 0" + autoHealTimer + " ");
            else debug = debug.append(": " + autoHealTimer + " ");
            if(this.getHealth() < 10) debug = debug.append("| 0" + this.getHealth());
            else debug = debug.append("| " + (int)Math.floor(this.getHealth()));
        }
        if(debug != null) InternalLogic.commandDebug(this, debug, true);
    } // displayExtra ()

    public void displayGeneralMessage(boolean canShow, boolean showLevelUp) {
        if(!canShow) return;
        InternalLogic.commandDebug(this, (new TranslatableText("msg.rlovelyr.bar")), false);
        if(showLevelUp) InternalLogic.commandDebug(this, (new TranslatableText("msg.rlovelyr.level_up")), false);
        if(this.getCustomName() != null) InternalLogic.commandDebug(this, new TranslatableText(Utility.getTranslatable(this.variant)).append(": " + this.getCustomName().getString()), false);
        else InternalLogic.commandDebug(this, new TranslatableText(Utility.getTranslatable(this.variant)), false);
        InternalLogic.commandDebug(this, new TranslatableText("msg.rlovelyr.level").append(": " + this.getCurrentLevel()             + "/" + this.getMaxLevel()), false);
        InternalLogic.commandDebug(this, new TranslatableText("msg.rlovelyr.experience").append(": " + this.getExp()                 + "/" + InternalLogic.getNextExp(this)), false);
        InternalLogic.commandDebug(this, new TranslatableText("msg.rlovelyr.health").append(": " + (int)Math.floor(this.getHealth()) + "/" + (int)this.getMaxHealth()), false);
        InternalLogic.commandDebug(this, new TranslatableText("msg.rlovelyr.attack").append(": " + this.getAttack()), false);
        InternalLogic.commandDebug(this, new TranslatableText("msg.rlovelyr.defence").append(": " + this.getDefense()), false);
    } // displayGeneralMessage ()

    public void displayEnchantmentMessage() {
        InternalLogic.commandDebug(this, new TranslatableText("msg.rlovelyr.bar"), false);
        InternalLogic.commandDebug(this, new TranslatableText("msg.rlovelyr.enchantment"), false);
        InternalLogic.commandDebug(this, new TranslatableText("msg.rlovelyr.looting").append(": " + this.getLooting()                       + "/" + InternalMetric.MAX_LOOT_ENCHANTMENT), false);
        InternalLogic.commandDebug(this, new TranslatableText("msg.rlovelyr.fire_protection").append(": " + this.getFireProtection()             + "/" + InternalMetric.PROTECTION_LIMIT_FIRE), false);
        InternalLogic.commandDebug(this, new TranslatableText("msg.rlovelyr.fall_protection").append(": " + this.getFallProtection()             + "/" + InternalMetric.PROTECTION_LIMIT_FALL), false);
        InternalLogic.commandDebug(this, new TranslatableText("msg.rlovelyr.blast_protection").append(": " + this.getBlastProtection()           + "/" + InternalMetric.PROTECTION_LIMIT_BLAST), false);
        InternalLogic.commandDebug(this, new TranslatableText("msg.rlovelyr.projectile_protection").append(": " + this.getProjectileProtection() + "/" + InternalMetric.PROTECTION_LIMIT_PROJECTILE), false);
    } // displayEnchantmentMessage ()

    // -- Save Methods --
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TEXTURE_ID, EntityTexture.PINK.getId());
        this.dataTracker.startTracking(STATE, EntityState.Follow.getId());
        this.dataTracker.startTracking(AUTO_ATTACK, true);

        this.dataTracker.startTracking(MAX_LEVEL, 200);
        this.dataTracker.startTracking(LEVEL, 0);
        this.dataTracker.startTracking(EXP, 0);

        this.dataTracker.startTracking(FIRE_PROTECTION, 0);
        this.dataTracker.startTracking(FALL_PROTECTION, 0);
        this.dataTracker.startTracking(BLAST_PROTECTION, 0);
        this.dataTracker.startTracking(PROJECTILE_PROTECTION, 0);

        this.dataTracker.startTracking(BASE_X, 0F);
        this.dataTracker.startTracking(BASE_Y, 0F);
        this.dataTracker.startTracking(BASE_Z, 0F);

        this.dataTracker.startTracking(NOTIFICATION, true);
    } // initDataTracker ()

    @Override
    public void writeCustomDataToTag(CompoundTag dataNBT) {
        super.writeCustomDataToTag(dataNBT);
        dataNBT.putInt("TextureID", this.getTextureID());
        dataNBT.putInt("State", this.getCurrentStateID());
        dataNBT.putBoolean("AutoAttack", this.getAutoAttack());

        dataNBT.putInt("MaxLevel", this.getMaxLevel());
        dataNBT.putInt("Level", this.getCurrentLevel());
        dataNBT.putInt("Exp", this.getExp());

        dataNBT.putInt("FireProtection", this.getFireProtection());
        dataNBT.putInt("FallProtection", this.getFallProtection());
        dataNBT.putInt("BlastProtection", this.getBlastProtection());
        dataNBT.putInt("ProjectileProtection", this.getProjectileProtection());

        dataNBT.putFloat("BaseX", this.getBaseX());
        dataNBT.putFloat("BaseY", this.getBaseY());
        dataNBT.putFloat("BaseZ", this.getBaseZ());

        dataNBT.putBoolean("Notification", this.getNotification());
    } // writeCustomDataToNbt ()

    @Override
    public void readCustomDataFromTag(CompoundTag dataNBT) {
        super.readCustomDataFromTag(dataNBT);
        this.setTexture(dataNBT.getInt("TextureID"));
        this.setCurrentState(dataNBT.getInt("State"));
        this.setAutoAttack(dataNBT.getBoolean("AutoAttack"));

        this.setMaxLevel(dataNBT.getInt("MaxLevel"));
        this.setCurrentLevel(dataNBT.getInt("Level"));
        this.setExp(dataNBT.getInt("Exp"));

        this.setFireProtection(dataNBT.getInt("FireProtection"));
        this.setFallProtection(dataNBT.getInt("FallProtection"));
        this.setBlastProtection(dataNBT.getInt("BlastProtection"));
        this.setProjectileProtection(dataNBT.getInt("ProjectileProtection"));

        this.setBaseY(dataNBT.getFloat("BaseY"));
        this.setBaseZ(dataNBT.getFloat("BaseZ"));
        this.setBaseX(dataNBT.getFloat("BaseX"));

        this.setNotification(dataNBT.getBoolean("Notification"));
    } // readCustomDataFromNbt ()

} // Class InternalEntity