# ADR 007: Dynamic Entity Configuration System

**Status**: Proposed  
**Date**: 2025-12-17  
**Decision Makers**: Development Team  
**Consulted**: Architecture Review  

## Context

The current entity configuration system in LovelyLib and Legacy uses hardcoded individual variables for each robot type's stats (e.g., `BunnyMaxLevel`, `DragonAttackSpeed`, etc.). This approach has several limitations:

### Current Problems
1. **Code Duplication**: 49+ individual config variables (7 robots × 7 stats each)
2. **Maintenance Overhead**: Adding new robot types requires extensive code changes across multiple files
3. **Configuration Complexity**: Users must navigate through dozens of individual settings
4. **Scalability Issues**: System doesn't scale well for future robot variants (Tribute, Reboot)
5. **Runtime Inflexibility**: Difficult to add new stats or robot types without code changes

### Current Architecture
- **LovelyLib SharedConfigs**: Contains hardcoded individual variables per robot type
- **Legacy LovelyConfigs**: Forge-specific config implementation with individual ForgeConfigSpec entries
- **LegacyConfigs**: Partial HashMap implementation started but incomplete
- **Multiloader Support**: Separate implementations for Forge, Fabric, and NeoForge

### Requirements
1. **Runtime Reloading**: Must support existing callback system (`LovelyRobotType::reloadFromConfig`)
2. **Multiloader Compatibility**: Must work across Forge, Fabric, and NeoForge
3. **Validation**: Invalid configs must fall back to defaults
4. **Backward Compatibility**: Smooth migration from current system
5. **Extensibility**: Easy addition of new robot types and stats

## Decision

We will implement a **Dynamic Entity Configuration System** that replaces hardcoded individual variables with a flexible, data-driven approach using HashMap-based configuration with builder patterns and comprehensive validation.

### Core Components

#### 1. Enhanced EntityConfigData Structure
```java
public static class EntityConfigData {
    // Core stats
    public int maxLevel;
    public int baseHp;
    public int baseAttack;
    public float attackSpeed;
    public int baseDefense;
    public float baseToughness;
    public float movementSpeed;
    
    // Validation and defaults
    public boolean isValid();
    public static EntityConfigData getDefault();
    public EntityConfigData validateOrDefault();
    
    // Builder pattern for flexibility
    public static class Builder {
        public Builder maxLevel(int level);
        public Builder baseHp(int hp);
        // ... other setters
        public EntityConfigData build();
    }
}
```

#### 2. Configuration Constants
Add to `LovelyConstant.java`:
```java
// -- Entity Configuration Keys --
public static final String CONFIG_MAX_LEVEL = "maxLevel";
public static final String CONFIG_BASE_HP = "baseHp";
public static final String CONFIG_BASE_ATTACK = "baseAttack";
public static final String CONFIG_ATTACK_SPEED = "attackSpeed";
public static final String CONFIG_BASE_DEFENSE = "baseDefense";
public static final String CONFIG_BASE_TOUGHNESS = "baseToughness";
public static final String CONFIG_MOVEMENT_SPEED = "movementSpeed";

// -- Robot Variant Arrays --
public static final String[] ALL_VARIANTS = {
    VARIANT_BUNNY, VARIANT_BUNNY2, VARIANT_DRAGON,
    VARIANT_HONEY, VARIANT_KITSUNE, VARIANT_NEKO, VARIANT_VANILLA
};
```

#### 3. Dynamic Configuration Loading
Replace individual config variables with:
```java
// In LovelyConfigs
private static final ForgeConfigSpec.ConfigValue<Config> ENTITY_CONFIGS;

// Dynamic loading with validation
private static void loadEntityConfigs() {
    Config entityConfigs = ENTITY_CONFIGS.get();
    LegacyConfigs.Common.Entities.clear();
    
    for (String variant : LovelyConstant.ALL_VARIANTS) {
        EntityConfigData data = loadVariantConfig(entityConfigs, variant);
        LegacyConfigs.Common.Entities.put(variant, data.validateOrDefault());
    }
}
```

