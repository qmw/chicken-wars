# Chicken Wars 🐔

Throwable tactical chickens. Aim well — angle is everything.

A [Fabric](https://fabricmc.net/) mod for Minecraft Java Edition 26.1.2.

## What it does

Throw a chicken and it sails up to **a hundred blocks** on a true ballistic
arc — a real, live, clucking chicken, visibly flying through the sky. The
optimal throw is around 35°; a flat throw barely makes 25 blocks.

Mid-flight, every chicken lays **three war eggs**. What the eggs do depends
on who laid them:

| Chicken | Egg effect |
|---|---|
| 🔴 **Explosive Chicken** | Eggs detonate like TNT |
| 🟤 **Terra Chicken** | Eggs splat into blobs of terrain |
| 🔵 **Frost Chicken** | Eggs freeze water, ground, and anything alive nearby |
| 🟣 **Spawner Chicken** | Eggs hatch 1–3 random mobs — pig or creeper, feeling lucky? |
| 🟡 **Midas Chicken** | Eggs turn the ground to gold (and sometimes diamond) |

When the chicken lands, it dies. It knew the job was dangerous when it took it.

Find all five in the **Combat** creative tab, or
`/give @s chickenwars:explosive_chicken`.

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for Minecraft 26.1.2.
2. Drop `chickenwars-x.y.z.jar` and [Fabric API](https://modrinth.com/mod/fabric-api)
   into your `mods` folder.

## Development

Requires JDK 25.

```bash
./gradlew build        # jar lands in build/libs/
./gradlew runClient    # dev client
python3 scripts/gen_assets.py .   # regenerate textures + asset JSONs
```

Flight tuning (speed, egg timing, effect radii) lives in the constants of
`ThrowableChickenItem`, `ChickenProjectileEntity`, and `WarEggEntity`.

## License

MIT
