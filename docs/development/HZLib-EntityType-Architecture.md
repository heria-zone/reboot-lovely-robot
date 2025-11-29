---
created: 2025-11-29
tags:
  - HZLib
  - Architecture
  - EntityType
  - Framework
---

# HZLib Entity Type System Architecture
## Modular, Extensible Entity Type Framework

## Executive Summary

This document defines a modular entity type system that can be shared across multiple mods (LovelyRobot, Monsters & Girls, etc.) while allowing each mod to extend with custom mechanics.

**Core Principle:** Composition over inheritance with optional feature modules.

## Problem Analysis

### Current Implementations Comparison

**LovelyRobot EntityType Features:**
- ✅ Textures (16-color palette)
- ✅ Models (Default, Armed)
- ✅ Animators
- ✅ Combat stats (health, attack, armor, etc.)
- ✅ Max level system
- ❌ No food/tempting items
- ❌ No sounds
- ❌ No animations (uses GeckoLib directly)
- ❌ No spawn configuration

**Monsters & Girls EntityType Features:**
- ✅ Textures (multiple variants)
- ✅ Models
- ✅ Animators
- ✅ Combat stats
- ✅ Sounds (default, hurt, death, attack)
- ✅ Animations (idle, walk, attack, rest, etc.)
- ✅ Food items
- ✅ Tempting items
- ✅ Spawn configuration (weight, group size, biomes)
- ✅ EntityNative grouping
- ❌ No level system

### Key Differences

| Feature | LovelyRobot | Monsters & Girls | Reusability |
|---------|-------------|------------------|-------------|
| Textures | Required | Required | ⭐⭐⭐⭐⭐ Core |
| Models | Required | Required | ⭐⭐⭐⭐⭐ Core |
| Animators | Required | Required | ⭐⭐⭐⭐⭐ Core |
| Combat Stats | Required | Required | ⭐⭐⭐⭐⭐ Core |
| Level System | Required | Not used | ⭐⭐⭐ Optional |
| Food/Tempting | Not used | Required | ⭐⭐⭐⭐ Optional |
| Sounds | Not used | Required | ⭐⭐⭐⭐ Optional |
| Animations | Not used | Required | ⭐⭐⭐⭐ Optional |
| Spawn Config | Not used | Required | ⭐⭐⭐⭐ Optional |

## Proposed Architecture

### Layer 1: Framework (Pure Java - Core)

**Location:** `framework/entity/type/`

**Purpose:** Pure data structures with zero Minecraft dependencies

```java
// framework/entity/type/EntityTypeData.java
public class EntityTypeData {
    private final String key;
    private final String translationKey;
    
    // Core stats (every entity has these)
    private float maxHealth;
    private float attackDamage;
    private float attackSpeed;
    private float armor;
    private float armorToughness;
    private float knockbackResistance;
    private float moveSpeed;
    
    // Getters/setters
}

// framework/entity/type/ResourceMap.java
public class ResourceMap<K, V> {
    private final Map<K, V> resources = new HashMap<>();
    
    public void put(K key, V value) { resources.put(key, value); }
    public V get(K key) { return resources.get(key); }
    public boolean has(K key) { return resources.containsKey(key); }
    public V getRandom() { /* random selection logic */ }
    public Set<K> keys() { return resources.keySet(); }
}
```

### Layer 2: Lib (Loader-Specific - Core Implementation)

**Location:** `lib/entity/type/`

**Purpose:** Base entity type with Minecraft integration

```java
// lib/entity/type/InternalEntityType.java
public abstract class InternalEntityType<T extends InternalEntityType<T>> {
    
    // -- Core Data --
    protected final String key;
    protected final MutableText name;
    protected final EntityTypeData data;
    
    // -- Resource Maps (Always Present) --
    protected final ResourceMap<TextureVariant, Identifier> textures;
    protected final ResourceMap<ModelVariant, Identifier> models;
    protected final ResourceMap<AnimatorVariant, Identifier> animators;
    
    // -- Feature Modules (Optional) --
    private final Map<Class<?>, Object> features = new HashMap<>();
    
    // -- Constructor --
    protected InternalEntityType(String key) {
        this.key = key;
        this.name = createTranslation(key);
        this.data = new EntityTypeData();
        this.textures = new ResourceMap<>();
        this.models = new ResourceMap<>();
        this.animators = new ResourceMap<>();
        
        // Subclasses populate resources
        populateTextures(textures);
        populateModels(models);
        populateAnimators(animators);
    }
    
    // -- Abstract Methods --
    protected abstract void populateTextures(ResourceMap<TextureVariant, Identifier> textures);
    protected abstract void populateModels(ResourceMap<ModelVariant, Identifier> models);
    protected abstract void populateAnimators(ResourceMap<AnimatorVariant, Identifier> animators);
    protected abstract MutableText createTranslation(String key);
    
    // -- Core Stats Configuration --
    public T withCombatStats(float health, float attack, float speed, 
                             float armor, float toughness, float knockback, float moveSpeed) {
        data.setMaxHealth(health);
        data.setAttackDamage(attack);
        data.setAttackSpeed(speed);
        data.setArmor(armor);
        data.setArmorToughness(toughness);
        data.setKnockbackResistance(knockback);
        data.setMoveSpeed(moveSpeed);
        return (T) this;
    }
    
    // -- Feature System --
    public <F> T withFeature(Class<F> featureClass, F feature) {
        features.put(featureClass, feature);
        return (T) this;
    }
    
    public <F> Optional<F> getFeature(Class<F> featureClass) {
        return Optional.ofNullable((F) features.get(featureClass));
    }
    
    public <F> boolean hasFeature(Class<F> featureClass) {
        return features.containsKey(featureClass);
    }
    
    // -- Core Accessors --
    public String getKey() { return key; }
    public MutableText getName() { return name; }
    public EntityTypeData getData() { return data; }
    public ResourceMap<TextureVariant, Identifier> getTextures() { return textures; }
    public ResourceMap<ModelVariant, Identifier> getModels() { return models; }
    public ResourceMap<AnimatorVariant, Identifier> getAnimators() { return animators; }
}
```



