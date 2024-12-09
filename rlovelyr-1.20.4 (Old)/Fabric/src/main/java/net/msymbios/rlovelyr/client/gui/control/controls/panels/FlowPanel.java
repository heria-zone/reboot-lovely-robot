package net.msymbios.rlovelyr.client.gui.control.controls.panels;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.msymbios.rlovelyr.client.gui.control.BaseControl;
import net.msymbios.rlovelyr.client.gui.control.Control;
import net.msymbios.rlovelyr.client.gui.control.event.events.ReorderEvent;
import net.msymbios.rlovelyr.client.gui.properties.enums.*;
import net.msymbios.rlovelyr.client.gui.util.ScissorStack;
import net.msymbios.rlovelyr.util.DoubleUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A panel that stacks its children on top of each other until it needs to wrap.
 */
@Environment(EnvType.CLIENT)
public class FlowPanel extends Control {
    
    private Flow flow = Flow.TOP_LEFT_LEFT_TO_RIGHT;

    private double horizontalSpacing = 0.0;

    private double verticalSpacing = 0.0;

    private Double lineAlignment = null;

    List<List<BaseControl>> orderedControls = new ArrayList<>();

    List<Double> lineBounds = new ArrayList<>();

    @Override
    protected void measureSelf(double availableWidth, double availableHeight) {
        double width = getWidth();
        double height = getHeight();

        if (getWidthPercentage() != null && DoubleUtil.isPositiveAndFinite(availableWidth)) {
            width = availableWidth * getWidthPercentage();
        } else if (shouldFitWidthToContent()) {
            double minX = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY;

            for (BaseControl childControl : getChildren()) {
                if (childControl.getVisibility() == Visibility.COLLAPSED) {
                    continue;
                }

                double childMinX = (childControl.getX() - childControl.getMargin().left) * getInnerScale().x;
                double childMaxX = (childControl.getX() + childControl.getWidth() + childControl.getMargin().right) * getInnerScale().x;

                if (childControl.isDragging()) {
                    childMinX = Math.min(childMinX, childControl.getPreDragX() - childControl.getMargin().left * getInnerScale().x);
                    childMaxX = Math.max(childMaxX, childControl.getPreDragX() + childControl.getWidth() + childControl.getMargin().right * getInnerScale().x);
                }

                if (childMinX < minX) {
                    minX = childMinX;
                }

                if (childMaxX > maxX) {
                    maxX = childMaxX;
                }
            }

            if (minX != Double.POSITIVE_INFINITY && maxX != Double.NEGATIVE_INFINITY) {
                width = maxX - minX + getPaddingWidth();
            } else if (maxX != Double.NEGATIVE_INFINITY) {
                width = maxX + getPaddingWidth();
            } else {
                width = 0.0;
            }
        }

        if (getHeightPercentage() != null && DoubleUtil.isPositiveAndFinite(availableHeight)) {
            height = availableHeight * getHeightPercentage();
        } else if (shouldFitHeightToContent()) {
            double minY = Double.POSITIVE_INFINITY;
            double maxY = Double.NEGATIVE_INFINITY;

            for (BaseControl childControl : getChildren()) {
                if (childControl.getVisibility() == Visibility.COLLAPSED) {
                    continue;
                }

                double childMinY = (childControl.getY() - childControl.getMargin().top) * getInnerScale().y;
                double childY = (childControl.getY() + childControl.getHeight() + childControl.getMargin().bottom) * getInnerScale().y;

                if (childControl.isDragging()) {
                    childMinY = Math.min(childMinY, childControl.getPreDragY() - childControl.getMargin().top * getInnerScale().y);
                    childY = Math.max(childY, childControl.getPreDragY() + childControl.getHeight() + childControl.getMargin().bottom * getInnerScale().y);
                }

                if (childMinY < minY) {
                    minY = childMinY;
                }

                if (childY > maxY) {
                    maxY = childY;
                }
            }

            if (minY != Double.POSITIVE_INFINITY && maxY != Double.NEGATIVE_INFINITY) {
                height = maxY - minY + getPaddingHeight();
            } else if (maxY != Double.NEGATIVE_INFINITY) {
                height = maxY + getPaddingHeight();
            } else {
                height = 0.0;
            }
        }

        if (availableWidth >= 0.0) {
            setDesiredWidth(width);
        }

        if (availableHeight >= 0.0) {
            setDesiredHeight(height);
        }
    }

