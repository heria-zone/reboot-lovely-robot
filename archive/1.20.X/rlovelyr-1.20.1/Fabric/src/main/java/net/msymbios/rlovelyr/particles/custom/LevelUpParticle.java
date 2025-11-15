package net.msymbios.rlovelyr.particles.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class LevelUpParticle extends SpriteBillboardParticle {

    // -- Constructor --
    protected LevelUpParticle(ClientWorld world, double xCoord, double yCoord, double zCoord, double xd, double yd, double zd) {
        super(world, xCoord, yCoord, zCoord, xd, yd, zd);

        this.velocityMultiplier = 0.6F;
        this.x = xd;
        this.y = yd;
        this.z = zd;
        this.scale *= 0.75F;
        this.maxAge = 20;

        this.red = 1F;
        this.green = 1F;
        this.blue = 1F;
    } // Constructor LevelUpParticle ()

    // -- Inherited Methods --
    @Override
    public void tick() {
        super.tick();
        fadeOut();
    } // tick ()

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    } // getType ()

    // -- Method --
    private void fadeOut() {
        this.alpha = (-(1/(float)maxAge) * age + 1);
    } // fadeOut ()

    // -- Class --
    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {

        // -- Variable --
        private final SpriteProvider sprites;

        // -- Constructor --
        public Factory(SpriteProvider spriteSet) {
            this.sprites = spriteSet;
        } // Constructor Factory ()

        // -- Method --
        public Particle createParticle(DefaultParticleType particleType, ClientWorld level, double x, double y, double z, double dx, double dy, double dz) {
            LevelUpParticle particle = new LevelUpParticle(level, x, y, z, dx, dy, dz);
            particle.setSprite(this.sprites);
            particle.setSpriteForAge(this.sprites);
            return particle;
        } // createParticle ()

    } // Class Factory

} // Class LevelUpParticle