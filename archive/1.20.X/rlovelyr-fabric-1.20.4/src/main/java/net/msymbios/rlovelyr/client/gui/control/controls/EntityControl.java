package net.msymbios.rlovelyr.client.gui.control.controls;

import net.minecraft.world.entity.LivingEntity;
import net.msymbios.rlovelyr.client.gui.control.Control;
import net.msymbios.rlovelyr.client.gui.util.GuiUtil;
import net.msymbios.rlovelyr.client.gui.util.ScissorStack;

/**
 * A control used to display an entity.
 */
public class EntityControl extends Control {
    
    /**
     * The entity to display.
     */
    private LivingEntity entity;

    /**
     * The offset to display the entity at in the x-axis. Effected by {@link #entityScale}.
     * An offset of 1.0f at a scale of 1.0f would shift the entity by 1/16 of a block.
     */
    private float offsetX = 0.0f;

    /**
     * The offset to display the entity at in the y-axis. Effected by {@link #entityScale}.
     * An offset of 1.0f at a scale of 1.0f would shift the entity by 1/16 of a block.
     */
    private float offsetY = 0.0f;

    /**
     * The scale of the entity (1.0f means a block will fit the control).
     */
    private float entityScale = 1.0f;

    /**
     * The x-coordinate to look at. If null, the entity will look at the mouse.
     */
    private Float lookX = null;

    /**
     * The y-coordinate to look at. If null, the entity will look at the mouse.
     */
    private Float lookY = null;

    /**
     * Whether to scale the entity up/down based on its bounding box. Useful for rendering all entities at a
     * similar relative scale.
     */
    private boolean scaleToBoundingBox = true;

    @Override
    public void onRender(ScissorStack scissorStack, double mouseX, double mouseY, float partialTicks) {
        super.onRender(scissorStack, mouseX, mouseY, partialTicks);

        double minDimension = Math.min(getWidth() * getScaleX(), getHeight() * getScaleY());
        double scale = getEntityScale() * minDimension / 16.0f;
        double x = ((getPixelX() + getPixelWidth() / 2) / getGuiScale()) + getOffsetX() * scale;
        double y = ((getPixelY() + getPixelHeight()) / getGuiScale()) + getOffsetY() * scale;
        float lookX = getLookX() != null ? getLookX() : (float) (mouseX / getGuiScale());
        float lookY = getLookY() != null ? getLookY() : (float) (mouseY / getGuiScale());

        GuiUtil.get().renderEntityOnScreen(entity, (int) x, (int) y, lookX, lookY, (float) scale, shouldScaleToBoundingBox());
    }

    /**
     * @return the entity to display.
     */
    public LivingEntity getEntity() {
        return entity;
    }

    /**
     * Sets the entity to display.
     */
    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    /**
     * Gets entity scale.
     */
    public float getEntityScale() {
        return entityScale;
    }

    /**
     * Sets entity scale.
     */
    public void setEntityScale(float entityScale) {
        this.entityScale = entityScale;
    }

    /**
     * Gets the entity offset in the x-axis.
     */
    public float getOffsetX() {
        return offsetX;
    }

    /**
     * Sets the entity offset in the x-axis.
     */
    public void setOffsetX(float offset) {
        this.offsetX = offset;
    }

    /**
     * Gets the entity offset in the y-axis.
     */
    public float getOffsetY() {
        return offsetY;
    }

    /**
     * Sets the entity offset in the y-axis.
     */
    public void setOffsetY(float offset) {
        this.offsetY = offset;
    }

    /**
     * @return whether to scale the entity relative to its bounding box.
     */
    public boolean shouldScaleToBoundingBox() {
        return scaleToBoundingBox;
    }

    /**
     * Sets whether to scale the entity relative to its bounding box.
     */
    public void setScaleToBoundingBox(boolean scaleToBoundingBox) {
        this.scaleToBoundingBox = scaleToBoundingBox;
    }

    /**
     * @return the x-coordinate to look at. If null, the entity will look at the mouse.
     */
    public Float getLookX() {
        return lookX;
    }

    /**
     * Sets the x-coordinate to look at. If null, the entity will look at the mouse.
     */
    public void setLookX(Float lookX) {
        this.lookX = lookX;
    }

    /**
     * @return the y-coordinate to look at. If null, the entity will look at the mouse.
     */
    public Float getLookY() {
        return lookY;
    }

    /**
     * Sets the y-coordinate to look at. If null, the entity will look at the mouse.
     */
    public void setLookY(Float lookY) {
        this.lookY = lookY;
    }
}
