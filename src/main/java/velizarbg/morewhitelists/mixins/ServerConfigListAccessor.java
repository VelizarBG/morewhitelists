package velizarbg.morewhitelists.mixins;

import net.minecraft.server.ServerConfigList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.File;

@Mixin(ServerConfigList.class)
public interface ServerConfigListAccessor {
	@Mutable
	@Accessor
	void setFile(File file);
}
