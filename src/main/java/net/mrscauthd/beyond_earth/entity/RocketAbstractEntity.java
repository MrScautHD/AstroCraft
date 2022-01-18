package net.mrscauthd.beyond_earth.entity;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.EntityArmorInvWrapper;
import net.minecraftforge.items.wrapper.EntityHandsInvWrapper;
import net.minecraftforge.network.NetworkHooks;
import net.mrscauthd.beyond_earth.BeyondEarthMod;
import net.mrscauthd.beyond_earth.ModInit;
import net.mrscauthd.beyond_earth.block.RocketLaunchPad;
import net.mrscauthd.beyond_earth.events.Methods;
import net.mrscauthd.beyond_earth.fluid.FluidUtil2;
import net.mrscauthd.beyond_earth.gauge.IGaugeValue;
import net.mrscauthd.beyond_earth.gui.screens.rocket.RocketGui;
import net.mrscauthd.beyond_earth.item.RocketAbstractItem;

public abstract class RocketAbstractEntity extends PathfinderMob {

	public double ar = 0;
	public double ay = 0;
	public double ap = 0;

	public static final EntityDataAccessor<Boolean> ROCKET_START = SynchedEntityData.defineId(RocketAbstractEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Integer> BUCKETS = SynchedEntityData.defineId(RocketAbstractEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> FUEL = SynchedEntityData.defineId(RocketAbstractEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> START_TIMER = SynchedEntityData.defineId(RocketAbstractEntity.class, EntityDataSerializers.INT);

	public RocketAbstractEntity(EntityType<? extends RocketAbstractEntity> type, Level world) {
		super(type, world);
		this.entityData.define(ROCKET_START, false);
		this.entityData.define(BUCKETS, 0);
		this.entityData.define(FUEL, 0);
		this.entityData.define(START_TIMER, 0);
	}

	public int getRocketTier() {
		return this.getRocketItem().getRocketTier();
	}

	public int getFuelBucketsOfFull() {
		return this.getRocketItem().getFuelBucketsOfFull();
	}

	public int getFuelAmountPerBucket() {
		return this.getRocketItem().getFuelAmountPerBucket();
	}

	public int getFuelLoadSpeed() {
		return this.getRocketItem().getFuelLoadSpeed();
	}

	public IGaugeValue getFuelGauge() {
		return this.getRocketItem().getFuelGauge(this.getFuelAmount());
	}

	public abstract double getRocketSpeed();

	public static AttributeSupplier.Builder setCustomAttributes() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20);
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public boolean canBeLeashed(Player p_21418_) {
		return false;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	protected void doPush(Entity p_20971_) {
	}

	@Override
	public void push(Entity p_21294_) {
	}

	@Deprecated
	public boolean canBeRiddenInWater() {
		return true;
	}

	@Override
	public boolean isAffectedByPotions() {
		return false;
	}

	@Override
	protected void onEffectAdded(MobEffectInstance p_147190_, @Nullable Entity p_147191_) {
	}

	@Override
	public boolean addEffect(MobEffectInstance p_147208_, @Nullable Entity p_147209_) {
		return false;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
	}

	@Override
	public MobType getMobType() {
		return MobType.UNDEFINED;
	}

	@Override
	protected MovementEmission getMovementEmission() {
		return MovementEmission.NONE;
	}

	@Override
	public boolean removeWhenFarAway(double p_21542_) {
		return false;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource p_21239_) {
		return null;
	}

	@Override
	public SoundEvent getDeathSound() {
		return null;
	}

	@Nullable
	@Override
	public ItemStack getPickResult() {
		RocketAbstractItem item = this.getRocketItem();
		ItemStack itemStack = new ItemStack(item);
		item.fetchItemNBT(itemStack, this);

		return itemStack;
	}

	public abstract RocketAbstractItem getRocketItem();

	@Override
	public Vec3 getDismountLocationForPassenger(LivingEntity livingEntity) {
		Vec3[] avector3d = new Vec3[] { getCollisionHorizontalEscapeVector((double) this.getBbWidth(), (double) livingEntity.getBbWidth(), livingEntity.getYRot()), getCollisionHorizontalEscapeVector((double) this.getBbWidth(), (double) livingEntity.getBbWidth(), livingEntity.getYRot() - 22.5F), getCollisionHorizontalEscapeVector((double) this.getBbWidth(), (double) livingEntity.getBbWidth(), livingEntity.getYRot() + 22.5F), getCollisionHorizontalEscapeVector((double) this.getBbWidth(), (double) livingEntity.getBbWidth(), livingEntity.getYRot() - 45.0F), getCollisionHorizontalEscapeVector((double) this.getBbWidth(), (double) livingEntity.getBbWidth(), livingEntity.getYRot() + 45.0F) };
		Set<BlockPos> set = Sets.newLinkedHashSet();
		double d0 = this.getBoundingBox().maxY;
		double d1 = this.getBoundingBox().minY - 0.5D;
		BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

		for (Vec3 vector3d : avector3d) {
			blockpos$mutable.set(this.getX() + vector3d.x, d0, this.getZ() + vector3d.z);

			for (double d2 = d0; d2 > d1; --d2) {
				set.add(blockpos$mutable.immutable());
				blockpos$mutable.move(Direction.DOWN);
			}
		}

		for (BlockPos blockpos : set) {
			if (!this.level.getFluidState(blockpos).is(FluidTags.LAVA)) {
				double d3 = this.level.getBlockFloorHeight(blockpos);
				if (DismountHelper.isBlockFloorValid(d3)) {
					Vec3 vector3d1 = Vec3.upFromBottomCenterOf(blockpos, d3);

					for (Pose pose : livingEntity.getDismountPoses()) {
						if (DismountHelper.isBlockFloorValid(this.level.getBlockFloorHeight(blockpos))) {
							livingEntity.setPose(pose);
							return vector3d1;
						}
					}
				}
			}
		}

		return new Vec3(this.getX(), this.getBoundingBox().maxY, this.getZ());
	}

	@Override
	public void kill() {
		this.dropEquipment();
		this.spawnRocketItem();
		this.remove(RemovalReason.DISCARDED);
	}

	@Override
	public boolean hurt(DamageSource source, float p_21017_) {
		Entity sourceentity = source.getEntity();

		if (!source.isProjectile() && sourceentity != null && sourceentity.isCrouching() && !this.isVehicle()) {

			this.spawnRocketItem();
			this.dropEquipment();
			this.remove(RemovalReason.DISCARDED);
		}

		return false;
	}

	protected void spawnRocketItem() {
		if (!level.isClientSide) {
			ItemStack itemStack = this.getPickResult();
			ItemEntity entityToSpawn = new ItemEntity(level, this.getX(), this.getY(), this.getZ(), itemStack);
			entityToSpawn.setPickUpDelay(10);
			level.addFreshEntity(entityToSpawn);
		}
	}

	@Override
	protected void dropEquipment() {
		super.dropEquipment();
		for (int i = 0; i < inventory.getSlots(); ++i) {
			ItemStack itemstack = inventory.getStackInSlot(i);
			if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack)) {
				this.spawnAtLocation(itemstack);
			}
		}
	}

	private final ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		public int getSlotLimit(int slot) {
			return 64;
		}
	};

