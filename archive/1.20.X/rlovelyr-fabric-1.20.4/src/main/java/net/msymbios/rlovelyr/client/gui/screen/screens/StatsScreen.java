package net.msymbios.rlovelyr.client.gui.screen.screens;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.msymbios.rlovelyr.client.gui.control.BaseControl;
import net.msymbios.rlovelyr.client.gui.control.Control;
import net.msymbios.rlovelyr.client.gui.control.controls.EntityControl;
import net.msymbios.rlovelyr.client.gui.control.controls.EnumeratingControl;
import net.msymbios.rlovelyr.client.gui.control.controls.TabbedUIControl;
import net.msymbios.rlovelyr.client.gui.control.controls.TextFieldControl;
import net.msymbios.rlovelyr.client.gui.control.controls.panels.GridPanel;
import net.msymbios.rlovelyr.client.gui.control.controls.panels.StackPanel;
import net.msymbios.rlovelyr.client.gui.control.controls.stats.EnumeratingStatControl;
import net.msymbios.rlovelyr.client.gui.control.controls.stats.HealthBarControl;
import net.msymbios.rlovelyr.client.gui.control.controls.stats.StatControl;
import net.msymbios.rlovelyr.client.gui.control.controls.stats.XpBarControl;
import net.msymbios.rlovelyr.client.gui.control.event.events.FocusChangedEvent;
import net.msymbios.rlovelyr.client.gui.properties.enums.Direction;
import net.msymbios.rlovelyr.client.gui.properties.enums.GridDefinition;
import net.msymbios.rlovelyr.client.gui.texture.Textures;
import net.msymbios.rlovelyr.client.gui.util.GuiUtil;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.attribute.BlocklingAttributes;
import net.msymbios.rlovelyr.entity.attribute.enums.Level;
import net.msymbios.rlovelyr.entity.attribute.enums.Operation;
import net.msymbios.rlovelyr.entity.attribute.interfaces.IModifiable;
import net.msymbios.rlovelyr.entity.attribute.interfaces.IModifier;
import net.msymbios.rlovelyr.entity.internal.InterfaceEntity;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.generate;

/**
 * A screen to show the stats of a blockling.
 */
