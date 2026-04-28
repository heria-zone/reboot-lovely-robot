package net.heriazone.hzlib.api.animation;

import java.util.*;
import java.util.function.Consumer;

/**
 * Declares the complete animation configuration for one animator variant.
 * <p>
 * <b>Architecture:</b> Replaces hardcoded {@code AnimationDefinitions} constants.
 * Each animator variant carries its own profile, registered alongside the animator
 * file path in {@code AnimatorVariantFeature}. The loader-specific
 * {@code InternalAnimation} reads the profile, calls {@link AnimationPool#selectNext}
 * to get a name string, then constructs the GeckoLib {@code RawAnimation}.
 * <p>
 * <b>Fallback chain:</b> When a locomotion slot is empty (null pool or empty pool),
 * the controller falls back to the next option in the priority chain:
 * <pre>
 *   vehicle riding?     → ride ?? sit ?? idle
 *   attacking?          → attack ?? idle
 *   moving?             → walk ?? idle
 *   standby (sitting)?  → sit ?? rest ?? idle
 *   standby (resting)?  → rest ?? idle
 *   default             → idle pool (random/weighted)
 * </pre>
 * The {@code ??} operator means "if null/empty, use next". Idle is always the
 * final fallback and should always be present.
 * <p>
 * <b>Base pose layer:</b> {@code basePoseAnimation} is a plain String (not a pool)
 * that the loader creates as a separate parallel GeckoLib controller. Used for
 * Gourdragora's {@code pose} animation that runs underneath all locomotion.
 * <p>
 * <b>No GeckoLib dependency:</b> This class is pure Java. All GeckoLib construction
 * happens in loader-specific modules.
 */
public final class AnimationProfile {

    // -- Locomotion Slots --

    private final AnimationPool idle;
    private final AnimationPool walk;
    private final AnimationPool rest;
    private final AnimationPool sit;
    private final AnimationPool ride;
    private final AnimationPool attack;
    private final AnimationPool hurt;

    // -- Base Layer --

    /** Optional parallel base pose animation (e.g., Gourdragora's {@code pose}). */
    private final String basePoseAnimation;

    // -- Special Animations --

    /** Named special animations — both pools and sequences are valid values. */
    private final Map<String, ISpecialAnimation> specialAnimations;

    // -- Constructor --

    private AnimationProfile(Builder builder) {
        this.idle   = builder.idle;
        this.walk   = builder.walk;
        this.rest   = builder.rest;
        this.sit    = builder.sit;
        this.ride   = builder.ride;
        this.attack = builder.attack;
        this.hurt   = builder.hurt;
        this.basePoseAnimation  = builder.basePoseAnimation;
        this.specialAnimations  = Collections.unmodifiableMap(new LinkedHashMap<>(builder.specialAnimations));
    } // Constructor: AnimationProfile ()

    // -- Locomotion Slot Accessors --

    /**
     * Returns the idle animation pool.
     * Should always be present — idle is the final fallback for all states.
     *
     * @return idle pool, or {@code null} if not configured
     */
    public AnimationPool getIdle()   { return idle;   } // getIdle ()

    /**
     * Returns the walk animation pool.
     * {@code null} or empty means "fall back to idle when moving" (e.g., Bee).
     *
     * @return walk pool, or {@code null} if not configured
     */
    public AnimationPool getWalk()   { return walk;   } // getWalk ()

    /**
     * Returns the rest animation pool (standby standing state).
     * {@code null} or empty means "fall back to idle in standby" (e.g., Gourdragora default).
     *
     * @return rest pool, or {@code null} if not configured
     */
    public AnimationPool getRest()   { return rest;   } // getRest ()

    /**
     * Returns the sit animation pool (standby sitting state, vehicle riding for robots).
     * {@code null} or empty means "fall back to rest or idle".
     *
     * @return sit pool, or {@code null} if not configured
     */
    public AnimationPool getSit()    { return sit;    } // getSit ()

    /**
     * Returns the ride animation pool (vehicle riding for monsters).
     * {@code null} or empty means "fall back to sit or idle when riding".
     *
     * @return ride pool, or {@code null} if not configured
     */
    public AnimationPool getRide()   { return ride;   } // getRide ()

    /**
     * Returns the attack animation pool.
     * {@code null} or empty means "no attack animation" (e.g., Bee, Slime).
     *
     * @return attack pool, or {@code null} if not configured
     */
    public AnimationPool getAttack() { return attack; } // getAttack ()

    /**
     * Returns the hurt animation pool.
     * {@code null} or empty means "no hurt animation" (e.g., robots, Bee, Slime).
     *
     * @return hurt pool, or {@code null} if not configured
     */
    public AnimationPool getHurt()   { return hurt;   } // getHurt ()

    // -- Base Layer Accessor --