### Layer 3: Lib Features (Optional Modules)

**Location:** `lib/entity/type/features/`

**Purpose:** Optional feature modules that mods can attach

#### Feature 1: Level System

```java
// lib/entity/type/features/LevelFeature.java
public class LevelFeature {
    private int maxLevel;
    private int currentLevel;
    private int experience;
    private ExpCalculationStrategy expStrategy;
    
    /**
     * Creates level feature with specified max level.
     * <p>
     * <b>Flexibility:</b> Each entity type can have different max levels.
     * Max level can be set from config values or hardcoded per type.
     * 
     * @param maxLevel maximum level this entity can reach
     */
    public LevelFeature(int maxLevel) {
        this.maxLevel = maxLevel;
        this.currentLevel = 0;
        this.experience = 0;
        this.expStrategy = new DefaultExpStrategy();
    }
    
    /**
     * Creates level feature with custom XP calculation strategy.
     * 
     * @param maxLevel maximum level
     * @param expStrategy custom XP calculation logic
     */
    public LevelFeature(int maxLevel, ExpCalculationStrategy expStrategy) {
        this.maxLevel = maxLevel;
        this.currentLevel = 0;
        this.experience = 0;
        this.expStrategy = expStrategy;
    }
    
    // -- Getters & Setters --
    
    public int getMaxLevel() { return maxLevel; }
    
    /**
     * Updates max level (useful for config reloads).
     * <p>
     * <b>Config Integration:</b> Call this when config values change.
     * 
     * @param maxLevel new maximum level
     */
    public void setMaxLevel(int maxLevel) { 
        this.maxLevel = maxLevel;
        // Clamp current level if it exceeds new max
        if (this.currentLevel > maxLevel) {
            this.currentLevel = maxLevel;
        }
    }
    
    public int getCurrentLevel() { return currentLevel; }
    public int getExperience() { return experience; }
    
    public void setCurrentLevel(int level) { 
        this.currentLevel = Math.min(Math.max(0, level), maxLevel); 
    }
    
    public void addExperience(int exp) { 
        this.experience += exp; 
    }
    
    public void setExperience(int exp) {
        this.experience = Math.max(0, exp);
    }
    
    /**
     * Gets XP required for next level using configured strategy.
     * 
     * @return XP needed to reach next level
     */
    public int getExpForNextLevel() {
        return expStrategy.calculateExpForLevel(currentLevel + 1);
    }
    
    /**
     * Gets XP required for specific level.
     * 
     * @param level target level
     * @return XP needed to reach that level
     */
    public int getExpForLevel(int level) {
        return expStrategy.calculateExpForLevel(level);
    }
    
    /**
     * Checks if entity can level up.
     * 
     * @return true if has enough XP and not at max level
     */
    public boolean canLevelUp() {
        return currentLevel < maxLevel && experience >= getExpForNextLevel();
    }
    
    /**
     * Attempts to level up, consuming XP.
     * 
     * @return true if leveled up successfully
     */
    public boolean tryLevelUp() {
        if (canLevelUp()) {
            experience -= getExpForNextLevel();
            currentLevel++;
            return true;
        }
        return false;
    }
    
    /**
     * Sets custom XP calculation strategy.
     * 
     * @param strategy new calculation strategy
     */
    public void setExpStrategy(ExpCalculationStrategy strategy) {
        this.expStrategy = strategy;
    }
    
    // -- XP Calculation Strategies --
    
    /**
     * Strategy interface for XP calculations.
     * <p>
     * <b>Extensibility:</b> Mods can implement custom XP curves.
     */
    public interface ExpCalculationStrategy {
        int calculateExpForLevel(int level);
    }
    
    /**
     * Default linear XP progression.
     */
    public static class DefaultExpStrategy implements ExpCalculationStrategy {
        @Override
        public int calculateExpForLevel(int level) {
            return level * 100;
        }
    }
    
    /**
     * Exponential XP progression (harder to level at high levels).
     */
    public static class ExponentialExpStrategy implements ExpCalculationStrategy {
        private final double base;
        
        public ExponentialExpStrategy(double base) {
            this.base = base;
        }
        
        @Override
        public int calculateExpForLevel(int level) {
            return (int) (100 * Math.pow(base, level / 10.0));
        }
    }
    
    /**
     * Custom XP progression using formula.
     */
    public static class FormulaExpStrategy implements ExpCalculationStrategy {
        private final java.util.function.IntUnaryOperator formula;
        
        public FormulaExpStrategy(java.util.function.IntUnaryOperator formula) {
            this.formula = formula;
        }
        
        @Override
        public int calculateExpForLevel(int level) {
            return formula.applyAsInt(level);
        }
    }
}
```

#### Feature 2: Food System

```java
// lib/entity/type/features/FoodFeature.java
public class FoodFeature {
    private final Set<Item> foods = new HashSet<>();
    private final Set<Item> temptingItems = new HashSet<>();
    
    public FoodFeature withFoods(Item... items) {
        foods.addAll(Arrays.asList(items));
        return this;
    }
    
    public FoodFeature withTemptingItems(Item... items) {
        temptingItems.addAll(Arrays.asList(items));
        return this;
    }
    
    public boolean isFood(Item item) {
        return foods.contains(item);
    }
    
    public boolean isFood(ItemStack stack) {
        return isFood(stack.getItem());
    }
    
    public Ingredient getFoodIngredient() {
        return Ingredient.ofItems(foods.toArray(new Item[0]));
    }
    
    public Ingredient getTemptingIngredient() {
        return Ingredient.ofItems(temptingItems.toArray(new Item[0]));
    }
    
    public Set<Item> getFoods() { return Collections.unmodifiableSet(foods); }
    public Set<Item> getTemptingItems() { return Collections.unmodifiableSet(temptingItems); }
}
```

#### Feature 3: Sound System

