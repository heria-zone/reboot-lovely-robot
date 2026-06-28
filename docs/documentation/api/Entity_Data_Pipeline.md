# Entity Data Pipeline — API Reference

**Module**: HZLib Common (`net.heriazone.hzlib.api.nbt`)  
**Status**: Active — Sprint 11  
**ADR**: `docs/development/decisions/ADR_019_Entity_Data_Pipeline.md`

---

## Overview

The Entity Data Pipeline replaces scattered `CompoundTag` string-key access in
`addAdditionalSaveData` / `readAdditionalSaveData` with a five-layer system:

```
Layer 0  DataCompound / NbtAdapterFactory   — MC-version abstraction
Layer 1  DataField<T>                       — typed field handles, keys encapsulated
Layer 2  EntityDataSchema                   — family-level schema declaration
Layer 3  MigrationChain / MigrationStep     — versioned format upgrades
Layer 4  NativeEntity save/load             — pipeline entry point, transparent to subclasses
```

**Why this matters**: a typo in a string key produces a silent default value at
runtime, not a compile error. A `DataField<T>` constant fails at compile time.
Renaming an NBT key means changing one constant; the compiler rejects every stale
reference automatically.

---

## Quick Start — adding the pipeline to a new entity family

### Step 1 — Declare field handles

Create a `*Fields` constants class in your mod. One class per entity family (or
shared across a family hierarchy). Keys are encapsulated; never expose them:

```java
// MyEntityFields.java
public final class MyEntityFields {

    private MyEntityFields() {}

    // DataField.of(nbtKey, DataType, defaultValue)
    public static final DataField<Integer> STAGE =
            DataField.of("Stage", DataType.INT, 0, v -> v >= 0 && v <= 3);

    public static final DataField<Boolean> IS_CHARGING =
            DataField.of("IsCharging", DataType.BOOLEAN, false);

    public static final DataField<Float> POWER =
            DataField.of("Power", DataType.FLOAT, 1.0f, v -> v > 0f);
}
```

Available types: `DataType.INT`, `DataType.FLOAT`, `DataType.BOOLEAN`,
`DataType.STRING`, `DataType.UUID`.

The optional fourth argument is a `Predicate<T>` validator. Values that fail it fall
back to the declared default and log a warning — corrupt saves never crash the entity.

---

### Step 2 — Declare the schema on the family

Override `configureSchema()` in your `NativeEntityFamily` subclass. This method is
called from the base constructor after `configureVariants()`, so static field
constants are safe to reference:

```java
// MyEntityFamily.java
public class MyEntityFamily extends NativeEntityFamily<MyEntityFamily> {

    @Override
    protected void configureSchema() {
        schema = EntityDataSchema.builder()
                .register(MyEntityFields.STAGE)
                .register(MyEntityFields.IS_CHARGING)
                .register(MyEntityFields.POWER)
                .version("1.0.0")
                .build();

        // If no legacy saves exist, use an empty chain:
        migrationChain = MigrationChain.empty();
    }
}
```

**Do not reference instance fields of the subclass inside `configureSchema()`** —
the subclass constructor body has not yet run when this fires.

---

### Step 3 — Wire the entity hooks

Override `provideFieldValue` (write path) and `consumeFieldValue` (read path) in
your entity class. Dispatch on field constants by identity — no string keys:

```java
// MyEntity.java
@Override
@SuppressWarnings("unchecked")
protected <T> T provideFieldValue(DataField<T> field) {
    if (field == MyEntityFields.STAGE)       return (T) Integer.valueOf(getStage());
    if (field == MyEntityFields.IS_CHARGING) return (T) Boolean.valueOf(isCharging());
    if (field == MyEntityFields.POWER)       return (T) Float.valueOf(getPower());
    return field.getDefaultValue(); // unknown field — return declared default
} // provideFieldValue ()

@Override
@SuppressWarnings("unchecked")
protected <T> void consumeFieldValue(DataField<T> field, T value) {
    if (field == MyEntityFields.STAGE)       { setStage((Integer) value);       return; }
    if (field == MyEntityFields.IS_CHARGING) { setCharging((Boolean) value);    return; }
    if (field == MyEntityFields.POWER)       { setPower((Float) value);          return; }
    // unknown field — no-op; base class handles root-level fields (TextureVariant, etc.)
} // consumeFieldValue ()
```

