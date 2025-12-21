# Project Documentation

## Overview
This project is a lightweight JavaFX platformer inspired by classic Mario games. It renders a scrolling 2D world with procedurally generated tile maps, a controllable player character, collectibles, enemies, spikes, and simple HUD overlays. The runtime is organized into a JavaFX application (`game.core.Game`) that builds the scene graph, spawns systems for world logic, and drives updates through an `AnimationTimer` loop.

### Goals
- Provide a clear reference for how the game world is composed and updated each frame.
- Explain the responsibilities of core classes and systems.
- Document the level format and how procedural maps are produced.
- Offer extension points for adding gameplay or UI features.

## Running the Game
- Prerequisites: JDK 23+ and Maven.
- Launch from the repository root:
  - `mvn clean compile javafx:run -Djavafx.main.class=game.core.Game`
  - If using modules, also pass `-DmainClass=game.core.Game`.
- Alternatively, run `game.core.Game` directly from your IDE as a JavaFX application.

## Controls
- Move left: `←` or `Q`
- Move right: `→` or `D`
- Jump: `Space`, `↑`, or `Z`
- Restart (when game over): `R`

## Package Layout
- **`game.core`**: Application entry point, JavaFX scene setup, player entity, HUD, input handling, physics helpers, and the frame-by-frame game loop.
- **`game.systems`**: World systems (tile map, camera, collectibles, enemies, spikes, overlays, level loading/generation).
- **`game.utils`**: Math and configuration helpers.

## Game Lifecycle
1. **Application startup** (`game.core.Game.start`)
   - Creates the JavaFX `Scene` and background, wires keyboard input, and procedurally generates multiple levels via `ProceduralLevelGenerator`.
   - Invokes `startLevel` with the first generated level index to build the playable scene graph and begin the loop.
2. **Level initialization** (`startLevel`)
   - Stops the previous `GameLoop`, clears the root pane, and validates bounds.
   - Normalizes and vertically aligns raw level strings so they rest on the ground plane, then parses them into a `TileMap` and spawn lists via `LevelLoader`.
   - Constructs the world layer (`Group`) containing the ground rectangle, player avatar, and rendered spikes. UI overlays (`UIManager`, `HUD`), game-over screen, and level-complete screen are also created.
   - Instantiates gameplay managers (`CoinManager`, `PowerUpManager`, `EnemyManager`, `SpikeManager`), jitters spawn positions for variation, and populates collectibles, enemies, and spikes.
   - Builds the overlay `Canvas` for HUD and debug rendering, sets up a fade overlay for restarts, and creates `GameWorld` to orchestrate updates.
   - Starts a custom `GameLoop` (extends `AnimationTimer`) that performs input handling, physics, camera updates, world updates, scene graph translation, and overlay rendering each frame. A win check stops the loop and shows the level-complete screen when the player nears the map end.
3. **Frame update** (`game.core.GameLoop.handle`)
   - Computes delta time (capped to 50 ms) and exits early if the game-over overlay is active unless restart is pressed.
   - Applies movement based on held keys, triggers jumps, and runs tile collision/ground collision via `Physics.moveAndCollide`/`checkGroundCollision`.
   - Constrains the player to map bounds, updates the camera follow position and clamps it to the map size.
   - Delegates to `GameWorld.update` for collectible handling, enemies/spikes, power-ups, and HUD values. Translates the world layer according to camera offsets to keep visuals aligned with collisions.
   - Clears and redraws overlay canvas content through `GameWorld.render`.
4. **Restart flow** (`restartCurrentLevel`)
   - Disables input, fades the screen to black, reloads the current level, then fades back in and re-enables input. The method guards against concurrent restarts and hides the game-over screen.

## Key Systems and Responsibilities
### Player (`game.core.Player`)
- Axis-aligned rectangle collider with a layered sprite. Holds position, velocity, dimensions, and grounded state.
- Exposes movement helpers (`moveLeft`, `moveRight`, `stopX`, `jump`), gravity application, and setters used by physics or respawn logic.
- Synchronizes its JavaFX `Group` node with physics coordinates and enforces world bounds.

### Input (`game.core.InputManager`)
- Captures keyboard state for movement, jumping, and restarting. Maintains booleans for pressed keys and supports enabling/disabling input during transitions.

