# Sprint Task: Ecosystem Terminology & Rename System

**Status**: 🔄 ACTIVE  
**Started**: 2026-06-20  
**Target Completion**: 2026-07-04  
**Priority**: High  
**Complexity**: High  

## Sprint Goal

Execute ADR_018 — rename all `Internal*` classes across HZLib, LovelyLib, and Monsters & Girls to align with the canonical Family / Variant / Appearance terminology. Every class name must reflect its tier and role after this sprint.

## Strategic Context

**Source ADR**: `docs/development/decisions/ADR_018_Ecosystem_Terminology_and_Rename_System.md`  
**Terminology Reference**: `docs/documentation/TERMINOLOGY.md`

**Why this sprint matters**: The ecosystem has three parallel `Internal*` naming patterns that obscure tier and role. The terminology is now formally defined. This sprint closes the gap between the vocabulary and the code — making the architecture self-describing for all future contributors and AI agents.

**Prerequisite**: Sprint 08 complete (InternalEntity Consolidation + OverlayFeature) ✅

## Objectives

### Phase 1 — HZLib Common
- [ ] `InternalEntity` → `NativeEntity`
- [ ] `InternalEntityType<T>` → `NativeEntityFamily<T>`
- [ ] `InternalLogic` → `EntityLogic`
- [ ] `InternalParticle` → `EntityParticles`
- [ ] `InternalLayerRenderer<T>` (Common) → `LayerRenderPipeline<T>`

### Phase 2 — HZLib Loaders (Fabric / Forge / NeoForge)
- [ ] `InternalAnimation` → `NativeAnimation`
- [ ] `InternalModel<T>` → `NativeModel<T>`
- [ ] `InternalLayerRenderer<T>` (loader) → `NativeRenderer<T>`

### Phase 3 — LovelyLib Common
- [ ] `NativeEntityType` → `RobotFamily`
- [ ] `LovelyRobotType` → `RobotFamilyRegistry`
- [ ] `EntityVariant` → `RobotVariant`

### Phase 4 — LovelyLib Loaders (Fabric / Forge / NeoForge)
- [ ] `InternalAnimation` → `RobotAnimation`

### Phase 5 — LovelyLib Source Registries
- [ ] `LegacyRobotType` → `LegacyRobotFamilies`
- [ ] `RebootRobotType` → `RebootRobotFamilies`
- [ ] `TributeRobotType` → `TributeRobotFamilies`

### Phase 6 — Monsters & Girls Common
- [ ] `NativeEntityType<T>` → `MonstersFamily<T>`
- [ ] `MandrakeType` → `MandrakeFamily`
- [ ] `MushroomType` → `MushroomFamily`
- [ ] `BeeType` → `BeeFamily`
- [ ] `GlobberieType` → `GlobberieFamily`
- [ ] `GourdragoraType` → `GourdragoraFamily`
- [ ] `MaidenType` → `MaidenFamily`
- [ ] `SlimeType` → `SlimeFamily`
- [ ] `SpookType` → `SpookFamily`
- [ ] `WispType` → `WispFamily`

### Phase 7 — Validation
- [ ] Full build passes across all projects (0 errors)
- [ ] Runtime smoke test for robots and monsters

---

## Implementation Tasks

### Phase 1: HZLib Common

#### Task 1.1 — Rename `InternalEntity` → `NativeEntity`
- **Priority**: Critical (blocks all other tasks)
- **Story Points**: 3
- **Location**: `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/entity/`
- **Description**: Rename the class file and update the class declaration. IDE rename refactoring handles all intra-package references automatically. Verify all dependents compile.
- **Key reference sites to verify**:
  - `MonsterEntity.java` (monsters_girls) — extends `InternalEntity`
  - `RobotEntity.java` (hzlib) — extends `InternalEntity`
  - All loader-specific entity classes that reference the type
- **Acceptance Criteria**:
  - [ ] File renamed to `NativeEntity.java`
  - [ ] Class declaration reads `public abstract class NativeEntity extends TamableAnimal`
  - [ ] HZLib Common compiles with 0 errors
  - [ ] No remaining references to `InternalEntity` in active sources (excluding archive/)

