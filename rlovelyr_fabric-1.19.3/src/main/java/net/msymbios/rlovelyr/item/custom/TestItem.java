package net.msymbios.rlovelyr.item.custom;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.msymbios.rlovelyr.entity.custom.VanillaEntity.*;

public class TestItem extends Item {

    // -- Constructor --
    public TestItem(Settings settings) {
        super(settings);
    } // Constructor EightBallItem

    // -- Methods --
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!world.isClient && hand == Hand.MAIN_HAND) {

            int holder = getRandomNumber(4);
            outputText(user, "Resource ID: " + holder);

            if(Screen.hasShiftDown())
                outputText(user, "Texture: " + getEntityTexture(holder));

            // user.getItemCooldownManager().set(this, 20); // add a cool down
        }
        return super.use(world, user, hand);
    } // use ()

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Item for testing!").formatted(Formatting.AQUA));
        super.appendTooltip(stack, world, tooltip, context);
    } // appendTooltip ()

    private void outputText(PlayerEntity player, String message) {
        player.sendMessage(Text.literal(message));
    } // outputRandomNumber ()

} // Class EightBallItem