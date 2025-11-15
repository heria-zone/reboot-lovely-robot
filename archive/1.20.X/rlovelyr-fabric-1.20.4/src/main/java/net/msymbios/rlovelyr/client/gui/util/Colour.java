package net.msymbios.rlovelyr.client.gui.util;

import com.mojang.blaze3d.systems.RenderSystem;

import java.awt.*;

/**
 * A class to represent a colour.
 */
public class Colour {

    // -- Variables --

    /**
     * The red component of the colour.
     */
    private float r;

    /**
     * The green component of the colour.
     */
    private float g;

    /**
     * The blue component of the colour.
     */
    private float b;

    /**
     * The alpha component of the colour.
     */
    private float a;

    // -- Constructors --

    /**
     * @param colour the colour.
     */
    public Colour(Color colour) {
        setColour(colour);
    }

    /**
     * @param colour the argb colour.
     */
    public Colour(int colour) {
        this((colour >> 16) & 0x000000ff, (colour >> 8) & 0x000000ff, colour & 0x000000ff, (colour >> 24) & 0x000000ff);
    }

    // -- Methods --

    /**
     * @return returns a {@link Colour} from an int colour code.
     */
    public static Colour fromRGBInt(int rgb) {
        return new Colour(0xff000000 + rgb);
    }

    /**
     * @return returns a {@link Colour} from an int colour code.
     */
    public static Colour fromARGBInt(int argb) {
        return new Colour(argb);
    }

    /**
     * @param r the red value.
     * @param g the green value.
     * @param b the blue value.
     */
    public Colour(float r, float g, float b) {
        this(r, g, b, 1.0f);
    }

    /**
     * @param r the red value.
     * @param g the green value.
     * @param b the blue value.
     * @param a the alpha value.
     */
    public Colour(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    /**
     * @param r the red value.
     * @param g the green value.
     * @param b the blue value.
     */
    public Colour(int r, int g, int b) {
        this(r, g, b, 255);
    }

    /**
     * @param r the red value.
     * @param g the green value.
     * @param b the blue value.
     * @param a the alpha value.
     */
    public Colour(int r, int g, int b, int a) {
        this(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
    }

    /**
     * Applies the colour to the render system.
     */
    public void apply() {
        RenderSystem.setShaderColor(r, g, b, a);
    }

    /**
     * Sets the colour.
     *
     * @param colour the colour.
     */
    public void setColour(Color colour) {
        r = colour.getRed() / 255.0f;
        g = colour.getGreen() / 255.0f;
        b = colour.getBlue() / 255.0f;
        a = colour.getAlpha() / 255.0f;
    }

    /**
     * Gets the red value.
     *
     * @return the red value.
     */
    public float getR() {
        return r;
    }

    /**
     * Gets the green value.
     *
     * @return the green value.
     */
    public float getG() {
        return g;
    }

    /**
     * Gets the blue value.
     *
     * @return the blue value.
     */
    public float getB() {
        return b;
    }

    /**
     * Gets the alpha value.
     *
     * @return the alpha value.
     */
    public float getA() {
        return a;
    }

    /**
     * Sets the red value.
     *
     * @param r the red value.
     */
    public void setR(float r) {
        this.r = r;
    }

    /**
     * Sets the green value.
     *
     * @param g the green value.
     */
    public void setG(float g) {
        this.g = g;
    }

    /**
     * Sets the blue value.
     *
     * @param b the blue value.
     */
    public void setB(float b) {
        this.b = b;
    }

    /**
     * Sets the alpha value.
     *
     * @param a the alpha value.
     */
    public void setA(float a) {
        this.a = a;
    }

    /**
     * @return the red value as an int.
     */
    public int r() {
        return (int) (r * 255);
    }

    /**
     * @return the green value as an int.
     */
    public int g() {
        return (int) (g * 255);
    }

    /**
     * @return the blue value as an int.
     */
    public int b() {
        return (int) (b * 255);
    }

    /**
     * @return the alpha value as an int.
     */
    public int a() {
        return (int) (a * 255);
    }

    /**
     * @return the colour as an integer.
     */
    public int argb() {
        return (a() << 24) + (r() << 16) + (g() << 8) + (b());
    }

} // Class Colour