package net.mrscauthd.beyond_earth.world.oregen;

import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrscauthd.beyond_earth.BeyondEarthMod;
import net.mrscauthd.beyond_earth.ModInnet;
import net.mrscauthd.beyond_earth.world.biomes.BiomeRegistry;

import java.util.List;

@Mod.EventBusSubscriber(modid = BeyondEarthMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OreGeneration {

    /**MOON ORES:*/
    public static RuleTestType<RuleTests.MoonRuleTest> MOON_MATCH = null;
    private static Feature<OreConfiguration> MOON_GEN_FEATURE = null;

    private static PlacedFeature moonChesseOre = null;
    private static PlacedFeature soulSoil = null;
    private static PlacedFeature moonIronOre = null;
    private static PlacedFeature moonDeshOre = null;

    /**MARS ORES:*/
    public static RuleTestType<RuleTests.MarsRuleTest> MARS_MATCH = null;
    private static Feature<OreConfiguration> MARS_GEN_FEATURE = null;

    private static PlacedFeature marsIceShardOre = null;
    private static PlacedFeature marsIronOre = null;
    private static PlacedFeature marsDiamondOre = null;
    private static PlacedFeature marsSiliconOre = null;

    /**MERCURY ORES:*/
    public static RuleTestType<RuleTests.MercuryRuleTest> MERCURY_MATCH = null;
    private static Feature<OreConfiguration> MERCURY_GEN_FEATURE = null;

    private static PlacedFeature mercuryIronOre = null;

    /**VENUS ORES:*/
    public static RuleTestType<RuleTests.VenusRuleTest> VENUS_MATCH = null;
    private static Feature<OreConfiguration> VENUS_GEN_FEATURE = null;

    private static PlacedFeature venusCoalOre = null;
    private static PlacedFeature venusGoldOre = null;
    private static PlacedFeature venusDiamondOre = null;

    @SubscribeEvent
    public static void registerFeature(RegistryEvent.Register<Feature<?>> event) {
        MOON_MATCH = Registry.register(Registry.RULE_TEST, new ResourceLocation(BeyondEarthMod.MODID,"moon_ore_match"), () -> RuleTests.MoonRuleTest.codec);
        MARS_MATCH = Registry.register(Registry.RULE_TEST, new ResourceLocation(BeyondEarthMod.MODID,"mars_ore_match"), () -> RuleTests.MarsRuleTest.codec);
        MERCURY_MATCH = Registry.register(Registry.RULE_TEST, new ResourceLocation(BeyondEarthMod.MODID,"mercury_ore_match"), () -> RuleTests.MercuryRuleTest.codec);
        VENUS_MATCH = Registry.register(Registry.RULE_TEST, new ResourceLocation(BeyondEarthMod.MODID,"venus_ore_match"), () -> RuleTests.VenusRuleTest.codec);

        /**MOON*/
        MOON_GEN_FEATURE = new OreFeature(OreConfiguration.CODEC);
        event.getRegistry().register(MOON_GEN_FEATURE.setRegistryName("moon_ore"));

        moonChesseOre = MOON_GEN_FEATURE.configured(new OreConfiguration(RuleTests.MoonRuleTest.INSTANCE, ModInnet.MOON_CHESSE_ORE.get().defaultBlockState(), 10)).placed(commonOrePlacement(4, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-75), VerticalAnchor.aboveBottom(75))));
        Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(BeyondEarthMod.MODID,"moon_cheese_ore"), moonChesseOre);

        soulSoil = MOON_GEN_FEATURE.configured(new OreConfiguration(RuleTests.MoonRuleTest.INSTANCE, Blocks.SOUL_SOIL.defaultBlockState(), 60)).placed(commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-50), VerticalAnchor.aboveBottom(50))));
        Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(BeyondEarthMod.MODID,"soul_soil"), soulSoil);

        moonIronOre = MOON_GEN_FEATURE.configured(new OreConfiguration(RuleTests.MoonRuleTest.INSTANCE, ModInnet.MOON_IRON_ORE.get().defaultBlockState(), 11)).placed(commonOrePlacement(5, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-64), VerticalAnchor.aboveBottom(64))));
        Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(BeyondEarthMod.MODID,"moon_iron_ore"), moonIronOre);

        moonDeshOre = MOON_GEN_FEATURE.configured(new OreConfiguration(RuleTests.MoonRuleTest.INSTANCE, ModInnet.MOON_DESH_ORE.get().defaultBlockState(), 9)).placed(commonOrePlacement(3, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-25), VerticalAnchor.aboveBottom(25))));
        Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(BeyondEarthMod.MODID,"moon_desh_ore"), moonDeshOre);



        /**MARS*/
        MARS_GEN_FEATURE = new OreFeature(OreConfiguration.CODEC);
        event.getRegistry().register(MARS_GEN_FEATURE.setRegistryName("mars_ore"));

        marsIceShardOre = MARS_GEN_FEATURE.configured(new OreConfiguration(RuleTests.MarsRuleTest.INSTANCE, ModInnet.MARS_ICE_SHARD_ORE.get().defaultBlockState(), 11)).placed(commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-64), VerticalAnchor.aboveBottom(64))));
        Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(BeyondEarthMod.MODID,"mars_ice_shard_ore"), marsIceShardOre);

        marsIronOre = MARS_GEN_FEATURE.configured(new OreConfiguration(RuleTests.MarsRuleTest.INSTANCE, ModInnet.MARS_IRON_ORE.get().defaultBlockState(), 11)).placed(commonOrePlacement(5, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-64), VerticalAnchor.aboveBottom(64))));
        Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(BeyondEarthMod.MODID,"mars_iron_ore"), marsIronOre);

        marsDiamondOre = MARS_GEN_FEATURE.configured(new OreConfiguration(RuleTests.MarsRuleTest.INSTANCE, ModInnet.MARS_DIAMOND_ORE.get().defaultBlockState(), 7)).placed(commonOrePlacement(3, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-16), VerticalAnchor.aboveBottom(16))));
        Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(BeyondEarthMod.MODID,"mars_diamond_ore"), marsDiamondOre);

        marsSiliconOre = MARS_GEN_FEATURE.configured(new OreConfiguration(RuleTests.MarsRuleTest.INSTANCE, ModInnet.MARS_SILICON_ORE.get().defaultBlockState(), 8)).placed(commonOrePlacement(3, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-20), VerticalAnchor.aboveBottom(20))));
        Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(BeyondEarthMod.MODID,"mars_silicon_ore"), marsSiliconOre);



        /**MERCURY*/
        MERCURY_GEN_FEATURE = new OreFeature(OreConfiguration.CODEC);
        event.getRegistry().register(MERCURY_GEN_FEATURE.setRegistryName("mercury_ore"));

        mercuryIronOre = MERCURY_GEN_FEATURE.configured(new OreConfiguration(RuleTests.MercuryRuleTest.INSTANCE, ModInnet.MERCURY_IRON_ORE.get().defaultBlockState(), 12)).placed(commonOrePlacement(5, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-64), VerticalAnchor.aboveBottom(64))));
        Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(BeyondEarthMod.MODID,"mercury_iron_ore"), mercuryIronOre);



        /**Venus*/
        VENUS_GEN_FEATURE = new OreFeature(OreConfiguration.CODEC);
        event.getRegistry().register(VENUS_GEN_FEATURE.setRegistryName("venus_ore"));

        venusCoalOre = VENUS_GEN_FEATURE.configured(new OreConfiguration(RuleTests.VenusRuleTest.INSTANCE, ModInnet.VENUS_COAL_ORE.get().defaultBlockState(), 17)).placed(commonOrePlacement(20, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-125), VerticalAnchor.aboveBottom(125))));
        Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(BeyondEarthMod.MODID,"venus_coal_ore"), venusCoalOre);

        venusGoldOre = VENUS_GEN_FEATURE.configured(new OreConfiguration(RuleTests.VenusRuleTest.INSTANCE, ModInnet.VENUS_GOLD_ORE.get().defaultBlockState(), 9)).placed(commonOrePlacement(2, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-32), VerticalAnchor.aboveBottom(32))));
        Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(BeyondEarthMod.MODID,"venus_gold_ore"), venusGoldOre);

        venusDiamondOre = VENUS_GEN_FEATURE.configured(new OreConfiguration(RuleTests.VenusRuleTest.INSTANCE, ModInnet.VENUS_DIAMOND_ORE.get().defaultBlockState(), 7)).placed(commonOrePlacement(3, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-16), VerticalAnchor.aboveBottom(16))));
        Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(BeyondEarthMod.MODID,"venus_diamond_ore"), venusDiamondOre);
    }

    /** Ores gen in Biome (check also in with Biome it should gen)*/
    public static void biomesLoading(BiomeLoadingEvent event) {
        if (event.getName().getPath().equals(BiomeRegistry.moon.getRegistryName().getPath())) {
            event.getGeneration().getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES).add(() -> moonIronOre);
            event.getGeneration().getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES).add(() -> moonDeshOre);
            event.getGeneration().getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES).add(() -> moonChesseOre);
            event.getGeneration().getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES).add(() -> soulSoil);
        }

        if (event.getName().getPath().equals(BiomeRegistry.mars.getRegistryName().getPath())) {
            event.getGeneration().getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES).add(() -> marsIceShardOre);
            event.getGeneration().getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES).add(() -> marsIronOre);
            event.getGeneration().getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES).add(() -> marsDiamondOre);
            event.getGeneration().getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES).add(() -> marsSiliconOre);
        }

        if (event.getName().getPath().equals(BiomeRegistry.mercury.getRegistryName().getPath())) {
            event.getGeneration().getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES).add(() -> mercuryIronOre);
        }

        if (event.getName().getPath().equals(BiomeRegistry.venus.getRegistryName().getPath())) {
            event.getGeneration().getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES).add(() -> venusCoalOre);
            event.getGeneration().getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES).add(() -> venusGoldOre);
            event.getGeneration().getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES).add(() -> venusDiamondOre);
        }
    }

    /**ORE PLACEMENT*/
    private static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
        return List.of(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int p_195344_, PlacementModifier p_195345_) {
        return orePlacement(CountPlacement.of(p_195344_), p_195345_);
    }

    private static List<PlacementModifier> rareOrePlacement(int p_195350_, PlacementModifier p_195351_) {
        return orePlacement(RarityFilter.onAverageOnceEvery(p_195350_), p_195351_);
    }
}