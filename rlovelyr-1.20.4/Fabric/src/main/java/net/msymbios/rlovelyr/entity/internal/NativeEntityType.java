package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.common.entity.InternalEntityType;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.internal.enums.EntityAnimator;
import net.msymbios.rlovelyr.entity.internal.enums.EntityModel;
import net.msymbios.rlovelyr.entity.internal.enums.EntityTexture;
import net.msymbios.rlovelyr.entity.internal.enums.EntityVariant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The Internal Variant contains properties used to determine textures, spawning and stats.
 * <p>
 * NOTE: If the key of anything type changes it must be reflected in the find method using the version.
 */
public class NativeEntityType extends InternalEntityType<NativeEntityType> {

    // -- Variables --

    /**
     * The list of all available natives entities.
     */
    public static final List<NativeEntityType> NATIVES = new ArrayList<>();

    public static final NativeEntityType BUNNY      = create(LovelyRobotID.VARIANT_BUNNY);
    public static final NativeEntityType BUNNY2     = create(LovelyRobotID.VARIANT_BUNNY2);
    public static final NativeEntityType DRAGON     = create(LovelyRobotID.VARIANT_DRAGON);
    public static final NativeEntityType HONEY      = create(LovelyRobotID.VARIANT_HONEY);
    public static final NativeEntityType KITSUNE    = create(LovelyRobotID.VARIANT_KITSUNE);
    public static final NativeEntityType NEKO       = create(LovelyRobotID.VARIANT_NEKO);
    public static final NativeEntityType VANILLA    = create(LovelyRobotID.VARIANT_VANILLA);
    public static final NativeEntityType PRIME      = create(LovelyRobotID.VARIANT_PRIME);
    public static final NativeEntityType HYPERION   = create(LovelyRobotID.VARIANT_HYPERION);
    public static final NativeEntityType EMPYRIUM   = create(LovelyRobotID.VARIANT_EMPYRIUM);
    public static final NativeEntityType STINGER   = create(LovelyRobotID.VARIANT_STINGER);

