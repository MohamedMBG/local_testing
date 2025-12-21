package game.utils;

import javafx.scene.paint.Color;

/**
 * Cosmetic and feel presets that tweak the world palette as well as a few
 * movement parameters so each theme feels distinct.
 */
public enum Theme {
    WINTER(
            "Winter",
            Color.web("#0d1b36"),
            Color.web("#1e3a63"),
            Color.web("#cbd9f5"),
            Color.web("#7fa8e6"),
            Color.web("#4b6a9d"),
            Color.web("#1c2947"),
            Color.web("#dce9ff"),
            Color.web("#ff7b8a"),
            Color.web("#6e1f2f"),
            Color.web("#fff3b5"),
            Color.web("#ffd874"),
            Color.web("#f2aa2f"),
            Color.web("#c57f1f"),
            Color.web("#9fe5ff"),
            Color.web("#12284a"),
            Color.web("#ffb6c7"),
            Color.web("#7a1c2c"),
            0.9,
            0.92,
            0.9
    ),
    SPRINT(
            "Sprint",
            Color.web("#0b0a1b"),
            Color.web("#251449"),
            Color.web("#f5c44e"),
            Color.web("#ff9c3f"),
            Color.web("#f4801f"),
            Color.web("#231129"),
            Color.web("#ffe3b3"),
            Color.web("#ff6f76"),
            Color.web("#781929"),
            Color.web("#fff7b0"),
            Color.web("#ffce5e"),
            Color.web("#f29c34"),
            Color.web("#b76f1e"),
            Color.web("#7fe7ff"),
            Color.web("#2d143f"),
            Color.web("#ff7bdc"),
            Color.web("#b42c80"),
            1.15,
            1.08,
            1.05
    ),
    SUMMER(
            "Summer",
            Color.web("#0f2c28"),
            Color.web("#1b4a3f"),
            Color.web("#b3f5c4"),
            Color.web("#6dd6a6"),
            Color.web("#3aa76f"),
            Color.web("#0f1f1b"),
            Color.web("#dfffe8"),
            Color.web("#ff8c63"),
            Color.web("#983d1d"),
            Color.web("#fff8b5"),
            Color.web("#ffd86e"),
            Color.web("#f1a743"),
            Color.web("#c26f26"),
            Color.web("#7ff0cd"),
            Color.web("#123129"),
            Color.web("#ff9e68"),
            Color.web("#b3562e"),
            1.02,
            0.98,
            1.0
    ),
    NEUTRAL(
            "Neutral",
            Color.web("#87b7ff"),
            Color.web("#5a82c8"),
            Color.web("#c58b5b"),
            Color.web("#f3c480"),
            Color.web("#ffdca6"),
            Color.web("#b77439"),
            Color.web("#ffe9bc"),
            Color.web("#e53935"),
            Color.web("#8b1b1b"),
            Color.web("#fff2b2"),
            Color.web("#ffd45c"),
            Color.web("#f1a100"),
            Color.web("#c87d00"),
            Color.web("#9ad5ff"),
            Color.web("#604430"),
            Color.web("#ff7b5b"),
            Color.web("#a83a2a"),
            1.0,
            1.0,
            1.0
    ),
    DUSK(
            "Dusk",
            Color.web("#1d0f2c"),
            Color.web("#432454"),
            Color.web("#f6d5ff"),
            Color.web("#bf8bff"),
            Color.web("#7a4aba"),
            Color.web("#2b103f"),
            Color.web("#ffe8ff"),
            Color.web("#ff7ad9"),
            Color.web("#7f1d62"),
            Color.web("#fff0d2"),
            Color.web("#ffcba7"),
            Color.web("#ff9b6d"),
            Color.web("#c46a3c"),
            Color.web("#9cd7ff"),
            Color.web("#2e143f"),
            Color.web("#ff9fb1"),
            Color.web("#b23d50"),
            1.0,
            0.96,
            1.0
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
