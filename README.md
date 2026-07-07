<p align="center">
    <img width="461" src="https://i.imgur.com/ibMVt4s.png" alt="Lovely Robot">
</p>

<p align="center">
    Robot companions for Minecraft — three variants, one codebase
</p>

<p align="center">
    <a href="https://discord.gg/ZmCPM22FCK">
        <img alt="Discord" src="https://img.shields.io/discord/1156134479149158402?logo=Discord">
    </a>
    <a href="https://github.com/heria-zone/reboot-lovely-robot/issues">
        <img alt="GitHub Issues" src="https://img.shields.io/github/issues/heria-zone/reboot-lovely-robot">
    </a>
    <a href="LICENSE">
        <img alt="License" src="https://img.shields.io/badge/license-MIT-blue">
    </a>
</p>

<p align="center">
    <a href="#lovely-robot-legacy">Legacy</a> ·
    <a href="#lovely-robot-tribute">Tribute</a> ·
    <a href="#lovely-robot-reboot">Reboot</a> ·
    <a href="#dependencies">Dependencies</a> ·
    <a href="#installation">Installation</a> ·
    <a href="#contributing">Contributing</a>
</p>

---

## About

Lovely Robot is a robot companion mod family for Minecraft. Craft a robot, name her, bring her along — and watch what changes when you actually spend time together.

This is a monorepo hosting three parallel variants that share a common codebase via [HZLib](https://github.com/heria-zone/hzlib) and [LovelyLib](https://modrinth.com/mod/lovelylib). Each variant is a separate published mod with its own identity and scope.

---

## The Three Variants

---

### Lovely Legacy

<p>
    <a href="https://www.curseforge.com/minecraft/mc-mods/lovely-legacy">
        <img alt="CurseForge Downloads" src="https://img.shields.io/curseforge/dt/1595115?logo=CurseForge">
    </a>
    <a href="https://modrinth.com/mod/NNG8nMth">
        <img alt="Modrinth Downloads" src="https://img.shields.io/modrinth/dt/NNG8nMth?logo=Modrinth">
    </a>
</p>

The current-generation experience. All the robots, all the features, expanded and updated — but still recognisably the mod you remember.

**8 robot types:** Vanilla · Honey · Bunny · Bunny2 · Bunny3 · Dragon · Neko · Kitsune — all in 16 dye colours.

**Features:**
- Levelling system with type-specific caps and XP bonus for named robots
- Protection system — fire, fall, blast, projectile resistance with enchantment integration
- Smart Core Retrieval — core auto-flies to inventory on death
- Base Defense Scan — 4 patrol patterns (FULL_SCAN, DOUBLE_SWEEP, QUADRANT_CHECK, RANDOM_POINTS)
- Sitting pose animation with dynamic hitbox and vehicle support
- Core glow — colour-coded scoreboard team system
- `/llovely` command tree — list, summon, teleport, stats, config

**Minecraft 1.21.1 · Fabric · Forge · NeoForge**

---

### Lovely Tribute

<p>
    <a href="https://www.curseforge.com/minecraft/mc-mods/lovely-tribute">
        <img alt="CurseForge Downloads" src="https://img.shields.io/curseforge/dt/1595114?logo=CurseForge">
    </a>
    <a href="https://modrinth.com/mod/ozWMGtmN">
        <img alt="Modrinth Downloads" src="https://img.shields.io/modrinth/dt/ozWMGtmN?logo=Modrinth">
    </a>
</p>

A faithful recreation of lilacx02's original companion mod. The same four robots, their original colours, their original feel — modernised to run on current Minecraft versions. This version exists to preserve what was built.

**4 robot types:** Vanilla · Honey · Bunny · Bunny2 — original colour palettes, no expanded palette.

**Minecraft 1.21.1 · Fabric · Forge · NeoForge**

---

### Lovely Reboot

<p>
    <a href="https://www.curseforge.com/minecraft/mc-mods/lovely-reboot">
        <img alt="CurseForge Downloads" src="https://img.shields.io/curseforge/dt/822853?logo=CurseForge">
    </a>
    <a href="https://modrinth.com/mod/aFZGvBS6">
        <img alt="Modrinth Downloads" src="https://img.shields.io/modrinth/dt/aFZGvBS6?logo=Modrinth">
    </a>
</p>

The forward-looking branch. Where the vision is being built. New robots, new mechanics, the Assembly Station — more to come.

**Current exclusive robots:**
- **Empyrium** — cold-gold apex; blocks all item interaction
- **Hyperion** — toggles Commander / Valkyrie forms with a Blaze Rod
- **Prime** — seven-colour Aldarian palette cycling with a Blaze Rod

**Minecraft 1.21.1 · Fabric · Forge · NeoForge** *(in active development)*

---

## Dependencies

All three variants require the same three libraries:

| Library | CurseForge | Modrinth |
|---|---|---|
| **HZLib** | [1586461](https://www.curseforge.com/minecraft/mc-mods/hzlib) | [KxsiDURd](https://modrinth.com/mod/hzlib) |
| **LovelyLib** | [1586602](https://www.curseforge.com/minecraft/mc-mods/lovelylib) | [bakhGE6B](https://modrinth.com/mod/lovelylib) |
| **GeckoLib** | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/geckolib) | [Modrinth](https://modrinth.com/mod/geckolib) |

---

## Installation

1. Download your chosen variant (Legacy, Tribute, or Reboot) from CurseForge or Modrinth
2. Download **HZLib**, **LovelyLib**, and **GeckoLib**
3. Place all `.jar` files in your `mods` folder
4. Launch Minecraft

---

## Monorepo Structure

```
sources/
├── legacy/          ← Lovely Robot: Legacy
├── tribute/         ← Lovely Robot: Tribute
├── reboot/          ← Lovely Robot: Reboot
└── common/
    ├── lovelylib-1.21.1/   ← LovelyLib (robot shared library)
    └── hzlib-1.21.1/       ← HZLib (general shared library)
```

Each variant shares the Common module via LovelyLib and HZLib. Loader modules (Fabric/, Forge/, NeoForge/) contain only loader-specific registration code.

---

## Version Support

| Minecraft | Fabric | Forge | NeoForge |
|-----------|--------|-------|----------|
| 1.21.1    | ✅ v1.0.0 | ✅ v1.0.0 | ✅ v1.0.0 |
| 1.20.1    | Planned | Planned | — |
| 1.19.4 → 1.16.5 | Planned | Planned | — |
| 1.12.2    | — | Planned | — |
| 1.7.10    | — | Planned | — |

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) and [CONTRIBUTORS.md](CONTRIBUTORS.md).

---

## Issues

[GitHub Issues](https://github.com/heria-zone/reboot-lovely-robot/issues)

---

## License

The **source code** of this project is licensed under the [MIT License](LICENSE).
Art assets are sourced from the original mods by **lilacx02** and **Virtualblack867 & D Flog Flag**, used under their MIT license with attribution.

See [LICENSE](LICENSE) and [CONTRIBUTORS.md](CONTRIBUTORS.md) for full details.

---

## Credits

**Original mod:** [Lovely Robot](https://www.curseforge.com/minecraft/mc-mods/lovelyrobot) by `lilacx02`

**Bedrock addon reference:** [Lovely Robots](https://www.curseforge.com/minecraft-bedrock/addons/lovelyrobots) by `Virtualblack867` & `D Flog Flag`

**Rebuilt by:** `MSymbios` / Heria Zone