#### 4. Multiloader Implementation
- **Forge**: Use ForgeConfigSpec with Config objects
- **Fabric**: Use custom config system with JSON/TOML parsing
- **NeoForge**: Use NeoForge config system (similar to Forge)

### Implementation Phases

#### Phase 1: LovelyLib Foundation
1. Enhance `EntityConfigData` with validation and builder pattern
2. Add configuration constants to `LovelyConstant`
3. Update `LegacyConfigs.Common.Entities` initialization
4. Add default value system with overrides

#### Phase 2: Multiloader Config Integration
1. **Forge**: Replace individual ForgeConfigSpec variables with dynamic Config
2. **Fabric**: Implement equivalent dynamic config loading
3. **NeoForge**: Implement NeoForge-specific config handling
4. Ensure runtime reloading works across all loaders

#### Phase 3: Migration and Validation
1. Add migration logic from old individual configs
2. Implement comprehensive validation with fallbacks
3. Add config versioning for future compatibility
4. Test runtime reloading functionality

#### Phase 4: Cleanup and Documentation
1. Remove deprecated individual config variables
2. Update documentation and examples
3. Add user migration guide
4. Performance testing and optimization

## Consequences

### Positive
- **Reduced Code Duplication**: From 49+ variables to single dynamic system
- **Enhanced Maintainability**: New robot types require minimal code changes
- **Improved User Experience**: Cleaner, more organized configuration files
- **Better Scalability**: System scales naturally with new variants and stats
- **Runtime Flexibility**: Easy to add new stats without code changes
- **Validation Robustness**: Comprehensive error handling with fallbacks

### Negative
- **Migration Complexity**: Requires careful migration from existing configs
- **Initial Development Time**: Significant upfront implementation effort
- **Config File Changes**: Users will need to update their configuration files
- **Testing Overhead**: Must test across three different mod loaders

### Risks
- **Config Corruption**: Invalid configs could break robot functionality
  - *Mitigation*: Comprehensive validation with default fallbacks
- **Performance Impact**: HashMap lookups vs direct variable access
  - *Mitigation*: Minimal impact, configs loaded once at startup
- **Multiloader Inconsistency**: Different behavior across mod loaders
  - *Mitigation*: Shared validation logic in LovelyLib
- **User Confusion**: Changed config structure may confuse existing users
  - *Mitigation*: Clear migration guide and backward compatibility period

## Alternatives Considered

### Alternative 1: Keep Individual Variables
**Rejected**: Doesn't solve scalability or maintenance issues

### Alternative 2: JSON/YAML External Files
**Rejected**: Breaks integration with mod loader config systems, complicates multiloader support

### Alternative 3: Annotation-Based Config
**Rejected**: Too complex for current needs, limited flexibility

### Alternative 4: Registry-Based System
**Considered**: Could be future enhancement, but adds unnecessary complexity for current requirements

## Related Decisions
- Links to existing config architecture decisions
- Future robot variant expansion plans
- Multiloader architecture strategy

## Implementation Notes

### Build Process
1. Modify LovelyLib source code
2. Build LovelyLib JAR
3. Copy and rename JAR to Legacy libs folder
4. Refresh Gradle dependencies
5. Implement Legacy-specific config changes
6. Test across all mod loaders

### Testing Strategy
- Unit tests for EntityConfigData validation
- Integration tests for config loading
- Runtime reload testing
- Multiloader compatibility testing
- Migration testing with existing configs

### Documentation Updates
- Update user configuration guides
- Create migration documentation
- Update developer API documentation
- Add troubleshooting guide for config issues

---

**Key Principle**: This system maintains backward compatibility while providing a foundation for future scalability and maintainability improvements across all robot variants and mod loaders.