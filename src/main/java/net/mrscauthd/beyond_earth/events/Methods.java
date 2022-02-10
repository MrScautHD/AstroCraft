package net.mrscauthd.beyond_earth.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.netty.buffer.Unpooled;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.mrscauthd.beyond_earth.BeyondEarthMod;
import net.mrscauthd.beyond_earth.ModInit;
import net.mrscauthd.beyond_earth.capability.oxygen.IOxygenStorage;
import net.mrscauthd.beyond_earth.capability.oxygen.OxygenUtil;
import net.mrscauthd.beyond_earth.entity.*;
import net.mrscauthd.beyond_earth.events.forgeevents.LivingSetFireInHotPlanetEvent;
import net.mrscauthd.beyond_earth.events.forgeevents.LivingSetVenusRainEvent;
import net.mrscauthd.beyond_earth.gui.screens.planetselection.PlanetSelectionGui;
import net.mrscauthd.beyond_earth.item.VehicleItem;
import net.mrscauthd.beyond_earth.rendertype.TranslucentArmorRenderType;

import java.util.HashSet;
import java.util.List;

public class Methods {

    public static final ResourceKey<Level> moon = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BeyondEarthMod.MODID, "moon"));
    public static final ResourceKey<Level> moon_orbit = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BeyondEarthMod.MODID, "moon_orbit"));
    public static final ResourceKey<Level> mars = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BeyondEarthMod.MODID, "mars"));
    public static final ResourceKey<Level> mars_orbit = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BeyondEarthMod.MODID, "mars_orbit"));
    public static final ResourceKey<Level> mercury = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BeyondEarthMod.MODID, "mercury"));
    public static final ResourceKey<Level> mercury_orbit = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BeyondEarthMod.MODID, "mercury_orbit"));
    public static final ResourceKey<Level> venus = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BeyondEarthMod.MODID, "venus"));
    public static final ResourceKey<Level> venus_orbit = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BeyondEarthMod.MODID, "venus_orbit"));
    public static final ResourceKey<Level> glacio = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BeyondEarthMod.MODID, "glacio"));
    public static final ResourceKey<Level> glacio_orbit = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BeyondEarthMod.MODID, "glacio_orbit"));
    public static final ResourceKey<Level> overworld = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("overworld"));
    public static final ResourceKey<Level> overworld_orbit = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BeyondEarthMod.MODID,"overworld_orbit"));

    public static final HashSet<ResourceKey<Level>> spaceWorldsWithoutOxygen = new HashSet<>(10);
    public static final HashSet<ResourceKey<Level>> orbitWorlds = new HashSet<>(6);
    static {
        spaceWorldsWithoutOxygen.addAll(List.of(moon, moon_orbit, mars, mars_orbit, mercury, mercury_orbit, venus, venus_orbit, glacio_orbit, overworld_orbit));
        orbitWorlds.addAll(List.of(moon_orbit, mars_orbit, mercury_orbit, venus_orbit, glacio_orbit, overworld_orbit));
    }

    public static void worldTeleport(Player entity, ResourceKey<Level> planet, double high) {
        ServerLevel nextLevel = entity.getServer().getLevel(planet);

        if (nextLevel != null && entity instanceof ServerPlayer) {
            ((ServerPlayer) entity).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 0));
            ((ServerPlayer) entity).teleportTo(nextLevel, entity.getX(), high, entity.getZ(), entity.getYRot(), entity.getXRot());
            ((ServerPlayer) entity).connection.send(new ClientboundPlayerAbilitiesPacket(entity.getAbilities()));

            for (MobEffectInstance effectinstance : entity.getActiveEffects()) {
                ((ServerPlayer) entity).connection.send(new ClientboundUpdateMobEffectPacket(entity.getId(), effectinstance));
            }
            ((ServerPlayer) entity).connection.send(new ClientboundSetExperiencePacket(entity.experienceProgress, entity.totalExperience, entity.experienceLevel));
        }
    }

    /**
     * @param entity the LivingEntity to check
     * @return true if entity is wearing a full set of netherite space gear, false otherwise
     */
    public static boolean netheriteSpaceSuitCheck(final LivingEntity entity) {
        if (!checkArmor(entity, 3, ModInit.NETHERITE_OXYGEN_MASK.get())) return false; // missing a netherite oxygen mask
        if (!checkArmor(entity, 2, ModInit.NETHERITE_SPACE_SUIT.get())) return false; // missing a netherite space suit
        if (!checkArmor(entity, 1, ModInit.NETHERITE_SPACE_PANTS.get())) return false; // missing netherite space pants
        if (!checkArmor(entity, 0, ModInit.NETHERITE_SPACE_BOOTS.get())) return false; // missing netherite space boots

        return true; // has mask + suit + pants + boots
    }

    /**
     * @param entity the LivingEntity to check
     * @return true if entity is wearing a full set of standard space gear, false otherwise
     */
    public static boolean spaceSuitCheck(final LivingEntity entity) {
        if (!checkArmor(entity, 3, ModInit.OXYGEN_MASK.get())) return false;
        if (!checkArmor(entity, 2, ModInit.SPACE_SUIT.get())) return false;
        if (!checkArmor(entity, 1, ModInit.SPACE_PANTS.get())) return false;
        if (!checkArmor(entity, 0, ModInit.SPACE_BOOTS.get())) return false;

        return true;
    }

    /**
     * @param entity the LivingEntity to check
     * @return true if entity is wearing a full set of standard or netherite space gear, false otherwise
     */
    public static boolean spaceSuitCheckBoth(final LivingEntity entity) {
        if (!(checkArmor(entity, 3, ModInit.OXYGEN_MASK.get()) || checkArmor(entity, 3, ModInit.NETHERITE_OXYGEN_MASK.get()))) {
            return false; // missing an oxygen mask
        }

        if (!(checkArmor(entity, 2, ModInit.SPACE_SUIT.get()) || checkArmor(entity, 2, ModInit.NETHERITE_SPACE_SUIT.get()))) {
            return false; // missing a space suit
        }

        if (!(checkArmor(entity, 1, ModInit.SPACE_PANTS.get()) || checkArmor(entity, 1, ModInit.NETHERITE_SPACE_PANTS.get()))) {
            return false; // missing space pants
        }

        if (!(checkArmor(entity, 0, ModInit.SPACE_BOOTS.get()) || checkArmor(entity, 0, ModInit.NETHERITE_SPACE_BOOTS.get()))) {
            return false; // missing space boots
        }

        return true; // has mask + suit + pants + boots
    }

    public static boolean checkArmor(final LivingEntity entity, final int number, final Item item) {
        return entity.getItemBySlot(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, number)).getItem() == item;
    }

    public static boolean isSpaceWorld(Level world) {
        if (Methods.isWorld(world, overworld)) {
            return false;
        }

        return Methods.isWorld(world, moon)
                || Methods.isWorld(world, moon_orbit)
                || Methods.isWorld(world, mars)
                || Methods.isWorld(world, mars_orbit)
                || Methods.isWorld(world, mercury)
                || Methods.isWorld(world, mercury_orbit)
                || Methods.isWorld(world, venus)
                || Methods.isWorld(world, venus_orbit)
                || Methods.isWorld(world, glacio)
                || Methods.isWorld(world, glacio_orbit)
                || Methods.isWorld(world, overworld_orbit);
    }

    public static boolean isSpaceWorldWithoutOxygen(final Level world) {
        return spaceWorldsWithoutOxygen.contains(world.dimension());
    }

    public static boolean isOrbitWorld(final Level world) {
        return orbitWorlds.contains(world.dimension());
    }

    public static boolean isWorld(final Level world, final ResourceKey<Level> loc) {
        return world.dimension() == loc;
    }

    public static void OxygenDamage(final LivingEntity entity) {
        entity.hurt(ModInit.DAMAGE_SOURCE_OXYGEN, 1.0F);
    }

    public static boolean isRocket(final Entity entity) {
        return entity instanceof RocketTier1Entity || entity instanceof RocketTier2Entity || entity instanceof RocketTier3Entity || entity instanceof RocketTier4Entity;
    }

    public static boolean AllVehiclesOr(final Entity entity) {
        return entity instanceof VehicleEntity;
    }

    public static void RocketSounds(Entity entity, Level world) {
        world.playSound(null, entity, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(BeyondEarthMod.MODID,"rocket_fly")), SoundSource.NEUTRAL,1,1);
    }

    public static void DropRocket(Player player) {
        Item item1 = player.getMainHandItem().getItem();
        Item item2 = player.getOffhandItem().getItem();

        if (item1 instanceof VehicleItem && item2 instanceof VehicleItem) {

            ItemEntity spawn = new ItemEntity(player.level, player.getX(),player.getY(),player.getZ(), new ItemStack(item2));
            spawn.setPickUpDelay(0);
            player.level.addFreshEntity(spawn);

            player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(capability -> {
                capability.extractItem(40, 1, false); //40 is offhand
            });

        }
    }

    /**If a entity should not get Fire add it to the Tag "venus_fire"*/
    public static void PlanetFire(LivingEntity entity, ResourceKey<Level> planet1, ResourceKey<Level> planet2) {
        Level level = entity.level;

        if (Methods.isWorld(level, planet1) || Methods.isWorld(level, planet2)) {
            if (!Methods.netheriteSpaceSuitCheck(entity) && !entity.hasEffect(MobEffects.FIRE_RESISTANCE) && !entity.fireImmune() && (entity instanceof Mob || entity instanceof Player)) {
                if (!MinecraftForge.EVENT_BUS.post(new LivingSetFireInHotPlanetEvent(entity))) {
                    if (!tagCheck(entity, BeyondEarthMod.MODID + ":entities/planet_fire")) {
                        entity.setSecondsOnFire(10);
                    }
                }
            }
        }
    }

    /**If a entity should not get Damage add it to the Tag "venus_rain"*/
    public static void VenusRain(LivingEntity entity, ResourceKey<Level> planet) {
        if (!Methods.isWorld(entity.level, planet)) {
            return;
        }

        if (entity.isPassenger() && (Methods.isRocket(entity.getVehicle()) || entity.getVehicle() instanceof LanderEntity)) {
            return;
        }

        if (MinecraftForge.EVENT_BUS.post(new LivingSetVenusRainEvent(entity))) {
            return;
        }

        if (tagCheck(entity, BeyondEarthMod.MODID + ":entities/venus_rain")) {
            return;
        }

        if (entity.level.getLevelData().isRaining() && entity.level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) Math.floor(entity.getX()), (int) Math.floor(entity.getZ())) <= Math.floor(entity.getY()) + 1) {
            entity.hurt(ModInit.DAMAGE_SOURCE_ACID_RAIN, 1);
        }
    }

    /**If a entity should get oxygen damage add it to the tag "oxygen" (don't add the Player, he has a own oxygen system)*/
    public static void EntityOxygen(LivingEntity entity, Level world) {
        if (Config.ENTITY_OXYGEN_SYSTEM.get() && Methods.isSpaceWorldWithoutOxygen(world) && tagCheck(entity, BeyondEarthMod.MODID + ":entities/oxygen")) {

            if (!entity.hasEffect(ModInit.OXYGEN_EFFECT.get())) {

                entity.getPersistentData().putDouble(BeyondEarthMod.MODID + ":oxygen_tick", entity.getPersistentData().getDouble(BeyondEarthMod.MODID + ":oxygen_tick") + 1);

                if (entity.getPersistentData().getDouble(BeyondEarthMod.MODID + ":oxygen_tick") > 15) {

                    if(!world.isClientSide) {
                        Methods.OxygenDamage(entity);
                    }

                    entity.getPersistentData().putDouble(BeyondEarthMod.MODID + ":oxygen_tick", 0);
                }
            }
        }

        //Out of Space
        if (Config.ENTITY_OXYGEN_SYSTEM.get() && entity.hasEffect(ModInit.OXYGEN_EFFECT.get())) {
            entity.setAirSupply(300);
        }
    }

    public static void vehicleRotation(Entity vehicle, float roation) {
        vehicle.setYRot(vehicle.getYRot() + roation);
        vehicle.setYBodyRot(vehicle.getYRot());
        vehicle.yRotO = vehicle.getYRot();
    }

    public static void noFuelMessage(Player player) {
        if (!player.level.isClientSide) {
            player.displayClientMessage(new TranslatableComponent("message." + BeyondEarthMod.MODID + ".no_fuel"), false);
        }
    }

    public static void holdSpaceMessage(Player player) {
        if (!player.level.isClientSide) {
            player.displayClientMessage(new TranslatableComponent("message." + BeyondEarthMod.MODID + ".hold_space"), false);
        }
    }

    public static boolean tagCheck(Entity entity, String tag) {
        return EntityTypeTags.getAllTags().getTagOrEmpty(new ResourceLocation(tag)).contains(entity.getType());
    }
    
    public static boolean tagCheck(Fluid fluid, ResourceLocation tag) {
        return FluidTags.getAllTags().getTagOrEmpty(tag).contains(fluid);
    }

    public static boolean tagCheck(Item item, ResourceLocation tag) {
        return ItemTags.getAllTags().getTagOrEmpty(tag).contains(item);
    }

    public static void landerTeleport(Player player, ResourceKey<Level> newPlanet) {
        LanderEntity lander = (LanderEntity) player.getVehicle();

        if (player.getY() < 1) {

            ItemStack slot_0 = lander.getInventory().getStackInSlot(0);
            ItemStack slot_1 = lander.getInventory().getStackInSlot(1);
            lander.remove(Entity.RemovalReason.DISCARDED);

            Methods.worldTeleport(player, newPlanet, 700);

            Level newWorld = player.level;

            if (!player.level.isClientSide) {
                LanderEntity entityToSpawn = new LanderEntity(ModInit.LANDER.get(), newWorld);
                entityToSpawn.moveTo(player.getX(), player.getY(), player.getZ(), 0, 0);
                newWorld.addFreshEntity(entityToSpawn);

                entityToSpawn.getInventory().setStackInSlot(0, slot_0);
                entityToSpawn.getInventory().setStackInSlot(1, slot_1);

                player.startRiding(entityToSpawn);
            }
        }
    }

    public static void rocketTeleport(Player player, ResourceKey<Level> planet, ItemStack rocketItem, boolean SpaceStation) {
        if (!Methods.isWorld(player.level, planet)) {
            Methods.worldTeleport(player, planet, 700);
        } else {
            player.setPos(player.getX(), 700, player.getZ());

            if (player instanceof ServerPlayer) {
                ((ServerPlayer) player).connection.teleport(player.getX(), 700, player.getZ(), player.getYRot(), player.getXRot());
            }
        }

        Level world = player.level;

        if (!world.isClientSide) {
            LanderEntity landerSpawn = new LanderEntity(ModInit.LANDER.get(), world);
            landerSpawn.moveTo(player.getX(), player.getY(), player.getZ(), 0, 0);
            world.addFreshEntity(landerSpawn);

            String itemId = player.getPersistentData().getString(BeyondEarthMod.MODID + ":slot0");

            landerSpawn.getInventory().setStackInSlot(0, new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId)), 1));
            landerSpawn.getInventory().setStackInSlot(1, rocketItem);

            if (SpaceStation) {
                createSpaceStation(player, (ServerLevel) world);
            }

            cleanUpPlayerNBT(player);

            player.startRiding(landerSpawn);
        }
    }

    public static void createSpaceStation(Player player, ServerLevel serverWorld) {
        BlockPos pos = new BlockPos(player.getX() - 15.5, 100, player.getZ() - 15.5);
        serverWorld.getStructureManager().getOrCreate(new ResourceLocation(BeyondEarthMod.MODID, "space_station")).placeInWorld(serverWorld, pos, pos, new StructurePlaceSettings(), serverWorld.random, 2);
    }

    public static void cleanUpPlayerNBT(Player player) {
        player.getPersistentData().putBoolean(BeyondEarthMod.MODID + ":planet_selection_gui_open", false);
        player.getPersistentData().putString(BeyondEarthMod.MODID + ":rocket_type", "");
        player.getPersistentData().putString(BeyondEarthMod.MODID + ":slot0", "");
    }

    public static void openPlanetGui(Player player) {
        if (!(player.containerMenu instanceof PlanetSelectionGui.GuiContainer) && player.getPersistentData().getBoolean(BeyondEarthMod.MODID + ":planet_selection_gui_open")) {
            if (player instanceof ServerPlayer) {

                NetworkHooks.openGui((ServerPlayer) player, new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return new TextComponent("Planet Selection");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
                        packetBuffer.writeUtf(player.getPersistentData().getString(BeyondEarthMod.MODID + ":rocket_type"));
                        return new PlanetSelectionGui.GuiContainer(id, inventory, packetBuffer);
                    }
                }, buf -> {
                    buf.writeUtf(player.getPersistentData().getString(BeyondEarthMod.MODID + ":rocket_type"));
                });
            }
        }
    }

    public static void teleportButton(Player player, ResourceKey<Level> planet, boolean SpaceStation) {
        ItemStack itemStack = new ItemStack(Items.AIR, 1);

        if (player.getPersistentData().getString(BeyondEarthMod.MODID + ":rocket_type").equals("entity." + BeyondEarthMod.MODID + ".rocket_t1")) {
            itemStack = new ItemStack(ModInit.TIER_1_ROCKET_ITEM.get(),1);
        }

        else if (player.getPersistentData().getString(BeyondEarthMod.MODID + ":rocket_type").equals("entity." + BeyondEarthMod.MODID + ".rocket_t2")) {
            itemStack = new ItemStack(ModInit.TIER_2_ROCKET_ITEM.get(),1);
        }

        else if (player.getPersistentData().getString(BeyondEarthMod.MODID + ":rocket_type").equals("entity." + BeyondEarthMod.MODID + ".rocket_t3")) {
            itemStack = new ItemStack(ModInit.TIER_3_ROCKET_ITEM.get(),1);
        }

        else if (player.getPersistentData().getString(BeyondEarthMod.MODID + ":rocket_type").equals("entity." + BeyondEarthMod.MODID + ".rocket_t4")) {
            itemStack = new ItemStack(ModInit.TIER_4_ROCKET_ITEM.get(),1);
        }

        Methods.rocketTeleport(player, planet, itemStack, SpaceStation);
    }

    public static void landerTeleportOrbit(Player player, Level world) {
        if (Methods.isWorld(world, Methods.overworld_orbit)) {
            Methods.landerTeleport(player, Methods.overworld);
        }
        else if (Methods.isWorld(world, Methods.moon_orbit)) {
            Methods.landerTeleport(player, Methods.moon);
        }
        else if (Methods.isWorld(world, Methods.mars_orbit)) {
            Methods.landerTeleport(player, Methods.mars);
        }
        else if (Methods.isWorld(world, Methods.glacio_orbit)) {
            Methods.landerTeleport(player, Methods.glacio);
        }
        else if (Methods.isWorld(world, Methods.mercury_orbit)) {
            Methods.landerTeleport(player, Methods.mercury);
        }
        else if (Methods.isWorld(world, Methods.venus_orbit)) {
            Methods.landerTeleport(player, Methods.venus);
        }
    }

    public static void playerFalltoPlanet(Level world, Player player) {
        ResourceKey<Level> world2 = world.dimension();

        if (world2 == Methods.overworld_orbit) {
            Methods.worldTeleport(player, Methods.overworld, 450);
        }

        else if (world2 == Methods.moon_orbit) {
            Methods.worldTeleport(player, Methods.moon, 450);
        }

        else if (world2 == Methods.mars_orbit) {
            Methods.worldTeleport(player, Methods.mars, 450);
        }

        else if (world2 == Methods.mercury_orbit) {
            Methods.worldTeleport(player, Methods.mercury, 450);
        }

        else if (world2 == Methods.venus_orbit) {
            Methods.worldTeleport(player, Methods.venus, 450);
        }

        else if (world2 == Methods.glacio_orbit) {
            Methods.worldTeleport(player, Methods.glacio, 450);
        }
    }

	public static void extractArmorOxygenUsingTimer(final ItemStack itemstack, final Player player) {
		if (!player.getAbilities().instabuild && !player.isSpectator() && Config.PLAYER_OXYGEN_SYSTEM.get() && Methods.spaceSuitCheckBoth(player) && !player.hasEffect(ModInit.OXYGEN_EFFECT.get()) && (Methods.isSpaceWorldWithoutOxygen(player.level) || player.isEyeInFluid(FluidTags.WATER))) {
			IOxygenStorage oxygenStorage = OxygenUtil.getItemStackOxygenStorage(itemstack);

            CompoundTag persistentData = player.getPersistentData();
			String key = BeyondEarthMod.MODID + ":oxygen_timer";
			int oxygenTimer = persistentData.getInt(key);
			oxygenTimer++;

			if (oxygenStorage.getOxygenStored() > 0 && oxygenTimer > 3) {
				oxygenStorage.extractOxygen(1, false);
				oxygenTimer = 0;
			}

			persistentData.putInt(key, oxygenTimer);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void renderArm(PoseStack poseStack, MultiBufferSource bufferSource, int light, ResourceLocation texture, AbstractClientPlayer player, PlayerModel<AbstractClientPlayer> playermodel, ModelPart arm) {
        Methods.setModelProperties(player, playermodel);
        playermodel.attackTime = 0.0F;
        playermodel.crouching = false;
        playermodel.swimAmount = 0.0F;
        playermodel.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        arm.xRot = 0.0F;

        ItemStack item = player.getItemBySlot(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, 2));

        VertexConsumer vertex = ItemRenderer.getArmorFoilBuffer(bufferSource, RenderType.armorCutoutNoCull(texture), false, item.isEnchanted());
        arm.render(poseStack, vertex, light, OverlayTexture.NO_OVERLAY);
    }

    @OnlyIn(Dist.CLIENT)
    public static void setModelProperties(AbstractClientPlayer p_117819_, PlayerModel<AbstractClientPlayer> modelPart) {
        PlayerModel<AbstractClientPlayer> playermodel = modelPart;
        if (p_117819_.isSpectator()) {
            playermodel.setAllVisible(false);
            playermodel.head.visible = true;
            playermodel.hat.visible = true;
        } else {
            playermodel.setAllVisible(true);
            playermodel.hat.visible = p_117819_.isModelPartShown(PlayerModelPart.HAT);
            playermodel.jacket.visible = p_117819_.isModelPartShown(PlayerModelPart.JACKET);
            playermodel.leftPants.visible = p_117819_.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
            playermodel.rightPants.visible = p_117819_.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
            playermodel.leftSleeve.visible = p_117819_.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
            playermodel.rightSleeve.visible = p_117819_.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
            playermodel.crouching = p_117819_.isCrouching();
            HumanoidModel.ArmPose humanoidmodel$armpose = getArmPose(p_117819_, InteractionHand.MAIN_HAND);
            HumanoidModel.ArmPose humanoidmodel$armpose1 = getArmPose(p_117819_, InteractionHand.OFF_HAND);
            if (humanoidmodel$armpose.isTwoHanded()) {
                humanoidmodel$armpose1 = p_117819_.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
            }

            if (p_117819_.getMainArm() == HumanoidArm.RIGHT) {
                playermodel.rightArmPose = humanoidmodel$armpose;
                playermodel.leftArmPose = humanoidmodel$armpose1;
            } else {
                playermodel.rightArmPose = humanoidmodel$armpose1;
                playermodel.leftArmPose = humanoidmodel$armpose;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static HumanoidModel.ArmPose getArmPose(AbstractClientPlayer p_117795_, InteractionHand p_117796_) {
        ItemStack itemstack = p_117795_.getItemInHand(p_117796_);
        if (itemstack.isEmpty()) {
            return HumanoidModel.ArmPose.EMPTY;
        } else {
            if (p_117795_.getUsedItemHand() == p_117796_ && p_117795_.getUseItemRemainingTicks() > 0) {
                final UseAnim useanim = itemstack.getUseAnimation();
                switch (useanim) {
                    case BLOCK: return HumanoidModel.ArmPose.BLOCK;
                    case BOW: return HumanoidModel.ArmPose.BOW_AND_ARROW;
                    case SPEAR: return HumanoidModel.ArmPose.THROW_SPEAR;
                    case CROSSBOW: {
                        if (p_117796_ == p_117795_.getUsedItemHand()) {
                            return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                        }
                        break;
                    }
                    case SPYGLASS: return HumanoidModel.ArmPose.SPYGLASS;
                }
            } else if (!p_117795_.swinging && itemstack.is(Items.CROSSBOW) && CrossbowItem.isCharged(itemstack)) {
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }

            return HumanoidModel.ArmPose.ITEM;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean checkSound(SoundSource sound) {
        return sound == SoundSource.BLOCKS || sound == SoundSource.NEUTRAL || sound == SoundSource.RECORDS || sound == SoundSource.WEATHER || sound == SoundSource.HOSTILE || sound == SoundSource.PLAYERS || sound == SoundSource.AMBIENT;
    }
}
