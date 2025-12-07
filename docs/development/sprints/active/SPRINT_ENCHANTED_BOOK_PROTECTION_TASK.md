# Sprint Task: Enchanted Book Protection Feature

**Status**: In Progress
**Started**: 2025-12-07
**Target Completion**: TBD
**Priority**: Medium
**Complexity**: Medium

## Objective

Implement enchanted book feeding system allowing players to increase robot protection values by feeding enchanted books with protection enchantments.

## Design Decisions (Finalized)

### Core Mechanics

**Formula**: `protectionGained = enchantmentLevel × contributionPercentage × configMaxProtection`
- Default contribution: 25% (0.25)
- Example: Fire Protection II with max=80 → 2 × 0.25 × 80 = 40 points

**Book Consumption**: Full consumption (book disappears)

**Max Protection Handling**: Prevent feeding if already at max for that protection type

**Multi-Enchantment Books**: Apply ALL valid protection enchantments from single book

**Generic "Protection" Enchantment**: Apply randomly to one protection type (Fire/Fall/Blast/Projectile)

### Enchantment Mapping

| Minecraft Enchantment | Robot Protection Type |
|----------------------|----------------------|
| Fire Protection | Fire Protection |
| Blast Protection | Blast Protection |
| Feather Falling | Fall Protection |
| Projectile Protection | Projectile Protection |
| Protection (generic) | Random selection |

### Configuration

Add to `SharedConfigs.Common`:
```java
public static boolean EnableEnchantedBookProtection = true;
public static double EnchantedBookContributionPercentage = 0.25;
```

## Implementation Tasks

### Phase 1: Common Module (Shared Logic)

- [x] **Task 1.1**: Add config values to `SharedConfigs.Common`
  - `EnableEnchantedBookProtection`
  - `EnchantedBookContributionPercentage`

- [x] **Task 1.2**: Create enchantment calculation utility
  - Location: `sources/legacy/llovelyr-1.21.1/Common/src/main/java/net/msymbios/llovelyr/common/utils/EnchantmentProtectionCalculator.java`
  - Methods:
    - `calculateProtectionGain(int enchantmentLevel, int maxProtection, double contribution)`
    - `canApplyProtection(int currentProtection, int maxProtection)`
    - `getRandomProtectionType()` (for generic Protection enchantment)

- [x] **Task 1.3**: Enchantment extraction and processing
  - Implemented directly in `LovelyRobotEntity.processEnchantedBook()`
  - Uses 1.21.1 DataComponents API for enchantment reading
  - Handles all protection types including generic Protection

- [x] **Task 1.4**: Add interaction handler to `LovelyRobotEntity`
  - Method: `handleEnchantedBookFeeding(ItemStack stack, Player player)`
  - Method: `processEnchantedBook(ItemStack stack, Player player)`
  - Location: `sources/legacy/llovelyr-1.21.1/Common/src/main/java/net/msymbios/llovelyr/common/entity/common/LovelyRobotEntity.java`
  - Integration point: `handleItemInteraction()`

### Phase 2: Loader-Specific Implementation

- [x] **Task 2.1**: NeoForge - Config registration
  - File: `sources/legacy/llovelyr-1.21.1/NeoForge/src/main/java/net/msymbios/llovelyr/source/LovelyConfigs.java`
  - Added to Protection section

- [x] **Task 2.2**: Forge - Config registration
  - File: `sources/legacy/llovelyr-1.21.1/Forge/src/main/java/net/msymbios/llovelyr/source/LovelyConfigs.java`
  - Added to Protection section

- [x] **Task 2.3**: Fabric - Config registration
  - File: `sources/legacy/llovelyr-1.21.1/Fabric/src/main/java/net/msymbios/llovelyr/source/LovelyConfigs.java`
  - Added to Protection section

- [x] **Task 2.4**: Enchantment reading (unified implementation)
  - Uses 1.21.1 DataComponents.STORED_ENCHANTMENTS API
  - Works across all loaders (NeoForge/Forge/Fabric)
  - Implemented in Common module

### Phase 3: Feedback & Polish

- [ ] **Task 3.1**: Visual feedback
  - Particle effects (enchantment glint)
  - Sound effect (enchantment sound)
  - Location: In `handleEnchantedBookFeeding()`

- [ ] **Task 3.2**: Player messages
  - Success: "Fire Protection: 40 → 80 (+40)"
  - Already max: "Fire Protection already at maximum!"
  - No valid enchantments: "This book has no protection enchantments"
  - Multiple protections: "Fire Protection +40, Blast Protection +60"

- [ ] **Task 3.3**: Book & Quill display (verify existing)
  - Confirm current protection values are shown
  - No changes needed (already implemented)

### Phase 4: Testing & Documentation

- [ ] **Task 4.1**: Test scenarios
  - Single enchantment book (Fire Protection I-IV)
  - Multi-enchantment book (Fire + Blast)
  - Generic Protection enchantment
  - Already at max protection
  - Config value changes (80 → 50 → 25)
  - Multiple books accumulation

- [ ] **Task 4.2**: Update CURRENT_STATE.md
  - Document new enchanted book feeding system
  - Add to robot interaction features

- [ ] **Task 4.3**: User documentation
  - Create usage guide in `docs/documentation/usage/`
  - Include examples and screenshots

## Technical Details

