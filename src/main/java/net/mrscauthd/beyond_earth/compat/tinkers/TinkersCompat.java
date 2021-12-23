package net.mrscauthd.beyond_earth.compat.tinkers;

import net.minecraft.resources.ResourceLocation;
import net.mrscauthd.beyond_earth.compat.CompatibleMod;

public class TinkersCompat extends CompatibleMod {

	public static final String MODID = "tconstruct";

	public static ResourceLocation rl(String path) {
		return new ResourceLocation(MODID, path);
	}

	@Override
	public String getModID() {
		return MODID;
	}

	@Override
	protected void onLoad() {
//		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
//		TinkersBossToolsFluids.FLUIDS.register(bus);
	}

}
