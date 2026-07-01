package net.heriazone.lovelylib.common.entity;

import net.heriazone.hzlib.api.animation.AnimationProfile;
import net.heriazone.hzlib.api.animation.LoopBehavior;
import net.heriazone.hzlib.api.animation.AnimationPool;
import net.heriazone.hzlib.api.entity.NativeEntityFamily;
import net.heriazone.hzlib.api.entity.features.LevelFeature;
import net.heriazone.hzlib.api.entity.features.AppearanceConditions;
import net.heriazone.hzlib.api.entity.features.ConditionalAppearanceFeature;
import net.heriazone.hzlib.api.entity.features.variants.AnimatorVariantFeature;
import net.heriazone.hzlib.api.entity.features.variants.ModelVariantFeature;
import net.heriazone.hzlib.api.entity.features.variants.TextureVariantFeature;
import net.heriazone.hzlib.framework.entity.enums.EntityState;
import net.minecraft.world.item.Items;
import net.heriazone.hzlib.api.entity.variants.VariantRegistries;
import net.heriazone.hzlib.api.nbt.EntityDataSchema;
import net.heriazone.hzlib.api.nbt.MigrationChain;
import net.heriazone.hzlib.framework.entity.variants.StandardAnimatorVariant;
import net.heriazone.hzlib.framework.entity.variants.StandardModelVariant;
import net.heriazone.hzlib.framework.entity.variants.StandardTextureVariant;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.data.RobotFields;
import net.heriazone.lovelylib.common.entity.data.migration.MigrationStep_V0_Fabric;
import net.heriazone.lovelylib.common.entity.data.migration.MigrationStep_V0_Forge;
import net.heriazone.lovelylib.common.entity.data.migration.MigrationStep_V1_1204;
import net.heriazone.lovelylib.common.entity.enums.EntityTexture;
import net.heriazone.lovelylib.common.entity.enums.RobotVariant;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.lovelylib.common.shared.LovelyIdentifier;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Random;

/**
 * <p>Robot-specific entity type with 16-color palette and level system integration.<p>
 * <p>
 * <b>Architecture:</b> Extends HZLib's {@link NativeEntityFamily} to provide robot-specific
 * functionality including color palette management and automatic {@link LevelFeature}
 * attachment. Bridges the framework layer (pure Java) with Minecraft's resource system.
 * <p>
 * <b>Variant Dimensions:</b> Robots have two independent variant dimensions:
 * <ul>
 *   <li><b>Model state</b>: {@code "default"} (unarmed) or {@code "armed"} (combat mode)
 *       — registered in {@link #configureVariants()}</li>
 *   <li><b>Color</b>: 16 dye colors ({@code "white"}, {@code "orange"}, etc.)
 *       — registered via {@link #withColorPalette(RobotVariant)}</li>
 * </ul>
 * <p>
 * <b>Design Decision:</b> Automatically attaches {@link LevelFeature} during construction
 * to ensure all robots have leveling capability. Color palette uses
 * {@link TextureVariantFeature} with string keys (migrated from the old int-based
 * {@code EntityTexture} enum system per ADR_012).
 * <p>
 * <b>NBT Backward Compatibility:</b> Old saves with {@code TextureID} (int) are migrated
 * to {@code TextureVariant} (string) on first load in
 * {@code InternalEntity.readAdditionalSaveData()}.
 */
public class RobotFamily extends NativeEntityFamily<RobotFamily> {

    // -- Shared Animation Profiles --

