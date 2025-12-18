# ADR 008: Hybrid Dynamic Entity Configuration Architecture

**Status**: Proposed  
**Date**: 2025-12-18  
**Decision Makers**: Development Team  
**Consulted**: Configuration System Analysis  

## Context

The current dynamic entity configuration system works perfectly for Fabric but faces architectural constraints with Forge/NeoForge loaders. We need a unified approach that:

1. **Maintains Fabric's flexibility** - Dynamic key generation using `LovelyConstant.ALL_VARIANTS`
2. **Solves Forge/NeoForge constraints** - Pre-declared `ForgeConfigSpec.ConfigValue<T>` requirements
3. **Provides future extensibility** - Custom serializer infrastructure for advanced features
4. **Ensures backward compatibility** - Existing configs continue working

## Problem Statement

### Current State Analysis

**Fabric Implementation (Working)**:
```java
// Dynamic key generation works perfectly
for (String variant : LovelyConstant.ALL_VARIANTS) {
    String key = variant + "-" + LovelyConstant.CONFIG_MAX_LEVEL;
    provider.define(key, getDefaultMaxLevel(variant));
}
```

**Forge/NeoForge Challenge**:
```java
// Requires pre-declared ConfigValue objects
private static final ForgeConfigSpec.ConfigValue<Integer> BUNNY_MAX_LEVEL;
// Cannot create these dynamically at runtime
```

**Architectural Mismatch**:
- Fabric: String-based flexible key access
- Forge/NeoForge: Strongly-typed pre-declared config values
- Need unified approach across all three loaders

## Decision

Implement a **Hybrid Dynamic Entity Configuration Architecture** combining:

1. **Option 2: Dynamic Key Generation** - Generate all possible config keys programmatically
2. **Option 3: Custom Serializer Infrastructure** - Support for complex data structures

This provides immediate solution for Forge/NeoForge while establishing foundation for advanced features.

## Architecture Overview

### Phase 1: Dynamic Key Generation

**Concept**: Generate all possible entity configuration keys during static initialization using `LovelyConstant.ALL_VARIANTS` array.

**Implementation Strategy**:
```java
static {
    // Generate config keys for all variants dynamically
    for (String variant : LovelyConstant.ALL_VARIANTS) {
        for (String configType : CONFIG_TYPES) {
            String key = variant + "-" + configType;
            // Create ForgeConfigSpec.ConfigValue for each combination
        }
    }
}
```

**Benefits**:
- Unified codebase across all loaders
- No hardcoded variant-specific variables
- Automatic support for new robot variants
- Maintains type safety for Forge/NeoForge

### Phase 2: Custom Serializer Infrastructure

**Concept**: Add support for serialized `EntityConfigData` objects alongside individual keys.

**Implementation Strategy**:
```java
// Individual keys (Phase 1)
"bunny-max-level" = 200
"bunny-attack-speed" = 1.6

// Serialized objects (Phase 2)
"bunny-config" = "{maxLevel:200,attackSpeed:1.6,baseHp:16,...}"
```

**Benefits**:
- Future-proof for complex configurations
- JSON support for advanced features
- Plugin system compatibility
- Runtime configuration modification

## Detailed Implementation Plan

### 1. Configuration Key Architecture

#### Key Generation Pattern
```java
public class ConfigKeyGenerator {
    
    // Configuration type constants
    private static final String[] CONFIG_TYPES = {
        LovelyConstant.CONFIG_MAX_LEVEL,
        LovelyConstant.CONFIG_ATTACK_SPEED,
        LovelyConstant.CONFIG_MOVEMENT_SPEED,
        LovelyConstant.CONFIG_BASE_TOUGHNESS,
        LovelyConstant.CONFIG_BASE_HP,
        LovelyConstant.CONFIG_BASE_ATTACK,
        LovelyConstant.CONFIG_BASE_DEFENSE
    };
    
    public static String generateKey(String variant, String configType) {
        return variant + "-" + configType;
    }
    
    public static Map<String, String> generateAllKeys() {
        Map<String, String> keys = new HashMap<>();
        for (String variant : LovelyConstant.ALL_VARIANTS) {
            for (String configType : CONFIG_TYPES) {
                String key = generateKey(variant, configType);
                keys.put(key, key);
            }
        }
        return keys;
    }
}
```

