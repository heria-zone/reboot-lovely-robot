# ADR 001: Library Architecture Strategy

**Status**: Accepted
**Date**: 2025-12-11
**Decision Makers**: Project Team
**Context**: Sprint 06 - Shared Library Architecture Foundation

## Context

The LovelyRobot ecosystem has significant code duplication across variants (Tribute, Legacy, Reboot) and Minecraft versions. To improve maintainability and enable new projects like Monsters & Girls, we need shared libraries that extract common functionality.

## Decision

### Library Structure
Create two shared libraries for Minecraft 1.21.1:

1. **HZ Lib** (`hzlib-1.21.1`)
   - **Purpose**: General utilities for any Minecraft mod
   - **Package**: `net.heriazone.hzlib.*`
   - **Content**: Math utilities, NBT handling, validation, configuration management

2. **Lovely Lib** (`lovelylib-1.21.1`)
   - **Purpose**: Robot-specific shared functionality
   - **Package**: `net.msymbios.lovelylib.*`
   - **Content**: BaseRobotEntity, AI systems, robot items, recipes
   - **Dependency**: Depends on HZ Lib

### Project Structure
```
sources/common/
├── hzlib-1.21.1/             # General utilities library
│   ├── buildSrc/              # Build configuration
│   ├── Common/                # Shared code
│   ├── Fabric/                # Fabric-specific implementations
│   ├── Forge/                 # Forge-specific implementations
│   ├── NeoForge/              # NeoForge-specific implementations
│   └── build.gradle           # Library-focused configuration
├── lovelylib-1.21.1/          # Robot-specific library
│   ├── buildSrc/              # Build configuration
│   ├── Common/                # Shared robot code
│   ├── Fabric/                # Fabric-specific implementations
│   ├── Forge/                 # Forge-specific implementations
│   ├── NeoForge/              # NeoForge-specific implementations
│   └── build.gradle           # Library-focused configuration
├── template-mod-1.21.1/       # Template for future mods
└── template-lib-1.21.1/       # Template for future libraries
```

### Build System Decisions

1. **Gradle Configuration**: Use Legacy 1.21.1's proven multi-loader setup as foundation
2. **Library Focus**: Remove unnecessary mod-specific configurations, keep useful metadata
3. **Publishing Setup**: Include Maven publishing configuration (inactive initially)
4. **Local Dependencies**: Start with local dependencies, migrate to Maven later

### Testing Strategy

1. **Unit Tests**: Each loader module has its own test suite
2. **Test Structure**: Mirror source structure in test directories
3. **Independence**: Tests run independently per loader
4. **Validation**: Integration tests verify cross-loader compatibility

### Versioning Strategy

1. **Independent Versioning**: HZ Lib and Lovely Lib version independently
2. **Semantic Versioning**: Follow semver (major.minor.patch)
3. **Starting Version**: Both libraries start at 1.0.0
4. **Version Coordination**: Document compatibility matrices between libraries

### Implementation Strategy

1. **Iterative Extraction**: Extract code from Legacy → Lovely Lib → HZ Lib
2. **API Discovery**: Let APIs emerge from real extraction needs, not upfront design
3. **Validation Loop**: Build Monsters & Girls using HZ Lib to validate design
4. **Template Creation**: Create reusable templates after libraries stabilize

### Dependency Chain
```
HZ Lib 1.0.0 (foundation utilities)
    ↓
Lovely Lib 1.0.0 (robot-specific, depends on HZ Lib)
    ↓
Robot Mods (Legacy, Tribute, Reboot - depend on Lovely Lib)

Monsters & Girls (depends on HZ Lib directly)
```

## Consequences

### Positive
- **Reduced Duplication**: ~50% code reduction across robot variants
- **Improved Maintainability**: Single source of truth for common functionality
- **Faster Development**: New projects can leverage existing libraries
- **Quality Improvement**: Shared code gets more testing and refinement
- **Ecosystem Growth**: Enables new projects like Monsters & Girls

### Negative
- **Initial Complexity**: More complex build setup during transition
- **Abstraction Overhead**: Some performance cost from abstraction layers
- **Dependency Management**: Need to coordinate library versions
- **Learning Curve**: Team needs to understand multi-project development

### Risks
- **Over-Engineering**: Risk of creating overly complex abstractions
- **Breaking Changes**: Library changes could break dependent mods
- **Version Conflicts**: Dependency version mismatches

## Alternatives Considered

1. **Single Shared Library**: Rejected - too monolithic, harder to maintain
2. **No Shared Libraries**: Rejected - continues current duplication problems
3. **External Dependencies**: Rejected - want control over robot-specific functionality
4. **Maven-First Approach**: Rejected - want to validate locally first

## Implementation Plan

### Phase 1: Foundation (Sprint 06)
- Examine Legacy 1.21.1 structure
- Create HZ Lib and Lovely Lib environments
- Establish build configurations
- Create templates

### Phase 2: Extraction (Sprint 07-09)
- Extract robot code from Legacy to Lovely Lib
- Extract general utilities from Lovely Lib to HZ Lib
- Update Legacy to use Lovely Lib

### Phase 3: Validation (Sprint 10+)
- Build Monsters & Girls using HZ Lib
- Refine libraries based on real usage
- Publish to Maven when stable

## Related Decisions
- ADR_002_Lovely_Lib_Design_Decisions.md (planned)
- ADR_003_HZ_Lib_Scope_Definition.md (planned)

---

**Key Principle**: Start simple, extract incrementally, validate through real usage, then generalize and publish.