    /**
     * Base animation profile shared by all Legacy and Reboot robot families.
     * <p>
     * <b>Architecture:</b> Passed directly into the {@link StandardAnimatorVariant}
     * constructor in {@link #configureVariants()} so that
     * {@code AnimationStateManager.resolveProfile()} finds it via the global
     * {@code VariantRegistries.ANIMATORS} lookup (Step 1). Previously the profile
     * was only attached as a family feature, which Step 1 never checks — making
     * the entire profile-aware animation path dead for robots (root missing-wire, ADR 022).
     * <p>
     * <b>Attack behavior:</b> Uses {@link LoopBehavior#INTERRUPT} — maps to GeckoLib's
     * {@code override_previous_animation: true}, ensuring the attack animation cuts
     * through the locomotion controller mid-swing.
     * <p>
     * Subclasses that need a different profile (e.g. Tribute) override
     * {@link #buildAnimatorProfile()} instead of modifying this constant.
     */
    protected static final AnimationProfile ROBOT_BASE_PROFILE = AnimationProfile.builder()
            .idle("idle")    // plays when stationary in any NON-Standby state (Follow, Defense, etc.)
            .walk("walk")
            .rest("rest")
            .sit("sit")
            .attack(pool -> pool.add("attack", LoopBehavior.INTERRUPT))
            // Idle slot 1: REST (stand-at-ease pose) — activates when in Standby and not floor-sitting.
            // The !isInSittingPose() guard does double duty:
            //   1. Normal operation: prevents rest from overriding sit after the threshold is crossed.
            //   2. World-reload race: on the first render frame, STATE may still read its default
            //      (Follow) before the SynchedEntityData packet arrives — so the Standby check
            //      alone cannot be trusted. isInSittingPose() (also synced) resolves the tie:
            //      if the robot was floor-sitting when saved, IS_IN_SITTING_POSE arrives in the
            //      same packet batch as STATE; this slot's condition correctly stays false.
            .idleSlot(AnimationPool.single("rest"),
                    entity -> entity.getCurrentState() == EntityState.Standby,
                    1,
                    0)
            // Idle slot 2: SIT (floor-sit pose) — activates after delay, OR immediately on reload
            // when IS_IN_SITTING_POSE is already true.
            // The OR on isInSittingPose() breaks the first-frame STATE race: if the robot was
            // floor-sitting when saved, IS_IN_SITTING_POSE=true arrives with the data packet and
            // this condition wins even before STATE is restored to Standby, guaranteeing "sit"
            // plays on the very first frame rather than falling through to "idle".
            .idleSlot(AnimationPool.single("sit"),
                    entity -> entity.getCurrentState() == EntityState.Standby
                           || entity.isInSittingPose(),
                    2,
                    SharedConfigs.Common.StandbyToSitDelayMin)
            .build();

    /**
     * Animation profile for Tribute robot families.
     * <p>
     * <b>Intentional omissions:</b> No idle slots. Tribute robots hold the {@code "rest"}
     * stand-at-ease pose indefinitely while in Standby — faithfully reproducing the original
     * LovelyRobot behaviour where robots never transition to the floor-sit pose.
     * <p>
     * <b>Naming note:</b> {@code "rest"} is the upright awaiting-orders animation.
     * Tribute uses it as the sole Standby idle because the original mod had no floor-sit state.
     * <p>
     * <b>Vehicle behaviour:</b> {@code .sit("rest")} is intentional — the vehicle branch in
     * {@code AnimationStateManager.getLocomotionAnimation()} checks the sit pool first when
     * riding. Pointing it at {@code "rest"} ensures Tribute robots play the upright
     * stand-at-ease animation while seated in a vehicle rather than the floor-sit pose,
     * which would look wrong on a mount or boat.
     */
    protected static final AnimationProfile TRIBUTE_PROFILE = AnimationProfile.builder()
            .idle("idle")
            .walk("walk")
            .sit("rest")
            .attack(pool -> pool.add("attack", LoopBehavior.INTERRUPT))
            .build();

    // -- Fields --

    private final RobotVariant variant;

    // -- Constructor --

    /**
     * Creates robot entity type with specified variant and attaches {@link LevelFeature}.
     * <p>
     * <b>State Impact:</b> Automatically attaches {@link LevelFeature} with maxLevel 0.
     * Caller should configure maxLevel via {@code LevelFeature.setMaxLevel()} after
     * construction (done in {@code RobotFamilyRegistry.create()}).
     * <p>
     * <b>Variant registration:</b> {@link #configureVariants()} is called by the parent
     * constructor and registers model and animator variants. Texture variants (16 colors)
     * are registered separately via {@link #withColorPalette(RobotVariant)}.
     *
     * @param key     unique identifier for this robot type (e.g., {@code "bunny"})
     * @param variant entity variant determining resource paths
     */
    public RobotFamily(String key, RobotVariant variant) {
        super(key);
        this.variant = variant;

        // Attach LevelFeature automatically — all robots have leveling capability
        withFeature(LevelFeature.class, new LevelFeature(0));
    } // Constructor: RobotFamily ()

    // -- Variant Configuration --

