package net.msymbios.rlovelyr.client.gui.control.controls;

import net.minecraft.network.chat.Component;
import net.msymbios.rlovelyr.client.gui.control.BaseControl;
import net.msymbios.rlovelyr.client.gui.control.event.events.TextChangedEvent;
import net.msymbios.rlovelyr.client.gui.util.GuiUtil;
import net.msymbios.rlovelyr.util.event.ValueChangedEvent;

/**
 * A text field that only accepts integers.
 */
public class NullableIntFieldControl extends TextFieldControl {

    /**
     * The minimum value that can be entered.
     */
    private int minVal = Integer.MIN_VALUE;

    /**
     * The maximum value that can be entered.
     */
    private int maxVal = Integer.MAX_VALUE;

    /**
     *
     */
    public NullableIntFieldControl() {
        super();

        eventBus.subscribe(this::onTextChanged);
    }

    @Override
    public void onRenderTooltip(double mouseX, double mouseY, float partialTicks) {
        if (isTextTrimmed()) {
            renderTooltip(mouseX, mouseY, Component.literal(getText()));
        }
    }

    /**
     * Called when the text is changed.
     *
     * @param c the control.
     * @param e the event.
     */
    protected void onTextChanged(BaseControl c, TextChangedEvent e) {
        Integer oldValue = null;
        Integer newValue = null;

        if (!e.oldText.isEmpty() && !e.oldText.equals("-")) {
            try {
                oldValue = Integer.parseInt(e.oldText);
            } catch (NumberFormatException ex) {
                oldValue = e.oldText.contains("-") ? getMinVal() : getMaxVal();
            }
        }

        if (!e.newText.isEmpty() && !e.newText.equals("-")) {
            try {
                newValue = Integer.parseInt(e.newText);
            } catch (NumberFormatException ex) {
                newValue = e.newText.contains("-") ? getMinVal() : getMaxVal();
            }
        }

        eventBus.post(this, new ValueChangedEvent<>(oldValue, newValue));
    }

    @Override
    protected boolean isValidText( String text) {
        try {
            Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return text.isEmpty() || text.equals("-");
        }

        return true;
    }

    @Override
    
    protected String processText( String text) {
        if (text.isEmpty() || text.equals("-")) {
            return text;
        }

        try {
            int value = Integer.parseInt(text);

            if (value < getMinVal()) return String.valueOf(getMinVal());
            if (value > getMaxVal()) return String.valueOf(getMaxVal());

            return text;
        } catch (NumberFormatException e) {
            return text.contains("-") ? String.valueOf(getMinVal()) : String.valueOf(getMaxVal());
        }
    }

    /**
     * @return the value of the int field.
     */
    public Integer getValue() {
        return getText().isEmpty() || getText().equals("-") ? null : Integer.parseInt(getText());
    }

    /**
     * Sets the value of the int field.
     *
     * @param value the value to set.
     */
    public void setValue(Integer value) {
        setText(value != null ? String.valueOf(value) : "");
    }

    /**
     * @return the minimum value that can be entered.
     */
    public int getMinVal() {
        return minVal;
    }

    /**
     * Sets the minimum value that can be entered.
     *
     * @param minVal the minimum value that can be entered.
     */
    public void setMinVal(int minVal) {
        this.minVal = minVal;
    }

    /**
     * @return the maximum value that can be entered.
     */
    public int getMaxVal() {
        return maxVal;
    }

    /**
     * Sets the maximum value that can be entered.
     *
     * @param maxVal the maximum value that can be entered.
     */
    public void setMaxVal(int maxVal) {
        this.maxVal = maxVal;
    }

    /**
     * @return whether the text is trimmed.
     */
    public boolean isTextTrimmed() {
        return !GuiUtil.get().trim(Component.literal(getText()), (int) getWidthWithoutPadding()).getString().equals(getText());
    }

} // Class NullableIntFieldControl