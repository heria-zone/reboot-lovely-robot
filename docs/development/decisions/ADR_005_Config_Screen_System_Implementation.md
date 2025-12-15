# ADR 005: Config Screen System Implementation

**Status**: Accepted
**Date**: 2025-01-15
**Decision Makers**: Development Team
**Consulted**: Project Architecture

## Context

The Legacy 1.21.1 variant needs a configuration screen system that works across NeoForge, Forge, and eventually Fabric loaders. The system must integrate with the existing `SharedConfigs` and `ConfigBounds` infrastructure while providing a user-friendly interface for the extensive configuration options available.

## Decision

Implement a vanilla Minecraft-based config screen system with the following architecture:

### Core Principles
1. **Preserve Existing Infrastructure**: No changes to `SharedConfigs.java` or `ConfigBounds.java`
2. **Common/Loader Separation**: All screen logic in Common module, loader-specific registration only
3. **Category-Based Organization**: Match existing config file structure with tabbed interface
4. **Save & Apply Pattern**: Changes stored temporarily until user confirms
5. **Comprehensive Reset Options**: Per-category and global reset functionality
6. **Cloth Config Migration Ready**: Architecture supports future migration to Cloth Config API

### Screen Hierarchy

```
Main Config Screen (Tabbed Interface)
├── General Tab
│   ├── Robot Limits (OwnerMaxRobotNum)
│   ├── Movement Speeds (Melee, Follow, Wander)
│   └── Follow/Look Distances (Min/Max distances, Look range)
├── Combat Tab
│   ├── Combat Settings (Friendly fire, Attack chance, Heal interval)
│   ├── Protection Limits (Fire, Fall, Blast, Projectile)
│   └── Enchanted Book Protection (Enable, Contribution percentage)
├── Experience Tab
│   └── XP System (Base XP, Multiplier)
├── AI Behavior Tab
│   ├── Wander Behavior (Check interval, Chance, Radius, Duration, Cooldown)
│   ├── Patrol Behavior (Duration, Guard duration, Pause duration, Rotation speed)
│   └── Collision Avoidance (Enable, Detection radius, Min spacing, Offset)
├── Animation Tab
│   └── Animation Timings (Standby to sit delays)
├── Renderer Tab (Client-only)
│   └── Visual Settings (Shadow radius)
└── Entity Tab (Sub-tabbed by Robot Type)
    ├── Vanilla Robot Sub-tab
    ├── Honey Robot Sub-tab
    ├── Bunny Robot Sub-tab
    ├── Bunny2 Robot Sub-tab
    ├── Dragon Robot Sub-tab
    ├── Kitsune Robot Sub-tab
    └── Neko Robot Sub-tab
```

### Implementation Architecture

#### Common Module Structure
```
Common/src/main/java/net/msymbios/llovelyr/common/
├── Configs/                           [Existing - No Changes]
│   ├── SharedConfigs.java             [Existing - Unchanged]
│   ├── ConfigBounds.java              [Existing - Unchanged]
│   └── ConfigScreenManager.java       [NEW - State management]
└── client/
    └── screens/
        ├── ConfigScreen.java                    [NEW - Main tabbed screen]
        ├── tabs/                               [NEW - Category implementations]
        │   ├── GeneralConfigTab.java
        │   ├── CombatConfigTab.java
        │   ├── ExperienceConfigTab.java
        │   ├── AIBehaviorConfigTab.java
        │   ├── AnimationConfigTab.java
        │   ├── RendererConfigTab.java
        │   └── EntityConfigTab.java            [NEW - Robot sub-tabs container]
        ├── subtabs/                            [NEW - Robot-specific screens]
        │   ├── VanillaRobotSubTab.java
        │   ├── HoneyRobotSubTab.java
        │   ├── BunnyRobotSubTab.java
        │   ├── Bunny2RobotSubTab.java
        │   ├── DragonRobotSubTab.java
        │   ├── KitsuneRobotSubTab.java
        │   └── NekoRobotSubTab.java
        └── widgets/                            [NEW - Custom config widgets]
            ├── BoundedSliderWidget.java
            ├── BoundedIntegerEditBox.java
            ├── ConfigToggleButton.java
            └── ResetButton.java
```

#### State Management Strategy

**ConfigScreenManager Responsibilities:**
- Maintain temporary values map (not applied until Save & Apply)
- Provide validation using existing `ConfigBounds` methods
- Handle category-specific and global reset operations
- Coordinate save operations with loader-specific config systems
- Track dirty state for visual feedback

