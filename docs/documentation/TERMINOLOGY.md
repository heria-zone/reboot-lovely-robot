# Ecosystem Terminology Reference

**Status**: Active  
**Last Updated**: 2026-06-20  
**Applies To**: HZLib, LovelyLib (Reboot, Legacy, Tribute, News), Monsters & Girls

---

## Purpose

This document is the authoritative reference for terminology used across the HZLib ecosystem. Any developer, contributor, or AI agent writing code, documentation, or ADRs for this project must use these terms consistently.

---

## The Three-Tier Conceptual Model

All entities in the ecosystem are described by three concepts that form a strict hierarchy. Understanding the boundary between each tier prevents the most common naming and design errors.

```
Entity Family
  └── Entity Variant
        └── Entity Appearance
```

---

### Entity Family

> **The conceptual creature. One Family = one registered `EntityType<>`.**

A family is the atomic unit of entity identity. It has a unique key, distinct base stats, specific behaviors, and its own spawn conditions. If two things are different families, they are different entries in Minecraft's entity registry — you cannot mistake one for the other, and converting between them requires explicit game mechanics.

**Examples:**

| Family | Notes |
|---|---|
| Gourdragora Golden | Distinct `EntityType<>`, distinct taming item, grayscale carving tint |
| Gourdragora Jack'o | Same `EntityType<>` root structure but different carving behavior and base texture |
| Mandrake Chorus | Ender-type Mandrake — distinct stats and EmanationFeature rules |
| Mandrake Flower | Flora-type Mandrake — distinct overlay slots (hair, emissive) |
| Bunny Robot (Legacy) | Legacy mod Bunny — its own progression system |
| Bunny Robot (Reboot) | Reboot mod Bunny — different stats, different XP curve |
| Bunny Robot (Tribute) | Tribute mod Bunny — faithful to the original mod |
| Dragon Robot (Legacy) | High HP, knockback resistance, exponential XP progression |

**In code:** A `NativeEntityFamily` instance. Each concrete family class (`MandrakeFamily`, `GourdragoraFamily`) holds its family instances as static constants (`MandrakeFamily.CHORUS`, `MandrakeFamily.FLOWER`). Robot families live in the mod-specific registry classes (`LegacyRobotFamilies.BUNNY`, `RebootRobotFamilies.DRAGON`).

---

### Entity Variant

> **A meaningful configuration within a family that differs in stats, behaviors, or spawn conditions.**

Variants are sub-configurations of a family that affect gameplay — different taming items, different combat abilities, different AI behaviors, or different spawn biomes. Not all families have multiple variants; a family with a single configuration is effectively its own variant.

**Examples:**

| Entity | Variants | Distinction |
|---|---|---|
| Gourdragora | Golden, Lumina, Jack'o | Different taming items; different carving rendering (tinted grayscale vs. full-color cutout) |
| Mushroom Brown | Boletus, Ruby, Scarlatina, Orange | Different spawn biomes; different tummy progression starting states |
| Mandrake | Chorus, Flower, Fructus | Entirely different EmanationFeature rules, overlay slots, sounds |
| Robots | Each design is its own Family | Bunny, Dragon, Honey, etc. are separate `EntityType<>` registrations — they are families, not variants of one family |

**In code:** Variants within a family are typically modeled as static constants on the concrete family class. The `TextureVariantFeature`, `ModelVariantFeature`, and `AnimatorVariantFeature` features on a `NativeEntityFamily` define which visual states each variant supports.

---

### Entity Appearance

> **A visual change within the same entity variant. Changes texture, model, hitbox, or scale — not identity, stats, or behavior.**

Appearance is pure presentation. Changing a robot's color does not affect its combat ability — it is an appearance change. Overlay layers (hair, carvings, seasonal costumes) are appearance-level concepts. They affect what the player sees, not how the entity acts.

**Examples:**

| Entity | Appearances | Mechanism |
|---|---|---|
| Any Robot | 16 dye colors (`white`, `orange`, `magenta`, ...) | `TextureVariantFeature` 16-color palette |
| Mandrake Flower | Hairstyle (straight, curly, short, long) | `OverlayFeature` RANDOM slot — chosen at spawn, persisted |
| Gourdragora | Carving pattern (uncarved, smile, scary, star) | `OverlayFeature` INTERACTIVE slot — changed by player with shears |
| Any Monster | Seasonal costume (Halloween, Christmas) | `OverlayFeature` CONDITIONAL slot — computed from real-world month |
| Robot Bunny | Armed / unarmed model | `ModelVariantFeature` — two model states |

