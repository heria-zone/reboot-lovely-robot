# Comprehensive Multi-Loader Extraction Guide

**Status**: Active Reference  
**Last Updated**: December 11, 2025  
**Purpose**: Complete guide for multi-loader code extraction, architecture, documentation, and maintenance

---

## Executive Summary

This document provides comprehensive guidance for extracting duplicate code from loader-specific modules to common codebase while maintaining GeckoLib isolation and architectural integrity. It covers architectural patterns, maintenance procedures, and documentation standards.

---

## Core Architectural Principles

### 1. Separation of Concerns
**Business Logic vs Platform Integration**

```
┌─────────────────────────────────────────────┐
│         Common Module (Business Logic)      │
│  Algorithms, Data Models, Validation        │
└─────────────────┬───────────────────────────┘
                  │
         ┌────────┴────────┐
         │                 │
┌────────▼────────┐  ┌────▼─────────────┐
│  Fabric Layer   │  │   Forge Layer    │
│  Registration   │  │   Registration   │
│  Event Hooks    │  │   Event Hooks    │
│  Platform APIs  │  │   Platform APIs  │
└─────────────────┘  └──────────────────┘
```

### 2. GeckoLib Isolation Boundary (CRITICAL)
**Absolute Rule**: GeckoLib dependencies must NEVER be in common module

**Automated Validation**:
```bash
# Pre-commit hook
if grep -r "software.bernie.geckolib" Common/src/; then
    echo "ERROR: GeckoLib imports found in common module!"
    exit 1
fi
```

### 3. Common-First Development Philosophy
**Rule**: Always consider implementing new functionality in common first

**Decision Framework**:
```
New Feature Analysis:
├─ Identical across loaders? → Implement in Common
├─ 80-95% similar? → Abstract Base Class in Common
├─ 50-80% similar? → Helper Class in Common  
├─ Varies by platform? → Strategy Interface in Common
└─ Loader-specific? → Implement in each loader
```

---

## Extraction Patterns

### 1. Direct Migration
**Use Case**: 100% identical code across loaders
**Implementation**: Move entire class/method to common module
**Example**: Utility classes, data models, algorithms

### 2. Abstract Base Class
**Use Case**: 80-95% similar code with loader-specific implementations
**Implementation**: Common abstract class with platform-specific overrides
**Example**: Entity base classes, configuration handlers

### 3. Helper Class
**Use Case**: Complex logic extracted from platform classes
**Implementation**: Static utility methods in common module
**Example**: Calculation helpers, validation utilities

### 4. Strategy Interface
**Use Case**: Platform services with service locator pattern
**Implementation**: Interface in common, implementations in loaders
**Example**: Registry services, event handlers

### 5. Codec Helper
**Use Case**: Serialization logic consolidation
**Implementation**: Common serialization/deserialization utilities
**Example**: NBT handling, network packet processing

---

## Project Scope and Constraints

### Target Specifications
- **Target Version**: Minecraft 1.21.1 Legacy variant (`sources/legacy/llovelyr-1.21.1/`)
- **Loaders Affected**: Fabric, Forge, NeoForge
- **Extraction Goal**: 30-40% reduction in duplicate code
- **Critical Constraint**: All GeckoLib dependencies must remain in loader-specific modules

### Extraction Boundaries
**Safe to Extract**:
- Business logic algorithms
- Data models and DTOs
- Validation logic
- Utility functions
- Configuration data structures

**Never Extract**:
- GeckoLib-dependent code
- Loader-specific registration
- Platform-specific event handling
- Mod loading hooks
- Loader-specific APIs

---

## Maintenance Procedures

### 1. Pre-Development Analysis
Before implementing any new feature:

1. **Analyze Similarity**: Determine if code will be identical across loaders
2. **Check Dependencies**: Ensure no GeckoLib or loader-specific dependencies
3. **Plan Architecture**: Design for common implementation if possible
4. **Document Decision**: Record rationale in ADR if significant

### 2. Code Review Checklist
For all changes involving common module:

- [ ] No GeckoLib imports in common code
- [ ] No loader-specific API usage in common code
- [ ] Proper abstraction boundaries maintained
- [ ] Documentation updated for extracted components
- [ ] Tests cover common functionality

### 3. Automated Validation
**Pre-commit Hooks**:
```bash
# Check for GeckoLib in common
if grep -r "software.bernie.geckolib" Common/src/; then
    echo "ERROR: GeckoLib imports found in common module!"
    exit 1
fi

# Check for loader-specific imports in common
if grep -r "net.fabricmc\|net.minecraftforge\|net.neoforged" Common/src/; then
    echo "ERROR: Loader-specific imports found in common module!"
    exit 1
fi
```

### 4. Duplication Detection
**Regular Audits**:
- Monthly code duplication analysis
- Automated detection of similar code blocks
- Review of new loader-specific implementations
- Identification of extraction opportunities

---

## Documentation Standards

### 1. Extraction Documentation
For each extracted component, document:

