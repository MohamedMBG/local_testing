package game.utils;

import javafx.scene.paint.Color;

/**
 * Cosmetic and feel presets that tweak the world palette as well as a few
 * movement parameters so each theme feels distinct.
 */
public enum Theme {
    WINTER(
            "Winter",
            Color.web("#dfe9ff"),
            Color.web("#b7c7e0"),
            Color.web("#8ba0c2"),
            Color.web("#d1e4ff"),
            Color.web("#5a6f8c"),
            Color.web("#eff6ff"),
            0.9,
            0.92,
            0.9
    ),
    SPRINT(
            "Sprint",
            Color.web("#fff3d9"),
            Color.web("#f7c873"),
            Color.web("#f2a541"),
            Color.web("#fce8b2"),
            Color.web("#d06b2f"),
            Color.web("#fff9ea"),
            1.15,
            1.08,
            1.05
    ),
    SUMMER(
            "Summer",
            Color.web("#c6f5d2"),
            Color.web("#77c372"),
            Color.web("#4d9b52"),
            Color.web("#a3e4a7"),
            Color.web("#2e6b34"),
            Color.web("#e8fce9"),
            1.0,
            1.0,
            1.0
    );

    private final String displayName;
    private final Color background;
    private final Color ground;
    private final Color tileBase;
    private final Color tileHighlight;
    private final Color tileShadow;
    private final Color tileAccent;
    private final double moveScale;
    private final double gravityScale;
    private final double jumpScale;

    Theme(String displayName,
          Color background,
          Color ground,
          Color tileBase,
          Color tileHighlight,
          Color tileShadow,
          Color tileAccent,
          double moveScale,
          double gravityScale,
          double jumpScale) {
        this.displayName = displayName;
        this.background = background;
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
        return background;
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
        return String.format("rgba(%d,%d,%d,1.0)",
                (int) (background.getRed() * 255),
                (int) (background.getGreen() * 255),
                (int) (background.getBlue() * 255));
    }
}
