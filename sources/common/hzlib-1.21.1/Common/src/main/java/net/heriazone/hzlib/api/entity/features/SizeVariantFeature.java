package net.heriazone.hzlib.api.entity.features;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;

import java.util.*;

/**
 * Carries per-size configuration for entities with size-based entity appearances.
 * <p>
 * <b>Architecture:</b> Attached to an entity type's feature set. The entity reads
 * its current {@code MODEL_VARIANT} key (e.g., {@code "mini"}, {@code "default"},
 * {@code "big"}) and calls {@link #getConfig(String)} to retrieve the corresponding
 * dimensions and stat multipliers.
 * <p>
 * <b>Dynamic hitbox:</b> The entity overrides {@code getDimensions(Pose)} to call
 * {@link #getDimensionsForPose(String, Pose)}, ensuring correct hitboxes for
 * standing, sitting, and riding states. This is Option B (dynamic) — the hitbox
 * is re-read on every pose change, not baked at spawn.
 * <p>
 * <b>Stat application:</b> Stats are baked into entity attributes at spawn via
 * {@link SizeConfig#applyTo(net.minecraft.world.entity.LivingEntity)}. They must
 * also be re-applied in {@code readAdditionalSaveData()} after loading
 * {@code MODEL_VARIANT} from NBT — otherwise stats reset to defaults on world reload.
 * <p>
 * <b>Scale factor:</b> Gourdragora Big uses the same model as Default but rendered
 * at a larger scale. The renderer reads {@link SizeConfig#scale()} and applies it.
 * A scale of {@code 1.0f} means no scaling.
 * <p>
 * <b>Performance:</b> All lookups are O(1) HashMap operations. No computation in
 * the hot path ({@code getDimensions(Pose)} is called frequently by Minecraft).
 */
public class SizeVariantFeature {

    // -- Fields --

    private final Map<String, SizeConfig> configs;
    private final SizeConfig defaultConfig;

    // -- Constructor --

    /**
     * Creates a size variant feature with the given configurations.
     *
     * @param configs       map of size key → configuration (defensive copy taken)
     * @param defaultConfig fallback configuration when a key is not found
     * @throws NullPointerException if configs or defaultConfig is null
     */
    public SizeVariantFeature(Map<String, SizeConfig> configs, SizeConfig defaultConfig) {
        Objects.requireNonNull(configs, "Configs map cannot be null");
        Objects.requireNonNull(defaultConfig, "Default config cannot be null");
        this.configs       = Collections.unmodifiableMap(new HashMap<>(configs));
        this.defaultConfig = defaultConfig;
    } // Constructor: SizeVariantFeature ()

    // -- Query Methods --

    /**
     * Returns the configuration for the given size key.
     * Returns the default configuration if the key is not found — never null.
     *
     * @param sizeKey size variant key (e.g., {@code "mini"}, {@code "default"}, {@code "big"})
     * @return configuration for that size, or the default config if key is unknown
     */
    public SizeConfig getConfig(String sizeKey) {
        if (sizeKey == null) return defaultConfig;
        return configs.getOrDefault(sizeKey, defaultConfig);
    } // getConfig ()

    /**
     * Selects a {@link SizeConfig} at random, weighted by each config's
     * {@link SizeConfig#getSpawnWeight()} value.
     * <p>
     * <b>Algorithm:</b> Sums all weights (not required to equal {@code 1.0}),
     * rolls a uniform float in {@code [0, total)}, then walks the insertion-ordered
     * config map accumulating a cursor until the roll falls below the cursor.
     * Insertion order is preserved via {@link java.util.LinkedHashMap} in the
     * {@link Builder}, so the distribution is stable given the same declaration order.
     * <p>
     * <b>Declared weight examples:</b>
     * <ul>
     *   <li>{@code 0.4, 0.4, 0.2} → 40 % / 40 % / 20 %</li>
     *   <li>{@code 40f, 40f, 20f} (normalized to 0.4, 0.4, 0.2 at build time)</li>
     *   <li>{@code 1.0, 1.0, 1.0} → equal 33 % each</li>
     * </ul>
     * <p>
     * <b>Fallback:</b> Returns {@link #defaultConfig} if the config map is empty or all
     * weights are zero — never throws.
     *
     * @return randomly selected {@link SizeConfig} according to declared weights
     */
    public SizeConfig pickWeightedRandom() {
        float total = 0f;
        for (SizeConfig c : configs.values()) total += c.getSpawnWeight();
        if (total <= 0f) return defaultConfig;
        float roll   = java.util.concurrent.ThreadLocalRandom.current().nextFloat() * total;
        float cursor = 0f;
        for (SizeConfig config : configs.values()) {
            cursor += config.getSpawnWeight();
            if (roll < cursor) return config;
        }
        return defaultConfig;
    } // pickWeightedRandom ()

