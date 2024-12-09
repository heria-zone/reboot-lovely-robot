package net.msymbios.rlovelyr.client.gui.control.controls;

import net.msymbios.rlovelyr.client.gui.control.Control;
import net.msymbios.rlovelyr.client.gui.texture.Textures;
import net.msymbios.rlovelyr.client.gui.util.ScissorStack;

/**
 * A vanilla button control.
 */
public class ButtonControl extends Control {

    // -- Variables --

    /**
     * The text block control containing the button's text.
     */
    public final TextBlockControl textBlock;

    // -- Constructor --

    public ButtonControl() {
        super();

        textBlock = new TextBlockControl();
        textBlock.setParent(this);
        textBlock.setFitWidthToContent(true);
        textBlock.setHorizontalAlignment(0.5);
        textBlock.setHorizontalContentAlignment(0.5);
        textBlock.setVerticalAlignment(0.5);
    } // Constructor ButtonControl ()

    // -- Inherited Methods --

    @Override
    protected void onRender(ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks) {
        super.onRender(scissorStack, mouseX, mouseY, partialTicks);
        if (isHovered()) {
            renderTextureAsBackground(Textures.Common.BUTTON_HOVERED.width((int) (getWidth() - 2)));
            renderTextureAsBackground(Textures.Common.BUTTON_HOVERED.width(2).dx(Textures.Common.BUTTON_HOVERED.width - 2), getWidth() - 2, 0);
        } else {
            renderTextureAsBackground(Textures.Common.BUTTON.width((int) (getWidth() - 2)));
            renderTextureAsBackground(Textures.Common.BUTTON.width(2).dx(Textures.Common.BUTTON.width - 2), getWidth() - 2, 0);
        }
    } // onRender ()

} // Class ButtonControl