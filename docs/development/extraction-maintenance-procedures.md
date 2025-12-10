# Multi-Loader Extraction Maintenance Procedures

**Status**: Active Procedures  
**Last Updated**: 2025-01-10  
**Purpose**: Comprehensive procedures for maintaining extracted code and preventing re-duplication

## Core Maintenance Principles

### 1. Common-First Development Philosophy
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

### 2. GeckoLib Isolation Enforcement (CRITICAL)
**Absolute Rule**: GeckoLib dependencies must NEVER be moved to common

**Automated Validation**:
```bash
# Pre-commit hook
if grep -r "software.bernie.geckolib" Common/src/; then
    echo "ERROR: GeckoLib imports found in common module!"
    exit 1
fi
```

### 3. Dependency Direction Monitoring
**Rule**: Common module must never depend on loader-specific modules
**Validation**: Build system fails on circular dependencies

## Development Workflows

### Feature Development Process

#### 1. Pre-Implementation Analysis
```markdown
## Feature Analysis Checklist
- [ ] Will this feature be identical across all loaders?
- [ ] Does this feature require GeckoLib functionality?
- [ ] Does this feature need platform-specific APIs?
- [ ] What extraction pattern is most appropriate?
- [ ] Are there existing common components to extend?
```

#### 2. Implementation by Pattern

**Direct Migration (100% Identical)**
1. Create component in appropriate common package
2. Implement using only safe dependencies (Minecraft APIs, Java stdlib)
3. Update loader implementations to use common component
4. Remove duplicate code from loaders
5. Test on all three loaders

**Abstract Base Class (80-95% Similar)**
1. Create abstract base class in common with shared behavior
2. Define abstract methods for loader differences
3. Implement concrete classes in each loader
4. Test behavioral consistency across loaders

**Helper Class (Complex Logic Extraction)**
1. Create static helper class in common
2. Extract complex logic to helper methods with clear interfaces
3. Update platform classes to delegate to helpers
4. Maintain platform-specific entry points

**Strategy Interface (Platform Variations)**
1. Define strategy interface in common
2. Implement concrete strategies in each loader
3. Create service locator for strategy access
4. Register implementations during mod initialization

### Code Review Procedures

#### Common Code Review Checklist
```markdown
- [ ] No GeckoLib imports present
- [ ] No loader-specific dependencies
- [ ] Follows project coding style guide
- [ ] Includes comprehensive JavaDoc documentation
- [ ] Has appropriate error handling
- [ ] Uses appropriate extraction pattern
- [ ] Includes extraction rationale in documentation
```

#### Loader Code Review Checklist
```markdown
- [ ] Delegates to common code when possible
- [ ] Minimal duplication with other loaders
- [ ] Platform-specific code is justified
- [ ] GeckoLib code remains in loader
- [ ] Registration code remains in loader
- [ ] Follows thin adapter pattern
```

## Duplication Prevention System

### Automated Detection Tools

#### Pre-Commit Hooks
```bash
#!/bin/bash
# Pre-commit duplication detection

echo "Checking for code duplication..."

# Check for GeckoLib violations
if grep -r "software.bernie.geckolib" Common/src/; then
    echo "ERROR: GeckoLib imports found in common module!"
    exit 1
fi

# Check for similar code patterns
./scripts/detect-duplication.sh --threshold=80 --exclude-patterns="registration,events"

# Check dependency direction
./scripts/check-dependencies.sh --no-common-to-loader

echo "Duplication check passed."
```

#### Continuous Integration
```yaml
name: Duplication Detection
on: [pull_request]

jobs:
  duplication-check:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Run duplication analysis
        run: |
          ./scripts/analyze-similarity.sh --report-format=json
          ./scripts/check-geckolib-isolation.sh
          ./scripts/validate-extraction-patterns.sh
```

### Development Workflow Integration

#### Feature Branch Workflow
```markdown
## Feature Development Checklist

### Pre-Implementation
- [ ] Analyzed similarity across loaders
- [ ] Selected appropriate extraction pattern
- [ ] Verified no GeckoLib boundary violations
- [ ] Documented extraction rationale

### During Implementation  
- [ ] Followed common-first development approach
- [ ] Used appropriate design patterns
- [ ] Added comprehensive documentation
- [ ] Included error handling and validation

### Pre-Merge
- [ ] Tested on all three loaders
- [ ] Verified behavioral consistency
- [ ] Ran duplication detection tools
- [ ] Updated architecture documentation
```

## Error Handling and Rollback

### Graceful Degradation Patterns
```java
public class RobustCommonComponent {
    public Result performOperation(Input input) {
        try {
            return executeOptimizedLogic(input);
        } catch (Exception e) {
            LOGGER.warn("Optimized operation failed, using fallback", e);
            return executeFallbackLogic(input);
        }
    }
    
    private Result executeFallbackLogic(Input input) {
        // Provide safe, simple implementation
        // Log degradation for monitoring
        return createSafeDefault();
    }
}
```

### Rollback Procedures

#### Component-Level Rollback
```bash
#!/bin/bash
# Component rollback script

COMPONENT_NAME=$1
BACKUP_COMMIT=$2

echo "Rolling back component: $COMPONENT_NAME to $BACKUP_COMMIT"

# Create rollback branch
git checkout -b "rollback-$COMPONENT_NAME-$(date +%Y%m%d-%H%M%S)"

# Restore loader implementations
for LOADER in Fabric Forge NeoForge; do
    git show $BACKUP_COMMIT:$LOADER/src/main/java/net/msymbios/llovelyr/$COMPONENT_NAME > \
        $LOADER/src/main/java/net/msymbios/llovelyr/$COMPONENT_NAME
done

# Remove common implementation
rm -rf Common/src/main/java/net/msymbios/llovelyr/lib/$COMPONENT_NAME

git add -A
git commit -m "ROLLBACK: $COMPONENT_NAME to $BACKUP_COMMIT"

echo "Rollback complete. Test compilation before merging."
```

