package game.utils;

import javafx.scene.paint.Color;

/**
 * Cosmetic and feel presets that tweak the world palette as well as a few
 * movement parameters so each theme feels distinct.
 */
public enum Theme {
    WINTER(
            "Frostbite",
            Color.web("#081226"),
            Color.web("#12396d"),
            Color.web("#d9e8ff"),
            Color.web("#8ab7ff"),
            Color.web("#5c8adf"),
            Color.web("#0f2548"),
            Color.web("#e9f3ff"),
            Color.web("#ff94a6"),
            Color.web("#531a30"),
            Color.web("#fff6c9"),
            Color.web("#ffe17a"),
            Color.web("#f7b63f"),
            Color.web("#c78522"),
            Color.web("#aee9ff"),
            Color.web("#193864"),
            Color.web("#ffc1d6"),
            Color.web("#7a2438"),
            0.9,
            0.92,
            0.92
    ),
    SPRINT(
            "Sunburst",
            Color.web("#120b22"),
            Color.web("#32174d"),
            Color.web("#ffe598"),
            Color.web("#ffb347"),
            Color.web("#ff8c32"),
            Color.web("#230f2f"),
            Color.web("#fff2c6"),
            Color.web("#ff7c8a"),
            Color.web("#6d142d"),
            Color.web("#fff3b0"),
            Color.web("#ffd36f"),
            Color.web("#f6a135"),
            Color.web("#bd7624"),
            Color.web("#92f0ff"),
            Color.web("#3a1b4b"),
            Color.web("#ff86e1"),
            Color.web("#c4318a"),
            1.12,
            1.07,
            1.04
    ),
    SUMMER(
            "Citrus Coast",
            Color.web("#0c2925"),
            Color.web("#135143"),
            Color.web("#c7ffd8"),
            Color.web("#6fe7b2"),
            Color.web("#41b883"),
            Color.web("#0d2420"),
            Color.web("#e9fff0"),
            Color.web("#ff9b6a"),
            Color.web("#7d2b1f"),
            Color.web("#fff7c2"),
            Color.web("#ffd77a"),
            Color.web("#f6b34b"),
            Color.web("#c97b2d"),
            Color.web("#8ef7d5"),
            Color.web("#163b33"),
            Color.web("#ffb07b"),
            Color.web("#c15b34"),
            1.03,
            0.98,
            1.02
    ),
    NEUTRAL(
            "Classic",
            Color.web("#6e9cff"),
            Color.web("#4065b0"),
            Color.web("#cfa06c"),
            Color.web("#f5c97a"),
            Color.web("#ffd8a3"),
            Color.web("#a96b35"),
            Color.web("#ffefc5"),
            Color.web("#ff5e57"),
            Color.web("#8a1f1f"),
            Color.web("#fff4b8"),
            Color.web("#ffd461"),
            Color.web("#f0a600"),
            Color.web("#c17b00"),
            Color.web("#a5dcff"),
            Color.web("#5e4533"),
            Color.web("#ff896e"),
            Color.web("#b73f32"),
            1.0,
            1.0,
            1.0
    ),
    DUSK(
            "Midnight Bloom",
            Color.web("#140c21"),
            Color.web("#2f1e48"),
            Color.web("#f3deff"),
            Color.web("#c1a4ff"),
            Color.web("#8a63d9"),
            Color.web("#28163d"),
            Color.web("#ffe9ff"),
            Color.web("#ff8ed4"),
            Color.web("#8a1f64"),
            Color.web("#fff0d7"),
            Color.web("#ffcaa5"),
            Color.web("#ff9a75"),
            Color.web("#c36b4a"),
            Color.web("#aad8ff"),
            Color.web("#2d1745"),
            Color.web("#ff9fbf"),
            Color.web("#c84569"),
            1.0,
            0.95,
            1.02
    ),
    NEON(
            "Neon Pulse",
            Color.web("#050816"),
            Color.web("#0f1f3f"),
            Color.web("#1f2c3d"),
            Color.web("#1dd3b0"),
            Color.web("#0fa3b1"),
            Color.web("#051426"),
            Color.web("#d5fefd"),
            Color.web("#ff5faf"),
            Color.web("#41124f"),
            Color.web("#fff6ce"),
            Color.web("#ffe29f"),
            Color.web("#f9a620"),
            Color.web("#c56c10"),
            Color.web("#6df1ff"),
            Color.web("#0b2436"),
            Color.web("#ff7ae3"),
            Color.web("#b71db0"),
            1.08,
            1.05,
            1.06
    );

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

    Theme(String displayName,
          Color backgroundTop,
          Color backgroundBottom,
          Color ground,
          Color tileBase,
          Color tileHighlight,
          Color tileShadow,
          Color tileAccent,
          Color enemyFill,
          Color enemyOutline,
          Color coinLight,
          Color coinMid,
          Color coinShadow,
          Color coinOutline,
          Color powerUpGlow,
          Color spikeBase,
          Color spikeFill,
          Color spikeOutline,
          double moveScale,
          double gravityScale,
          double jumpScale) {
        this.displayName = displayName;
        this.backgroundTop = backgroundTop;
        this.backgroundBottom = backgroundBottom;
        this.ground = ground;
        this.tileBase = tileBase;
        this.tileHighlight = tileHighlight;
        this.tileShadow = tileShadow;
        this.tileAccent = tileAccent;
        this.enemyFill = enemyFill;
        this.enemyOutline = enemyOutline;
        this.coinLight = coinLight;
        this.coinMid = coinMid;
        this.coinShadow = coinShadow;
        this.coinOutline = coinOutline;
        this.powerUpGlow = powerUpGlow;
        this.spikeBase = spikeBase;
        this.spikeFill = spikeFill;
        this.spikeOutline = spikeOutline;
        this.moveScale = moveScale;
        this.gravityScale = gravityScale;
        this.jumpScale = jumpScale;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Color getBackground() {
        return backgroundTop;
    }

    public Color getBackgroundBottom() {
        return backgroundBottom;
    }

    public Color getGround() {
        return ground;
    }

    public Color getTileBase() {
        return tileBase;
    }

    public Color getTileHighlight() {
        return tileHighlight;
    }

    public Color getTileShadow() {
        return tileShadow;
    }

    public Color getTileAccent() {
        return tileAccent;
    }

    public Color getEnemyFill() {
        return enemyFill;
    }

    public Color getEnemyOutline() {
        return enemyOutline;
    }

    public Color getCoinLight() {
        return coinLight;
    }

    public Color getCoinMid() {
        return coinMid;
    }

    public Color getCoinShadow() {
        return coinShadow;
    }

    public Color getCoinOutline() {
        return coinOutline;
    }

    public Color getPowerUpGlow() {
        return powerUpGlow;
    }

    public Color getSpikeBase() {
        return spikeBase;
    }

    public Color getSpikeFill() {
        return spikeFill;
    }

    public Color getSpikeOutline() {
        return spikeOutline;
    }

    public double getMoveScale() {
        return moveScale;
    }

    public double getGravityScale() {
        return gravityScale;
    }

    public double getJumpScale() {
        return jumpScale;
    }

    public String toCss() {
        return String.format("linear-gradient(from 0%% 0%% to 0%% 100%%, rgba(%d,%d,%d,1.0), rgba(%d,%d,%d,1.0))",
                (int) (backgroundTop.getRed() * 255),
                (int) (backgroundTop.getGreen() * 255),
                (int) (backgroundTop.getBlue() * 255),
                (int) (backgroundBottom.getRed() * 255),
                (int) (backgroundBottom.getGreen() * 255),
                (int) (backgroundBottom.getBlue() * 255));
    }
}