**Save & Apply Flow:**
1. User modifies widget → Store in temporary values map
2. Real-time validation with visual feedback
3. User clicks "Save & Apply" → Write to loader config → Reload `SharedConfigs`
4. User clicks "Cancel" → Discard temporary values, revert widgets

**Reset Operations:**
- Per-category reset: Reset temporary values for current category to defaults
- Per-robot reset: Reset temporary values for specific robot to defaults
- Global reset: Reset all temporary values to defaults
- All reset operations require confirmation dialog

#### Widget Design Patterns

**Boolean Settings:**
- Standard toggle buttons with descriptive labels
- Tooltip support for detailed explanations

**Integer/Float Ranges:**
- Custom slider widgets with text display
- Bounded by `ConfigBounds` validation
- Support for special cases (e.g., -1 = unlimited)
- Real-time validation with error indicators

**Robot Stats Layout:**
- Consistent widget layout across all robot types
- Sliders for: Max Level, Attack Speed, Movement Speed, Base Toughness, Base HP, Base Attack, Base Defense
- Per-robot reset button

#### Loader Integration

**NeoForge/Forge Registration:**
```java
ModLoadingContext.get().registerExtensionPoint(
    IModConfigScreenFactory.class,
    () -> (minecraft, parent) -> new ConfigScreen(parent)
);
```

**Fabric Integration (Future):**
- Placeholder for ModMenu integration
- Ready for Cloth Config API when implemented

## Consequences

### Positive
- **Preserves Existing Architecture**: No disruption to current config system
- **User-Friendly Interface**: Organized, tabbed interface matching config file structure
- **Robust State Management**: Save & Apply pattern prevents accidental changes
- **Comprehensive Reset Options**: Users can reset individual categories or everything
- **Future-Proof**: Easy migration path to Cloth Config API
- **Cross-Loader Compatibility**: Works on NeoForge, Forge, and ready for Fabric

### Negative
- **Implementation Complexity**: Significant amount of UI code to implement
- **Maintenance Overhead**: Custom widgets require ongoing maintenance
- **Vanilla Widget Limitations**: Less sophisticated than dedicated config libraries

### Risks
- **Widget Compatibility**: Vanilla widgets may have version-specific behavior changes
- **Performance**: Large number of config options may impact screen performance
- **Validation Complexity**: Ensuring all edge cases are handled correctly

## Alternatives Considered

### Direct Cloth Config Implementation
**Rejected**: Would require immediate Fabric dependency and complicate multi-loader support

### Simple Properties File Editor
**Rejected**: Poor user experience, no validation, error-prone

### Loader-Specific Config Screens
**Rejected**: Would duplicate code across loaders and complicate maintenance

## Related Decisions
- ADR_002: Multiloader Dependency Configuration - Establishes Common/Loader separation pattern
- ADR_004: Robot Creator System - Demonstrates complex UI implementation patterns

## Implementation Plan

### Phase 1: Core Infrastructure
1. Implement `ConfigScreenManager` with state management
2. Create base widget classes with validation
3. Implement main `ConfigScreen` with tab system

### Phase 2: Category Implementation
1. Implement General, Combat, Experience tabs
2. Implement AI Behavior, Animation, Renderer tabs
3. Add per-category reset functionality

### Phase 3: Entity System
1. Implement `EntityConfigTab` with sub-tab system
2. Create robot-specific sub-tabs
3. Add per-robot reset functionality

### Phase 4: Loader Integration
1. Implement NeoForge registration
2. Implement Forge registration
3. Add Fabric placeholder

### Phase 5: Polish & Testing
1. Add tooltips and help text
2. Implement confirmation dialogs
3. Comprehensive testing across loaders
4. Performance optimization

## Validation Criteria

### Functional Requirements
- [ ] All config values accessible through UI
- [ ] Save & Apply pattern works correctly
- [ ] Reset operations work for categories and globally
- [ ] Validation prevents invalid values
- [ ] Works on NeoForge and Forge loaders

### User Experience Requirements
- [ ] Intuitive navigation between categories
- [ ] Clear visual feedback for modified values
- [ ] Helpful tooltips and descriptions
- [ ] Responsive performance with all options visible

### Technical Requirements
- [ ] No modifications to existing config infrastructure
- [ ] Common module contains all screen logic
- [ ] Loader-specific code minimal and isolated
- [ ] Ready for Cloth Config migration

---

**Implementation Status**: Ready to begin
**Next Steps**: Start with Phase 1 - Core Infrastructure implementation