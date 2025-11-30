package net.msymbios.llovelyr.common.entity.internal;

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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.msymbios.llovelyr.LovelyLegacy;
import net.msymbios.llovelyr.common.entity.enums.EntityModel;
import net.msymbios.llovelyr.framework.entity.enums.*;
import net.msymbios.llovelyr.common.entity.enums.EntityVariant;
import net.msymbios.llovelyr.common.utils.internal.*;
import net.msymbios.llovelyr.framework.utils.Version;
import net.msymbios.llovelyr.lib.utils.interfaces.IReadWriteNBT;
import net.msymbios.llovelyr.source.LovelyConfigs;
import net.msymbios.llovelyr.common.shared.LovelyIdentifier;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Objects;

import static net.msymbios.llovelyr.common.utils.internal.Utility.invertBoolean;

public abstract class InternalEntity extends TamableAnimal implements IReadWriteNBT {

    // -- Variables --

    protected static final EntityDataAccessor<Integer> TEXTURE_ID = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> NOTIFICATION = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.BOOLEAN);

    protected int waryTimer = 0, autoHealTimer = 0;
    protected boolean combatMode = false, autoHeal = false, canWander = false;
    public net.msymbios.llovelyr.lib.entity.type.InternalEntityType<?> nativeEntity;
    protected EntityModel model = EntityModel.Default;

    // -- Properties --

    // TEXTURE

    public ResourceLocation getTexture() { 
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
        try {value = this.entityData.get(TEXTURE_ID);}
        catch (Exception ignored) {}
        return value;
    } // getTextureID ()

    public void setTexture(int value) { 
        // Use RobotEntityType's color checking system
        if (nativeEntity instanceof net.msymbios.llovelyr.common.entity.type.RobotEntityType) {
            if (((net.msymbios.llovelyr.common.entity.type.RobotEntityType) nativeEntity).hasColor(EntityTexture.byId(value))) {
                this.entityData.set(TEXTURE_ID, value);
            }
        } else {
            this.entityData.set(TEXTURE_ID, value);
        }
    } // setTexture ()

    public void setTexture(EntityTexture value) { setTexture(value.getId()); } // setTexture ()

    // MODEL

    public ResourceLocation getCurrentModel() { 
        // Map EntityModel to EntityVariantModel
        EntityVariantModel variant = (model == EntityModel.Armed) ? EntityVariantModel.ARMED : EntityVariantModel.DEFAULT;
        return nativeEntity.getModels().get(variant);
    } // getCurrentModel ()

    // ANIMATOR

    public ResourceLocation getAnimator() { 
        return nativeEntity.getAnimators().get(EntityVariantAnimator.DEFAULT);
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
    } // setCurrentState ()

    // NOTIFICATION

    public boolean getNotification() {
        boolean value = true;
        try {value = this.entityData.get(NOTIFICATION);}
        catch (Exception ignored) {}
        return value;
    } // getNotification ()

    public void setNotification(boolean value) {
        this.entityData.set(NOTIFICATION, value);
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

    protected InternalEntity(EntityType<? extends TamableAnimal> entityType, Level world, net.msymbios.llovelyr.lib.entity.type.InternalEntityType<?> nativeEntityType) {
        super(entityType, world);
        this.nativeEntity = nativeEntityType;

        rotate(Rotation.getRandom(this.getRandom()));

        // Apply config-based attributes after construction
        // Config is guaranteed to be loaded by the time entities spawn
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(nativeEntityType.getData().getMaxHealth());
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(nativeEntityType.getData().getAttackDamage());
        this.getAttribute(Attributes.ATTACK_SPEED).setBaseValue(nativeEntityType.getData().getAttackSpeed());
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(nativeEntityType.getData().getMoveSpeed());
        this.getAttribute(Attributes.ARMOR).setBaseValue(nativeEntityType.getData().getArmor());
        this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(nativeEntityType.getData().getArmorToughness());

        // Refresh navigation to pick up new movement speed
        // AI goals cache the speed attribute, so we need to refresh after changing it
        this.getNavigation().stop();
    } // Constructor InternalEntity ()

    // -- Inherited Methods --

    // Sound

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundEvents.GENERIC_HURT;
    } // getHurtSound ()

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    } // getDeathSound ()

    // INITIALISE

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor levelAccessor, @NotNull DifficultyInstance instance, @NotNull MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        // Use RobotEntityType's random color system
        if (nativeEntity instanceof net.msymbios.llovelyr.common.entity.type.RobotEntityType) {
            this.setTexture(((net.msymbios.llovelyr.common.entity.type.RobotEntityType) nativeEntity).getRandomColorId());
        } else {
            this.setTexture(0);  // Default to WHITE
        }
        this.setHealth(this.getMaxHealth());
        return super.finalizeSpawn(levelAccessor, instance, mobSpawnType, spawnGroupData, compoundTag);
    } // finalizeSpawn ()

    @Override
    protected void dropEquipment() {
        handleItemDrop();
        super.dropEquipment();
    } // dropEquipment ()

    @Nullable @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {
        return null;
    } // getBreedOffspring ()

    // COMBAT

    @Override
    public boolean doHurtTarget(@NotNull Entity target) {
        handleAttackTarget(target);
        return super.doHurtTarget(target);
    } // doHurtTarget ()

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        boolean result = handleDamage(source, amount);

        final Entity entity = source.getEntity();
        if (entity != null && !(entity instanceof Player) && !(entity instanceof Arrow)) amount = (amount + 1.0f) / 2.0f;

        return !result ? false : super.hurt(source, amount);
    } // hurt ()

    // INTERACTION

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();
        if (this.level().isClientSide) {
            boolean flag = this.isOwnedBy(player) || this.isTame() || canInteractWithItems(stack) && !this.isTame();
            return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            if (this.isTame() && this.isOwnedBy(player)) {
                if (!(item instanceof DyeItem)) {
                    InteractionResult interactionresult = super.mobInteract(player, hand);
                    if ((!interactionresult.consumesAction()) && this.isOwnedBy(player)) {
                        handleInteract(stack, player);
                        return InteractionResult.SUCCESS;
                    }

                    return interactionresult;
                }

                return handleItemInteraction (stack, player);
            }

            return super.mobInteract(player, hand);
        }
    } // mobInteract ()

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TEXTURE_ID, EntityTexture.RANDOM.getId());
        this.entityData.define(STATE, EntityState.Follow.getId());
        this.entityData.define(NOTIFICATION, true);
    } // defineSynchedData ()

    @Override
    public void addAdditionalSaveData(CompoundTag dataNBT) {
        super.addAdditionalSaveData(dataNBT);

        dataNBT.putInt("TextureID", this.getTextureID());
        dataNBT.putInt("State", this.getCurrentStateID());
        dataNBT.putBoolean("Notification", this.getNotification());

        CompoundTag entityData = new CompoundTag();
        entityData.putString("VersionNBT", LovelyLegacy.VERSION.toString());
        writeToNBT(entityData);
        dataNBT.put("EntityData", entityData);
    } // addAdditionalSaveData ()

    @Override
    public void readAdditionalSaveData(CompoundTag dataNBT) {
        super.readAdditionalSaveData(dataNBT);

        this.setTexture(dataNBT.getInt("TextureID"));
        this.setCurrentState(dataNBT.getInt("State"));
        this.setNotification(dataNBT.getBoolean("Notification"));

        //CompoundTag entityData = dataNBT.getCompound("EntityData");
        //readFromNBT(entityData, ObjectUtil.coalesce(new Version(entityData.getString("VersionNBT")), LovelyRobot.VERSION));
    } // readAdditionalSaveData ()

    @Override
    public CompoundTag writeToNBT(@Nonnull CompoundTag dataNBT) {
        dataNBT.putInt("TextureID", this.getTextureID());
        dataNBT.putInt("State", this.getCurrentStateID());
        dataNBT.putBoolean("Notification", this.getNotification());
        return dataNBT;
    } // writeToNBT

    @Override
    public void readFromNBT(@Nonnull CompoundTag dataNBT, @Nonnull Version version) {
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

    protected void handleInteract (ItemStack stack, Player player) {
        handleSit(stack);
        handleState(stack);
    } // handleInteract ()

    protected InteractionResult handleItemInteraction (ItemStack stack, Player player) {
        return InteractionResult.PASS;
    } // handleItemInteraction ()

    protected void handleAutoHeal () {
        if(this.getHealth() < this.getMaxHealth()) autoHeal = true;
        if(this.level().isClientSide &&!autoHeal) return;

        if(autoHealTimer != 0) {
            autoHealTimer--;
        } else {
            final float healValue = this.getHealth() / 16.0F;
            this.heal(healValue);
            autoHeal = false;
            autoHealTimer = LovelyConfigs.HealInterval;
        }
    } // handleAutoHeal ()

    protected void handleActivateCombatMode () {
        if(!combatMode) combatMode = true;
        waryTimer = LovelyConfigs.WaryTime;
    } // handleActivateCombatMode ()

    protected void handleCombatMode() {
        // Check if robot has a target and activate combat mode
        if(this.getTarget() != null && !combatMode) {
            handleActivateCombatMode();
        }
        
        if(this.level().isClientSide && !combatMode) return;

        if(waryTimer != 0) {
            if(this.model != EntityModel.Armed) this.model = EntityModel.Armed;
            waryTimer--;
        } else {
            if(this.model != EntityModel.Default) this.model = EntityModel.Default;
            combatMode = false;
        }
    } // handleCombatMode ()

    public void handleTame(Player player) {
        this.tame(player);
        this.setTame(true);
        InternalParticle.Heart(this);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_OWNER).append(Component.nullToEmpty(": " + player.getName().getString())), true);
    } // handleTame ()

    protected boolean handleTexture(ItemStack stack, Player player) {
        return false;
    } // handleTexture ()

    protected void handleSit(ItemStack stack) {
        if(!canInteractWithItems(stack)) return;
        setOrderedToSit(invertBoolean(isOrderedToSit()));
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
        if(!customName.isEmpty()) InternalLogic.displayInfo(this, Component.nullToEmpty(customName + " | ").copy().append(LovelyIdentifier.getMessageTranslation(notification).append(Component.nullToEmpty(": ").copy().append(LovelyIdentifier.getMessageTranslation(message)))), true);
        else InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(notification).append(Component.nullToEmpty(": ").copy().append(LovelyIdentifier.getMessageTranslation(message))), true);
    } // displayNotification ()

    protected void displayNotification(String message, boolean display) {
        if(!display) return;
        String customName = Utility.getEntityCustomName(this);
        if(!customName.isEmpty()) InternalLogic.displayInfo(this, Component.nullToEmpty(customName + " | ").copy().append(LovelyIdentifier.getMessageTranslation(message)), true);
        else InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(message), true);
    } // displayNotification ()

    protected void displayExtra() {
        Component debug = null;
        MutableComponent entityName = !Utility.getEntityCustomName(this).isEmpty() ? Component.literal(Utility.getEntityCustomName(this)) : LovelyIdentifier.getTranslation(Objects.requireNonNull(EntityVariant.byName(nativeEntity.getKey())));
        if(combatMode && getNotification()) {
            debug = entityName.append(Component.nullToEmpty(": ").copy().append(LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_WARY)));
            if(waryTimer < 10) debug = debug.copy().append(": 0" + waryTimer + " ");
            else debug = debug.copy().append(": " + waryTimer + " ");
        }

        if(autoHeal && getNotification()) {
            if(debug != null) debug = debug.copy().append(LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_HEAL));
            else debug = entityName.append(Component.nullToEmpty(": ").copy().append(LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_HEAL)));

            if(autoHealTimer < 10) debug = debug.copy().append(": 0" + autoHealTimer + " ");
            else debug = debug.copy().append(": " + autoHealTimer + " ");
            if(this.getHealth() < 10) debug = debug.copy().append("| 0" + this.getHealth());
            else debug = debug.copy().append("| " + (int)this.getHealth());
        }
        if(debug != null) InternalLogic.displayInfo(this, debug, true);
    } // displayExtra ()

} // Class InternalEntity
