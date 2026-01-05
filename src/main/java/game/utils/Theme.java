package game.utils; // Declares the package for this source file.

import javafx.scene.paint.Color;

/**
 * Cosmetic and feel presets that tweak the world palette as well as a few
 * movement parameters so each theme feels distinct.
 */
public enum Theme { // Executes: public enum Theme {
    WINTER( // Executes: WINTER(
            "Frostbite", // Executes: "Frostbite",
            Color.web("#081226"), // Executes: Color.web("#081226"),
            Color.web("#12396d"), // Executes: Color.web("#12396d"),
            Color.web("#d9e8ff"), // Executes: Color.web("#d9e8ff"),
            Color.web("#8ab7ff"), // Executes: Color.web("#8ab7ff"),
            Color.web("#5c8adf"), // Executes: Color.web("#5c8adf"),
            Color.web("#0f2548"), // Executes: Color.web("#0f2548"),
            Color.web("#e9f3ff"), // Executes: Color.web("#e9f3ff"),
            Color.web("#ff94a6"), // Executes: Color.web("#ff94a6"),
            Color.web("#531a30"), // Executes: Color.web("#531a30"),
            Color.web("#fff6c9"), // Executes: Color.web("#fff6c9"),
            Color.web("#ffe17a"), // Executes: Color.web("#ffe17a"),
            Color.web("#f7b63f"), // Executes: Color.web("#f7b63f"),
            Color.web("#c78522"), // Executes: Color.web("#c78522"),
            Color.web("#aee9ff"), // Executes: Color.web("#aee9ff"),
            Color.web("#193864"), // Executes: Color.web("#193864"),
            Color.web("#ffc1d6"), // Executes: Color.web("#ffc1d6"),
            Color.web("#7a2438"), // Executes: Color.web("#7a2438"),
            0.9, // Executes: 0.9,
            0.92, // Executes: 0.92,
            0.92 // Executes: 0.92
    ), // Executes: ),
    SPRINT( // Executes: SPRINT(
            "Sunburst", // Executes: "Sunburst",
            Color.web("#120b22"), // Executes: Color.web("#120b22"),
            Color.web("#32174d"), // Executes: Color.web("#32174d"),
            Color.web("#ffe598"), // Executes: Color.web("#ffe598"),
            Color.web("#ffb347"), // Executes: Color.web("#ffb347"),
            Color.web("#ff8c32"), // Executes: Color.web("#ff8c32"),
            Color.web("#230f2f"), // Executes: Color.web("#230f2f"),
            Color.web("#fff2c6"), // Executes: Color.web("#fff2c6"),
            Color.web("#ff7c8a"), // Executes: Color.web("#ff7c8a"),
            Color.web("#6d142d"), // Executes: Color.web("#6d142d"),
            Color.web("#fff3b0"), // Executes: Color.web("#fff3b0"),
            Color.web("#ffd36f"), // Executes: Color.web("#ffd36f"),
            Color.web("#f6a135"), // Executes: Color.web("#f6a135"),
            Color.web("#bd7624"), // Executes: Color.web("#bd7624"),
            Color.web("#92f0ff"), // Executes: Color.web("#92f0ff"),
            Color.web("#3a1b4b"), // Executes: Color.web("#3a1b4b"),
            Color.web("#ff86e1"), // Executes: Color.web("#ff86e1"),
            Color.web("#c4318a"), // Executes: Color.web("#c4318a"),
            1.12, // Executes: 1.12,
            1.07, // Executes: 1.07,
            1.04 // Executes: 1.04
    ), // Executes: ),
    SUMMER( // Executes: SUMMER(
            "Citrus Coast", // Executes: "Citrus Coast",
            Color.web("#0c2925"), // Executes: Color.web("#0c2925"),
            Color.web("#135143"), // Executes: Color.web("#135143"),
            Color.web("#c7ffd8"), // Executes: Color.web("#c7ffd8"),
            Color.web("#6fe7b2"), // Executes: Color.web("#6fe7b2"),
            Color.web("#41b883"), // Executes: Color.web("#41b883"),
            Color.web("#0d2420"), // Executes: Color.web("#0d2420"),
            Color.web("#e9fff0"), // Executes: Color.web("#e9fff0"),
            Color.web("#ff9b6a"), // Executes: Color.web("#ff9b6a"),
            Color.web("#7d2b1f"), // Executes: Color.web("#7d2b1f"),
            Color.web("#fff7c2"), // Executes: Color.web("#fff7c2"),
            Color.web("#ffd77a"), // Executes: Color.web("#ffd77a"),
            Color.web("#f6b34b"), // Executes: Color.web("#f6b34b"),
            Color.web("#c97b2d"), // Executes: Color.web("#c97b2d"),
            Color.web("#8ef7d5"), // Executes: Color.web("#8ef7d5"),
            Color.web("#163b33"), // Executes: Color.web("#163b33"),
            Color.web("#ffb07b"), // Executes: Color.web("#ffb07b"),
            Color.web("#c15b34"), // Executes: Color.web("#c15b34"),
            1.03, // Executes: 1.03,
            0.98, // Executes: 0.98,
            1.02 // Executes: 1.02
    ), // Executes: ),
    NEUTRAL( // Executes: NEUTRAL(
            "Classic", // Executes: "Classic",
            Color.web("#6e9cff"), // Executes: Color.web("#6e9cff"),
            Color.web("#4065b0"), // Executes: Color.web("#4065b0"),
            Color.web("#cfa06c"), // Executes: Color.web("#cfa06c"),
            Color.web("#f5c97a"), // Executes: Color.web("#f5c97a"),
            Color.web("#ffd8a3"), // Executes: Color.web("#ffd8a3"),
            Color.web("#a96b35"), // Executes: Color.web("#a96b35"),
            Color.web("#ffefc5"), // Executes: Color.web("#ffefc5"),
            Color.web("#ff5e57"), // Executes: Color.web("#ff5e57"),
            Color.web("#8a1f1f"), // Executes: Color.web("#8a1f1f"),
            Color.web("#fff4b8"), // Executes: Color.web("#fff4b8"),
            Color.web("#ffd461"), // Executes: Color.web("#ffd461"),
            Color.web("#f0a600"), // Executes: Color.web("#f0a600"),
            Color.web("#c17b00"), // Executes: Color.web("#c17b00"),
            Color.web("#a5dcff"), // Executes: Color.web("#a5dcff"),
            Color.web("#5e4533"), // Executes: Color.web("#5e4533"),
            Color.web("#ff896e"), // Executes: Color.web("#ff896e"),
            Color.web("#b73f32"), // Executes: Color.web("#b73f32"),
            1.0, // Executes: 1.0,
            1.0, // Executes: 1.0,
            1.0 // Executes: 1.0
    ), // Executes: ),
    DUSK( // Executes: DUSK(
            "Midnight Bloom", // Executes: "Midnight Bloom",
            Color.web("#140c21"), // Executes: Color.web("#140c21"),
            Color.web("#2f1e48"), // Executes: Color.web("#2f1e48"),
            Color.web("#f3deff"), // Executes: Color.web("#f3deff"),
            Color.web("#c1a4ff"), // Executes: Color.web("#c1a4ff"),
            Color.web("#8a63d9"), // Executes: Color.web("#8a63d9"),
            Color.web("#28163d"), // Executes: Color.web("#28163d"),
            Color.web("#ffe9ff"), // Executes: Color.web("#ffe9ff"),
            Color.web("#ff8ed4"), // Executes: Color.web("#ff8ed4"),
            Color.web("#8a1f64"), // Executes: Color.web("#8a1f64"),
            Color.web("#fff0d7"), // Executes: Color.web("#fff0d7"),
            Color.web("#ffcaa5"), // Executes: Color.web("#ffcaa5"),
            Color.web("#ff9a75"), // Executes: Color.web("#ff9a75"),
            Color.web("#c36b4a"), // Executes: Color.web("#c36b4a"),
            Color.web("#aad8ff"), // Executes: Color.web("#aad8ff"),
            Color.web("#2d1745"), // Executes: Color.web("#2d1745"),
            Color.web("#ff9fbf"), // Executes: Color.web("#ff9fbf"),
            Color.web("#c84569"), // Executes: Color.web("#c84569"),
            1.0, // Executes: 1.0,
            0.95, // Executes: 0.95,
            1.02 // Executes: 1.02
    ), // Executes: ),
    NEON( // Executes: NEON(
            "Neon Pulse", // Executes: "Neon Pulse",
            Color.web("#050816"), // Executes: Color.web("#050816"),
            Color.web("#0f1f3f"), // Executes: Color.web("#0f1f3f"),
            Color.web("#1f2c3d"), // Executes: Color.web("#1f2c3d"),
            Color.web("#1dd3b0"), // Executes: Color.web("#1dd3b0"),
            Color.web("#0fa3b1"), // Executes: Color.web("#0fa3b1"),
            Color.web("#051426"), // Executes: Color.web("#051426"),
            Color.web("#d5fefd"), // Executes: Color.web("#d5fefd"),
            Color.web("#ff5faf"), // Executes: Color.web("#ff5faf"),
            Color.web("#41124f"), // Executes: Color.web("#41124f"),
            Color.web("#fff6ce"), // Executes: Color.web("#fff6ce"),
            Color.web("#ffe29f"), // Executes: Color.web("#ffe29f"),
            Color.web("#f9a620"), // Executes: Color.web("#f9a620"),
            Color.web("#c56c10"), // Executes: Color.web("#c56c10"),
            Color.web("#6df1ff"), // Executes: Color.web("#6df1ff"),
            Color.web("#0b2436"), // Executes: Color.web("#0b2436"),
            Color.web("#ff7ae3"), // Executes: Color.web("#ff7ae3"),
            Color.web("#b71db0"), // Executes: Color.web("#b71db0"),
            1.08, // Executes: 1.08,
            1.05, // Executes: 1.05,
            1.06 // Executes: 1.06
    ); // Executes: );

