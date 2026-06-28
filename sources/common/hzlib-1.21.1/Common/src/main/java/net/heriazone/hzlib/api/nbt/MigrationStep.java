package net.heriazone.hzlib.api.nbt;

import org.jetbrains.annotations.NotNull;

/**
 * <p>One versioned transformation in the {@link MigrationChain}.<p>
 * <p>
 * <b>Sole migration authority:</b> All format transformation logic lives here —
 * {@link DataField} has no migrator field by design. Cross-field migrations (e.g.
 * belly/texture ID disambiguation that reads two fields simultaneously) are a natural
 * fit for a step implementation; they are impossible in a per-field migrator.
 * <p>
 * <b>Input:</b> The full root {@link DataCompound}, not just {@code EntityData}.
 * Steps that need context from the root (e.g. which entity type wrote this save) can
 * read top-level keys before transforming the {@code EntityData} sub-compound.
 * <p>
 * <b>Idempotency contract:</b> Running a step on an already-migrated compound must
 * produce the same output as running it once. Steps should check for the presence
 * of legacy keys before overwriting — never unconditionally clobber data that a
 * previous run already migrated.
 */
public interface MigrationStep {

    /**
     * Returns a stable identifier for this step, used in log messages.
     * Convention: {@code "V{from_version}_{source}"}, e.g. {@code "V0_Fabric"}.
     */
    @NotNull
    String id();

    /**
     * Transforms the root compound in place from the legacy format this step
     * expects to the format it produces.
     * <p>
     * Receives and returns the full root compound. Implementations may freely
     * read and write any key at any nesting level. The return value replaces the
     * root compound for the next step in the chain.
     *
     * @param root the full root entity NBT compound (not just {@code EntityData})
     * @return the transformed root compound (may be the same instance)
     */
    @NotNull
    DataCompound migrate(@NotNull DataCompound root);

} // Interface: MigrationStep
