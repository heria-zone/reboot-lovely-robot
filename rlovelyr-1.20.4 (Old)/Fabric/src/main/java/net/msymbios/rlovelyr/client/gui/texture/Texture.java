package net.msymbios.rlovelyr.client.gui.texture;

import net.minecraft.util.Identifier;

/**
 * A {@link Identifier} based gui texture.
 */
public class Texture {
    /**
     * The resource location of the texture.
     */
    public final Identifier resourceLocation;

    /**
     * The pixel x position of the texture inside the resource location.
     */
    public final int x;

    /**
     * The pixel y position of the texture inside the resource location.
     */
    public final int y;

    /**
     * The pixel width of the texture.
     */
    public final int width;

    /**
     * The pixel height of the texture.
     */
    public final int height;

    /**
     * @param resourceLocation the resource location of the texture.
     * @param x                the pixel x position of the texture inside the resource location.
     * @param y                the pixel y position of the texture inside the resource location.
     * @param width            the pixel width of the texture.
     * @param height           the pixel height of the texture.
     */
    public Texture(Identifier resourceLocation, int x, int y, int width, int height) {
        this.resourceLocation = resourceLocation;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Texture x(int x) {
        return new Texture(resourceLocation, x, y, width, height);
    }

    public Texture y(int y) {
        return new Texture(resourceLocation, x, y, width, height);
    }

    public Texture width(int width) {
        return new Texture(resourceLocation, x, y, width, height);
    }

    public Texture height(int height) {
        return new Texture(resourceLocation, x, y, width, height);
    }

    public Texture dx(int dx) {
        return new Texture(resourceLocation, x + dx, y, width, height);
    }

    public Texture dy(int dy) {
        return new Texture(resourceLocation, x, y + dy, width, height);
    }

    public Texture dWidth(int dWidth) {
        return new Texture(resourceLocation, x, y, width + dWidth, height);
    }

    public Texture dHeight(int dHeight) {
        return new Texture(resourceLocation, x, y, width, height + dHeight);
    }

} // Class Texture