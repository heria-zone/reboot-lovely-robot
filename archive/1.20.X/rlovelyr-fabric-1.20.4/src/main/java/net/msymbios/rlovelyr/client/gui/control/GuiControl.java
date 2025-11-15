package net.msymbios.rlovelyr.client.gui.control;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.msymbios.rlovelyr.client.gui.texture.Texture;
import net.msymbios.rlovelyr.client.gui.util.GuiUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class GuiControl implements HudRenderingEntrypoint {

    // -- Variables --

    private static final Minecraft mc = Minecraft.getInstance();

    // -- Methods --

    /**
     * Renders a rectangle with a solid colour at the given position with the size.
     *
     * @param x         the x position to render at.
     * @param y         the y position to render at.
     * @param width     the width of the rectangle.
     * @param height    the height of the rectangle.
     * @param colour    the colour of the rectangle.
     */
    public void renderRectangle(double x, double y, int width, int height, int colour) {
        DrawableHelper.fill(matrices, (int) Math.round(x), (int) Math.round(y), (int) Math.round(x + width), (int) Math.round(y + height), colour);
    } // renderRectangle ()

    /**
     * Renders a rectangle with the given width and height centered on the given position.
     *
     * @param x         the x position to render at.
     * @param y         the y position to render at.
     * @param width     the width of the rectangle.
     * @param height    the height of the rectangle.
     * @param colour    the colour of the rectangle.
     */
    public void renderCenteredRectangle(double x, double y, double width, double height, int colour) {
        DrawableHelper.fill(matrices, (int) Math.round(x - width / 2.0), (int) Math.round(y - height / 2.0), (int) Math.round(x + width / 2.0), (int) Math.round(y + height / 2.0), colour);
    } // renderCenteredRectangle ()

    /**
     * Renders a texture using the given matrix stack.
     *
     * @param texture   the texture to render.
     */
    protected void renderTexture(Texture texture) {
        GuiUtil.get().bindTexture(texture.resourceLocation);
        DrawableHelper.drawTexture(matrices, 0, 0, texture.x, texture.y, texture.width, texture.height, texture.width, texture.height);
    } // renderTexture ()

    /**
     * Renders a texture at the given pixel position at the given scale.
     *
     * @param texture   the texture to render.
     * @param x         the x position to render at.
     * @param y         the y position to render at.
     * @param scaleX    the x scale to render at.
     * @param scaleY    the y scale to render at.
     */
    protected void renderTexture(Texture texture, double x, double y, double scaleX, double scaleY) {
        matrices.push();
        matrices.translate((int) Math.round(x), (int) Math.round(y), 0.0);
        matrices.scale((float) scaleX, (float) scaleY, 1.0f);
        renderTexture(texture);
        matrices.pop();
    } // renderTexture ()

    /**
     * Renders shadowed text using the given matrix stack.
     *
     * @param text      the text to render.
     * @param colour    the colour of the text.
     */
    protected void renderShadowedText(Component text, int colour) {
        DrawableHelper.drawTextWithShadow(matrices, mc.textRenderer, text, 0, 0, colour);
    } // renderShadowedText ()

    /**
     * Renders shadowed text using the given matrix stack and coordinates.
     *
     * @param text      the text to render.
     * @param colour    the colour of the text.
     */
    protected void renderShadowedText(OrderedText text, int x, int y, int colour) {
        DrawableHelper.drawTextWithShadow(matrices, mc.textRenderer, text, x, y, colour);
    } // renderShadowedText ()

    /**
     * Renders text using the given matrix stack.
     *
     * @param text      the text to render.
     * @param colour    the colour of the text.
     */
    protected void renderText(Component text, int colour) {
        mc.textRenderer.draw(text, 0, 0, colour, false, matrices.peek().getModel(), VertexConsumerProvider.INSTANCE.getBuffer(mc.textRenderer.getTexturedRenderType()), false, 0, 15728880);
    } // renderText ()

    /**
     * Renders a tooltip at the mouse position.
     *
     * @param mouseX      the mouse x position.
     * @param mouseY      the mouse y position.
     * @param pixelScaleX the x pixel scale.
     * @param pixelScaleY the y pixel scale.
     * @param tooltip     the tooltip to render.
     */
    public void renderTooltip(double mouseX, double mouseY, double pixelScaleX, double pixelScaleY, Component tooltip) {
        List<Component> tooltip2 = new ArrayList<>();
        tooltip2.add(tooltip);
        renderTooltip(mouseX, mouseY, pixelScaleX, pixelScaleY, tooltip2);
    } // renderTooltip ()

    /**
     * Renders a tooltip at the mouse position.
     *
     * @param mouseX      the mouse x position.
     * @param mouseY      the mouse y position.
     * @param pixelScaleX the x pixel scale.
     * @param pixelScaleY the y pixel scale.
     * @param tooltip     the tooltip to render.
     */
    public void renderTooltip(double mouseX, double mouseY, double pixelScaleX, double pixelScaleY, List<Component> tooltip) {
        mc.screen.renderWithTooltip(matrices, tooltip, (int) (mouseX / pixelScaleX), (int) (mouseY / pixelScaleY));
    } // renderTooltip ()

} // Class GuiControl