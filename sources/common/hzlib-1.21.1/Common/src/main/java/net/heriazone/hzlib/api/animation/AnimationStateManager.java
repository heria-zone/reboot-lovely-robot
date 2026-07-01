package net.heriazone.hzlib.api.animation;

import net.heriazone.hzlib.api.entity.NativeEntity;
import net.heriazone.hzlib.framework.entity.enums.EntityState;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Manages animation state transitions and condition checking for entities.
 * <p>
 * <b>Architecture:</b> Centralizes animation decision logic that was previously
 * scattered across loader-specific animation controllers. Provides consistent
 * animation behavior across all loaders while keeping GeckoLib integration
 * in loader modules.
 * <p>
 * <b>Profile-aware:</b> When the entity's current animator variant carries an
 * {@link AnimationProfile}, the profile's pools are used to select animation names.
 * When no profile is present, falls back to the standard animation name constants
 * defined in this class for backward compatibility.
 * <p>
 * <b>Design Decision:</b> Static methods allow easy reuse without inheritance
 * complexity. Animation state logic is pure business logic that doesn't depend
 * on GeckoLib types or rendering context.
 */
public class AnimationStateManager {

    // -- Standard Animation Name Constants (fallback when no profile is present) --

    public static final String IDLE   = "idle";
    public static final String WALK   = "walk";
    public static final String REST   = "rest";
    public static final String SIT    = "sit";
    public static final String RIDE   = "ride";
    public static final String ATTACK = "attack";
    public static final String HURT   = "hurt";

    // -- Controller Type --

    /**
     * Identifies the type of animation controller.
     * Used by loader-specific animation controllers to determine which animation logic to apply.
     */
    public enum ControllerType {
        ATTACK,
        LOCOMOTION;

        public String getName() {
            return name().charAt(0) + name().substring(1).toLowerCase();
        } // getName ()
    } // Enum: ControllerType

    // -- Transition Timing Constants --

    public static final int DEFAULT_TRANSITION_TICKS    = 2;
    public static final int ATTACK_TRANSITION_TICKS     = 0;
    public static final int LOCOMOTION_TRANSITION_TICKS = 2;

    // -- Loop Constants --

    public static final boolean DEFAULT_LOOP = true;
    public static final boolean ATTACK_LOOP  = false;

    // -- Shared Random --

    /**
     * Shared random source for pool selection.
     * <p>
     * <b>Design Decision:</b> {@link AnimationPool#selectNext} takes {@code java.util.Random}
     * to keep the animation package pure Java with no Minecraft dependency. We use a static
     * instance here rather than pulling {@code entity.getRandom()} (which returns Minecraft's
     * {@code RandomSource}) to maintain that boundary.
     */
    private static final Random RANDOM = new Random();

    // -- Attack Animation Logic --

    /**
     * Determines if attack animation should be playing.
     * <p>
     * <b>Logic:</b> Attack animation plays when entity is actively swinging.
     * This is synchronized with damage application and provides visual feedback
     * for combat actions.
     *
     * @param entity entity to check
     * @return true if attack animation should play
     */
    public static boolean shouldPlayAttackAnimation(NativeEntity entity) {
        return entity.swinging;
    } // shouldPlayAttackAnimation ()

    // -- Locomotion Animation Logic --

