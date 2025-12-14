# ADR 003: HZ Lib Scope Definition

**Status**: Accepted  
**Date**: 2025-12-13  
**Decision Makers**: Solo Developer  
**Consulted**: Legacy 1.21.1 Codebase Analysis, Monsters & Girls Requirements  
**Related**: [ADR_001_Library_Architecture_Strategy.md](ADR_001_Library_Architecture_Strategy.md), [ADR_002_Lovely_Lib_Design_Decisions.md](ADR_002_Lovely_Lib_Design_Decisions.md)

## Context

Following the Lovely Lib First approach, we need to define HZ Lib's scope after extracting robot-specific code to Lovely Lib. HZ Lib should contain general-purpose utilities that can benefit multiple mods, including the planned Monsters & Girls mod and potentially other future projects. We need to determine what utilities from the Legacy codebase are general enough for HZ Lib and what additional utilities should be created.

## Decision

### HZ Lib Scope Definition

**HZ Lib will contain general-purpose utilities that:**
1. **Support multiple mod projects** (not robot-specific)
2. **Provide common Minecraft modding patterns** (NBT, math, validation, etc.)
3. **Abstract platform differences** (multi-loader support utilities)
4. **Offer reusable infrastructure** (configuration, networking, data management)

### Package Structure for HZ Lib

```
net.heriazone.hzlib/
├── math/
│   ├── MathUtils.java               [Mathematical operations and calculations]
│   ├── GeometryUtils.java           [Distance, angles, geometric calculations]
│   ├── RandomUtils.java             [Enhanced random generation utilities]
│   └── InterpolationUtils.java      [Lerp, smoothing, animation math]
├── nbt/
│   ├── NbtProcessingUtils.java      [NBT reading, writing, validation]
│   ├── NbtMigrationUtils.java       [Data version migration utilities]
│   ├── NbtCompressionUtils.java     [NBT compression and optimization]
│   └── NbtValidationUtils.java      [NBT structure validation]
├── validation/
│   ├── ValidationUtils.java         [General data validation]
│   ├── RangeValidator.java          [Numeric range validation]
│   ├── StringValidator.java         [String format and content validation]
│   └── ItemStackValidator.java      [ItemStack validation utilities]
├── config/
│   ├── ConfigManager.java           [Configuration management system]
│   ├── ConfigValidator.java         [Configuration validation]
│   ├── ConfigMigration.java         [Config version migration]
│   └── ConfigDefaults.java          [Default value management]
├── platform/
│   ├── PlatformUtils.java           [Platform detection and utilities]
│   ├── LoaderServices.java          [Multi-loader service abstraction]
│   └── VersionUtils.java            [Version comparison and compatibility]
├── data/
│   ├── DataManager.java             [Generic data persistence]
│   ├── SavedDataUtils.java          [World saved data utilities]
│   ├── PlayerDataManager.java       [Per-player data management]
│   └── DataMigrationUtils.java      [Data structure migration]
├── networking/
│   ├── PacketUtils.java             [Network packet utilities]
│   ├── SyncManager.java             [Client-server synchronization]
│   └── NetworkConstants.java        [Common networking constants]
├── text/
│   ├── StringUtils.java             [String manipulation and formatting]
│   ├── TextComponentUtils.java      [Minecraft text component utilities]
│   ├── LocalizationUtils.java       [I18n and localization support]
│   └── ChatUtils.java               [Chat formatting and messaging]
├── inventory/
│   ├── InventoryUtils.java          [Inventory manipulation utilities]
│   ├── ItemStackUtils.java          [ItemStack creation and modification]
│   ├── SlotUtils.java               [Slot management utilities]
│   └── ContainerUtils.java          [Container and GUI utilities]
├── world/
│   ├── WorldUtils.java              [World interaction utilities]
│   ├── BlockUtils.java              [Block placement and interaction]
│   ├── EntityUtils.java             [General entity utilities]
│   └── ChunkUtils.java              [Chunk loading and management]
└── debug/
    ├── DebugUtils.java              [Development and debugging utilities]
    ├── LoggingUtils.java            [Enhanced logging capabilities]
    ├── PerformanceProfiler.java     [Performance monitoring]
    └── TestingUtils.java            [Unit testing support utilities]
```

### What Goes Into HZ Lib

#### From Legacy `lib/utils/` Package
**Direct Migration:**
- `MathUtils.java` → `hzlib/math/MathUtils.java`
- `NbtProcessingUtils.java` → `hzlib/nbt/NbtProcessingUtils.java`
- `ValidationUtils.java` → `hzlib/validation/ValidationUtils.java`
- `StringUtils.java` → `hzlib/text/StringUtils.java`
- `PlatformUtils.java` → `hzlib/platform/PlatformUtils.java`