**`SynchedEntityData` rule**: for any field that is also a synced data value, push
the loaded value into the `entityData` accessor inside `consumeFieldValue`. NBT is
authoritative on load; the synced register may hold a stale pre-load value.

```java
if (field == MyEntityFields.STAGE) {
    setStage((Integer) value);
    entityData.set(STAGE_ACCESSOR, (Integer) value); // push into synced register
}
```

That is all. `addAdditionalSaveData` and `readAdditionalSaveData` do not need
overrides — `NativeEntity` drives the full pipeline through these two hooks.

---

## Adding a New Field to an Existing Family

1. **Add a constant** to the `*Fields` class:
   ```java
   public static final DataField<Boolean> WARY_MODE =
           DataField.of("WaryMode", DataType.BOOLEAN, false);
   ```

2. **Register it** in `configureSchema()`:
   ```java
   schema = EntityDataSchema.builder()
           // existing fields...
           .register(MyEntityFields.WARY_MODE)  // ← new
           .version("1.0.0")
           .build();
   ```

3. **Dispatch it** in `provideFieldValue` and `consumeFieldValue`.

4. **No migration step needed** for new fields. The schema reads the value if
   present; returns the declared default if absent (existing saves). This is safe
   because `DataField.readFrom` checks `has(key)` before reading.

---

## Setting Up Migrations

Migrations are needed when a published save format must be upgraded. Each format
transition is one `MigrationStep` implementation.

### Step 1 — Implement `MigrationStep`

```java
// MigrationStep_V1_MyMod.java
public final class MigrationStep_V1_MyMod implements MigrationStep {

    @Override
    public @NotNull String id() { return "V1_MyMod"; }

    @Override
    public @NotNull DataCompound migrate(@NotNull DataCompound root) {
        // Guard: already migrated — skip. Check the most specific marker your
        // step produces. McVersion is the strongest signal.
        if (root.hasCompound("EntityData") &&
                root.getCompound("EntityData").has("McVersion")) return root;

        DataCompound entityData = NbtAdapterFactory.createEmpty();
        entityData.putString("SchemaVersion", "1.0.0");
        entityData.putString("McVersion", McVersionProvider.current());

        // Move flat root keys into the EntityData sub-compound
        entityData.putInt("Stage",      root.getInt("stage", 0));
        entityData.putBoolean("WaryMode", root.getBoolean("wary", false));

        root.remove("stage");
        root.remove("wary");
        root.put("EntityData", entityData);
        return root;
    }
}
```

**Key rules for `migrate()`**:

| Rule | Reason |
|------|--------|
| Check the idempotency guard first — return unchanged if already migrated | The chain may run more than once on partially migrated saves |
| Receive and return the **full root compound**, not just `EntityData` | Cross-field transforms (e.g. belly/texture disambiguation) need root-level context |
| Use `NbtAdapterFactory.createEmpty()` for new sub-compounds | Never import `CompoundTag` inside a migration step |
| Use `McVersionProvider.current()` for the `McVersion` header | Never hardcode a version string |
| Log errors with enough context to diagnose without a debugger | Unknown keys, missing family registrations, type mismatches |

---

### Step 2 — Register the step on the family

Add the step to `configureSchema()`. Steps run in declaration order — declare
oldest-format steps first:

```java
@Override
protected void configureSchema() {
    schema = EntityDataSchema.builder()
            .register(MyEntityFields.STAGE)
            .register(MyEntityFields.WARY_MODE)
            .version("1.0.0")
            .build();

    migrationChain = MigrationChain.builder()
            .addStep(new MigrationStep_V1_MyMod()) // oldest first
            .build();
}
```

If multiple format generations exist, chain all steps:

```java
migrationChain = MigrationChain.builder()
        .addStep(new MigrationStep_V0_OldFormat())   // 1.16 saves
        .addStep(new MigrationStep_V1_NewFormat())   // 1.20 saves
        .build();
```

The chain auto-detects format using `MigrationChain.isCurrentFormat(root)` and
skips all steps when the save is already current. When migration is needed, all
steps run in sequence — each step should guard against its own preconditions so it
skips safely if a later step's format is already present.

---

### Format Detection Reference

