package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.internal.enums.EntityAnimator;
import net.msymbios.rlovelyr.entity.internal.enums.EntityModel;
import net.msymbios.rlovelyr.entity.internal.enums.EntityTexture;
import net.msymbios.rlovelyr.entity.internal.enums.EntityVariant;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;

/**
 * The Internal Entity Type contains properties used to determine textures, spawning and stats.
 * <p>
 * NOTE: If the key of anything type changes it must be reflected in the find method using the version.
 */
public abstract class InternalEntityType<T> {

    // -- Variables --

    /**
     * The key used to identify the entity type (for things like translation text components).
     */
    @Nonnull
    public final String key;

    /**
     * The translation text component for the entity type name.
     */
    @Nonnull
    public final TranslatableComponent name;

    /**
     * The standard texture for the entity type.
     */
    @Nonnull
    public final HashMap<EntityTexture, ResourceLocation> texture;

    /**
     * The standard model for the entity type.
     */
    @Nonnull
    public final HashMap<EntityModel, ResourceLocation> model;

    /**
     * The standard animator for the entity type.
     */
    @Nonnull
    public final HashMap<EntityAnimator, ResourceLocation> animator;

    public static Predicate<LivingEntity> AvoidAttackingEntities = entity -> entity instanceof Monster && !(entity instanceof Creeper) && !(entity instanceof InternalEntity);

    // -- STATS

    /**
     * The bonus max level the entity type gives.
     */
    protected int maxLevel = 0;

    /**
     * The bonus max health the entity type gives.
     */
    protected float maxHealth = 0.0F;

    /**
     * The bonus attack damage the entity type gives.
     */
    protected float attackDamage = 0.0F;

    /**
     * The bonus attack speed the entity type gives.
     */
    protected float attackSpeed = 0.0F;

    /**
     * The bonus armour the entity type gives.
     */
    protected float armour = 0.0F;

    /**
     * The bonus armour toughness the entity type gives.
     */
    protected float armourToughness = 0.0F;

    /**
     * The bonus knockback resistance the entity type gives.
     */
    protected float knockbackResistance = 0.0F;

    /**
     * The bonus move speed the entity type gives.
     */
    protected float moveSpeed = 0.0F;

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

    // -- Constructor --

    public InternalEntityType(@Nonnull String key) {
        this.key = key;
        this.name = LovelyRobotID.getTranslation(LovelyRobotID.VARIANT_PREFIX, key);
        this.texture = setTexture(Objects.requireNonNull(EntityVariant.byName(key)));
        this.model = setModel(Objects.requireNonNull(EntityVariant.byName(key)));
        this.animator = setAnimator(Objects.requireNonNull(EntityVariant.byName(key)));
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
    @Nonnull
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

    protected T addCombat(int maxLevel, float maxHealth, float attackDamage, float attackSpeed, float armour, float armourToughness, float knockbackResistance, float moveSpeed) {
        this.maxLevel = maxLevel;
        return addCombat(maxHealth, attackDamage, attackSpeed, armour, armourToughness, knockbackResistance, moveSpeed);
    } // addCombat ()

    // TEXTURE

    protected abstract HashMap<EntityTexture, ResourceLocation> setTexture(EntityVariant variant);

    public ResourceLocation getTexture() {
        EntityTexture randomTexture = EntityTexture.byId(getRandomTextureID());
        if (checkTexture(randomTexture)) return texture.get(randomTexture);
        return texture.get(EntityTexture.WHITE);
    } // getTexture ()

    public ResourceLocation getTexture(EntityTexture texture) {
        return checkTexture(texture) ? this.texture.get(texture) : getTexture();
    } // getTexture ()

    public boolean checkTexture(EntityTexture texture) {
        return this.texture.containsKey(texture);
    } // checkTexture ()

    public int getRandomTextureID() {
        List<EntityTexture> textures = texture.keySet().stream().toList();
        if (!textures.isEmpty()) {
            EntityTexture randomTexture = textures.get(new Random().nextInt(textures.size()));
            return randomTexture.getId(); // Replace with the appropriate method to get the ID
        }

        // Return a default or error ID if no valid variant is found
        return 0;
    } // getRandomTextureID ()

    // MODEL

    protected abstract HashMap<EntityModel, ResourceLocation> setModel(EntityVariant variant);

    public ResourceLocation getModel() {
        EntityModel defaultModel = EntityModel.Default; // Use the default model key, or adjust as needed
        if (model.containsKey(defaultModel)) return model.get(defaultModel);
        return null; // Return a default or error identifier if the combination is not found
    } // getModel ()

    public ResourceLocation getModel(EntityModel model) {
        if (this.model.containsKey(model)) return this.model.get(model);
        return this.model.get(EntityModel.Default);  // Return a default or error identifier if the combination is not found
    } // getModel ()

    public int getRandomModelID() {
        List<EntityModel> models = model.keySet().stream().toList();
        if (!models.isEmpty()) {
            EntityModel randomModel = models.get(new Random().nextInt(models.size()));
            return randomModel.getId();
        }

        // Return a default or error ID if no valid variant is found
        return 0;
    } // getRandomModelID ()

    // ANIMATOR

    protected abstract HashMap<EntityAnimator, ResourceLocation> setAnimator(EntityVariant variant);

    public ResourceLocation getAnimator() {
        return getAnimator(EntityAnimator.byId(getRandomAnimatorID()));
    } // getAnimator ()

    public ResourceLocation getAnimator(EntityAnimator animator) {
        if (this.animator.containsKey(animator)) return this.animator.get(animator);
        return null;
    } // getAnimator ()

    public int getRandomAnimatorID() {
        List<EntityAnimator> animators = animator.keySet().stream().toList();
        if (!animators.isEmpty()) {
            EntityAnimator randomAnimator = animators.get(new Random().nextInt(animators.size()));
            return randomAnimator.getId();
        }

        // Return a default or error ID if no valid variant is found
        return 0;
    } // getRandomAnimatorID ()

} // Class InternalEntityType