**Enhanced and Expanded:**
- `ItemSpawnHelper.java` → Split into `hzlib/inventory/ItemStackUtils.java` and `hzlib/world/EntityUtils.java`

#### From Legacy `common/utils/` Package
**Selective Migration:**
- `Utility.java` → Extract general utilities to appropriate HZ Lib packages
- `EnchantmentProtectionCalculator.java` → Stay in Legacy (robot-specific)

#### From Legacy `framework/utils/` Package
**Selective Migration:**
- `ObjectUtil.java` → Extract general object utilities to `hzlib/validation/`
- `Version.java` → Enhanced version to `hzlib/platform/VersionUtils.java`

#### New Utilities for Multi-Mod Support

**Configuration System:**
```java
// Unified configuration management
public class ConfigManager {
    public static <T> T loadConfig(String modId, Class<T> configClass);
    public static void saveConfig(String modId, Object config);
    public static void validateConfig(Object config);
    public static void migrateConfig(String modId, int fromVersion, int toVersion);
}
```

**Data Persistence:**
```java
// Generic data management
public class DataManager {
    public static <T> T loadPlayerData(Player player, String key, Class<T> dataClass);
    public static void savePlayerData(Player player, String key, Object data);
    public static <T> T loadWorldData(Level level, String key, Class<T> dataClass);
    public static void saveWorldData(Level level, String key, Object data);
}
```

**Networking Utilities:**
```java
// Multi-loader networking abstraction
public class PacketUtils {
    public static void sendToPlayer(Player player, Object packet);
    public static void sendToAllPlayers(Level level, Object packet);
    public static void sendToServer(Object packet);
}
```

### What Stays in Lovely Lib

#### Robot-Specific Utilities
- **Animation utilities** - GeckoLib integration, robot-specific animations
- **Robot registry** - Robot ownership, spawn management
- **Robot conversion** - Cross-variant conversion system
- **Robot rendering** - Color layers, emissive effects, robot-specific rendering
- **Robot recipes** - NBT transfer recipes, robot crafting

#### Robot-Specific Services
- **Robot platform services** - Robot entity registration, robot item registration
- **Robot data management** - Robot-specific NBT handling, robot save data

### What Stays in Legacy

#### Legacy-Specific Code
- **Robot implementations** - 7 robot types, Legacy-specific behavior
- **Legacy commands** - Admin commands, robot management
- **Legacy configuration** - Legacy-specific config options
- **Legacy identifiers** - Resource locations, NBT keys
- **Protection system** - Enchanted book feeding, adaptive protection

### Multi-Mod Integration Strategy

#### Monsters & Girls Integration
**Shared Utilities:**
- `MathUtils` - Damage calculations, stat progression
- `ValidationUtils` - Data validation for monster/girl entities
- `NbtProcessingUtils` - Save data management
- `ConfigManager` - Unified configuration system
- `DataManager` - Player progression data, relationship data

**M&G-Specific Extensions:**
```java
// Example: Relationship system utilities
public class RelationshipUtils extends ValidationUtils {
    public static boolean isValidRelationshipLevel(int level);
    public static float calculateRelationshipBonus(int level);
    public static void updateRelationshipData(Player player, Entity entity, int change);
}
```

#### Future Mod Support
**Extensible Design:**
- Plugin-style architecture for mod-specific extensions
- Common interfaces for entity management, data persistence
- Shared configuration and networking patterns
- Reusable GUI and inventory management utilities

### Platform Abstraction Strategy

#### Multi-Loader Services
```java
// Platform service interface
public interface IPlatformServices {
    // Registration
    void registerItem(String name, Item item);
    void registerBlock(String name, Block block);
    void registerEntityType(String name, EntityType<?> entityType);
    
    // Configuration
    Path getConfigDirectory();
    boolean isModLoaded(String modId);
    String getModVersion(String modId);
    
    // Networking
    void sendPacketToPlayer(Player player, Object packet);
    void sendPacketToServer(Object packet);
    
    // Data
    CompoundTag getPlayerPersistentData(Player player);
    CompoundTag getWorldPersistentData(Level level);
}
```

#### Loader-Specific Implementations
- **Fabric**: FabricLoader integration, Fabric API usage
- **Forge**: ForgeRegistry usage, Forge event system
- **NeoForge**: NeoForge-specific APIs and patterns

### API Design Principles

#### 1. Simplicity
- Clean, intuitive APIs that are easy to use
- Minimal boilerplate code required
- Clear method names and documentation

