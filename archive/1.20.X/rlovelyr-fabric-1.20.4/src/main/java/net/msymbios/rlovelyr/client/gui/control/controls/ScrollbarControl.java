package net.msymbios.rlovelyr.client.gui.control.controls;

import com.mojang.blaze3d.systems.RenderSystem;
import net.msymbios.rlovelyr.client.gui.control.BaseControl;
import net.msymbios.rlovelyr.client.gui.control.Control;
import net.msymbios.rlovelyr.client.gui.control.event.events.input.MouseClickedEvent;
import net.msymbios.rlovelyr.client.gui.control.event.events.input.MouseScrolledEvent;
import net.msymbios.rlovelyr.client.gui.texture.Textures;
import net.msymbios.rlovelyr.client.gui.util.ScissorStack;

/**
 * A scrollbar control.
 */
public class ScrollbarControl extends Control {
    
    /**
     * The grabber control.
     */
    private final Control grabber;

    /**
     * The control the scrollbar is attached to.
     */
    private BaseControl attachedControl;

    /**
     *
     */
    public ScrollbarControl() {
        super();

        setWidth(Textures.Common.Scrollbar.GRABBER_PRESSED.width);
        setPressable(false);

        grabber = new TexturedControl(Textures.Common.Scrollbar.GRABBER_UNPRESSED, Textures.Common.Scrollbar.GRABBER_PRESSED) {
            @Override
            protected void onRenderUpdate( ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks) {
                if (getAttachedControl() != null) {
                    if (!getAttachedControl().canScrollVertically()) {
                        setInteractive(false);
                        setVerticalAlignment(0.0);
                    } else {
                        if (getAttachedControl().getMaxScrollY() - getAttachedControl().getMinScrollY() == 0.0) {
                            setInteractive(false);
                            setVerticalAlignment(0.0);
                        } else {
                            setInteractive(true);

                            if (isDragging()) {
                                double minPixelY = getParent().toPixelY(0.0 + getHeight() / 2.0);
                                double maxPixelY = getParent().toPixelY(getParent().getHeight() - getHeight() / 2.0);
                                double percentage = ((mouseY - getHeight() / 2.0) - minPixelY) / (maxPixelY - minPixelY);

                                getAttachedControl().setScrollPercentY(percentage);
                            }

                            setVerticalAlignment(getAttachedControl().getScrollPercentY());
                        }
                    }
                }
            }

            @Override
            protected void onRender(ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks) {
                if (isInteractive()) {
                    if (isPressed() && getPressedBackgroundTexture() != null) {
                        renderTextureAsBackground(getPressedBackgroundTexture());
                    } else {
                        renderTextureAsBackground(getBackgroundTexture());
                    }
                } else {
                    RenderSystem.setShaderColor(0.7f, 0.7f, 0.7f, 1.0f);
                    renderTextureAsBackground(getBackgroundTexture());
                }
            }
        };
        grabber.setParent(this);
        grabber.setDraggableY(true);
        grabber.setDragThreshold(0.0);
    }

    @Override
    protected void onMouseClicked(MouseClickedEvent e) {
        if (grabber.isInteractive()) {
            grabber.setIsDragging(true);
            grabber.setPressed(true);

            e.setIsHandled(true);
        }
    }

    @Override
    public void onMouseScrolled(MouseScrolledEvent e) {
        if (getAttachedControl() != null) {
            if (getAttachedControl().canScrollVertically()) {
                getAttachedControl().onMouseScrolled(e);
            }
        }
    }

    /**
     * @return the control the scrollbar is attached to.
     */
    public BaseControl getAttachedControl() {
        return attachedControl;
    }

    /**
     * Sets the control the scrollbar is attached to.
     *
     * @param control the control the scrollbar is attached to.
     */
    public void setAttachedControl(BaseControl control) {
        attachedControl = control;
    }
}