### Physics (`game.core.Physics` and `game.core.Ground`)
- Applies gravity and axis-separated collision resolution against solid tiles from `TileMap`. After horizontal and vertical movement, clamps player coordinates and clears velocity appropriately.
- Provides a ground rectangle fallback so the player can still collide with a floor plane even if tiles are absent at the bottom of the scene.

### Game loop (`game.core.GameLoop`)
- Extends `AnimationTimer` to run once per frame. Orders processing as: input → physics → bounds clamp → camera follow/clamp → world update → node translation → overlay render.
- Listens for restart input after game over and invokes the restart callback.

### Camera (`game.systems.Camera`)
- Stores the viewport size, tracks a target position using linear interpolation (`follow` with a configurable smoothing factor), clamps to map bounds, and exposes offsets used to translate the world layer and to convert world coordinates to screen space during rendering.

### World orchestration (`game.systems.GameWorld`)
- Centralizes per-frame updates and rendering for collectibles, enemies, spikes, and the goal flag.
- Maintains score, coin count, lives, and an invincibility timer. Applies power-ups (extra points, invincibility, extra life) and signals the `GameOverScreen` when lives reach zero.
- Handles respawns at the saved spawn point when the player is hit and writes HUD values via `UIManager`.

### Collectibles and power-ups (`game.systems.CoinManager`, `PowerUpManager`, `PowerUpType`, `Collectible`)
- Parse spawn coordinates from the level, jitter them for variation, and render simple shapes relative to camera offsets.
- Detect axis-aligned overlap with the player to increment coin counts or return collected power-up types for further effects.

### Hazards (`game.systems.EnemyManager`, `SpikeManager`, `Enemy`, `Spike`)
- Manage enemy and spike positions, update enemy AI (basic horizontal patrol), and test collisions with the player. Contact reduces lives unless invincibility is active.
- Spikes also draw supporting base/triangle geometry directly into the world layer and an additional debug overlay square in `GameWorld.render`.

### UI overlays (`game.core.HUD`, `game.systems.UIManager`, `game.systems.PopupText`, `game.systems.GameOverScreen`, `game.systems.LevelCompleteScreen`)
- HUD: static score text anchored to the screen root (currently minimal).
- UIManager: renders coin count (and can host more UI widgets) on the overlay canvas.
- PopupText: timed floating text utility not yet wired into the loop.
- GameOverScreen and LevelCompleteScreen: JavaFX panes layered above the scene for restarting the current level or proceeding to the next.

### Level loading and generation (`game.systems.LevelLoader`, `game.systems.ProceduralLevelGenerator`, `game.systems.TileMap`)
- `LevelLoader` converts equal-length character lines into a `TileMap` plus spawn lists for player, coins, power-ups, enemies, and spikes.
- `TileMap` exposes world/tile dimensions, collision checks (`isSolidTile`), and helper conversions between tiles and pixels.
- `ProceduralLevelGenerator` creates varied raw text maps given a difficulty index, map dimensions, and a seed; `Game` normalizes and vertically aligns them so platforms sit on the ground plane.

### Constants and utilities (`game.utils.Constants`, `game.utils.MathUtils`)
- Shared values for tile size, physics tuning, and scoring.
- Math helpers for clamping, interpolation, and intersection checks.

## Level Format
Levels are defined as lists of equal-length strings. Supported symbols:
- `#`: solid tile (collidable)
- `.` or space: empty tile
- `P`: player spawn (last occurrence wins)
- `C`: coin spawn
- `U`: power-up spawn
- `E`: enemy spawn
- `S`: spike spawn

All tile coordinates are converted to world pixels by multiplying indices by `TileMap.TILE_SIZE` (32 px). Before loading, `Game` pads levels with empty rows to ensure the ground row aligns with the floor plane.

## Extending the Project
- **Levels**: Swap in custom text maps or extend `ProceduralLevelGenerator` to add new patterns. Ensure lines are uniform width so `LevelLoader` can parse them.
- **Rendering**: Replace placeholder shapes in `CoinManager`, `PowerUpManager`, and `EnemyManager` with sprites or animations. Use the camera offsets to position artwork correctly.
- **Player abilities**: React to `PowerUpType` in `GameWorld.applyPowerUp` to add new movement or combat effects. The invincibility timer is already wired for temporary immunity.
- **UI/overlays**: Expand `UIManager` and `HUD` to show timers, health, or objective prompts. Consider integrating `PopupText` for feedback on pickups or damage.
- **Progression**: Hook `GameWorld` goal detection to load the next level, and use `LevelCompleteScreen` to gate transitions.

