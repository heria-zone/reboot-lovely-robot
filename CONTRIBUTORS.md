# Lovely Robot — Contributors

This file records everyone who has made a meaningful contribution to the Lovely Robot family of mods, with a description of what they built. Copyright in each contribution belongs to its author.

---

## Original Creators

**lilacx02**

Creator of the original [Lovely Robot](https://www.curseforge.com/minecraft/mc-mods/lovelyrobot) mod (1.12.2). The concept, the original four robot types (Vanilla, Honey, Bunny, Bunny2), and all original art assets originate from their work. Lovely Robot: Tribute exists specifically to preserve what they built.

**Virtualblack867 & D Flog Flag**

Creators of the [Lovely Robots Bedrock addon](https://www.curseforge.com/minecraft-bedrock/addons/lovelyrobots). Art references and additional robot designs used in the rebuild draw from their work.

---

## Heria Zone

**MSymbios** ([@MSymbios](https://github.com/MSymbios)) — *Heria Zone*

Full rebuild of the Lovely Robot ecosystem from the ground up. All code, all new systems, all new robot types:

**Architecture & Libraries:**
- HZLib — shared multi-loader entity framework (see HZLib contributors)
- LovelyLib — robot-specific shared library (see LovelyLib contributors)
- Multi-loader monorepo structure (Fabric, Forge, NeoForge via shared Common modules)
- Data-driven robot registration (`RobotDefinitionRegistry`) — adding a robot from 30 file edits to 2

**Lovely Robot: Legacy**
- All 8 robot types: Vanilla, Honey, Bunny, Bunny2, Bunny3, Dragon, Neko, Kitsune
- 16-colour palette system
- Levelling system with type-specific caps and XP bonus for named robots
- Protection system (fire, fall, blast, projectile resistance with enchantment integration)
- Smart Core Retrieval — core auto-flies to inventory on death
- Ctrl+Shift+Empty Hand pickup — robot → spawn item with data preserved
- Health persistence across world reloads
- Sitting pose animation with dynamic hitbox and vehicle support
- Core glow — colour-coded team scoreboard system
- Base Defense Scan — 4 patrol patterns

**Lovely Robot: Tribute**
- Preservation build — original four robots, original colour palettes
- `TRIBUTE_PROFILE` — faithful standby animation (no idle slot cycle)
- `AiTributeReturnToBaseGoal` — faithful recreation of original `EntityAIBunnyFollowPoint`
- Save migration chain including Gen1-Fabric locale string resolution

**Lovely Robot: Reboot**
- `Empyrium` — cold-gold apex robot, blocks all item interaction
- `Hyperion` — Commander/Valkyrie toggle via Blaze Rod
- `Prime` — seven-colour Aldarian palette cycling via Blaze Rod

---

## Contributors

*No external contributions yet. Your name could be here.*

---

## How to Read This File

Each entry lists:
- **Name** and GitHub handle (or platform handle for original creators)
- **What they built** — specific systems, features, robot types, or art
- Copyright in each contribution belongs to the contributor

For contribution guidelines, see [`CONTRIBUTING.md`](CONTRIBUTING.md).
