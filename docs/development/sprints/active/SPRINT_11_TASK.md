# Sprint Task: Entity Data Pipeline — ADR 019 Full Implementation

**Status**: 🔄 ACTIVE  
**Started**: 2026-06-27  
**Target Completion**: 2026-07-11  
**Priority**: High  
**Complexity**: Very High  
**Sprint Number**: 11

## Sprint Goal

Implement the full five-layer Entity Data Pipeline defined in ADR 019. The result is a production-ready,
MC-version-agnostic NBT persistence layer with typed field handles, schema-driven serialisation,
a `MigrationChain` that covers all published save formats (Gen1-Fabric, Gen1-Forge/Gen2, 1.20.4
robots, 1.20.4 Monsters & Girls), and explicit `SynchedEntityData` protocol on load. The current
flat-write hack in `RobotEntity` and the disconnected `EntityData` infrastructure are both deleted by
the end of this sprint.

## Strategic Context

**Source ADR**: `docs/development/decisions/ADR_019_Entity_Data_Pipeline.md`  
**Prerequisite**: Sprint 09 Phase 1 (HZLib rename — NativeEntity/NativeEntityFamily) ✅  
**Prerequisite**: Sprint 10 (BellyLevel integer field on MonsterEntity) ✅  
**Affects**: HZLib Common, LovelyLib Common, Monsters & Girls Common  
**Build dependency**: Phase 0 → Phase 1 → Phase 2 & Phase 3 (parallel) → Phase 4

---

## Objectives

### Phase 0 — `DataCompound` / `NbtAdapterFactory` (HZLib Common + MC-version module)
> **Prerequisite for everything else. Nothing in Phase 1 or later can compile without this.**

- [x] Define `DataCompound` interface in `sources/common/hzlib-1.21.1/Common/.../nbt/DataCompound.java`
- [x] Define `NbtAdapterFactory` in `sources/common/hzlib-1.21.1/Common/.../nbt/NbtAdapterFactory.java`
- [x] Define `McVersionProvider` interface in HZLib Common
- [x] Implement `CompoundTagDataCompound` in 1.21.1 source set (wraps `net.minecraft.nbt.CompoundTag`)
- [x] Handle all `DataCompound` methods: `putInt/getInt`, `putFloat/getFloat`, `putBoolean/getBoolean`, `putString/getString`, `putUUID/getUUID`, `getOrCreate`, `getCompound`, `put`, `has`, `hasCompound`, `keys`, `remove`
- [x] UUID storage: 1.21.1 uses `tag.putUUID(key, uuid)` / `tag.getUUID(key)` — implement accordingly
- [x] Register `CompoundTagDataCompound` factory and `McVersionProvider` in 1.21.1 mod initialiser (earliest hook, before any entity loads)
- [x] Publish HZLib Common snapshot so LovelyLib can depend on it

### Phase 1 — Pipeline Core (HZLib Common)
> **Depends on Phase 0 complete.**

- [x] Define `DataType<T>` enum: `INT`, `FLOAT`, `BOOLEAN`, `STRING`, `UUID`
- [x] Define `DataField<T>` — `key`, `type`, `defaultValue`, optional `Predicate<T> validator`. **No migrator field.**
  - `readFrom(DataCompound)` — reads value, validates against predicate, falls back to default on failure, logs warning
  - `writeTo(DataCompound, T value)` — writes typed value
  - `getKey()` — package-private, not exposed at call sites
- [x] Define `FieldValueProvider` functional interface: `<T> T provide(DataField<T> field)`
- [x] Define `FieldValueConsumer` functional interface: `<T> void consume(DataField<T> field, T value)`
- [x] Define `EntityDataSchema` builder:
  - `.register(DataField<?>)` — adds field to ordered list
  - `.version(String)` — sets schema version string
  - `.build()` → `EntityDataSchema`
  - `writeTo(DataCompound entityDataCompound, FieldValueProvider entity)` — iterates fields, calls provider
  - `readFrom(DataCompound entityDataCompound, FieldValueConsumer entity)` — iterates fields, validates, calls consumer
  - `getVersion()` — returns schema version string