#### Storage Architecture
```java
public class LovelyConfigs {
    
    // Phase 1: Dynamic individual keys
    private static final Map<String, ForgeConfigSpec.ConfigValue<Integer>> ENTITY_INT_CONFIGS = new HashMap<>();
    private static final Map<String, ForgeConfigSpec.ConfigValue<Float>> ENTITY_FLOAT_CONFIGS = new HashMap<>();
    
    // Phase 2: Serialized config support
    private static final Map<String, ForgeConfigSpec.ConfigValue<String>> ENTITY_SERIALIZED_CONFIGS = new HashMap<>();
    
    // Unified access layer
    public static int getEntityIntConfig(String variant, String configType) {
        String key = ConfigKeyGenerator.generateKey(variant, configType);
        return ENTITY_INT_CONFIGS.get(key).get();
    }
    
    public static float getEntityFloatConfig(String variant, String configType) {
        String key = ConfigKeyGenerator.generateKey(variant, configType);
        return ENTITY_FLOAT_CONFIGS.get(key).get();
    }
}
```

### 2. Custom Serializer Infrastructure

#### EntityConfigSerializer Class
```java
public class EntityConfigSerializer {
    
    // Simple format for Phase 2
    public static String serialize(EntityConfigData config) {
        return String.format(
            "{maxLevel:%d,baseHp:%d,baseAttack:%d,attackSpeed:%.2f,baseDefense:%d,baseToughness:%.2f,movementSpeed:%.3f}",
            config.maxLevel, config.baseHp, config.baseAttack, 
            config.attackSpeed, config.baseDefense, config.baseToughness, config.movementSpeed
        );
    }
    
    public static EntityConfigData deserialize(String data) {
        try {
            // Parse simple format
            return parseSimpleFormat(data);
        } catch (Exception e) {
            Legacy.LOGGER.warn("Failed to deserialize config: {}, using defaults", e.getMessage());
            return EntityConfigData.getDefault();
        }
    }
    
    // Future: JSON support
    public static EntityConfigData deserializeJson(String jsonData) {
        // JSON parsing for advanced features
        // Support for nested objects, arrays, custom fields
    }
    
    // Future: Plugin support
    public static EntityConfigData deserializePlugin(String pluginData, String pluginId) {
        // Plugin-specific deserialization
        // Custom configuration formats
    }
}
```

#### Fallback Chain Architecture
```java
public class ConfigAccessLayer {
    
    public static EntityConfigData getEntityConfig(String variant) {
        // Priority chain: Serialized → Individual → Defaults
        
        // 1. Try serialized config (Phase 2)
        String serializedKey = variant + "-config";
        if (ENTITY_SERIALIZED_CONFIGS.containsKey(serializedKey)) {
            String serializedData = ENTITY_SERIALIZED_CONFIGS.get(serializedKey).get();
            if (!serializedData.isEmpty()) {
                EntityConfigData config = EntityConfigSerializer.deserialize(serializedData);
                if (config.isValid()) {
                    return config;
                }
            }
        }
        
        // 2. Try individual keys (Phase 1)
        try {
            return new EntityConfigData.Builder()
                .maxLevel(getEntityIntConfig(variant, LovelyConstant.CONFIG_MAX_LEVEL))
                .attackSpeed(getEntityFloatConfig(variant, LovelyConstant.CONFIG_ATTACK_SPEED))
                .movementSpeed(getEntityFloatConfig(variant, LovelyConstant.CONFIG_MOVEMENT_SPEED))
                .baseToughness(getEntityFloatConfig(variant, LovelyConstant.CONFIG_BASE_TOUGHNESS))
                .baseHp(getEntityIntConfig(variant, LovelyConstant.CONFIG_BASE_HP))
                .baseAttack(getEntityIntConfig(variant, LovelyConstant.CONFIG_BASE_ATTACK))
                .baseDefense(getEntityIntConfig(variant, LovelyConstant.CONFIG_BASE_DEFENSE))
                .build();
        } catch (Exception e) {
            Legacy.LOGGER.warn("Failed to load individual configs for {}, using defaults", variant);
        }
        
        // 3. Fallback to defaults
        return LegacyConfigs.Common.getDefaultConfig(variant);
    }
}
```

### 3. Loader-Specific Implementation