---

#### Task 1.2 — Rename `InternalEntityType<T>` → `NativeEntityFamily<T>`
- **Priority**: Critical
- **Story Points**: 3
- **Location**: `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/entity/`
- **Description**: Rename the class file and declaration. Update all type parameter usages and generic bounds across the codebase.
- **Key reference sites to verify**:
  - `lovelylib NativeEntityType` — `extends InternalEntityType<NativeEntityType>`
  - `monsters_girls NativeEntityType<T>` — `extends InternalEntityType<T>`
  - `InternalEntity.nativeEntity` field — type changes to `NativeEntityFamily<?>`
- **Acceptance Criteria**:
  - [ ] File renamed to `NativeEntityFamily.java`
  - [ ] Class declaration reads `public abstract class NativeEntityFamily<T extends NativeEntityFamily<T>>`
  - [ ] HZLib Common compiles with 0 errors
  - [ ] No remaining references to `InternalEntityType` in active sources

---

#### Task 1.3 — Rename `InternalLogic` → `EntityLogic`
- **Priority**: High
- **Story Points**: 1
- **Location**: `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/entity/internal/`
- **Description**: Rename file and class. Static utility — no inheritance impact. Update all call sites.
- **Acceptance Criteria**:
  - [ ] File renamed to `EntityLogic.java`
  - [ ] All call sites updated (search for `InternalLogic.` across active sources)
  - [ ] HZLib Common compiles with 0 errors

---

#### Task 1.4 — Rename `InternalParticle` → `EntityParticles`
- **Priority**: High
- **Story Points**: 1
- **Location**: `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/entity/internal/`
- **Description**: Rename file and class. Static utility — no inheritance impact. Note the plural: `EntityParticles` (a bag of particle effects).
- **Acceptance Criteria**:
  - [ ] File renamed to `EntityParticles.java`
  - [ ] All call sites updated (search for `InternalParticle.` across active sources)
  - [ ] HZLib Common compiles with 0 errors

---

#### Task 1.5 — Rename Common `InternalLayerRenderer<T>` → `LayerRenderPipeline<T>`
- **Priority**: High
- **Story Points**: 2
- **Location**: `sources/common/hzlib-1.21.1/Common/src/main/java/net/heriazone/hzlib/api/rendering/`
- **Description**: Rename the Common (non-GeckoLib) layer coordinator class. This is separate from the loader-specific `InternalLayerRenderer` which extends `GeoEntityRenderer` (Task 2.3).
- **Acceptance Criteria**:
  - [ ] File renamed to `LayerRenderPipeline.java`
  - [ ] All usages updated
  - [ ] HZLib Common compiles with 0 errors

---

### Phase 2: HZLib Loaders

#### Task 2.1 — Rename `InternalAnimation` → `NativeAnimation` (Fabric / Forge / NeoForge)
- **Priority**: Critical (loader rename, 3 files)
- **Story Points**: 3
- **Location**: `sources/common/hzlib-1.21.1/{Fabric,Forge,NeoForge}/src/main/java/net/heriazone/hzlib/api/entity/`
- **Description**: Rename in all three loader modules. The class extends no HZLib type directly — it is a static factory class. Update all references in `InternalModel.setCustomAnimations()` and all entity `registerControllers()` methods.
- **Acceptance Criteria**:
  - [ ] Renamed in Fabric loader
  - [ ] Renamed in Forge loader
  - [ ] Renamed in NeoForge loader
  - [ ] All three loaders compile with 0 errors
  - [ ] No remaining references to `InternalAnimation` in hzlib loader sources

---

#### Task 2.2 — Rename `InternalModel<T>` → `NativeModel<T>` (Fabric / Forge / NeoForge)
- **Priority**: High (loader rename, 3 files)
- **Story Points**: 2
- **Location**: `sources/common/hzlib-1.21.1/{Fabric,Forge,NeoForge}/src/main/java/net/heriazone/hzlib/api/entity/`
- **Description**: Rename in all three loader modules. This is the abstract GeoModel base. All concrete model classes that extend it must update their `extends` clause. `setCustomAnimations()` calls `NativeAnimation` (already renamed in Task 2.1).
- **Key reference sites**: All `*Model.java` classes in monsters_girls and lovelylib loaders.
- **Acceptance Criteria**:
  - [ ] Renamed in Fabric, Forge, NeoForge loaders
  - [ ] All concrete model subclasses updated
  - [ ] All three loaders compile with 0 errors