- [x] Define `MigrationStep` interface:
  - `String id()` — unique identifier
  - `DataCompound migrate(DataCompound root)` — receives and returns full root compound
- [x] Implement `MigrationChain`:
  - Stage 1 format detection: `SchemaVersion + McVersion` present → current; `SchemaVersion` only → pre-McVersion; `EntityData` compound without version → Gen3 partial; flat Gen1/Gen2 keys → legacy
  - Runs registered `MigrationStep` list in declaration order when migration needed
  - Idempotency: running a step twice must be safe
- [x] Update `NativeEntity.addAdditionalSaveData`:
  - Wraps `CompoundTag rootTag` immediately via `NbtAdapterFactory.wrap()`
  - Writes root-level synced fields (TextureVariant, ModelVariant, AnimatorVariant, State, Notification) from `SynchedEntityData` accessors
  - Writes `OverlaySlots` compound — one key per persistent overlay slot
  - Writes `EntityData` compound via `schema.writeTo()` — writes `SchemaVersion` and `McVersion` headers first
  - No `CompoundTag` usage below the initial wrap
- [x] Update `NativeEntity.readAdditionalSaveData`:
  - Wraps `CompoundTag rootTag` immediately
  - Stage 1 format detection
  - Stage 2 `MigrationChain.migrate()` if needed
  - Stage 3 push root-level fields into `SynchedEntityData` accessors
  - Stage 4 restore `OverlaySlots` — push each slot key into its accessor
  - Stage 5 `schema.readFrom()` — entity receives values via typed `FieldValueConsumer`
  - No `CompoundTag` usage below the initial wrap
- [x] Add `provideFieldValue(DataField<T>)` and `consumeFieldValue(DataField<T>, T)` override hooks to `NativeEntity`
- [x] Add `configureSchema()` hook + `getSchema()` / `getMigrationChain()` accessors to `NativeEntityFamily`
- [x] Fix overlay slot NBT gap (RANDOM and INTERACTIVE slots were not persisted — now written under `OverlaySlots`)
- [x] Delete `CombatStatsNBT.java`, `ProtectionStatsNBT.java`, `EnchantmentStatsNBT.java` (superseded by `DataField` + `EntityDataSchema`)
- [x] Delete `EntityData.java` (old Gen4 partial — superseded by `EntityDataSchema`)
- [x] Delete HZLib `EntityDataMigration.java` (superseded by `MigrationChain/MigrationStep`)
- [x] Verify full build passes with zero errors across all HZLib modules — **13 files, 0 diagnostics**

### Phase 2 — LovelyLib Robot Schema and Migrations (LovelyLib Common)
> **Depends on Phase 1 complete and HZLib Common snapshot published.**

- [x] Create `RobotFields.java` — all robot `DataField<T>` constants:
  - `LEVEL`, `EXP`, `MAX_LEVEL` (DataType.INT)
  - `FIRE_PROT`, `FALL_PROT`, `BLAST_PROT`, `PROJ_PROT` (DataType.INT, validator: 0–100)
  - `AUTO_ATTACK` (DataType.BOOLEAN)
  - `BASE_X`, `BASE_Y`, `BASE_Z` (DataType.FLOAT)
  - `SITTING` (DataType.BOOLEAN, key `"IsInSittingPose"`)
  - `HEALTH` (DataType.FLOAT, key `"CurrentHealth"`)
  - `STANDBY_TICKS`, `STANDBY_TARGET_TICKS` (DataType.INT)