`MigrationChain` identifies four save formats by inspecting the root compound. You
can use these static methods in step logic:

```java
// Current — both headers present: safe to skip migration
MigrationChain.isCurrentFormat(root)

// Has SchemaVersion but no McVersion — written by an earlier pipeline version
MigrationChain.isPreMcVersionFormat(root)

// Has EntityData compound but no SchemaVersion — Gen3 partial (1.20.4 dead-code format)
MigrationChain.isGen3PartialFormat(root)

// No EntityData compound at all — flat Gen1/Gen2 layout
MigrationChain.isLegacyFlatFormat(root)
```

---

### When Migration Involves Cross-Field Logic

Some transforms cannot be expressed as per-field operations because the output
value for one field depends on reading a second field. The classic example is
`MigrationStep_V1_MG` — belly level was encoded as a `TextureID` offset, so the
correct belly level can only be computed after knowing how many base textures the
family has.

The pattern: maintain a static registry on the migration step that families
populate from their static initialiser blocks.

```java
// In the migration step
private static final Map<String, Integer> BASE_TEXTURE_COUNTS = new HashMap<>();

public static void registerBaseTextureCount(String familyKey, int count) {
    BASE_TEXTURE_COUNTS.put(familyKey, count);
}

// In migrate()
int baseCount = BASE_TEXTURE_COUNTS.getOrDefault(typeKey, -1);
if (baseCount < 0) {
    LOGGER.error("[HZLib] Family '{}' has no registered count — belly defaults to 0", typeKey);
}
```

```java
// In the family's static block
static {
    registerVariants();
    MigrationStep_V1_MG.registerBaseTextureCount("wisp_girl_blue", 1);
    MigrationStep_V1_MG.registerBaseTextureCount("wisp_girl_green", 1);
}
```

**Timing guarantee**: static blocks run when the class is loaded, which happens
during mod init before any world load. The registry is complete by the time any
save is read.

---

## New MC Version Port

When porting HZLib to a new Minecraft version, only one class needs to be written —
the `DataCompound` implementation for that version's NBT class:

```java
// In the version-specific source set (e.g. sources/mc-1.19.2/)
public final class CompoundTagDataCompound implements DataCompound {
    private final CompoundTag tag;   // net.minecraft.nbt.CompoundTag (1.19.2 flavour)

    public CompoundTagDataCompound(@NotNull CompoundTag tag) { this.tag = tag; }
    public CompoundTagDataCompound() { this(new CompoundTag()); }

    @Override public void putInt(String key, int v)      { tag.putInt(key, v); }
    @Override public int  getInt(String key, int def)    { return tag.contains(key) ? tag.getInt(key) : def; }
    // ... implement all DataCompound methods

    // UUID: use two-long encoding for pre-1.18, tag.putUUID() for 1.18+
    @Override public void putUUID(String key, UUID v) {
        tag.putLong(key + "Most",  v.getMostSignificantBits());
        tag.putLong(key + "Least", v.getLeastSignificantBits());
    }
    @Override public UUID getUUID(String key) {
        if (!tag.contains(key + "Most")) return null;
        return new UUID(tag.getLong(key + "Most"), tag.getLong(key + "Least"));
    }
}
```

Register both services in the loader entry point (constructor / `onInitialize`),
**before any entity is deserialized**:

```java
// Forge constructor / Fabric onInitialize — earliest possible hook
NbtAdapterFactory.register(new NbtAdapterFactory.Factory() {
    @Override public DataCompound wrap(Object native) {
        if (!(native instanceof CompoundTag tag))
            throw new IllegalArgumentException("Expected CompoundTag, got: " + native.getClass());
        return new CompoundTagDataCompound(tag);
    }
    @Override public DataCompound createEmpty() { return new CompoundTagDataCompound(); }
});

McVersionProvider.register(() -> SharedConstants.getCurrentVersion().getName());
```

All pipeline code in HZLib Common — `DataField`, `EntityDataSchema`, `MigrationChain`,
every migration step — runs without modification on the new version.

---

## NBT Layout Reference

Every entity save written by the pipeline follows this structure:

