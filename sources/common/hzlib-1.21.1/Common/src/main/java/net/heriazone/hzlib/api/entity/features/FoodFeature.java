package net.heriazone.hzlib.api.entity.features;

import net.heriazone.hzlib.api.entity.NativeEntity;
import net.heriazone.hzlib.api.entity.internal.EntityParticles;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>Manages taming food, tempting items, taming probability, and taming feedback.<p>
 * <p>
 * <b>Architecture:</b> Composable feature attached to any entity type via the feature
 * system. Owns the complete taming interaction contract:
 * <ul>
 *   <li>Which items can tame the entity</li>
 *   <li>The probability of taming succeeding per item use</li>
 *   <li>The visual and audio feedback on success and failure</li>
 *   <li>Which items make untamed entities follow (tempt goal)</li>
 * </ul>
 * <p>
 * <b>Probability model:</b> Mirrors vanilla wolf/cat taming — one item is always
 * consumed per attempt, success or failure. Probabilities are independent per attempt.
 * <ul>
 *   <li><b>No chance configured</b> → 1/3 (vanilla default, matches wolf and cat)</li>
 *   <li><b>Global chance</b> → applied to every food that has no per-item override</li>
 *   <li><b>Per-food chance</b> → overrides global for that specific item only</li>
 *   <li><b>Chance 1.0</b> → guaranteed tame (explicit opt-in, not the default)</li>
 * </ul>
 * <p>
 * <b>Feedback:</b> Default particles are heart (success) and ash (failure), matching
 * vanilla feel. Sound is muted by default — configure explicitly if desired.
 * <p>
 * <b>TemptGoal:</b> Taming probability has no effect on tempting behavior. The AI
 * goal still activates when the player holds any registered food.
 * <p>
 * <b>Usage:</b>
 * <pre>{@code
 * // Simple — vanilla 1/3 chance, default particles, no sound
 * new FoodFeature().withFoods(Items.COOKIE)
 *
 * // Custom global chance with sound
 * new FoodFeature()
 *     .withFoods(Items.COOKIE)
 *     .withGlobalChance(0.25f)
 *     .withFeedback(TamingFeedback.builder()
 *         .onSuccess(TamingFeedback.ParticleType.HEART)
 *         .onFail(TamingFeedback.ParticleType.ASH)
 *         .successSound(SoundEvents.PLAYER_LEVELUP)
 *         .build())
 *
 * // Per-food chance — cookie 25%, cake 75%
 * new FoodFeature()
 *     .withFood(Items.COOKIE, 0.25f)
 *     .withFood(Items.CAKE,   0.75f)
 *
 * // Guaranteed tame — no gamble
 * new FoodFeature().withFoods(Items.COOKIE).withGlobalChance(1.0f)
 * }</pre>
 */
public class FoodFeature {

    // -- Constants --

    /**
     * Vanilla-equivalent taming probability — 1/3 per attempt, matching wolf and cat.
     * Applied when no global chance is explicitly configured.
     */
    public static final float VANILLA_CHANCE = 1.0f / 3.0f;

    /**
     * Sentinel value used in the per-food map to indicate "use global chance".
     * Not exposed to callers — use {@link #withFoods(Item...)} for global-chance foods.
     */
    private static final float USE_GLOBAL = -1f;

    // -- Fields --

    /**
     * Maps each registered food item to its per-item taming chance.
     * Value {@link #USE_GLOBAL} means the global chance applies for that item.
     */
    private final Map<Item, Float> foods;

    /** Global taming chance applied to items without a per-item override. */
    private float globalChance = USE_GLOBAL; // sentinel: use VANILLA_CHANCE when not set

    private final Set<Item> temptingItems;

    @Nullable private TamingFeedback feedback;

    // -- Constructors --

    /**
     * Creates empty food feature with no configured foods or tempting items.
     */
    public FoodFeature() {
        this.foods        = new LinkedHashMap<>();
        this.temptingItems = new HashSet<>();
        this.feedback      = null;
    } // Constructor: FoodFeature ()

    // -- Food Registration --

    /**
     * Adds items using the global taming chance (or vanilla default if none set).
     * <p>
     * Existing call sites using {@code withFoods(Items.COOKIE)} require no changes.
     *
     * @param items items to register as taming foods
     * @return this instance for chaining
     */
    public FoodFeature withFoods(Item... items) {
        if (items != null) {
            for (Item item : items) {
                if (item != null) foods.put(item, USE_GLOBAL);
            }
        }
        return this;
    } // withFoods ()

