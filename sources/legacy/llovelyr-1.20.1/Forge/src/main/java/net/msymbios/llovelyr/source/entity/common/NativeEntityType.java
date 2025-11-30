package net.msymbios.llovelyr.source.entity.common;

import net.minecraft.resources.ResourceLocation;
import net.msymbios.llovelyr.common.entity.internal.InternalEntityType;
import net.msymbios.llovelyr.source.LovelyConfigs;
import net.msymbios.llovelyr.common.shared.LovelyIdentifier;
import net.msymbios.llovelyr.common.entity.enums.EntityAnimator;
import net.msymbios.llovelyr.common.entity.enums.EntityModel;
import net.msymbios.llovelyr.framework.entity.enums.EntityTexture;
import net.msymbios.llovelyr.common.entity.enums.EntityVariant;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Native robot type definitions for Legacy variant.
 * <p>
 * <b>Architecture:</b> Defines available robot types (Vanilla, Bunny2) with their
 * textures, models, animations, and combat stats. Each type is initialized once
 * at mod load and cached for entity queries.
 * <p>
 * <b>Design Decision:</b> Static initialization block configures stats from config
 * values, allowing server admins to balance robot types without code changes.
 * Texture maps support 16-color palette for customization.
 * <p>
 * <b>Legacy Variant:</b> Contains only original robot types (Vanilla, Bunny2)
 * without extended variants (Dragon, Neko, Kitsune). Maintains compatibility
 * with original LovelyRobot mod design.
 * <p>
 * <i>Note:</i> If variant keys change, update find() method with version migration
 * logic to preserve existing entity data.
 */
public class NativeEntityType extends InternalEntityType<NativeEntityType> {

    // -- Constants --

    /**
     * Registry of all available native robot types.
     * <p>
     * <b>Usage:</b> Iterated during entity registration and type lookup operations.
     */
    public static final List<NativeEntityType> NATIVES = new ArrayList<>();

    // -- Type Definitions --

    /** Alternative bunny design with different aesthetic. */
    public static final NativeEntityType BUNNY2 = create(LovelyIdentifier.VARIANT_BUNNY2);

    /** General-purpose companion with belt pouch and patrol abilities. */
    public static final NativeEntityType VANILLA = create(LovelyIdentifier.VARIANT_VANILLA);

    // -- Static Initialization --

    public static void register () {
        BUNNY2.addCombat(
                LovelyConfigs.Bunny2MaxLevel,
                LovelyConfigs.Bunny2MaxHealth,
                LovelyConfigs.Bunny2AttackDamage,
                LovelyConfigs.Bunny2AttackSpeed,
                LovelyConfigs.Bunny2Armor,
                LovelyConfigs.Bunny2ArmorToughness,
                0.1F,
                LovelyConfigs.Bunny2MovementSpeed
        );

        // Configure Vanilla stats from config
        VANILLA.addCombat(
                LovelyConfigs.VanillaMaxLevel,
                LovelyConfigs.VanillaMaxHealth,
                LovelyConfigs.VanillaAttackDamage,
                LovelyConfigs.VanillaAttackSpeed,
                LovelyConfigs.VanillaArmor,
                LovelyConfigs.VanillaArmorToughness,
                0F,
                LovelyConfigs.VanillaMovementSpeed
        );
    } // register ()

    // -- Constructor --

    /**
     * Creates native entity type with variant key.
     * <p>
     * <b>Private:</b> Use create() factory method to ensure types are registered
     * in NATIVES list.
     *
     * @param key variant identifier (e.g., "vanilla", "bunny2")
     */
    private NativeEntityType(@Nonnull String key) {
        super(key);
    } // Constructor: NativeEntityType

    // -- Factory Method --

    /**
     * Creates and registers new native entity type.
     * <p>
     * <b>Registration:</b> Automatically adds type to NATIVES list for iteration
     * during entity registration and lookup operations.
     *
     * @param key variant identifier for translation and resource paths
     * @return newly created and registered entity type
     */
    @Nonnull
    protected static NativeEntityType create(@Nonnull String key) {
        NativeEntityType entityNative = new NativeEntityType(key);
        NATIVES.add(entityNative);
        return entityNative;
    } // create

    // -- Resource Methods --

