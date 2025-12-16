package net.heriazone.lovelylib.api.commands;

import com.google.gson.JsonObject;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Serializer for ColorArgumentType to enable client-server synchronization.
 * <p>
 * <b>Architecture:</b> Implements ArgumentTypeInfo to allow Minecraft's command
 * system to serialize and deserialize ColorArgumentType across network boundaries.
 * <p>
 * <b>Design Decision:</b> ColorArgumentType has no parameters, so serialization
 * is trivial - we just create new instances without any data transfer.
 * <p>
 * <b>Network Protocol:</b> Required for custom argument types to work in multiplayer
 * environments where commands are sent from client to server.
 */
public class ColorArgumentTypeInfo implements ArgumentTypeInfo<ColorArgumentType, ColorArgumentTypeInfo.Template> {

    /**
     * Serializes argument type to JSON for data pack compatibility.
     * <p>
     * <b>Behavior:</b> ColorArgumentType has no parameters, so returns empty JSON object.
     *
     * @param template the argument type template
     * @param json the JSON object to populate
     */
    @Override
    public void serializeToJson(Template template, JsonObject json) {
        // No parameters to serialize
    } // serializeToJson()

    /**
     * Deserializes argument type from network buffer.
     * <p>
     * <b>Behavior:</b> ColorArgumentType has no parameters, so just creates new template.
     *
     * @param buffer the network buffer (unused)
     * @return new template instance
     */
    @Override
    public Template deserializeFromNetwork(FriendlyByteBuf buffer) {
        return new Template();
    } // deserializeFromNetwork()

    /**
     * Serializes argument type to network buffer.
     * <p>
     * <b>Behavior:</b> ColorArgumentType has no parameters, so nothing to write.
     *
     * @param template the argument type template
     * @param buffer the network buffer (unused)
     */
    @Override
    public void serializeToNetwork(Template template, FriendlyByteBuf buffer) {
        // No parameters to serialize
    } // serializeToNetwork()

    /**
     * Unwraps template to get the actual argument type instance.
     * <p>
     * <b>Behavior:</b> Creates new ColorArgumentType instance.
     *
     * @param template the argument type template
     * @return new ColorArgumentType instance
     */
    @Override
    public Template unpack(ColorArgumentType template) {
        return new Template();
    } // unpack()

    /**
     * Template class for ColorArgumentType serialization.
     * <p>
     * <b>Architecture:</b> Acts as a serializable wrapper around ColorArgumentType.
     * Since ColorArgumentType has no parameters, this is essentially a marker class.
     */
    public class Template implements ArgumentTypeInfo.Template<ColorArgumentType> {

        /**
         * Instantiates the actual argument type from this template.
         * <p>
         * <b>Behavior:</b> Creates new ColorArgumentType instance.
         *
         * @param context the argument type info (unused)
         * @return new ColorArgumentType instance
         */
        @Override
        public ColorArgumentType instantiate(CommandBuildContext context) {
            return ColorArgumentType.color();
        } // instantiate()

        /**
         * Gets the argument type info for this template.
         * <p>
         * <b>Behavior:</b> Returns the parent ColorArgumentTypeInfo instance.
         *
         * @return the argument type info
         */
        @Override
        public ArgumentTypeInfo<ColorArgumentType, ?> type() {
            return ColorArgumentTypeInfo.this;
        } // type()

    } // Class: Template

} // Class: ColorArgumentTypeInfo