    /**
     * Registers model and animator variants for this robot type.
     * <p>
     * <b>Model variants:</b> {@code "{key}_default"} (unarmed) and {@code "{key}_armed"}
     * (combat mode). Each variant is registered as a {@link StandardModelVariant} in
     * {@link net.heriazone.hzlib.api.entity.variants.VariantRegistries#MODELS} with the
     * correct geo file path, then enabled for this entity key via {@link ModelVariantFeature}.
     * <p>
     * <b>Animator variants:</b> {@code "{key}_default"} only — all robots share one
     * animation file ({@code default.animation.json}). Registered as a
     * {@link StandardAnimatorVariant} in
     * {@link net.heriazone.hzlib.api.entity.variants.VariantRegistries#ANIMATORS},
     * with the {@link AnimationProfile} supplied by {@link #buildAnimatorProfile()} passed
     * directly into the constructor. This is the wire that allows
     * {@code AnimationStateManager.resolveProfile()} Step 1 to find the profile via the
     * global animator registry lookup — closing the root missing-wire from ADR 022.
     * <p>
     * <b>Path patterns (matching backup populateModels/populateAnimators):</b>
     * <ul>
     *   <li>Model default: {@code lovelylib:geo/{key}.default.geo.json}</li>
     *   <li>Model armed:   {@code lovelylib:geo/{key}.armed.geo.json}</li>
     *   <li>Animator:      {@code lovelylib:animations/default.animation.json}</li>
     * </ul>
     * <p>
     * <b>Texture variants:</b> Registered separately via {@link #withColorPalette(RobotVariant)}
     * after construction, since the color palette requires the variant name for path construction.
     */
    @Override
    protected void configureVariants() {
        // -- Model Variants --
        // Register StandardModelVariant instances in the global registry with actual resource paths,
        // then enable them for this entity key via ModelVariantFeature.
        String defaultModelKey = key + "_default";
        String armedModelKey   = key + "_armed";

        VariantRegistries.MODELS.register(
                new StandardModelVariant(
                        defaultModelKey,
                        defaultModelKey,
                        LovelyIdentifier.getId("geo/" + key + "." + LovelyConstant.MOD_DEFAULT + ".geo.json").toString(),
                        1
                )
        );
        VariantRegistries.MODELS.register(
                new StandardModelVariant(
                        armedModelKey,
                        armedModelKey,
                        LovelyIdentifier.getId("geo/" + key + "." + LovelyConstant.MOD_ARMED + ".geo.json").toString(),
                        0
                )
        );

        withFeature(ModelVariantFeature.class, new ModelVariantFeature()
                .withVariants(key, defaultModelKey, armedModelKey)
                .withDefault(key, defaultModelKey));

        // -- Animator Variants --
        // All robots share one animation file — register once per key.
        // The AnimationProfile is passed into the StandardAnimatorVariant constructor so that
        // AnimationStateManager.resolveProfile() Step 1 finds it via VariantRegistries.ANIMATORS.
        // Without this, resolveProfile() returns null for every robot and the entire profile-aware
        // animation path is dead — the root missing-wire identified in ADR 022 Change C.
        String defaultAnimKey = key + "_default";

        VariantRegistries.ANIMATORS.register(
                new StandardAnimatorVariant(
                        defaultAnimKey,
                        defaultAnimKey,
                        LovelyIdentifier.getId("animations/" + LovelyConstant.ANIM_DEFAULT + ".animation.json").toString(),
                        buildAnimatorProfile(),   // <-- wire closed here
                        0
                )
        );

        withFeature(AnimatorVariantFeature.class, new AnimatorVariantFeature()
                .withVariants(key, defaultAnimKey)
                .withDefault(key, defaultAnimKey));
    } // configureVariants ()

    /**
     * Returns the {@link AnimationProfile} to embed in this family's
     * {@link StandardAnimatorVariant} registration.
     * <p>
     * <b>Design Decision:</b> Override point rather than constructor parameter — keeps the
     * profile choice in the subclass declaration alongside all other family configuration,
     * without requiring callers ({@link RobotFamilyRegistry#create}) to thread a profile
     * argument through every factory overload.
     * <p>
     * The base implementation returns {@link #ROBOT_BASE_PROFILE}, which is correct for
     * all Legacy and Reboot families. {@code TributeRobotFamilies} overrides this to return
     * {@link #TRIBUTE_PROFILE} (no rest/sit slots — faithful to the original mod behaviour).
     *
     * @return animation profile for the animator variant; never {@code null}
     */
    protected AnimationProfile buildAnimatorProfile() {
        return ROBOT_BASE_PROFILE;
    } // buildAnimatorProfile ()

