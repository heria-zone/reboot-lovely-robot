# Enchanted Book Protection Feature - Design Discussion

**Status**: Design Phase
**Date**: 2025-12-07
**Feature**: Feed enchanted books to robots to increase protection values

## Current System Understanding

### How Protection Works Now

**Damage Reduction Formula** (from `LovelyRobotEntity.java` line 643-654):
```java
if (fireProtection > 0)
    amount *= (100.0f - fireProtection) / 100.0f;
```

**Example Calculations**:
- Protection = 80 → Damage multiplier = (100-80)/100 = 0.20 → **Takes 20% damage**
- Protection = 50 → Damage multiplier = (100-50)/100 = 0.50 → **Takes 50% damage**
- Protection = 25 → Damage multiplier = (100-25)/100 = 0.75 → **Takes 75% damage**
- Protection = 100 → Damage multiplier = 0.00 → **Takes 0% damage (invincible)**

**Current Leveling System**:
- Robots gain +1 protection when taking damage of that type
- Caps at config value (default 80 for each type)
- Config: `ProtectionLimitFire/Fall/Blast/Projectile` (0-100 range)

### Why 80 is Special

At **80% protection**, robots become **effectively invincible** because:
1. They take only 20% of incoming damage
2. They gain +1 protection when hit
3. This creates a feedback loop where they level up faster than they take damage

At **lower values** (50, 25):
- Robots still take significant damage even at max protection
- This is intentional - allows server admins to balance difficulty

## Proposed Feature: Enchanted Book Feeding

### Core Concept
Players can feed enchanted books with protection enchantments to robots to increase their protection values directly, bypassing the "take damage to level up" mechanic.

### Minecraft Protection Enchantments

| Enchantment | Max Level | Effect |
|-------------|-----------|--------|
| Fire Protection | IV (4) | Reduces fire/lava damage |
| Blast Protection | IV (4) | Reduces explosion damage |
| Feather Falling | IV (4) | Reduces fall damage |
| Projectile Protection | IV (4) | Reduces projectile damage |

**Note**: Minecraft doesn't have a level V - max is IV (4)

## Design Questions & Discussion

### 1. Value Mapping: How do enchantment levels map to protection points?

**Option A: Proportional Scaling** (RECOMMENDED)
```
protectionGained = (enchantmentLevel / maxEnchantmentLevel) * configMaxProtection
```

**Examples with Fire Protection IV and config = 80**:
- Level I (1/4) → 20 protection points
- Level II (2/4) → 40 protection points
- Level III (3/4) → 60 protection points
- Level IV (4/4) → 80 protection points (full max)

**Examples with Fire Protection IV and config = 50**:
- Level I (1/4) → 12.5 → 13 protection points
- Level II (2/4) → 25 protection points
- Level III (3/4) → 37.5 → 38 protection points
- Level IV (4/4) → 50 protection points (full max)

**Pros**:
- Scales automatically with config changes
- One Level IV book = instant max protection
- Clear, predictable progression

**Cons**:
- Makes books very powerful (one book = max)
- Reduces gameplay progression

---

**Option B: Fixed Point Values**
```
Level I = 10 points
Level II = 20 points
Level III = 30 points
Level IV = 40 points
```

**Examples to reach 80 protection**:
- 2x Level IV books = 80
- 1x Level IV + 1x Level III + 1x Level I = 80
- 4x Level II books = 80

**Pros**:
- Requires multiple books (more gameplay)
- Predictable costs
- Encourages book collection

**Cons**:
- Doesn't scale with config changes
- If config = 50, you'd overshoot with 2 books
- Needs separate config for point values

---

**Option C: Percentage-Based Contribution**
```
Each level adds (level * X%) of remaining protection needed
```

**Example with X=25% and max=80**:
- Start: 0/80
- Add Level I: 0 + (1 * 25% * 80) = 20 → 20/80
- Add Level II: 20 + (2 * 25% * 60) = 50 → 50/80
- Add Level III: 50 + (3 * 25% * 30) = 72.5 → 73/80
- Add Level IV: 73 + (4 * 25% * 7) = 80 → 80/80 (capped)

