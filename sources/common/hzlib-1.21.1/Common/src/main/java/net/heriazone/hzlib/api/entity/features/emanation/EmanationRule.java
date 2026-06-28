package net.heriazone.hzlib.api.entity.features.emanation;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <p>One emanation rule: a trigger moment, an optional condition gate, a list of
 * effects, and an optional cooldown.<p>
 * <p>
 * <b>Firing contract:</b> A rule fires when its trigger matches, its condition passes
 * (or is absent), and its cooldown has expired (or is zero). All declared effects
 * execute together — there is no short-circuiting between effects within one rule.
 * <p>
 * <b>consumesGift:</b> Only meaningful for {@link EmanationTrigger#ON_GIFT} rules.
 * When true, one item is removed from the giver's hand on a successful fire.
 * Creative-mode givers are not charged.
 * <p>
 * <b>cooldown:</b> In ticks. {@code 0} means no cooldown. Currently enforced only
 * for {@link EmanationTrigger#ON_THRESHOLD} rules — ON_ATTACK and ON_HURT fire freely
 * per event since those events are already rate-limited by the combat system.
 */
public final class EmanationRule {

    // -- Fields --

    private final EmanationTrigger trigger;
    @Nullable private final EmanationCondition condition;
    private final List<EmanationEffect> effects;
    private final int cooldownTicks;
    private final boolean consumesGift;

    // -- Constructor --

    private EmanationRule(Builder builder) {
        this.trigger       = Objects.requireNonNull(builder.trigger, "Trigger is required");
        this.condition     = builder.condition;
        this.effects       = Collections.unmodifiableList(new ArrayList<>(builder.effects));
        this.cooldownTicks = builder.cooldownTicks;
        this.consumesGift  = builder.consumesGift;
        if (this.effects.isEmpty())
            throw new IllegalStateException("EmanationRule must have at least one effect");
    } // Constructor: EmanationRule ()

    // -- Accessors --

    public EmanationTrigger getTrigger()      { return trigger;       }
    public int getCooldownTicks()             { return cooldownTicks; }
    public boolean consumesGift()             { return consumesGift;  }
    public List<EmanationEffect> getEffects() { return effects;       }

    // -- Execution --

    /**
     * Attempts to fire this rule in the given context.
     * <p>
     * Checks condition, checks cooldown via {@link EmanationState}, fires all effects,
     * then resets cooldown if applicable. Returns true if the rule fired.
     *
     * @param ctx   emanation context for this moment
     * @param state per-entity mutable state (threshold cooldown tracking)
     * @return true if the rule fired
     */
    public boolean tryFire(EmanationContext ctx, EmanationState state) {
        // Condition guard
        if (condition != null && !condition.test(ctx)) return false;

        // Cooldown guard — only enforced for ON_THRESHOLD
        if (cooldownTicks > 0 && trigger == EmanationTrigger.ON_THRESHOLD) {
            long currentTick = ctx.getLevel().getGameTime();
            if (!state.isThresholdReady(currentTick)) return false;
            // Fire all effects
            for (EmanationEffect effect : effects) effect.apply(ctx);
            state.setThresholdCooldown(currentTick, cooldownTicks);
            consumeGiftIfNeeded(ctx);
            return true;
        }

        // No cooldown — fire immediately
        for (EmanationEffect effect : effects) effect.apply(ctx);
        consumeGiftIfNeeded(ctx);
        return true;
    } // tryFire ()

    private void consumeGiftIfNeeded(EmanationContext ctx) {
        if (consumesGift && ctx.giver != null && ctx.gift != null && !ctx.gift.isEmpty()) {
            if (!ctx.giver.getAbilities().instabuild) ctx.gift.shrink(1);
        }
    } // consumeGiftIfNeeded ()

    // -- Builder --

    public static Builder builder() { return new Builder(); }

    /**
     * Fluent builder for {@link EmanationRule}.
     * <pre>{@code
     * EmanationRule.builder()
     *     .trigger(EmanationTrigger.ON_GIFT)
     *     .condition(EmanationConditions.giftIs(Items.COOKIE))
     *     .effect(EmanationEffects.applyToGiver(MobEffects.REGENERATION, 200, 0))
     *     .consumesGift(true)
     *     .build()
     * }</pre>
     */
    public static final class Builder {

        private EmanationTrigger trigger;
        @Nullable private EmanationCondition condition;
        private final List<EmanationEffect> effects = new ArrayList<>();
        private int cooldownTicks = 0;
        private boolean consumesGift = false;

        private Builder() {}

        public Builder trigger(EmanationTrigger trigger) {
            this.trigger = trigger;
            return this;
        }

        public Builder condition(EmanationCondition condition) {
            this.condition = condition;
            return this;
        }

        /** Adds one effect. Call multiple times for multiple effects on the same rule. */
        public Builder effect(EmanationEffect effect) {
            if (effect != null) effects.add(effect);
            return this;
        }

        /** Cooldown in ticks. Only enforced for ON_THRESHOLD rules. */
        public Builder cooldown(int ticks) {
            this.cooldownTicks = Math.max(0, ticks);
            return this;
        }

        /** Whether the gifted item is consumed on a successful ON_GIFT fire. */
        public Builder consumesGift(boolean consume) {
            this.consumesGift = consume;
            return this;
        }

        public EmanationRule build() { return new EmanationRule(this); }

    } // Class: Builder

} // Class: EmanationRule