    /**
     * Declares the robot entity data schema and registers the three-step migration chain.
     * <p>
     * <b>Schema:</b> All {@link RobotFields} constants are registered in write order —
     * the order they appear in the old flat-write block for readability parity.
     * <p>
     * <b>Migration chain (oldest first):</b>
     * <ol>
     *   <li>{@link MigrationStep_V0_Fabric} — Gen1-Fabric locale strings → stable keys</li>
     *   <li>{@link MigrationStep_V0_Forge} — Gen1-Forge/Gen2 flat PascalCase → EntityData</li>
     *   <li>{@link MigrationStep_V1_1204} — 1.20.4 flat int-ID format → EntityData</li>
     * </ol>
     * Each step is idempotent and skips if its target format is already present.
     */
    @Override
    protected void configureSchema() {
        schema = EntityDataSchema.builder()
                .register(RobotFields.LEVEL)
                .register(RobotFields.EXP)
                .register(RobotFields.MAX_LEVEL)
                .register(RobotFields.FIRE_PROT)
                .register(RobotFields.FALL_PROT)
                .register(RobotFields.BLAST_PROT)
                .register(RobotFields.PROJ_PROT)
                .register(RobotFields.AUTO_ATTACK)
                .register(RobotFields.BASE_X)
                .register(RobotFields.BASE_Y)
                .register(RobotFields.BASE_Z)
                .register(RobotFields.SITTING)
                .register(RobotFields.HEALTH)
                .register(RobotFields.IDLE_STATIONARY_TICKS)
                .register(RobotFields.STANDBY_TICKS)
                .register(RobotFields.STANDBY_TARGET_TICKS)
                .version("1.0.0")
                .build();

        migrationChain = MigrationChain.builder()
                .addStep(new MigrationStep_V0_Fabric())
                .addStep(new MigrationStep_V0_Forge())
                .addStep(new MigrationStep_V1_1204())
                .build();
    } // configureSchema ()


    /**
     * Creates the translatable display name for this robot type.
     *
     * @param key entity type key
     * @return translatable text component for the robot name
     */
    @Override
    protected MutableComponent createTranslation(String key) {
        return LovelyIdentifier.getTranslation("entity.", key);
    } // createTranslation ()

    // -- Color Palette System --

    /**
     * Registers the full 16-color texture palette and a matching {@link ConditionalAppearanceFeature}
     * with one dye-to-variant-key rule per color.
     * <p>
     * <b>Architecture:</b> Delegates to {@link #withColorPalette(RobotVariant, java.util.List)}
     * with all 16 non-RANDOM colors. Use the overload directly when a restricted palette is needed.
     * <p>
     * Both features must be registered together: {@link TextureVariantFeature} drives random
     * spawn selection (Lane A); {@link ConditionalAppearanceFeature} drives interaction-time
     * dye resolution. Neither is redundant.
     *
     * @param variant entity variant determining color texture paths
     * @return this instance for method chaining
     */
    public RobotFamily withColorPalette(RobotVariant variant) {
        // Delegate to the full 16-color set, excluding RANDOM.
        java.util.List<EntityTexture> allColors = EntityTexture.VALUES.stream()
                .filter(c -> c != EntityTexture.RANDOM)
                .collect(java.util.stream.Collectors.toList());
        return withColorPalette(variant, allColors);
    } // withColorPalette ()

    /**
     * Registers a restricted color palette for this robot type.
     * <p>
     * <b>Architecture:</b> Identical to {@link #withColorPalette(RobotVariant)} but only
     * registers texture variants and dye-reaction rules for the provided subset of colors.
     * Intended for robot types (e.g., Bunny3) whose texture sheets cover fewer than all
     * 16 dye colors. Dyes outside the subset produce no visual change at interaction time.
     * <p>
     * <b>Default texture:</b> Falls back to the first color in the provided list rather
     * than {@code WHITE}, since {@code WHITE} may not be in the restricted set.
     *
     * @param variant entity variant determining texture path prefix
     * @param colors  ordered list of active colors — must not be empty, must not contain RANDOM
     * @return this instance for method chaining
     */
    public RobotFamily withColorPalette(RobotVariant variant, java.util.List<EntityTexture> colors) {
        return withColorPaletteInternal(variant, colors, LovelyIdentifier.MODID());
    } // withColorPalette ()