#### 2. Consistency
- Consistent naming conventions across all utilities
- Uniform error handling and validation patterns
- Standardized configuration and data management

#### 3. Performance
- Efficient implementations with minimal overhead
- Lazy loading where appropriate
- Caching for frequently accessed data

#### 4. Extensibility
- Plugin-style architecture for mod-specific extensions
- Interface-based design for easy customization
- Event-driven patterns where appropriate

### Utility Categories

#### Core Utilities (High Priority)
**Math & Calculations:**
- Basic math operations (clamp, lerp, random)
- Geometric calculations (distance, angles)
- Statistical functions (average, median, distribution)

**Data Management:**
- NBT processing and validation
- Configuration management
- Data persistence and migration

**Validation & Safety:**
- Input validation and sanitization
- Range checking and bounds validation
- Error handling and recovery

#### Extended Utilities (Medium Priority)
**Platform Integration:**
- Multi-loader abstraction
- Version compatibility checking
- Mod integration utilities

**Networking & Sync:**
- Packet utilities and abstractions
- Client-server synchronization
- Data consistency management

**Text & Localization:**
- String manipulation and formatting
- Localization and internationalization
- Chat and messaging utilities

#### Advanced Utilities (Low Priority)
**Development Tools:**
- Debug utilities and logging
- Performance profiling
- Testing and validation tools

**World Interaction:**
- Block and entity utilities
- Chunk management
- World generation helpers

## Consequences

### Positive
- **Code Reuse**: Multiple mods can share common utilities
- **Consistency**: Standardized patterns across all HZ projects
- **Maintenance**: Bug fixes benefit all dependent mods
- **Development Speed**: Faster development with proven utilities
- **Quality**: Well-tested, robust utility functions

### Negative
- **Dependency Management**: All mods depend on HZ Lib versions
- **API Stability**: Changes to HZ Lib affect multiple projects
- **Complexity**: Additional abstraction layer
- **Version Coordination**: Updates must be coordinated across projects

### Risks
- **Breaking Changes**: HZ Lib updates could break dependent mods
- **Over-Engineering**: Risk of creating overly complex abstractions
- **Performance**: Additional layers could impact performance
- **Maintenance Burden**: Library maintenance affects multiple projects

## Alternatives Considered

### Alternative 1: No Shared Library
**Rejected**: Would result in significant code duplication across mods and inconsistent implementations.

### Alternative 2: Mod-Specific Utilities Only
**Rejected**: Misses opportunity for code reuse and standardization across projects.

### Alternative 3: Single Monolithic Library
**Rejected**: Would create unnecessary dependencies and bloat for mods that only need specific utilities.

### Alternative 4: External Library Dependency
**Rejected**: Adds external dependency management complexity and reduces control over API evolution.

## Implementation Plan

### Phase 1: Core Utilities (Sprint 09)
1. Extract and enhance math utilities from Legacy
2. Extract and expand NBT processing utilities
3. Extract and generalize validation utilities
4. Create basic configuration management system

### Phase 2: Platform Abstraction (Sprint 09)
1. Create platform service interfaces
2. Implement loader-specific service providers
3. Create version and compatibility utilities
4. Test multi-loader functionality

### Phase 3: Extended Utilities (Post-Phase 1)
1. Add networking and synchronization utilities
2. Create data persistence and migration systems
3. Add text and localization utilities
4. Implement inventory and world utilities

### Phase 4: Advanced Features (Future)
1. Add development and debugging tools
2. Create performance profiling utilities
3. Add testing and validation frameworks
4. Implement advanced world interaction utilities

## Success Criteria

- [ ] All Legacy utilities successfully extracted to appropriate libraries
- [ ] HZ Lib compiles and runs on all supported loaders
- [ ] Lovely Lib successfully depends on HZ Lib
- [ ] No regression in Legacy functionality after extraction
- [ ] API is well-documented and easy to use
- [ ] Performance impact is minimal
- [ ] Monsters & Girls can successfully use HZ Lib utilities

## Future Considerations

### Monsters & Girls Integration
- Relationship system utilities
- Entity interaction patterns
- Advanced AI and behavior systems
- Custom data management for complex entities

### Additional Mod Support
- Plugin architecture for mod-specific extensions
- Common interfaces for entity management
- Shared GUI and inventory patterns
- Reusable networking and synchronization

### Long-Term Evolution
- Semantic versioning for API stability
- Deprecation and migration strategies
- Community contribution guidelines
- Documentation and example maintenance

---

**Key Principle**: HZ Lib provides general-purpose utilities that benefit multiple mods while maintaining simplicity and performance. It serves as the foundation for the entire HZ mod ecosystem.