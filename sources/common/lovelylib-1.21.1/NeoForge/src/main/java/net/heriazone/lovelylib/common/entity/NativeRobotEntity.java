package net.heriazone.lovelylib.common.entity;

import net.heriazone.lovelylib.hzlib.api.entity.RobotAnimation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

/**
 * NeoForge-specific robot entity implementation with GeckoLib integration.
 * <p>
 * <b>Architecture:</b> Extends {@link RobotEntity} for common behavior while implementing
 * {@code GeoEntity} for NeoForge-specific GeckoLib animation system integration.
 * <p>
 * <b>Design Decision:</b> Composition over inheritance — single entity class with
 * behavior configured via {@link RobotFamily} instead of separate classes per variant.
 * Reduces code duplication and simplifies variant addition.
 * <p>
 * <b>NeoForge Specifics:</b> Uses variant-based pickup item resolution with Supplier.get()
 * calls for deferred registry object access.
 */
public class NativeRobotEntity extends RobotEntity implements GeoEntity {

    // -- Variables --

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    // -- Constructor --

    public NativeRobotEntity(EntityType<? extends RobotEntity> entityType, Level level, RobotFamily nativeEntity) {
        super(entityType, level, nativeEntity);
    } // Constructor: NativeRobotEntity ()

    // -- Inherited Methods --

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegister) {
        controllerRegister.add(RobotAnimation.locomotionAnimation(this));
        controllerRegister.add(RobotAnimation.attackAnimation(this));
    } // registerControllers ()

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; } // getAnimatableInstanceCache ()

} // Class: NativeRobotEntity