package net.heriazone.hzlib.api.entity.features.exchange;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

/**
 * <p>Declares a single exchange: what the player provides, under what conditions,
 * and what the entity produces in return.<p>
 * <p>
 * <b>Architecture:</b> Stateless declaration. Runtime state (cooldowns, sequence
 * buffers) lives on the entity instance, not here. This allows one {@link ExchangeRule}
 * instance to be shared across all entities of the same type without contention.
 * <p>
 * <b>Input matching:</b> Inputs are a sequence of items. For single-item exchanges
 * the sequence has length 1. For multi-item sequences the player must present the
 * items in the declared order, one per interaction. The entity maintains a per-player
 * buffer that accumulates partial matches until the sequence completes or is broken.
 * <p>
 * <b>Outputs:</b> Each output is a {@link Supplier}{@code <ItemStack>} evaluated at
 * give-time, not registration time. This allows outputs with NBT data, randomized
 * contents, or quantities that depend on world state at the moment of exchange.
 * <p>
 * <b>Condition evaluation:</b> The optional condition is checked after the full input
 * sequence is matched but before outputs are produced. If the condition fails the rule
 * does not fire — evaluation continues to the next rule in the feature.
 * <p>
 * <b>Cooldown:</b> Per-rule cooldown in ticks. Shared globally across all players for
 * the same entity instance (prevents exploiting multiple players to bypass cooldown).
 * A value of 0 means no cooldown.
 */
public class ExchangeRule {

    // -- Fields --

    private final List<Item>                  inputs;
    @Nullable private final ExchangeCondition condition;
    private final List<Supplier<ItemStack>>   outputs;
    private final int                         cooldownTicks;
    @Nullable private final ExchangeFeedback  feedback;

    // -- Constructor --

    ExchangeRule(Builder builder) {
        this.inputs        = Collections.unmodifiableList(new ArrayList<>(builder.inputs));
        this.condition     = builder.condition;
        this.outputs       = Collections.unmodifiableList(new ArrayList<>(builder.outputs));
        this.cooldownTicks = Math.max(0, builder.cooldownTicks);
        this.feedback      = builder.feedback;

        if (this.inputs.isEmpty())  throw new IllegalStateException("ExchangeRule must have at least one input item");
        if (this.outputs.isEmpty()) throw new IllegalStateException("ExchangeRule must have at least one output");
    } // Constructor: ExchangeRule ()

    // -- Accessors --

    /**
     * Returns the ordered list of items the player must provide to trigger this rule.
     * <p>
     * Single-item exchanges have a list of size 1. Multi-item sequences have size > 1
     * and must be given by the player in the declared order.
     *
     * @return immutable ordered input list
     */
    public List<Item> getInputs() { return inputs; } // getInputs ()

    /**
     * Returns the item at the specified position in the input sequence.
     *
     * @param index position (0-based)
     * @return item at that position
     */
    public Item getInput(int index) { return inputs.get(index); } // getInput ()

    /**
     * Returns the number of items in the input sequence.
     *
     * @return sequence length
     */
    public int getInputCount() { return inputs.size(); } // getInputCount ()

    /**
     * Returns the optional condition that must pass before this rule fires.
     *
     * @return condition, or null if always applicable once inputs match
     */
    @Nullable
    public ExchangeCondition getCondition() { return condition; } // getCondition ()

    /**
     * Returns the output suppliers.
     * <p>
     * <b>Evaluation:</b> Each supplier is called once per successful exchange.
     * The resulting stacks are given to the player (or dropped if inventory is full
     * or the player is in creative mode).
     *
     * @return immutable list of output suppliers
     */
    public List<Supplier<ItemStack>> getOutputs() { return outputs; } // getOutputs ()

    /**
     * Returns the cooldown duration in ticks.
     * <p>
     * 0 means no cooldown. The cooldown is per rule per entity instance.
     *
     * @return cooldown in ticks
     */
    public int getCooldownTicks() { return cooldownTicks; } // getCooldownTicks ()

    /**
     * Returns the per-rule feedback configuration, or null if the global feedback
     * from {@link ExchangeFeature} should be used.
     *
     * @return rule-specific feedback, or null
     */
    @Nullable
    public ExchangeFeedback getFeedback() { return feedback; } // getFeedback ()

    /**
     * Evaluates whether this rule matches the player's current input and all
     * configured conditions.
     * <p>
     * <b>Input check:</b> Verifies the given partial buffer matches the full input
     * sequence (buffer size must equal input count, each item must match in order).
     * <p>
     * <b>Condition check:</b> If inputs match and a condition is set, the condition
     * is evaluated. If no condition is set the rule is unconditional.
     *
     * @param buffer  the ordered list of items the player has provided so far
     * @param context interaction context for condition evaluation
     * @return true if this rule fully matches and should fire
     */
    public boolean matches(List<Item> buffer, ExchangeContext context) {
        if (buffer.size() != inputs.size()) return false;
        for (int i = 0; i < inputs.size(); i++) {
            if (!inputs.get(i).equals(buffer.get(i))) return false;
        }
        return condition == null || condition.test(context);
    } // matches ()