**In code:** Appearances are resolved through `TextureVariantFeature` (color palette and texture swaps), `ModelVariantFeature` (model state), `AnimatorVariantFeature` (animation file), and `OverlayFeature` (layered overlay textures). None of these affect stats, AI, or entity identity.

---

## The Boundary Rules

These three rules define where each tier ends:

1. **Family boundary** — If two entities cannot share the same `EntityType<>` registration, they are different families.
2. **Variant boundary** — If two configurations share the same `EntityType<>` but differ in stats, taming items, or AI behavior, they are variants of the same family.
3. **Appearance boundary** — If two configurations share the same `EntityType<>` and the same stats/behavior, they are different appearances.

---

## Multi-Mod Coexistence

LovelyLib is a shared library that serves multiple robot mods simultaneously: **Legacy**, **Reboot**, **Tribute**, and the upcoming **News**. All mods are designed to run together in the same Minecraft instance.

This means there are intentionally multiple Bunny families — one per mod:

```
LegacyRobotFamilies.BUNNY    ←  Legacy Bunny,  its own EntityType<>,  its own stats/progression
RebootRobotFamilies.BUNNY    ←  Reboot Bunny,  different stats,        different XP curve
TributeRobotFamilies.BUNNY   ←  Tribute Bunny, faithful to original mod
```

These are **not duplicates**. They are distinct families registered by distinct mods. A player can own all three simultaneously. The planned conversion mechanic will allow switching a robot between mod variants, but that is a game feature, not a code problem.

---

## Code Name Reference

### HZLib (root layer)

| Concept | Class | Package |
|---|---|---|
| Entity instance base | `NativeEntity` | `net.heriazone.hzlib.api.entity` |
| Family descriptor base | `NativeEntityFamily<T>` | `net.heriazone.hzlib.api.entity` |
| Attribute/display utility | `EntityLogic` | `net.heriazone.hzlib.api.entity.internal` |
| Particle effect utility | `EntityParticles` | `net.heriazone.hzlib.api.entity.internal` |
| Render layer coordinator | `LayerRenderPipeline<T>` | `net.heriazone.hzlib.api.rendering` |
| GeckoLib animation factory | `NativeAnimation` | `net.heriazone.hzlib.api.entity` *(loader)* |
| GeckoLib GeoModel base | `NativeModel<T>` | `net.heriazone.hzlib.api.entity` *(loader)* |
| GeckoLib renderer base | `NativeRenderer<T>` | `net.heriazone.hzlib.api.entity` *(loader)* |

### LovelyLib (robot layer)

| Concept | Class | Package |
|---|---|---|
| Robot family descriptor | `RobotFamily` | `net.heriazone.lovelylib.common.entity` |
| Robot factory/registry base | `RobotFamilyRegistry` | `net.heriazone.lovelylib.common.entity` |
| Legacy mod family registry | `LegacyRobotFamilies` | `net.heriazone.lovelylib.source.legacy` |
| Reboot mod family registry | `RebootRobotFamilies` | `net.heriazone.lovelylib.source.reboot` |
| Tribute mod family registry | `TributeRobotFamilies` | `net.heriazone.lovelylib.source.tribute` |
| Robot design discriminator | `RobotVariant` | `net.heriazone.lovelylib.common.entity.enums` |
| Robot GeckoLib animation | `RobotAnimation` | `net.heriazone.lovelylib.hzlib.api.entity` *(loader)* |

### Monsters & Girls (monster layer)

| Concept | Class | Package |
|---|---|---|
| Monster family descriptor | `MonstersFamily<T>` | `net.heriazone.monsters_girls.entity` |
| Mandrake family | `MandrakeFamily` | `net.heriazone.monsters_girls.entity.custom` |
| Mushroom family | `MushroomFamily` | `net.heriazone.monsters_girls.entity.custom` |
| Bee family | `BeeFamily` | `net.heriazone.monsters_girls.entity.custom` |
| Globberie family | `GlobberieFamily` | `net.heriazone.monsters_girls.entity.custom` |
| Gourdragora family | `GourdragoraFamily` | `net.heriazone.monsters_girls.entity.custom` |
| Maiden family | `MaidenFamily` | `net.heriazone.monsters_girls.entity.custom` |
| Slime family | `SlimeFamily` | `net.heriazone.monsters_girls.entity.custom` |
| Spook family | `SpookFamily` | `net.heriazone.monsters_girls.entity.custom` |
| Wisp family | `WispFamily` | `net.heriazone.monsters_girls.entity.custom` |

