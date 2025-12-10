# LovelyRobot Legacy - Player Features

## What is LovelyRobot Legacy?

LovelyRobot Legacy is a Minecraft mod that adds customizable robotic companions to your world. These robots are loyal allies that fight alongside you, defend your base, and grow stronger through experience. With 7 unique robot types and 16 color variations, you can build a personalized robot army.

## Robot Types

The mod features **7 distinct robot variants**, each with unique stats and specializations:

- **Vanilla** - General-purpose companion with balanced stats
- **Honey** - Support-oriented robot with lower combat stats but high utility
- **Bunny** - Balanced robot with good speed and moderate combat ability
- **Bunny2** - Alternative bunny design with enhanced combat stats
- **Dragon** - Heavy combat robot with high HP, attack, and knockback resistance
- **Neko** - Agile fighter with high attack and exceptional speed
- **Kitsune** - Mystical companion with balanced stats and unique aesthetics

Each robot type has different base stats (HP, Attack, Defense, Speed) and can reach **level 200**.

## Customization

### 16-Color Palette
Customize your robot's appearance using any of Minecraft's 16 dye colors:
- White, Orange, Magenta, Light Blue
- Yellow, Lime, Pink, Gray
- Light Gray, Cyan, Purple, Blue
- Brown, Green, Red, Black

**How to change colors:** Right-click your robot with any dye to instantly change its color.

### Custom Names
Give your robots unique names to personalize them. Named robots receive a **50% experience bonus** (1.5x multiplier) as a reward for your investment.

## Leveling System

### Experience Gain
Robots gain experience by:
- **Dealing damage** to hostile mobs
- **Taking damage** from enemies
- **Killing enemies** (awards accumulated experience)

Experience is accumulated during combat and awarded when the enemy dies, preventing farming from immortal entities.

### Level Progression
- Maximum level: **200** (configurable per robot type)
- Experience formula: Base (50) × Multiplier (2) × Level
- Named robots earn **1.5x experience**
- Level-up triggers visual effects (particles) and sound

## Protection System

### Adaptive Protection
Robots develop resistance to damage types through exposure:
- **Fire Protection** - Reduces fire, lava, and burning damage
- **Fall Protection** - Reduces fall damage
- **Blast Protection** - Reduces explosion damage
- **Projectile Protection** - Reduces arrow and projectile damage

Each protection type can reach up to **80%** damage reduction (configurable).

### Enchanted Book Feeding
Feed enchanted books to your robots to boost their protection:
- **Fire Protection** enchantment → Fire Protection stat
- **Blast Protection** enchantment → Blast Protection stat
- **Feather Falling** enchantment → Fall Protection stat
- **Projectile Protection** enchantment → Projectile Protection stat
- **Protection** (generic) → Random protection type

**Formula:** Each enchantment level contributes 25% of max protection (e.g., Protection IV = 20% of 80% = 16 points).

Books are consumed on use, and multi-enchanted books apply all valid protections.

## Combat Features

### Auto-Attack Mode
Toggle automatic hostile mob targeting:
- **Enable/Disable:** Right-click robot with any sword
- When enabled, robots automatically attack nearby monsters
- Excludes creepers and other robots
- Configurable attack chance (default: 5%)

### Looting Enchantment
Robots can have looting enchantment for better mob drops:
- Configurable looting level (default: up to level 10)
- Maximum looting level: 3 (configurable)
- Applies to all mob kills

### Auto-Heal
Robots automatically regenerate health over time:
- Heal interval: 50 ticks (2.5 seconds) by default
- Can be toggled globally in config
- Activates outside of combat

### Combat Modes
Robots enter combat mode when:
- Taking damage
- Attacking enemies
- Being attacked by players (if friendly fire enabled)
- Owner is attacked

Combat mode affects behavior and animation states.

## Behavioral Modes

### Follow Mode (Default)
Robot follows its owner:
- Maintains distance: 4-10 blocks (configurable)
- Teleports if too far away
- Can wander when owner is stationary (15% chance every 10 seconds)
- Wander radius: 3-6 blocks for 5-10 seconds

### Standby Mode
Robot stays in place:
- Sits down after 30-90 seconds of inactivity (random)
- Hitbox shrinks when sitting (1.8 → 1.0 blocks tall)
- Stands up when moving
- **Toggle:** Right-click robot 