	private final CombinedInvWrapper combined = new CombinedInvWrapper(inventory, new EntityHandsInvWrapper(this), new EntityArmorInvWrapper(this));

	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
		if (this.isAlive() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side == null) {
			return LazyOptional.of(() -> combined).cast();
		}
		return super.getCapability(capability, side);
	}

	public IItemHandlerModifiable getItemHandler() {
		return (IItemHandlerModifiable) this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).resolve().get();
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.put("InventoryCustom", inventory.serializeNBT());

		compound.putBoolean("rocket_start", this.entityData.get(ROCKET_START));
		compound.putInt("buckets", this.entityData.get(BUCKETS));
		compound.putInt("fuel", this.entityData.get(FUEL));
		compound.putInt("start_timer", this.entityData.get(START_TIMER));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		Tag inventoryCustom = compound.get("InventoryCustom");
		if (inventoryCustom instanceof CompoundTag) {
			inventory.deserializeNBT((CompoundTag) inventoryCustom);
		}

		this.entityData.set(ROCKET_START, compound.getBoolean("rocket_start"));
		this.entityData.set(BUCKETS, compound.getInt("buckets"));
		this.entityData.set(FUEL, compound.getInt("fuel"));
		this.entityData.set(START_TIMER, compound.getInt("start_timer"));
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		super.mobInteract(player, hand);
		InteractionResult retval = InteractionResult.sidedSuccess(this.level.isClientSide);

		if (player instanceof ServerPlayer && player.isCrouching()) {

			NetworkHooks.openGui((ServerPlayer) player, new MenuProvider() {
				@Override
				public Component getDisplayName() {
					return new TranslatableComponent("container.entity." + BeyondEarthMod.MODID + ".rocket_t" + getRocketTier());
				}

				@Override
				public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
					FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
					packetBuffer.writeVarInt(RocketAbstractEntity.this.getId());
					return new RocketGui.GuiContainer(id, inventory, packetBuffer);
				}
			}, buf -> {
				buf.writeVarInt(this.getId());
			});

			return retval;
		}

		player.startRiding(this);
		return retval;
	}

	@Override
	public void baseTick() {
		super.baseTick();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();

		if (this.entityData.get(ROCKET_START)) {

			// Rocket Animation
			ar = ar + 1;
			if (ar == 1) {
				ay = ay + 0.006;
				ap = ap + 0.006;
			}
			if (ar == 2) {
				ar = 0;
				ay = 0;
				ap = 0;
			}

			if (this.entityData.get(START_TIMER) < 200) {
				this.entityData.set(START_TIMER, this.entityData.get(START_TIMER) + 1);
			}

			if (this.entityData.get(START_TIMER) == 200) {
				if (this.getDeltaMovement().y() < this.getRocketSpeed() - 0.1) {
					this.setDeltaMovement(this.getDeltaMovement().x, this.getDeltaMovement().y + 0.1, this.getDeltaMovement().z);
				} else {
					this.setDeltaMovement(this.getDeltaMovement().x, this.getRocketSpeed(), this.getDeltaMovement().z);
				}
			}

			if (y > 600 && !this.getPassengers().isEmpty()) {
				Entity pass = this.getPassengers().get(0);
				CompoundTag persistentData = pass.getPersistentData();
				persistentData.putBoolean(BeyondEarthMod.MODID + ":planet_selection_gui_open", true);
				persistentData.putString(BeyondEarthMod.MODID + ":rocket_type", this.getType().toString());
				persistentData.putString(BeyondEarthMod.MODID + ":slot0", this.inventory.getStackInSlot(0).getItem().getRegistryName().toString());
				pass.setNoGravity(true);

				this.remove(RemovalReason.DISCARDED);
			} else if (y > 600 && this.getPassengers().isEmpty()) {
				this.remove(RemovalReason.DISCARDED);
			}

			Vec3 vec = this.getDeltaMovement();

			// Particle Spawn

			if (level instanceof ServerLevel) {
				ServerLevel serverlevel = (ServerLevel) level;

				for (ServerPlayer p : serverlevel.getServer().getPlayerList().getPlayers()) {
					if (this.entityData.get(START_TIMER) == 200) {
						this.sendParticlesFlying(vec, serverlevel, p);
					} else {
						this.sendParticlesStarting(vec, serverlevel, p);
					}
				}
			}
		}

		this.loadFuel();

		if (this.isOnGround() || this.isInWater()) {

			BlockState state = level.getBlockState(new BlockPos(Math.floor(x), y - 0.1, Math.floor(z)));

			if (!level.isEmptyBlock(new BlockPos(Math.floor(x), y - 0.01, Math.floor(z))) && state.getBlock() instanceof RocketLaunchPad && !state.getValue(RocketLaunchPad.STAGE) || level.getBlockState(new BlockPos(Math.floor(x), Math.floor(y), Math.floor(z))).getBlock() != ModInit.ROCKET_LAUNCH_PAD.get().defaultBlockState().getBlock()) {

				this.dropEquipment();
				this.spawnRocketItem();
				this.remove(RemovalReason.DISCARDED);
			}
		}
	}

	protected void sendParticlesFlying(Vec3 vec, ServerLevel serverlevel, ServerPlayer p) {
	}

	protected void sendParticlesStarting(Vec3 vec, ServerLevel serverlevel, ServerPlayer p) {
		serverlevel.sendParticles(p, ParticleTypes.CAMPFIRE_COSY_SMOKE, true, this.getX() - vec.x, this.getY() - vec.y - 0.1, this.getZ() - vec.z, 6, 0.1, 0.1, 0.1, 0.023);
	}

	protected void loadFuel() {

		int buckets = this.getFuelBuckets();
		int fuel = this.getFuelAmount();

		if (fuel == this.getRocketItem().getFuelAmountOfBucket(buckets) && buckets < this.getFuelBucketsOfFull()) {
			ItemStackHandler inventory = this.inventory;
			int slot = 0;

			if (Methods.tagCheck(FluidUtil2.findBucketFluid(inventory.getStackInSlot(slot).getItem()), ModInit.FLUID_VEHICLE_FUEL_TAG)) {
				inventory.setStackInSlot(slot, new ItemStack(Items.BUCKET));
				buckets += 1;
				this.setFuelBuckets(buckets);
			}

		}

		if (fuel < this.getRocketItem().getFuelAmountOfBucket(buckets)) {
			fuel += 1;
			this.setFuelAmount(fuel);
		}

	}

	public int getFuelAmount() {
		return this.getEntityData().get(FUEL);
	}

	public void setFuelAmount(int amount) {
		this.getEntityData().set(FUEL, amount);
	}

	public int getFuelBuckets() {
		return this.getEntityData().get(BUCKETS);
	}

	public void setFuelBuckets(int buckets) {
		this.getEntityData().set(BUCKETS, buckets);
	}
}
