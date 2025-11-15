package net.msymbios.rlovelyr.client.gui.control.controls.stats;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.msymbios.rlovelyr.client.gui.control.Control;
import net.msymbios.rlovelyr.client.gui.control.controls.TextBlockControl;
import net.msymbios.rlovelyr.client.gui.control.controls.TexturedControl;
import net.msymbios.rlovelyr.client.gui.screen.screens.StatsScreen;
import net.msymbios.rlovelyr.client.gui.texture.Textures;
import net.msymbios.rlovelyr.client.gui.util.Colour;
import net.msymbios.rlovelyr.client.gui.util.ScissorStack;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.internal.InterfaceEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Displays a blockling's health bar.
 */
@Environment(EnvType.CLIENT)
public class HealthBarControl extends Control {
    /**
     * The blockling.
     */
    private final InterfaceEntity blockling;

    /**
     * @param blockling the blockling.
     */
    public HealthBarControl(InterfaceEntity blockling) {
        super();
        this.blockling = blockling;

        setFitWidthToContent(true);
        setFitHeightToContent(true);

        TexturedControl backgroundHealthBarControl = new TexturedControl(Textures.Stats.HEALTH_BAR) {
            @Override
            public void onRender(ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks) {
                RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1.0f);
                super.onRender(scissorStack, mouseX, mouseY, partialTicks);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            }
        };
        backgroundHealthBarControl.setParent(this);
        backgroundHealthBarControl.setVerticalAlignment(0.5);
        backgroundHealthBarControl.setInteractive(false);

        TexturedControl colouredHealthBarControl = new TexturedControl(Textures.Stats.HEALTH_BAR) {
            @Override
            public void onTick() {
                setWidth(getBackgroundTexture().width * ((float) blockling.getStats().getHealth() / blockling.getStats().getMaxHealth()));
            }

            @Override
            public void onRender(ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks) {
                RenderSystem.setShaderColor((float) (1.3f - (Math.ceil(blockling.getStats().getHealth() / blockling.getStats().getMaxHealth()))), 0.3f + (float) (Math.ceil(blockling.getStats().getHealth() / blockling.getStats().getMaxHealth())), 0.1f, 1.0f);
                super.onRender(scissorStack, mouseX, mouseY, partialTicks);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            }
        };
        colouredHealthBarControl.setParent(this);
        colouredHealthBarControl.setVerticalAlignment(0.5);
        colouredHealthBarControl.onTick();
        colouredHealthBarControl.setInteractive(false);
        colouredHealthBarControl.setFitWidthToContent(false);

        TextBlockControl levelTextControl = new TextBlockControl() {
            @Override
            public void onTick() {
                int r = (int) (215 - blockling.getStats().getHealthPercentage() * 150);
                int g = (int) (50 + blockling.getStats().getHealthPercentage() * 180);
                int b = 50;
                setTextColour(new Colour(r, g, b).argb());

                String healthText = blockling.getStats().getHealth() + "/" + blockling.getStats().getMaxHealth();
                setText(healthText);
            }
        };
        levelTextControl.setParent(this);
        levelTextControl.setFitWidthToContent(true);
        levelTextControl.setFitHeightToContent(true);
        levelTextControl.useDescenderlessLineHeight();
        levelTextControl.setHorizontalAlignment(0.5);
        levelTextControl.setVerticalAlignment(0.5);
        levelTextControl.setInteractive(false);
        levelTextControl.onTick();
    }

    @Override
    public void onRenderTooltip(double mouseX, double mouseY, float partialTicks) {
        List<Component> tooltip = StatsScreen.createModifiableFloatAttributeTooltip(blockling.getStats().maxHealth, ChatFormatting.DARK_GREEN);
        tooltip.add(0, LovelyRobotID.getAttributeTranslation("health.name").withStyle(ChatFormatting.GOLD));
        renderTooltip(mouseX, mouseY, tooltip.stream().map(Component::getVisualOrderText).collect(Collectors.toList()));
    }

}
