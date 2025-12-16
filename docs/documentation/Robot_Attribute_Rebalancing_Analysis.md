# Robot Attribute Rebalancing Analysis

**Purpose**: Design balanced attribute values for robot entities across Tribute, Legacy & Reboot variants that reflect their described characteristics and roles.

**Date**: 2025-12-15
**Status**: Draft

## Design Philosophy

### Core Principles
- **Tribute**: Maintain original/official stats as baseline reference
- **Legacy & Reboot**: Enhanced versions with distinct characteristics
- **Role-Based Stats**: Attributes should reflect each robot's described purpose
- **Minecraft Vanilla Balance**: Use tamed wolf (40 HP) as reference point
- **Progressive Enhancement**: Legacy > Tribute, Reboot ≥ Legacy

### Constraints
- **HP Cap**: 30 maximum base value (they level up from there)
- **Movement Speed**: 0.3 minimum (anything lower is too slow)
- **Attack Speed**: 1.0-2.0 range (Minecraft standard)
- **Defence**: 0-10 range (reasonable armor values)

## Robot Role Analysis

### Combat Hierarchy (Based on Descriptions)
1. **Dragon**: "Most powerful", "highest damage output", "combat specialist"
2. **Neko**: "Second only to Dragon", "swift and agile", "superior speed and precision"
3. **Bunny/Bunny2**: "Surpass both in speed", "lower damage output", "speed-focused"
4. **Vanilla**: "Weakest in overall capability", "balanced", "general-purpose"
5. **Honey**: "More house worker than fighter", "utility-focused"
6. **Kitsune**: "Not combatants", "supporter units", "priest-like"

### Specialization Focus
- **Dragon**: Pure combat power (high attack, high HP, moderate speed)
- **Neko**: Agile fighter (high attack, high speed, moderate HP)
- **Bunny/Bunny2**: Speed utility (highest speed, moderate attack, lower HP)
- **Honey**: Utility worker (low combat stats, moderate HP for durability)
- **Kitsune**: Support buffer (low combat, high HP for survivability)
- **Vanilla**: Balanced generalist (average in all areas, slightly lower)

## Proposed Attribute Values

### Tribute (Original/Official - Baseline)
**Bunny**
- HP: 25 (agile, lighter build)
- Attack: 4 (lower damage as described)
- Defence: 3 (light armor)
- Movement Speed: 0.35 (fastest movement)
- Attack Speed: 1.6 (fast attacks)

**Bunny2**
- HP: 26 (slightly improved)
- Attack: 4.5 (marginally better)
- Defence: 4 (better armor)
- Movement Speed: 0.34 (still very fast)
- Attack Speed: 1.5 (fast attacks)

**Honey**
- HP: 28 (durable for work)
- Attack: 3 (weak fighter)
- Defence: 4 (work protection)
- Movement Speed: 0.3 (standard speed)
- Attack Speed: 1.0 (slow attacks)

**Vanilla**
- HP: 24 (weakest overall)
- Attack: 4 (balanced)
- Defence: 4 (balanced)
- Movement Speed: 0.31 (slightly above minimum)
- Attack Speed: 1.2 (standard)

### Legacy (Enhanced Recreations)
**Bunny**
- HP: 26 (improved durability)
- Attack: 5 (better than tribute)
- Defence: 4 (improved protection)
- Movement Speed: 0.37 (even faster)
- Attack Speed: 1.8 (very fast attacks)

**Bunny2**
- HP: 27 (enhanced version)
- Attack: 5.5 (stronger than bunny)
- Defence: 5 (better armor)
- Movement Speed: 0.36 (faster than original)
- Attack Speed: 1.7 (fast attacks)

**Dragon**
- HP: 30 (maximum base HP - tank)
- Attack: 8 (highest damage)
- Defence: 7 (heavy armor)
- Movement Speed: 0.3 (minimum - heavy build)
- Attack Speed: 1.0 (slow but powerful)

**Honey**
- HP: 29 (very durable for utility)
- Attack: 3.5 (slightly improved)
- Defence: 5 (work protection)
- Movement Speed: 0.31 (utility speed)
- Attack Speed: 1.1 (still slow)

**Kitsune**
- HP: 28 (survivable support)
- Attack: 2 (weakest fighter)
- Defence: 6 (good protection for support)
- Movement Speed: 0.33 (moderate speed)
- Attack Speed: 1.3 (support casting speed)

