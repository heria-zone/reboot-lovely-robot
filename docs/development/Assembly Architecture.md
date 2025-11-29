---
created: 2025-04-13 20:46
tags:
  - LovelyRobot
---
# Architecture.md  
Modular Cross-Loader Architecture for Model and Texture Assembly – Final Plan (Minecraft 1.20.1+)

---

## Overview  
This architecture defines a cross-loader, highly modular assembly system for Minecraft mods, enabling dynamic entity models and textures. The design uses a **folder-discovered configuration system** to facilitate team collaboration, extensibility, and simplified maintenance while maintaining loader-agnostic compatibility.

---

## Core Principles  

- **Loader-Agnostic Core:** Core logic operates independently of loader-specific APIs
- **Dynamic Model Discovery:** Per-model JSON configurations automatically discovered from a standardized folder structure
- **GeckoLib Integration with Migration Path:** Current implementation leverages GeckoLib with clear abstraction layers for future replacement
- **Interface-Driven Design:** Extensive use of interfaces and adapters for swappable implementations
- **Data-Driven Assembly:** Models and textures assembled via discoverable, composable components
- **Separation of Concerns:** Clear division between configuration, model logic, rendering, and loader integration
- **Third-Party Extensibility:** Architecture supports extension via data packs and addon mods
- **Runtime External Content Management:** Support for fetching, updating, and hot-loading content without rebuilds
- **Versioned Content Handling:** Manifest-based system for tracking and updating default resources

---

## Package Layout  

```  
com.example.modid
│
├── api/              # Public core interfaces for modular assembly
├── core/             # Assembly logic, managers, model & texture composition engine  
│    ├── assembly/    # Assembly implementation classes
│    ├── model/       # Model data structures and handlers
│    ├── texture/     # Texture processing and composition
│    └── animation/   # Animation and Animset handling 
├── config/           # Data-driven configuration system, folder scanner, JSON parser
├── content/          # Content fetching, versioning and external content management
│    ├── fetch/       # Content fetcher interfaces and implementations
│    ├── manifest/    # Manifest handling and version tracking
│    └── update/      # Content update management
├── rendering/        # Renderer abstraction, adapters for GeckoLib and future rendering systems
├── loader/           # Loader-specific bridging code
│    ├── forge/       # Forge-specific implementation
│    ├── fabric/      # Fabric-specific implementation
│    └── common/      # Shared loader utilities
└── utils/            # Helpers, image processing, resource management
```

---

## External Content Structure

Located under `.minecraft/modprofiles/{modid}/`, organized as follows:

```
manifest.json            # Master manifest listing all official defaults, by type
geometries/
   <geometry_id>/
      geometry.json      # GeckoLib format geometry definition
      metadata.json      # Additional geometry metadata
textures/
   <texture_id>/
      texture_1.png      # Design variant 1
      texture_2.png      # Design variant 2
      metadata.json      # Texture metadata with compatibility info
animations/
   <category>/
      anim_x.animation.json   # GeckoLib animation bundle file
      anim_y.animation.json   # Another animation bundle
      metadata.json           # Animation metadata
animsets/
   dragon_wings.json          # Animation set definitions
   knight_armor.json          # Another animation set
```

---

## Key Interfaces & Classes  

### 1. Core Assembly Abstractions

#### `IAssembly<T extends IPart>`
```java
public interface IAssembly<T extends IPart> {
    void addPart(T part);
    void removePart(String id);
    List<T> getParts();
    T getPart(String id);
    void assemble();
    void disassemble();
    AssemblyStatus getStatus();
}
```
- Generic interface for both model and texture assemblies
- Manages collections of composable parts

---

#### IPart
```java
public interface IPart {
    String getId();
    Set<AnchorPoint> getAnchorPoints();
    List<String> getTags();
}
```
- Base interface for all model and texture parts
- Provides consistent identification and connection points

---