**Pros**:
- Diminishing returns (prevents instant max)
- Always useful regardless of current level
- Scales with config

**Cons**:
- Complex calculation
- Hard for players to predict
- May feel arbitrary

### 2. Accumulation: How do multiple books work?

**Current Understanding from Your Description**:
> "if I add to the robots 2 books of level II and one book of level I then that would be equal to the 80!"

This suggests **additive accumulation** where:
- Books add their values together
- Capped at config max
- Can "overfeed" (waste enchantment levels beyond max)

**Questions**:
- Should we warn players when they're about to waste enchantment levels?
- Should we prevent feeding if already at max?
- Should we show current protection value in tooltip/GUI?

### 3. Book Consumption: What happens to the book?

**Option A: Full Consumption** (RECOMMENDED)
- Book is completely consumed
- Simple, clean mechanic
- Matches vanilla enchanting table behavior

**Option B: Partial Consumption**
- Book loses enchantment but remains as regular book
- More "realistic" but adds inventory clutter

**Option C: Chance-Based**
- X% chance to consume book
- Adds RNG element (may frustrate players)

### 4. Validation: What books are accepted?

**Must Have**:
- Fire Protection → Increases Fire Protection
- Blast Protection → Increases Blast Protection
- Feather Falling → Increases Fall Protection
- Projectile Protection → Increases Projectile Protection

**Edge Cases**:
- Book with multiple enchantments? (e.g., Fire Protection + Blast Protection)
  - **Option A**: Apply all valid protections
  - **Option B**: Only apply first valid protection
  - **Option C**: Reject multi-enchanted books

- Book with Protection (generic)? (Minecraft has this)
  - **Option A**: Ignore it (only specific protections)
  - **Option B**: Apply to all protection types equally
  - **Option C**: Let player choose which protection to boost

- Book with non-protection enchantments?
  - **Reject**: Only accept books with at least one protection enchantment

### 5. User Feedback: How does player know it worked?

**Visual Feedback**:
- Particle effects (enchantment table particle effecst(?))
- Sound effect (enchantment sound)
- Action bar message: "Fire Protection: 45 → 65 (+20)"

### 6. Configuration: What should be configurable?

**Suggested Config Options**:
```toml
[Protection.EnchantedBooks]
# Enable feeding enchanted books to robots
enableEnchantedBookFeeding = true

# How enchantment levels map to protection points
# "proportional" = scales with protection limits
# "fixed" = fixed points per level (see below)
# "percentage" = percentage-based contribution
mappingMode = "proportional"

# Fixed point values (only used if mappingMode = "fixed")
protectionPointsPerLevel = [10, 20, 30, 40]

# Percentage contribution (only used if mappingMode = "percentage")
percentageContributionPerLevel = 25

# Allow books with multiple enchantments
allowMultiEnchantedBooks = true

# Apply all valid enchantments from multi-enchanted books
applyAllEnchantments = true

# Show warning when feeding would waste enchantment levels
warnOnWaste = true

# Prevent feeding if already at max protection
preventOverfeeding = false
```

## Implementation Architecture

### Where to Add the Logic

**Interaction Handler** (`LovelyRobotEntity.java`):
```java
@Override
protected InteractionResult handleItemInteraction(ItemStack stack, Player player) {
    if (handleTexture(stack, player)) return InteractionResult.SUCCESS;
    if (handleEnchantedBookFeeding(stack, player)) return InteractionResult.SUCCESS; // NEW
    return InteractionResult.PASS;
}
```

**New Method**:
```java
protected boolean handleEnchantedBookFeeding(ItemStack stack, Player player) {
    // 1. Check if item is enchanted book
    // 2. Extract enchantments
    // 3. Validate protection enchantments exist
    // 4. Calculate protection points to add
    // 5. Update robot protection values
    // 6. Consume book
    // 7. Show feedback (particles, sound, message)
    // 8. Return true if consumed
}
```

### Multi-Loader Considerations

**Common** (shared logic):
- Protection calculation logic
- Validation logic
- Config values
- NBT serialization

**Forge/NeoForge/Fabric** (loader-specific):
- Enchantment reading from ItemStack
- Item consumption
- Particle spawning
- Sound playing