---

## Old Name → New Name Quick Reference

For orientation when reading older code, ADRs, or commit history that predates this rename.

| Old name | New name |
|---|---|
| `InternalEntity` | `NativeEntity` |
| `InternalEntityType<T>` | `NativeEntityFamily<T>` |
| `InternalLogic` | `EntityLogic` |
| `InternalParticle` | `EntityParticles` |
| `InternalAnimation` *(HZLib loader)* | `NativeAnimation` |
| `InternalModel<T>` *(HZLib loader)* | `NativeModel<T>` |
| `InternalLayerRenderer<T>` *(HZLib loader)* | `NativeRenderer<T>` |
| `InternalLayerRenderer<T>` *(HZLib Common)* | `LayerRenderPipeline<T>` |
| `InternalAnimation` *(LovelyLib loader)* | `RobotAnimation` |
| `lovelylib NativeEntityType` | `RobotFamily` |
| `LovelyRobotType` | `RobotFamilyRegistry` |
| `LegacyRobotType` | `LegacyRobotFamilies` |
| `RebootRobotType` | `RebootRobotFamilies` |
| `TributeRobotType` | `TributeRobotFamilies` |
| `EntityVariant` *(enum)* | `RobotVariant` |
| `monsters_girls NativeEntityType<T>` | `MonstersFamily<T>` |
| `MandrakeType` | `MandrakeFamily` |
| `MushroomType` | `MushroomFamily` |
| `BeeType` | `BeeFamily` |
| `GlobberieType` | `GlobberieFamily` |
| `GourdragoraType` | `GourdragoraFamily` |
| `MaidenType` | `MaidenFamily` |
| `SlimeType` | `SlimeFamily` |
| `SpookType` | `SpookFamily` |
| `WispType` | `WispFamily` |

---

## Conceptual Model → Code Flow

How a new entity goes from concept to code, following the terminology:

```
1. Define the Family
   └─ Create a NativeEntityFamily subclass (or add a static instance to an existing one)
      e.g., MandrakeFamily.CHORUS = create("mandrake_chorus")

2. Configure the Family
   └─ withCombatStats()          — HP, attack, speed, armor
   └─ withFeature(...)           — EmanationFeature, ExchangeFeature, SoundFeature, OverlayFeature, etc.
   └─ configureVariants()        — TextureVariantFeature, ModelVariantFeature, AnimatorVariantFeature

3. Register the Family
   └─ EntityType<T> registration in the loader-specific Entities class
      e.g., MonstersEntities.MANDRAKE_CHORUS = register("mandrake_chorus", MandrakeFamily.CHORUS)

4. Define Variants (if the family has multiple behavioral configurations)
   └─ Static constants on the family class
      e.g., MandrakeFamily.CHORUS, MandrakeFamily.FLOWER, MandrakeFamily.FRUCTUS

5. Define Appearances (visual-only differences within a variant)
   └─ TextureVariantFeature       — texture key palette
   └─ ModelVariantFeature         — model state (e.g., armed/unarmed)
   └─ OverlayFeature              — layered overlay slots (hair, carvings, costumes)
```

---

## Related Documents

- `docs/development/decisions/ADR_018_Ecosystem_Terminology_and_Rename_System.md` — the ADR that established this vocabulary and the full rename rationale
- `docs/development/decisions/ADR_012_InternalEntity_Consolidation.md` — the consolidation that produced the classes renamed here
- `docs/development/decisions/ADR_009_Entity_Hierarchy_Refactoring.md` — the original hierarchy design this terminology names
- `docs/development/decisions/ADR_011_Variant_and_Spawn_System_Refactoring.md` — the variant system that underpins the Appearance tier
- `docs/development/decisions/ADR_017_OverlayFeature_Composable_Visual_Layer_System.md` — the overlay system that implements appearance-level composition
- `docs/workflow/ARCHITECTURE.md` — implementation-level architecture reference
