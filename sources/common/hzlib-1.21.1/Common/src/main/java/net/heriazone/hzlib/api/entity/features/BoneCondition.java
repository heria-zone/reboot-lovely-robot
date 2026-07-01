package net.heriazone.hzlib.api.entity.features;

import net.heriazone.hzlib.api.entity.NativeEntity;

/**
 * Per-frame predicate for {@link BoneRule} evaluation inside {@link BoneVisibilityFeature}.
 * <p>
 * <b>Architecture:</b> Evaluated client-side inside
 * {@code NativeModel.setCustomAnimations()} on every render frame. Conditions
 * must be pure and allocation-free — no world queries, no new object creation.
 * The entity reference gives read-only access to synced data (level, state,
 * texture variant key) which is sufficient for all known use cases.
 * <p>
 * <b>Design Decision:</b> Takes {@link NativeEntity} directly rather than a
 * dedicated context wrapper. Bone visibility depends only on entity state already
 * synced to the client — wrapping that in a context object adds indirection with
 * no benefit. Contrast with {@link AppearanceCondition}, which needs a richer
 * context (biome, spawn reason, held item) that is not available on the entity.
 * <p>
 * <b>Loader isolation:</b> Declared in HZLib Common — no GeckoLib dependency.
 * The only GeckoLib call site is {@code NativeModel.applyBoneVisibility()} in
 * the loader module, which receives the evaluated {@code boolean} result and
 * calls {@code GeoBone.setHidden()} accordingly.
 */
@FunctionalInterface
public interface BoneCondition {

    /**
     * Evaluates this condition against the given entity on the current render frame.
     * <p>
     * <i>Note:</i> Must be fast — called every frame for every {@link BoneRule} on
     * every visible entity. Avoid allocations, world access, and blocking operations.
     *
     * @param entity the entity being rendered; provides read access to synced state
     * @return {@code true} if this condition is satisfied
     */
    boolean test(NativeEntity entity);

} // Interface: BoneCondition
