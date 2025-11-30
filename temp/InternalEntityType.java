package net.msymbios.monsters_girls.common.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.msymbios.monsters_girls.config.MonstersGirlsID;
import net.msymbios.monsters_girls.entity.internal.enums.*;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.*;
import java.util.function.Predicate;

/**
 * The Internal Entity Type contains properties used to determine textures, spawning and stats.
 * <p>
 * NOTE: If the key of anything type changes it must be reflected in the find method using the version.
 */
public abstract class InternalEntityType<T> {

    // -- Variables --

    public final EntityNative nativeEntity;

    /**
     * The key used to identify the entity type (for things like translation text components).
     */
    @NotNull
    public final String key;

    /**
     * The translation text component for the entity type name.
     */
    @NotNull
    public final MutableText name;

    /**
     * The standard texture for the entity type.
     */
    @NotNull
    public HashMap<EntityTexture, Identifier> textures;

    /**
     * The standard model for the entity type.
     */
    @NotNull
    public final HashMap<EntityModel, Identifier> models;

    /**
     * The standard animator for the entity type.
     */
    @NotNull
    public final HashMap<EntityAnimator, Identifier> animators;

    /**
     * The foods for the entity type.
     */
    @NotNull
    public final Set<Item> foods = new HashSet<>();

    /**
     * The items to be tempted by the entity type.
     */
    @NotNull
    public final Set<Item> temptingItems = new HashSet<>();

    /**
     * The standard sounds for the entity type.
     */
    @NotNull
    public HashMap<EntitySound, SoundEvent> sounds;

    /**
     * The standard animations for the entity type.
     */
    @NotNull
    public HashMap<EntityAnimation, RawAnimation> animations;

    /**
     * The list of entities this entity type avoid attacking.
     */
    public final Predicate<LivingEntity> AvoidAttackingEntities = entity -> entity instanceof Monster && !(entity instanceof InternalEntity);

    // -- STATS

    /**
     * The bonus max level the entity type gives.
     */
    protected int maxLevel = 0;

    /**
     * The bonus max health the entity type gives.
     */
    protected float maxHealth = 10.0F;

    /**
     * The bonus attack damage the entity type gives.
     */
    protected float attackDamage = 0.5F;

    /**
     * The bonus attack speed the entity type gives.
     */
    protected float attackSpeed = 0.5F;

    /**
     * The bonus armour the entity type gives.
     */
    protected float armour = 0.5F;

    /**
     * The bonus armour toughness the entity type gives.
     */
    protected float armourToughness = 0.5F;

    /**
     * The bonus knockback resistance the entity type gives.
     */
    protected float knockbackResistance = 0.5F;

    /**
     * The bonus move speed the entity type gives.
     */
    protected float moveSpeed = 0.5F;

    // -- Getters & Setters --

    /**
     * @return the bonus max level the entity type gives.
     */
    public int getMaxLevel() {
        return maxLevel;
    } // getMaxLevel ()

    /**
     * @return the bonus max health the entity type gives.
     */
    public float getMaxHealth() {
        return maxHealth;
    } // getMaxHealth ()

    /**
     * @return the bonus attack damage the entity type gives.
     */
    public float getAttackDamage() {
        return attackDamage;
    } // getAttackDamage ()

    /**
     * @return the bonus attack speed the entity type gives.
     */
    public float getAttackSpeed() {
        return attackSpeed;
    } // getAttackSpeed ()

    /**
     * @return the bonus armour the entity type gives.
     */
    public float getArmour() {
        return armour;
    } // getArmour ()

    /**
     * @return the bonus armour toughness the entity type gives.
     */
    public float getArmourToughness() {
        return armourToughness;
    } // getArmourToughness ()

    /**
     * @return the bonus knockback resistance the entity type gives.
     */
    public float getKnockbackResistance() {
        return knockbackResistance;
    } // getKnockbackResistance ()

    /**
     * @return the bonus move speed the entity type gives.
     */
    public float getMoveSpeed() {
        return moveSpeed;
    } // getMoveSpeed ()

    /**
     * The Spawn Weight for the entity type.
     */
    public int spawnWeight = 0;

    /**
     * The Spawn Minimum Group texture for the entity type.
     */
    public int spawnMinGroup = 0;

    /**
     * The Spawn Maximum Group for the entity type.
     */
    public int spawnMaxGroup = 0;

    /**
     * The Spawn Biomes for the entity type.
     */
    public List<RegistryKey<Biome>> spawnBiomes = new ArrayList<>();