- **Original Location**: Where code was extracted from
- **Extraction Rationale**: Why it was moved to common
- **Dependencies**: What the extracted code depends on
- **Usage**: How loader-specific code should use it
- **Maintenance Notes**: Special considerations for future changes

### 2. Architecture Decision Records
Create ADRs for:
- Major extraction decisions
- Architecture pattern choices
- Boundary definition changes
- Tool and process updates

### 3. Component Catalog
Maintain comprehensive catalog of:
- All extracted components
- Their purposes and responsibilities
- Interdependencies
- Loader-specific integration points

---

## Implementation Guidelines

### 1. Extraction Process
1. **Identify Candidate**: Find duplicate or similar code
2. **Analyze Dependencies**: Check for extraction blockers
3. **Design Common Interface**: Plan abstraction if needed
4. **Implement in Common**: Create common implementation
5. **Update Loaders**: Modify loader-specific code to use common
6. **Test Thoroughly**: Ensure functionality preserved
7. **Document Changes**: Update all relevant documentation

### 2. Testing Strategy
- **Unit Tests**: Test common functionality in isolation
- **Integration Tests**: Test loader-specific integration
- **Regression Tests**: Ensure existing functionality preserved
- **Cross-Loader Tests**: Verify consistent behavior across loaders

### 3. Migration Strategy
- **Gradual Migration**: Extract components incrementally
- **Backward Compatibility**: Maintain compatibility during transition
- **Feature Flags**: Use flags to control extraction rollout
- **Rollback Plan**: Maintain ability to revert changes

---

## Quality Assurance

### 1. Code Quality Metrics
- **Duplication Percentage**: Target 30-40% reduction
- **Test Coverage**: Maintain >80% coverage for common code
- **Complexity Metrics**: Monitor cyclomatic complexity
- **Dependency Analysis**: Track common module dependencies

### 2. Performance Monitoring
- **Build Time**: Monitor impact on compilation time
- **Runtime Performance**: Ensure no performance regression
- **Memory Usage**: Track memory footprint changes
- **Startup Time**: Monitor mod loading performance

### 3. Maintenance Overhead
- **Documentation Currency**: Keep documentation up-to-date
- **Test Maintenance**: Update tests as code evolves
- **Dependency Management**: Monitor and update dependencies
- **Refactoring Debt**: Address technical debt regularly

---

## Tools and Automation

### 1. Detection Tools
- **Code Duplication Scanners**: Automated duplicate detection
- **Dependency Analyzers**: Track module dependencies
- **Import Validators**: Prevent invalid imports in common
- **Similarity Analyzers**: Identify extraction candidates

### 2. Build Integration
- **Gradle Tasks**: Custom tasks for validation
- **CI/CD Pipelines**: Automated quality checks
- **Pre-commit Hooks**: Prevent invalid commits
- **Documentation Generation**: Auto-generate component docs

### 3. Monitoring Tools
- **Metrics Collection**: Track extraction progress
- **Performance Monitoring**: Monitor impact on performance
- **Quality Dashboards**: Visualize code quality metrics
- **Dependency Graphs**: Visualize module relationships

---

## Best Practices

### 1. Design Principles
- **Single Responsibility**: Each common component has one purpose
- **Loose Coupling**: Minimize dependencies between components
- **High Cohesion**: Related functionality grouped together
- **Interface Segregation**: Small, focused interfaces

### 2. Implementation Guidelines
- **Fail Fast**: Validate inputs early and clearly
- **Immutable Data**: Prefer immutable data structures
- **Error Handling**: Consistent error handling patterns
- **Logging**: Comprehensive logging for debugging

### 3. Maintenance Practices
- **Regular Reviews**: Periodic architecture reviews
- **Refactoring**: Continuous improvement of extracted code
- **Documentation**: Keep documentation current and accurate
- **Testing**: Maintain comprehensive test coverage

---

## Troubleshooting

### Common Issues
1. **GeckoLib Leakage**: Accidentally importing GeckoLib in common
2. **Loader Coupling**: Tight coupling between loaders and common
3. **Performance Regression**: Extraction causing performance issues
4. **Test Failures**: Tests breaking after extraction

### Solutions
1. **Automated Validation**: Use pre-commit hooks to prevent issues
2. **Interface Design**: Use proper abstractions to reduce coupling
3. **Performance Testing**: Monitor performance during extraction
4. **Test Strategy**: Update tests to match new architecture

---

## Success Metrics

### Quantitative Metrics
- **Code Duplication Reduction**: 30-40% target
- **Build Time Impact**: <10% increase acceptable
- **Test Coverage**: >80% for common code
- **Bug Reduction**: Fewer loader-specific bugs

### Qualitative Metrics
- **Developer Experience**: Easier to add new features
- **Code Maintainability**: Cleaner, more organized codebase
- **Architecture Clarity**: Clear separation of concerns
- **Documentation Quality**: Comprehensive and current docs

---

**Next Review Date**: January 15, 2026  
**Maintenance Schedule**: Monthly duplication audits  
**Quality Gate**: All extractions must pass automated validation