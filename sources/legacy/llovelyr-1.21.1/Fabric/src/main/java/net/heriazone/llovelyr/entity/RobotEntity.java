package net.heriazone.llovelyr.entity;

import net.heriazone.lovelylib.common.entity.*;
import net.heriazone.lovelylib.hzlib.api.entity.InternalAnimation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.*;
import software.bernie.geckolib.animatable.instance.*;
import software.bernie.geckolib.animation.AnimatableManager;

/**
 * Fabric-specific robot entity implementation with GeckoLib integration.
 * <p>
 * <b>Architecture:</b> Extends BaseRobotEntity for common behavior while implementing
 * GeoEntity for Fabric-specific GeckoLib animation system integration.
 * <p>
 * <b>Design Decision:</b> Composition over inheritance - single entity class with
 * behavior configured via NativeEntityType instead of separate classes per variant.
 * Reduces code duplication and simplifies variant addition.
 * <p>
 * <b>Fabric Specifics:</b> Uses constructor-injected pickup item for flexibility
 * and direct item references without Supplier wrappers.
 */
public class RobotEntity extends LovelyRobotEntity implements GeoEntity {

    // -- Variables --

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    // -- Constructor --

    public RobotEntity(EntityType<? extends LovelyRobotEntity> entityType, Level level, NativeEntityType nativeEntity) {
        super(entityType, level, nativeEntity);
        handlePostSpawnInitialization();
    } // Constructor: RobotEntity ()

    // -- Inherited Methods --

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegister) {
        controllerRegister.add(InternalAnimation.locomotionAnimation(this));
        controllerRegister.add(InternalAnimation.attackAnimation(this));
    } // registerControllers ()

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; } // getAnimatableInstanceCache ()

} // Class: RobotEntity