package net.heriazone.lovelylib.common.entity.data.migration;

import net.heriazone.hzlib.api.nbt.DataCompound;
import net.heriazone.hzlib.api.nbt.McVersionProvider;
import net.heriazone.hzlib.api.nbt.MigrationStep;
import net.heriazone.hzlib.api.nbt.NbtAdapterFactory;
import net.heriazone.lovelylib.common.entity.enums.EntityTexture;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Migrates Gen1-Forge and Gen2 saves to the current structured format.<p>
 * <p>
 * <b>Context:</b> Gen1-Forge (archive 1.16.X/rlovelyr-1.16.5 Forge) and Gen2
 * (1.18.x single Forge build) both wrote all fields flat to the entity root with
 * PascalCase keys. {@code TextureID} was stored as an int (0–15). No {@code EntityData}
 * compound, no {@code SchemaVersion}, no {@code McVersion}.
 * <p>
 * <b>Receives:</b> Root compound with flat PascalCase keys — either directly from the
 * save (Gen1-Forge, Gen2) or normalised by {@link MigrationStep_V0_Fabric} first.
 * <p>
 * <b>Produces:</b> Root compound with {@code TextureVariant} string key at root and
 * all stats wrapped into an {@code EntityData} sub-compound with {@code SchemaVersion}
 * and {@code McVersion} headers, ready for schema read.
 * <p>
 * <b>Idempotency:</b> If {@code EntityData.SchemaVersion} already exists, this step
 * returns the root unchanged.
 */
public final class MigrationStep_V0_Forge implements MigrationStep {

    @Override
    public @NotNull String id() { return "V0_Forge"; }

    @Override
    public @NotNull DataCompound migrate(@NotNull DataCompound root) {
        // Already structured — skip
        if (root.hasCompound("EntityData") &&
                root.getCompound("EntityData").has("SchemaVersion")) return root;

        // Migrate TextureID (int) → TextureVariant (string key) at root
        // The Variant key (stable robot type key, e.g. "bunny") was set by V0_Fabric or was
        // already a stable string in Gen1-Forge. Use it as prefix for the texture key.
        if (root.has("TextureID") && !root.has("TextureVariant")) {
            int textureId    = root.getInt("TextureID", 0);
            String robotKey  = root.getString("Variant", "");
            EntityTexture tex = EntityTexture.byId(textureId);
            String textureName = (tex != null && tex != EntityTexture.RANDOM)
                    ? tex.Name() : EntityTexture.WHITE.Name();
            // Entity-specific key: "bunny_white", "kitsune_magenta", etc.
            String variantKey = robotKey.isEmpty() ? textureName : robotKey + "_" + textureName;
            root.putString("TextureVariant", variantKey);
        }

        // Wrap all stat fields into EntityData sub-compound
        DataCompound entityData = NbtAdapterFactory.createEmpty();
        entityData.putString("SchemaVersion", "1.0.0");
        entityData.putString("McVersion", McVersionProvider.current());

        moveIntField(root, entityData,  "Level",                 1);
        moveIntField(root, entityData,  "Exp",                   0);
        moveIntField(root, entityData,  "MaxLevel",              0);
        moveIntField(root, entityData,  "FireProtection",        0);
        moveIntField(root, entityData,  "FallProtection",        0);
        moveIntField(root, entityData,  "BlastProtection",       0);
        moveIntField(root, entityData,  "ProjectileProtection",  0);
        moveBoolField(root, entityData, "AutoAttack",            true);
        moveFloatField(root, entityData, "BaseX",                0f);
        moveFloatField(root, entityData, "BaseY",                0f);
        moveFloatField(root, entityData, "BaseZ",                0f);

        root.put("EntityData", entityData);
        return root;
    } // migrate ()

    // -- Private Helpers --

    private static void moveIntField(DataCompound src, DataCompound dst, String key, int defaultVal) {
        dst.putInt(key, src.getInt(key, defaultVal));
        src.remove(key);
    }

    private static void moveBoolField(DataCompound src, DataCompound dst, String key, boolean defaultVal) {
        dst.putBoolean(key, src.getBoolean(key, defaultVal));
        src.remove(key);
    }

    private static void moveFloatField(DataCompound src, DataCompound dst, String key, float defaultVal) {
        dst.putFloat(key, src.getFloat(key, defaultVal));
        src.remove(key);
    }

} // Class: MigrationStep_V0_Forge
