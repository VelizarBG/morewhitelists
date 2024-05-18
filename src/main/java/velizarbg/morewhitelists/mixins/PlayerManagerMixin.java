package velizarbg.morewhitelists.mixins;

import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import velizarbg.morewhitelists.ModConfig;

import java.io.File;

import static velizarbg.morewhitelists.Constants.config;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
	@ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/Whitelist;<init>(Ljava/io/File;)V"))
	private File useDifferentWhitelistIfNecessary(File file) {
		return config.currentWhitelist.equals(ModConfig.DEFAULT_WHITELIST)
			? file
			: ModConfig.getWhitelistFile(config.currentWhitelist);
	}
}
