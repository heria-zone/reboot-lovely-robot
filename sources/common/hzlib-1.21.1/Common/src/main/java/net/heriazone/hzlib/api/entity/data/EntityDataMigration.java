package net.heriazone.hzlib.api.entity.data;

import net.heriazone.hzlib.framework.entity.data.*;
import net.heriazone.hzlib.framework.utils.Version;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * <p>Handles migration from legacy NBT format to new EntityData structure.</p>
 * <p>
 * <b>Architecture:</b> Provides backward compatibility for robots saved before
 * EntityData consolidation. Detects old format and migrates to new structure
 * transparently.
 * <p>
 * <b>Design Decision:</b> Migration is one-way (old → new). Once migrated,
 * entity saves in new format. This prevents data loss while enabling cleanup
 * of legacy code in future versions.
 * <p>
 * <b>Migration Strategy:</b>
 * <ol>
 * <li>Detect format by checking for "EntityData" compound</li>
 * <li>If missing, extract scattered fields from root NBT</li>
 * <li>Construct EntityData from extracted values</li>
 * <li>Validate migrated data</li>
 * <li>Return EntityData for entity to use</li>
 * </ol>
 */
public class EntityDataMigration { // TODO: This needs to migrate the current published mod

    // -- Legacy Field Names --

    // Combat Stats (from LovelyRobotEntity)
    private static final String LEGACY_LEVEL = "Level";
    private static final String LEGACY_EXP = "Exp";
    private static final String LEGACY_MAX_LEVEL = "MaxLevel";

    // Protection Stats (from LovelyRobotEntity)
    private static final String LEGACY_FIRE_PROTECTION = "FireProtection";
    private static final String LEGACY_FALL_PROTECTION = "FallProtection";
    private static final String LEGACY_BLAST_PROTECTION = "BlastProtection";
    private static final String LEGACY_PROJECTILE_PROTECTION = "ProjectileProtection";

    // Additional fields that might exist
    private static final String LEGACY_AUTO_ATTACK = "AutoAttack";
    private static final String LEGACY_BASE_X = "BaseX";
    private static final String LEGACY_BASE_Y = "BaseY";
    private static final String LEGACY_BASE_Z = "BaseZ";
    private static final String LEGACY_SITTING_POSE = "IsInSittingPose";
    private static final String LEGACY_CURRENT_HEALTH = "CurrentHealth";

    // -- Public Methods --

    /**
     * Migrates entity data from legacy format to new EntityData structure.
     * <p>
     * <b>Detection:</b> Checks for "EntityData" compound. If present, loads
     * directly. If missing, performs migration from scattered fields.
     * <p>
     * <b>Validation:</b> Migrated data is validated before return. Invalid
     * data results in default EntityData instance.
     *
     * @param nbt NBT compound from entity
     * @param version data version for compatibility
     * @return EntityData instance (migrated or new)
     * @throws NullPointerException if nbt is null
     */
    public static EntityData migrate(@NotNull CompoundTag nbt, @NotNull Version version) {
        Objects.requireNonNull(nbt, "NBT cannot be null");
        Objects.requireNonNull(version, "Version cannot be null");

        // Check if already in new format
        if (EntityData.hasEntityData(nbt)) {
            return EntityData.fromParentNBT(nbt);
        }

        // Perform migration from legacy format
        return migrateFromLegacy(nbt, version);
    } // migrate ()

    /**
     * Checks if NBT contains legacy format data.
     * <p>
     * <b>Detection:</b> Looks for legacy field names in root NBT.
     *
     * @param nbt NBT compound to check
     * @return true if legacy format detected
     * @throws NullPointerException if nbt is null
     */
    public static boolean isLegacyFormat(@NotNull CompoundTag nbt) {
        Objects.requireNonNull(nbt, "NBT cannot be null");

        // Check for absence of new format and presence of legacy fields
        return !EntityData.hasEntityData(nbt) &&
                (nbt.contains(LEGACY_LEVEL) ||
                        nbt.contains(LEGACY_FIRE_PROTECTION) ||
                        nbt.contains(LEGACY_EXP));
    } // isLegacyFormat ()

