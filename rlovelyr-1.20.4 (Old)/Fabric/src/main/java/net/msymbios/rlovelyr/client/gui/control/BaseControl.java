package net.msymbios.rlovelyr.client.gui.control;

import net.msymbios.rlovelyr.client.gui.control.controls.ScreenControl;
import net.msymbios.rlovelyr.client.gui.control.event.events.*;
import net.msymbios.rlovelyr.client.gui.control.event.events.input.*;
import net.msymbios.rlovelyr.client.gui.properties.*;
import net.msymbios.rlovelyr.client.gui.properties.enums.*;
import net.msymbios.rlovelyr.client.gui.util.Colour;
import net.msymbios.rlovelyr.client.gui.util.GuiUtil;
import net.msymbios.rlovelyr.client.gui.util.ScissorStack;
import net.msymbios.rlovelyr.util.DoubleUtil;

import java.util.*;

/**
 * A base class for {@link Control}. Used to reduce the size of {@link Control}.
 */
public abstract class BaseControl extends GuiControl {

    // -- Variables --

    public static final Random random = new Random();

    public final ControlEventBus eventBus = new ControlEventBus();

    /**
     * The event bus that events fired on the screen are forwarded to.
     */
    public final ControlEventBus screenEventBus = new ControlEventBus();

    /**
     * A string used as a debug name for this control.
     */
    private String debugName = "Control";

    private boolean isMeasuring = false;

    private boolean isArranging = false;

    private BaseControl parent = null;

    private final LinkedList<BaseControl> children = new LinkedList<>();

    private final Scale innerScale = new Scale(1.0, 1.0);

    private final Size desiredSize = new Size(0.0, 0.0);

