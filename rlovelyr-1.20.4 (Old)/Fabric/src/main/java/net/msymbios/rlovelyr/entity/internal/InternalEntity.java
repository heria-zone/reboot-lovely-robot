package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.enums.*;
import net.msymbios.rlovelyr.util.internal.Utility;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.msymbios.rlovelyr.item.LovelyRobotItems.ROBOT_CORE;
import static net.msymbios.rlovelyr.util.internal.Utility.invertBoolean;

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

    public final ItemStack[] handItemsForAnimation = new ItemStack[]{ItemStack.EMPTY, ItemStack.EMPTY};
    protected int waryTimer = 0, autoHealTimer = 0, sitPoseChangeTimer = 0;
    protected boolean combatMode = false, autoHeal = false;
    public EntityVariant variant;
    protected EntityModel model = EntityModel.Default;

    // -- Properties --

    // MISC
    public int getSitPoseChangeTimer() { return sitPoseChangeTimer; }

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
        var oldValue = value;
        try {value = this.dataTracker.get(MAX_LEVEL);}
        catch (Exception ignored) {}
        if(value != oldValue) setMaxLevel(oldValue);
        return value;
    } // getMaxLevel ()

    public void setMaxLevel(int value) {
        this.dataTracker.set(MAX_LEVEL, value);
    } // setMaxLevel ()

    public int getCurrentLevel() {
        var level = getAttribute(EntityAttribute.MAX_LEVEL);
        if(level != getMaxLevel()) setMaxLevel(level);
        try {level = this.dataTracker.get(LEVEL);}
        catch (Exception ignored){}
        return level;
    } // getCurrentLevel ()

    public void setCurrentLevel(int value){
        this.dataTracker.set(LEVEL, value);
        InternalLogic.handleLevel(this, getHp(), getAttack(), getArmorLevel(), getArmorToughnessLevel());
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

    public int getHp() { return InternalLogic.calculateHp(this.getCurrentLevel(), getAttribute(EntityAttribute.MAX_HEALTH)); } // getHp ()

    public int getAttack() { return InternalLogic.calculateAttack(this.getCurrentLevel(), getAttribute(EntityAttribute.ATTACK_DAMAGE)); } // getAttack ()

    public int getDefense() { return InternalLogic.calculateDefense(this.getCurrentLevel(), getAttribute(EntityAttribute.DEFENSE)); } // getDefenseValue ()

    public int getLooting() {
        return InternalLogic.calculateLooting(this.getCurrentLevel());
    } // getLooting ()

    public double getArmorLevel() {
        return InternalLogic.calculateArmor(this.getDefense());
    } // getArmorLevel ()

    public double getArmorToughnessLevel() {
        return InternalLogic.calculateArmorToughness(this.getArmorLevel());
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

    // -- Constructor --
    protected InternalEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    } // Constructor InternalEntity ()

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
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
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
        handleSitPoseChange();
    } // tick ()

    @Override
    public void onAttacking(Entity target) {
        handleActivateCombatMode();
        if(InternalLogic.handleLevelUp(this.getCurrentLevel(), this.getMaxLevel()) && !(target instanceof PlayerEntity) && target != null && !this.getWorld().isClient) {
            final int maxHp = (int)((LivingEntity)target).getMaxHealth();
            addExp(maxHp / 4);
        }
        this.getWorld().sendEntityStatus(this, (byte)4);
        super.onAttacking(target);
    } // onAttacking ()

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) return false;
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
            if((source.isOf(DamageTypes.ON_FIRE) || source.isOf(DamageTypes.IN_FIRE) || source.isOf(DamageTypes.LAVA)) && InternalLogic.handleFireProtectionLevelUp(this.getFireProtection())) this.setFireProtection(this.getFireProtection() + 1);
            if(source.isOf(DamageTypes.FALL) && InternalLogic.handleFallProtectionLevelUp(this.getFallProtection())) this.setFallProtection(this.getFallProtection() + 1);
            if(source.isOf(DamageTypes.EXPLOSION) && InternalLogic.handleBlastProtectionLevelUp(this.getBlastProtection())) this.setBlastProtection(this.getBlastProtection() + 1);
            if(source.isOf(DamageTypes.ARROW) && InternalLogic.handleProjectileProtectionLevelUp(this.getProjectileProtection())) this.setProjectileProtection(this.getProjectileProtection() + 1);
        }

        final Entity entity = source.getSource();

        if (InternalLogic.handleLevelUp(this.getCurrentLevel(), this.getMaxLevel()) && !(entity instanceof PlayerEntity) && entity instanceof LivingEntity && !this.getWorld().isClient) {
            final int maxHp = (int)((LivingEntity)entity).getMaxHealth();
            addExp(maxHp / 6);
        }

        if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof ArrowEntity)) {
            amount = (amount + 1.0f) / 2.0f;
        }

        return super.damage(source, amount);
    } // damage ()

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        final ItemStack dropItem = new ItemStack(ROBOT_CORE, 1);
        NbtCompound nbt = dropItem.getNbt();
        if(nbt == null) nbt = new NbtCompound();

        String customName = Utility.getEntityCustomName(this);
        if (!customName.isEmpty()) nbt.putString(LovelyRobotID.STAT_CUSTOM_NAME, customName);

        nbt.putString(LovelyRobotID.STAT_TYPE, Utility.getTranslation(this.variant));
        nbt.putInt(LovelyRobotID.STAT_COLOR, this.getTextureID());

        nbt.putInt(LovelyRobotID.STAT_MAX_LEVEL, this.getMaxLevel());
        nbt.putInt(LovelyRobotID.STAT_LEVEL, this.getCurrentLevel());
        nbt.putInt(LovelyRobotID.STAT_EXP, this.getExp());

        nbt.putInt(LovelyRobotID.STAT_FIRE_PROTECTION, this.getFireProtection());
        nbt.putInt(LovelyRobotID.STAT_FALL_PROTECTION, this.getFallProtection());
        nbt.putInt(LovelyRobotID.STAT_BLAST_PROTECTION, this.getBlastProtection());
        nbt.putInt(LovelyRobotID.STAT_PROJECTILE_PROTECTION, this.getProjectileProtection());

        dropItem.setNbt(nbt);
        this.dropStack(dropItem, 0.0f);
    } // dropEquipment ()

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

    @Nullable @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    } // createChild

    // -- Interact Methods --
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        var stack = player.getStackInHand(hand);
        Item item = stack.getItem();

        if (this.getWorld().isClient) {
            boolean bl = this.isOwner(player) || this.isTamed() || item instanceof DyeItem && !this.isTamed();
            return bl ? ActionResult.CONSUME : ActionResult.PASS;
        } else {
            label90: {
                if (this.isTamed()) {
                    if (!this.isOwner(player)) break label90;
                    if (!(item instanceof DyeItem)) break label90;
                    if(handleTexture(stack, player)) return ActionResult.SUCCESS;
                }
                return super.interactMob(player, hand);
            }

            ActionResult actionResult = super.interactMob(player, hand);
            if ((!actionResult.isAccepted() || this.isBaby()) && this.isOwner(player)) {
                handleSit(stack);
                handleState(stack);
                handleAutoAttack(stack);
                handleDisplayInteraction(stack);

                handleInteract(player);
                return ActionResult.SUCCESS;
            } else {
                return actionResult;
            }
        }
    } // interactMob ()

    private boolean canInteract(ItemStack stack){
        if(stack.isOf(Items.WHITE_DYE) ||stack.isOf(Items.ORANGE_DYE) || stack.isOf(Items.MAGENTA_DYE) || stack.isOf(Items.LIGHT_BLUE_DYE) ||
                stack.isOf(Items.YELLOW_DYE) || stack.isOf(Items.LIME_DYE) || stack.isOf(Items.PINK_DYE) || stack.isOf(Items.GRAY_DYE) ||
                stack.isOf(Items.LIGHT_GRAY_DYE) || stack.isOf(Items.CYAN_DYE) || stack.isOf(Items.PURPLE_DYE) || stack.isOf(Items.BLUE_DYE) ||
                stack.isOf(Items.BROWN_DYE) || stack.isOf(Items.GREEN_DYE) || stack.isOf(Items.RED_DYE) || stack.isOf(Items.BLACK_DYE)) return false;
        if(stack.isOf(Items.WOODEN_SWORD) || stack.isOf(Items.STONE_SWORD) || stack.isOf(Items.IRON_SWORD) || stack.isOf(Items.GOLDEN_SWORD) || stack.isOf(Items.DIAMOND_SWORD) || stack.isOf(Items.NETHERITE_SWORD)) return false;
        if(stack.isOf(Items.STICK) || stack.isOf(Items.BOOK) || stack.isOf(Items.WRITABLE_BOOK) || stack.isOf(Items.OAK_BUTTON)) return false;
        return !stack.isOf(Items.COMPASS) && !stack.isOf(Items.RECOVERY_COMPASS);
    } // canInteract ()

    private boolean canInteractGuardMode(ItemStack stack){
        return stack.isOf(Items.COMPASS) || stack.isOf(Items.RECOVERY_COMPASS);
    } // canInteractGuardMode ()

    private boolean canInteractAutoAttack(ItemStack stack) {
        return stack.isOf(Items.WOODEN_SWORD) || stack.isOf(Items.STONE_SWORD) || stack.isOf(Items.IRON_SWORD) || stack.isOf(Items.GOLDEN_SWORD) || stack.isOf(Items.DIAMOND_SWORD) || stack.isOf(Items.NETHERITE_SWORD);
    } // canInteractAutoAttack ()

    // -- Logic Methods --
    public void handleInteract (PlayerEntity player) {}

    public void addExp (int value) {
        int addExp = value;
        int exp = getExp();
        String customName = "";
        try {customName = getCustomName().getString();}
        catch (Exception ignored) {}

        // if they have a name they earn more exp
        if(!customName.isEmpty()) addExp = addExp * 3 / 2;
        exp += addExp;

        var oldLevel = getCurrentLevel();
        while (exp >= InternalLogic.calculateNextExp(getCurrentLevel())) {
            exp -= InternalLogic.calculateNextExp(getCurrentLevel());
            setCurrentLevel(getCurrentLevel() + 1);
            InternalParticle.LevelUp(this);
        }

        setExp(exp);
        if(oldLevel != getCurrentLevel()) {
            if(!getWorld().isClient) {
                try {
                    final LivingEntity owner = getOwner();
                    if (owner == null) return;
                    displayGeneralMessage(getNotification(), true);
                } catch (Exception ignored) {}
            }
        }
    } // addExp ()

    protected void handleAutoHeal () {
        if(this.getHealth() < this.getHp()) autoHeal = true;
        if(this.getWorld().isClient && !autoHeal) return;

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
        if(this.getWorld().isClient && !combatMode) return;

        if(waryTimer != 0) {
            if(this.model != EntityModel.Armed) this.model = EntityModel.Armed;
            waryTimer--;
        } else {
            if(this.model != EntityModel.Default) this.model = EntityModel.Default;
            combatMode = false;
        }
    } // handleCombatMode ()

    protected void handleSitPoseChange() {
        if(this.getCurrentState() == EntityState.Standby) {
            if(this.getWorld().isClient) return;
            if(sitPoseChangeTimer < 50) sitPoseChangeTimer++;
            //InternalLogic.commandDebug(this, Text.literal("Pose Change: " + sitPoseChangeTimer), true);
        }
    } // handleSitPoseChange ()

    public void handleTame(PlayerEntity player) {
        this.setOwner(player);
        this.setTamed(true);
        InternalParticle.Heart(this);
        InternalLogic.displayInfo(this, Text.translatable(LovelyRobotID.TRANS_MSG_OWNER).append(Text.literal(": " + player.getName().getString())), true);
    } // handleTame ()

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

    protected void handleSit(ItemStack stack) {
        if(!canInteract(stack)) return;
        setSitting(invertBoolean(isSitting()));
        this.jumping = false;
        this.navigation.stop();
        this.setTarget(null);

    } // handleSit ()

    protected void handleAutoAttack(ItemStack stack){
        if (!canInteractAutoAttack(stack)) return;
        setAutoAttack(invertBoolean(getAutoAttack()));

        if(getAutoAttack()) displayNotification(LovelyRobotID.TRANS_MSG_AUTO_ATTACK, LovelyRobotID.TRANS_MSG_ON, getNotification());
        else displayNotification(LovelyRobotID.TRANS_MSG_AUTO_ATTACK, LovelyRobotID.TRANS_MSG_OFF, getNotification());
    } // handleAutoAttack ()

    protected boolean handleDisplayInteraction (ItemStack stack) {
        if(stack.getItem() == (Items.OAK_BUTTON)) {
            this.setNotification(invertBoolean(getNotification()));
            if(getNotification()) displayNotification(LovelyRobotID.TRANS_MSG_NOTIFICATION, LovelyRobotID.TRANS_MSG_ON, getNotification());
            else displayNotification(LovelyRobotID.TRANS_MSG_NOTIFICATION, LovelyRobotID.TRANS_MSG_OFF, true);
        }

        if(stack.getItem() == (Items.BOOK)) displayGeneralMessage(true, false);
        if(stack.getItem() == (Items.WRITABLE_BOOK)) displayEnchantmentMessage();
        return true;
    } // handleDisplayInteraction ()

    protected void handleState(ItemStack stack) {
        handleStandbyState(stack);
        handleFollowState(stack);
        handleBaseDefenseState(stack);
    } // handleState

    protected void handleStandbyState(ItemStack stack){
        if(!canInteract(stack)) return;
        if(!isSitting()) return;

        sitPoseChangeTimer = 0;
        setCurrentState(EntityState.Standby);
        displayNotification(LovelyRobotID.TRANS_MSG_STANDBY, getNotification());
    } // handleStandbyState ()

    protected void handleFollowState(ItemStack stack){
        if(!canInteract(stack)) return;
        if(isSitting()) return;

        setCurrentState(EntityState.Follow);
        displayNotification(LovelyRobotID.TRANS_MSG_FOLLOW, getNotification());
    } // handleFollowState ()

    protected void handleBaseDefenseState(ItemStack stack){
        if(!canInteractGuardMode(stack)) return;
        setSitting(false);
        setAutoAttack(true);

        var currentPosition = this.getPos();
        this.setBaseX((float)currentPosition.x);
        this.setBaseY((float)currentPosition.y);
        this.setBaseZ((float)currentPosition.z);
        setCurrentState(EntityState.Defense);

        displayNotification(LovelyRobotID.TRANS_MSG_BASE_DEFENCE, getNotification());
    } // handleBaseDefenseState ()

    // -- Display --

    private void displayNotification(String notification, String message, boolean display) {
        if(!display) return;
        String customName = Utility.getEntityCustomName(this);
        if(!customName.isEmpty()) InternalLogic.displayInfo(this, Text.literal(customName + " | ").append(Text.translatable(notification).append(Text.literal(": ").append(Text.translatable(message)))), true);
        else InternalLogic.displayInfo(this, Text.translatable(notification).append(Text.literal(": ").append(Text.translatable(message))), true);
    } // displayNotification ()

    private void displayNotification(String message, boolean display) {
        if(!display) return;
        String customName = Utility.getEntityCustomName(this);
        if(!customName.isEmpty()) InternalLogic.displayInfo(this, Text.literal(customName + " | ").append(Text.translatable(message)), true);
        else InternalLogic.displayInfo(this, Text.translatable(message), true);
    } // displayNotification ()

    public void displayExtra() {
        MutableText debug = null;
        if(combatMode && getNotification()) {
            debug = Text.translatable(Utility.getTranslation(this.variant)).append(Text.literal(": ").append(Text.translatable("msg.rlovelyr.wary")));
            if(waryTimer < 10) debug = debug.append(": 0" + waryTimer + " ");
            else debug = debug.append(": " + waryTimer + " ");
        }

        if(autoHeal && getNotification()) {
            if(debug != null) debug = debug.append(Text.translatable("msg.rlovelyr.heal"));
            else debug = Text.translatable(Utility.getTranslation(this.variant)).append(Text.literal(": ").append(Text.translatable("msg.rlovelyr.heal")));

            if(autoHealTimer < 10) debug = debug.append(": 0" + autoHealTimer + " ");
            else debug = debug.append(": " + autoHealTimer + " ");
            if(this.getHealth() < 10) debug = debug.append("| 0" + this.getHealth());
            else debug = debug.append("| " + (int)Math.floor(this.getHealth()));
        }
        if(debug != null) InternalLogic.displayInfo(this, debug, true);
    } // displayExtra ()

    public void displayGeneralMessage(boolean canShow, boolean showLevelUp) {
        if(!canShow) return;
        InternalLogic.displayInfo(this, (Text.translatable(LovelyRobotID.TRANS_MSG_BAR)), false);
        if(showLevelUp) InternalLogic.displayInfo(this, (Text.translatable(LovelyRobotID.TRANS_MSG_LEVEL_UP)), false);
        if(this.getCustomName() != null) InternalLogic.displayInfo(this, Text.translatable(Utility.getTranslation(this.variant)).append(": " + this.getCustomName().getString()), false);
        else InternalLogic.displayInfo(this, Text.translatable(Utility.getTranslation(this.variant)), false);
        InternalLogic.displayInfo(this, Text.translatable(LovelyRobotID.TRANS_MSG_LEVEL).append(": " + this.getCurrentLevel()             + "/" + this.getMaxLevel()), false);
        InternalLogic.displayInfo(this, Text.translatable(LovelyRobotID.TRANS_MSG_EXPERIENCE).append(": " + this.getExp()                 + "/" + InternalLogic.calculateNextExp(this.getCurrentLevel())), false);
        InternalLogic.displayInfo(this, Text.translatable(LovelyRobotID.TRANS_MSG_HEALTH).append(": " + (int)Math.floor(this.getHealth()) + "/" + (int)this.getMaxHealth()), false);
        InternalLogic.displayInfo(this, Text.translatable(LovelyRobotID.TRANS_MSG_ATTACK).append(": " + this.getAttack()), false);
        InternalLogic.displayInfo(this, Text.translatable(LovelyRobotID.TRANS_MSG_DEFENCE).append(": " + this.getDefense()), false);
    } // displayGeneralMessage ()

    public void displayEnchantmentMessage() {
        InternalLogic.displayInfo(this, Text.translatable(LovelyRobotID.TRANS_MSG_BAR), false);
        InternalLogic.displayInfo(this, Text.translatable(LovelyRobotID.TRANS_MSG_ENCHANTMENT), false);
        InternalLogic.displayInfo(this, Text.translatable(LovelyRobotID.TRANS_MSG_LOOTING).append(": " + this.getLooting()                            + "/" + InternalMetric.MAX_LOOT_ENCHANTMENT), false);
        InternalLogic.displayInfo(this, Text.translatable(LovelyRobotID.TRANS_MSG_FIRE_PROTECTION).append(": " + this.getFireProtection()             + "/" + InternalMetric.PROTECTION_LIMIT_FIRE), false);
        InternalLogic.displayInfo(this, Text.translatable(LovelyRobotID.TRANS_MSG_FALL_PROTECTION).append(": " + this.getFallProtection()             + "/" + InternalMetric.PROTECTION_LIMIT_FALL), false);
        InternalLogic.displayInfo(this, Text.translatable(LovelyRobotID.TRANS_MSG_BLAST_PROTECTION).append(": " + this.getBlastProtection()           + "/" + InternalMetric.PROTECTION_LIMIT_BLAST), false);
        InternalLogic.displayInfo(this, Text.translatable(LovelyRobotID.TRANS_MSG_PROJECTILE_PROTECTION).append(": " + this.getProjectileProtection() + "/" + InternalMetric.PROTECTION_LIMIT_PROJECTILE), false);
    } // displayEnchantmentMessage ()

    // -- Save --
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

    public void writeCustomDataToNbt(NbtCompound dataNBT) {
        super.writeCustomDataToNbt(dataNBT);
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

        dataNBT.putBoolean("Notification", getNotification());
    } // writeCustomDataToNbt ()

    public void readCustomDataFromNbt(NbtCompound dataNBT) {
        super.readCustomDataFromNbt(dataNBT);
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

        setNotification(dataNBT.getBoolean("Notification"));
    } // readCustomDataFromNbt ()

} // Class InternalEntity