package velizarbg.morewhitelists.commands;

import com.google.common.io.Files;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.apache.commons.io.FileUtils;
import velizarbg.morewhitelists.ModConfig;
import velizarbg.morewhitelists.mixins.ServerConfigListAccessor;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static velizarbg.morewhitelists.Constants.config;

public class WhitelistCommand {
	public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> {
		var file = ModConfig.WHITELIST_DIR_FILE;
		Stream<File> whitelists;
		try {
			whitelists = file.isDirectory()
				? FileUtils.streamFiles(file, false, "json")
				: Stream.empty();
		} catch (IOException ignored) {
			whitelists = Stream.empty();
		}
		return CommandSource.suggestMatching(whitelists.map(File::getName).map(Files::getNameWithoutExtension).sorted(), builder);
	};
	private static final DynamicCommandExceptionType WHITELIST_NOT_EXIST_EXCEPTION = new DynamicCommandExceptionType(fileName -> Text.literal("A whitelist named '%s' doesn't exist".formatted(fileName)));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("whitelist")
			.requires(source -> source.hasPermissionLevel(3))
			.then(literal("swap")
				.then(literal(ModConfig.DEFAULT_WHITELIST)
					.executes(context -> swapWhitelist(context.getSource(), PlayerManager.WHITELIST_FILE))
				)
				.then(argument("whitelist", StringArgumentType.word())
					.suggests(SUGGESTION_PROVIDER)
					.executes(context ->
						swapWhitelist(
							context.getSource(),
							ModConfig.getWhitelistFile(StringArgumentType.getString(context, "whitelist"))
						)
					)
				)
			)
		);
	}

	private static int swapWhitelist(ServerCommandSource source, File newFile) throws CommandSyntaxException {
		var whitelistName = Files.getNameWithoutExtension(newFile.getName());
		if (!newFile.exists())
			throw WHITELIST_NOT_EXIST_EXCEPTION.create(whitelistName);

		config.currentWhitelist = newFile == PlayerManager.WHITELIST_FILE ? ModConfig.DEFAULT_WHITELIST : whitelistName;
		config.save();
		var whitelist = source.getServer().getPlayerManager().getWhitelist();
		((ServerConfigListAccessor) whitelist).setFile(newFile);
		source.getServer().getPlayerManager().reloadWhitelist();
		source.sendFeedback(Text.literal("Swapped the whitelist to '%s'".formatted(config.currentWhitelist)), true);
		source.getServer().kickNonWhitelistedPlayers(source);
		return 1;
	}
}
