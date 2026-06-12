package dev.piotr.chickenwars;

public enum ChickenType {
	EXPLOSIVE("explosive_chicken"),
	TERRA("terra_chicken"),
	FROST("frost_chicken"),
	SPAWNER("spawner_chicken"),
	MIDAS("midas_chicken");

	public final String id;

	ChickenType(String id) {
		this.id = id;
	}

	public static ChickenType byOrdinal(int ordinal) {
		ChickenType[] values = values();
		return values[Math.floorMod(ordinal, values.length)];
	}
}
