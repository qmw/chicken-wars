# Chicken Wars — Fabric mod project

## The mod
Mod id `chickenwars`, package `dev.piotr.chickenwars`. Five throwable chicken
items (`ThrowableChickenItem`, one per `ChickenType`): thrown chickens ride an
invisible carrier projectile (`ChickenProjectileEntity`) as a live vanilla
chicken passenger, fly a ballistic arc (~105 blocks max at ~35°, tuned via
simulation in scripts — speed 2.4, default gravity), lay 3 `WarEggEntity` at
ticks 15/30/45, and die on landing. Egg effects by type: explode, terrain blob,
freeze, mob spawn, gold/diamond conversion.

## Environment
Same as ../my-first-mod (Bananarang): JDK 25 at `~/.local/jdks/jdk-25.0.3+9`,
MC 26.1.2, Fabric. **Read ../my-first-mod/CLAUDE.md for the 26.x API notes**
(Identifier rename, setId, hurtServer, javap-the-deobf-jar trick).
26.x quirks found here: adult chicken sounds are variant-based (only
`CHICKEN_EGG` and `*_BABY` sound constants exist); `startRiding` is now
3-arg (entity, force, ?3rd?) — semantics of 3rd arg unverified.

## Commands
- `python3 scripts/gen_assets.py .` regenerates all textures + asset JSONs + lang
- `./gradlew build` / `runClient` / `runServer`

## Status (2026-06-13)
- Builds clean; server smoke test pending/passed (check git log).
- NOT yet play-tested: chicken passenger attachment (startRiding 3rd arg),
  egg drop timing feel, effect balance.
- No GitHub repo yet; no store listings yet.