    /**
     * Determines the appropriate locomotion animation name for the current entity state.
     * <p>
     * <b>Priority chain:</b>
     * <ol>
     *   <li>Vehicle riding → ride pool, then sit pool, then {@code IDLE} (not {@code SIT}).
     *       Falling back to {@code IDLE} (not the {@code SIT} constant) ensures families
     *       without a sit pool — such as Tribute robots — show {@code idle} while riding
     *       rather than hardcoding a {@code sit} animation they don't have.</li>
     *   <li>Moving → walk pool, then {@code WALK} constant.</li>
     *   <li>Idle-slot path — active when the profile declares at least one
     *       {@link IdleSlot}: evaluates slots by priority and threshold via
     *       {@link #resolveIdleSlot}.</li>
     *   <li>Legacy sitting-pose branch — active when no idle slots are declared.
     *       Preserved for backward compatibility with entities that have not yet
     *       migrated to idle slots.</li>
     *   <li>Idle pool / {@code IDLE} constant — always the final fallback.</li>
     * </ol>
     * <p>
     * <b>Profile-aware:</b> If the entity's current animator variant carries an
     * {@link AnimationProfile}, its pools are consulted. Falls back to string constants
     * when no profile is present.
     *
     * @param entity   entity to check
     * @param isMoving whether the entity is currently moving
     * @return animation name that should be playing; never {@code null}
     */
    public static String getLocomotionAnimation(NativeEntity entity, boolean isMoving) {
        AnimationProfile profile = resolveProfile(entity);

        // Priority 1: vehicle riding.
        // Falls back to IDLE (not SIT) when neither ride nor sit pool is declared —
        // families without a sit pool (e.g. Tribute) should show idle while riding.
        if (entity.getVehicle() != null) {
            if (profile != null) {
                String ride = selectFromSlot(profile.getRide(), null);
                if (ride != null) return ride;
                String sit = selectFromSlot(profile.getSit(), null);
                if (sit != null) return sit;
            }
            return IDLE;
        }

        // Priority 2: walking.
        if (isMoving) {
            return selectFromSlot(profile != null ? profile.getWalk() : null, WALK);
        }

        // Priority 3: idle-slot path — only active when the profile declares slots.
        if (profile != null && !profile.getIdleSlots().isEmpty()) {
            return resolveIdleSlot(entity, profile);
        }

        // Priority 4: legacy sitting-pose branch — active when no idle slots declared.
        // Preserved for backward compatibility with entities not yet using idle slots.
        if (entity.getCurrentState() == EntityState.Standby) {
            if (entity.isInSittingPose()) {
                return selectFromSlot(profile != null ? profile.getSit() : null, SIT);
            } else {
                return selectFromSlot(profile != null ? profile.getRest() : null, REST);
            }
        }

        // Final fallback: idle pool.
        return selectFromSlot(profile != null ? profile.getIdle() : null, IDLE);
    } // getLocomotionAnimation ()

    /**
     * Evaluates the declared {@link IdleSlot} list and returns the animation name of the
     * winning slot, calling {@link NativeEntity#onIdleSlotChanged} when the winner changes.
     * <p>
     * <b>Evaluation:</b> Slots are sorted by priority descending. The first slot whose
     * {@link IdleCondition} passes AND whose {@link IdleSlot#getActivationThresholdTicks()}
     * is satisfied wins.
     * <p>
     * <b>Stability guarantee:</b> {@code idleStationaryTicks} grows monotonically while
     * the entity is idle. The threshold comparison {@code >= N} is stable once crossed —
     * the same slot wins on every controller tick until the entity moves, eliminating the
     * flicker from the old two-clock desync in {@code handleStandbyAnimation()}.
     * <p>
     * <b>Slot-change hook:</b> When the winning slot differs from the previous call,
     * {@link NativeEntity#onIdleSlotChanged} is invoked so the entity can update
     * {@code IS_IN_SITTING_POSE} or refresh its hitbox.
     *
     * @param entity  the entity being animated
     * @param profile the entity's resolved animation profile (guaranteed non-null with slots)
     * @return animation name from the winning slot's pool, or {@code IDLE} as fallback
     */
    private static String resolveIdleSlot(NativeEntity entity, AnimationProfile profile) {
        List<IdleSlot> sorted = profile.getIdleSlots().stream()
                .sorted(Comparator.comparingInt(IdleSlot::getPriority).reversed())
                .toList();

        IdleSlot winner = null;
        for (IdleSlot slot : sorted) {
            if (slot.getCondition().test(entity)
                    && entity.getIdleStationaryTicks() >= slot.getActivationThresholdTicks()) {
                winner = slot;
                break;
            }
        }

        // Fire hook when the winning slot changes.
        IdleSlot previous = entity.getCurrentIdleSlot();
        if (previous != winner) {
            entity.setCurrentIdleSlot(winner);
            entity.onIdleSlotChanged(previous, winner);
        }

        if (winner != null) {
            String name = selectFromSlot(winner.getPool(), null);
            return name != null ? name : IDLE;
        }
        return selectFromSlot(profile.getIdle(), IDLE);
    } // resolveIdleSlot ()

    // -- Transition Logic --

    /**
     * Calculates appropriate transition time between animations.
     *
     * @param fromAnimation  current animation name (unused, kept for API compatibility)
     * @param toAnimation    target animation name (unused, kept for API compatibility)
     * @param controllerType type of animation controller
     * @return transition time in ticks
     */
    public static int getTransitionTime(String fromAnimation, String toAnimation, ControllerType controllerType) {
        return switch (controllerType) {
            case ATTACK     -> ATTACK_TRANSITION_TICKS;
            case LOCOMOTION -> LOCOMOTION_TRANSITION_TICKS;
        };
    } // getTransitionTime ()

    /**
     * Determines if an animation should loop.
     *
     * @param animationName name of the animation
     * @return true if animation should loop
     */
    public static boolean shouldLoop(String animationName) {
        return !ATTACK.equals(animationName);
    } // shouldLoop ()

    // -- Private Helpers --