    /**
     * Returns the entity dimensions for the given size key and pose.
     * Convenience method combining {@link #getConfig(String)} and
     * {@link SizeConfig#getDimensions(Pose)}.
     *
     * @param sizeKey size variant key
     * @param pose    current entity pose
     * @return entity dimensions for that size and pose
     */
    public EntityDimensions getDimensionsForPose(String sizeKey, Pose pose) {
        return getConfig(sizeKey).getDimensions(pose);
    } // getDimensionsForPose ()

    /**
     * Returns all registered size keys.
     *
     * @return unmodifiable set of size keys
     */
    public Set<String> getSizeKeys() {
        return configs.keySet();
    } // getSizeKeys ()

    // -- Builder --

    /**
     * Creates a new builder for constructing a {@link SizeVariantFeature}.
     *
     * @return new builder instance
     */
    public static Builder builder() {
        return new Builder();
    } // builder ()

    /**
     * Fluent builder for {@link SizeVariantFeature}.
     */
    public static final class Builder {

        private final Map<String, SizeConfig> configs = new LinkedHashMap<>();
        private SizeConfig defaultConfig;

        private Builder() {} // Constructor: Builder ()

        /**
         * Registers a size configuration under the given key.
         * The {@code sizeKey} is stamped onto the config so it can be retrieved later
         * via {@link SizeConfig#getSizeKey()} without string manipulation at call sites.
         *
         * @param sizeKey size variant key
         * @param config  configuration for that size
         * @return this builder
         */
        public Builder size(String sizeKey, SizeConfig config) {
            Objects.requireNonNull(sizeKey, "Size key cannot be null");
            Objects.requireNonNull(config, "Config cannot be null");
            config.sizeKey = sizeKey; // stamp the map key onto the config
            configs.put(sizeKey, config);
            return this;
        } // size ()

        /**
         * Sets the fallback configuration for unknown size keys.
         * If not set, the first registered size is used as the default.
         *
         * @param config fallback configuration
         * @return this builder
         */
        public Builder defaultConfig(SizeConfig config) {
            this.defaultConfig = Objects.requireNonNull(config, "Default config cannot be null");
            return this;
        } // defaultConfig ()

        /**
         * Builds the feature.
         *
         * @return new {@link SizeVariantFeature} instance
         * @throws IllegalStateException if no sizes have been registered
         */
        public SizeVariantFeature build() {
            if (configs.isEmpty()) {
                throw new IllegalStateException("SizeVariantFeature must have at least one size configuration");
            }
            SizeConfig fallback = defaultConfig != null
                    ? defaultConfig
                    : configs.values().iterator().next();
            return new SizeVariantFeature(configs, fallback);
        } // build ()

    } // Class: Builder

    // =========================================================================
    // SizeConfig — inner class
    // =========================================================================

    /**
     * Configuration for a single size variant.
     * <p>
     * <b>Dimensions:</b> Carries per-pose {@link EntityDimensions} so that standing,
     * sitting, and riding all have correct hitboxes for this size.
     * <p>
     * <b>Stat multipliers:</b> Applied once at spawn and re-applied on NBT load.
     * A multiplier of {@code 1.0f} means no change from the base stats.
     * <p>
     * <b>Scale:</b> Renderer scale factor. {@code 1.0f} = no scaling. Used for
     * Gourdragora Big, which uses the same model as Default at a larger scale.
     */
    public static final class SizeConfig {

