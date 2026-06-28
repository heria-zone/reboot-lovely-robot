# ADR 019: Entity Data Pipeline

**Status**: Revised — Proposed  
**Date**: 2026-06-26  
**Supersedes**: 2026-06-25 draft  
**Decision Makers**: Serge Maia 
**Scope**: HZLib Common, LovelyLib Common, Monsters & Girls (future)

---

## Context

### The Problem

Entity NBT persistence in this codebase has evolved through five distinct generations without a unifying
contract. Each generation added fields independently, producing a sprawl of scattered string keys, format
inconsistencies, and migration gaps. The published 1.20.x mod saves cannot be cleanly loaded by 1.21.1 —
`EntityDataMigration` contains an explicit `TODO` acknowledging this. Beyond the migration gap, the
architectural problem is structural: there is no single place that declares what data an entity owns.
Fields are discovered by reading `addAdditionalSaveData` line by line.

Additionally, the codebase targets MC versions ranging from 1.7.10 to 1.21.1. The NBT API changed
substantially across that span — class names, method signatures, and UUID persistence all differ between
versions. Any pipeline that couples directly to the MC-version-specific NBT API cannot be shared across
the supported version range without modification.

---

### Full Generation History

#### Generation 1 — Original LovelyRobot (archive: 1.16.x rlovelyr-forge / rlovelyr-fabric)

Two separate key namespaces existed simultaneously — the Forge and Fabric builds used different string
keys for the same fields:

| Concept     | Forge keys                    | Fabric keys                         |
|-------------|-------------------------------|-------------------------------------|
| Variant     | `"Variant"` (string)          | `"type"` (string, translatable!)    |
| Texture     | `"TextureID"` (int)           | `"color"` (int)                     |
| Level       | `"Level"` / `"MaxLevel"`      | `"level"` / `"max_level"`           |
| Exp         | `"Exp"`                       | `"exp"`                             |
| AutoAttack  | `"AutoAttack"`                | `"auto_attack"`                     |
| Fire prot.  | `"FireProtection"`            | `"fire_protection"`                 |
| Base pos    | `"BaseX/Y/Z"`                 | `"base_x/y/z"`                      |

**Pattern**: All fields written flat to the entity root NBT. No versioning, no container compound, no
validation. The Fabric build stored the variant as a translatable string (`"entity.rlovelyr.bunny"`)
rather than a stable key — making it locale-dependent and unmigrateable without a hardcoded lookup table.

#### Generation 2 — 1.18.x single Forge build

Standardised to PascalCase Forge-style keys across the board. Added `LovelyRobotID` string constants to
centralise key literals. Introduced `RobotCoreItem.appendHoverText` with all five tooltip fields. No
versioning, no container.

#### Generation 3 — 1.20.4 Reboot (archive: rlovelyr-1.20.4 Fabric)

First attempt at structure. Introduced a nested `"EntityData"` compound with a `"VersionNBT"` string
inside it. Defined `IReadWriteNBT` interface with `writeToNBT(NbtCompound)` / `readFromNBT(NbtCompound,
Version)`. However:
- The `readCustomDataFromNbt` method **commented out** the `EntityData` read block — it wrote the
  structure but never read it back, making it dead code.
- Surface fields (`"TextureID"`, `"State"`, `"Notification"`) remained flat on the root.
- The `writeToNBT` / `readFromNBT` pair in `InternalEntity` duplicated the same three flat fields — no
  actual structured data was stored inside the compound.

#### Generation 3b — 1.20.4 Monsters & Girls (archive)

Independent evolution alongside the robot mod. Used integer IDs for texture, model, and animator
(`"TextureID"`, `"ModelID"`, `"AnimatorID"`). Added `"Belly"` (boolean), `"Plant"`, `"Sound"`,
`"Notification"` — all flat on root. No versioning, no `IReadWriteNBT`.

**Critical design flaw**: Belly state was encoded as a texture ID offset rather than a separate field.
Apple/feather interactions incremented/decremented the texture ID, treating belly levels as extra texture
entries. This made belly state inseparable from appearance state in the save format.

#### Generation 4 — 1.21.1 HZLib current (partial)

`EntityData` container class introduced, wrapping `CombatLevelStats`, `ProtectionStats`,
`EnchantmentStats` each with dedicated `*NBT` serialiser classes implementing `IReadWriteNBT`:

```
root NBT
├── TextureVariant   (String, SynchedEntityData)
├── ModelVariant     (String, SynchedEntityData)
├── AnimatorVariant  (String, SynchedEntityData)
├── State            (int, SynchedEntityData)
├── Notification     (boolean, SynchedEntityData)
└── EntityData       (compound)
    ├── DataVersion
    ├── CombatLevelStats { Level, Experience, CurrentHp, MaxHp, Attack, Defense }
    ├── ProtectionStats  { FireProtection, FallProtection, BlastProtection, ProjectileProtection }
    └── EnchantmentStats { LootingLevel, SharpnessLevel, KnockbackLevel }
```

**Current state in `RobotEntity.addAdditionalSaveData`**: The `EntityData` compound is **not used**.
`RobotEntity` writes all fields flat to the root — `"Level"`, `"Exp"`, `"MaxLevel"`,
`"FireProtection"`, etc. — bypassing the `EntityData` infrastructure entirely. The `EntityDataMigration`
class exists but is wired to nothing. The structured layer and the actual save layer are disconnected.

#### Summary: Fields across generations