```java
// lib/entity/type/features/SoundFeature.java
public class SoundFeature {
    private final ResourceMap<SoundType, SoundEvent> sounds;
    
    public enum SoundType {
        DEFAULT, AMBIENT, HURT, DEATH, ATTACK, INTERACT, STEP
    }
    
    public SoundFeature() {
        this.sounds = new ResourceMap<>();
    }
    
    public SoundFeature withSound(SoundType type, SoundEvent sound) {
        sounds.put(type, sound);
        return this;
    }
    
    public SoundEvent getSound(SoundType type) {
        return sounds.get(type);
    }
    
    public SoundEvent getRandomSound() {
        return sounds.getRandom();
    }
    
    public boolean hasSound(SoundType type) {
        return sounds.has(type);
    }
}
```

#### Feature 4: Animation System

```java
// lib/entity/type/features/AnimationFeature.java
public class AnimationFeature {
    private final ResourceMap<AnimationType, RawAnimation> animations;
    
    public enum AnimationType {
        IDLE, WALK, RUN, ATTACK, HURT, DEATH, 
        SIT, REST, INTERACT, WAVE, SPECIAL
    }
    
    public AnimationFeature() {
        this.animations = new ResourceMap<>();
    }
    
    public AnimationFeature withAnimation(AnimationType type, RawAnimation animation) {
        animations.put(type, animation);
        return this;
    }
    
    public AnimationFeature withLoopingAnimation(AnimationType type, String animationName) {
        animations.put(type, RawAnimation.begin().thenLoop(animationName));
        return this;
    }
    
    public AnimationFeature withOnceAnimation(AnimationType type, String animationName) {
        animations.put(type, RawAnimation.begin().then(animationName, Animation.LoopType.PLAY_ONCE));
        return this;
    }
    
    public RawAnimation getAnimation(AnimationType type) {
        return animations.get(type);
    }
    
    public boolean hasAnimation(AnimationType type) {
        return animations.has(type);
    }
}
```

#### Feature 5: Spawn Configuration

```java
// lib/entity/type/features/SpawnFeature.java
public class SpawnFeature {
    private int weight;
    private int minGroupSize;
    private int maxGroupSize;
    private List<RegistryKey<Biome>> biomes;
    
    public SpawnFeature(int weight, int minGroup, int maxGroup, RegistryKey<Biome>... biomes) {
        this.weight = weight;
        this.minGroupSize = minGroup;
        this.maxGroupSize = maxGroup;
        this.biomes = Arrays.asList(biomes);
    }
    
    public int getWeight() { return weight; }
    public int getMinGroupSize() { return minGroupSize; }
    public int getMaxGroupSize() { return maxGroupSize; }
    public List<RegistryKey<Biome>> getBiomes() { return biomes; }
}
```

#### Feature 6: Texture Variants

```java
// lib/entity/type/features/TextureVariantFeature.java
public class TextureVariantFeature {
    private final ResourceMap<String, Identifier> additionalTextures;
    
    public TextureVariantFeature() {
        this.additionalTextures = new ResourceMap<>();
    }
    
    public TextureVariantFeature withTexture(String variant, Identifier texture) {
        additionalTextures.put(variant, texture);
        return this;
    }
    
    // Helper for common patterns
    public TextureVariantFeature withBodyVariants(String basePath, String... variants) {
        for (String variant : variants) {
            additionalTextures.put(variant, 
                new Identifier(basePath + "_" + variant + ".png"));
        }
        return this;
    }
    
    public Identifier getTexture(String variant) {
        return additionalTextures.get(variant);
    }
    
    public boolean hasTexture(String variant) {
        return additionalTextures.has(variant);
    }
    
    public Identifier getRandomTexture() {
        return additionalTextures.getRandom();
    }
}
```



### Layer 4: Mod-Specific Implementation

**Location:** `common/entity/type/` (in each mod)

**Purpose:** Mod-specific entity type implementations using HZLib base + features

#### Example 1: LovelyRobot Implementation

