package net.msymbios.rlovelyr.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.LovelyRobotMod;
import net.msymbios.rlovelyr.entity.enums.*;
import net.msymbios.rlovelyr.entity.utils.*;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import java.util.HashMap;

import static net.msymbios.rlovelyr.entity.utils.ModUtils.*;
import static net.msymbios.rlovelyr.item.ModItems.ROBOT_CORE;

public class VanillaEntity extends TameableEntity implements VariantHolder<RobotTexture>, GeoEntity {

    // -- Variables --
    private static final HashMap<Integer, Identifier> TEXTURES = new HashMap<>() {{
        put(RobotTexture.ORANGE.getId(),     new Identifier(LovelyRobotMod.MODID, "textures/entity/vanilla/vanilla_01.png")); // Orange
        put(RobotTexture.MAGENTA.getId(),    new Identifier(LovelyRobotMod.MODID, "textures/entity/vanilla/vanilla_02.png")); // Magenta
        put(RobotTexture.YELLOW.getId(),     new Identifier(LovelyRobotMod.MODID, "textures/entity/vanilla/vanilla_04.png")); // Yellow
        put(RobotTexture.LIME.getId(),       new Identifier(LovelyRobotMod.MODID, "textures/entity/vanilla/vanilla_05.png")); // Lime
        put(RobotTexture.PINK.getId(),       new Identifier(LovelyRobotMod.MODID, "textures/entity/vanilla/vanilla_06.png")); // Pink
        put(RobotTexture.LIGHT_BLUE.getId(), new Identifier(LovelyRobotMod.MODID, "textures/entity/vanilla/vanilla_08.png")); // Light Blue
        put(RobotTexture.PURPLE.getId(),     new Identifier(LovelyRobotMod.MODID, "textures/entity/vanilla/vanilla_10.png")); // Purple
        put(RobotTexture.BLUE.getId(),       new Identifier(LovelyRobotMod.MODID, "textures/entity/vanilla/vanilla_11.png")); // Blue
        put(RobotTexture.RED.getId(),        new Identifier(LovelyRobotMod.MODID, "textures/entity/vanilla/vanilla_14.png")); // Red
        put(RobotTexture.BLACK.getId(),      new Identifier(LovelyRobotMod.MODID, "textures/entity/vanilla/vanilla_15.png")); // Black
    }};
    private static final HashMap<String, Identifier> MODELS = new HashMap<>() {{
        put(RobotModel.Unarmed.getName(), new Identifier(LovelyRobotMod.MODID, "geo/vanilla.geo.json"));
    }};
    private static final HashMap<String, Identifier> ANIMATIONS = new HashMap<>() {{
        put(RobotAnimation.Locomotion.getName(), new Identifier(LovelyRobotMod.MODID, "animations/lovelyrobot.animation.json"));
    }};

