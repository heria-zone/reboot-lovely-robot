package net.msymbios.rlovelyr.client.gui.control;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.msymbios.rlovelyr.client.gui.control.event.events.*;
import net.msymbios.rlovelyr.client.gui.control.event.events.input.*;
import net.msymbios.rlovelyr.client.gui.properties.enums.*;
import net.msymbios.rlovelyr.client.gui.texture.*;
import net.msymbios.rlovelyr.client.gui.util.*;
import net.msymbios.rlovelyr.util.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A base class for all controls.
 */
public class Control extends BaseControl {

    // -- Inherited Methods --

    @Override
    public void doMeasure(double availableWidth, double availableHeight) {
        setMeasuring(true);
        measureSelf(availableWidth, availableHeight);
        setMeasuring(false);
        markMeasureDirty(false);
        measureChildren();
    } // doMeasure ()

    @Override
    protected void measureSelf(double availableWidth, double availableHeight) {
        double width = getWidth();
        double height = getHeight();

        if (getWidthPercentage() != null && DoubleUtil.isPositiveAndFinite(availableWidth)) {
            width = availableWidth * getWidthPercentage();
        } else if (shouldFitWidthToContent()) {
            double maxX = Double.NEGATIVE_INFINITY;

            for (BaseControl childControl : getChildren()) {
                if (childControl.getVisibility() == Visibility.COLLAPSED) {
                    continue;
                }

                if (childControl.getWidthPercentage() != null) {
                    continue;
                }

                double childX = (childControl.getX() + childControl.getWidth() + childControl.getMargin().right) * getInnerScale().x;

                if (childX > maxX) {
                    maxX = childX;
                }
            }

            width = maxX != Double.NEGATIVE_INFINITY ? maxX + getPaddingWidth() : 0.0;
        }

        if (getHeightPercentage() != null && DoubleUtil.isPositiveAndFinite(availableHeight)) {
            height = availableHeight * getHeightPercentage();
        } else if (shouldFitHeightToContent()) {
            double maxY = Double.NEGATIVE_INFINITY;

            for (BaseControl childControl : getChildren()) {
                if (childControl.getVisibility() == Visibility.COLLAPSED) {
                    continue;
                }

                if (childControl.getHeightPercentage() != null) {
                    continue;
                }

                double childY = (childControl.getY() + childControl.getHeight() + childControl.getMargin().bottom) * getInnerScale().y;

                if (childY > maxY) {
                    maxY = childY;
                }
            }

            height = maxY != Double.NEGATIVE_INFINITY ? maxY + getPaddingHeight() : 0.0;
        }

        if (availableWidth >= 0.0) {
            setDesiredWidth(width);
        }

        if (availableHeight >= 0.0) {
            setDesiredHeight(height);
        }
    } // measureSelf ()

    @Override
    public void measureChildren() {
        for (BaseControl child : getChildrenCopy()) {
            if (child.getVisibility() == Visibility.COLLAPSED) {
                continue;
            }

            double availableWidth = ((getDesiredWidth() - getPaddingWidth()) / getInnerScale().x) - child.getMarginWidth();
            double availableHeight = ((getDesiredHeight() - getPaddingHeight()) / getInnerScale().y) - child.getMarginHeight();

            if (shouldFitWidthToContent()) {
                availableWidth = getMaxWidth();
            }

            if (shouldFitHeightToContent()) {
                availableHeight = getMaxHeight();
            }

            child.doMeasure(availableWidth, availableHeight);
        }
    } // measureChildren ()

    @Override
    public void doArrange() {
        // Don't arrange if any children need measuring.
        if (getChildrenCopy().stream().anyMatch(control -> control.isMeasureDirty() && !control.isCollapsedOrAncestor())) return;

        setArranging(true);
        arrange();
        setArranging(false);
        markArrangeDirty(false);

        for (BaseControl child : getChildrenCopy()) {
            if (child.isCollapsedOrAncestor()) continue;
            child.doArrange();
        }

        calculateScroll();
    } // doArrange ()

    @Override
    protected void arrange() {
        for (BaseControl control : getChildrenCopy()) {
            if (control.getVisibility() == Visibility.COLLAPSED) continue;

            control.setWidth(control.getDesiredWidth());
            control.setHeight(control.getDesiredHeight());

            double x = (((getWidthWithoutPadding() / getInnerScale().x) - control.getWidthWithMargin()) * getHorizontalAlignmentFor(control)) + control.getMargin().left;
            double y = (((getHeightWithoutPadding() / getInnerScale().y) - control.getHeightWithMargin()) * getVerticalAlignmentFor(control)) + control.getMargin().top;

            control.setX(x);
            control.setY(y);
        }
    } // arrange ()

