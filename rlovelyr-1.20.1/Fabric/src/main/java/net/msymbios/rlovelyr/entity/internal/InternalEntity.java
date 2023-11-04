package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
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
import net.msymbios.rlovelyr.entity.enums.*;
import org.jetbrains.annotations.Nullable;

import static net.msymbios.rlovelyr.entity.internal.Utility.*;
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
    protected static final TrackedData<Float> BASE_Z = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.FLOAT);;

    protected static final TrackedData<Boolean> NOTIFICATION = DataTracker.registerData(InternalEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    protected int waryTimer = 0, autoHealTimer = 0;
    protected boolean combatMode = false, autoHeal = false;
    protected EntityVariant variant;
    protected EntityModel model = EntityModel.Default;

    // -- Properties --

    // VARIANT
    public String getVariant() { return this.variant.getName(); } // getVariant ()

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
    public Identifier getAnimatorByID(EntityAnimator value) { return InternalMetric.getAnimator(this.variant, value); } // getAnimatorByID ()

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
    public int getAttribute(EntityAttribute attribute) { return (int) InternalMetric.getAttributeValue(this.variant, attribute); } // getAttribute ()

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

        EntityAttributeInstance maxHealthAttribute = this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        assert maxHealthAttribute != null;
        maxHealthAttribute.setBaseValue(getHpValue());

        EntityAttributeInstance damageAttribute = this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        assert damageAttribute != null;
        damageAttribute.setBaseValue(getAttackValue());

        EntityAttributeInstance armorAttribute = this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
        assert armorAttribute != null;
        armorAttribute.setBaseValue(getArmorValue());

        EntityAttributeInstance armorToughnessAttribute = this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
        assert armorToughnessAttribute != null;
        armorToughnessAttribute.setBaseValue(getArmorToughnessValue());
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

    public double getArmorValue () {
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
    } // getLog ()

    public void setNotification(boolean value) {
        this.dataTracker.set(NOTIFICATION, value);
    } // setLog ()

    // -- Construct --
    protected InternalEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    } // Construct InternalRobot

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
        //commandDebugExtra();
    } // tick ()

    @Override
    public void onAttacking(Entity target) {
        handleActivateCombatMode();
        if(this.canLevelUp() && !(target instanceof PlayerEntity) && target != null && !this.getWorld().isClient) {
            final int maxHp = (int)((LivingEntity)target).getMaxHealth();
            this.addExp(maxHp / 4);
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
            if((source.isOf(DamageTypes.ON_FIRE) || source.isOf(DamageTypes.IN_FIRE) || source.isOf(DamageTypes.LAVA)) && canLevelUpFireProtection()) this.setFireProtection(this.getFireProtection() + 1);
            if(source.isOf(DamageTypes.FALL) && canLevelUpFallProtection()) this.setFallProtection(this.getFallProtection() + 1);
            if(source.isOf(DamageTypes.EXPLOSION) && canLevelUpBlastProtection()) this.setBlastProtection(this.getBlastProtection() + 1);
            if(source.isOf(DamageTypes.ARROW) && canLevelUpProjectileProtection()) this.setProjectileProtection(this.getProjectileProtection() + 1);
        }

        final Entity entity = source.getSource();

        if (this.canLevelUp() && !(entity instanceof PlayerEntity) && entity instanceof LivingEntity && !this.getWorld().isClient) {
            final int maxHp = (int)((LivingEntity)entity).getMaxHealth();
            this.addExp(maxHp / 6);
        }

        if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof ArrowEntity)) {
            amount = (amount + 1.0f) / 2.0f;
        }

        return super.damage(source, amount);
    } // damage ()

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        final ItemStack dropItem = new ItemStack(ROBOT_CORE, 1);
        try {
            NbtCompound nbt = dropItem.getNbt();
            if(nbt == null) nbt = new NbtCompound();

            final String customName = this.getCustomName().getString();
            if (customName != null && !customName.trim().isEmpty()) nbt.putString("custom_name", customName);

            nbt.putString("type", Utility.getTranslatable(this.variant));
            nbt.putInt("color", this.getTextureID());

            nbt.putInt("max_level", this.getMaxLevel());
            nbt.putInt("level", this.getCurrentLevel());
            nbt.putInt("exp", this.getExp());

            nbt.putInt("fire_protection", this.getFireProtection());
            nbt.putInt("fall_protection", this.getFallProtection());
            nbt.putInt("blast_protection", this.getBlastProtection());
            nbt.putInt("projectile_protection", this.getProjectileProtection());

            dropItem.setNbt(nbt);
        } catch (Exception ignored) {}
        this.dropStack(dropItem, 0.0f);
    } // dropEquipment ()

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        switch (slot.getType()){
            case HAND: {
                final ItemStack tempSword = new ItemStack(Items.DIAMOND_SWORD,1);
                final int lootingLevel = this.getLootingLevel();
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
        var itemStack = player.getStackInHand(hand);

        if(hand == Hand.MAIN_HAND) {
            if(getOwner() != null) handleSit(itemStack);
            if(this.getWorld().isClient) {
                boolean bl = this.isOwner(player) || this.isTamed() || itemStack.isOf(Items.BONE) && !this.isTamed();
                return bl ? ActionResult.CONSUME : ActionResult.PASS;
            } else {
                if(getOwner() == null) handleTame(player);
                if(getOwner() != null) {
                    handleState(itemStack, player);
                    handleAutoAttack(itemStack, player);
                    handleTexture(itemStack, player);

                    if(itemStack.getItem() == (Items.OAK_BUTTON)) {
                        this.setNotification(Utility.invertBoolean(getNotification()));
                        if(getNotification()) commandDebug(Text.translatable("msg.rlovelyr.notification").append(Text.literal(" ").append(Text.translatable("msg.rlovelyr.on"))), true);
                        else commandDebug(Text.translatable("msg.rlovelyr.notification").append(Text.literal(" ").append(Text.translatable("msg.rlovelyr.off"))), true);
                    }

                    if(itemStack.getItem() == (Items.BOOK)) displayMessage(player, true, false);
                    if(itemStack.getItem() == (Items.WRITABLE_BOOK)) displayProtectionMessage(player);
                }
                return ActionResult.SUCCESS;
            }
        }

        return super.interactMob(player, hand);
    } // interactMob ()

    public boolean canInteract(ItemStack itemStack){
        if(itemStack.isOf(Items.WHITE_DYE) ||itemStack.isOf(Items.ORANGE_DYE) || itemStack.isOf(Items.MAGENTA_DYE) || itemStack.isOf(Items.LIGHT_BLUE_DYE) ||
                itemStack.isOf(Items.YELLOW_DYE) || itemStack.isOf(Items.LIME_DYE) || itemStack.isOf(Items.PINK_DYE) || itemStack.isOf(Items.GRAY_DYE) ||
                itemStack.isOf(Items.LIGHT_GRAY_DYE) || itemStack.isOf(Items.CYAN_DYE) || itemStack.isOf(Items.PURPLE_DYE) || itemStack.isOf(Items.BLUE_DYE) ||
                itemStack.isOf(Items.BROWN_DYE) || itemStack.isOf(Items.GREEN_DYE) || itemStack.isOf(Items.RED_DYE) || itemStack.isOf(Items.BLACK_DYE)) return false;
        if(itemStack.isOf(Items.WOODEN_SWORD) || itemStack.isOf(Items.STONE_SWORD) || itemStack.isOf(Items.IRON_SWORD) || itemStack.isOf(Items.GOLDEN_SWORD) || itemStack.isOf(Items.DIAMOND_SWORD) || itemStack.isOf(Items.NETHERITE_SWORD)) return false;
        if(itemStack.isOf(Items.STICK) || itemStack.isOf(Items.BOOK) || itemStack.isOf(Items.WRITABLE_BOOK) || itemStack.isOf(Items.OAK_BUTTON)) return false;
        return !itemStack.isOf(Items.COMPASS) && !itemStack.isOf(Items.RECOVERY_COMPASS);
    } // canInteract ()

    public boolean canInteractGuardMode(ItemStack itemStack){
        return itemStack.isOf(Items.COMPASS) || itemStack.isOf(Items.RECOVERY_COMPASS);
    } // canInteractGuardMode ()

    public boolean canInteractAutoAttack(ItemStack itemStack) {
        return itemStack.isOf(Items.WOODEN_SWORD) || itemStack.isOf(Items.STONE_SWORD) || itemStack.isOf(Items.IRON_SWORD) || itemStack.isOf(Items.GOLDEN_SWORD) || itemStack.isOf(Items.DIAMOND_SWORD) || itemStack.isOf(Items.NETHERITE_SWORD);
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
        var addExp = value;

        final String customName = this.getEntityName();
        if(customName != null && !customName.trim().isEmpty())
            addExp = addExp * 3 / 2;

        int exp = this.getExp();
        exp += addExp;

        var oldLevel = getCurrentLevel();
        while (exp >= this.getNextExp()) {
            exp -= this.getNextExp();
            this.setCurrentLevel(this.getCurrentLevel() + 1);
        }

        this.setExp(exp);

        if(oldLevel != getCurrentLevel()) {
            if(!getWorld().isClient) {
                try {
                    final LivingEntity entity = this.getOwner();
                    if (entity == null) return;
                    this.displayMessage((PlayerEntity)entity, getNotification(), true);
                } catch (Exception ignored) {}
            }
        }
    } // addExp ()

    // -- Logic Methods --
    protected void handleAutoHeal () {
        if(this.getHealth() < this.getHpValue()) autoHeal = true;
        if(this.getWorld().isClient && !autoHeal) return;

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

    public void handleTame(PlayerEntity player) {
        this.setOwner(player);
        this.setTamed(true);
        InternalParticle.Heart(this);
        this.getWorld().sendEntityStatus(this, (byte) 7);
    } // handleTame ()

    public void handleTexture(ItemStack items, PlayerEntity player) {
        var oldTexture = getTextureID();
        if(items.isOf(Items.WHITE_DYE)) setTexture(EntityTexture.WHITE);
        if(items.isOf(Items.ORANGE_DYE)) setTexture(EntityTexture.ORANGE);
        if(items.isOf(Items.MAGENTA_DYE)) setTexture(EntityTexture.MAGENTA);
        if(items.isOf(Items.LIGHT_BLUE_DYE)) setTexture(EntityTexture.LIGHT_BLUE);
        if(items.isOf(Items.YELLOW_DYE)) setTexture(EntityTexture.YELLOW);
        if(items.isOf(Items.LIME_DYE)) setTexture(EntityTexture.LIME);
        if(items.isOf(Items.PINK_DYE)) setTexture(EntityTexture.PINK);
        if(items.isOf(Items.GRAY_DYE)) setTexture(EntityTexture.GRAY);
        if(items.isOf(Items.LIGHT_GRAY_DYE)) setTexture(EntityTexture.LIGHT_GRAY);
        if(items.isOf(Items.CYAN_DYE)) setTexture(EntityTexture.CYAN);
        if(items.isOf(Items.PURPLE_DYE)) setTexture(EntityTexture.PURPLE);
        if(items.isOf(Items.BLUE_DYE)) setTexture(EntityTexture.BLUE);
        if(items.isOf(Items.BROWN_DYE)) setTexture(EntityTexture.BROWN);
        if(items.isOf(Items.GREEN_DYE)) setTexture(EntityTexture.GREEN);
        if(items.isOf(Items.RED_DYE)) setTexture(EntityTexture.RED);
        if(items.isOf(Items.BLACK_DYE)) setTexture(EntityTexture.BLACK);

        if(oldTexture != getTextureID()) {
            if (!player.getAbilities().creativeMode) items.decrement(1);
        }
    } // handleTexture ()

    public void handleSit(ItemStack itemStack) {
        if(!canInteract(itemStack)) return;
        setSitting(invertBoolean(isSitting()));
    } // handleSit ()

    public void handleAutoAttack(ItemStack itemStack, PlayerEntity player){
        if (!canInteractAutoAttack(itemStack)) return;
        setAutoAttack(invertBoolean(getAutoAttack()));
        String msgAutoAttack;
        if(this.getAutoAttack()) msgAutoAttack = "msg.rlovelyr.on";
        else msgAutoAttack = "msg.rlovelyr.off";
        player.sendMessage(Text.translatable("msg.rlovelyr.auto_attack").append(Text.literal(": ").append(Text.translatable(msgAutoAttack))), true);
    } // handleAutoAttack ()

    public void handleState(ItemStack itemStack, PlayerEntity player) {
        var previousState = getCurrentState();
        StandbyState(itemStack);
        FollowState(itemStack);
        BaseDefenseState(itemStack);
        if(previousState != getCurrentState()) player.sendMessage(Text.translatable("msg.rlovelyr.state").append(Text.literal(": ").append(Text.translatable(Utility.getTranslatable(this.getCurrentState())))), true);
    } // handleState

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

        var currentPosition = this.getPos();
        this.setBaseX((float)currentPosition.x);
        this.setBaseY((float)currentPosition.y);
        this.setBaseZ((float)currentPosition.z);
        setCurrentState(EntityState.BaseDefense);
    } // BaseDefenseState ()

    // -- Debug Methods --
    public void commandDebug(String message, boolean overlay) {
        if(this.getOwner() != null) {
            PlayerEntity player = (PlayerEntity)this.getOwner();
            player.sendMessage(Text.literal(message), overlay);
        }
    } // commandDebug ()

    public void commandDebug(MutableText message, boolean overlay) {
        if(this.getOwner() != null) {
            PlayerEntity player = (PlayerEntity)this.getOwner();
            player.sendMessage(message, overlay);
        }
    } // commandDebug ()

    public void commandDebugExtra() {
        MutableText debug = null;
        if(combatMode && getNotification()) {
            debug = Text.translatable(Utility.getTranslatable(this.variant)).append(Text.literal(": ").append(Text.translatable("msg.rlovelyr.wary")));
            if(waryTimer < 10) debug = debug.append(": 0" + waryTimer + " ");
            else debug = debug.append(": " + waryTimer + " ");
        }

        if(autoHeal && getNotification()) {
            if(debug != null) debug = debug.append(Text.translatable("msg.rlovelyr.heal"));
            else debug = Text.translatable(Utility.getTranslatable(this.variant)).append(Text.literal(": ").append(Text.translatable("msg.rlovelyr.heal")));

            if(autoHealTimer < 10) debug = debug.append(": 0" + autoHealTimer + " ");
            else debug = debug.append(": " + autoHealTimer + " ");
            if(this.getHealth() < 10) debug = debug.append("| 0" + this.getHealth());
            else debug = debug.append("| " + (int)Math.floor(this.getHealth()));
        }
        if(debug != null) commandDebug(debug, true);
    } // commandDebugExtra ()

    public void displayMessage (PlayerEntity player, boolean canShow, boolean showLevelUp) {
        if(!canShow) return;
        player.sendMessage(Text.translatable("msg.rlovelyr.bar"));
        if(showLevelUp) player.sendMessage(Text.translatable("msg.rlovelyr.level_up"));
        if(this.getCustomName() != null) player.sendMessage(Text.translatable(Utility.getTranslatable(this.variant)).append(": " + this.getCustomName().getString()));
        else player.sendMessage(Text.translatable(Utility.getTranslatable(this.variant)));
        player.sendMessage(Text.translatable("msg.rlovelyr.level").append(": " + this.getCurrentLevel()             + "/" + this.getMaxLevel()));
        player.sendMessage(Text.translatable("msg.rlovelyr.experience").append(": " + this.getExp()                 + "/" + this.getNextExp()));
        player.sendMessage(Text.translatable("msg.rlovelyr.health").append(": " + (int)Math.floor(this.getHealth()) + "/" + (int)this.getMaxHealth()));
        player.sendMessage(Text.translatable("msg.rlovelyr.attack").append(": " + this.getAttackValue()));
        player.sendMessage(Text.translatable("msg.rlovelyr.defence").append(": " + this.getDefenseValue()));
    } // displayMessage ()

    public void displayProtectionMessage (PlayerEntity player) {
        player.sendMessage(Text.translatable("msg.rlovelyr.bar"));
        player.sendMessage(Text.translatable("msg.rlovelyr.enchantment"));
        player.sendMessage(Text.translatable("msg.rlovelyr.looting").append(": " + this.getLootingLevel()                       + "/" + InternalMetric.MaxLootingLevel));
        player.sendMessage(Text.translatable("msg.rlovelyr.fire_protection").append(": " + this.getFireProtection()             + "/" + InternalMetric.FireProtectionLimit));
        player.sendMessage(Text.translatable("msg.rlovelyr.fall_protection").append(": " + this.getFallProtection()             + "/" + InternalMetric.FallProtectionLimit));
        player.sendMessage(Text.translatable("msg.rlovelyr.blast_protection").append(": " + this.getBlastProtection()           + "/" + InternalMetric.BlastProtectionLimit));
        player.sendMessage(Text.translatable("msg.rlovelyr.projectile_protection").append(": " + this.getProjectileProtection() + "/" + InternalMetric.ProjectileProtectionLimit));
    } // displayProtectionMessage ()

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