        // -- Fields --

        private final String modelKey;
        private final float scale;
        private final Map<Pose, EntityDimensions> dimensions;
        private final EntityDimensions defaultDimensions;
        private final float spawnWeight;
        private String sizeKey   = ""; // the map key this config was registered under
        private String sizeLabel = ""; // short size word, e.g. "mini", "default", "big"

        // Stat multipliers (1.0 = no change)
        private final float healthMultiplier;
        private final float attackMultiplier;
        private final float speedMultiplier;
        private final float armorMultiplier;
        private final float knockbackResistance;

        // -- Constructor --

        private SizeConfig(Builder builder) {
            this.modelKey           = builder.modelKey;
            this.scale              = builder.scale;
            this.dimensions         = Collections.unmodifiableMap(new EnumMap<>(builder.dimensions));
            this.defaultDimensions  = builder.defaultDimensions;
            this.spawnWeight        = builder.spawnWeight;
            this.healthMultiplier   = builder.healthMultiplier;
            this.attackMultiplier   = builder.attackMultiplier;
            this.speedMultiplier    = builder.speedMultiplier;
            this.armorMultiplier    = builder.armorMultiplier;
            this.knockbackResistance = builder.knockbackResistance;
        } // Constructor: SizeConfig ()

        // -- Accessors --

        /**
         * Returns the model key for this size (e.g., {@code "gourdragora_girl_mini"}).
         * Used by the renderer to select the correct geo model.
         *
         * @return model key
         */
        public String getModelKey() { return modelKey; } // getModelKey ()

        /**
         * Returns the map key under which this config was registered in
         * {@link SizeVariantFeature} (e.g., {@code "gourdragora_girl_mini"}).
         * <p>
         * <b>Use case:</b> Entity spawn logic can derive the appearance variant key
         * directly from this value without any string surgery — e.g.
         * {@code nativeEntity.getKey() + "_" + sizeConfig.getSizeKey().substring(prefix.length())}
         * is better expressed as {@code appFeature.getVariant(nativeEntity.getKey(), sizeConfig.getSizeKey())}.
         * When the size key and model key share the same string (common case), this
         * is identical to {@link #getModelKey()}.
         *
         * @return size map key, never null (empty string if not set via {@link Builder#size})
         */
        public String getSizeKey() { return sizeKey; } // getSizeKey ()

        /**
         * Returns the short size label declared via {@link Builder#sizeLabel(String)}.
         * <p>
         * <b>Purpose:</b> Provides a concise, family-agnostic identifier (e.g. {@code "mini"},
         * {@code "default"}, {@code "big"}) that entity classes can append to any prefix to
         * form an appearance variant key — with zero string surgery.
         * <pre>{@code
         * // Clean — no replace() calls:
         * String appearanceKey = nativeEntity.getKey() + "_" + sizeConfig.getSizeLabel();
         * }</pre>
         *
         * @return short size label (e.g. {@code "mini"}), empty string if not set
         */
        public String getSizeLabel() { return sizeLabel; } // getSizeLabel ()

        /**
         * Returns the renderer scale factor.
         * {@code 1.0f} means no scaling. Values {@code > 1.0f} make the entity larger.
         *
         * @return scale factor
         */
        public float getScale() { return scale; } // getScale ()

        /**
         * Returns the spawn weight as a normalized probability in {@code [0.0, 1.0]}.
         * <p>
         * <b>Normalization contract:</b> Values declared via {@link Builder#spawnWeight(float)}
         * are automatically normalized at build time — values already in {@code (0, 1]} are
         * stored as-is; values {@code > 1} are divided by {@code 100} so that intuitive
         * percentage notation (e.g. {@code 40f} → {@code 0.40}) works without any manual
         * conversion at call sites.
         * <p>
         * {@code 0.6f} means this size is selected approximately 60 % of the time when all
         * weights sum to {@code 1.0}. If the declared weights do not sum to exactly {@code 1.0}
         * that is fine — {@link SizeVariantFeature#pickWeightedRandom()} normalizes on the fly.
         *
         * @return spawn weight in {@code (0.0, 1.0]}
         */
        public float getSpawnWeight() { return spawnWeight; } // getSpawnWeight ()