    /**
     * Adds a single food item with an explicit per-item taming chance.
     * <p>
     * Overrides the global chance for this item only.
     *
     * @param item   food item
     * @param chance probability of taming succeeding (0.0–1.0)
     * @return this instance for chaining
     */
    public FoodFeature withFood(Item item, float chance) {
        if (item != null) foods.put(item, clampChance(chance));
        return this;
    } // withFood ()

    /**
     * Sets the global taming chance applied to all foods that have no per-item override.
     * <p>
     * <b>Not set</b> → vanilla 1/3 chance.<br>
     * <b>Set to 1.0f</b> → guaranteed tame.
     *
     * @param chance probability of taming succeeding (0.0–1.0)
     * @return this instance for chaining
     */
    public FoodFeature withGlobalChance(float chance) {
        this.globalChance = clampChance(chance);
        return this;
    } // withGlobalChance ()

    /**
     * Configures visual and audio feedback for taming attempts.
     * <p>
     * When not set: heart particles on success, ash particles on failure, no sound.
     *
     * @param feedback feedback configuration
     * @return this instance for chaining
     */
    public FoodFeature withFeedback(TamingFeedback feedback) {
        this.feedback = feedback;
        return this;
    } // withFeedback ()

    // -- Tempting Items --

    /**
     * Adds items to the tempting set — used by {@code TemptGoal} to make untamed
     * entities walk toward a player holding the item.
     * <p>
     * Tempting is independent of taming chance.
     *
     * @param items items to register as tempting
     * @return this instance for chaining
     */
    public FoodFeature withTemptingItems(Item... items) {
        if (items != null) {
            for (Item item : items) {
                if (item != null) temptingItems.add(item);
            }
        }
        return this;
    } // withTemptingItems ()

    // -- Taming Attempt --

    /**
     * Attempts to tame an entity using the given item stack.
     * <p>
     * <b>Contract:</b>
     * <ol>
     *   <li>If the item is not a registered food → no effect, returns {@code false}</li>
     *   <li>Roll the taming chance for this item</li>
     *   <li>Always consume one item (creative mode excluded)</li>
     *   <li>Play success or failure feedback</li>
     *   <li>Return {@code true} if taming succeeded</li>
     * </ol>
     * <p>
     * <b>Server-only:</b> Must only be called on the server side. Call site is
     * {@link NativeEntity#onCommonInteraction}.
     *
     * @param entity entity being tamed
     * @param player player attempting to tame
     * @param stack  item the player is holding
     * @return {@code true} if the taming succeeded this attempt
     */
    public boolean attemptTame(net.minecraft.world.entity.TamableAnimal entity,
                               net.minecraft.world.entity.player.Player player,
                               ItemStack stack) {
        Item item = stack.getItem();
        if (!foods.containsKey(item)) return false;

        // Resolve effective chance for this item
        float itemChance = foods.get(item);
        float effectiveChance = (itemChance == USE_GLOBAL) ? resolvedGlobalChance() : itemChance;

        // Always consume the item — success or failure
        if (!player.getAbilities().instabuild) stack.shrink(1);

        boolean success = ThreadLocalRandom.current().nextFloat() < effectiveChance;
        playFeedback(entity, success);
        return success;
    } // attemptTame ()

    // -- Queries --

    /**
     * Returns true if the item is registered as a taming food.
     *
     * @param item item to check
     * @return true if this item can be used to attempt taming
     */
    public boolean isFood(Item item) {
        return foods.containsKey(item);
    } // isFood ()

    /**
     * Returns true if the item stack's item is registered as a taming food.
     *
     * @param stack item stack to check
     * @return true if this stack's item can be used to attempt taming
     */
    public boolean isFood(ItemStack stack) {
        return stack != null && isFood(stack.getItem());
    } // isFood ()

    /**
     * Returns the effective taming chance for the given item.
     * Uses the per-item chance if configured, otherwise falls back to the global chance.
     * Returns {@link #VANILLA_CHANCE} if neither is configured.
     *
     * @param item item to resolve chance for
     * @return effective taming chance (0.0–1.0)
     */
    public float getChanceFor(Item item) {
        if (!foods.containsKey(item)) return 0f;
        float itemChance = foods.get(item);
        return (itemChance == USE_GLOBAL) ? resolvedGlobalChance() : itemChance;
    } // getChanceFor ()

