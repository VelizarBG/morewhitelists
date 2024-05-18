package velizarbg.morewhitelists;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoreWhitelists implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("morewhitelists");

	@Override
	public void onInitialize() {
		Constants.config = ModConfig.load();
	}
}