```java
// common/entity/type/RobotEntityType.java
public class RobotEntityType extends InternalEntityType<RobotEntityType> {
    
    // Robot-specific: 16-color palette support
    private final Map<EntityTexture, Identifier> colorTextures = new HashMap<>();
    
    public RobotEntityType(String key) {
        super(key);
        
        // Add level feature (robot-specific)
        withFeature(LevelFeature.class, new LevelFeature(200));
    }
    
    @Override
    protected void populateTextures(ResourceMap<TextureVariant, Identifier> textures) {
        // Base texture
        textures.put(TextureVariant.DEFAULT, 
            LovelyIdentifier.getId("textures/entity/" + key + "/" + key + ".png"));
    }
    
    @Override
    protected void populateModels(ResourceMap<ModelVariant, Identifier> models) {
        models.put(ModelVariant.DEFAULT, 
            LovelyIdentifier.getId("geo/" + key + ".geo.json"));
        models.put(ModelVariant.ARMED, 
            LovelyIdentifier.getId("geo/" + key + ".attack.geo.json"));
    }
    
    @Override
    protected void populateAnimators(ResourceMap<AnimatorVariant, Identifier> animators) {
        animators.put(AnimatorVariant.DEFAULT, 
            LovelyIdentifier.getId("animations/default.animation.json"));
    }
    
    @Override
    protected MutableText createTranslation(String key) {
        return LovelyIdentifier.getVariantTranslation(key);
    }
    
    // Robot-specific: 16-color palette
    public RobotEntityType withColorPalette(EntityVariant variant) {
        String path = variant.getName() + "/" + variant.getName();
        for (EntityTexture color : EntityTexture.values()) {
            colorTextures.put(color, 
                LovelyIdentifier.getId("textures/entity/" + path + "_" + 
                    String.format("%02d", color.getId()) + ".png"));
        }
        return this;
    }
    
    public Identifier getColorTexture(EntityTexture color) {
        return colorTextures.getOrDefault(color, colorTextures.get(EntityTexture.WHITE));
    }
    
    public boolean hasColor(EntityTexture color) {
        return colorTextures.containsKey(color);
    }
    
    public int getRandomColorId() {
        List<EntityTexture> colors = new ArrayList<>(colorTextures.keySet());
        return colors.isEmpty() ? 0 : 
            colors.get(new Random().nextInt(colors.size())).getId();
    }
    
    // Convenience accessors for level feature
    public int getMaxLevel() {
        return getFeature(LevelFeature.class)
            .map(LevelFeature::getMaxLevel)
            .orElse(0);
    }
}

// source/entity/type/NativeRobotType.java
public class NativeRobotType {
    public static final List<RobotEntityType> TYPES = new ArrayList<>();
    
    // Define robot types with default values
    public static final RobotEntityType VANILLA = create("vanilla");
    public static final RobotEntityType BUNNY2 = create("bunny2");
    public static final RobotEntityType DRAGON = create("dragon");
    public static final RobotEntityType KITSUNE = create("kitsune");
    
    static {
        // Configure VANILLA from config
        VANILLA
            .withMaxLevel(LovelyConfigs.VanillaMaxLevel)  // e.g., 200
            .withCombatStats(
                LovelyConfigs.VanillaMaxHealth,
                LovelyConfigs.VanillaAttackDamage,
                LovelyConfigs.VanillaAttackSpeed,
                LovelyConfigs.VanillaArmor,
                LovelyConfigs.VanillaArmorToughness,
                0F,
                LovelyConfigs.VanillaMovementSpeed
            )
            .withColorPalette(EntityVariant.VANILLA);
        
        // Configure BUNNY2 from config (different max level)
        BUNNY2
            .withMaxLevel(LovelyConfigs.Bunny2MaxLevel)  // e.g., 150
            .withCombatStats(
                LovelyConfigs.Bunny2MaxHealth,
                LovelyConfigs.Bunny2AttackDamage,
                LovelyConfigs.Bunny2AttackSpeed,
                LovelyConfigs.Bunny2Armor,
                LovelyConfigs.Bunny2ArmorToughness,
                0F,
                LovelyConfigs.Bunny2MovementSpeed
            )
            .withColorPalette(EntityVariant.BUNNY2);
        
        // Configure DRAGON from config (higher max level)
        DRAGON
            .withMaxLevel(LovelyConfigs.DragonMaxLevel)  // e.g., 300
            .withCombatStats(
                LovelyConfigs.DragonMaxHealth,
                LovelyConfigs.DragonAttackDamage,
                LovelyConfigs.DragonAttackSpeed,
                LovelyConfigs.DragonArmor,
                LovelyConfigs.DragonArmorToughness,
                0.5F,  // Dragons have knockback resistance
                LovelyConfigs.DragonMovementSpeed
            )
            .withColorPalette(EntityVariant.DRAGON)
            // Dragons use exponential XP curve (harder to level)
            .withExpStrategy(new LevelFeature.ExponentialExpStrategy(1.1));
        
        // Configure KITSUNE from config (progressive max level based on tails)
        KITSUNE
            .withMaxLevel(LovelyConfigs.KitsuneMaxLevel)  // e.g., 250
            .withCombatStats(
                LovelyConfigs.KitsuneMaxHealth,
                LovelyConfigs.KitsuneAttackDamage,
                LovelyConfigs.KitsuneAttackSpeed,
                LovelyConfigs.KitsuneArmor,
                LovelyConfigs.KitsuneArmorToughness,
                0F,
                LovelyConfigs.KitsuneMovementSpeed
            )
            .withColorPalette(EntityVariant.KITSUNE)
            // Kitsune uses custom formula (unlocks tails at specific levels)
            .withExpStrategy(new LevelFeature.FormulaExpStrategy(level -> {
                // Custom progression: harder every 30 levels (tail unlock)
                int tailTier = level / 30;
                return 100 * level + (tailTier * tailTier * 500);
            }));
    }
    
    private static RobotEntityType create(String key) {
        RobotEntityType type = new RobotEntityType(key);
        TYPES.add(type);
        return type;
    }
    
    /**
     * Reloads all robot types from config.
     * <p>
     * <b>Config Reload:</b> Call this when config values change at runtime.
     */
    public static void reloadFromConfig() {
        VANILLA.withMaxLevel(LovelyConfigs.VanillaMaxLevel);
        BUNNY2.withMaxLevel(LovelyConfigs.Bunny2MaxLevel);
        DRAGON.withMaxLevel(LovelyConfigs.DragonMaxLevel);
        KITSUNE.withMaxLevel(LovelyConfigs.KitsuneMaxLevel);
        
        // Update combat stats too
        VANILLA.withCombatStats(
            LovelyConfigs.VanillaMaxHealth,
            LovelyConfigs.VanillaAttackDamage,
            LovelyConfigs.VanillaAttackSpeed,
            LovelyConfigs.VanillaArmor,
            LovelyConfigs.VanillaArmorToughness,
            0F,
            LovelyConfigs.VanillaMovementSpeed
        );
        // ... repeat for other types
    }
}
```

#### Example 2: Monsters & Girls Implementation

