package net.heriazone.hzlib.api.entity.features;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Declares conditional per-frame bone visibility for entities that have geometry
 * which must be shown or hidden based on runtime entity state.
 * <p>
 * <b>Architecture:</b> Attached to a {@link net.heriazone.hzlib.api.entity.NativeEntityFamily}
 * via {@code withFeature(BoneVisibilityFeature.class, ...)}. Each render frame,
 * {@code NativeModel.setCustomAnimations()} retrieves this feature and calls
 * {@code applyBoneVisibility()} — which iterates the rule list and calls
 * {@code GeoBone.setHidden()} accordingly. No reflection, no GeckoLib imports in
 * Common; the only GeckoLib call site lives in the loader's {@code NativeModel}.
 * <p>
 * <b>Use cases covered by one API:</b>
 * <ol>
 *   <li><b>Level-progressive unlock</b> — Kitsune tails visible only above a level
 *       threshold; each tail bone declared with {@code showWhen}</li>
 *   <li><b>Appearance-variant conflict</b> — Sentry wings: {@code UWings} hidden in
 *       dragon form, {@code UWingsno} hidden in honey form; declared with
 *       {@code hideWhen} keyed on the active texture variant</li>
 *   <li><b>Any future conditional bone</b> — state-dependent geometry without
 *       a model-file change or a model-class subclass</li>
 * </ol>
 * <p>
 * <b>Evaluation order:</b> Rules are evaluated in declaration order. All rules are
 * applied — unlike appearance resolution there is no early-exit, because multiple
 * bones may need to change state in the same frame.
 * <p>
 * <b>Usage — Kitsune tail progression:</b>
 * <pre>{@code
 * BoneVisibilityFeature.builder()
 *     .showWhen("tail0",  e -> e instanceof RobotEntity r && r.getCurrentLevel() < threshold(r))
 *     .hideWhen("tail0",  e -> e instanceof RobotEntity r && r.getCurrentLevel() >= threshold(r))
 *     .showWhen("tail01", e -> e instanceof RobotEntity r && r.getCurrentLevel() >= threshold(r))
 *     .build()
 * }</pre>
 * <p>
 * <b>Usage — Sentry wing conflict:</b>
 * <pre>{@code
 * BoneVisibilityFeature.builder()
 *     .hideWhen("UWings",   BoneVisibilityConditions.textureVariantIs("sentry_dragon"))
 *     .hideWhen("UWingsno", BoneVisibilityConditions.textureVariantIs("sentry_honey"))
 *     .build()
 * }</pre>
 */
public final class BoneVisibilityFeature implements NativeFeature {

    // -- Fields --

    private final List<BoneRule> rules;

    // -- Constructor --

    private BoneVisibilityFeature(List<BoneRule> rules) {
        this.rules = Collections.unmodifiableList(rules);
    } // Constructor: BoneVisibilityFeature ()

    // -- Accessors --

    /**
     * Returns the ordered list of bone visibility rules declared on this feature.
     * <p>
     * The list is unmodifiable. Iterate it in {@code NativeModel.applyBoneVisibility()}
     * to evaluate each rule and call {@code GeoBone.setHidden()} in the loader.
     *
     * @return immutable ordered rule list; never {@code null}, never empty after
     *         construction via {@link Builder#build()}
     */
    public List<BoneRule> getRules() {
        return rules;
    } // getRules ()

    // -- Builder --

    /**
     * Creates a new builder for assembling a {@link BoneVisibilityFeature}.
     *
     * @return fresh builder instance
     */
    public static Builder builder() {
        return new Builder();
    } // builder ()

    /**
     * Fluent builder for {@link BoneVisibilityFeature}.
     * <p>
     * Accumulates {@link BoneRule} instances in declaration order. Call
     * {@link #hideWhen} or {@link #showWhen} for each bone that needs
     * conditional visibility, then call {@link #build()}.
     */
    public static final class Builder {

        // -- Fields --

        private final List<BoneRule> rules = new ArrayList<>();

        // -- Constructor --

        private Builder() {} // Constructor: Builder ()

        // -- Rule Declarations --

        /**
         * Adds a rule that hides the named bone (and all its children) when the
         * condition evaluates to {@code true}.
         * <p>
         * This is the common case: the bone is visible by default in the model
         * and should be suppressed when a runtime condition is met.
         *
         * @param boneName  exact bone name as declared in the {@code .geo.json} file;
         *                  silently skipped at evaluation time if the bone is absent
         * @param condition evaluated client-side each render frame; must be
         *                  allocation-free and avoid world access
         * @return this builder for chaining
         */
        public Builder hideWhen(String boneName, BoneCondition condition) {
            Objects.requireNonNull(boneName,  "boneName must not be null");
            Objects.requireNonNull(condition, "condition must not be null");
            rules.add(new BoneRule(boneName, condition, true));
            return this;
        } // hideWhen ()

        /**
         * Adds a rule that shows (un-hides) the named bone when the condition
         * evaluates to {@code true}.
         * <p>
         * Use this when the model defaults the bone to hidden and a condition
         * should reveal it — for example, tail bones that unlock at higher levels.
         *
         * @param boneName  exact bone name as declared in the {@code .geo.json} file;
         *                  silently skipped at evaluation time if the bone is absent
         * @param condition evaluated client-side each render frame; must be
         *                  allocation-free and avoid world access
         * @return this builder for chaining
         */
        public Builder showWhen(String boneName, BoneCondition condition) {
            Objects.requireNonNull(boneName,  "boneName must not be null");
            Objects.requireNonNull(condition, "condition must not be null");
            rules.add(new BoneRule(boneName, condition, false));
            return this;
        } // showWhen ()

        /**
         * Constructs the immutable {@link BoneVisibilityFeature} from the accumulated rules.
         *
         * @return new feature instance; rule list is frozen on creation
         * @throws IllegalStateException if no rules have been declared
         */
        public BoneVisibilityFeature build() {
            if (rules.isEmpty()) {
                throw new IllegalStateException(
                        "BoneVisibilityFeature must declare at least one rule before building");
            }
            return new BoneVisibilityFeature(new ArrayList<>(rules));
        } // build ()

    } // Class: Builder

} // Class: BoneVisibilityFeature
