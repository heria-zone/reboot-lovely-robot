package net.msymbios.rlovelyr.client.gui.texture;

import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.config.LovelyRobotID;

import java.util.Random;

/**
 * A collection of all gui textures used.
 */
public class Textures {

    // -- Classes --

    public static class Common {

        // -- Variables --

        public static final Identifier COMMON = LovelyRobotID.getId("textures/gui/common_widgets.png");
        public static final Identifier WIDGETS_LOCATION = new Identifier("textures/gui/widgets.png");

        public static final Texture BAR_RAISED = new Texture(COMMON, 0, 48, 256, 20);
        public static final Texture BAR_FLAT = new Texture(COMMON, 0, 68, 256, 20);

        public static final Texture BUTTON = new Texture(WIDGETS_LOCATION, 0, 66, 200, 20);
        public static final Texture BUTTON_HOVERED = new Texture(WIDGETS_LOCATION, 0, 86, 200, 20);

        public static final Texture NODE_UNPRESSED = new Texture(COMMON, 46, 0, 12, 12);
        public static final Texture NODE_PRESSED = new Texture(COMMON, 58, 0, 12, 12);

        public static final Texture SEARCH_ICON = new Texture(COMMON, 70, 0, 14, 14);

        public static final Texture PLUS_ICON = new Texture(COMMON, 84, 0, 12, 12);
        public static final Texture PLUS_ICON_DISABLED = new Texture(COMMON, 106, 0, 12, 12);
        public static final Texture CROSS_ICON = new Texture(COMMON, 96, 0, 10, 10);

        public static final Texture SLIDER_BAR = new Texture(COMMON, 0, 88, 256, 4);

        // -- Classes --

        public static class Scrollbar {
            public static final Texture GRABBER_UNPRESSED = new Texture(COMMON, 0, 0, 12, 15);
            public static final Texture GRABBER_PRESSED = new Texture(COMMON, 12, 0, 12, 15);
        } // Class Scrollbar

        public static class ComboBox {
            public static final int BORDER_SIZE = 2;

            public static final Texture DOWN_ARROW = new Texture(COMMON, 24, 0, 11, 7);
            public static final Texture UP_ARROW = new Texture(COMMON, 35, 0, 11, 7);

            public static final Texture SELECTED_BACKGROUND = BAR_RAISED;
            public static final Texture UNSELECTED_BACKGROUND = BAR_FLAT.dy(1).dHeight(-1);
        } // Class ComboBox

        public static class Tab {
            public static final int EDGE_WIDTH = 4;
            public static final int FULLY_OPAQUE_HEIGHT = 13;

            public static final Texture TAB_SELECTED_BACKGROUND = new Texture(COMMON, 0, 15, 256, 18);
            public static final Texture TAB_UNSELECTED_BACKGROUND = new Texture(COMMON, 0, 33, 256, 15);
        } // Class Tab

    } // Class Common

    public static class Tabs {

        // -- Variables --

        public static final Identifier TABS = LovelyRobotID.getId("textures/gui/tabs.png");

        public static final Texture UNSELECTED_BACKGROUND_TEXTURE_LEFT = new Texture(TABS, 0, 0, 25, 28);
        public static final Texture UNSELECTED_BACKGROUND_TEXTURE_RIGHT = new Texture(TABS, 26, 0, 25, 28);
        public static final Texture SELECTED_BACKGROUND_TEXTURE_LEFT = new Texture(TABS, 52, 0, 32, 28);
        public static final Texture SELECTED_BACKGROUND_TEXTURE_RIGHT = new Texture(TABS, 85, 0, 32, 28);

        public static final Texture STATS = new Texture(TABS, 0, 28, 22, 22);
        public static final Texture TASKS = new Texture(TABS, 22, 28, 22, 22);
        public static final Texture EQUIPMENT = new Texture(TABS, 44, 28, 22, 22);
        public static final Texture GENERAL = new Texture(TABS, 0, 50, 22, 22);
        public static final Texture COMBAT = new Texture(TABS, 22, 50, 22, 22);
        public static final Texture MINING = new Texture(TABS, 44, 50, 22, 22);
        public static final Texture WOODCUTTING = new Texture(TABS, 66, 50, 22, 22);
        public static final Texture FARMING = new Texture(TABS, 88, 50, 22, 22);

    } // Class Tabs