    private final String displayName;
    private final Color backgroundTop;
    private final Color backgroundBottom;
    private final Color ground;
    private final Color tileBase;
    private final Color tileHighlight;
    private final Color tileShadow;
    private final Color tileAccent;
    private final Color enemyFill;
    private final Color enemyOutline;
    private final Color coinLight;
    private final Color coinMid;
    private final Color coinShadow;
    private final Color coinOutline;
    private final Color powerUpGlow;
    private final Color spikeBase;
    private final Color spikeFill;
    private final Color spikeOutline;
    private final double moveScale;
    private final double gravityScale;
    private final double jumpScale;

    Theme(String displayName, // Executes: Theme(String displayName,
          Color backgroundTop, // Executes: Color backgroundTop,
          Color backgroundBottom, // Executes: Color backgroundBottom,
          Color ground, // Executes: Color ground,
          Color tileBase, // Executes: Color tileBase,
          Color tileHighlight, // Executes: Color tileHighlight,
          Color tileShadow, // Executes: Color tileShadow,
          Color tileAccent, // Executes: Color tileAccent,
          Color enemyFill, // Executes: Color enemyFill,
          Color enemyOutline, // Executes: Color enemyOutline,
          Color coinLight, // Executes: Color coinLight,
          Color coinMid, // Executes: Color coinMid,
          Color coinShadow, // Executes: Color coinShadow,
          Color coinOutline, // Executes: Color coinOutline,
          Color powerUpGlow, // Executes: Color powerUpGlow,
          Color spikeBase, // Executes: Color spikeBase,
          Color spikeFill, // Executes: Color spikeFill,
          Color spikeOutline, // Executes: Color spikeOutline,
          double moveScale, // Executes: double moveScale,
          double gravityScale, // Executes: double gravityScale,
          double jumpScale) { // Executes: double jumpScale) {
        this.displayName = displayName; // Executes: this.displayName = displayName;
        this.backgroundTop = backgroundTop; // Executes: this.backgroundTop = backgroundTop;
        this.backgroundBottom = backgroundBottom; // Executes: this.backgroundBottom = backgroundBottom;
        this.ground = ground; // Executes: this.ground = ground;
        this.tileBase = tileBase; // Executes: this.tileBase = tileBase;
        this.tileHighlight = tileHighlight; // Executes: this.tileHighlight = tileHighlight;
        this.tileShadow = tileShadow; // Executes: this.tileShadow = tileShadow;
        this.tileAccent = tileAccent; // Executes: this.tileAccent = tileAccent;
        this.enemyFill = enemyFill; // Executes: this.enemyFill = enemyFill;
        this.enemyOutline = enemyOutline; // Executes: this.enemyOutline = enemyOutline;
        this.coinLight = coinLight; // Executes: this.coinLight = coinLight;
        this.coinMid = coinMid; // Executes: this.coinMid = coinMid;
        this.coinShadow = coinShadow; // Executes: this.coinShadow = coinShadow;
        this.coinOutline = coinOutline; // Executes: this.coinOutline = coinOutline;
        this.powerUpGlow = powerUpGlow; // Executes: this.powerUpGlow = powerUpGlow;
        this.spikeBase = spikeBase; // Executes: this.spikeBase = spikeBase;
        this.spikeFill = spikeFill; // Executes: this.spikeFill = spikeFill;
        this.spikeOutline = spikeOutline; // Executes: this.spikeOutline = spikeOutline;
        this.moveScale = moveScale; // Executes: this.moveScale = moveScale;
        this.gravityScale = gravityScale; // Executes: this.gravityScale = gravityScale;
        this.jumpScale = jumpScale; // Executes: this.jumpScale = jumpScale;
    } // Closes a code block.