| Field            | Gen1-Forge           | Gen1-Fabric           | Gen2          | Gen3-Robot     | Gen3-MG         | Gen4                      |
|------------------|----------------------|-----------------------|---------------|----------------|-----------------|---------------------------|
| Variant/type     | `Variant` (str)      | `type` (locale str)   | `Variant`     | —              | —               | `TextureVariant` (str key)|
| Texture          | `TextureID` (int)    | `color` (int)         | `TextureID`   | `TextureID`    | `TextureID`     | `TextureVariant` (str key)|
| Model            | —                    | —                     | —             | implicit       | `ModelID` (int) | `ModelVariant` (str key)  |
| Animator         | —                    | —                     | —             | implicit       | `AnimatorID`    | `AnimatorVariant` (str key)|
| State            | `State` (int)        | `State` (int)         | `State`       | `State`        | `State`         | `State` (int)             |
| AutoAttack       | `AutoAttack`         | `auto_attack`         | `AutoAttack`  | `AutoAttack`   | —               | `AutoAttack`              |
| Level            | `Level`              | `level`               | `Level`       | `Level`        | —               | `Level` (flat, bypassed)  |
| Exp              | `Exp`                | `exp`                 | `Exp`         | `Exp`          | —               | `Exp` (flat, bypassed)    |
| MaxLevel         | `MaxLevel`           | `max_level`           | `MaxLevel`    | `MaxLevel`     | —               | `MaxLevel` (flat)         |
| Fire prot.       | `FireProtection`     | `fire_protection`     | `FireProt.`   | `FireProt.`    | —               | `FireProtection` (flat)   |
| Fall prot.       | `FallProtection`     | `fall_protection`     | `FallProt.`   | `FallProt.`    | —               | `FallProtection` (flat)   |
| Blast prot.      | `BlastProtection`    | `blast_protection`    | `BlastProt.`  | `BlastProt.`   | —               | `BlastProtection` (flat)  |
| Proj. prot.      | `ProjectileProtection`| `projectile_protection`| `Proj.Prot.`| `Proj.Prot.`   | —               | `ProjectileProtection`    |
| Base pos         | `BaseX/Y/Z`          | `base_x/y/z`          | `BaseX/Y/Z`   | `BaseX/Y/Z`    | —               | `BaseX/Y/Z` (flat)        |
| Health           | —                    | —                     | —             | `CurrentHealth`| —               | `CurrentHealth` (flat)    |
| Sitting          | —                    | —                     | —             | `IsInSittingPose`| —             | `IsInSittingPose` (flat)  |
| Notification     | —                    | —                     | `Notification`| `Notification` | `Notification`  | `Notification` (flat)     |
| Belly            | —                    | —                     | —             | —              | `Belly` / offset| `BellyLevel` (int)        |
| Plant toggle     | —                    | —                     | —             | —              | `Plant`         | `PlantingEnabled`         |
| Sound toggle     | —                    | —                     | —             | —              | `Sound`         | `SoundEnabled`            |
| Standby ticks    | —                    | —                     | —             | —              | —               | `StandbyTicks` (flat)     |
| Overlay slots    | —                    | —                     | —             | —              | —               | SynchedEntityData only    |

---

### Identified Problems

**1 — Disconnected infrastructure**: `EntityData`, `CombatStatsNBT`, `ProtectionStatsNBT`,
`EnchantmentStatsNBT` exist but `RobotEntity` writes everything flat, bypassing the entire structure.
The two layers are never connected.

**2 — No schema declaration**: There is no single place to look up what data a given entity family owns.
Fields are discovered by reading `addAdditionalSaveData`. Adding a new field requires touching the save
method, the load method, the migration class, and the SynchedEntityData declaration — all separately,
with nothing enforcing consistency between them.

**3 — Migration gap to published mod**: The 1.20.4 published save format uses flat keys with int-based
texture/model IDs. The 1.21.1 format uses string keys. `EntityDataMigration` maps the old int-based
flat format to the new `EntityData` structure but is wired to nothing.

**4 — Monsters & Girls belly/texture entanglement**: Belly state was texture ID offset in 1.20.4.
Sprint 10 introduced `BellyLevel` as a separate `int` field — correct direction, but the migration from
saves that encoded belly as a texture ID offset is not defined.

**5 — Overlay slot persistence gap**: `OverlayFeature` RANDOM and INTERACTIVE slots persist via
`SynchedEntityData` at runtime but are not written to NBT in `NativeEntity.addAdditionalSaveData`.
Overlay slot state is lost on world reload.

**6 — Loose string keys everywhere**: String key literals appear in `addAdditionalSaveData`,
`readAdditionalSaveData`, the `EntityDataMigration` constants, and the `CombatStatsNBT` constants —
four separate places that must be kept in sync manually.

**7 — No NBT API abstraction across MC versions** *(identified in architectural review)*: The entire
pipeline assumes `net.minecraft.nbt.CompoundTag` with methods like `putInt()`, `contains()`,
`putUUID()`. These do not exist before 1.18. In 1.12.2 the class is `NBTTagCompound` with
`setInteger()`, `hasKey()`. In 1.7.10 it is the same class but with obfuscated method names via Forge
mappings. UUID persistence changed in 1.16 (before: two longs `UUIDMost`/`UUIDLeast`; after:
`nbt.putUUID()`). Any code in HZLib Common that directly references `CompoundTag` cannot be shared
across the target version range without branching.

**8 — String keys survive at call sites** *(identified in architectural review)*: The original draft's
"after" example still contained `schema.getInt("Level", entityNbt)`. The key literal just moved from
`addAdditionalSaveData` to the getter call site. A typo in the string produces a silent default value,
not a compile error — the exact failure mode the schema was meant to prevent.

