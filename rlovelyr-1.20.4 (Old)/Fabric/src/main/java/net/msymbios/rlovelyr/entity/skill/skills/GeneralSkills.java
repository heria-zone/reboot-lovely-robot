package net.msymbios.rlovelyr.entity.skill.skills;

import net.msymbios.rlovelyr.client.gui.texture.Textures;
import net.msymbios.rlovelyr.entity.attribute.enums.Level;
import net.msymbios.rlovelyr.entity.skill.info.*;
import net.msymbios.rlovelyr.entity.skill.*;

import java.util.*;
import java.util.function.Function;

/**
 * The general skills.
 */
public class GeneralSkills {
    
    // -- Variables --
    
    public static final SkillInfo HEAL = new SkillInfo("e6361ca8-a0c5-4a64-8be9-6928a98a4594",
            new SkillGeneralInfo(Skill.Type.OTHER, "general.heal"),
            new SkillDefaultsInfo(Skill.State.UNLOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.TOTAL, 10);
            }}),
            new SkillGuiInfo(0, 50, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xa8f4a1, new SkillGuiInfo.SkillIconTexture(Textures.Skills.General.ICONS, 0, 0)));

    public static final SkillInfo PACKLING = new SkillInfo("5cd54257-954f-4962-b248-99f58fb11d5d",
            new SkillGeneralInfo(Skill.Type.OTHER, "general.packling"),
            new SkillDefaultsInfo(Skill.State.UNLOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.TOTAL, 25);
            }}),
            new SkillGuiInfo(-50, 0, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xcca58a, new SkillGuiInfo.SkillIconTexture(Textures.Skills.General.ICONS, 1, 0)));

    public static final SkillInfo ARMADILLO = new SkillInfo("28ae60b1-1e8a-4c73-b1a1-5519be35d0ea",
            new SkillGeneralInfo(Skill.Type.OTHER, "general.armadillo"),
            new SkillDefaultsInfo(Skill.State.UNLOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.TOTAL, 50);
            }}),
            new SkillGuiInfo(50, 0, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xa8924f, new SkillGuiInfo.SkillIconTexture(Textures.Skills.General.ICONS, 2, 0))) {
        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(PACKLING);
        }
    };

    public static final SkillInfo SPEED_1 = new SkillInfo("157b1dae-e1e5-4ba7-9cb2-32e417b311ae",
            new SkillGeneralInfo(Skill.Type.STAT, "general.speed_1"),
            new SkillDefaultsInfo(Skill.State.UNLOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.TOTAL, 50);
            }}),
            new SkillGuiInfo(-100, -50, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x662522, new SkillGuiInfo.SkillIconTexture(Textures.Skills.General.ICONS, 3, 0))) {
        @Override
        public void onBuy(Skill skill) {
            skill.blockling.getStats().moveSpeedSkillSpeedModifier.setValue(1.05f, false);
        }
    };

    public static final SkillInfo SPEED_2 = new SkillInfo("77bedcd6-596c-4ebd-bb90-b5da1c5a5559",
            new SkillGeneralInfo(Skill.Type.STAT, "general.speed_2"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.TOTAL, 100);
            }}),
            new SkillGuiInfo(0, -50, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xb06d1f, new SkillGuiInfo.SkillIconTexture(Textures.Skills.General.ICONS, 4, 0))) {
        @Override
        public void onBuy(Skill skill) {
            skill.blockling.getStats().moveSpeedSkillSpeedModifier.setValue(1.1f, false);
        }

        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(SPEED_1);
        }
    };

    public static final SkillInfo SPEED_3 = new SkillInfo("57b70a86-e2e0-4b0e-b7a1-f299915f03dd",
            new SkillGeneralInfo(Skill.Type.STAT, "general.speed_3"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.TOTAL, 150);
            }}),
            new SkillGuiInfo(100, -50, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x03595e, new SkillGuiInfo.SkillIconTexture(Textures.Skills.General.ICONS, 5, 0))) {
        @Override
        public void onBuy(Skill skill) {
            skill.blockling.getStats().moveSpeedSkillSpeedModifier.setValue(1.2f, false);
        }

        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(SPEED_2);
        }
    };

    public static final SkillInfo AUTOSWITCH = new SkillInfo("b431f534-40eb-47c9-9cfe-5192b0492704",
            new SkillGeneralInfo(Skill.Type.OTHER, "general.autoswitch"),
            new SkillDefaultsInfo(Skill.State.UNLOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.TOTAL, 100);
            }}),
            new SkillGuiInfo(-50, -100, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xd8d8d8, new SkillGuiInfo.SkillIconTexture(Textures.Skills.General.ICONS, 6, 0)));

    public static final SkillInfo FIND_BLOCKLINGS = new SkillInfo("d28ea410-bd84-4a83-8aeb-0451c00314c3",
            new SkillGeneralInfo(Skill.Type.AI, "general.find_blocklings"),
            new SkillDefaultsInfo(Skill.State.UNLOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.TOTAL, 150);
            }}),
            new SkillGuiInfo(50, -100, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xaaaaff, new SkillGuiInfo.SkillIconTexture(Textures.Skills.General.ICONS, 7, 0))) {
        @Override
        public void onBuy(Skill skill) {
            // TODO: Uncomment when blockling tasks are implemented
            //skill.blockling.getTasks().setIsUnlocked(BlocklingTasks.FIND_BLOCKLINGS, true);
        }
    };

    public static final SkillInfo PATROL = new SkillInfo("04549ea5-6fb8-44d0-97ca-8c599b7aefe7",
            new SkillGeneralInfo(Skill.Type.AI, "general.patrol"),
            new SkillDefaultsInfo(Skill.State.UNLOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.TOTAL, 100);
            }}),
            new SkillGuiInfo(0, -150, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x77ffda, new SkillGuiInfo.SkillIconTexture(Textures.Skills.General.ICONS, 1, 1))) {
        @Override
        public void onBuy(Skill skill) {
            // TODO: Uncomment when blockling tasks are implemented
            //skill.blockling.getTasks().setIsUnlocked(BlocklingTasks.PATROL, true);
        }
    };

    public static final SkillInfo COURIER = new SkillInfo("077e318e-ed55-4fd4-9639-2344fd4a3e02",
            new SkillGeneralInfo(Skill.Type.AI, "general.courier"),
            new SkillDefaultsInfo(Skill.State.UNLOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.TOTAL, 75);
            }}),
            new SkillGuiInfo(0, 100, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0xffc72d, new SkillGuiInfo.SkillIconTexture(Textures.Skills.General.ICONS, 8, 0))) {
        @Override
        public void onBuy(Skill skill) {
            // TODO: Uncomment when blockling tasks are implemented
            //skill.blockling.getTasks().setIsUnlocked(BlocklingTasks.DEPOSIT_ITEMS, true);
            //skill.blockling.getTasks().setIsUnlocked(BlocklingTasks.TAKE_ITEMS, true);
        }
    };

    public static final SkillInfo ADVANCED_COURIER = new SkillInfo("e65e6b8d-8217-4934-b302-e8a79588f97a",
            new SkillGeneralInfo(Skill.Type.OTHER, "general.advanced_courier"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.TOTAL, 100);
            }}),
            new SkillGuiInfo(-50, 150, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x00ed83, new SkillGuiInfo.SkillIconTexture(Textures.Skills.General.ICONS, 9, 0))) {
        @Override
        public void onBuy(Skill skill) {
            // TODO: Uncomment when blockling tasks are implemented
            /*for (Task task : skill.blockling.getTasks().getPrioritisedTasks()) {
                if (task.isConfigured() && task.getGoal() instanceof BlocklingContainerGoal) {
                    BlocklingContainerGoal goal = (BlocklingContainerGoal) task.getGoal();
                    goal.itemConfigurationTypeProperty.setEnabled(true);
                }
            }*/
        }

        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(COURIER);
        }
    };

    public static final SkillInfo FIRST_CLASS = new SkillInfo("f2d3c6d9-44b0-42af-9c90-fbcc8ee21bee",
            new SkillGeneralInfo(Skill.Type.OTHER, "general.first_class"),
            new SkillDefaultsInfo(Skill.State.LOCKED),
            new SkillRequirementsInfo(new HashMap<Level, Integer>() {{
                put(Level.TOTAL, 125);
            }}),
            new SkillGuiInfo(50, 150, SkillGuiInfo.ConnectionType.SINGLE_LONGEST_FIRST, 0x81bd63, new SkillGuiInfo.SkillIconTexture(Textures.Skills.General.ICONS, 0, 1))) {
        @Override
        public void onBuy(Skill skill) {
            // TODO: Uncomment when blockling tasks are implemented
            /*for (Task task : skill.blockling.getTasks().getPrioritisedTasks()) {
                if (task.isConfigured() && task.getGoal() instanceof BlocklingContainerGoal) {
                    BlocklingContainerGoal goal = (BlocklingContainerGoal) task.getGoal();
                    goal.itemTransferAmount.setEnabled(true);
                }
            }*/
        }

        @Override
        public List<SkillInfo> parents() {
            return Collections.singletonList(COURIER);
        }
    };

    public static final List<Function<SkillGroup, Skill>> SKILLS = new ArrayList<Function<SkillGroup, Skill>>() {{
        add(group -> new Skill(HEAL, group));
        add(group -> new Skill(PACKLING, group));
        add(group -> new Skill(ARMADILLO, group));
        add(group -> new Skill(SPEED_1, group));
        add(group -> new Skill(SPEED_2, group));
        add(group -> new Skill(SPEED_3, group));
        add(group -> new Skill(AUTOSWITCH, group));
        add(group -> new Skill(FIND_BLOCKLINGS, group));
        add(group -> new Skill(PATROL, group));
        add(group -> new Skill(COURIER, group));
        add(group -> new Skill(ADVANCED_COURIER, group));
        add(group -> new Skill(FIRST_CLASS, group));
    }};

} // Class GeneralSkills