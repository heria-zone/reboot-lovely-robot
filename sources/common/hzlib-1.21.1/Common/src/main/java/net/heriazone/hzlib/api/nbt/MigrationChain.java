package net.heriazone.hzlib.api.nbt;

import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <p>Ordered pipeline of {@link MigrationStep} implementations that upgrades a
 * legacy root compound to the current format before schema read.<p>
 * <p>
 * <b>Architecture:</b> Sole migration authority per ADR 019. A family registers one
 * chain on its descriptor; the entity's read path runs it whenever the format
 * detector identifies a legacy save. Steps execute in declaration order — each step
 * receives the output of the previous.
 * <p>
 * <b>Format detection (Stage 1):</b> The chain inspects the root compound and skips
 * migration when the current format is already present. Four cases are distinguished:
 * <ol>
 *   <li>Current — {@code EntityData.SchemaVersion} + {@code EntityData.McVersion} both present</li>
 *   <li>Pre-McVersion — {@code EntityData.SchemaVersion} present, {@code McVersion} absent</li>
 *   <li>Gen3 partial — {@code EntityData} compound present but no {@code SchemaVersion}</li>
 *   <li>Legacy flat — no {@code EntityData} compound; Gen1/Gen2 PascalCase keys at root</li>
 * </ol>
 * Cases 2–4 all trigger the step pipeline. Case 1 skips it.
 */
public final class MigrationChain {

    // -- Fields --

    private final List<MigrationStep> steps;

    // -- Constructor --

    private MigrationChain(@NotNull List<MigrationStep> steps) {
        this.steps = Collections.unmodifiableList(new ArrayList<>(steps));
    } // Constructor: MigrationChain ()

    // -- Migration --

    /**
     * Detects the format of {@code root} and, if migration is needed, runs all registered
     * steps in order. Returns the root compound after migration (may be the same instance).
     * <p>
     * When no steps are registered and the format is legacy, a warning is logged and
     * {@code root} is returned as-is — Phase 2 migration steps handle the actual transforms.
     *
     * @param root the full root entity NBT compound (wrapped by {@link NbtAdapterFactory})
     * @return migrated root compound ready for schema read
     */
    @NotNull
    public DataCompound migrate(@NotNull DataCompound root) {
        Objects.requireNonNull(root, "Root compound cannot be null");

        if (isCurrentFormat(root)) return root; // Stage 1: already current — skip entirely

        if (steps.isEmpty()) {
            LoggerFactory.getLogger("HZLib-MigrationChain").warn(
                "[HZLib] Legacy save detected but no MigrationSteps are registered. " +
                "Register migration steps via MigrationChain.Builder to upgrade old saves.");
            return root;
        }

        DataCompound current = root;
        for (MigrationStep step : steps) {
            try {
                current = step.migrate(current);
            } catch (Exception e) {
                LoggerFactory.getLogger("HZLib-MigrationChain").error(
                    "[HZLib] MigrationStep '{}' threw an exception — skipping remaining steps. " +
                    "Entity may load with partial data.", step.id(), e);
                break;
            }
        }
        return current;
    } // migrate ()

    // -- Format Detection --

    /**
     * Returns {@code true} when the root compound represents the current format —
     * both {@code EntityData.SchemaVersion} and {@code EntityData.McVersion} are present.
     * When {@code true}, {@link #migrate} returns immediately without running any step.
     */
    public static boolean isCurrentFormat(@NotNull DataCompound root) {
        if (!root.hasCompound("EntityData")) return false;
        DataCompound ed = root.getCompound("EntityData");
        return EntityDataSchema.isCurrentFormat(ed);
    } // isCurrentFormat ()

    /**
     * Returns {@code true} when the root compound uses the pre-McVersion structured format —
     * {@code EntityData} compound present with {@code SchemaVersion} but no {@code McVersion}.
     */
    public static boolean isPreMcVersionFormat(@NotNull DataCompound root) {
        if (!root.hasCompound("EntityData")) return false;
        DataCompound ed = root.getCompound("EntityData");
        return EntityDataSchema.isPreMcVersionFormat(ed);
    } // isPreMcVersionFormat ()

    /**
     * Returns {@code true} when the root compound has an {@code EntityData} compound
     * but no {@code SchemaVersion} inside it — Gen3 partial format.
     */
    public static boolean isGen3PartialFormat(@NotNull DataCompound root) {
        return root.hasCompound("EntityData")
                && !root.getCompound("EntityData").has(EntityDataSchema.KEY_SCHEMA_VERSION);
    } // isGen3PartialFormat ()

    /**
     * Returns {@code true} when the root compound has no {@code EntityData} compound
     * at all — flat Gen1/Gen2 layout with PascalCase or snake_case keys at root level.
     */
    public static boolean isLegacyFlatFormat(@NotNull DataCompound root) {
        return !root.hasCompound("EntityData");
    } // isLegacyFlatFormat ()

    // -- Builder --

    /** Returns a new builder for composing a migration chain. */
    @NotNull
    public static Builder builder() { return new Builder(); } // builder ()

    /**
     * Returns an empty chain — use when a family has no legacy saves to migrate.
     * The chain will skip all migration and proceed directly to schema read.
     */
    @NotNull
    public static MigrationChain empty() {
        return new MigrationChain(List.of());
    } // empty ()

    /**
     * Fluent builder for {@link MigrationChain}.
     * Add steps in chronological order (oldest format first).
     */
    public static final class Builder {

        private final List<MigrationStep> steps = new ArrayList<>();

        private Builder() {}

        /**
         * Appends a migration step. Steps execute in the order they are added —
         * declare oldest-format steps first.
         *
         * @param step the migration step to append
         * @return this builder
         */
        public Builder addStep(@NotNull MigrationStep step) {
            Objects.requireNonNull(step, "MigrationStep cannot be null");
            steps.add(step);
            return this;
        } // addStep ()

        /** Builds the immutable {@link MigrationChain}. */
        @NotNull
        public MigrationChain build() {
            return new MigrationChain(steps);
        } // build ()

    } // Class: Builder

} // Class: MigrationChain
