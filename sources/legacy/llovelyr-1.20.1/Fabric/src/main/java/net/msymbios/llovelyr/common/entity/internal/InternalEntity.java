package net.msymbios.llovelyr.common.entity.internal;

import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
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
import net.msymbios.llovelyr.LovelyLegacy;
import net.msymbios.llovelyr.common.entity.enums.EntityModel;
import net.msymbios.llovelyr.common.entity.enums.EntityVariant;
import net.msymbios.llovelyr.common.utils.internal.*;
import net.msymbios.llovelyr.framework.entity.enums.EntityState;
import net.msymbios.llovelyr.framework.entity.enums.EntityTexture;
import net.msymbios.llovelyr.framework.entity.enums.EntityVariantModel;
import net.msymbios.llovelyr.framework.entity.enums.EntityVariantTexture;
import net.msymbios.llovelyr.framework.entity.enums.EntityVariantAnimator;
import net.msymbios.llovelyr.framework.utils.Version;
import net.msymbios.llovelyr.lib.utils.interfaces.IReadWriteNBT;
import net.msymbios.llovelyr.source.LovelyConfigs;
import net.msymbios.llovelyr.common.shared.LovelyIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.msymbios.llovelyr.common.utils.internal.Utility.invertBoolean;

public abstract class InternalEntity extends TameableEntity implements IReadWriteNBT {

    // -- Variables --

    protected static final TrackedData<Integer> TEXTURE_ID = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Integer> STATE = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Boolean> NOTIFICATION = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    protected int waryTimer = 0, autoHealTimer = 0;
    protected boolean combatMode = false, autoHeal = false, canWander = false;
    public net.msymbios.llovelyr.lib.entity.type.InternalEntityType<?> nativeEntity;
    protected EntityModel model = EntityModel.Default;

    // -- Properties --

    // TEXTURE

    public Identifier getTexture() { 
        // Use RobotEntityType's color texture system
        if (nativeEntity instanceof net.msymbios.llovelyr.common.entity.type.RobotEntityType) {
            return ((net.msymbios.llovelyr.common.entity.type.RobotEntityType) nativeEntity).getColorTexture(EntityTexture.byId(getTextureID()));
        }
        // Fallback for non-robot entities
        return nativeEntity.getTextures().get(EntityVariantTexture.DEFAULT);
    } // getTexture ()

    public int getTextureID() {
        int value = 0;  // Default to WHITE
        // Use RobotEntityType's random color system
        if (nativeEntity instanceof net.msymbios.llovelyr.common.entity.type.RobotEntityType) {
            value = ((net.msymbios.llovelyr.common.entity.type.RobotEntityType) nativeEntity).getRandomColorId();
        }
        try {value = this.dataTracker.get(TEXTURE_ID);}
        catch (Exception ignored) {}
        return value;
    } // getTextureID ()

    public void setTexture(int value) { 
        // Use RobotEntityType's color checking system
        if (nativeEntity instanceof net.msymbios.llovelyr.common.entity.type.RobotEntityType) {
            if (((net.msymbios.llovelyr.common.entity.type.RobotEntityType) nativeEntity).hasColor(EntityTexture.byId(value))) {
                this.dataTracker.set(TEXTURE_ID, value);
            }
        } else {
            this.dataTracker.set(TEXTURE_ID, value);
        }
    } // setTexture ()

    public void setTexture(EntityTexture value) { setTexture(value.getId()); } // setTexture ()

    // MODEL

    public Identifier getCurrentModel() { 
        // Map EntityModel to EntityVariantModel
        EntityVariantModel variant = (model == EntityModel.Armed) ? EntityVariantModel.ARMED : EntityVariantModel.DEFAULT;
        return nativeEntity.getModels().get(variant);
    } // getCurrentModel ()

    // ANIMATOR

    public Identifier getAnimator() { 
        return nativeEntity.getAnimators().get(EntityVariantAnimator.DEFAULT);
    } // getAnimator ()

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

    // NOTIFICATION

    public boolean getNotification() {
        boolean value = true;
        try {value = this.dataTracker.get(NOTIFICATION);}
        catch (Exception ignored) {}
        return value;
    } // getNotification ()

    public void setNotification(boolean value) {
        this.dataTracker.set(NOTIFICATION, value);
    } // setNotification ()

    // COMBAT MODE

    /**
     * Checks if robot is in combat (wary) mode.
     * <p>
     * <b>Usage:</b> Determines if robot is actively engaged in combat or recently
     * was in combat. Combat mode is activated when robot attacks or is attacked,
     * and persists for WaryTime ticks after combat ends.
     * <p>
     * <b>Behavior Impact:</b> Affects follow distance, movement priorities, and
     * visual model (armed vs default stance).
     *
     * @return true if robot is in combat mode (wary)
     */
    public boolean isWary() {
        return combatMode;
    } // isWary ()

    // WANDER PERMISSION

