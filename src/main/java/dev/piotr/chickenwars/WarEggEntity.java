package dev.piotr.chickenwars;

import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** An egg laid mid-flight. What hatches depends on the chicken that laid it. */
public class WarEggEntity extends ThrowableItemProjectile {
	private static final Block[] TERRA_BLOCKS = {Blocks.DIRT, Blocks.COBBLESTONE, Blocks.OAK_PLANKS};
	private static final EntityType<?>[] SPAWN_POOL = {
			EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER,
			EntityType.SLIME, EntityType.PIG, EntityType.COW, EntityType.CHICKEN};

	private ChickenType chickenType = ChickenType.EXPLOSIVE;

	public WarEggEntity(EntityType<? extends WarEggEntity> type, Level level) {
		super(type, level);
	}

	public WarEggEntity(ServerLevel level, double x, double y, double z, ChickenType chickenType, Entity owner) {
		super(ChickenWars.WAR_EGG, x, y, z, level, new ItemStack(Items.EGG));
		this.chickenType = chickenType;
		setOwner(owner);
	}

	@Override
	protected Item getDefaultItem() {
		return Items.EGG;
	}

	@Override
	protected void onHit(HitResult result) {
		if (level() instanceof ServerLevel level) {
			applyEffect(level, position(), getOwner(), chickenType);
			discard();
		}
	}

	/** The payload, shared by eggs and the chicken's own landing. */
	public static void applyEffect(ServerLevel level, Vec3 pos, Entity owner, ChickenType type) {
		BlockPos center = BlockPos.containing(pos.x, pos.y, pos.z);
		switch (type) {
			case EXPLOSIVE -> level.explode(owner, pos.x, pos.y, pos.z, 2.5F, Level.ExplosionInteraction.TNT);
			case TERRA -> terraBlob(level, center);
			case FROST -> freeze(level, center, owner);
			case SPAWNER -> spawnMobs(level, center);
			case MIDAS -> midasTouch(level, center);
		}
	}

	private static void terraBlob(ServerLevel level, BlockPos center) {
		Block material = TERRA_BLOCKS[level.getRandom().nextInt(TERRA_BLOCKS.length)];
		forSphere(center, 2.5, pos -> {
			BlockState state = level.getBlockState(pos);
			if (state.isAir() || state.canBeReplaced()) {
				level.setBlockAndUpdate(pos, material.defaultBlockState());
			}
		});
	}

	private static void freeze(ServerLevel level, BlockPos center, Entity owner) {
		forSphere(center, 3.0, pos -> {
			BlockState state = level.getBlockState(pos);
			if (state.is(Blocks.WATER)) {
				level.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
			} else if (state.is(Blocks.LAVA)) {
				level.setBlockAndUpdate(pos, Blocks.OBSIDIAN.defaultBlockState());
			} else if (state.isAir() && level.getBlockState(pos.below()).isSolidRender()) {
				level.setBlockAndUpdate(pos, Blocks.SNOW.defaultBlockState());
			}
		});
		for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
				new AABB(center).inflate(4.0), e -> e != owner && e.isAlive())) {
			target.setTicksFrozen(Math.max(target.getTicksFrozen(), 400));
		}
	}

	private static void spawnMobs(ServerLevel level, BlockPos center) {
		RandomSource random = level.getRandom();
		int count = 1 + random.nextInt(3);
		for (int i = 0; i < count; i++) {
			EntityType<?> type = SPAWN_POOL[random.nextInt(SPAWN_POOL.length)];
			type.spawn(level, center, EntitySpawnReason.MOB_SUMMONED);
		}
	}

	private static void midasTouch(ServerLevel level, BlockPos center) {
		RandomSource random = level.getRandom();
		forSphere(center, 2.0, pos -> {
			BlockState state = level.getBlockState(pos);
			if (!state.isAir() && state.getDestroySpeed(level, pos) >= 0.0F && state.isSolidRender()) {
				Block treasure = random.nextFloat() < 0.1F ? Blocks.DIAMOND_BLOCK : Blocks.GOLD_BLOCK;
				level.setBlockAndUpdate(pos, treasure.defaultBlockState());
			}
		});
	}

	private static void forSphere(BlockPos center, double radius, Consumer<BlockPos> action) {
		int r = (int) Math.ceil(radius);
		for (BlockPos pos : BlockPos.betweenClosed(center.offset(-r, -r, -r), center.offset(r, r, r))) {
			if (pos.distSqr(center) <= radius * radius) {
				action.accept(pos.immutable());
			}
		}
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.putInt("ChickenType", chickenType.ordinal());
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		chickenType = ChickenType.byOrdinal(input.getIntOr("ChickenType", 0));
	}
}
