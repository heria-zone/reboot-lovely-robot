package net.heriazone.hzlib.api.entity.features;

/**
 * Marker interface for all feature modules attached to a {@link net.heriazone.hzlib.api.entity.NativeEntityFamily}.
 * <p>
 * <b>Architecture:</b> Features are registered on a family descriptor via
 * {@code withFeature(Class, instance)} and retrieved via {@code getFeature(Class)}.
 * Implementing this interface signals that a class is a first-class feature module —
 * a self-contained data carrier that extends entity behaviour without subclassing.
 * <p>
 * <b>Design Decision:</b> Marker only — no abstract methods. Features have
 * heterogeneous APIs; a shared method contract would force every feature to implement
 * no-op stubs. The Class-keyed map in {@code NativeEntityFamily} provides type-safe
 * retrieval without a common interface method.
 */
public interface NativeFeature {
    // Marker — no contract. Features are retrieved by Class key, not by polymorphism.
} // Interface: NativeFeature