```
root NBT
├── TextureVariant        String   — appearance key (e.g. "bunny_white")
├── ModelVariant          String   — model key
├── AnimatorVariant       String   — animator key
├── StateId               int      — EntityState ordinal
├── NotificationEnabled   boolean
├── OverlaySlots          compound — one string key per persistent overlay slot
└── EntityData            compound — all schema-declared fields
    ├── SchemaVersion     String   "1.0.0"     — data layout version
    ├── McVersion         String   "1.21.1"    — MC version that wrote this save
    └── [field keys declared by the family's EntityDataSchema]
```

`SchemaVersion` and `McVersion` serve different purposes and are never merged:

- **`SchemaVersion`** tracks data layout changes — field renames, additions, removals.
- **`McVersion`** records which MC release wrote the save. Migration steps that
  must behave differently per MC release (e.g. UUID encoding changed in 1.18) use
  this to select the correct transform path.

---

## API Reference Summary

### `DataField<T>`
```
DataField.of(String key, DataType<T> type, T defaultValue)
DataField.of(String key, DataType<T> type, T defaultValue, Predicate<T> validator)
T    readFrom(DataCompound compound)           // returns default if absent or invalid
void writeTo(DataCompound compound, T value)
T    getDefaultValue()
```

### `EntityDataSchema`
```
EntityDataSchema.builder()
    .register(DataField<?>)     // add field in write order
    .version(String)            // default "1.0.0"
    .build()
void writeTo(DataCompound entityDataCompound, FieldValueProvider entity)
void readFrom(DataCompound entityDataCompound, FieldValueConsumer entity)
```

### `MigrationChain`
```
MigrationChain.builder().addStep(MigrationStep).build()
MigrationChain.empty()
DataCompound migrate(DataCompound root)    // skips if already current
static boolean isCurrentFormat(DataCompound root)
static boolean isPreMcVersionFormat(DataCompound root)
static boolean isGen3PartialFormat(DataCompound root)
static boolean isLegacyFlatFormat(DataCompound root)
```

### `MigrationStep` (interface to implement)
```
String        id()                          // "V{n}_{source}" convention
DataCompound  migrate(DataCompound root)    // receives and returns full root
```

### `NativeEntityFamily` hooks
```
protected void configureSchema()           // override to set schema + migrationChain
protected EntityDataSchema  schema         // assign in configureSchema()
protected MigrationChain    migrationChain // assign in configureSchema(); defaults to empty()
EntityDataSchema  getSchema()
MigrationChain    getMigrationChain()
```

### `NativeEntity` hooks
```
protected <T> T    provideFieldValue(DataField<T> field)         // override for write path
protected <T> void consumeFieldValue(DataField<T> field, T value) // override for read path
```

### `NbtAdapterFactory`
```
NbtAdapterFactory.register(NbtAdapterFactory.Factory factory)   // call once at mod init
NbtAdapterFactory.wrap(Object nativeCompound)                   // CompoundTag → DataCompound
NbtAdapterFactory.createEmpty()                                  // new empty DataCompound
```

### `McVersionProvider`
```
McVersionProvider.register(McVersionProvider.Provider provider) // call once at mod init
McVersionProvider.current()                                      // returns "1.21.1" etc.
```

---

## Existing Implementations — Reference

| Implementation | Location | Covers |
|---|---|---|
| `RobotFields` | `lovelylib/.../entity/data/` | All robot entity fields (15 fields) |
| `MigrationStep_V0_Fabric` | `lovelylib/.../data/migration/` | Gen1-Fabric locale strings → stable keys |
| `MigrationStep_V0_Forge` | `lovelylib/.../data/migration/` | Gen1-Forge / Gen2 flat → `EntityData` |
| `MigrationStep_V1_1204` | `lovelylib/.../data/migration/` | 1.20.4 Reboot flat → `EntityData` |
| `MonsterFields` | `monsters_girls/.../entity/data/` | Common monster fields (3 fields) |
| `MigrationStep_V1_MG` | `monsters_girls/.../data/migration/` | 1.20.4 M&G belly/texture disambiguation |
| `CompoundTagDataCompound` | `hzlib-1.21.1/Common/.../nbt/` | MC 1.21.1 `CompoundTag` wrapper |

---

## Related Documents

- `docs/development/decisions/ADR_019_Entity_Data_Pipeline.md` — full architectural rationale and generation history
- `docs/development/sprints/active/SPRINT_11_TASK.md` — implementation sprint tracking
