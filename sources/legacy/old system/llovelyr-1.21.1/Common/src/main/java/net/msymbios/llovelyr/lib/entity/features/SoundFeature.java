package net.msymbios.llovelyr.lib.entity.features;

import net.minecraft.sounds.SoundEvent;
import net.msymbios.llovelyr.framework.entity.type.ResourceMap;

/**
 * <p>Manages sound events for different entity actions and states.</p>
 * <p>
 * <b>Architecture:</b> Provides composable sound management that can be attached to any entity type
 * through the feature system. Maps sound types to specific sound events, enabling entities to have
 * distinct audio profiles.
 * <p>
 * <b>Design Decision:</b> Uses ResourceMap for type-safe sound storage with random selection support.
 * Enables fallback to DEFAULT sound when specific sound types are not configured.
 * <p>
 * <b>Use Case:</b> Allows entities to have configurable sound profiles without hardcoding
 * sound events in entity classes.
 */
public class SoundFeature {

    // -- Sound Type Enumeration --

    /**
     * <p>Defines standard sound event types for entity audio.</p>
     * <p>
     * <b>Coverage:</b> Includes all common entity sound events from ambient to interaction.
     */
    public enum SoundType {
        /** Default/fallback sound */
        DEFAULT,
        /** Ambient/idle sound played periodically */
        AMBIENT,
        /** Sound played when entity takes damage */
        HURT,
        /** Sound played when entity dies */
        DEATH,
        /** Sound played when entity attacks */
        ATTACK,
        /** Sound played during player interaction */
        INTERACT,
        /** Sound played when entity walks/moves */
        STEP
    } // Enums: SoundType

    // -- Variables --

    private final ResourceMap<SoundType, SoundEvent> sounds;

    // -- Constructors --

    /**
     * Creates empty sound feature with no configured sounds.
     */
    public SoundFeature() {
        this.sounds = new ResourceMap<>();
    } // Constructor: SoundFeature ()

    // -- Sound Management --

    /**
     * Associates sound event with specified sound type.
     * <p>
     * <b>State Impact:</b> Adds or replaces sound mapping for specified type.
     * <p>
     * <b>Fluent API:</b> Returns this instance for method chaining.
     *
     * @param type sound type to configure
     * @param sound sound event to associate with type
     * @return this feature instance for chaining
     */
    public SoundFeature withSound(SoundType type, SoundEvent sound) {
        if (type != null && sound != null) {
            this.sounds.put(type, sound);
        }
        return this;
    } // withSound ()

    // -- Sound Queries --

    /**
     * Returns sound event for specified type.
     * <p>
     * <b>Fallback Behavior:</b> Returns null if sound type not configured.
     * Callers should handle null or provide DEFAULT sound as fallback.
     *
     * @param type sound type to retrieve
     * @return sound event for type, or null if not configured
     */
    public SoundEvent getSound(SoundType type) {
        return this.sounds.get(type);
    } // getSound ()

    /**
     * Returns randomly selected sound event from configured sounds.
     * <p>
     * <b>Use Case:</b> Useful for adding variety to ambient or idle sounds.
     * <p>
     * <b>Performance:</b> O(1) after initial cache build due to ResourceMap caching.
     *
     * @return randomly selected sound event, or null if no sounds configured
     */
    public SoundEvent getRandomSound() {
        return this.sounds.getRandom();
    } // getRandomSound ()

    /**
     * Checks if sound event is configured for specified type.
     *
     * @param type sound type to check
     * @return true if sound is configured for type, false otherwise
     */
    public boolean hasSound(SoundType type) {
        return this.sounds.has(type);
    } // hasSound ()

} // Class: SoundFeature