#### IConstraint
```java
public interface IConstraint {
    boolean isSatisfied(IAssembly<?> assembly);
    List<String> getRequiredAnchors();
}
```
- Manages dependencies between parts that require specific anchors
- Validates assembly constraints before merging

---

### 2. Manifest and Content Management

#### Manifest
```java
public class Manifest {
    private int specVersion;
    private Map<ContentType, List<ContentReference>> defaults;
    
    public int getSpecVersion() {
        return specVersion;
    }
    
    public List<ContentReference> getDefaults(ContentType type) {
        return defaults.getOrDefault(type, Collections.emptyList());
    }
    
    public boolean isCompatible(int minimumSupportedVersion) {
        return specVersion >= minimumSupportedVersion;
    }
}
```
- Represents the master manifest that lists all official default resources
- Contains versioning information for spec compatibility
- Groups content references by type (geometries, textures, animations, animsets)

---

#### ContentReference
```java
public class ContentReference {
    private String identifier;
    private String version;
    
    public String getIdentifier() {
        return identifier;
    }
    
    public String getVersion() {
        return version;
    }
}
```
- Represents a reference to a specific content item in the manifest
- Contains identifier and version information

---

#### IContentFetcher
```java
public interface IContentFetcher {
    Manifest fetchManifest();
    InputStream fetchFile(String filePath);
    boolean isAvailable();
}
```
- Abstracts the content fetching mechanism
- Can be implemented for different sources (GitHub, server, etc.)
- Provides methods to fetch the manifest and individual files

---

#### ContentUpdateManager
```java
public class ContentUpdateManager {
    private final List<IContentFetcher> sources;
    private final Path profileFolder;
    
    public ContentUpdateManager(Path profileFolder, List<IContentFetcher> sources) {
        this.profileFolder = profileFolder;
        this.sources = sources;
    }
    
    public void updateContent() {
        // Fetch manifest from first available source
        // Compare with local manifest
        // Download or update missing or outdated files
    }
    
    public boolean validateContent(Manifest manifest) {
        // Check if all required content is available
        // Verify version compatibility
        return true;
    }
}
```
- Manages the content update process
- Uses content fetchers to retrieve content
- Compares versions and updates as needed

---

### 3. Modular Dynamic Data System

#### ModelPartDefinition
```java
public class ModelPartDefinition {
    private String id;                      // Unique identifier
    private String resource;                // Model geometry file path (GeckoLib JSON)
    private List<String> textures;          // Associated textures
    private Set<String> anchors;            // Anchors this part exposes
    private String requiresAnchor;          // If overlay, anchor it depends on
    private String type;                    // "base" or "overlay"
    private ModelConstraint constraint;     // Constraints/dependencies
    private List<String> tags;              // Classification tags for filtering
    private boolean isDefault;              // Flag for default inclusion
    private float priority;                 // Priority for overlays (sorting)
    private String version;                 // Version of this part definition
    
    // Getters, setters, builders...
}
```
- Parsed from individual JSON discovery files
- Represents a single model part configuration

---