    public String getDisplayName() { // Begins a method or constructor with its signature.
        return displayName;
    } // Closes a code block.

    public Color getBackground() { // Begins a method or constructor with its signature.
        return backgroundTop;
    } // Closes a code block.

    public Color getBackgroundBottom() { // Begins a method or constructor with its signature.
        return backgroundBottom;
    } // Closes a code block.

    public Color getGround() { // Begins a method or constructor with its signature.
        return ground;
    } // Closes a code block.

    public Color getTileBase() { // Begins a method or constructor with its signature.
        return tileBase;
    } // Closes a code block.

    public Color getTileHighlight() { // Begins a method or constructor with its signature.
        return tileHighlight;
    } // Closes a code block.

    public Color getTileShadow() { // Begins a method or constructor with its signature.
        return tileShadow;
    } // Closes a code block.

    public Color getTileAccent() { // Begins a method or constructor with its signature.
        return tileAccent;
    } // Closes a code block.

    public Color getEnemyFill() { // Begins a method or constructor with its signature.
        return enemyFill;
    } // Closes a code block.

    public Color getEnemyOutline() { // Begins a method or constructor with its signature.
        return enemyOutline;
    } // Closes a code block.

    public Color getCoinLight() { // Begins a method or constructor with its signature.
        return coinLight;
    } // Closes a code block.

