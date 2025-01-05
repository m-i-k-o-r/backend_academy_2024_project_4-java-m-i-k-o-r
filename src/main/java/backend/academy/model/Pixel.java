package backend.academy.model;

import java.awt.Color;

public record Pixel(
    int r,
    int g,
    int b,
    int hitCount
) {
    public Color color() {
        try {
            return new Color(r, g, b, hitCount);
        } catch (IllegalArgumentException e) {
            return new Color(r, g, b);
        }
    }
}