### File Structure

```
sources/legacy/llovelyr-1.21.1/
├── Common/
│   └── src/main/java/net/msymbios/llovelyr/
│       ├── common/
│       │   ├── Configs/
│       │   │   └── SharedConfigs.java [MODIFY]
│       │   ├── entity/
│       │   │   └── common/
│       │   │       └── LovelyRobotEntity.java [MODIFY]
│       │   └── utils/
│       │       ├── EnchantmentProtectionCalculator.java [NEW]
│       │       └── ProtectionEnchantmentValidator.java [NEW]
│       └── lib/
│           └── entity/
│               └── features/
│                   └── ProtectionFeature.java [REFERENCE]
├── NeoForge/
│   └── src/main/java/net/msymbios/llovelyr/source/
│       └── LovelyConfigs.java [MODIFY]
├── Forge/
│   └── src/main/java/net/msymbios/llovelyr/source/
│       └── LovelyConfigs.java [MODIFY]
└── Fabric/
    └── src/main/java/net/msymbios/llovelyr/source/
        └── LovelyConfigs.java [MODIFY]
```

### Code Integration Points

**Existing Interaction Flow**:
```java
mobInteract(Player, InteractionHand)
  → handleItemInteraction(ItemStack, Player)
    → handleTexture(ItemStack, Player) [existing]
    → handleEnchantedBookFeeding(ItemStack, Player) [NEW]
```

**Protection Getters/Setters** (already exist):
```java
getFireProtection() / setFireProtection(int)
getFallProtection() / setFallProtection(int)
getBlastProtection() / setBlastProtection(int)
getProjectileProtection() / setProjectileProtection(int)
```

**Config Access** (already exists):
```java
SharedConfigs.Common.ProtectionLimitFire
SharedConfigs.Common.ProtectionLimitFall
SharedConfigs.Common.ProtectionLimitBlast
SharedConfigs.Common.ProtectionLimitProjectile
```

## Implementation Notes

### Percentage-Based Formula Explanation

The formula ensures:
1. **Scales with config**: If admin sets max=50, books give proportionally less
2. **Level matters**: Higher enchantment levels = more protection
3. **Predictable**: Same book always gives same amount

**Examples**:
- Config max = 80, contribution = 25%
  - Fire Protection I: 1 × 0.25 × 80 = 20 points
  - Fire Protection IV: 4 × 0.25 × 80 = 80 points (instant max)

- Config max = 50, contribution = 25%
  - Fire Protection I: 1 × 0.25 × 50 = 12.5 → 13 points
  - Fire Protection IV: 4 × 0.25 × 50 = 50 points (instant max)

### Multi-Loader Considerations

**Enchantment Reading**:
- All loaders use `EnchantmentHelper` but with slightly different APIs
- Common module defines interface, loaders implement specifics

**Item Consumption**:
- `stack.shrink(1)` works across all loaders
- Handle in Common module after validation

**Particles & Sounds**:
- Use Minecraft's built-in enchantment effects
- `level.addParticle()` and `level.playSound()` work across loaders

## Blockers & Dependencies

**None identified** - All required systems already exist:
- Protection storage (NBT)
- Protection getters/setters
- Config system
- Interaction system
- Book & Quill display

## Success Criteria

- [ ] Players can feed enchanted books to robots
- [ ] Protection values increase according to formula
- [ ] Books are consumed on successful feeding
- [ ] Feeding is prevented when at max protection
- [ ] Multi-enchantment books apply all valid protections
- [ ] Generic Protection enchantment applies randomly
- [ ] Visual and audio feedback works
- [ ] Player receives clear messages
- [ ] Config values are respected
- [ ] Works on all loaders (NeoForge, Forge, Fabric)
- [ ] No crashes or errors
- [ ] Documentation is complete

## Progress Log

### 2025-12-07
- ✓ Design discussion completed
- ✓ Design decisions finalized
- ✓ Task document created
- ✓ Phase 1 implementation completed
  - Created `EnchantmentProtectionCalculator` utility class
  - Added config values to `SharedConfigs.Common`
  - Implemented `handleEnchantedBookFeeding()` in `LovelyRobotEntity`
  - Implemented `processEnchantedBook()` with full enchantment processing
- ✓ Phase 2 implementation completed
  - Added config registration for NeoForge
  - Added config registration for Forge
  - Added config registration for Fabric
  - Unified enchantment reading using 1.21.1 DataComponents API
- ✓ Fixed interaction handling
  - Added enchanted books to `canInteractWithItems()` whitelist
  - Prevents state switching when feeding enchanted books
- ✓ Refactored to follow established patterns
  - Renamed `handleEnchantedBookFeeding()` to `handleProtectionLevelUpInteraction()`
  - Changed to void return type (following pattern of other handlers)
  - Added to `handleInteract()` method chain
  - Follows same pattern as `handleAutoAttack()`, `handleDisplayInteraction()`, etc.
- ✓ Improved generic Protection enchantment logic
  - Now builds list of available (non-maxed) protection types
  - Only selects from protections that can still be increased
  - Prevents wasting generic Protection books on maxed protections
  - If all protections are maxed, book won't be consumed
- Next: Phase 3 - Testing and validation

---

**Notes**: This feature integrates cleanly with existing protection system. No architectural changes needed - just adding a new interaction pathway to increase protection values.
