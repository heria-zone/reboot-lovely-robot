package net.msymbios.rlovelyr.client.gui.screen.screens;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.msymbios.rlovelyr.client.gui.control.Control;
import net.msymbios.rlovelyr.client.gui.control.controls.TabbedUIControl;
import net.msymbios.rlovelyr.client.gui.screen.BlocklingsScreen;
import net.msymbios.rlovelyr.entity.internal.InterfaceEntity;

/**
 * A base screen for all tabbed blockling screens.
 */
@Environment(EnvType.CLIENT)
public abstract class TabbedScreen extends BlocklingsScreen {

    // -- Variables --

    /**
     * The tabbed UI control.
     */
    protected final TabbedUIControl tabbedUIControl;

    // -- Constructors --

    /**
     * @param blockling   the blockling associated with the screen.
     * @param selectedTab the tab to select when the screen is opened.
     */
    public TabbedScreen(InterfaceEntity blockling, TabbedUIControl.Tab selectedTab) {
        super(blockling);

        Control background = new Control();
        background.setParent(screenControl);
        background.setInteractive(false);
        background.setWidthPercentage(1.0);
        background.setHeightPercentage(1.0);
        background.setBackgroundColour(0xaa000000);
        background.setDebugName("Screen Background");

        tabbedUIControl = new TabbedUIControl(blockling, selectedTab);
        tabbedUIControl.setParent(screenControl);
    } // Constructor TabbedScreen ()

    @Override
    public boolean shouldPause() {
        return false;
    }

} // Class TabbedScreen