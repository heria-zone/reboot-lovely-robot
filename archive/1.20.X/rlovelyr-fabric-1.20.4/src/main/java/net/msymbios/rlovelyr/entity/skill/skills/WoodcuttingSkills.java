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
 * The woodcutting skills.
 */
public class WoodcuttingSkills {
    public static final SkillInfo NOVICE_LUMBERJACK = new SkillInfo("c70f6e84-b82f-4a2d-8cbf-5914c589e8b6",
            new SkillGeneralInfo(Skill.Type.AI, "woodcutting.novice_lumberjack"),
            new SkillDefaultsInfo(Skill.State.UNLOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
            }}),
            new SkillGuiInfo(0, 0, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xdddddd, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Woodcutting.ICONS, 0, 0))) {
        @Override
        public void onBuy(Skill skill) {
            // TODO: Uncomment when blockling tasks are implemented
            //skill.blockling.getTasks().setIsUnlocked(BlocklingTasks.WOODCUT, true, false);
        }
    };

    public static final SkillInfo WHITELIST = new SkillInfo("6c1c96c3-c784-4022-bcdd-432618f5d33d",
            new SkillGeneralInfo(Skill.Type.OTHER, "woodcutting.whitelist"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.WOODCUTTING, 5);
            }}),
            new SkillGuiInfo(0, 70, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xe0f1ff, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Woodcutting.ICONS, 1, 0))) {
        @Override
        public void onBuy(Skill skill) {
            BlocklingSkills.unlockExistingWhitelists(skill, "fbfbfd44-c1b0-4420-824a-270b34c866f7");
        }

        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(NOVICE_LUMBERJACK);
        }
    };

    public static final SkillInfo EFFICIENCY = new SkillInfo("3dae8614-511c-4378-9c8a-2ae0d3cddc97",
            new SkillGeneralInfo(Skill.Type.STAT, "woodcutting.efficiency"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.WOODCUTTING, 10);
            }}),
            new SkillGuiInfo(70, 0, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xffd56d, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Woodcutting.ICONS, 2, 0))) {
        @Override
        public void onBuy(Skill skill) {
            skill.blockling.getStats().woodcuttingSpeedSkillEfficiencyModifier.setIsEnabled(true, false);
        }

        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(NOVICE_LUMBERJACK);
        }
    };

    public static final SkillInfo ADRENALINE = new SkillInfo("bdb58fa2-174e-4be6-880b-c355ee76aab6",
            new SkillGeneralInfo(Skill.Type.STAT, "woodcutting.adrenaline"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.WOODCUTTING, 25);
            }}),
            new SkillGuiInfo(140, -50, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xb72626, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Woodcutting.ICONS, 3, 0))) {
        @Override
        public void onBuy(Skill skill) {
            skill.blockling.getStats().woodcuttingSpeedSkillAdrenalineModifier.setIsEnabled(true, false);
        }

        @Override
        public void tick(Skill skill) {
            skill.blockling.getStats().woodcuttingSpeedSkillAdrenalineModifier.setValue(10.0f * (1.0f - ((Math.max(skill.blockling.getHealth() - 1.0f, 0.0f)) / (skill.blockling.getMaxHealth() - 1.0f))), false);
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

    public static final SkillInfo MOMENTUM = new SkillInfo("7b7ce4aa-8f05-48b9-a2c1-f3b714ba339a",
            new SkillGeneralInfo(Skill.Type.STAT, "woodcutting.momentum"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.WOODCUTTING, 25);
            }}),
            new SkillGuiInfo(140, 50, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x9f6a16, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Woodcutting.ICONS, 4, 0))) {
        @Override
        public void onBuy(Skill skill) {
            skill.blockling.getStats().woodcuttingSpeedSkillMomentumModifier.setIsEnabled(true, false);
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

    public static final SkillInfo HASTY = new SkillInfo("a7f1ab81-e057-4f6a-a978-f10e8ee98005",
            new SkillGeneralInfo(Skill.Type.STAT, "woodcutting.hasty"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.WOODCUTTING, 25);
            }}),
            new SkillGuiInfo(210, -50, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x4eb2aa, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Woodcutting.ICONS, 5, 0))) {
        @Override
        public void onBuy(Skill skill) {
            skill.blockling.getStats().woodcuttingSpeedSkillHastyModifier.setIsEnabled(true, false);
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

    public static final SkillInfo NIGHT_OWL = new SkillInfo("1476e2be-9d0f-40cf-901d-b5ba18dea16f",
            new SkillGeneralInfo(Skill.Type.STAT, "woodcutting.night_owl"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.WOODCUTTING, 25);
            }}),
            new SkillGuiInfo(210, 50, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x2b2a3d, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Woodcutting.ICONS, 6, 0))) {
        @Override
        public void onBuy(Skill skill) {
            skill.blockling.getStats().woodcuttingSpeedSkillNightOwlModifier.setIsEnabled(true, false);
        }

        @Override
        public void tick(Skill skill) {
            if (!skill.blockling.getWorld().isClient) {
                float value = 15.0f * (1.0f - (skill.blockling.getWorld().getLightLevel(skill.blockling.getBlockPos())) / 15.0f);

                if (value != skill.blockling.getStats().woodcuttingSpeedSkillNightOwlModifier.getValue()) {
                    skill.blockling.getStats().woodcuttingSpeedSkillNightOwlModifier.setValue(value, true);
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

    public static final SkillInfo LEAF_BLOWER = new SkillInfo("c4443300-1004-4527-904f-ef097b65e816",
            new SkillGeneralInfo(Skill.Type.OTHER, "woodcutting.leaf_blower"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.WOODCUTTING, 15);
            }}),
            new SkillGuiInfo(-70, -70, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x227010, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Woodcutting.ICONS, 7, 0))) {
        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(NOVICE_LUMBERJACK);
        }
    };

    public static final SkillInfo TREE_SURGEON = new SkillInfo("07168fe8-6434-446a-ad37-09f41fa9b9d9",
            new SkillGeneralInfo(Skill.Type.OTHER, "woodcutting.tree_surgeon"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.WOODCUTTING, 30);
            }}),
            new SkillGuiInfo(-70, -140, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x5f6d18, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Woodcutting.ICONS, 8, 0))) {
        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(LEAF_BLOWER);
        }
    };

    public static final SkillInfo REPLANTER = new SkillInfo("47db8c56-3a95-44a4-8389-210e0de84d5a",
            new SkillGeneralInfo(Skill.Type.OTHER, "woodcutting.replanter"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.WOODCUTTING, 20);
            }}),
            new SkillGuiInfo(-70, 70, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xaec600, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Woodcutting.ICONS, 9, 0))) {
        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(NOVICE_LUMBERJACK);
        }
    };

    public static final SkillInfo LUMBER_AXE = new SkillInfo("e28a9248-7265-478c-8fbf-044b9f9db5e6",
            new SkillGeneralInfo(Skill.Type.OTHER, "woodcutting.lumber_axe"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.WOODCUTTING, 50);
            }}),
            new SkillGuiInfo(0, -70, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x6b4e49, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Woodcutting.ICONS, 0, 1))) {
        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(NOVICE_LUMBERJACK);
        }
    };

    public static final List<Function<SkillGroup, Skill>> SKILLS = new ArrayList<Function<SkillGroup, Skill>>() {{
        add(group -> new Skill(NOVICE_LUMBERJACK, group));
        add(group -> new Skill(WHITELIST, group));
        add(group -> new Skill(EFFICIENCY, group));
        add(group -> new Skill(ADRENALINE, group));
        add(group -> new Skill(MOMENTUM, group));
        add(group -> new Skill(HASTY, group));
        add(group -> new Skill(NIGHT_OWL, group));
        add(group -> new Skill(LEAF_BLOWER, group));
        add(group -> new Skill(TREE_SURGEON, group));
        add(group -> new Skill(REPLANTER, group));
        add(group -> new Skill(LUMBER_AXE, group));
    }};
}