    /**
     * Returns the optional base pose animation name for a parallel GeckoLib controller.
     * {@code null} means no base pose controller should be created.
     *
     * @return base pose animation name, or {@code null}
     */
    public String getBasePoseAnimation() { return basePoseAnimation; } // getBasePoseAnimation ()

    // -- Special Animation Accessors --

    /**
     * Returns the special animation registered under the given trigger name.
     * Returns {@code Optional.empty()} if no special is registered for that name.
     *
     * @param triggerName the trigger name (e.g., {@code "wave"}, {@code "fury_attack"})
     * @return optional special animation
     */
    public Optional<ISpecialAnimation> getSpecial(String triggerName) {
        return Optional.ofNullable(specialAnimations.get(triggerName));
    } // getSpecial ()

    /**
     * Returns an unmodifiable view of all registered special animations.
     *
     * @return map of trigger name → special animation
     */
    public Map<String, ISpecialAnimation> getSpecialAnimations() {
        return specialAnimations;
    } // getSpecialAnimations ()

    // -- Convenience Helpers --

    /**
     * Returns the pool for the given locomotion state name.
     * Supports the standard state names: {@code idle}, {@code walk}, {@code rest},
     * {@code sit}, {@code ride}, {@code attack}, {@code hurt}.
     * Returns {@code null} for unknown state names.
     *
     * @param stateName locomotion state name
     * @return corresponding pool, or {@code null}
     */
    public AnimationPool getPoolForState(String stateName) {
        return switch (stateName.toLowerCase(java.util.Locale.ROOT)) {
            case "idle"   -> idle;
            case "walk"   -> walk;
            case "rest"   -> rest;
            case "sit"    -> sit;
            case "ride"   -> ride;
            case "attack" -> attack;
            case "hurt"   -> hurt;
            default       -> null;
        };
    } // getPoolForState ()

    /**
     * Returns whether a pool is non-null and non-empty.
     * Used by the locomotion controller to determine if a slot is usable.
     *
     * @param pool pool to check
     * @return true if pool has at least one entry
     */
    public static boolean isUsable(AnimationPool pool) {
        return pool != null && !pool.isEmpty();
    } // isUsable ()

    // -- Builder --

    /**
     * Creates a new builder for constructing an {@link AnimationProfile}.
     *
     * @return new builder instance
     */
    public static Builder builder() {
        return new Builder();
    } // builder ()

    /**
     * Fluent builder for {@link AnimationProfile}.
     * <p>
     * <b>Shorthand methods</b> (e.g., {@code .idle("idle")}) create single-entry
     * LOOP pools. Use the {@code Consumer<AnimationPool.Builder>} overloads for
     * multi-entry pools or non-LOOP behaviors.
     */
    public static final class Builder {

        private AnimationPool idle;
        private AnimationPool walk;
        private AnimationPool rest;
        private AnimationPool sit;
        private AnimationPool ride;
        private AnimationPool attack;
        private AnimationPool hurt;
        private String basePoseAnimation;
        private final Map<String, ISpecialAnimation> specialAnimations = new LinkedHashMap<>();

        private Builder() {} // Constructor: Builder ()

        // -- Shorthand setters (single-entry LOOP pools) --

        /** Sets idle to a single-entry LOOP pool. */
        public Builder idle(String name)   { this.idle   = AnimationPool.single(name); return this; }
        /** Sets walk to a single-entry LOOP pool. */
        public Builder walk(String name)   { this.walk   = AnimationPool.single(name); return this; }
        /** Sets rest to a single-entry LOOP pool. */
        public Builder rest(String name)   { this.rest   = AnimationPool.single(name); return this; }
        /** Sets sit to a single-entry LOOP pool. */
        public Builder sit(String name)    { this.sit    = AnimationPool.single(name); return this; }
        /** Sets ride to a single-entry LOOP pool. */
        public Builder ride(String name)   { this.ride   = AnimationPool.single(name); return this; }
        /** Sets attack to a single-entry LOOP pool. */
        public Builder attack(String name) { this.attack = AnimationPool.single(name); return this; }
        /** Sets hurt to a single-entry LOOP pool. */
        public Builder hurt(String name)   { this.hurt   = AnimationPool.single(name); return this; }

        // -- Full pool setters (Consumer<AnimationPool.Builder>) --

        /** Sets idle using a full pool builder. */
        public Builder idle(Consumer<AnimationPool.Builder> config) {
            AnimationPool.Builder b = AnimationPool.builder();
            config.accept(b);
            this.idle = b.build();
            return this;
        } // idle ()

        /** Sets walk using a full pool builder. */
        public Builder walk(Consumer<AnimationPool.Builder> config) {
            AnimationPool.Builder b = AnimationPool.builder();
            config.accept(b);
            this.walk = b.build();
            return this;
        } // walk ()