```java
// common/entity/type/MonsterEntityType.java
public class MonsterEntityType extends InternalEntityType<MonsterEntityType> {
    
    private final EntityNative nativeGroup;
    
    public MonsterEntityType(String key, EntityNative nativeGroup) {
        super(key);
        this.nativeGroup = nativeGroup;
        
        // Add features that monsters use
        withFeature(FoodFeature.class, new FoodFeature());
        withFeature(SoundFeature.class, new SoundFeature());
        withFeature(AnimationFeature.class, new AnimationFeature());
    }
    
    @Override
    protected void populateTextures(ResourceMap<TextureVariant, Identifier> textures) {
        String path = "textures/entity/" + nativeGroup.getName() + "/" + key;
        textures.put(TextureVariant.DEFAULT, 
            MonstersGirlsID.getId(path + ".png"));
    }
    
    @Override
    protected void populateModels(ResourceMap<ModelVariant, Identifier> models) {
        String path = "geo/" + nativeGroup.getName() + "_girl.geo.json";
        models.put(ModelVariant.DEFAULT, MonstersGirlsID.getId(path));
    }
    
    @Override
    protected void populateAnimators(ResourceMap<AnimatorVariant, Identifier> animators) {
        String path = "animations/" + nativeGroup.getName() + "_girl.animation.json";
        animators.put(AnimatorVariant.DEFAULT, MonstersGirlsID.getId(path));
    }
    
    @Override
    protected MutableText createTranslation(String key) {
        return MonstersGirlsID.getTranslation(MonstersGirlsID.VARIANT_PREFIX, key);
    }
    
    // Convenience methods for features
    public MonsterEntityType withFoods(Item... items) {
        getFeature(FoodFeature.class).ifPresent(food -> food.withFoods(items));
        return this;
    }
    
    public MonsterEntityType withTemptingItems(Item... items) {
        getFeature(FoodFeature.class).ifPresent(food -> food.withTemptingItems(items));
        return this;
    }
    
    public MonsterEntityType withSounds(SoundEvent defaultSound, SoundEvent hurt, 
                                        SoundEvent death, SoundEvent attack) {
        getFeature(SoundFeature.class).ifPresent(sound -> {
            sound.withSound(SoundFeature.SoundType.DEFAULT, defaultSound);
            sound.withSound(SoundFeature.SoundType.HURT, hurt);
            sound.withSound(SoundFeature.SoundType.DEATH, death);
            sound.withSound(SoundFeature.SoundType.ATTACK, attack);
        });
        return this;
    }
    
    public MonsterEntityType withAnimations(AnimationFeature.AnimationType... types) {
        getFeature(AnimationFeature.class).ifPresent(anim -> {
            for (AnimationFeature.AnimationType type : types) {
                anim.withLoopingAnimation(type, type.name().toLowerCase());
            }
        });
        return this;
    }
    
    public MonsterEntityType withSpawn(int weight, int minGroup, int maxGroup, 
                                       RegistryKey<Biome>... biomes) {
        withFeature(SpawnFeature.class, new SpawnFeature(weight, minGroup, maxGroup, biomes));
        return this;
    }
    
    public MonsterEntityType withTextureVariants(String... variants) {
        TextureVariantFeature feature = new TextureVariantFeature();
        String basePath = "textures/entity/" + nativeGroup.getName() + "/" + key;
        feature.withBodyVariants(basePath, variants);
        withFeature(TextureVariantFeature.class, feature);
        return this;
    }
    
    public EntityNative getNativeGroup() {
        return nativeGroup;
    }
}

// source/entity/type/WispVariant.java
public class WispVariant {
    public static final List<MonsterEntityType> VARIANTS = new ArrayList<>();
    
    public static final MonsterEntityType BLUE = create("wisp_blue")
        .withCombatStats(18F, 3F, 1.2F, 0.1F, 0.2F, 0F, 0.6F)
        .withFoods(Items.GOLD_NUGGET, Items.GOLD_INGOT, Items.RAW_GOLD)
        .withTemptingItems(Items.GOLD_NUGGET, Items.GOLD_INGOT, Items.RAW_GOLD, Items.GOLD_BLOCK)
        .withSounds(MonstersGirlsSounds.WISP_LAUGH, MonstersGirlsSounds.WISP_HURT, 
                   MonstersGirlsSounds.WISP_DEATH, null)
        .withAnimations(AnimationFeature.AnimationType.IDLE, 
                       AnimationFeature.AnimationType.WALK,
                       AnimationFeature.AnimationType.REST, 
                       AnimationFeature.AnimationType.ATTACK)
        .withSpawn(20, 1, 2, BiomeKeys.DARK_FOREST, BiomeKeys.SWAMP);
    
    public static final MonsterEntityType YELLOW = create("wisp_yellow")
        .withCombatStats(18F, 3F, 1.2F, 0.1F, 0.2F, 0F, 0.6F)
        .withFoods(Items.GOLD_NUGGET, Items.GOLD_INGOT, Items.RAW_GOLD)
        .withTemptingItems(Items.GOLD_NUGGET, Items.GOLD_INGOT, Items.RAW_GOLD, Items.GOLD_BLOCK)
        .withSounds(MonstersGirlsSounds.WISP_LAUGH, MonstersGirlsSounds.WISP_HURT, 
                   MonstersGirlsSounds.WISP_DEATH, null)
        .withAnimations(AnimationFeature.AnimationType.IDLE, 
                       AnimationFeature.AnimationType.WALK,
                       AnimationFeature.AnimationType.REST, 
                       AnimationFeature.AnimationType.ATTACK)
        .withTextureVariants("default", "tummy")
        .withSpawn(20, 1, 2, BiomeKeys.DARK_FOREST, BiomeKeys.SWAMP);
    
    private static MonsterEntityType create(String key) {
        MonsterEntityType type = new MonsterEntityType(key, EntityNative.WISP);
        VARIANTS.add(type);
        return type;
    }
    
    public static MonsterEntityType findByFood(Item item) {
        return VARIANTS.stream()
            .filter(type -> type.getFeature(FoodFeature.class)
                .map(food -> food.isFood(item))
                .orElse(false))
            .findFirst()
            .orElse(null);
    }
}
```



## Architecture Benefits

### 1. Modularity

**Core System (Always Present):**
- Textures, Models, Animators
- Combat stats
- Resource management

**Optional Features (Add as Needed):**
- Level system (LovelyRobot)
- Food/Tempting (Monsters & Girls)
- Sounds (Monsters & Girls)
- Animations (Monsters & Girls)
- Spawn config (Monsters & Girls)
- Texture variants (Monsters & Girls)

### 2. Extensibility

**Adding New Features:**
```java
// New mod wants "reputation" system
public class ReputationFeature {
    private int reputation;
    
    public void addReputation(int amount) { reputation += amount; }
    public int getReputation() { return reputation; }
}

// Use it
myEntityType.withFeature(ReputationFeature.class, new ReputationFeature());
```

### 3. Type Safety