@Environment(EnvType.CLIENT)
public class StatsScreen extends TabbedScreen {
    /**
     * @param blockling the blockling associated with the screen.
     */
    public StatsScreen(InterfaceEntity blockling) {
        super(blockling, TabbedUIControl.Tab.STATS);

        GridPanel mainGridPanel = new GridPanel();
        mainGridPanel.setParent(tabbedUIControl.contentControl);
        mainGridPanel.setWidthPercentage(1.0);
        mainGridPanel.setHeightPercentage(1.0);
        mainGridPanel.addRowDefinition(GridDefinition.FIXED, 20.0);
        mainGridPanel.addRowDefinition(GridDefinition.RATIO, 1.0);
        mainGridPanel.addColumnDefinition(GridDefinition.RATIO, 1.0);

        TextFieldControl textFieldControl = new TextFieldControl();
        mainGridPanel.addChild(textFieldControl, 0, 0);
        textFieldControl.setWidthPercentage(1.0);
        textFieldControl.setHeightPercentage(1.0);
        textFieldControl.setText(blockling.getCustomName().getString());
        textFieldControl.setHorizontalContentAlignment(0.5);
        textFieldControl.setMaxTextLength(25);
        textFieldControl.setShouldRenderBackground(false);
        textFieldControl.setBackgroundColour(0x00000000);
        textFieldControl.eventBus.subscribe((BaseControl control, FocusChangedEvent e) -> {
            if (!textFieldControl.getText().trim().isEmpty()) {
                blockling.setCustomName(Text.literal(textFieldControl.getText()));
            } else {
                //Text name = BlocklingsItems.BLOCKLING.get().getName(BlocklingsItems.BLOCKLING.get().getDefaultInstance());
                //blockling.setCustomName(Text.literal(name.getString()), true);
                //textFieldControl.setText(name.getString());
            }
        });

        GridPanel statsGridPanel = new GridPanel();
        mainGridPanel.addChild(statsGridPanel, 1, 0);
        statsGridPanel.setWidthPercentage(1.0);
        statsGridPanel.setHeightPercentage(1.0);
        statsGridPanel.setMargins(1.0, 0.0, 1.0, 1.0);
        statsGridPanel.addRowDefinition(GridDefinition.AUTO, 1.0);
        statsGridPanel.addRowDefinition(GridDefinition.RATIO, 1.0);
        statsGridPanel.addRowDefinition(GridDefinition.AUTO, 1.0);
        statsGridPanel.addColumnDefinition(GridDefinition.RATIO, 1.0);

        HealthBarControl healthBarControl = new HealthBarControl(blockling);
        statsGridPanel.addChild(healthBarControl, 0, 0);
        healthBarControl.setHorizontalAlignment(0.5);
        healthBarControl.setVerticalAlignment(0.5);
        healthBarControl.setMarginTop(6.0);

        Control statsContainer = new Control();
        statsGridPanel.addChild(statsContainer, 1, 0);
        statsContainer.setWidthPercentage(1.0);
        statsContainer.setHeightPercentage(1.0);

        Control leftStatsControl = new Control();
        leftStatsControl.setParent(statsContainer);
        leftStatsControl.setWidthPercentage(0.5);
        leftStatsControl.setHeightPercentage(1.0);
        leftStatsControl.setPadding(10.0, 0.0, 0.0, 0.0);
        leftStatsControl.setHorizontalAlignment(0.0);

        Control rightStatsControl = new Control();
        rightStatsControl.setParent(statsContainer);
        rightStatsControl.setWidthPercentage(0.5);
        rightStatsControl.setHeightPercentage(1.0);
        rightStatsControl.setPadding(0.0, 0.0, 10.0, 0.0);
        rightStatsControl.setHorizontalAlignment(1.0);

        BlocklingAttributes stats = blockling.getStats();

        EnumeratingControl combatStats = new EnumeratingStatControl(LovelyRobotID.getTranslation("stats.attack.name"));
        combatStats.setParent(leftStatsControl);
        combatStats.setHorizontalAlignment(0.0);
        combatStats.setVerticalAlignment(0.22);
        combatStats.addControl(new StatControl(
                        Textures.Stats.ATTACK_DAMAGE_MAIN,
                        () -> stats.mainHandAttackDamage.displayStringValueFunction.apply(stats.mainHandAttackDamage.getValue()),
                        () -> createModifiableFloatAttributeTooltip(stats.mainHandAttackDamage, ChatFormatting.DARK_RED), false)
                        //() -> blockling.getEquipment().isAttackingWith(BlocklingHand.MAIN)
        );
        combatStats.addControl(new StatControl(
                        Textures.Stats.ATTACK_DAMAGE_OFF,
                        () -> stats.offHandAttackDamage.displayStringValueFunction.apply(stats.offHandAttackDamage.getValue()),
                        () -> createModifiableFloatAttributeTooltip(stats.offHandAttackDamage, ChatFormatting.DARK_RED), false)
                        //() -> blockling.getEquipment().isAttackingWith(BlocklingHand.OFF)
        );
        combatStats.addControl(new StatControl(
                Textures.Stats.ATTACK_SPEED,
                () -> stats.attackSpeed.displayStringValueFunction.apply(stats.attackSpeed.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.attackSpeed, ChatFormatting.DARK_PURPLE), false));

        EnumeratingControl defenceStats = new EnumeratingStatControl(LovelyRobotID.getTranslation("stats.defence.name"));
        defenceStats.setParent(leftStatsControl);
        defenceStats.setHorizontalAlignment(0.0);
        defenceStats.setVerticalAlignment(0.72);
        defenceStats.addControl(new StatControl(
                Textures.Stats.ARMOUR,
                () -> stats.armour.displayStringValueFunction.apply(stats.armour.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.armour, ChatFormatting.DARK_AQUA), false));
        defenceStats.addControl(new StatControl(
                Textures.Stats.ARMOUR_TOUGHNESS,
                () -> stats.armourToughness.displayStringValueFunction.apply(stats.armourToughness.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.armourToughness, ChatFormatting.AQUA), false));
        defenceStats.addControl(new StatControl(
                Textures.Stats.KNOCKBACK_RESISTANCE,
                () -> stats.knockbackResistance.displayStringValueFunction.apply(stats.knockbackResistance.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.knockbackResistance, ChatFormatting.YELLOW), false));

        EnumeratingControl gatherStats = new EnumeratingStatControl(LovelyRobotID.getTranslation("stats.gathering.name"));
        gatherStats.setParent(rightStatsControl);
        gatherStats.setHorizontalAlignment(1.0);
        gatherStats.setVerticalAlignment(0.22);
        gatherStats.addControl(new StatControl(
                Textures.Stats.MINING_SPEED,
                () -> stats.miningSpeed.displayStringValueFunction.apply(stats.miningSpeed.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.miningSpeed, ChatFormatting.BLUE), true));
        gatherStats.addControl(new StatControl(
                Textures.Stats.WOODCUTTING_SPEED,
                () -> stats.woodcuttingSpeed.displayStringValueFunction.apply(stats.woodcuttingSpeed.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.woodcuttingSpeed, ChatFormatting.DARK_GREEN), true));
        gatherStats.addControl(new StatControl(
                Textures.Stats.FARMING_SPEED,
                () -> stats.farmingSpeed.displayStringValueFunction.apply(stats.farmingSpeed.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.farmingSpeed, ChatFormatting.YELLOW), true));

        EnumeratingControl movementStats = new EnumeratingStatControl(LovelyRobotID.getTranslation("stats.movement.name"));
        movementStats.setParent(rightStatsControl);
        movementStats.setHorizontalAlignment(1.0);
        movementStats.setVerticalAlignment(0.72);
        movementStats.addControl(new StatControl(
                Textures.Stats.MOVE_SPEED,
                () -> stats.moveSpeed.displayStringValueFunction.apply(stats.moveSpeed.getValue()),
                () -> createModifiableFloatAttributeTooltip(stats.moveSpeed, ChatFormatting.BLUE), true));

        EntityControl entityControl = new EntityControl() {
            @Override
            public void onRenderTooltip(double mouseX, double mouseY, float partialTicks) {
                List<Component> tooltip = new ArrayList<>();

                tooltip.add(Component.literal(ChatFormatting.GOLD + blockling.getCustomName().getString()));
                //tooltip.add(Text.literal(ChatFormatting.GRAY + LovelyRobotID.getTranslation("type.natural.name").getString() + ChatFormatting.WHITE + blockling.getNaturalBlocklingType().name.getString()).getVisualOrderText());

                List<String> splitText;

                if (GuiUtil.get().isCrouchKeyDown()) {
                    splitText = GuiUtil.get().split(LovelyRobotID.getTranslation("type.natural.desc"), 200);
                    splitText.stream().map(s -> Component.literal(ChatFormatting.DARK_GRAY + s)).forEach(tooltip::add);
                }

                //splitText = GuiUtil.get().split(LovelyRobotID.getTranslation("type." + blockling.getNaturalBlocklingType().key + ".passive").getString(), 200);
                //splitText.stream().map(s -> Text.literal(ChatFormatting.AQUA + s)).forEach(tooltip::add);

                //tooltip.add(Text.literal(ChatFormatting.GRAY + LovelyRobotID.getTranslation("type.name").getString() + ChatFormatting.WHITE + blockling.getBlocklingType().name.getString()).getVisualOrderText());

                if (GuiUtil.get().isCrouchKeyDown()) {
                    splitText = GuiUtil.get().split(LovelyRobotID.getTranslation("type.desc"), 200);
                    splitText.stream().map(s -> Component.literal(ChatFormatting.DARK_GRAY + s)).forEach(tooltip::add);
                }

                //splitText = GuiUtil.get().split(LovelyRobotID.getTranslation("type." + blockling.getBlocklingType().key + ".passive").getString(), 200);
                //splitText.stream().map(s -> Text.literal(ChatFormatting.AQUA + s)).forEach(tooltip::add);

                String foodsString = ChatFormatting.GRAY + LovelyRobotID.getTranslation("type.foods") + ChatFormatting.WHITE + LovelyRobotID.getTranslation("type.foods.flowers").getString() + ", ";
                //foodsString += blockling.getBlocklingType().foods.stream().map(food -> food.getDescription().getString()).collect(joining(", "));
                splitText = GuiUtil.get().split(foodsString, 200);
                splitText.stream().map(s -> Component.literal(s)).forEach(tooltip::add);

                if (!GuiUtil.get().isCrouchKeyDown()) {
                    //tooltip.add(Text.literal(ChatFormatting.DARK_GRAY + "" + ChatFormatting.ITALIC + LovelyRobotID.getTranslation("gui.more_info", Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage().getString()).getString()).getVisualOrderText());
                }

                renderTooltip(mouseX, mouseY, getPixelScaleX(), getPixelScaleY(), tooltip);
            }
        };
        entityControl.setParent(statsContainer);
        entityControl.setWidth(48.0);
        entityControl.setHeight(48.0);
        entityControl.setEntity(blockling);
        entityControl.setScaleToBoundingBox(true);
        entityControl.setEntityScale(0.9f);
        entityControl.setOffsetY(-1.0f);
        entityControl.setClipContentsToBounds(false);
        entityControl.setHorizontalAlignment(0.5);
        entityControl.setVerticalAlignment(0.47);

        StackPanel levelsPanel = new StackPanel();
        statsGridPanel.addChild(levelsPanel, 2, 0);
        levelsPanel.setWidthPercentage(1.0);
        levelsPanel.setFitHeightToContent(true);
        levelsPanel.setDirection(Direction.TOP_TO_BOTTOM);
        levelsPanel.setPadding(0.0, 0.0, 0.0, 6.0);
        levelsPanel.setSpacing(1.0);

        XpBarControl combatXpBarControl = new XpBarControl(blockling, Level.COMBAT);
        combatXpBarControl.setParent(levelsPanel);
        combatXpBarControl.setHorizontalAlignment(0.5);
        XpBarControl miningXpBarControl = new XpBarControl(blockling, Level.MINING);
        miningXpBarControl.setParent(levelsPanel);
        miningXpBarControl.setHorizontalAlignment(0.5);
        XpBarControl woodcuttingXpBarControl = new XpBarControl(blockling, Level.WOODCUTTING);
        woodcuttingXpBarControl.setParent(levelsPanel);
        woodcuttingXpBarControl.setHorizontalAlignment(0.5);
        XpBarControl farmingXpBarControl = new XpBarControl(blockling, Level.FARMING);
        farmingXpBarControl.setParent(levelsPanel);
        farmingXpBarControl.setHorizontalAlignment(0.5);
    }