**9 — Two conflicting migration systems** *(identified in architectural review)*: The original
`DataField<T>` design included a per-field `migrator: Function<CompoundTag, T>`. This conflicts with the
`MigrationStep` chain. The belly/texture disambiguation requires reading two fields simultaneously
(`TextureID` AND the family's `baseTextureCount`) — which a per-field migrator cannot do because it only
sees one field at a time. Having migration logic in two places creates confusion about which is
authoritative.

**10 — `SchemaVersion` is one-dimensional** *(identified in architectural review)*: A single
`"SchemaVersion"` string does not encode which MC version wrote the save. A `"1.0.0"` save from 1.16.5
may have been written with a different `NbtAdapter` than a `"1.0.0"` save from 1.21.1. The format
detector in Stage 1 cannot distinguish these without a second axis.

**11 — `SynchedEntityData` write protocol undefined** *(identified in architectural review)*: Root-level
fields (State, Notification, TextureVariant, etc.) exist in both `SynchedEntityData` (runtime sync) and
NBT (disk). The original draft did not specify which is authoritative on load, or that NBT values must
be pushed back into `SynchedEntityData` accessors after reading. If an entity is loaded from disk but
SynchedEntityData is not updated, the entity can use stale synced values without any error.

**12 — `DataType.UUID` missing** *(identified in architectural review)*: Tamed entities require owner
UUID persistence. UUID storage changed across MC versions. The original `DataType` enum covered only
`INT`, `FLOAT`, `BOOLEAN`, `STRING` — leaving UUID-keyed fields (ownership, trusted player lists) to
bypass the schema system entirely.

---

## Decision

### Architecture: Five-Layer Data Pipeline

```
  ┌────────────────────────────────────────────────────────┐
  │  Layer 0 — DataCompound / NbtAdapter                     │
  │  Pure Java interface. MC-version impl per source set.  │
  │  ONLY point of contact with MC NBT API.                │
  ├────────────────────────────────────────────────────────┤
  │  Layer 1 — DataField<T> typed handles                  │
  │  Static constants. String key encapsulated inside.     │
  │  Zero string literals at call sites.                   │
  ├────────────────────────────────────────────────────────┤
  │  Layer 2 — EntityDataSchema                            │
  │  Ordered list of DataField<T>. Write/read via          │
  │  DataCompound. Validates on read.                        │
  ├────────────────────────────────────────────────────────┤
  │  Layer 3 — MigrationChain                              │
  │  Sole authority over format migration.                 │
  │  Works exclusively with DataCompound.                    │
  ├────────────────────────────────────────────────────────┤
  │  Layer 4 — NativeEntity save/load                      │
  │  Entry point for MC-native CompoundTag. Wraps          │
  │  immediately. Drives SynchedEntityData on load.        │
  └────────────────────────────────────────────────────────┘
```

---

### Layer 0 — `DataCompound` and `NbtAdapter`

#### Why

The MC NBT API is not stable across versions. `CompoundTag` (1.18+), `NbtCompound` (Fabric
1.16–1.17), and `NBTTagCompound` (1.7.10–1.12.2) are different classes with different method signatures.
`putUUID()` does not exist before 1.18. `contains()` replaced `hasKey()` in 1.18. Any class in HZLib
Common that imports `net.minecraft.nbt.*` is bound to exactly one MC version and cannot be shared.

The solution is a pure-Java wrapper interface that all higher layers use. MC-version-specific
implementations live in version-scoped source sets — not in HZLib Common.

#### `DataCompound` — the interface (HZLib Common, zero MC imports)

```java
package hzlib.common.nbt;

import java.util.Set;
import java.util.UUID;

/**
 * Version-agnostic compound NBT wrapper.
 * All HZLib pipeline code operates on DataCompound — never on MC-native tag types.
 */
public interface DataCompound {

    // Primitives
    void    putInt(String key, int value);
    int     getInt(String key, int defaultValue);
    void    putFloat(String key, float value);
    float   getFloat(String key, float defaultValue);
    void    putBoolean(String key, boolean value);
    boolean getBoolean(String key, boolean defaultValue);
    void    putString(String key, String value);
    String  getString(String key, String defaultValue);

    // UUID — implementation handles version-specific storage
    void putUUID(String key, UUID value);
    UUID getUUID(String key);         // returns null if absent

    // Nesting
    DataCompound getOrCreate(String key);
    DataCompound getCompound(String key); // returns empty compound if absent
    void       put(String key, DataCompound value);

    // Introspection
    boolean     has(String key);
    boolean     hasCompound(String key);
    Set<String> keys();
    void        remove(String key);
}
```

#### `NbtAdapterFactory` — registration interface (HZLib Common)

```java
package hzlib.common.nbt;

/**
 * Registered once during mod initialisation by the MC-version-specific module.
 * The factory converts MC-native compound tag objects into DataCompound wrappers.
 */
public final class NbtAdapterFactory {

    private static Factory instance;

    public static void register(Factory factory) { instance = factory; }

    /** Wraps a MC-native compound tag. The tag object is cast internally. */
    public static DataCompound wrap(Object nativeCompound) {
        return instance.wrap(nativeCompound);
    }

    /** Creates a new empty compound. */
    public static DataCompound createEmpty() {
        return instance.createEmpty();
    }

    public interface Factory {
        DataCompound wrap(Object nativeCompound);
        DataCompound createEmpty();
    }
}
```

#### MC-version-specific implementations

Each supported MC version provides one implementation class in its scoped source set. These classes are
**not** in HZLib Common — they are in the version-specific module (e.g. `sources/mc-1.21.1/`,
`sources/mc-1.12.2/`).

```
sources/
  mc-1.21.1/   CompoundTagDataCompound.java    — uses tag.putInt(), tag.putUUID(), tag.contains()
  mc-1.16.5/   NbtCompoundDataCompound.java    — uses tag.putInt(), UUID via two longs
  mc-1.12.2/   NBTTagCompoundDataCompound.java — uses tag.setInteger(), tag.hasKey()
  mc-1.7.10/   NBTTagCompoundDataCompound.java — uses srg-mapped method names via Forge
```

UUID handling by version:

| MC version range | Storage method                                   |
|------------------|--------------------------------------------------|
| 1.18+            | `tag.putUUID(key, uuid)` / `tag.getUUID(key)`    |
| 1.16–1.17        | Two longs: `key + "Most"`, `key + "Least"`       |
| 1.7.10–1.12.2    | Same two-long format via `setLong`/`getLong`     |

The `DataCompound.putUUID()` / `getUUID()` methods abstract this — callers never branch on MC version.

---

### Layer 1 — `DataField<T>` typed handles

#### Why

`schema.getInt("Level", nbt)` — a string literal at the call site — does not solve the original problem.
A typo silently returns the default value. A rename requires grep-and-replace across every call site. The
compiler cannot verify the string key against the schema declaration.

The fix is typed field handles: `DataField<T>` constants declared once, referenced by identity
everywhere. The string key is encapsulated inside the handle — never exposed at a call site.

#### `DataField<T>` (HZLib Common)

```java
public final class DataField<T> {
    private final String key;                     // NBT key — stable, never changes
    private final DataType<T> type;
    private final T defaultValue;
    @Nullable private final Predicate<T> validator;

    // No migrator field. Migration is exclusively MigrationChain's responsibility.

    public static <T> DataField<T> of(String key, DataType<T> type, T defaultValue) { ... }

    public static <T> DataField<T> of(
            String key, DataType<T> type, T defaultValue, Predicate<T> validator) { ... }

    T readFrom(DataCompound compound) { ... }
    void writeTo(DataCompound compound, T value) { ... }
    public String getKey() { return key; }
}
```

**Rule**: `DataField` has no `migrator` field. The original draft included
`Function<CompoundTag, T> migrator` on each field. This is removed. Migration is a cross-field concern
that belongs exclusively to `MigrationStep` (see Layer 3). A field-level migrator cannot handle cases
such as belly/texture disambiguation, where the correct output value for one field depends on reading
another field simultaneously.

#### `DataType<T>` (HZLib Common)

```java
public enum DataType<T> {
    INT(Integer.class),
    FLOAT(Float.class),
    BOOLEAN(Boolean.class),
    STRING(String.class),
    UUID(UUID.class);   // NEW — delegates to DataCompound.putUUID/getUUID
}
```

`DataType.UUID` is handled by `DataCompound`, which internally uses the version-appropriate storage method.
No caller branches on MC version to persist a UUID.

#### Field handle declarations (LovelyLib Common example — `RobotFields.java`)

```java
public final class RobotFields {
    // All NBT keys live here — nowhere else. Renaming a key means changing one constant.
    public static final DataField<Integer> LEVEL      = DataField.of("Level",     DataType.INT,     0);
    public static final DataField<Integer> EXP        = DataField.of("Exp",       DataType.INT,     0);
    public static final DataField<Integer> MAX_LEVEL  = DataField.of("MaxLevel",  DataType.INT,     50);
    public static final DataField<Integer> FIRE_PROT  = DataField.of("FireProtection",     DataType.INT, 0, v -> v >= 0 && v <= 100);
    public static final DataField<Integer> FALL_PROT  = DataField.of("FallProtection",     DataType.INT, 0, v -> v >= 0 && v <= 100);
    public static final DataField<Integer> BLAST_PROT = DataField.of("BlastProtection",    DataType.INT, 0, v -> v >= 0 && v <= 100);
    public static final DataField<Integer> PROJ_PROT  = DataField.of("ProjectileProtection", DataType.INT, 0, v -> v >= 0 && v <= 100);
    public static final DataField<Boolean> AUTO_ATTACK = DataField.of("AutoAttack", DataType.BOOLEAN, false);
    public static final DataField<Float>   BASE_X     = DataField.of("BaseX",      DataType.FLOAT,   0f);
    public static final DataField<Float>   BASE_Y     = DataField.of("BaseY",      DataType.FLOAT,   0f);
    public static final DataField<Float>   BASE_Z     = DataField.of("BaseZ",      DataType.FLOAT,   0f);
    public static final DataField<Boolean> SITTING    = DataField.of("IsInSittingPose", DataType.BOOLEAN, false);
    public static final DataField<Float>   HEALTH     = DataField.of("CurrentHealth",   DataType.FLOAT,   20f);

    private RobotFields() {}
}
```

Usage in entity code — zero string literals at call sites:

```java
// Read
int level = schema.get(RobotFields.LEVEL, entityDataCompound);

// Write
schema.set(RobotFields.LEVEL, getCurrentLevel(), entityDataCompound);
```

---

### Layer 2 — `EntityDataSchema`

A new class in HZLib Common. Each family attaches one via `withFeature(EntityDataSchema.class, ...)`.
The schema is an ordered list of `DataField<?>` handles. The builder registers handles — no inline
string key declarations:

```java
// RobotFamily — schema declared in configureSchema(), after configureVariants()
@Override
protected void configureSchema() {
    this.schema = EntityDataSchema.builder()
        .register(RobotFields.LEVEL)
        .register(RobotFields.EXP)
        .register(RobotFields.MAX_LEVEL)
        .register(RobotFields.FIRE_PROT)
        .register(RobotFields.FALL_PROT)
        .register(RobotFields.BLAST_PROT)
        .register(RobotFields.PROJ_PROT)
        .register(RobotFields.AUTO_ATTACK)
        .register(RobotFields.BASE_X)
        .register(RobotFields.BASE_Y)
        .register(RobotFields.BASE_Z)
        .register(RobotFields.SITTING)
        .register(RobotFields.HEALTH)
        .version("1.0.0")
        .build();
}
```

`EntityDataSchema` provides two operations:

```java
// Write — schema iterates registered fields; entity provides values by handle
schema.writeTo(entityDataCompound, this::provideFieldValue);

// Read — schema iterates registered fields; entity receives values by handle
schema.readFrom(entityDataCompound, this::consumeFieldValue);
```

The entity implements `FieldValueProvider` and `FieldValueConsumer` — both typed on `DataField<T>`,
not on `String`. The schema validates each value against the field's `Predicate<T>` on read, using the
default value and logging a warning on validation failure instead of throwing.

#### Schema declaration order (construction safety)

`NativeEntityFamily` calls `configureSchema()` from `build()`, which runs after `configureVariants()`.
Subclasses call `build()` at the end of their constructor. This guarantees that `configureSchema()` is
invoked only after the subclass constructor body has run — field handles declared as static constants are
safe to reference. Instance fields assigned in the subclass constructor are not safe to reference inside
`configureSchema()`.

---

### Layer 3 — `MigrationChain` (sole migration authority)

#### Rule

**All migration logic lives in `MigrationStep` implementations. `DataField<T>` has no migrator field.**

A `MigrationStep` receives and returns `DataCompound`. It can read any field combination it needs for its
transformation — cross-field migration (e.g. belly/texture disambiguation) is a natural fit.

```java
public interface MigrationStep {
    /** Unique identifier for this step (used in logging). */
    String id();

    /**
     * Transforms the input compound from the format this step expects
     * to the format it produces. Returns the transformed compound.
     * Input is the full root compound (not just EntityData) so cross-field
     * access (e.g. family-declared baseTextureCount alongside TextureID) is possible.
     */
    DataCompound migrate(DataCompound root);
}
```

`MigrationChain` runs steps in declared order. Each step declares the format it expects and the format
it produces. Steps are idempotent — running a step on an already-migrated compound must be safe.

#### Gen1-Fabric locale string lookup table

`MigrationStep_V0_Fabric` (in lovelylib common) hardcodes the mapping from all known Gen1-Fabric
translatable strings to stable keys. This table **must be maintained indefinitely**. Any Gen1-Fabric
entity type not in the table falls back to the raw locale string (which will fail to resolve a texture)
and logs an error. Future additions to the table require only a constant — no migration logic changes.

```java
// MigrationStep_V0_Fabric.java — in lovelylib-1.21.1 / common
private static final Map<String, String> LOCALE_TO_STABLE_KEY = new LinkedHashMap<>();
static {
    // Every entity type that shipped in the Gen1-Fabric build must be listed here.
    LOCALE_TO_STABLE_KEY.put("entity.rlovelyr.bunny",     "bunny");
    LOCALE_TO_STABLE_KEY.put("entity.rlovelyr.cat",       "cat");
    LOCALE_TO_STABLE_KEY.put("entity.rlovelyr.dragon",    "dragon");
    LOCALE_TO_STABLE_KEY.put("entity.rlovelyr.fox",       "fox");
    LOCALE_TO_STABLE_KEY.put("entity.rlovelyr.honey_bee", "honey_bee");
    LOCALE_TO_STABLE_KEY.put("entity.rlovelyr.neko",      "neko");
    LOCALE_TO_STABLE_KEY.put("entity.rlovelyr.panda",     "panda");
    // TODO: complete list — cross-reference Gen1-Fabric entity registry
}

@Override
public DataCompound migrate(DataCompound root) {
    String localeType = root.getString("type", "");
    String stableKey = LOCALE_TO_STABLE_KEY.getOrDefault(localeType, localeType);
    if (!LOCALE_TO_STABLE_KEY.containsKey(localeType)) {
        LOGGER.warn("[HZLib] Unknown Gen1-Fabric locale type '{}' — falling back to raw string", localeType);
    }
    root.putString("TextureVariant", stableKey);
    root.remove("type");
    root.remove("color"); // color (int) → TextureVariant already resolved above for Gen1-Fabric
    // Map remaining Gen1-Fabric keys to Gen4 flat format
    mapFlatKey(root, "level",               "Level");
    mapFlatKey(root, "max_level",           "MaxLevel");
    mapFlatKey(root, "exp",                 "Exp");
    mapFlatKey(root, "auto_attack",         "AutoAttack");
    mapFlatKey(root, "fire_protection",     "FireProtection");
    mapFlatKey(root, "fall_protection",     "FallProtection");
    mapFlatKey(root, "blast_protection",    "BlastProtection");
    mapFlatKey(root, "projectile_protection","ProjectileProtection");
    mapFlatKey(root, "base_x",              "BaseX");
    mapFlatKey(root, "base_y",              "BaseY");
    mapFlatKey(root, "base_z",              "BaseZ");
    return root;
}
```

---

### Storage Format

```
root NBT
├── TextureVariant        (String — SynchedEntityData primary; read from NBT on load)
├── ModelVariant          (String — SynchedEntityData primary; read from NBT on load)
├── AnimatorVariant       (String — SynchedEntityData primary; read from NBT on load)
├── State                 (int   — SynchedEntityData primary; read from NBT on load)
├── Notification          (bool  — SynchedEntityData primary; read from NBT on load)
├── OverlaySlots          (compound — one key per persistent slot)
└── EntityData            (compound — all schema-declared fields)
    ├── SchemaVersion     "1.0.0"   (data layout version — increments on field name changes)
    ├── McVersion         "1.21.1"  (MC version that wrote this save — informs migration context)
    └── [field keys declared by schema...]
```

`SchemaVersion` and `McVersion` serve different purposes and must not be merged. `SchemaVersion` tracks
evolution of what fields exist and what keys they use. `McVersion` records which MC release wrote the
save — necessary context for migration steps that must behave differently depending on the originating
MC API.

---

### Read Path and Migration

```
readAdditionalSaveData(CompoundTag rootTag)          ← MC-native type, entry point only
   │
   └── DataCompound root = NbtAdapterFactory.wrap(rootTag)   ← ONLY wrap here; no MC types below
          │
          ▼
   Stage 1: Format detection (operates on DataCompound)
          ├── Has EntityData.SchemaVersion AND EntityData.McVersion?  → Stage 3 (current)
          ├── Has EntityData.SchemaVersion only (no McVersion)?        → Stage 2 (pre-McVersion)
          ├── Has EntityData compound without SchemaVersion?            → Stage 2 (Gen3 partial)
          └── Has flat Gen1/Gen2 keys (Level, Exp, TextureID, etc.)?   → Stage 2 (legacy)
          │
          ▼
   Stage 2: MigrationChain (each step: DataCompound → DataCompound)
          ├── V0_Fabric   → locale type string → TextureVariant stable key; snake_case → PascalCase
          ├── V0_Forge    → flat PascalCase → EntityData compound; write SchemaVersion + McVersion
          ├── V1_1204     → TextureID (int) → TextureVariant (string); flat → EntityData compound
          └── V1_MG       → belly offset → BellyLevel; ModelID/AnimatorID int → string variant keys
          │
          ▼
   Stage 3: Push root-level fields into SynchedEntityData
          │   entityData.set(TEXTURE_ACCESSOR, root.getString("TextureVariant", ""))
          │   entityData.set(MODEL_ACCESSOR,   root.getString("ModelVariant",   ""))
          │   entityData.set(STATE_ACCESSOR,   root.getInt("State", 0))
          │   entityData.set(NOTIF_ACCESSOR,   root.getBoolean("Notification", false))
          │
          ▼
   Stage 4: Overlay slots
          │   root.getCompound("OverlaySlots").keys()
          │       .forEach(k -> entityData.set(overlayAccessors.get(k), ...))
          │
          ▼
   Stage 5: Schema read
          schema.readFrom(root.getCompound("EntityData"), this)
```

**Key rule**: `CompoundTag` (MC-native) is touched only at the method entry point. Everything from
format detection through schema read operates exclusively on `DataCompound`.

---

### Write Path

```java
// NativeEntity.addAdditionalSaveData — entry point only
@Override
public void addAdditionalSaveData(CompoundTag rootTag) {
    super.addAdditionalSaveData(rootTag);                    // vanilla entity fields
    DataCompound root = NbtAdapterFactory.wrap(rootTag);       // wrap immediately

    // Root-level synced fields: read from SynchedEntityData → write to NBT
    root.putString("TextureVariant",  entityData.get(TEXTURE_VARIANT_ACCESSOR));
    root.putString("ModelVariant",    entityData.get(MODEL_VARIANT_ACCESSOR));
    root.putString("AnimatorVariant", entityData.get(ANIMATOR_VARIANT_ACCESSOR));
    root.putInt   ("State",           entityData.get(STATE_ACCESSOR));
    root.putBoolean("Notification",   entityData.get(NOTIFICATION_ACCESSOR));

    // Overlay slots
    DataCompound overlayNbt = NbtAdapterFactory.createEmpty();
    overlaySlotAccessors.forEach((key, acc) -> overlayNbt.putString(key, entityData.get(acc)));
    root.put("OverlaySlots", overlayNbt);

    // EntityData compound — schema-driven, no field names in this method
    DataCompound entityDataNbt = root.getOrCreate("EntityData");
    entityDataNbt.putString("SchemaVersion", schema.getVersion());
    entityDataNbt.putString("McVersion",     McVersionProvider.current());
    schema.writeTo(entityDataNbt, this);                     // entity provides values by DataField<T>
}
```

`RobotEntity` and `MonsterEntity` override nothing. All field writes go through `NativeEntity` via the
schema. The subclass provides values through `provideFieldValue(DataField<T> field)` — typed, no strings.

---

### Layer 4 — `SynchedEntityData` Protocol

**Rule**: `SynchedEntityData` is the authoritative source of truth at runtime. NBT is authoritative at
load time. On load, NBT values **must** be pushed into `SynchedEntityData` accessors before the entity
ticks.

For root-level synced fields this is already shown in the read path above (Stage 3). For schema fields
that also have a `SynchedEntityData` accessor (e.g. future fields added to both systems), the entity's
`consumeFieldValue(DataField<T> field, T value)` implementation is responsible for calling
`entityData.set(accessor, value)` when the field is received from the schema read.

Do not read synced fields directly from the `SynchedEntityData` register in `addAdditionalSaveData` —
the register may reflect stale values from before the last NBT load. Always write what was read from NBT,
not what is currently in the register, unless the field is runtime-only (e.g. computed per tick).

---

### Migration Map — Concrete Key Mappings

#### 1.20.4 published robots → 1.21.1 (`MigrationStep_V1_1204`)

| Legacy key (1.20.4 flat) | Type   | New location  | New key                | Notes                                         |
|--------------------------|--------|---------------|------------------------|-----------------------------------------------|
| `TextureID` (int)        | int    | root          | `TextureVariant` (str) | `EntityTexture.byId(n)` + family prefix       |
| `State`                  | int    | root          | `State`                | Direct copy                                   |
| `Notification`           | bool   | root          | `Notification`         | Direct copy                                   |
| `Level`                  | int    | `EntityData`  | `Level`                | Direct copy                                   |
| `Exp`                    | int    | `EntityData`  | `Exp`                  | Direct copy                                   |
| `MaxLevel`               | int    | `EntityData`  | `MaxLevel`             | Direct copy                                   |
| `FireProtection`         | int    | `EntityData`  | `FireProtection`       | Clamp 0–100                                   |
| `FallProtection`         | int    | `EntityData`  | `FallProtection`       | Clamp 0–100                                   |
| `BlastProtection`        | int    | `EntityData`  | `BlastProtection`      | Clamp 0–100                                   |
| `ProjectileProtection`   | int    | `EntityData`  | `ProjectileProtection` | Clamp 0–100                                   |
| `AutoAttack`             | bool   | `EntityData`  | `AutoAttack`           | Direct copy                                   |
| `BaseX/Y/Z`              | float  | `EntityData`  | `BaseX/Y/Z`            | Direct copy                                   |
| `IsInSittingPose`        | bool   | `EntityData`  | `IsInSittingPose`      | Direct copy                                   |
| `CurrentHealth`          | float  | `EntityData`  | `CurrentHealth`        | Direct copy                                   |

#### 1.20.4 Monsters & Girls → 1.21.1 (`MigrationStep_V1_MG`)

| Legacy key                    | Type          | New location  | Notes                                                |
|-------------------------------|---------------|---------------|------------------------------------------------------|
| `TextureID` (int ≤ baseCount) | int           | root          | Extract base key via family prefix lookup            |
| `TextureID` (int > baseCount) | int           | `EntityData`  | `BellyLevel = textureId - baseTextureCount`          |
| `Belly` (bool)                | bool          | `EntityData`  | `true → 2` (TUMMY), `false → 0` (SLIM)              |
| `ModelID` (int)               | int           | root          | Model key lookup from family                         |
| `AnimatorID` (int)            | int           | root          | Animator key lookup from family                      |
| `State`                       | int           | root          | Direct copy                                          |
| `Plant`, `Sound`, `Notification` | bool       | `EntityData`  | Direct copy                                          |

**Belly disambiguation requirement**: each family must declare `baseTextureCount` before the migration
chain runs. `MigrationStep_V1_MG` queries the family registry by entity type ID to retrieve
`baseTextureCount` for the ambiguous TextureID boundary calculation.

---

## Scope Boundaries

### Minecraft versions 1.12.2 and 1.7.10

The migration steps in this ADR cover published mod save formats: Gen1-Fabric (1.16.5), Gen1-Forge
(1.16.x–1.18.x), Gen3-Robot (1.20.4), and Gen3-MG (1.20.4). Versions 1.12.2 and 1.7.10 have no
published mod and therefore no live save data to migrate. They are **out of scope for the migration
steps** in this ADR.

However, the `DataCompound` / `NbtAdapterFactory` architecture (Layer 0) is what makes those versions
viable in the future. When a 1.12.2 or 1.7.10 port is built, it provides one `NBTTagCompoundDataCompound`
implementation and registers it via `NbtAdapterFactory`. All HZLib Common and LovelyLib Common
pipeline code then operates on those versions without modification. No new migration logic is needed
unless the mod itself was previously published on those versions with a legacy save format.

---

## Consequences

### Positive

- **Single source of truth**: every field an entity owns is declared once on its family descriptor as a
  `DataField<T>` constant. Adding a field means adding one constant and one `.register()` call — nothing
  else.
- **No string literals at call sites**: compile-time type safety. Mismatched field references fail at
  compile time, not at runtime with a silent default value.
- **MC-version isolation**: `DataCompound` is the only point of contact with the MC NBT API. Porting to a
  new MC version means writing one `DataCompound` implementation — HZLib Common is untouched.
- **Single migration authority**: `MigrationChain` is the only place migration logic lives. The removal
  of `DataField.migrator` eliminates the ambiguity about where format transforms belong.
- **Automatic migration for new entities**: entities loaded through the pipeline pick up migration for
  free. Old entity classes need no individual migration methods.
- **Validated reads**: corrupt or missing NBT fields use their declared default and log a warning instead
  of crashing.
- **SynchedEntityData protocol explicit**: the load path pushes NBT values into `SynchedEntityData`
  before the entity ticks, eliminating stale-value bugs.
- **Overlay slot persistence fixed**: the gap where RANDOM/INTERACTIVE slots were lost on world reload is
  closed by the `OverlaySlots` compound.
- **UUID persistence version-safe**: `DataType.UUID` delegates to `DataCompound`, which selects the
  correct storage strategy per MC version.
- **Extensible by downstream mods**: Monsters & Girls, Tribute, Legacy, Reboot each extend the base
  `EntityDataSchema` with their own `DataField<T>` constants — no changes to HZLib Common required.

### Negative / Trade-offs

- **Added abstraction layer**: `DataCompound` wraps the native compound. One wrapper allocation per
  save/load call. Negligible at the scale of entity NBT operations but worth documenting.
- **Schema must be declared via `configureSchema()`**: schemas cannot be declared inline in the family
  constructor body due to Java construction order. Subclasses must use the `configureSchema()` override
  hook, which runs from `build()` after the superclass chain completes.
- **Migration is one-way**: once a world loads through the migration chain, saves are in 1.21.1 format.
  Rolling back to 1.20.4 will not load the migrated saves. Intended — document in the migration guide.
- **Gen1-Fabric locale table requires ongoing maintenance**: any entity type added to a hypothetical
  future Gen1-Fabric build would require a new table entry to be migrateable. The table lives in
  `MigrationStep_V0_Fabric` in lovelylib common and must be treated as append-only.

### Risks

- **Belly/texture ID boundary for Monsters & Girls**: a Wisp with `TextureID = 3` is ambiguous without
  the family's `baseTextureCount`. Migration must query the family registry before splitting the value.
  If the family is not registered when migration runs (e.g. during a world conversion tool pass),
  migration will log an error and default to `BellyLevel = 0`.
- **`McVersionProvider.current()`**: this method must return the actual running MC version string at
  save time. If it returns a hardcoded constant that is not updated between ports, the `McVersion` field
  in saved data will be wrong. It must be implemented as a runtime lookup, not a constant.
- **`NbtAdapterFactory` not registered at load**: if the MC-version module fails to register the factory
  (e.g. wrong mod init order), all `DataCompound` operations throw. Registration must happen in the
  earliest mod init hook before any entity is loaded.

---

## Alternatives Considered

### A — Keep current approach, just wire `EntityData` to `RobotEntity`

Connect `RobotEntity.addAdditionalSaveData` to the existing `EntityData` / `CombatStatsNBT` /
`ProtectionStatsNBT` infrastructure that already exists but is bypassed.

**Rejected**: this fixes the robot save/load but does not solve the schema declaration problem. Fields
are still discovered by reading entity code. Monsters & Girls and future entities would still need
independent solutions. The migration chain remains disconnected. No MC-version isolation.

### B — ECS-style component registry (full)

Model each data field as an independent registered component with its own serialiser, similar to
Minecraft's `DataComponent` system or Terasology's ECS. Each component is independently versioned and
independently migrated.

**Rejected**: significant architectural investment appropriate for a general-purpose engine, not a mod
library. The family system already provides the composition point. A full ECS would conflict with
Minecraft's `TamableAnimal` inheritance hierarchy which cannot be replaced. The declared-schema approach
achieves the same goals within the existing class hierarchy.

### C — Minecraft `DataComponent` (1.20.5+)

Use Minecraft's native `DataComponentType` system introduced in 1.20.5 for item NBT, extended to
entities.

**Rejected**: `DataComponentType` is for items, not entities. Entity data is still managed through
`SynchedEntityData` and `addAdditionalSaveData` in 1.21.1. There is no native equivalent for entity
persistence.

### D — Use MC-version-specific HZLib builds instead of `DataCompound`

Ship a separate HZLib Common jar per MC version. Each version's HZLib directly uses `CompoundTag` (or
`NBTTagCompound`), eliminating the wrapper layer.

