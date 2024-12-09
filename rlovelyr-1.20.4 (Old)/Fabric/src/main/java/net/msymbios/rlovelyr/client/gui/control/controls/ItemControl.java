package net.msymbios.rlovelyr.client.gui.control.controls;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.msymbios.rlovelyr.client.gui.control.Control;
import net.msymbios.rlovelyr.client.gui.util.GuiUtil;
import net.msymbios.rlovelyr.client.gui.util.ScissorStack;

/**
 * A control used to display an item.
 */
public class ItemControl extends Control {
    
    /**
     * The item to display.
     */
    private ItemStack itemStack = ItemStack.EMPTY;

    /**
     * The scale of the item.
     */
    private float itemScale = 1.0f;

    /**
     *
     */
    public ItemControl() {
        super();
    }

    @Override
    protected void onRender(ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks) {
        super.onRender(scissorStack, mouseX, mouseY, partialTicks);

        float scale = (float) (Math.min(getPixelWidth(), getPixelHeight()) / getGuiScale()) * getItemScale();
        int x = (int) ((getPixelX() + getPixelWidth() / 2.0) / getGuiScale());
        int y = (int) ((getPixelY() + getPixelHeight() / 2.0) / getGuiScale());

        float z = (float) (isDraggingOrAncestor() ? getDraggedControl().getDragZ() : getRenderZ());

        try {
            // For some reason we can't just access the values in the matrix.
            // So we have to get the z translation via reflection. Nice.
            //z = ObfuscationReflectionHelper.getPrivateValue(Matrix4f.class, poseStack.last().pose(), "f_27614_");
        } catch (Exception ex) {
//            Blocklings.LOGGER.warn(ex.toString());
        }

        GuiUtil.get().renderItemStack(itemStack, x, y, z, scale);

        if (getForegroundColourInt() != 0xffffffff) {
            //poseStack.pushPose();
            //poseStack.translate(0.0, 0.0, 16.0);
            renderRectangleAsBackground(getForegroundColourInt());
            //poseStack.popPose();
        }
    }

    /**
     * @return the item to display.
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Sets the item to display.
     *
     * @param itemStack the item to display.
     */
    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    /**
     * @return the item to display.
     */
    public Item getItem() {
        return itemStack.getItem();
    }

    /**
     * Sets the item to display.
     *
     * @param item the item to display.
     */
    public void setItem(Item item) {
        setItemStack(new ItemStack(item));
    }

    /**
     * @return the scale of the item.
     */
    public float getItemScale() {
        return itemScale;
    }

    /**
     * Sets the scale of the item.
     *
     * @param itemScale the scale of the item.
     */
    public void setItemScale(float itemScale) {
        this.itemScale = itemScale;
    }
}