    static {
        BUNNY.addCombat(LovelyRobotConfig.Common.BunnyMaxLevel,LovelyRobotConfig.Common.BunnyMaxHealth, LovelyRobotConfig.Common.BunnyAttackDamage, LovelyRobotConfig.Common.BunnyAttackSpeed, LovelyRobotConfig.Common.BunnyArmor, LovelyRobotConfig.Common.BunnyArmorToughness, 0F, LovelyRobotConfig.Common.BunnyMovementSpeed);
        BUNNY2.addCombat(LovelyRobotConfig.Common.Bunny2MaxLevel,LovelyRobotConfig.Common.Bunny2MaxHealth, LovelyRobotConfig.Common.Bunny2AttackDamage, LovelyRobotConfig.Common.Bunny2AttackSpeed, LovelyRobotConfig.Common.Bunny2Armor, LovelyRobotConfig.Common.Bunny2ArmorToughness, 0F, LovelyRobotConfig.Common.Bunny2MovementSpeed);
        DRAGON.addCombat(LovelyRobotConfig.Common.DragonMaxLevel,LovelyRobotConfig.Common.DragonMaxHealth, LovelyRobotConfig.Common.DragonAttackDamage, LovelyRobotConfig.Common.DragonAttackSpeed, LovelyRobotConfig.Common.DragonArmor, LovelyRobotConfig.Common.DragonArmorToughness, 0F, LovelyRobotConfig.Common.DragonMovementSpeed);
        HONEY.addCombat(LovelyRobotConfig.Common.HoneyMaxLevel,LovelyRobotConfig.Common.HoneyMaxHealth, LovelyRobotConfig.Common.HoneyAttackDamage, LovelyRobotConfig.Common.HoneyAttackSpeed, LovelyRobotConfig.Common.HoneyArmor, LovelyRobotConfig.Common.HoneyArmorToughness, 0F, LovelyRobotConfig.Common.HoneyMovementSpeed);
        KITSUNE.addCombat(LovelyRobotConfig.Common.KitsuneMaxLevel,LovelyRobotConfig.Common.KitsuneMaxHealth, LovelyRobotConfig.Common.KitsuneAttackDamage, LovelyRobotConfig.Common.KitsuneAttackSpeed, LovelyRobotConfig.Common.KitsuneArmor, LovelyRobotConfig.Common.KitsuneArmorToughness, 0F, LovelyRobotConfig.Common.KitsuneMovementSpeed);
        NEKO.addCombat(LovelyRobotConfig.Common.NekoMaxLevel,LovelyRobotConfig.Common.NekoMaxHealth, LovelyRobotConfig.Common.NekoAttackDamage, LovelyRobotConfig.Common.NekoAttackSpeed, LovelyRobotConfig.Common.NekoArmor, LovelyRobotConfig.Common.NekoArmorToughness, 0F, LovelyRobotConfig.Common.NekoMovementSpeed);
        VANILLA.addCombat(LovelyRobotConfig.Common.VanillaMaxLevel,LovelyRobotConfig.Common.VanillaMaxHealth, LovelyRobotConfig.Common.VanillaAttackDamage, LovelyRobotConfig.Common.VanillaAttackSpeed, LovelyRobotConfig.Common.VanillaArmor, LovelyRobotConfig.Common.VanillaArmorToughness, 0F, LovelyRobotConfig.Common.VanillaMovementSpeed);

        PRIME.addCombat(LovelyRobotConfig.Common.VanillaMaxLevel,LovelyRobotConfig.Common.VanillaMaxHealth, LovelyRobotConfig.Common.VanillaAttackDamage, LovelyRobotConfig.Common.VanillaAttackSpeed, LovelyRobotConfig.Common.VanillaArmor, LovelyRobotConfig.Common.VanillaArmorToughness, 0F, LovelyRobotConfig.Common.VanillaMovementSpeed);
        PRIME.addTexture(EntityVariant.Prime, EntityTexture.DARK_MATTER, EntityTexture.SUPERNOVA, EntityTexture.COLD_GOLD, EntityTexture.EMBRYON, EntityTexture.DARK_GOLD, EntityTexture.GOLD_MATTER, EntityTexture.HESTIA);

        HYPERION.addCombat(LovelyRobotConfig.Common.VanillaMaxLevel,LovelyRobotConfig.Common.VanillaMaxHealth, LovelyRobotConfig.Common.VanillaAttackDamage, LovelyRobotConfig.Common.VanillaAttackSpeed, LovelyRobotConfig.Common.VanillaArmor, LovelyRobotConfig.Common.VanillaArmorToughness, 0F, LovelyRobotConfig.Common.VanillaMovementSpeed);
        HYPERION.addTexture(EntityVariant.Hyperion, EntityTexture.COMMANDER, EntityTexture.VALKYRIE);

        EMPYRIUM.addCombat(LovelyRobotConfig.Common.VanillaMaxLevel,LovelyRobotConfig.Common.VanillaMaxHealth, LovelyRobotConfig.Common.VanillaAttackDamage, LovelyRobotConfig.Common.VanillaAttackSpeed, LovelyRobotConfig.Common.VanillaArmor, LovelyRobotConfig.Common.VanillaArmorToughness, 0F, LovelyRobotConfig.Common.VanillaMovementSpeed);
        EMPYRIUM.addTexture(EntityVariant.Empyrium, EntityTexture.COLD_GOLD);

        STINGER.addCombat(LovelyRobotConfig.Common.VanillaMaxLevel,LovelyRobotConfig.Common.VanillaMaxHealth, LovelyRobotConfig.Common.VanillaAttackDamage, LovelyRobotConfig.Common.VanillaAttackSpeed, LovelyRobotConfig.Common.VanillaArmor, LovelyRobotConfig.Common.VanillaArmorToughness, 0F, LovelyRobotConfig.Common.VanillaMovementSpeed);
        STINGER.addTexture(EntityVariant.Stinger, EntityTexture.BLUE);
    }
    
    // -- Constructors --

    public NativeEntityType(String key) {
        super(key);
    } // Constructor NativeEntityType ()

    // -- Methods --

    /**
     * Creates and adds a new entity type to the list of entities types.
     *
     * @param key the key used to identify the entity type (for things like translation text components).
     * @return the instance of the entity type.
     */
    protected static NativeEntityType create(String key) {
        NativeEntityType entityNative = new NativeEntityType(key);
        NATIVES.add(entityNative);
        return entityNative;
    } // create ()

