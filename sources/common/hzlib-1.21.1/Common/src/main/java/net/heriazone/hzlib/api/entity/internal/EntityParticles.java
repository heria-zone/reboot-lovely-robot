package net.heriazone.hzlib.api.entity.internal;

import net.heriazone.hzlib.framework.utils.MathUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;

/**
 * Static particle effect library for HZLib-managed entities.
 * <p>
 * Each method spawns a fixed set of particles around the given entity.
 * The particle count is capped at {@link #MAXIMUM_NUMBER_PARTICLES} to keep
 * visual feedback consistent regardless of how many entities are on screen.
 * Velocity is randomised via a Gaussian distribution scaled by {@link #VELOCITY_MULTIPLIER}.
 */
public class EntityParticles {

    // -- Configuration --

    private static final double VELOCITY_MULTIPLIER    = 0.02D;
    private static final int    MAXIMUM_NUMBER_PARTICLES = 7;

    // -- Particle Effects --

    /**
     * Spawns {@link net.minecraft.core.particles.ParticleTypes#ASH ASH} particles around
     * the entity and broadcasts the hurt event (byte 6) to nearby clients.
     */
    public static void Ash(Entity entity) {
        for (int i = 0; i < MAXIMUM_NUMBER_PARTICLES; ++i) {
            entity.level().addParticle(ParticleTypes.ASH,
                    entity.getX(1.0D), entity.getRandomY() + 0.5D, entity.getZ(1.0D),
                    calculateVelocity(), calculateVelocity(), calculateVelocity());
        }
        entity.level().broadcastEntityEvent(entity, (byte) 6);
    } // Ash ()

    /**
     * Spawns {@link net.minecraft.core.particles.ParticleTypes#HEART HEART} particles around
     * the entity and broadcasts the tame event (byte 7) to nearby clients.
     */
    public static void Heart(Entity entity) {
        for (int i = 0; i < MAXIMUM_NUMBER_PARTICLES; ++i) {
            entity.level().addParticle(ParticleTypes.HEART,
                    entity.getX(1.0D), entity.getRandomY() + 0.5D, entity.getZ(1.0D),
                    calculateVelocity(), calculateVelocity(), calculateVelocity());
        }
        entity.level().broadcastEntityEvent(entity, (byte) 7);
    } // Heart ()

    /**
     * Spawns {@link net.minecraft.core.particles.ParticleTypes#POOF POOF} particles
     * around the entity. Server-side only — uses {@code ServerLevel.sendParticles}.
     */
    public static void Poof(Entity entity) {
        if (entity.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            for (int i = 0; i < MAXIMUM_NUMBER_PARTICLES; ++i) {
                serverLevel.sendParticles(ParticleTypes.POOF,
                        entity.getX(1.0D), entity.getRandomY() + 0.5D, entity.getZ(1.0D),
                        1,
                        calculateVelocity(), calculateVelocity(), calculateVelocity(),
                        0.0);
            }
        }
    } // Poof ()

    /**
     * Spawns {@link net.minecraft.core.particles.ParticleTypes#HAPPY_VILLAGER HAPPY_VILLAGER}
     * particles around the entity. Server-side only.
     */
    public static void HappyVillager(Entity entity) {
        if (entity.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            for (int i = 0; i < MAXIMUM_NUMBER_PARTICLES; ++i) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        entity.getX(1.0D), entity.getRandomY() + 0.5D, entity.getZ(1.0D),
                        1,
                        calculateVelocity(), calculateVelocity(), calculateVelocity(),
                        0.0);
            }
        }
    } // HappyVillager ()

    /**
     * Spawns {@link net.minecraft.core.particles.ParticleTypes#SMOKE SMOKE} particles
     * centered on the entity. Server-side only.
     * <p>
     * Unlike the fixed-count effects above, particle count and spread are caller-controlled
     * to support different signal strengths at different call sites.
     *
     * @param particleCount number of smoke particles to emit
     * @param spread        spread radius in each axis
     */
    public static void Smoke(Entity entity, int particleCount, double spread) {
        if (entity.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                    entity.getX(), entity.getY() + 0.5, entity.getZ(),
                    particleCount,
                    spread, spread, spread,
                    0.01);
        }
    } // Smoke ()

    // -- Velocity Helpers --

    // Gaussian distribution keeps most particles near the entity while allowing
    // occasional outliers, producing a natural scattered burst rather than a fixed shell.

    private static double calculateVelocity() {
        return calculateVelocity(VELOCITY_MULTIPLIER);
    } // calculateVelocity ()

    private static double calculateVelocity(double multiplier) {
        return MathUtils.RANDOM.nextGaussian() * multiplier;
    } // calculateVelocity ()

} // Class: EntityParticles