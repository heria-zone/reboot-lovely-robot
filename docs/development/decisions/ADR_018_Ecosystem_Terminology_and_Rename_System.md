# ADR 018: Ecosystem Terminology and Rename System

**Status**: Accepted  
**Date**: 2026-06-20  
**Decision Makers**: Serge  
**Consulted**: Design session — multi-mod ecosystem naming brainstorm, Internal* rename discussion, Family/Variant/Appearance vocabulary definition

---

## Context

### The naming problem

The ecosystem now spans three projects — HZLib, LovelyLib (serving Reboot, Legacy, Tribute, and the upcoming News mod), and Monsters & Girls — all sharing a common entity hierarchy. Through that growth, the word `Internal` has accumulated three unrelated meanings:

- **`InternalEntity`** — the shared entity base class for everything in the ecosystem
- **`InternalEntityType`** — the abstract family descriptor that carries stats, features, and variant configuration
- **`InternalAnimation`** / **`InternalModel`** / **`InternalLayerRenderer`** — the GeckoLib integration layer (loader-specific rendering classes)
- **`InternalLogic`** / **`InternalParticle`** — static utility helpers (attribute mutation, particle effects)

`Internal` was a pragmatic choice during the initial consolidation work (ADR_012), where the goal was to establish a shared base quickly. It communicated "this belongs to the framework, not the game logic." That job is done. The prefix now obscures rather than clarifies — a reader seeing `InternalAnimation` cannot tell whether it is a base entity class, a GeckoLib bridge, or a utility class without opening the file.

The problem is compounded on the descriptor side. Both LovelyLib and Monsters & Girls define a class called `NativeEntityType`, in different packages, with different responsibilities. Neither name reflects the conceptual role these objects play in the domain model.

### The conceptual model

Through the design of Monsters & Girls — the more recent and architecturally polished project in the ecosystem — a clear three-tier conceptual model has emerged:

**Entity Family** — the conceptual creature. One Family = one registered `EntityType<>`. Examples: Gourdragora Golden, Mushroom Brown, Bunny Robot. Families have distinct stats, behaviors, and possibly spawn conditions. This is the unit of identity.

**Entity Variant** — a meaningful configuration within a family that differs in stats, behaviors, or spawn conditions. Examples: Gourdragora Golden vs. Jack'o (different taming items, different carving behavior). Robot Bunny vs. Dragon (different stats and abilities, different `EntityType<>` registrations).

**Entity Appearance** — a visual change within the same entity variant. Changes texture, model, hitbox, or scale — but not fundamental identity, stats, or behavior class. Examples: Robot 16 dye colors. Mushroom Brown Boletus/Ruby/Scarlatina/Orange (different textures by biome). Gourdragora carving patterns.

This model is precise, has clear boundaries, and accurately describes every entity currently in the ecosystem. It needs to be reflected in the code.

### Why the asymmetry between robots and monsters exists

The robot mod architecture (LovelyLib serving Reboot, Legacy, Tribute, and the upcoming News mod) deliberately registers each robot design — Bunny, Dragon, Honey, etc. — as a separate `EntityType<>`. This means `LegacyRobotFamilies.BUNNY` and `RebootRobotFamilies.BUNNY` are two different families with different stats, different progression curves, and ultimately a planned conversion mechanic between them. All four mods are designed to coexist in the same game instance — the triple (and eventually quadruple) Bunny is intentional, not a duplication error.

The monster architecture (Monsters & Girls) takes a different approach: each concrete type class (`MandrakeType`, `MushroomType`, etc.) is its own class with multiple static instances representing the variants within it (CHORUS, FLOWER, FRUCTUS for Mandrake). Each instance maps to its own registered `EntityType<>`.

Both approaches map onto the same Family/Variant/Appearance model — they just arrive at "one Family = one `EntityType<>`" from different code structures.

### The dead `dynamic/` package

The `hzlib.api.entity.dynamic` package contains a legacy variant system (`InternalVariant`, `InternalEntityVariant`, `InternalModelVariant`, etc.) that was never completed and has zero consumers outside of HZLib itself. The `hzlib.api.entity.monsters` package holds stub implementations (`NativeAnimation`, `NativeModel`, `NativeEntity`) that extend these dead classes. Neither package is referenced by any mod source (verified: no imports in Tribute, Legacy, Reboot, or Monsters & Girls). These are out of scope for the rename described in this ADR and are noted as candidates for deletion in a follow-up cleanup sprint.