    protected NativeEntityType addTexture(EntityVariant variant, EntityTexture... textureSet) {
        // Construct the path for the textures
        String path = LovelyRobotID.TEXTURE_ENTITY_PATH + variant.getName() + "/" + variant.getName();
        // Map to store the textures
        this.texture.clear();
        this.texture = new HashMap<>() {{
            // Loop through the given texture set
            for (EntityTexture texture : textureSet) {
                // Switch based on the texture type
                switch (texture) {
                    case WHITE      -> put(texture,         LovelyRobotID.getId(path + "_00.png")); // White
                    case ORANGE     -> put(texture,         LovelyRobotID.getId(path + "_01.png")); // Orange
                    case MAGENTA    -> put(texture,         LovelyRobotID.getId(path + "_02.png")); // Magenta
                    case LIGHT_BLUE -> put(texture,         LovelyRobotID.getId(path + "_03.png")); // Light Blue
                    case YELLOW     -> put(texture,         LovelyRobotID.getId(path + "_04.png")); // Yellow
                    case LIME       -> put(texture,         LovelyRobotID.getId(path + "_05.png")); // Lime
                    case PINK       -> put(texture,         LovelyRobotID.getId(path + "_06.png")); // Pink
                    case GRAY       -> put(texture,         LovelyRobotID.getId(path + "_07.png")); // Gray
                    case LIGHT_GRAY -> put(texture,         LovelyRobotID.getId(path + "_08.png")); // Light Gray
                    case CYAN       -> put(texture,         LovelyRobotID.getId(path + "_09.png")); // Cyan
                    case PURPLE     -> put(texture,         LovelyRobotID.getId(path + "_10.png")); // Purple
                    case BLUE       -> put(texture,         LovelyRobotID.getId(path + "_11.png")); // Blue
                    case BROWN      -> put(texture,         LovelyRobotID.getId(path + "_12.png")); // Brown
                    case GREEN      -> put(texture,         LovelyRobotID.getId(path + "_13.png")); // Green
                    case RED        -> put(texture,         LovelyRobotID.getId(path + "_14.png")); // Red
                    case BLACK      -> put(texture,         LovelyRobotID.getId(path + "_15.png")); // Black

                    case DARK_MATTER    -> put(texture, LovelyRobotID.getId(path + "_02.png")); // Dark Matter
                    case SUPERNOVA      -> put(texture, LovelyRobotID.getId(path + "_01.png")); // Supernova
                    case COLD_GOLD      -> put(texture, LovelyRobotID.getId(path + "_00.png")); // Cold Gold
                    case EMBRYON        -> put(texture, LovelyRobotID.getId(path + "_05.png")); // Embryon
                    case DARK_GOLD      -> put(texture, LovelyRobotID.getId(path + "_04.png")); // Dark Gold
                    case GOLD_MATTER    -> put(texture, LovelyRobotID.getId(path + "_03.png")); // Gold Matter
                    case HESTIA         -> put(texture, LovelyRobotID.getId(path + "_06.png")); // Hestia

                    case COMMANDER      -> put(texture, LovelyRobotID.getId(path + "_00.png")); // Commander
                    case VALKYRIE       -> put(texture, LovelyRobotID.getId(path + "_01.png")); // Valkyrie
                }
            }
        }};
        return this;
    } // addTexture ()

    @Override
    protected HashMap<EntityTexture, Identifier> setTexture(EntityVariant variant){
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
    protected HashMap<EntityModel, Identifier> setModel(EntityVariant variant){
        return new HashMap<>() {{
            put(EntityModel.Default,    LovelyRobotID.getId("geo/" + variant.getName() + ".geo.json"));
            put(EntityModel.Armed,      LovelyRobotID.getId("geo/" + variant.getName() + ".attack.geo.json"));
        }};
    } // setModel ()

    @Override
    protected HashMap<EntityAnimator, Identifier> setAnimator(EntityVariant variant){
        return new HashMap<>() {{
            put(EntityAnimator.Default, LovelyRobotID.getId("animations/default.animation.json"));
        }};
    } // setAnimator ()

} // Class NativeEntityType