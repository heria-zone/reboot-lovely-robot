package net.msymbios.llovelyr.common.entity;

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
import net.msymbios.llovelyr.common.util.interfaces.*;
import net.msymbios.llovelyr.common.util.internal.*;
import net.msymbios.llovelyr.source.configs.LovelyConfigs;
import net.msymbios.llovelyr.source.configs.LovelyIdentifier;
import net.msymbios.llovelyr.source.entity.internal.enums.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.msymbios.llovelyr.common.util.internal.Utility.invertBoolean;

/**
 * Abstract base entity for all robot variants with synchronized state management.
 * <p>
 * <b>Architecture:</b> Extends TameableEntity to leverage vanilla taming mechanics
 * while adding robot-specific state (texture, combat mode, auto-heal). Uses entity
 * data synchronization for client-server state consistency.
 * <p>
 * <b>State Management:</b> Maintains three synchronized data trackers (texture,
 * state, notifications) plus local timers for combat/heal logic. Separates
 * synchronized state (replicated) from local state (server-only calculations).
 * <p>
 * <b>Design Pattern:</b> Template method pattern with abstract methods for
 * variant-specific behavior (item drops, damage handling, attack logic).
 */
public abstract class InternalEntity extends TameableEntity implements IReadWriteNBT {

    // -- Variables --

