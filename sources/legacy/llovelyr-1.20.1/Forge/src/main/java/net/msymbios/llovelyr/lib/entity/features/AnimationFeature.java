package net.msymbios.llovelyr.lib.entity.features;

import net.msymbios.llovelyr.framework.entity.type.ResourceMap;
import software.bernie.geckolib.core.animation.RawAnimation;

/**
 * <p>Manages GeckoLib animations for different entity states and actions.</p>
 * <p>
 * <b>Architecture:</b> Provides composable animation management that can be attached to any entity type
 * through the feature system. Maps animation types to GeckoLib RawAnimation instances, enabling entities
 * to have distinct animation profiles.
 * <p>
 * <b>Design Decision:</b> Uses ResourceMap for type-safe animation storage. Provides convenience methods
 * for common animation patterns (looping vs. once) to reduce boilerplate in entity implementations.
 * <p>
 * <b>Integration:</b> Works with GeckoLib animation system. RawAnimation instances are created using
 * GeckoLib's animation builder API.
 */
public class AnimationFeature {

    // -- Animation Type Enumeration --
    
    /**
     * <p>Defines standard animation types for entity behavior.</p>
     * <p>
     * <b>Coverage:</b> Includes all common entity animations from basic movement to special actions.
     */
    public enum AnimationType {
        /** Idle/standing animation */
        IDLE,
        /** Walking animation */
        WALK,
        /** Running animation */
        RUN,
        /** Attack animation */
        ATTACK,
        /** Hurt/damage taken animation */
        HURT,
        /** Death animation */
        DEATH,
        /** Sitting animation */
        SIT,
        /** Resting/sleeping animation */
        REST,
        /** Interaction animation */
        INTERACT,
        /** Waving animation */
        WAVE,
        /** Special/unique animation */
        SPECIAL
    }

    // -- Fields --
    
    private final ResourceMap<AnimationType, RawAnimation> animations;

    // -- Constructors --
    
    /**
     * Creates empty animation feature with no configured animations.
     */
    public AnimationFeature() {
        this.animations = new ResourceMap<>();
    }

    // -- Animation Management --
    
    /**
     * Associates animation with specified animation type.
     * <p>
     * <b>State Impact:</b> Adds or replaces animation mapping for specified type.
     * <p>
     * <b>Fluent API:</b> Returns this instance for method chaining.
     * 
     * @param type animation type to configure
     * @param animation raw animation to associate with type
     * @return this feature instance for chaining
     */
    public AnimationFeature withAnimation(AnimationType type, RawAnimation animation) {
        if (type != null && animation != null) {
            this.animations.put(type, animation);
        }
        return this;
    }

    /**
     * Creates and associates looping animation with specified type.
     * <p>
     * <b>Convenience Method:</b> Wraps GeckoLib's looping animation creation to reduce boilerplate.
     * <p>
     * <b>Use Case:</b> Ideal for continuous animations like IDLE, WALK, RUN.
     * <p>
     * <b>Fluent API:</b> Returns this instance for method chaining.
     * 
     * @param type animation type to configure
     * @param animationName name of animation in GeckoLib model
     * @return this feature instance for chaining
     */
    public AnimationFeature withLoopingAnimation(AnimationType type, String animationName) {
        if (type != null && animationName != null) {
            RawAnimation animation = RawAnimation.begin().thenLoop(animationName);
            this.animations.put(type, animation);
        }
        return this;
    }

    /**
     * Creates and associates one-time animation with specified type.
     * <p>
     * <b>Convenience Method:</b> Wraps GeckoLib's one-time animation creation to reduce boilerplate.
     * <p>
     * <b>Use Case:</b> Ideal for triggered animations like ATTACK, HURT, DEATH.
     * <p>
     * <b>Fluent API:</b> Returns this instance for method chaining.
     * 
     * @param type animation type to configure
     * @param animationName name of animation in GeckoLib model
     * @return this feature instance for chaining
     */
    public AnimationFeature withOnceAnimation(AnimationType type, String animationName) {
        if (type != null && animationName != null) {
            RawAnimation animation = RawAnimation.begin().thenPlay(animationName);
            this.animations.put(type, animation);
        }
        return this;
    }

    // -- Animation Queries --
    
    /**
     * Returns animation for specified type.
     * <p>
     * <b>Fallback Behavior:</b> Returns null if animation type not configured.
     * Callers should handle null or provide IDLE animation as fallback.
     * 
     * @param type animation type to retrieve
     * @return raw animation for type, or null if not configured
     */
    public RawAnimation getAnimation(AnimationType type) {
        return this.animations.get(type);
    }

    /**
     * Checks if animation is configured for specified type.
     * 
     * @param type animation type to check
     * @return true if animation is configured for type, false otherwise
     */
    public boolean hasAnimation(AnimationType type) {
        return this.animations.has(type);
    }

} // Class: AnimationFeature