#### Forge/NeoForge Implementation
```java
static {
    BUILDER = new ForgeConfigSpec.Builder();
    
    // ... existing general configs ...
    
    BUILDER.push("Entity");
    
    // Phase 1: Generate individual keys dynamically
    for (String variant : LovelyConstant.ALL_VARIANTS) {
        BUILDER.push(variant);
        
        // Integer configs
        String maxLevelKey = ConfigKeyGenerator.generateKey(variant, LovelyConstant.CONFIG_MAX_LEVEL);
        ENTITY_INT_CONFIGS.put(maxLevelKey, BUILDER
            .comment("Highest level this robot type can reach...")
            .defineInRange(maxLevelKey, getDefaultMaxLevel(variant), 
                          ConfigBounds.MAX_LEVEL_MIN, ConfigBounds.MAX_LEVEL_MAX));
        
        String baseHpKey = ConfigKeyGenerator.generateKey(variant, LovelyConstant.CONFIG_BASE_HP);
        ENTITY_INT_CONFIGS.put(baseHpKey, BUILDER
            .comment("Base HP value for combat level calculations...")
            .defineInRange(baseHpKey, getDefaultBaseHp(variant),
                          ConfigBounds.BASE_HP_MIN, ConfigBounds.BASE_HP_MAX));
        
        // Float configs
        String attackSpeedKey = ConfigKeyGenerator.generateKey(variant, LovelyConstant.CONFIG_ATTACK_SPEED);
        ENTITY_FLOAT_CONFIGS.put(attackSpeedKey, BUILDER
            .comment("How fast this robot type attacks...")
            .defineInRange(attackSpeedKey, getDefaultAttackSpeed(variant),
                          ConfigBounds.ATTACK_SPEED_MIN, ConfigBounds.ATTACK_SPEED_MAX));
        
        // Phase 2: Add serialized config support
        String serializedKey = variant + "-config";
        ENTITY_SERIALIZED_CONFIGS.put(serializedKey, BUILDER
            .comment("Serialized configuration object (advanced users only)")
            .define(serializedKey, ""));
        
        BUILDER.pop();
    }
    
    BUILDER.pop();
    SPEC = BUILDER.build();
}
```

#### Fabric Implementation (Enhanced)
```java
private static void buildConfigProvider(ConfigProvider provider) {
    // ... existing general configs ...
    
    provider.push("Entity");
    
    // Phase 1: Dynamic key generation (existing)
    for (String variant : LovelyConstant.ALL_VARIANTS) {
        provider.push(variant);
        
        provider.comment("Highest level this robot type can reach...")
                .define(ConfigKeyGenerator.generateKey(variant, LovelyConstant.CONFIG_MAX_LEVEL), 
                       getDefaultMaxLevel(variant));
        
        // ... other individual configs ...
        
        // Phase 2: Add serialized config support
        provider.comment("Serialized configuration object (advanced users only)")
                .define(variant + "-config", "");
        
        provider.pop();
    }
    
    provider.pop();
}
```

### 4. Migration Strategy

#### Backward Compatibility
```java
public class ConfigMigration {
    
    public static void migrateFromLegacyConfigs() {
        // Migrate from old individual variables to new dynamic system
        for (String variant : LovelyConstant.ALL_VARIANTS) {
            EntityConfigData legacyConfig = getLegacyConfig(variant);
            
            // Update individual keys
            setEntityConfig(variant, legacyConfig);
            
            // Optionally create serialized version
            String serializedConfig = EntityConfigSerializer.serialize(legacyConfig);
            setSerializedConfig(variant, serializedConfig);
        }
    }
    
    private static EntityConfigData getLegacyConfig(String variant) {
        return switch (variant) {
            case LovelyConstant.VARIANT_BUNNY -> new EntityConfigData.Builder()
                .maxLevel(SharedConfigs.Common.BunnyMaxLevel)
                .attackSpeed(SharedConfigs.Common.BunnyAttackSpeed)
                // ... other legacy values
                .build();
            // ... other variants
            default -> EntityConfigData.getDefault();
        };
    }
}
```

#### Validation and Error Handling
```java
public class ConfigValidator {
    
    public static ValidationResult validateEntityConfig(String variant, EntityConfigData config) {
        List<String> errors = new ArrayList<>();
        
        // Validate ranges
        if (config.maxLevel <= 0) {
            errors.add("maxLevel must be positive");
        }
        if (config.attackSpeed <= 0) {
            errors.add("attackSpeed must be positive");
        }
        // ... other validations
        
        return new ValidationResult(errors.isEmpty(), errors);
    }
    
    public static EntityConfigData sanitizeConfig(EntityConfigData config) {
        // Apply bounds and sanitization
        return new EntityConfigData.Builder()
            .maxLevel(Math.max(1, Math.min(config.maxLevel, ConfigBounds.MAX_LEVEL_MAX)))
            .attackSpeed(Math.max(ConfigBounds.ATTACK_SPEED_MIN, 
                         Math.min(config.attackSpeed, ConfigBounds.ATTACK_SPEED_MAX)))
            // ... other sanitizations
            .build();
    }
}
```

## Implementation Phases

### Phase 1: Dynamic Key Generation (Immediate)
**Objective**: Get Forge/NeoForge working with same flexibility as Fabric

**Tasks**:
1. Create `ConfigKeyGenerator` utility class
2. Implement dynamic key generation in Forge/NeoForge static blocks
3. Update `loadDynamicEntityConfigs()` to use generated keys
4. Test compilation and runtime functionality
5. Verify config file generation and loading

