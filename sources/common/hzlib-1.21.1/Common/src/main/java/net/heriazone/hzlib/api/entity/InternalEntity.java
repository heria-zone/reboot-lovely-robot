package net.heriazone.hzlib.api.entity;

import net.heriazone.hzlib.api.entity.features.variants.AnimatorVariantFeature;
import net.heriazone.hzlib.api.entity.features.variants.ModelVariantFeature;
import net.heriazone.hzlib.api.entity.features.variants.TextureVariantFeature;
import net.heriazone.hzlib.api.entity.variants.interfaces.IAnimatorVariant;
import net.heriazone.hzlib.api.entity.variants.interfaces.IModelVariant;
import net.heriazone.hzlib.api.entity.variants.interfaces.ITextureVariant;
import net.heriazone.hzlib.framework.entity.enums.EntityState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * <p>Generic base entity class providing common functionality for all HZLib-based mods.<p>
 * <p>
 * <b>Architecture:</b> Serves as the foundation for both robot and monster entities,
 * providing variant system integration, basic NBT handling, and common entity behaviors
 * without domain-specific functionality.
 * <p>
 * <b>Design Decision:</b> Keeps generic functionality separate from specialized features
 * (robot levels, monster belly systems) to enable code reuse across different entity types
 * while maintaining clear separation of concerns.
 * <p>
 * <b>Variant Integration:</b> Integrates with the dynamic variant system through
 * InternalEntityType features, enabling flexible texture/model/animator selection
 * without hardcoded enum dependencies.
 * <p>
 * <b>Thread Safety:</b> EntityDataAccessor fields are synchronized automatically by
 * Minecraft's entity data system. NBT operations should be performed on server thread.
 */
public abstract class InternalEntity extends TamableAnimal {

    // -- Entity Data Accessors --

