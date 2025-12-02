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
import net.msymbios.llovelyr.common.entity.NativeEntityType;
import net.msymbios.llovelyr.framework.entity.enums.*;
import net.msymbios.llovelyr.common.entity.enums.EntityVariant;
import net.msymbios.llovelyr.common.utils.internal.*;
import net.msymbios.llovelyr.framework.entity.data.CombatStats;
import net.msymbios.llovelyr.framework.entity.data.ProtectionStats;
import net.msymbios.llovelyr.framework.entity.data.EnchantmentStats;
import net.msymbios.llovelyr.framework.registry.RobotRegistryEntry;
import net.msymbios.llovelyr.LovelyLegacy;
import net.msymbios.llovelyr.lib.entity.InternalEntityType;
import net.msymbios.llovelyr.lib.entity.data.CombatStatsNBT;
import net.msymbios.llovelyr.lib.entity.data.ProtectionStatsNBT;
import net.msymbios.llovelyr.lib.entity.data.EnchantmentStatsNBT;
import net.msymbios.llovelyr.framework.utils.Version;
import net.msymbios.llovelyr.lib.entity.features.*;
import net.msymbios.llovelyr.lib.registry.RobotRegistryManager;
import net.msymbios.llovelyr.lib.utils.interfaces.IReadWriteNBT;
import net.msymbios.llovelyr.source.LovelyConfigs;
import net.msymbios.llovelyr.common.shared.LovelyIdentifier;
import net.msymbios.llovelyr.source.LovelyItems;
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
    public InternalEntityType<?> nativeEntity;
    protected EntityModel model = EntityModel.Default;

    // -- Stat Objects --
    
    /**
     * <p>Encapsulates combat-related statistics.<p>
     * <p>
     * <b>Architecture:</b> Replaces scattered level/exp/hp fields with cohesive
     * data object, enabling feature-based attribute calculations.
     */
    protected CombatStats combatStats;
    
    /**
     * <p>Encapsulates protection statistics.<p>
     * <p>
     * <b>Architecture:</b> Centralizes fire/fall/blast/projectile protection
     * levels with validation support.
     */
    protected ProtectionStats protectionStats;
    
    /**
     * <p>Encapsulates enchantment statistics.<p>
     * <p>
     * <b>Architecture:</b> Manages looting/sharpness/knockback enchantment
     * levels calculated from robot level.
     */
    protected EnchantmentStats enchantmentStats;

    // -- NBT Handlers --
    
    /**
     * <p>Handles NBT serialization for combat stats.<p>
     * <p>
     * <b>Design Decision:</b> Separates serialization logic from data model,
     * enabling format changes without modifying stat classes.
     */
    protected CombatStatsNBT combatStatsNBT;
    
    /**
     * <p>Handles NBT serialization for protection stats.<p>
     */
    protected ProtectionStatsNBT protectionStatsNBT;
    
    /**
     * <p>Handles NBT serialization for enchantment stats.<p>
     */
    protected EnchantmentStatsNBT enchantmentStatsNBT;

    // -- Properties --

    // TEXTURE

    public ResourceLocation getTexture() { 
        // Use NativeEntityType's color texture system
        if (nativeEntity instanceof NativeEntityType) {
            return ((NativeEntityType) nativeEntity).getColorTexture(EntityTexture.byId(getTextureID()));
        }
        // Fallback for non-robot entities
        return nativeEntity.getTextures().get(EntityVariantTexture.DEFAULT);
    } // getTexture ()

    public int getTextureID() {
        int value = 0;  // Default to WHITE
        // Use NativeEntityType's random color system
        if (nativeEntity instanceof NativeEntityType) {
            value = ((NativeEntityType) nativeEntity).getRandomColorId();
        }
        try {value = this.entityData.get(TEXTURE_ID);}
        catch (Exception ignored) {}
        return value;
    } // getTextureID ()

    public void setTexture(int value) { 
        // Use NativeEntityType's color checking system
        if (nativeEntity instanceof NativeEntityType) {
            if (((NativeEntityType) nativeEntity).hasColor(EntityTexture.byId(value))) {
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

    // -- Simplified Stat Accessors --

    /**
     * <p>Gets robot's current level.<p>
     * <p>
     * <b>Architecture:</b> Delegates to combatStats, providing clean interface
     * without exposing internal stat object structure.
     *
     * @return current robot level
     */
    public int getLevel() {
        return combatStats.getLevel();
    } // getLevel ()

    /**
     * <p>Sets robot's level and recalculates attributes.<p>
     * <p>
     * <b>State Impact:</b> Triggers full attribute recalculation including HP,
     * attack, defense, armor, enchantments, and optional protection upgrades.
     *
     * @param level new robot level
     */
    public void setLevel(int level) {
        combatStats.setLevel(level);
        recalculateAttributes();
    } // setLevel ()

    /**
     * <p>Gets robot's current experience points.<p>
     *
     * @return current experience
     */
    public int getExperience() {
        return combatStats.getExperience();
    } // getExperience ()

    /**
     * <p>Sets robot's experience and triggers auto-level-up if sufficient.<p>
     * <p>
     * <b>Behavior:</b> Automatically consumes experience and increments level
     * when sufficient exp is available, repeating until insufficient exp remains.
     *
     * @param experience new experience value
     */
    public void setExperience(int experience) {
        combatStats.setExperience(experience);
        // Auto-level-up logic will be handled by command system
    } // setExperience ()

    // Protection accessors

    /**
     * <p>Gets fire protection level.<p>
     *
     * @return fire protection level
     */
    public int getFireProtection() {
        return protectionStats.getFireProtection();
    } // getFireProtection ()

    /**
     * <p>Sets fire protection level.<p>
     *
     * @param level new fire protection level
     */
    public void setFireProtection(int level) {
        protectionStats.setFireProtection(level);
    } // setFireProtection ()

    /**
     * <p>Gets fall protection level.<p>
     *
     * @return fall protection level
     */
    public int getFallProtection() {
        return protectionStats.getFallProtection();
    } // getFallProtection ()

    /**
     * <p>Sets fall protection level.<p>
     *
     * @param level new fall protection level
     */
    public void setFallProtection(int level) {
        protectionStats.setFallProtection(level);
    } // setFallProtection ()

    /**
     * <p>Gets blast protection level.<p>
     *
     * @return blast protection level
     */
    public int getBlastProtection() {
        return protectionStats.getBlastProtection();
    } // getBlastProtection ()

    /**
     * <p>Sets blast protection level.<p>
     *
     * @param level new blast protection level
     */
    public void setBlastProtection(int level) {
        protectionStats.setBlastProtection(level);
    } // setBlastProtection ()

    /**
     * <p>Gets projectile protection level.<p>
     *
     * @return projectile protection level
     */
    public int getProjectileProtection() {
        return protectionStats.getProjectileProtection();
    } // getProjectileProtection ()

    /**
     * <p>Sets projectile protection level.<p>
     *
     * @param level new projectile protection level
     */
    public void setProjectileProtection(int level) {
        protectionStats.setProjectileProtection(level);
    } // setProjectileProtection ()

    // Enchantment accessors

    /**
     * <p>Gets looting enchantment level.<p>
     *
     * @return looting level
     */
    public int getLootingLevel() {
        return enchantmentStats.getLootingLevel();
    } // getLootingLevel ()

    /**
     * <p>Sets looting enchantment level.<p>
     *
     * @param level new looting level
     */
    public void setLootingLevel(int level) {
        enchantmentStats.setLootingLevel(level);
    } // setLootingLevel ()

    /**
     * <p>Gets sharpness enchantment level.<p>
     *
     * @return sharpness level
     */
    public int getSharpnessLevel() {
        return enchantmentStats.getSharpnessLevel();
    } // getSharpnessLevel ()

    /**
     * <p>Sets sharpness enchantment level.<p>
     *
     * @param level new sharpness level
     */
    public void setSharpnessLevel(int level) {
        enchantmentStats.setSharpnessLevel(level);
    } // setSharpnessLevel ()

    /**
     * <p>Gets knockback enchantment level.<p>
     *
     * @return knockback level
     */
    public int getKnockbackLevel() {
        return enchantmentStats.getKnockbackLevel();
    } // getKnockbackLevel ()

    /**
     * <p>Sets knockback enchantment level.<p>
     *
     * @param level new knockback level
     */
    public void setKnockbackLevel(int level) {
        enchantmentStats.setKnockbackLevel(level);
    } // setKnockbackLevel ()

    // -- Attribute Recalculation --

    /**
     * <p>Recalculates all entity attributes based on current level and features.<p>
     * <p>
     * <b>Architecture:</b> Coordinates feature-based calculations for combat stats,
     * enchantments, and protections. Delegates to attached features on entity type.
     * <p>
     * <b>State Impact:</b> Updates HP, attack, defense, armor, armor toughness,
     * enchantment levels, and optionally auto-upgrades protections based on level.
     * <p>
     * <b>Design Decision:</b> Centralized recalculation ensures consistency across
     * level-ups, NBT loading, and command-based stat changes.
     */
    protected void recalculateAttributes() {
        // Get CombatLevelFeature from entity type
        if (nativeEntity instanceof InternalEntityType) {
            InternalEntityType<?> entityType =
                (InternalEntityType<?>) nativeEntity;
            
            // Calculate combat attributes if feature exists
            if (entityType.getFeature(CombatLevelFeature.class).isPresent()) {
                CombatLevelFeature combatFeature = entityType.getFeature(CombatLevelFeature.class).get();
                
                int level = combatStats.getLevel();
                
                // Calculate HP, attack, defense
                int maxHp = combatFeature.calculateHp(level);
                int attack = combatFeature.calculateAttack(level);
                int defense = combatFeature.calculateDefense(level);
                
                // Calculate armor and armor toughness
                double armor = combatFeature.calculateArmor(level);
                double armorToughness = combatFeature.calculateArmorToughness(level);
                
                // Update combat stats
                combatStats.setMaxHp(maxHp);
                combatStats.setAttack(attack);
                combatStats.setDefense(defense);
                
                // Update entity attributes
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHp);
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(attack);
                this.getAttribute(Attributes.ARMOR).setBaseValue(armor);
                this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(armorToughness);
                
                // Ensure current HP doesn't exceed new max HP
                if (this.getHealth() > maxHp) {
                    this.setHealth(maxHp);
                }
                combatStats.setCurrentHp((int) this.getHealth());
            }
            
            // Calculate enchantment levels if feature exists
            if (entityType.getFeature(EnchantmentFeature.class).isPresent()) {
                EnchantmentFeature enchantFeature = entityType.getFeature(EnchantmentFeature.class).get();
                
                int level = combatStats.getLevel();
                
                // Calculate enchantment levels
                int looting = enchantFeature.calculateLooting(level);
                int sharpness = enchantFeature.calculateSharpness(level);
                int knockback = enchantFeature.calculateKnockback(level);
                
                // Update enchantment stats
                enchantmentStats.setLootingLevel(looting);
                enchantmentStats.setSharpnessLevel(sharpness);
                enchantmentStats.setKnockbackLevel(knockback);
            }
            
            // Auto-upgrade protections if feature exists and enabled
            if (entityType.getFeature(ProtectionFeature.class).isPresent()) {
                ProtectionFeature protectionFeature = entityType.getFeature(ProtectionFeature.class).get();
                
                int level = combatStats.getLevel();
                
                // Calculate auto-protection levels (only if auto-upgrade enabled)
                int fireProtection = protectionFeature.calculateAutoFireProtection(level, protectionStats.getFireProtection());
                int fallProtection = protectionFeature.calculateAutoFallProtection(level, protectionStats.getFallProtection());
                int blastProtection = protectionFeature.calculateAutoBlastProtection(level, protectionStats.getBlastProtection());
                int projectileProtection = protectionFeature.calculateAutoProjectileProtection(level, protectionStats.getProjectileProtection());
                
                // Update protection stats (only if auto-upgrade increased them)
                if (fireProtection > protectionStats.getFireProtection()) {
                    protectionStats.setFireProtection(fireProtection);
                }
                if (fallProtection > protectionStats.getFallProtection()) {
                    protectionStats.setFallProtection(fallProtection);
                }
                if (blastProtection > protectionStats.getBlastProtection()) {
                    protectionStats.setBlastProtection(blastProtection);
                }
                if (projectileProtection > protectionStats.getProjectileProtection()) {
                    protectionStats.setProjectileProtection(projectileProtection);
                }
            }
        }
    } // recalculateAttributes ()

    // -- Registry Lifecycle --

    /**
     * <p>Registers robot in owner's registry.<p>
     * <p>
     * <b>Architecture:</b> Called when robot is tamed, establishing tracking
     * relationship between owner and robot for spawn limits and management commands.
     * <p>
     * <b>State Impact:</b> Creates registry entry with WeakReference to this entity,
     * enabling live position/health/stats access without cached data synchronization.
     * <p>
     * <b>Design Decision:</b> Only registers on server side to prevent client-side
     * registry pollution and ensure single source of truth.
     */
    protected void registerRobot() {
        // Only register on server side
        if (this.level().isClientSide || !this.isTame() || this.getOwnerUUID() == null) {
            return;
        }
        
        try {
            ServerLevel serverLevel = (ServerLevel) this.level();
            RobotRegistryManager.getRegistry(serverLevel)
                .registerRobot(new RobotRegistryEntry(
                    this.getUUID(),
                    this.getOwnerUUID(),
                    this,
                    nativeEntity.getKey()
                ));
        } catch (Exception e) {
            // Log error but don't fail taming
            LovelyLegacy.LOGGER.error("Failed to register robot {} for owner {}", 
                this.getUUID(), this.getOwnerUUID(), e);
        }
    } // registerRobot ()

    /**
     * <p>Unregisters robot from owner's registry.<p>
     * <p>
     * <b>Architecture:</b> Called when robot is removed or dies, cleaning up
     * tracking relationship and freeing registry slot for new robots.
     * <p>
     * <b>State Impact:</b> Removes registry entry. Robot will no longer appear
     * in owner list commands or count toward spawn limits.
     */
    protected void unregisterRobot() {
        // Only unregister on server side
        if (this.level().isClientSide) {
            return;
        }
        
        try {
            ServerLevel serverLevel = (ServerLevel) this.level();
            RobotRegistryManager.getRegistry(serverLevel)
                .unregisterRobot(this.getUUID());
        } catch (Exception e) {
            // Log error but don't fail removal
            LovelyLegacy.LOGGER.error("Failed to unregister robot {}", this.getUUID(), e);
        }
    } // unregisterRobot ()

    /**
     * <p>Updates registry timestamp for this robot.<p>
     * <p>
     * <b>Architecture:</b> Called periodically (every 20 ticks) to maintain
     * "last seen" timestamp for offline detection and cleanup.
     * <p>
     * <b>Performance:</b> Lightweight operation (O(1) lookup + timestamp update).
     * Batched to once per second to minimize overhead.
     */
    protected void updateRegistryTimestamp() {
        // Only update on server side
        if (this.level().isClientSide || !this.isTame() || this.getOwnerUUID() == null) {
            return;
        }
        
        try {
            ServerLevel serverLevel = (ServerLevel) this.level();
            RobotRegistryEntry entry =
                RobotRegistryManager.getRegistry(serverLevel).getRobotById(this.getUUID());
            
            if (entry != null) {
                entry.updateTimestamp();
            }
        } catch (Exception e) {
            // Silently fail - timestamp update is not critical
        }
    } // updateRegistryTimestamp ()

    // -- Constructor --

    protected InternalEntity(EntityType<? extends TamableAnimal> entityType, Level world, InternalEntityType<?> nativeEntityType) {
        super(entityType, world);
        this.nativeEntity = nativeEntityType;

        // Initialize stat objects
        this.combatStats = new CombatStats();
        this.protectionStats = new ProtectionStats();
        this.enchantmentStats = new EnchantmentStats();
        
        // Initialize NBT handlers
        this.combatStatsNBT = new CombatStatsNBT(combatStats);
        this.protectionStatsNBT = new ProtectionStatsNBT(protectionStats);
        this.enchantmentStatsNBT = new EnchantmentStatsNBT(enchantmentStats);

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
        // Use NativeEntityType's random color system
        if (nativeEntity instanceof NativeEntityType) {
            this.setTexture(((NativeEntityType) nativeEntity).getRandomColorId());
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

        // Save stat objects using NBT handlers
        CompoundTag statsData = new CompoundTag();
        combatStatsNBT.writeToNBT(statsData);
        dataNBT.put("CombatStats", statsData);
        
        CompoundTag protectionData = new CompoundTag();
        protectionStatsNBT.writeToNBT(protectionData);
        dataNBT.put("ProtectionStats", protectionData);
        
        CompoundTag enchantmentData = new CompoundTag();
        enchantmentStatsNBT.writeToNBT(enchantmentData);
        dataNBT.put("EnchantmentStats", enchantmentData);

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

        // Check if new format exists
        boolean hasNewFormat = dataNBT.contains("CombatStats") || 
                              dataNBT.contains("ProtectionStats") || 
                              dataNBT.contains("EnchantmentStats");
        
        if (hasNewFormat) {
            // Load stat objects using NBT handlers (new format)
            net.msymbios.llovelyr.framework.utils.Version version = new net.msymbios.llovelyr.framework.utils.Version("1.0.0");
            
            if (dataNBT.contains("CombatStats")) {
                CompoundTag statsData = dataNBT.getCompound("CombatStats");
                combatStatsNBT.readFromNBT(statsData, version);
            }
            
            if (dataNBT.contains("ProtectionStats")) {
                CompoundTag protectionData = dataNBT.getCompound("ProtectionStats");
                protectionStatsNBT.readFromNBT(protectionData, version);
            }
            
            if (dataNBT.contains("EnchantmentStats")) {
                CompoundTag enchantmentData = dataNBT.getCompound("EnchantmentStats");
                enchantmentStatsNBT.readFromNBT(enchantmentData, version);
            }
        } else {
            // Migrate from legacy format
            LovelyLegacy.LOGGER.warn("Migrating robot {} from legacy NBT format to new format", this.getUUID());
            
            // Migrate combat stats (if they exist in legacy format)
            // Note: Legacy format may have stored these differently or not at all
            // Use default values for missing fields
            if (dataNBT.contains("Level")) {
                combatStats.setLevel(dataNBT.getInt("Level"));
            }
            if (dataNBT.contains("Experience")) {
                combatStats.setExperience(dataNBT.getInt("Experience"));
            }
            
            // Current HP will be set from entity health
            combatStats.setCurrentHp((int) this.getHealth());
            combatStats.setMaxHp((int) this.getMaxHealth());
            
            // Migrate protection stats (if they exist in legacy format)
            if (dataNBT.contains("FireProtection")) {
                protectionStats.setFireProtection(dataNBT.getInt("FireProtection"));
            }
            if (dataNBT.contains("FallProtection")) {
                protectionStats.setFallProtection(dataNBT.getInt("FallProtection"));
            }
            if (dataNBT.contains("BlastProtection")) {
                protectionStats.setBlastProtection(dataNBT.getInt("BlastProtection"));
            }
            if (dataNBT.contains("ProjectileProtection")) {
                protectionStats.setProjectileProtection(dataNBT.getInt("ProjectileProtection"));
            }
            
            // Enchantment stats will be calculated from level during recalculation
            // No need to migrate as they're derived values
            
            LovelyLegacy.LOGGER.info("Successfully migrated robot {} to new format", this.getUUID());
        }

        // Recalculate attributes after loading to ensure consistency
        recalculateAttributes();

        //CompoundTag entityData = dataNBT.getCompound("EntityData");
        //readFromNBT(entityData, ObjectUtil.coalesce(new Version(entityData.getString("VersionNBT")), LovelyRobotEntity.VERSION));
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

    public ItemStack setDropItem() {
        return new ItemStack(LovelyItems.ROBOT_CORE.get(), 1);
    } // setDropItem

    protected abstract void handleItemDrop();

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
        // Set ownership without taming particles (spawning, not taming)
        this.tame(player);
        this.setTame(true);
        this.setOrderedToSit(false);

        // Spawn POOF particles (spawn effect)
        InternalParticle.Poof(this);

        // Play spawn sound effect (totem activation sound) - volume scales with entity size
        float volume = (float) Math.max(0.5F, Math.min(2.0F, this.getBbWidth() * this.getBbHeight()));
        this.level().playSound(
                null,
                this.blockPosition(),
                net.minecraft.sounds.SoundEvents.TOTEM_USE,
                net.minecraft.sounds.SoundSource.NEUTRAL,
                volume,
                1.2F
        );

        InternalLogic.displayInfo(this, LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_OWNER).append(Component.literal(": " + player.getName().getString())), true);
        
        // Register robot in owner's registry
        registerRobot();
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