    // -- Constructor --

    public InternalEntityType(@NotNull String key, EntityNative entityNative) {
        this.key = key;
        this.name = MonstersGirlsID.getTranslation(MonstersGirlsID.VARIANT_PREFIX, key);
        this.nativeEntity = entityNative;
        this.textures = setTexture(Objects.requireNonNull(key));
        this.models = setModel();
        this.animators = setAnimator();
        this.animations = setAnimation();
        this.sounds = setSound(SoundEvents.ENTITY_GENERIC_WIND_BURST);
    } // Constructor InternalEntityType ()

    // -- Custom Methods --

    /**
     * Sets the combat stats for the entity type.
     *
     * @param maxHealth           the bonus max health the entity type gives.
     * @param attackDamage        the bonus attack damage health the entity type gives.
     * @param attackSpeed         the bonus attack speed health the entity type gives.
     * @param armour              the bonus armour the entity type gives.
     * @param armourToughness     the bonus armour toughness health the entity type gives.
     * @param knockbackResistance the bonus knockback resistance health the entity type gives.
     * @param moveSpeed           the bonus move speed the entity type gives.
     */
    @NotNull
    protected T addCombat(float maxHealth, float attackDamage, float attackSpeed, float armour, float armourToughness, float knockbackResistance, float moveSpeed) {
        this.maxHealth = maxHealth;
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.armour = armour;
        this.armourToughness = armourToughness;
        this.knockbackResistance = knockbackResistance;
        this.moveSpeed = moveSpeed;
        return (T)this;
    } // addCombat ()

    /**
     * Adds textures to the InternalEntityType.
     *
     * @param overrideDefault indicates whether to override the default texture.
     * @param textureSet      the set of textures to add.
     * @return the updated InternalEntityType instance.
     */
    protected T addTextures(boolean overrideDefault, EntityTexture... textureSet) {
        // Construct the path for the textures
        var path = "textures/entity/" + this.nativeEntity.getName() + "/" + key;                 // Example: "textures/entity/wisp/wisp_girl_blue"
        // Map to store the textures
        this.textures = new HashMap<>() {{
            // Loop through the given texture set
            for (EntityTexture texture : textureSet) {
                // Switch based on the texture type
                switch (texture) {
                    case SLIM -> put(EntityTexture.SLIM,                MonstersGirlsID.getId(path + "_slim.png"));      // Slim
                    case DEFAULT -> {
                        // Add default texture based on override condition
                        if(overrideDefault) put(EntityTexture.DEFAULT,  MonstersGirlsID.getId(path + "_default.png"));   // Default
                        else put(EntityTexture.DEFAULT,                 MonstersGirlsID.getId(path + ".png"));           // Default
                    }
                    case TUMMY -> put(EntityTexture.TUMMY,              MonstersGirlsID.getId(path + "_tummy.png"));     // Tummy
                    case INFLATED -> put(EntityTexture.INFLATED,        MonstersGirlsID.getId(path + "_inflated.png"));  // Inflated
                    case CHUNKY -> put(EntityTexture.CHUNKY,            MonstersGirlsID.getId(path + "_chunky.png"));    // Chunky
                }
            }
        }};
        return (T)this;
    } // addTextures ()

    /**
     * Adds animations to the InternalEntityType entity based on the provided animation set.
     *
     * @param animationSet the set of animations to add
     * @return the updated InternalEntityType instance with animations added
     */
    protected T addAnimations(EntityAnimation... animationSet) {
        // Map to store the animations
        this.animations = new HashMap<>() {{
            // Loop through the given animation set
            for (EntityAnimation animation : animationSet) {
                // Switch based on the animation type
                switch (animation) {
                    case IDLE -> put(EntityAnimation.IDLE,          RawAnimation.begin().thenLoop(EntityAnimation.IDLE.getName()));                                 // Idle
                    case WALK -> put(EntityAnimation.WALK,          RawAnimation.begin().thenLoop(EntityAnimation.WALK.getName()));                                 // Walk
                    case ATTACK -> put(EntityAnimation.ATTACK,      RawAnimation.begin().then(EntityAnimation.ATTACK.getName(), Animation.LoopType.PLAY_ONCE));     // Attack
                    case INTERACT -> put(EntityAnimation.INTERACT,  RawAnimation.begin().then(EntityAnimation.INTERACT.getName(), Animation.LoopType.PLAY_ONCE));   // Interact
                    case REST -> put(EntityAnimation.REST,          RawAnimation.begin().thenLoop(EntityAnimation.REST.getName()));                                 // Rest
                    case WAVE -> put(EntityAnimation.WAVE,          RawAnimation.begin().then(EntityAnimation.WAVE.getName(), Animation.LoopType.PLAY_ONCE));       // Wave
                    case HURT -> put(EntityAnimation.HURT,          RawAnimation.begin().then(EntityAnimation.HURT.getName(), Animation.LoopType.PLAY_ONCE));       // Hurt
                }
            }
        }};
        return (T)this;
    } // addAnimations ()

