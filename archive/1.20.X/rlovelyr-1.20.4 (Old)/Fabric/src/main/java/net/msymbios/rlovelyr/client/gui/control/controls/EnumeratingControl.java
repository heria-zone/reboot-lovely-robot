package net.msymbios.rlovelyr.client.gui.control.controls;

import net.msymbios.rlovelyr.client.gui.control.BaseControl;
import net.msymbios.rlovelyr.client.gui.control.Control;
import net.msymbios.rlovelyr.client.gui.control.event.events.input.*;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Enumerates a list of controls in place of each other.
 */
public class EnumeratingControl<T extends Control> extends Control {
    
    /**
     * The controls to enumerate.
     */
    protected final List<T> controls = new ArrayList<>();

    /**
     * The corresponding list of display conditions for each control.
     */
    protected final List<Supplier<Boolean>> displayConditions = new ArrayList<>();

    /**
     * The number of ticks between switching to the next control.
     */
    private int interval = 60;

    /**
     * The number of ticks the current control has been displayed.
     */
    private int tickCount = 0;

    /**
     *
     */
    public EnumeratingControl() {
        setFitWidthToContent(true);
        setFitHeightToContent(true);
    }

    @Override
    public void onTick() {
        tickCount++;

        if (tickCount > interval) {
            switchToControl(true);
        }
    }

    /**
     * Switches to the next control in the list.
     *
     * @param forwards whether to enumerate forwards or backwards.
     */
    private void switchToControl(boolean forwards) {
        tickCount = 0;

        if (controls.isEmpty()) {
            return;
        }

        int indexOfCurrentChild = getIndexOfCurrentChild();

        for (int i = 1; i <= controls.size(); i++) {
            int indexOfNewChild = (indexOfCurrentChild + (forwards ? i : -i) + controls.size()) % controls.size();

            if (displayConditions.get(indexOfNewChild).get()) {
                clearChildren();
                addChild(controls.get(indexOfNewChild));

                break;
            }
        }
    }

    /**
     * @return the index of the currently displayed child.
     */
    protected int getIndexOfCurrentChild() {
        return getChildren().isEmpty() ? -1 : controls.indexOf(getChildren().get(0));
    }

    @Override
    protected void onMouseReleased(MouseReleasedEvent e) {
        if (e.button == GLFW.GLFW_MOUSE_BUTTON_1) {
            switchToControl(true);
        } else {
            switchToControl(false);
        }

        //e.setIsHandled(true);
    }

    /**
     * @return whether the control is part of the enumeration.
     */
    public boolean contains(T control) {
        return controls.contains(control);
    }

    /**
     * Adds the given control to the list of control to be enumerated.
     */
    public void addControl(T control) {
        addControl(control, () -> true);
    }

    /**
     * Adds the given control to the list of control to be enumerated.
     */
    public void addControl(T control, Supplier<Boolean> displayCondition) {
        controls.add(control);
        displayConditions.add(displayCondition);

        if (!getChildren().isEmpty()) {
            BaseControl current = getChildren().get(0);
            clearChildren();
            addChild(control);
            clearChildren();
            addChild(current);
        } else {
            addChild(control);
            clearChildren();
        }

        if (getIndexOfCurrentChild() == -1) {
            switchToControl(true);
        }
    }

    /**
     * Removes the given control from the list of control to be enumerated.
     */
    public void removeControl(T control) {
        // If we are removing the displayed control, switch to a new one.
        if (!getChildren().isEmpty() && getChildren().get(0) == control) {
            switchToControl(true);
        }

        displayConditions.remove(controls.indexOf(control));
        controls.remove(control);
    }

    /**
     * @return the current interval ticks.
     */
    public int getInterval() {
        return interval;
    }

    /**
     * Sets the interval to the given number of ticks.
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }
}

