package net.msymbios.rlovelyr.client.gui.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.client.gui.texture.Texture;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * Contains fully implemented methods for {@link GuiUtil}.
 */
public class FullGuiUtil extends GuiUtil {
    
    /**
     * The instance of {@link MinecraftClient }.
     */
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public float getGuiScale() {
        return (float) mc.getWindow().getScaleFactor();
    }

    @Override
    public float getMaxGuiScale() {
        return (float) mc.getWindow().calculateScaleFactor(0, mc.forcesUnicodeFont());
    }

    @Override
    public int getPixelMouseX() {
        return (int) mc.mouse.getX();
    }

    @Override
    public int getPixelMouseY() {
        return (int) mc.mouse.getY();
    }

    @Override
    public boolean isKeyDown(int key) {
        return InputUtil.isKeyPressed(mc.getWindow().getHandle(), key);
    }

    //@Override
    public boolean isKeyDown(KeyBinding key) {
        return isKeyDown(key.getDefaultKey().getCode());
    }

    @Override
    public boolean isControlKeyDown() {
        return isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL) || isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL);
    }

    @Override
    public boolean isCrouchKeyDown() {
        return isKeyDown(mc.options.sneakKey.getDefaultKey().getCode());
    }

    @Override
    public boolean isCloseKey(int key) {
        return key == GLFW.GLFW_KEY_ESCAPE || key == mc.options.inventoryKey.getDefaultKey().getCode();
    }

    @Override
    public boolean isUnfocusTextFieldKey(int key) {
        return key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER;
    }

    /*@Override
    public OrderedText trimWithEllipsis(OrderedText text, int width) {
        if (text.getString().equals(trim(text, width).getString())) {
            return text.wrapLines(TextRenderer.getWidth(text.getString(), mc.textRenderer.getWidth(text.getString())));
        } else {
            return OrderedText.join(trim(text, width - mc.textRenderer.getWidth("...")), Text.literal("..."));
        }
    }*/

    @Override
    public OrderedText trim(OrderedText  text, int width) {
        return mc.textRenderer.trimToWidth(text, width);
    }

    //@Override
    /*public List<OrderedText> split(OrderedText text, int width) {
        //return new ArrayList<>(mc.textRenderer.wrapLines(text, width));
    }*/

    @Override
    public List<String> split(String text, int width) {
        //return mc.textRenderer.wrapLines(text, width).stream().map(OrderedText::getString).collect(Collectors.toList());
    }

    @Override
    public int getTextWidth(String text) {
        return mc.textRenderer.getWidth(text);
    }

    //@Override
    public int getTextWidth(OrderedText  text) {
        return mc.textRenderer.getWidth(text);
    }

    @Override
    public int getLineHeight() {
        return mc.textRenderer.fontHeight;
    }

    //@Override
    public void renderShadowedText(MatrixStack matrices, OrderedText text, int x, int y, int color) {
        //mc.textRenderer.draw(text, x, y, color, true, matrices, new VertexConsumerProvider (), TextRenderer.TextLayerType.NORMAL, 0, 0);
    }

    //@Override
    public void renderText(MatrixStack matrices, OrderedText text, int x, int y, int color) {
        //mc.textRenderer.draw(text, x, y, color, false, matrices, new VertexConsumerProvider (), TextRenderer.TextLayerType.NORMAL, 0, 0);
    }

    @Override
    public void bindTexture(Identifier texture) {
        mc.getTextureManager().bindTexture(texture);
    }

    @Override
    public void bindTexture(Texture texture) {
        bindTexture(texture.resourceLocation);
    }

}
