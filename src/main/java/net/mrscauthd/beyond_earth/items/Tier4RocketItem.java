package net.mrscauthd.beyond_earth.items;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.mrscauthd.beyond_earth.entities.IRocketEntity;
import net.mrscauthd.beyond_earth.entities.RocketTier4Entity;
import net.mrscauthd.beyond_earth.events.ClientEventBusSubscriber;
import net.mrscauthd.beyond_earth.registries.EntitiesRegistry;

public class Tier4RocketItem extends IRocketItem {
    public Tier4RocketItem(Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public BlockEntityWithoutLevelRenderer getRenderer() {
        return ClientEventBusSubscriber.ROCKET_TIER_4_ITEM_RENDERER;
    }

    @Override
    public EntityType getEntityType() {
        return EntitiesRegistry.TIER_4_ROCKET.get();
    }

    @Override
    public IRocketEntity getRocket(Level level) {
        return new RocketTier4Entity(EntitiesRegistry.TIER_4_ROCKET.get(), level);
    }

    @Override
    public void fillItemCategory(CreativeModeTab p_41391_, NonNullList<ItemStack> p_41392_) {
        super.fillItemCategory(p_41391_, p_41392_);
        if (this.allowdedIn(p_41391_)) {
            ItemStack itemStack = new ItemStack(this);
            itemStack.getOrCreateTag().putInt(fuelTag, 300);
            p_41392_.add(itemStack);
        }
    }
}
