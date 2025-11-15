package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.internal.enums.EntityAnimator;
import net.msymbios.rlovelyr.entity.internal.enums.EntityModel;
import net.msymbios.rlovelyr.entity.internal.enums.EntityTexture;
import net.msymbios.rlovelyr.entity.internal.enums.EntityVariant;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The Internal Variant contains properties used to determine textures, spawning and stats.
 * <p>
 * NOTE: If the key of anything type changes it must be reflected in the find method using the version.
 */
public class NativeEntity extends InternalEntityType<NativeEntity> {

    // -- Variables --

    /**
     * The list of all available natives entities.
     */
    public static final List<NativeEntity> NATIVES = new ArrayList<>();

    public static final NativeEntity BUNNY      = create(LovelyRobotID.VARIANT_BUNNY).addCombat(LovelyRobotConfig.COMMON.bunnyAttributeMaxLevel.get(),LovelyRobotConfig.COMMON.bunnyAttributeMaxHealth.get().floatValue(), LovelyRobotConfig.COMMON.bunnyAttributeAttackDamage.get().floatValue(), LovelyRobotConfig.COMMON.bunnyAttributeAttackSpeed.get().floatValue(), LovelyRobotConfig.COMMON.bunnyAttributeArmor.get().floatValue(), LovelyRobotConfig.COMMON.bunnyAttributeArmorToughness.get().floatValue(), 0F, LovelyRobotConfig.COMMON.bunnyAttributeMovementSpeed.get().floatValue());
    public static final NativeEntity BUNNY2     = create(LovelyRobotID.VARIANT_BUNNY2).addCombat(LovelyRobotConfig.COMMON.bunny2AttributeMaxLevel.get(),LovelyRobotConfig.COMMON.bunny2AttributeMaxHealth.get().floatValue(), LovelyRobotConfig.COMMON.bunny2AttributeAttackDamage.get().floatValue(), LovelyRobotConfig.COMMON.bunny2AttributeAttackSpeed.get().floatValue(), LovelyRobotConfig.COMMON.bunny2AttributeArmor.get().floatValue(), LovelyRobotConfig.COMMON.bunny2AttributeArmorToughness.get().floatValue(), 0F, LovelyRobotConfig.COMMON.bunny2AttributeMovementSpeed.get().floatValue());
    public static final NativeEntity DRAGON     = create(LovelyRobotID.VARIANT_DRAGON).addCombat(LovelyRobotConfig.COMMON.dragonAttributeMaxLevel.get(),LovelyRobotConfig.COMMON.dragonAttributeMaxHealth.get().floatValue(), LovelyRobotConfig.COMMON.dragonAttributeAttackDamage.get().floatValue(), LovelyRobotConfig.COMMON.dragonAttributeAttackSpeed.get().floatValue(), LovelyRobotConfig.COMMON.dragonAttributeArmor.get().floatValue(), LovelyRobotConfig.COMMON.dragonAttributeArmorToughness.get().floatValue(), 0F, LovelyRobotConfig.COMMON.dragonAttributeMovementSpeed.get().floatValue());
    public static final NativeEntity HONEY      = create(LovelyRobotID.VARIANT_HONEY).addCombat(LovelyRobotConfig.COMMON.honeyAttributeMaxLevel.get(),LovelyRobotConfig.COMMON.honeyAttributeMaxHealth.get().floatValue(), LovelyRobotConfig.COMMON.honeyAttributeAttackDamage.get().floatValue(), LovelyRobotConfig.COMMON.honeyAttributeAttackSpeed.get().floatValue(), LovelyRobotConfig.COMMON.honeyAttributeArmor.get().floatValue(), LovelyRobotConfig.COMMON.honeyAttributeArmorToughness.get().floatValue(), 0F, LovelyRobotConfig.COMMON.honeyAttributeMovementSpeed.get().floatValue());
    public static final NativeEntity KITSUNE    = create(LovelyRobotID.VARIANT_KITSUNE).addCombat(LovelyRobotConfig.COMMON.kitsuneAttributeMaxLevel.get(),LovelyRobotConfig.COMMON.kitsuneAttributeMaxHealth.get().floatValue(), LovelyRobotConfig.COMMON.kitsuneAttributeAttackDamage.get().floatValue(), LovelyRobotConfig.COMMON.kitsuneAttributeAttackSpeed.get().floatValue(), LovelyRobotConfig.COMMON.kitsuneAttributeArmor.get().floatValue(), LovelyRobotConfig.COMMON.kitsuneAttributeArmorToughness.get().floatValue(), 0F, LovelyRobotConfig.COMMON.kitsuneAttributeMovementSpeed.get().floatValue());
    public static final NativeEntity NEKO       = create(LovelyRobotID.VARIANT_NEKO).addCombat(LovelyRobotConfig.COMMON.nekoAttributeMaxLevel.get(),LovelyRobotConfig.COMMON.nekoAttributeMaxHealth.get().floatValue(), LovelyRobotConfig.COMMON.nekoAttributeAttackDamage.get().floatValue(), LovelyRobotConfig.COMMON.nekoAttributeAttackSpeed.get().floatValue(), LovelyRobotConfig.COMMON.nekoAttributeArmor.get().floatValue(), LovelyRobotConfig.COMMON.nekoAttributeArmorToughness.get().floatValue(), 0F, LovelyRobotConfig.COMMON.nekoAttributeMovementSpeed.get().floatValue());
    public static final NativeEntity VANILLA    = create(LovelyRobotID.VARIANT_VANILLA).addCombat(LovelyRobotConfig.COMMON.vanillaAttributeMaxLevel.get(),LovelyRobotConfig.COMMON.vanillaAttributeMaxHealth.get().floatValue(), LovelyRobotConfig.COMMON.vanillaAttributeAttackDamage.get().floatValue(), LovelyRobotConfig.COMMON.vanillaAttributeAttackSpeed.get().floatValue(), LovelyRobotConfig.COMMON.vanillaAttributeArmor.get().floatValue(), LovelyRobotConfig.COMMON.vanillaAttributeArmorToughness.get().floatValue(), 0F, LovelyRobotConfig.COMMON.vanillaAttributeMovementSpeed.get().floatValue());

