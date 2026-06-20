package net.heriazone.hzlib.api.entity.features;

import net.heriazone.hzlib.framework.entity.data.ResourceMap;
import net.minecraft.sounds.SoundEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Manages sound events for different entity actions and states.</p>
 * <p>
 * <b>Architecture:</b> Provides composable sound management that can be attached to any entity type
 * through the feature system. Supports two lookup dimensions:
 * <ul>
 *   <li><b>Unkeyed</b> — one sound per {@link SoundType}, shared across all visual forms.</li>
 *   <li><b>Variant-keyed</b> — sound per {@code (SoundType, variantKey)} pair, where
 *       {@code variantKey} is typically the entity's current {@code MODEL_VARIANT} synced
 *       data value. Falls back to the unkeyed entry when no variant-specific sound is found.</li>
 * </ul>
 * <p>
 * <b>Design Decision:</b> The variant-keyed dimension was added to handle entities like
 * Gourdragora (mini/default/mega size variants with distinct ambient sounds) and Mandrake
 * (per-variant-type sounds). Rather than ad-hoc per-entity overrides, all entities use the
 * same `SoundFeature` API — those that don't need variants simply ignore the second dimension.
 * <p>
 * <b>Animation-linked sounds:</b> Use {@link SoundType#INTERACT} with the animation name as
 * the variant key. This allows {@code getSound(INTERACT, "sing")} to return the correct sound
 * for that specific animation without a separate sound type for every possible animation.
 * <p>
 * <b>Design reference:</b> ADR_016 — Sound & Animation Lifecycle System.
 */
public class SoundFeature {

    // -- Sound Type Enumeration --

    /**
     * Defines standard sound event types for entity audio.
     * <p>
     * <b>Usage note:</b> {@link #INTERACT} doubles as the animation-linked sound bucket —
     * use the animation name as the variant key when calling
     * {@link #getSound(SoundType, String)}.
     */
    public enum SoundType {
        /** Default/fallback sound */
        DEFAULT,
        /** Ambient/idle sound played periodically by Minecraft's ambient tick */
        AMBIENT,
        /** Sound played when entity takes damage */
        HURT,
        /** Sound played when entity dies */
        DEATH,
        /** Sound played when entity attacks */
        ATTACK,
        /**
         * Sound played during player interaction or animation-driven events.
         * <p>
         * <b>Animation-linked:</b> When used with a variant key equal to an animation name
         * (e.g., {@code getSound(INTERACT, "sing")}), returns the sound associated with
         * that specific animation trigger.
         */
        INTERACT,
        /** Sound played when entity walks/moves */
        STEP
    } // Enum: SoundType

    // -- Variables --

    /** Unkeyed sounds — one per SoundType. */
    private final ResourceMap<SoundType, SoundEvent> sounds;

    /**
     * Variant-keyed sounds — {@code Map<variantKey, SoundEvent>} per SoundType.
     * <p>
     * Populated only for entity types that need per-variant audio (Gourdragora, animation-linked).
     * Null entries are never stored — absent keys fall back to the unkeyed map.
     */
    private final Map<SoundType, Map<String, SoundEvent>> variantSounds;

    // -- Constructors --

    /**
     * Creates an empty sound feature with no configured sounds.
     */
    public SoundFeature() {
        this.sounds       = new ResourceMap<>();
        this.variantSounds = new HashMap<>();
    } // Constructor: SoundFeature ()

    // -- Unkeyed Sound Management (existing API) --

    /**
     * Associates a sound event with the specified sound type.
     * <p>
     * This is the unkeyed entry — applies to all visual forms of the entity
     * unless overridden by a variant-specific entry.
     *
     * @param type  sound type to configure
     * @param sound sound event to associate
     * @return this instance for method chaining
     */
    public SoundFeature withSound(SoundType type, SoundEvent sound) {
        if (type != null && sound != null) {
            this.sounds.put(type, sound);
        }
        return this;
    } // withSound ()

    // -- Variant-Keyed Sound Management (new API) --

    /**
     * Associates a sound event with a specific {@code (SoundType, variantKey)} pair.
     * <p>
     * <b>Typical use cases:</b>
     * <ul>
     *   <li>Gourdragora size variants: {@code withSound(AMBIENT, "gourdragora_girl_mega", MEGA_SOUND)}</li>
     *   <li>Animation-linked sounds: {@code withSound(INTERACT, "sing", MANDRAKE_SONG)}</li>
     * </ul>
     * <p>
     * The variant key is typically the entity's {@code MODEL_VARIANT} synced data value
     * for visual-form sounds, or the animation name for animation-linked sounds.
     *
     * @param type       sound type to configure
     * @param variantKey discriminator key (model variant name or animation name)
     * @param sound      sound event to associate
     * @return this instance for method chaining
     */
    public SoundFeature withSound(SoundType type, String variantKey, SoundEvent sound) {
        if (type != null && variantKey != null && !variantKey.isEmpty() && sound != null) {
            variantSounds.computeIfAbsent(type, k -> new HashMap<>()).put(variantKey, sound);
        }
        return this;
    } // withSound ()

    // -- Sound Queries --

    /**
     * Returns the unkeyed sound event for the specified type.
     * <p>
     * Use this when the entity's sound does not depend on its current visual form.
     *
     * @param type sound type to retrieve
     * @return sound event, or {@code null} if not configured
     */
    public SoundEvent getSound(SoundType type) {
        return this.sounds.get(type);
    } // getSound ()

    /**
     * Returns the sound event for the specified type and variant key, with fallback.
     * <p>
     * <b>Lookup order:</b>
     * <ol>
     *   <li>Variant-keyed entry: {@code variantSounds[type][variantKey]}</li>
     *   <li>Unkeyed entry: {@code sounds[type]}</li>
     *   <li>{@code null} if neither is configured</li>
     * </ol>
     * <p>
     * <b>For visual-form sounds:</b> pass {@code entity.getModelVariant()} as the key.
     * <b>For animation-linked sounds:</b> pass the animation name as the key.
     *
     * @param type       sound type to retrieve
     * @param variantKey discriminator key (model variant or animation name)
     * @return sound event, or {@code null} if not configured for either key
     */
    public SoundEvent getSound(SoundType type, String variantKey) {
        if (variantKey != null && !variantKey.isEmpty()) {
            Map<String, SoundEvent> variants = variantSounds.get(type);
            if (variants != null) {
                SoundEvent found = variants.get(variantKey);
                if (found != null) return found;
            }
        }
        return this.sounds.get(type); // fallback to unkeyed
    } // getSound ()

    /**
     * Returns a randomly selected sound event from all configured sounds.
     * <p>
     * <b>Use case:</b> Adding variety to ambient or idle sounds when multiple
     * unkeyed entries exist.
     *
     * @return randomly selected sound event, or {@code null} if no sounds configured
     */
    public SoundEvent getRandomSound() {
        return this.sounds.getRandom();
    } // getRandomSound ()

    /**
     * Returns whether a sound event is configured for the specified type (unkeyed).
     *
     * @param type sound type to check
     * @return {@code true} if a sound is configured
     */
    public boolean hasSound(SoundType type) {
        return this.sounds.has(type);
    } // hasSound ()

    /**
     * Returns whether a sound event is configured for the specified type and variant key.
     *
     * @param type       sound type to check
     * @param variantKey discriminator key
     * @return {@code true} if a variant-specific or unkeyed sound is configured
     */
    public boolean hasSound(SoundType type, String variantKey) {
        return getSound(type, variantKey) != null;
    } // hasSound ()

} // Class: SoundFeature