**Feature Access:**
```java
// Type-safe feature access
Optional<LevelFeature> level = entityType.getFeature(LevelFeature.class);
level.ifPresent(l -> l.addExperience(100));

// Check if feature exists
if (entityType.hasFeature(FoodFeature.class)) {
    // Handle food logic
}
```

### 4. No Bloat

**Each mod only includes what it needs:**
- LovelyRobot: Core + LevelFeature
- Monsters & Girls: Core + FoodFeature + SoundFeature + AnimationFeature + SpawnFeature
- Future mods: Core + whatever they need

## Implementation Roadmap

### Phase 1: Framework Layer (Week 1)

**Create in `framework/entity/type/`:**
1. `EntityTypeData.java` - Pure data container
2. `ResourceMap.java` - Generic resource mapping
3. `TextureVariant.java` - Enum for texture types
4. `ModelVariant.java` - Enum for model types
5. `AnimatorVariant.java` - Enum for animator types

**Estimated Time:** 4-6 hours

### Phase 2: Lib Core (Week 2)

**Create in `lib/entity/type/`:**
1. `InternalEntityType.java` - Core implementation
2. Test with minimal entity type
3. Verify feature system works

**Estimated Time:** 8-12 hours

### Phase 3: Lib Features (Week 3)

**Create in `lib/entity/type/features/`:**
1. `LevelFeature.java`
2. `FoodFeature.java`
3. `SoundFeature.java`
4. `AnimationFeature.java`
5. `SpawnFeature.java`
6. `TextureVariantFeature.java`

**Estimated Time:** 12-16 hours

### Phase 4: LovelyRobot Migration (Week 4)

**Refactor LovelyRobot to use HZLib:**
1. Create `RobotEntityType extends InternalEntityType`
2. Migrate `NativeEntityType` to use new system
3. Update entity classes to use new type system
4. Test all features work

**Estimated Time:** 10-15 hours

### Phase 5: Monsters & Girls Migration (Week 5)

**Refactor Monsters & Girls to use HZLib:**
1. Create `MonsterEntityType extends InternalEntityType`
2. Migrate `WispVariant`, `SpookVariant` to use new system
3. Update entity classes to use new system
4. Test all features work

**Estimated Time:** 10-15 hours

**Total Estimated Time:** 44-64 hours (6-8 working days)

## Code Organization

### HZLib Structure

```
hzlib/
├── framework/
│   └── entity/
│       └── type/
│           ├── EntityTypeData.java
│           ├── ResourceMap.java
│           ├── TextureVariant.java
│           ├── ModelVariant.java
│           └── AnimatorVariant.java
│
└── lib/
    └── entity/
        └── type/
            ├── InternalEntityType.java
            └── features/
                ├── LevelFeature.java
                ├── FoodFeature.java
                ├── SoundFeature.java
                ├── AnimationFeature.java
                ├── SpawnFeature.java
                └── TextureVariantFeature.java
```

### Mod Structure (LovelyRobot)

```
llovelyr/
├── common/
│   └── entity/
│       └── type/
│           └── RobotEntityType.java
│
└── source/
    └── entity/
        └── type/
            └── NativeRobotType.java
```

### Mod Structure (Monsters & Girls)

```
monsters_girls/
├── common/
│   └── entity/
│       └── type/
│           └── MonsterEntityType.java
│
└── source/
    └── entity/
        └── type/
            ├── WispVariant.java
            ├── SpookVariant.java
            └── [OtherVariants].java
```

## Usage Examples

### Example 1: Simple Entity Type (Minimal)

```java
public class SimpleEntityType extends InternalEntityType<SimpleEntityType> {
    public SimpleEntityType(String key) {
        super(key);
    }
    
    @Override
    protected void populateTextures(ResourceMap<TextureVariant, Identifier> textures) {
        textures.put(TextureVariant.DEFAULT, new Identifier("mod", "textures/entity/" + key + ".png"));
    }
    
    @Override
    protected void populateModels(ResourceMap<ModelVariant, Identifier> models) {
        models.put(ModelVariant.DEFAULT, new Identifier("mod", "geo/" + key + ".geo.json"));
    }
    
    @Override
    protected void populateAnimators(ResourceMap<AnimatorVariant, Identifier> animators) {
        animators.put(AnimatorVariant.DEFAULT, new Identifier("mod", "animations/" + key + ".animation.json"));
    }
    
    @Override
    protected MutableText createTranslation(String key) {
        return Text.translatable("entity.mod." + key);
    }
}

// Usage
SimpleEntityType myType = new SimpleEntityType("my_entity")
    .withCombatStats(20F, 5F, 1.0F, 2F, 0F, 0F, 0.25F);
```

### Example 2: Entity with Levels

```java
SimpleEntityType myType = new SimpleEntityType("my_entity")
    .withCombatStats(20F, 5F, 1.0F, 2F, 0F, 0F, 0.25F)
    .withFeature(LevelFeature.class, new LevelFeature(100));

// Later in entity
entityType.getFeature(LevelFeature.class).ifPresent(level -> {
    level.addExperience(50);
    if (level.getExperience() >= level.getExpForNextLevel()) {
        level.setCurrentLevel(level.getCurrentLevel() + 1);
    }
});
```

### Example 3: Entity with Food

```java
SimpleEntityType myType = new SimpleEntityType("my_entity")
    .withCombatStats(20F, 5F, 1.0F, 2F, 0F, 0F, 0.25F)
    .withFeature(FoodFeature.class, new FoodFeature()
        .withFoods(Items.APPLE, Items.BREAD)
        .withTemptingItems(Items.GOLDEN_APPLE));

// Later in entity
entityType.getFeature(FoodFeature.class).ifPresent(food -> {
    if (food.isFood(playerHeldItem)) {
        // Feed entity
    }
});
```

### Example 4: Full-Featured Entity