### Data Storage

**Already Exists** (no changes needed):
```java
// Protection values already stored in entity NBT
private int fireProtection;
private int fallProtection;
private int blastProtection;
private int projectileProtection;
```

## FINAL DESIGN DECISIONS ✓

### 1. Percentage-Based Contribution (Option C)
```java
protectionGained = (enchantmentLevel * contributionPercentage * configMaxProtection)
// Default contributionPercentage = 25% (0.25)
```

**Examples with 25% contribution and max=80**:
- Fire Protection I: 1 × 0.25 × 80 = 20 points
- Fire Protection II: 2 × 0.25 × 80 = 40 points
- Fire Protection III: 3 × 0.25 × 80 = 60 points
- Fire Protection IV: 4 × 0.25 × 80 = 80 points

**To reach 80 from 0**:
- 2× Level II + 1× Level I = 40+40+20 = 100 (capped at 80) ✓
- 1× Level IV = 80 ✓
- 4× Level I = 80 ✓

**With max=50**:
- Fire Protection IV: 4 × 0.25 × 50 = 50 points (instant max)
- Fire Protection II: 2 × 0.25 × 50 = 25 points (need 2 books)

### 2. Prevent Feeding if Already at Max
- Check current protection value before accepting book
- If already at max, reject the book (no consumption)
- Show message: "Fire Protection already at maximum!"

### 3. Full Book Consumption (Option A)
- Book is consumed completely
- Simple, clean mechanic
- Matches vanilla enchanting behavior

### 4. Multi-Enchantment Books (Option A)
- Apply ALL valid protection enchantments from the book
- Example: Book with Fire Protection II + Blast Protection III
  - Adds 40 to Fire Protection
  - Adds 60 to Blast Protection
  - Consumes book once

### 5. Generic "Protection" Enchantment
- Apply randomly to ONE protection type
- Random selection from: [Fire, Fall, Blast, Projectile]
- Show message indicating which protection was increased

### 6. Current Protection Display
- Already exists: Book & Quill interaction shows current stats
- No changes needed to display system

### 7. Configuration (Following Existing Pattern)
Add to `SharedConfigs.Common`:
```java
// -- ENCHANTED BOOK PROTECTION --
public static boolean EnableEnchantedBookProtection = true;
public static double EnchantedBookContributionPercentage = 0.25; // 25%
```

Add to loader-specific configs (Forge/NeoForge/Fabric):
```java
// In Protection section
ENABLE_ENCHANTED_BOOK_PROTECTION = BUILDER
    .comment("Allow robots to consume enchanted books to increase protection.")
    .define("enable-enchanted-book-protection", true);

ENCHANTED_BOOK_CONTRIBUTION = BUILDER
    .comment("Percentage contribution per enchantment level (0.25 = 25%).",
             "Formula: protectionGained = level * percentage * maxProtection",
             "Example: Fire Protection II with 25% = 2 * 0.25 * 80 = 40 points")
    .defineInRange("enchanted-book-contribution", 0.25, 0.01, 1.0);
```

## Questions for You

1. **Scaling**: Do you want one Level IV book to instantly max out protection, or should it require multiple books?

2. **Multi-Enchantments**: If a book has Fire Protection II + Blast Protection III, should both be applied?

3. **Generic Protection**: Minecraft has a "Protection" enchantment (not fire/blast/fall/projectile specific). Should we:
   - Ignore it?
   - Apply it to all protection types?
   - Let player choose?

4. **Visual Feedback**: Do you want any visual indicator on the robot showing it has high protection? (glow effect, particle aura, etc.)

5. **Balance**: With the current system, robots level up protection by taking damage. Won't books make this too easy? Should books be expensive/rare?

6. **Removal**: Should there be a way to remove/reset protection values? (e.g., using a specific item)

## Next Steps

Once we agree on the design:
1. Create feature specification document
2. Implement in Common module
3. Add loader-specific code for Forge/NeoForge/Fabric
4. Add configuration options
5. Test with various config values
6. Add user documentation

---

**Let's discuss these questions and finalize the design before implementation!**
