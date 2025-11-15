package net.msymbios.rlovelyr.client.gui.control.controls.panels;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.msymbios.rlovelyr.client.gui.control.BaseControl;
import net.msymbios.rlovelyr.client.gui.control.Control;
import net.msymbios.rlovelyr.client.gui.properties.enums.Visibility;

/**
 * A panel that lays out its contents absolutely.
 */
@Environment(EnvType.CLIENT)
public class CanvasPanel extends Control {
    /**
     * The minimum X coordinate of children of the canvas.
     */
    private double minX = 0.0;

    /**
     * The minimum Y coordinate of children of the canvas.
     */
    private double minY = 0.0;

    /**
     * The maximum X coordinate of children of the canvas.
     */
    private double maxX = 0.0;

    /**
     * The maximum Y coordinate of children of the canvas.
     */
    private double maxY = 0.0;

    @Override
    protected void arrange() {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (BaseControl control : getChildrenCopy()) {
            if (control.getVisibility() == Visibility.COLLAPSED) {
                continue;
            }

            control.setWidth(control.getDesiredWidth());
            control.setHeight(control.getDesiredHeight());

            minX = Math.min(minX, control.getX());
            minY = Math.min(minY, control.getY());
            maxX = Math.max(maxX, control.getX() + control.getWidth());
            maxY = Math.max(maxY, control.getY() + control.getHeight());
        }

        setMinX(minX);
        setMinY(minY);
        setMaxX(maxX);
        setMaxY(maxY);
    }

    /**
     * @return the minimum X coordinate of children of the canvas.
     */
    public double getMinX() {
        return minX;
    }

    /**
     * @return the minimum pixel X coordinate of children of the canvas.
     */
    public double getMinPixelX() {
        return toPixelX(getMinX());
    }

    /**
     * Sets the minimum X coordinate of children of the canvas.
     *
     * @param minX the minimum X coordinate of children of the canvas.
     */
    public void setMinX(double minX) {
        this.minX = minX;
    }

    /**
     * @return the minimum Y coordinate of children of the canvas.
     */
    public double getMinY() {
        return minY;
    }

    /**
     * @return the minimum pixel Y coordinate of children of the canvas.
     */
    public double getMinPixelY() {
        return toPixelY(getMinY());
    }

    /**
     * Sets the minimum Y coordinate of children of the canvas.
     *
     * @param minY the minimum Y coordinate of children of the canvas.
     */
    public void setMinY(double minY) {
        this.minY = minY;
    }

    /**
     * @return the maximum X coordinate of children of the canvas.
     */
    public double getMaxX() {
        return maxX;
    }

    /**
     * @return the maximum pixel X coordinate of children of the canvas.
     */
    public double getMaxPixelX() {
        return toPixelX(getMaxX());
    }


    /**
     * Sets the maximum X coordinate of children of the canvas.
     *
     * @param maxX the maximum X coordinate of children of the canvas.
     */
    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    /**
     * @return the maximum Y coordinate of children of the canvas.
     */
    public double getMaxY() {
        return maxY;
    }

    /**
     * @return the maximum pixel Y coordinate of children of the canvas.
     */
    public double getMaxPixelY() {
        return toPixelY(getMaxY());
    }

    /**
     * Sets the maximum Y coordinate of children of the canvas.
     *
     * @param maxY the maximum Y coordinate of children of the canvas.
     */
    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }
}