    @Override
    public void calculateScroll() {
        if (canScrollHorizontally()) {
            double minX = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY;

            for (BaseControl child : getChildren()) {
                double childMinX = child.getX() - child.getMargin().left;
                double childMaxX = child.getX() + child.getWidth() + child.getMargin().right;

                if (childMinX < minX) {
                    minX = childMinX;
                }

                if (childMaxX > maxX) {
                    maxX = childMaxX;
                }
            }

            if (minX != Double.POSITIVE_INFINITY && maxX != Double.NEGATIVE_INFINITY) {
                double scaledWidth = getWidthWithoutPadding() / getInnerScale().x;
                double scrollableWidth = maxX - minX - scaledWidth;

                if (scrollableWidth > 0.0) {
                    setMinScrollX(minX);
                    setMaxScrollX(maxX - scaledWidth);
                } else {
                    setMinScrollX(0.0);
                    setMaxScrollX(0.0);
                }
            }
        }

        if (canScrollVertically()) {
            double minY = Double.POSITIVE_INFINITY;
            double maxY = Double.NEGATIVE_INFINITY;

            for (BaseControl child : getChildren()) {
                double childMinY = child.getY() - child.getMargin().top;
                double childMaxY = child.getY() + child.getHeight() + child.getMargin().bottom;

                if (childMinY < minY) {
                    minY = childMinY;
                }

                if (childMaxY > maxY) {
                    maxY = childMaxY;
                }
            }

            if (minY != Double.POSITIVE_INFINITY && maxY != Double.NEGATIVE_INFINITY) {
                double scaledHeight = getHeightWithoutPadding() / getInnerScale().y;
                double scrollableHeight = maxY - minY - scaledHeight;

                if (scrollableHeight > 0.0) {
                    setMinScrollY(minY);
                    setMaxScrollY(maxY - scaledHeight);
                } else {
                    setMinScrollY(0.0);
                    setMaxScrollY(0.0);
                }
            } else {
                setMinScrollX(0.0);
                setMaxScrollX(0.0);
            }
        }
    } // calculateScroll ()

    @Override
    public void forwardClose(boolean isRealClose) {
        for (BaseControl child : getChildrenCopy())
            child.forwardClose(isRealClose);
        onClose(isRealClose);
    } // forwardClose ()

    @Override
    public void onClose(boolean isRealClose) {
        if (isRealClose) clearEventBuses(false);
    } // onClose ()

    @Override
    public void forwardTick() {
        for (BaseControl child : getChildrenCopy())
            child.forwardTick();
        onTick();
    } // forwardTick ()

    @Override
    public void onTick() {}

    @Override
    public void forwardRender(ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks) {
        if (getVisibility() != Visibility.VISIBLE) return;

        Colour colour = getForegroundColour();
        RenderSystem.setShaderColor(colour.getR(), colour.getG(), colour.getB(), colour.getA());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        //poseStack.pushPose();
        //poseStack.translate(0.0f, 0.0f, isDraggingOrAncestor() ? getDraggedControl().getDragZ() : getRenderZ());

        applyScissor(scissorStack);
        onRenderUpdate(scissorStack, mouseX, mouseY, partialTicks);
        onRender(scissorStack, mouseX, mouseY, partialTicks);

        for (BaseControl child : getChildrenCopy()) child.forwardRender(scissorStack, mouseX, mouseY, partialTicks);

        //poseStack.popPose();

        undoScissor(scissorStack);
    } // forwardRender ()

    @Override
    protected void onRenderUpdate(ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks) {}

    @Override
    protected void onRender(ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks) {
        renderRectangle(getPixelX() + getPixelPadding().left, getPixelY() + getPixelPadding().top, (int) getPixelWidthWithoutPadding(), (int) getPixelHeightWithoutPadding(), getBackgroundColourInt());
    } // onRender ()

    @Override
    public void onRenderTooltip(double mouseX, double mouseY, float partialTicks) { }

    protected void renderRectangleAsBackground(int colour) {
        renderRectangle((int) getPixelX(), (int) getPixelY(), (int) getPixelWidth(), (int) getPixelHeight(), colour);
    } // renderRectangleAsBackground ()

