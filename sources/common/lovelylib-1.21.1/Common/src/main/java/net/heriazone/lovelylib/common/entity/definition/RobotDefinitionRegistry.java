package net.heriazone.lovelylib.common.entity.definition;

import net.heriazone.lovelylib.common.configs.SharedConfigs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Central registry for all robot entity definitions — the single source of truth
 * for all variant metadata.
 * <p>
 * <b>Architecture:</b> Replaces five previously scattered per-entity data locations:
 * <ul>
 *   <li>LovelyConstant per-entity constants and variant arrays</li>
 *   <li>SharedConfigs.Common per-entity static fields</li>
 *   <li>ConfigAccessLayer.getDefaultEntityConfig() switch statement</li>
 *   <li>LovelyIdentifier.getTranslation() switch statement</li>
 *   <li>LegacyConfigs / RebootConfigs Default.put() static entries</li>
 * </ul>
 * <p>
 * <b>Initialization Order:</b> LegacyRobotDefinitions.register() and
 * RebootRobotDefinitions.register() must complete before any consumer reads from
 * this registry. Lovely.onInitialize() is the guaranteed entry point — it must be
 * called as the very first statement in every loader entry point constructor.
 * <p>
 * <b>Fail-Fast:</b> seal() is called after all definitions are registered. Any late
 * registration throws immediately. Any access to an unregistered variant throws with
 * a clear message rather than silently returning a wrong value — structurally
 * eliminating the "Vanilla fallback" class of bugs from ADR-023 §1.2.
 */
public final class RobotDefinitionRegistry {

    // -- Constructor --

    private RobotDefinitionRegistry() {}

    // -- State --

    /** Insertion-ordered for deterministic iteration across all consumers. */
    private static final LinkedHashMap<RobotVariant, RobotEntityDefinition> REGISTRY
        = new LinkedHashMap<>();

    private static boolean sealed = false;

    // -- Registration --

    /**
     * Registers a definition. Must be called before seal().
     *
     * @throws IllegalStateException if the registry is already sealed or the variant is a duplicate
     */
    public static void register(RobotEntityDefinition def) {
        if (sealed)
            throw new IllegalStateException(
                "RobotDefinitionRegistry is sealed — cannot register " + def.getVariant()
                + " after Lovely.onInitialize() has completed.");
        if (REGISTRY.containsKey(def.getVariant()))
            throw new IllegalStateException(
                "Duplicate registration for variant: " + def.getVariant());
        REGISTRY.put(def.getVariant(), def);
    } // register()

    /**
     * Seals the registry. Must be called after all definition files have registered.
     * Prevents accidental late registration from any loader or third-party code.
     */
    public static void seal() {
        sealed = true;
    } // seal()

    public static boolean isSealed() { return sealed; }

    // -- Query API --

    /**
     * Returns the definition for a variant.
     *
     * @throws NoSuchElementException if the variant was not registered before seal()
     */
    public static RobotEntityDefinition get(RobotVariant variant) {
        RobotEntityDefinition def = REGISTRY.get(variant);
        if (def == null)
            throw new NoSuchElementException(
                "No RobotEntityDefinition registered for: " + variant
                + ". Was LegacyRobotDefinitions.register() called before seal()?");
        return def;
    } // get()

    /** All definitions registered for a given mod, in registration order. */
    public static Collection<RobotEntityDefinition> getForMod(ModTarget mod) {
        List<RobotEntityDefinition> result = new ArrayList<>();
        for (RobotEntityDefinition def : REGISTRY.values()) {
            if (def.isForMod(mod)) result.add(def);
        }
        return Collections.unmodifiableList(result);
    } // getForMod()

    /** All registered definitions, in registration order. */
    public static Collection<RobotEntityDefinition> getAll() {
        return Collections.unmodifiableCollection(REGISTRY.values());
    } // getAll()

    /**
     * All variant key strings for a given mod.
     * Replaces LovelyConstant.LEGACY_VARIANTS, REBOOT_VARIANTS, and ALL_VARIANTS.
     * Also replaces the LEGACY_VARIANTS reference in Fabric-side config loops.
     */
    public static String[] getVariantKeysForMod(ModTarget mod) {
        return getForMod(mod).stream()
            .map(RobotEntityDefinition::getVariantKey)
            .toArray(String[]::new);
    } // getVariantKeysForMod()

    public static boolean isRegistered(RobotVariant variant) {
        return REGISTRY.containsKey(variant);
    } // isRegistered()

    // -- Derived lookups (replace removed switch statements) --

    /**
     * Returns the EntityConfigData defaults for a variant.
     * Replaces ConfigAccessLayer.getDefaultEntityConfig() switch.
     */
    public static SharedConfigs.EntityConfigData getDefaultConfig(RobotVariant variant) {
        return get(variant).getStats().toEntityConfigData();
    } // getDefaultConfig()

    /**
     * Returns the variant key string.
     * Delegates to RobotEntityDefinition.getVariantKey() → RobotVariant.getName().
     */
    public static String getVariantKey(RobotVariant variant) {
        return get(variant).getVariantKey();
    } // getVariantKey()

    /**
     * Returns the lovelylib lang key for a variant's display name.
     * e.g. "variant.lovelylib.bunny3"
     * Used by LovelyIdentifier.getVariantTranslation().
     */
    public static String getDisplayTranslationKey(RobotVariant variant) {
        return "variant.lovelylib." + get(variant).getVariantKey();
    } // getDisplayTranslationKey()

} // Class: RobotDefinitionRegistry