### Base Defense Mode
Robot guards a specific location:
- **Activate:** Right-click robot with compass or recovery compass
- Patrols around set base location
- Automatically attacks nearby hostiles
- Enforces defense radius (10 blocks default)
- Warps back if pulled too far (15 blocks default)

## Interaction System

### Right-Click Interactions
- **Dye** - Change robot color
- **Sword** - Toggle auto-attack mode
- **Compass/Recovery Compass** - Set base defense mode
- **Enchanted Book** - Increase protection stats
- **Book** - Display robot stats
- **Writable Book** - Display enchantment info
- **Oak Button** - Toggle notification messages
- **Sneak + Right-Click** - Picks up the robot
- **Right-Click** - Toggle states between standby and follow

### Robot Core Item
When a robot dies, it drops a **Robot Core** containing:
- Robot type and color
- Custom name
- Owner information
- Level and experience
- All protection values

**Smart Core Retrieval:** If owner is within 16 blocks (configurable) and in survival mode, the core automatically goes to their inventory instead of dropping on the ground.

Cores glow with a color matching the robot's dye color for easy visibility.

## Commands

Comprehensive command system for robot management (requires OP level 2):

### Basic Commands
- `/llovely robot stats` - View robot stats (crosshair targeting)
- `/llovely robot heal` - Heal robot to full health
- `/llovely robot recall` - Teleport robot to you

### Owner Management
- `/llovely owner list player` - List all robot owners
- `/llovely owner list robot <player>` - List player's robots
- `/llovely owner stats <player> <index>` - View specific robot stats
- `/llovely owner heal <player> <index>` - Heal specific robot
- `/llovely owner healall <player>` - Heal all of player's robots
- `/llovely owner transfer <from> <index> <to>` - Transfer ownership

### Stat Modification
- `/llovely robot set combat all <level> <exp>` - Set level and exp
- `/llovely robot set attribute all <hp> <attack> <defense> <speed>` - Set all attributes
- `/llovely robot set protection all <fire> <fall> <blast> <projectile>` - Set all protections
- `/llovely robot set appearance <color>` - Change color
- `/llovely robot set identifier <name>` - Rename robot

### Target Commands
Use entity selectors (@e, @p, @a, @r) for batch operations:
- `/llovely target @e[type=llovelyr:bunny] heal` - Heal all bunnies
- `/llovely target @e[distance=..10] set combat level 50` - Level up nearby robots

## Visual Features

### Animations
- **IDLE** - Standing still
- **WALK** - Moving
- **REST** - Standby mode (standing)
- **SIT** - Standby mode (sitting after delay)
- **COMBAT** - Fighting stance
- **ATTACK** - Attack animation

### Particles
- Level-up: Happy villager particles
- Combat radius: Particle ring (when enabled)
- Core drop: Glowing effect with color-coded glow

### Sounds
- Level-up: Player level-up sound
- Core recovery: Enchantment table sound
- Combat: Standard entity sounds

## Configuration Options

Extensive config system allows customization of:
- Robot spawn limits per player (default: 30)
- Movement speeds for all modes
- Follow distances and ranges
- Experience formulas and multipliers
- Protection limits for each type
- Combat behavior (friendly fire, auto-heal, attack chance)
- AI behavior timings (wander, patrol, guard)
- Smart core retrieval distance
- Enchanted book contribution percentage
- Animation timings
- Entity dimensions

## Multiplayer Features

- **Ownership System:** Each robot belongs to one player
- **Spawn Limits:** Configurable max robots per player (default: 30)
- **Ownership Transfer:** Admins can transfer robots between players
- **Friendly Fire:** Configurable - prevent owner from damaging their robots
- **Smart Retrieval:** Cores automatically go to owner's inventory when nearby

## Technical Features

- **Multi-Loader Support:** Forge, Fabric, and NeoForge
- **Minecraft Version:** 1.21.1
- **Data Persistence:** All robot stats saved in NBT/Data Components
- **Client-Server Sync:** Proper synchronization of all robot states
- **Performance:** Optimized AI with configurable tick intervals
- **Collision Avoidance:** Robots space themselves out to prevent clustering

---

**In Summary:** LovelyRobot Legacy gives you customizable robotic companions that level up, learn protections, and fight alongside you. With 7 robot types, 16 colors, and extensive behavioral options, you can build a unique robot army tailored to your playstyle.
