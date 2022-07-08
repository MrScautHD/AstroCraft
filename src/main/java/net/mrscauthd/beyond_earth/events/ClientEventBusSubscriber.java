package net.mrscauthd.beyond_earth.events;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.gui.overlay.GuiOverlayManager;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.Minecraft;
import net.mrscauthd.beyond_earth.BeyondEarth;
import net.mrscauthd.beyond_earth.entities.renderers.TileEntityBoxRenderer;
import net.mrscauthd.beyond_earth.entities.renderers.alien.AlienModel;
import net.mrscauthd.beyond_earth.entities.renderers.alienzombie.AlienZombieModel;
import net.mrscauthd.beyond_earth.entities.renderers.alienzombie.AlienZombieRenderer;
import net.mrscauthd.beyond_earth.entities.renderers.armors.JetSuitModel;
import net.mrscauthd.beyond_earth.entities.renderers.flag.TileEntityHeadModel;
import net.mrscauthd.beyond_earth.entities.renderers.flag.TileEntityHeadRenderer;
import net.mrscauthd.beyond_earth.entities.renderers.globe.GlobeBlockRenderer;
import net.mrscauthd.beyond_earth.entities.renderers.globe.GlobeModel;
import net.mrscauthd.beyond_earth.entities.renderers.lander.LanderModel;
import net.mrscauthd.beyond_earth.entities.renderers.lander.LanderRenderer;
import net.mrscauthd.beyond_earth.entities.renderers.martianraptor.MartianRaptorModel;
import net.mrscauthd.beyond_earth.entities.renderers.martianraptor.MartianRaptorRenderer;
import net.mrscauthd.beyond_earth.entities.renderers.mogler.MoglerModel;
import net.mrscauthd.beyond_earth.entities.renderers.mogler.MoglerRenderer;
import net.mrscauthd.beyond_earth.entities.renderers.pygro.PygroModel;
import net.mrscauthd.beyond_earth.entities.renderers.pygro.PygroRenderer;
import net.mrscauthd.beyond_earth.entities.renderers.pygrobrute.PygroBruteRenderer;
import net.mrscauthd.beyond_earth.entities.renderers.rockettier1.RocketTier1ItemRenderer;
import net.mrscauthd.beyond_earth.entities.renderers.rockettier1.RocketTier1Model;
import net.mrscauthd.beyond_earth.entities.renderers.rockettier1.RocketTier1Renderer;
import net.mrscauthd.beyond_earth.entities.renderers.rockettier2.RocketTier2ItemRenderer;
import net.mrscauthd.beyond_earth.entities.renderers.rockettier2.RocketTier2Model;
import net.mrscauthd.beyond_earth.entities.renderers.rockettier2.RocketTier2Renderer;
import net.mrscauthd.beyond_earth.entities.renderers.rockettier3.RocketTier3ItemRenderer;
import net.mrscauthd.beyond_earth.entities.renderers.rockettier3.RocketTier3Model;
import net.mrscauthd.beyond_earth.entities.renderers.rockettier3.RocketTier3Renderer;
import net.mrscauthd.beyond_earth.entities.renderers.rockettier4.RocketTier4ItemRenderer;
import net.mrscauthd.beyond_earth.entities.renderers.rockettier4.RocketTier4Model;
import net.mrscauthd.beyond_earth.entities.renderers.rockettier4.RocketTier4Renderer;
import net.mrscauthd.beyond_earth.entities.renderers.rover.RoverItemRenderer;
import net.mrscauthd.beyond_earth.entities.renderers.rover.RoverModel;
import net.mrscauthd.beyond_earth.entities.renderers.rover.RoverRenderer;
import net.mrscauthd.beyond_earth.entities.renderers.armors.SpaceSuitModel;
import net.mrscauthd.beyond_earth.entities.renderers.starcrawler.StarCrawlerModel;
import net.mrscauthd.beyond_earth.entities.renderers.starcrawler.StarCrawlerRenderer;
import net.mrscauthd.beyond_earth.guis.screens.coalgenerator.CoalGeneratorScreen;
import net.mrscauthd.beyond_earth.guis.screens.compressor.CompressorScreen;
import net.mrscauthd.beyond_earth.guis.screens.fuelrefinery.FuelRefineryScreen;
import net.mrscauthd.beyond_earth.guis.screens.lander.LanderScreen;
import net.mrscauthd.beyond_earth.guis.screens.nasaworkbench.NasaWorkbenchGuiWindow;
import net.mrscauthd.beyond_earth.guis.screens.oxygenbubbledistributor.OxygenBubbleDistributorScreen;
import net.mrscauthd.beyond_earth.guis.screens.oxygenloader.OxygenLoaderScreen;
import net.mrscauthd.beyond_earth.guis.screens.planetselection.PlanetSelectionScreen;
import net.mrscauthd.beyond_earth.guis.screens.rocket.RocketScreen;
import net.mrscauthd.beyond_earth.guis.screens.rover.RoverScreen;
import net.mrscauthd.beyond_earth.guis.screens.solarpanel.SolarPanelScreen;
import net.mrscauthd.beyond_earth.guis.screens.waterpump.WaterPumpScreen;
import net.mrscauthd.beyond_earth.overlays.Overlays;
import net.mrscauthd.beyond_earth.particles.*;
import net.mrscauthd.beyond_earth.entities.renderers.alien.AlienRenderer;
import net.mrscauthd.beyond_earth.registries.*;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = BeyondEarth.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {
	public static KeyMapping key1;

	public static final ResourceLocation OXYGEN_BUBBLE = new ResourceLocation(BeyondEarth.MODID, "entities/tile_entity_box_oxygen_generator");

	public static final RocketTier1ItemRenderer ROCKET_TIER_1_ITEM_RENDERER = new RocketTier1ItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	public static final RocketTier2ItemRenderer ROCKET_TIER_2_ITEM_RENDERER = new RocketTier2ItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	public static final RocketTier3ItemRenderer ROCKET_TIER_3_ITEM_RENDERER = new RocketTier3ItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	public static final RocketTier4ItemRenderer ROCKET_TIER_4_ITEM_RENDERER = new RocketTier4ItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	public static final RoverItemRenderer ROVER_ITEM_RENDERER = new RoverItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());

	@SubscribeEvent
	public static void registerEntityRenderingHandler(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(EntitiesRegistry.ALIEN.get(), AlienRenderer::new);
		event.registerEntityRenderer(EntitiesRegistry.ALIEN_ZOMBIE.get(), AlienZombieRenderer::new);
		event.registerEntityRenderer(EntitiesRegistry.STAR_CRAWLER.get(), StarCrawlerRenderer::new);
		event.registerEntityRenderer(EntitiesRegistry.PYGRO.get(), (p_174068_) -> {
			return new PygroRenderer(p_174068_, PygroModel.LAYER_LOCATION, ModelLayers.PIGLIN_INNER_ARMOR, ModelLayers.PIGLIN_OUTER_ARMOR);
		});
		event.registerEntityRenderer(EntitiesRegistry.PYGRO_BRUTE.get(), (p_174068_) -> {
			return new PygroBruteRenderer(p_174068_, PygroModel.LAYER_LOCATION, ModelLayers.PIGLIN_BRUTE_INNER_ARMOR, ModelLayers.PIGLIN_BRUTE_OUTER_ARMOR);
		});
		event.registerEntityRenderer(EntitiesRegistry.MOGLER.get(), MoglerRenderer::new);
		event.registerEntityRenderer(EntitiesRegistry.MARTIAN_RAPTOR.get(), MartianRaptorRenderer::new);
		event.registerEntityRenderer(EntitiesRegistry.TIER_1_ROCKET.get(), RocketTier1Renderer::new);
		event.registerEntityRenderer(EntitiesRegistry.TIER_2_ROCKET.get(), RocketTier2Renderer::new);
		event.registerEntityRenderer(EntitiesRegistry.TIER_3_ROCKET.get(), RocketTier3Renderer::new);
		event.registerEntityRenderer(EntitiesRegistry.TIER_4_ROCKET.get(), RocketTier4Renderer::new);
		event.registerEntityRenderer(EntitiesRegistry.LANDER.get(), LanderRenderer::new);
		event.registerEntityRenderer(EntitiesRegistry.ROVER.get(), RoverRenderer::new);

		event.registerEntityRenderer(EntitiesRegistry.ICE_SPIT_ENTITY.get(), renderManager -> new ThrownItemRenderer(renderManager, 1, true));

		//event.registerBlockEntityRenderer(BlockEntitiesRegistry.OXYGEN_BUBBLE_DISTRIBUTOR_BLOCK_ENTITY.get(), TileEntityBoxRenderer::new);

		event.registerBlockEntityRenderer(BlockEntitiesRegistry.FLAG_BLOCK_ENTITY.get(), TileEntityHeadRenderer::new);
		event.registerBlockEntityRenderer(BlockEntitiesRegistry.GLOBE_BLOCK_ENTITY.get(), GlobeBlockRenderer::new);
	}

	@SubscribeEvent
	public static void registerEntityRenderingHandler(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(AlienModel.LAYER_LOCATION, AlienModel::createBodyLayer);
		event.registerLayerDefinition(AlienZombieModel.LAYER_LOCATION, AlienZombieModel::createBodyLayer);
		event.registerLayerDefinition(StarCrawlerModel.LAYER_LOCATION, StarCrawlerModel::createBodyLayer);
		event.registerLayerDefinition(PygroModel.LAYER_LOCATION, PygroModel::createBodyLayer);
		event.registerLayerDefinition(MoglerModel.LAYER_LOCATION, MoglerModel::createBodyLayer);
		event.registerLayerDefinition(MartianRaptorModel.LAYER_LOCATION, MartianRaptorModel::createBodyLayer);
		event.registerLayerDefinition(TileEntityHeadModel.LAYER_LOCATION, TileEntityHeadModel::createHumanoidHeadLayer);
		event.registerLayerDefinition(GlobeModel.LAYER_LOCATION, GlobeModel::createLayer);
		
		event.registerLayerDefinition(RocketTier1Model.LAYER_LOCATION, RocketTier1Model::createBodyLayer);
		event.registerLayerDefinition(RocketTier2Model.LAYER_LOCATION, RocketTier2Model::createBodyLayer);
		event.registerLayerDefinition(RocketTier3Model.LAYER_LOCATION, RocketTier3Model::createBodyLayer);
		event.registerLayerDefinition(RocketTier4Model.LAYER_LOCATION, RocketTier4Model::createBodyLayer);
		event.registerLayerDefinition(LanderModel.LAYER_LOCATION, LanderModel::createBodyLayer);
		event.registerLayerDefinition(RoverModel.LAYER_LOCATION, RoverModel::createBodyLayer);

		event.registerLayerDefinition(SpaceSuitModel.SPACE_SUIT_P1.LAYER_LOCATION, SpaceSuitModel.SPACE_SUIT_P1::createBodyLayer);
		event.registerLayerDefinition(SpaceSuitModel.SPACE_SUIT_P2.LAYER_LOCATION, SpaceSuitModel.SPACE_SUIT_P2::createBodyLayer);

		event.registerLayerDefinition(JetSuitModel.JET_SUIT_P1.LAYER_LOCATION, JetSuitModel.JET_SUIT_P1::createBodyLayer);
		event.registerLayerDefinition(JetSuitModel.JET_SUIT_P2.LAYER_LOCATION, JetSuitModel.JET_SUIT_P2::createBodyLayer);
	}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		//GUIS
		MenuScreens.register(ScreensRegistry.ROCKET_GUI.get(), RocketScreen::new);
		/*MenuScreens.register(ScreensRegistry.COMPRESSOR_GUI.get(), CompressorScreen::new);
		MenuScreens.register(ScreensRegistry.FUEL_REFINERY_GUI.get(), FuelRefineryScreen::new);
		MenuScreens.register(ScreensRegistry.COAL_GENERATOR_GUI.get(), CoalGeneratorScreen::new);
		MenuScreens.register(ScreensRegistry.NASA_WORKBENCH_GUI.get(), NasaWorkbenchGuiWindow::new);
		MenuScreens.register(ScreensRegistry.OXYGEN_LOADER_GUI.get(), OxygenLoaderScreen::new);
		MenuScreens.register(ScreensRegistry.SOLAR_PANEL_GUI.get(), SolarPanelScreen::new);
		MenuScreens.register(ScreensRegistry.WATER_PUMP_GUI.get(), WaterPumpScreen::new);
		MenuScreens.register(ScreensRegistry.OXYGEN_BUBBLE_DISTRIBUTOR_GUI.get(), OxygenBubbleDistributorScreen::new);*/
		MenuScreens.register(ScreensRegistry.LANDER_GUI.get(), LanderScreen::new);
		MenuScreens.register(ScreensRegistry.ROVER_GUI.get(), RoverScreen::new);
		MenuScreens.register(ScreensRegistry.PLANET_SELECTION_GUI.get(), PlanetSelectionScreen::new);

		//Key Binding Registrys
		key1 = new KeyMapping("key." + BeyondEarth.MODID + ".rocket_start", GLFW.GLFW_KEY_SPACE, "key.categories." + BeyondEarth.MODID);
		ClientRegistry.registerKeyBinding(key1);

		//Fluid Translucent Renderer
		ItemBlockRenderTypes.setRenderLayer(FluidsRegistry.FLOWING_FUEL.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(FluidsRegistry.FUEL_STILL.get(), RenderType.translucent());

		ItemBlockRenderTypes.setRenderLayer(FluidsRegistry.FLOWING_OIL.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(FluidsRegistry.OIL_STILL.get(), RenderType.translucent());

		//Block Translucent Renderer
		ItemBlockRenderTypes.setRenderLayer(BlocksRegistry.COAL_LANTERN_BLOCK.get(), RenderType.translucent());

		//Cutout
		//TODO DONT FORGOT IT TO REWORK IT
		//ItemBlockRenderTypes.setRenderLayer(BlocksRegistry.NASA_WORKBENCH_BLOCK.get(), RenderType.cutout());

		//ItemBlockRenderTypes.setRenderLayer(BlocksRegistry.WATER_PUMP_BLOCK.get(), RenderType.cutout());

		//OVERLAY
		GuiOverlayManager.registerOverlayTop("warning", Overlays.WARNING);
		GuiOverlayManager.registerOverlayTop("rocket_timer", Overlays.ROCKET_TIMER);
		GuiOverlayManager.registerOverlayBottom("oxygen_tank", Overlays.OXYGEN_TANK);
		GuiOverlayManager.registerOverlayBottom("rocket_height", Overlays.ROCKET_HEIGHT);
	}

	@SubscribeEvent
	public static void registerParticlesFactory(RegisterParticleProvidersEvent event) {
		Minecraft mc = Minecraft.getInstance();
		mc.particleEngine.register(ParticlesRegistry.VENUS_RAIN_PARTICLE.get(), VenusRainParticle.ParticleFactory::new);
		mc.particleEngine.register(ParticlesRegistry.LARGE_FLAME_PARTICLE.get(), LargeFlameParticle.ParticleFactory::new);
		mc.particleEngine.register(ParticlesRegistry.LARGE_SMOKE_PARTICLE.get(), LargeSmokeParticle.ParticleFactory::new);
		mc.particleEngine.register(ParticlesRegistry.SMALL_FLAME_PARTICLE.get(), SmallFlameParticle.ParticleFactory::new);
		mc.particleEngine.register(ParticlesRegistry.SMALL_SMOKE_PARTICLE.get(), SmallSmokeParticle.ParticleFactory::new);
	}

	@SubscribeEvent
	public static void atlas(TextureStitchEvent.Pre event) {
		event.addSprite(OXYGEN_BUBBLE);
	}
}