#### ModelPartDataLoader
```java
public class ModelPartDataLoader implements ResourceReloadListener {
    private final Map<String, ModelPartDefinition> allParts = new HashMap<>();
    private final ContentUpdateManager contentManager;
    
    public ModelPartDataLoader(ContentUpdateManager contentManager) {
        this.contentManager = contentManager;
    }
    
    @Override
    public void reload(ResourceManager resourceManager) {
        allParts.clear();
        
        // Update external content first
        contentManager.updateContent();
        
        // Load from profile folder (highest priority)
        loadFromProfileFolder();
        
        // Load from data packs (medium priority)
        loadFromDataPacks(resourceManager);
        
        // Load official content (lowest priority, only if not overridden)
        loadOfficialContent();
    }
    
    private void loadFromProfileFolder() {
        // Scan profile folder for model part definitions
        // Parse each JSON into ModelPartDefinition
        // Add to allParts map
    }
    
    private void loadFromDataPacks(ResourceManager resourceManager) {
        // Scan data packs for model part definitions
        // Parse each JSON into ModelPartDefinition
        // Add to allParts map, overriding official content if needed
    }
    
    private void loadOfficialContent() {
        // Load official content from manifest for parts not already loaded
        // Parse each JSON into ModelPartDefinition
        // Add to allParts map only if not already present
    }

    public List<ModelPartDefinition> getBaseParts() {
        return allParts.values().stream()
            .filter(part -> "base".equals(part.getType()))
            .collect(Collectors.toList());
    }
    
    public List<ModelPartDefinition> getOverlays() {
        return allParts.values().stream()
            .filter(part -> "overlay".equals(part.getType()))
            .collect(Collectors.toList());
    }
    
    public Optional<ModelPartDefinition> getPart(String id) {
        return Optional.ofNullable(allParts.get(id));
    }
    
    public List<ModelPartDefinition> getPartsByTag(String tag) {
        return allParts.values().stream()
            .filter(part -> part.getTags().contains(tag))
            .collect(Collectors.toList());
    }
}
```
- Scans and indexes all model part definitions at startup and resource reload
- Respects priority order: profile folder > data packs > official content
- Provides querying capabilities by type, id, and tags
- Integrates with Minecraft's resource reload system to support data packs

---

### 4. Animation and Animset Management

#### AnimationDefinition
```java
public class AnimationDefinition {
    private String id;
    private String resource;
    private Map<String, String> animationKeys;
    private String version;
    private List<String> tags;
    
    // Getters, setters, builders...
}
```
- Represents a GeckoLib animation bundle
- Contains references to individual animation keys within the bundle

---

#### Animset
```java
public class Animset {
    private String id;
    private String version;
    private int specVersion;
    private String defaultState;
    private Map<String, List<String>> states;
    
    public String getId() {
        return id;
    }
    
    public String getDefaultState() {
        return defaultState;
    }
    
    public List<String> getAnimationsForState(String state) {
        return states.getOrDefault(state, Collections.emptyList());
    }
}
```
- Represents a collection of animations grouped by logical states
- Animations are referenced using `file#key` syntax

---

#### AnimsetManager
```java
public class AnimsetManager {
    private final Map<String, Animset> animsets = new HashMap<>();
    private final ContentUpdateManager contentManager;
    
    public AnimsetManager(ContentUpdateManager contentManager) {
        this.contentManager = contentManager;
    }
    
    public void reload() {
        animsets.clear();
        
        // Update external content first
        contentManager.updateContent();
        
        // Load from profile folder (highest priority)
        loadFromProfileFolder();
        
        // Load from data packs (medium priority)
        loadFromDataPacks();
        
        // Load official content (lowest priority, only if not overridden)
        loadOfficialContent();
    }
    
    public Optional<Animset> getAnimset(String id) {
        return Optional.ofNullable(animsets.get(id));
    }
    
    public void registerAnimset(Animset animset) {
        animsets.put(animset.getId(), animset);
    }
}
```
- Manages animation sets
- Loads from profile folder, data packs, and official content
- Respects priority order

---

### 5. Model Assembly Implementation

