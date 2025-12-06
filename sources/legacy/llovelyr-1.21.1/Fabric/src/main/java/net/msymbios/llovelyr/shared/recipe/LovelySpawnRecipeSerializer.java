package net.msymbios.llovelyr.shared.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.jetbrains.annotations.NotNull;

/**
 * Serializer for spawn egg crafting recipes with data transfer.
 * <p>
 * <b>Architecture:</b> Uses codec-based serialization matching vanilla ShapedRecipe
 * format while wrapping in custom recipe class for data transfer logic.
 * <p>
 * <b>Migration Note:</b> Minecraft 1.21.1 uses codec-based serialization. This
 * serializer directly parses the recipe JSON using the same structure as vanilla
 * shaped recipes (group, category, pattern/key, result).
 * <p>
 * <b>Design Decision:</b> Direct codec approach ensures full compatibility with
 * vanilla recipe format and proper pattern validation. Uses supplier pattern to
 * break cyclical dependency with registry class.
 */
public class LovelySpawnRecipeSerializer implements RecipeSerializer<LovelySpawnRecipe> {

    // -- Variables --

    private final Item robotCoreItem;
    private final MapCodec<LovelySpawnRecipe> codecInstance;
    private final StreamCodec<RegistryFriendlyByteBuf, LovelySpawnRecipe> streamCodecInstance;

    // -- Constructor --

    public LovelySpawnRecipeSerializer(Item robotCoreItem) {
        this.robotCoreItem = robotCoreItem;
        
        this.codecInstance = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(LovelySpawnRecipe::getGroup),
                        CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(LovelySpawnRecipe::category),
                        ShapedRecipePattern.MAP_CODEC.forGetter(LovelySpawnRecipe::pattern),
                        ItemStack.STRICT_SINGLE_ITEM_CODEC.fieldOf("result").forGetter(recipe -> recipe.getResultItem(null))
                ).apply(instance, (group, category, pattern, result) ->
                        new LovelySpawnRecipe(group, category, pattern, result, robotCoreItem))
        );
        
        this.streamCodecInstance = StreamCodec.of(this::toNetwork, this::fromNetwork);
    } // Constructor: LovelySpawnRecipeSerializer ()

    // -- Inherited Methods --

    @Override
    public @NotNull MapCodec<LovelySpawnRecipe> codec() {
        return codecInstance;
    } // codec()

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, LovelySpawnRecipe> streamCodec() {
        return streamCodecInstance;
    } // streamCodec()

    // -- Network Serialization --

    private LovelySpawnRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
        String group = buf.readUtf();
        CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
        ShapedRecipePattern pattern = ShapedRecipePattern.STREAM_CODEC.decode(buf);
        ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
        return new LovelySpawnRecipe(group, category, pattern, result, robotCoreItem);
    } // fromNetwork()

    private void toNetwork(RegistryFriendlyByteBuf buf, LovelySpawnRecipe recipe) {
        buf.writeUtf(recipe.getGroup());
        buf.writeEnum(recipe.category());
        ShapedRecipePattern.STREAM_CODEC.encode(buf, recipe.pattern());
        ItemStack.STREAM_CODEC.encode(buf, recipe.getResultItem(null));
    } // toNetwork()

} // Class: LovelySpawnRecipeSerializer