---

#### Task 2.3 — Rename Loader `InternalLayerRenderer<T>` → `NativeRenderer<T>` (Fabric / Forge / NeoForge)
- **Priority**: High (loader rename, 3 files)
- **Story Points**: 3
- **Location**: `sources/common/hzlib-1.21.1/{Fabric,Forge,NeoForge}/src/main/java/net/heriazone/hzlib/api/entity/`
- **Description**: Rename in all three loader modules. This is the GeckoLib `GeoEntityRenderer` subclass — distinct from the Common `LayerRenderPipeline` (Task 1.5). All concrete renderer classes that extend it must update their `extends` clause.
- **Key reference sites**: All `*Renderer.java` classes in monsters_girls and lovelylib loaders.
- **Acceptance Criteria**:
  - [ ] Renamed in Fabric, Forge, NeoForge loaders
  - [ ] All concrete renderer subclasses updated
  - [ ] All three loaders compile with 0 errors

---

### Phase 3: LovelyLib Common

#### Task 3.1 — Rename `NativeEntityType` → `RobotFamily`
- **Priority**: Critical
- **Story Points**: 3
- **Location**: `sources/common/lovelylib-1.21.1/Common/src/main/java/net/heriazone/lovelylib/common/entity/`
- **Description**: Rename file and class. Update the `extends NativeEntityFamily<NativeEntityType>` bound to `extends NativeEntityFamily<RobotFamily>`. Update all references in `LovelyRobotType`, `LegacyRobotType`, `RebootRobotType`, `TributeRobotType`, and the three mod variant `Entities` classes.
- **Acceptance Criteria**:
  - [ ] File renamed to `RobotFamily.java`
  - [ ] Class declaration reads `public class RobotFamily extends NativeEntityFamily<RobotFamily>`
  - [ ] `LovelyRobotType.TYPES` list type updated to `List<RobotFamily>`
  - [ ] LovelyLib Common compiles with 0 errors

---

#### Task 3.2 — Rename `LovelyRobotType` → `RobotFamilyRegistry`
- **Priority**: High
- **Story Points**: 2
- **Location**: `sources/common/lovelylib-1.21.1/Common/src/main/java/net/heriazone/lovelylib/common/entity/`
- **Description**: Rename file and class. `LegacyRobotType`, `RebootRobotType`, `TributeRobotType` all extend this — update their `extends` clauses after this rename.
- **Acceptance Criteria**:
  - [ ] File renamed to `RobotFamilyRegistry.java`
  - [ ] All `extends LovelyRobotType` updated to `extends RobotFamilyRegistry`
  - [ ] LovelyLib Common compiles with 0 errors

---

#### Task 3.3 — Rename `EntityVariant` → `RobotVariant`
- **Priority**: High
- **Story Points**: 2
- **Location**: `sources/common/lovelylib-1.21.1/Common/src/main/java/net/heriazone/lovelylib/common/entity/enums/`
- **Description**: Rename the enum file and declaration. `RobotFamily` holds an `EntityVariant variant` field — update to `RobotVariant variant`. Update all references in `LovelyConstant.ALL_VARIANTS`, `TRIBUTE_VARIANTS`, `LEGACY_VARIANTS`, `REBOOT_VARIANTS`, and all `create(EntityVariant)` call sites.
- **Acceptance Criteria**:
  - [ ] File renamed to `RobotVariant.java`
  - [ ] All enum usages updated (e.g., `RobotVariant.Bunny`, `RobotVariant.Dragon`)
  - [ ] LovelyLib Common compiles with 0 errors

---

### Phase 4: LovelyLib Loaders