#### ModelAssembly
```java
public class ModelAssembly implements IAssembly<ModelPart> {
    private final Map<String, ModelPart> parts = new LinkedHashMap<>();
    private final List<IConstraint> constraints = new ArrayList<>();
    private final ISkeletonHandler skeletonHandler;
    private final AnimsetManager animsetManager;
    private AssemblyStatus status = AssemblyStatus.UNASSEMBLED;
    
    public ModelAssembly(ISkeletonHandler skeletonHandler, AnimsetManager animsetManager) {
        this.skeletonHandler = skeletonHandler;
        this.animsetManager = animsetManager;
    }
    
    @Override
    public void addPart(ModelPart part) {
        // Add validation and constraint checking
        parts.put(part.getId(), part);
    }
    
    @Override
    public void assemble() {
        // Validate all constraints are satisfied
        // Create and initialize the base model
        // Merge all parts in order using the skeleton handler
        // Rebuild model hierarchy and update animations
        
        // Collect Animsets from all parts
        List<String> animsetIds = parts.values().stream()
            .flatMap(part -> part.getAnimsetIds().stream())
            .collect(Collectors.toList());
        
        // Merge Animsets and apply to the assembled model
        mergeAndApplyAnimsets(animsetIds);
        
        status = AssemblyStatus.ASSEMBLED;
    }
    
    private void mergeAndApplyAnimsets(List<String> animsetIds) {
        Map<String, List<String>> mergedStates = new HashMap<>();
        String defaultState = null;
        
        // Process each Animset
        for (String animsetId : animsetIds) {
            animsetManager.getAnimSet(animsetId).ifPresent(animset -> {
                // Use the first encountered default state if none set yet
                if (defaultState == null) {
                    defaultState = animset.getDefaultState();
                }
                
                // Merge animation states
                for (Map.Entry<String, List<String>> entry : animset.getStates().entrySet()) {
                    String state = entry.getKey();
                    List<String> animations = entry.getValue();
                    
                    mergedStates.computeIfAbsent(state, k -> new ArrayList<>())
                        .addAll(animations);
                }
            });
        }
        
        // Apply merged Animset to the skeleton handler
        skeletonHandler.updateAnimationSet(mergedStates, defaultState);
    }
    
    // Other interface implementations...
}
```
- Coordinates model composition through the skeleton handler
- Manages model part collection and assembly state
- Enforces constraints during assembly
- Merges Animsets from all parts

---

#### ISkeletonHandler
```java
public interface ISkeletonHandler {
    void initializeFromBase(ModelPart basePart);
    void attachBone(String anchorPoint, IBone bone, ITransformation transformation);
    void replaceBone(String boneId, IBone newBone);
    void updateAnimationSet(Map<String, List<String>> animationStates, String defaultState);
    Object getRenderableModel();  // Returns GeckoLib model now, custom model later
}
```
- Abstraction for model skeleton manipulation
- Hides GeckoLib dependency from core logic
- Supports animation state management

---

#### GeckoLibSkeletonHandler
```java
public class GeckoLibSkeletonHandler implements ISkeletonHandler {
    private GeoModel<?> geoModel;
    private final AnimationResolver animationResolver;
    
    public GeckoLibSkeletonHandler(AnimationResolver animationResolver) {
        this.animationResolver = animationResolver;
    }
    
    @Override
    public void initializeFromBase(ModelPart basePart) {
        // Extract GeckoLib model from the base part
        // Initialize as root model
    }
    
    @Override
    public void attachBone(String anchorPoint, IBone bone, ITransformation transformation) {
        // Find anchor in GeckoLib skeleton
        // Convert IBone to GeoBone
        // Attach with transformation applied
    }
    
    @Override
    public void updateAnimationSet(Map<String, List<String>> animationStates, String defaultState) {
        // For each state, resolve the animation references
        Map<String, List<AnimationDefinition>> resolvedAnimations = new HashMap<>();
        
        for (Map.Entry<String, List<String>> entry : animationStates.entrySet()) {
            String state = entry.getKey();
            List<String> animRefs = entry.getValue();
            
            List<AnimationDefinition> resolved = animRefs.stream()
                .map(animationResolver::resolveAnimation)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
            
            resolvedAnimations.put(state, resolved);
        }
        
        // Apply to GeckoLib model
        // Configure animation controller with states and transitions
    }
    
    // Other implementations...
    
    @Override
    public Object getRenderableModel() {
        return geoModel;  // Return GeckoLib model
    }
}
```
- Concrete implementation using GeckoLib
- Translates assembly operations to GeckoLib-specific API calls
- Resolves animation references and applies them to the model
- Will be replaceable with a custom implementation in the future

---

