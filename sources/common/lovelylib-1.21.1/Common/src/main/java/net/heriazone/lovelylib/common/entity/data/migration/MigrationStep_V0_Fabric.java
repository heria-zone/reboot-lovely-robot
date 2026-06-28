package net.heriazone.lovelylib.common.entity.data.migration;

import net.heriazone.hzlib.api.nbt.DataCompound;
import net.heriazone.hzlib.api.nbt.MigrationStep;
import net.heriazone.lovelylib.common.entity.enums.EntityTexture;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>Migrates Gen1-Fabric saves to the Gen4 flat PascalCase format.<p>
 * <p>
 * <b>Context:</b> The original Fabric build (archive 1.16.X/rlovelyr-fabric) used
 * snake_case keys and stored robot type as a locale-translatable string
 * (e.g. {@code "entity.rlovelyr.bunny"}), making saves locale-dependent.
 * The Gen1-Forge build used PascalCase keys but the same int {@code TextureID}.
 * This step normalises the Fabric format to the Gen1-Forge/Gen2 PascalCase layout
 * so that {@link MigrationStep_V0_Forge} can process both uniformly.
 * <p>
 * <b>Locale table:</b> The {@code LOCALE_TO_STABLE_KEY} map is the permanent record
 * of every entity type that shipped in the Gen1-Fabric build. It must remain
 * append-only — never remove an entry.
 */
public final class MigrationStep_V0_Fabric implements MigrationStep {

    // -- Locale-to-stable-key lookup --
    // Every entity type that shipped in the Gen1-Fabric build.
    // Type was stored as the full translatable string (e.g. "entity.rlovelyr.bunny").
    // Values are the stable family keys used in all subsequent formats.

    private static final Map<String, String> LOCALE_TO_STABLE_KEY = new LinkedHashMap<>();

    static {
        LOCALE_TO_STABLE_KEY.put("entity.rlovelyr.bunny",   "bunny");
        LOCALE_TO_STABLE_KEY.put("entity.rlovelyr.bunny2",  "bunny2");
        LOCALE_TO_STABLE_KEY.put("entity.rlovelyr.dragon",  "dragon");
        LOCALE_TO_STABLE_KEY.put("entity.rlovelyr.honey",   "honey");
        LOCALE_TO_STABLE_KEY.put("entity.rlovelyr.kitsune", "kitsune");
        LOCALE_TO_STABLE_KEY.put("entity.rlovelyr.neko",    "neko");
        LOCALE_TO_STABLE_KEY.put("entity.rlovelyr.vanilla", "vanilla");
        // Legacy variant keys also seen without full path in some builds
        LOCALE_TO_STABLE_KEY.put("bunny",   "bunny");
        LOCALE_TO_STABLE_KEY.put("bunny2",  "bunny2");
        LOCALE_TO_STABLE_KEY.put("dragon",  "dragon");
        LOCALE_TO_STABLE_KEY.put("honey",   "honey");
        LOCALE_TO_STABLE_KEY.put("kitsune", "kitsune");
        LOCALE_TO_STABLE_KEY.put("neko",    "neko");
        LOCALE_TO_STABLE_KEY.put("vanilla", "vanilla");
    }

    // -- MigrationStep --

    @Override
    public @NotNull String id() { return "V0_Fabric"; }

    /**
     * Detects Gen1-Fabric format by the presence of the snake_case {@code "type"} key
     * or by absence of the {@code "Variant"} PascalCase key alongside a {@code "color"}
     * int key. Normalises to Gen1-Forge PascalCase layout.
     * <p>
     * <b>Idempotency:</b> If {@code "Variant"} (PascalCase) is already present and
     * {@code "type"} (snake_case) is absent, this step is a no-op.
     */
    @Override
    public @NotNull DataCompound migrate(@NotNull DataCompound root) {
        boolean hasFabricType  = root.has("type");
        boolean hasForgeVariant = root.has("Variant");

        // Already in PascalCase format or no locale type key present — skip
        if (!hasFabricType && hasForgeVariant) return root;
        if (!hasFabricType && !root.has("color")) return root; // no recognisable Gen1-Fabric marker

        // Resolve stable key from locale string
        String localeType = root.getString("type", "");
        String stableKey  = LOCALE_TO_STABLE_KEY.get(localeType);

        if (stableKey == null) {
            // Unknown locale type — log error, fall back to raw string as best effort
            if (!localeType.isEmpty()) {
                LoggerFactory.getLogger("HZLib-V0_Fabric").error(
                    "[HZLib] Unknown Gen1-Fabric locale type '{}' — cannot resolve to stable key. " +
                    "Add it to MigrationStep_V0_Fabric.LOCALE_TO_STABLE_KEY.", localeType);
            }
            stableKey = localeType;
        }

        // Write Forge-style Variant (resolved stable key) and TextureID from color int
        if (!stableKey.isEmpty()) root.putString("Variant", stableKey);

        int colorId = root.getInt("color", 0);
        root.putInt("TextureID", colorId);

        // Rename snake_case keys to PascalCase equivalents
        renameKey    (root, "level",                  "Level");
        renameKey    (root, "max_level",              "MaxLevel");
        renameKey    (root, "exp",                    "Exp");
        renameBoolKey(root, "auto_attack",            "AutoAttack");
        renameKey    (root, "fire_protection",        "FireProtection");
        renameKey    (root, "fall_protection",        "FallProtection");
        renameKey    (root, "blast_protection",       "BlastProtection");
        renameKey    (root, "projectile_protection",  "ProjectileProtection");
        renameFloatKey(root, "base_x",                "BaseX");
        renameFloatKey(root, "base_y",                "BaseY");
        renameFloatKey(root, "base_z",                "BaseZ");

        // Remove consumed Fabric keys
        root.remove("type");
        root.remove("color");

        return root;
    } // migrate ()

    // -- Private Helpers --

    /**
     * Moves a field from one key name to another, inferring its type from the
     * DataCompound presence flags. Called only when we know the key exists
     * ({@code root.has(from)} already verified by caller).
     * <p>
     * Gen1-Fabric stored integers and booleans; strings were only used for
     * {@code "type"} and {@code "custom_name"}, both handled separately.
     * All numeric fields are safely treated as INT here.
     */
    private static void renameKey(DataCompound root, String from, String to) {
        if (!root.has(from) || root.has(to)) return;
        // All Gen1-Fabric numeric fields are ints; booleans use the same storage slot
        // putInt / getInt covers both (boolean stored as 1/0 in NBT byte tag under CompoundTag).
        // For fields we know are boolean (auto_attack), getBoolean is used explicitly in the
        // caller loop below, but we keep a safe int-copy path here as the general case.
        root.putInt(to, root.getInt(from, 0));
        root.remove(from);
    } // renameKey ()

    /** Explicitly renames a boolean field, preserving its semantic type. */
    private static void renameBoolKey(DataCompound root, String from, String to) {
        if (!root.has(from) || root.has(to)) return;
        root.putBoolean(to, root.getBoolean(from, false));
        root.remove(from);
    } // renameBoolKey ()

    /** Explicitly renames a float field. */
    private static void renameFloatKey(DataCompound root, String from, String to) {
        if (!root.has(from) || root.has(to)) return;
        root.putFloat(to, root.getFloat(from, 0f));
        root.remove(from);
    } // renameFloatKey ()

} // Class: MigrationStep_V0_Fabric