    protected void renderRectangleAsBackground(int colour, double dx, double dy, double width, double height) {
        renderRectangle((int) getPixelX() + dx * getPixelScaleX(), (int) getPixelY() + dy * getPixelScaleY(), (int) (width * getPixelScaleX()), (int) (height * getPixelScaleY()), colour);
    } // renderRectangleAsBackground ()

    protected void renderBackgroundColour() {
        renderRectangleAsBackground(getBackgroundColourInt());
    } // renderBackgroundColour ()

    protected void renderTextureAsBackground(Texture texture) {
        renderTexture(texture, getPixelX(), getPixelY(), getPixelScaleX(), getPixelScaleY());
    } // renderTextureAsBackground ()

    protected void renderTextureAsBackground(Texture texture, double dx, double dy) {
        renderTexture(texture, getPixelX() + dx * getPixelScaleX(), getPixelY() + dy * getPixelScaleX(), getPixelScaleX(), getPixelScaleY());
    } // renderTextureAsBackground ()

    /**
     * Renders a tooltip at the mouse position.
     *
     * @param mouseX    the mouse x position.
     * @param mouseY    the mouse y position.
     * @param tooltip   the tooltip to render.
     */
    public void renderTooltip(double mouseX, double mouseY, Text tooltip) {
        List<Text> tooltip2 = new ArrayList<>();
        tooltip2.add(tooltip);
        renderTooltip(mouseX, mouseY, getPixelScaleX(), getPixelScaleY(), tooltip2);
    } // renderTooltip ()

    /**
     * Renders a tooltip at the mouse position.
     *
     * @param tooltip   the tooltip to render.
     * @param mouseX    the mouse x position.
     * @param mouseY    the mouse y position.
     */
    public void renderTooltip(List<? extends Text> tooltip, double mouseX, double mouseY) {
        MinecraftClient.getInstance().currentScreen.render(tooltip.stream().map(Text::getLiteralString).collect(Collectors.toList()), (int) (mouseX / getPixelScaleX()), (int) (mouseY / getPixelScaleY()));
    } // renderTooltip ()

    /**
     * Renders a tooltip at the mouse position.
     *
     * @param mouseX    the mouse x position.
     * @param mouseY    the mouse y position.
     * @param tooltip   the tooltip to render.
     */
    public void renderTooltip(double mouseX, double mouseY, List<Text> tooltip) {
        MinecraftClient.getInstance().currentScreen.renderWithTooltip(tooltip, (int) (mouseX / getPixelScaleX()), (int) (mouseY / getPixelScaleY()));
    } // renderTooltip ()

    /**
     * Applies any scissoring to the control before rendering.
     */
    protected void applyScissor(ScissorStack scissorStack) {
        if (shouldClipContentsToBounds()) {
            scissorStack.push(new ScissorBounds((int) Math.round(getPixelX()), (int) Math.round(getPixelY()), (int) Math.round(getPixelWidth()), (int) Math.round(getPixelHeight())));
            scissorStack.enable();
        } else {
            scissorStack.disable();
        }
    } // applyScissor ()

    /**
     * Undoes any scissoring to the control after rendering.
     */
    protected void undoScissor(ScissorStack scissorStack) {
        if (shouldClipContentsToBounds()) {
            scissorStack.pop();
            scissorStack.disable();
        }
    } // undoScissor ()

    @Override
    public void forwardHover(TryHoverEvent e) {
        if (!isInteractive()) return;
        if (getVisibility() == Visibility.COLLAPSED) return;
        if (isDragging()) return;
        if (!contains(e.mouseX, e.mouseY)) return;

        if (areChildrenInteractive()) {
            for (BaseControl child : getReverseChildrenCopy()) {
                if (!e.isHandled()) {
                    child.forwardHover(e);
                }
            }
        }

        if (!e.isHandled() && getParent() != null) {
            setIsHovered(true);
            e.setIsHandled(isHovered());
        }
    } // forwardHover ()

    @Override
    public void onHoverEnter() { }

    @Override
    public void onHoverExit() { }

    @Override
    public void onPressStart() { }

    @Override
    public void onPressEnd() { }

    @Override
    public void onFocused() { }

    @Override
    public void onUnfocused() { }

