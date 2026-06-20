# TODO

## 1. Base Defence — Smarter Patrol Scan Behaviour

The patrol scanner currently looks at plain ground during the scan phase, which feels lifeless. The goal is to make it look like she's actually checking things out.

**What to improve:**

- During the scan rotation, instead of pointing at empty ground, bias the look target toward nearby "suspicious" blocks — things like bushes (leaves, tall grass, flowers, ferns) or decorative blocks that a guard would realistically glance at.
- Also allow the scan to lock onto a nearby passive entity (animal, villager) passing through the patrol area, as if she's watching it with suspicion before deciding it's harmless.

**The illusion this creates:**

She pauses, looks at a flower patch or a passing chicken, holds for a moment, then continues the sweep. The player reads it as attentiveness rather than a scripted rotation.

**Implementation notes:**

- Collect a short candidate list on scan entry: nearby leaf/grass/flower blocks within a small radius, plus any living entities within the same radius that aren't the owner or other robots.
- Pick one candidate per scan cycle. If none are found, fall back to the current ground-level look behaviour.
- The look duration per candidate should match the existing scan dwell time so the pacing stays consistent.

---

## 2. Sound Effects — Dye Interaction and Other Gaps

### Dye interaction

When a player dyes a robot, there's no audio feedback. It should feel tactile.

- Look for a vanilla sound that fits — something like `SoundEvents.WOOL_HIT`, `SoundEvents.SLIME_SQUISH`, or one of the dyeing-adjacent sounds. A soft, satisfying click or squish would work well.
- Play it at the robot's position, not the player's, so it feels like the robot reacted.

### Other places worth adding sound to

Go through the interaction surface and check what's currently silent:

- **Sitting down / standing up** — a small mechanical click or settle sound.
- **State change (Follow → Standby → Defence)** — a short confirmation beep or chime. Could reuse an existing UI sound.
- **Protection upgrade** (enchanted book fed) — currently silent. A soft enchantment shimmer would fit.

---

## 3. Looting — Verify In-Game Drop Bonus

The looting implementation is architecturally correct (investigated 2026-04-30):

- `getItemBySlot(MAINHAND)` returns a diamond sword enchanted with Looting N, where N = `robotLevel / LootEnchantmentLevel`, capped at `MaxLootEnchantment` (default 3).
- Minecraft's loot context reads the killer's main-hand item enchantments to apply the looting bonus — so the hook is correct.
- The `LootEnchantment` config flag is respected via `EnchantmentFeature.lootingEnabled`, set during `reloadFromConfig()`.

**What still needs in-game verification:**

- Confirm that mob drops actually increase at the expected level thresholds (level 10 → Looting I, level 20 → Looting II, level 30 → Looting III with default config).
- Confirm that setting `loot-enchantment = false` in config disables the bonus.
- Check whether the looting bonus applies when the robot kills via the `MeleeAttackGoal` path specifically — the loot context must correctly identify the robot as the killer entity.
