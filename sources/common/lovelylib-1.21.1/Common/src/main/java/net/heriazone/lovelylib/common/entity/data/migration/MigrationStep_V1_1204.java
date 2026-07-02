package net.heriazone.lovelylib.common.entity.data.migration;

import net.heriazone.hzlib.api.nbt.DataCompound;
import net.heriazone.hzlib.api.nbt.McVersionProvider;
import net.heriazone.hzlib.api.nbt.MigrationStep;
import net.heriazone.hzlib.api.nbt.NbtAdapterFactory;
import net.heriazone.lovelylib.common.entity.enums.EntityTexture;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Migrates the published 1.20.4 Reboot save format to the current 1.21.1 format.<p>
 * <p>
 * <b>Context:</b> The 1.20.4 archive (rlovelyr-1.20.4 Fabric) wrote all fields flat to
 * the entity root. {@code TextureID} was an int (0–15). An {@code EntityData} sub-compound
 * was written but never read back (the read path was commented out), so it contains only
 * partial data — specifically it cannot be trusted. This step ignores any partial
 * {@code EntityData} compound and rebuilds it from the flat root fields.
 * <p>
 * <b>Receives:</b> Root compound with flat 1.20.4 keys and optional orphaned
 * {@code EntityData} compound.
 * <p>
 * <b>Produces:</b> Root compound with {@code TextureVariant} string key at root and
 * all stats in a complete {@code EntityData} sub-compound with version headers.
 * <p>
 * <b>Idempotency:</b> If {@code EntityData.McVersion} is already present (fully migrated
 * by this step or higher), returns unchanged.
 */
public final class MigrationStep_V1_1204 implements MigrationStep {

    @Override
    public @NotNull String id() { return "V1_1204"; }

    @Override
    public @NotNull DataCompound migrate(@NotNull DataCompound root) {
        // Already fully migrated — McVersion present means this step already ran
        if (root.hasCompound("EntityData") &&
                root.getCompound("EntityData").has("McVersion")) return root;

        // Migrate TextureID (int) → TextureVariant (string key) at root
        // The 1.20.4 build stored the robot type key in the "EntityData.type" compound
        // (written but never read). We can infer the family prefix from TextureVariant
        // if it was set by NativeEntity (possible in partially migrated saves).
        if (root.has("TextureID") && !root.has("TextureVariant")) {
            int textureId    = root.getInt("TextureID", 0);
            EntityTexture tex = EntityTexture.byId(textureId);

            // IDs 17–25 are extended-palette textures for restricted-palette robots
            // (Prime, Hyperion, Empyrium). byId() resolves them correctly since we
            // appended the enum entries above ID 16 without shifting existing entries.
            // COLD_GOLD (ID 19) is shared by both Prime and Empyrium — disambiguate
            // using the robot key stored in the partial EntityData compound.
            String textureName = (tex != null && tex != EntityTexture.RANDOM)
                    ? tex.Name() : EntityTexture.WHITE.Name();

            // Try to get the robot family key from the partial EntityData compound if available
            String robotKey = "";
            if (root.hasCompound("EntityData")) {
                robotKey = root.getCompound("EntityData").getString("type", "");
            }
            // If no family key found, use bare color name — Phase 3 MigrationChain
            // in LovelyLib will have added the family prefix by this point if known
            String variantKey = robotKey.isEmpty() ? textureName : robotKey + "_" + textureName;
            root.putString("TextureVariant", variantKey);
        }

        // Discard the orphaned/partial EntityData compound — rebuild from flat root fields
        root.remove("EntityData");

        DataCompound entityData = NbtAdapterFactory.createEmpty();
        entityData.putString("SchemaVersion", "1.0.0");
        entityData.putString("McVersion", McVersionProvider.current());

        // Direct-copy fields — same PascalCase keys as 1.20.4
        copyIntField(root,  entityData, "Level",                1);
        copyIntField(root,  entityData, "Exp",                  0);
        copyIntField(root,  entityData, "MaxLevel",             0);
        copyIntField(root,  entityData, "FireProtection",       0);
        copyIntField(root,  entityData, "FallProtection",       0);
        copyIntField(root,  entityData, "BlastProtection",      0);
        copyIntField(root,  entityData, "ProjectileProtection", 0);
        copyBoolField(root, entityData, "AutoAttack",           true);
        copyFloatField(root, entityData, "BaseX",               0f);
        copyFloatField(root, entityData, "BaseY",               0f);
        copyFloatField(root, entityData, "BaseZ",               0f);

        // Fields not present in Gen1/Gen2 — added in 1.20.4
        copyBoolField(root, entityData, "IsInSittingPose",      false);
        copyFloatField(root, entityData, "CurrentHealth",       20f,
                v -> v > 0f ? v : 20f); // guard against corrupt zero-health saves

        // Remove migrated flat keys from root
        removeAllMigratedKeys(root);

        root.put("EntityData", entityData);
        return root;
    } // migrate ()

    // -- Private Helpers --

    private static void copyIntField(DataCompound src, DataCompound dst, String key, int def) {
        dst.putInt(key, src.getInt(key, def));
    }

    private static void copyBoolField(DataCompound src, DataCompound dst, String key, boolean def) {
        dst.putBoolean(key, src.getBoolean(key, def));
    }

    private static void copyFloatField(DataCompound src, DataCompound dst, String key, float def) {
        dst.putFloat(key, src.getFloat(key, def));
    }

    /** Variant that applies a sanitiser function to the float value. */
    @FunctionalInterface
    private interface FloatSanitiser { float apply(float v); }

    private static void copyFloatField(DataCompound src, DataCompound dst,
                                        String key, float def, FloatSanitiser sanitiser) {
        float value = src.getFloat(key, def);
        dst.putFloat(key, sanitiser.apply(value));
    }

    /** Removes all root-level flat keys that have been moved into EntityData. */
    private static void removeAllMigratedKeys(DataCompound root) {
        for (String key : new String[]{
                "Level", "Exp", "MaxLevel",
                "FireProtection", "FallProtection", "BlastProtection", "ProjectileProtection",
                "AutoAttack", "BaseX", "BaseY", "BaseZ",
                "IsInSittingPose", "CurrentHealth", "TextureID"}) {
            root.remove(key);
        }
    } // removeAllMigratedKeys ()

} // Class: MigrationStep_V1_1204