**Neko**
- HP: 28 (agile fighter)
- Attack: 7 (second highest damage)
- Defence: 5 (moderate armor)
- Movement Speed: 0.34 (fast and agile)
- Attack Speed: 1.4 (quick strikes)

**Vanilla**
- HP: 25 (improved but still weakest)
- Attack: 4.5 (balanced improvement)
- Defence: 5 (better protection)
- Movement Speed: 0.32 (improved mobility)
- Attack Speed: 1.3 (better responsiveness)

### Reboot (Advanced Features - Same as Legacy for now)
*Note: Reboot 2.0 uses same robot types as Legacy with same stat values*

**Bunny**
- HP: 26
- Attack: 5
- Defence: 4
- Movement Speed: 0.37
- Attack Speed: 1.8

**Bunny2**
- HP: 27
- Attack: 5.5
- Defence: 5
- Movement Speed: 0.36
- Attack Speed: 1.7

**Dragon**
- HP: 30
- Attack: 8
- Defence: 7
- Movement Speed: 0.3
- Attack Speed: 1.0

**Honey**
- HP: 29
- Attack: 3.5
- Defence: 5
- Movement Speed: 0.31
- Attack Speed: 1.1

**Kitsune**
- HP: 28
- Attack: 2
- Defence: 6
- Movement Speed: 0.33
- Attack Speed: 1.3

**Neko**
- HP: 28
- Attack: 7
- Defence: 5
- Movement Speed: 0.34
- Attack Speed: 1.4

**Vanilla**
- HP: 25
- Attack: 4.5
- Defence: 5
- Movement Speed: 0.32
- Attack Speed: 1.3

## Balancing Rationale

### Combat Effectiveness Ranking
1. **Dragon**: 30 HP, 8 ATK - Pure powerhouse
2. **Neko**: 28 HP, 7 ATK - Agile fighter
3. **Bunny2**: 27 HP, 5.5 ATK - Fast utility fighter
4. **Bunny**: 26 HP, 5 ATK - Speed-focused
5. **Vanilla**: 25 HP, 4.5 ATK - Balanced generalist
6. **Honey**: 29 HP, 3.5 ATK - Durable utility
7. **Kitsune**: 28 HP, 2 ATK - Support tank

### Speed Ranking
1. **Bunny**: 0.37 - Fastest
2. **Bunny2**: 0.36 - Very fast
3. **Neko**: 0.34 - Fast and agile
4. **Kitsune**: 0.33 - Moderate
5. **Vanilla**: 0.32 - Improved standard
6. **Honey**: 0.31 - Utility speed
7. **Dragon**: 0.3 - Heavy and slow

### Survivability (HP + Defence)
1. **Dragon**: 30 HP + 7 DEF = 37 total
2. **Kitsune**: 28 HP + 6 DEF = 34 total
3. **Honey**: 29 HP + 5 DEF = 34 total
4. **Neko**: 28 HP + 5 DEF = 33 total
5. **Bunny2**: 27 HP + 5 DEF = 32 total
6. **Vanilla**: 25 HP + 5 DEF = 30 total
7. **Bunny**: 26 HP + 4 DEF = 30 total

## Implementation Notes

### Key Changes from Current Stats
- **Dragon**: Significantly boosted attack (5→8) and defence (6→7) to match "most powerful" description
- **Neko**: High attack (5→7) and speed (0.3→0.34) to match "second in combat" and "agile" description
- **Bunny/Bunny2**: Increased movement speed (0.3→0.35+) and attack speed to match "fastest" description
- **Kitsune**: Lowest attack (5→2) but good defence (6) to match "not combatants" but "supporter" role
- **Honey**: Reduced attack (6→3.5) to match "house worker not fighter" description
- **Vanilla**: Slightly reduced stats to match "weakest overall" description

### Minecraft Balance Considerations
- All HP values under 30 (tamed wolf has 40 HP)
- Movement speeds above 0.3 minimum threshold
- Attack values reasonable for Minecraft combat (2-8 range)
- Defence values provide meaningful protection without being overpowered
- Attack speeds within Minecraft's standard ranges (1.0-2.0)