    /**
     * Populates texture map with 16-color palette for variant.
     * <p>
     * <b>Path Convention:</b> textures/entity/{variant}/{variant}_{colorID}.png
     * where colorID is 00-15 for Minecraft's 16 dye colors.
     * <p>
     * <b>Color Mapping:</b> WHITE=00, ORANGE=01, MAGENTA=02, LIGHT_BLUE=03,
     * YELLOW=04, LIME=05, PINK=06, GRAY=07, LIGHT_GRAY=08, CYAN=09,
     * PURPLE=10, BLUE=11, BROWN=12, GREEN=13, RED=14, BLACK=15.
     *
     * @param variant entity variant enum
     * @return map of textures keyed by color variant
     */
    @Override
    protected HashMap<EntityTexture, ResourceLocation> setTexture(EntityVariant variant) {
        String path = variant.getName() + "/" + variant.getName();
        return new HashMap<>() {{
            put(EntityTexture.WHITE,         LovelyIdentifier.getId("textures/entity/" + path + "_00.png"));
            put(EntityTexture.ORANGE,        LovelyIdentifier.getId("textures/entity/" + path + "_01.png"));
            put(EntityTexture.MAGENTA,       LovelyIdentifier.getId("textures/entity/" + path + "_02.png"));
            put(EntityTexture.LIGHT_BLUE,    LovelyIdentifier.getId("textures/entity/" + path + "_03.png"));
            put(EntityTexture.YELLOW,        LovelyIdentifier.getId("textures/entity/" + path + "_04.png"));
            put(EntityTexture.LIME,          LovelyIdentifier.getId("textures/entity/" + path + "_05.png"));
            put(EntityTexture.PINK,          LovelyIdentifier.getId("textures/entity/" + path + "_06.png"));
            put(EntityTexture.GRAY,          LovelyIdentifier.getId("textures/entity/" + path + "_07.png"));
            put(EntityTexture.LIGHT_GRAY,    LovelyIdentifier.getId("textures/entity/" + path + "_08.png"));
            put(EntityTexture.CYAN,          LovelyIdentifier.getId("textures/entity/" + path + "_09.png"));
            put(EntityTexture.PURPLE,        LovelyIdentifier.getId("textures/entity/" + path + "_10.png"));
            put(EntityTexture.BLUE,          LovelyIdentifier.getId("textures/entity/" + path + "_11.png"));
            put(EntityTexture.BROWN,         LovelyIdentifier.getId("textures/entity/" + path + "_12.png"));
            put(EntityTexture.GREEN,         LovelyIdentifier.getId("textures/entity/" + path + "_13.png"));
            put(EntityTexture.RED,           LovelyIdentifier.getId("textures/entity/" + path + "_14.png"));
            put(EntityTexture.BLACK,         LovelyIdentifier.getId("textures/entity/" + path + "_15.png"));
        }};
    } // setTexture

    /**
     * Populates model map with default and armed variants.
     * <p>
     * <b>Path Convention:</b> geo/{variant}.geo.json for default model,
     * geo/{variant}.attack.geo.json for armed model.
     * <p>
     * <b>Model Switching:</b> Armed model displays when robot has weapon equipped,
     * showing visual feedback of combat readiness.
     *
     * @param variant entity variant enum
     * @return map of models keyed by equipment state
     */
    @Override
    protected HashMap<EntityModel, ResourceLocation> setModel(EntityVariant variant) {
        return new HashMap<>() {{
            put(EntityModel.Default,    LovelyIdentifier.getId("geo/" + variant.getName() + ".geo.json"));
            put(EntityModel.Armed,      LovelyIdentifier.getId("geo/" + variant.getName() + ".attack.geo.json"));
        }};
    } // setModel

    /**
     * Populates animator map with default animation set.
     * <p>
     * <b>Path Convention:</b> animations/default.animation.json
     * <p>
     * <b>Shared Animations:</b> All Legacy robots use same animation set for
     * consistency. Includes idle, walk, attack, sit, and death animations.
     *
     * @param variant entity variant enum
     * @return map of animators keyed by animation set
     */
    @Override
    protected HashMap<EntityAnimator, ResourceLocation> setAnimator(EntityVariant variant) {
        return new HashMap<>() {{
            put(EntityAnimator.Default, LovelyIdentifier.getId("animations/default.animation.json"));
        }};
    } // setAnimator

} // Class: NativeEntityType