**Rejected**: this approach multiplies the number of artifacts to maintain for every class in HZLib
Common. Any bug fix or feature addition must be applied to every version-specific copy. The wrapper layer
is a single implementation per MC version (one class per version), while the callers — EntityDataSchema,
MigrationChain, DataField — remain shared. The maintenance tradeoff strongly favours the wrapper.

### E — Stateless string-keyed schema with no typed handles

Keep `schema.getInt("Level", nbt)` — a string-keyed accessor — but enforce uniqueness at schema build
time (duplicate keys fail at startup).

**Rejected**: startup validation catches key collisions but not typos in callers. A rename still requires
grep-and-replace across all call sites. Compile-time safety, which typed handles provide for free, is a
stronger guarantee than runtime validation.

---

## Implementation Plan

### Phase 0 — `DataCompound` / `NbtAdapterFactory` (HZLib Common + MC-version modules)

This phase must complete before all others. No higher-layer code can compile against HZLib Common until
`DataCompound` exists.

1. Define `DataCompound` interface in HZLib Common (`hzlib.common.nbt`)
2. Define `NbtAdapterFactory` in HZLib Common
3. Define `McVersionProvider` interface in HZLib Common (returns current MC version string)
4. Implement `CompoundTagDataCompound` in the 1.21.1 source set
5. Register factory and version provider in the 1.21.1 mod initialiser
6. Write unit tests for `CompoundTagDataCompound` against the `DataCompound` contract