    @Override
    public void forwardTryDrag(TryDragEvent e) {
        if (!isInteractive()) {
            return;
        }

        if (getVisibility() == Visibility.COLLAPSED) {
            return;
        }

        if (!contains(e.mouseX, e.mouseY) && !isPressedOrDescendant()) {
            return;
        }

        if (areChildrenInteractive()) {
            for (BaseControl child : getReverseChildrenCopy()) {
                if (!e.isHandled()) {
                    child.forwardTryDrag(e);
                }
            }
        }

        if (!e.isHandled() && getParent() != null) {
            if (isPressedOrDescendant()) {
                if (isDraggable()) {
                    double pixelDragDifX = e.mouseX - getScreen().getPressedStartPixelX();
                    double pixelDragDifY = e.mouseY - getScreen().getPressedStartPixelY();
                    double localDragDifX = pixelDragDifX / getPixelScaleX();
                    double localDragDifY = pixelDragDifY / getPixelScaleY();
                    double absLocalDragDifX = Math.abs(localDragDifX);
                    double absLocalDragDifY = Math.abs(localDragDifY);

                    boolean isDraggedX = absLocalDragDifX >= getDragThreshold();
                    boolean isDraggedY = absLocalDragDifY >= getDragThreshold();

                    if (isDraggedX || isDraggedY) {
                        setIsDragging(true);
                        e.setIsHandled(true);
                    }
                }
            }
        }

        if (!e.isHandled() && isPressedOrDescendant()) {
            e.setIsHandled(!shouldPropagateDrag());
        }
    } // forwardTryDrag ()

    @Override
    public void onDragStart(double mouseX, double mouseY) { }

    @Override
    public void onDrag(double mouseX, double mouseY, float partialTicks) {
        if (isDraggableX()) {
            setPixelX(mouseX - getPixelWidth() / 2.0);
        }

        if (isDraggableY()) {
            setPixelY(mouseY - getPixelHeight() / 2.0);
        }

        BaseControl scrollFromDragControl = getScrollFromDragControl();

        if (scrollFromDragControl == null) {
            return;
        }

        List<Side> atParentBounds = getBoundsAt(scrollFromDragControl);
        double scrollAmount = 10.0 * partialTicks;

        if (isDraggableX()) {
            if (atParentBounds.contains(Side.LEFT)) {
                if (scrollFromDragControl.canScrollHorizontally()) {
                    scrollFromDragControl.scrollX(scrollAmount * -1);
                }

                if (scrollFromDragControl.shouldBlockDrag()) {
                    setX(getParent().toLocalX(scrollFromDragControl.getPixelX()) / getParent().getInnerScale().x + (scrollFromDragControl == getParent() ? getParent().getScrollX() : 0.0));
                }
            } else if (atParentBounds.contains(Side.RIGHT)) {
                if (scrollFromDragControl.canScrollHorizontally()) {
                    scrollFromDragControl.scrollX(scrollAmount);
                }

                if (scrollFromDragControl.shouldBlockDrag()) {
                    setX(getParent().toLocalX(scrollFromDragControl.getPixelX() + scrollFromDragControl.getPixelWidth()) / getParent().getInnerScale().x - getWidth() + (scrollFromDragControl == getParent() ? getParent().getScrollX() : 0.0));
                }
            }
        }

        if (isDraggableY()) {
            if (atParentBounds.contains(Side.TOP)) {
                if (scrollFromDragControl.canScrollVertically()) {
                    scrollFromDragControl.scrollY(scrollAmount * -1);
                }

                if (scrollFromDragControl.shouldBlockDrag()) {
                    setY(getParent().toLocalY(scrollFromDragControl.getPixelY()) / getParent().getInnerScale().y + (scrollFromDragControl == getParent() ? getParent().getScrollY() : 0.0));
                }
            } else if (atParentBounds.contains(Side.BOTTOM)) {
                if (scrollFromDragControl.canScrollVertically()) {
                    scrollFromDragControl.scrollY(scrollAmount);
                }

                if (scrollFromDragControl.shouldBlockDrag()) {
                    setY(getParent().toLocalY(scrollFromDragControl.getPixelY() + scrollFromDragControl.getPixelHeight()) / getParent().getInnerScale().y - getHeight() + (scrollFromDragControl == getParent() ? getParent().getScrollY() : 0.0));
                }
            }
        }
    } // onDrag ()

