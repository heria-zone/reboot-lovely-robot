---
created: 2025-11-30
status: Index
tags:
  - Legacy
  - Enhancement
  - Navigation
---

# Legacy 1.20.1 Feature Enhancement - Document Index

## Quick Navigation

### 📋 Start Here
**[Summary Document](./Legacy-1.20.1-Feature-Enhancement-Summary.md)** - Quick overview of all features and what to review

### 📖 Full Proposal
**[Main Proposal](./Legacy-1.20.1-Feature-Enhancement-Proposal.md)** - Complete architectural design with implementation plan

### 🏗️ Architecture
**[Architecture Diagrams](./Legacy-1.20.1-Feature-Enhancement-Architecture-Diagram.md)** - Visual representation of layers and data flows

### 💻 Code Examples
**[Code Examples](./Legacy-1.20.1-Feature-Enhancement-Examples.md)** - Concrete before/after code samples

## What Was Refined

Your original feature requests have been analyzed and refined into a comprehensive architectural proposal:

### 1. CombatLevelFeature ✅
- **Your Request**: Convert InternalLogic calculations into features
- **Refined**: Strategy pattern with multiple calculation formulas
- **Benefits**: Configurable per robot, testable, reusable

### 2. Data Management ✅
- **Your Request**: Add enchantment/protection data, move get/set to proper classes
- **Refined**: Separate stat classes with NBT serialization
- **Benefits**: Encapsulated, maintainable, clear ownership

### 3. Robot Registry ✅
- **Your Request**: Track robots per owner, enforce spawn limits
- **Refined**: Full registry system with position tracking
- **Benefits**: Spawn limits, powerful commands, future features

### 4. Command System ✅
- **Your Request**: New command format with validation
- **Refined**: Three targeting methods with full validation
- **Benefits**: Better UX, prevents invalid states, extensible

## Document Structure

```
Legacy-1.20.1-Feature-Enhancement-INDEX.md (this file)
├── Summary.md
│   ├── Quick overview
│   ├── Key improvements
│   ├── What to review
│   └── Questions for you
│
├── Proposal.md
│   ├── Executive summary
│   ├── Problem statement
│   ├── Proposed solution
│   │   ├── CombatLevelFeature
│   │   ├── Data management
│   │   ├── Robot registry
│   │   └── Command system
│   ├── Implementation plan (7 phases)
│   ├── Architecture alignment
│   ├── Benefits & risks
│   └── Success criteria
│
├── Architecture-Diagram.md
│   ├── Layer architecture
│   ├── Data flow diagrams
│   ├── Class relationships
│   └── Package structure
│
└── Examples.md
    ├── CombatLevelFeature usage
    ├── Data management before/after
    ├── Registry integration
    ├── Command implementations
    ├── Calculation strategies
    ├── Config integration
    └── Testing examples
```

## Key Features

### CombatLevelFeature
- **Location**: Framework → Lib → Common
- **Purpose**: Level-based attribute calculation with strategy pattern
- **Strategies**: Linear (current), Exponential (Dragon), Custom (Kitsune)
- **Integration**: Attached to RobotEntityType like LevelFeature

### Data Management
- **Classes**: CombatStats, ProtectionStats, EnchantmentStats
- **NBT**: Each class handles own serialization via IReadWriteNBT
- **Benefits**: Encapsulated, testable, maintainable

### Robot Registry
- **Tracking**: Per-owner robot collection with position updates
- **Limits**: Configurable max robots per owner
- **Data**: UUID, name, type, dimension, position, timestamp
- **Integration**: Automatic registration/unregistration

### Command System
- **Formats**:
  - `/llovelyr group <selector> <action> [args...]`
  - `/llovelyr owner <player> list`
  - `/llovelyr owner <player> robot <index> <action> [args...]`
  - `/llovelyr robot <action> [args...]`
- **Validation**: Against config limits (max level, max protections)
- **Features**: Auto-level-up, ownership transfer, registry integration

## Implementation Timeline

**Total**: 19-25 days across 7 phases

1. **Framework Layer** (2-3 days) - Pure Java
2. **Lib Layer** (3-4 days) - Minecraft integration
3. **Common Layer** (4-5 days) - Entity integration
4. **Command System** (3-4 days) - New commands
5. **Config Integration** (2-3 days) - Config updates
6. **Testing** (3-4 days) - Comprehensive testing
7. **Documentation** (2 days) - Docs and ADRs

## Architecture Alignment

Follows established HZ Framework → HZ Lib → Common → Source pattern:

- **Framework**: Pure Java, zero Minecraft dependencies
- **Lib**: Loader-specific bridges, minimal dependencies
- **Common**: Mod-specific shared code
- **Source**: Loader-specific implementations

All code designed for future extraction to external HZLib library.

## Questions to Answer

### Architecture
1. Do you approve the strategy pattern approach?
2. Are the stat classes sufficient?
3. Is the registry structure adequate?
4. Are the command formats correct?

### Implementation
1. Which phase should we start with?
2. Can any features be deferred?
3. Forge first, then Fabric? Or parallel?
4. Parallel with other work?

### Configuration
1. Different formulas per robot type?
2. Additional registry data needed?
3. Spawn limits: global, per-dimension, or per-owner?
4. Command permission levels?

### Features
1. Which enchantments initially?
2. Are 4 protection types sufficient?
3. Backward compatibility importance?
4. Migration tool needed?

## Next Steps

1. **Review**: Read Summary.md for quick overview
2. **Deep Dive**: Read Proposal.md for full details
3. **Visualize**: Check Architecture-Diagram.md
4. **Understand**: Review Examples.md for code samples
5. **Feedback**: Provide your thoughts and questions
6. **Approve**: Green light the approach
7. **Plan**: Create detailed implementation tasks
8. **Implement**: Start with Framework layer

## Related Documents

### Project Documentation
- **HZLib Architecture**: `docs/development/HZLib-EntityType-Architecture.md`
- **Refactoring Plan**: `docs/development/Legacy-1.20.1-Refactoring-Plan.md`
- **Current Tasks**: `specs/hzlib-entitytype-legacy-1-20-1/tasks.md`
- **Development Checklist**: `docs/development/tasks/November-2025-Development-Checklist.md`

### Steering Documents
- **Coding Standards**: `steering/project-coding-style.md`
- **Project Structure**: `steering/project-structure.md`
- **Development Guidelines**: `steering/development.md`
- **Documentation Standards**: `steering/documentation.md`
- **Workflows**: `steering/workflows.md`

## Status

- **Created**: 2025-11-30
- **Status**: Proposal - Awaiting Review
- **Author**: Development Team
- **Version**: 1.0
- **Ready for Review**: ✅ Yes

---

**Start with**: [Summary Document](./Legacy-1.20.1-Feature-Enhancement-Summary.md) for quick overview
