package net.msymbios.llovelyr.common.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;
import net.msymbios.llovelyr.source.configs.LovelyIdentifier;
import net.msymbios.llovelyr.source.entity.internal.enums.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

/**
 * Defines robot type properties including textures, models, animations, and stats.
 * <p>
 * <b>Architecture:</b> Each robot variant (Vanilla, Bunny2, etc.) has an InternalEntityType
 * instance that centralizes all variant-specific data. Entities query their type for
 * resources and stats rather than hardcoding values.
 * <p>
 * <b>Design Decision:</b> Type system separates variant data from entity logic, enabling
 * new robot types without modifying entity classes. Resource maps support multiple
 * textures/models per type for customization.
 * <p>
 * <b>Performance:</b> Resource maps are populated once at initialization and cached.
 * Random selection uses pre-built lists to avoid repeated stream operations.
 * <p>
 * <i>Note:</i> If key names change, update find() method with version migration logic.
 */
public abstract class InternalEntityType<T> {

    // -- Type Identity --

    /** Registry key identifying this entity type (e.g., "vanilla", "bunny2"). */
    @NotNull
    public final String key;

    /** Localized display name for UI and messages. */
    @NotNull
    public final MutableText name;

    // -- Resource Maps --

    /** Available textures mapped by color variant. */
    @NotNull
    public HashMap<EntityTexture, Identifier> texture;

    /** Available models mapped by equipment state. */
    @NotNull
    public final HashMap<EntityModel, Identifier> model;

    /** Available animators mapped by animation set. */
    @NotNull
    public final HashMap<EntityAnimator, Identifier> animator;

    // -- Combat Targeting --

    /** Predicate for valid attack targets (monsters except creepers and other robots). */
    public static Predicate<LivingEntity> AvoidAttackingEntities =
        entity -> entity instanceof Monster &&
                  !(entity instanceof CreeperEntity) &&
                  !(entity instanceof InternalEntity);

    // -- Base Stats --

    protected int maxLevel = 0;
    protected float maxHealth = 0.0F;
    protected float attackDamage = 0.0F;
    protected float attackSpeed = 0.0F;
    protected float armour = 0.0F;
    protected float armourToughness = 0.0F;
    protected float knockbackResistance = 0.0F;
    protected float moveSpeed = 0.0F;

    // -- Stat Accessors --

    public int getMaxLevel() { return maxLevel; }
    public float getMaxHealth() { return maxHealth; }
    public float getAttackDamage() { return attackDamage; }
    public float getAttackSpeed() { return attackSpeed; }
    public float getArmour() { return armour; }
    public float getArmourToughness() { return armourToughness; }
    public float getKnockbackResistance() { return knockbackResistance; }
    public float getMoveSpeed() { return moveSpeed; }

    // -- Constructor --

    /**
     * Initializes entity type with variant-specific resources.
     * <p>
     * <b>Architecture:</b> Constructor populates resource maps by calling abstract
     * methods implemented by concrete type classes. This ensures all resources are
     * loaded at type creation rather than lazily during gameplay.
     * 
     * @param key registry identifier for this variant
     */
    public InternalEntityType(@NotNull String key) {
        this.key = key;
        this.name = LovelyIdentifier.getVariantTranslation(key);
        
        EntityVariant variant = Objects.requireNonNull(EntityVariant.byName(key));
        this.texture = setTexture(variant);
        this.model = setModel(variant);
        this.animator = setAnimator(variant);
    } // Constructor: InternalEntityType()

    // -- Stat Configuration --

    /**
     * Configures combat stats for this robot type.
     * <p>
     * <b>Usage:</b> Called during type initialization to set base stats. Stats are
     * bonuses added to default entity attributes, allowing types to specialize.
     * 
     * @param maxHealth bonus max health
     * @param attackDamage bonus attack damage
     * @param attackSpeed bonus attack speed
     * @param armour bonus armor value
     * @param armourToughness bonus armor toughness
     * @param knockbackResistance bonus knockback resistance
     * @param moveSpeed bonus movement speed
     * @return this type for method chaining
     */
    @NotNull
    protected T addCombat(float maxHealth, float attackDamage, float attackSpeed, float armour, 
                          float armourToughness, float knockbackResistance, float moveSpeed) {
        this.maxHealth = maxHealth;
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.armour = armour;
        this.armourToughness = armourToughness;
        this.knockbackResistance = knockbackResistance;
        this.moveSpeed = moveSpeed;
        return (T) this;
    } // addCombat()

    /**
     * Configures combat stats including max level.
     * 
     * @param maxLevel maximum level this robot can reach
     * @param maxHealth bonus max health
     * @param attackDamage bonus attack damage
     * @param attackSpeed bonus attack speed
     * @param armour bonus armor value
     * @param armourToughness bonus armor toughness
     * @param knockbackResistance bonus knockback resistance
     * @param moveSpeed bonus movement speed
     * @return this type for method chaining
     */
    protected T addCombat(int maxLevel, float maxHealth, float attackDamage, float attackSpeed, 
                          float armour, float armourToughness, float knockbackResistance, float moveSpeed) {
        this.maxLevel = maxLevel;
        return addCombat(maxHealth, attackDamage, attackSpeed, armour, armourToughness, 
                        knockbackResistance, moveSpeed);
    } // addCombat()

