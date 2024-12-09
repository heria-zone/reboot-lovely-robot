package net.msymbios.rlovelyr.client.gui.util;

import java.util.Stack;

/**
 * Represents a stack of scissor bounds.
 */
public class ScissorStack extends Stack<ScissorBounds> {
    /**
     * The bounds calculated from the current stack.
     */
    private ScissorBounds calculatedBounds = new ScissorBounds(0, 0, 0, 0);

    @Override
    public ScissorBounds push(ScissorBounds item) {
        ScissorBounds bounds = super.push(item);

        recalculateBounds();

        return bounds;
    }

    @Override
    public synchronized ScissorBounds pop() {
        ScissorBounds bounds = super.pop();

        recalculateBounds();

        return bounds;
    }

    /**
     * Recalculates the scissor bounds for the current stack.
     */
    public void recalculateBounds() {
        int minX = 0;
        int minY = 0;
        int maxX = Integer.MAX_VALUE;
        int maxY = Integer.MAX_VALUE;

        for (ScissorBounds bounds : this) {
            minX = Math.max(minX, bounds.pixelX);
            minY = Math.max(minY, bounds.pixelY);
            maxX = Math.min(maxX, bounds.pixelX + bounds.pixelWidth);
            maxY = Math.min(maxY, bounds.pixelY + bounds.pixelHeight);
        }

        calculatedBounds = new ScissorBounds(minX, minY, maxX - minX, maxY - minY);
    }

    /**
     * Enables scissoring using the {@link #calculatedBounds}.
     */
    public void enable() {
        GuiUtil.scissor(calculatedBounds.pixelX, calculatedBounds.pixelY, calculatedBounds.pixelWidth, calculatedBounds.pixelHeight);
    }

    /**
     * Disables scissoring.
     */
    public void disable() {
        GuiUtil.disableScissor();
    }

    /**
     * @return the calculated bounds based on the current stack.
     */
    public ScissorBounds getCalculatedBounds() {
        return calculatedBounds;
    }
}
