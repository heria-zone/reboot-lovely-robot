package net.msymbios.rlovelyr.entity.attribute;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.attribute.attributes.EnumAttribute;
import net.msymbios.rlovelyr.entity.attribute.attributes.numbers.*;
import net.msymbios.rlovelyr.entity.attribute.enums.Level;
import net.msymbios.rlovelyr.entity.attribute.enums.Operation;
import net.msymbios.rlovelyr.entity.attribute.interfaces.IModifiable;
import net.msymbios.rlovelyr.entity.attribute.interfaces.IModifier;
import net.msymbios.rlovelyr.entity.enums.BlocklingHand;
import net.msymbios.rlovelyr.entity.internal.InterfaceEntity;
import net.msymbios.rlovelyr.entity.skill.info.SkillInfo;
import net.msymbios.rlovelyr.entity.skill.skills.*;
import net.msymbios.rlovelyr.util.interfaces.IReadWriteNBT;
import net.msymbios.rlovelyr.util.internal.Version;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Used to manage the attributes associated with a blockling.
 */
public class BlocklingAttributes implements IReadWriteNBT {
    /**
     * The list of attributes added via add attribute.
     * Used to save/load nbt data and sync to client/server.
     */
    public final List<Attribute<?>> attributes = new ArrayList<>();

    /**
     * The list of attributes added via add modifier.
     * Used to save/load nbt data and sync to client/server.
     */
    public final List<IModifier<?>> modifiers = new ArrayList<>();


    public final IntAttribute combatLevel;
    public final IntAttribute miningLevel;
    public final IntAttribute woodcuttingLevel;
    public final IntAttribute farmingLevel;
    public final Attribute<Integer> totalLevel;

    public final IntAttribute combatXp;
    public final IntAttribute miningXp;
    public final IntAttribute woodcuttingXp;
    public final IntAttribute farmingXp;

    public final EnumAttribute<BlocklingHand> hand;

    public final ModifiableFloatAttribute maxHealth;
    public final ModifiableFloatAttributeModifier maxHealthBlocklingModifier;
    public final FloatAttributeModifier maxHealthTypeModifier;
    public final FloatAttributeModifier maxHealthCombatLevelModifier;

    public final ModifiableFloatAttributeModifier attackDamageBlocklingModifier;
    public final FloatAttributeModifier attackDamageTypeModifier;
    public final FloatAttributeModifier attackDamageCombatLevelModifier;
    public final FloatAttributeModifier attackDamageSkillSharpnessModifier;
    public final FloatAttributeModifier attackDamageSkillBerserkerModifier;
    public final FloatAttributeModifier attackDamageSkillWrecklessModifier;

    public final ModifiableFloatAttribute mainHandAttackDamage;
    public final FloatAttributeModifier mainHandAttackDamageToolModifier;

    public final ModifiableFloatAttribute offHandAttackDamage;
    public final FloatAttributeModifier offHandAttackDamageToolModifier;

    public final AveragedAttribute attackSpeed;
    public final ModifiableFloatAttributeModifier attackSpeedBlocklingModifier;
    public final FloatAttributeModifier attackSpeedTypeModifier;
    public final FloatAttributeModifier attackSpeedLevelModifier;
    public final ModifiableFloatAttributeModifier attackSpeedToolsModifier;
    public final FloatAttributeModifier attackSpeedMainHandModifier;
    public final FloatAttributeModifier attackSpeedOffHandModifier;
    public final FloatAttributeModifier attackSpeedSkillMomentumModifier;
    public final FloatAttributeModifier attackSpeedSkillPhotophileModifier;

    public final ModifiableFloatAttribute armour;
    public final ModifiableFloatAttributeModifier armourBlocklingModifier;
    public final FloatAttributeModifier armourCombatLevelModifier;
    public final FloatAttributeModifier armourTypeModifier;

    public final ModifiableFloatAttribute armourToughness;
    public final ModifiableFloatAttributeModifier armourToughnessBlocklingModifier;
    public final FloatAttributeModifier armourToughnessCombatLevelModifier;
    public final FloatAttributeModifier armourToughnessTypeModifier;

    public final ModifiableFloatAttribute knockbackResistance;
    public final ModifiableFloatAttributeModifier knockbackResistanceBlocklingModifier;
    public final FloatAttributeModifier knockbackResistanceCombatLevelModifier;
    public final FloatAttributeModifier knockbackResistanceTypeModifier;

    public final ModifiableFloatAttribute moveSpeed;
    public final ModifiableFloatAttributeModifier moveSpeedBlocklingModifier;
    public final FloatAttributeModifier moveSpeedTypeModifier;
    public final FloatAttributeModifier moveSpeedSkillSpeedModifier;

    public final ModifiableFloatAttribute miningRange;
    public final ModifiableFloatAttribute miningRangeSq;
    public final ModifiableFloatAttribute woodcuttingRange;
    public final ModifiableFloatAttribute woodcuttingRangeSq;
    public final ModifiableFloatAttribute farmingRange;
    public final ModifiableFloatAttribute farmingRangeSq;

    public final ModifiableFloatAttribute miningSpeed;
    public final ModifiableFloatAttributeModifier miningSpeedBlocklingModifier;
    public final FloatAttributeModifier miningSpeedTypeModifier;
    public final FloatAttributeModifier miningSpeedLevelModifier;
    public final FloatAttributeModifier miningSpeedMainHandModifier;
    public final FloatAttributeModifier miningSpeedOffHandModifier;
    public final FloatAttributeModifier miningSpeedSkillEfficiencyModifier;
    public final FloatAttributeModifier miningSpeedSkillAdrenalineModifier;
    public final FloatAttributeModifier miningSpeedSkillMomentumModifier;
    public final FloatAttributeModifier miningSpeedSkillHastyModifier;
    public final FloatAttributeModifier miningSpeedSkillNightOwlModifier;