- [x] Add `configureSchema()` override hook to `NativeEntityFamily` base (called from constructor, after `configureVariants()`) ← **delivered in Phase 1**
- [x] Implement `configureSchema()` in `RobotFamily` — registers all `RobotFields.*` constants + migration chain `[V0_Fabric → V0_Forge → V1_1204]`
- [x] Implement `provideFieldValue(DataField<T>)` in `RobotEntity` — dispatches on `RobotFields` constants by identity, zero string literals
- [x] Implement `consumeFieldValue(DataField<T>, T)` in `RobotEntity` — restores entity state and pushes health into `SynchedEntityData` accessor on load
- [x] Implement `MigrationStep_V0_Fabric.java`:
  - Complete `LOCALE_TO_STABLE_KEY` lookup table (all 7 Gen1-Fabric entity types: bunny, bunny2, dragon, honey, kitsune, neko, vanilla)
  - Maps `"type"` (locale string) → `TextureVariant` (stable key)
  - Maps `"color"` (int) → resolved via `EntityTexture.byId()` if `"type"` mapping fails
  - Renames all snake_case Gen1-Fabric keys to Gen4 PascalCase equivalents using typed rename helpers
- [x] Implement `MigrationStep_V0_Forge.java`:
  - Handles Gen1-Forge and Gen2 flat PascalCase keys
  - Maps `TextureID` (int) → `TextureVariant` (string key via `EntityTexture.byId()` + `Variant` family prefix)
  - Moves all flat fields into `EntityData` compound with `SchemaVersion "1.0.0"` and `McVersion` headers
- [x] Implement `MigrationStep_V1_1204.java`:
  - Full key mapping per ADR 019 (15 field mappings); discards orphaned partial EntityData from 1.20.4
  - `TextureID` int → `TextureVariant` string; flat → `EntityData` compound
  - Health sanitiser guard against corrupt zero-health saves
- [x] Register migration chain on `RobotFamily`: `[V0_Fabric, V0_Forge, V1_1204]` in order
- [x] Remove all flat-write overrides from `RobotEntity.addAdditionalSaveData` / `readAdditionalSaveData` — both delegate entirely to `NativeEntity` base; post-load work (recalculate, hitbox, registry) kept
- [x] Delete `EntityDataMigration.java` (superseded)
- [x] Verify full build passes — **6 files, 0 diagnostics**

### Phase 3 — Monsters & Girls Monster Schema and Migrations (Monsters & Girls Common)
> **Depends on Phase 1 complete. Can run in parallel with Phase 2.**

- [x] Declare `baseTextureCount` per family on each `NativeEntityFamily` subclass (required by belly migration disambiguation)
- [x] Create `MonsterFields.java` — `DataField<T>` constants per monster family:
  - `BELLY_LEVEL` (DataType.INT, key `"BellyLevel"`)
  - `PLANTING_ENABLED` (DataType.BOOLEAN, key `"Plant"`)
  - `SOUND_ENABLED` (DataType.BOOLEAN, key `"Sound"`)
  - `HUNGER_WATCH_COOLDOWN` (DataType.INT — Gourdragora-specific, deferred until subclass implemented)
  - `BELLY_LEGACY` (DataType.BOOLEAN — migration bridge constant only, not registered in schema)
- [x] Implement `configureSchema()` in `MonstersFamily` base — registers `BELLY_LEVEL`, `PLANTING_ENABLED`, `SOUND_ENABLED`; subclasses with extra fields override and call `super.configureSchema()`
- [x] Implement `provideFieldValue` / `consumeFieldValue` in `MonsterEntity` — dispatches on `MonsterFields` constants by identity; pushes values into `SynchedEntityData` accessors on load
- [x] Implement `MigrationStep_V1_MG.java`:
  - `TextureID` int disambiguation: `textureId <= baseTextureCount` → base texture key; `textureId > baseTextureCount` → `BellyLevel = textureId - baseTextureCount`
  - Queries `BASE_TEXTURE_COUNTS` static registry populated by each family's static block
  - `Belly` (bool) migration: `true → 2` (TUMMY), `false → 0` (SLIM)
  - `ModelID` (int) → `ModelVariant` (string) via family prefix + `"_default"` key
  - `AnimatorID` (int) → `AnimatorVariant` (string) via family prefix + `"_default"` key
  - Direct copy: `Plant`, `Sound`, `Notification` (renamed to `NotificationEnabled`)
  - Logs an error and defaults to `BellyLevel = 0` if family not found in registry
