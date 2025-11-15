package net.msymbios.rlovelyr.entity.skill.skills;

import net.msymbios.rlovelyr.client.gui.texture.Textures;
import net.msymbios.rlovelyr.entity.attribute.enums.Level;
import net.msymbios.rlovelyr.entity.skill.BlocklingSkills;
import net.msymbios.rlovelyr.entity.skill.Skill;
import net.msymbios.rlovelyr.entity.skill.SkillGroup;
import net.msymbios.rlovelyr.entity.skill.info.*;

import java.util.*;
import java.util.function.Function;

/**
 * The combat skills.
 */
public class CombatSkills {

    // -- Variables --

    public static final SkillInfo NOVICE_GUARD = new SkillInfo("dcbf7cc1-8bef-49aa-a5a0-cd70cb40cbac",
            new SkillGeneralInfo(Skill.Type.AI, "combat.novice_guard"),
            new SkillDefaultsInfo(Skill.State.UNLOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
            }}),
            new SkillGuiInfo(0, 0, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xdddddd, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Combat.ICONS, 0, 0))) {

        @Override
        public void onBuy(Skill skill) {
            // TODO: Uncomment when task is implemented
            //skill.blockling.getTasks().setIsUnlocked(BlocklingTasks.MELEE_ATTACK_OWNER_HURT_BY, true);
            //skill.blockling.getTasks().setIsUnlocked(BlocklingTasks.MELEE_ATTACK_OWNER_HURT, true);
        }
    };

    public static final SkillInfo WHITELIST = new SkillInfo("9065aad7-70df-4ae1-88f3-40ef702b7212",
            new SkillGeneralInfo(Skill.Type.OTHER, "combat.whitelist"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.COMBAT, 5);
            }}),
            new SkillGuiInfo(0, 70, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xe0f1ff, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Combat.ICONS, 1, 0))) {
        @Override
        public void onBuy(Skill skill) {
            BlocklingSkills.unlockExistingWhitelists(skill, "540241cd-085a-4c1f-9e90-8aea973568a8");
        }

        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(NOVICE_GUARD);
        }
    };

    public static final SkillInfo SHARPNESS = new SkillInfo("1aac1132-6cde-4c1d-8292-1cffe93a7f5a",
            new SkillGeneralInfo(Skill.Type.STAT, "combat.sharpness"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.COMBAT, 10);
            }}),
            new SkillGuiInfo(70, 0, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xffd56d, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Combat.ICONS, 2, 0))) {
        @Override
        public void onBuy(Skill skill) {
            skill.blockling.getStats().attackDamageSkillSharpnessModifier.setIsEnabled(true, false);
        }

        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(NOVICE_GUARD);
        }
    };

    public static final SkillInfo BERSERKER = new SkillInfo("dfcfc9df-608b-4d6e-b6a7-ba20c55191b6",
            new SkillGeneralInfo(Skill.Type.STAT, "combat.berserker"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.COMBAT, 25);
            }}),
            new SkillGuiInfo(140, -50, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xb72626, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Combat.ICONS, 3, 0))) {
        @Override
        public void onBuy(Skill skill) {
            skill.blockling.getStats().attackDamageSkillBerserkerModifier.setIsEnabled(true, false);
        }

        @Override
        public void tick(Skill skill) {
            skill.blockling.getStats().attackDamageSkillBerserkerModifier.setValue(10.0f * (1.0f - ((Math.max(skill.blockling.getHealth() - 1.0f, 0.0f)) / (skill.blockling.getMaxHealth() - 1.0f))), false);
        }

        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(SHARPNESS);
        }

        @Override
        public List<SkillInfo> conflicts() {
            return Arrays.asList(MOMENTUM, WRECKLESS, PHOTOPHILE);
        }
    };

    public static final SkillInfo MOMENTUM = new SkillInfo("31c6169c-605a-4d88-b4ea-71210afcd028",
            new SkillGeneralInfo(Skill.Type.STAT, "combat.momentum"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.COMBAT, 25);
            }}),
            new SkillGuiInfo(140, 50, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xad79b5, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Combat.ICONS, 4, 0))) {
        @Override
        public void onBuy(Skill skill) {
            skill.blockling.getStats().attackSpeedSkillMomentumModifier.setIsEnabled(true, false);
        }

        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(SHARPNESS);
        }

        @Override
        public List<SkillInfo> conflicts() {
            return Arrays.asList(BERSERKER, WRECKLESS, PHOTOPHILE);
        }
    };

    public static final SkillInfo WRECKLESS = new SkillInfo("662b0efa-e150-44c7-aaf8-2cf61bcef330",
            new SkillGeneralInfo(Skill.Type.STAT, "combat.wreckless"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.COMBAT, 25);
            }}),
            new SkillGuiInfo(210, -50, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x4eb2aa, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Combat.ICONS, 5, 0))) {
        @Override
        public void onBuy(Skill skill) {
            skill.blockling.getStats().attackDamageSkillWrecklessModifier.setIsEnabled(true, false);
        }

        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(SHARPNESS);
        }

        @Override
        public List<SkillInfo> conflicts() {
            return Arrays.asList(BERSERKER, MOMENTUM, PHOTOPHILE);
        }
    };

    public static final SkillInfo PHOTOPHILE = new SkillInfo("07ccf340-a3c1-406e-81fc-3622ee0e9463",
            new SkillGeneralInfo(Skill.Type.STAT, "combat.photophile"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.COMBAT, 25);
            }}),
            new SkillGuiInfo(210, 50, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xd3b630, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Combat.ICONS, 6, 0))) {
        @Override
        public void onBuy(Skill skill) {
            skill.blockling.getStats().attackSpeedSkillPhotophileModifier.setIsEnabled(true, false);
        }

        @Override
        public void tick(Skill skill) {
            if (!skill.blockling.getWorld().isClient) {
                float value = 5.0f * (skill.blockling.getWorld().getLightLevel(skill.blockling.getBlockPos()) / 15.0f);
                if (value != skill.blockling.getStats().attackSpeedSkillPhotophileModifier.getValue()) {
                    skill.blockling.getStats().attackSpeedSkillPhotophileModifier.setValue(value, true);
                }
            }
        }

        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(SHARPNESS);
        }

        @Override
        public List<SkillInfo> conflicts() {
            return Arrays.asList(BERSERKER, MOMENTUM, WRECKLESS);
        }
    };

    public static final SkillInfo REGENERATION_1 = new SkillInfo("0b1a72e4-486a-46a6-80f3-4a98694d99ea",
            new SkillGeneralInfo(Skill.Type.OTHER, "combat.regeneration_1"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.COMBAT, 10);
            }}),
            new SkillGuiInfo(-140, 0, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xabce61, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Combat.ICONS, 7, 0))) {
        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(NOVICE_GUARD);
        }
    };

    public static final SkillInfo REGENERATION_2 = new SkillInfo("f790d1a7-f680-4ea1-a3e1-18bac656614d",
            new SkillGeneralInfo(Skill.Type.OTHER, "combat.regeneration_2"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.COMBAT, 30);
            }}),
            new SkillGuiInfo(-210, 0, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xc4ff47, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Combat.ICONS, 8, 0))) {
        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(REGENERATION_1);
        }
    };

    public static final SkillInfo REGENERATION_3 = new SkillInfo("1330ddfe-9405-4bde-b870-831b999bce18",
            new SkillGeneralInfo(Skill.Type.OTHER, "combat.regeneration_3"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.COMBAT, 50);
            }}),
            new SkillGuiInfo(-280, 0, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x75ff35, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Combat.ICONS, 9, 0))) {
        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(REGENERATION_2);
        }
    };

    public static final SkillInfo HUNTER = new SkillInfo("c08277df-5985-4d24-985c-f6d6cb579082",
            new SkillGeneralInfo(Skill.Type.AI, "combat.hunter"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.COMBAT, 25);
            }}),
            new SkillGuiInfo(0, -140, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x8e0000, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Combat.ICONS, 0, 1))) {
        @Override
        public void onBuy(Skill skill) {
            // TODO: Uncomment once blockling tasks are implemented
            //skill.blockling.getTasks().setIsUnlocked(BlocklingTasks.MELEE_ATTACK_HUNT, true);
        }

        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(NOVICE_GUARD);
        }
    };

    public static final SkillInfo ANIMAL_HUNTER = new SkillInfo("a623122a-04f2-41a1-ac70-c5cb7fb04ee4",
            new SkillGeneralInfo(Skill.Type.STAT, "combat.animal_hunter"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.COMBAT, 50);
            }}),
            new SkillGuiInfo(-70, -210, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xdb6da9, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Combat.ICONS, 1, 1))) {
        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(HUNTER);
        }

        @Override
        public List<SkillInfo> conflicts() {
            return Collections.singletonList(MONSTER_HUNTER);
        }
    };

    public static final SkillInfo MONSTER_HUNTER = new SkillInfo("7af5b098-eada-40b5-9c57-595a436fcd47",
            new SkillGeneralInfo(Skill.Type.STAT, "combat.monster_hunter"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.COMBAT, 50);
            }}),
            new SkillGuiInfo(70, -210, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x1e681f, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Combat.ICONS, 2, 1))) {
        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(HUNTER);
        }

        @Override
        public List<SkillInfo> conflicts() {
            return Collections.singletonList(ANIMAL_HUNTER);
        }
    };

    public static final SkillInfo POISON_ATTACKS = new SkillInfo("f0c1678b-712d-4496-9227-1edceca022b2",
            new SkillGeneralInfo(Skill.Type.OTHER, "combat.poison_attacks"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.COMBAT, 30);
            }}),
            new SkillGuiInfo(-70, -70, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x3a6e18, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Combat.ICONS, 3, 1))) {
        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(NOVICE_GUARD);
        }

        @Override
        public List<SkillInfo> conflicts() {
            return Collections.singletonList(WITHER_ATTACKS);
        }
    };

    public static final SkillInfo WITHER_ATTACKS = new SkillInfo("81e85a99-9167-4138-8733-3870fbffeb45",
            new SkillGeneralInfo(Skill.Type.OTHER, "combat.wither_attacks"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.COMBAT, 30);
            }}),
            new SkillGuiInfo(-70, 70, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x2d2122, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Combat.ICONS, 4, 1))) {
        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(NOVICE_GUARD);
        }

        @Override
        public List<SkillInfo> conflicts() {
            return Collections.singletonList(POISON_ATTACKS);
        }
    };

    public static final List<Function<SkillGroup, Skill>> SKILLS = new ArrayList<Function<SkillGroup, Skill>>() {{
        add(group -> new Skill(NOVICE_GUARD, group));
        add(group -> new Skill(WHITELIST, group));
        add(group -> new Skill(SHARPNESS, group));
        add(group -> new Skill(BERSERKER, group));
        add(group -> new Skill(MOMENTUM, group));
        add(group -> new Skill(WRECKLESS, group));
        add(group -> new Skill(PHOTOPHILE, group));
        add(group -> new Skill(REGENERATION_1, group));
        add(group -> new Skill(REGENERATION_2, group));
        add(group -> new Skill(REGENERATION_3, group));
        add(group -> new Skill(HUNTER, group));
        add(group -> new Skill(ANIMAL_HUNTER, group));
        add(group -> new Skill(MONSTER_HUNTER, group));
        add(group -> new Skill(POISON_ATTACKS, group));
        add(group -> new Skill(WITHER_ATTACKS, group));
    }};

} // Class CombatSkills