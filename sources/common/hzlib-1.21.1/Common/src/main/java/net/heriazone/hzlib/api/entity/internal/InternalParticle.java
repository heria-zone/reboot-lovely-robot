package net.heriazone.hzlib.api.entity.internal;

import net.heriazone.hzlib.framework.utils.MathUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;

/**
 * Manages particle effect spawning for robot entities.
 * <p>
 * <b>Architecture:</b> Centralizes particle logic to ensure consistent visual feedback
 * across all robot types. Particles are spawned client-side for performance.
 * <p>
 * <b>Performance:</b> Spawns fixed particle count to avoid overwhelming clients with
 * excessive particle rendering in scenarios with many robots.
 */
public class InternalParticle {

    // -- Particle Configuration --

    private static final double VELOCITY_MULTIPLIER = 0.02D;
    private static final int MAXIMUM_NUMBER_PARTICLES = 7;

    // -- Particle Effects --

    /**
     * Spawns ash particles around entity.
     * <p>
     * <b>Usage:</b> Visual feedback for robot death or damage effects.
     *
     * @param entity entity to spawn particles around
     */
    public static void Ash(Entity entity) {
        for (int i = 0; i < MAXIMUM_NUMBER_PARTICLES; ++i) {
            double d0 = calculateVelocity();
            double d1 = calculateVelocity();
            double d2 = calculateVelocity();
            entity.level().addParticle(ParticleTypes.ASH, entity.getX(1.0D), entity.getRandomY() + 0.5D, entity.getZ(1.0D), d0, d1, d2);
        }
        entity.level().broadcastEntityEvent(entity, (byte) 6);
    } // Ash ()

    /**
     * Spawns heart particles around entity.
     * <p>
     * <b>Usage:</b> Visual feedback when robot is tamed or healed.
     *
     * @param entity entity to spawn particles around
     */
    public static void Heart(Entity entity) {
        for (int i = 0; i < MAXIMUM_NUMBER_PARTICLES; ++i) {
            double d0 = calculateVelocity();
            double d1 = calculateVelocity();
            double d2 = calculateVelocity();
            entity.level().addParticle(ParticleTypes.HEART, entity.getX(1.0D), entity.getRandomY() + 0.5D, entity.getZ(1.0D), d0, d1, d2);
        }
        entity.level().broadcastEntityEvent(entity, (byte) 7);
    } // Heart ()

    /**
     * Spawns poof particles around entity with size-based scaling.
     * <p>
     * <b>Usage:</b> Visual feedback for robot spawn, retrieval, or teleportation.
     * Creates cloud-like effect that scales with entity dimensions.
     *
     * @param entity entity to spawn particles around
     */
    public static void Poof(Entity entity) {
        if (entity.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            for (int i = 0; i < MAXIMUM_NUMBER_PARTICLES; ++i) {
                double d0 = calculateVelocity();
                double d1 = calculateVelocity();
                double d2 = calculateVelocity();
                serverLevel.sendParticles(
                        ParticleTypes.POOF,
                        entity.getX(1.0D),
                        entity.getRandomY() + 0.5D,
                        entity.getZ(1.0D),
                        1, // count per iteration
                        d0, d1, d2, // velocity
                        0.0 // speed multiplier
                );
            }
        }
    } // Poof ()

    /**
     * Spawns happy villager particles around entity with size-based scaling.
     * <p>
     * <b>Usage:</b> Visual feedback for robot level-up or positive events.
     * Creates green sparkle effect that scales with entity dimensions.
     *
     * @param entity entity to spawn particles around
     */
    public static void HappyVillager(Entity entity) {
        if (entity.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            for (int i = 0; i < MAXIMUM_NUMBER_PARTICLES; ++i) {
                double d0 = calculateVelocity();
                double d1 = calculateVelocity();
                double d2 = calculateVelocity();
                serverLevel.sendParticles(
                        ParticleTypes.HAPPY_VILLAGER,
                        entity.getX(1.0D),
                        entity.getRandomY() + 0.5D,
                        entity.getZ(1.0D),
                        1, // count per iteration
                        d0, d1, d2, // velocity
                        0.0 // speed multiplier
                );
            }
        }
    } // HappyVillager ()

    /**
     * Spawns smoke particles around entity to indicate combat radius limitation.
     * <p>
     * <b>Usage:</b> Visual feedback when robot cannot chase enemy beyond allowed radius.
     * Indicates robot is at maximum pursuit distance from owner or base position.
     * <p>
     * <b>Configuration:</b> Particle count and spread controlled by config values.
     *
     * @param entity entity to spawn particles around
     * @param particleCount number of smoke particles to spawn
     * @param spread particle spread radius
     */
    public static void Smoke(Entity entity, int particleCount, double spread) {
        if (entity.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.SMOKE,
                    entity.getX(),
                    entity.getY() + 0.5,
                    entity.getZ(),
                    particleCount,
                    spread, spread, spread, // spread in x, y, z
                    0.01 // speed
            );
        }
    } // Smoke ()

    // -- Utility Methods --

    /**
     * Calculates random particle velocity using Gaussian distribution.
     *
     * @return random velocity value
     */
    private static double calculateVelocity() {
        return MathUtils.RANDOM.nextGaussian() * VELOCITY_MULTIPLIER;
    } // calculateVelocity ()

    /**
     * Calculates random particle velocity with custom multiplier.
     *
     * @param multiplier velocity multiplier
     * @return random velocity value
     */
    private static double calculateVelocity(double multiplier) {
        return MathUtils.RANDOM.nextGaussian() * multiplier;
    } // calculateVelocity ()

} // Class: InternalParticle