- [x] Register migration chain on each monster family — `[MigrationStep_V1_MG]` wired in `MonstersFamily.configureSchema()`
- [x] Remove legacy flat-write code from `MonsterEntity.addAdditionalSaveData` / `readAdditionalSaveData` — `MonsterEntity` had no such overrides; pipeline driven entirely by `NativeEntity` base + schema hooks
- [x] Verify full build passes — **13 files, 0 diagnostics**

### Phase 4 — Validation
> **Depends on Phase 2 and Phase 3 both complete.**

- [ ] **Robot save migration**: load a 1.20.4 robot world save — verify all stats (level, exp, protection values, base coordinates, sitting pose, health) migrate with no data loss
- [ ] **Gen1-Fabric migration**: locate archive Gen1-Fabric saves (if available at `archive/1.16.X/`) — verify locale strings resolve to stable keys via lookup table
- [ ] **Gen1-Forge migration**: load a Gen1-Forge / Gen2 save — verify flat PascalCase keys migrate correctly to `EntityData` compound
- [ ] **Overlay slot persistence**: spawn a Mandrake Flower (RANDOM hairstyle slot), save and reload world — verify hairstyle slot is preserved
- [ ] **Belly level persistence**: set a Wisp to TUMMY belly level, save and reload world — verify `BellyLevel = 2` is preserved correctly
- [ ] **Corrupt field handling**: manually corrupt a `Level` field in an NBT file to an out-of-range value — verify entity loads with default value `0` and logs a warning rather than throwing
- [ ] **McVersion field**: verify `McVersion "1.21.1"` is written correctly on first save after migration
- [ ] **SynchedEntityData authority**: verify that after world reload, `TextureVariant`, `State`, `Notification` synced fields reflect the NBT-loaded values, not stale pre-load values
- [ ] **OverlaySlots compound**: verify INTERACTIVE overlay slots (Gourdragora carving) persist across world reload
- [ ] Full build passes (0 errors, 0 warnings from pipeline) across all modules

---

## Implementation Notes

### Critical Build Order
Phase 0 must publish a HZLib Common snapshot before any Phase 1 work begins on classes that import `DataCompound` or `NbtAdapterFactory`. Do not start Phase 2 or 3 until Phase 1 is complete and the HZLib artifact is available.

### `NbtAdapterFactory` Registration Timing
Must register in the **earliest** mod init hook — before any entity is loaded or deserialized. If `NbtAdapterFactory.wrap()` is called before registration, it will throw. The registration call belongs in the same hook used for registry init, not in post-init or gameplay events.

### `DataField.getKey()` Access
`getKey()` is package-private within `hzlib.common.nbt`. Call sites must reference the `DataField<T>` constant directly — never the key string. This is the whole point of the typed handle design.

### Migration Idempotency
Every `MigrationStep.migrate()` must be safe to call on an already-migrated compound. Test each step individually: run it twice on the same input and verify the output is identical on both runs.

### Gen1-Fabric Locale Table
The `LOCALE_TO_STABLE_KEY` table in `MigrationStep_V0_Fabric` must be complete before Phase 4 validation. Cross-reference the entity registry in `archive/1.16.X/rlovelyr-fabric/src/main/java/.../entity/` to enumerate every entity type that shipped in that build. Log a distinct warning for any locale string not found in the table — do not silently use the raw locale string as the texture key.

### Belly Disambiguation Safety
`MigrationStep_V1_MG` must handle the case where the family is not in the registry at migration time (world converter tool pass, entity loaded before registry is populated). Default to `BellyLevel = 0`, log an error including the entity type ID and the raw `TextureID` value for post-mortem diagnosis.

### `EntityData.java` and `CombatStatsNBT` Deletion
Only delete these after Phase 1 and Phase 2 verify successfully. They are superseded — not broken — so deletion is safe only once the replacement pipeline has been validated end-to-end.

