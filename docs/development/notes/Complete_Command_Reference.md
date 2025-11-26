# LovelyRobot Complete Command Reference

**Status:** ✅ Fully Implemented (Forge 1.20.1)  
**Last Updated:** 2025-11-25

## 🎮 Command Overview

All commands support **two targeting modes**:
1. **Crosshair Targeting** - Look at robot, execute command (Blocklings style)
2. **Entity Selector** - Use `@e` selectors for batch operations (Minecraft style)

---

## 📊 Stats Commands (Progression)

### Set Level
**Crosshair:**
```bash
/llovely stats set level <1-200>
```

**Entity Selector:**
```bash
/llovely stats set @e[type=llovelyr:vanilla] level <1-200>
/llovely stats set @e[distance=..10] level 100
```

### Set Experience
**Crosshair:**
```bash
/llovely stats set exp <0+>
```

**Entity Selector:**
```bash
/llovely stats set @e[type=llovelyr:bunny] exp 1000
```

---

## ⚔️ Attributes Commands (Combat Stats)

### Set HP (Health Points)
**Crosshair:**
```bash
/llovely attributes set hp <1+>
```

**Entity Selector:**
```bash
/llovely attributes set @e[type=llovelyr:vanilla] hp 100
```

### Set Attack
**Crosshair:**
```bash
/llovely attributes set attack <1+>
```

**Entity Selector:**
```bash
/llovely attributes set @e[type=llovelyr:dragon] attack 50
```

### Set Defense
**Crosshair:**
```bash
/llovely attributes set defense <0+>
```

**Entity Selector:**
```bash
/llovely attributes set @e[type=llovelyr:vanilla] defense 30
```

### Set Speed
**Crosshair:**
```bash
/llovely attributes set speed <1+>
```

**Entity Selector:**
```bash
/llovely attributes set @e[type=llovelyr:bunny] speed 5
```

**Note:** Speed values are divided by 10 internally (5 = 0.5 movement speed)

### Set All Attributes
**Crosshair:**
```bash
/llovely attributes set all <hp> <attack> <defense> <speed>
```

**Entity Selector:**
```bash
/llovely attributes set @e[type=llovelyr:dragon] all 100 50 30 5
```

---

## ✨ Enchantment Commands

### Set Looting
**Crosshair:**
```bash
/llovely enchant set looting <0-3>
```

**Entity Selector:**
```bash
/llovely enchant set @e[type=llovelyr:vanilla] looting 3
```

### Set All Enchantments
**Crosshair:**
```bash
/llovely enchant set all <looting>
```

**Entity Selector:**
```bash
/llovely enchant set @e[type=llovelyr:neko] all 3
```

---

## 🛡️ Protection Commands

### Set Fire Protection
**Crosshair:**
```bash
/llovely protection set fire <0-80>
```

**Entity Selector:**
```bash
/llovely protection set @e[type=llovelyr:vanilla] fire 40
```

### Set Fall Protection
**Crosshair:**
```bash
/llovely protection set fall <0-80>
```

**Entity Selector:**
```bash
/llovely protection set @e[type=llovelyr:bunny2] fall 60
```

### Set Blast Protection
**Crosshair:**
```bash
/llovely protection set blast <0-80>
```

**Entity Selector:**
```bash
/llovely protection set @e[type=llovelyr:dragon] blast 70
```

### Set Projectile Protection
**Crosshair:**
```bash
/llovely protection set projectile <0-80>
```

**Entity Selector:**
```bash
/llovely protection set @e[type=llovelyr:kitsune] projectile 50
```

### Set All Protections
**Crosshair:**
```bash
/llovely protection set all <fire> <fall> <blast> <projectile>
```

**Entity Selector:**
```bash
/llovely protection set @e[type=llovelyr:vanilla] all 40 40 40 40
/llovely protection set @e[distance=..20] all 80 80 80 80
```

---

## 🎨 Design Commands

### Set Color
**Crosshair:**
```bash
/llovely design set here <color>
```

**Entity Selector:**
```bash
/llovely design set @e[type=llovelyr:vanilla] red
/llovely design set @e[distance=..10] blue
```

**Available Colors:**
- white, orange, magenta, light_blue, yellow, lime, pink, gray
- light_gray, cyan, purple, blue, brown, green, red, black

**Note:** ✅ **FIXED** - Color names are now case-insensitive! `purple`, `PURPLE`, and `Purple` all work.

---

## 👤 Owner Commands

### Get Owner
**Crosshair:**
```bash
/llovely owner get here
```

**Entity Selector:**
```bash
/llovely owner get @e[type=llovelyr:vanilla,limit=1]
```

### Set Owner
**Crosshair:**
```bash
/llovely owner set here <player>
```

**Entity Selector:**
```bash
/llovely owner set @e[type=llovelyr:bunny] PlayerName
/llovely owner set @e[distance=..10] @p
```