    // -- Constructors --

    public NativeEntity(@Nonnull String key) {
        super(key);
    } // Constructor NativeEntity ()

    // -- Methods --

    /**
     * Creates and adds a new entity type to the list of entities types.
     *
     * @param key the key used to identify the entity type (for things like translation text components).
     * @return the instance of the entity type.
     */
    @Nonnull
    protected static NativeEntity create(@Nonnull String key) {
        NativeEntity entityNative = new NativeEntity(key);
        NATIVES.add(entityNative);
        return entityNative;
    } // create ()

    @Override
    protected HashMap<EntityTexture, ResourceLocation> setTexture(EntityVariant variant){
        String path = variant.getName() + "/" + variant.getName();
        return new HashMap<>() {{
            put(EntityTexture.WHITE,         LovelyRobotID.getId("textures/entity/" + path + "_00.png")); // White
            put(EntityTexture.ORANGE,        LovelyRobotID.getId("textures/entity/" + path + "_01.png")); // Orange
            put(EntityTexture.MAGENTA,       LovelyRobotID.getId("textures/entity/" + path + "_02.png")); // Magenta
            put(EntityTexture.LIGHT_BLUE,    LovelyRobotID.getId("textures/entity/" + path + "_03.png")); // Light Blue
            put(EntityTexture.YELLOW,        LovelyRobotID.getId("textures/entity/" + path + "_04.png")); // Yellow
            put(EntityTexture.LIME,          LovelyRobotID.getId("textures/entity/" + path + "_05.png")); // Lime
            put(EntityTexture.PINK,          LovelyRobotID.getId("textures/entity/" + path + "_06.png")); // Pink
            put(EntityTexture.GRAY,          LovelyRobotID.getId("textures/entity/" + path + "_07.png")); // Gray
            put(EntityTexture.LIGHT_GRAY,    LovelyRobotID.getId("textures/entity/" + path + "_08.png")); // Light Gray
            put(EntityTexture.CYAN,          LovelyRobotID.getId("textures/entity/" + path + "_09.png")); // Cyan
            put(EntityTexture.PURPLE,        LovelyRobotID.getId("textures/entity/" + path + "_10.png")); // Purple
            put(EntityTexture.BLUE,          LovelyRobotID.getId("textures/entity/" + path + "_11.png")); // Blue
            put(EntityTexture.BROWN,         LovelyRobotID.getId("textures/entity/" + path + "_12.png")); // Brown
            put(EntityTexture.GREEN,         LovelyRobotID.getId("textures/entity/" + path + "_13.png")); // Green
            put(EntityTexture.RED,           LovelyRobotID.getId("textures/entity/" + path + "_14.png")); // Red
            put(EntityTexture.BLACK,         LovelyRobotID.getId("textures/entity/" + path + "_15.png")); // Black
        }};
    } // setTexture ()

    @Override
    protected HashMap<EntityModel, ResourceLocation> setModel(EntityVariant variant){
        return new HashMap<>() {{
            put(EntityModel.Default,    LovelyRobotID.getId("geo/" + variant.getName() + ".geo.json"));
            put(EntityModel.Armed,      LovelyRobotID.getId("geo/" + variant.getName() + ".attack.geo.json"));
        }};
    } // setModel ()

    @Override
    protected HashMap<EntityAnimator, ResourceLocation> setAnimator(EntityVariant variant){
        return new HashMap<>() {{
            put(EntityAnimator.Default, LovelyRobotID.getId("animations/default.animation.json"));
        }};
    } // setAnimator ()

} // Class NativeEntity