    public static class Stats {

        // -- Variables --

        public static final Identifier STATS = LovelyRobotID.getId("textures/gui/stats.png");
        public static final Texture BACKGROUND = new Texture(STATS, 0, 0, 176, 166);

        public static final Texture HEALTH_BAR = new Texture(STATS, 0, 228, 134, 5);

        public static final Texture COMBAT_BAR_BACKGROUND = new XpBarTexture(1);
        public static final Texture COMBAT_BAR_FOREGROUND = new XpBarTexture(0);
        public static final Texture MINING_BAR_BACKGROUND = new XpBarTexture(3);
        public static final Texture MINING_BAR_FOREGROUND = new XpBarTexture(2);
        public static final Texture WOODCUTTING_BAR_BACKGROUND = new XpBarTexture(5);
        public static final Texture WOODCUTTING_BAR_FOREGROUND = new XpBarTexture(4);
        public static final Texture FARMING_BAR_BACKGROUND = new XpBarTexture(7);
        public static final Texture FARMING_BAR_FOREGROUND = new XpBarTexture(6);

        public static final LevelIconsTexture COMBAT_LEVEL_ICONS = new LevelIconsTexture(0);
        public static final LevelIconsTexture MINING_LEVEL_ICONS = new LevelIconsTexture(1);
        public static final LevelIconsTexture WOODCUTTING_LEVEL_ICONS = new LevelIconsTexture(2);
        public static final LevelIconsTexture FARMING_LEVEL_ICONS = new LevelIconsTexture(3);

        public static final Texture ATTACK_DAMAGE_MAIN = new StatIconTexture(12, 0);
        public static final Texture ATTACK_DAMAGE_OFF = new StatIconTexture(10, 0);
        public static final Texture ATTACK_SPEED = new StatIconTexture(11, 0);

        public static final Texture ARMOUR = new StatIconTexture(5, 0);
        public static final Texture ARMOUR_TOUGHNESS = new StatIconTexture(6, 0);
        public static final Texture KNOCKBACK_RESISTANCE = new StatIconTexture(7, 0);

        public static final Texture MINING_SPEED = new StatIconTexture(1, 0);
        public static final Texture WOODCUTTING_SPEED = new StatIconTexture(2, 0);
        public static final Texture FARMING_SPEED = new StatIconTexture(3, 0);

        public static final Texture MOVE_SPEED = new StatIconTexture(8, 0);

        // -- Classes --

        public static class StatIconTexture extends Texture {
            public StatIconTexture(int x, int y) {
                super(STATS, x * 11, 166 + y * 11, 11, 11);
            }
        } // Class StatIconTexture

        private static class XpBarTexture extends Texture {
            public XpBarTexture(int y) {
                super(STATS, 0, 188 + y * 5, 111, 5);
            }
        } // Class XpBarTexture

        public static class LevelIconsTexture extends Texture {

            // -- Variables --
            public static final int ICON_SIZE = 11;
            public static final int NUMBER_OF_ICONS = 6;

            // -- Constructors --

            public LevelIconsTexture(int y) {
                super(STATS, 176, y * ICON_SIZE, ICON_SIZE * NUMBER_OF_ICONS, ICON_SIZE);
            } // Constructor LevelIconsTexture ()

        } // Class LevelIconsTexture

    } // Class Stats

    public static class Tasks {

        // -- Variables --
        public static final Identifier TASKS = LovelyRobotID.getId("textures/gui/tasks.png");
        public static final Identifier TASKS_CONFIG = LovelyRobotID.getId("textures/gui/task_configure.png");
        public static final Identifier WHITELIST = LovelyRobotID.getId("textures/gui/whitelist.png");

        public static final Texture BACKGROUND = new Texture(TASKS, 0, 0, 176, 166);
        public static final Texture CONFIG_BACKGROUND = new Texture(TASKS_CONFIG, 0, 0, 176, 166);

