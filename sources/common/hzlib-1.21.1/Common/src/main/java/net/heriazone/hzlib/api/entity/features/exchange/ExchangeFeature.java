package net.heriazone.hzlib.api.entity.features.exchange;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * <p>Enables an entity to participate in item exchanges: the player provides one
 * or more items in sequence, the entity produces one or more items in return.<p>
 * <p>
 * <b>Architecture:</b> The feature declaration is stateless and shared across all
 * entity instances of the same type. Per-instance runtime state — the per-player
 * sequence buffer, per-rule cooldowns, and buffer expiry timestamps — is managed
 * separately by {@link ExchangeState} and stored on the entity via NBT.
 * <p>
 * <b>Rule evaluation order:</b> Rules are evaluated in declaration order. The first
 * rule whose input sequence is complete AND whose condition passes fires. This means
 * more specific rules (with conditions) must be declared before general fallback rules.
 * <p>
 * <b>Global vs. local feedback:</b> {@code globalFeedback} is played for any rule
 * that does not declare its own {@link ExchangeFeedback}. A rule-level feedback fully
 * overrides the global one for that rule.
 * <p>
 * <b>Owner-only shorthand:</b> Calling {@link Builder#ownerOnly()} on the feature
 * injects an {@link ExchangeConditions#ownerOnly()} condition that is AND-ed onto
 * every rule in the feature that does not already have a condition. For rules that
 * already declare a condition, the owner check is AND-ed onto that condition.
 * <p>
 * <b>Item delivery:</b> Outputs are given to the player's inventory. If the
 * inventory is full OR the player is in creative mode, the item is dropped at the
 * entity's feet. Creative mode always drops to avoid the vanilla bug where
 * {@code Inventory.add()} silently destroys items in creative.
 * <p>
 * <b>Usage example (Nether mushroom gal):</b>
 * <pre>{@code
 * ExchangeFeature.builder()
 *     .ownerOnly()
 *     .globalFeedback(ExchangeFeedback.builder()
 *         .sound(SoundEvents.BUCKET_FILL)
 *         .particle(ExchangeFeedback.ParticleType.HAPPY_VILLAGER)
 *         .build())
 *     // Flower biome → suspicious stew
 *     .rule(ExchangeRule.builder()
 *         .inputs(Items.BOWL)
 *         .condition(ExchangeConditions.inBiome(Biomes.FLOWER_FOREST))
 *         .output(() -> buildSuspiciousStew())
 *         .cooldown(6000)
 *         .build())
 *     // Anywhere → nether stew (fallback)
 *     .rule(ExchangeRule.builder()
 *         .inputs(Items.BOWL)
 *         .output(() -> new ItemStack(MonstersItems.NETHER_STEW))
 *         .cooldown(6000)
 *         .build())
 *     .build()
 * }</pre>
 */
public final class ExchangeFeature {

    // -- Fields --

    private final List<ExchangeRule>        rules;
    @Nullable private final ExchangeFeedback globalFeedback;
    private final boolean                   ownerOnly;

    // -- Constructor --

    private ExchangeFeature(Builder builder) {
        this.globalFeedback = builder.globalFeedback;
        this.ownerOnly      = builder.ownerOnly;

        // Inject the owner-only condition on every rule if ownerOnly is set
        List<ExchangeRule> compiled = new ArrayList<>(builder.rules.size());
        if (ownerOnly) {
            ExchangeCondition ownerCheck = ExchangeConditions.ownerOnly();
            for (ExchangeRule rule : builder.rules) {
                ExchangeCondition existing = rule.getCondition();
                // and() returns EntityCondition<C> — wrap in an ExchangeCondition lambda
                // rather than casting, since the lambda was not created as ExchangeCondition
                // and the cast fails at runtime.
                ExchangeCondition combined = existing == null
                        ? ownerCheck
                        : ctx -> ownerCheck.test(ctx) && existing.test(ctx);
                // Rebuild the rule with the injected condition via a proxy wrapper
                compiled.add(new ConditionOverrideRule(rule, combined));
            }
        } else {
            compiled.addAll(builder.rules);
        }
        this.rules = Collections.unmodifiableList(compiled);
    } // Constructor: ExchangeFeature ()

    // -- Accessors --

    /**
     * Returns all declared exchange rules in evaluation order.
     *
     * @return immutable rule list
     */
    public List<ExchangeRule> getRules() { return rules; } // getRules ()

    /**
     * Returns the global feedback played for rules without their own feedback,
     * or null if no global feedback is configured.
     *
     * @return global feedback, or null
     */
    @Nullable
    public ExchangeFeedback getGlobalFeedback() { return globalFeedback; } // getGlobalFeedback ()

    /**
     * Returns whether all rules in this feature require owner interaction.
     *
     * @return true if owner-only was set
     */
    public boolean isOwnerOnly() { return ownerOnly; } // isOwnerOnly ()

    // -- Interaction Entry Point --

    /**
     * Processes one player input item against this feature's rules.
     * <p>
     * <b>Flow:</b>
     * <ol>
     *   <li>Update the player's sequence buffer with the presented item.</li>
     *   <li>If no rule can continue from the current buffer, reset it.</li>
     *   <li>Check each rule in order against the current buffer and context.</li>
     *   <li>If a match is found and the rule is not on cooldown, fire the exchange.</li>
     *   <li>Return {@code SUCCESS} if an exchange fired, {@code PASS} otherwise.</li>
     * </ol>
     * <p>
     * <b>Server-only:</b> Must only be called on the server side.
     *
     * @param entity entity performing the exchange
     * @param player interacting player
     * @param stack  item the player is holding (trigger item)
     * @param state  per-entity exchange runtime state
     * @return {@code SUCCESS} if an exchange was performed, {@code PASS} otherwise
     */
    public InteractionResult tryExchange(TamableAnimal entity, Player player,
                                         ItemStack stack, ExchangeState state) {
        if (entity.level().isClientSide) return InteractionResult.PASS;
        if (rules.isEmpty())             return InteractionResult.PASS;

        UUID playerUUID = player.getUUID();
        Item presentedItem = stack.getItem();
        ExchangeContext context = new ExchangeContext(entity, player);
        long currentTick = entity.level().getGameTime();

        // -- Step 1: Expire stale buffers (no input for more than 5 seconds) --
        state.expireBuffer(playerUUID, currentTick, 100L);

        // -- Step 2: Advance the sequence buffer --
        List<Item> buffer = state.getBuffer(playerUUID);
        buffer.add(presentedItem);

        // -- Step 3: Is the current buffer still viable for any rule? --
        boolean anyRuleContinues = rules.stream().anyMatch(r ->
                bufferMatchesPrefix(r.getInputs(), buffer));
        if (!anyRuleContinues) {
            // This item cannot continue any rule — try a fresh single-item buffer
            buffer.clear();
            buffer.add(presentedItem);
            boolean freshViable = rules.stream().anyMatch(r ->
                    bufferMatchesPrefix(r.getInputs(), buffer));
            if (!freshViable) {
                // Not even a valid first item for any rule — don't consume it
                buffer.clear();
                state.clearBuffer(playerUUID);
                return InteractionResult.PASS;
            }
        }
        state.updateBufferTimestamp(playerUUID, currentTick);

        // -- Step 4: Check each rule for a complete match --
        for (int ruleIndex = 0; ruleIndex < rules.size(); ruleIndex++) {
            ExchangeRule rule = rules.get(ruleIndex);
            if (!rule.matches(buffer, context)) continue;

            // Check cooldown
            if (state.isOnCooldown(ruleIndex, currentTick)) continue;

            // -- Fire the exchange --

            // 1. Consume the input items from the player
            consumeInputSequence(player, rule, stack);

            // 2. Produce outputs
            List<ItemStack> outputs = rule.generateOutputs();
            for (ItemStack output : outputs) {
                deliverToPlayer(entity, player, output);
            }

            // 3. Set cooldown
            if (rule.getCooldownTicks() > 0) {
                state.setCooldown(ruleIndex, currentTick + rule.getCooldownTicks());
            }

            // 4. Clear the buffer
            state.clearBuffer(playerUUID);

            // 5. Play feedback
            ExchangeFeedback effectiveFeedback = rule.getFeedback() != null
                    ? rule.getFeedback()
                    : globalFeedback;
            if (effectiveFeedback != null) effectiveFeedback.play(entity);

            return InteractionResult.SUCCESS;
        }

        // Determine why step 4 fell through:
        // - If any rule has a *complete* input match but was on cooldown → silent block, clear buffer.
        // - If all rules only have a *partial* input match → this item is accepted as part of a
        //   multi-item sequence; consume it so the player knows it was received.
        boolean anyCompleteMatchOnCooldown = false;
        for (int ruleIndex = 0; ruleIndex < rules.size(); ruleIndex++) {
            ExchangeRule rule = rules.get(ruleIndex);
            // Input-only match check (ignores condition and cooldown)
            List<Item> inputs = rule.getInputs();
            if (buffer.size() == inputs.size()) {
                boolean inputsMatch = true;
                for (int i = 0; i < inputs.size(); i++) {
                    if (!inputs.get(i).equals(buffer.get(i))) { inputsMatch = false; break; }
                }
                if (inputsMatch && state.isOnCooldown(ruleIndex, currentTick)) {
                    anyCompleteMatchOnCooldown = true;
                    break;
                }
            }
        }

        if (anyCompleteMatchOnCooldown) {
            // Exchange is ready but on cooldown — don't consume the item, clear buffer silently.
            state.clearBuffer(playerUUID);
            return InteractionResult.PASS;
        }

        // Partial sequence in progress — consume the item so the player gets confirmation
        // that it was accepted as part of a multi-item exchange.
        if (!player.getAbilities().instabuild) stack.shrink(1);
        return InteractionResult.CONSUME;
    } // tryExchange ()

    // -- Private Helpers --

    /**
     * Checks if the current buffer is a valid prefix of the given input sequence.
     *
     * @param inputs full rule input sequence
     * @param buffer current player buffer
     * @return true if buffer matches the beginning of the sequence
     */
    private boolean bufferMatchesPrefix(List<Item> inputs, List<Item> buffer) {
        if (buffer.size() > inputs.size()) return false;
        for (int i = 0; i < buffer.size(); i++) {
            if (!inputs.get(i).equals(buffer.get(i))) return false;
        }
        return true;
    } // bufferMatchesPrefix ()

    /**
     * Consumes the entire input sequence from the player's inventory.
     * <p>
     * For the last item in the sequence, shrinks the held stack directly.
     * For previous items (already given in prior interactions), removes them
     * from the player's inventory by searching for a matching stack.
     * <p>
     * <b>Creative mode:</b> Does not consume items if the player is in creative mode.
     *
     * @param player interacting player
     * @param rule   matched rule providing the input list
     * @param heldStack the item currently held (the final input in the sequence)
     */
    private void consumeInputSequence(Player player, ExchangeRule rule, ItemStack heldStack) {
        if (player.getAbilities().instabuild) return;

        // The held stack is the last item given — shrink it
        heldStack.shrink(1);

        // Previous items in the sequence were already consumed in prior interactions
        // (each partial step consumed 1 item). Nothing more to do for single-item rules.
        // For multi-item sequences, prior items were consumed when accepted into the buffer.
    } // consumeInputSequence ()

    /**
     * Delivers an output stack to the player.
     * <p>
     * <b>Creative mode:</b> Always drops at entity's feet — creative inventory
     * behaviour is undefined and vanilla can silently destroy items added to it.
     * <p>
     * <b>Full inventory:</b> Drops at entity's feet if the player cannot accept
     * the stack.
     *
     * @param entity entity performing the exchange (used as drop origin)
     * @param player target player
     * @param stack  output stack to deliver
     */
    private void deliverToPlayer(TamableAnimal entity, Player player, ItemStack stack) {
        if (stack.isEmpty()) return;

        boolean forceDropp = player.getAbilities().instabuild
                || !player.getInventory().add(stack);

        if (forceDropp) {
            ItemEntity dropped = new ItemEntity(
                    entity.level(),
                    entity.getX(), entity.getY() + 0.5, entity.getZ(),
                    stack.copy()
            );
            dropped.setDefaultPickUpDelay();
            dropped.setThrower(entity);
            entity.level().addFreshEntity(dropped);
        }
    } // deliverToPlayer ()

    // -- Builder --

    /**
     * Returns a new builder for composing an {@link ExchangeFeature}.
     *
     * @return fresh builder instance
     */
    public static Builder builder() {
        return new Builder();
    } // builder ()

    /**
     * <p>Fluent builder for {@link ExchangeFeature}.<p>
     */
    public static final class Builder {

        // -- Variables --

        private final List<ExchangeRule>    rules          = new ArrayList<>();
        @Nullable private ExchangeFeedback  globalFeedback = null;
        private boolean                     ownerOnly      = false;

        // -- Constructor --

        private Builder() {} // Constructor: Builder ()

        // -- Methods --

        /**
         * Marks all rules in this feature as owner-only.
         * <p>
         * Injects {@link ExchangeConditions#ownerOnly()} into every rule.
         * Rules with existing conditions get the owner check AND-ed in.
         *
         * @return this builder for chaining
         */
        public Builder ownerOnly() {
            this.ownerOnly = true;
            return this;
        } // ownerOnly ()

        /**
         * Sets the default feedback played for rules that do not specify their own.
         *
         * @param feedback global feedback configuration
         * @return this builder for chaining
         */
        public Builder globalFeedback(ExchangeFeedback feedback) {
            this.globalFeedback = feedback;
            return this;
        } // globalFeedback ()

        /**
         * Adds an exchange rule.
         * <p>
         * Rules are evaluated in declaration order — add more specific (conditioned)
         * rules before general fallback rules.
         *
         * @param rule rule to add
         * @return this builder for chaining
         */
        public Builder rule(ExchangeRule rule) {
            if (rule != null) rules.add(rule);
            return this;
        } // rule ()

        /**
         * Builds the immutable {@link ExchangeFeature}.
         *
         * @return configured feature instance
         */
        public ExchangeFeature build() {
            return new ExchangeFeature(this);
        } // build ()

    } // Class: Builder

    // =========================================================================
    // ConditionOverrideRule — private wrapper
    // =========================================================================

    /**
     * <p>Wraps an {@link ExchangeRule} with a replacement condition.<p>
     * <p>
     * <b>Design:</b> Used internally to inject the owner-only condition without
     * rebuilding rule instances from scratch. Delegates all methods to the wrapped
     * rule except {@link #getCondition()} and {@link #matches(List, ExchangeContext)}.
     */
    private static final class ConditionOverrideRule extends ExchangeRule {

        // -- Fields --

        private final ExchangeRule        delegate;
        private final ExchangeCondition   injectedCondition;

        // -- Constructor --

        ConditionOverrideRule(ExchangeRule delegate, ExchangeCondition injectedCondition) {
            // Call super with a minimal builder that would normally throw — we override everything
            super(buildMirror(delegate));
            this.delegate          = delegate;
            this.injectedCondition = injectedCondition;
        } // Constructor: ConditionOverrideRule ()

        private static Builder buildMirror(ExchangeRule source) {
            Builder b = new Builder();
            source.getInputs().forEach(b::input);
            source.getOutputs().forEach(b::output);
            b.cooldown(source.getCooldownTicks());
            if (source.getFeedback() != null) b.feedback(source.getFeedback());
            // Intentionally no condition here — ConditionOverrideRule provides its own
            return b;
        } // buildMirror ()

        @Override
        public ExchangeCondition getCondition() { return injectedCondition; }

        @Override
        public boolean matches(List<Item> buffer, ExchangeContext context) {
            if (buffer.size() != delegate.getInputs().size()) return false;
            for (int i = 0; i < delegate.getInputs().size(); i++) {
                if (!delegate.getInput(i).equals(buffer.get(i))) return false;
            }
            return injectedCondition.test(context);
        } // matches ()

        @Override
        public boolean continuesSequence(List<Item> buffer, Item item) {
            return delegate.continuesSequence(buffer, item);
        } // continuesSequence ()

        @Override
        public List<ItemStack> generateOutputs() { return delegate.generateOutputs(); }

        @Override
        public ExchangeFeedback getFeedback() { return delegate.getFeedback(); }

        @Override
        public int getCooldownTicks() { return delegate.getCooldownTicks(); }

    } // Class: ConditionOverrideRule

} // Class: ExchangeFeature
