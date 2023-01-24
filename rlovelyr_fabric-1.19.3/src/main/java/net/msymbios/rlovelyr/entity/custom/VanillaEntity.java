package net.msymbios.rlovelyr.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.LovelyRobotMod;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class VanillaEntity extends RobotEntity implements GeoEntity  {

    // -- Variables --
    private static final Identifier VANILLA_00 = new Identifier(LovelyRobotMod.MODID,"textures/entity/vanilla/vanilla_00.png");
    private static final Identifier VANILLA_01 = new Identifier(LovelyRobotMod.MODID,"textures/entity/vanilla/vanilla_01.png");
    private static final Identifier VANILLA_02 = new Identifier(LovelyRobotMod.MODID,"textures/entity/vanilla/vanilla_02.png");
    private static final Identifier VANILLA_03 = new Identifier(LovelyRobotMod.MODID,"textures/entity/vanilla/vanilla_03.png");

    public static final Identifier ARMED_MODEL = new Identifier(LovelyRobotMod.MODID, "geo/vanilla.attack.geo.json");
    public static final Identifier UNARMED_MODEL = new Identifier(LovelyRobotMod.MODID, "geo/vanilla.geo.json");

    public static final Identifier ENTITY_ANIMATION = new Identifier(LovelyRobotMod.MODID, "animations/lovelyrobot.animation.json");

    private static Identifier currentTexture;
    private static Identifier currentModel = UNARMED_MODEL;

    private static int numberOfTextures = 4;

    private AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);


    // -- Properties --
    public static Identifier getCurrentTexture() {
        return currentTexture;
    } // getCurrentTexture ()
    public static Identifier getCurrentModel() {
        return currentModel;
    } // getCurrentTexture ()

    public static int getNumberOfTextures() {
        return numberOfTextures;
    } // getNumberOfTextures ()


    // -- Constructor --
    public VanillaEntity(EntityType<? extends RobotEntity> entityType, World world) {
        super(entityType, world);
        currentTexture = getEntityTexture();
    } // Constructor ModEntities ()


    // -- Methods --
    public static DefaultAttributeContainer.Builder setAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 2.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4f);
    } // setAttributes ()

    public static Identifier getEntityTexture() {
        return getEntityTexture(getRandomNumber(getNumberOfTextures()));
    } // getRandomEntityTexture ()

    public static Identifier getEntityTexture(int key) {
        Identifier resource = null;

        switch (key) {
            case 0: resource = VANILLA_00; break;
            case 1: resource = VANILLA_01; break;
            case 2: resource = VANILLA_02; break;
            case 3: resource = VANILLA_03; break;
        }

        return resource;
    } // getEntityTexture ()

    private PlayState idleAnim(AnimationState animationState) {
        if(animationState.isMoving()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.lovelyrobot.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if(this.isSitting) {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.lovelyrobot.sit", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        } else {
            animationState.getController().setAnimation(RawAnimation.begin().then("animation.lovelyrobot.idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

    } // idleAnim ()

    protected void ChangeColor(ItemStack itemStack) {
        if(itemStack.isOf(Items.PINK_DYE) ) currentTexture = getEntityTexture(0);
        if(itemStack.isOf(Items.YELLOW_DYE) ) currentTexture = getEntityTexture(1);
        if(itemStack.isOf(Items.LIGHT_BLUE_DYE) ) currentTexture = getEntityTexture(2);
        if(itemStack.isOf(Items.BLACK_DYE) ) currentTexture = getEntityTexture(3);
    } // ChangeColor

    public void ChangeModel() {
        if(currentState == RobotState.Hunt || currentState == RobotState.Track)
            currentModel = ARMED_MODEL;
        else {
            currentModel = UNARMED_MODEL;
        }
    } // ChangeModel ()


    // -- Inheritance --
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController(this, "controller", 0, this::idleAnim));
    } // registerControllers ()

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    } // getAnimatableInstanceCache ()

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        super.interactMob(player, hand);

        ItemStack itemStack = player.getStackInHand(hand);
        if (this.world.isClient) {
            if (hand == Hand.MAIN_HAND) ChangeColor(itemStack);
        }

        return ActionResult.SUCCESS;
    } // interactMob ()

    @Override
    public void HuntMode (ItemStack itemStack) {
        super.HuntMode(itemStack);
        ChangeModel();
    } // HuntMode ()

} // Class VanillaEntity