---

## Story Points

| Phase | Tasks | Points |
|-------|-------|--------|
| Phase 0 — DataCompound / NbtAdapterFactory | 8 tasks | 8 |
| Phase 1 — Pipeline core (HZLib) | 11 tasks | 21 |
| Phase 2 — LovelyLib robot schema + migrations | 13 tasks | 21 |
| Phase 3 — Monsters & Girls schema + migrations | 8 tasks | 13 |
| Phase 4 — Validation | 10 tasks | 8 |
| **Total** | | **71** |

> This is a large sprint. If Phase 3 (Monsters & Girls) creates schedule pressure, it may be deferred to Sprint 12 with Phase 4 running only against robot saves. The pipeline is still fully functional with only robot migration — M&G migration is an additive step.

---

## Files Created / Modified

### HZLib Common — new
- `sources/common/hzlib-1.21.1/Common/.../nbt/DataCompound.java`
- `sources/common/hzlib-1.21.1/Common/.../nbt/NbtAdapterFactory.java`
- `sources/common/hzlib-1.21.1/Common/.../nbt/McVersionProvider.java`
- `sources/common/hzlib-1.21.1/.../data/DataType.java`
- `sources/common/hzlib-1.21.1/.../data/DataField.java`
- `sources/common/hzlib-1.21.1/.../data/FieldValueProvider.java`
- `sources/common/hzlib-1.21.1/.../data/FieldValueConsumer.java`
- `sources/common/hzlib-1.21.1/.../data/EntityDataSchema.java`
- `sources/common/hzlib-1.21.1/.../data/MigrationStep.java`
- `sources/common/hzlib-1.21.1/.../data/MigrationChain.java`

### HZLib 1.21.1 source set — new
- `sources/mc-1.21.1/.../nbt/CompoundTagDataCompound.java`

### HZLib Common — modified
- `sources/common/hzlib-1.21.1/Common/.../NativeEntity.java` — full pipeline wiring
- `sources/common/hzlib-1.21.1/Common/.../NativeEntityFamily.java` — `configureSchema()` hook added

### HZLib Common — deleted
- `sources/common/hzlib-1.21.1/Common/.../data/CombatStatsNBT.java`
- `sources/common/hzlib-1.21.1/Common/.../data/ProtectionStatsNBT.java`
- `sources/common/hzlib-1.21.1/Common/.../data/EnchantmentStatsNBT.java`

### LovelyLib Common — new
- `sources/common/lovelylib-1.21.1/Common/.../data/RobotFields.java`
- `sources/common/lovelylib-1.21.1/Common/.../data/migration/MigrationStep_V0_Fabric.java`
- `sources/common/lovelylib-1.21.1/Common/.../data/migration/MigrationStep_V0_Forge.java`
- `sources/common/lovelylib-1.21.1/Common/.../data/migration/MigrationStep_V1_1204.java`

### LovelyLib Common — modified
- `sources/common/lovelylib-1.21.1/Common/.../RobotFamily.java` — `configureSchema()` implementation
- `sources/common/lovelylib-1.21.1/Common/.../RobotEntity.java` — `provideFieldValue` / `consumeFieldValue`, remove flat-write overrides

### LovelyLib Common — deleted
- `sources/common/lovelylib-1.21.1/Common/.../data/EntityDataMigration.java`

### Monsters & Girls — new
- `sources/monsters/.../data/MonsterFields.java`
- `sources/monsters/.../data/migration/MigrationStep_V1_MG.java`

### Monsters & Girls — modified
- Each family class — `configureSchema()` + `baseTextureCount` declaration
- `MonsterEntity.java` — `provideFieldValue` / `consumeFieldValue`, remove legacy flat-write code

---

## Unforeseen Work — Template Environment Setup (1.20.1 & 1.16.5)

