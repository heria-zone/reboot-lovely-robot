package net.msymbios.llovelyr.common.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.network.chat.Component;
import net.msymbios.llovelyr.framework.entity.enums.EntityTexture;

import java.util.concurrent.CompletableFuture;

/**
 * Custom argument type for robot color/texture selection in commands.
 * <p>
 * <b>Architecture:</b> Bridges Brigadier command system with EntityTexture enum,
 * enabling type-safe color selection with autocomplete support.
 * <p>
 * <b>Design Decision:</b> Uses lowercase color names for user-friendly input
 * while maintaining enum compatibility. Excludes RANDOM from suggestions to
 * prevent confusion in command context.
 * <p>
 * <b>Validation:</b> Rejects invalid color names immediately during parsing,
 * providing clear error messages before command execution.
 */
public class ColorArgumentType implements ArgumentType<EntityTexture> {

    // -- Factory Method --

    /**
     * Creates new color argument type instance.
     * <p>
     * <b>Usage:</b> Register in command tree with Commands.argument("color", ColorArgumentType.color())
     * 
     * @return new ColorArgumentType instance
     */
    public static ColorArgumentType color() {
        return new ColorArgumentType();
    } // color()

    // -- Parsing --

    /**
     * Parses color name from command input.
     * <p>
     * <b>Behavior:</b> Reads unquoted string, matches against enum constant names
     * (case-insensitive). Accepts user-friendly names like "purple" or "light_blue".
     * <p>
     * <b>Error Handling:</b> Throws CommandSyntaxException with descriptive
     * message if color name is invalid or not found.
     *
     * @param reader string reader positioned at color argument
     * @return corresponding EntityTexture enum value
     * @throws CommandSyntaxException if color name is invalid
     */
    @Override
    public EntityTexture parse(StringReader reader) throws CommandSyntaxException {
        String input = reader.readUnquotedString();
        
        // Match against enum constant names (case-insensitive)
        for (EntityTexture texture : EntityTexture.VALUES) {
            if (texture.name().equalsIgnoreCase(input)) {
                return texture;
            }
        }
        
        // Build error message with valid colors
        StringBuilder validColors = new StringBuilder();
        for (EntityTexture texture : EntityTexture.VALUES) {
            if (texture != EntityTexture.RANDOM) {
                if (validColors.length() > 0) {
                    validColors.append(", ");
                }
                validColors.append(texture.name().toLowerCase());
            }
        }
        
        throw new SimpleCommandExceptionType(
            Component.literal("Unknown color: " + input + ". Valid colors: " + validColors)
        ).create();
    } // parse()

    // -- Autocomplete --

    /**
     * Provides autocomplete suggestions for color names.
     * <p>
     * <b>Behavior:</b> Lists all EntityTexture enum constant names except RANDOM
     * in lowercase. Filters suggestions based on partial input for efficient selection.
     * <p>
     * <b>User Experience:</b> Lowercase suggestions match typical command input
     * style, reducing cognitive load during command construction.
     *
     * @param context command context (unused but required by interface)
     * @param builder suggestions builder for adding color options
     * @return future completing with filtered suggestions
     */
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(
        CommandContext<S> context, 
        SuggestionsBuilder builder
    ) {
        for (EntityTexture texture : EntityTexture.VALUES) {
            // Exclude RANDOM from command suggestions
            if (texture != EntityTexture.RANDOM) {
                builder.suggest(texture.name().toLowerCase());
            }
        }
        return builder.buildFuture();
    } // listSuggestions()

} // Class: ColorArgumentType