#### AnimationResolver
```java
public class AnimationResolver {
    private final Map<String, AnimationDefinition> animations;
    
    public AnimationResolver(Map<String, AnimationDefinition> animations) {
        this.animations = animations;
    }
    
    public Optional<AnimationDefinition> resolveAnimation(String reference) {
        // Parse reference in format "file#key"
        String[] parts = reference.split("#", 2);
        if (parts.length != 2) {
            return Optional.empty();
        }
        
        String file = parts[0];
        String key = parts[1];
        
        // Find the animation definition
        AnimationDefinition animDef = animations.get(file);
        if (animDef == null) {
            return Optional.empty();
        }
        
        // Make sure the key exists in this animation bundle
        if (!animDef.getAnimationKeys().containsKey(key)) {
            return Optional.empty();
        }
        
        return Optional.of(animDef);
    }
}
```
- Resolves animation references in the format "file#key"
- Retrieves the appropriate animation definition

---

#### ModelPart
```java
public class ModelPart implements IPart {
    private final String id;
    private final Set<AnchorPoint> anchorPoints;
    private final List<String> tags;
    private final Map<String, IBone> bones;
    private final List<IMesh> meshes;
    private final ModelConstraint constraint;
    private final List<ResourceLocation> textures;
    private final List<String> animsetIds;
    
    // Constructor, getters, etc.
    
    public IBone getBone(String name) {
        return bones.get(name);
    }
    
    public List<IMesh> getMeshes() {
        return Collections.unmodifiableList(meshes);
    }
    
    public List<String> getAnimsetIds() {
        return Collections.unmodifiableList(animsetIds);
    }
}
```
- Represents a single component of a model assembly
- Contains bones, meshes, textures, and metadata
- Includes references to animation sets
- Abstracted from specific rendering implementation (GeckoLib)

---

### 6. Texture System

#### TextureAssembly
```java
public class TextureAssembly implements IAssembly<TexturePart> {
    private final Map<String, TexturePart> parts = new LinkedHashMap<>();
    private final ITextureComposer textureComposer;
    private BufferedImage combinedTexture;
    private AssemblyStatus status = AssemblyStatus.UNASSEMBLED;
    
    public TextureAssembly(ITextureComposer textureComposer) {
        this.textureComposer = textureComposer;
    }
    
    @Override
    public void assemble() {
        // Initialize base texture
        // Layer and composite texture parts in order
        // Apply tinting and effects
        // Store the result
        status = AssemblyStatus.ASSEMBLED;
    }
    
    public BufferedImage getComposedTexture() {
        return combinedTexture;
    }
    
    // Other interface implementations...
}
```
- Manages texture layers and composition
- Uses a texture composer for actual image manipulation

---

#### TextureDesignManager
```java
public class TextureDesignManager {
    private final Map<String, List<TextureDesign>> designsByPart = new HashMap<>();
    private final ContentUpdateManager contentManager;
    
    public TextureDesignManager(ContentUpdateManager contentManager) {
        this.contentManager = contentManager;
    }
    
    public void reload() {
        designsByPart.clear();
        
        // Update external content first
        contentManager.updateContent();
        
        // Load from profile folder (highest priority)
        loadFromProfileFolder();
        
        // Load from data packs (medium priority)
        loadFromDataPacks();
        
        // Load official content (lowest priority, only if not overridden)
        loadOfficialContent();
    }
    
    public List<TextureDesign> getDesignsForPart(String partId) {
        return designsByPart.getOrDefault(partId, Collections.emptyList());
    }
    
    public List<TextureDesign> getDesignsByTags(List<String> tags) {
        return designsByPart.values().stream()
            .flatMap(Collection::stream)
            .filter(design -> design.getTags().containsAll(tags))
            .collect(Collectors.toList());
    }
}
```
- Manages texture designs
- Loads from profile folder, data packs, and official content
- Provides methods to find designs by part or tags

---

