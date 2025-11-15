package net.msymbios.rlovelyr.client.gui.control.controls;

import com.mojang.blaze3d.systems.RenderSystem;
import net.msymbios.rlovelyr.client.gui.control.BaseControl;
import net.msymbios.rlovelyr.client.gui.control.Control;
import net.msymbios.rlovelyr.client.gui.control.event.events.*;
import net.msymbios.rlovelyr.client.gui.control.event.events.input.*;
import net.msymbios.rlovelyr.client.gui.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScreenControl extends Control {
    
    private final List<BaseControl> measureList = new ArrayList<>();

    private final List<BaseControl> arrangeList = new ArrayList<>();

    private BaseControl hoveredControl;

    private BaseControl pressedControl;

    /**
     * The pixel x coordinate that the pressed control was pressed at.
     */
    private double pressedStartPixelX = 0.0;

    /**
     * The pixel y coordinate that the pressed control was pressed at.
     */
    private double pressedStartPixelY = 0.0;

    private BaseControl focusedControl;

    private BaseControl draggedControl;

    /**
     * Whether the screen should really close and wipe all event subscribers.
     */
    private boolean shouldReallyClose = true;

    public void measureAndArrange() {
        measureList.removeIf(control -> control.getScreen() != this);
        arrangeList.removeIf(control -> control.getScreen() != this);

        List<BaseControl> filteredMeasureList = filterControls(measureList);
        List<BaseControl> filteredArrangeList = filterControls(arrangeList);

        while (!filteredMeasureList.isEmpty() || !filteredArrangeList.isEmpty()) {
            while (!filteredMeasureList.isEmpty()) {
                int minDepth = Integer.MAX_VALUE;
                BaseControl minDepthControl = null;

                for (BaseControl control : filteredMeasureList) {
                    if (control.getTreeDepth() < minDepth) {
                        minDepth = control.getTreeDepth();
                        minDepthControl = control;
                    }
                }

                if (minDepthControl == this) {
                    minDepthControl.doMeasure(getWidth(), getHeight());
                } else if (minDepthControl != null && minDepthControl.getParent() != null) {
                    minDepthControl.getParent().measureChildren();
                }

                filteredMeasureList = filterControls(measureList);
            }

            filteredArrangeList = filterControls(arrangeList);

            while (!filteredArrangeList.isEmpty() && filteredMeasureList.isEmpty()) {
                int minDepth = Integer.MAX_VALUE;
                BaseControl minDepthControl = null;

                for (BaseControl control : filteredArrangeList) {
                    if (control.getTreeDepth() < minDepth) {
                        minDepth = control.getTreeDepth();
                        minDepthControl = control;
                    }
                }

                if (minDepthControl != null) {
                    minDepthControl.doArrange();
                }

                filteredMeasureList = filterControls(measureList);
                filteredArrangeList = filterControls(arrangeList);
            }

            filteredMeasureList = filterControls(measureList);
            filteredArrangeList = filterControls(arrangeList);
        }
    }

    /**
     * Filters the given list.
     *
     * @param controls the list to filter.
     * @return the filtered list.
     */
    private List<BaseControl> filterControls(List<BaseControl> controls) {
        return controls.stream().filter(c -> !c.isCollapsedOrAncestor()).collect(Collectors.toList());
    }

    @Override
    public void measureSelf(double availableWidth, double availableHeight) {
        setDesiredWidth(availableWidth);
        setDesiredHeight(availableHeight);
    }

    @Override
    public void arrange() {
        setSize(getDesiredSize());

        super.arrange();
    }

    public boolean isInMeasureQueue(BaseControl control) {
        return measureList.contains(control);
    }

    public void addToMeasureQueue(BaseControl control) {
        if (!measureList.contains(control)) {
            measureList.add(control);
        }
    }

    public void removeFromMeasureQueue(BaseControl control) {
        measureList.remove(control);
    }

    public boolean isInArrangeQueue(BaseControl control) {
        return arrangeList.contains(control);
    }

    public void addToArrangeQueue(BaseControl control) {
        if (!arrangeList.contains(control)) {
            arrangeList.add(control);
        }
    }

    public void removeFromArrangeQueue(BaseControl control) {
        arrangeList.remove(control);
    }

    public void render(int screenMouseX, int screenMouseY, float partialTicks) {
        float guiScale = GuiUtil.get().getGuiScale();
        double mouseX = GuiUtil.get().getPixelMouseX();
        double mouseY = GuiUtil.get().getPixelMouseY();

        ScissorStack scissorStack = new ScissorStack();
        TryHoverEvent e = new TryHoverEvent(mouseX, mouseY);

        forwardHover(e);

        if (!e.isHandled()) {
            setHoveredControl(null);
        }

        forwardTryDrag(new TryDragEvent(mouseX, mouseY));

        if (getDraggedControl() != null) {
            getDraggedControl().onDrag(mouseX, mouseY, partialTicks);
        }

        measureAndArrange();

        //poseStack.pushPose();
        //poseStack.scale(1.0f / guiScale, 1.0f / guiScale, 1.0f);

        forwardRender(scissorStack, mouseX, mouseY, partialTicks);

        //poseStack.popPose();

        if (getHoveredControl() != null && getDraggedControl() == null) {
            //poseStack.pushPose();
            //poseStack.scale((float) getHoveredControl().getScaleX(), (float) getHoveredControl().getScaleY(), 1.0f);

            RenderSystem.enableDepthTest();
            getHoveredControl().onRenderTooltip(mouseX, mouseY, partialTicks);

            //poseStack.popPose();
        }
    }

    @Override
    protected void onRender( ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks) {

    }

    @Override
    public void forwardMouseClicked(MouseClickedEvent e) {
        forwardGlobalMouseClicked(e);
        super.forwardMouseClicked(e);
    }

    @Override
    protected void onMouseClicked(MouseClickedEvent e) {

    }

    @Override
    public void forwardMouseReleased(MouseReleasedEvent e) {
        forwardGlobalMouseReleased(e);
        super.forwardMouseReleased(e);

        setPressedControl(null);
        setDraggedControl(null);
    }

    @Override
    protected void onMouseReleased(MouseReleasedEvent e) {

    }

    @Override
    public void forwardMouseScrolled(MouseScrolledEvent e) {
        forwardGlobalMouseScrolled(e);
        super.forwardMouseScrolled(e);
    }

    @Override
    public void onMouseScrolled(MouseScrolledEvent e) {

    }

    @Override
    public void forwardGlobalKeyPressed(KeyPressedEvent e) {
        super.forwardGlobalKeyPressed(e);

        if (!e.isHandled() && getFocusedControl() != null) {
            getFocusedControl().forwardKeyPressed(e);
        } else if (!e.isHandled()) {
            forwardKeyPressed(e);
        }
    }

    @Override
    public void forwardGlobalKeyReleased(KeyReleasedEvent e) {
        super.forwardGlobalKeyReleased(e);

        if (!e.isHandled() && getFocusedControl() != null) {
            getFocusedControl().forwardKeyReleased(e);
        } else if (!e.isHandled()) {
            forwardKeyReleased(e);
        }
    }

    @Override
    public void forwardGlobalCharTyped(CharTypedEvent e) {
        super.forwardGlobalCharTyped(e);

        if (!e.isHandled() && getFocusedControl() != null) {
            getFocusedControl().forwardCharTyped(e);
        } else if (!e.isHandled()) {
            forwardCharTyped(e);
        }
    }

    @Override
    public void setParent(BaseControl parent) {
        // The screen control should never have a parent.
    }

    @Override
    public ScreenControl getScreen() {
        return this;
    }

    @Override
    public BaseControl getHoveredControl() {
        return hoveredControl;
    }

    public void setHoveredControl(BaseControl control) {
        if (hoveredControl != control) {
            if (hoveredControl != null) {
                hoveredControl.onHoverExit();
            }

            if (control != null) {
                control.onHoverEnter();
            }
        }

        hoveredControl = control;
    }

    @Override
    public BaseControl getPressedControl() {
        return pressedControl;
    }

    public void setPressedControl(BaseControl control) {
        if (pressedControl != control) {
            if (pressedControl != null) {
                pressedControl.onPressEnd();
            }

            if (control != null) {
                control.onPressStart();
                setPressedStartPixelX(GuiUtil.get().getPixelMouseX());
                setPressedStartPixelY(GuiUtil.get().getPixelMouseY());
            }
        }

        pressedControl = control;
    }

    @Override
    public BaseControl getFocusedControl() {
        return focusedControl;
    }

    public void setFocusedControl(BaseControl control) {
        if (focusedControl == control) {
            return;
        }

        BaseControl previousFocusedControl = focusedControl;

        focusedControl = control;

        if (previousFocusedControl != null) {
            previousFocusedControl.eventBus.post(previousFocusedControl, new FocusChangedEvent(true, false));
            eventBus.post(previousFocusedControl, new FocusChangedEvent(true, false));
            previousFocusedControl.onUnfocused();
        }

        if (focusedControl != null) {
            focusedControl.eventBus.post(focusedControl, new FocusChangedEvent(false, true));
            eventBus.post(focusedControl, new FocusChangedEvent(false, true));
            focusedControl.onFocused();
        }
    }

    @Override
    public BaseControl getDraggedControl() {
        return draggedControl;
    }

    public void setDraggedControl(BaseControl control) {
        if (draggedControl != control) {
            if (draggedControl != null) {
                draggedControl.onDragEnd();
            }

            if (control != null) {
                control.setPreDragPosition(control.getX(), control.getY());
                control.onDragStart(GuiUtil.get().getPixelMouseX(), GuiUtil.get().getPixelMouseY());
            }
        }

        draggedControl = control;
    }

    public double getPressedStartPixelX() {
        return pressedStartPixelX;
    }

    public void setPressedStartPixelX(double pressedStartPixelX) {
        this.pressedStartPixelX = pressedStartPixelX;
    }

    public double getPressedStartPixelY() {
        return pressedStartPixelY;
    }

    public void setPressedStartPixelY(double pressedStartPixelY) {
        this.pressedStartPixelY = pressedStartPixelY;
    }

    /**
     * @return Whether the screen should really close or not.
     */
    public boolean shouldReallyClose() {
        return shouldReallyClose;
    }

    /**
     * Sets whether the screen should really close or not.
     *
     * @param shouldReallyClose Whether the screen should really close or not.
     */
    public void setShouldReallyClose(boolean shouldReallyClose) {
        this.shouldReallyClose = shouldReallyClose;
    }
}