**Success Criteria**:
- Forge/NeoForge configs work identically to Fabric
- All robot variants supported without hardcoded variables
- Config files generate correctly with all variants
- Runtime config loading populates `LegacyConfigs.Common.Entities` HashMap

### Phase 2: Custom Serializer Infrastructure (Future Enhancement)
**Objective**: Add support for complex configuration objects

**Tasks**:
1. Create `EntityConfigSerializer` class
2. Add serialized config entries to all loaders
3. Implement fallback chain (serialized → individual → defaults)
4. Create migration utilities
5. Add validation and error handling

**Success Criteria**:
- Serialized configs work alongside individual keys
- Fallback chain handles all error scenarios gracefully
- Migration from individual to serialized configs works
- Advanced users can use JSON-like configuration format

### Phase 3: Advanced Features (Long-term)
**Objective**: Enable plugin system and runtime modifications

**Tasks**:
1. JSON serialization support
2. Plugin configuration system
3. Runtime config modification APIs
4. Web-based configuration interface
5. Configuration templates and presets

## Benefits

### Immediate Benefits (Phase 1)
- **Unified Codebase**: Same logic across Fabric, Forge, NeoForge
- **Automatic Variant Support**: New robot types work without code changes
- **Maintainability**: Single source of truth for configuration logic
- **Type Safety**: Maintains Forge/NeoForge's type validation

### Future Benefits (Phase 2+)
- **Extensibility**: Support for complex nested configurations
- **Plugin System**: Third-party robot types and configurations
- **Runtime Modification**: Server admins can update configs without restart
- **Advanced Features**: JSON configs, templates, presets

### Technical Benefits
- **Error Resilience**: Multiple fallback layers prevent crashes
- **Validation**: Comprehensive validation and sanitization
- **Migration Support**: Smooth transition from legacy systems
- **Performance**: Efficient config access with caching

## Risks and Mitigations

### Risk: Configuration Complexity
**Mitigation**: 
- Maintain simple individual keys as primary interface
- Serialized configs are optional advanced feature
- Comprehensive documentation and examples

### Risk: Loader-Specific Bugs
**Mitigation**:
- Extensive testing across all three loaders
- Shared validation logic prevents inconsistencies
- Fallback mechanisms handle loader-specific failures

### Risk: Migration Issues
**Mitigation**:
- Backward compatibility maintained indefinitely
- Migration is optional, not required
- Validation prevents corrupted configurations

### Risk: Performance Impact
**Mitigation**:
- Config access is cached after loading
- Serialization only occurs during config save/load
- Minimal runtime overhead

## Alternatives Considered

### Alternative 1: Separate Implementations
**Rejected**: Would create maintenance burden and feature divergence

### Alternative 2: Forge/NeoForge Only Solution
**Rejected**: Would not provide future extensibility benefits

### Alternative 3: Complete Rewrite
**Rejected**: Would break existing configurations and require extensive migration

## Implementation Notes

### Code Organization
```
sources/common/lovelylib-1.21.1/Common/src/main/java/net/heriazone/lovelylib/common/configs/
├── SharedConfigs.java (existing)
├── ConfigKeyGenerator.java (new)
├── EntityConfigSerializer.java (new)
├── ConfigAccessLayer.java (new)
├── ConfigValidator.java (new)
└── ConfigMigration.java (new)
```

### Testing Strategy
1. **Unit Tests**: Each component tested in isolation
2. **Integration Tests**: Cross-loader compatibility testing
3. **Migration Tests**: Legacy config migration scenarios
4. **Performance Tests**: Config loading and access benchmarks
5. **Error Handling Tests**: Invalid configuration scenarios

### Documentation Requirements
1. **User Guide**: How to configure robots using new system
2. **Developer Guide**: How to add new robot variants
3. **Migration Guide**: How to migrate from legacy configs
4. **API Documentation**: Configuration access methods
5. **Troubleshooting Guide**: Common configuration issues

## Conclusion

The Hybrid Dynamic Entity Configuration Architecture provides an elegant solution that:

1. **Solves immediate problem**: Gets Forge/NeoForge working with dynamic configs
2. **Maintains compatibility**: Existing configs continue working
3. **Enables future growth**: Serializer infrastructure supports advanced features
4. **Unifies codebase**: Same logic across all three loaders

This architecture positions the project for long-term success while solving the immediate technical constraints.

---

**Next Steps**: 
1. Review and approve this ADR
2. Create implementation task in active sprint
3. Begin Phase 1 implementation
4. Update CURRENT_STATE.md with new architecture components