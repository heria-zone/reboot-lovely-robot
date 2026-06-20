package net.heriazone.hzlib.api.entity.features;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.*;

/**
 * <p>Maps entity appearance keys to biomes, driving spawn-time visual selection.<p>
 * <p>
 * <b>Responsibility:</b> Answers one question at spawn — "which variant key applies here?"
 * It knows nothing about what that key resolves to (texture, model, size). That's the
 * variant system's concern. This feature is purely a biome-to-key lookup table.
 * <p>
 * <b>Integration:</b> Declare in the entity type class as the source of truth.
 * The entity reads it via {@code nativeEntity.getFeature(BiomeAppearanceFeature.class)}
 * inside {@code initializeSpawnVariants}, replacing any hardcoded biome chains.
 * <p>
 * <b>Fallback:</b> Any biome not explicitly mapped returns the configured default key,
 * allowing sparse mapping — only biomes that deviate from the default need entries.
 */
public class BiomeAppearanceFeature {

    // -- Fields --

    private final Map<ResourceKey<Biome>, List<String>> biomeToVariant;
    private final String defaultVariantKey;

    // -- Constructor --

    private BiomeAppearanceFeature(Map<ResourceKey<Biome>, List<String>> biomeToVariant, String defaultVariantKey) {
        this.biomeToVariant = Collections.unmodifiableMap(biomeToVariant);
        this.defaultVariantKey = defaultVariantKey;
    } // Constructor: BiomeAppearanceFeature ()

    // -- Lookup --

    /**
     * Resolves the variant key for a given spawn biome.
     * <p>
     * <b>Fallback chain:</b> Explicit mapping → default key → null.
     * Callers should guard against null if no default was configured.
     *
     * @param biomeKey the biome the entity is spawning in
     * @return variant key for the biome, or the default if unmapped
     */
    public String resolve(ResourceKey<Biome> biomeKey) {
        List<String> candidates = biomeToVariant.get(biomeKey);
        if (candidates == null || candidates.isEmpty()) return defaultVariantKey;
        if (candidates.size() == 1) return candidates.get(0);
        return candidates.get(new Random().nextInt(candidates.size()));
    } // resolve ()

    /**
     * Returns the fallback variant key used for any unmapped biome.
     *
     * @return default variant key, or null if none was configured
     */
    public String getDefaultVariantKey() {
        return defaultVariantKey;
    } // getDefaultVariantKey ()

    // -- Builder --

    /**
     * Returns a new builder for composing a {@link BiomeAppearanceFeature}.
     *
     * @return fresh builder instance
     */
    public static Builder builder() {
        return new Builder();
    } // builder ()

    /**
     * <p>Fluent builder for {@link BiomeAppearanceFeature}.<p>
     * <p>
     * <b>Usage pattern:</b> Group biomes under their variant key — one call per
     * entity appearance, multiple biomes per call. Unmapped biomes fall through
     * to the configured default.
     *
     * <pre>{@code
     * BiomeAppearanceFeature.builder()
     *     .withMapping("mushroom_brown_ruby",      Biomes.TAIGA, Biomes.SNOWY_TAIGA)
     *     .withMapping("mushroom_brown_scarlatina", Biomes.DARK_FOREST)
     *     .withDefault("mushroom_brown_boletus")
     *     .build();
     * }</pre>
     */
    public static class Builder {

        // -- Variables --

        private final Map<ResourceKey<Biome>, List<String>> biomeToVariant = new HashMap<>();
        private String defaultVariantKey = null;

        // -- Constructor --

        public Builder() {} // Constructor: Builder ()

        // -- Methods --

        /**
         * Maps one or more biomes to a variant key.
         * <p>
         * If a biome is mapped multiple times, the last call wins.
         *
         * @param variantKey the entity appearance key these biomes resolve to
         * @param biomes     one or more biomes that trigger this variant
         * @return this builder for chaining
         */
        @SafeVarargs
        public final Builder withMapping(String variantKey, ResourceKey<Biome>... biomes) {
            if (variantKey != null && biomes != null) {
                for (ResourceKey<Biome> biome : biomes) {
                    if (biome != null) biomeToVariant
                            .computeIfAbsent(biome, k -> new ArrayList<>())
                            .add(variantKey);
                }
            }
            return this;
        } // withMapping ()

        /**
         * Sets the fallback variant key for any biome not explicitly mapped.
         *
         * @param variantKey default entity appearance key
         * @return this builder for chaining
         */
        public Builder withDefault(String variantKey) {
            this.defaultVariantKey = variantKey;
            return this;
        } // withDefault ()

        /**
         * Builds the immutable {@link BiomeAppearanceFeature}.
         *
         * @return configured feature instance
         */
        public BiomeAppearanceFeature build() {
            Map<ResourceKey<Biome>, List<String>> immutable = new HashMap<>();
            biomeToVariant.forEach((k, v) -> immutable.put(k, Collections.unmodifiableList(new ArrayList<>(v))));
            return new BiomeAppearanceFeature(Collections.unmodifiableMap(immutable), defaultVariantKey);
        } // build ()

    } // Class: Builder

} // Class: BiomeAppearanceFeature