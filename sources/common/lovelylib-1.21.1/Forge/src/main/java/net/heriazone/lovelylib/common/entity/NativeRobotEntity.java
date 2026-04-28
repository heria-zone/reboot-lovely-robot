package net.heriazone.lovelylib.common.entity;

import net.heriazone.lovelylib.hzlib.api.entity.InternalAnimation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

/**
 * Forge-specific robot entity implementation with GeckoLib integration.
 * <p>
 * <b>Architecture:</b> Extends BaseRobotEntity for common behavior while implementing
 * GeoEntity for Forge-specific GeckoLib animation system integration.
 * <p>
 * <b>Design Decision:</b> Composition over inheritance - single entity class with
 * behavior configured via NativeEntityType instead of separate classes per variant.
 * Reduces code duplication and simplifies variant addition.
 * <p>
 * <b>Forge Specifics:</b> Uses variant-based pickup item resolution with Supplier.get()
 * calls for deferred registry object access.
 */
public class NativeRobotEntity extends LovelyRobotEntity implements GeoEntity {

    // -- Variables --

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    // -- Constructor --

    public NativeRobotEntity(EntityType<? extends RobotEntity> entityType, Level level, NativeEntityType nativeEntity) {
        super(entityType, level, nativeEntity);
        handlePostSpawnInitialization();
    } // Constructor: NativeRobotEntity ()

    // -- Inherited Methods --

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegister) {
        controllerRegister.add(InternalAnimation.locomotionAnimation(this));
        controllerRegister.add(InternalAnimation.attackAnimation(this));
    } // registerControllers ()

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; } // getAnimatableInstanceCache ()

} // Class: NativeRobotEntity