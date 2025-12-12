# ADR 001: Local Library Dependencies for Development

**Status**: Accepted
**Date**: 2025-12-12
**Decision Makers**: Development Team
**Consulted**: Project Architecture

## Context

We need to create the "Lovely Tribute" mod (`tlovelyr`) for Minecraft 1.21.1 and establish a dependency system with LovelyLib. The challenge is setting up local library dependencies for development before publishing to repositories.

## Decision

We will implement a local JAR-based dependency system for development:

### 1. Create Lovely Tribute Mod
- Use `template-mod-1.21.1` as the base
- Transform to `tlovelyr-1.21.1` (Tribute variant)
- Document all necessary changes for template transformation

### 2. Create LovelyLib Library
- Use `template-lib-1.21.1` as the base
- Transform to `lovelylib-1.21.1`
- Build JAR files for Common and each loader (Fabric/Forge/NeoForge)

### 3. Local Dependency System
- Create `libs/` folder structure for local JAR dependencies
- Build LovelyLib JARs and place in `libs/` folder
- Configure Tribute mod to use local JAR dependencies
- Test dependency integration with library calls

### 4. Validation Process
- Ensure Tribute mod builds successfully
- Verify `runClient` works with library dependency
- Test library functionality through mod calls
- Document the complete dependency setup process

## Implementation Plan

```
Phase 1: Create Tribute Mod
├── Copy template-mod-1.21.1 → sources/tribute/tlovelyr-1.21.1/
├── Update all identifiers (template → tlovelyr)
├── Update package structure (net.heriazone.template → net.msymbios.tlovelyr)
├── Test build and runClient
└── Document transformation process

Phase 2: Create LovelyLib
├── Copy template-lib-1.21.1 → sources/common/lovelylib-1.21.1/
├── Update all identifiers (templatelib → lovelylib)
├── Update package structure (net.heriazone.templatelib → net.msymbios.lovelylib)
├── Add basic robot-related functionality
└── Build JAR files

Phase 3: Local Dependency Setup
├── Create libs/ folder structure
├── Copy built LovelyLib JARs to libs/
├── Configure Tribute mod dependencies
├── Add library call in Tribute mod
└── Test complete integration

Phase 4: Validation & Documentation
├── Verify build process
├── Test runClient with dependencies
├── Document complete process
└── Create reusable dependency patterns
```

## Consequences

### Positive
- **Local Development**: Can develop and test without external repositories
- **Rapid Iteration**: Quick feedback loop for library changes
- **Dependency Validation**: Ensures library integration works correctly
- **Reusable Pattern**: Establishes pattern for other mod variants
- **Documentation**: Complete process documentation for future reference

### Negative
- **Manual Process**: Requires manual JAR building and copying
- **Version Management**: Need to manage local JAR versions carefully
- **Build Complexity**: Additional build steps for dependency management

### Risks
- **Dependency Drift**: Local JARs may get out of sync
- **Build Failures**: Dependency issues may cause build failures
- **Documentation Lag**: Process changes may not be documented immediately

## Alternatives Considered

1. **Maven Local Repository**: More complex setup, overkill for development
2. **Git Submodules**: Adds complexity to repository management
3. **Direct Source Dependencies**: Would require complex build configuration

## Related Decisions

This decision establishes the foundation for:
- Future mod variant creation (Legacy, Reboot)
- Library development workflow
- Multi-loader dependency management
- Local development environment setup

## Implementation Notes

- Use `flatDir` repositories in Gradle for local JAR dependencies
- Maintain consistent naming conventions across all components
- Document every transformation step for template reuse
- Test on all three loaders (Fabric/Forge/NeoForge)