    /**
     * Creates a tooltip based on a modifiable float attribute and a colour.
     *
     * @param attribute the modifiable attribute.
     * @param colour    the colour for the attribute's value.
     * @return the tooltip.
     */
    public static List<Component> createModifiableFloatAttributeTooltip(IModifiable<Float> attribute, ChatFormatting colour) {
        List<Component> tooltip = new ArrayList<>();

        tooltip.add(Component.literal(colour + attribute.getDisplayStringValueFunction().apply(attribute.getValue()) + " " + ChatFormatting.GRAY + attribute.createTranslation("name")));

        appendModifiableFloatAttributeToTooltip(tooltip, attribute, 1);

        return tooltip;
    }

    /**
     * Appends to a tooltip based on a modifiable float attribute and a depth.
     *
     * @param tooltip   the tooltip to append to.
     * @param attribute the modifiable attribute.
     * @param depth     the current depth in terms of modifiers on modifiers.
     */
    public static void appendModifiableFloatAttributeToTooltip(List<Component> tooltip, IModifiable<Float> attribute, int depth) {
        for (IModifier<Float> modifier : attribute.getModifiers()) {
            if (!modifier.isEnabled() || !modifier.isEffective()) {
                continue;
            }

            String sign = modifier.getValue() < 0.0f && modifier.getOperation() == Operation.ADD ? "" : modifier.getValue() < 1.0f && modifier.getOperation() != Operation.ADD ? "" : "+";
            tooltip.add(Component.literal(ChatFormatting.GRAY + generate(() -> " ").limit(depth).collect(joining()) + sign + modifier.getDisplayStringValueFunction().apply(modifier.getValue()) + " " + ChatFormatting.DARK_GRAY + modifier.getDisplayStringNameSupplier().get()));

            if (modifier instanceof IModifiable<?>) {
                appendModifiableFloatAttributeToTooltip(tooltip, (IModifiable<Float>) modifier, depth + 1);
            }
        }
    }

}
