package net.mrscauthd.beyond_earth.crafting;

import javax.annotation.Nonnull;

import net.minecraftforge.registries.ForgeRegistryEntry;

public class RocketPart extends ForgeRegistryEntry<RocketPart> {

    @Nonnull
    public static final RocketPart EMPTY = new RocketPart(0);
    
	private final int slots;

	public RocketPart(int slots) {
		this.slots = slots;
	}

	public int getSlots() {
		return this.slots;
	}
}
