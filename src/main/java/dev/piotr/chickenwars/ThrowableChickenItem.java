package dev.piotr.chickenwars;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ThrowableChickenItem extends Item {
	// tuned by simulating vanilla projectile physics: max range ~70 blocks
	// at a ~40° throw, while a flat throw only reaches ~20 — angle matters
	public static final float THROW_SPEED = 1.8F;
	private static final int COOLDOWN_TICKS = 30;

	public final ChickenType type;

	public ThrowableChickenItem(ChickenType type, Properties properties) {
		super(properties);
		this.type = type;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		level.playSound(null, player.getX(), player.getY(), player.getZ(),
				SoundEvents.CHICKEN_EGG, SoundSource.PLAYERS, 1.0F, 0.8F);
		if (level instanceof ServerLevel serverLevel) {
			ChickenProjectileEntity carrier = Projectile.spawnProjectileFromRotation(
					ChickenProjectileEntity::new, serverLevel, stack, player, 0.0F, THROW_SPEED, 1.0F);
			Chicken chicken = EntityType.CHICKEN.create(serverLevel, EntitySpawnReason.MOB_SUMMONED);
			if (carrier != null && chicken != null) {
				chicken.setPos(carrier.getX(), carrier.getY(), carrier.getZ());
				serverLevel.addFreshEntity(chicken);
				chicken.startRiding(carrier, true, true);
				carrier.setPassengerChicken(chicken);
			}
		}
		player.getCooldowns().addCooldown(stack, COOLDOWN_TICKS);
		stack.consume(1, player);
		return InteractionResult.SUCCESS;
	}
}
