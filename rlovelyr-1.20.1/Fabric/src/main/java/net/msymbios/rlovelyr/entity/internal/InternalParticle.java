package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.random.Random;
import net.msymbios.rlovelyr.particles.LovelyRobotParticles;

public class InternalParticle {

    // -- Variables --
    private static final double VelocityMultiplier = 0.02D;
    private static final int MaximumNumberParticles = 7;

    private static final Random random = Random.create();

    // -- Methods --

    // PARTICLES
    public static void Ash(Entity entity) {
        for (int i = 0; i < MaximumNumberParticles; ++i) {
            double d0 = CalculateVelocity();
            double d1 = CalculateVelocity();
            double d2 = CalculateVelocity();
            entity.getWorld().addParticle(ParticleTypes.ASH, entity.getParticleX(1.0D), entity.getRandomBodyY() + 0.5D, entity.getParticleZ(1.0D), d0, d1, d2);
        }
    } // Ash ()

    public static void Heart(Entity entity) {
        for (int i = 0; i < MaximumNumberParticles; ++i) {
            double d0 = CalculateVelocity();
            double d1 = CalculateVelocity();
            double d2 = CalculateVelocity();
            entity.getWorld().addParticle(ParticleTypes.HEART, entity.getParticleX(1.0D), entity.getRandomBodyY() + 0.5D, entity.getParticleZ(1.0D), d0, d1, d2);
        }
    } // Heart ()

    public static void LevelUp(Entity entity) {
        for (int i = 0; i < MaximumNumberParticles; ++i) {
            double d0 = CalculateVelocity();
            double d1 = CalculateVelocity();
            double d2 = CalculateVelocity();
            entity.getWorld().addParticle(LovelyRobotParticles.LEVEL_UP, entity.getParticleX(1.0D), entity.getRandomBodyY() + 0.5D, entity.getParticleZ(1.0D), d0, d1, d2);
        }
    } // LevelUp ()

    // UTILITY
    private static double CalculateVelocity() {
        return random.nextGaussian() * VelocityMultiplier;
    } // CalculateVelocity ()

    private static double CalculateVelocity(double multiplier) {
        return random.nextGaussian() * multiplier;
    } // CalculateVelocity ()

} // Class InternalParticle