#### TextureDesign
```java
public class TextureDesign {
    private String id;
    private String version;
    private List<String> compatibleParts;
    private String type;
    private List<String> tags;
    private List<String> files;
    
    // Constructor, getters, etc.
    
    public boolean isCompatibleWith(String partId) {
        return compatibleParts.contains(partId);
    }
    
    public List<String> getFiles() {
        return Collections.unmodifiableList(files);
    }
}
```
- Represents a texture design with potentially multiple variants
- Contains compatibility information
- Loaded from metadata.json in texture folder

---

#### TexturePart
```java
public class TexturePart implements IPart {
    private final String id;
    private final Set<AnchorPoint> anchorPoints;
    private final List<String> tags;
    private final TextureDesign design;
    private final Map<String, BufferedImage> loadedDesigns = new HashMap<>();
    private String currentDesign;
    
    // Constructor, getters, methods to select design, etc.
    
    public BufferedImage getCurrentDesign() {
        return loadedDesigns.get(currentDesign);
    }
}
```
- Represents a texture component with potentially multiple variants
- Manages texture resources and selection

---

### 7. Cross-Loader Abstraction

#### LoaderPlatform
```java
public interface LoaderPlatform {
    void registerModelDataLoader();
    void registerTextureDataLoader();
    void registerAnimationDataLoader();
    ResourceLocation resolveResource(String path);
    void enqueueTask(Runnable task);
    Optional<Object> getLoaderSpecificHandle();
    IRenderAdapter<?> createRenderAdapter(String type);
    Path getProfileFolder();
}
```
- Abstracts loader-specific APIs and lifecycles
- Enables loader-agnostic core logic
- Provides access to the profile folder

---

#### ForgeLoaderPlatform
```java
public class ForgeLoaderPlatform implements LoaderPlatform {
    @Override
    public void registerModelDataLoader() {
        // Register with Forge's resource reload system
    }
    
    @Override
    public IRenderAdapter<?> createRenderAdapter(String type) {
        if ("model".equals(type)) {
            return new ForgeGeckoLibRenderAdapter();
        } else if ("texture".equals(type)) {
            return new ForgeTextureRenderAdapter();
        }
        throw new IllegalArgumentException("Unknown adapter type: " + type);
    }
    
    @Override
    public Path getProfileFolder() {
        // Return the path to the mod profile folder
        return Paths.get(FMLPaths.GAMEDIR.get().toString(), "modprofiles", MOD_ID);
    }
    
    // Other implementations...
}
```
- Implements loader-specific functionality for Forge
- Creates appropriate render adapters for the Forge environment
- Provides access to the profile folder

---

#### FabricLoaderPlatform
```java
public class FabricLoaderPlatform implements LoaderPlatform {
    @Override
    public void registerModelDataLoader() {
        // Register with Fabric's resource reload system
    }
    
    @Override
    public IRenderAdapter<?> createRenderAdapter(String type) {
        if ("model".equals(type)) {
            return new FabricGeckoLibRenderAdapter();
        } else if ("texture".equals(type)) {
            return new FabricTextureRenderAdapter();
        }
        throw new IllegalArgumentException("Unknown adapter type: " + type);
    }
    
    @Override
    public Path getProfileFolder() {
        // Return the path to the mod profile folder
        return FabricLoader.getInstance().getGameDir().resolve("modprofiles").resolve(MOD_ID);
    }
    
    // Other implementations...
}
```
- Implements loader-specific functionality for Fabric
- Creates appropriate render adapters for the Fabric environment
- Provides access to the profile folder

---

### 8. Assembly Manager and Orchestration