    @Override
    public void measureChildren() {
        for (BaseControl child : getChildrenCopy()) {
            if (child.getVisibility() == Visibility.COLLAPSED) {
                continue;
            }

            double availableWidth = ((getDesiredWidth() - getPaddingWidth()) / getInnerScale().x) - child.getMarginWidth();
            double availableHeight = ((getDesiredHeight() - getPaddingHeight()) / getInnerScale().y) - child.getMarginHeight();

            if (getOverflowDirection().isHorizontal) {
                availableWidth = Double.POSITIVE_INFINITY;
                availableHeight -= child.getMarginHeight() + getPaddingHeight();
            } else {
                availableWidth -= child.getMarginWidth() + getPaddingWidth();
                availableHeight = Double.POSITIVE_INFINITY;
            }

            child.doMeasure(availableWidth, availableHeight);
        }
    }

    @Override
    protected void arrange() {
        double availableWidth = getWidthWithoutPadding() / getInnerScale().x;
        double availableHeight = getHeightWithoutPadding() / getInnerScale().y;

        double newLineControlX = 0.0;
        double newLineControlY = 0.0;

        if (getStartCorner() == Corner.TOP_RIGHT || getStartCorner() == Corner.BOTTOM_RIGHT) {
            newLineControlX = getWidthWithoutPadding() / getInnerScale().x;
        }

        if (getStartCorner() == Corner.BOTTOM_LEFT || getStartCorner() == Corner.BOTTOM_RIGHT) {
            newLineControlY = getHeightWithoutPadding() / getInnerScale().y;
        }

        double controlX = newLineControlX;
        double controlLeft = 0.0;
        double controlRight = 0.0;
        double controlY = newLineControlY;
        double controlTop = 0.0;
        double controlBottom = 0.0;
        boolean hasJustReset = true;
        List<BaseControl> controlsInLine = new ArrayList<>();
        orderedControls.clear();
        lineBounds.clear();

        if (getOverflowDirection().isHorizontal) {
            lineBounds.add(newLineControlX);
        } else {
            lineBounds.add(newLineControlY);
        }

        double draggedControlX = getDraggedControl() != null ? getDraggedControl().getX() : 0.0;
        double draggedControlY = getDraggedControl() != null ? getDraggedControl().getY() : 0.0;

        for (BaseControl control : getChildrenCopy()) {
            if (control.getVisibility() == Visibility.COLLAPSED) {
                continue;
            }

            control.setWidth(control.getDesiredWidth());
            control.setHeight(control.getDesiredHeight());

            if (getDirection() == Direction.LEFT_TO_RIGHT) {
                controlX += control.getMargin().left;
                controlRight = controlX + control.getWidth() + control.getMargin().right;

                if (!hasJustReset && controlRight > availableWidth) {
                    controlX = newLineControlX + control.getMargin().left;
                    controlY = newLineControlY;
                    hasJustReset = true;

                    orderedControls.add(controlsInLine);
                    alignVertically(controlsInLine);
                    controlsInLine = new ArrayList<>();
                } else {
                    hasJustReset = false;
                }

                if (getOverflowDirection() == Direction.TOP_TO_BOTTOM) {
                    controlY += control.getMargin().top;
                } else if (getOverflowDirection() == Direction.BOTTOM_TO_TOP) {
                    controlY -= control.getHeight() + control.getMargin().bottom;
                }
            }
            if (getDirection() == Direction.RIGHT_TO_LEFT) {
                controlX -= control.getWidth() + control.getMargin().right;
                controlLeft = controlX - control.getMargin().left;

                if (!hasJustReset && controlLeft < 0.0) {
                    controlX = newLineControlX - control.getWidth() - control.getMargin().right;
                    controlY = newLineControlY;
                    hasJustReset = true;

                    orderedControls.add(controlsInLine);
                    alignVertically(controlsInLine);
                    controlsInLine = new ArrayList<>();
                } else {
                    hasJustReset = false;
                }

                if (getOverflowDirection() == Direction.TOP_TO_BOTTOM) {
                    controlY += control.getMargin().top;
                } else if (getOverflowDirection() == Direction.BOTTOM_TO_TOP) {
                    controlY -= control.getHeight() + control.getMargin().bottom;
                }
            } else if (getDirection() == Direction.TOP_TO_BOTTOM) {
                controlY += control.getMargin().top;
                controlBottom = controlY + control.getHeight() + control.getMargin().bottom;

                if (!hasJustReset && controlBottom > availableHeight) {
                    controlX = newLineControlX;
                    controlY = newLineControlY + control.getMargin().top;
                    hasJustReset = true;

                    orderedControls.add(controlsInLine);
                    alignHorizontally(controlsInLine);
                    controlsInLine = new ArrayList<>();
                } else {
                    hasJustReset = false;
                }

                if (getOverflowDirection() == Direction.LEFT_TO_RIGHT) {
                    controlX += control.getMargin().left;
                } else if (getOverflowDirection() == Direction.RIGHT_TO_LEFT) {
                    controlX -= control.getWidth() + control.getMargin().right;
                }
            } else if (getDirection() == Direction.BOTTOM_TO_TOP) {
                controlY -= control.getHeight() + control.getMargin().bottom;
                controlTop = controlY - control.getMargin().top;

                if (!hasJustReset && controlTop < 0.0) {
                    controlX = newLineControlX;
                    controlY = newLineControlY - control.getHeight() - control.getMargin().bottom;
                    hasJustReset = true;

                    orderedControls.add(controlsInLine);
                    alignHorizontally(controlsInLine);
                    controlsInLine = new ArrayList<>();
                } else {
                    hasJustReset = false;
                }

                if (getOverflowDirection() == Direction.LEFT_TO_RIGHT) {
                    controlX += control.getMargin().left;
                } else if (getOverflowDirection() == Direction.RIGHT_TO_LEFT) {
                    controlX -= control.getWidth() + control.getMargin().right;
                }
            }

            if (control.isDragging()) {
                control.setPreDragPosition(controlX, controlY);
            } else {
                control.setPosition(controlX, controlY);
            }

            controlsInLine.add(control);

            if (getDirection() == Direction.LEFT_TO_RIGHT) {
                controlX += control.getWidth() + control.getMargin().right + getHorizontalSpacing();

                if (getOverflowDirection() == Direction.TOP_TO_BOTTOM) {
                    controlY -= control.getMargin().top;
                } else if (getOverflowDirection() == Direction.BOTTOM_TO_TOP) {
                    controlY += control.getHeight() + control.getMargin().bottom;
                }
            } else if (getDirection() == Direction.RIGHT_TO_LEFT) {
                controlX -= control.getMargin().left + getHorizontalSpacing();

                if (getOverflowDirection() == Direction.TOP_TO_BOTTOM) {
                    controlY -= control.getMargin().top;
                } else if (getOverflowDirection() == Direction.BOTTOM_TO_TOP) {
                    controlY += control.getHeight() + control.getMargin().bottom;
                }
            } else if (getDirection() == Direction.TOP_TO_BOTTOM) {
                if (getOverflowDirection() == Direction.LEFT_TO_RIGHT) {
                    controlX -= control.getMargin().left;
                } else if (getOverflowDirection() == Direction.RIGHT_TO_LEFT) {
                    controlX += control.getWidth() + control.getMargin().right;
                }

                controlY += control.getHeight() + control.getMargin().bottom + getVerticalSpacing();
            } else if (getDirection() == Direction.BOTTOM_TO_TOP) {
                if (getOverflowDirection() == Direction.LEFT_TO_RIGHT) {
                    controlX -= control.getMargin().left;
                } else if (getOverflowDirection() == Direction.RIGHT_TO_LEFT) {
                    controlX += control.getWidth() + control.getMargin().right;
                }

                controlY -= control.getMargin().top + getVerticalSpacing();
            }

            if (hasJustReset) {
                if (getOverflowDirection().isHorizontal) {
                    lineBounds.add(newLineControlX + (getOverflowDirection() == Direction.LEFT_TO_RIGHT ? -getHorizontalSpacing() : getHorizontalSpacing()) / 2.0);
                } else {
                    lineBounds.add(newLineControlY + (getOverflowDirection() == Direction.TOP_TO_BOTTOM ? -getVerticalSpacing() : getVerticalSpacing()) / 2.0);
                }
            }

            if (getOverflowDirection() == Direction.LEFT_TO_RIGHT) {
                controlRight = controlX + control.getWidthWithMargin() + getHorizontalSpacing();

                if (controlRight > newLineControlX) {
                    newLineControlX = controlRight;
                }
            } else if (getOverflowDirection() == Direction.RIGHT_TO_LEFT) {
                controlLeft = controlX - control.getWidthWithMargin() - getHorizontalSpacing();

                if (controlLeft < newLineControlX) {
                    newLineControlX = controlLeft;
                }
            } else if (getOverflowDirection() == Direction.TOP_TO_BOTTOM) {
                controlBottom = controlY + control.getHeightWithMargin() + getVerticalSpacing();

                if (controlBottom > newLineControlY) {
                    newLineControlY = controlBottom;
                }
            } else if (getOverflowDirection() == Direction.BOTTOM_TO_TOP) {
                controlTop = controlY - control.getHeightWithMargin() - getVerticalSpacing();

                if (controlTop < newLineControlY) {
                    newLineControlY = controlTop;
                }
            }
        }

        if (getOverflowDirection().isHorizontal) {
            lineBounds.add(newLineControlX + (getOverflowDirection() == Direction.LEFT_TO_RIGHT ? -getHorizontalSpacing() : getHorizontalSpacing()));
        } else {
            lineBounds.add(newLineControlY + (getOverflowDirection() == Direction.TOP_TO_BOTTOM ? -getVerticalSpacing() : getVerticalSpacing()));
        }

        if (!controlsInLine.isEmpty()) {
            orderedControls.add(controlsInLine);

            if (getDirection() == Direction.LEFT_TO_RIGHT || getDirection() == Direction.RIGHT_TO_LEFT) {
                alignVertically(controlsInLine);
            } else {
                alignHorizontally(controlsInLine);
            }
        }

        if (getHorizontalContentAlignment() != null) {
            if (getDirection().isVertical) {
                double minX = Double.POSITIVE_INFINITY;
                double maxX = Double.NEGATIVE_INFINITY;

                for (BaseControl controlToAlign : getChildren()) {
                    double controlToAlignMinX = controlToAlign.getX() - controlToAlign.getMargin().left;
                    double controlToAlignMaxX = controlToAlign.getX() + controlToAlign.getWidth() + controlToAlign.getMargin().right;

                    if (controlToAlignMinX < minX) {
                        minX = controlToAlignMinX;
                    }

                    if (controlToAlignMaxX > maxX) {
                        maxX = controlToAlignMaxX;
                    }
                }
                double dif = ((maxX - minX - availableWidth)) * (getOverflowDirection() == Direction.LEFT_TO_RIGHT ? -getHorizontalContentAlignment() : 1.0 - getHorizontalContentAlignment());

                for (BaseControl controlToAlign : getChildren()) {
                    controlToAlign.setX(controlToAlign.getX() + dif);
                }
            } else {
                for (List<BaseControl> line : orderedControls) {
                    double lineMinX = Double.POSITIVE_INFINITY;
                    double lineMaxX = Double.NEGATIVE_INFINITY;

                    for (BaseControl controlToAlign : line) {
                        double controlToAlignMinX = controlToAlign.getX() - controlToAlign.getMargin().left;
                        double controlToAlignMaxX = controlToAlign.getX() + controlToAlign.getWidth() + controlToAlign.getMargin().right;

                        if (controlToAlignMinX < lineMinX) {
                            lineMinX = controlToAlignMinX;
                        }

                        if (controlToAlignMaxX > lineMaxX) {
                            lineMaxX = controlToAlignMaxX;
                        }
                    }

                    double dif = ((availableWidth - (lineMaxX - lineMinX))) * (getDirection() == Direction.RIGHT_TO_LEFT ? 1.0 - getHorizontalContentAlignment() : -getHorizontalContentAlignment());

                    for (BaseControl controlToAlign : line) {
                        controlToAlign.setX(controlToAlign.getX() - dif);
                    }
                }
            }
        }

        if (getVerticalContentAlignment() != null) {
            if (getDirection().isHorizontal) {
                double minY = Double.POSITIVE_INFINITY;
                double maxY = Double.NEGATIVE_INFINITY;

                for (BaseControl controlToAlign : getChildren()) {
                    double controlToAlignMinY = controlToAlign.getY() - controlToAlign.getMargin().top;
                    double controlToAlignMaxY = controlToAlign.getY() + controlToAlign.getHeight() + controlToAlign.getMargin().bottom;

                    if (controlToAlignMinY < minY) {
                        minY = controlToAlignMinY;
                    }

                    if (controlToAlignMaxY > maxY) {
                        maxY = controlToAlignMaxY;
                    }
                }
                double dif = ((maxY - minY - availableHeight)) * (getOverflowDirection() == Direction.TOP_TO_BOTTOM ? -getVerticalContentAlignment() : 1.0 - getVerticalContentAlignment());

                for (BaseControl controlToAlign : getChildren()) {
                    controlToAlign.setY(controlToAlign.getY() + dif);
                }
            } else {
                for (List<BaseControl> line : orderedControls) {
                    double lineMinY = Double.POSITIVE_INFINITY;
                    double lineMaxY = Double.NEGATIVE_INFINITY;

                    for (BaseControl controlToAlign : line) {
                        double controlToAlignMinY = controlToAlign.getY() - controlToAlign.getMargin().top;
                        double controlToAlignMaxY = controlToAlign.getY() + controlToAlign.getHeight() + controlToAlign.getMargin().bottom;

                        if (controlToAlignMinY < lineMinY) {
                            lineMinY = controlToAlignMinY;
                        }

                        if (controlToAlignMaxY > lineMaxY) {
                            lineMaxY = controlToAlignMaxY;
                        }
                    }

                    double dif = ((availableHeight - (lineMaxY - lineMinY))) * (getDirection() == Direction.BOTTOM_TO_TOP ? 1.0 - getVerticalContentAlignment() : -getVerticalContentAlignment());

                    for (BaseControl controlToAlign : line) {
                        controlToAlign.setY(controlToAlign.getY() - dif);
                    }
                }
            }
        }

        if (getDraggedControl() != null) {
            getDraggedControl().setX(draggedControlX);
            getDraggedControl().setY(draggedControlY);
        }
    }

