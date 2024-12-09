package net.msymbios.rlovelyr.client.gui.control.controls;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.EditBox;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.msymbios.rlovelyr.client.gui.control.BaseControl;
import net.msymbios.rlovelyr.client.gui.control.Control;
import net.msymbios.rlovelyr.client.gui.control.event.events.SizeChangedEvent;
import net.msymbios.rlovelyr.client.gui.control.event.events.TextChangedEvent;
import net.msymbios.rlovelyr.client.gui.control.event.events.input.*;
import net.msymbios.rlovelyr.client.gui.util.GuiUtil;
import net.msymbios.rlovelyr.client.gui.util.ScissorStack;
import org.lwjgl.glfw.GLFW;

/**
 * A wrapper around a {@link EditBox}
 */
public class TextFieldControl extends Control {
    /**
     * Whether to render the text field's background.
     */
    private boolean shouldRenderBackground = true;

    /**
     * The underlying {@link EditBox};
     */
    protected final TextFieldWidget textFieldWidget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 100, GuiUtil.get().getLineHeight() - 1, Text.literal("message?"));

    /**
     * The amount to offset the text field's x position by.
     */
    private double textFieldXRemainder = 0.0;

    /**
     * The amount to offset the text field's y position by.
     */
    private double textFieldYRemainder = 0.0;

    /**
     * The colour of the text field's border.
     */
    private int borderColour = 0xffcccccc;

    /**
     * The colour of the text field's border when focused.
     */
    private int borderFocusedColour = 0xffffffff;

    /**
     *
     */
    public TextFieldControl() {
        super();

        textFieldWidget.setEditableColor(0xFFFFFFFF);
        textFieldWidget.setDrawsBackground(false);
        textFieldWidget.setMaxLength(32);
        textFieldWidget.setChangedListener(this::setText);

        eventBus.subscribe((BaseControl control, SizeChangedEvent e) -> {
            textFieldWidget.setCursorToStart(false);
        });

        setHorizontalContentAlignment(0.0);
        setVerticalContentAlignment(0.5);
        setPadding(6, 3, 6, 3);
        setHeight(20.0);
        setBackgroundColour(0xff111111);
    }

    /**
     * Recalculates the position of the {@link TextFieldControl#textFieldWidget} based on the position of this control.
     */
    private void recalcTextFieldWidget() {
        textFieldWidget.setWidth((int) Math.min(getPixelWidthWithoutPadding() / getGuiScale(), (getPixelWidthWithoutPadding() / getGuiScale())));

        double screenX = ((getPixelX() / getGuiScale()) + Math.max(0.0, getWidthWithoutPadding() - GuiUtil.get().getTextWidth(getText())) * getHorizontalContentAlignment()) + getPadding().left;
        double screenY = ((getPixelY() / getGuiScale()) + (getHeightWithoutPadding() - textFieldWidget.getHeight()) * getVerticalContentAlignment()) + getPadding().top;
        textFieldWidget.setX((int) screenX);
        textFieldWidget.setY((int) screenY);
        textFieldXRemainder = screenX - textFieldWidget.getX();
        textFieldYRemainder = screenY - textFieldWidget.getY();
    } // recalcTextFieldWidget ()

    @Override
    public void onRender(ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks) {
        // This would be better done using events, but there aren't currently events for when pixel sizes and positions change.
        recalcTextFieldWidget();

        super.onRender(scissorStack, mouseX, mouseY, partialTicks);

        if (shouldRenderBackground()) renderRectangleAsBackground(getBackgroundColourInt(), 1, 1, (int) (getWidth() - 2), (int) (getHeight() - 2));

        float z = isDraggingOrAncestor() ? (float) getDraggedControl().getDragZ() : (float) getRenderZ();

        //PoseStack textFieldPoseStack = new PoseStack();
        //textFieldPoseStack.translate(textFieldXRemainder, textFieldYRemainder, z);
        textFieldWidget.render(, (int) Math.round(mouseX), (int) Math.round(mouseY), partialTicks);
        RenderSystem.enableDepthTest();

        // Reset the color to white so that the rest of the GUI renders properly.
        // The text field renders a blue highlight which can cascade into other controls.
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        if (shouldRenderBackground) {
            int borderColour = isFocused() ? getBorderFocusedColour() : getBorderColour();
            renderRectangleAsBackground(borderColour, 0, 0, 1, getHeight());
            renderRectangleAsBackground(borderColour, getWidth() - 1, 0, 1, getHeight());
            renderRectangleAsBackground(borderColour, 0, 0, getWidth(), 1);
            renderRectangleAsBackground(borderColour, 0, getHeight() - 1, getWidth(), 1);
        }
    }

    @Override
    protected void onMouseClicked(MouseClickedEvent e) {
        double textFieldMouseX = e.mouseX / getGuiScale();
        double textFieldMouseY = e.mouseY / getGuiScale();
        boolean beforeStartOfText = textFieldMouseX < (double) textFieldWidget.getX();
        boolean afterEndOfText = textFieldMouseX > (double) (textFieldWidget.getX() + textFieldWidget.getWidth());

        textFieldWidget.mouseClicked(textFieldMouseX, textFieldMouseY, e.button);

        if (beforeStartOfText) {
            textFieldWidget.setCursorToStart(false);
            textFieldWidget.setFocused(true);
        } else if (afterEndOfText) {
            textFieldWidget.setCursorToEnd(false);
            textFieldWidget.setFocused(true);
        }

        e.setIsHandled(true);
    }

    @Override
    public void onMouseReleased(MouseReleasedEvent e) {
        textFieldWidget.mouseReleased(e.mouseX / getGuiScale(), e.mouseY / getGuiScale(), e.button);
        e.setIsHandled(true);
    }

    @Override
    public void onKeyPressed(KeyPressedEvent e) {
        if (GuiUtil.get().isUnfocusTextFieldKey(e.keyCode)) {
            if (getScreen() != null) {
                getScreen().setFocusedControl(null);
            }

            e.setIsHandled(true);
        } else {
            e.setIsHandled(textFieldWidget.keyPressed(e.keyCode, e.scanCode, e.modifiers) || textFieldWidget.isFocused());
        }
    }

    @Override
    public void onKeyReleased(KeyReleasedEvent e) {
        e.setIsHandled(textFieldWidget.keyReleased(e.keyCode, e.scanCode, e.modifiers));
    }

    @Override
    public void onCharTyped(CharTypedEvent e) {
        e.setIsHandled(textFieldWidget.charTyped(e.character, e.keyCode));
    }

    @Override
    public void onFocused() {
        textFieldWidget.setFocused(true);
    }

    @Override
    public void onUnfocused() {
        // Send a fake key input to clear the shiftPressed flag.
        onKeyPressed(new KeyPressedEvent(GLFW.GLFW_KEY_LEFT_CONTROL, 0, 0));
        textFieldWidget.setFocused(false);
        textFieldWidget.setCursor(0, false);
        textFieldWidget.setSelectionEnd(0);
    }

    @Override
    public Double getHorizontalContentAlignment() {
        return super.getHorizontalContentAlignment() != null ? super.getHorizontalContentAlignment() : 0.5;
    }

    @Override
    public Double getVerticalContentAlignment() {
        return super.getVerticalContentAlignment() != null ? super.getVerticalContentAlignment() : 0.5;
    }

    /**
     * @param text the text to validate.
     * @return whether the text is valid.
     */
    protected boolean isValidText(String text) {
        return true;
    }

    /**
     * Processes the text before it is set.
     */
    protected String processText(String text) {
        return text;
    }

    public String getText() {
        return textFieldWidget.getText();
    }

    public void setText(String text) {
        text = processText(text);
        String oldText = textFieldWidget.getText();
        if (!oldText.equals(text)) {
            textFieldWidget.setText(text);
            textFieldWidget.setCursorToStart(false);
        }
        eventBus.post(this, new TextChangedEvent(oldText, text));
    }

    public void setText(Text text) {
        setText(text.getLiteralString());
    }

    /**
     * Sets the max text length for the text field.
     */
    public void setMaxTextLength(int maxTextLength) {
        textFieldWidget.setMaxLength(maxTextLength);
    }

    /**
     * @return whether to render the text field's background.
     */
    public boolean shouldRenderBackground() {
        return shouldRenderBackground;
    }

    /**
     * Sets whether to render the text field's background.
     */
    public void setShouldRenderBackground(boolean shouldRenderBackground) {
        this.shouldRenderBackground = shouldRenderBackground;
    }

    /**
     * @return the text field's border colour.
     */
    public int getBorderColour() {
        return borderColour;
    }

    /**
     * Sets the text field's border colour.
     */
    public void setBorderColour(int borderColour) {
        this.borderColour = borderColour;
    }

    /**
     * @return the text field's border colour when focused.
     */
    public int getBorderFocusedColour() {
        return borderFocusedColour;
    }

    /**
     * Sets the text field's border colour when focused.
     */
    public void setBorderFocusedColour(int borderFocusedColour) {
        this.borderFocusedColour = borderFocusedColour;
    }
}