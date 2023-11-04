package net.msymbios.rlovelyr.particles;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.particles.custom.LevelUpParticle;

public class LovelyRobotParticles {

    // -- Variables --
    public static final DefaultParticleType LEVEL_UP = FabricParticleTypes.simple();

    // -- Methods --
    public static DefaultParticleType register() {
        return Registry.register(Registries.PARTICLE_TYPE, new Identifier(LovelyRobot.MODID, "level_up"), LEVEL_UP);
    } // register ()

    public static void registerRender () {
        ParticleFactoryRegistry.getInstance().register(LEVEL_UP, LevelUpParticle.Factory::new);
    } // registerRender ()

} // Class LovelyRobotParticles