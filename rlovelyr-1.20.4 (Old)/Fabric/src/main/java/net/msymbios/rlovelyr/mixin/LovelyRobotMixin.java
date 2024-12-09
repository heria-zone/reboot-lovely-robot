package net.msymbios.rlovelyr.mixin;

import net.minecraft.server.MinecraftServer;
import net.msymbios.rlovelyr.LovelyRobot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class LovelyRobotMixin {

	// -- Method --
	@Inject(at = @At("HEAD"), method = "loadWorld")
	private void init(CallbackInfo info) {
		LovelyRobot.LOGGER.info("This line is printed by an reboot lovely robot mixin!");
	} // init ()

} // Class LovelyRobotMixin