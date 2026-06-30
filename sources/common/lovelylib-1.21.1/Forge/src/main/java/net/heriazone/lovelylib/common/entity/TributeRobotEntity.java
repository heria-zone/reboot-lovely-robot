package net.heriazone.lovelylib.common.entity;

import net.heriazone.hzlib.api.entity.internal.EntityParticles;
import net.heriazone.lovelylib.hzlib.api.entity.RobotAnimation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

/**
 * Tribute-specific robot entity with heart particles and no spawn sound.
 * <p>
 * <b>Architecture:</b> Extends {@link NativeRobotEntity} and overrides only
 * {@link #onTameEffect(Player)} — the protected hook extracted in {@link RobotEntity}
 * specifically for this purpose. The ownership, registry, and display-message flow
 * in {@link RobotEntity#handleTame(Player)} is untouched.
 * <p>
 * <b>Tribute behavior:</b> Spawning produces {@link EntityParticles#Heart Heart}
 * particles (tame event byte 7) with no sound, evoking a gentler, nostalgic feel
 * that matches the Tribute mod's faithful-recreation identity.
 * <p>
 * <b>Contrast with legacy/reboot:</b> Those mods use the default {@link RobotEntity}
 * implementation — {@link EntityParticles#Poof Poof} particles + TOTEM_USE sound.
 */
public class TributeRobotEntity extends NativeRobotEntity implements GeoEntity {

    // -- Variables --

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    // -- Constructor --

    public TributeRobotEntity(EntityType<? extends RobotEntity> entityType, Level level, RobotFamily nativeEntity) {
        super(entityType, level, nativeEntity);
        handlePostSpawnInitialization();
    } // Constructor: TributeRobotEntity ()

    // -- Tame Effect Override --

    /**
     * Tribute spawn effect — heart particles only, no sound.
     * <p>
     * Overrides {@link RobotEntity#onTameEffect(Player)} so that only the
     * visual feedback changes; all ownership and registry logic in
     * {@link RobotEntity#handleTame(Player)} remains unmodified.
     *
     * @param player the player taming this robot
     */
    @Override
    protected void onTameEffect(Player player) {
        EntityParticles.Heart(this);
        // Intentionally no sound — Tribute spawn is silent
    } // onTameEffect ()

    // -- GeckoLib --

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegister) {
        controllerRegister.add(RobotAnimation.locomotionAnimation(this));
        controllerRegister.add(RobotAnimation.attackAnimation(this));
    } // registerControllers ()

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; } // getAnimatableInstanceCache ()

} // Class: TributeRobotEntity