    public final ModifiableFloatAttribute woodcuttingSpeed;
    public final ModifiableFloatAttributeModifier woodcuttingSpeedBlocklingModifier;
    public final FloatAttributeModifier woodcuttingSpeedTypeModifier;
    public final FloatAttributeModifier woodcuttingSpeedLevelModifier;
    public final FloatAttributeModifier woodcuttingSpeedMainHandModifier;
    public final FloatAttributeModifier woodcuttingSpeedOffHandModifier;
    public final FloatAttributeModifier woodcuttingSpeedSkillEfficiencyModifier;
    public final FloatAttributeModifier woodcuttingSpeedSkillAdrenalineModifier;
    public final FloatAttributeModifier woodcuttingSpeedSkillMomentumModifier;
    public final FloatAttributeModifier woodcuttingSpeedSkillHastyModifier;
    public final FloatAttributeModifier woodcuttingSpeedSkillNightOwlModifier;

    public final ModifiableFloatAttribute farmingSpeed;
    public final ModifiableFloatAttributeModifier farmingSpeedBlocklingModifier;
    public final FloatAttributeModifier farmingSpeedTypeModifier;
    public final FloatAttributeModifier farmingSpeedLevelModifier;
    public final FloatAttributeModifier farmingSpeedMainHandModifier;
    public final FloatAttributeModifier farmingSpeedOffHandModifier;
    public final FloatAttributeModifier farmingSpeedSkillEfficiencyModifier;
    public final FloatAttributeModifier farmingSpeedSkillAdrenalineModifier;
    public final FloatAttributeModifier farmingSpeedSkillMomentumModifier;
    public final FloatAttributeModifier farmingSpeedSkillHastyModifier;
    public final FloatAttributeModifier farmingSpeedSkillNightOwlModifier;

    /**
     * The associated blockling.
     */
    public final InterfaceEntity blockling;

    /**
     * The world the associated blockling is in.
     */
    public final World world;

    /**
     * @param blockling the associated blockling.
     */
    public BlocklingAttributes(InterfaceEntity blockling) {
        this.blockling = blockling;
        this.world = blockling.getWorld();

        addAttribute(combatLevel = new IntAttribute("17beee8e-3fca-4601-a766-46f811ad6b69", "combat_level", blockling, blockling.getRandom().nextInt(5) + 1, null, null, true));
        addAttribute(miningLevel = new IntAttribute("a2a62308-0a6e-41bb-9844-4645eeb72fb7", "mining_level", blockling, blockling.getRandom().nextInt(5) + 1, null, null, true));
        addAttribute(woodcuttingLevel = new IntAttribute("c6d3ce7c-52af-44df-833b-fede277eec7f", "woodcutting_level", blockling, blockling.getRandom().nextInt(5) + 1, null, null, true));
        addAttribute(farmingLevel = new IntAttribute("ac1c6d1b-18bb-435a-ad93-c24d6fa90816", "farming_level", blockling, blockling.getRandom().nextInt(5) + 1, null, null, true));
        totalLevel = new Attribute<Integer>("ecd6a307-2f55-4d9b-96ab-f5efc82f8e5a", "total_level", blockling, true) {
            @Override
            public Integer getValue() {
                return combatLevel.getValue() + miningLevel.getValue() + woodcuttingLevel.getValue() + farmingLevel.getValue();
            }

            @Override
            protected void setValue(Integer value, boolean sync) {
                // Value is always calculated on get
            }
        };

        addAttribute(combatXp = new IntAttribute("ec56a177-2a08-4f43-b77a-b1d4544a8656", "combat_xp", blockling, blockling.getRandom().nextInt(getXpForLevel(combatLevel.getValue())), null, null, true));
        addAttribute(miningXp = new IntAttribute("ce581807-3fad-45b1-9aec-43ed0cb53c8f", "mining_xp", blockling, blockling.getRandom().nextInt(getXpForLevel(miningLevel.getValue())), null, null, true));
        addAttribute(woodcuttingXp = new IntAttribute("82165063-6d47-4534-acc9-db3543c3db74", "woodcutting_xp", blockling, blockling.getRandom().nextInt(getXpForLevel(woodcuttingLevel.getValue())), null, null, true));
        addAttribute(farmingXp = new IntAttribute("1f1e4cbc-358e-4477-92d8-03e818d5272c", "farming_xp", blockling, blockling.getRandom().nextInt(getXpForLevel(farmingLevel.getValue())), null, null, true));

        addAttribute(hand = new EnumAttribute<>("f21fcbaa-f800-468e-8c22-ec4b4fd0fdc2", "hand", blockling, BlocklingHand.class, BlocklingHand.NONE, null, null, true));

        addModifier(maxHealthCombatLevelModifier = new FloatAttributeModifier("a78160fa-7bc3-493e-b74b-27af4206d111", "max_health_combat_level", blockling, 0.0f, Operation.ADD, null, combatLevel.displayStringNameSupplier, true));
        addModifier(maxHealthTypeModifier = new FloatAttributeModifier("79043f39-6f44-4077-a358-0f75a0a1e995", "max_health_type", blockling, 0.0f, Operation.ADD, null, () -> blockling.variant.getName(), true));
        addModifier(maxHealthBlocklingModifier = new ModifiableFloatAttributeModifier("42418962-175b-4c06-84a2-f770ebd00a88", "max_health_blockling", blockling, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString(), true, maxHealthTypeModifier, maxHealthCombatLevelModifier));
        addAttribute(maxHealth = new ModifiableFloatAttribute("9c6eb101-f025-4f8f-895b-10868b7d06b2", "max_health", blockling, 10.0f, null, null, true, maxHealthBlocklingModifier));

        addModifier(attackDamageSkillWrecklessModifier = new FloatAttributeModifier("39b9820d-1805-484e-8bf0-9de9e12a878c", "attack_damage_skill_wreckless", blockling, 10.0f, Operation.ADD, null, () -> skillDisplayNameProvider(CombatSkills.WRECKLESS), false));
        addModifier(attackDamageSkillBerserkerModifier = new FloatAttributeModifier("2d1bc0b1-53cc-46e7-8a15-93c3bb538cdc", "attack_damage_skill_berserker", blockling, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(CombatSkills.BERSERKER), false));
        addModifier(attackDamageSkillSharpnessModifier = new FloatAttributeModifier("b27747e9-7e73-416f-bb1f-8853b4132e90", "attack_damage_skill_sharpness", blockling, 5.0f, Operation.ADD, null, () -> skillDisplayNameProvider(CombatSkills.SHARPNESS), false));
        addModifier(attackDamageCombatLevelModifier = new FloatAttributeModifier("406a98f7-df1f-4c7f-93e4-990d71c7747f", "attack_damage_combat_level", blockling, 0.0f, Operation.ADD, null, combatLevel.displayStringNameSupplier, true));
        addModifier(attackDamageTypeModifier = new FloatAttributeModifier("ddb441fc-2d8c-4950-b0a9-b96b60680ac1", "attack_damage_type", blockling, 0.0f, Operation.ADD, null, () -> blockling.variant.getName(), true));
        addModifier(attackDamageBlocklingModifier = new ModifiableFloatAttributeModifier("9bfdfe35-c6c4-4364-8535-7aa50927f484", "attack_damage_blockling", blockling, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString(), true, attackDamageTypeModifier, attackDamageCombatLevelModifier));

        attackDamageSkillWrecklessModifier.setVanillaAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        attackDamageSkillBerserkerModifier.setVanillaAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        attackDamageSkillSharpnessModifier.setVanillaAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        attackDamageBlocklingModifier.setVanillaAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE);

