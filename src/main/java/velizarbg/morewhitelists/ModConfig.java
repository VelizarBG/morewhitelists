package velizarbg.morewhitelists;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import static velizarbg.morewhitelists.MoreWhitelists.LOGGER;

public class ModConfig {
	public static final String DEFAULT_WHITELIST = "default";
	public static final String WHITELIST_DIR = "morewhitelists";
	public static final File WHITELIST_DIR_FILE = resolveToFile(WHITELIST_DIR);
	public static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("morewhitelists.json").toFile();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public String currentWhitelist = DEFAULT_WHITELIST;

	public static File getWhitelistFile(String name) {
		return resolveToFile("%s/%s.json".formatted(WHITELIST_DIR, name));
	}

	private static File resolveToFile(String path) {
		return FabricLoader.getInstance().getGameDir().resolve(path).toFile();
	}

	public static ModConfig load() {
		ModConfig config = null;
		if (CONFIG_FILE.exists()) {
			try (Reader fileReader = Files.newReader(CONFIG_FILE, StandardCharsets.UTF_8)) {
				config = GSON.fromJson(fileReader, ModConfig.class);
			} catch (IOException e) {
				LOGGER.error("Exception while loading config", e);
			}
		}
		// gson.fromJson() can return null if file is empty
		if (config == null) {
			config = new ModConfig();
		}
		config.save();
		return config;
	}

	public void save() {
		try (Writer writer = Files.newWriter(CONFIG_FILE, StandardCharsets.UTF_8)) {
			GSON.toJson(this, writer);
		} catch (IOException e) {
			LOGGER.error("Exception while saving config", e);
		}
	}
}