        public static final Texture TASK_ICON_BACKGROUND_RAISED = new Texture(TASKS, 0, 166, 20, 20);
        public static final Texture TASK_ICON_BACKGROUND_PRESSED = new Texture(TASKS, 20, 166, 20, 20);
        public static final Texture TASK_NAME_BACKGROUND = new Texture(TASKS, 41, 166, 96, 20);
        public static final Texture TASK_ADD_ICON_BACKGROUND = new Texture(TASKS, 176, 0, 20, 20);
        public static final Texture TASK_ADD_ICON = new Texture(TASKS, 140, 170, 12, 12);
        public static final Texture TASK_REMOVE_ICON = new Texture(TASKS, 160, 170, 12, 12);
        public static final Texture TASK_CONFIG_ICON = new Texture(TASKS, 176, 166, 20, 20);

        public static final Texture ENTRY_UNSELECTED = new Texture(WHITELIST, 0, 166, 30, 30);
        public static final Texture ENTRY_SELECTED = ENTRY_UNSELECTED.dx(ENTRY_UNSELECTED.width);

    } // Class Tasks

    public static class Equipment {
        // -- Variables --
        public static final Identifier EQUIPMENT = LovelyRobotID.getId("textures/gui/equipment.png");
        public static final Texture BACKGROUND = new Texture(EQUIPMENT, 0, 0, 176, 166);
    } // Class Equipment

    // -- Classes --

    public static class Skills {

        // -- Variables --

        public static final Identifier SKILLS = LovelyRobotID.getId("textures/gui/skills.png");
        public static final Texture BACKGROUND = new Texture(SKILLS, 0, 0, 176, 166);

        public static final Texture MAXIMISE_BUTTON = new Texture(SKILLS, 0, 206, 11, 11);
        public static final Texture MAXIMISE_BUTTON_HOVERED = new Texture(SKILLS, 11, 206, 11, 11);

        public static final Texture SKILL_NAME_BACKGROUND = new Texture(SKILLS, 0, 166, 200, 20);
        public static final Texture SKILL_DESC_BACKGROUND = new Texture(SKILLS, 0, 186, 200, 20);

        public static final Texture STAT_SKILL_BACKGROUND = new Texture(SKILLS, 0, 217, 24, 24);
        public static final Texture AI_SKILL_BACKGROUND_HOVERED = new Texture(SKILLS, 24, 217, 24, 24);
        public static final Texture UTILITY_SKILL_BACKGROUND = new Texture(SKILLS, 48, 217, 24, 24);
        public static final Texture OTHER_SKILL_BACKGROUND_HOVERED = new Texture(SKILLS, 72, 217, 24, 24);

        // -- Classes --

        public static class General {
            public static final Tiles TILES = new Tiles("textures/gui/skills_backgrounds/general.png");
            public static final Tiles ICONS = new Tiles("textures/gui/skills_icons/general.png");
        } // Class General

        public static class Combat {
            public static final Tiles TILES = new Tiles("textures/gui/skills_backgrounds/combat.png");
            public static final Tiles ICONS = new Tiles("textures/gui/skills_icons/combat.png");
        } // Class General

        public static class Mining {
            public static final Tiles TILES = new Tiles("textures/gui/skills_backgrounds/mining.png");
            public static final Tiles ICONS = new Tiles("textures/gui/skills_icons/mining.png");
        } // Class General

        public static class Woodcutting {
            public static final Tiles TILES = new Tiles("textures/gui/skills_backgrounds/woodcutting.png");
            public static final Tiles ICONS = new Tiles("textures/gui/skills_icons/woodcutting.png");
        } // Class General

        public static class Farming {
            public static final Tiles TILES = new Tiles("textures/gui/skills_backgrounds/farming.png");
            public static final Tiles ICONS = new Tiles("textures/gui/skills_icons/farming.png");
        } // Class General

        public static class Tiles extends Identifier {

            // -- Variables --
            public static final int TILE_SIZE = 16;

            // -- Constructors --

            private Tiles(String path) {
                super(LovelyRobot.MODID, path);
            } // Constructor Tiles ()

            // -- Methods --

            public Texture randomTile(Random random) {
                return new Texture(this, random.nextInt(256 / TILE_SIZE) * TILE_SIZE, random.nextInt(256 / TILE_SIZE) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            } // randomTile ()

        } // Class Tiles

    } // Class Skills

} // Class Textures