        addModifier(mainHandAttackDamageToolModifier = new FloatAttributeModifier("2ae58b89-fed6-4b2f-90bc-e7dbc9d7b249", "main_hand_attack_damage_tool", blockling, 0.0f, Operation.ADD, null, () -> blockling.getMainHandStack().toHoverableText().getString(), true));
        addAttribute(mainHandAttackDamage = new ModifiableFloatAttribute("e8549f17-e473-4849-8f48-ae624ee0c242", "main_hand_attack_damage", blockling, 0.0f, null, null, true, attackDamageBlocklingModifier, mainHandAttackDamageToolModifier, attackDamageSkillSharpnessModifier, attackDamageSkillBerserkerModifier, attackDamageSkillWrecklessModifier));

        addModifier(offHandAttackDamageToolModifier = new FloatAttributeModifier("c1aa1629-fe40-47eb-8c5c-a02d0b82e636", "off_hand_attack_damage_tool", blockling, 0.0f, Operation.ADD, null, () -> blockling.getOffHandStack().toHoverableText().getString(), true));
        addAttribute(offHandAttackDamage = new ModifiableFloatAttribute("519e98de-c213-4c1c-8a07-cd659bc9982c", "off_hand_attack_damage", blockling, 0.0f, null, null, true, attackDamageBlocklingModifier, offHandAttackDamageToolModifier, attackDamageSkillSharpnessModifier, attackDamageSkillBerserkerModifier, attackDamageSkillWrecklessModifier));

        // Default set to 4.0f (seems to be the same for a player too)
        // A sword is 1.6f * 2.0f
        // An axe is 0.8f * 2.0f
        // So any tool use should be slower than fists
        addModifier(attackSpeedSkillPhotophileModifier = new FloatAttributeModifier("8f7b5867-918a-46b0-bcfb-6ae470220171", "attack_speed_skill_photophile", blockling, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(CombatSkills.PHOTOPHILE), false));
        addModifier(attackSpeedSkillMomentumModifier = new FloatAttributeModifier("b97dcf6f-d1e4-4988-aa42-819e79af4a02", "attack_speed_skill_momentum", blockling, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(CombatSkills.MOMENTUM), false));
        addModifier(attackSpeedOffHandModifier = new FloatAttributeModifier("3566961d-db2b-4833-8c0c-cf6813ade8cc", "attack_speed_off_hand", blockling, 0.0f, Operation.ADD, null, () -> blockling.getOffHandStack().toHoverableText().getString(), true));
        addModifier(attackSpeedMainHandModifier = new FloatAttributeModifier("87343a1e-7a0b-4963-8d7e-e95f809e90ee", "attack_speed_main_hand", blockling, 0.0f, Operation.ADD, null, () -> blockling.getMainHandStack().toHoverableText().getString(), true));
        addModifier(attackSpeedToolsModifier = new ModifiableFloatAttributeModifier("8642d8f0-7554-4764-a58d-ee926c808fc8", "attack_speed_tools", blockling, 0.0f, Operation.ADD, null, null, true, attackSpeedMainHandModifier, attackSpeedOffHandModifier));
        addModifier(attackSpeedLevelModifier = new FloatAttributeModifier("bfeb22fe-aaaf-4294-9850-27449e27e44f", "attack_speed_level", blockling, 0.0f, Operation.ADD, null, combatLevel.displayStringNameSupplier, true));
        addModifier(attackSpeedTypeModifier = new FloatAttributeModifier("f40d211d-c6fd-449a-a2f8-8bffd24ac810", "attack_speed_type", blockling, 0.0f, Operation.ADD, null, () -> blockling.variant.getName(), true));
        addModifier(attackSpeedBlocklingModifier = new ModifiableFloatAttributeModifier("8c4e3d41-2a17-4cd1-8dbb-8866008960a5", "attack_speed_blockling", blockling, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString(), true, attackSpeedTypeModifier, attackSpeedLevelModifier));
        addAttribute(attackSpeed = new AveragedAttribute("4cbc129d-281d-410e-bba0-45d4e064932a", "attack_speed", blockling, 0.0f, null, null, true, attackSpeedBlocklingModifier, attackSpeedToolsModifier, attackSpeedSkillMomentumModifier, attackSpeedSkillPhotophileModifier));

        addModifier(armourCombatLevelModifier = new FloatAttributeModifier("15f2f2ce-cdf1-4188-882e-67ceab22df41", "armour_combat_level", blockling, 0.0f, Operation.ADD, null, combatLevel.displayStringNameSupplier, true));
        addModifier(armourTypeModifier = new FloatAttributeModifier("a72fb401-abb7-4d95-ad7e-e83fc6a399d1", "armour_type", blockling, 0.0f, Operation.ADD, null, () -> blockling.variant.getName(), true));
        addModifier(armourBlocklingModifier = new ModifiableFloatAttributeModifier("1e5b8f41-bab6-41f9-9a3d-5303f2f1ed6e", "armour_blockling", blockling, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString(), true, armourTypeModifier, armourCombatLevelModifier));
        addAttribute(armour = new ModifiableFloatAttribute("6b34a986-f1ad-4476-a1c6-700d841fb1ec", "armour", blockling, 0.0f, null, null, true, armourBlocklingModifier));