    private static final TrackedData<String> VARIANT = DataTracker.registerData(VanillaEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> TEXTURE_ID = DataTracker.registerData(VanillaEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final TrackedData<Integer> STATE = DataTracker.registerData(VanillaEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> AUTO_ATTACK = DataTracker.registerData(VanillaEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<Integer> MAX_LEVEL = DataTracker.registerData(VanillaEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> LEVEL = DataTracker.registerData(VanillaEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> EXP = DataTracker.registerData(VanillaEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private Identifier currentModel;
    private Identifier currentAnimator;

    private boolean isAutoAttackOn;
    private RobotState currentState;

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);


    // -- Properties --

    // -- MODEL --
    public Identifier getCurrentModel() {
        return currentModel;
    } // getCurrentTexture ()

    public void setCurrentModel(String value) {
        currentModel = MODELS.get(value);
    } // setCurrentAnimator ()

    public void setCurrentModel(RobotModel value) {
        currentModel = MODELS.get(value.getName());
    } // setCurrentAnimator ()


    // -- ANIMATOR --
    public Identifier getCurrentAnimator() {
        return currentAnimator;
    } // getCurrentAnimator ()

    public void setCurrentAnimator(String value) {
        currentAnimator = ANIMATIONS.get(value);
    } // setCurrentAnimator ()

    public void setCurrentAnimator(RobotAnimation value) {
        currentAnimator = ANIMATIONS.get(value.getName());
    } // setCurrentAnimator ()


    // -- TEXTURE --
    public Identifier getCurrentTexture() {
        return getTextureById(getEntityVariant());
    } // getCurrentTexture ()

    public Identifier getTextureById(int key) {
        return TEXTURES.containsKey(key) ? TEXTURES.get(key) : getCurrentTexture();
    } // getTexture ()

    public void setTexture(ItemStack itemStack) {
        if(itemStack.isOf(Items.ORANGE_DYE)) setVariant(RobotTexture.ORANGE);
        if(itemStack.isOf(Items.MAGENTA_DYE)) setVariant(RobotTexture.MAGENTA);
        if(itemStack.isOf(Items.YELLOW_DYE)) setVariant(RobotTexture.YELLOW);
        if(itemStack.isOf(Items.LIME_DYE)) setVariant(RobotTexture.LIME);
        if(itemStack.isOf(Items.PINK_DYE)) setVariant(RobotTexture.PINK);
        if(itemStack.isOf(Items.LIGHT_BLUE_DYE)) setVariant(RobotTexture.LIGHT_BLUE);
        if(itemStack.isOf(Items.PURPLE_DYE)) setVariant(RobotTexture.PURPLE);
        if(itemStack.isOf(Items.BLUE_DYE)) setVariant(RobotTexture.BLUE);
        if(itemStack.isOf(Items.RED_DYE)) setVariant(RobotTexture.RED);
        if(itemStack.isOf(Items.BLACK_DYE)) setVariant(RobotTexture.BLACK);
    } // setTexture ()


    // -- VARIANT --
    public void setEntityVariant(int variant) {
        this.dataTracker.set(TEXTURE_ID, variant);
    } // setVariant ()

    public int getEntityVariant() {
        return this.dataTracker.get(TEXTURE_ID);
    } // getVariant ()

    public String getVariantID() {
        return this.dataTracker.get(VARIANT);
    } // getVariantID ()

    @Override
    public RobotTexture getVariant() {
        return RobotTexture.byId(getEntityVariant());
    } // getVariant ()

    public void setVariantID(String value) {
        this.dataTracker.set(VARIANT, value);
    } // setVariantID ()

    @Override
    public void setVariant(RobotTexture variant) {
        setEntityVariant(variant.getId());
    } // setVariant ()


    // -- STATE --
    public int getCurrentState() {
        dataTracker.set(STATE, currentState.getId());
        return currentState.getId();
    } // getMode ()

    public void setCurrentState(RobotState value){
        this.dataTracker.set(STATE, value.getId());
        currentState = value;
    } // setCurrentMode ()

    public void setCurrentState(int value){
        this.dataTracker.set(STATE, value);
        currentState = RobotState.byId(value);
    } // setCurrentState ()


    // -- AUTO ATTACK --
    public boolean getAutoAttack() {
        dataTracker.set(AUTO_ATTACK, isAutoAttackOn);
        return isAutoAttackOn;
    } // getAutoAttack ()

    public void setAutoAttack(boolean value) {
        this.dataTracker.set(AUTO_ATTACK, value);
        isAutoAttackOn = value;
    } // setAutoAttack ()


    // -- STATS --
    public int getMaxLevel(){
        return this.dataTracker.get(MAX_LEVEL);
    } // getMaxLevel ()

    public void setMaxLevel(int value) {
        this.dataTracker.set(MAX_LEVEL, value);
    } // setMaxLevel ()

    protected int getNextExp() {
        return ModMetrics.BaseExp + this.getLevel() * ModMetrics.UpExpValue;
    } // getNextExp ()

    public int getLevel(){
        int newLevel = 1;
        try {newLevel = this.dataTracker.get(LEVEL);}
        catch (Exception ignored){}
        return newLevel;
    } // getLevel ()

    public void setLevel(int value){
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
    } // setLevel ()

    public int getExp(){
        int newExp = 1;
        try {newExp = this.dataTracker.get(EXP);}
        catch (Exception ignored){}
        return newExp;
    } // getExp ()

    public void setExp(int value){
        this.dataTracker.set(EXP, value);
    } // setExp ()

    public int getHpValue() {
        return (int)(ModMetrics.VanillaBaseHp + this.getLevel() * ModMetrics.VanillaBaseHp / 50);
    } // getHpValue ()

    public int getAttackValue() {
        return (int)(ModMetrics.VanillaBaseAttack + this.getLevel() * ModMetrics.VanillaBaseAttack / 50);
    } // getAttackValue ()

    public int getDefenseValue() {
        return (int)(ModMetrics.VanillaBaseDefense + this.getLevel() * ModMetrics.VanillaBaseDefense / 50);
    } // getDefenseValue ()

    public int getLootingLevel() {
        int level = 0;
        if (ModMetrics.LootingEnchantment) {
            level = this.getLevel() / ModMetrics.LootingRequiredLevel;
            if (level > ModMetrics.MaxLootingLevel) {
                level = ModMetrics.MaxLootingLevel;
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


    // -- Constructor --
    public VanillaEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        setCurrentModel(RobotModel.Unarmed);
        setCurrentAnimator(RobotAnimation.Locomotion);
    } // Constructor RobotEntity ()


    // -- Animations --
    private PlayState locomotionAnim(AnimationState animationState) {
        if(animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.lovelyrobot.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if(isSitting()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.lovelyrobot.sit", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        } else {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.lovelyrobot.idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
    } // locomotionAnim ()

    private PlayState attackAnim(AnimationState state) {
        if(this.handSwinging && state.getController().getAnimationState().equals(AnimationController.State.STOPPED)) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(RawAnimation.begin().then("animation.lovelyrobot.attack", Animation.LoopType.PLAY_ONCE));
            this.handSwinging = false;
        }

        return PlayState.CONTINUE;
    } // attackAnim ()


    // -- Inheritance --
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController(this, "locomotionController", 0, this::locomotionAnim));
        controllerRegistrar.add(new AnimationController(this, "attackController", 0, this::attackAnim));
    } // registerControllers ()

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    } // getAnimatableInstanceCache ()


    // -- Methods --
    public static DefaultAttributeContainer.Builder setAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, ModMetrics.VanillaBaseHp)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, ModMetrics.VanillaBaseAttack)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, ModMetrics.AttackMoveSpeed)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, ModMetrics.VanillaMovementSpeed)
                .add(EntityAttributes.GENERIC_ARMOR, 0)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 0);
    } // setAttributes ()

    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setVariantID(RobotVariant.Vanilla.getName());
        this.setEntityVariant(getRandomNumber(TEXTURES.size()));
        this.setMaxLevel(ModMetrics.MaxLevel);

