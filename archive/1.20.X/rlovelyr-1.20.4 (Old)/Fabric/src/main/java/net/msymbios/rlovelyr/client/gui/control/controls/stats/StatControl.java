package net.msymbios.rlovelyr.client.gui.control.controls.stats;

import net.minecraft.text.Text;
import net.msymbios.rlovelyr.client.gui.control.Control;
import net.msymbios.rlovelyr.client.gui.control.controls.TextBlockControl;
import net.msymbios.rlovelyr.client.gui.control.controls.TexturedControl;
import net.msymbios.rlovelyr.client.gui.control.controls.panels.StackPanel;
import net.msymbios.rlovelyr.client.gui.properties.enums.Direction;
import net.msymbios.rlovelyr.client.gui.texture.Texture;

import java.util.List;
import java.util.function.Supplier;

/**
 * Displays a single blockling statistic, e.g. attack damage, mining speed.
 */
public class StatControl extends Control {
    /**
     * Supplies the stat's value string to display.
     */
    public final Supplier<String> valueSupplier;

    /**
     * Supplies the stat's tooltip to display.
     */
    public final Supplier<List<Text>> tooltipSupplier;

    /**
     * Whether to render the value on the left of the icon or the right.
     */
    private final boolean renderValueToLeftOfIcon;

    /**
     * The icon control.
     */
    private final TexturedControl iconControl;

    /**
     * The value text control.
     */
    private final TextBlockControl valueControl;

    /**
     * @param iconTexture             the stat's icon texture.
     * @param valueSupplier           the stat's value supplier.
     * @param tooltipSupplier         the stat's tooltip supplier.
     * @param renderValueToLeftOfIcon whether to render the value on the left of the icon or the right.
     */
    public StatControl(Texture iconTexture, Supplier<String> valueSupplier, Supplier<List<Text>> tooltipSupplier, boolean renderValueToLeftOfIcon) {
        super();
        this.valueSupplier = valueSupplier;
        this.tooltipSupplier = tooltipSupplier;
        this.renderValueToLeftOfIcon = renderValueToLeftOfIcon;

        setFitHeightToContent(true);
        setFitWidthToContent(true);
        setInteractive(false);

        StackPanel stackPanel = new StackPanel();
        stackPanel.setParent(this);
        stackPanel.setSpacing(6.0);
        stackPanel.setFitWidthToContent(true);
        stackPanel.setFitHeightToContent(true);
        stackPanel.setDirection(Direction.LEFT_TO_RIGHT);

        iconControl = new TexturedControl(iconTexture);
        valueControl = new TextBlockControl();
        valueControl.setMargins(0, 1, 0, 0);
        valueControl.setFitWidthToContent(true);
        valueControl.setFitHeightToContent(true);
        valueControl.setTextColour(0xffffe100);
        valueControl.setVerticalAlignment(0.5);
        valueControl.setText(Text.literal(valueSupplier.get()));
        valueControl.useDescenderlessLineHeight();

        if (renderValueToLeftOfIcon) {
            valueControl.setParent(stackPanel);
            iconControl.setParent(stackPanel);
        } else {
            iconControl.setParent(stackPanel);
            valueControl.setParent(stackPanel);
        }
    }

    @Override
    public void onTick() {
        valueControl.setText(Text.literal(valueSupplier.get()).getString());
    }

}