    /**
     * Synchronized texture ID for client-side rendering.
     * <p>
     * <b>Replication:</b> Automatically synced to clients for model/texture selection.
     */
    protected static final TrackedData<Integer> TEXTURE_ID = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.INTEGER);

    /**
     * Synchronized behavioral state (Follow, Standby, etc.).
     * <p>
     * <b>Replication:</b> Controls AI goal activation on both sides.
     */
    protected static final TrackedData<Integer> STATE = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.INTEGER);

    /**
     * Synchronized notification toggle for owner messages.
     * <p>
     * <b>Replication:</b> Allows client-side notification filtering.
     */
    protected static final TrackedData<Boolean> NOTIFICATION = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    /**
     * Server-side timers for combat and healing logic.
     * <p>
     * <b>Not Synchronized:</b> Timers are server-authoritative, no client replication needed.
     */
    protected int waryTimer = 0, autoHealTimer = 0;

    /**
     * Server-side flags for combat and healing states.
     */
    protected boolean combatMode = false, autoHeal = false;

    /**
     * Entity type wrapper providing variant-specific resources.
     * <p>
     * <b>Initialization:</b> Must be set by subclass constructor.
     */
    public InternalEntityType<?> nativeEntity;

    /**
     * Current model state (Default, Armed) for animation switching.
     * <p>
     * <b>Not Synchronized:</b> Derived from combat mode, recalculated each tick.
     */
    protected EntityModel model = EntityModel.Default;

    // -- Properties --

    // TEXTURE

    /**
     * Retrieves texture identifier for current texture ID.
     *
     * @return the texture identifier
     */
    public Identifier getTexture() {
        return nativeEntity.getTexture(EntityTexture.byId(getTextureID()));
    } // getTexture()

    /**
     * Gets synchronized texture ID with fallback to random.
     * <p>
     * <b>Exception Handling:</b> Returns random texture if data tracker not initialized.
     *
     * @return the texture ID
     */
    public int getTextureID() {
        int value = nativeEntity.getRandomTextureID();
        try {
            value = this.dataTracker.get(TEXTURE_ID);
        } catch (Exception ignored) {
        }
        return value;
    } // getTextureID()

    /**
     * Sets texture ID with validation.
     * <p>
     * <b>Validation:</b> Only accepts valid textures for this entity type.
     *
     * @param value the texture ID
     */
    public void setTexture(int value) {
        if (nativeEntity.checkTexture(EntityTexture.byId(value))) this.dataTracker.set(TEXTURE_ID, value);
    } // setTexture()

    /**
     * Convenience overload accepting EntityTexture enum.
     *
     * @param value the texture enum
     */
    public void setTexture(EntityTexture value) {
        setTexture(value.getId());
    } // setTexture()

    // MODEL

    /**
     * Gets current model identifier based on combat state.
     *
     * @return the model identifier
     */
    public Identifier getCurrentModel() {
        return nativeEntity.getModel(model);
    } // getCurrentModel()

    // ANIMATOR

    /**
     * Gets GeckoLib animator identifier.
     *
     * @return the animator identifier
     */
    public Identifier getAnimator() {
        return nativeEntity.getAnimator();
    } // getAnimator()

    // STATE

    /**
     * Gets synchronized state ID with fallback to Standby.
     *
     * @return the state ID
     */
    public int getCurrentStateID() {
        int value = EntityState.Standby.getId();
        try {
            value = this.dataTracker.get(STATE);
        } catch (Exception ignored) {
        }
        return value;
    } // getCurrentStateID()

    /**
     * Gets synchronized state enum with fallback to Standby.
     *
     * @return the state enum
     */
    public EntityState getCurrentState() {
        EntityState value = EntityState.Standby;
        try {
            value = EntityState.byId(this.dataTracker.get(STATE));
        } catch (Exception ignored) {
        }
        return value;
    } // getCurrentState()

    /**
     * Sets state from enum.
     *
     * @param value the state enum
     */
    public void setCurrentState(EntityState value) {
        this.dataTracker.set(STATE, value.getId());
    } // setCurrentState()

    /**
     * Sets state from ID.
     *
     * @param value the state ID
     */
    public void setCurrentState(int value) {
        this.dataTracker.set(STATE, value);
    } // setCurrentState()

    // NOTIFICATION

    /**
     * Gets notification toggle with fallback to true.
     *
     * @return true if notifications enabled
     */
    public boolean getNotification() {
        boolean value = true;
        try {
            value = this.dataTracker.get(NOTIFICATION);
        } catch (Exception ignored) {
        }
        return value;
    } // getNotification()

    /**
     * Sets notification toggle.
     *
     * @param value true to enable notifications
     */
    public void setNotification(boolean value) {
        this.dataTracker.set(NOTIFICATION, value);
    } // setNotification()

    // -- Constructor --

    /**
     * Protected constructor for subclass initialization.
     * <p>
     * <b>Subclass Responsibility:</b> Must initialize nativeEntity field.
     *
     * @param entityType the entity type
     * @param world the world
     */
    protected InternalEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    } // Constructor: InternalEntity()

    // -- Inherited Methods --

    // SOUND

    /**
     * Generic hurt sound for all robot variants.
     *
     * @param source the damage source
     * @return the hurt sound event
     */
    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundEvents.ENTITY_GENERIC_HURT;
    } // getHurtSound()

    /**
     * Generic death sound for all robot variants.
     *
     * @return the death sound event
     */
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_GENERIC_DEATH;
    } // getDeathSound()

    // INITIALIZE

    /**
     * Initializes entity on spawn with random texture.
     * <p>
     * <b>Timing:</b> Called after entity placement but before first tick.
     *
     * @param world the world accessor
     * @param difficulty the local difficulty
     * @param spawnReason the spawn reason
     * @param entityData the spawn entity data
     * @param entityNbt the spawn NBT data
     * @return the entity data
     */
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setTexture(nativeEntity.getRandomTextureID());
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    } // initialize()

    /**
     * Handles item drops on death.
     * <p>
     * <b>Hook Point:</b> Calls abstract handleItemDrop for variant-specific drops.
     *
     * @param source the damage source
     * @param lootingMultiplier the looting level
     * @param allowDrops true if drops allowed
     */
    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        handleItemDrop();
        super.dropEquipment(source, lootingMultiplier, allowDrops);
    } // dropEquipment()

    /**
     * Prevents breeding (robots don't breed).
     *
     * @param world the server world
     * @param entity the breeding partner
     * @return null (no offspring)
     */
    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    } // createChild()

    // COMBAT

    /**
     * Handles attack target with variant-specific logic.
     * <p>
     * <b>Hook Point:</b> Calls abstract handleAttackTarget before vanilla damage.
     *
     * @param target the attack target
     */
    @Override
    public void onAttacking(Entity target) {
        handleAttackTarget(target);
        super.onAttacking(target);
    } // onAttacking()

    /**
     * Handles incoming damage with variant-specific logic and damage reduction.
     * <p>
     * <b>Damage Reduction:</b> Non-player, non-arrow damage is halved to balance
     * robot survivability against environmental hazards.
     *
     * @param source the damage source
     * @param amount the damage amount
     * @return true if damage was applied
     */
    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean result = handleDamage(source, amount);

        final Entity entity = source.getSource();
        if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof ArrowEntity))
            amount = (amount + 1.0f) / 2.0f;

        return !result ? false : super.damage(source, amount);
    } // damage()

    // INTERACTION

    /**
     * Handles player interaction with robot.
     * <p>
     * <b>Client-Side:</b> Returns consumption status for animation.
     * <b>Server-Side:</b> Processes actual interaction logic (sitting, state changes,
     * item interactions).
     * <p>
     * <b>Dye Handling:</b> Dye items bypass normal interaction for color customization.
     *
     * @param player the interacting player
     * @param hand the interaction hand
     * @return the action result
     */
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

                return handleItemInteraction(stack, player);
            }

            return super.interactMob(player, hand);
        }
    } // interactMob()

    // DATA SYNCHRONIZATION

    /**
     * Initializes data tracker for client replication.
     * <p>
     * <b>Initialization Order:</b> Called before first tick, sets default values.
     */
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TEXTURE_ID, EntityTexture.RANDOM.getId());
        this.dataTracker.startTracking(STATE, EntityState.Follow.getId());
        this.dataTracker.startTracking(NOTIFICATION, true);
    } // initDataTracker()

    /**
     * Serializes entity data to NBT for world save.
     * <p>
     * <b>Version Tagging:</b> Stores mod version for future migration support.
     *
     * @param dataNBT the NBT compound to write to
     */
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
    } // writeCustomDataToNbt()

    /**
     * Deserializes entity data from NBT on world load.
     * <p>
     * <i>Note:</i> Version-aware migration currently commented out pending implementation.
     *
     * @param dataNBT the NBT compound to read from
     */
    @Override
    public void readCustomDataFromNbt(NbtCompound dataNBT) {
        super.readCustomDataFromNbt(dataNBT);
        this.setTexture(dataNBT.getInt("TextureID"));
        this.setCurrentState(dataNBT.getInt("State"));
        this.setNotification(dataNBT.getBoolean("Notification"));

        //NbtCompound entityData = dataNBT.getCompound("EntityData");
        //readFromNBT(entityData, ObjectUtil.coalesce(new Version(entityData.getString("VersionNBT")), LovelyLegacy.VERSION));
    } // readCustomDataFromNbt()

    /**
     * Implements IReadWriteNBT for custom serialization.
     *
     * @param dataNBT the NBT compound to write to
     * @return the NBT compound
     */
    @Override
    public NbtCompound writeToNBT(NbtCompound dataNBT) {
        dataNBT.putInt("TextureID", this.getTextureID());
        dataNBT.putInt("State", this.getCurrentStateID());
        dataNBT.putBoolean("Notification", this.getNotification());
        return dataNBT;
    } // writeToNBT()

    /**
     * Implements IReadWriteNBT for version-aware deserialization.
     *
     * @param dataNBT the NBT compound to read from
     * @param version the data version for migration
     */
    @Override
    public void readFromNBT(NbtCompound dataNBT, Version version) {
        this.setTexture(dataNBT.getInt("TextureID"));
        this.setCurrentState(dataNBT.getInt("State"));
        this.setNotification(dataNBT.getBoolean("Notification"));
    } // readFromNBT()

    // -- Custom Methods --

    // ABSTRACT METHODS (Variant-Specific)

    /**
     * Handles item drops on death (variant-specific).
     */
    protected abstract void handleItemDrop();

    /**
     * Creates drop item stack (variant-specific).
     *
     * @return the item stack to drop
     */
    public abstract ItemStack setDropItem();

    /**
     * Handles attack target logic (variant-specific).
     *
     * @param target the attack target
     */
    protected abstract void handleAttackTarget(@NotNull Entity target);

    /**
     * Handles damage logic with variant-specific behavior.
     *
     * @param source the damage source
     * @param amount the damage amount
     * @return true to cancel damage, false to allow
     */
    protected abstract boolean handleDamage(@NotNull DamageSource source, float amount);

    // INTERACTION HANDLERS

    /**
     * Checks if item can be used for interaction.
     * <p>
     * <b>Override:</b> Subclasses define valid interaction items.
     *
     * @param stack the item stack
     * @return true if item can interact
     */
    protected boolean canInteractWithItems(ItemStack stack) {
        return false;
    } // canInteractWithItems()

    /**
     * Handles standard interaction (sit toggle, state changes).
     *
     * @param stack the item stack
     * @param player the player
     */
    protected void handleInteract(ItemStack stack, PlayerEntity player) {
        handleSit(stack);
        handleState(stack);
    } // handleInteract()

    /**
     * Handles special item interactions (dyes, upgrades).
     *
     * @param stack the item stack
     * @param player the player
     * @return the action result
     */
    protected ActionResult handleItemInteraction(ItemStack stack, PlayerEntity player) {
        return ActionResult.SUCCESS;
    } // handleItemInteraction()

    // AUTO-HEAL SYSTEM

    /**
     * Handles automatic healing over time.
     * <p>
     * <b>Healing Rate:</b> health/16 per interval (config-driven).
     * <b>Server-Only:</b> Healing calculations on server, synced via health attribute.
     */
    protected void handleAutoHeal() {
        if (this.getHealth() < this.getMaxHealth()) autoHeal = true;
        if (this.getWorld().isClient && !autoHeal) return;

        if (autoHealTimer != 0) {
            autoHealTimer--;
        } else {
            final float healValue = this.getHealth() / 16.0F;
            this.heal(healValue);
            autoHeal = false;
            autoHealTimer = LovelyConfigs.Common.HealInterval;
        }
    } // handleAutoHeal()

    // COMBAT MODE SYSTEM

    /**
     * Activates combat mode with wary timer.
     */
    protected void handleActivateCombatMode() {
        if (!combatMode) combatMode = true;
        waryTimer = LovelyConfigs.Common.WaryTime;
    } // handleActivateCombatMode()

    /**
     * Manages combat mode state and model switching.
     * <p>
     * <b>Model Switching:</b> Armed model during wary timer, Default after timeout.
     * <b>Server-Only:</b> Combat mode is server-authoritative.
     */
    protected void handleCombatMode() {
        if (this.isAttacking()) handleActivateCombatMode();
        if (this.getWorld().isClient && !combatMode) return;

        if (waryTimer != 0) {
            if (this.model != EntityModel.Armed) this.model = EntityModel.Armed;
            waryTimer--;
        } else {
            if (this.model != EntityModel.Default) this.model = EntityModel.Default;
            combatMode = false;
        }
    } // handleCombatMode()

    // TAMING

    /**
     * Handles taming by player with particle effect and notification.
     *
     * @param player the taming player
     */
    public void handleTame(PlayerEntity player) {
        this.setOwner(player);
        this.setTamed(true);
        InternalParticle.Heart(this);
        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_OWNER).append(Text.literal(": " + player.getName().getString())), true);
    } // handleTame()

    // STATE HANDLERS

    /**
     * Handles texture change (override for variant-specific logic).
     *
     * @param stack the item stack
     * @param player the player
     * @return true if texture changed
     */
    protected boolean handleTexture(ItemStack stack, PlayerEntity player) {
        return false;
    } // handleTexture()

    /**
     * Toggles sitting state.
     *
     * @param stack the item stack
     */
    protected void handleSit(ItemStack stack) {
        if (!canInteractWithItems(stack)) return;
        setSitting(invertBoolean(isSitting()));
        this.jumping = false;
        this.navigation.stop();
        this.setTarget(null);
    } // handleSit()

    /**
     * Handles state changes (Follow, Standby).
     *
     * @param stack the item stack
     */
    protected void handleState(ItemStack stack) {
        if (handleFollowState(stack)) return;
        if (handleStandbyState(stack)) return;
    } // handleState()

    /**
     * Switches to Standby state.
     *
     * @param stack the item stack
     * @return true if state changed
     */
    protected boolean handleStandbyState(ItemStack stack) {
        if (!canInteractWithItems(stack) || getCurrentState() == EntityState.Standby) return false;
        setCurrentState(EntityState.Standby);
        displayNotification(LovelyIdentifier.MSG_STANDBY, getNotification());
        return true;
    } // handleStandbyState()

    /**
     * Switches to Follow state.
     *
     * @param stack the item stack
     * @return true if state changed
     */
    protected boolean handleFollowState(ItemStack stack) {
        if (!canInteractWithItems(stack) || getCurrentState() == EntityState.Follow) return false;
        setCurrentState(EntityState.Follow);
        displayNotification(LovelyIdentifier.MSG_FOLLOW, getNotification());
        return true;
    } // handleFollowState()

    // DISPLAY

    /**
     * Displays notification with category and message.
     *
     * @param notification the notification category key
     * @param message the message key
     * @param display true to display
     */
    protected void displayNotification(String notification, String message, boolean display) {
        if (!display) return;
        String customName = Utility.getEntityCustomName(this);
        if (!customName.isEmpty())
            InternalLogic.displayInfo(this, Text.literal(customName + " | ").copy().append(LovelyIdentifier.getMessageTranslation(notification).append(Text.literal(": ").copy().append(LovelyIdentifier.getMessageTranslation(message)))), true);
        else
            InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(notification).append(Text.literal(": ").copy().append(LovelyIdentifier.getMessageTranslation(message))), true);
    } // displayNotification()

    /**
     * Displays simple notification message.
     *
     * @param message the message key
     * @param display true to display
     */
    protected void displayNotification(String message, boolean display) {
        if (!display) return;
        String customName = Utility.getEntityCustomName(this);
        if (!customName.isEmpty())
            InternalLogic.displayInfo(this, Text.literal(customName + " | ").copy().append(LovelyIdentifier.getMessageTranslation(message)), true);
        else InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(message), true);
    } // displayNotification()

    /**
     * Displays debug information (combat mode, auto-heal status).
     * <p>
     * <b>Action Bar:</b> Shows wary timer and heal timer with current health.
     */
    protected void displayExtra() {
        MutableText debug = null;
        MutableText entityName = !Utility.getEntityCustomName(this).isEmpty() ? Text.literal(Utility.getEntityCustomName(this)) : LovelyIdentifier.getTranslation(Objects.requireNonNull(EntityVariant.byName(nativeEntity.key)));
        if (combatMode && getNotification()) {
            debug = entityName.append(Text.literal(": ").copy().append(LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_WARY)));
            if (waryTimer < 10) debug = debug.copy().append(": 0" + waryTimer + " ");
            else debug = debug.copy().append(": " + waryTimer + " ");
        }

        if (autoHeal && getNotification()) {
            if (debug != null) debug = debug.copy().append(LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_HEAL));
            else debug = entityName.append(Text.literal(": ").copy().append(LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_HEAL)));

            if (autoHealTimer < 10) debug = debug.copy().append(": 0" + autoHealTimer + " ");
            else debug = debug.copy().append(": " + autoHealTimer + " ");
            if (this.getHealth() < 10) debug = debug.copy().append("| 0" + this.getHealth());
            else debug = debug.append("| " + (int) this.getHealth());
        }
        if (debug != null) InternalLogic.displayInfo(this, debug, true);
    } // displayExtra()

} // Class: InternalEntity