**Build dependency note**: HZLib Common (Phase 0) must publish a snapshot before LovelyLib (Phase 2)
can begin. LovelyLib cannot import `DataField<T>` or `EntityDataSchema` until the HZLib artifact is
available. Do not begin Phase 2 work in parallel with Phase 0.

### Phase 1 — Pipeline core (HZLib Common)

Depends on: Phase 0 complete.

1. Define `DataType<T>` enum with `INT`, `FLOAT`, `BOOLEAN`, `STRING`, `UUID`
2. Define `DataField<T>` (no migrator field) with `readFrom(DataCompound)` / `writeTo(DataCompound, T)`
3. Define `EntityDataSchema` builder — `.register(DataField<?>)` + `.version(String)`
4. Define `MigrationStep` interface (input/output `DataCompound`)
5. Implement `MigrationChain` runner with format detection (Stage 1 logic)
6. Define `FieldValueProvider` and `FieldValueConsumer` interfaces (typed on `DataField<T>`)
7. Wire `NativeEntity.addAdditionalSaveData` / `readAdditionalSaveData` to the full pipeline:
   - wrap rootTag → detect format → migrate if needed → push synced fields → read schema
8. Fix overlay slot NBT gap in `NativeEntity` using `DataCompound`
9. Delete `CombatStatsNBT`, `ProtectionStatsNBT`, `EnchantmentStatsNBT` (superseded)