---

## Decision

Establish the Family / Variant / Appearance vocabulary as the canonical ecosystem terminology and execute a full rename sweep that aligns all framework class names with this vocabulary.

The rename follows two principles:

1. **Tier visibility**: Names at the HZLib root layer are prefixed `Native*` — consistent with the entity class itself (`NativeEntity`). Names at the mid-tier layers carry the domain qualifier: `Robot*` for LovelyLib, `Monsters*` for the Monsters & Girls mid-layer.

2. **Role clarity**: Classes whose role is "a registry of static instances" become plural (`LegacyRobotFamilies`). Classes whose role is "a factory and base for all robot registries" become explicit (`RobotFamilyRegistry`). Utility helpers become named services (`EntityLogic`, `EntityParticles`).

---

## Full Rename Table

### HZLib — `net.heriazone.hzlib.api.entity` (Common)

| Current name | New name | Role |
|---|---|---|
| `InternalEntity` | `NativeEntity` | Root entity base for all HZLib entities |
| `InternalEntityType<T>` | `NativeEntityFamily<T>` | Root family descriptor base |

### HZLib — `net.heriazone.hzlib.api.entity.internal` (Common)

| Current name | New name | Role |
|---|---|---|
| `InternalLogic` | `EntityLogic` | Static utility: attribute mutation, owner display |
| `InternalParticle` | `EntityParticles` | Static utility: particle effect spawning |

### HZLib — `net.heriazone.hzlib.api.rendering` (Common)

| Current name | New name | Role |
|---|---|---|
| `InternalLayerRenderer<T>` | `LayerRenderPipeline<T>` | Manages the ordered `IInternalRenderLayer` list |

### HZLib — `net.heriazone.hzlib.api.entity` (Fabric / Forge / NeoForge — loader-specific)

| Current name | New name | Role |
|---|---|---|
| `InternalAnimation` | `NativeAnimation` | GeckoLib animation controller factory |
| `InternalModel<T>` | `NativeModel<T>` | GeckoLib GeoModel base |
| `InternalLayerRenderer<T>` | `NativeRenderer<T>` | GeckoLib GeoEntityRenderer base |

### LovelyLib — `net.heriazone.lovelylib.common.entity` (Common)

| Current name | New name | Role |
|---|---|---|
| `NativeEntityType` | `RobotFamily` | Robot-specific family descriptor |
| `LovelyRobotType` | `RobotFamilyRegistry` | Factory base and shared registry list for all robot mod families |

### LovelyLib — `net.heriazone.lovelylib.source.*` (Common)

| Current name | New name | Package |
|---|---|---|
| `LegacyRobotType` | `LegacyRobotFamilies` | `source.legacy` |
| `RebootRobotType` | `RebootRobotFamilies` | `source.reboot` |
| `TributeRobotType` | `TributeRobotFamilies` | `source.tribute` |

### LovelyLib — `net.heriazone.lovelylib.common.entity.enums` (Common)

| Current name | New name | Role |
|---|---|---|
| `EntityVariant` | `RobotVariant` | Enum of robot design discriminators (Bunny, Dragon, etc.) |

### LovelyLib — `net.heriazone.lovelylib.hzlib.api.entity` (Fabric / Forge / NeoForge — loader-specific)

| Current name | New name | Role |
|---|---|---|
| `InternalAnimation` | `RobotAnimation` | Robot-specific GeckoLib animation: tail config, robot locomotion states |

### Monsters & Girls — `net.heriazone.monsters_girls.entity` (Common)

| Current name | New name | Role |
|---|---|---|
| `NativeEntityType<T>` | `MonstersFamily<T>` | Monster-specific family descriptor mid-layer |

### Monsters & Girls — `net.heriazone.monsters_girls.entity.custom` (Common)

| Current name | New name | Role |
|---|---|---|
| `MandrakeType` | `MandrakeFamily` | Concrete Mandrake family (CHORUS, FLOWER, FRUCTUS) |
| `MushroomType` | `MushroomFamily` | Concrete Mushroom family |
| `BeeType` | `BeeFamily` | Concrete Bee family |
| `GlobberieType` | `GlobberieFamily` | Concrete Globberie family |
| `GourdragoraType` | `GourdragoraFamily` | Concrete Gourdragora family |
| `MaidenType` | `MaidenFamily` | Concrete Maiden family |
| `SlimeType` | `SlimeFamily` | Concrete Slime family |
| `SpookType` | `SpookFamily` | Concrete Spook family |
| `WispType` | `WispFamily` | Concrete Wisp family |