    /**
     * Returns true if the given item could be the next expected input for this rule
     * given the current partial buffer.
     * <p>
     * <b>Use:</b> Called by the sequence buffer logic to determine whether to keep
     * the buffer alive or discard it when a non-matching item is presented.
     *
     * @param buffer current partial input buffer
     * @param item   item the player just provided
     * @return true if item continues this rule's sequence
     */
    public boolean continuesSequence(List<Item> buffer, Item item) {
        if (buffer.size() >= inputs.size()) return false;
        return inputs.get(buffer.size()).equals(item);
    } // continuesSequence ()

    /**
     * Generates all output stacks by invoking each output supplier.
     * <p>
     * <b>Call site:</b> Only call after confirming the rule matches.
     *
     * @return list of ItemStacks to give to the player
     */
    public List<ItemStack> generateOutputs() {
        List<ItemStack> result = new ArrayList<>(outputs.size());
        for (Supplier<ItemStack> supplier : outputs) {
            ItemStack stack = supplier.get();
            if (stack != null && !stack.isEmpty()) result.add(stack);
        }
        return result;
    } // generateOutputs ()

    // -- Builder --

    /**
     * Returns a new builder for composing an {@link ExchangeRule}.
     *
     * @return fresh builder instance
     */
    public static Builder builder() {
        return new Builder();
    } // builder ()

    /**
     * <p>Fluent builder for {@link ExchangeRule}.<p>
     * <pre>{@code
     * ExchangeRule.builder()
     *     .inputs(Items.BOWL)
     *     .condition(ExchangeConditions.inBiome(Biomes.FLOWER_FOREST))
     *     .output(() -> buildSuspiciousStew())
     *     .cooldown(6000)
     *     .feedback(ExchangeFeedback.builder()
     *         .sound(SoundEvents.BOTTLE_FILL)
     *         .particle(ExchangeFeedback.ParticleType.HAPPY_VILLAGER)
     *         .build())
     *     .build()
     * }</pre>
     */
    public static final class Builder {

        // -- Variables --

        private final List<Item>                inputs        = new ArrayList<>();
        @Nullable private ExchangeCondition     condition     = null;
        private final List<Supplier<ItemStack>> outputs       = new ArrayList<>();
        private int                             cooldownTicks = 0;
        @Nullable private ExchangeFeedback      feedback      = null;

        // -- Constructor --

        Builder() {} // Constructor: Builder ()

        // -- Input --

        /**
         * Declares the single item the player must provide.
         * <p>
         * For sequences call this multiple times in order, or use {@link #inputs(Item...)}.
         *
         * @param item trigger item
         * @return this builder for chaining
         */
        public Builder input(Item item) {
            if (item != null) inputs.add(item);
            return this;
        } // input ()

        /**
         * Declares the input sequence in order.
         * <p>
         * The player must give these items one at a time in the declared order.
         * Order matters — {@code [BOWL, ORCHID]} is a different rule than {@code [ORCHID, BOWL]}.
         *
         * @param items ordered input items
         * @return this builder for chaining
         */
        public Builder inputs(Item... items) {
            if (items != null) {
                for (Item item : items) { if (item != null) inputs.add(item); }
            }
            return this;
        } // inputs ()

        // -- Condition --

        /**
         * Attaches a condition that must pass after the full input sequence is matched.
         * <p>
         * Multiple calls replace the previous condition — compose using
         * {@link ExchangeCondition#and} and {@link ExchangeCondition#or} instead.
         *
         * @param condition context-based predicate
         * @return this builder for chaining
         */
        public Builder condition(ExchangeCondition condition) {
            this.condition = condition;
            return this;
        } // condition ()

        // -- Outputs --

        /**
         * Adds a single output supplier.
         * <p>
         * Suppliers are evaluated at give-time so NBT, randomization, or world-state
         * dependent logic is fully supported.
         *
         * @param output supplier that produces the output stack
         * @return this builder for chaining
         */
        public Builder output(Supplier<ItemStack> output) {
            if (output != null) outputs.add(output);
            return this;
        } // output ()

        /**
         * Adds multiple output suppliers.
         *
         * @param outputList suppliers to add
         * @return this builder for chaining
         */
        @SafeVarargs
        public final Builder outputs(Supplier<ItemStack>... outputList) {
            if (outputList != null) {
                for (Supplier<ItemStack> o : outputList) { if (o != null) outputs.add(o); }
            }
            return this;
        } // outputs ()

        // -- Cooldown --

        /**
         * Sets the per-rule cooldown in ticks. 0 means no cooldown.
         *
         * @param ticks cooldown duration
         * @return this builder for chaining
         */
        public Builder cooldown(int ticks) {
            this.cooldownTicks = Math.max(0, ticks);
            return this;
        } // cooldown ()

        // -- Feedback --

        /**
         * Attaches rule-specific feedback (sound, particle, animation).
         * <p>
         * When set, overrides the global feedback from {@link ExchangeFeature} for
         * this rule only. When absent, the global feedback is used.
         *
         * @param feedback rule-specific feedback configuration
         * @return this builder for chaining
         */
        public Builder feedback(ExchangeFeedback feedback) {
            this.feedback = feedback;
            return this;
        } // feedback ()

        /**
         * Builds the immutable {@link ExchangeRule}.
         *
         * @return configured rule instance
         * @throws IllegalStateException if inputs or outputs are empty
         */
        public ExchangeRule build() {
            return new ExchangeRule(this);
        } // build ()

    } // Class: Builder

} // Class: ExchangeRule
