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
            1.02,
            0.98,
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