### Phase 2 — LovelyLib (robot schema and migrations)

Depends on: Phase 1 complete and HZLib Common artifact published.

1. Declare `RobotFields.java` with all robot `DataField<T>` constants
2. Declare schema in `RobotFamily.configureSchema()` using `.register(RobotFields.*)`
3. Implement `MigrationStep_V0_Fabric` with complete locale-to-stable-key table
4. Implement `MigrationStep_V0_Forge` (Gen1-Forge / Gen2 flat → EntityData compound)
5. Implement `MigrationStep_V1_1204` (1.20.4 flat → EntityData compound, int IDs → string keys)
6. Register migration chain on `RobotFamily`
7. Wire `RobotEntity` to delegate `addAdditionalSaveData` / `readAdditionalSaveData` entirely to
   `NativeEntity` base (remove all flat-write overrides from `RobotEntity`)
8. Delete `EntityDataMigration.java` (superseded by `MigrationChain`)

### Phase 3 — Monsters & Girls (monster schema and migrations)

Depends on: Phase 1 complete.

1. Declare `MonsterFields.java` per family with `DataField<T>` constants including `BELLY_LEVEL`,
   `PLANTING_ENABLED`, `SOUND_ENABLED`
2. Declare `baseTextureCount` on each family (required by belly migration)
3. Implement `MigrationStep_V1_MG` with belly/texture ID disambiguation
4. Register migration chain on each monster family
5. Wire `MonsterEntity.addAdditionalSaveData` to delegate to `NativeEntity` base

