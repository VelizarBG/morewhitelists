package velizarbg.morewhitelists.mixins;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import velizarbg.morewhitelists.commands.WhitelistCommand;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
	@Shadow @Final private CommandDispatcher<ServerCommandSource> dispatcher;

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/command/WhitelistCommand;register(Lcom/mojang/brigadier/CommandDispatcher;)V", shift = At.Shift.AFTER))
	private void registerCustomWhitelistCommand(CallbackInfo ci) {
		WhitelistCommand.register(this.dispatcher);
	}
}