    /**
     * <p>Synchronized the entity state identifier.<p>
     * <p>
     * <b>State System:</b> Manages behavioral states like REST, MOVE, COMBAT, etc.
     * Different from texture variants - controls AI behavior patterns.
     */
    protected static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);

    /**
     * <p>Synchronized texture variant identifier.<p>
     * <p>
     * <b>Architecture:</b> Uses string-based variant keys instead of enum IDs
     * for flexibility with the dynamic variant system.
     */
    protected static final EntityDataAccessor<String> TEXTURE_VARIANT = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.STRING);

    /**
     * <p>Synchronized model variant identifier.<p>
     */
    protected static final EntityDataAccessor<String> MODEL_VARIANT = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.STRING);

    /**
     * <p>Synchronized animator variant identifier.<p>
     */
    protected static final EntityDataAccessor<String> ANIMATOR_VARIANT = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.STRING);

    /**
     * <p>Synchronized notification preference.<p>
     * <p>
     * <b>Design Decision:</b> Common preference that applies to both robots and monsters
     * for consistent user experience across entity types.
     */
    protected static final EntityDataAccessor<Boolean> NOTIFICATION_ENABLED = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.BOOLEAN);

    // -- Entity Type Reference --

    /**
     * <p>Reference to entity type configuration.<p>
     * <p>
     * <b>Architecture:</b> Provides access to variant features and resource resolution
     * without coupling to specific entity type implementations.
     */
    public InternalEntityType<?> nativeEntity;

    // -- Properties --

    // STATE

    /**
     * Returns current state ID.
     *
     * @return state ID
     */
    public int getCurrentStateID() {
        int value = EntityState.Standby.getId();
        try {value = this.entityData.get(STATE);}
        catch (Exception ignored) {}
        return value;
    } // getCurrentStateID ()

    /**
     * Returns current state.
     *
     * @return monster state enum
     */
    public EntityState getCurrentState() {
        EntityState value = EntityState.Standby;
        try {value = EntityState.byId(this.entityData.get(STATE));}
        catch (Exception ignored) {}
        return value;
    } // getCurrentState ()

    /**
     * Sets current state.
     * <p>
     * <b>State Impact:</b> Changes AI behavior patterns and animation states.
     * REST state typically makes monster sit, MOVE state enables following, etc.
     *
     * @param state new monster state
     */
    public void setCurrentState(EntityState state){
        this.entityData.set(STATE, state.getId());
        onStateChanged(state);
    } // setCurrentMode ()

    /**
     * Sets current state by ID.
     *
     * @param stateId new  state ID
     */
    public void setCurrentState(int stateId){
        this.entityData.set(STATE, stateId);
        onStateChanged(EntityState.byId(stateId));
    } // setCurrentState ()

    /**
     * Called when monster state changes.
     * <p>
     * <b>Extensibility:</b> Subclasses can override to add state-specific behaviors,
     * animations, or AI modifications.
     *
     * @param newState new monster state
     */
    protected void onStateChanged(EntityState newState) {
        // Handle common state changes
        switch (newState) {
            case Standby:
                if (isTame()) {
                    setOrderedToSit(true);
                    setInSittingPose(true);
                    setTarget(null);
                    getNavigation().stop();
                }
                break;
            case Follow:
                if (isTame()) {
                    setOrderedToSit(false);
                    setInSittingPose(false);
                }
                break;
            default: break;
        }
    } // onStateChanged ()

    // -- Constructor --

    /**
     * Creates generic internal entity with specified entity type and world.
     * <p>
     * <b>State Impact:</b> Initializes entity data accessors with default values.
     * Subclasses should call this constructor and then set their nativeEntity reference.
     *
     * @param entityType Minecraft entity type for registration
     * @param world world instance where entity exists
     */
    protected InternalEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
    } // Constructor: InternalEntity ()

    // -- Entity Data Initialization --

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(STATE, EntityState.Follow.getId());
        builder.define(TEXTURE_VARIANT, "default");
        builder.define(MODEL_VARIANT, "default");
        builder.define(ANIMATOR_VARIANT, "default");
        builder.define(NOTIFICATION_ENABLED, true);
    } // defineSynchedData ()

    // -- Variant Access Methods --

    /**
     * Returns current texture variant key.
     * <p>
     * <b>Thread Safety:</b> Safe to call from any thread due to EntityDataAccessor synchronization.
     *
     * @return texture variant key, defaults to "default"
     */
    public String getTextureVariant() {
        try {
            return this.entityData.get(TEXTURE_VARIANT);
        } catch (Exception e) {
            return "default";
        }
    } // getTextureVariant ()

    /**
     * Sets current texture variant key.
     * <p>
     * <b>Validation:</b> Checks if variant is available for this entity before setting.
     * Invalid variants are ignored to prevent client-server desync.
     *
     * @param variantKey texture variant key to set
     */
    public void setTextureVariant(String variantKey) {
        if (nativeEntity != null && isValidTextureVariant(variantKey)) {
            this.entityData.set(TEXTURE_VARIANT, variantKey);
        }
    } // setTextureVariant ()

    /**
     * Returns current model variant key.
     *
     * @return model variant key, defaults to "default"
     */
    public String getModelVariant() {
        try {
            return this.entityData.get(MODEL_VARIANT);
        } catch (Exception e) {
            return "default";
        }
    } // getModelVariant ()

    /**
     * Sets current model variant key.
     *
     * @param variantKey model variant key to set
     */
    public void setModelVariant(String variantKey) {
        if (nativeEntity != null && isValidModelVariant(variantKey)) {
            this.entityData.set(MODEL_VARIANT, variantKey);
        }
    } // setModelVariant ()

    /**
     * Returns current animator variant key.
     *
     * @return animator variant key, defaults to "default"
     */
    public String getAnimatorVariant() {
        try {
            return this.entityData.get(ANIMATOR_VARIANT);
        } catch (Exception e) {
            return "default";
        }
    } // getAnimatorVariant ()

    /**
     * Sets current animator variant key.
     *
     * @param variantKey animator variant key to set
     */
    public void setAnimatorVariant(String variantKey) {
        if (nativeEntity != null && isValidAnimatorVariant(variantKey)) {
            this.entityData.set(ANIMATOR_VARIANT, variantKey);
        }
    } // setAnimatorVariant ()

    // -- Resource Resolution Methods --

    /**
     * Returns current texture resource location.
     * <p>
     * <b>Variant Integration:</b> Uses entity type's texture variant feature to resolve
     * the current texture variant to an actual resource location.
     *
     * @return texture resource location, or null if not available
     */
    public ResourceLocation getCurrentTexture() {
        if (nativeEntity == null) return null;
        ITextureVariant variant = nativeEntity.getTextureVariant(nativeEntity.getKey(), getTextureVariant());
        return variant != null ? variant.getResource(nativeEntity.getKey()) : null;
    } // getCurrentTexture ()

    /**
     * Returns current model resource location.
     *
     * @return model resource location, or null if not available
     */
    public ResourceLocation getCurrentModel() {
        if (nativeEntity == null) return null;
        
        IModelVariant variant = nativeEntity.getModelVariant(nativeEntity.getKey(), getModelVariant());
        return variant != null ? variant.getResource(nativeEntity.getKey()) : null;
    } // getCurrentModel ()

    /**
     * Returns current animator resource location.
     *
     * @return animator resource location, or null if not available
     */
    public ResourceLocation getCurrentAnimator() {
        if (nativeEntity == null) return null;
        
        IAnimatorVariant variant = nativeEntity.getAnimatorVariant(nativeEntity.getKey(), getAnimatorVariant());
        return variant != null ? variant.getResource(nativeEntity.getKey()) : null;
    } // getCurrentAnimator ()

    // -- Variant Validation Methods --

    /**
     * Checks if texture variant is valid for this entity.
     *
     * @param variantKey variant key to validate
     * @return true if variant is available, false otherwise
     */
    protected boolean isValidTextureVariant(String variantKey) {
        if (nativeEntity == null || variantKey == null) return false;
        
        return nativeEntity.getFeature(TextureVariantFeature.class)
                .map(feature -> feature.hasVariant(nativeEntity.getKey(), variantKey))
                .orElse(false);
    } // isValidTextureVariant ()

    /**
     * Checks if model variant is valid for this entity.
     *
     * @param variantKey variant key to validate
     * @return true if variant is available, false otherwise
     */
    protected boolean isValidModelVariant(String variantKey) {
        if (nativeEntity == null || variantKey == null) return false;
        
        return nativeEntity.getFeature(ModelVariantFeature.class)
                .map(feature -> feature.hasVariant(nativeEntity.getKey(), variantKey))
                .orElse(false);
    } // isValidModelVariant ()

    /**
     * Checks if animator variant is valid for this entity.
     *
     * @param variantKey variant key to validate
     * @return true if variant is available, false otherwise
     */
    protected boolean isValidAnimatorVariant(String variantKey) {
        if (nativeEntity == null || variantKey == null) return false;
        
        return nativeEntity.getFeature(AnimatorVariantFeature.class)
                .map(feature -> feature.hasVariant(nativeEntity.getKey(), variantKey))
                .orElse(false);
    } // isValidAnimatorVariant ()

    // -- Notification System --

    /**
     * Returns notification preference.
     *
     * @return true if notifications are enabled, false otherwise
     */
    public boolean isNotificationEnabled() {
        try {
            return this.entityData.get(NOTIFICATION_ENABLED);
        } catch (Exception e) {
            return true;
        }
    } // isNotificationEnabled ()

    /**
     * Sets notification preference.
     *
     * @param enabled true to enable notifications, false to disable
     */
    public void setNotificationEnabled(boolean enabled) {
        this.entityData.set(NOTIFICATION_ENABLED, enabled);
    } // setNotificationEnabled ()

    // -- Entity Lifecycle Methods --

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData) {
        // Initialize with random texture variant if available
        initializeRandomVariants();
        return super.finalizeSpawn(world, difficulty, spawnReason, entityData);
    } // finalizeSpawn ()

    /**
     * Initializes entity with random variants from available options.
     * <p>
     * <b>Design Decision:</b> Uses entity type's variant features to select random
     * variants, providing variety while respecting entity-specific variant restrictions.
     */
    protected void initializeRandomVariants() {
        if (nativeEntity == null) return;

        // Set random texture variant
        nativeEntity.getFeature(TextureVariantFeature.class)
                .ifPresent(feature -> {
                    ITextureVariant randomVariant = feature.getRandomVariant(nativeEntity.getKey());
                    if (randomVariant != null) {
                        setTextureVariant(randomVariant.getKey());
                    }
                });

        // Set random model variant
        nativeEntity.getFeature(ModelVariantFeature.class)
                .ifPresent(feature -> {
                    IModelVariant randomVariant = feature.getRandomVariant(nativeEntity.getKey());
                    if (randomVariant != null) {
                        setModelVariant(randomVariant.getKey());
                    }
                });

        // Set random animator variant
        nativeEntity.getFeature(AnimatorVariantFeature.class)
                .ifPresent(feature -> {
                    IAnimatorVariant randomVariant = feature.getRandomVariant(nativeEntity.getKey());
                    if (randomVariant != null) {
                        setAnimatorVariant(randomVariant.getKey());
                    }
                });
    } // initializeRandomVariants ()

    // -- NBT Serialization --

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("StateId", getCurrentStateID());
        nbt.putString("TextureVariant", getTextureVariant());
        nbt.putString("ModelVariant", getModelVariant());
        nbt.putString("AnimatorVariant", getAnimatorVariant());
        nbt.putBoolean("NotificationEnabled", isNotificationEnabled());
    } // addAdditionalSaveData ()

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("StateId")) setCurrentState(nbt.getInt("StateId"));
        if (nbt.contains("TextureVariant")) setTextureVariant(nbt.getString("TextureVariant"));
        if (nbt.contains("ModelVariant")) setModelVariant(nbt.getString("ModelVariant"));
        if (nbt.contains("AnimatorVariant")) setAnimatorVariant(nbt.getString("AnimatorVariant"));
        if (nbt.contains("NotificationEnabled")) setNotificationEnabled(nbt.getBoolean("NotificationEnabled"));
    } // readAdditionalSaveData ()

    // -- Sound System Integration --

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        // Subclasses can override to provide entity-specific sounds
        return super.getHurtSound(damageSource);
    } // getHurtSound ()

    @Override
    protected SoundEvent getDeathSound() {
        // Subclasses can override to provide entity-specific sounds
        return super.getDeathSound();
    } // getDeathSound ()

    @Override
    protected SoundEvent getAmbientSound() {
        // Subclasses can override to provide entity-specific sounds
        return super.getAmbientSound();
    } // getAmbientSound ()

    // -- Interaction Framework --

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Handle common interactions
        InteractionResult result = handleCommonInteractions(player, hand, stack);
        if (result != InteractionResult.PASS) {
            return result;
        }
        
        // Delegate to subclass-specific interactions
        return handleSpecificInteractions(player, hand, stack);
    } // mobInteract ()

    /**
     * Handles common interactions shared across all entity types.
     * <p>
     * <b>Extensibility:</b> Subclasses can override to add entity-type-specific
     * common interactions while maintaining base functionality.
     *
     * @param player interacting player
     * @param hand interaction hand
     * @param stack item stack in hand
     * @return interaction result, PASS if no common interaction handled
     */
    protected InteractionResult handleCommonInteractions(Player player, InteractionHand hand, ItemStack stack) {
        // Common interactions can be added here
        // For now, just pass to specific interactions
        return InteractionResult.PASS;
    } // handleCommonInteractions ()

    /**
     * Handles entity-type-specific interactions.
     * <p>
     * <b>Abstract Method:</b> Subclasses must implement to provide their specific
     * interaction behaviors (robot commands, monster feeding, etc.).
     *
     * @param player interacting player
     * @param hand interaction hand
     * @param stack item stack in hand
     * @return interaction result
     */
    protected abstract InteractionResult handleSpecificInteractions(Player player, InteractionHand hand, ItemStack stack);

    // -- Child Creation --

    @Override
    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob otherParent) {
        // Most HZLib entities don't breed, but subclasses can override
        return null;
    } // getBreedOffspring ()

    // -- Utility Methods --

    /**
     * Checks if this entity is owned by the specified player.
     * <p>
     * <b>Null Safety:</b> Handles null player and owner references gracefully.
     *
     * @param player player to check ownership against
     * @return true if player owns this entity, false otherwise
     */
    public boolean isOwnedBy(@Nullable Player player) {
        if (player == null) return false;
        return Objects.equals(getOwnerUUID(), player.getUUID());
    } // isOwnedBy ()

    /**
     * Gets entity type key for variant resolution.
     * <p>
     * <b>Null Safety:</b> Returns "unknown" if nativeEntity is not set.
     *
     * @return entity type key
     */
    public String getEntityTypeKey() {
        return nativeEntity != null ? nativeEntity.getKey() : "unknown";
    } // getEntityTypeKey ()

} // Class: InternalEntity