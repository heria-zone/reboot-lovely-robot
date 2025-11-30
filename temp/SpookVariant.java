package net.msymbios.monsters_girls.entity.internal;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.msymbios.monsters_girls.common.entity.InternalEntityType;
import net.msymbios.monsters_girls.config.MonstersGirlsID;
import net.msymbios.monsters_girls.entity.internal.enums.EntityAnimation;
import net.msymbios.monsters_girls.entity.internal.enums.EntityNative;
import net.msymbios.monsters_girls.entity.internal.enums.EntitySound;
import net.msymbios.monsters_girls.entity.internal.enums.EntityTexture;
import net.msymbios.monsters_girls.item.MonstersGirlsItems;
import net.msymbios.monsters_girls.sound.MonstersGirlsSounds;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * The Internal Variant contains properties used to determine textures, spawning and stats.
 * <p>
 * NOTE: If the key of anything type changes it must be reflected in the find method using the version.
 */
public class SpookVariant extends InternalEntityType<SpookVariant> {

    // -- Variables --

    /**
     * The list of all available variant entities.
     */
    public static final List<SpookVariant> VARIANTS = new ArrayList<>();

    public static final SpookVariant PEACH = create(MonstersGirlsID.SPOOK_PEACH).addCombat(20F, 3F, 1.2F, 0.1F, 0.2F, 0F, 0.9F);
    public static final SpookVariant TEAL = create(MonstersGirlsID.SPOOK_TEAL).addCombat(20F, 3F, 1.2F, 0.1F, 0.2F, 0F, 0.9F);

    static {
        // SPOOK TEAL
        TEAL.addSounds();
        TEAL.addAnimations(EntityAnimation.IDLE, EntityAnimation.WALK, EntityAnimation.REST, EntityAnimation.ATTACK);
        TEAL.addTextures(false, EntityTexture.DEFAULT, EntityTexture.TUMMY);
        TEAL.addFoods(MonstersGirlsItems.SPECTRAL_CAKE, MonstersGirlsItems.CANDIES);
        TEAL.addTemptingItems(Items.SOUL_LANTERN);

        // SPOOK PEACH
        PEACH.addSounds();
        PEACH.addAnimations(EntityAnimation.IDLE, EntityAnimation.WALK, EntityAnimation.REST, EntityAnimation.ATTACK);
        PEACH.addTextures(false, EntityTexture.DEFAULT, EntityTexture.TUMMY);
        PEACH.addFoods(Items.MELON_SLICE, Items.SWEET_BERRIES, MonstersGirlsItems.SPECTRAL_CAKE, MonstersGirlsItems.CANDIES);
        PEACH.addTemptingItems(Items.SOUL_LANTERN);
    }

    // -- Constructors --

    public SpookVariant(@NotNull String key) {
        super(key, EntityNative.SPOOK);
    } // Constructor SpookVariant ()

    // -- Custom Methods --

    /**
     * Creates and adds a new entity type to the list of entities types.
     *
     * @param key the key used to identify the entity type (for things like translation text components).
     * @return the instance of the entity type.
     */
    @NotNull
    protected static SpookVariant create(@NotNull String key) {
        SpookVariant entityNative = new SpookVariant(key);
        VARIANTS.add(entityNative);
        return entityNative;
    } // create ()

    /**
     * Adds default sounds to the WispVariant entity.
     *
     * @return the updated WispVariant instance with default sounds added.
     */
    protected SpookVariant addSounds() {
        // Initialize a new HashMap for storing sounds
        this.sounds = new HashMap<>() {{
            // Add default sounds for different actions
            put(EntitySound.DEFAULT,    MonstersGirlsSounds.SPOOK_LAUGH);       // Default
            put(EntitySound.HURT,       MonstersGirlsSounds.SPOOK_HURT);        // Hurt
            put(EntitySound.ATTACK,     MonstersGirlsSounds.SPOOK_ATTACK);      // Death
        }};
        return this;
    } // addSounds ()

    /**
     * @param stack the food stack.
     * @return the variant type that eats the given food.
     */
    @NotNull
    public static SpookVariant findByFood(@NotNull ItemStack stack) {
        return findByFood(stack.getItem());
    } // findByFood ()

    /**
     * @param item the food item.
     * @return the variant type that eats the given food.
     */
    @NotNull
    public static SpookVariant findByFood(@NotNull Item item) {
        return Objects.requireNonNull(VARIANTS.stream().filter(blocklingType -> blocklingType.isFood(item)).findFirst().orElse(null));
    } // findByFood ()

} // Class WispVariant