    /**
     * Creates an {@link Ingredient} matching all registered food items.
     * Used for recipe integration and AI goal targeting.
     *
     * @return ingredient matching all foods, or empty if no foods registered
     */
    public Ingredient getFoodIngredient() {
        if (foods.isEmpty()) return Ingredient.EMPTY;
        return Ingredient.of(foods.keySet().toArray(new Item[0]));
    } // getFoodIngredient ()

    /**
     * Creates an {@link Ingredient} matching all registered tempting items.
     * Used by {@code TemptGoal}.
     *
     * @return ingredient matching all tempting items, or empty if none registered
     */
    public Ingredient getTemptingIngredient() {
        if (temptingItems.isEmpty()) return Ingredient.EMPTY;
        return Ingredient.of(temptingItems.toArray(new Item[0]));
    } // getTemptingIngredient ()

    /**
     * Returns a defensive copy of all registered food items.
     *
     * @return set of food items
     */
    public Set<Item> getFoods() {
        return new HashSet<>(foods.keySet());
    } // getFoods ()

    /**
     * Returns a defensive copy of all registered tempting items.
     *
     * @return set of tempting items
     */
    public Set<Item> getTemptingItems() {
        return new HashSet<>(temptingItems);
    } // getTemptingItems ()

    // -- Private Helpers --

    /**
     * Resolves the global chance, defaulting to {@link #VANILLA_CHANCE} if not configured.
     */
    private float resolvedGlobalChance() {
        return (globalChance == USE_GLOBAL) ? VANILLA_CHANCE : globalChance;
    } // resolvedGlobalChance ()

    /**
     * Clamps a chance value to [0.0, 1.0].
     */
    private float clampChance(float chance) {
        return Math.max(0f, Math.min(1f, chance));
    } // clampChance ()

    /**
     * Plays feedback particles (and optional sound) based on taming outcome.
     * Falls back to default particles (heart / ash) when no feedback is configured.
     */
    private void playFeedback(net.minecraft.world.entity.TamableAnimal entity, boolean success) {
        TamingFeedback effective = (feedback != null) ? feedback : TamingFeedback.DEFAULT;
        effective.play(entity, success);
    } // playFeedback ()

    // =========================================================================
    // TamingFeedback — inner class
    // =========================================================================

    /**
     * <p>Configures the visual and audio response to a taming attempt.<p>
     * <p>
     * <b>Defaults:</b> heart particles on success, ash particles on failure, no sound.
     * Mirrors vanilla wolf/cat feedback.
     * <p>
     * <b>Sound:</b> Muted by default — set explicitly when a sound is desired.
     * Success and failure sounds are configured independently.
     */
    public static final class TamingFeedback {

        // -- Default instance --

        /**
         * Default feedback: heart on success, ash on failure, no sounds.
         * Used when no explicit feedback is configured on the feature.
         */
        public static final TamingFeedback DEFAULT = builder().build();

        // -- Inner Enum --

        /**
         * Particle types available for taming feedback.
         * Delegates to {@link EntityParticles} for consistent visuals.
         */
        public enum ParticleType {
            /** Heart burst — taming success, bonding. */
            HEART,
            /** Ash cloud — failure, rejection. */
            ASH,
            /** Green sparkles — positive event, level up feel. */
            HAPPY_VILLAGER,
            /** Poof cloud — dramatic success or summoning feel. */
            POOF,
            /** Smoke — blocked or refused. */
            SMOKE,
            /** No particle effect. */
            NONE
        } // Enum: ParticleType

        // -- Fields --

        private final ParticleType successParticle;
        private final ParticleType failParticle;
        @Nullable private final SoundEvent successSound;
        @Nullable private final SoundEvent failSound;
        private final float successVolume;
        private final float successPitch;
        private final float failVolume;
        private final float failPitch;

        // -- Constructor --

        private TamingFeedback(Builder builder) {
            this.successParticle = builder.successParticle;
            this.failParticle    = builder.failParticle;
            this.successSound    = builder.successSound;
            this.failSound       = builder.failSound;
            this.successVolume   = builder.successVolume;
            this.successPitch    = builder.successPitch;
            this.failVolume      = builder.failVolume;
            this.failPitch       = builder.failPitch;
        } // Constructor: TamingFeedback ()

        // -- Execution --

