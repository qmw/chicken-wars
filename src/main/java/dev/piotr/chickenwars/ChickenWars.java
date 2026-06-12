package dev.piotr.chickenwars;

import java.util.EnumMap;
import java.util.Map;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChickenWars implements ModInitializer {
	public static final String MOD_ID = "chickenwars";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Map<ChickenType, Item> CHICKENS = new EnumMap<>(ChickenType.class);

	static {
		for (ChickenType type : ChickenType.values()) {
			ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
					Identifier.fromNamespaceAndPath(MOD_ID, type.id));
			CHICKENS.put(type, new ThrowableChickenItem(type,
					new Item.Properties().setId(key).stacksTo(16)));
		}
	}

	public static final ResourceKey<EntityType<?>> CHICKEN_PROJECTILE_KEY =
			ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, "chicken_projectile"));
	public static final EntityType<ChickenProjectileEntity> CHICKEN_PROJECTILE =
			EntityType.Builder.<ChickenProjectileEntity>of(ChickenProjectileEntity::new, MobCategory.MISC)
					.sized(0.4F, 0.4F)
					.clientTrackingRange(10)
					.updateInterval(2)
					.build(CHICKEN_PROJECTILE_KEY);

	public static final ResourceKey<EntityType<?>> WAR_EGG_KEY =
			ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, "war_egg"));
	public static final EntityType<WarEggEntity> WAR_EGG =
			EntityType.Builder.<WarEggEntity>of(WarEggEntity::new, MobCategory.MISC)
					.sized(0.25F, 0.25F)
					.clientTrackingRange(8)
					.updateInterval(4)
					.build(WAR_EGG_KEY);

	private static final ResourceKey<CreativeModeTab> COMBAT_TAB =
			ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("combat"));

	@Override
	public void onInitialize() {
		for (ChickenType type : ChickenType.values()) {
			Registry.register(BuiltInRegistries.ITEM,
					Identifier.fromNamespaceAndPath(MOD_ID, type.id), CHICKENS.get(type));
		}
		Registry.register(BuiltInRegistries.ENTITY_TYPE, CHICKEN_PROJECTILE_KEY, CHICKEN_PROJECTILE);
		Registry.register(BuiltInRegistries.ENTITY_TYPE, WAR_EGG_KEY, WAR_EGG);

		CreativeModeTabEvents.modifyOutputEvent(COMBAT_TAB).register(output -> {
			for (ChickenType type : ChickenType.values()) {
				output.accept(CHICKENS.get(type));
			}
		});

		LOGGER.info("Chicken Wars loaded. The sky will have feathers.");
	}
}
