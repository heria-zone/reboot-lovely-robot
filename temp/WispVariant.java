package net.msymbios.monsters_girls.entity.internal;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.biome.BiomeKeys;
import net.msymbios.monsters_girls.common.entity.InternalEntityType;
import net.msymbios.monsters_girls.config.MonstersGirlsID;
import net.msymbios.monsters_girls.entity.internal.enums.*;
import net.msymbios.monsters_girls.sound.MonstersGirlsSounds;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * The Internal Variant contains properties used to determine textures, spawning and stats.
 * <p>
 * NOTE: If the key of anything type changes it must be reflected in the find method using the version.
 */
public class WispVariant extends InternalEntityType<WispVariant> {

    // -- Variables --

    /**
     * The list of all available variant entities.
     */
    public static final List<WispVariant> VARIANTS = new ArrayList<>();

    public static final WispVariant BLUE    = create(MonstersGirlsID.WISP_BLUE).addCombat(18F, 3F, 1.2F, 0.1F, 0.2F, 0F, 0.6F);
    public static final WispVariant GREEN   = create(MonstersGirlsID.WISP_GREEN).addCombat(18F, 3F, 1.2F, 0.1F, 0.2F, 0F, 0.6F);
    public static final WispVariant YELLOW  = create(MonstersGirlsID.WISP_YELLOW).addCombat(18F, 3F, 1.2F, 0.1F, 0.2F, 0F, 0.6F);

    static {
        // WISP BLUE
        BLUE.addSounds();
        BLUE.addAnimations(EntityAnimation.IDLE, EntityAnimation.WALK, EntityAnimation.REST, EntityAnimation.ATTACK);
        BLUE.addTextures(false,EntityTexture.DEFAULT);
        BLUE.addFoods(Items.GOLD_NUGGET, Items.GOLD_INGOT, Items.RAW_GOLD);
        BLUE.addTemptingItems(Items.GOLD_NUGGET, Items.GOLD_INGOT, Items.RAW_GOLD, Items.GOLD_BLOCK);
        BLUE.addSpawn(20, 1, 2, BiomeKeys.DARK_FOREST, BiomeKeys.SWAMP);

        // WISP GREEN
        GREEN.addSounds();
        GREEN.addAnimations(EntityAnimation.IDLE, EntityAnimation.WALK, EntityAnimation.REST, EntityAnimation.ATTACK);
        GREEN.addTextures(false,EntityTexture.DEFAULT);
        GREEN.addFoods(Items.GOLD_NUGGET, Items.GOLD_INGOT, Items.RAW_GOLD);
        GREEN.addTemptingItems(Items.GOLD_NUGGET, Items.GOLD_INGOT, Items.RAW_GOLD, Items.GOLD_BLOCK);
        GREEN.addSpawn(20, 1, 2, BiomeKeys.DARK_FOREST, BiomeKeys.SWAMP);

        // WISP YELLOW
        YELLOW.addSounds();
        YELLOW.addAnimations(EntityAnimation.IDLE, EntityAnimation.WALK, EntityAnimation.REST, EntityAnimation.ATTACK);
        YELLOW.addTextures(false, EntityTexture.DEFAULT, EntityTexture.TUMMY);
        YELLOW.addFoods(Items.GOLD_NUGGET, Items.GOLD_INGOT, Items.RAW_GOLD);
        YELLOW.addTemptingItems(Items.GOLD_NUGGET, Items.GOLD_INGOT, Items.RAW_GOLD, Items.GOLD_BLOCK);
        YELLOW.addSpawn(20, 1, 2, BiomeKeys.DARK_FOREST, BiomeKeys.SWAMP);
    }

    // -- Constructors --

    public WispVariant(@NotNull String key) {
        super(key, EntityNative.WISP);
    } // Constructor WispVariant ()

    // -- Custom Methods --

    /**
     * Creates and adds a new entity type to the list of entities types.
     *
     * @param key the key used to identify the entity type (for things like translation text components).
     * @return the instance of the entity type.
     */
    @NotNull
    protected static WispVariant create(@NotNull String key) {
        WispVariant entityNative = new WispVariant(key);
        VARIANTS.add(entityNative);
        return entityNative;
    } // create ()

    /**
     * Adds default sounds to the WispVariant entity.
     *
     * @return the updated WispVariant instance with default sounds added.
     */
    protected WispVariant addSounds() {
        // Initialize a new HashMap for storing sounds
        this.sounds = new HashMap<>() {{
            // Add default sounds for different actions
            put(EntitySound.DEFAULT,    MonstersGirlsSounds.WISP_LAUGH);    // Default
            put(EntitySound.HURT,       MonstersGirlsSounds.WISP_HURT);     // Hurt
            put(EntitySound.DEATH,      MonstersGirlsSounds.WISP_DEATH);    // Death
        }};
        return this;
    } // addSounds ()

    /**
     * @param stack the food stack.
     * @return the variant type that eats the given food.
     */
    @NotNull
    public static WispVariant findByFood(@NotNull ItemStack stack) {
        return findByFood(stack.getItem());
    } // findByFood ()

    /**
     * @param item the food item.
     * @return the variant type that eats the given food.
     */
    @NotNull
    public static WispVariant findByFood(@NotNull Item item) {
        return Objects.requireNonNull(VARIANTS.stream().filter(blocklingType -> blocklingType.isFood(item)).findFirst().orElse(null));
    } // findByFood ()

} // Class WispVariant