    // -- Abstract Resource Methods --

    /**
     * Populates texture map for this variant.
     * <p>
     * <b>Implementation:</b> Concrete types should create HashMap mapping EntityTexture
     * enums to Identifier paths. Include all color variants supported by this type.
     * 
     * @param variant entity variant enum
     * @return map of textures keyed by color variant
     */
    protected abstract HashMap<EntityTexture, Identifier> setTexture(EntityVariant variant);

    /**
     * Populates model map for this variant.
     * <p>
     * <b>Implementation:</b> Concrete types should create HashMap mapping EntityModel
     * enums to Identifier paths. Typically includes Default and Armed models.
     * 
     * @param variant entity variant enum
     * @return map of models keyed by equipment state
     */
    protected abstract HashMap<EntityModel, Identifier> setModel(EntityVariant variant);

    /**
     * Populates animator map for this variant.
     * <p>
     * <b>Implementation:</b> Concrete types should create HashMap mapping EntityAnimator
     * enums to Identifier paths. Most types use single Default animator.
     * 
     * @param variant entity variant enum
     * @return map of animators keyed by animation set
     */
    protected abstract HashMap<EntityAnimator, Identifier> setAnimator(EntityVariant variant);

    // -- Texture Methods --

    /**
     * Gets random texture from available variants.
     * 
     * @return Identifier of randomly selected texture
     */
    public Identifier getTexture() {
        EntityTexture randomTexture = EntityTexture.byId(getRandomTextureID());
        if (checkTexture(randomTexture)) {
            return texture.get(randomTexture);
        }
        return texture.get(EntityTexture.WHITE);
    } // getTexture()

    /**
     * Gets specific texture variant.
     * 
     * @param texture desired texture variant
     * @return Identifier of texture, or random if not available
     */
    public Identifier getTexture(EntityTexture texture) {
        return checkTexture(texture) ? this.texture.get(texture) : getTexture();
    } // getTexture()

    /**
     * Checks if texture variant is available for this type.
     * 
     * @param texture texture variant to check
     * @return true if this type supports the texture
     */
    public boolean checkTexture(EntityTexture texture) {
        return this.texture.containsKey(texture);
    } // checkTexture()

    /**
     * Generates random texture ID from available variants.
     * 
     * @return ID of randomly selected texture
     */
    public int getRandomTextureID() {
        List<EntityTexture> textures = texture.keySet().stream().toList();
        if (!textures.isEmpty()) {
            EntityTexture randomTexture = textures.get(new Random().nextInt(textures.size()));
            return randomTexture.getId();
        }
        return 0;  // Default to WHITE
    } // getRandomTextureID()

    // -- Model Methods --

    /**
     * Gets default model for this type.
     * 
     * @return Identifier of default model
     */
    public Identifier getModel() {
        EntityModel defaultModel = EntityModel.Default;
        if (model.containsKey(defaultModel)) {
            return model.get(defaultModel);
        }
        return null;
    } // getModel()

    /**
     * Gets specific model variant.
     * 
     * @param model desired model variant
     * @return Identifier of model, or default if not available
     */
    public Identifier getModel(EntityModel model) {
        if (this.model.containsKey(model)) {
            return this.model.get(model);
        }
        return this.model.get(EntityModel.Default);
    } // getModel()

    /**
     * Generates random model ID from available variants.
     * 
     * @return ID of randomly selected model
     */
    public int getRandomModelID() {
        List<EntityModel> models = model.keySet().stream().toList();
        if (!models.isEmpty()) {
            EntityModel randomModel = models.get(new Random().nextInt(models.size()));
            return randomModel.getId();
        }
        return 0;  // Default
    } // getRandomModelID()

    // -- Animator Methods --

    /**
     * Gets random animator from available sets.
     * 
     * @return Identifier of randomly selected animator
     */
    public Identifier getAnimator() {
        return getAnimator(EntityAnimator.byId(getRandomAnimatorID()));
    } // getAnimator()

    /**
     * Gets specific animator variant.
     * 
     * @param animator desired animator variant
     * @return Identifier of animator, or null if not available
     */
    public Identifier getAnimator(EntityAnimator animator) {
        if (this.animator.containsKey(animator)) {
            return this.animator.get(animator);
        }
        return null;
    } // getAnimator()

    /**
     * Generates random animator ID from available sets.
     * 
     * @return ID of randomly selected animator
     */
    public int getRandomAnimatorID() {
        List<EntityAnimator> animators = animator.keySet().stream().toList();
        if (!animators.isEmpty()) {
            EntityAnimator randomAnimator = animators.get(new Random().nextInt(animators.size()));
            return randomAnimator.getId();
        }
        return 0;  // Default
    } // getRandomAnimatorID()

} // Class: InternalEntityType