package net.msymbios.llovelyr.source.events;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.msymbios.llovelyr.source.configs.LovelyIdentifier;
import net.msymbios.llovelyr.source.entity.internal.enums.EntityTexture;
import net.msymbios.llovelyr.source.events.interfaces.IItemCraftCallback;
import net.msymbios.llovelyr.source.items.custom.LovelyCoreItem;
import net.msymbios.llovelyr.source.items.custom.LovelySpawnItem;

import java.util.Objects;

/**
 * Handles custom crafting logic for robot spawn eggs and cores.
 * <p>
 * <b>Architecture:</b> Implements IItemCraftCallback to intercept spawn egg
 * crafting, enabling NBT transfer from robot cores and color customization
 * via dye ingredients without custom recipe definitions.
 * <p>
 * <b>Crafting Patterns:</b>
 * - Spawn Egg + Robot Core → Transfers all NBT (name, level, stats)
 * - Spawn Egg + Dye → Sets color texture in NBT
 * <p>
 * <b>Design Decision:</b> Uses event-based approach rather than custom recipes
 * to maintain compatibility with vanilla crafting mechanics and recipe viewers.
 */
public class ItemCraftHandler implements IItemCraftCallback {

    // -- Method --

    /**
     * Processes spawn egg crafting to apply NBT modifications.
     * <p>
     * <b>NBT Transfer Logic:</b> Scans crafting matrix for robot core (full NBT
     * copy) or spawn egg + dye combination (color customization). Core transfer
     * takes precedence over dye coloring.
     * <p>
     * <b>Color Mapping:</b> Maps all 16 Minecraft dye colors to EntityTexture
     * enum values, storing color ID in STAT_COLOR NBT key for model predicate
     * selection.
     * <p>
     * <i>Note:</i> Returns PASS to allow vanilla crafting to proceed normally
     * after NBT modification.
     *
     * @param player the player crafting the item
     * @param crafted the crafted spawn egg (NBT will be modified)
     * @param inventory the crafting input inventory
     * @return PASS to continue vanilla crafting flow
     */
    @Override
    public ActionResult onCraft(PlayerEntity player, ItemStack crafted, RecipeInputInventory inventory) {

        // Check if the crafted item is a SpawnItem
        if (crafted.getItem() instanceof SpawnEggItem) {
            ItemStack spawn = null;
            ItemStack dye = null;

            // Iterate through the crafting inventory to find ingredients
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack ingredient = inventory.getStack(i);

                // Pass NBT data from RobotCoreItem
                if (ingredient.getItem() instanceof LovelyCoreItem) {
                    NbtCompound nbt = ingredient.getNbt();
                    if (nbt != null) crafted.setNbt(nbt.copy());
                    break;
                }

                // Check for SpawnItem and DyeItem ingredients
                if (ingredient.getItem() instanceof LovelySpawnItem) spawn = ingredient;
                if (ingredient.getItem() instanceof DyeItem) dye = ingredient;
            }

            // Modify the crafted item based on the SpawnItem and DyeItem ingredients
            if(spawn != null && dye != null) {
                NbtCompound data = spawn.getNbt();
                if(dye.getItem() == Items.WHITE_DYE) Objects.requireNonNull(data).putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.WHITE.getId());
                if(dye.getItem() == Items.ORANGE_DYE) Objects.requireNonNull(data).putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.ORANGE.getId());
                if(dye.getItem() == Items.MAGENTA_DYE) Objects.requireNonNull(data).putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.MAGENTA.getId());
                if(dye.getItem() == Items.LIGHT_BLUE_DYE) Objects.requireNonNull(data).putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.LIGHT_BLUE.getId());
                if(dye.getItem() == Items.YELLOW_DYE) Objects.requireNonNull(data).putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.YELLOW.getId());
                if(dye.getItem() == Items.LIME_DYE) Objects.requireNonNull(data).putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.LIME.getId());
                if(dye.getItem() == Items.PINK_DYE) Objects.requireNonNull(data).putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.PINK.getId());
                if(dye.getItem() == Items.GRAY_DYE) Objects.requireNonNull(data).putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.GRAY.getId());
                if(dye.getItem() == Items.LIGHT_GRAY_DYE) Objects.requireNonNull(data).putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.LIGHT_GRAY.getId());
                if(dye.getItem() == Items.CYAN_DYE) Objects.requireNonNull(data).putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.CYAN.getId());
                if(dye.getItem() == Items.PURPLE_DYE) Objects.requireNonNull(data).putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.PURPLE.getId());
                if(dye.getItem() == Items.BLUE_DYE) Objects.requireNonNull(data).putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.BLUE.getId());
                if(dye.getItem() == Items.BROWN_DYE) Objects.requireNonNull(data).putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.BROWN.getId());
                if(dye.getItem() == Items.GREEN_DYE) Objects.requireNonNull(data).putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.GREEN.getId());
                if(dye.getItem() == Items.RED_DYE) Objects.requireNonNull(data).putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.RED.getId());
                if(dye.getItem() == Items.BLACK_DYE) Objects.requireNonNull(data).putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.BLACK.getId());
                crafted.setNbt(data);
            }
        }
        return ActionResult.PASS;
    } // Class onCraft ()

} // Class: ItemCraftHandler