### Phase 4 — Validation

1. Load 1.20.4 robot world save — verify all stats migrate with no data loss
2. Load Gen1-Fabric world save (if archive available) — verify locale strings resolve to stable keys
3. Verify overlay slot state persists across world reload
4. Verify belly level persists correctly for migrated Monsters & Girls entities
5. Verify a corrupt NBT field (validator failure) uses the declared default and logs a warning rather
   than throwing
6. Verify `McVersion` field is written correctly on first save after migration

### Phase 5 — Additional MC version ports (future, as needed)

When a new MC version port is initiated:
1. Implement `*DataCompound` for that version's NBT class in the version-specific source set
2. Register factory and version provider in that version's mod initialiser
3. No changes to HZLib Common, LovelyLib Common, or any migration step are required

---

## Related Documents

- `docs/development/notes/Pre_Publish_Checklist_Notes.md` — item 11
- `sources/common/hzlib-1.21.1/.../nbt/DataCompound.java` — to be created (Phase 0)
- `sources/common/hzlib-1.21.1/.../nbt/NbtAdapterFactory.java` — to be created (Phase 0)
- `sources/mc-1.21.1/.../nbt/CompoundTagDataCompound.java` — to be created (Phase 0)
- `sources/common/hzlib-1.21.1/.../data/EntityData.java` — existing partial structure (superseded)
- `sources/common/lovelylib-1.21.1/.../data/EntityDataMigration.java` — migration stub (superseded)
- `sources/common/lovelylib-1.21.1/.../data/migration/MigrationStep_V0_Fabric.java` — to be created
- `sources/common/lovelylib-1.21.1/.../data/migration/MigrationStep_V1_1204.java` — to be created
- `archive/1.20.X/rlovelyr-1.20.4/` — legacy save format reference
- `archive/1.16.X/rlovelyr-1.16.5/` — original format reference (both Fabric and Forge)
- `monsters-girls/monsters-girls-1.20.4/` — Monsters & Girls legacy format reference