---

## 📝 Name Commands

### Set Custom Name
**Crosshair:**
```bash
/llovely name set here <name>
/llovely name set here Guardian
/llovely name set here "My Robot"
```

**Entity Selector:**
```bash
/llovely name set @e[type=llovelyr:vanilla] Guard Bot
/llovely name set @e[distance=..5] "Elite Robot"
```

**Note:** ✅ **FIXED** - Now accepts plain text! No need for JSON format. Names with spaces should be quoted.

---

## 🚀 Teleport Commands

### Teleport Robot to You
**Crosshair (NEW!):**
```bash
/llovely teleport
```

**Robot Identifier:**
```bash
/llovely teleport <robot_name_uuid>
```

**Examples:**
```bash
# Look at robot, then teleport it to you
/llovely teleport

# Teleport specific robot by identifier (with autocomplete)
/llovely teleport Guardian_a1b2c3d4
```

**Features:**
- ✅ Crosshair targeting for quick teleportation
- ✅ Autocomplete suggestions with robot names and levels
- ✅ Safe landing spot detection (prevents suffocation/fall damage)
- ✅ Visual feedback (particles at origin and destination)
- ✅ Audio feedback (enderman teleport sound)
- ⚠️ Cross-dimension teleportation not supported

---

## 🎯 Practical Examples

### Quick Single Robot Operations
```bash
# Look at your robot, then:
/llovely stats set level 100
/llovely stats set exp 5000
/llovely attributes set all 100 50 30 5
/llovely protection set fire 80
/llovely design set here red
/llovely name set here "Guardian"
/llovely owner get here
/llovely teleport
```

### Batch Operations
```bash
# Level up all vanilla robots
/llovely stats set @e[type=llovelyr:vanilla] level 100

# Set attributes for all dragons
/llovely attributes set @e[type=llovelyr:dragon] all 200 100 50 8

# Max protection for all nearby robots
/llovely protection set @e[distance=..20] all 80 80 80 80

# Color code your robot army
/llovely design set @e[type=llovelyr:vanilla] red
/llovely design set @e[type=llovelyr:bunny] blue
/llovely design set @e[type=llovelyr:dragon] gold

# Transfer ownership of all robots to another player
/llovely owner set @e[type=llovelyr:vanilla] PlayerName
```

### Advanced Filtering
```bash
# Only robots within 10 blocks
/llovely stats set @e[type=llovelyr:vanilla,distance=..10] level 50

# Limit to 5 robots
/llovely design set @e[type=llovelyr:bunny,limit=5] pink

# Robots in specific coordinates
/llovely stats set @e[type=llovelyr:dragon,x=100,y=64,z=200,distance=..50] level 200

# Sort by nearest
/llovely protection set @e[type=llovelyr:vanilla,sort=nearest,limit=3] fire 80
```

---

## 🔒 Permission Requirements

- **All commands require OP level 2** (operator permissions)
- **Ownership validation:** Crosshair commands only work on robots you own
- **Entity selectors:** Can target multiple robots but still validates ownership

---

## ⚠️ Important Notes

1. **Crosshair Range:** 5 block reach distance
2. **Ownership:** You can only modify robots you own (unless you're an admin)
3. **Entity Types:** Use `llovelyr:vanilla`, `llovelyr:bunny`, `llovelyr:bunny2`, `llovelyr:dragon`, `llovelyr:neko`, `llovelyr:kitsune`
4. **Teleport Limitations:** Cross-dimension teleportation is not supported (robot and player must be in same dimension)
5. **Safe Teleportation:** System automatically finds safe landing spots to prevent suffocation or fall damage

---

## 🐛 Error Messages

- **"No robot found in crosshair or you don't own it"** - Look directly at your robot
- **"You don't own this robot"** - Can only modify robots you own
- **"Target is not a robot"** - Entity selector matched non-robot entities
- **"No safe location found for teleportation"** - No safe spot near player (try moving to open area)
- **"Cannot teleport robot across dimensions"** - Robot and player must be in same dimension
- **"Robot not found or you don't own it"** - Invalid robot identifier or ownership issue

---

## 🎉 New Features

### Hybrid Teleport Command
The teleport command now supports both targeting modes:

**Crosshair Mode (Blocklings Style):**
- Look at robot → Type `/llovely teleport` → Robot teleports to you
- No need to remember robot names or UUIDs
- Perfect for quick teleportation during gameplay

**Identifier Mode (Original Style):**
- Type `/llovely teleport` → Press TAB for autocomplete
- Select robot from list (shows name and level)
- Useful when robot is far away or in unloaded chunks

---

**Command System Status:** ✅ Fully Implemented (Forge) | ⏳ Pending (Fabric Port)  
**Teleport Command Status:** ✅ ENABLED with Hybrid Targeting