    /**
     * Registers a restricted color palette with textures resolved from a custom namespace.
     * <p>
     * <b>Architecture:</b> Allows a mod (e.g., {@code lovely_robot} / Tribute) to register
     * its own texture variants that point to resources in its own namespace rather than
     * {@code lovelylib:}. The variant registry keys are prefixed with the entity key to avoid
     * collisions with the standard lovelylib registrations for the same variant/color pair.
     * <p>
     * <b>Texture path pattern:</b>
     * {@code {namespace}:textures/entity/{variantName}/{variantName}_{id:02d}.png}
     * <p>
     * <b>Example:</b> Tribute Bunny pink →
     * {@code lovely_robot:textures/entity/bunny/bunny_06.png}
     *
     * @param variant    entity variant determining texture path prefix
     * @param colors     ordered list of active colors — must not be empty, must not contain RANDOM
     * @param namespace  mod namespace to use for texture {@link ResourceLocation}s (e.g. {@code "lovely_robot"})
     * @return this instance for method chaining
     */
    public RobotFamily withColorPalette(RobotVariant variant, java.util.List<EntityTexture> colors, String namespace) {
        return withColorPaletteInternal(variant, colors, namespace);
    } // withColorPalette ()

    /**
     * Internal implementation shared by all {@code withColorPalette} overloads.
     * <p>
     * <b>Registry key strategy:</b> When {@code namespace} differs from
     * {@link LovelyIdentifier#MODID}, variant keys are prefixed with the namespace
     * to prevent collisions — e.g., {@code "t_bunny_pink"} vs {@code "bunny_pink"}.
     * Same-namespace registrations keep the standard key format for backward compatibility.
     *
     * @param variant   entity variant
     * @param colors    active colors
     * @param namespace texture resource namespace
     * @return this instance for method chaining
     */
    private RobotFamily withColorPaletteInternal(RobotVariant variant, java.util.List<EntityTexture> colors, String namespace) {
        String basePath = LovelyConstant.TEXTURE_ENTITY_PATH + variant.getName() + "/";

        // Namespace-based key prefix prevents registry collisions when the same
        // variant/color is registered by both lovelylib and a downstream mod.
        boolean useNamespacePrefix = !namespace.equals(LovelyIdentifier.MODID());
        String keyPrefix = useNamespacePrefix ? namespace.replace(':', '_') + "_" : "";

        // Build dye-condition map for quick Item → EntityTexture lookup.
        java.util.Map<EntityTexture, net.minecraft.world.item.Item> dyeItems = new java.util.EnumMap<>(EntityTexture.class);
        dyeItems.put(EntityTexture.WHITE,      Items.WHITE_DYE);
        dyeItems.put(EntityTexture.ORANGE,     Items.ORANGE_DYE);
        dyeItems.put(EntityTexture.MAGENTA,    Items.MAGENTA_DYE);
        dyeItems.put(EntityTexture.LIGHT_BLUE, Items.LIGHT_BLUE_DYE);
        dyeItems.put(EntityTexture.YELLOW,     Items.YELLOW_DYE);
        dyeItems.put(EntityTexture.LIME,       Items.LIME_DYE);
        dyeItems.put(EntityTexture.PINK,       Items.PINK_DYE);
        dyeItems.put(EntityTexture.GRAY,       Items.GRAY_DYE);
        dyeItems.put(EntityTexture.LIGHT_GRAY, Items.LIGHT_GRAY_DYE);
        dyeItems.put(EntityTexture.CYAN,       Items.CYAN_DYE);
        dyeItems.put(EntityTexture.PURPLE,     Items.PURPLE_DYE);
        dyeItems.put(EntityTexture.BLUE,       Items.BLUE_DYE);
        dyeItems.put(EntityTexture.BROWN,      Items.BROWN_DYE);
        dyeItems.put(EntityTexture.GREEN,      Items.GREEN_DYE);
        dyeItems.put(EntityTexture.RED,        Items.RED_DYE);
        dyeItems.put(EntityTexture.BLACK,      Items.BLACK_DYE);

        TextureVariantFeature textureFeature = new TextureVariantFeature();

        ConditionalAppearanceFeature.Builder dyeBuilder = ConditionalAppearanceFeature.builder();
        for (EntityTexture color : colors) {
            net.minecraft.world.item.Item dyeItem = dyeItems.get(color);
            if (dyeItem != null) {
                dyeBuilder.when(AppearanceConditions.heldItem(dyeItem), keyPrefix + key + "_" + color.Name());
            }
        }
        ConditionalAppearanceFeature dyeFeature = dyeBuilder.build();

        for (EntityTexture color : colors) {
            // Entity-specific key prevents global registry collisions between robot types.
            // Namespace prefix additionally prevents collisions across mods for the same type+color.
            String colorKey = keyPrefix + key + "_" + color.Name();
            String colorId  = String.format("%02d", color.getId());
            ResourceLocation path = ResourceLocation.fromNamespaceAndPath(
                    namespace, basePath + variant.getName() + "_" + colorId + ".png");

            net.heriazone.hzlib.api.entity.variants.VariantRegistries.TEXTURES.register(
                    new StandardTextureVariant(colorKey, color.Name(), path.toString(), color.getId())
            );

            textureFeature.withVariant(key, colorKey);
        }

        // Default falls back to first active color — WHITE may not exist in restricted palettes.
        String defaultColorKey = keyPrefix + key + "_" + colors.get(0).Name();
        textureFeature.withDefault(key, defaultColorKey);
        withFeature(TextureVariantFeature.class, textureFeature);
        withFeature(ConditionalAppearanceFeature.class, dyeFeature);

        return this;
    } // withColorPaletteInternal ()

