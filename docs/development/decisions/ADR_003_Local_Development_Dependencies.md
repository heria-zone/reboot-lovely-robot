# ADR 003: Local Development Dependencies for LovelyLib Integration

**Status**: Proposed  
**Date**: 2024-12-12  
**Decision Makers**: Development Team  
**Consulted**: Project Architecture  

## Context

The LovelyLib 1.21.1 multiloader library is under active development and not yet ready for Maven publication. The Tribute 1.21.1 mod needs to reference and use LovelyLib code during development iterations. We need a local dependency system that allows:

1. **Rapid iteration**: Build LovelyLib → Test in Tribute → Iterate
2. **Multiloader support**: Each loader variant (Forge/NeoForge/Fabric) references appropriate dependencies
3. **Common code sharing**: Tribute Common references LovelyLib Common
4. **Development workflow**: Seamless build process without external publishing

## Current Structure

```
sources/
├── common/
│   └── lovelylib-1.21.1/          [Library under development]
│       ├── Common/                [Shared library code]
│       ├── Forge/                 [Forge-specific library]
│       ├── NeoForge/              [NeoForge-specific library]
│       └── Fabric/                [Fabric-specific library]
└── tribute/
    └── tlovelyr-1.21.x/           [Tribute mod - existing]
        ├── Common/                [Shared tribute code]
        ├── Forge/                 [Forge-specific tribute]
        ├── NeoForge/              [NeoForge-specific tribute]
        └── Fabric/                [Fabric-specific tribute]
```

## Decision

Implement a **Local JAR Dependencies** system with the following architecture:

### 1. Build Output Structure
LovelyLib will build JARs to a standardized location:
```
sources/common/lovelylib-1.21.1/build/
├── libs/
│   ├── lovelylib-common-1.21.1-dev.jar
│   ├── lovelylib-forge-1.21.1-dev.jar
│   ├── lovelylib-neoforge-1.21.1-dev.jar
│   └── lovelylib-fabric-1.21.1-dev.jar
```

### 2. Local Dependencies Directory
Tribute will maintain a local dependencies directory:
```
sources/tribute/tlovelyr-1.21.x/
├── libs/
│   ├── lovelylib-common-1.21.1-dev.jar      [Copied from LovelyLib build]
│   ├── lovelylib-forge-1.21.1-dev.jar       [Copied from LovelyLib build]
│   ├── lovelylib-neoforge-1.21.1-dev.jar    [Copied from LovelyLib build]
│   └── lovelylib-fabric-1.21.1-dev.jar      [Copied from LovelyLib build]
```

### 3. Dependency Mapping
Each Tribute loader variant references appropriate dependencies:

- **Tribute Common** → LovelyLib Common JAR
- **Tribute Forge** → LovelyLib Common JAR + LovelyLib Forge JAR
- **Tribute NeoForge** → LovelyLib Common JAR + LovelyLib NeoForge JAR
- **Tribute Fabric** → LovelyLib Common JAR + LovelyLib Fabric JAR

### 4. Build Integration
Implement Gradle tasks for seamless workflow:

#### LovelyLib Tasks
- `buildAllJars`: Build all loader variants
- `copyToTribute`: Copy built JARs to Tribute's libs directory

#### Tribute Tasks
- `refreshDependencies`: Copy latest LovelyLib JARs
- `buildWithDeps`: Refresh dependencies then build

## Implementation Details

### LovelyLib Gradle Configuration

#### Root build.gradle additions:
```gradle
// Task to copy all built JARs to Tribute
task copyToTribute {
    dependsOn ':Common:build', ':Forge:build', ':NeoForge:build', ':Fabric:build'
    
    doLast {
        def tributeLibsDir = file('../../../tribute/tlovelyr-1.21.x/libs')
        tributeLibsDir.mkdirs()
        
        // Copy each variant's JAR
        copy {
            from project(':Common').tasks.jar.archiveFile
            into tributeLibsDir
        }
        copy {
            from project(':Forge').tasks.jar.archiveFile
            into tributeLibsDir
        }
        copy {
            from project(':NeoForge').tasks.jar.archiveFile
            into tributeLibsDir
        }
        copy {
            from project(':Fabric').tasks.jar.archiveFile
            into tributeLibsDir
        }
        
        println "Copied LovelyLib JARs to Tribute libs directory"
    }
}

// Convenience task for full build and copy
task buildAndCopy {
    dependsOn 'build', 'copyToTribute'
}
```

### Tribute Gradle Configuration

