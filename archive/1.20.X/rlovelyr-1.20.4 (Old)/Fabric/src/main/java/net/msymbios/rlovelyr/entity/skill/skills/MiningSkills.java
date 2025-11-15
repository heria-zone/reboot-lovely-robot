package net.msymbios.rlovelyr.entity.skill.skills;

import net.msymbios.rlovelyr.client.gui.texture.Textures;
import net.msymbios.rlovelyr.entity.attribute.enums.Level;
import net.msymbios.rlovelyr.entity.skill.info.*;
import net.msymbios.rlovelyr.entity.skill.*;

import java.util.*;
import java.util.function.Function;

/**
 * The mining skills.
 */
public class MiningSkills {
    public static final SkillInfo NOVICE_MINER = new SkillInfo("dcbf7cc1-8be-49aa-a5a0-cd70cb40cbac",
            new SkillGeneralInfo(Skill.Type.AI, "mining.novice_miner"),
            new SkillDefaultsInfo(Skill.State.UNLOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
            }}),
            new SkillGuiInfo(0, 0, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xdddddd, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Mining.ICONS, 0, 0))) {
        @Override
        public void onBuy(Skill skill) {
            // TODO: Uncomment when blockling tasks are implemented
            //skill.blockling.getTasks().setIsUnlocked(BlocklingTasks.MINE, true);
        }
    };

    public static final SkillInfo WHITELIST = new SkillInfo("8963cddd-06dd-4b5a-8c1e-b1e38a99b25f",
            new SkillGeneralInfo(Skill.Type.OTHER, "mining.whitelist"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.MINING, 5);
            }}),
            new SkillGuiInfo(0, 70, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xe0f1ff, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Mining.ICONS, 1, 0))) {
        @Override
        public void onBuy(Skill skill) {
            BlocklingSkills.unlockExistingWhitelists(skill, "24d7135e-607b-413b-a2a7-00d19119b9de");
        }

        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(NOVICE_MINER);
        }
    };

    public static final SkillInfo EFFICIENCY = new SkillInfo("19253148-ff6e-4395-9464-289e081b442b",
            new SkillGeneralInfo(Skill.Type.STAT, "mining.efficiency"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.MINING, 10);
            }}),
            new SkillGuiInfo(70, 0, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xffd56d, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Mining.ICONS, 2, 0))) {
        @Override
        public void onBuy(Skill skill) {
            skill.blockling.getStats().miningSpeedSkillEfficiencyModifier.setIsEnabled(true, false);
        }

        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(NOVICE_MINER);
        }
    };

    public static final SkillInfo ADRENALINE = new SkillInfo("9cd9212d-0f3b-47c3-85f1-9fe18388a42b",
            new SkillGeneralInfo(Skill.Type.STAT, "mining.adrenaline"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.MINING, 25);
            }}),
            new SkillGuiInfo(140, -50, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xb72626, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Mining.ICONS, 3, 0))) {
        @Override
        public void onBuy(Skill skill) {
            skill.blockling.getStats().miningSpeedSkillAdrenalineModifier.setIsEnabled(true, false);
        }

        @Override
        public void tick(Skill skill) {
            skill.blockling.getStats().miningSpeedSkillAdrenalineModifier.setValue(10.0f * (1.0f - ((Math.max(skill.blockling.getHealth() - 1.0f, 0.0f)) / (skill.blockling.getMaxHealth() - 1.0f))), false);
        }

        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(EFFICIENCY);
        }

        @Override
        public List<SkillInfo> conflicts() {
            return Arrays.asList(MOMENTUM, HASTY, NIGHT_OWL);
        }
    };

    public static final SkillInfo MOMENTUM = new SkillInfo("656aaacb-3c87-4b36-b12c-6ab970f09279",
            new SkillGeneralInfo(Skill.Type.STAT, "mining.momentum"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.MINING, 25);
            }}),
            new SkillGuiInfo(140, 50, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xad79b5, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Mining.ICONS, 4, 0))) {
        @Override
        public void onBuy(Skill skill) {
            skill.blockling.getStats().miningSpeedSkillMomentumModifier.setIsEnabled(true, false);
        }

        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(EFFICIENCY);
        }

        @Override
        public List<SkillInfo> conflicts() {
            return Arrays.asList(ADRENALINE, HASTY, NIGHT_OWL);
        }
    };

    public static final SkillInfo HASTY = new SkillInfo("1cfca8ba-d518-4403-b0ed-8da83e350de3",
            new SkillGeneralInfo(Skill.Type.STAT, "mining.hasty"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.MINING, 25);
            }}),
            new SkillGuiInfo(210, -50, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x4eb2aa, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Mining.ICONS, 5, 0))) {
        @Override
        public void onBuy(Skill skill) {
            skill.blockling.getStats().miningSpeedSkillHastyModifier.setIsEnabled(true, false);
        }

        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(EFFICIENCY);
        }

        @Override
        public List<SkillInfo> conflicts() {
            return Arrays.asList(ADRENALINE, MOMENTUM, NIGHT_OWL);
        }
    };

    public static final SkillInfo NIGHT_OWL = new SkillInfo("1a2fca9b-c745-4274-9f35-a577dfe65c8d",
            new SkillGeneralInfo(Skill.Type.STAT, "mining.night_owl"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.MINING, 25);
            }}),
            new SkillGuiInfo(210, 50, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x2b2a3d, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Mining.ICONS, 6, 0))) {
        @Override
        public void onBuy(Skill skill) {
            skill.blockling.getStats().miningSpeedSkillNightOwlModifier.setIsEnabled(true, false);
        }

        @Override
        public void tick(Skill skill) {
            if (!skill.blockling.getWorld().isClient) {
                float value = 15.0f * (1.0f - (skill.blockling.getWorld().getLightLevel(skill.blockling.getBlockPos())) / 15.0f);

                if (value != skill.blockling.getStats().miningSpeedSkillNightOwlModifier.getValue()) {
                    skill.blockling.getStats().miningSpeedSkillNightOwlModifier.setValue(value, true);
                }
            }
        }

        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(EFFICIENCY);
        }

        @Override
        public List<SkillInfo> conflicts() {
            return Arrays.asList(ADRENALINE, MOMENTUM, HASTY);
        }
    };

    public static final SkillInfo HOT_HANDS = new SkillInfo("6ddccec1-af7b-4e8a-90a0-3962ef422858",
            new SkillGeneralInfo(Skill.Type.OTHER, "mining.hot_hands"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.MINING, 40);
            }}),
            new SkillGuiInfo(-70, 0, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xdd3355, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Mining.ICONS, 7, 0))) {
        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(NOVICE_MINER);
        }
    };

    public static final SkillInfo HAMMER = new SkillInfo("f3f2a413-5324-4a87-9d5f-83a5a2df0b5e",
            new SkillGeneralInfo(Skill.Type.OTHER, "mining.hammer"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.MINING, 50);
            }}),
            new SkillGuiInfo(0, -70, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x6b4e49, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Mining.ICONS, 8, 0))) {
        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(NOVICE_MINER);
        }
    };

    public static final List<Function<SkillGroup, Skill>> SKILLS = new ArrayList<Function<SkillGroup, Skill>>() {{
        add(group -> new Skill(NOVICE_MINER, group));
        add(group -> new Skill(WHITELIST, group));
        add(group -> new Skill(EFFICIENCY, group));
        add(group -> new Skill(ADRENALINE, group));
        add(group -> new Skill(MOMENTUM, group));
        add(group -> new Skill(HASTY, group));
        add(group -> new Skill(NIGHT_OWL, group));
        add(group -> new Skill(HOT_HANDS, group));
        add(group -> new Skill(HAMMER, group));
    }};
}