### Progression Logic
- **Tribute→Legacy**: Moderate improvements across the board
- **Legacy→Reboot**: Same values (focus on features, not raw stats)
- **Within Variants**: Clear role differentiation through specialized stat distributions

## Key Design Changes

### Combat Hierarchy Established
**Before**: All robots had similar combat stats (Attack 5-6)
**After**: Clear combat hierarchy reflecting descriptions
- **Dragon**: 8 Attack (highest - "most powerful")
- **Neko**: 7 Attack (second - "ranks second in combat")
- **Bunny2**: 5.5 Attack (improved utility fighter)
- **Bunny**: 5 Attack (speed over power)
- **Vanilla**: 4-4.5 Attack (balanced generalist)
- **Honey**: 3-3.5 Attack (utility worker)
- **Kitsune**: 2 Attack (support only)

### Speed Differentiation
**Before**: All robots had 0.3 movement speed
**After**: Speed reflects role and agility
- **Bunny**: 0.35-0.37 (fastest - "surpass both in speed")
- **Bunny2**: 0.34-0.36 (very fast - enhanced version)
- **Neko**: 0.34 (agile fighter - "swift and agile")
- **Kitsune**: 0.33 (moderate support speed)
- **Vanilla**: 0.31-0.32 (improved standard)
- **Honey**: 0.3-0.31 (utility speed)
- **Dragon**: 0.3 (heavy build - minimum speed)

### Survivability Balance
**Before**: Most robots had 30 HP
**After**: HP reflects role and build
- **Dragon**: 30 HP (tank - maximum base)
- **Honey**: 28-29 HP (durable utility worker)
- **Kitsune**: 28 HP (survivable support)
- **Neko**: 28 HP (agile fighter)
- **Bunny2**: 26-27 HP (enhanced but still agile)
- **Bunny**: 25-26 HP (lightweight speedster)
- **Vanilla**: 24-25 HP (weakest overall)

## Variant Progression

### Tribute → Legacy → Reboot
- **Tribute**: Baseline/official stats
- **Legacy**: Enhanced versions with role specialization
- **Reboot**: Same as Legacy (focus on features, not raw stats)

### Example: Bunny Evolution
- **Tribute Bunny**: 25 HP, 4 ATK, 0.35 Speed
- **Legacy Bunny**: 26 HP, 5 ATK, 0.37 Speed
- **Reboot Bunny**: 26 HP, 5 ATK, 0.37 Speed (same as Legacy)

## Balance Validation

### Combat Effectiveness (Attack × Speed × Survivability)
1. **Dragon**: High damage, high HP, slow speed = Tank DPS
2. **Neko**: High damage, moderate HP, high speed = Agile DPS
3. **Bunny2**: Moderate damage, moderate HP, very high speed = Mobile Fighter
4. **Bunny**: Moderate damage, lower HP, highest speed = Glass Cannon
5. **Vanilla**: Balanced stats = Generalist
6. **Honey**: Low damage, high HP, slow speed = Utility Tank
7. **Kitsune**: Lowest damage, moderate HP, moderate speed = Pure Support

### Role Fulfillment Check
- ✅ **Dragon**: "Most powerful" - Highest attack and HP
- ✅ **Neko**: "Second in combat" - Second highest attack
- ✅ **Bunny/Bunny2**: "Fastest" - Highest movement and attack speeds
- ✅ **Vanilla**: "Weakest overall" - Lowest total stats
- ✅ **Honey**: "House worker not fighter" - Lowest attack, high utility HP
- ✅ **Kitsune**: "Not combatants" - Lowest attack, support-focused

## Implementation Impact

### For NativeEntityType Configuration
These values should be used when configuring:
- Base health points
- Attack damage
- Armor/defense values
- Movement speed attributes
- Attack speed modifiers

### For Gameplay Balance
- **Early Game**: Vanilla provides balanced introduction
- **Mid Game**: Specialized robots offer distinct advantages
- **Late Game**: Dragon and Neko provide combat superiority
- **Utility**: Honey and Kitsune serve non-combat roles effectively

### For Player Choice
Each robot now has a clear niche:
- **Want speed?** → Bunny/Bunny2
- **Want power?** → Dragon
- **Want agility?** → Neko
- **Want utility?** → Honey
- **Want support?** → Kitsune
- **Want balance?** → Vanilla

---

**Status**: Ready for implementation in NativeEntityType configurations