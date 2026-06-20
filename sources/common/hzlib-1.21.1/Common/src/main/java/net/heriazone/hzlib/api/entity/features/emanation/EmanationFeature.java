package net.heriazone.hzlib.api.entity.features.emanation;

import java.util.*;

/**
 * <p>Declares how an entity's inner nature manifests outward at significant moments
 * in her life — being struck, striking a foe, receiving a gift, or nearing death.<p>
 * <p>
 * <b>Concept:</b> Every companion has a nature. That nature radiates outward in
 * response to the moments that matter. A Molten Gal sets things on fire because fire
 * is what she is — not because she has a "fire attack skill." A Mandrake screams
 * when hurt because pain flows outward from her as dread. A Mandrake blesses you
 * when you give her something because happiness is what flows out of her when she
 * feels it. This is not a combat system. It is a presence system.
 * <p>
 * <b>Two kinds of emanation:</b>
 * <ul>
 *   <li><b>Blessing</b> — a benevolent outflow. She was made happy; her happiness
 *       reached you. Declared as {@link EmanationTrigger#ON_GIFT} rules.</li>
 *   <li><b>Curse</b> — a hostile outflow. She was hurt or provoked; her pain or anger
 *       reached everything nearby. Declared as {@link EmanationTrigger#ON_HURT} or
 *       {@link EmanationTrigger#ON_ATTACK} rules.</li>
 * </ul>
 * <p>
 * <b>Architecture:</b> Stateless. Declared once on the entity type via
 * {@code withFeature(EmanationFeature.class, ...)} and shared across all instances.
 * Per-entity mutable state (threshold cooldown) lives in {@link EmanationState}.
 * <p>
 * <b>Rules evaluation:</b> All rules matching the given trigger are returned by
 * {@link #getRulesFor}. Unlike {@code ExchangeFeature}, there is no first-match-wins
 * logic — all matching rules fire. This allows combinations: the Soul Wanderer fires
 * both a general attack rule (wither + regen) and a conditional undead bonus rule
 * on the same attack, both of which apply independently.
 * <p>
 * <b>ON_GIFT exception:</b> Gift rules use first-match-wins (like ExchangeFeature),
 * because a gift has one meaning — the entity responds to one specific blessing for
 * each item given, not all blessings simultaneously.
 */
public final class EmanationFeature {

    // -- Fields --

    private final List<EmanationRule> rules;
    private final Map<EmanationTrigger, List<EmanationRule>> rulesByTrigger;

    // -- Constructor --

    private EmanationFeature(Builder builder) {
        this.rules = Collections.unmodifiableList(new ArrayList<>(builder.rules));

        // Pre-group by trigger for O(1) lookup at runtime
        Map<EmanationTrigger, List<EmanationRule>> grouped = new EnumMap<>(EmanationTrigger.class);
        for (EmanationTrigger t : EmanationTrigger.values()) grouped.put(t, new ArrayList<>());
        for (EmanationRule rule : this.rules) grouped.get(rule.getTrigger()).add(rule);
        for (EmanationTrigger t : EmanationTrigger.values()) {
            grouped.put(t, Collections.unmodifiableList(grouped.get(t)));
        }
        this.rulesByTrigger = Collections.unmodifiableMap(grouped);
    } // Constructor: EmanationFeature ()

    // -- Query --

    /**
     * Returns all rules declared for the given trigger, in declaration order.
     * Never null — returns an empty list if no rules are registered for that trigger.
     *
     * @param trigger the emanation moment to query
     * @return ordered list of rules for that trigger
     */
    public List<EmanationRule> getRulesFor(EmanationTrigger trigger) {
        return rulesByTrigger.getOrDefault(trigger, Collections.emptyList());
    } // getRulesFor ()

    /** Returns all declared rules across all triggers, in declaration order. */
    public List<EmanationRule> getAllRules() { return rules; }

    // -- Builder --

    public static Builder builder() { return new Builder(); }

    /**
     * Fluent builder for {@link EmanationFeature}.
     * <pre>{@code
     * EmanationFeature.builder()
     *     .rule(EmanationRule.builder()
     *         .trigger(EmanationTrigger.ON_ATTACK)
     *         .effect(EmanationEffects.igniteTarget(4))
     *         .build())
     *     .build()
     * }</pre>
     */
    public static final class Builder {

        private final List<EmanationRule> rules = new ArrayList<>();

        private Builder() {}

        /** Adds a rule. Call in declaration order — matters for ON_GIFT first-match evaluation. */
        public Builder rule(EmanationRule rule) {
            if (rule != null) rules.add(rule);
            return this;
        }

        public EmanationFeature build() {
            if (rules.isEmpty())
                throw new IllegalStateException("EmanationFeature must have at least one rule");
            return new EmanationFeature(this);
        }

    } // Class: Builder

} // Class: EmanationFeature