    public Color getCoinMid() { // Begins a method or constructor with its signature.
        return coinMid;
    } // Closes a code block.

    public Color getCoinShadow() { // Begins a method or constructor with its signature.
        return coinShadow;
    } // Closes a code block.

    public Color getCoinOutline() { // Begins a method or constructor with its signature.
        return coinOutline;
    } // Closes a code block.

    public Color getPowerUpGlow() { // Begins a method or constructor with its signature.
        return powerUpGlow;
    } // Closes a code block.

    public Color getSpikeBase() { // Begins a method or constructor with its signature.
        return spikeBase;
    } // Closes a code block.

    public Color getSpikeFill() { // Begins a method or constructor with its signature.
        return spikeFill;
    } // Closes a code block.

    public Color getSpikeOutline() { // Begins a method or constructor with its signature.
        return spikeOutline;
    } // Closes a code block.

    public double getMoveScale() { // Begins a method or constructor with its signature.
        return moveScale;
    } // Closes a code block.

    public double getGravityScale() { // Begins a method or constructor with its signature.
        return gravityScale;
    } // Closes a code block.

    public double getJumpScale() { // Begins a method or constructor with its signature.
        return jumpScale;
    } // Closes a code block.

    public String toCss() { // Begins a method or constructor with its signature.
        return String.format("linear-gradient(from 0%% 0%% to 0%% 100%%, rgba(%d,%d,%d,1.0), rgba(%d,%d,%d,1.0))", // Returns a value from the method.
                (int) (backgroundTop.getRed() * 255), // Executes: (int) (backgroundTop.getRed() * 255),
                (int) (backgroundTop.getGreen() * 255), // Executes: (int) (backgroundTop.getGreen() * 255),
                (int) (backgroundTop.getBlue() * 255), // Executes: (int) (backgroundTop.getBlue() * 255),
                (int) (backgroundBottom.getRed() * 255), // Executes: (int) (backgroundBottom.getRed() * 255),
                (int) (backgroundBottom.getGreen() * 255), // Executes: (int) (backgroundBottom.getGreen() * 255),
                (int) (backgroundBottom.getBlue() * 255)); // Executes: (int) (backgroundBottom.getBlue() * 255));
    } // Closes a code block.
} // Closes a code block.
