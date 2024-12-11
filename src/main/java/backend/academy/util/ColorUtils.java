package backend.academy.util;

import lombok.experimental.UtilityClass;
import java.awt.Color;
import java.util.Random;

@UtilityClass
public class ColorUtils {
    public static Color generateColor(Random random) {
        float hue = random.nextFloat();                      // [0, 1] - оттенок
        float saturation = 0.7f + random.nextFloat() * 0.3f; // [0.7, 1] - насыщенность
        float lightness = 0.4f + random.nextFloat() * 0.2f;  // [0.4, 0.6] - яркость

        return hslToRgb(hue, saturation, lightness);
    }

    private static Color hslToRgb(float h, float s, float l) {
        float r, g, b;

        if (s == 0) {
            r = g = b = l; // Achromatic (без цвета)
        } else {
            float q = l < 0.5 ? l * (1 + s) : (l + s - l * s);
            float p = 2 * l - q;
            r = hueToRgb(p, q, h + 1.0f / 3.0f);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1.0f / 3.0f);
        }

        return new Color(clamp(r), clamp(g), clamp(b));
    }

    private static float hueToRgb(float p, float q, float t) {
        if (t < 0) {
            t += 1;
        }
        if (t > 1) {
            t -= 1;
        }
        if (t < 1.0 / 6.0) {
            return p + (q - p) * 6 * t;
        }
        if (t < 1.0 / 2.0) {
            return q;
        }
        if (t < 2.0 / 3.0) {
            return p + (q - p) * (2.0f / 3.0f - t) * 6;
        }
        return p;
    }

    private static float clamp(float value) {
        return Math.min(1.0f, Math.max(0.0f, value));
    }
}
