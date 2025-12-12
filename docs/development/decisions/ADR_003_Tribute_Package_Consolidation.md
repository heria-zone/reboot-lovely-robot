# ADR 003: Tribute Package Consolidation

**Status**: Accepted
**Date**: 2025-01-12
**Decision Makers**: Development Team
**Consulted**: Project Architecture

## Context

The Tribute 1.21.1 multiloader project was using loader-specific package namespaces that were inconsistent with other project components:

**Previous Structure**:
- Common: `net.heriazone.tlovelyr.common.*`
- Fabric: `net.heriazone.tlovelyr.fabric.*`
- Forge: `net.heriazone.tlovelyr.forge.*`
- NeoForge: `net.heriazone.tlovelyr.neoforge.*`

**Inconsistency Issues**:
- Legacy 1.21.1 uses unified structure: `net.msymbios.llovelyr.*`
- LovelyLib 1.21.1 uses unified structure: `net.heriazone.lovelylib.*`
- Unnecessary package hierarchy complexity
- Inconsistent import patterns across project components

## Decision

Consolidate all Tribute 1.21.1 packages into a unified `net.heriazone.tlovelyr.*` namespace across all loader modules.

**New Structure**:
- Common: `net.heriazone.tlovelyr.*`
- Fabric: `net.heriazone.tlovelyr.*`
- Forge: `net.heriazone.tlovelyr.*`
- NeoForge: `net.heriazone.tlovelyr.*`

## Consequences

### Positive
- **Consistency**: Aligns with Legacy and LovelyLib package structures
- **Simplicity**: Reduces package hierarchy complexity
- **Maintainability**: Easier navigation and understanding
- **Import Clarity**: Cleaner import statements without loader-specific sub-packages
- **Developer Experience**: Consistent patterns across all project variants

### Negative
- **Migration Effort**: Required moving files and updating imports
- **Temporary Disruption**: Brief period where builds might fail during transition

### Risks
- **Class Name Conflicts**: Mitigated by the fact that loader modules are separate and never loaded together
- **Configuration Breakage**: Mitigated by thorough testing of all loader configurations

## Implementation

**Phase 1: File Movement**
- Moved `Tribute.java` from `common/` to root package
- Moved `LovelyTribute.java` from `fabric/`, `forge/`, `neoforge/` to root package
- Removed empty loader-specific subdirectories

**Phase 2: Code Updates**
- Updated package declarations in all Java files
- Removed loader-specific import statements
- Updated Fabric mod configuration (`fabric.mod.json`)

**Phase 3: Validation**
- All modules compile successfully
- Full build passes without errors
- No functional regressions detected

## Alternatives Considered

**Alternative 1: Keep Current Structure**
- **Rejected**: Maintains inconsistency with other project components
- **Rejected**: Adds unnecessary complexity without benefits

**Alternative 2: Rename Entry Point Classes**
- **Considered**: `LovelyTributeFabric`, `LovelyTributeForge`, etc.
- **Rejected**: Unnecessary since modules are separate; follows Legacy pattern

## Related Decisions

- Aligns with Legacy 1.21.1 architecture patterns
- Consistent with LovelyLib 1.21.1 structure decisions
- Supports future multiloader development consistency

## Validation Results

- ✅ Common module: Compiles successfully
- ✅ Fabric module: Compiles successfully  
- ✅ Forge module: Compiles successfully
- ✅ NeoForge module: Compiles successfully
- ✅ Full build: Passes without functional errors
- ✅ Configuration files: Updated and validated

## Notes

This decision establishes the pattern for future multiloader projects within the Lovely Robot ecosystem, ensuring consistency across all variants (Tribute, Legacy, Reboot) and supporting libraries (LovelyLib, HZLib).