#### `AssemblyManager<T extends IPart>`
```java
public class AssemblyManager<T extends IPart> {
    private final Map<String, IAssembly<T>> assemblies = new ConcurrentHashMap<>();
    private final Function<String, IAssembly<T>> assemblyFactory;
    
    public AssemblyManager(Function<String, IAssembly<T>> assemblyFactory) {
        this.assemblyFactory = assemblyFactory;
    }
    
    public IAssembly<T> createAssembly(String id) {
        IAssembly<T> assembly = assemblyFactory.apply(id);
        assemblies.put(id, assembly);
        return assembly;
    }
    
    public Optional<IAssembly<T>> getAssembly(String id) {
        return Optional.ofNullable(assemblies.get(id));
    }
    
    public void removeAssembly(String id) {
        IAssembly<T> assembly = assemblies.remove(id);
        if (assembly != null) {
            assembly.disassemble();
        }
    }
    
    public void assembleAll() {
        assemblies.values().forEach(IAssembly::assemble);
    }
    
    public void disassembleAll() {
        assemblies.values().forEach(IAssembly::disassemble);
    }
}
```
- Manages collections of assemblies (model or texture)
- Factory pattern for creating typed assemblies
- Thread-safe for concurrent access

---

### 9. Rendering Abstraction

#### `IRenderAdapter<T extends IAssembly<? extends IPart>>`
```java
public interface IRenderAdapter<T extends IAssembly<? extends IPart>> {
    void render(T assembly, MatrixStack stack, float partialTicks);
    void refreshRenderData(T assembly);
}
```
- Abstracts rendering operations
- Implemented specifically for each loader and renderer type

---

## JSON Configuration Formats

### Master Manifest (manifest.json)
```json
{
  "specVersion": 2,
  "defaults": {
    "geometries": [
      { "identifier": "base_humanoid", "version": "1.0.2" },
      { "identifier": "base_arachnid", "version": "1.0.1" }
    ],
    "textures": [
      { "identifier": "samurai_mask_red", "version": "1.0.2" }
    ],
    "animations": [
      { "identifier": "common_actions", "version": "1.1.0" }
    ],
    "animsets": [
      { "identifier": "dragon_wings", "version": "1.0.0" },
      { "identifier": "knight_armor", "version": "1.0.0" }
    ]
  }
}
```

### Base Model Definition (geometry metadata.json)
```json
{
  "identifier": "base_humanoid",
  "version": "1.0.2",
  "type": "base",
  "anchors": ["torso", "head", "left_leg", "right_leg"],
  "textures": ["base_humanoid"],
  "tags": ["humanoid", "biped"],
  "animsets": ["human_basic"]
}
```

### Model Overlay Definition (geometry metadata.json)
```json
{
  "identifier": "knight_overlay",
  "version": "1.0.0",
  "type": "overlay",
  "requiresAnchor": "torso",
  "textures": ["knight_armor"],
  "constraint": {
    "dependsOnAnchors": ["torso", "head"]
  },
  "tags": ["armor", "knight"],
  "priority": 10,
  "animsets": ["knight_armor"]
}
```

### Texture Definition (textures metadata.json)
```json
{
  "identifier": "samurai_mask_red",
  "version": "1.0.2",
  "compatibleParts": ["head", "facemask"],
  "type": "mask",
  "tags": ["red", "samurai"],
  "files": [
    "samurai_mask_red.png"
  ]
}
```

### Animation Set Definition (animsets .json)
```json
{
  "identifier": "dragon_wings",
  "version": "1.0.0",
  "specVersion": 2,
  "defaultState": "idle",
  "states": {
    "idle": ["common_actions#idle_1", "common_actions#idle_2"],
    "fly": ["dragon_wings#fly_slow", "dragon_wings#fly_fast"],
    "attack": ["common_actions#wing_attack"]
  }
}
```

---

## Setting Up the Development Environment

### Multi-Loader Project Structure

#### Option 1: Multi-Module Gradle Project (Recommended)
```
MyMod/
├── build.gradle             # Root project build file
├── settings.gradle          # Defines subprojects
├── core/                    # Core module (loader-agnostic)
│   ├── build.gradle
│   └── src/main/java/...
├── forge/                   # Forge module
│   ├── build.gradle
│   └── src/main/java/...
└── fabric/                  # Fabric module
    ├── build.gradle
    └── src/main/java/...
```

