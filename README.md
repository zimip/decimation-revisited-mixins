# Decimation: Mixin Revisited

Some side project i worked on for quite some time, that in the end, didn't really came to life.

This project works by applying bytecode patches at runtime, without ever touching or redistributing modified binaries.

> [!IMPORTANT]
> **LEGAL NOTICE**
>
> This repository **DOES NOT** contain any copyrighted assets or modified binaries belonging to the original creators (*BoehMod* / Decimation Team). All patches are applied at runtime via Mixin and require the user to provide their own official Decimation 1.7.10 `.jar` file. No decompiled or redistributed original code is included in this repository.
>
> For any concerns regarding this project, please contact me on Discord for DMCA/takedown requests.

---

## What is this?

Forge 1.7.10 predates official Mixin support in FML — this project integrates the **SpongePowered Mixin** framework into a 1.7.10 environment to enable clean, non-invasive runtime modification of Decimation's classes.

Instead of decompiling, editing, and recompiling Decimation's source (which requires redistributing modified binaries and raises copyright issues), Mixin lets patches be:

- Applied directly to the game's bytecode at class-load time
- Kept independent of the original, unmodified `.jar`
- Distributed as pure patch logic, with zero original code included

## Why "Revisited"?

Back when "Decimation: Revival" came to life, i dreamt of making something myself seeing how succesful all those projects were. Needless to say this project didn't really fruit as much as i hoped it would.

## How it Works

1. Download the official **Decimation 1.7.10** `.jar` (not included in this repo).
2. Place it in your mods/libraries folder as instructed below.
3. This project's Mixin config hooks into Decimation's classes at launch, applying patches without ever modifying the original jar on disk.

```bash
# Clone the repository
git clone https://github.com/zimipiri/decimation_revisited_mixins.git

# Navigate to the directory
cd decimation_revisited_mixins

# Setup ForgeGradle workspace
./gradlew setupDecompWorkspace

# Build the mod
./gradlew build
```

## Requirements

- Minecraft Forge **1.7.10**
- Official **Decimation 1.21.10f (LATEST)** `.jar` (deobfuscated, and provided by the user)
- SpongePowered Mixin (Bundled into the project)

---

## Disclaimer

This is an unofficial, community-driven project not affiliated with or endorsed by the original Decimation developers. Use at your own risk; patched behavior may differ from vanilla Decimation and is not guaranteed to be stable or bug-free.
