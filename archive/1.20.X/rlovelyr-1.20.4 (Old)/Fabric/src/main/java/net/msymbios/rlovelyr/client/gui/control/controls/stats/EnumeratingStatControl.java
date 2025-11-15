package net.msymbios.rlovelyr.client.gui.control.controls.stats;


import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.msymbios.rlovelyr.client.gui.control.controls.EnumeratingControl;
import net.msymbios.rlovelyr.client.gui.util.GuiUtil;
import net.msymbios.rlovelyr.config.LovelyRobotID;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An enumerating control specifically for stats.
 */
public class EnumeratingStatControl extends EnumeratingControl<StatControl> {
    /**
     * The name of the enumerating control to display in the tooltip.
     */
    private final Text name;

    /**
     * @param name the name of the enumerating control to display in the tooltip.
     */
    public EnumeratingStatControl(Text name) {
        super();
        this.name = name;
    }

    @Override
    public void onRenderTooltip(double mouseX, double mouseY, float partialTicks) {
        renderTooltip(mouseX, mouseY, getPixelScaleX(), getPixelScaleY(), (Text) prependNameToTooltip(combineTooltips()).stream().map(t -> t.getContent()).collect(Collectors.toList()));
    }

    private List<Text> prependNameToTooltip(List<Text> tooltip) {
//        tooltip.add(0, new TextComponent("").getVisualOrderText());
        tooltip.add(0, name);

        return tooltip;
    }

    private List<Text> combineTooltips() {
        List<Text> tooltip = new ArrayList<>();

        for (int i = 0; i < displayConditions.size(); i++) {
            if (!displayConditions.get(i).get()) {
                continue;
            }

            List<Text> subTooltip = controls.get(i).tooltipSupplier.get();

            if (i == getIndexOfCurrentChild()) {
                subTooltip.set(0, Text.literal(subTooltip.get(0).getString().substring(0, 2) + Formatting.ITALIC + subTooltip.get(0).getString().substring(2)));
            }

            if (GuiUtil.get().isCrouchKeyDown()) {
                tooltip.addAll(subTooltip);
            } else {
                tooltip.add(subTooltip.get(0));
            }
        }

        if (!GuiUtil.get().isCrouchKeyDown()) {
            tooltip.add(Text.literal(Formatting.DARK_GRAY + "" + Formatting.ITALIC + LovelyRobotID.getTranslation("gui.more_info", MinecraftClient.getInstance().options.sneakKey.getTranslationKey().toString()).getString()));
        }

        return tooltip;
    }
}

