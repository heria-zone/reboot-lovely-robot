package net.msymbios.rlovelyr.client.gui.control.controls;

import net.msymbios.rlovelyr.client.gui.control.BaseControl;
import net.msymbios.rlovelyr.client.gui.control.event.events.*;
import net.msymbios.rlovelyr.util.event.ValueChangedEvent;

/**
 * A text field that only accepts integers.
 */
public class IntFieldControl extends NullableIntFieldControl {
    /**
     *
     */
    public IntFieldControl() {
        super();
    }

    @Override
    protected void onTextChanged(BaseControl c, TextChangedEvent e) {
        int oldValue = 0;
        int newValue = 0;

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
    public void onUnfocused() {
        super.onUnfocused();

        if (getText().isEmpty() || getText().equals("-")) setText("0");
    }

    @Override
    public Integer getValue() {
        return Integer.parseInt(getText());
    }

    @Override
    public void setValue(Integer value) {
        super.setValue(value == null ? 0 : value);
    }
}
