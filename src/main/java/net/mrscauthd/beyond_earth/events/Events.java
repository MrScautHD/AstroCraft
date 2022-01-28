package net.mrscauthd.beyond_earth.events;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mrscauthd.beyond_earth.BeyondEarthMod;
import net.mrscauthd.beyond_earth.ModInit;
import net.mrscauthd.beyond_earth.entity.*;
import net.mrscauthd.beyond_earth.events.forgeevents.RenderHandItemEvent;
import net.mrscauthd.beyond_earth.events.forgeevents.SetupLivingBipedAnimEvent;
import net.mrscauthd.beyond_earth.item.VehicleItem;

@Mod.EventBusSubscriber(modid = BeyondEarthMod.MODID)
public class Events {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            Level world = player.level;

            //Lander Teleport System
            if (player.getVehicle() instanceof LanderEntity) {
                Methods.landerTeleportOrbit(player, world);
            }

            //Planet Gui Open
            Methods.openPlanetGui(player);

            //Oxygen System
            OxygenSystem.OxygenSystem(player);

            //Gravity Methode Call
            Gravity.Gravity(player, Gravity.GravityType.PLAYER, world);

            //Drop Off Hand Item
            Methods.DropRocket(player);

            //Player orbit Fall Teleport
            if (player.getY() < 1 && !(player.getVehicle() instanceof LanderEntity)) {
                Methods.playerFalltoPlanet(world, player);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingEntityTick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        Level world = entity.level;

        Methods.EntityOxygen(entity,world);

        //Gravity Methode Call
        Gravity.Gravity(entity, Gravity.GravityType.LIVING, world);

        //Venus Rain
        Methods.VenusRain(entity, Methods.venus);

        //Venus Fire
        Methods.PlanetFire(entity, Methods.venus, Methods.mercury);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void CameraPos(EntityViewRenderEvent.CameraSetup event) {
        Entity ridding = event.getCamera().getEntity().getVehicle();

        if (Methods.isRocket(ridding) || ridding instanceof LanderEntity) {
            CameraType pointOfView = Minecraft.getInstance().options.getCameraType();

            if (pointOfView.equals(CameraType.THIRD_PERSON_FRONT) || pointOfView.equals(CameraType.THIRD_PERSON_BACK)) {
                event.getCamera().move(-event.getCamera().getMaxZoom(12d), 0d, 0);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void renderPlayerArm(RenderArmEvent event) {
        PlayerModel<AbstractClientPlayer> playerModel = ((PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(event.getPlayer())).getModel();

        Player player = event.getPlayer();

        Item item = player.getOffhandItem().getItem();
        Item item2 = player.getMainHandItem().getItem();

        /**Cancel Event if it Hold a Vehicle in off hand*/
        if (item instanceof VehicleItem || item2 instanceof VehicleItem) {
            event.setCanceled(true);
            return;
        }

        if (event.getArm() == HumanoidArm.RIGHT) {
            if (Methods.checkArmor(event.getPlayer(), 2, ModInit.SPACE_SUIT.get())) {

                Methods.renderArm(event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), new ResourceLocation(BeyondEarthMod.MODID, "textures/models/armor/arm/space_suit.png"), event.getPlayer(), playerModel, playerModel.rightArm);
                event.setCanceled(true);
            } else if (Methods.checkArmor(event.getPlayer(), 2, ModInit.NETHERITE_SPACE_SUIT.get())) {

                Methods.renderArm(event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), new ResourceLocation(BeyondEarthMod.MODID, "textures/models/armor/arm/netherite_space_suit.png"), event.getPlayer(), playerModel, playerModel.rightArm);
                event.setCanceled(true);
            }
        }

        if (event.getArm() == HumanoidArm.LEFT) {
            if (Methods.checkArmor(event.getPlayer(), 2, ModInit.SPACE_SUIT.get())) {

                Methods.renderArm(event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), new ResourceLocation(BeyondEarthMod.MODID, "textures/models/armor/arm/space_suit.png"), event.getPlayer(), playerModel, playerModel.leftArm);
                event.setCanceled(true);
            } else if (Methods.checkArmor(event.getPlayer(), 2, ModInit.NETHERITE_SPACE_SUIT.get())) {

                Methods.renderArm(event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), new ResourceLocation(BeyondEarthMod.MODID, "textures/models/armor/arm/netherite_space_suit.png"), event.getPlayer(), playerModel, playerModel.leftArm);
                event.setCanceled(true);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void render(RenderPlayerEvent event) {
        if (event.getEntity().getVehicle() instanceof LanderEntity) {
            event.setCanceled(true);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void setupPlayerAngles(SetupLivingBipedAnimEvent.Post event) {
        if (event.getLivingEntity() instanceof Player) {
            Player player = (Player) event.getLivingEntity();
            HumanoidModel model = event.getModel();

            //Player Rocket Sit Rotations
            if (Methods.isRocket(player.getVehicle())) {
                model.rightLeg.xRot = (float) Math.toRadians(0F);
                model.leftLeg.xRot = (float) Math.toRadians(0F);
                model.leftLeg.yRot = (float) Math.toRadians(3F);
                model.leftLeg.yRot = (float) Math.toRadians(3F);
                // Arms
                model.leftArm.xRot = -0.07f;
                model.rightArm.xRot = -0.07f;
            }

            //Player Hold Vehicles Rotation
            if (!Methods.isRocket(player.getVehicle())) {
                Item item1 = player.getMainHandItem().getItem();
                Item item2 = player.getOffhandItem().getItem();

                if (item1 instanceof VehicleItem || item2 instanceof VehicleItem) {
                    model.rightArm.xRot = 10;
                    model.leftArm.xRot = 10;
                    model.rightArm.zRot = 0;
                    model.leftArm.zRot = 0;
                    model.rightArm.yRot = 0;
                    model.leftArm.yRot = 0;
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void ItemRender(RenderHandItemEvent.Pre event) {
        if (event.getLivingEntity() instanceof Player) {
            Player player = (Player) event.getLivingEntity();

            if (Methods.isRocket(player.getVehicle())) {
                event.setCanceled(true);
            }

            Item item1 = player.getMainHandItem().getItem();
            Item item2 = player.getOffhandItem().getItem();

            if (item1 instanceof VehicleItem) {
                if (event.getHandSide() == HumanoidArm.LEFT) {
                    event.setCanceled(true);
                }
            }

            if (item2 instanceof VehicleItem) {
                if (event.getHandSide() == HumanoidArm.RIGHT) {
                    event.setCanceled(true);
                }
            }

        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Level world = event.world;

            if (Methods.isWorld(world, Methods.moon)
             || Methods.isWorld(world, Methods.moon_orbit)
             || Methods.isWorld(world, Methods.mars)
             || Methods.isWorld(world, Methods.mars_orbit)
             || Methods.isWorld(world, Methods.mercury)
             || Methods.isWorld(world, Methods.mercury_orbit)
             || Methods.isWorld(world, Methods.venus_orbit)
             || Methods.isWorld(world, Methods.glacio_orbit)
             || Methods.isWorld(world, Methods.overworld_orbit)) {
                world.thunderLevel = 0;
                world.rainLevel = 0;
            }

            if (Methods.isWorld(world, Methods.venus)) {
                world.thunderLevel = 0;
            }
        }
    }

    @SubscribeEvent
    public static void onEntityAttacked(LivingAttackEvent event) {
        if (event != null && event.getEntity() instanceof Player) {
            Player entity = (Player) event.getEntity();

            if (Methods.netheriteSpaceSuitCheck(entity)) {
                if (event.getSource().isFire()) {
                    entity.setRemainingFireTicks(0);
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void FishingBobberTick(ProjectileImpactEvent event) {
        if (event.getRayTraceResult().getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) event.getRayTraceResult()).getEntity();
            if (Methods.AllVehiclesOr(entity)) {
                event.setCanceled(true);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void SpaceSounds(PlaySoundEvent event) {
        if (event.getSound() != null) {
            if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.level != null && Methods.checkSound(event.getSound().getSource()) && Methods.isSpaceWorldWithoutOxygen(Minecraft.getInstance().player.level)) {

                if (!(event.getSound() instanceof TickableSoundInstance)) {
                    event.setSound(new SpaceSoundSystem(event.getSound()));

                } else if (event.getSound() instanceof TickableSoundInstance) {
                    event.setSound(new TickableSpaceSoundSystem((TickableSoundInstance) event.getSound()));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player && event.getEntity().getPersistentData().getBoolean(BeyondEarthMod.MODID + ":planet_selection_gui_open")) {
            ((Player) event.getEntity()).closeContainer();
            Methods.cleanUpPlayerNBT((Player) event.getEntity());
            event.getEntity().setNoGravity(false);
        }
    }
}
