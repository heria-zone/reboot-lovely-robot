package net.heriazone.hzlib.api.entity.features.exchange;

import net.heriazone.hzlib.api.entity.internal.InternalParticle;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

/**
 * <p>Configures the audiovisual and animation response when an exchange fires.<p>
 * <p>
 * <b>Architecture:</b> Pure data container. Execution is the entity's responsibility —
 * it calls {@link #play(net.minecraft.world.entity.Entity)} on the server side after a
 * successful exchange. This keeps feedback logic centralized without coupling the feature
 * to entity internals.
 * <p>
 * <b>Global vs. local:</b> An {@link ExchangeFeature} can declare a
 * {@code globalFeedback} used by any rule that does not specify its own feedback.
 * Per-rule feedback overrides the global one for that rule only.
 * <p>
 * <b>Particles:</b> Delegates to {@link InternalParticle} for consistent visual behavior
 * across all HZLib entities. The {@link ParticleType} enum maps to existing
 * {@link InternalParticle} effects so new particle types added there automatically
 * become available here.
 * <p>
 * <b>Animation:</b> Stores an optional animation key that the entity can read and
 * trigger via its GeckoLib controller. Actual animation triggering is the entity's
 * responsibility since the feature layer has no GeckoLib dependency.
 */
public final class ExchangeFeedback {

    // -- Inner Enum --

    /**
     * <p>Maps symbolic particle choices to {@link InternalParticle} methods.<p>
     * <p>
     * <b>Extension:</b> Add new entries here when {@link InternalParticle} gains new
     * particle methods. The {@code play} method below dispatches to them.
     */
    public enum ParticleType {
        /** Green sparkle — positive interaction (level-up, gift received). */
        HAPPY_VILLAGER,
        /** Heart burst — taming, bonding, affection. */
        HEART,
        /** Ash cloud — damage, decay, dark interactions. */
        ASH,
        /** Poof cloud — spawn, vanish, summoning. */
        POOF,
        /** Smoke — blocked, limited, refused. */
        SMOKE
    } // Enum: ParticleType

    // -- Fields --

    @Nullable private final SoundEvent   sound;
    @Nullable private final ParticleType particle;
    @Nullable private final String       animationKey;

    private final float soundVolume;
    private final float soundPitch;

    // -- Constructor --

    private ExchangeFeedback(Builder builder) {
        this.sound        = builder.sound;
        this.particle     = builder.particle;
        this.animationKey = builder.animationKey;
        this.soundVolume  = builder.soundVolume;
        this.soundPitch   = builder.soundPitch;
    } // Constructor: ExchangeFeedback ()

    // -- Accessors --

    /** Returns the sound to play, or null if no sound is configured. */
    @Nullable
    public SoundEvent getSound() { return sound; } // getSound ()

    /** Returns the particle type to spawn, or null if no particle is configured. */
    @Nullable
    public ParticleType getParticle() { return particle; } // getParticle ()

    /**
     * Returns the GeckoLib animation key to trigger, or null if none configured.
     * <p>
     * <b>Usage:</b> Entity reads this after a successful exchange and triggers the
     * animation via its own animation controller.
     */
    @Nullable
    public String getAnimationKey() { return animationKey; } // getAnimationKey ()

    /** Returns the configured sound volume (default 1.0). */
    public float getSoundVolume() { return soundVolume; } // getSoundVolume ()

    /** Returns the configured sound pitch (default 1.0). */
    public float getSoundPitch() { return soundPitch; } // getSoundPitch ()

    // -- Execution --

    /**
     * Executes all configured feedback effects on the given entity.
     * <p>
     * <b>Server-only:</b> Must be called server-side. Sound is broadcast to nearby
     * players. Particles are sent to all clients in range.
     * <p>
     * <b>Animation key:</b> Not triggered here — the entity handles that itself
     * after calling this method.
     *
     * @param entity entity performing the exchange
     */
    public void play(net.minecraft.world.entity.Entity entity) {
        if (entity.level().isClientSide) return;

        // -- Particles --
        if (particle != null) {
            switch (particle) {
                case HAPPY_VILLAGER -> InternalParticle.HappyVillager(entity);
                case HEART          -> InternalParticle.Heart(entity);
                case ASH            -> InternalParticle.Ash(entity);
                case POOF           -> InternalParticle.Poof(entity);
                case SMOKE          -> InternalParticle.Smoke(entity, 7, 0.2);
            }
        }

        // -- Sound --
        if (sound != null) {
            entity.level().playSound(
                    null,
                    entity.blockPosition(),
                    sound,
                    net.minecraft.sounds.SoundSource.NEUTRAL,
                    soundVolume,
                    soundPitch
            );
        }
    } // play ()

    // -- Builder --

    /**
     * Returns a new builder for composing an {@link ExchangeFeedback}.
     *
     * @return fresh builder instance
     */
    public static Builder builder() {
        return new Builder();
    } // builder ()

    /**
     * <p>Fluent builder for {@link ExchangeFeedback}.<p>
     * <pre>{@code
     * ExchangeFeedback.builder()
     *     .sound(SoundEvents.BUCKET_FILL, 1.0f, 1.2f)
     *     .particle(ExchangeFeedback.ParticleType.HAPPY_VILLAGER)
     *     .animation("give")
     *     .build()
     * }</pre>
     */
    public static final class Builder {

        // -- Variables --

        @Nullable private SoundEvent   sound        = null;
        @Nullable private ParticleType particle     = null;
        @Nullable private String       animationKey = null;
        private float soundVolume = 1.0f;
        private float soundPitch  = 1.0f;

        // -- Constructor --

        private Builder() {} // Constructor: Builder ()

        // -- Methods --

        /**
         * Sets the sound to play on successful exchange with default volume and pitch.
         *
         * @param sound sound event to play
         * @return this builder for chaining
         */
        public Builder sound(SoundEvent sound) {
            this.sound = sound;
            return this;
        } // sound ()

        /**
         * Sets the sound to play on successful exchange with custom volume and pitch.
         *
         * @param sound  sound event to play
         * @param volume sound volume (1.0 = normal)
         * @param pitch  sound pitch (1.0 = normal, &lt;1.0 = lower, &gt;1.0 = higher)
         * @return this builder for chaining
         */
        public Builder sound(SoundEvent sound, float volume, float pitch) {
            this.sound       = sound;
            this.soundVolume = volume;
            this.soundPitch  = pitch;
            return this;
        } // sound ()

        /**
         * Sets the particle effect to spawn on successful exchange.
         *
         * @param particle particle type from {@link ParticleType}
         * @return this builder for chaining
         */
        public Builder particle(ParticleType particle) {
            this.particle = particle;
            return this;
        } // particle ()

        /**
         * Sets the GeckoLib animation key to trigger on successful exchange.
         * <p>
         * <b>Note:</b> The entity reads this key and triggers the animation itself.
         * The feature layer does not interact with GeckoLib directly.
         *
         * @param animationKey GeckoLib animation identifier
         * @return this builder for chaining
         */
        public Builder animation(String animationKey) {
            this.animationKey = animationKey;
            return this;
        } // animation ()

        /**
         * Builds the immutable {@link ExchangeFeedback}.
         *
         * @return configured feedback instance
         */
        public ExchangeFeedback build() {
            return new ExchangeFeedback(this);
        } // build ()

    } // Class: Builder

} // Class: ExchangeFeedback