    /**
     * @return the sides of the given control this control is currently at or exceeding, null if not.
     */
    public List<Side> getBoundsAt(BaseControl control) {
        List<Side> sides = new ArrayList<>();
        if (getPixelX() <= control.getPixelX()) sides.add(Side.LEFT);
        if (getPixelX() + getPixelWidth() >= control.getPixelX() + control.getPixelWidth()) sides.add(Side.RIGHT);
        if (getPixelY() <= control.getPixelY()) sides.add(Side.TOP);
        if (getPixelY() + getPixelHeight() >= control.getPixelY() + control.getPixelHeight()) sides.add(Side.BOTTOM);
        return sides;
    } // getBoundsAt ()

    @Override
    public void onDragEnd() {
        if (getParent() != null) getParent().markArrangeDirty(true);
    } // onDragEnd ()

    @Override
    public void forwardGlobalMouseClicked(MouseClickedEvent e) {
        if (!contains(e.mouseX, e.mouseY)) return;
        for (BaseControl child : getReverseChildrenCopy()) child.forwardGlobalMouseClicked(e);
        onGlobalMouseClicked(e);
    } // forwardGlobalMouseClicked ()

    @Override
    protected void onGlobalMouseClicked(MouseClickedEvent e) { }

    @Override
    public void forwardMouseClicked(MouseClickedEvent e) {
        if (!isInteractive()) {
            return;
        }

        if (getVisibility() == Visibility.COLLAPSED) {
            return;
        }

        if (areChildrenInteractive()) {
            for (BaseControl child : getReverseChildrenCopy()) {
                if (child.contains(e.mouseX, e.mouseY)) {
                    child.forwardMouseClicked(e);
                }
            }
        }

        if (!e.isHandled()) {
            eventBus.post(this, e);

            if (!e.isHandled()) {
                onMouseClicked(e);

                if (e.isHandled()) {
                    setPressed(true);
                    setFocused(true);
                }
            }
        }
    } // forwardMouseClicked ()

    @Override
    protected void onMouseClicked(MouseClickedEvent e) {
        e.setIsHandled(true);
    } // onMouseClicked ()

    @Override
    public void forwardGlobalMouseReleased(MouseReleasedEvent e) {
        if (!contains(e.mouseX, e.mouseY)) {
            return;
        }

        for (BaseControl child : getReverseChildrenCopy()) {
            child.forwardGlobalMouseReleased(e);
        }

        onGlobalMouseReleased(e);
    } // forwardGlobalMouseReleased ()

    @Override
    protected void onGlobalMouseReleased(MouseReleasedEvent e) { }

    @Override
    public void forwardMouseReleased(MouseReleasedEvent e) {
        if (!isInteractive()) {
            return;
        }

        if (getVisibility() == Visibility.COLLAPSED) {
            return;
        }

        if (getDraggedControl() != null) {
            return;
        }

        if (areChildrenInteractive()) {
            for (BaseControl child : getReverseChildrenCopy()) {
                if (child.contains(e.mouseX, e.mouseY)) {
                    child.forwardMouseReleased(e);
                }
            }
        }

        if (!e.isHandled()) {
            eventBus.post(this, e);

            if (!e.isHandled()) {
                onMouseReleased(e);
            }
        }
    } // forwardMouseReleased ()

    @Override
    protected void onMouseReleased(MouseReleasedEvent e) {
        e.setIsHandled(true);
    } // onMouseReleased ()

    @Override
    public void forwardGlobalMouseScrolled(MouseScrolledEvent e) {
        if (!contains(e.mouseX, e.mouseY)) {
            return;
        }

        for (BaseControl child : getReverseChildrenCopy()) {
            child.forwardGlobalMouseScrolled(e);
        }

        onGlobalMouseScrolled(e);
    } // forwardGlobalMouseScrolled ()

    @Override
    protected void onGlobalMouseScrolled(MouseScrolledEvent e) { }

    @Override
    public void forwardMouseScrolled(MouseScrolledEvent e) {
        if (!isInteractive()) {
            return;
        }

        if (getVisibility() == Visibility.COLLAPSED) {
            return;
        }

        if (!contains(e.mouseX, e.mouseY)) {
            return;
        }

        if (areChildrenInteractive()) {
            for (BaseControl child : getReverseChildrenCopy()) {
                if (!e.isHandled()) {
                    child.forwardMouseScrolled(e);
                }
            }
        }

        if (!e.isHandled()) {
            eventBus.post(this, e);

            if (!e.isHandled()) {
                onMouseScrolled(e);
            }
        }
    } // forwardMouseScrolled ()