        addModifier(armourToughnessCombatLevelModifier = new FloatAttributeModifier("0f438ab0-2e72-4555-840f-3b3dc2335014", "armour_toughness_combat_level", blockling, 0.0f, Operation.ADD, null, combatLevel.displayStringNameSupplier, true));
        addModifier(armourToughnessTypeModifier = new FloatAttributeModifier("4f806fa7-1ebe-4426-99c6-5c0d0f41be25", "armour_toughness_type", blockling, 0.0f, Operation.ADD, null, () -> blockling.variant.getName(), true));
        addModifier(armourToughnessBlocklingModifier = new ModifiableFloatAttributeModifier("9f08c15b-fa91-4c1e-97ef-4b465936c5ab", "armour_toughness_blockling", blockling, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString(), true, armourToughnessTypeModifier, armourToughnessCombatLevelModifier));
        addAttribute(armourToughness = new ModifiableFloatAttribute("1cfdad6a-0bd3-461f-8007-c0a591a30783", "armour_toughness", blockling, 0.0f, null, null, true, armourToughnessBlocklingModifier));

        addModifier(knockbackResistanceCombatLevelModifier = new FloatAttributeModifier("711cf234-2f57-413c-be3d-ce4c5f809b86", "knockback_resistance_combat_level", blockling, 0.0f, Operation.ADD, (v) -> String.format("%.0f%%", v * 100.0f), combatLevel.displayStringNameSupplier, true));
        addModifier(knockbackResistanceTypeModifier = new FloatAttributeModifier("eb217d5e-6e7d-4ef0-9ca1-153a7bc18593", "knockback_resistance_type", blockling, 0.0f, Operation.ADD, (v) -> String.format("%.0f%%", v * 100.0f), () -> blockling.variant.getName(), true));
        addModifier(knockbackResistanceBlocklingModifier = new ModifiableFloatAttributeModifier("3b6bf894-3beb-42f5-b516-759bacf9acab", "knockback_resistance_blockling", blockling, 0.0f, Operation.ADD, (v) -> String.format("%.0f%%", v * 100.0f), () -> blockling.getCustomName().getString(), true, knockbackResistanceTypeModifier, knockbackResistanceCombatLevelModifier));
        addAttribute(knockbackResistance = new ModifiableFloatAttribute("ddc90fc2-4a68-4c30-8701-d2d9dbe8b94a", "knockback_resistance", blockling, 0.0f, (v) -> String.format("%.0f%%", v * 100.0f), null, true, knockbackResistanceBlocklingModifier));

        addModifier(moveSpeedSkillSpeedModifier = new FloatAttributeModifier("38ae3f89-349e-4b99-a8a1-d2b7a7b585c2", "move_speed_skill_speed", blockling, 1.0f, Operation.MULTIPLY_TOTAL, (v) -> String.format("%.0f%%", (v - 1.0f) * 100.0f), this::speedSkillDisplayNameProvider, true));
        addModifier(moveSpeedTypeModifier = new FloatAttributeModifier("6f685317-7be6-4ea8-ae63-b1c907209040", "move_speed_type", blockling, 0.0f, Operation.ADD, null, () -> blockling.variant.getName(), true));
        addModifier(moveSpeedBlocklingModifier = new ModifiableFloatAttributeModifier("f4300b1a-ee93-4d36-a457-4d71c349a4ab", "move_speed_blockling", blockling, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString(), true, moveSpeedTypeModifier));
        addAttribute(moveSpeed = new ModifiableFloatAttribute("9a0bb639-8543-4725-9be1-8a8ce688da70", "move_speed", blockling, 0.0f, null, null, true, moveSpeedBlocklingModifier, moveSpeedSkillSpeedModifier));

        addAttribute(miningRange = new ModifiableFloatAttribute("76e044ca-e73e-4004-b576-920a8446612d", "mining_range", blockling, 2.5f, null, null, true));
        addAttribute(miningRangeSq = new ModifiableFloatAttribute("55af3992-cf8d-4d5d-8634-fbc1e05d30fe", "mining_range_sq", blockling, miningRange.getValue() * miningRange.getValue(), null, null, true));
        addAttribute(woodcuttingRange = new ModifiableFloatAttribute("bc50cc2d-2323-4743-a175-5af87e61e04e", "woodcutting_range", blockling, 2.5f, null, null, true));
        addAttribute(woodcuttingRangeSq = new ModifiableFloatAttribute("8ba7fea6-6790-4010-b210-fa69b1effad8", "woodcutting_range_sq", blockling, woodcuttingRange.getValue() * woodcuttingRange.getValue(), null, null, true));
        addAttribute(farmingRange = new ModifiableFloatAttribute("c549a710-62d9-4d79-8d9d-ba3690752d08", "farming_range", blockling, 2.5f, null, null, true));
        addAttribute(farmingRangeSq = new ModifiableFloatAttribute("bc3a8f41-d033-437f-bce2-840df7a55fad", "farming_range_sq", blockling, farmingRange.getValue() * farmingRange.getValue(), null, null, true));

