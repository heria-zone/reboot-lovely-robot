package net.msymbios.rlovelyr.client.gui.control.controls;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.text.Text;
import net.msymbios.rlovelyr.client.gui.control.Control;
import net.msymbios.rlovelyr.client.gui.control.controls.panels.*;
import net.msymbios.rlovelyr.client.gui.control.event.events.*;
import net.msymbios.rlovelyr.client.gui.control.event.events.input.MouseReleasedEvent;
import net.msymbios.rlovelyr.client.gui.properties.enums.*;
import net.msymbios.rlovelyr.client.gui.texture.Texture;
import net.msymbios.rlovelyr.client.gui.texture.Textures;
import net.msymbios.rlovelyr.client.gui.util.ScissorStack;

import java.util.ArrayList;
import java.util.List;

/**
 * A control that allows the user to select a single option from a set.
 */
public class SingleSelectorStrip<O> extends Control {
    
    /**
     * The options that the user can select from.
     */
    private List<O> options = new ArrayList<>();

    /**
     * The index of the currently selected option.
     */
    private int selectedIndex = 0;

    /**
     *
     */
    public SingleSelectorStrip() {
        super();

        setFitHeightToContent(true);
    }

    /**
     * Recreates the strip.
     */
    private void recreate() {
        clearChildren();

        GridPanel strip = new GridPanel();
        strip.setParent(this);
        strip.setWidthPercentage(1.0);
        strip.setFitHeightToContent(true);
        strip.addRowDefinition(GridDefinition.AUTO, 1.0);

        for (int i = 0; i < getOptions().size(); i++) {
            strip.addColumnDefinition(GridDefinition.RATIO, 1.0);

            O option = getOptions().get(i);

            OptionControl optionControl = new OptionControl(option);
            strip.addChild(optionControl, 0, i);
            optionControl.setWidthPercentage(1.0);
        }
    }

    /**
     * @return the options that the user can select from.
     */
    public List<O> getOptions() {
        return options;
    }

    /**
     * Sets the options that the user can select from.
     *
     * @param options the options that the user can select from.
     */
    public void setOptions(List<O> options) {
        this.options = options;

        recreate();
    }

    /**
     * @return the currently selected option.
     */
    public O getSelectedOption() {
        return getOptions().get(getSelectedIndex());
    }

    /**
     * Sets the currently selected option.
     *
     * @param option the currently selected option.
     */
    public void setSelectedOption(O option) {
        for (int i = 0; i < getOptions().size(); i++) {
            if (getOptions().get(i).equals(option)) {
                setSelectedIndex(i);

                return;
            }
        }
    }

    /**
     * @return the index of the currently selected option.
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Sets the index of the currently selected option.
     *
     * @param selectedIndex the index of the currently selected option.
     */
    public void setSelectedIndex(int selectedIndex) {
        O previousOption = getSelectedOption();

        this.selectedIndex = selectedIndex;

        eventBus.post(this, new SelectionChangedEvent<>(previousOption, getSelectedOption()));
    }

    private class OptionControl extends TexturedControl {
        /**
         * The option that this control represents.
         */
            public final O option;

        /**
         * @param option the option that this control represents.
         */
        public OptionControl(O option) {
            super(Textures.Common.BAR_RAISED, Textures.Common.BAR_FLAT);
            this.option = option;

            TextBlockControl textBlockControl = new TextBlockControl();
            textBlockControl.setParent(this);
            textBlockControl.setText(option.toString());
            textBlockControl.setHorizontalContentAlignment(0.5);
            textBlockControl.setVerticalAlignment(0.5);
            textBlockControl.setWidthPercentage(1.0);
            textBlockControl.setMargins(4.0);
        }

        @Override
        protected void onRender( ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks) {
            Texture texture = isSelected() ? getPressedBackgroundTexture() : getBackgroundTexture();

            if (isHovered()) {
                RenderSystem.setShaderColor(0.7f, 0.9f, 1.0f, 1.0f);
            }

            renderTextureAsBackground(texture);

            if (isLast()) {
                renderTextureAsBackground(texture.x(texture.width - 2).width(2), getWidth() - 2, 0);
            } else {
                renderTextureAsBackground(texture.x(texture.width - 2).width(1), getWidth() - 1, 0);
            }
        }

        @Override
        public void onRenderTooltip( double mouseX, double mouseY, float partialTicks) {
            renderTooltip(mouseX, mouseY, Text.literal(option.toString()));
        }

        @Override
        protected void onMouseReleased(MouseReleasedEvent e) {
            if (isPressed() && !isSelected()) {
                select();
            }
        }

        /**
         * @return whether this option is selected.
         */
        public boolean isSelected() {
            return getOptions().indexOf(option) == selectedIndex;
        }

        /**
         * Selects this option.
         */
        public void select() {
            setSelectedIndex(getOptions().indexOf(option));
        }

        /**
         * @return whether this option is the last in the list.
         */
        public boolean isLast() {
            return getOptions().indexOf(option) == getOptions().size() - 1;
        }
    }
}
