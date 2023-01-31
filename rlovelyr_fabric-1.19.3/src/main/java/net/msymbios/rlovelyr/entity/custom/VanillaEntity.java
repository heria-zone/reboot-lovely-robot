package net.msymbios.rlovelyr.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.*;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.LovelyRobotMod;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class VanillaEntity extends RobotEntity implements GeoEntity{

    // -- Variables --
    private AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    // -- Initialize --
    static {
        TEXTURES.put(0, new Identifier(LovelyRobotMod.MODID, "textures/entity/vanilla/vanilla_00.png"));
        TEXTURES.put(1, new Identifier(LovelyRobotMod.MODID, "textures/entity/vanilla/vanilla_01.png"));
        TEXTURES.put(2, new Identifier(LovelyRobotMod.MODID, "textures/entity/vanilla/vanilla_02.png"));
        TEXTURES.put(3, new Identifier(LovelyRobotMod.MODID, "textures/entity/vanilla/vanilla_03.png"));

        MODELS.put("normal", new Identifier(LovelyRobotMod.MODID, "geo/vanilla.geo.json"));
        MODELS.put("attack", new Identifier(LovelyRobotMod.MODID, "geo/vanilla.attack.geo.json"));

        ANIMATIONS.put("locomotion", new Identifier(LovelyRobotMod.MODID, "animations/lovelyrobot.animation.json"));
    }

    // -- Constructor --
    public VanillaEntity(EntityType<? extends RobotEntity> entityType, World world) {
        super(entityType, world);
    } // Constructor ModEntities ()

    // -- Methods --
    public static DefaultAttributeContainer.Builder setAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 2.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4f);
    } // setAttributes ()

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
    protected void InitializeEntity() {
        super.InitializeEntity();
    } // InitializeEntity ()

    @Override
    protected void initGoals() {
        super.initGoals();
    } // initGoals ()

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController(this, "locomotionController", 0, this::locomotionAnim));
        controllerRegistrar.add(new AnimationController(this, "attackController", 0, this::attackAnim));
    } // registerControllers ()

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    } // getAnimatableInstanceCache ()

} // Class VanillaEntity