        /**
         * Returns the entity dimensions for the given pose.
         * Falls back to the default dimensions if no specific dimensions are registered
         * for that pose.
         *
         * @param pose current entity pose
         * @return entity dimensions for that pose
         */
        public EntityDimensions getDimensions(Pose pose) {
            return dimensions.getOrDefault(pose, defaultDimensions);
        } // getDimensions ()

        /** @return health multiplier (1.0 = no change) */
        public float getHealthMultiplier()    { return healthMultiplier;    }
        /** @return attack multiplier (1.0 = no change) */
        public float getAttackMultiplier()    { return attackMultiplier;    }
        /** @return speed multiplier (1.0 = no change) */
        public float getSpeedMultiplier()     { return speedMultiplier;     }
        /** @return armor multiplier (1.0 = no change) */
        public float getArmorMultiplier()     { return armorMultiplier;     }
        /** @return knockback resistance (0.0 = none, 1.0 = full) */
        public float getKnockbackResistance() { return knockbackResistance; }

        /**
         * Applies stat multipliers to the given entity's attributes.
         * <p>
         * <b>Call sites:</b> Must be called both at spawn (in {@code finalizeSpawn()})
         * and on NBT load (in {@code readAdditionalSaveData()}) after loading
         * {@code MODEL_VARIANT}. Failing to call on NBT load causes stats to reset
         * to base values after every world reload.
         *
         * @param entity entity to apply stats to
         */
        public void applyTo(net.minecraft.world.entity.LivingEntity entity) {
            var attributes = entity.getAttributes();

            applyMultiplier(attributes, net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH,
                    healthMultiplier);
            applyMultiplier(attributes, net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE,
                    attackMultiplier);
            applyMultiplier(attributes, net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED,
                    speedMultiplier);
            applyMultiplier(attributes, net.minecraft.world.entity.ai.attributes.Attributes.ARMOR,
                    armorMultiplier);

            // Knockback resistance is set directly, not multiplied
            var kbAttr = attributes.getInstance(
                    net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE);
            if (kbAttr != null) {
                kbAttr.setBaseValue(knockbackResistance);
            }

            // Clamp health to new max after changing max health
            if (healthMultiplier != 1.0f) {
                entity.setHealth(entity.getMaxHealth());
            }
        } // applyTo ()

        private void applyMultiplier(
                net.minecraft.world.entity.ai.attributes.AttributeMap attributes,
                net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute,
                float multiplier) {
            if (multiplier == 1.0f) return;
            var instance = attributes.getInstance(attribute);
            if (instance != null) {
                instance.setBaseValue(instance.getBaseValue() * multiplier);
            }
        } // applyMultiplier ()

        // -- Builder --

        /**
         * Creates a new builder for constructing a {@link SizeConfig}.
         *
         * @param modelKey model key for this size
         * @return new builder instance
         */
        public static Builder builder(String modelKey) {
            return new Builder(modelKey);
        } // builder ()

        /**
         * Fluent builder for {@link SizeConfig}.
         */
        public static final class Builder {

            private final String modelKey;
            private float scale = 1.0f;
            private String sizeLabel = "";
            private final Map<Pose, EntityDimensions> dimensions = new EnumMap<>(Pose.class);
            private EntityDimensions defaultDimensions = EntityDimensions.scalable(0.6f, 1.0f);
            private float spawnWeight        = 1.0f;
            private float healthMultiplier   = 1.0f;
            private float attackMultiplier   = 1.0f;
            private float speedMultiplier    = 1.0f;
            private float armorMultiplier    = 1.0f;
            private float knockbackResistance = 0.0f;