**Added**: 2026-07-11  
**Reason**: The `template-mod-1.20.1` and `template-mod-1.16.5` directories were created by copying `template-mod-1.21.1` but were never updated — every version number, loader plugin, and GeckoLib coordinate still pointed at 1.21.1. Additionally, a comparative audit of all production mods against the base template revealed several deviations that had accumulated in production but were never propagated back to the template.

### Completed Tasks

- [x] **Audit all production mods against template-mod-1.21.1** — identified 4 structural deviations that needed back-propagation
- [x] **template-mod-1.20.1 — gradle.properties**: Java 17, Forge 47.1.46, FG `[47,)`, official mappings 1.20.1, GeckoLib 4.2.2 (Forge + Fabric), fabric-loom 1.0-SNAPSHOT, fabric-loader 0.14.22, fabric-yarn 1.20.1+build.10, fabric-api 0.83.0+1.20.1. NeoForge properties removed. Added `hzlib_project_path`. Updated `lovelylib_project_path` to `1.20.1`.
- [x] **template-mod-1.20.1 — build.gradle**: fabric-loom 1.0-SNAPSHOT plugin, FG `[5.1.+,6.0)` plugin, NeoForge plugin removed, Java 17, production-grade `refreshDependencies` handling both HZLib and LovelyLib via per-loader `build/libs` dirs (Common/Fabric/Forge only), excludes `*-dev.jar`. Repository block uses `rootProject.file()` + per-loader flatDir for both libs.
- [x] **template-mod-1.20.1 — settings.gradle**: NeoForge `exclusiveContent` repo block removed, `includeModloader("NeoForge")` removed, explanatory comment added.
- [x] **template-mod-1.20.1 — Forge/build.gradle**: FG `[5.1.+,6.0)` plugin, `reobf=false` removed (not valid for FG5), `reobfJar` finalizer added, dual-path dependency resolution for both LovelyLib and HZLib (checks build JAR path OR local libs path).
- [x] **template-mod-1.20.1 — Fabric/build.gradle**: dual-path dependency resolution for both LovelyLib and HZLib (same pattern as Forge).
- [x] **template-mod-1.20.1 — NeoForge/**: directory deleted entirely (NeoForge did not exist for MC 1.20.1).
- [x] **template-mod-1.20.1 — buildSrc/multiloader-common.gradle**: `neoforge_*` properties removed from `processResources` expand map, `fabric_api_version` guarded with `project.hasProperty()`, `java_level` guarded with `project.hasProperty()` fallback to `"JAVA_${java_version}"`, NeoForge `mods.toml` removed from `filesMatching`.
- [x] **template-mod-1.20.1 — gradle-wrapper.properties**: upgraded to Gradle 8.8.
- [x] **template-mod-1.16.5 — gradle.properties**: Java 16, Forge 36.2.34, FG `[36,)`, official mappings 1.16.5, GeckoLib 3.0.106 (Forge) / 3.0.107 (Fabric), fabric-loom 0.8-SNAPSHOT, fabric-loader 0.14.21, yarn `1.16.5+build.10:v2`. No `fabric_api_version` (bundled in 1.16.5). NeoForge properties removed. Added `hzlib_project_path`. `lovelylib_project_path` set to `1.16.5`.
- [x] **template-mod-1.16.5 — build.gradle**: fabric-loom 0.8-SNAPSHOT plugin, FG `5.1.+` plugin, NeoForge plugin removed, Java 16, production-grade `refreshDependencies` for HZLib and LovelyLib (Common/Fabric/Forge dirs), excludes `*-dev.jar`.
- [x] **template-mod-1.16.5 — settings.gradle**: NeoForge repo + `includeModloader` removed, explanatory comment added.
- [x] **template-mod-1.16.5 — Forge/build.gradle**: FG `5.1.+` plugin, old-style run configs with `mods {}` block (1.16.5 style), `reobfJar` finalizer, dual-path dependency resolution.
- [x] **template-mod-1.16.5 — Fabric/build.gradle**: yarn mappings (not `officialMojangMappings()` — not available in loom 0.8), no fabric-api dep, dual-path dependency resolution.
- [x] **template-mod-1.16.5 — NeoForge/**: directory deleted entirely (NeoForge did not exist for MC 1.16.5).
- [x] **template-mod-1.16.5 — buildSrc/multiloader-common.gradle**: same optional-property guards as 1.20.1 — `neoforge_*` removed, `fabric_api_version` and `java_level` guarded.
- [x] **template-mod-1.16.5 — gradle-wrapper.properties**: upgraded to Gradle 8.8.

### Back-Propagated to template-mod-1.21.1

The following deviations from the 1.21.1 base template were found in production mods and back-propagated:

- [x] **build.gradle — repositories block**: changed from naive `flatDir { dirs 'libs' }` to `rootProject.file('libs')` + per-loader build output dirs for both LovelyLib and HZLib via `flatDir` entries (Common/Fabric/Forge/NeoForge).
- [x] **build.gradle — refreshDependencies task**: extended to handle **both** HZLib and LovelyLib (previously only handled LovelyLib). Now iterates `['Common','Fabric','Forge','NeoForge']` loaders for each library, excludes `*-dev.jar`.
- [x] **Forge/build.gradle — dependency resolution**: upgraded from single-path `lovelyLibJar.exists()` check to dual-path (`lovelyLibLocalJar.exists() || lovelyLibBuildJar.exists()`). HZLib branch changed from `else if` (mutually exclusive with LovelyLib) to independent `if` block. Same pattern applied.
- [x] **Fabric/build.gradle — dependency resolution**: same dual-path upgrade and HZLib independence fix as Forge.
- [x] **NeoForge/build.gradle — dependency resolution**: same dual-path upgrade and HZLib independence fix.
- [x] **buildSrc/multiloader-common.gradle — processResources**: `java_level` made optional with `project.hasProperty('java_level')` guard to maintain compatibility with library projects (HZLib) that omit this property.
- [x] **gradle.properties**: added `hzlib_project_path=../../common/hzlib-1.21.1` alongside the existing `lovelylib_project_path`.

### Key Version Reference Table

| Property | 1.16.5 | 1.20.1 | 1.21.1 |
|---|---|---|---|
| Java | 8 | 17 | 21 |
| Forge | 36.2.42 | 47.4.0 | 52.1.0 |
| ForgeGradle plugin | `5.1.+` | `[6.0,6.2)` via `${gradle_version}` | `[6.0.24,6.2)` via `${gradle_version}` |
| FG plugin property | hardcoded in `Forge/build.gradle` | `gradle_version=[6.0,6.2)` in `gradle.properties` | `gradle_version=[6.0.24,6.2)` in `gradle.properties` |
| Gradle wrapper | **7.2** (FG5 cap) | 8.8 | 8.10 |
| reobfJar | required | required | not needed (`reobf=false`) |
| NeoForge | — | — | 21.1.214 |
| fabric-loom | `0.7-SNAPSHOT` | `1.1-SNAPSHOT` via `${loom_version}` | `1.7-SNAPSHOT` via `${loom_version}` |
| fabric-loader | 0.11.3 | 0.17.3 | 0.16.9 |
| Fabric mappings | **yarn** `1.16.5+build.9:v2` (officialMojangMappings not in loom 0.7) | `loom.officialMojangMappings()` | `loom.officialMojangMappings()` |
| fabric-api | `0.34.2+1.16` | `0.92.6+1.20.1` | `0.105.0+1.21.1` |
| GeckoLib (Forge) | `geckolib-forge-1.16.5:3.0.106` | `geckolib-forge-1.20.1:4.2.2` | `geckolib-forge-1.21.1:4.7.3` |
| GeckoLib (Fabric) | `geckolib-fabric-1.16.5:3.0.107` | `geckolib-fabric-1.20.1:4.2.2` | `geckolib-fabric-1.21.1:4.7.3` |
| GeckoLib (NeoForge) | — | — | `geckolib-neoforge-1.21.1:4.7.3` |