    /**
     * Checks if robot is allowed to wander.
     * <p>
     * <b>Usage:</b> Controlled by AiConditionalWanderGoal to signal when wandering
     * is active. When true, AiFollowOwnerGoal yields priority to allow wandering.
     * <p>
     * <b>Behavior Impact:</b> Disables follow goal when owner is stationary and
     * robot is wandering nearby.
     *
     * @return true if robot can wander (owner stationary, conditions met)
     */
    public boolean canWander() {
        return canWander;
    } // canWander ()

    /**
     * Sets whether robot is allowed to wander.
     * <p>
     * <b>Usage:</b> Set by AiConditionalWanderGoal when wander conditions are met
     * or when owner starts moving again.
     *
     * @param value true to allow wandering, false to disable
     */
    public void setCanWander(boolean value) {
        canWander = value;
    } // setCanWander ()

    // -- Constructor --

    protected InternalEntity(EntityType<? extends TameableEntity> entityType, World world, net.msymbios.llovelyr.lib.entity.type.InternalEntityType<?> nativeEntityType) {
        super(entityType, world);
        this.nativeEntity = nativeEntityType;
    } // Constructor InternalEntity ()

    // -- Inherited Methods --

    // SOUND

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundEvents.ENTITY_GENERIC_HURT;
    } // getHurtSound ()

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_GENERIC_DEATH;
    } // getDeathSound ()

    // INITIALISE

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (nativeEntity instanceof net.msymbios.llovelyr.common.entity.type.RobotEntityType) {
            this.setTexture(((net.msymbios.llovelyr.common.entity.type.RobotEntityType) nativeEntity).getRandomColorId());
        }
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    } // initialize ()

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        handleItemDrop();
        super.dropEquipment(source, lootingMultiplier, allowDrops);
    } // dropEquipment ()

    @Nullable @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    } // createChild

    // COMBAT

    @Override
    public void onAttacking(Entity target) {
        handleAttackTarget(target);
        super.onAttacking(target);
    } // onAttacking ()

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean result = handleDamage(source, amount);

        final Entity entity = source.getSource();
        if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof ArrowEntity)) amount = (amount + 1.0f) / 2.0f;

        return !result ? false : super.damage(source, amount);
    } // damage ()

    // INTERACTION

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        Item item = stack.getItem();
        if (this.getWorld().isClient) {
            boolean flag = this.isOwner(player) || this.isTamed() || canInteractWithItems(stack) && !this.isTamed();
            return flag ? ActionResult.CONSUME : ActionResult.PASS;
        } else {
            if (this.isTamed() && this.isOwner(player)) {
                if (!(item instanceof DyeItem)) {
                    ActionResult interactionresult = super.interactMob(player, hand);
                    if ((!interactionresult.isAccepted()) && this.isOwner(player)) {
                        handleInteract(stack, player);
                        return ActionResult.SUCCESS;
                    }

                    return interactionresult;
                }

                return handleItemInteraction (stack, player);
            }

            return super.interactMob(player, hand);
        }
    } // interactMob ()

    // DATA

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TEXTURE_ID, EntityTexture.RANDOM.getId());
        this.dataTracker.startTracking(STATE, EntityState.Follow.getId());
        this.dataTracker.startTracking(NOTIFICATION, true);
    } // initDataTracker ()

    @Override
    public void writeCustomDataToNbt(NbtCompound dataNBT) {
        dataNBT.putInt("TextureID", this.getTextureID());
        dataNBT.putInt("State", this.getCurrentStateID());
        dataNBT.putBoolean("Notification", this.getNotification());

        NbtCompound entityData = new NbtCompound();
        entityData.putString("VersionNBT", LovelyLegacy.VERSION.toString());
        writeToNBT(entityData);
        dataNBT.put("EntityData", entityData);

        super.writeCustomDataToNbt(dataNBT);
    } // writeCustomDataToNbt ()

    @Override
    public void readCustomDataFromNbt(NbtCompound dataNBT) {
        super.readCustomDataFromNbt(dataNBT);
        this.setTexture(dataNBT.getInt("TextureID"));
        this.setCurrentState(dataNBT.getInt("State"));
        this.setNotification(dataNBT.getBoolean("Notification"));

        //NbtCompound entityData = dataNBT.getCompound("EntityData");
        //readFromNBT(entityData, ObjectUtil.coalesce(new Version(entityData.getString("VersionNBT")), LovelyRobot.VERSION));
    } // readCustomDataFromNbt ()

    @Override
    public NbtCompound writeToNBT(NbtCompound dataNBT) {
        dataNBT.putInt("TextureID", this.getTextureID());
        dataNBT.putInt("State", this.getCurrentStateID());
        dataNBT.putBoolean("Notification", this.getNotification());
        return dataNBT;
    } // writeToNBT

    @Override
    public void readFromNBT(NbtCompound dataNBT, Version version) {
        this.setTexture(dataNBT.getInt("TextureID"));
        this.setCurrentState(dataNBT.getInt("State"));
        this.setNotification(dataNBT.getBoolean("Notification"));
    } // readFromNBT ()

    // -- Custom Methods --

    protected abstract void handleItemDrop();

    public abstract ItemStack setDropItem();

    protected abstract void handleAttackTarget(@NotNull Entity target);

    protected abstract boolean handleDamage (@NotNull DamageSource source, float amount);

    protected boolean canInteractWithItems(ItemStack stack){
        return false;
    } // canInteractWithItems ()

    protected void handleInteract (ItemStack stack, PlayerEntity player) {
        handleSit(stack);
        handleState(stack);
    } // handleInteract ()

    protected ActionResult handleItemInteraction (ItemStack stack, PlayerEntity player) {
        return ActionResult.SUCCESS;
    } // handleItemInteraction ()

    protected void handleAutoHeal () {
        if(this.getHealth() < this.getMaxHealth()) autoHeal = true;
        if(this.getWorld().isClient &&!autoHeal) return;

        if(autoHealTimer != 0) {
            autoHealTimer--;
        } else {
            final float healValue = this.getHealth() / 16.0F;
            this.heal(healValue);
            autoHeal = false;
            autoHealTimer = LovelyConfigs.Common.HealInterval;
        }
    } // handleAutoHeal ()

    protected void handleActivateCombatMode () {
        if(!combatMode) combatMode = true;
        waryTimer = LovelyConfigs.Common.WaryTime;
    } // handleActivateCombatMode ()

    protected void handleCombatMode() {
        // Check if robot is attacking or has a target and activate combat mode
        if(this.isAttacking() || this.getTarget() != null) {
            handleActivateCombatMode();
        }
        
        if(this.getWorld().isClient && !combatMode) return;

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
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_OWNER).append(Text.literal(": " + player.getName().getString())), true);
    } // handleTame ()

    protected boolean handleTexture(ItemStack stack, PlayerEntity player) {
        return false;
    } // handleTexture ()

    protected void handleSit(ItemStack stack) {
        if(!canInteractWithItems(stack)) return;
        setSitting(invertBoolean(isSitting()));
        this.jumping = false;
        this.navigation.stop();
        this.setTarget(null);
    } // handleSit ()

    protected void handleState(ItemStack stack) {
        if (handleFollowState(stack)) return;
        if (handleStandbyState(stack)) return;
    } // handleState

    protected boolean handleStandbyState(ItemStack stack){
        if(!canInteractWithItems(stack) || getCurrentState() == EntityState.Standby) return false;
        setCurrentState(EntityState.Standby);
        displayNotification(LovelyIdentifier.MSG_STANDBY, getNotification());
        return true;
    } // handleStandbyState ()

    protected boolean handleFollowState(ItemStack stack){
        if(!canInteractWithItems(stack) || getCurrentState() == EntityState.Follow) return false;
        setCurrentState(EntityState.Follow);
        displayNotification(LovelyIdentifier.MSG_FOLLOW, getNotification());
        return true;
    } // handleFollowState ()

    // DISPLAY

    protected void displayNotification(String notification, String message, boolean display) {
        if(!display) return;
        String customName = Utility.getEntityCustomName(this);
        if(!customName.isEmpty()) InternalLogic.displayInfo(this, Text.literal(customName + " | ").copy().append(LovelyIdentifier.getMessageTranslation(notification).append(Text.literal(": ").copy().append(LovelyIdentifier.getMessageTranslation(message)))), true);
        else InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(notification).append(Text.literal(": ").copy().append(LovelyIdentifier.getMessageTranslation(message))), true);
    } // displayNotification ()

    protected void displayNotification(String message, boolean display) {
        if(!display) return;
        String customName = Utility.getEntityCustomName(this);
        if(!customName.isEmpty()) InternalLogic.displayInfo(this, Text.literal(customName + " | ").copy().append(LovelyIdentifier.getMessageTranslation(message)), true);
        else InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(message), true);
    } // displayNotification ()

    protected void displayExtra() {
        MutableText debug = null;
        MutableText entityName = !Utility.getEntityCustomName(this).isEmpty() ? Text.literal(Utility.getEntityCustomName(this)) : LovelyIdentifier.getTranslation(Objects.requireNonNull(EntityVariant.byName(nativeEntity.getKey())));
        if(combatMode && getNotification()) {
            debug = entityName.append(Text.literal(": ").copy().append(LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_WARY)));
            if(waryTimer < 10) debug = debug.copy().append(": 0" + waryTimer + " ");
            else debug = debug.copy().append(": " + waryTimer + " ");
        }

        if(autoHeal && getNotification()) {
            if(debug != null) debug = debug.copy().append(LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_HEAL));
            else debug = entityName.append(Text.literal(": ").copy().append(LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_HEAL)));

            if(autoHealTimer < 10) debug = debug.copy().append(": 0" + autoHealTimer + " ");
            else debug = debug.copy().append(": " + autoHealTimer + " ");
            if(this.getHealth() < 10) debug = debug.copy().append("| 0" + this.getHealth());
            else debug = debug.append("| " + (int)this.getHealth());
        }
        if(debug != null) InternalLogic.displayInfo(this, debug, true);
    } // displayExtra ()

} // Class InternalEntity