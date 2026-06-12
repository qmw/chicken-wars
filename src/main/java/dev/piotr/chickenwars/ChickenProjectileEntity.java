package dev.piotr.chickenwars;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.HitResult;

/**
 * The invisible carrier the thrown chicken rides. Flies a ballistic arc,
 * lays three war eggs along the way, and on landing the chicken pays the price.
 */
public class ChickenProjectileEntity extends ThrowableItemProjectile {
	private static final int[] EGG_DROP_TICKS = {15, 30, 45};
	private static final int MAX_LIFETIME_TICKS = 400;

	private ChickenType chickenType = ChickenType.EXPLOSIVE;
	private Chicken passengerChicken;
	private int age;
	private int eggsLaid;

	public ChickenProjectileEntity(EntityType<? extends ChickenProjectileEntity> type, Level level) {
		super(type, level);
	}

	public ChickenProjectileEntity(ServerLevel level, LivingEntity owner, ItemStack stack) {
		super(ChickenWars.CHICKEN_PROJECTILE, owner, level, stack);
		if (stack.getItem() instanceof ThrowableChickenItem chickenItem) {
			this.chickenType = chickenItem.type;
		}
	}

	public void setPassengerChicken(Chicken chicken) {
		this.passengerChicken = chicken;
	}

	@Override
	protected Item getDefaultItem() {
		return ChickenWars.CHICKENS.get(ChickenType.EXPLOSIVE);
	}

	@Override
	public void tick() {
		super.tick();
		if (!(level() instanceof ServerLevel serverLevel)) {
			return;
		}
		age++;
		if (age > MAX_LIFETIME_TICKS) {
			land(serverLevel);
			return;
		}
		if (eggsLaid < EGG_DROP_TICKS.length && age >= EGG_DROP_TICKS[eggsLaid]) {
			layEgg(serverLevel);
		}
	}

	@Override
	protected void onHit(HitResult result) {
		if (level() instanceof ServerLevel serverLevel) {
			land(serverLevel);
		}
	}

	private void layEgg(ServerLevel level) {
		eggsLaid++;
		WarEggEntity egg = new WarEggEntity(level, getX(), getY() - 0.3, getZ(), chickenType, getOwner());
		// inherit a touch of forward momentum, then fall
		egg.setDeltaMovement(getDeltaMovement().scale(0.3).add(
				(level.getRandom().nextDouble() - 0.5) * 0.1, -0.1,
				(level.getRandom().nextDouble() - 0.5) * 0.1));
		level.addFreshEntity(egg);
		level.playSound(null, getX(), getY(), getZ(),
				SoundEvents.CHICKEN_EGG, SoundSource.NEUTRAL, 1.0F, 1.0F);
	}

	private void land(ServerLevel level) {
		if (passengerChicken != null && passengerChicken.isAlive()) {
			passengerChicken.stopRiding();
			// gravity always wins; the chicken's own death cry plays vanilla-style
			passengerChicken.hurtServer(level, damageSources().fall(), 100.0F);
		}
		discard();
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.putInt("ChickenType", chickenType.ordinal());
		output.putInt("EggsLaid", eggsLaid);
		output.putInt("Age", age);
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		chickenType = ChickenType.byOrdinal(input.getIntOr("ChickenType", 0));
		eggsLaid = input.getIntOr("EggsLaid", 0);
		age = input.getIntOr("Age", 0);
	}
}