    private void alignHorizontally(List<BaseControl> controlsToAlign) {
        if (getLineAlignment() != null) {
            double minX = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY;

            for (BaseControl controlToAlign : controlsToAlign) {
                double controlToAlignMinX = controlToAlign.getX() - controlToAlign.getMargin().left;
                double controlToAlignMaxX = controlToAlign.getX() + controlToAlign.getWidth() + controlToAlign.getMargin().right;

                if (controlToAlignMinX < minX) {
                    minX = controlToAlignMinX;
                }

                if (controlToAlignMaxX > maxX) {
                    maxX = controlToAlignMaxX;
                }
            }

            double dif = maxX - minX;

            for (BaseControl controlToAlign : controlsToAlign) {
                controlToAlign.setX(minX + controlToAlign.getMargin().left + (dif - controlToAlign.getWidthWithMargin()) * getLineAlignment());
            }
        }
    }

    private void alignVertically(List<BaseControl> controlsToAlign) {
        if (getLineAlignment() != null) {
            double minY = Double.POSITIVE_INFINITY;
            double maxY = Double.NEGATIVE_INFINITY;

            for (BaseControl controlToAlign : controlsToAlign) {
                double controlToAlignMinY = controlToAlign.getY() - controlToAlign.getMargin().top;
                double controlToAlignMaxY = controlToAlign.getY() + controlToAlign.getHeight() + controlToAlign.getMargin().bottom;

                if (controlToAlignMinY < minY) {
                    minY = controlToAlignMinY;
                }

                if (controlToAlignMaxY > maxY) {
                    maxY = controlToAlignMaxY;
                }
            }

            double dif = maxY - minY;

            for (BaseControl controlToAlign : controlsToAlign) {
                controlToAlign.setY(minY + controlToAlign.getMargin().top + (dif - controlToAlign.getHeightWithMargin()) * getLineAlignment());
            }
        }
    }