---

## Resulting Hierarchy

```
HZLib (root layer):
  NativeEntity                  ← base entity instance          (was InternalEntity)
  NativeEntityFamily<T>         ← root family descriptor        (was InternalEntityType<T>)
  EntityLogic                   ← attribute/display utility     (was InternalLogic)
  EntityParticles               ← particle effect utility       (was InternalParticle)
  LayerRenderPipeline<T>        ← render layer coordinator      (was Common InternalLayerRenderer<T>)
  NativeAnimation               ← GeckoLib animation factory    (was loader InternalAnimation)
  NativeModel<T>                ← GeckoLib GeoModel base        (was InternalModel<T>)
  NativeRenderer<T>             ← GeckoLib renderer base        (was loader InternalLayerRenderer<T>)

LovelyLib (robot layer):
  RobotFamily                   ← robot family descriptor       (was lovelylib NativeEntityType)
  RobotFamilyRegistry           ← factory base + registry list  (was LovelyRobotType)
  LegacyRobotFamilies           ← Legacy mod family registry    (was LegacyRobotType)
  RebootRobotFamilies           ← Reboot mod family registry    (was RebootRobotType)
  TributeRobotFamilies          ← Tribute mod family registry   (was TributeRobotType)
  RobotVariant                  ← design discriminator enum     (was EntityVariant)
  RobotAnimation                ← robot GeckoLib animation      (was lovelylib InternalAnimation)

Monsters & Girls (monster layer):
  MonstersFamily<T>             ← monster family descriptor     (was monsters_girls NativeEntityType<T>)
  MandrakeFamily                ← Mandrake family               (was MandrakeType)
  MushroomFamily                ← Mushroom family               (was MushroomType)
  BeeFamily                     ← Bee family                    (was BeeType)
  GlobberieFamily               ← Globberie family              (was GlobberieType)
  GourdragoraFamily             ← Gourdragora family            (was GourdragoraType)
  MaidenFamily                  ← Maiden family                 (was MaidenType)
  SlimeFamily                   ← Slime family                  (was SlimeType)
  SpookFamily                   ← Spook family                  (was SpookType)
  WispFamily                    ← Wisp family                   (was WispType)
```

---

## What Is Not Renamed

**`MonsterEntity`** and **`RobotEntity`** — both are already correctly named. They describe the entity instance tier, not the family descriptor tier.

**`EntityTexture`** — the enum naming the 16-color robot palette entries. The name is accurate for the Appearance tier and is widely referenced. Renaming it is a follow-up if desired, not part of this sweep.

**`EntityVariantTexture` / `EntityVariantModel` / `EntityVariantAnimator`** — legacy lovelylib enums from the old resource-map-based variant system. Candidates for removal when that system is fully retired.

**`hzlib.api.entity.dynamic.*` and `hzlib.api.entity.monsters.*` (legacy stubs)** — confirmed dead code with zero consumers outside their own packages. Candidates for deletion in a cleanup sprint, not renamed here.

**`IInternalRenderLayer`** — the render layer interface. Renaming it is lower priority given its number of consumers. Deferred to a follow-up.

**The `nativeEntity` field** — the public field on `NativeEntity` of type `NativeEntityFamily<?>`. The field name is well-established across all mod sources. Renaming a field is a higher blast-radius operation than renaming a class and is deferred as a standalone task.

---

## Implementation Notes

### Rename order

The rename should be executed project-by-project in dependency order to keep the build green at each step:

```
1. HZLib Common
   InternalEntity → NativeEntity
   InternalEntityType<T> → NativeEntityFamily<T>
   InternalLogic → EntityLogic
   InternalParticle → EntityParticles
   InternalLayerRenderer<T> (Common) → LayerRenderPipeline<T>

2. HZLib loaders (Fabric / Forge / NeoForge)
   InternalAnimation → NativeAnimation
   InternalModel<T> → NativeModel<T>
   InternalLayerRenderer<T> (loader) → NativeRenderer<T>

3. LovelyLib Common
   NativeEntityType → RobotFamily
   LovelyRobotType → RobotFamilyRegistry
   EntityVariant → RobotVariant

4. LovelyLib loaders (Fabric / Forge / NeoForge)
   InternalAnimation → RobotAnimation

5. LovelyLib source registries
   LegacyRobotType → LegacyRobotFamilies
   RebootRobotType → RebootRobotFamilies
   TributeRobotType → TributeRobotFamilies

6. Monsters & Girls Common
   NativeEntityType<T> → MonstersFamily<T>
   MandrakeType → MandrakeFamily
   MushroomType → MushroomFamily
   [... remaining concrete families]
```

### Pattern for the upcoming News robot mod

When the News mod variant is added to LovelyLib, its registry class follows the established pattern directly:

```java
public class NewsRobotFamilies extends RobotFamilyRegistry {
    public static final RobotFamily BUNNY  = create(RobotVariant.Bunny);
    public static final RobotFamily DRAGON = create(RobotVariant.Dragon);
    // ...
}
```

---

## Alternatives Considered

**Keep `Internal*` prefix, add per-class documentation** — rejected. Documentation describes intent; naming encodes intent. A class named `NativeEntity` communicates its role to every reader without requiring them to open a doc. Documentation supplements names; it does not substitute for them.

**`Base*` prefix for HZLib root classes** (`BaseEntity`, `BaseEntityFamily`) — rejected. `Base` implies "incomplete, extend me for real functionality." `NativeEntity` is fully functional; it is the HZLib-native entity root, not a skeleton.

**`EntityFamily` without the `Native` prefix** — considered and rejected in favor of `NativeEntityFamily`. The `Native` prefix consistently marks the HZLib tier across all classes (`NativeEntity`, `NativeModel`, `NativeRenderer`, `NativeAnimation`). Tier consistency across the family outweighs a marginal naming gain.

**`MonstersEntityFamily` instead of `MonstersFamily`** — rejected. "Entity" is redundant in context. `MonstersFamily` carries the domain qualifier cleanly.

**`RobotsFamily` (plural) for the mid-layer class** — rejected. `RobotFamily` reads as "a family in the robot domain," which is correct. Plural form (`LegacyRobotFamilies`) is reserved for registry/collection classes.

---

## Consequences

### Positive

- Every class name directly communicates its tier (`Native*` = HZLib root, `Robot*` = LovelyLib, `Monsters*` = Monsters & Girls) and its role (`*Family` = family descriptor, `*Families` = registry, descriptive name = utility)
- The canonical three-tier terminology (Family / Variant / Appearance) is now reflected in code, not just in discussion
- New contributors and AI agents can infer the architecture from class names alone
- The pattern for new robot mods is immediately obvious from the existing names
- No more ambiguity between the two former `NativeEntityType` classes that shared a name across packages

### Negative

- Large rename surface — touches all three projects and all loader variants (Fabric, Forge, NeoForge)
- All existing ADRs, documentation, and commit history reference the old names — the Quick Reference table in `TERMINOLOGY.md` bridges this gap
- IDE rename tooling is mechanical but must be validated against the full multi-project build

### Risks

- **Partial rename**: Renaming HZLib without its dependents breaks the build. The implementation order defined above mitigates this — rename in strict dependency order.
- **The `nativeEntity` field**: Stays named `nativeEntity` (type `NativeEntityFamily<?>`). Readers will see a field named for the old class name of the type it holds. This is a minor mismatch, documented here, deferred to a standalone rename.

---

## Related Decisions

- **ADR_009**: Entity Hierarchy Refactoring — established the three-tier entity class hierarchy that this ADR names
- **ADR_012**: InternalEntity Consolidation — the consolidation work that produced `InternalEntity` and the two `NativeEntityType` classes now renamed; this ADR supersedes the naming choices made there
- **ADR_011**: Variant and Spawn System Refactoring — established the string-based variant system; the Appearance tier in the conceptual model corresponds to what that ADR calls "variants"
- **ADR_015**: EmanationFeature — the Monsters & Girls feature that first demonstrated the mature architecture now reflected in `MonstersFamily` / concrete family class design
- **ADR_017**: OverlayFeature — code examples in this ADR use the old class names and should be read with the rename table in mind
