package net.msymbios.rlovelyr.client.gui.control.controls.stats;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.msymbios.rlovelyr.client.gui.control.Control;
import net.msymbios.rlovelyr.client.gui.control.controls.TextBlockControl;
import net.msymbios.rlovelyr.client.gui.control.controls.TexturedControl;
import net.msymbios.rlovelyr.client.gui.control.controls.panels.StackPanel;
import net.msymbios.rlovelyr.client.gui.properties.enums.Direction;
import net.msymbios.rlovelyr.client.gui.texture.Texture;
import net.msymbios.rlovelyr.client.gui.texture.Textures;
import net.msymbios.rlovelyr.client.gui.util.ScissorStack;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.attribute.Attribute;
import net.msymbios.rlovelyr.entity.attribute.enums.Level;
import net.msymbios.rlovelyr.entity.internal.InterfaceEntity;

import java.util.ArrayList;
import java.util.List;

import static net.msymbios.rlovelyr.entity.attribute.BlocklingAttributes.getXpForLevel;

/**
 * Displays a blockling's xp bar.
 */
public class XpBarControl extends Control {
    /**
     * The blockling.
     */
    private final InterfaceEntity blockling;

    /**
     * The level.
     */
    private final Level level;

    /**
     * @param blockling the blockling.
     * @param level     the level.
     */
    public XpBarControl(InterfaceEntity blockling, Level level) {
        super();
        this.blockling = blockling;
        this.level = level;

        setFitWidthToContent(true);
        setFitHeightToContent(true);

        StackPanel stackPanel = new StackPanel();
        stackPanel.setParent(this);
        stackPanel.setFitWidthToContent(true);
        stackPanel.setFitHeightToContent(true);
        stackPanel.setDirection(Direction.LEFT_TO_RIGHT);
        stackPanel.setSpacing(5);
        stackPanel.setInteractive(false);

        LevelIconControl leftIconControl = new LevelIconControl(true);
        leftIconControl.setParent(stackPanel);

        Control xpBarControl = new Control();
        xpBarControl.setParent(stackPanel);
        xpBarControl.setFitWidthToContent(true);
        xpBarControl.setFitHeightToContent(true);
        xpBarControl.setVerticalAlignment(0.5);

        TexturedControl xpBarBackgroundControl = new TexturedControl(level.getXpBarBackgroundTexture());
        xpBarBackgroundControl.setParent(xpBarControl);

        TexturedControl xpBarForegroundControl = new TexturedControl(level.getXpBarForegroundTexture()) {
            @Override
            public void onTick() {
                setWidth(getBackgroundTexture().width * getXpPercentage());
            }
        };
        xpBarForegroundControl.setParent(xpBarControl);
        xpBarForegroundControl.onTick();
        xpBarForegroundControl.setFitWidthToContent(false);

        LevelIconControl rightIconControl = new LevelIconControl(false);
        rightIconControl.setParent(stackPanel);

        TextBlockControl levelTextControl = new TextBlockControl() {
            @Override
            public void onTick() {
                setText(String.valueOf(blockling.getStats().getLevelAttribute(level).getValue()));
            }
        };
        levelTextControl.setParent(this);
        levelTextControl.setFitWidthToContent(true);
        levelTextControl.setFitHeightToContent(true);
        levelTextControl.useDescenderlessLineHeight();
        levelTextControl.setTextColour(level.getLevelColour().argb());
        levelTextControl.setHorizontalAlignment(0.5);
        levelTextControl.setVerticalAlignment(0.5);
        levelTextControl.setInteractive(false);
        levelTextControl.onTick();
    }

    @Override
    public void onRenderTooltip(double mouseX, double mouseY, float partialTicks) {
        Attribute<Integer> level = blockling.getStats().getLevelAttribute(this.level);

        List<Text> tooltip = new ArrayList<>();
        tooltip.add(Text.literal(String.valueOf(blockling.getStats().getLevelAttribute(this.level).displayStringNameSupplier.get())).formatted(Formatting.GOLD));
        tooltip.add(LovelyRobotID.getTranslation("gui.current_level").formatted(Formatting.GRAY).append(String.valueOf(level.getValue())).formatted(Formatting.WHITE));
        //tooltip.add(LovelyRobotID.getTranslation("gui.xp_required", blockling.getStats().getLevelXpAttribute(this.level).getValue(), getXpForLevel(level.getValue())).getString()).getVisualOrderText()
        renderTooltip(mouseX, mouseY, getPixelScaleX(), getPixelScaleY(), tooltip);
    }

    /**
     * @return the percentage of the way to the next level.
     */
    private float getXpPercentage() {
        return Math.min(1.0f, (float) blockling.getStats().getLevelXpAttribute(level).getValue() / getXpForLevel(blockling.getStats().getLevelAttribute(level).getValue()));
    }

    /**
     * Displays an icon for a level.
     */
    private class LevelIconControl extends Control {
        /**
         * Whether the icon represents the current or next level.
         */
        private final boolean current;

        /**
         * The icons texture.
         */
            private final Texture iconsTexture = level.getLevelIconsTexture();

        /**
         * @param current whether the icon represents the current or next level.
         */
        public LevelIconControl(boolean current) {
            super();
            this.current = current;

            setWidth(Textures.Stats.LevelIconsTexture.ICON_SIZE);
            setHeight(Textures.Stats.LevelIconsTexture.ICON_SIZE);
        }

        @Override
        public void onRender(ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks) {
            int iconToRender = (int) (((float) (blockling.getStats().getLevelAttribute(level).getValue() + (current ? 0 : 1)) / Level.MAX) * (Textures.Stats.LevelIconsTexture.NUMBER_OF_ICONS - 1));

            int iconSize = Textures.Stats.LevelIconsTexture.ICON_SIZE;
            renderTextureAsBackground( iconsTexture.dx(iconToRender * iconSize).width(iconSize));
        }
    }
}