            private Builder(String modelKey) {
                this.modelKey = Objects.requireNonNull(modelKey, "Model key cannot be null");
            } // Constructor: Builder ()

            /**
             * Sets the renderer scale factor. */
            public Builder scale(float scale) {
                this.scale = scale;
                return this;
            } // scale ()

            /**
             * Sets the short size label used for appearance variant key derivation.
             * Keep this concise and family-agnostic (e.g. {@code "mini"}, {@code "default"},
             * {@code "big"}) — entity classes append it to their own key prefix to form the
             * full appearance variant key, with no string replacement needed.
             *
             * @param label short size identifier
             * @return this builder
             */
            public Builder sizeLabel(String label) {
                if (label != null) this.sizeLabel = label;
                return this;
            } // sizeLabel ()

            /**
             * Sets the spawn weight for weighted random size selection.
             * <p>
             * <b>Scale:</b> Accepts values in {@code (0, 1]} directly (e.g. {@code 0.4f} = 40 %)
             * or values {@code > 1} which are automatically normalized by dividing by {@code 100}
             * (e.g. {@code 40f} → {@code 0.40f}). This lets callers use either intuitive
             * percentage notation or normalized fractions without manual conversion.
             * <p>
             * Weights do not need to sum to exactly {@code 1.0} — {@link SizeVariantFeature#pickWeightedRandom()}
             * normalizes on the fly. Default is {@code 1.0f} (equal weight across all sizes).
             *
             * @param weight spawn weight in {@code (0, 1]} or as a percentage {@code > 1}
             * @return this builder
             */
            public Builder spawnWeight(float weight) {
                // Normalize percentage notation (>1) to [0,1] scale
                float normalized = weight > 1.0f ? weight / 100.0f : weight;
                this.spawnWeight = Math.max(0.001f, normalized); // floor at 0.001 to keep positive
                return this;
            } // spawnWeight ()

            /** Sets dimensions for a specific pose. */
            public Builder dimensions(Pose pose, EntityDimensions dims) {
                dimensions.put(Objects.requireNonNull(pose), Objects.requireNonNull(dims));
                return this;
            } // dimensions ()

            /** Sets the default dimensions used when no pose-specific dimensions are registered. */
            public Builder defaultDimensions(EntityDimensions dims) {
                this.defaultDimensions = Objects.requireNonNull(dims);
                return this;
            } // defaultDimensions ()

            /**
             * Convenience method: sets standing dimensions and derives sitting dimensions
             * as half the standing height.
             */
            public Builder standingDimensions(float width, float height) {
                dimensions.put(Pose.STANDING, EntityDimensions.scalable(width, height));
                dimensions.put(Pose.CROUCHING, EntityDimensions.scalable(width, height * 0.5f));
                this.defaultDimensions = EntityDimensions.scalable(width, height);
                return this;
            } // standingDimensions ()

            /** Sets the health multiplier (1.0 = no change). */
            public Builder health(float multiplier)   { this.healthMultiplier   = multiplier; return this; }
            /** Sets the attack multiplier (1.0 = no change). */
            public Builder attack(float multiplier)   { this.attackMultiplier   = multiplier; return this; }
            /** Sets the speed multiplier (1.0 = no change). */
            public Builder speed(float multiplier)    { this.speedMultiplier    = multiplier; return this; }
            /** Sets the armor multiplier (1.0 = no change). */
            public Builder armor(float multiplier)    { this.armorMultiplier    = multiplier; return this; }
            /** Sets the knockback resistance (0.0 = none, 1.0 = full). */
            public Builder knockback(float resistance) { this.knockbackResistance = resistance; return this; }

            /** Builds the size configuration. */
            public SizeConfig build() {
                SizeConfig config = new SizeConfig(this);
                config.sizeLabel = this.sizeLabel;
                return config;
            } // build ()

        } // Class: Builder

    } // Class: SizeConfig

} // Class: SizeVariantFeature