    @Override
    protected void onRenderUpdate(ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks) {
        if (getDraggedControl() != null && getDraggedControl().getParent() == this) {
            updateDraggedControl(getDraggedControl());
        }
    }

    private void updateDraggedControl(BaseControl draggedControl) {
        double pixelMidX = draggedControl.getPixelMidX();
        double pixelMidY = draggedControl.getPixelMidY();

        List<BaseControl> containingLine = null;

        for (int i = 0; i < orderedControls.size(); i++) {
            double lineMin = lineBounds.get(i);
            double lineMax = lineBounds.get(i + 1);
            double temp = lineMin;

            if (lineMin > lineMax) {
                lineMin = lineMax;
                lineMax = temp;
            }

            if (getOverflowDirection().isHorizontal) {
                lineMin = toPixelX(lineMin);
                lineMax = toPixelX(lineMax);

                if (pixelMidX >= lineMin && pixelMidX <= lineMax) {
                    containingLine = orderedControls.get(i);
                    break;
                }
            } else {
                lineMin = toPixelY(lineMin);
                lineMax = toPixelY(lineMax);

                if (pixelMidY >= lineMin && pixelMidY <= lineMax) {
                    containingLine = orderedControls.get(i);
                    break;
                }
            }
        }

        if (containingLine != null) {
            Side closestSide = null;
            BaseControl closestControl = null;
            double closestDistance = Double.POSITIVE_INFINITY;

            for (BaseControl control : containingLine) {
                if (control == draggedControl) {
                    continue;
                }

                if (!control.isReorderable()) {
                    continue;
                }

                if (getDirection().isHorizontal) {
                    double controlLeft = control.getPixelLeft();
                    double controlRight = control.getPixelRight();

                    if (pixelMidX >= controlLeft && pixelMidX <= controlRight) {
                        closestSide = pixelMidX < control.getPixelMidX() ? Side.LEFT : Side.RIGHT;
                        closestControl = control;
                        break;
                    } else if (pixelMidX < controlLeft) {
                        double distance = controlLeft - pixelMidX;

                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestSide = Side.LEFT;
                            closestControl = control;
                        }
                    } else if (pixelMidX > controlRight) {
                        double distance = pixelMidX - controlRight;

                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestSide = Side.RIGHT;
                            closestControl = control;
                        }
                    }
                } else if (getDirection().isVertical) {
                    double controlTop = control.getPixelTop();
                    double controlBottom = control.getPixelBottom();

                    if (pixelMidY >= controlTop && pixelMidY <= controlBottom) {
                        closestSide = pixelMidY < control.getPixelMidY() ? Side.TOP : Side.BOTTOM;
                        closestControl = control;
                        break;
                    } else if (pixelMidY < controlTop) {
                        double distance = controlTop - pixelMidY;

                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestSide = Side.TOP;
                            closestControl = control;
                        }
                    } else if (pixelMidY > controlBottom) {
                        double distance = pixelMidY - controlBottom;

                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestSide = Side.BOTTOM;
                            closestControl = control;
                        }
                    }
                }
            }

            if (closestControl != null) {
                boolean isDraggedDirectlyBeforeClosest = getChildrenCopy().indexOf(draggedControl) == getChildrenCopy().indexOf(closestControl) - 1;
                boolean isDraggedDirectlyAfterClosest = getChildrenCopy().indexOf(draggedControl) == getChildrenCopy().indexOf(closestControl) + 1;

                if (getDirection() == Direction.LEFT_TO_RIGHT) {
                    if (closestSide == Side.LEFT && !isDraggedDirectlyBeforeClosest) {
                        eventBus.post(this, new ReorderEvent(draggedControl, closestControl, true));
                        insertChildBefore(draggedControl, closestControl);
                    } else if (closestSide == Side.RIGHT && !isDraggedDirectlyAfterClosest) {
                        eventBus.post(this, new ReorderEvent(draggedControl, closestControl, false));
                        insertChildAfter(draggedControl, closestControl);
                    }
                } else if (getDirection() == Direction.RIGHT_TO_LEFT) {
                    if (closestSide == Side.LEFT && !isDraggedDirectlyAfterClosest) {
                        eventBus.post(this, new ReorderEvent(draggedControl, closestControl, false));
                        insertChildAfter(draggedControl, closestControl);
                    } else if (closestSide == Side.RIGHT && !isDraggedDirectlyBeforeClosest) {
                        eventBus.post(this, new ReorderEvent(draggedControl, closestControl, true));
                        insertChildBefore(draggedControl, closestControl);
                    }
                } else if (getDirection() == Direction.TOP_TO_BOTTOM) {
                    if (closestSide == Side.TOP && !isDraggedDirectlyBeforeClosest) {
                        eventBus.post(this, new ReorderEvent(draggedControl, closestControl, true));
                        insertChildBefore(draggedControl, closestControl);
                    } else if (closestSide == Side.BOTTOM && !isDraggedDirectlyAfterClosest) {
                        eventBus.post(this, new ReorderEvent(draggedControl, closestControl, false));
                        insertChildAfter(draggedControl, closestControl);
                    }
                } else if (getDirection() == Direction.BOTTOM_TO_TOP) {
                    if (closestSide == Side.TOP && !isDraggedDirectlyAfterClosest) {
                        eventBus.post(this, new ReorderEvent(draggedControl, closestControl, false));
                        insertChildAfter(draggedControl, closestControl);
                    } else if (closestSide == Side.BOTTOM && !isDraggedDirectlyBeforeClosest) {
                        eventBus.post(this, new ReorderEvent(draggedControl, closestControl, true));
                        insertChildBefore(draggedControl, closestControl);
                    }
                }
            }
        }
    }

    public Flow getFlow() {
        return flow;
    }

    public void setFlow(Flow flow) {
        this.flow = flow;

        markArrangeDirty(true);
    }

    public Corner getStartCorner() {
        return flow.startCorner;
    }

    public Direction getDirection() {
        return flow.direction;
    }

    public Direction getOverflowDirection() {
        return flow.overflowDirection;
    }

    public double getHorizontalSpacing() {
        return horizontalSpacing;
    }

    public void setHorizontalSpacing(double horizontalSpacing) {
        this.horizontalSpacing = horizontalSpacing;

        markArrangeDirty(true);
    }

    public double getVerticalSpacing() {
        return verticalSpacing;
    }

    public void setVerticalSpacing(double verticalSpacing) {
        this.verticalSpacing = verticalSpacing;

        markArrangeDirty(true);
    }

    public Double getLineAlignment() {
        return lineAlignment;
    }

    public void setLineAlignment(Double lineAlignment) {
        this.lineAlignment = lineAlignment;

        markArrangeDirty(true);
    }
}