```java
MonsterEntityType fullType = new MonsterEntityType("advanced_entity", EntityNative.CUSTOM)
    .withCombatStats(25F, 6F, 1.5F, 3F, 1F, 0.2F, 0.3F)
    .withFeature(LevelFeature.class, new LevelFeature(150))
    .withFoods(Items.DIAMOND, Items.EMERALD)
    .withTemptingItems(Items.DIAMOND_BLOCK)
    .withSounds(customSound, hurtSound, deathSound, attackSound)
    .withAnimations(AnimationType.IDLE, AnimationType.WALK, AnimationType.ATTACK)
    .withSpawn(15, 1, 3, BiomeKeys.PLAINS, BiomeKeys.FOREST)
    .withTextureVariants("default", "slim", "chunky");
```

## Migration Guide

### Migrating LovelyRobot InternalEntityType

**Before:**
```java
public abstract class InternalEntityType<T> {
    public final String key;
    public final MutableText name;
    public HashMap<EntityTexture, Identifier> texture;
    public final HashMap<EntityModel, Identifier> model;
    public final HashMap<EntityAnimator, Identifier> animator;
    
    protected int maxLevel = 0;
    protected float maxHealth = 0.0F;
    // ... more stats
    
    protected abstract HashMap<EntityTexture, Identifier> setTexture(EntityVariant variant);
    protected abstract HashMap<EntityModel, Identifier> setModel(EntityVariant variant);
    protected abstract HashMap<EntityAnimator, Identifier> setAnimator(EntityVariant variant);
}
```

**After:**
```java
public class RobotEntityType extends InternalEntityType<RobotEntityType> {
    private final Map<EntityTexture, Identifier> colorTextures = new HashMap<>();
    
    public RobotEntityType(String key) {
        super(key);
        withFeature(LevelFeature.class, new LevelFeature(200));
    }
    
    @Override
    protected void populateTextures(ResourceMap<TextureVariant, Identifier> textures) {
        // Populate base textures
    }
    
    @Override
    protected void populateModels(ResourceMap<ModelVariant, Identifier> models) {
        // Populate models
    }
    
    @Override
    protected void populateAnimators(ResourceMap<AnimatorVariant, Identifier> animators) {
        // Populate animators
    }
    
    public RobotEntityType withColorPalette(EntityVariant variant) {
        // Populate 16-color palette
        return this;
    }
}
```

**Benefits:**
- ✅ Cleaner separation of concerns
- ✅ Level system is optional feature
- ✅ Can be reused by other mods
- ✅ Type-safe feature access
- ✅ No bloat from unused features



### Migrating Monsters & Girls InternalEntityType

**Before:**
```java
public abstract class InternalEntityType<T> {
    public final EntityNative nativeEntity;
    public final String key;
    public HashMap<EntityTexture, Identifier> textures;
    public final HashMap<EntityModel, Identifier> models;
    public final HashMap<EntityAnimator, Identifier> animators;
    public final Set<Item> foods = new HashSet<>();
    public final Set<Item> temptingItems = new HashSet<>();
    public HashMap<EntitySound, SoundEvent> sounds;
    public HashMap<EntityAnimation, RawAnimation> animations;
    
    public int spawnWeight = 0;
    public int spawnMinGroup = 0;
    public int spawnMaxGroup = 0;
    public List<RegistryKey<Biome>> spawnBiomes = new ArrayList<>();
    
    protected T addTextures(boolean overrideDefault, EntityTexture... textureSet) { }
    protected T addAnimations(EntityAnimation... animationSet) { }
    protected T addFoods(@NotNull Item... items) { }
    protected T addTemptingItems(@NotNull Item... items) { }
    protected T addSpawn(int weight, int minGroup, int maxGroup, RegistryKey<Biome>... biomes) { }
}
```

**After:**
```java
public class MonsterEntityType extends InternalEntityType<MonsterEntityType> {
    private final EntityNative nativeGroup;
    
    public MonsterEntityType(String key, EntityNative nativeGroup) {
        super(key);
        this.nativeGroup = nativeGroup;
        
        // Add features
        withFeature(FoodFeature.class, new FoodFeature());
        withFeature(SoundFeature.class, new SoundFeature());
        withFeature(AnimationFeature.class, new AnimationFeature());
    }
    
    // Convenience methods delegate to features
    public MonsterEntityType withFoods(Item... items) {
        getFeature(FoodFeature.class).ifPresent(food -> food.withFoods(items));
        return this;
    }
    
    public MonsterEntityType withSounds(SoundEvent defaultSound, SoundEvent hurt, 
                                        SoundEvent death, SoundEvent attack) {
        getFeature(SoundFeature.class).ifPresent(sound -> {
            sound.withSound(SoundFeature.SoundType.DEFAULT, defaultSound);
            sound.withSound(SoundFeature.SoundType.HURT, hurt);
            sound.withSound(SoundFeature.SoundType.DEATH, death);
            sound.withSound(SoundFeature.SoundType.ATTACK, attack);
        });
        return this;
    }
    
    // ... other convenience methods
}
```

**Benefits:**
- ✅ Features are optional and composable
- ✅ Can add new features without modifying base class
- ✅ Type-safe feature access
- ✅ Cleaner API with convenience methods
- ✅ Can be reused by other mods

## Advanced Features

### Custom Feature Example

**Creating a "Reputation" feature for a new mod:**

```java
// lib/entity/type/features/ReputationFeature.java
public class ReputationFeature {
    private int reputation;
    private final Map<UUID, Integer> playerReputations = new HashMap<>();
    
    public ReputationFeature() {
        this.reputation = 0;
    }
    
    public void addGlobalReputation(int amount) {
        reputation += amount;
    }
    
    public void addPlayerReputation(UUID playerId, int amount) {
        playerReputations.merge(playerId, amount, Integer::sum);
    }
    
    public int getGlobalReputation() {
        return reputation;
    }
    
    public int getPlayerReputation(UUID playerId) {
        return playerReputations.getOrDefault(playerId, 0);
    }
    
    public boolean isHostile(UUID playerId) {
        return getPlayerReputation(playerId) < -50;
    }
    
    public boolean isFriendly(UUID playerId) {
        return getPlayerReputation(playerId) > 50;
    }
}

// Usage in mod
myEntityType.withFeature(ReputationFeature.class, new ReputationFeature());

// In entity behavior
entityType.getFeature(ReputationFeature.class).ifPresent(rep -> {
    if (rep.isHostile(player.getUuid())) {
        // Attack player
    } else if (rep.isFriendly(player.getUuid())) {
        // Help player
    }
});
```