#### Option 2: Single Project with Source Sets (Difficult)
```
MyMod/
├── build.gradle             # Configures source sets
├── src/
│   ├── main/                # Common/core code
│   │   ├── java/...
│   │   └── resources/...
│   ├── forge/               # Forge-specific code
│   │   ├── java/...
│   │   └── resources/...
│   └── fabric/              # Fabric-specific code
│       ├── java/...
│       └── resources/...
```
Notes: With my current knowledge option 1 is viable, later in the future as my knowledge & experience grows I can migrate to option 2.

### Environment Setup Steps

1. **Configure Gradle build:**
   - Define dependencies (MC, GeckoLib, loader APIs)
   - Set up source set configurations
   - Configure platform-specific artifacts

2. **Initialize loader detection:**
   ```java
   public class ModInitializer {
       private static LoaderPlatform PLATFORM;
       
       public static void init() {
           // Detect loader and initialize appropriate platform
           if (isForgeEnvironment()) {
               PLATFORM = new ForgeLoaderPlatform();
           } else if (isFabricEnvironment()) {
               PLATFORM = new FabricLoaderPlatform();
           } else {
               throw new RuntimeException("Unsupported mod loader");
           }
           
           // Initialize core systems
           PLATFORM.registerModelDataLoader();
           PLATFORM.registerTextureDataLoader();
       }
       
       public static LoaderPlatform getPlatform() {
           return PLATFORM;
       }
   }
   ```

3. **Create loader-specific entry points:**
   - `ForgeInitializer extends ModLoader` for Forge
   - `FabricInitializer implements ModInitializer` for Fabric
   - Each calls the common `ModInitializer.init()`

---

## GeckoLib Integration with Migration Path

### Current Implementation
- Uses GeckoLib's model format, animation system, and rendering capabilities
- Adapters translate between your abstractions and GeckoLib classes:
  - `IBone` ↔ `GeoBone`
  - `IAnimation` ↔ `GeckoAnimation`
  - `ISkeletonHandler` → manipulates GeckoLib models

### Future Migration Strategy
1. **Develop custom renderer** alongside GeckoLib
2. **Implement same interfaces** with different backing classes
3. **Provide toggle** to switch implementations (config or build flag)
4. **Phase out GeckoLib dependency** once custom renderer is mature

---

## Texture Processing System

Uses these technologies for texture manipulation:
- **Java's BufferedImage/Graphics2D** for basic texture composition
- **Minecraft's NativeImage** utilities for runtime texture processing
- Abstracted behind **ITextureComposer interface** for flexibility
- Support for tinting, masking, layering, and basic animations

---

## Key Relationships and Information Flow

1. **Discovery Process**
   - ModelPartDataLoader scans configured folders
   - Parses JSONs into ModelPartDefinition objects
   - Indexes by ID, type, tags for quick lookups

2. **Assembly Creation**
   - Game code requests an assembly from AssemblyManager
   - Selects parts from PartDataLoader based on entity type/configuration
   - Validates constraints before constructing

3. **Model Assembly**
   - ModelAssembly coordinates through ISkeletonHandler
   - Handler performs actual GeckoLib operations
   - Result is a complete, renderable model

4. **Rendering**
   - IRenderAdapter bridges assembled models to loader-specific rendering
   - Adapters handle Forge/Fabric specifics

---

## Benefits of This Architecture

- **Team-friendly:** Multiple team members can add models independently
- **Data pack friendly:** Server owners can extend or override models
- **Migration ready:** Abstractions allow replacing GeckoLib later
- **Cross-loader:** Works on both Forge and Fabric with minimal duplication
- **Extendable:** Third-party mods can add components without depending on core code
- **Maintainable:** Small, focused JSON files rather than massive configs
- **Performance aware:** Caching and efficient data structures

---

## Future Directions

- Custom model format and renderer to replace GeckoLib dependency
- Visual editor for assembling models from discovered parts
- Runtime model assembly customization via in-game UI
- Advanced texture effects (shaders, procedural generation)
- Animation composition and blending system
- Extended support for more loaders (Quilt, etc.)
- Optimization for large model counts