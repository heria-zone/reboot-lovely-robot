package net.heriazone.lovelylib.common.entity;

import net.heriazone.hzlib.api.entity.NativeAnimation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

/**
 * Fabric-specific robot entity implementation with GeckoLib integration.
 * <p>
 * <b>Architecture:</b> Extends {@link RobotEntity} for common behavior while implementing
 * {@code GeoEntity} for Fabric-specific GeckoLib animation system integration.
 * <p>
 * <b>Design Decision:</b> Composition over inheritance — single entity class with
 * behavior configured via {@link RobotFamily} instead of separate classes per variant.
 * Reduces code duplication and simplifies variant addition.
 * <p>
 * <b>Fabric Specifics:</b> Uses constructor-injected pickup item for flexibility
 * and direct item references without Supplier wrappers.
 */
public class NativeRobotEntity extends RobotEntity implements GeoEntity {

    // -- Variables --

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    // -- Constructor --

    public NativeRobotEntity(EntityType<? extends RobotEntity> entityType, Level level, RobotFamily nativeEntity) {
        super(entityType, level, nativeEntity);
        handlePostSpawnInitialization();
    } // Constructor: NativeRobotEntity ()

    // -- Inherited Methods --

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegister) {
        controllerRegister.add(NativeAnimation.locomotionAnimation(this));
        controllerRegister.add(NativeAnimation.attackAnimation(this));
    } // registerControllers ()

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; } // getAnimatableInstanceCache ()

} // Class: NativeRobotEntity