        EquipmentSlot slot = EquipmentSlot.MAINHAND;
        ItemStack diamondSword = new ItemStack(Items.DIAMOND_SWORD);
        this.equipStack(slot, diamondSword);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    } // initialize ()

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(3, new PounceAtTargetGoal(this, 0.4F));
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(5, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F, false));
        this.goalSelector.add(6, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(9, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, (new RevengeGoal(this)).setGroupRevenge());
        this.targetSelector.add(4, new UniversalAngerGoal(this, true));
    } // initGoals ()

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) return false;

        // ToDo: Add damage protected by getFireProtection
        // ToDo: Add damage protected by getFallProtection
        // ToDo: Add damage protected by getBlastProtection
        // ToDo: Add damage protected by getProjectileProtection

        if (amount < 1.0f) return false;

        if(!world.isClient) {
            // if(source.isFire()) this.setFireProtection(this.getFireProtection() + 1);
            // ToDo: Add fire protection
            // ToDo: Add fall protection
            // ToDo: Add blast protection
            // ToDo: Add projectile protection
        }

        final Entity entity = source.getSource();

        if (this.canUpLevel() && !(entity instanceof PlayerEntity) && entity instanceof LivingEntity && !this.world.isClient) {
            final int maxHp = (int)((LivingEntity)entity).getMaxHealth();
            this.addExp(maxHp / 6);
        }

        if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof ArrowEntity)) {
            amount = (amount + 1.0f) / 2.0f;
        }

        return super.damage(source, amount);
    } // damage ()

    @Override
    public boolean handleAttack(Entity attacker) {
        if(this.canUpLevel() && !(attacker instanceof PlayerEntity) && attacker instanceof LivingEntity && !this.world.isClient) {
            final int maxHp = (int)((LivingEntity)attacker).getMaxHealth();
            this.addExp(maxHp / 4);
        }

        this.world.sendEntityStatus(this, (byte)4);

        return super.handleAttack(attacker);
    } // handleAttack ()

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        final ItemStack craftPurchaseOrder = new ItemStack(ROBOT_CORE, 1);

        try {
            final NbtCompound nbt = new NbtCompound();
            final String customName = this.getEntityName();
            if (customName != null && !customName.trim().equals("")) {
                nbt.putString("CustomName", customName);
            }

            nbt.putString("Variant", this.getVariantID());
            nbt.putInt("TextureID", this.getEntityVariant());

            nbt.putInt("State", this.getCurrentState());
            nbt.putBoolean("AutoAttack", this.getAutoAttack());

            nbt.putInt("MaxLevel", this.getMaxLevel());
            nbt.putInt("Level", this.getLevel());
            nbt.putInt("Exp", this.getExp());

            craftPurchaseOrder.setNbt(nbt);
        } catch (Exception ignored) {}
        this.dropStack(craftPurchaseOrder, 0.0f);
    } // dropEquipment ()

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClient && ModMetrics.AutoHeal && this.age % ModMetrics.AutoHealInterval == 0 && this.getHealth() < this.getHpValue()) {
            final float healValue = this.getHpValue() / 16.0f;
            this.heal(healValue);
        }
    } // tick ()

    @Nullable @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    } // createChild


    // -- Custom Methods --
    protected boolean canUpLevel() {
        return this.getLevel() < getMaxLevel();
    } // canUpLevel ()

    protected void addExp (int value) {
        int addExp = value;

        final String customName = this.getEntityName();
        if(customName != null && !customName.trim().equals(""))
            addExp = addExp * 3 / 2;

        int exp = this.getExp();
        exp += addExp;

        while (exp >= this.getNextExp()) {
            exp -= this.getNextExp();
            this.setLevel(this.getLevel() + 1);

            if(!world.isClient) {
                try {
                    final LivingEntity entity = this.getOwner();
                    if (entity == null) continue;
                    this.displayMessage((PlayerEntity)entity);
                } catch (Exception ignored) {}
            }
        }

        this.setExp(exp);
    } // addExp ()


    // -- Interact Methods --
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        var itemStack = player.getStackInHand(hand);

        if(hand == Hand.MAIN_HAND) {
            setSittingState(itemStack);

            if (this.world.isClient) {
                return ActionResult.PASS;
            } else {
                setAutoAttackState(itemStack, player);
                setMode(itemStack);
                setTexture(itemStack);

                if(getOwner() == null){
                    this.setOwner(player);
                    player.sendMessage(Text.literal("Owner: " + getOwner().getEntityName()));
                }

                if(itemStack.isOf(Items.STICK))
                    displayMessage(player);

                return ActionResult.SUCCESS;
            }
        }

        return super.interactMob(player, hand);
    } // interactMob ()

    public void setSittingState(ItemStack itemStack) {
        if(!canInteract(itemStack)) return;
        setSitting(invertBoolean(isSitting()));
    } // setSittingState ()

    public void setAutoAttackState(ItemStack itemStack, PlayerEntity player){
        if (!canInteractAutoAttack(itemStack)) return;
        setAutoAttack(invertBoolean(isAutoAttackOn));
        player.sendMessage(Text.literal("Auto Attack: " + this.isAutoAttackOn));
    } // setAutoAttackState ()

    public void setMode(ItemStack itemStack) {
        StandbyMode(itemStack);
        FollowMode(itemStack);
        GuardMode(itemStack);
    } // setMode

    public void StandbyMode(ItemStack itemStack){
        if(!canInteract(itemStack)) return;
        if(isSitting()) setCurrentState(RobotState.Standby);
    } // StandbyMode ()

    public void FollowMode(ItemStack itemStack){
        if(!canInteract(itemStack)) return;
        if(!isSitting()) setCurrentState(RobotState.Follow);
    } // FollowMode ()

    public void GuardMode(ItemStack itemStack){
        if(!canInteractGuardMode(itemStack)) return;
        setSitting(false);
        setCurrentState(RobotState.Defense);
    } // GuardMode ()

    public void displayMessage (PlayerEntity player) {
        player.sendMessage(Text.literal("|--------------------------"));
        player.sendMessage(Text.literal("MaxLevel: " + this.getMaxLevel()));
        player.sendMessage(Text.literal("Model: " + this.getVariantID()));
        player.sendMessage(Text.literal("Health: " + this.getHealth() + "/" + this.getMaxHealth()));
        player.sendMessage(Text.literal("Attack: " + this.getAttackValue()));
        player.sendMessage(Text.literal("Auto Attack: " + this.getAutoAttack()));
        player.sendMessage(Text.literal("Level: " + this.getLevel()));
        player.sendMessage(Text.literal("Exp: " + this.getExp()));
    } // displayMessage ()


    // -- Save Methods --
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, RobotVariant.Vanilla.getName());
        this.dataTracker.startTracking(TEXTURE_ID, 0);

        this.dataTracker.startTracking(STATE, 0);
        this.dataTracker.startTracking(AUTO_ATTACK, false);

        this.dataTracker.startTracking(MAX_LEVEL, ModMetrics.MaxLevel);
        this.dataTracker.startTracking(LEVEL, 0);
        this.dataTracker.startTracking(EXP, 0);
    } // initDataTracker ()

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("Variant", this.getVariantID());
        nbt.putInt("TextureID", this.getEntityVariant());

        nbt.putInt("State", this.getCurrentState());
        nbt.putBoolean("AutoAttack", this.getAutoAttack());

        nbt.putInt("MaxLevel", this.getMaxLevel());
        nbt.putInt("Level", this.getLevel());
        nbt.putInt("Exp", this.getExp());
    } // writeCustomDataToNbt ()

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setVariantID(nbt.getString("Variant"));
        this.setEntityVariant(nbt.getInt("TextureID"));

        this.setCurrentState(nbt.getInt("State"));
        this.setAutoAttack(nbt.getBoolean("AutoAttack"));

        this.setMaxLevel(nbt.getInt("MaxLevel"));
        this.setLevel(nbt.getInt("Level"));
        this.setExp(nbt.getInt("Exp"));
    } // readCustomDataFromNbt ()


    // -- Sound Methods --
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_GENERIC_HURT;
    } // getHurtSound ()

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_GENERIC_DEATH;
    } // getDeathSound ()

} // Class RobotEntity