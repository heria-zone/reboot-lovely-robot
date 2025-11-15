package net.msymbios.rlovelyr.client.gui.control.controls;

import net.msymbios.rlovelyr.client.gui.control.BaseControl;
import net.msymbios.rlovelyr.client.gui.control.Control;
import net.msymbios.rlovelyr.client.gui.properties.enums.Visibility;
import net.msymbios.rlovelyr.client.gui.texture.Texture;
import net.msymbios.rlovelyr.client.gui.util.ScissorStack;
import net.msymbios.rlovelyr.util.DoubleUtil;

/**
 * A control for all tabbed blockling controls.
 */
public class TexturedControl extends Control {

    // -- Variables --

    /**
     * The background texture.
     */
    private Texture backgroundTexture;

    /**
     * The pressed background texture.
     */
    private Texture pressedBackgroundTexture;

    // -- Constructors --

    /**
     * @param backgroundTexture the background texture.
     */
    public TexturedControl(Texture backgroundTexture) {
        this(backgroundTexture, null);
    } // Constructor TexturedControl ()

    /**
     * @param backgroundTexture the background texture.
     */
    public TexturedControl(Texture backgroundTexture, Texture pressedBackgroundTexture) {
        super();

        setFitWidthToContent(true);
        setFitHeightToContent(true);
        setBackgroundTexture(backgroundTexture);
        setPressedBackgroundTexture(pressedBackgroundTexture);

        setWidth(backgroundTexture.width);
        setHeight(backgroundTexture.height);
    } // Constructor TexturedControl ()

    // -- Inherited Methods --

    @Override
    protected void measureSelf(double availableWidth, double availableHeight) {
        double width = getWidth();
        double height = getHeight();

        if (getWidthPercentage() != null && DoubleUtil.isPositiveAndFinite(availableWidth)) {
            width = availableWidth * getWidthPercentage();
        } else if (shouldFitWidthToContent()) {
            double maxX = isPressed() && getPressedBackgroundTexture() != null ? getPressedBackgroundTexture().width : getBackgroundTexture().width;

            for (BaseControl childControl : getChildren()) {
                if (childControl.getVisibility() == Visibility.COLLAPSED) continue;
                if (childControl.getWidthPercentage() != null) continue;
                double childX = (childControl.getX() + childControl.getWidth() + childControl.getMargin().right) * getInnerScale().x;
                if (childX > maxX) maxX = childX;
            }

            width = maxX != Double.NEGATIVE_INFINITY ? maxX + getPaddingWidth() : 0.0;
        }

        if (getHeightPercentage() != null && DoubleUtil.isPositiveAndFinite(availableHeight)) {
            height = availableHeight * getHeightPercentage();
        } else if (shouldFitHeightToContent()) {
            double maxY = isPressed() && getPressedBackgroundTexture() != null ? getPressedBackgroundTexture().height : getBackgroundTexture().height;

            for (BaseControl childControl : getChildren()) {
                if (childControl.getVisibility() == Visibility.COLLAPSED) continue;
                if (childControl.getHeightPercentage() != null) continue;
                double childY = (childControl.getY() + childControl.getHeight() + childControl.getMargin().bottom) * getInnerScale().y;
                if (childY > maxY) maxY = childY;
            }

            height = maxY != Double.NEGATIVE_INFINITY ? maxY + getPaddingHeight() : 0.0;
        }

        if (availableWidth >= 0.0) setDesiredWidth(width);
        if (availableHeight >= 0.0) setDesiredHeight(height);
    } // measureSelf ()

    @Override
    public void measureChildren() {
        for (BaseControl child : getChildrenCopy()) {
            if (child.getVisibility() == Visibility.COLLAPSED) continue;
            double availableWidth = ((getDesiredWidth() - getPaddingWidth()) / getInnerScale().x) - child.getMarginWidth();
            double availableHeight = ((getDesiredHeight() - getPaddingHeight()) / getInnerScale().y) - child.getMarginHeight();
            child.doMeasure(availableWidth, availableHeight);
        }
    } // measureChildren ()

    @Override
    protected void onRender(ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks) {
        super.onRender(scissorStack, mouseX, mouseY, partialTicks);
        if (isPressed() && !isDraggingOrAncestor() && getPressedBackgroundTexture() != null) renderTextureAsBackground(pressedBackgroundTexture);
        else renderTextureAsBackground(backgroundTexture);
    } // onRender ()

    // -- Custom Methods --

    /**
     * @return the background texture.
     */
    public Texture getBackgroundTexture() {
        return backgroundTexture;
    } // getBackgroundTexture ()

    /**
     * Sets the background texture.
     */
    public void setBackgroundTexture(Texture backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
        if (shouldFitToContent()) markMeasureDirty(true);
    } // setBackgroundTexture ()

    /**
     * @return the pressed background texture.
     */
    public Texture getPressedBackgroundTexture() {
        return pressedBackgroundTexture;
    } // getPressedBackgroundTexture ()

    /**
     * Sets the pressed background texture.
     */
    public void setPressedBackgroundTexture(Texture pressedBackgroundTexture) {
        this.pressedBackgroundTexture = pressedBackgroundTexture;
        if (shouldFitToContent()) markMeasureDirty(true);
    } // setPressedBackgroundTexture ()

} // Class TexturedControl