        /**
         * Plays the appropriate feedback for the given outcome.
         * <p>
         * <b>Server-only.</b> Must only be called on the server side.
         *
         * @param entity entity performing the taming attempt
         * @param success true if taming succeeded
         */
        public void play(Entity entity, boolean success) {
            if (entity.level().isClientSide) return;

            // -- Particles --
            ParticleType particle = success ? successParticle : failParticle;
            switch (particle) {
                case HEART         -> EntityParticles.Heart(entity);
                case ASH           -> EntityParticles.Ash(entity);
                case HAPPY_VILLAGER-> EntityParticles.HappyVillager(entity);
                case POOF          -> EntityParticles.Poof(entity);
                case SMOKE         -> EntityParticles.Smoke(entity, 7, 0.2);
                case NONE          -> { /* intentionally silent */ }
            }

            // -- Sound --
            SoundEvent sound  = success ? successSound : failSound;
            float      volume = success ? successVolume : failVolume;
            float      pitch  = success ? successPitch  : failPitch;

            if (sound != null) {
                entity.level().playSound(
                        null,
                        entity.blockPosition(),
                        sound,
                        SoundSource.NEUTRAL,
                        volume,
                        pitch
                );
            }
        } // play ()

        // -- Builder --

        /**
         * Returns a new builder for composing a {@link TamingFeedback}.
         *
         * @return fresh builder instance
         */
        public static Builder builder() {
            return new Builder();
        } // builder ()

        /**
         * <p>Fluent builder for {@link TamingFeedback}.<p>
         * <pre>{@code
         * TamingFeedback.builder()
         *     .onSuccess(TamingFeedback.ParticleType.HEART)
         *     .onFail(TamingFeedback.ParticleType.ASH)
         *     .successSound(SoundEvents.PLAYER_LEVELUP, 0.5f, 1.2f)
         *     .build()
         * }</pre>
         */
        public static final class Builder {

            // -- Variables --

            private ParticleType successParticle = ParticleType.HEART;
            private ParticleType failParticle    = ParticleType.ASH;
            @Nullable private SoundEvent successSound = null;
            @Nullable private SoundEvent failSound    = null;
            private float successVolume = 1.0f;
            private float successPitch  = 1.0f;
            private float failVolume    = 1.0f;
            private float failPitch     = 1.0f;

            // -- Constructor --

            private Builder() {} // Constructor: Builder ()

            // -- Particles --

            /**
             * Sets the particle effect to show on a successful taming attempt.
             * Default: {@link ParticleType#HEART}.
             */
            public Builder onSuccess(ParticleType particle) {
                this.successParticle = particle != null ? particle : ParticleType.NONE;
                return this;
            } // onSuccess ()

            /**
             * Sets the particle effect to show on a failed taming attempt.
             * Default: {@link ParticleType#ASH}.
             */
            public Builder onFail(ParticleType particle) {
                this.failParticle = particle != null ? particle : ParticleType.NONE;
                return this;
            } // onFail ()

            // -- Success Sound --

            /**
             * Sets the sound to play on a successful taming attempt at default
             * volume (1.0) and pitch (1.0). No sound plays if not set.
             */
            public Builder successSound(SoundEvent sound) {
                this.successSound = sound;
                return this;
            } // successSound ()

            /**
             * Sets the sound to play on a successful taming attempt with custom
             * volume and pitch.
             */
            public Builder successSound(SoundEvent sound, float volume, float pitch) {
                this.successSound  = sound;
                this.successVolume = volume;
                this.successPitch  = pitch;
                return this;
            } // successSound ()

            // -- Fail Sound --

            /**
             * Sets the sound to play on a failed taming attempt at default
             * volume (1.0) and pitch (1.0). No sound plays if not set.
             */
            public Builder failSound(SoundEvent sound) {
                this.failSound = sound;
                return this;
            } // failSound ()

            /**
             * Sets the sound to play on a failed taming attempt with custom
             * volume and pitch.
             */
            public Builder failSound(SoundEvent sound, float volume, float pitch) {
                this.failSound   = sound;
                this.failVolume  = volume;
                this.failPitch   = pitch;
                return this;
            } // failSound ()

            /**
             * Builds the immutable {@link TamingFeedback}.
             */
            public TamingFeedback build() {
                return new TamingFeedback(this);
            } // build ()

        } // Class: Builder

    } // Class: TamingFeedback

} // Class: FoodFeature