#### Task 4.1 — Rename `InternalAnimation` → `RobotAnimation` (Fabric / Forge / NeoForge)
- **Priority**: High (loader rename, 3 files)
- **Story Points**: 3
- **Location**: `sources/common/lovelylib-1.21.1/{Fabric,Forge,NeoForge}/src/main/java/net/heriazone/lovelylib/hzlib/api/entity/`
- **Description**: Rename in all three loader modules. This is the robot-specific animation override that adds tail config and robot-specific locomotion. Distinct from hzlib's `NativeAnimation` (Task 2.1). Update all `registerControllers()` methods across robot entity classes.
- **Acceptance Criteria**:
  - [ ] Renamed in Fabric, Forge, NeoForge loaders
  - [ ] All `registerControllers()` call sites updated
  - [ ] All three loaders compile with 0 errors

---

### Phase 5: LovelyLib Source Registries

#### Task 5.1 — Rename `LegacyRobotType` → `LegacyRobotFamilies`
- **Priority**: High
- **Story Points**: 1
- **Location**: `sources/common/lovelylib-1.21.1/Common/src/main/java/net/heriazone/lovelylib/source/legacy/`
- **Description**: Rename file and class. The static field names (`BUNNY`, `DRAGON`, etc.) stay unchanged — they are correct as family instance names.
- **Acceptance Criteria**:
  - [ ] File renamed to `LegacyRobotFamilies.java`
  - [ ] All imports in Legacy mod variant updated
  - [ ] LovelyLib + Legacy mod compile with 0 errors

---

#### Task 5.2 — Rename `RebootRobotType` → `RebootRobotFamilies`
- **Priority**: High
- **Story Points**: 1
- **Location**: `sources/common/lovelylib-1.21.1/Common/src/main/java/net/heriazone/lovelylib/source/reboot/`
- **Acceptance Criteria**:
  - [ ] File renamed to `RebootRobotFamilies.java`
  - [ ] All imports in Reboot mod variant updated
  - [ ] LovelyLib + Reboot mod compile with 0 errors

---

#### Task 5.3 — Rename `TributeRobotType` → `TributeRobotFamilies`
- **Priority**: High
- **Story Points**: 1
- **Location**: `sources/common/lovelylib-1.21.1/Common/src/main/java/net/heriazone/lovelylib/source/tribute/`
- **Acceptance Criteria**:
  - [ ] File renamed to `TributeRobotFamilies.java`
  - [ ] All imports in Tribute mod variant updated
  - [ ] LovelyLib + Tribute mod compile with 0 errors

---

### Phase 6: Monsters & Girls Common

#### Task 6.1 — Rename `NativeEntityType<T>` → `MonstersFamily<T>`
- **Priority**: Critical (blocks all concrete family renames)
- **Story Points**: 3
- **Location**: `sources/monsters/monsters_girls-1.21.1/Common/src/main/java/net/heriazone/monsters_girls/entity/`
- **Description**: Rename file and class. Update the `extends NativeEntityFamily<T>` bound (already correct after Phase 1). Update all concrete family classes that `extends NativeEntityType<X>` to `extends MonstersFamily<X>`.
- **Acceptance Criteria**:
  - [ ] File renamed to `MonstersFamily.java`
  - [ ] Class declaration reads `public abstract class MonstersFamily<T extends MonstersFamily<T>> extends NativeEntityFamily<T>`
  - [ ] monsters_girls Common compiles with 0 errors

---

#### Task 6.2 — Rename all nine concrete family classes
- **Priority**: High
- **Story Points**: 5
- **Location**: `sources/monsters/monsters_girls-1.21.1/Common/src/main/java/net/heriazone/monsters_girls/entity/custom/`
- **Description**: Rename all nine `*Type` classes to `*Family` in a single pass. IDE rename each file; confirm all `MonstersEntities` registration sites and entity class `extends` clauses update correctly.

