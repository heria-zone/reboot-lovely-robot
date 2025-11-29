---
created: 2025-11-29
tags:
  - HZLib
  - LevelFeature
  - Configuration
---

# LevelFeature Configuration Integration
## Flexible Per-Entity Max Levels with Config Support

## Problem Statement

Different robot types need different max levels:
- **Vanilla:** 200 (general purpose)
- **Bunny2:** 150 (speed specialist, lower combat)
- **Dragon:** 300 (combat specialist, higher potential)
- **Kitsune:** 250 (support specialist, progressive unlocks)

Max levels should be configurable via config files, allowing server admins to balance gameplay.

## Enhanced LevelFeature Implementati