#### Root build.gradle additions:
```gradle
// Add flatDir repository for local JARs
allprojects {
    repositories {
        flatDir {
            dirs 'libs'
        }
        // ... other repositories
    }
}

// Task to refresh dependencies from LovelyLib
task refreshDependencies {
    doLast {
        def lovelyLibProject = file('../../common/lovelylib-1.21.1')
        if (lovelyLibProject.exists()) {
            exec {
                workingDir lovelyLibProject
                commandLine './gradlew', 'copyToTribute'
            }
        } else {
            throw new GradleException("LovelyLib project not found at expected location")
        }
    }
}
```

#### Common/build.gradle:
```gradle
dependencies {
    // LovelyLib Common dependency
    implementation name: 'lovelylib-common-1.21.1-dev', version: ''
    
    // ... other dependencies
}
```

#### Forge/build.gradle:
```gradle
dependencies {
    // LovelyLib dependencies
    implementation name: 'lovelylib-common-1.21.1-dev', version: ''
    implementation name: 'lovelylib-forge-1.21.1-dev', version: ''
    
    // ... other dependencies
}
```

#### NeoForge/build.gradle:
```gradle
dependencies {
    // LovelyLib dependencies
    implementation name: 'lovelylib-common-1.21.1-dev', version: ''
    implementation name: 'lovelylib-neoforge-1.21.1-dev', version: ''
    
    // ... other dependencies
}
```

#### Fabric/build.gradle:
```gradle
dependencies {
    // LovelyLib dependencies
    implementation name: 'lovelylib-common-1.21.1-dev', version: ''
    implementation name: 'lovelylib-fabric-1.21.1-dev', version: ''
    
    // ... other dependencies
}
```

## Development Workflow

### Initial Setup
1. Build LovelyLib: `cd sources/common/lovelylib-1.21.1 && ./gradlew buildAndCopy`
2. Verify JARs copied to `sources/tribute/tlovelyr-1.21.x/libs/`
3. Build Tribute: `cd sources/tribute/tlovelyr-1.21.x && ./gradlew build`

### Iteration Cycle
1. **Modify LovelyLib code**
2. **Rebuild and copy**: `./gradlew buildAndCopy` (from LovelyLib directory)
3. **Test in Tribute**: `./gradlew build` (from Tribute directory)
4. **Repeat as needed**

### Alternative Workflow (from Tribute)
1. **Modify LovelyLib code**
2. **Refresh and build**: `./gradlew refreshDependencies build` (from Tribute directory)

## Consequences

### Positive
- **Rapid iteration**: Changes in LovelyLib immediately available to Tribute
- **No external dependencies**: No need for Maven publishing during development
- **Multiloader support**: Each loader gets appropriate dependencies
- **Version control**: JAR files can be gitignored, only source code tracked
- **Build automation**: Single command workflow for updates

### Negative
- **Manual dependency management**: Must remember to rebuild LovelyLib after changes
- **Local file dependencies**: JARs not automatically versioned
- **Build order dependency**: LovelyLib must be built before Tribute
- **Disk space**: Duplicate JARs in libs directory

### Risks
- **Stale dependencies**: Forgetting to rebuild LovelyLib leads to outdated code
- **Path dependencies**: Relative paths may break if project structure changes
- **IDE integration**: IDEs may not automatically detect JAR updates

## Migration Path

### To Maven Publishing
When LovelyLib is ready for publication:

1. **Publish to Maven**: Configure LovelyLib for Maven Central or local repository
2. **Update Tribute dependencies**: Replace flatDir JARs with Maven coordinates
3. **Remove local JARs**: Clean up libs directory
4. **Update build scripts**: Remove copy tasks and local dependency logic

### Version Management
- Use `-dev` suffix for development JARs
- Increment version numbers for significant changes
- Document breaking changes in development notes

## Related Decisions
- **ADR_001_Library_Architecture_Strategy**: Establishes multiloader architecture
- **ADR_002_Multiloader_Dependency_Configuration**: Defines loader-specific configurations

## Validation Criteria

### Success Metrics
- [ ] LovelyLib builds all loader variants successfully
- [ ] JARs copy to Tribute libs directory automatically
- [ ] Tribute can import and use LovelyLib classes
- [ ] Each loader variant uses appropriate dependencies
- [ ] Build process completes without errors
- [ ] IDE recognizes LovelyLib classes in Tribute

### Testing Checklist
- [ ] Build LovelyLib from clean state
- [ ] Verify all 4 JARs created and copied
- [ ] Import LovelyLib class in Tribute Common
- [ ] Import loader-specific class in each Tribute variant
- [ ] Build Tribute successfully with LovelyLib dependencies
- [ ] Test in development environment

---

**Implementation Priority**: High - Required for Tribute development to proceed with LovelyLib integration.

**Next Steps**: 
1. Implement Gradle tasks in LovelyLib
2. Configure Tribute build files for local dependencies
3. Test full workflow with sample integration
4. Document usage in development notes