### Feature Composition

**Combining multiple features:**

```java
public class AdvancedEntityType extends InternalEntityType<AdvancedEntityType> {
    
    public AdvancedEntityType(String key) {
        super(key);
        
        // Compose multiple features
        withFeature(LevelFeature.class, new LevelFeature(100));
        withFeature(FoodFeature.class, new FoodFeature());
        withFeature(ReputationFeature.class, new ReputationFeature());
    }
    
    // Convenience method that uses multiple features
    public void feedAndReward(Player player, ItemStack food) {
        getFeature(FoodFeature.class).ifPresent(foodFeature -> {
            if (foodFeature.isFood(food)) {
                // Add XP
                getFeature(LevelFeature.class).ifPresent(level -> {
                    level.addExperience(10);
                });
                
                // Increase reputation
                getFeature(ReputationFeature.class).ifPresent(rep -> {
                    rep.addPlayerReputation(player.getUuid(), 5);
                });
            }
        });
    }
}
```

## Testing Strategy

### Unit Tests (Framework)

```java
@Test
public void testResourceMap() {
    ResourceMap<String, String> map = new ResourceMap<>();
    map.put("key1", "value1");
    map.put("key2", "value2");
    
    assertTrue(map.has("key1"));
    assertEquals("value1", map.get("key1"));
    assertNotNull(map.getRandom());
}

@Test
public void testEntityTypeData() {
    EntityTypeData data = new EntityTypeData();
    data.setMaxHealth(20F);
    data.setAttackDamage(5F);
    
    assertEquals(20F, data.getMaxHealth(), 0.01F);
    assertEquals(5F, data.getAttackDamage(), 0.01F);
}
```

### Integration Tests (Lib)

```java
@Test
public void testInternalEntityTypeWithFeatures() {
    TestEntityType type = new TestEntityType("test")
        .withCombatStats(20F, 5F, 1.0F, 2F, 0F, 0F, 0.25F)
        .withFeature(LevelFeature.class, new LevelFeature(100));
    
    assertEquals("test", type.getKey());
    assertTrue(type.hasFeature(LevelFeature.class));
    
    type.getFeature(LevelFeature.class).ifPresent(level -> {
        assertEquals(100, level.getMaxLevel());
    });
}

@Test
public void testFoodFeature() {
    FoodFeature food = new FoodFeature()
        .withFoods(Items.APPLE, Items.BREAD);
    
    assertTrue(food.isFood(Items.APPLE));
    assertFalse(food.isFood(Items.DIAMOND));
}
```

### Behavioral Tests (Mod)

```java
@Test
public void testRobotEntityTypeColorPalette() {
    RobotEntityType robot = new RobotEntityType("vanilla")
        .withColorPalette(EntityVariant.VANILLA);
    
    assertTrue(robot.hasColor(EntityTexture.WHITE));
    assertTrue(robot.hasColor(EntityTexture.RED));
    assertNotNull(robot.getColorTexture(EntityTexture.BLUE));
}

@Test
public void testMonsterEntityTypeFeatures() {
    MonsterEntityType monster = new MonsterEntityType("wisp_blue", EntityNative.WISP)
        .withFoods(Items.GOLD_NUGGET)
        .withSounds(sound1, sound2, sound3, sound4);
    
    assertTrue(monster.hasFeature(FoodFeature.class));
    assertTrue(monster.hasFeature(SoundFeature.class));
    
    monster.getFeature(FoodFeature.class).ifPresent(food -> {
        assertTrue(food.isFood(Items.GOLD_NUGGET));
    });
}
```

## Performance Considerations

### Resource Map Caching

**Problem:** Random selection creates new lists every time
**Solution:** Cache key lists

```java
public class ResourceMap<K, V> {
    private final Map<K, V> resources = new HashMap<>();
    private List<K> cachedKeys = null;
    
    public void put(K key, V value) {
        resources.put(key, value);
        cachedKeys = null; // Invalidate cache
    }
    
    public V getRandom() {
        if (cachedKeys == null) {
            cachedKeys = new ArrayList<>(resources.keySet());
        }
        if (cachedKeys.isEmpty()) return null;
        return resources.get(cachedKeys.get(new Random().nextInt(cachedKeys.size())));
    }
}
```

### Feature Access Optimization

**Problem:** Optional.ifPresent() creates overhead
**Solution:** Cache frequently accessed features

```java
public class OptimizedEntityType extends InternalEntityType<OptimizedEntityType> {
    // Cache frequently accessed features
    private LevelFeature cachedLevel;
    private FoodFeature cachedFood;
    
    @Override
    public <F> OptimizedEntityType withFeature(Class<F> featureClass, F feature) {
        super.withFeature(featureClass, feature);
        
        // Update cache
        if (featureClass == LevelFeature.class) {
            cachedLevel = (LevelFeature) feature;
        } else if (featureClass == FoodFeature.class) {
            cachedFood = (FoodFeature) feature;
        }
        
        return this;
    }
    
    // Fast accessors
    public LevelFeature getLevel() {
        return cachedLevel;
    }
    
    public FoodFeature getFood() {
        return cachedFood;
    }
}
```

## Conclusion

This architecture provides:

1. **Modularity** - Core + optional features
2. **Reusability** - Shared across all mods
3. **Extensibility** - Easy to add new features
4. **Type Safety** - Compile-time feature checking
5. **No Bloat** - Only include what you need
6. **Clean API** - Fluent builder pattern
7. **Performance** - Optimized resource access

**Next Steps:**
1. Review this architecture
2. Implement Framework layer
3. Implement Lib core + features
4. Migrate LovelyRobot
5. Migrate Monsters & Girls
6. Extract to external HZLib

---

**Document Status:** Architecture Proposal
**Next Action:** Review and approve architecture
**Owner:** Development Team
**Last Updated:** 2025-11-29