        /** Sets rest using a full pool builder. */
        public Builder rest(Consumer<AnimationPool.Builder> config) {
            AnimationPool.Builder b = AnimationPool.builder();
            config.accept(b);
            this.rest = b.build();
            return this;
        } // rest ()

        /** Sets sit using a full pool builder. */
        public Builder sit(Consumer<AnimationPool.Builder> config) {
            AnimationPool.Builder b = AnimationPool.builder();
            config.accept(b);
            this.sit = b.build();
            return this;
        } // sit ()

        /** Sets ride using a full pool builder. */
        public Builder ride(Consumer<AnimationPool.Builder> config) {
            AnimationPool.Builder b = AnimationPool.builder();
            config.accept(b);
            this.ride = b.build();
            return this;
        } // ride ()

        /** Sets attack using a full pool builder. */
        public Builder attack(Consumer<AnimationPool.Builder> config) {
            AnimationPool.Builder b = AnimationPool.builder();
            config.accept(b);
            this.attack = b.build();
            return this;
        } // attack ()

        /** Sets hurt using a full pool builder. */
        public Builder hurt(Consumer<AnimationPool.Builder> config) {
            AnimationPool.Builder b = AnimationPool.builder();
            config.accept(b);
            this.hurt = b.build();
            return this;
        } // hurt ()

        // -- Direct pool setters --

        /** Sets idle to the provided pool directly. */
        public Builder idle(AnimationPool pool)   { this.idle   = pool; return this; }
        /** Sets walk to the provided pool directly. */
        public Builder walk(AnimationPool pool)   { this.walk   = pool; return this; }
        /** Sets rest to the provided pool directly. */
        public Builder rest(AnimationPool pool)   { this.rest   = pool; return this; }
        /** Sets sit to the provided pool directly. */
        public Builder sit(AnimationPool pool)    { this.sit    = pool; return this; }
        /** Sets ride to the provided pool directly. */
        public Builder ride(AnimationPool pool)   { this.ride   = pool; return this; }
        /** Sets attack to the provided pool directly. */
        public Builder attack(AnimationPool pool) { this.attack = pool; return this; }
        /** Sets hurt to the provided pool directly. */
        public Builder hurt(AnimationPool pool)   { this.hurt   = pool; return this; }

        // -- Base pose --

        /**
         * Sets the base pose animation name for a parallel GeckoLib controller.
         * Pass {@code null} to disable the base pose controller.
         *
         * @param animationName base pose animation name, or {@code null}
         * @return this builder
         */
        public Builder basePose(String animationName) {
            this.basePoseAnimation = animationName;
            return this;
        } // basePose ()

        // -- Special animations --

        /**
         * Registers a special animation pool under the given trigger name.
         *
         * @param triggerName trigger name (e.g., {@code "wave"})
         * @param pool        animation pool to play when triggered
         * @return this builder
         */
        public Builder special(String triggerName, AnimationPool pool) {
            Objects.requireNonNull(triggerName, "Trigger name cannot be null");
            Objects.requireNonNull(pool, "Pool cannot be null");
            specialAnimations.put(triggerName, pool);
            return this;
        } // special ()

        /**
         * Registers a special animation sequence under the given trigger name.
         *
         * @param triggerName trigger name (e.g., {@code "fury_attack"})
         * @param sequence    animation sequence to run when triggered
         * @return this builder
         */
        public Builder special(String triggerName, AnimationSequence sequence) {
            Objects.requireNonNull(triggerName, "Trigger name cannot be null");
            Objects.requireNonNull(sequence, "Sequence cannot be null");
            specialAnimations.put(triggerName, sequence);
            return this;
        } // special ()

        /**
         * Registers a special animation pool using a builder consumer.
         *
         * @param triggerName trigger name
         * @param config      consumer that configures the pool builder
         * @return this builder
         */
        public Builder special(String triggerName, Consumer<AnimationPool.Builder> config) {
            AnimationPool.Builder b = AnimationPool.builder();
            config.accept(b);
            return special(triggerName, b.build());
        } // special ()

        /**
         * Builds the animation profile.
         *
         * @return new {@link AnimationProfile} instance
         */
        public AnimationProfile build() {
            return new AnimationProfile(this);
        } // build ()

    } // Class: Builder

    // -- Object Overrides --

    @Override
    public String toString() {
        return "AnimationProfile{" +
                "idle=" + idle +
                ", walk=" + walk +
                ", rest=" + rest +
                ", sit=" + sit +
                ", ride=" + ride +
                ", attack=" + attack +
                ", hurt=" + hurt +
                ", basePose=" + basePoseAnimation +
                ", specials=" + specialAnimations.keySet() +
                '}';
    } // toString ()

} // Class: AnimationProfile
