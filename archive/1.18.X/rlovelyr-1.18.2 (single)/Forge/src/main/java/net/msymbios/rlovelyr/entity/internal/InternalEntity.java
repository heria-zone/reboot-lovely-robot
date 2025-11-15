package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.common.util.ObjectUtil;
import net.msymbios.rlovelyr.common.util.interfaces.IReadWriteNBT;
import net.msymbios.rlovelyr.common.util.internal.Version;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.internal.enums.*;
import net.msymbios.rlovelyr.item.utils.Utility;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Objects;

import static net.msymbios.rlovelyr.item.utils.Utility.invertBoolean;

public abstract class InternalEntity extends TamableAnimal implements IEntityAdditionalSpawnData, IReadWriteNBT {

    // -- Variables --

    protected static final EntityDataAccessor<Integer> TEXTURE_ID = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> AUTO_ATTACK = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.BOOLEAN);

    protected static final EntityDataAccessor<Integer> MAX_LEVEL = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> LEVEL = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> EXP = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);

    protected static final EntityDataAccessor<Integer> FIRE_PROTECTION = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> FALL_PROTECTION = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> BLAST_PROTECTION = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> PROJECTILE_PROTECTION = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.INT);

    protected static final EntityDataAccessor<Float> BASE_X = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> BASE_Y = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> BASE_Z = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.FLOAT);

    protected static final EntityDataAccessor<Boolean> NOTIFICATION = SynchedEntityData.defineId(InternalEntity.class, EntityDataSerializers.BOOLEAN);

    protected int waryTimer = 0, autoHealTimer = 0, sitPoseChangeTimer = 0;
    protected boolean combatMode = false, autoHeal = false;
    public NativeEntity nativeEntity;
    protected EntityModel model = EntityModel.Default;

    // -- Properties --

    // TEXTURE

    public ResourceLocation getTexture() { return nativeEntity.getTexture(EntityTexture.byId(getTextureID())); } // getTexture ()

    public int getTextureID() {
        int value = nativeEntity.getRandomTextureID();
        try {value = this.entityData.get(TEXTURE_ID);}
        catch (Exception ignored) {}
        return value;
    } // getTextureID ()

    public void setTexture(int value) { if(nativeEntity.checkTexture(EntityTexture.byId(value))) this.entityData.set(TEXTURE_ID, value); } // setTexture ()

    public void setTexture(EntityTexture value) { setTexture(value.getId()); } // setTexture ()

    // MODEL

    public ResourceLocation getCurrentModel() { return nativeEntity.getModel(model); } // getCurrentModel ()

    // ANIMATOR

    public ResourceLocation getAnimator() { return nativeEntity.getAnimator(); } // getAnimator ()

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

    public int getMaxLevel() { return nativeEntity.getMaxLevel(); } // getMaxLevel ()

    public int getHp() { return InternalLogic.calculateHp(this.getCurrentLevel(), (int)this.nativeEntity.getMaxHealth()); } // getHp ()

    public int getAttackDamage() { return InternalLogic.calculateAttack(this.getCurrentLevel(), (int)nativeEntity.getAttackDamage()); } // getAttackDamage ()

    public int getArmorLevel() {
        var defence = InternalLogic.calculateDefense(this.getCurrentLevel(), (int)nativeEntity.getArmour());
        return (int) InternalLogic.calculateArmor(defence);
    } // getArmorLevel ()

    public int getArmorToughnessLevel() { return (int) InternalLogic.calculateArmorToughness(getArmorLevel()); } // getArmorToughnessLevel ()

    public int getLooting() {return InternalLogic.calculateLooting(this.getCurrentLevel());} // getLooting ()

    public int getCurrentLevel() {
        var level = 0;
        try {level = this.entityData.get(LEVEL);}
        catch (Exception ignored){}
        return level;
    } // getCurrentLevel ()

    public void setCurrentLevel(int value){
        this.entityData.set(LEVEL, value);
        InternalLogic.handleLevel(this, getHp(), getAttackDamage(), getArmorLevel(), getArmorToughnessLevel());
    } // setCurrentLevel ()

    public int getExp(){
        int value = 1;
        try {value = this.entityData.get(EXP);}
        catch (Exception ignored){}
        return value;
    } // getExp ()

    public void setExp(int value){
        this.entityData.set(EXP, value);
    } // setExp ()

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
        float value = this.getBlockX();
        try {value = this.entityData.get(BASE_X);}
        catch (Exception ignored) {}
        return value;
    } // getBaseX ()

    public void setBaseX(float value) {
        this.entityData.set(BASE_X, value);
    } // setBaseX ()

    public float getBaseY() {
        float value = this.getBlockY();
        try {value = this.entityData.get(BASE_Y);}
        catch (Exception ignored) {}
        return value;
    } // getBaseY ()

    public void setBaseY(float value) {
        this.entityData.set(BASE_Y, value);
    } // setBaseY ()

    public float getBaseZ() {
        float value = this.getBlockZ();
        try {value = this.entityData.get(BASE_Z);}
        catch (Exception ignored) {}
        return value;
    } // getBaseZ ()

    public void setBaseZ(float value) {
        this.entityData.set(BASE_Z, value);
    } // setBaseZ ()

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

    // -- Constructor --

    protected InternalEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
    } // Constructor InternalEntity ()

    // -- Sound Methods --

    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return SoundEvents.GENERIC_HURT;
    } // getHurtSound ()

    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    } // getDeathSound ()

    // -- Inherited Methods --

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor levelAccessor, @NotNull DifficultyInstance instance, @NotNull MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        this.setTexture(nativeEntity.getRandomTextureID());
        return super.finalizeSpawn(levelAccessor, instance, mobSpawnType, spawnGroupData, compoundTag);
    } // finalizeSpawn ()

    @Override
    public @Nonnull Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    } // getAddEntityPacket ()

    @Override
    public void tick() {
        super.tick();
        handleCombatMode();
        handleAutoHeal();
        displayExtra();
    } // tick ()

    @Override
    public boolean doHurtTarget(@NotNull Entity target) {
        handleActivateCombatMode();
        if(InternalLogic.handleLevelUp(this.getCurrentLevel(), this.getMaxLevel()) && !(target instanceof Player) && !this.level.isClientSide) {
            final int maxHp = (int)((LivingEntity)target).getMaxHealth();
            addExp(maxHp / 4);
        }
        this.level.broadcastEntityEvent(this, (byte)4);
        return super.doHurtTarget(target);
    } // doHurtTarget ()

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
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
            if(source.isFire() && InternalLogic.handleFireProtectionLevelUp(this.getFireProtection())) this.setFireProtection(this.getFireProtection() + 1);
            if(source == DamageSource.FALL && InternalLogic.handleFallProtectionLevelUp(this.getFallProtection())) this.setFallProtection(this.getFallProtection() + 1);
            if(source.isExplosion() && InternalLogic.handleBlastProtectionLevelUp(this.getBlastProtection())) this.setBlastProtection(this.getBlastProtection() + 1);
            if(source.isProjectile() && InternalLogic.handleProjectileProtectionLevelUp(this.getProjectileProtection())) this.setProjectileProtection(this.getProjectileProtection() + 1);
        }

        final Entity entity = source.getEntity();

        if (InternalLogic.handleLevelUp(this.getCurrentLevel(), this.getMaxLevel()) && !(entity instanceof Player) && entity instanceof LivingEntity && !this.level.isClientSide) {
            final int maxHp = (int)((LivingEntity)entity).getMaxHealth();
            addExp(maxHp / 6);
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

        String customName = Utility.getEntityCustomName(this);
        if (!customName.isEmpty()) nbt.putString(LovelyRobotID.STAT_CUSTOM_NAME, customName);

        nbt.putString(LovelyRobotID.STAT_OWNER, Objects.requireNonNull(this.getOwner()).getScoreboardName());

        nbt.putString(LovelyRobotID.STAT_TYPE, this.nativeEntity.key);
        nbt.putInt(LovelyRobotID.STAT_COLOR, this.getTextureID());

        nbt.putInt(LovelyRobotID.STAT_MAX_LEVEL, this.getMaxLevel());
        nbt.putInt(LovelyRobotID.STAT_LEVEL, this.getCurrentLevel());
        nbt.putInt(LovelyRobotID.STAT_EXP, this.getExp());

        nbt.putInt(LovelyRobotID.STAT_FIRE_PROTECTION, this.getFireProtection());
        nbt.putInt(LovelyRobotID.STAT_FALL_PROTECTION, this.getFallProtection());
        nbt.putInt(LovelyRobotID.STAT_BLAST_PROTECTION, this.getBlastProtection());
        nbt.putInt(LovelyRobotID.STAT_PROJECTILE_PROTECTION, this.getProjectileProtection());

        dropItem.setTag(nbt);

        if (!customName.isEmpty()) dropItem.setHoverName(Component.nullToEmpty(customName).copy().append(Utility.getRandomTitle()).withStyle(ChatFormatting.DARK_PURPLE));

        ItemEntity itemEntity = new ItemEntity(this.level, this.getX(), this.getY() + (double)0.0F, this.getZ(), dropItem);
        itemEntity.setDefaultPickUpDelay();
        this.level.addFreshEntity(itemEntity);

        super.dropEquipment();
    } // dropEquipment ()

    @Nullable
    @Override
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

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();
        if (this.level.isClientSide) {
            boolean flag = this.isOwnedBy(player) || this.isTame() || stack.is(Items.BONE) && !this.isTame();
            return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            if (this.isTame() && this.isOwnedBy(player)) {
                if (!(item instanceof DyeItem)) {
                    InteractionResult interactionresult = super.mobInteract(player, hand);
                    if ((!interactionresult.consumesAction() || this.isBaby()) && this.isOwnedBy(player)) {
                        handleSit(stack);
                        handleState(stack);
                        handleAutoAttack(stack);
                        handleDisplayInteraction(stack);

                        handleInteract(player);
                        return InteractionResult.SUCCESS;
                    }

                    return interactionresult;
                }

                if(handleTexture(stack, player)) return InteractionResult.SUCCESS;
            }

            return super.mobInteract(player, hand);
        }
    } // mobInteract ()

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TEXTURE_ID, EntityTexture.RANDOM.getId());
        this.entityData.define(STATE, EntityState.Follow.getId());
        this.entityData.define(AUTO_ATTACK, true);

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

    @Override
    public void addAdditionalSaveData(CompoundTag dataNBT) {
        super.addAdditionalSaveData(dataNBT);
        CompoundTag entityData = new CompoundTag();
        entityData.putString("VersionNBT", LovelyRobot.VERSION.toString());
        writeToNBT(entityData);
        dataNBT.put("EntityData", entityData);
    } // addAdditionalSaveData ()

    @Override
    public void readAdditionalSaveData(CompoundTag dataNBT) {
        super.readAdditionalSaveData(dataNBT);
        CompoundTag entityData = dataNBT.getCompound("EntityData");
        readFromNBT(entityData, ObjectUtil.coalesce(new Version(entityData.getString("VersionNBT")), LovelyRobot.VERSION));
    } // readAdditionalSaveData ()

    @Override
    public CompoundTag writeToNBT(@Nonnull CompoundTag dataNBT) {
        dataNBT.putInt("TextureID", this.getTextureID());
        dataNBT.putInt("State", this.getCurrentStateID());
        dataNBT.putBoolean("AutoAttack", this.getAutoAttack());

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

        //dataNBT.put("Inventory", equipmentInv.writeToNBT());
        //dataNBT.put("Attributes", attributes.writeToNBT());
        //dataNBT.put("Tasks", tasks.writeToNBT());
        //dataNBT.put("Skills", skills.writeToNBT());
        return dataNBT;
    } // writeToNBT

    @Override
    public void readFromNBT(@Nonnull CompoundTag dataNBT, @Nonnull Version version) {
        this.setTexture(dataNBT.getInt("TextureID"));
        this.setCurrentState(dataNBT.getInt("State"));
        this.setAutoAttack(dataNBT.getBoolean("AutoAttack"));

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


        //equipmentInv.readFromNBT(dataNBT.getCompound("Inventory"), version);
        //attributes.readFromNBT(dataNBT.getCompound("Attributes"), version);
        //tasks.readFromNBT(dataNBT.getCompound("Tasks"), version);
        //skills.readFromNBT(dataNBT.getCompound("Skills"), version);

        //equipmentInv.updateToolAttributes();
        //attributes.updateNativeBonuses(false);
    } // readFromNBT ()

    @Override
    public void writeSpawnData(@Nonnull FriendlyByteBuf buf) {
        buf.writeInt(NativeEntity.NATIVES.indexOf(nativeEntity));

        //equipmentInv.encode(buf);
        //attributes.encode(buf);
        //tasks.encode(buf);
        //skills.encode(buf);
    } // writeSpawnData ()

    @Override
    public void readSpawnData(@Nonnull FriendlyByteBuf buf) {
        nativeEntity = NativeEntity.NATIVES.get(buf.readInt());

        //equipmentInv.decode(buf);
        //attributes.decode(buf);
        //tasks.decode(buf);
        //skills.decode(buf);

        //equipmentInv.updateToolAttributes();
        //attributes.updateNativeBonuses(false);
    } // writeSpawnData ()

    // -- Custom Methods --

    public abstract ItemStack setDropItem();

    private boolean canInteract(ItemStack stack){
        if(stack.getItem() instanceof DyeItem) return false;
        if(stack.getItem() instanceof SwordItem) return false;
        if(stack.is(Items.STICK) || stack.is(Items.BOOK) || stack.is(Items.WRITABLE_BOOK) || stack.is(Items.OAK_BUTTON)) return false;
        return !stack.is(Items.COMPASS);
    } // canInteract ()

    private boolean canInteractGuardMode(ItemStack stack){
        return stack.is(Items.COMPASS);
    } // canInteractGuardMode ()

    private boolean canInteractAutoAttack(ItemStack stack) {
        return stack.getItem() instanceof SwordItem;
    } // canInteractAutoAttack ()

    public void handleInteract (Player player) {}

    public void addExp (int value) {
        int addExp = value;
        int exp = getExp();
        String customName = "";
        try {customName = getCustomName().getString();}
        catch (Exception ignored) {}

        // if they have a name they earn more exp
        if(!customName.isEmpty()) addExp = addExp * 3 / 2;
        exp += addExp;

        var oldLevel = this.getCurrentLevel();
        while (exp >= InternalLogic.calculateNextExp(this.getCurrentLevel())) {
            exp -= InternalLogic.calculateNextExp(this.getCurrentLevel());
            setCurrentLevel(this.getCurrentLevel() + 1);
            InternalParticle.LevelUp(this);
        }

        setExp(exp);
        if(oldLevel != this.getCurrentLevel()) {
            if(!getLevel().isClientSide) {
                try {
                    final LivingEntity owner = getOwner();
                    if (owner == null) return;
                    displayGeneralMessage(getNotification(), true);
                } catch (Exception ignored) {}
            }
        }
    } // addExp ()

    protected void handleAutoHeal () {
        if(this.getHealth() < this.getMaxHealth()) autoHeal = true;
        if(this.getLevel().isClientSide && !autoHeal) return;

        if(autoHealTimer != 0) {
            autoHealTimer--;
        } else {
            final float healValue = this.getHealth() / 16.0F;
            this.heal(healValue);
            autoHeal = false;
            autoHealTimer = LovelyRobotConfig.COMMON.healInterval.get();
        }
    } // handleAutoHeal ()

    protected void handleActivateCombatMode () {
        if(!combatMode) combatMode = true;
        waryTimer = LovelyRobotConfig.COMMON.waryTime.get();
    } // handleActivateCombatMode ()

    protected void handleCombatMode() {
        //if(this.isAttacking()) handleActivateCombatMode();
        if(this.getLevel().isClientSide && !combatMode) return;

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
            if(this.getLevel().isClientSide) return;
            if(sitPoseChangeTimer < 50) sitPoseChangeTimer++;
            //InternalLogic.commandDebug(this, Component.nullToEmpty("Pose Change: " + sitPoseChangeTimer), true);
        }
    } // handleSitPoseChange ()

    public void handleTame(Player player) {
        this.tame(player);
        this.setTame(true);
        InternalParticle.Heart(this);
        InternalLogic.displayInfo(this, new TranslatableComponent(LovelyRobotID.TRANS_MSG_OWNER).append(Component.nullToEmpty(": " + player.getName().getString())), true);
    } // handleTame ()

    protected boolean handleTexture(ItemStack stack, Player player) {
        var oldTexture = getTextureID();
        if(stack.is(Items.WHITE_DYE)) setTexture(EntityTexture.WHITE);
        if(stack.is(Items.ORANGE_DYE)) setTexture(EntityTexture.ORANGE);
        if(stack.is(Items.MAGENTA_DYE)) setTexture(EntityTexture.MAGENTA);
        if(stack.is(Items.LIGHT_BLUE_DYE)) setTexture(EntityTexture.LIGHT_BLUE);
        if(stack.is(Items.YELLOW_DYE)) setTexture(EntityTexture.YELLOW);
        if(stack.is(Items.LIME_DYE)) setTexture(EntityTexture.LIME);
        if(stack.is(Items.PINK_DYE)) setTexture(EntityTexture.PINK);
        if(stack.is(Items.GRAY_DYE)) setTexture(EntityTexture.GRAY);
        if(stack.is(Items.LIGHT_GRAY_DYE)) setTexture(EntityTexture.LIGHT_GRAY);
        if(stack.is(Items.CYAN_DYE)) setTexture(EntityTexture.CYAN);
        if(stack.is(Items.PURPLE_DYE)) setTexture(EntityTexture.PURPLE);
        if(stack.is(Items.BLUE_DYE)) setTexture(EntityTexture.BLUE);
        if(stack.is(Items.BROWN_DYE)) setTexture(EntityTexture.BROWN);
        if(stack.is(Items.GREEN_DYE)) setTexture(EntityTexture.GREEN);
        if(stack.is(Items.RED_DYE)) setTexture(EntityTexture.RED);
        if(stack.is(Items.BLACK_DYE)) setTexture(EntityTexture.BLACK);

        if(oldTexture != getTextureID()) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    } // handleTexture ()

    protected void handleSit(ItemStack stack) {
        if(!canInteract(stack)) return;
        setOrderedToSit(invertBoolean(isOrderedToSit()));
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
        if(!isOrderedToSit()) return;

        sitPoseChangeTimer = 0;
        setCurrentState(EntityState.Standby);
        displayNotification(LovelyRobotID.TRANS_MSG_STANDBY, getNotification());
    } // handleStandbyState ()

    protected void handleFollowState(ItemStack stack){
        if(!canInteract(stack)) return;
        if(isOrderedToSit()) return;

        setCurrentState(EntityState.Follow);
        displayNotification(LovelyRobotID.TRANS_MSG_FOLLOW, getNotification());
    } // handleFollowState ()

    protected void handleBaseDefenseState(ItemStack stack){
        if(!canInteractGuardMode(stack)) return;
        setOrderedToSit(false);
        setAutoAttack(true);

        this.setBaseX((float)this.getBlockX());
        this.setBaseY((float)this.getBlockY());
        this.setBaseZ((float)this.getBlockZ());
        setCurrentState(EntityState.Defense);

        displayNotification(LovelyRobotID.TRANS_MSG_BASE_DEFENCE, getNotification());
    } // handleBaseDefenseState ()

    // -- Display --

    private void displayNotification(String notification, String message, boolean display) {
        if(!display) return;
        String customName = Utility.getEntityCustomName(this);
        if(!customName.isEmpty()) InternalLogic.displayInfo(this, Component.nullToEmpty(customName + " | ").copy().append(new TranslatableComponent(notification).append(Component.nullToEmpty(": ").copy().append(new TranslatableComponent(message)))), true);
        else InternalLogic.displayInfo(this, new TranslatableComponent(notification).append(Component.nullToEmpty(": ").copy().append(new TranslatableComponent(message))), true);
    } // displayNotification ()

    private void displayNotification(String message, boolean display) {
        if(!display) return;
        String customName = Utility.getEntityCustomName(this);
        if(!customName.isEmpty()) InternalLogic.displayInfo(this, Component.nullToEmpty(customName + " | ").copy().append(new TranslatableComponent(message)), true);
        else InternalLogic.displayInfo(this, new TranslatableComponent(message), true);
    } // displayNotification ()


    public void displayExtra() {
        Component debug = null;
        if(combatMode && getNotification()) {
            debug = LovelyRobotID.getTranslation(Objects.requireNonNull(EntityVariant.byName(nativeEntity.key))).append(Component.nullToEmpty(": ").copy().append(new TranslatableComponent("msg.rlovelyr.wary")));
            if(waryTimer < 10) debug = debug.copy().append(": 0" + waryTimer + " ");
            else debug = debug.copy().append(": " + waryTimer + " ");
        }

        if(autoHeal && getNotification()) {
            if(debug != null) debug = debug.copy().append(new TranslatableComponent("msg.rlovelyr.heal"));
            else debug = LovelyRobotID.getTranslation(Objects.requireNonNull(EntityVariant.byName(nativeEntity.key))).append(Component.nullToEmpty(": ").copy().append(new TranslatableComponent("msg.rlovelyr.heal")));

            if(autoHealTimer < 10) debug = debug.copy().append(": 0" + autoHealTimer + " ");
            else debug = debug.copy().append(": " + autoHealTimer + " ");
            if(this.getHealth() < 10) debug = debug.copy().append("| 0" + this.getHealth());
            else debug = debug.copy().append("| " + (int)Math.floor(this.getHealth()));
        }
        if(debug != null) InternalLogic.displayInfo(this, debug, true);
    } // displayExtra ()

    public void displayGeneralMessage(boolean canShow, boolean showLevelUp) {
        if(!canShow) return;
        InternalLogic.displayInfo(this, (new TranslatableComponent(LovelyRobotID.TRANS_MSG_BAR)), false);
        if(showLevelUp) InternalLogic.displayInfo(this, (new TranslatableComponent(LovelyRobotID.TRANS_MSG_LEVEL_UP)), false);
        if(this.getCustomName() != null) InternalLogic.displayInfo(this, LovelyRobotID.getTranslation(Objects.requireNonNull(EntityVariant.byName(nativeEntity.key))).append(": " + this.getCustomName().getString()), false);
        else InternalLogic.displayInfo(this, LovelyRobotID.getTranslation(Objects.requireNonNull(EntityVariant.byName(nativeEntity.key))), false);
        InternalLogic.displayInfo(this, new TranslatableComponent(LovelyRobotID.TRANS_MSG_LEVEL).append(": " + this.getCurrentLevel()             + "/" + this.getMaxLevel()), false);
        InternalLogic.displayInfo(this, new TranslatableComponent(LovelyRobotID.TRANS_MSG_EXPERIENCE).append(": " + this.getExp()                 + "/" + InternalLogic.calculateNextExp(this.getExp())), false);
        InternalLogic.displayInfo(this, new TranslatableComponent(LovelyRobotID.TRANS_MSG_HEALTH).append(": " + (int)Math.floor(this.getHealth()) + "/" + (int)this.getMaxHealth()), false);
        InternalLogic.displayInfo(this, new TranslatableComponent(LovelyRobotID.TRANS_MSG_ATTACK).append(": " + this.getAttackDamage()), false);
        InternalLogic.displayInfo(this, new TranslatableComponent(LovelyRobotID.TRANS_MSG_DEFENCE).append(": " + this.getArmorLevel()), false);
    } // displayGeneralMessage ()

    public void displayEnchantmentMessage() {
        InternalLogic.displayInfo(this, new TranslatableComponent(LovelyRobotID.TRANS_MSG_BAR), false);
        InternalLogic.displayInfo(this, new TranslatableComponent(LovelyRobotID.TRANS_MSG_ENCHANTMENT), false);
        InternalLogic.displayInfo(this, new TranslatableComponent(LovelyRobotID.TRANS_MSG_LOOTING).append(": " + this.getLooting()                            + "/" + LovelyRobotConfig.COMMON.maxLootEnchantment.get()), false);
        InternalLogic.displayInfo(this, new TranslatableComponent(LovelyRobotID.TRANS_MSG_FIRE_PROTECTION).append(": " + this.getFireProtection()             + "/" + LovelyRobotConfig.COMMON.protectionLimitFire.get()), false);
        InternalLogic.displayInfo(this, new TranslatableComponent(LovelyRobotID.TRANS_MSG_FALL_PROTECTION).append(": " + this.getFallProtection()             + "/" + LovelyRobotConfig.COMMON.protectionLimitFall.get()), false);
        InternalLogic.displayInfo(this, new TranslatableComponent(LovelyRobotID.TRANS_MSG_BLAST_PROTECTION).append(": " + this.getBlastProtection()           + "/" + LovelyRobotConfig.COMMON.protectionLimitBlast.get()), false);
        InternalLogic.displayInfo(this, new TranslatableComponent(LovelyRobotID.TRANS_MSG_PROJECTILE_PROTECTION).append(": " + this.getProjectileProtection() + "/" + LovelyRobotConfig.COMMON.protectionLimitProjectile.get()), false);
    } // displayEnchantmentMessage ()

} // Class InternalEntity