    /**
     * Resolves the {@link AnimationProfile} from the entity's current animator variant.
     * Returns {@code null} if no profile is configured (legacy fallback path).
     * <p>
     * <b>Visibility:</b> Public to allow loader-specific {@code InternalAnimation} classes
     * to retrieve the profile for constructing GeckoLib controllers.
     */
    public static AnimationProfile resolveProfilePublic(NativeEntity entity) {
        return resolveProfile(entity);
    } // resolveProfilePublic ()

    /**
     * Resolves the {@link AnimationProfile} from the entity's current animator variant key.
     * Returns {@code null} if no profile is configured (legacy fallback path).
     * <p>
     * <b>Resolution strategy:</b> The entity's {@code ANIMATOR_VARIANT} synced data holds
     * the current animator key — set at spawn time by either:
     * <ul>
     *   <li>{@code AppearanceVariantFeature} entities (e.g. Gourdragora) —
     *       set via {@code setAnimatorVariant(appearance.getAnimatorKey())}</li>
     *   <li>{@code AnimatorVariantFeature} entities (e.g. Mushrooms) —
     *       set via {@code applyBaseAttributes()} default, or biome init</li>
     * </ul>
     * In both cases the key points to a {@link StandardAnimatorVariant} in
     * {@code VariantRegistries.ANIMATORS}. We look it up there directly — this works
     * for both feature types without any branching on which feature is present.
     * <p>
     * <b>Fallback:</b> If the exact key isn't registered (e.g. {@code BiomeAppearanceFeature}
     * set a texture key as the animator variant), we fall back to the default animator variant
     * from the {@code AnimatorVariantFeature}, then try {@code AppearanceVariantFeature}'s
     * default. If neither resolves, returns {@code null} and the fallback animation name
     * constants in {@link AnimationStateManager} apply.
     */
    private static AnimationProfile resolveProfile(NativeEntity entity) {
        if (entity.nativeEntity == null) return null;

        // Step 1: Try the current animator variant key directly in the global registry.
        // Works for both AppearanceVariantFeature and AnimatorVariantFeature entities.
        String animatorKey = entity.getAnimatorVariant();
        if (animatorKey != null && !animatorKey.isEmpty()) {
            var found = net.heriazone.hzlib.api.entity.variants.VariantRegistries.ANIMATORS.get(animatorKey);
            if (found.isPresent() && found.get() instanceof net.heriazone.hzlib.framework.entity.variants.StandardAnimatorVariant sav
                    && sav.hasAnimationProfile()) {
                return sav.getAnimationProfile();
            }
        }

        // Step 2: Fallback — animator key not in registry (e.g. texture key contamination).
        // Try the default animator variant from AnimatorVariantFeature.
        var animatorFeatureProfile = entity.nativeEntity
                .getFeature(net.heriazone.hzlib.api.entity.features.variants.AnimatorVariantFeature.class)
                .map(feature -> feature.getDefaultVariant(entity.nativeEntity.getKey()))
                .filter(v -> v instanceof net.heriazone.hzlib.framework.entity.variants.StandardAnimatorVariant)
                .map(v -> ((net.heriazone.hzlib.framework.entity.variants.StandardAnimatorVariant) v).getAnimationProfile())
                .orElse(null);
        if (animatorFeatureProfile != null) return animatorFeatureProfile;

        // Step 3: Fallback — try the default appearance's animator key for AppearanceVariantFeature entities.
        return entity.nativeEntity
                .getFeature(net.heriazone.hzlib.api.entity.features.variants.AppearanceVariantFeature.class)
                .map(feature -> feature.getDefaultVariant(entity.nativeEntity.getKey()))
                .map(appearance -> net.heriazone.hzlib.api.entity.variants.VariantRegistries.ANIMATORS
                        .get(appearance.getAnimatorKey())
                        .filter(v -> v instanceof net.heriazone.hzlib.framework.entity.variants.StandardAnimatorVariant)
                        .map(v -> ((net.heriazone.hzlib.framework.entity.variants.StandardAnimatorVariant) v).getAnimationProfile())
                        .orElse(null))
                .orElse(null);
    } // resolveProfile ()

    /**
     * Selects an animation name from a pool, falling back to the default name if the pool
     * is null or empty.
     * <p>
     * <b>Note:</b> Uses the shared {@link #RANDOM} instance — {@code java.util.Random} —
     * to keep this class free of Minecraft's {@code RandomSource} dependency.
     */
    private static String selectFromSlot(AnimationPool pool, String fallback) {
        if (AnimationProfile.isUsable(pool)) {
            String selected = pool.selectNext(RANDOM);
            return selected != null ? selected : fallback;
        }
        return fallback;
    } // selectFromSlot ()

} // Class: AnimationStateManager
