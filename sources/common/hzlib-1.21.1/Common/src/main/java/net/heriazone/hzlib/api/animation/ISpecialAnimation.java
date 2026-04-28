package net.heriazone.hzlib.api.animation;

/**
 * Marker interface for values that can be registered as special animations
 * in an {@link AnimationProfile}.
 * <p>
 * <b>Implementations:</b>
 * <ul>
 *   <li>{@link AnimationPool} — simple interaction animations (wave, yipee)</li>
 *   <li>{@link AnimationSequence} — complex multi-phase abilities (Dragon's Fury)</li>
 * </ul>
 * <p>
 * <b>Design Decision:</b> A common interface allows the {@code specialAnimations}
 * map in {@link AnimationProfile} to accept both pools and sequences without
 * requiring the caller to know which type is stored.
 */
public interface ISpecialAnimation {
    // Marker interface — no methods required.
    // Type identity is sufficient for the profile to dispatch correctly.
} // Interface: ISpecialAnimation