    @Override
    public void onMouseScrolled(MouseScrolledEvent e) {
        double amountScrolled = 0.0;
        double amountToScroll = -10.0 * e.verticalAmount;

        if (GuiUtil.get().isControlKeyDown()) amountScrolled = scrollX(amountToScroll);
        else amountScrolled = scrollY(amountToScroll);

        if (amountScrolled == amountToScroll) e.setIsHandled(true);
        else e.verticalAmount = e.verticalAmount * (1.0 - (amountScrolled / amountToScroll));
    } // onMouseScrolled ()

    @Override
    public void forwardGlobalKeyPressed(KeyPressedEvent e) {
        for (BaseControl child : getReverseChildrenCopy())
            child.forwardGlobalKeyPressed(e);
        onGlobalKeyPressed(e);
    } // forwardGlobalKeyPressed ()

    @Override
    protected void onGlobalKeyPressed(KeyPressedEvent e) {}

    @Override
    public void forwardKeyPressed(KeyPressedEvent e) {
        if (e.isHandled()) return;

        if (isInteractive() && getVisibility() != Visibility.COLLAPSED) {
            eventBus.post(this, e);
            if (!e.isHandled()) onKeyPressed(e);
        }

        if (getParent() != null) getParent().forwardKeyPressed(e);
    } // forwardKeyPressed ()

    @Override
    public void onKeyPressed(KeyPressedEvent e) {}

    @Override
    public void forwardGlobalKeyReleased(KeyReleasedEvent e) {
        for (BaseControl child : getReverseChildrenCopy())
            child.forwardGlobalKeyReleased(e);
        onGlobalKeyReleased(e);
    } // forwardGlobalKeyReleased ()

    @Override
    protected void onGlobalKeyReleased(KeyReleasedEvent e) {}

    @Override
    public void forwardKeyReleased(KeyReleasedEvent e) {
        if (e.isHandled()) return;

        if (isInteractive() && getVisibility() != Visibility.COLLAPSED) {
            eventBus.post(this, e);
            if (!e.isHandled()) onKeyReleased(e);
        }

        if (getParent() != null) getParent().forwardKeyReleased(e);
    } // forwardKeyReleased ()

    @Override
    public void onKeyReleased(KeyReleasedEvent e) {}

    @Override
    public void forwardGlobalCharTyped(CharTypedEvent e) {
        for (BaseControl child : getReverseChildrenCopy())
            child.forwardGlobalCharTyped(e);
        onGlobalCharTyped(e);
    } // forwardGlobalCharTyped ()

    @Override
    protected void onGlobalCharTyped(CharTypedEvent e) {}

    @Override
    public void forwardCharTyped(CharTypedEvent e) {
        if (e.isHandled()) return;

        if (isInteractive() && getVisibility() != Visibility.COLLAPSED) {
            eventBus.post(this, e);
            if (!e.isHandled()) onCharTyped(e);
        }

        if (getParent() != null) getParent().forwardCharTyped(e);
    } // forwardCharTyped ()

    @Override
    public void onCharTyped(CharTypedEvent e) {}

    @Override
    protected void onChildDesiredSizeChanged(BaseControl child) {
        if (shouldFitToContent()) markMeasureDirty(true);
        if (!isArranging()) markArrangeDirty(true);
    } // onChildDesiredSizeChanged ()

    @Override
    protected void onChildSizeChanged(BaseControl child) {
        if (shouldFitToContent()) markMeasureDirty(true);
        if (!isArranging()) markArrangeDirty(true);
    } // onChildSizeChanged ()

    @Override
    protected void onChildMarginChanged(BaseControl child) {
        if (!isArranging()) markArrangeDirty(true);
    } // onChildMarginChanged ()

    @Override
    protected void onChildPositionSizeChanged(BaseControl child) {
        if (shouldFitToContent()) markMeasureDirty(true);
        if (!isArranging()) markArrangeDirty(true);
    } // onChildPositionSizeChanged ()

    @Override
    protected void onChildAlignmentChanged(BaseControl child) {
        markArrangeDirty(true);
    } // onChildAlignmentChanged ()

    @Override
    protected void onChildVisiblityChanged(BaseControl child) {
        if (shouldFitToContent()) markMeasureDirty(true);
        markArrangeDirty(true);
    } // onChildVisiblityChanged ()

    @Override
    public boolean contains(double pixelX, double pixelY) {
        return pixelX >= getPixelX() && pixelX <= getPixelX() + getPixelWidth() && pixelY >= getPixelY() && pixelY <= getPixelY() + getPixelHeight();
    } // contains ()

} // Class Control