package net.msymbios.rlovelyr.client.gui.control.controls.panels;

import net.msymbios.rlovelyr.client.gui.control.BaseControl;
import net.msymbios.rlovelyr.client.gui.control.Control;
import net.msymbios.rlovelyr.client.gui.control.event.events.ReorderEvent;
import net.msymbios.rlovelyr.client.gui.properties.enums.Direction;
import net.msymbios.rlovelyr.client.gui.properties.enums.Side;
import net.msymbios.rlovelyr.client.gui.properties.enums.Visibility;
import net.msymbios.rlovelyr.client.gui.util.ScissorStack;
import net.msymbios.rlovelyr.util.DoubleUtil;

/**
 * A panel that stacks its children on top of each other.
 */
public class StackPanel extends Control {

    private Direction direction = Direction.TOP_TO_BOTTOM;

    private double spacing = 0.0;

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
                double childMaxY = (childControl.getY() + childControl.getHeight() + childControl.getMargin().bottom) * getInnerScale().y;

                if (childControl.isDragging()) {
                    childMinY = Math.min(childMinY, childControl.getPreDragY() - childControl.getMargin().top * getInnerScale().y);
                    childMaxY = Math.max(childMaxY, childControl.getPreDragY() + childControl.getHeight() + childControl.getMargin().bottom * getInnerScale().y);
                }

                if (childMinY < minY) {
                    minY = childMinY;
                }

                if (childMaxY > maxY) {
                    maxY = childMaxY;
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

            if (getDirection().isHorizontal) {
                availableWidth = Double.POSITIVE_INFINITY;
            } else {
                availableHeight = Double.POSITIVE_INFINITY;
            }

            child.doMeasure(availableWidth, availableHeight);
        }
    }

    @Override
    protected void arrange() {
        double startControlX = 0.0;
        double startControlY = 0.0;

        if (getDirection() == Direction.RIGHT_TO_LEFT) {
            startControlX = (getWidth() - getPaddingWidth()) / getInnerScale().x;
        } else if (getDirection() == Direction.BOTTOM_TO_TOP) {
            startControlY = (getHeight() - getPaddingHeight()) / getInnerScale().y;
        }

        double nextControlX = startControlX;
        double nextControlY = startControlY;

        for (BaseControl control : getChildrenCopy()) {
            if (control.getVisibility() == Visibility.COLLAPSED) {
                continue;
            }

            control.setWidth(control.getDesiredWidth());
            control.setHeight(control.getDesiredHeight());

            double controlX = 0.0;
            double controlY = 0.0;

            if (getDirection() == Direction.LEFT_TO_RIGHT) {
                nextControlX += control.getMargin().left;
            }
            if (getDirection() == Direction.RIGHT_TO_LEFT) {
                nextControlX -= control.getWidth() + control.getMargin().right;
            } else if (getDirection() == Direction.TOP_TO_BOTTOM) {
                nextControlY += control.getMargin().top;
            } else if (getDirection() == Direction.BOTTOM_TO_TOP) {
                nextControlY -= control.getHeight() + control.getMargin().bottom;
            }

            if (getDirection().isHorizontal) {
                controlX = nextControlX;
            } else {
                controlY = nextControlY;
            }

            if (getDirection().isVertical) {
                controlX += (((getWidthWithoutPadding() / getInnerScale().x) - control.getWidthWithMargin()) * getHorizontalAlignmentFor(control)) + control.getMargin().left;
            }

            if (getDirection().isHorizontal) {
                controlY += (((getHeightWithoutPadding() / getInnerScale().y) - control.getHeightWithMargin()) * getVerticalAlignmentFor(control)) + control.getMargin().top;
            }

            if (control.isDragging()) {
                control.setPreDragPosition(controlX, controlY);
            } else {
                control.setPosition(controlX, controlY);
            }

            if (getDirection() == Direction.LEFT_TO_RIGHT) {
                nextControlX += control.getWidth() + control.getMargin().right + getSpacing();
            } else if (getDirection() == Direction.RIGHT_TO_LEFT) {
                nextControlX -= control.getMargin().left + getSpacing();
            } else if (getDirection() == Direction.TOP_TO_BOTTOM) {
                nextControlY += control.getHeight() + control.getMargin().bottom + getSpacing();
            } else if (getDirection() == Direction.BOTTOM_TO_TOP) {
                nextControlY -= control.getMargin().right + getSpacing();
            }
        }

        if (getHorizontalContentAlignment() != null) {
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

            double dif = ((getWidthWithoutPadding() * getInnerScale().x) - (maxX - minX)) * getHorizontalContentAlignment();

            for (BaseControl controlToAlign : getChildren()) {
                controlToAlign.setX(controlToAlign.getX() + dif);
            }
        }

        if (getVerticalContentAlignment() != null) {
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

            double dif = ((getHeightWithoutPadding() * getInnerScale().y) - (maxY - minY)) * getVerticalContentAlignment();

            for (BaseControl controlToAlign : getChildren()) {
                controlToAlign.setY(controlToAlign.getY() + dif);
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

        double closestDistance = Double.POSITIVE_INFINITY;
        BaseControl closestControl = null;
        Side closestSide = null;

        for (BaseControl control : getChildrenCopy()) {
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
            boolean isDraggedBeforeClosest = getChildrenCopy().indexOf(draggedControl) < getChildrenCopy().indexOf(closestControl);

            if (getDirection() == Direction.LEFT_TO_RIGHT) {
                if (closestSide == Side.LEFT && !isDraggedBeforeClosest) {
                    eventBus.post(this, new ReorderEvent(draggedControl, closestControl, true));
                    insertChildBefore(draggedControl, closestControl);
                } else if (closestSide == Side.RIGHT && isDraggedBeforeClosest) {
                    eventBus.post(this, new ReorderEvent(draggedControl, closestControl, false));
                    insertChildAfter(draggedControl, closestControl);
                }
            } else if (getDirection() == Direction.RIGHT_TO_LEFT) {
                if (closestSide == Side.LEFT && isDraggedBeforeClosest) {
                    eventBus.post(this, new ReorderEvent(draggedControl, closestControl, false));
                    insertChildAfter(draggedControl, closestControl);
                } else if (closestSide == Side.RIGHT && !isDraggedBeforeClosest) {
                    eventBus.post(this, new ReorderEvent(draggedControl, closestControl, true));
                    insertChildBefore(draggedControl, closestControl);
                }
            } else if (getDirection() == Direction.TOP_TO_BOTTOM) {
                if (closestSide == Side.TOP && !isDraggedBeforeClosest) {
                    eventBus.post(this, new ReorderEvent(draggedControl, closestControl, true));
                    insertChildBefore(draggedControl, closestControl);
                } else if (closestSide == Side.BOTTOM && isDraggedBeforeClosest) {
                    eventBus.post(this, new ReorderEvent(draggedControl, closestControl, false));
                    insertChildAfter(draggedControl, closestControl);
                }
            } else if (getDirection() == Direction.BOTTOM_TO_TOP) {
                if (closestSide == Side.TOP && isDraggedBeforeClosest) {
                    eventBus.post(this, new ReorderEvent(draggedControl, closestControl, false));
                    insertChildAfter(draggedControl, closestControl);
                } else if (closestSide == Side.BOTTOM && !isDraggedBeforeClosest) {
                    eventBus.post(this, new ReorderEvent(draggedControl, closestControl, true));
                    insertChildBefore(draggedControl, closestControl);
                }
            }
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public double getSpacing() {
        return spacing;
    }

    public void setSpacing(double spacing) {
        this.spacing = spacing;

        markArrangeDirty(true);
    }
}