    // -- Private Migration Logic --

    /**
     * Performs actual migration from scattered legacy fields.
     * <p>
     * <b>Process:</b>
     * <ol>
     * <li>Extract combat stats from legacy fields</li>
     * <li>Extract protection stats from legacy fields</li>
     * <li>Calculate enchantment stats (not stored in legacy)</li>
     * <li>Construct EntityData</li>
     * <li>Validate and return</li>
     * </ol>
     *
     * @param nbt legacy NBT compound
     * @param version data version
     * @return migrated EntityData
     */
    private static EntityData migrateFromLegacy(@NotNull CompoundTag nbt, @NotNull Version version) {
        // Migrate combat stats
        CombatLevelStats combatStats = migrateCombatStats(nbt);

        // Migrate protection stats
        ProtectionStats protectionStats = migrateProtectionStats(nbt);

        // Enchantment stats are calculated from level, not stored
        // Entity will recalculate these after loading
        EnchantmentStats enchantmentStats = new EnchantmentStats();

        // Construct EntityData
        EntityData entityData = new EntityData(combatStats, protectionStats, enchantmentStats, version);

        // Validate migrated data
        if (!entityData.validate()) {
            // If validation fails, return default data
            // Log warning in production code
            return new EntityData();
        }

        return entityData;
    } // migrateFromLegacy ()

    /**
     * Migrates combat statistics from legacy NBT.
     * <p>
     * <b>Legacy Fields:</b>
     * <ul>
     * <li>Level → level</li>
     * <li>Exp → experience</li>
     * <li>MaxLevel → stored separately in LevelFeature</li>
     * </ul>
     *
     * @param nbt legacy NBT compound
     * @return CombatLevelStats with migrated values
     */
    private static CombatLevelStats migrateCombatStats(@NotNull CompoundTag nbt) {
        CombatLevelStats stats = new CombatLevelStats();

        // Migrate level
        if (nbt.contains(LEGACY_LEVEL)) {
            stats.setLevel(nbt.getInt(LEGACY_LEVEL));
        }

        // Migrate experience
        if (nbt.contains(LEGACY_EXP)) {
            stats.setExperience(nbt.getInt(LEGACY_EXP));
        }

        // MaxLevel is not stored in CombatLevelStats, it's in LevelFeature
        // Entity will handle this separately

        // HP, attack, defense are recalculated by entity based on level
        // No need to migrate these

        return stats;
    } // migrateCombatStats ()

    /**
     * Migrates protection statistics from legacy NBT.
     * <p>
     * <b>Legacy Fields:</b>
     * <ul>
     * <li>FireProtection → fireProtection</li>
     * <li>FallProtection → fallProtection</li>
     * <li>BlastProtection → blastProtection</li>
     * <li>ProjectileProtection → projectileProtection</li>
     * </ul>
     *
     * @param nbt legacy NBT compound
     * @return ProtectionStats with migrated values
     */
    private static ProtectionStats migrateProtectionStats(@NotNull CompoundTag nbt) {
        ProtectionStats stats = new ProtectionStats();

        // Migrate fire protection
        if (nbt.contains(LEGACY_FIRE_PROTECTION)) {
            stats.setFireProtection(nbt.getInt(LEGACY_FIRE_PROTECTION));
        }

        // Migrate fall protection
        if (nbt.contains(LEGACY_FALL_PROTECTION)) {
            stats.setFallProtection(nbt.getInt(LEGACY_FALL_PROTECTION));
        }

        // Migrate blast protection
        if (nbt.contains(LEGACY_BLAST_PROTECTION)) {
            stats.setBlastProtection(nbt.getInt(LEGACY_BLAST_PROTECTION));
        }

        // Migrate projectile protection
        if (nbt.contains(LEGACY_PROJECTILE_PROTECTION)) {
            stats.setProjectileProtection(nbt.getInt(LEGACY_PROJECTILE_PROTECTION));
        }

        return stats;
    } // migrateProtectionStats ()

