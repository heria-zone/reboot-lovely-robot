package net.heriazone.lovelylib.common.entity.definition;

import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.RobotFamily;
import net.heriazone.lovelylib.common.entity.enums.EntityTexture;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Complete descriptor for one robot entity — the single source of truth for all
 * per-entity data that was previously scattered across LovelyConstant, SharedConfigs,
 * LegacyConfigs, LegacyRobotFamilies, and the loader-side Items/Entities/Groups files.
 * <p>
 * <b>Architecture:</b> Immutable after construction via the fluent Builder. All downstream
 * consumers — config defaults, family initialization, loader registration — derive their
 * data from this object rather than maintaining parallel per-entity state.
 * <p>
 * <b>Variant Key:</b> getVariantKey() delegates to RobotVariant.getName(), which returns
 * the key string passed to RobotVariant.register(id, name). Never re-derived from the
 * constant field name — a key like "bunny3" must not come from lowercasing "Bunny3".
 * <p>
 * <b>Renderer:</b> Stored as a RendererFactory functional interface so it can be passed
 * directly as a method reference (MyRenderer::new) on all loaders without reflection.
 * <p>
 * <b>Feature Configurator:</b> Optional BiConsumer applied after the standard feature loop
 * in reloadFromConfig(). Handles per-variant extras (e.g. Kitsune's BoneVisibilityFeature)
 * that cannot be expressed uniformly across all variants.
 */
public final class RobotEntityDefinition {

    // -- Types --

    /**
     * Factory for creating an entity renderer given the loader's renderer context.
     * <p>
     * Implemented as a constructor method reference: e.g. KitsuneRenderer::new.
     * The Object in/out types allow the same interface to work across Forge, NeoForge,
     * and Fabric without a loader-specific generic parameter at this layer.
     */
    @FunctionalInterface
    public interface RendererFactory {
        Object create(Object context);
    } // Interface: RendererFactory

    // -- Constants --

    /** Sentinel: empty list signals "use the full 16-color palette." */
    private static final List<EntityTexture> FULL_PALETTE = Collections.emptyList();

    // -- Fields --

    private final RobotVariant                                    variant;
    private final String                                          displayName;
    private final Set<ModTarget>                                  mods;
    private final List<EntityTexture>                             palette;
    private final EntityStats                                     stats;
    private final RendererFactory                                 rendererFactory;
    private final BiConsumer<RobotFamily, SharedConfigs.EntityConfigData> featureConfigurator;

    // -- Constructor --

    private RobotEntityDefinition(Builder b) {
        this.variant             = b.variant;
        this.displayName         = b.displayName;
        this.mods                = Collections.unmodifiableSet(EnumSet.copyOf(b.mods));
        this.palette             = b.palette == null ? FULL_PALETTE
                                                     : Collections.unmodifiableList(b.palette);
        this.stats               = b.stats;
        this.rendererFactory     = b.rendererFactory;
        this.featureConfigurator = b.featureConfigurator;
    } // Constructor: RobotEntityDefinition()

    // -- Identity --

    public RobotVariant getVariant() { return variant; }

    /**
     * The canonical string key for this entity.
     * Delegates to RobotVariant.getName() — e.g. RobotVariant.Bunny3 → "bunny3".
     * Replaces LovelyConstant.VARIANT_{KEY}.
     */
    public String getVariantKey() {
        return variant.getName();
    } // getVariantKey()

    /**
     * The spawn item registry name — e.g. "bunny3_spawn".
     * Replaces LovelyConstant.{KEY}_SPAWN.
     */
    public String getSpawnItemKey() {
        return getVariantKey() + "_spawn";
    } // getSpawnItemKey()

    // -- Display --

    /** Human-readable name shown in wary/heal messages and UI. Drives the variant.lovelylib.{key} lang key. */
    public String getDisplayName() { return displayName; }

    // -- Mod Membership --

    public Set<ModTarget> getMods()              { return mods; }
    public boolean        isForMod(ModTarget mod) { return mods.contains(mod); }

    // -- Palette --

    /** True if this entity uses the full 16-color palette — omitting .palette() in the builder sets this. */
    public boolean              isFullPalette() { return palette.isEmpty(); }

    /** Active colors for a restricted palette. Empty when isFullPalette() is true. */
    public List<EntityTexture>  getPalette()    { return palette; }

    // -- Stats --

    public EntityStats getStats() { return stats; }

    // -- Renderer --

    /** Null when NativeRobotRenderer should be used (the common case for 6 of 8 variants). */
    public RendererFactory getRendererFactory()  { return rendererFactory; }
    public boolean         usesNativeRenderer()  { return rendererFactory == null; }

    // -- Feature Configurator --

    /**
     * Applies any variant-specific features to the family after the standard reload loop.
     * <p>
     * <b>Design:</b> Most variants need only the standard feature set. Variants with extras
     * (e.g. Kitsune's BoneVisibilityFeature) provide a BiConsumer here. The reload loop
     * calls this after standard features — it is a no-op for all other variants, keeping
     * the loop body uniform with no per-variant if-blocks.
     */
    public void applyFeatureConfigurator(RobotFamily family,
                                          SharedConfigs.EntityConfigData cfg) {
        if (featureConfigurator != null) featureConfigurator.accept(family, cfg);
    } // applyFeatureConfigurator()

    public boolean hasFeatureConfigurator() { return featureConfigurator != null; }

    // -- Builder --

    public static final class Builder {

        private final RobotVariant  variant;
        private String              displayName;
        private final Set<ModTarget> mods = EnumSet.noneOf(ModTarget.class);
        private List<EntityTexture> palette         = null;
        private EntityStats         stats           = null;
        private RendererFactory     rendererFactory = null;
        private BiConsumer<RobotFamily, SharedConfigs.EntityConfigData> featureConfigurator = null;

        public Builder(RobotVariant variant) {
            this.variant = variant;
        } // Constructor: Builder()

        public Builder displayName(String name) {
            this.displayName = name;
            return this;
        }

        public Builder forMod(ModTarget mod) {
            this.mods.add(mod);
            return this;
        }

        public Builder forMods(ModTarget... targets) {
            this.mods.addAll(Arrays.asList(targets));
            return this;
        }

        /**
         * Restricts the color palette to the given textures.
         * Omit this call entirely to use the full 16-color palette.
         */
        public Builder palette(EntityTexture... colors) {
            this.palette = colors.length == 0 ? null : Arrays.asList(colors);
            return this;
        }

        public Builder stats(int maxLevel, int baseHp, int baseAttack,
                             float attackSpeed, int baseDefense,
                             float baseToughness, float movementSpeed) {
            this.stats = new EntityStats(maxLevel, baseHp, baseAttack,
                                         attackSpeed, baseDefense, baseToughness, movementSpeed);
            return this;
        }

        public Builder stats(EntityStats stats) {
            this.stats = stats;
            return this;
        }

        /**
         * Specifies a custom renderer factory. Omit to use NativeRobotRenderer.
         * Pass a constructor reference: .renderer(KitsuneRenderer::new)
         */
        public Builder renderer(RendererFactory factory) {
            this.rendererFactory = factory;
            return this;
        }

        /**
         * Specifies per-variant feature additions applied after the standard reload loop.
         * Use for features that cannot be expressed uniformly across all variants —
         * e.g. Kitsune's BoneVisibilityFeature.
         */
        public Builder featureConfigurator(
                BiConsumer<RobotFamily, SharedConfigs.EntityConfigData> configurator) {
            this.featureConfigurator = configurator;
            return this;
        }

        public RobotEntityDefinition build() {
            if (displayName == null)
                throw new IllegalStateException("displayName is required for " + variant);
            if (mods.isEmpty())
                throw new IllegalStateException("at least one ModTarget is required for " + variant);
            if (stats == null)
                throw new IllegalStateException("stats are required for " + variant);
            return new RobotEntityDefinition(this);
        } // build()

    } // Class: Builder

} // Class: RobotEntityDefinition
