package net.heriazone.lovelylib.api.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.heriazone.hzlib.api.entity.NativeEntityFamily;
import net.heriazone.lovelylib.common.commands.NativeCommands;
import net.heriazone.lovelylib.common.entity.RobotEntity;
import net.heriazone.lovelylib.common.entity.enums.EntityTexture;
import net.heriazone.lovelylib.api.entity.features.BlazeCycleFeature;
import net.heriazone.hzlib.api.entity.features.variants.TextureVariantFeature;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Custom argument type for robot colour/texture selection in commands.
 * <p>
 * <b>Palette awareness:</b> {@link #listSuggestions} looks up the robot the
 * player is facing and filters suggestions to that family's registered palette.
 * Standard 16-colour robots suggest 16 names; restricted-palette robots (Prime,
 * Hyperion) suggest only their named textures; single-colour robots (Empyrium)
 * suggest nothing — there is nothing to change.
 * <p>
 * Falls back to the full standard palette if no robot is in range.
 */
public class ColorArgumentType implements ArgumentType<EntityTexture> {

    public static ColorArgumentType color() {
        return new ColorArgumentType();
    } // color()

    @Override
    public EntityTexture parse(StringReader reader) throws CommandSyntaxException {
        String input = reader.readUnquotedString();
        for (EntityTexture texture : EntityTexture.VALUES) {
            if (texture.name().equalsIgnoreCase(input) || texture.Name().equalsIgnoreCase(input)) {
                return texture;
            }
        }
        throw new SimpleCommandExceptionType(
                Component.literal("Unknown colour: " + input)
        ).create();
    } // parse()

    /**
     * Suggests only the colours available on the robot the player is facing.
     * <p>
     * Restricted-palette robots expose their named textures (e.g. {@code dark_matter},
     * {@code commander}). Standard robots expose the 16 dye colours. Single-colour
     * robots (Empyrium) expose no suggestions — the Design cannot be changed.
     */
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(
            CommandContext<S> context, SuggestionsBuilder builder) {

        List<String> suggestions = resolveAvailableColours(context);
        for (String name : suggestions) {
            builder.suggest(name);
        }
        return builder.buildFuture();
    } // listSuggestions()

    // -- Palette Resolution --

    private <S> List<String> resolveAvailableColours(CommandContext<S> context) {
        if (context.getSource() instanceof CommandSourceStack source) {
            try {
                Player player = source.getPlayerOrException();
                Object entity = NativeCommands.findEntityInFront(player);
                if (entity instanceof RobotEntity robot && robot.nativeEntity != null) {
                    return paletteFromFamily(robot.nativeEntity);
                }
            } catch (Exception ignored) {}
        }
        return standardPalette();
    } // resolveAvailableColours()

    /**
     * Derives colour suggestions from the family's registered features.
     * <p>
     * Restricted-palette robots declare a {@link BlazeCycleFeature} whose palette
     * list is the authoritative source. Single-colour robots (one texture variant)
     * suggest nothing — there is no alternative to cycle to.
     * Standard 16-colour robots fall through to the full dye palette.
     */
    private List<String> paletteFromFamily(NativeEntityFamily<?> family) {
        // Restricted palette: use BlazeCycleFeature's ordered list
        if (family.hasFeature(BlazeCycleFeature.class)) {
            return family.getFeature(BlazeCycleFeature.class)
                    .map(f -> f.getPalette().stream()
                            .map(EntityTexture::Name)
                            .toList())
                    .orElseGet(this::standardPalette);
        }
        // Single-colour: count variants registered for this family key
        var variants = family.getFeature(TextureVariantFeature.class)
                .map(f -> f.getAvailableVariants(family.getKey()))
                .orElse(java.util.Collections.emptyList());
        if (variants.size() <= 1) {
            return java.util.Collections.emptyList();
        }
        return standardPalette();
    } // paletteFromFamily()

    private List<String> standardPalette() {
        List<String> names = new ArrayList<>(16);
        for (EntityTexture t : EntityTexture.VALUES) {
            if (t != EntityTexture.RANDOM && t.getId() <= 15) {
                names.add(t.Name());
            }
        }
        return names;
    } // standardPalette()

} // Class: ColorArgumentType