#### Emergency Rollback Protocol
```markdown
## Emergency Response (Critical Issues)

### Immediate (0-30 minutes)
1. Identify critical issue clearly
2. Make immediate rollback decision
3. Execute rollback using prepared scripts
4. Notify team of emergency rollback

### Short-term (30 minutes - 2 hours)
1. Validate rollback success
2. Verify system stability
3. Document issue and rollback
4. Begin root cause analysis

### Medium-term (2-24 hours)
1. Run comprehensive test suite
2. Update all relevant documentation
3. Review and improve procedures
4. Plan prevention measures
```

## Performance Monitoring

### Optimization Guidelines
```java
public class PerformantHelper {
    // Prefer static methods to avoid object creation overhead
    public static Result processData(Input input) {
        return computeResult(input);
    }
    
    // Cache expensive computations
    private static final Map<String, Result> CACHE = new ConcurrentHashMap<>();
    
    public static Result getCachedResult(String key) {
        return CACHE.computeIfAbsent(key, k -> computeExpensiveResult(k));
    }
}
```

### Performance Validation
- **Compilation Time**: Monitor build times for regression detection
- **Runtime Performance**: Benchmark critical paths before/after changes
- **Memory Usage**: Profile memory consumption of common components
- **Startup Time**: Measure mod initialization performance

## Documentation Standards

### Component Documentation Template
```java
/**
 * [Brief description of component purpose and role in system]
 * <p>
 * <b>Architecture:</b> [How this fits in the larger system architecture]
 * <p>
 * <b>Extraction Rationale:</b> [Why this was extracted to common and benefits gained]
 * <p>
 * <b>Pattern Used:</b> [Direct Migration/Abstract Base Class/Helper Class/Strategy Interface]
 * <p>
 * <b>Similarity Score:</b> [Percentage similarity that led to extraction decision]
 * <p>
 * <b>Maintenance Notes:</b> [Special considerations for maintaining this component]
 * <p>
 * <b>GeckoLib Boundary:</b> [What GeckoLib functionality remains in loaders and why]
 */
public class ExtractedComponent {
    // Implementation with clear, documented logic
}
```

### Change Documentation
```markdown
## Change Log Entry Template

### [Date] - [Component Name] - [Change Type]

**Change Description**: Brief description of what changed
**Rationale**: Why the change was necessary
**Pattern Impact**: How this affects the extraction pattern used
**Loader Impact**: Which loaders are affected and how
**Testing**: How the change was validated
**Performance Impact**: Any performance implications
**Breaking Changes**: Any API or behavior changes
**Migration Guide**: How to adapt to changes if needed
**Rollback Procedure**: How to revert if needed
```

## Testing Requirements

### Unit Testing Standards
```java
/**
 * Test class for common components.
 * Must verify behavior without loader-specific dependencies.
 */
class CommonComponentTest {
    @Test
    void testCommonBehavior() {
        // Test common logic with mock data
        // Verify expected behavior
        // Assert no loader-specific dependencies
    }
    
    @Test
    void testErrorHandling() {
        // Test input validation
        // Test error conditions
        // Test graceful degradation
    }
}
```

### Cross-Loader Consistency Testing
```java
/**
 * Integration test verifying consistent behavior across loaders.
 * Run on each loader to ensure identical results.
 */
class CrossLoaderConsistencyTest {
    @Test
    void testIdenticalBehavior() {
        // Setup identical test conditions
        // Execute same operations on each loader
        // Assert identical results across all loaders
    }
}
```

## Team Collaboration Guidelines

### Knowledge Sharing
- **Monthly Architecture Reviews**: Discuss extraction decisions and patterns
- **Quarterly Retrospectives**: Evaluate extraction success and lessons learned
- **Annual Training Updates**: Update guidelines based on project evolution

### Onboarding New Developers
**Essential Training Topics**:
1. Extraction patterns and when to use each
2. GeckoLib boundary and critical importance of isolation
3. Dependency direction rules
4. Testing strategy for cross-loader consistency
5. Documentation standards and extraction rationale

**Hands-On Exercises**:
1. Analyze code samples for appropriate extraction patterns
2. Review code for GeckoLib boundary violations
3. Extract duplicate code using established patterns
4. Write cross-loader consistency tests

### Code Review Process
- **Common Code**: Requires review from at least 2 developers
- **Loader Code**: Requires review from 1 developer familiar with that loader
- **Architecture Changes**: Requires review from lead developer
- **Performance Changes**: Requires performance testing validation

## Maintenance Automation

### Automated Monitoring
```bash
#!/bin/bash
# Weekly maintenance check

echo "Running weekly maintenance checks..."

# Check for new duplication
./scripts/detect-duplication.sh --report-weekly

# Validate GeckoLib isolation
./scripts/check-geckolib-isolation.sh

# Check dependency direction
./scripts/validate-dependencies.sh

# Performance regression check
./scripts/performance-regression-check.sh

echo "Maintenance check complete"
```

### Continuous Improvement
- **Metrics Tracking**: Monitor code reduction percentage, duplication trends
- **Process Refinement**: Regular review and improvement of procedures
- **Tool Enhancement**: Improve automation and detection capabilities
- **Documentation Updates**: Keep procedures current with project evolution

---

**Key Principle**: These maintenance procedures preserve the benefits of code extraction while enabling continued development. They should be followed consistently but can evolve based on project needs and lessons learned. The goal is maintainable, efficient code that respects architectural boundaries while maximizing code reuse.