    /**
     * Adds the given items as foods to variant type.
     *
     * @param items the items to add as foods.
     */
    protected T addFoods(@NotNull Item... items) {
        this.foods.addAll(Arrays.asList(items));
        return (T)this;
    } // addFoods ()

    /**
     * Adds the tempting items to variant type.
     *
     * @param items the items to be tempted with.
     */
    protected T addTemptingItems(@NotNull Item... items) {
        this.temptingItems.addAll(Arrays.asList(items));
        return (T)this;
    } // addTemptingItems ()

    /**
     * Adds spawn information to the InternalEntityType entity.
     *
     * @param weight    the weight of the spawn.
     * @param minGroup  the minimum group size for spawning.
     * @param maxGroup  the maximum group size for spawning.
     * @param biomes    the biomes where the entity can spawn.
     * @return the updated InternalEntityType instance with spawn information added.
     */
    protected T addSpawn(int weight, int minGroup, int maxGroup, RegistryKey<Biome>... biomes) {
        this.spawnWeight = weight;
        this.spawnMinGroup = minGroup;
        this.spawnMaxGroup = maxGroup;
        this.spawnBiomes = List.of(biomes);
        return (T)this;
    } // addAnimations ()

    // TEXTURE

    protected HashMap<EntityTexture, Identifier> setTexture(String variant){
        var path = "textures/entity/" + this.nativeEntity.getName() + "/" + variant;
        return new HashMap<>() {{
            put(EntityTexture.DEFAULT, MonstersGirlsID.getId(path + variant + ".png"));
        }};
    } // setTexture ()

    public Identifier getTexture() {
        EntityTexture randomTexture = EntityTexture.byId(getRandomTextureID());
        if (checkTexture(randomTexture)) return textures.get(randomTexture);
        return textures.get(EntityTexture.DEFAULT);
    } // getTexture ()

    public Identifier getTexture(EntityTexture texture) {
        return checkTexture(texture) ? this.textures.get(texture) : getTexture();
    } // getTexture ()

    public boolean checkTexture(EntityTexture texture) {
        return this.textures.containsKey(texture);
    } // checkTexture ()

    public int getRandomTextureID() {
        List<EntityTexture> textures = this.textures.keySet().stream().toList();
        if (!textures.isEmpty()) {
            EntityTexture randomTexture = textures.get(new Random().nextInt(0, textures.size()));
            return randomTexture.getId(); // Replace with the appropriate method to get the ID
        }

        // Return a default or error ID if no valid variant is found
        return EntityTexture.DEFAULT.getId();
    } // getRandomTextureID ()

    // MODEL

    protected HashMap<EntityModel, Identifier> setModel() {
        var path = "geo/" + this.nativeEntity.getName() + "_girl.geo.json";
        return new HashMap<>() {{
            put(EntityModel.DEFAULT,    MonstersGirlsID.getId(path));
        }};
    } // setModel ()

    public Identifier getModel() {
        EntityModel randomModel = EntityModel.byId(getRandomModelID());
        if (checkModel(randomModel)) return models.get(randomModel);
        return models.get(EntityModel.DEFAULT);
    } // getModel ()

    public Identifier getModel(EntityModel model) {
        return checkModel(model) ? this.models.get(model) : getModel();
    } // getModel ()

    public boolean checkModel(EntityModel model) {
        return this.models.containsKey(model);
    } // checkModel ()

    public int getRandomModelID() {
        List<EntityModel> models = this.models.keySet().stream().toList();
        if (!models.isEmpty()) {
            EntityModel randomModel = models.get(new Random().nextInt(0, models.size()));
            return randomModel.getId(); // Replace with the appropriate method to get the ID
        }

        // Return a default or error ID if no valid variant is found
        return EntityModel.DEFAULT.getId();
    } // getRandomModelID ()

    // ANIMATOR

    protected HashMap<EntityAnimator, Identifier> setAnimator() {
        var path = "animations/" + this.nativeEntity.getName() + "_girl.animation.json";
        return new HashMap<>() {{
            put(EntityAnimator.DEFAULT, MonstersGirlsID.getId(path));
        }};
    } // setAnimator ()

