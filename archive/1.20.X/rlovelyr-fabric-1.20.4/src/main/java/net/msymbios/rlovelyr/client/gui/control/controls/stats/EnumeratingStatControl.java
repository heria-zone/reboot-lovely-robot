package net.msymbios.rlovelyr.client.gui.control.controls.stats;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.msymbios.rlovelyr.client.gui.control.controls.EnumeratingControl;
import net.msymbios.rlovelyr.client.gui.util.GuiUtil;
import net.msymbios.rlovelyr.config.LovelyRobotID;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An enumerating control specifically for stats.
 */
public class EnumeratingStatControl extends EnumeratingControl<StatControl> {
    /**
     * The name of the enumerating control to display in the tooltip.
     */
    private final Component name;

    /**
     * @param name the name of the enumerating control to display in the tooltip.
     */
    public EnumeratingStatControl(Component name) {
        super();
        this.name = name;
    }

    @Override
    public void onRenderTooltip(double mouseX, double mouseY, float partialTicks) {
        renderTooltip(mouseX, mouseY, getPixelScaleX(), getPixelScaleY(), prependNameToTooltip(combineTooltips()).stream().map(t -> t.getVisualOrderText()).collect(Collectors.toList()));
    }

    private List<Component> prependNameToTooltip(List<Component> tooltip) {
        tooltip.add(0, name);
        return tooltip;
    }

    private List<Component> combineTooltips() {
        List<Component> tooltip = new ArrayList<>();

        for (int i = 0; i < displayConditions.size(); i++) {
            if (!displayConditions.get(i).get()) {
                continue;
            }

            List<Component> subTooltip = controls.get(i).tooltipSupplier.get();

            if (i == getIndexOfCurrentChild()) {
                subTooltip.set(0, Component.literal(subTooltip.get(0).getString().substring(0, 2) + ChatFormatting.ITALIC + subTooltip.get(0).getString().substring(2)));
            }

            if (GuiUtil.get().isCrouchKeyDown()) {
                tooltip.addAll(subTooltip);
            } else {
                tooltip.add(subTooltip.get(0));
            }
        }

        if (!GuiUtil.get().isCrouchKeyDown()) {
            tooltip.add(Component.translatable(ChatFormatting.DARK_GRAY + "" + ChatFormatting.ITALIC + LovelyRobotID.getTranslation("gui.more_info", Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage().toString())));
        }

        return tooltip;
    }
}

