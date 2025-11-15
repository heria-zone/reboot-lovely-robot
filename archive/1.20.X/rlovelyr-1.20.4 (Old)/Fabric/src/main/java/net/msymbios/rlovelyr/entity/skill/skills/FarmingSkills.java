package net.msymbios.rlovelyr.entity.skill.skills;

import net.msymbios.rlovelyr.client.gui.texture.Textures;
import net.msymbios.rlovelyr.entity.attribute.enums.Level;
import net.msymbios.rlovelyr.entity.skill.info.*;
import net.msymbios.rlovelyr.entity.skill.*;

import java.util.*;
import java.util.function.Function;

/**
 * The farming skills.
 */
public class FarmingSkills {

    // -- Variables --

    public static final SkillInfo NOVICE_FARMER = new SkillInfo("d70e08ef-25e0-4639-8af5-4b7d55893568",
            new SkillGeneralInfo(Skill.Type.AI, "farming.novice_farmer"),
            new SkillDefaultsInfo(Skill.State.UNLOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
            }}),
            new SkillGuiInfo(0, 0, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xdddddd, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Farming.ICONS, 0, 0))) {
        @Override
        public void onBuy(Skill skill) {
            // TODO: Uncomment when task is implemented
            //skill.blockling.getTasks().setIsUnlocked(BlocklingTasks.FARM, true, false);
        }
    };

    public static final SkillInfo CROP_WHITELIST = new SkillInfo("0d9f7b71-3930-4848-9329-8994b0ce7cd1",
            new SkillGeneralInfo(Skill.Type.OTHER, "farming.crop_whitelist"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.FARMING, 5);
            }}),
            new SkillGuiInfo(0, 70, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xe0f1ff, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Farming.ICONS, 1, 0))) {
        @Override
        public void onBuy(Skill skill) {
            BlocklingSkills.unlockExistingWhitelists(skill, "25140edf-f60e-459e-b1f0-9ff82108ec0b");
        }

        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(NOVICE_FARMER);
        }
    };

    public static final SkillInfo EFFICIENCY = new SkillInfo("a7a02e05-c349-4a6c-9822-f05025c73bb5",
            new SkillGeneralInfo(Skill.Type.STAT, "farming.efficiency"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.FARMING, 10);
            }}),
            new SkillGuiInfo(70, 0, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xffd56d, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Farming.ICONS, 2, 0))) {
        @Override
        public void onBuy(Skill skill) {
            skill.blockling.getStats().farmingSpeedSkillEfficiencyModifier.setIsEnabled(true, false);
        }

        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(NOVICE_FARMER);
        }
    };

    public static final SkillInfo REPLANTER = new SkillInfo("25e708f5-fc53-452f-b882-9d31f754235c",
            new SkillGeneralInfo(Skill.Type.AI, "farming.replanter"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.FARMING, 10);
            }}),
            new SkillGuiInfo(-70, 0, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x64de10, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Farming.ICONS, 3, 0))) {
        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(NOVICE_FARMER);
        }
    };

    public static final SkillInfo SEED_WHITELIST = new SkillInfo("8595c654-a19c-4c58-a9c1-a7a5087c397f",
            new SkillGeneralInfo(Skill.Type.OTHER, "farming.seed_whitelist"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.FARMING, 15);
            }}),
            new SkillGuiInfo(-70, 70, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xe0f1ff, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Farming.ICONS, 1, 0))) {
        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(REPLANTER);
        }

        @Override
        public void onBuy(Skill skill) {
            BlocklingSkills.unlockExistingWhitelists(skill, "d77bf1c1-7718-4733-b763-298b03340eea");
        }
    };

    public static final SkillInfo ADRENALINE = new SkillInfo("51bb0230-e484-47ae-9c7f-8a4ec7868683",
            new SkillGeneralInfo(Skill.Type.STAT, "farming.adrenaline"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.FARMING, 25);
            }}),
            new SkillGuiInfo(140, -50, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xb72626, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Farming.ICONS, 4, 0))) {
        @Override
        public void onBuy(Skill skill) {
            skill.blockling.getStats().farmingSpeedSkillAdrenalineModifier.setIsEnabled(true, false);
        }

        @Override
        public void tick(Skill skill) {
            skill.blockling.getStats().farmingSpeedSkillAdrenalineModifier.setValue(10.0f * (1.0f - ((Math.max(skill.blockling.getHealth() - 1.0f, 0.0f)) / (skill.blockling.getMaxHealth() - 1.0f))), false);
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

    public static final SkillInfo MOMENTUM = new SkillInfo("e2c8db1a-bc32-482e-9225-54027196f7d2",
            new SkillGeneralInfo(Skill.Type.STAT, "farming.momentum"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.FARMING, 25);
            }}),
            new SkillGuiInfo(140, 50, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x39bb39, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Farming.ICONS, 5, 0))) {
        @Override
        public void onBuy(Skill skill) {
            skill.blockling.getStats().farmingSpeedSkillMomentumModifier.setIsEnabled(true, false);
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

    public static final SkillInfo HASTY = new SkillInfo("da1bd12f-044b-434c-a627-ef7146013d9a",
            new SkillGeneralInfo(Skill.Type.STAT, "farming.hasty"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.FARMING, 25);
            }}),
            new SkillGuiInfo(210, -50, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x4eb2aa, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Farming.ICONS, 6, 0))) {
        @Override
        public void onBuy(Skill skill) {
            skill.blockling.getStats().farmingSpeedSkillHastyModifier.setIsEnabled(true, false);
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

    public static final SkillInfo NIGHT_OWL = new SkillInfo("b06bfa1b-8b01-4802-a980-cad92b537273",
            new SkillGeneralInfo(Skill.Type.STAT, "farming.night_owl"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.FARMING, 25);
            }}),
            new SkillGuiInfo(210, 50, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x2b2a3d, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Farming.ICONS, 7, 0))) {
        @Override
        public void onBuy(Skill skill) {
            skill.blockling.getStats().farmingSpeedSkillNightOwlModifier.setIsEnabled(true, false);
        }

        @Override
        public void tick(Skill skill) {
            if (!skill.blockling.getWorld().isClient) {
                float value = 15.0f * (1.0f - (skill.blockling.getWorld().getLightLevel(skill.blockling.getBlockPos())) / 15.0f);

                if (value != skill.blockling.getStats().farmingSpeedSkillNightOwlModifier.getValue()) {
                    skill.blockling.getStats().farmingSpeedSkillNightOwlModifier.setValue(value, true);
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

    public static final SkillInfo SCYTHE = new SkillInfo("b5131481-6530-4733-bfa2-ab6b3cfeb76e",
            new SkillGeneralInfo(Skill.Type.AI, "farming.scythe"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.FARMING, 50);
            }}),
            new SkillGuiInfo(0, -70, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x6b4e49, new SkillGuiInfo.SkillIconTexture(Textures.Skills.Farming.ICONS, 8, 0))) {
        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(NOVICE_FARMER);
        }
    };

    public static final List<Function<SkillGroup, Skill>> SKILLS = new ArrayList<Function<SkillGroup, Skill>>() {{
        add(group -> new Skill(NOVICE_FARMER, group));
        add(group -> new Skill(CROP_WHITELIST, group));
        add(group -> new Skill(EFFICIENCY, group));
        add(group -> new Skill(REPLANTER, group));
        add(group -> new Skill(SEED_WHITELIST, group));
        add(group -> new Skill(ADRENALINE, group));
        add(group -> new Skill(MOMENTUM, group));
        add(group -> new Skill(HASTY, group));
        add(group -> new Skill(NIGHT_OWL, group));
        add(group -> new Skill(SCYTHE, group));
    }};

} // Class FarmingSkills