        // Default mining speed for an item/hand is 1.0f
        // A wooden pickaxe is 2.0f
        // A diamond pickaxe is 8.0f
        // Our default mining speed can be 0.0f as it will be determined by the level
        addModifier(miningSpeedSkillNightOwlModifier = new FloatAttributeModifier("f858c34f-d215-450a-847d-a54525d2f82f", "mining_speed_skill_night_owl", blockling, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(MiningSkills.NIGHT_OWL), false));
        addModifier(miningSpeedSkillHastyModifier = new FloatAttributeModifier("035c4e96-a628-4b20-9699-28ade0fa5a80", "mining_speed_skill_hasty", blockling, 10.0f, Operation.ADD, null, () -> skillDisplayNameProvider(MiningSkills.HASTY), false));
        addModifier(miningSpeedSkillMomentumModifier = new FloatAttributeModifier("1ca4d69f-05b8-4598-97c5-95f6bc750b7a", "mining_speed_skill_momentum", blockling, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(MiningSkills.MOMENTUM), false));
        addModifier(miningSpeedSkillAdrenalineModifier = new FloatAttributeModifier("1543fadc-3a9e-412b-819b-a6379a0911ca", "mining_speed_skill_adrenaline", blockling, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(MiningSkills.ADRENALINE), false));
        addModifier(miningSpeedSkillEfficiencyModifier = new FloatAttributeModifier("9464fc16-0f3c-438f-ac0b-8715a3542aaa", "mining_speed_skill_efficiency", blockling, 1.1f, Operation.MULTIPLY_TOTAL, (v) -> String.format("%.0f%%", (v - 1.0f) * 100.0f), () -> skillDisplayNameProvider(MiningSkills.EFFICIENCY), false));
        addModifier(miningSpeedOffHandModifier = new FloatAttributeModifier("aada86a0-4233-47cf-b5ab-aa208a216bb5", "mining_speed_off_hand", blockling, 0.0f, Operation.ADD, null, () -> blockling.getOffHandStack().toHoverableText().getString(), true));
        addModifier(miningSpeedMainHandModifier = new FloatAttributeModifier("fc0dd885-273b-4465-a9ff-e801dcaf07e2", "mining_speed_main_hand", blockling, 0.0f, Operation.ADD, null, () -> blockling.getMainHandStack().toHoverableText().getString(), true));
        addModifier(miningSpeedLevelModifier = new FloatAttributeModifier("f0914966-d53a-4292-b48c-5595f944f5d2", "mining_speed_level", blockling, 0.0f, Operation.ADD, null, miningLevel.displayStringNameSupplier, true));
        addModifier(miningSpeedTypeModifier = new FloatAttributeModifier("565dea40-53ac-4861-bc6c-1eafce77f80f", "mining_speed_type", blockling, 0.0f, Operation.ADD, null, () -> blockling.variant.getName(), true));
        addModifier(miningSpeedBlocklingModifier = new ModifiableFloatAttributeModifier("1e9d4f59-e3a5-410b-a3dd-c9dce952f22d", "mining_speed_blockling", blockling, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString(), true, miningSpeedTypeModifier, miningSpeedLevelModifier));
        addAttribute(miningSpeed = new ModifiableFloatAttribute("0d918c08-2e94-481b-98e1-c2ff3ae395de", "mining_speed", blockling, 0.0f, null, null, true, miningSpeedBlocklingModifier, miningSpeedMainHandModifier, miningSpeedOffHandModifier, miningSpeedSkillEfficiencyModifier, miningSpeedSkillAdrenalineModifier, miningSpeedSkillMomentumModifier, miningSpeedSkillHastyModifier, miningSpeedSkillNightOwlModifier));

        addModifier(woodcuttingSpeedSkillNightOwlModifier = new FloatAttributeModifier("adbe5ab0-4a08-4335-a68a-964b9126c40f", "woodcutting_speed_skill_night_owl", blockling, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(WoodcuttingSkills.NIGHT_OWL), false));
        addModifier(woodcuttingSpeedSkillHastyModifier = new FloatAttributeModifier("38709558-3da6-4f86-896f-195e84c18525", "woodcutting_speed_skill_hasty", blockling, 10.0f, Operation.ADD, null, () -> skillDisplayNameProvider(WoodcuttingSkills.HASTY), false));
        addModifier(woodcuttingSpeedSkillMomentumModifier = new FloatAttributeModifier("868bba90-a29c-4b70-b599-c3f56b00928e", "woodcutting_speed_skill_momentum", blockling, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(WoodcuttingSkills.MOMENTUM), false));
        addModifier(woodcuttingSpeedSkillAdrenalineModifier = new FloatAttributeModifier("e832c5da-0465-4a63-96b7-398eb32ba206", "woodcutting_speed_skill_adrenaline", blockling, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(WoodcuttingSkills.ADRENALINE), false));
        addModifier(woodcuttingSpeedSkillEfficiencyModifier = new FloatAttributeModifier("38a9d80c-4f96-4929-bd34-8c03156dec6d", "woodcutting_speed_skill_efficiency", blockling, 1.1f, Operation.MULTIPLY_TOTAL, (v) -> String.format("%.0f%%", (v - 1.0f) * 100.0f), () -> skillDisplayNameProvider(WoodcuttingSkills.EFFICIENCY), false));
        addModifier(woodcuttingSpeedOffHandModifier = new FloatAttributeModifier("80fd0028-a793-491c-bc9c-fe94071f91c7", "woodcutting_speed_off_hand", blockling, 0.0f, Operation.ADD, null, () -> blockling.getOffHandStack().toHoverableText().getString(), true));
        addModifier(woodcuttingSpeedMainHandModifier = new FloatAttributeModifier("978f9dd4-3fbb-41ee-9bba-eddcfb42b6ff", "woodcutting_speed_main_hand", blockling, 0.0f, Operation.ADD, null, () -> blockling.getMainHandStack().toHoverableText().getString(), true));
        addModifier(woodcuttingSpeedLevelModifier = new FloatAttributeModifier("6b71ee16-7d04-442e-9d49-9373833f5539", "woodcutting_speed_level", blockling, 0.0f, Operation.ADD, null, woodcuttingLevel.displayStringNameSupplier, true));
        addModifier(woodcuttingSpeedTypeModifier = new FloatAttributeModifier("a66110e2-3a7f-4907-b85c-05c65341cd2e", "woodcutting_speed_type", blockling, 0.0f, Operation.ADD, null, () -> blockling.variant.getName(), true));
        addModifier(woodcuttingSpeedBlocklingModifier = new ModifiableFloatAttributeModifier("51a21884-8c41-49d8-bae4-f21866b58718", "woodcutting_speed_blockling", blockling, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString(), true, woodcuttingSpeedTypeModifier, woodcuttingSpeedLevelModifier));
        addAttribute(woodcuttingSpeed = new ModifiableFloatAttribute("e1e3ecb3-ae1d-46c5-8ea8-a7180641910b", "woodcutting_speed", blockling, 0.0f, null, null, true, woodcuttingSpeedBlocklingModifier, woodcuttingSpeedMainHandModifier, woodcuttingSpeedOffHandModifier, woodcuttingSpeedSkillEfficiencyModifier, woodcuttingSpeedSkillAdrenalineModifier, woodcuttingSpeedSkillMomentumModifier, woodcuttingSpeedSkillHastyModifier, woodcuttingSpeedSkillNightOwlModifier));

