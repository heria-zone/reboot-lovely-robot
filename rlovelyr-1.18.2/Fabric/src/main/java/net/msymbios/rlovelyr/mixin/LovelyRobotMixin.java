package net.msymbios.rlovelyr.mixin;

import net.msymbios.rlovelyr.LovelyRobotMod;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class LovelyRobotMixin {

	// -- Method --
	@Inject(at = @At("HEAD"), method = "init()V")
	private void init(CallbackInfo info) {
		LovelyRobotMod.LOGGER.info("This line is printed by an reboot lovely robot mixin!");
	} // init ()

} // Class LovelyRobotMixin
