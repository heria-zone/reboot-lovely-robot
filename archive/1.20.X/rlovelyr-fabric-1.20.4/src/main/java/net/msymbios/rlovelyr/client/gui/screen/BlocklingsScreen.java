package net.msymbios.rlovelyr.client.gui.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.msymbios.rlovelyr.client.gui.control.controls.ScreenControl;
import net.msymbios.rlovelyr.client.gui.control.event.events.input.*;
import net.msymbios.rlovelyr.client.gui.util.GuiUtil;
import net.msymbios.rlovelyr.entity.internal.InterfaceEntity;

/**
 * A base screen to provide an adapter for {@link Screen}.
 */
public class BlocklingsScreen extends Screen {

    // -- Variables --

    /**
     * The blockling associated with the screen.
     */
    public final InterfaceEntity blockling;

    /**
     * The root control that contains all the sub controls on the screen.
     */
    public final ScreenControl screenControl = new ScreenControl();

    // -- Constructors --

    /**
     * @param blockling the blockling associated with the screen.
     */
    protected BlocklingsScreen(InterfaceEntity blockling) {
        super(Component.empty());
        this.blockling = blockling;
    } // Constructor BlocklingsScreen ()

    // -- Inherited Methods --

    @Override
    protected void init() {
        super.init();
        screenControl.setWidth(width);
        screenControl.setHeight(height);
        screenControl.markMeasureDirty(true);
        screenControl.markArrangeDirty(true);
    } // init ()

    @Override
    public void close() {
        super.close();
        screenControl.forwardClose(screenControl.shouldReallyClose());
    } // onClose ()

    @Override
    public void tick() {
        screenControl.forwardTick();
        super.tick();
    } // tick ()

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        screenControl.render(mouseX, mouseY, partialTicks);
    } // render ()

    @Override
    public boolean mouseClicked(double screenMouseX, double screenMouseY, int button) {
        double mouseX = GuiUtil.get().getPixelMouseX();
        double mouseY = GuiUtil.get().getPixelMouseY();

        MouseClickedEvent e = new MouseClickedEvent(mouseX, mouseY, button);
        screenControl.forwardMouseClicked(e);

        if (e.isHandled() || super.mouseClicked(screenMouseX, screenMouseY, button)) {
            return true;
        } else {
            screenControl.setPressed(true);
            screenControl.setFocused(true);

            return false;
        }
    } // mouseClicked ()

    @Override
    public boolean mouseReleased(double screenMouseX, double screenMouseY, int button) {
        double mouseX = GuiUtil.get().getPixelMouseX();
        double mouseY = GuiUtil.get().getPixelMouseY();
        MouseReleasedEvent e = new MouseReleasedEvent(mouseX, mouseY, button);
        screenControl.forwardMouseReleased(e);
        return e.isHandled() || super.mouseReleased(screenMouseX, screenMouseY, button);
    } // mouseReleased ()

    @Override
    public boolean mouseScrolled(double screenMouseX, double screenMouseY, double horizontalAmount, double amount) {
        double mouseX = GuiUtil.get().getPixelMouseX();
        double mouseY = GuiUtil.get().getPixelMouseY();
        MouseScrolledEvent e = new MouseScrolledEvent(mouseX, mouseY, horizontalAmount, amount);
        screenControl.forwardMouseScrolled(e);
        return e.isHandled() || super.mouseScrolled(screenMouseX, screenMouseY, horizontalAmount, amount);
    } // mouseScrolled ()

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        KeyPressedEvent e = new KeyPressedEvent(keyCode, scanCode, modifiers);
        screenControl.forwardGlobalKeyPressed(e);

        if (e.isHandled() || super.keyPressed(keyCode, scanCode, modifiers)) return true;

        if (GuiUtil.get().isCloseKey(keyCode)) {
            close();
            return true;
        }

        return false;
    } // keyPressed ()

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        KeyReleasedEvent e = new KeyReleasedEvent(keyCode, scanCode, modifiers);
        screenControl.forwardGlobalKeyReleased(e);
        return e.isHandled() || super.keyReleased(keyCode, scanCode, modifiers);
    } // keyReleased ()

    @Override
    public boolean charTyped(char character, int modifiers) {
        CharTypedEvent e = new CharTypedEvent(character, modifiers);
        screenControl.forwardGlobalCharTyped(e);
        return e.isHandled() || super.charTyped(character, modifiers);
    } // charTyped ()

} // Class BlocklingsScreen