        addModifier(farmingSpeedSkillNightOwlModifier = new FloatAttributeModifier("451f37a9-649e-44a3-a21f-d76e49f1afd0", "farming_speed_skill_night_owl", blockling, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(FarmingSkills.NIGHT_OWL), false));
        addModifier(farmingSpeedSkillHastyModifier = new FloatAttributeModifier("3f045e39-8185-4d2c-8980-54e06f8d548b", "farming_speed_skill_hasty", blockling, 10.0f, Operation.ADD, null, () -> skillDisplayNameProvider(FarmingSkills.HASTY), false));
        addModifier(farmingSpeedSkillMomentumModifier = new FloatAttributeModifier("d58d5336-d2fe-447d-a237-bb0bbff313d7", "farming_speed_skill_momentum", blockling, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(FarmingSkills.MOMENTUM), false));
        addModifier(farmingSpeedSkillAdrenalineModifier = new FloatAttributeModifier("79f7d5ff-2fd7-4385-a795-c281f984445b", "farming_speed_skill_adrenaline", blockling, 0.0f, Operation.ADD, null, () -> skillDisplayNameProvider(FarmingSkills.ADRENALINE), false));
        addModifier(farmingSpeedSkillEfficiencyModifier = new FloatAttributeModifier("792be316-19cb-49ec-a24a-ee224312c60f", "farming_speed_skill_efficiency", blockling, 1.1f, Operation.MULTIPLY_TOTAL, (v) -> String.format("%.0f%%", (v - 1.0f) * 100.0f), () -> skillDisplayNameProvider(FarmingSkills.EFFICIENCY), false));
        addModifier(farmingSpeedOffHandModifier = new FloatAttributeModifier("b4bb7131-f2ce-41cd-88ed-ee27e3837679", "farming_speed_off_hand", blockling, 0.0f, Operation.ADD, null, () -> blockling.getOffHandStack().toHoverableText().getString(), true));
        addModifier(farmingSpeedMainHandModifier = new FloatAttributeModifier("5ece4240-17b3-4983-bd0d-67f962a0a838", "farming_speed_main_hand", blockling, 0.0f, Operation.ADD, null, () -> blockling.getMainHandStack().toHoverableText().getString(), true));
        addModifier(farmingSpeedLevelModifier = new FloatAttributeModifier("3b3079cf-8640-436b-bf0a-3aae4deb29be", "farming_speed_level", blockling, 0.0f, Operation.ADD, null, farmingLevel.displayStringNameSupplier, true));
        addModifier(farmingSpeedTypeModifier = new FloatAttributeModifier("f99bbc83-30fc-451f-a53b-fa343cc9244a", "farming_speed_type", blockling, 0.0f, Operation.ADD, null, () -> blockling.variant.getName(), true));
        addModifier(farmingSpeedBlocklingModifier = new ModifiableFloatAttributeModifier("39548773-84ee-42ca-8ad6-681d64eaee54", "farming_speed_blockling", blockling, 0.0f, Operation.ADD, null, () -> blockling.getCustomName().getString(), true, farmingSpeedTypeModifier, farmingSpeedLevelModifier));
        addAttribute(farmingSpeed = new ModifiableFloatAttribute("f6c026b6-1fa9-432f-aca3-d97af784f6d0", "farming_speed", blockling, 0.0f, null, null, true, farmingSpeedBlocklingModifier, farmingSpeedMainHandModifier, farmingSpeedOffHandModifier, farmingSpeedSkillEfficiencyModifier, farmingSpeedSkillAdrenalineModifier, farmingSpeedSkillMomentumModifier, farmingSpeedSkillHastyModifier, farmingSpeedSkillNightOwlModifier));
    } // Constructor BlocklingAttributes ()

    /**
     * Applies the value of a vanilla attribute modifier on the given vanilla attribute.
     * Vanilla modifier values are not mutable so they need to be reapplied each time the value changes.
     *
     * @param vanillaAttribute the vanilla attribute to update.
     * @param modifier         the modifier to apply to the vanilla attribute.
     */
    public void applyVanillaModifier(EntityAttribute vanillaAttribute, FloatAttributeModifier modifier) {
        EntityAttributeInstance attributeInstance = blockling.getAttributeInstance(vanillaAttribute);

        // Remove the modifier it exists as you can't just set the value again.
        attributeInstance.removeModifier(modifier.id);

        // Do not apply the modifier if it is not enabled.
        if (modifier.isEnabled()) {
            // Add the attribute modifier with the current value.
            attributeInstance.addPersistentModifier(new EntityAttributeModifier(modifier.id, modifier.getDisplayStringNameSupplier().get(), modifier.getValue(), Operation.vanillaOperation(modifier.getOperation())));
        }
    }

    /**
     * Used to produce a display name for an attribute relating to a skill.
     *
     * @param skillInfo the relate skill.
     * @return the display string.
     */
    private String skillDisplayNameProvider(SkillInfo skillInfo) {
        return skillInfo.general.name.getString() + " (" + LovelyRobotID.getTranslation("skill.name").getString() + ")";
    }

    private String speedSkillDisplayNameProvider() {
        if (blockling.getSkills().getSkill(GeneralSkills.SPEED_3).isBought()) {
            return skillDisplayNameProvider(GeneralSkills.SPEED_3);
        } else if (blockling.getSkills().getSkill(GeneralSkills.SPEED_2).isBought()) {
            return skillDisplayNameProvider(GeneralSkills.SPEED_2);
        }

        return skillDisplayNameProvider(GeneralSkills.SPEED_1);
    }

    /**
     * Adds the given attribute to the attributes list.
     *
     * @param attribute the attribute to add.
     */
    public void addAttribute(Attribute<?> attribute) {
        attributes.add(attribute);
    }

    /**
     * Adds the given modifier to the attributes and modifiers lists.
     *
     * @param modifier the modifier to add.
     */
    public <V, T extends Attribute<V> & IModifier<V>> void addModifier(T modifier) {
        attributes.add(modifier);
        modifiers.add(modifier);
    }

    /**
     * Initialised the attributes by calling any calculate methods and update callbacks.
     */
    public void init() {
        for (Attribute<?> attribute : attributes) {
            if (attribute instanceof IModifiable<?>) {
                ((ModifiableAttribute<?>) attribute).calculate();
            }
        }

        for (Attribute<?> attribute : attributes) {
            attribute.onValueChanged();
        }

        updateCombatLevelBonuses(false);
        //updateTypeBonuses(false);
    }

    /**
     * Initialises any update callbacks for attributes.
     */
    public void initUpdateCallbacks() {
        combatLevel.addUpdateCallback((i) -> {
            updateCombatLevelBonuses(false);
            updateOnLevelChange(Level.COMBAT);
        });
        miningLevel.addUpdateCallback((i) -> {
            miningSpeedLevelModifier.setValue(calcBonusHarvestSpeedFromLevel(i), false);
            updateOnLevelChange(Level.MINING);
        });
        woodcuttingLevel.addUpdateCallback((i) -> {
            woodcuttingSpeedLevelModifier.setValue(calcBonusHarvestSpeedFromLevel(i), false);
            updateOnLevelChange(Level.WOODCUTTING);
        });
        farmingLevel.addUpdateCallback((i) -> {
            farmingSpeedLevelModifier.setValue(calcBonusHarvestSpeedFromLevel(i), false);
            updateOnLevelChange(Level.FARMING);
        });
        combatXp.addUpdateCallback((i) -> checkForLevelUpAndUpdate(false));
        miningXp.addUpdateCallback((i) -> checkForLevelUpAndUpdate(false));
        woodcuttingXp.addUpdateCallback((i) -> checkForLevelUpAndUpdate(false));
        farmingXp.addUpdateCallback((i) -> checkForLevelUpAndUpdate(false));
        maxHealth.addUpdateCallback((f) -> {
            Objects.requireNonNull(blockling.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).setBaseValue(f);
            checkAndCapHealth();
        });
        attackSpeed.addUpdateCallback((f) -> Objects.requireNonNull(blockling.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED)).setBaseValue(f));
        armour.addUpdateCallback((f) -> Objects.requireNonNull(blockling.getAttributeInstance(EntityAttributes.GENERIC_ARMOR)).setBaseValue(f));
        armourToughness.addUpdateCallback((f) -> Objects.requireNonNull(blockling.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)).setBaseValue(f));
        knockbackResistance.addUpdateCallback((f) -> Objects.requireNonNull(blockling.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)).setBaseValue(f));
        moveSpeed.addUpdateCallback((f) -> Objects.requireNonNull(blockling.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(f / 10.0f));
        miningRange.addUpdateCallback((f) -> miningRangeSq.setBaseValue(miningRange.getValue() * miningRange.getValue(), false));
        woodcuttingRange.addUpdateCallback((f) -> woodcuttingRangeSq.setBaseValue(woodcuttingRange.getValue() * woodcuttingRange.getValue(), false));
        farmingRange.addUpdateCallback((f) -> farmingRangeSq.setBaseValue(farmingRange.getValue() * farmingRange.getValue(), false));
    }

    /**
     * Updates the health of the blockling and xp for the given level in case it is over the threshold after decreasing.
     */
    private void updateOnLevelChange(Level level) {
        IntAttribute xpAttribute = (IntAttribute) getLevelXpAttribute(level);
        int currentXp = xpAttribute.getValue();
        int targetXp = BlocklingAttributes.getXpForLevel(getLevelAttribute(level).getValue());

        if (currentXp >= targetXp) {
            xpAttribute.setValue(targetXp - 1, false);
        }

        if (!blockling.getWorld().isClient) {
            if (blockling.getHealth() > blockling.getMaxHealth()) {
                blockling.setHealth(blockling.getMaxHealth());
            }
        }
    }

    @Override
    public NbtCompound writeToNBT(NbtCompound attributesTag) {
        for (Attribute<?> attribute : attributes) {
            NbtCompound attributeTag = new NbtCompound();

            attribute.writeToNBT(attributeTag);

            attributesTag.put(attribute.id.toString(), attributeTag);
        }

        return attributesTag;
    }

    @Override
    public void readFromNBT(NbtCompound attributesTag, Version tagVersion) {
        for (Attribute<?> attribute : attributes) {
            NbtCompound attributeTag = (NbtCompound) attributesTag.get(attribute.id.toString());

            if (attributeTag != null) {
                attribute.readFromNBT(attributeTag, tagVersion);
            }
        }

        init();
    }

    /**
     * Writes all the attributes to the given buffer.
     *
     * @param buf the buffer to write to.
     */
    public void encode(PacketByteBuf buf) {
        for (Attribute<?> attribute : attributes) {
            attribute.encode(buf);
        }
    }

    /**
     * Reads all the attributes from the given buffer.
     *
     * @param buf the buffer to read from.
     */
    public void decode(PacketByteBuf buf) {
        for (Attribute<?> attribute : attributes) {
            attribute.decode(buf);
        }

        init();
    }

    /**
     * @param level the level to enquire about.
     * @return the xp needed to reach the next level.
     */
    public static int getXpForLevel(int level) {
        return (int) (Math.exp(level / 25.0) * 40) - 30;
    }

    /**
     * Checks each level to see if the target xp has been reached and updates the state accordingly.
     *
     * @param sync whether to sync any resulting changes to the client/server.
     */
    public void checkForLevelUpAndUpdate(boolean sync) {
        if (combatLevel.getValue() < Level.MAX) {
            int combatLevel = this.combatLevel.getValue();
            int combatXp = this.combatXp.getValue();
            int combatXpReq = getXpForLevel(combatLevel);
            if (combatXp >= combatXpReq) {
                this.combatLevel.setValue(combatLevel + 1, sync);
                this.combatXp.setValue(combatXp - combatXpReq, sync);
            }
        }

        if (miningLevel.getValue() < Level.MAX) {
            int miningLevel = this.miningLevel.getValue();
            int miningXp = this.miningXp.getValue();
            int miningXpReq = getXpForLevel(miningLevel);
            if (miningXp >= miningXpReq) {
                this.miningLevel.setValue(miningLevel + 1, sync);
                this.miningXp.setValue(miningXp - miningXpReq, sync);
            }
        }

        if (woodcuttingLevel.getValue() < Level.MAX) {
            int woodcuttingLevel = this.woodcuttingLevel.getValue();
            int woodcuttingXp = this.woodcuttingXp.getValue();
            int woodcuttingXpReq = getXpForLevel(woodcuttingLevel);
            if (woodcuttingXp >= woodcuttingXpReq) {
                this.woodcuttingLevel.setValue(woodcuttingLevel + 1, sync);
                this.woodcuttingXp.setValue(woodcuttingXp - woodcuttingXpReq, sync);
            }
        }

        if (farmingLevel.getValue() < Level.MAX) {
            int farmingLevel = this.farmingLevel.getValue();
            int farmingXp = this.farmingXp.getValue();
            int farmingXpReq = getXpForLevel(farmingLevel);
            if (farmingXp >= farmingXpReq) {
                this.farmingLevel.setValue(farmingLevel + 1, sync);
                this.farmingXp.setValue(farmingXp - farmingXpReq, sync);
            }
        }
    }

    /**
     * Updates the bonuses provided by the combat level.
     *
     * @param sync whether to sync the changes to the client/server.
     */
    public void updateCombatLevelBonuses(boolean sync) {
        maxHealthCombatLevelModifier.setValue(calcBonusHealthFromCombatLevel(), sync);
        attackDamageCombatLevelModifier.setValue(calcBonusDamageFromCombatLevel(), sync);
        attackSpeedLevelModifier.setValue(calcBonusAttackSpeedFromLevel(combatLevel.getValue()), sync);
        armourCombatLevelModifier.setValue(calcBonusArmourFromCombatLevel(), sync);
        armourToughnessCombatLevelModifier.setValue(calcBonusArmourToughnessFromCombatLevel(), sync);
        knockbackResistanceCombatLevelModifier.setValue(calcBonusKnockbackResistanceFromCombatLevel(), sync);
    }

    /**
     * Updates the bonuses provided by the blockling type.
     *
     * @param sync whether to sync the changes to the client/server.
     */
    /*public void updateTypeBonuses(boolean sync) {
        BlocklingType type = blockling.getBlocklingType();
        maxHealthTypeModifier.setValue(type.getMaxHealth(), sync);
        attackDamageTypeModifier.setValue(type.getAttackDamage(), sync);
        attackSpeedTypeModifier.setValue(type.getAttackSpeed(), sync);
        armourTypeModifier.setValue(type.getArmour(), sync);
        armourToughnessTypeModifier.setValue(type.getArmourToughness(), sync);
        knockbackResistanceTypeModifier.setValue(type.getKnockbackResistance(), sync);
        moveSpeedTypeModifier.setValue(type.getMoveSpeed(), sync);
        miningSpeedTypeModifier.setValue(type.getMiningSpeed(), sync);
        woodcuttingSpeedTypeModifier.setValue(type.getWoodcuttingSpeed(), sync);
        farmingSpeedTypeModifier.setValue(type.getFarmingSpeed(), sync);
    }*/

    /**
     * @param level the level to enquire about.
     * @return the block break speed for that level.
     */
    private float calcBonusHarvestSpeedFromLevel(int level) {
        return (float) (10.0f * Math.tan((level / (float) Level.MAX) * (Math.PI / 4.0f)));
    }

    /**
     * @return the bonus health provided by the current combat level.
     */
    private float calcBonusHealthFromCombatLevel() {
        return (float) (50.0f * Math.tan((combatLevel.getValue() / (float) Level.MAX) * (Math.PI / 4.0f)));
    }

    /**
     * @return the bonus damage provided by the current combat level.
     */
    private float calcBonusDamageFromCombatLevel() {
        return (float) (20.0f * Math.tan((combatLevel.getValue() / (float) Level.MAX) * (Math.PI / 4.0f)));
    }

    /**
     * @param level the level to enquire about.
     * @return the bonus attack speed for that level.
     */
    private float calcBonusAttackSpeedFromLevel(int level) {
        return (float) (5.0f * Math.tan((level / (float) Level.MAX) * (Math.PI / 4.0f)));
    }

    /**
     * @return the bonus armour provided by the current combat level.
     */
    private float calcBonusArmourFromCombatLevel() {
        return (float) (10.0f * Math.tan((combatLevel.getValue() / (float) Level.MAX) * (Math.PI / 4.0f)));
    }

    /**
     * @return the bonus armour toughness provided by the current combat level.
     */
    private float calcBonusArmourToughnessFromCombatLevel() {
        return (float) (5.0f * Math.tan((combatLevel.getValue() / (float) Level.MAX) * (Math.PI / 4.0f)));
    }

    /**
     * @return the bonus knockback resistance provided by the current combat level.
     */
    private float calcBonusKnockbackResistanceFromCombatLevel() {
        return (float) (0.5f * Math.tan((combatLevel.getValue() / (float) Level.MAX) * (Math.PI / 4.0f)));
    }

    /**
     * Ensures the blockling's health doesn't exceed its max health.
     * Can happen the max health is lowered below the current health.
     */
    public void checkAndCapHealth() {
        if (blockling.getHealth() > blockling.getMaxHealth()) {
            blockling.setHealth(blockling.getMaxHealth());
        }
    }

    /**
     * @return the blockling's health as an integer rounded up.
     */
    public int getHealth() {
        return (int) Math.ceil(blockling.getHealth());
    }

    /**
     * @return the blockling's max health as an integer rounded up.
     */
    public int getMaxHealth() {
        return (int) Math.ceil(blockling.getMaxHealth());
    }

    /**
     * @return the blockling's health as a percentage of its max health.
     */
    public float getHealthPercentage() {
        return blockling.getHealth() / blockling.getMaxHealth();
    }

    /**
     * @param level the level to enquire about.
     * @return the corresponding attribute for that level.
     */
    public Attribute<Integer> getLevelAttribute(Level level) {
        return switch (level) {
            case COMBAT -> combatLevel;
            case MINING -> miningLevel;
            case WOODCUTTING -> woodcuttingLevel;
            case FARMING -> farmingLevel;
            default -> totalLevel;
        };
    }

    /**
     * @param level the level to enquire about.
     * @return the corresponding xp attribute for that level.
     */
    public Attribute<Integer> getLevelXpAttribute(Level level) {
        return switch (level) {
            case COMBAT -> combatXp;
            case MINING -> miningXp;
            case WOODCUTTING -> woodcuttingXp;
            case FARMING -> farmingXp;
            default -> totalLevel;
        };
    }

} // Class BlocklingAttributes