    public Identifier getAnimator() {
        EntityAnimator randomAnimator = EntityAnimator.byId(getRandomAnimatorID());
        if (checkAnimator(randomAnimator)) return animators.get(randomAnimator);
        return animators.get(EntityAnimator.DEFAULT);
    } // getAnimator ()

    public Identifier getAnimator(EntityAnimator animator) {
        return checkAnimator(animator) ? this.animators.get(animator) : getAnimator();
    } // getAnimator ()

    public boolean checkAnimator(EntityAnimator animator) {
        return this.animators.containsKey(animator);
    } // checkAnimator ()

    public int getRandomAnimatorID() {
        List<EntityAnimator> animators = this.animators.keySet().stream().toList();
        if (!animators.isEmpty()) {
            EntityAnimator randomAnimator = animators.get(new Random().nextInt(0, animators.size()));
            return randomAnimator.getId(); // Replace with the appropriate method to get the ID
        }

        // Return a default or error ID if no valid variant is found
        return EntityAnimator.DEFAULT.getId();
    } // getRandomAnimatorID ()

    // SOUNDS

    protected HashMap<EntitySound, SoundEvent> setSound(SoundEvent sound) {
        return new HashMap<>() {{
            put(EntitySound.DEFAULT, sound);
        }};
    } // setSound ()

    public SoundEvent getSound() {
        EntitySound randomSound = EntitySound.byId(getRandomSoundID());
        if (checkSound(randomSound)) return sounds.get(randomSound);
        return sounds.get(EntitySound.DEFAULT);
    } // getSound ()

    public SoundEvent getSound(EntitySound sound) {
        return checkSound(sound) ? this.sounds.get(sound) : getSound();
    } // getSound ()

    public boolean checkSound(EntitySound sound) {
        return this.sounds.containsKey(sound);
    } // checkSound ()

    public int getRandomSoundID() {
        List<EntitySound> entitySounds = sounds.keySet().stream().toList();
        if (!entitySounds.isEmpty()) {
            EntitySound randomSound = entitySounds.get(new Random().nextInt(entitySounds.size()));
            return randomSound.getId(); // Replace with the appropriate method to get the ID
        }

        // Return a default or error ID if no valid variant is found
        return 0;
    } // getRandomSoundID ()

    // ANIMATIONS

    protected HashMap<EntityAnimation, RawAnimation> setAnimation() {
        return new HashMap<>() {{
            put(EntityAnimation.IDLE,          RawAnimation.begin().thenLoop(EntityAnimation.IDLE.getName()));
        }};
    } // setAnimation ()

    public RawAnimation getAnimation() {
        EntityAnimation randomAnimation = EntityAnimation.byId(getRandomAnimationID());
        if (checkAnimation(randomAnimation)) return animations.get(randomAnimation);
        return animations.get(EntityAnimation.IDLE);
    } // getAnimation ()

    public RawAnimation getAnimation(EntityAnimation animation) {
        return checkAnimation(animation) ? this.animations.get(animation) : getAnimation();
    } // getAnimation ()

    public boolean checkAnimation(EntityAnimation animation) {
        return this.animations.containsKey(animation);
    } // checkAnimation ()

    public int getRandomAnimationID() {
        List<EntityAnimation> entityAnimations = animations.keySet().stream().toList();
        if (!entityAnimations.isEmpty()) {
            EntityAnimation randomAnimation = entityAnimations.get(new Random().nextInt(entityAnimations.size()));
            return randomAnimation.getId(); // Replace with the appropriate method to get the ID
        }

        // Return a default or error ID if no valid variant is found
        return 0;
    } // getRandomAnimationID ()

    // FOOD

    /**
     * @param stack the stack to check.
     * @return true if the stack is food for this variant type.
     */
    public boolean isFood(@NotNull ItemStack stack) {
        return isFood(stack.getItem());
    } // isFood ()

    /**
     * @param item the item to check.
     * @return true if the item is food for this variant type.
     */
    public boolean isFood(Item item) {
        return foods.contains(item);
    } // isFood ()

    public Ingredient getFood() {
        return Ingredient.ofItems(foods.toArray(new Item[0]));
    } // getFood ()

    // Tempting Items

    public Ingredient getTemptingItems() {
        return Ingredient.ofItems(temptingItems.toArray(new Item[0]));
    } // getTemptingItems ()

} // Class InternalEntityType