| File | Renamed to |
|---|---|
| `MandrakeType.java` | `MandrakeFamily.java` |
| `MushroomType.java` | `MushroomFamily.java` |
| `BeeType.java` | `BeeFamily.java` |
| `GlobberieType.java` | `GlobberieFamily.java` |
| `GourdragoraType.java` | `GourdragoraFamily.java` |
| `MaidenType.java` | `MaidenFamily.java` |
| `SlimeType.java` | `SlimeFamily.java` |
| `SpookType.java` | `SpookFamily.java` |
| `WispType.java` | `WispFamily.java` |

- **Key reference sites**: `MonstersEntities.java` (all loaders), all `*Entity.java` classes that reference their type (e.g., `GourdragoraEntity` references `GourdragoraType`).
- **Acceptance Criteria**:
  - [ ] All nine files renamed
  - [ ] All `MonstersEntities` registration sites reference the new names
  - [ ] All entity classes that hold a reference to their family class updated
  - [ ] `MandrakeFamily.SLOT_HAIR`, `MandrakeFamily.SLOT_EMISSIVE` constants unchanged (slot keys don't change)
  - [ ] monsters_girls Common compiles with 0 errors
  - [ ] monsters_girls Fabric compiles with 0 errors

---

### Phase 7: Validation

#### Task 7.1 — Full Build Verification
- **Priority**: Critical
- **Story Points**: 3
- **Description**: Clean build all projects in dependency order. Zero errors required before marking sprint complete.
- **Build order**:
  1. HZLib Common → HZLib Fabric, Forge, NeoForge
  2. LovelyLib Common → LovelyLib Fabric, Forge, NeoForge
  3. Legacy mod (llovelyr-1.21.1) — Fabric, Forge, NeoForge
  4. Reboot mod (rlovelyr-1.21.1) — Fabric, Forge, NeoForge
  5. Tribute mod (tlovelyr-1.21.1) — Fabric, Forge, NeoForge
  6. Monsters & Girls (monsters_girls-1.21.1) — Fabric
- **Acceptance Criteria**:
  - [ ] All projects build with 0 errors
  - [ ] No remaining `InternalEntity`, `InternalEntityType`, `InternalAnimation`, `InternalModel`, `InternalLayerRenderer`, `InternalLogic`, `InternalParticle`, `NativeEntityType`, `LovelyRobotType`, `LegacyRobotType`, `RebootRobotType`, `TributeRobotType`, `EntityVariant`, or `*Type` (monster family) references in any active source file

---

#### Task 7.2 — Runtime Smoke Test
- **Priority**: High
- **Story Points**: 2
- **Description**: In-game validation that nothing was broken by the renames. The renames are purely nominal — no behavior changes — so a light smoke test is sufficient.
- **Test checklist**:
  - [ ] Spawn all 7 robot types (Legacy) — correct textures, correct stats, animate correctly
  - [ ] Robot dye interaction changes color
  - [ ] Spawn Mandrake Flower — hairstyle overlay renders correctly
  - [ ] Spawn Gourdragora Golden — shears cycle carving pattern correctly
  - [ ] Spawn Mushroom Brown — correct biome texture applied

---

## Technical Notes

### Rename order is critical

HZLib must be renamed before LovelyLib. LovelyLib must be renamed before the three mod variants (Legacy, Reboot, Tribute). Monsters & Girls can be renamed in parallel with LovelyLib once HZLib is done.

Renaming a class that is depended on before its dependents causes compilation failures. Follow the phase order strictly.

### IDE rename vs. find-and-replace

Use IDE semantic rename (F2 / Rename Symbol) rather than text find-and-replace. IDE rename handles:
- Import statements
- Generic type bounds (`extends InternalEntityType<T>` → `extends NativeEntityFamily<T>`)
- Method parameter types
- Field types
- Javadoc `{@link}` references

Find-and-replace may miss generic bounds and will produce false positives in comments. Use it only to verify no references remain after IDE rename.

### What does NOT change

Per ADR_018:
- The `nativeEntity` **field name** on `NativeEntity` stays `nativeEntity` — only the type of the field changes to `NativeEntityFamily<?>`
- `MonsterEntity` and `RobotEntity` class names are unchanged
- `EntityTexture` enum is unchanged
- `IInternalRenderLayer` interface is unchanged
- The `dynamic/` and `monsters/` legacy stub packages in HZLib are not touched

### JavaDoc updates

After renaming, update the class-level JavaDoc on each renamed class to reflect the new name in `@see` and `{@link}` references. The `// Class: ClassName` footer comments must also be updated to match the new class name.

---

## Story Points Summary

| Task | Points | Phase | Status |
|---|---|---|---|
| 1.1 `InternalEntity` → `NativeEntity` | 3 | 1 | ⬜ |
| 1.2 `InternalEntityType` → `NativeEntityFamily` | 3 | 1 | ⬜ |
| 1.3 `InternalLogic` → `EntityLogic` | 1 | 1 | ⬜ |
| 1.4 `InternalParticle` → `EntityParticles` | 1 | 1 | ⬜ |
| 1.5 Common `InternalLayerRenderer` → `LayerRenderPipeline` | 2 | 1 | ⬜ |
| 2.1 HZLib `InternalAnimation` → `NativeAnimation` (×3 loaders) | 3 | 2 | ⬜ |
| 2.2 `InternalModel` → `NativeModel` (×3 loaders) | 2 | 2 | ⬜ |
| 2.3 Loader `InternalLayerRenderer` → `NativeRenderer` (×3 loaders) | 3 | 2 | ⬜ |
| 3.1 `NativeEntityType` → `RobotFamily` | 3 | 3 | ⬜ |
| 3.2 `LovelyRobotType` → `RobotFamilyRegistry` | 2 | 3 | ⬜ |
| 3.3 `EntityVariant` → `RobotVariant` | 2 | 3 | ⬜ |
| 4.1 LovelyLib `InternalAnimation` → `RobotAnimation` (×3 loaders) | 3 | 4 | ⬜ |
| 5.1 `LegacyRobotType` → `LegacyRobotFamilies` | 1 | 5 | ⬜ |
| 5.2 `RebootRobotType` → `RebootRobotFamilies` | 1 | 5 | ⬜ |
| 5.3 `TributeRobotType` → `TributeRobotFamilies` | 1 | 5 | ⬜ |
| 6.1 `NativeEntityType<T>` → `MonstersFamily<T>` | 3 | 6 | ⬜ |
| 6.2 Nine concrete `*Type` → `*Family` classes | 5 | 6 | ⬜ |
| 7.1 Full build verification | 3 | 7 | ⬜ |
| 7.2 Runtime smoke test | 2 | 7 | ⬜ |
| **Total** | **44** | | |

---

## Dependencies

- Sprint 08 complete ✅ (InternalEntity Consolidation, OverlayFeature)
- ADR_018 accepted ✅ (`docs/development/decisions/ADR_018_Ecosystem_Terminology_and_Rename_System.md`)
- TERMINOLOGY.md published ✅ (`docs/documentation/TERMINOLOGY.md`)

## Risks

### Risk 1: Partial rename breaks build
**Probability**: High if phases are not followed in order  
**Impact**: High — compilation failures across multiple projects  
**Mitigation**: Follow the phase order strictly. Complete and verify each phase before starting the next.

### Risk 2: Generic type bounds missed by IDE rename
**Probability**: Medium — `extends InternalEntityType<T>` bounds require careful handling  
**Impact**: Medium — compile errors with clear error messages  
**Mitigation**: After IDE rename of each class, run a project-scoped search for the old name to catch any missed references before proceeding to the next phase.

### Risk 3: `nativeEntity` field name confusion
**Probability**: Low  
**Impact**: Low — cosmetic only  
**Mitigation**: Documented in ADR_018 and TERMINOLOGY.md. The field type changes to `NativeEntityFamily<?>` but the name stays `nativeEntity`. This is intentional and documented.

---

## References

- `docs/development/decisions/ADR_018_Ecosystem_Terminology_and_Rename_System.md`
- `docs/documentation/TERMINOLOGY.md`
- `docs/development/decisions/ADR_012_InternalEntity_Consolidation.md` — prior consolidation work
- `docs/development/decisions/ADR_009_Entity_Hierarchy_Refactoring.md` — hierarchy this terminology names
