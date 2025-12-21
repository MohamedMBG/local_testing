# BB_Mario

A lightweight JavaFX platformer prototype inspired by classic Mario games. The project is split between two main packages:
- `game.core` (owned by Monssef): window setup, game loop, player, HUD, input, physics helpers.
- `game.systems` (owned by Baghdad): world/level systems such as camera, tile map, collectibles, UI overlays, and loading utilities.

## Architecture

- **Entry point (`game.core.Game`)**
  - Builds the JavaFX scene graph, creates the level from inline text, wires player/ground/HUD/canvas layers, and starts the `GameLoop`.
  - Composes systems: `LevelLoader` → `TileMap`, `Camera`, `CoinManager`, `PowerUpManager`, `UIManager`, `GameWorld`.
- **Game loop (`game.core.GameLoop`)**
  - Runs on `AnimationTimer`: reads input → moves player with `Physics.moveAndCollide` (tile collisions first on X then Y) → clamps to map → updates world (coins/power-ups/UI) → syncs camera offsets to the world layer → renders overlays on a canvas.
- **Player + physics (`game.core.Player`, `Physics`, `Ground`)**
  - Axis-aligned rectangle player with velocities, gravity, jump, movement, bounds clamping.
  - Physics handles tile-based collision (tile size = 32 px) and optional ground-rectangle fallback.
- **Input (`game.core.InputManager`)**
  - Keyboard controls: left `←/Q`, right `→/D`, jump `Space/↑/Z`.
- **HUD (`game.core.HUD`)**
  - Static score text anchored to the screen.
- **World systems (`game.systems`)**
  - `TileMap`: integer grid parsed from characters, exposes collision helpers and pixel/tile dimensions.
  - `LevelLoader`: converts text-map lines to `TileMap`, player spawn, coin and power-up spawn lists.
  - `GameWorld`: orchestrates camera following, collectible updates, and UI rendering on the overlay canvas.
  - `Camera`: smooth follow with lerp; exposes offsets so the world layer can be translated.
  - `Coin`, `CoinManager`, `Collectible`: AABB collection; manager spawns coins and renders them relative to camera.
  - `PowerUp`, `PowerUpType`, `PowerUpManager`: same pattern as coins, returns collected types for future player effects.
  - `UIManager`: draws the coin count (hook for more UI elements).
  - `PopupText`: simple timed floating text (not yet wired into the loop).
  - `GameOverScreen`, `MenuScreen`: placeholders for future scenes.
- **Utilities (`game.utils`)**
  - `MathUtils`: clamp/lerp/intersection helpers.
  - `Constants`: shared values (tile size, gravity, player speed, coin score) — note some core classes also embed their own constants.
- **Modules**
  - `module-info.java` exports `game.core` and requires JavaFX + FXGL (FXGL is currently unused).

## Level format

Levels are defined as a list of equal-length strings (see `Game.start`). Supported symbols:
- `#`: solid tile (collides)
- `.` or space: empty
- `P`: player spawn (last occurrence wins)
- `C`: coin spawn
- `U`: power-up spawn

All coordinates are converted to world pixels by multiplying tile indices by `TileMap.TILE_SIZE` (32 px).

## Running locally

Prerequisites: JDK 23+ and Maven.

Commands (from repo root):
- Run with JavaFX plugin, pointing to the main class:
  - `mvn clean compile javafx:run -Djavafx.main.class=game.core.Game`
  - If using modules, also pass `-DmainClass=game.core.Game`.
- Or run directly from your IDE by launching `game.core.Game` as a JavaFX application.

## Controls

- Move left: `←` or `Q`
- Move right: `→` or `D`
- Jump: `Space`, `↑`, or `Z`

## Extending the project

- **Levels**: replace the inline `rawLines` in `Game` or load from a file, then reuse `LevelLoader`.
- **Art & rendering**: swap placeholder shapes/ovals/rects in `GameWorld`, `CoinManager`, and `PowerUpManager` with sprites.
- **Player abilities**: use `PowerUpManager.updateAndGetCollected` to apply effects per `PowerUpType`.
- **UI**: expand `UIManager` and `HUD` for timers, lives, and power-up indicators.

## Full project documentation

For a complete tour of the runtime, systems, and level format, see [docs/PROJECT_DOCUMENTATION.md](docs/PROJECT_DOCUMENTATION.md).