    private final Size minSize = new Size(0.0, 0.0);
    private final Size maxSize = new Size(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

    private final Size size = new Size(desiredSize.width, desiredSize.height);

    private boolean shouldFitWidthToContent = false;

    private boolean shouldFitHeightToContent = false;

    private Double widthPercentage = null;

    private Double heightPercentage = null;

    private final Margin margin = new Margin(0.0, 0.0, 0.0, 0.0);

    private final Padding padding = new Padding(0.0, 0.0, 0.0, 0.0);

    private final Position position = new Position(0.0, 0.0);

    /**
     * The position of the control before it was dragged.
     */
    private final Position preDragPosition = new Position(0.0, 0.0);

    private boolean shouldSnapToScreenCoords = false;

    private boolean shouldSnapToPixelCoords = false;

    private Double horizontalAlignment = null;
    private Double verticalAlignment = null;

    private Double horizontalContentAlignment = null;
    private Double verticalContentAlignment = null;

    private final Offset scroll = new Offset(0.0, 0.0);

    private final Offset minScroll = new Offset(0.0, 0.0);

    private final Offset maxScroll = new Offset(0.0, 0.0);

    private boolean canScrollHorizontally = false;

    private boolean canScrollVertically = false;

    private Visibility visibility = Visibility.VISIBLE;

    private boolean isHoverable = true;

    private boolean isPressable = true;

    private boolean isFocusable = true;

    private boolean isInteractive = true;

    private boolean areChildrenInteractive = true;

    private boolean isReorderable = true;

    private boolean isDraggableX = false;

    private boolean isDraggableY = false;

    private double dragThreshold = 4.0;

    /**
     * The z value to render at when dragging. If null, use {@link #renderZ}.
     */
    private Double dragZ = 50.0;

    private boolean shouldBlockDrag = true;

    /**
     * Whether to propagate the drag event to the parent control if this control doesn't handle it.
     */
    private boolean shouldPropagateDrag = true;

    /**
     * The control that will scroll when this control is dragged beyond its bounds.
     */
    private BaseControl scrollFromDragControl = null;

    /**
     * Whether to clip its contents to its bounds. If null, inherit from parent.
     */
    private Boolean shouldClipContentsToBounds = true;

    private double renderZ = 0.0;

    private Colour foregroundColour = new Colour(0xffffffff);

    private Colour backgroundColour = new Colour(0x00000000);

    // -- Inherited Methods --

    @Override
    public String toString() {
        return getDebugName();
    }

    // -- Custom Methods --

    /**
     * Calls {@link #measureSelf(double, double)} then {@link #measureChildren()} while also setting
     * {@link #isMeasuring} appropriately and {@link #markMeasureDirty(boolean)} to false after {@link #measureSelf(double, double)}.
     *
     * @param availableWidth  the available width to measure with.
     * @param availableHeight the available height to measure with.
     */
    public abstract void doMeasure(double availableWidth, double availableHeight);

    /**
     * Forwards the call to {@link #doMeasure(double, double)} to each child control. Might be overridden to calculate
     * the available width and height for each child control.
     */
    public abstract void measureChildren();

    /**
     * Uses the given {@param availableWidth} and {@param availableHeight} to calculate the desired width and height of
     * this control.
     *
     * @param availableWidth  the available width to measure with.
     * @param availableHeight the available height to measure with.
     */
    protected abstract void measureSelf(double availableWidth, double availableHeight);

    public abstract void doArrange();

    protected abstract void arrange();

    protected abstract void calculateScroll();

    /**
     * Forwards the call to {@link #onClose(boolean)} to each child control. This should be called when the screen is closed.
     *
     * @param isRealClose whether the screen is being properly closed, or only temporarily hidden.
     */
    public abstract void forwardClose(boolean isRealClose);

    /**
     * Called when the attached screen is closed.
     *
     * @param isRealClose whether the screen is being properly closed, or only temporarily hidden.
     */
    public abstract void onClose(boolean isRealClose);

    public abstract void forwardTick();

    public abstract void onTick();

    public abstract void forwardRender(ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks);

    protected abstract void onRenderUpdate(ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks);

    protected abstract void onRender(ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks);

    public abstract void onRenderTooltip(double mouseX, double mouseY, float partialTicks);

    public abstract void forwardHover(TryHoverEvent e);

    public abstract void onHoverEnter();

    public abstract void onHoverExit();

    public abstract void onPressStart();

    public abstract void onPressEnd();

    public abstract void onFocused();

    public abstract void onUnfocused();

    public abstract void forwardTryDrag(TryDragEvent e);

    public abstract void onDragStart(double mouseX, double mouseY);

    public abstract void onDrag(double mouseX, double mouseY, float partialTicks);

    public abstract void onDragEnd();

    abstract public void forwardGlobalMouseClicked(MouseClickedEvent e);

    abstract protected void onGlobalMouseClicked(MouseClickedEvent e);

    abstract public void forwardMouseClicked(MouseClickedEvent e);

    abstract protected void onMouseClicked(MouseClickedEvent e);

    abstract public void forwardGlobalMouseReleased(MouseReleasedEvent e);

    abstract protected void onGlobalMouseReleased(MouseReleasedEvent e);

    abstract public void forwardMouseReleased(MouseReleasedEvent e);

    abstract protected void onMouseReleased(MouseReleasedEvent e);

    abstract public void forwardGlobalMouseScrolled(MouseScrolledEvent e);

    abstract protected void onGlobalMouseScrolled(MouseScrolledEvent e);

    abstract public void forwardMouseScrolled(MouseScrolledEvent e);

    abstract public void onMouseScrolled(MouseScrolledEvent e);

    abstract public void forwardGlobalKeyPressed(KeyPressedEvent e);

    abstract protected void onGlobalKeyPressed(KeyPressedEvent e);

    abstract public void forwardKeyPressed(KeyPressedEvent e);

    abstract protected void onKeyPressed(KeyPressedEvent e);

    abstract public void forwardGlobalKeyReleased(KeyReleasedEvent e);

    abstract protected void onGlobalKeyReleased(KeyReleasedEvent e);

    abstract public void forwardKeyReleased(KeyReleasedEvent e);

    abstract protected void onKeyReleased(KeyReleasedEvent e);

    abstract public void forwardGlobalCharTyped(CharTypedEvent e);

    abstract protected void onGlobalCharTyped(CharTypedEvent e);

    abstract public void forwardCharTyped(CharTypedEvent e);

    abstract protected void onCharTyped(CharTypedEvent e);

    abstract protected void onChildDesiredSizeChanged(BaseControl child);

    abstract protected void onChildSizeChanged(BaseControl child);

    abstract protected void onChildMarginChanged(BaseControl child);

    abstract protected void onChildPositionSizeChanged(BaseControl child);

    abstract protected void onChildAlignmentChanged(BaseControl child);

    abstract protected void onChildVisiblityChanged(BaseControl child);

    abstract public boolean contains(double pixelX, double pixelY);

    /**
     * Clears the event buses.
     *
     * @param forwardToChildren whether to forward the call to each child control.
     */
    protected void clearEventBuses(boolean forwardToChildren) {
        if (forwardToChildren) {
            for (BaseControl child : getChildren()) {
                child.clearEventBuses(true);
            }
        }

        eventBus.clear();
        screenEventBus.clear();
    }

    public boolean isMeasuring() {
        return isMeasuring;
    }

    public void setMeasuring(boolean measuring) {
        isMeasuring = measuring;
    }

    public boolean isMeasureDirty() {
        return getScreen() == null || getScreen().isInMeasureQueue(this);
    }

    public void markMeasureDirty(boolean isDirty) {
        if (getScreen() != null) {
            if (isDirty) {
                getScreen().addToMeasureQueue(this);
            } else {
                getScreen().removeFromMeasureQueue(this);
            }
        }
    }

    public boolean isArranging() {
        return isArranging;
    }

    public void setArranging(boolean arranging) {
        isArranging = arranging;
    }

    public boolean isArrangeDirty() {
        return getScreen() == null || getScreen().isInArrangeQueue(this);
    }

    public void markArrangeDirty(boolean isDirty) {
        if (getScreen() != null) {
            if (isDirty) {
                getScreen().addToArrangeQueue(this);
            } else {
                getScreen().removeFromArrangeQueue(this);
            }
        }
    }

    /**
     * @return the screen that this control is attached to.
     */
    public BaseControl getParent() {
        return parent;
    }

    /**
     * Sets the parent of this control. This will also add this control to the parent's children list.
     * If the parent is null, this control will be removed from the parent's children list. If the
     * parent is the same as the current parent, this method will do nothing. If the parent is not
     * null, this control will be removed from the current parent's children list. This method will also
     * remove the event subscribers of this control from the current parent's event bus. If you want to
     * preserve the event subscribers, use {@link #setParent(BaseControl, boolean)}.
     *
     * @param parent the new parent.
     */
    public void setParent(BaseControl parent) {
        setParent(parent, false);
    }

    /**
     * Sets the parent of this control. This will also add this control to the parent's children list.
     * If the parent is null, this control will be removed from the parent's children list. If the
     * parent is the same as the current parent, this method will do nothing. If the parent is not
     * null, this control will be removed from the current parent's children list.
     * <br><br>
     * {@code preserveEventSubscribers} is used to preserve the event subscribers of this control when
     * removing the parent. Normally, event subscribers are removed when the parent is removed to avoid
     * memory leaks. But if you are removing the parent temporarily, you can use this parameter to
     * preserve the event subscribers.
     *
     * @param parent                   the new parent.
     * @param preserveEventSubscribers if true, the event subscribers will be preserved.
     */
    public void setParent(BaseControl parent, boolean preserveEventSubscribers) {
        if (getParent() == parent) {
            return;
        }

        if (parent != null) {
            parent.addChild(this);
        } else {
            if (getParent() != null) {
                getParent().removeChild(this, preserveEventSubscribers);
            }
        }
    }

    /**
     * @return the children list.
     */
    public List<BaseControl> getChildren() {
        return children;
    }

    /**
     * @return a copy of the children list.
     */
    public List<BaseControl> getChildrenCopy() {
        return new ArrayList<>(children);
    }

    /**
     * @return a copy of the children list, but in reverse order.
     */
    public List<BaseControl> getReverseChildrenCopy() {
        List<BaseControl> reverseChildren = new ArrayList<>(children);
        Collections.reverse(reverseChildren);

        return reverseChildren;
    }

    /**
     * @param child the child to check.
     * @return true if the given control is a child of this control.
     */
    public boolean isChild(BaseControl child) {
        return getChildren().contains(child);
    }

    /**
     * Adds the given control as a child of this control. If the control is already a child of this control,
     * nothing will happen. If the control is a child of another control, it will be removed from that control.
     *
     * @param child the control to add.
     */
    public void addChild(BaseControl child) {
        if (getChildren().contains(child)) {
            return;
        }

        if (child.getParent() != null) {
            child.getParent().removeChild(child, true);
        }

        BaseControl oldParent = child.getParent();

        child.removeChainedScreenBus();
        children.add(child);
        child.parent = this;
        child.addChainedScreenBus();

        child.eventBus.post(child, new ParentChangedEvent(oldParent, this));

        markMeasureDirty(true);
        markArrangeDirty(true);
    }

    /**
     * Inserts the given control at the given index. If the control is already a child of this control,
     * it will be moved. If the control is a child of another control, it will be removed from that control.
     *
     * @param controlToInsert       the control to insert.
     * @param controlToInsertBefore the control to insert before.
     */
    public void insertChildBefore(BaseControl controlToInsert, BaseControl controlToInsertBefore) {
        int beforeIndex = children.indexOf(controlToInsertBefore);

        if (beforeIndex == -1) {
            throw new IllegalArgumentException("The given control to insert before is not a child of this control.");
        }

        int index = children.indexOf(controlToInsert);

        if (index == -1) {
            insertChildAt(controlToInsert, beforeIndex);
        } else {
            insertChildAt(controlToInsert, index > beforeIndex ? beforeIndex : beforeIndex - 1);
        }

        markMeasureDirty(true);
        markArrangeDirty(true);
    }

    /**
     * Inserts the given control at the given index. If the control is already a child of this control,
     * it will be moved. If the control is a child of another control, it will be removed from that control.
     *
     * @param controlToInsert      the control to insert
     * @param controlToInsertAfter the control to insert after.
     */
    public void insertChildAfter(BaseControl controlToInsert, BaseControl controlToInsertAfter) {
        int afterIndex = children.indexOf(controlToInsertAfter);

        if (afterIndex == -1) {
            throw new IllegalArgumentException("The given control to insert after is not a child of this control.");
        }

        int index = children.indexOf(controlToInsert);

        if (index == -1) {
            insertChildAt(controlToInsert, afterIndex + 1);
        } else {
            insertChildAt(controlToInsert, index < afterIndex ? afterIndex : afterIndex + 1);
        }

        markMeasureDirty(true);
        markArrangeDirty(true);
    }

    /**
     * Inserts the given control at the given index. If the control is already a child of this control,
     * it will be moved. If the control is a child of another control, it will be removed from that control.
     * If the control's current index is less than the given index, the controls below the given index will
     * move down. If the control's current index is greater than the given index, the controls above the
     * given index will move up. If the control is not a child of this control, it will be added as a child
     * and the controls at or above the given index will move up.
     * <br><br>
     * E.g. if the control is currently at index 2, and the given index is 4, the control will be moved
     * to index 4, and the controls at index 3 and 4 will move down to index 2 and 3 respectively.
     * If the control is currently at index 4, and the given index is 2, the control will be moved to
     * index 2, and the controls at index 2 and 3 will move up to index 3 and 4 respectively.
     *
     * @param controlToInsert the control to insert.
     * @param index           the index to insert the control at.
     */
    public void insertChildAt(BaseControl controlToInsert, int index) {
        if (index < 0 || index > children.size()) {
            throw new IllegalArgumentException("The given index is out of bounds.");
        }

        BaseControl oldParent = controlToInsert.getParent();
        int indexOfControlToInsert = children.indexOf(controlToInsert);

        if (indexOfControlToInsert != -1) {
            children.set(indexOfControlToInsert, null);
            children.remove(null);
        } else {
            if (controlToInsert.getParent() != null) {
                controlToInsert.getParent().removeChild(controlToInsert, true);
            }
        }

        children.add(index, controlToInsert);

        controlToInsert.removeChainedScreenBus();
        controlToInsert.parent = this;
        controlToInsert.addChainedScreenBus();

        if (this != oldParent) {
            controlToInsert.eventBus.post(controlToInsert, new ParentChangedEvent(oldParent, this));
        }

        markMeasureDirty(true);
        markArrangeDirty(true);
    }

    /**
     * Inserts the given control at the beginning of the list of children. If the control is already a child
     * of this control, it will be moved to the beginning of the list of children. If the control is a child
     * of another control, it will be removed from that control.
     *
     * @param controlToInsert the control to insert.
     */
    public void insertChildFirst(BaseControl controlToInsert) {
        insertChildAt(controlToInsert, 0);
    }

    /**
     * Inserts the given control at the end of the list of children. If the control is already a child
     * of this control, it will be moved to the end of the list of children. If the control is a child
     * of another control, it will be removed from that control.
     *
     * @param controlToInsert the control to insert.
     */
    public void insertChildLast(BaseControl controlToInsert) {
        insertChildAt(controlToInsert, children.contains(controlToInsert) ? children.size() - 1 : children.size());
    }

    /**
     * Removes the given child from this control. If the given child is not a child of this control,
     * nothing will happen. This will also remove the event subscribers of the child. If you want to preserve
     * the event subscribers of the child, use {@link #removeChild(BaseControl, boolean)}.
     *
     * @param child the child to remove.
     */
    public void removeChild(BaseControl child) {
        removeChild(child, false);
    }

    /**
     * Removes the given child from this control. If the given child is not a child of this control,
     * nothing will happen.
     * <br><br>
     * {@code preserveEventSubscribers} is used to preserve the event subscribers of the child when
     * removing the control. This is useful when you want to remove the control from the parent, but
     * still want to receive events from the child.
     *
     * @param child                    the child to remove.
     * @param preserveEventSubscribers whether to preserve the event subscribers of the child.
     */
    public void removeChild(BaseControl child, boolean preserveEventSubscribers) {
        if (!getChildren().contains(child)) {
            return;
        }

        BaseControl oldParent = child.getParent();

        children.remove(child);

        child.removeChainedScreenBus();
        child.parent = null;

        child.eventBus.post(child, new ParentChangedEvent(oldParent, null));

        if (!preserveEventSubscribers) {
            child.clearEventBuses(true);
        }

        markMeasureDirty(true);
        markArrangeDirty(true);
    }

    /**
     * Removes all children from this control. This will also remove the event subscribers of the children.
     */
    public void clearChildren() {
        clearChildren(false);
    }

    /**
     * Removes all children from this control.
     *
     * @param preserveEventSubscribers whether to preserve the event subscribers of the children.
     */
    public void clearChildren(boolean preserveEventSubscribers) {
        for (BaseControl child : getChildrenCopy()) {
            removeChild(child, preserveEventSubscribers);
        }
    }

    /**
     * Adds a screen bus to the control.
     */
    private void addChainedScreenBus() {
        if (getScreen() != null) {
            getScreen().eventBus.addChainedBus(screenEventBus);

            for (BaseControl child : getChildren()) {
                child.addChainedScreenBus();
            }
        }
    }

    /**
     * Remove the chained screen bus.
     */
    private void removeChainedScreenBus() {
        if (getScreen() != null) {
            getScreen().eventBus.removeChainedBus(screenEventBus);

            for (BaseControl child : getChildren()) {
                child.removeChainedScreenBus();
            }
        }
    }

    public int getTreeDepth() {
        return getParent() != null ? getParent().getTreeDepth() + 1 : 0;
    }

    public boolean isThisOrAncestor(BaseControl control) {
        return control == this || isAncestor(control);
    }

    public boolean isAncestor(BaseControl control) {
        if (control == null) {
            return false;
        }

        if (control == this) {
            return false;
        }

        if (getParent() == null) {
            return false;
        }

        if (control == getParent()) {
            return true;
        }

        return getParent().isAncestor(this);
    }

    public boolean isThisOrDescendant(BaseControl control) {
        return control == this || isDescendant(control);
    }

    public boolean isDescendant(BaseControl control) {
        if (control == null) {
            return false;
        }

        if (control == this) {
            return false;
        }

        if (getChildren().contains(control)) {
            return true;
        }

        return getChildren().stream().anyMatch(child -> child.isDescendant(control));
    }

    public ScreenControl getScreen() {
        return getParent() != null ? getParent().getScreen() : null;
    }

    public Scale getInnerScale() {
        return new Scale(innerScale);
    }

    public void setInnerScale(double innerScaleX, double innerScaleY) {
        if (innerScaleX == innerScale.x && innerScaleY == innerScale.y) {
            return;
        }

        if (!DoubleUtil.isPositiveAndFinite(innerScaleX)) {
            throw new IllegalArgumentException("The inner scale X must be positive and finite.");
        }

        if (!DoubleUtil.isPositiveAndFinite(innerScaleY)) {
            throw new IllegalArgumentException("The inner scale Y must be positive and finite.");
        }

        innerScale.x = innerScaleX;
        innerScale.y = innerScaleY;

        markMeasureDirty(true);
        markArrangeDirty(true);
    }

    public Size getDesiredSize() {
        return new Size(desiredSize);
    }

    public void setDesiredSize(double desiredWidth, double desiredHeight) {
        desiredWidth = DoubleUtil.clamp(desiredWidth, getMinWidth(), getMaxWidth());
        desiredHeight = DoubleUtil.clamp(desiredHeight, getMinHeight(), getMaxHeight());

        if (desiredWidth == desiredSize.width && desiredHeight == desiredSize.height) {
            return;
        }

        if (!DoubleUtil.isPositiveAndFinite(desiredWidth)) {
            throw new IllegalArgumentException("The desired width must be positive and finite.");
        }

        if (!DoubleUtil.isPositiveAndFinite(desiredHeight)) {
            throw new IllegalArgumentException("The desired height must be positive and finite.");
        }

        desiredSize.width = desiredWidth;
        desiredSize.height = desiredHeight;

        if (getParent() != null && isMeasuring()) {
            getParent().onChildDesiredSizeChanged(this);
        }
    }

    public void setDesiredSize(Size desiredSize) {
        setDesiredSize(desiredSize.width, desiredSize.height);
    }

    public double getDesiredWidth() {
        return desiredSize.width;
    }

    protected void setDesiredWidth(double desiredWidth) {
        setDesiredSize(desiredWidth, desiredSize.height);
    }

    public double getDesiredHeight() {
        return desiredSize.height;
    }

    protected void setDesiredHeight(double desiredHeight) {
        setDesiredSize(desiredSize.width, desiredHeight);
    }

    public Size getMinSize() {
        return new Size(minSize);
    }

    public void setMinSize(double minWidth, double minHeight) {
        minWidth = Math.max(0.0, minWidth);
        minHeight = Math.max(0.0, minHeight);

        if (minWidth == minSize.width && minHeight == minSize.height) {
            return;
        }

        minSize.width = minWidth;
        minSize.height = minHeight;

        markMeasureDirty(true);
    }

    public void setMinSize(Size minSize) {
        setMinSize(minSize.width, minSize.height);
    }

    public double getMinWidth() {
        return minSize.width;
    }

    public double getMinHeight() {
        return minSize.height;
    }

    public void setMinWidth(double minWidth) {
        setMinSize(minWidth, minSize.height);
    }

    public void setMinHeight(double minHeight) {
        setMinSize(minSize.width, minHeight);
    }

    public Size getMaxSize() {
        return new Size(maxSize);
    }

    public void setMaxSize(double maxWidth, double maxHeight) {
        maxWidth = Math.max(0.0, maxWidth);
        maxHeight = Math.max(0.0, maxHeight);

        if (maxWidth == maxSize.width && maxHeight == maxSize.height) {
            return;
        }

        maxSize.width = maxWidth;
        maxSize.height = maxHeight;

        markMeasureDirty(true);
    }

    public void setMaxSize(Size maxSize) {
        setMaxSize(maxSize.width, maxSize.height);
    }

    public double getMaxWidth() {
        return maxSize.width;
    }

    public double getMaxHeight() {
        return maxSize.height;
    }

    public void setMaxWidth(double maxWidth) {
        setMaxSize(maxWidth, maxSize.height);
    }

    public void setMaxHeight(double maxHeight) {
        setMaxSize(maxSize.width, maxHeight);
    }

    public Size getSize() {
        return new Size(size);
    }

    public Size getPixelSize() {
        return new Size(getPixelWidth(), getPixelHeight());
    }

    public void setSize(double width, double height) {
        width = DoubleUtil.clamp(width, getMinWidth(), getMaxWidth());
        height = DoubleUtil.clamp(height, getMinHeight(), getMaxHeight());

        if (width == size.width && height == size.height) {
            return;
        }

        double oldWidth = size.width;
        double oldHeight = size.height;

        size.width = width;
        size.height = height;

        if (!isMeasuring()) {
            markMeasureDirty(true);
        }

        if (getScreen() == null) {
            setDesiredSize(width, height);
        }

        if (getParent() != null) {
            getParent().onChildSizeChanged(this);
        }

        eventBus.post(this, new SizeChangedEvent(oldWidth, oldHeight));
    }

    public void setSize(Size size) {
        setSize(size.width, size.height);
    }

    public double getWidth() {
        return size.width;
    }

    public double getPixelWidth() {
        return getWidth() * getPixelScaleX();
    }

    public double getWidthWithMargin() {
        return getWidth() + getMarginWidth();
    }

    public double getWidthWithoutPadding() {
        return getWidth() - getPaddingWidth();
    }

    public double getPixelWidthWithoutPadding() {
        return getPixelWidth() - getPixelPaddingWidth();
    }

    public void setWidth(double width) {
        setSize(width, size.height);
    }

    public double getHeight() {
        return size.height;
    }

    public double getPixelHeight() {
        return getHeight() * getPixelScaleY();
    }

    public double getHeightWithMargin() {
        return getHeight() + getMarginHeight();
    }

    public double getHeightWithoutPadding() {
        return getHeight() - getPaddingHeight();
    }

    public double getPixelHeightWithoutPadding() {
        return getPixelHeight() - getPixelPaddingHeight();
    }

    public void setHeight(double height) {
        setSize(size.width, height);
    }

    public boolean shouldFitWidthToContent() {
        return shouldFitWidthToContent;
    }

    public void setFitWidthToContent(boolean shouldFitWidthToContent) {
        if (this.shouldFitWidthToContent == shouldFitWidthToContent) {
            return;
        }

        this.shouldFitWidthToContent = shouldFitWidthToContent;

        markMeasureDirty(true);
    }

    public boolean shouldFitHeightToContent() {
        return shouldFitHeightToContent;
    }

    public void setFitHeightToContent(boolean shouldFitHeightToContent) {
        if (this.shouldFitHeightToContent == shouldFitHeightToContent) {
            return;
        }

        this.shouldFitHeightToContent = shouldFitHeightToContent;

        markMeasureDirty(true);
    }

    public boolean shouldFitToContent() {
        return shouldFitWidthToContent || shouldFitHeightToContent;
    }

    public Double getWidthPercentage() {
        return widthPercentage;
    }

    public void setWidthPercentage(Double widthPercentage) {
        if (widthPercentage == this.widthPercentage) {
            return;
        }

        this.widthPercentage = widthPercentage == null ? null : Math.max(0.0, Math.min(1.0, widthPercentage));

        if (getParent() != null) {
            onChildSizeChanged(this);
        }
    }

    public Double getHeightPercentage() {
        return heightPercentage;
    }

    public void setHeightPercentage(Double heightPercentage) {
        if (heightPercentage == this.heightPercentage) {
            return;
        }

        this.heightPercentage = heightPercentage == null ? null : Math.max(0.0, Math.min(1.0, heightPercentage));

        if (getParent() != null) {
            onChildSizeChanged(this);
        }
    }

    public Margin getMargin() {
        return new Margin(margin);
    }

    public Margin getPixelMargin() {
        return new Margin(margin.left * getPixelScaleX(), margin.top * getPixelScaleY(), margin.right * getPixelScaleX(), margin.bottom * getPixelScaleY());
    }

    public double getMarginWidth() {
        return margin.left + margin.right;
    }

    public double getMarginHeight() {
        return margin.top + margin.bottom;
    }

    public void setMargins(double left, double top, double right, double bottom) {
        if (left == margin.left && top == margin.top && right == margin.right && bottom == margin.bottom) {
            return;
        }

        if (!DoubleUtil.isPositiveAndFinite(left) || !DoubleUtil.isPositiveAndFinite(top) || !DoubleUtil.isPositiveAndFinite(right) || !DoubleUtil.isPositiveAndFinite(bottom)) {
            throw new IllegalArgumentException("The margins must be positive and finite.");
        }

        margin.left = left;
        margin.top = top;
        margin.right = right;
        margin.bottom = bottom;

        if (getParent() != null) {
            getParent().onChildMarginChanged(this);
        }
    }

    public void setMargins(double margin) {
        setMargins(margin, margin, margin, margin);
    }

    public void setMargins(Margin margin) {
        setMargins(margin.left, margin.top, margin.right, margin.bottom);
    }

    public void setMarginLeft(double left) {
        setMargins(left, margin.top, margin.right, margin.bottom);
    }

    public void setMarginTop(double top) {
        setMargins(margin.left, top, margin.right, margin.bottom);
    }

    public void setMarginRight(double right) {
        setMargins(margin.left, margin.top, right, margin.bottom);
    }

    public void setMarginBottom(double bottom) {
        setMargins(margin.left, margin.top, margin.right, bottom);
    }

    public Padding getPadding() {
        return padding;
    }

    public Padding getPixelPadding() {
        return new Padding(padding.left * getPixelScaleX(), padding.top * getPixelScaleY(), padding.right * getPixelScaleX(), padding.bottom * getPixelScaleY());
    }

    public double getPaddingWidth() {
        return padding.left + padding.right;
    }

    public double getPixelPaddingWidth() {
        return getPixelPadding().left + getPixelPadding().right;
    }

    public double getPaddingHeight() {
        return padding.top + padding.bottom;
    }

    public double getPixelPaddingHeight() {
        return getPixelPadding().top + getPixelPadding().bottom;
    }

    public void setPadding(double left, double top, double right, double bottom) {
        if (left == padding.left && top == padding.top && right == padding.right && bottom == padding.bottom) {
            return;
        }

        if (!DoubleUtil.isPositiveAndFinite(left) || !DoubleUtil.isPositiveAndFinite(top) || !DoubleUtil.isPositiveAndFinite(right) || !DoubleUtil.isPositiveAndFinite(bottom)) {
            throw new IllegalArgumentException("The paddings must be positive and finite.");
        }

        padding.left = left;
        padding.top = top;
        padding.right = right;
        padding.bottom = bottom;

        if (!isMeasuring()) {
            for (BaseControl childControl : children) {
                childControl.markMeasureDirty(true);
            }
        }
    }

    public void setPadding(double padding) {
        setPadding(padding, padding, padding, padding);
    }

    public void setPadding(Padding padding) {
        setPadding(padding.left, padding.top, padding.right, padding.bottom);
    }

    public void setPaddingLeft(double padding) {
        setPadding(padding, getPadding().top, getPadding().right, getPadding().bottom);
    }

    public void setPaddingTop(double padding) {
        setPadding(getPadding().left, padding, getPadding().right, getPadding().bottom);
    }

    public void setPaddingRight(double padding) {
        setPadding(getPadding().left, getPadding().top, padding, getPadding().bottom);
    }

    public void setPaddingBottom(double padding) {
        setPadding(getPadding().left, getPadding().top, getPadding().right, padding);
    }

    public Position getPosition() {
        return new Position(position);
    }

    public void setPosition(double x, double y) {
        if (x == position.x && y == position.y) {
            return;
        }

        double oldX = position.x;
        double oldY = position.y;

        position.x = x;
        position.y = y;

        if (getParent() != null && !isDragging()) {
            getParent().onChildPositionSizeChanged(this);
        }

        eventBus.post(this, new PositionChangedEvent(oldX, oldY));
    }

    public void setPosition(Position position) {
        setPosition(position.x, position.y);
    }

    public double getX() {
        return position.x;
    }

    public void setX(double x) {
        setPosition(x, getY());
    }

    public double getY() {
        return position.y;
    }

    public void setY(double y) {
        setPosition(getX(), y);
    }

    /**
     * Converts a local x coordinate to a pixel x coordinate inside the control. I.e. if a child control had
     * an x coordinate of 10 what would the pixel x coordinate be? So this takes into account the inner scaling
     * of this control.
     *
     * @param x the local x coordinate to convert.
     * @return the pixel x coordinate.
     */
    public double toPixelX(double x) {
        double pixelX = x * getChildPixelScaleX();
        pixelX += getPixelX() + getPixelPadding().left;
        pixelX -= getScrollX() * getChildPixelScaleX();

        return pixelX;
    }

    public double getPixelX() {
        double pixelX = getX() * getPixelScaleX();

        if (getParent() != null) {
            pixelX = getParent().toPixelX(getX());
        }

        if (shouldSnapToScreenCoords()) {
            pixelX = Math.round(pixelX / getGuiScale()) * getGuiScale();
        } else if (shouldSnapToPixelCoords()) {
            pixelX = Math.round(pixelX);
        }

        return pixelX;
    }

    public void setPixelX(double pixelX) {
        if (getParent() != null) {
            pixelX -= getParent().getPixelX() + getParent().getPixelPadding().left;
            pixelX += getParent().getScrollX() * getPixelScaleX();
        }

        setX(pixelX / getPixelScaleX());
    }

    /**
     * Converts a local y coordinate to a pixel y coordinate inside the control. I.e. if a child control had
     * an y coordinate of 10 what would the pixel y coordinate be? So this takes into account the inner scaling
     * of this control.
     *
     * @param y the local y coordinate to convert.
     * @return the pixel y coordinate.
     */
    public double toPixelY(double y) {
        double pixelY = y * getChildPixelScaleY();
        pixelY += getPixelY() + getPixelPadding().top;
        pixelY -= getScrollY() * getChildPixelScaleY();

        return pixelY;
    }

    public double getPixelY() {
        double pixelY = getY() * getPixelScaleY();

        if (getParent() != null) {
            pixelY = getParent().toPixelY(getY());
        }

        if (shouldSnapToScreenCoords()) {
            pixelY = Math.round(pixelY / getGuiScale()) * getGuiScale();
        } else if (shouldSnapToPixelCoords()) {
            pixelY = Math.round(pixelY);
        }

        return pixelY;
    }

    public void setPixelY(double pixelY) {
        if (getParent() != null) {
            pixelY -= getParent().getPixelY() + getParent().getPixelPadding().top;
            pixelY += getParent().getScrollY() * getPixelScaleY();
        }

        setY(pixelY / getPixelScaleY());
    }

    public double getPixelMidX() {
        return getPixelX() + getPixelWidth() / 2.0;
    }

    public double getPixelMidY() {
        return getPixelY() + getPixelHeight() / 2.0;
    }

    public double getPixelLeft() {
        return getPixelX();
    }

    public double getPixelTop() {
        return getPixelY();
    }

    public double getPixelRight() {
        return getPixelX() + getPixelWidth();
    }

    public double getPixelBottom() {
        return getPixelY() + getPixelHeight();
    }

    public double getPreDragX() {
        return preDragPosition.x;
    }

    public double getPreDragY() {
        return preDragPosition.y;
    }

    public void setPreDragPosition(double x, double y) {
        preDragPosition.x = x;
        preDragPosition.y = y;
    }

    public double getPreDragPixelX() {
        return toPixelX(getPreDragPixelX());
    }

    public double getPreDragPixelY() {
        return toPixelY(getPreDragPixelY());
    }

    public boolean shouldSnapToScreenCoords() {
        return shouldSnapToScreenCoords;
    }

    public void setShouldSnapToScreenCoords(boolean shouldSnapToScreenCoords) {
        this.shouldSnapToScreenCoords = shouldSnapToScreenCoords;
    }

    public boolean shouldSnapToPixelCoords() {
        return shouldSnapToPixelCoords;
    }

    public void setShouldSnapToPixelCoords(boolean shouldSnapToPixelCoords) {
        this.shouldSnapToPixelCoords = shouldSnapToPixelCoords;
    }

    public Double getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public void setHorizontalAlignment(Double horizontalAlignment) {
        if (this.horizontalAlignment == horizontalAlignment) {
            return;
        }

        if (horizontalAlignment != null) {
            horizontalAlignment = DoubleUtil.clamp(horizontalAlignment, 0.0, 1.0);
        }

        this.horizontalAlignment = horizontalAlignment;

        if (getParent() != null) {
            getParent().onChildAlignmentChanged(this);
        }
    }

    public Double getVerticalAlignment() {
        return verticalAlignment;
    }

    public void setVerticalAlignment(Double verticalAlignment) {
        if (this.verticalAlignment == verticalAlignment) {
            return;
        }

        if (verticalAlignment != null) {
            verticalAlignment = DoubleUtil.clamp(verticalAlignment, 0.0, 1.0);
        }

        this.verticalAlignment = verticalAlignment;

        if (getParent() != null) {
            getParent().onChildAlignmentChanged(this);
        }
    }

    public Double getHorizontalContentAlignment() {
        return horizontalContentAlignment;
    }

    public boolean isHorizontalContentAlignmentSet() {
        return horizontalContentAlignment != null;
    }

    public void setHorizontalContentAlignment(Double horizontalContentAlignment) {
        if (this.horizontalContentAlignment == horizontalContentAlignment) {
            return;
        }

        if (horizontalContentAlignment != null) {
            horizontalContentAlignment = DoubleUtil.clamp(horizontalContentAlignment, 0.0, 1.0);
        }

        this.horizontalContentAlignment = horizontalContentAlignment;

        markArrangeDirty(true);
    }

    public Double getVerticalContentAlignment() {
        return verticalContentAlignment;
    }

    public boolean isVerticalContentAlignmentSet() {
        return verticalContentAlignment != null;
    }

    public void setVerticalContentAlignment(Double verticalContentAlignment) {
        if (this.verticalContentAlignment == verticalContentAlignment) {
            return;
        }

        if (verticalContentAlignment != null) {
            verticalContentAlignment = DoubleUtil.clamp(verticalContentAlignment, 0.0, 1.0);
        }

        this.verticalContentAlignment = verticalContentAlignment;

        markArrangeDirty(true);
    }

    public double getHorizontalAlignmentFor(BaseControl control) {
        if (isChild(control)) {
            if (getHorizontalContentAlignment() != null) {
                return getHorizontalContentAlignment();
            } else if (control.getHorizontalAlignment() != null) {
                return control.getHorizontalAlignment();
            }
        }

        return 0.0;
    }

    public double getVerticalAlignmentFor(BaseControl control) {
        if (isChild(control)) {
            if (getVerticalContentAlignment() != null) {
                return getVerticalContentAlignment();
            } else if (control.getVerticalAlignment() != null) {
                return control.getVerticalAlignment();
            }
        }

        return 0.0;
    }

    public Offset getScroll() {
        return new Offset(scroll);
    }

    public void setScroll(double x, double y) {
        x = DoubleUtil.clamp(x, minScroll.x, maxScroll.x);
        y = DoubleUtil.clamp(y, minScroll.y, maxScroll.y);

        if (x == scroll.x && y == scroll.y) {
            return;
        }

        scroll.x = x;
        scroll.y = y;
    }

    public void setScroll(Offset offset) {
        setScroll(offset.x, offset.y);
    }

    public double getParentScrollX() {
        if (getParent() != null) {
            getParent().getScrollX();
        }

        return 0.0;
    }

    public double getScrollX() {
        return scroll.x;
    }

    public void setScrollX(double x) {
        setScroll(x, getScrollY());
    }

    public double getScrollPercentX() {
        return (getScrollX() - getMinScrollX()) / (getMaxScrollX() - getMinScrollX());
    }

    public void setScrollPercentX(double percent) {
        setScrollX(getMinScrollX() + (getMaxScrollX() - getMinScrollX()) * percent);
    }

    public double getParentScrollY() {
        if (getParent() != null) {
            getParent().getScrollY();
        }

        return 0.0;
    }

    public double getScrollY() {
        return scroll.y;
    }

    public void setScrollY(double y) {
        setScroll(getScrollX(), y);
    }

    public double getScrollPercentY() {
        return (getScrollY() - getMinScrollY()) / (getMaxScrollY() - getMinScrollY());
    }

    public void setScrollPercentY(double percent) {
        setScrollY(getMinScrollY() + (getMaxScrollY() - getMinScrollY()) * percent);
    }

    /**
     * Tries to scroll in the x direction.
     *
     * @param x the amount to scroll.
     * @return the amount scrolled.
     */
    public double scrollX(double x) {
        double previousScroll = getScrollX();

        setScrollX(getScrollX() + x);

        return getScrollX() - previousScroll;
    }

    /**
     * Tries to scroll in the y direction.
     *
     * @param y the amount to scroll.
     * @return the amount scrolled.
     */
    public double scrollY(double y) {
        double previousScroll = getScrollY();

        setScrollY(getScrollY() + y);

        return getScrollY() - previousScroll;
    }

    public double getScrollXPercent() {
        return (getScrollX() - getMinScrollX()) / (getMaxScrollX() - getMinScrollX());
    }

    public void setScrollXPercent(double percent) {
        setScrollX(getMinScrollX() + (getMaxScrollX() - getMinScrollX()) * percent);
    }

    public void scrollXPercent(double percent) {
        setScrollX(getScrollX() + (getMaxScrollX() - getMinScrollX()) * percent);
    }

    public double getScrollYPercent() {
        return (getScrollY() - getMinScrollY()) / (getMaxScrollY() - getMinScrollY());
    }

    public void setScrollYPercent(double percent) {
        setScrollY(getMinScrollY() + (getMaxScrollY() - getMinScrollY()) * percent);
    }

    public void scrollYPercent(double percent) {
        setScrollY(getScrollY() + (getMaxScrollY() - getMinScrollY()) * percent);
    }

    public Offset getMinScroll() {
        return new Offset(minScroll);
    }

    public void setMinScroll(double x, double y) {
        if (x == minScroll.x && y == minScroll.y) {
            return;
        }

        minScroll.x = x;
        minScroll.y = y;

        setScroll(getScroll());
    }

    public void setMinScroll(Offset offset) {
        setMinScroll(offset.x, offset.y);
    }

    public double getMinScrollX() {
        return minScroll.x;
    }

    public void setMinScrollX(double x) {
        setMinScroll(x, getMinScrollY());
    }

    public double getMinScrollY() {
        return minScroll.y;
    }

    public void setMinScrollY(double y) {
        setMinScroll(getMinScrollX(), y);
    }

    public Offset getMaxScroll() {
        return new Offset(maxScroll);
    }

    public void setMaxScroll(double x, double y) {
        if (x == maxScroll.x && y == maxScroll.y) {
            return;
        }

        maxScroll.x = x;
        maxScroll.y = y;

        setScroll(getScroll());
    }

    public void setMaxScroll(Offset offset) {
        setMaxScroll(offset.x, offset.y);
    }

    public double getMaxScrollX() {
        return maxScroll.x;
    }

    public void setMaxScrollX(double x) {
        setMaxScroll(x, getMaxScrollY());
    }

    public double getMaxScrollY() {
        return maxScroll.y;
    }

    public void setMaxScrollY(double y) {
        setMaxScroll(getMaxScrollX(), y);
    }

    public boolean canScroll() {
        return canScrollHorizontally || canScrollVertically;
    }

    public boolean canScrollHorizontally() {
        return canScrollHorizontally;
    }

    public void setCanScrollHorizontally(boolean canScrollHorizontally) {
        this.canScrollHorizontally = canScrollHorizontally;

        if (!canScrollHorizontally()) {
            setScrollX(0.0);
            setMinScrollX(0.0);
            setMaxScrollX(0.0);
        }
    }

    public boolean canScrollVertically() {
        return canScrollVertically;
    }

    public void setCanScrollVertically(boolean canScrollVertically) {
        this.canScrollVertically = canScrollVertically;

        if (!canScrollVertically()) {
            setScrollY(0.0);
            setMinScrollY(0.0);
            setMaxScrollY(0.0);
        }
    }

    public BaseControl getHoveredControl() {
        return getScreen() == null ? null : getScreen().getHoveredControl();
    }

    public boolean isHovered() {
        return getHoveredControl() == this;
    }

    public void setIsHovered(boolean isHovered) {
        if (getScreen() == null) {
            return;
        }

        if (isHovered && isHoverable()) {
            getScreen().setHoveredControl(this);
        } else if (isHovered()) {
            getScreen().setHoveredControl(null);
        }
    }

    public BaseControl getPressedControl() {
        return getScreen() == null ? null : getScreen().getPressedControl();
    }

    public boolean isPressed() {
        return getPressedControl() == this;
    }

    public void setPressed(boolean isPressed) {
        if (getScreen() == null) {
            return;
        }

        if (isPressed && isPressable()) {
            getScreen().setPressedControl(this);
        } else if (isPressed()) {
            getScreen().setPressedControl(null);
        }
    }

    public boolean isAncestorPressed() {
        return isAncestor(getPressedControl());
    }

    public boolean isPressedOrAncestor() {
        return isPressed() || isAncestorPressed();
    }

    public boolean isDescendantPressed() {
        return isDescendant(getPressedControl());
    }

    public boolean isPressedOrDescendant() {
        return isPressed() || isDescendantPressed();
    }

    public BaseControl getFocusedControl() {
        return getScreen() == null ? null : getScreen().getFocusedControl();
    }

    public boolean isFocused() {
        return getFocusedControl() == this;
    }

    public void setFocused(boolean isFocused) {
        if (getScreen() == null) {
            return;
        }

        if (isFocused && isInteractive() && isFocusable()) {
            getScreen().setFocusedControl(this);
        } else if (isFocused()) {
            getScreen().setFocusedControl(null);
        }
    }

    public BaseControl getDraggedControl() {
        return getScreen() == null ? null : getScreen().getDraggedControl();
    }

    public boolean isDragging() {
        return getDraggedControl() == this;
    }

    public boolean isDraggingOrAncestor() {
        if (isDragging()) {
            return true;
        }

        if (getParent() == null) {
            return false;
        }

        return getParent().isDraggingOrAncestor();
    }

    public void setIsDragging(boolean isDragging) {
        if (getScreen() == null) {
            return;
        }

        if (isDragging) {
            getScreen().setDraggedControl(this);
        } else if (isDragging()) {
            getScreen().setDraggedControl(null);
        }
    }

    public boolean isDraggable() {
        return isDraggableX || isDraggableY;
    }

    public boolean isDraggableX() {
        return isDraggableX;
    }

    public void setDraggableX(boolean draggableX) {
        isDraggableX = draggableX;
    }

    public boolean isDraggableY() {
        return isDraggableY;
    }

    public void setDraggableY(boolean draggableY) {
        isDraggableY = draggableY;
    }

    public double getDragThreshold() {
        return dragThreshold;
    }

    public void setDragThreshold(double dragThreshold) {
        this.dragThreshold = dragThreshold;
    }

    public boolean shouldBlockDrag() {
        return shouldBlockDrag;
    }

    public void setShouldBlockDrag(boolean shouldBlockDrag) {
        this.shouldBlockDrag = shouldBlockDrag;
    }

    public BaseControl getScrollFromDragControl() {
        if (scrollFromDragControl != null) {
            return scrollFromDragControl;
        } else if (getParent() != null) {
            BaseControl parent = getParent();

            while (parent != null && parent.scrollFromDragControl == null) {
                parent = parent.getParent();
            }

            if (parent != null) {
                BaseControl ancestorScrollFromDragControl = parent.scrollFromDragControl;

                if (ancestorScrollFromDragControl != null) {
                    return ancestorScrollFromDragControl;
                }
            }
        }

        return getParent();
    }

    public void setScrollFromDragControl(BaseControl scrollFromDragControl) {
        this.scrollFromDragControl = scrollFromDragControl;
    }

    public double getDragZ() {
        return dragZ != null ? dragZ + getRenderZ() : getRenderZ();
    }

    public void setDragZ(Double dragZ) {
        this.dragZ = dragZ;
    }

    /**
     * @return whether the drag event should be propagated to the parent control if not handled by this control.
     */
    public boolean shouldPropagateDrag() {
        return shouldPropagateDrag;
    }

    /**
     * Sets whether the drag event should be propagated to the parent control if not handled by this control.
     */
    public void setShouldPropagateDrag(boolean shouldPropagateDrag) {
        this.shouldPropagateDrag = shouldPropagateDrag;
    }

    public boolean isAncestorCollapsed() {
        if (getParent() == null) {
            return false;
        }

        if (getParent().getVisibility() == Visibility.COLLAPSED) {
            return true;
        }

        return getParent().isAncestorCollapsed();
    }

    public boolean isCollapsedOrAncestor() {
        if (getVisibility() == Visibility.COLLAPSED) {
            return true;
        }

        if (getParent() == null) {
            return false;
        }

        return getParent().isCollapsedOrAncestor();
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        if (this.visibility == visibility) {
            return;
        }

        this.visibility = visibility;

        markMeasureDirty(true);

        if (getParent() != null) {
            getParent().onChildVisiblityChanged(this);
        }
    }

    public boolean isHoverable() {
        return isHoverable;
    }

    public void setHoverable(boolean hoverable) {
        isHoverable = hoverable;

        if (!isHoverable() && isHovered()) {
            setIsHovered(false);
        }
    }

    public boolean isPressable() {
        return isPressable;
    }

    public void setPressable(boolean pressable) {
        isPressable = pressable;

        if (!isPressable() && isPressed()) {
            setPressed(false);
        }
    }

    public boolean isFocusable() {
        return isFocusable;
    }

    public void setFocusable(boolean focusable) {
        isFocusable = focusable;

        if (!isFocusable() && isFocused()) {
            setFocused(false);
        }
    }

    public boolean isInteractive() {
        return isInteractive && !isNoninteractiveFromAncestor();
    }

    public void setInteractive(boolean isInteractive) {
        this.isInteractive = isInteractive;
    }

    public boolean isNoninteractiveFromAncestor() {
        if (getParent() == null) {
            return false;
        }

        if (!getParent().areChildrenInteractive()) {
            return true;
        }

        return getParent().isNoninteractiveFromAncestor();
    }

    public boolean areChildrenInteractive() {
        return areChildrenInteractive;
    }

    public void setChildrenInteractive(boolean areChildrenInteractive) {
        this.areChildrenInteractive = areChildrenInteractive;
    }

    public boolean isReorderable() {
        return isReorderable;
    }

    public void setReorderable(boolean reorderable) {
        isReorderable = reorderable;
    }

    public boolean shouldClipContentsToBounds() {
        if (shouldClipContentsToBounds != null) {
            return shouldClipContentsToBounds;
        }

        return getParent() != null ? getParent().shouldClipContentsToBounds() : true;
    }

    public void setClipContentsToBounds(Boolean shouldClipContentsToBounds) {
        this.shouldClipContentsToBounds = shouldClipContentsToBounds;
    }

    public double getRenderZ() {
        return renderZ;
    }

    public void setRenderZ(double renderZ) {
        this.renderZ = renderZ;
    }

    public Colour getForegroundColour() {
        return foregroundColour;
    }

    public int getForegroundColourInt() {
        return foregroundColour.argb();
    }

    public void setForegroundColour(Colour foregroundColour) {
        this.foregroundColour = foregroundColour;
    }

    public void setForegroundColour(int foregroundColour) {
        setForegroundColour(new Colour(foregroundColour));
    }

    public Colour getBackgroundColour() {
        return backgroundColour;
    }

    public int getBackgroundColourInt() {
        return backgroundColour.argb();
    }

    public void setBackgroundColour(Colour backgroundColour) {
        this.backgroundColour = backgroundColour;
    }

    public void setBackgroundColour(int backgroundColour) {
        setBackgroundColour(new Colour(backgroundColour));
    }

    /**
     * @return converts a pixel x coordinate into a local x coordinate.
     */
    public double toLocalX(double pixelX) {
        return (pixelX - getPixelPadding().left - getPixelX()) / getPixelScaleX() / getInnerScale().x;
    }

    /**
     * @return converts a pixel y coordinate into a local y coordinate.
     */
    public double toLocalY(double pixelY) {
        return (pixelY - getPixelPadding().top - getPixelY()) / getPixelScaleY() / getInnerScale().y;
    }

    public double getGuiScale() {
        return GuiUtil.get().getGuiScale();
    }

    public double getScaleX() {
        return getParent() != null ? getParent().getScaleX() * getParent().getInnerScale().x : 1.0;
    }

    /**
     * Returns how many pixels a single unit takes up for this control. E.g. if the control had a scale of 4.0
     * and the gui scale was 2.0 then the pixel scale would be 4.0 * 2.0 = 8.0, so the control would take up 8
     * pixels for every unit.
     *
     * @return the pixel scale.
     */
    public double getPixelScaleX() {
        return getGuiScale() * getScaleX();
    }

    public double getChildPixelScaleX() {
        return getPixelScaleX() * getInnerScale().x;
    }

    public double getScaleY() {
        return getParent() != null ? getParent().getScaleY() * getParent().getInnerScale().y : 1.0;
    }

    /**
     * Returns how many pixels a single unit takes up for this control. E.g. if the control had a scale of 4.0
     * and the gui scale was 2.0 then the pixel scale would be 4.0 * 2.0 = 8.0, so the control would take up 8
     * pixels for every unit.
     *
     * @return the pixel scale.
     */
    public double getPixelScaleY() {
        return getGuiScale() * getScaleY();
    }

    public double getChildPixelScaleY() {
        return getPixelScaleY() * getInnerScale().y;
    }

    public int randomColour() {
        int a = 255;
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);
        int colour = (a << 24) | (r << 16) | (g << 8) | b;

        return colour;
    }

    public int randomInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public int randomInt(int max) {
        return random.nextInt(max);
    }

    public String getDebugName() {
        return debugName + "   " + getClass().getSimpleName();
    }

    public void setDebugName(String debugName) {
        this.debugName = debugName;
    }

} // Class BaseControl