    /**
     * Generates a random color key from the registered color palette.
     * <p>
     * <b>Migration note:</b> Replaces the old {@code getRandomColorId()} which returned
     * an int. Now returns a string key (e.g., {@code "magenta"}) for use with
     * {@code setTextureVariant()}.
     *
     * @return random color key string, or {@code "white"} if no palette registered
     */
    public String getRandomColorKey() {
        return getFeature(TextureVariantFeature.class)
                .map(feature -> {
                    var variants = feature.getAvailableVariants(key);
                    if (variants.isEmpty()) return EntityTexture.WHITE.Name();
                    var list = new java.util.ArrayList<>(variants);
                    return list.get(new Random().nextInt(list.size())).getKey();
                })
                .orElse(EntityTexture.WHITE.Name());
    } // getRandomColorKey ()

    /**
     * Returns the model resource location for the specified model variant key.
     * <p>
     * <b>Usage:</b> Called by renderers to resolve the geo model file path.
     *
     * @param variantKey model variant key ({@code "default"} or {@code "armed"})
     * @return resource location for the model, or {@code null} if not found
     */
    public ResourceLocation getModelResource(String variantKey) {
        var modelVariant = getModelVariant(key, variantKey);
        return modelVariant != null ? modelVariant.getResource(key) : null;
    } // getModelResource ()

    /**
     * Returns the animator resource location for the specified animator variant key.
     * <p>
     * <b>Usage:</b> Called by renderers to resolve the animation file path.
     *
     * @param variantKey animator variant key ({@code "default"})
     * @return resource location for the animator, or {@code null} if not found
     */
    public ResourceLocation getAnimatorResource(String variantKey) {
        var animatorVariant = getAnimatorVariant(key, variantKey);
        return animatorVariant != null ? animatorVariant.getResource(key) : null;
    } // getAnimatorResource ()

    // -- Convenience Accessors --

    /**
     * Returns the entity variant for this robot type.
     *
     * @return entity variant
     */
    public RobotVariant getVariant() {
        return variant;
    } // getVariant ()

    /**
     * Returns the maximum level from the attached {@link LevelFeature}.
     * <p>
     * <b>Design Decision:</b> Convenience method eliminates boilerplate Optional
     * handling at call sites. Returns 0 if {@link LevelFeature} not present (defensive).
     *
     * @return maximum level, or 0 if feature not present
     */
    public int getMaxLevel() {
        return getFeature(LevelFeature.class)
                .map(LevelFeature::getMaxLevel)
                .orElse(0);
    } // getMaxLevel ()

} // Class: RobotFamily