    /**
     * Migrates additional entity fields that aren't part of EntityData.
     * <p>
     * <b>Use Case:</b> Some fields (AutoAttack, Base coordinates, etc.) are
     * stored separately from EntityData. This method extracts them for
     * entity to handle.
     * <p>
     * <b>Design Decision:</b> These fields remain outside EntityData as they
     * represent entity state rather than stats.
     *
     * @param nbt legacy NBT compound
     * @return CompoundTag with additional fields
     */
    public static CompoundTag migrateAdditionalFields(@NotNull CompoundTag nbt) {
        Objects.requireNonNull(nbt, "NBT cannot be null");

        CompoundTag additional = new CompoundTag();

        // Migrate auto-attack flag
        if (nbt.contains(LEGACY_AUTO_ATTACK)) {
            additional.putBoolean(LEGACY_AUTO_ATTACK, nbt.getBoolean(LEGACY_AUTO_ATTACK));
        }

        // Migrate base coordinates
        if (nbt.contains(LEGACY_BASE_X)) {
            additional.putFloat(LEGACY_BASE_X, nbt.getFloat(LEGACY_BASE_X));
        }
        if (nbt.contains(LEGACY_BASE_Y)) {
            additional.putFloat(LEGACY_BASE_Y, nbt.getFloat(LEGACY_BASE_Y));
        }
        if (nbt.contains(LEGACY_BASE_Z)) {
            additional.putFloat(LEGACY_BASE_Z, nbt.getFloat(LEGACY_BASE_Z));
        }

        // Migrate sitting pose
        if (nbt.contains(LEGACY_SITTING_POSE)) {
            additional.putBoolean(LEGACY_SITTING_POSE, nbt.getBoolean(LEGACY_SITTING_POSE));
        }

        // Migrate current health
        if (nbt.contains(LEGACY_CURRENT_HEALTH)) {
            additional.putFloat(LEGACY_CURRENT_HEALTH, nbt.getFloat(LEGACY_CURRENT_HEALTH));
        }

        return additional;
    } // migrateAdditionalFields ()

    /**
     * Creates migration report for debugging.
     * <p>
     * <b>Use Case:</b> Log migration details for troubleshooting.
     *
     * @param nbt legacy NBT compound
     * @return human-readable migration report
     * @throws NullPointerException if nbt is null
     */
    public static String createMigrationReport(@NotNull CompoundTag nbt) {
        Objects.requireNonNull(nbt, "NBT cannot be null");

        StringBuilder report = new StringBuilder();
        report.append("EntityData Migration Report:\n");
        report.append("  Format: ").append(isLegacyFormat(nbt) ? "Legacy" : "New").append("\n");

        if (isLegacyFormat(nbt)) {
            report.append("  Legacy Fields Found:\n");
            if (nbt.contains(LEGACY_LEVEL)) {
                report.append("    - Level: ").append(nbt.getInt(LEGACY_LEVEL)).append("\n");
            }
            if (nbt.contains(LEGACY_EXP)) {
                report.append("    - Exp: ").append(nbt.getInt(LEGACY_EXP)).append("\n");
            }
            if (nbt.contains(LEGACY_FIRE_PROTECTION)) {
                report.append("    - FireProtection: ").append(nbt.getInt(LEGACY_FIRE_PROTECTION)).append("\n");
            }
            if (nbt.contains(LEGACY_FALL_PROTECTION)) {
                report.append("    - FallProtection: ").append(nbt.getInt(LEGACY_FALL_PROTECTION)).append("\n");
            }
            if (nbt.contains(LEGACY_BLAST_PROTECTION)) {
                report.append("    - BlastProtection: ").append(nbt.getInt(LEGACY_BLAST_PROTECTION)).append("\n");
            }
            if (nbt.contains(LEGACY_PROJECTILE_PROTECTION)) {
                report.append("    - ProjectileProtection: ").append(nbt.getInt(LEGACY_PROJECTILE_PROTECTION)).append("\n");
            }
        }

        return report.toString();
    } // createMigrationReport ()

} // Class: EntityDataMigration
