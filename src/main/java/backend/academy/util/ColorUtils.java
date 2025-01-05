package backend.academy.util;

import java.awt.Color;
import java.util.Random;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ColorUtils {
    // Константы цвета
    public static final int COLOR_MAX_VALUE = 255;

    private static final float RGB_CLAMP_MIN = 0.0f;
    private static final float RGB_CLAMP_MAX = 1.0f;

    // Параметры преобразования оттенка
    private static final float HUE_TO_RGB_ONE_THIRD = 1.0f / 3.0f;
    private static final float HUE_TO_RGB_ONE_SIXTH = 1.0f / 6.0f;
    private static final float HUE_TO_RGB_TWO_THIRD = 2.0f / 3.0f;
    private static final int HUE_RED_GREEN_ADJUSTMENT = 4;

    // Параметры яркости и насыщенности
    private static final float DEFAULT_HUE_VARIATION = 0.1f;
    private static final float DEFAULT_SATURATION_MIN = 0.7f;
    private static final float DEFAULT_SATURATION_RANGE = 0.3f;
    private static final float DEFAULT_LIGHTNESS_MIN = 0.4f;
    private static final float DEFAULT_LIGHTNESS_RANGE = 0.2f;
    private static final float LIGHTNESS_THRESHOLD = 0.5f;

    // Математические параметры
    private static final int BLEND_ITERATION = 6;
    private static final float EPSILON = 1e-6f;

    public static Color generateColor(Random random, Color baseColor) {
        float[] hsl = rgbToHsl(baseColor);

        float hue = hsl[0] + (random.nextFloat() * 2 * DEFAULT_HUE_VARIATION - DEFAULT_HUE_VARIATION);
        float saturation = DEFAULT_SATURATION_MIN + random.nextFloat() * DEFAULT_SATURATION_RANGE;
        float lightness = DEFAULT_LIGHTNESS_MIN + random.nextFloat() * DEFAULT_LIGHTNESS_RANGE;

        return hslToRgb(hue, saturation, lightness);
    }

    public static Color generateColor(Random random) {
        float hue = random.nextFloat();
        float saturation = DEFAULT_SATURATION_MIN + random.nextFloat() * DEFAULT_SATURATION_RANGE;
        float lightness = DEFAULT_LIGHTNESS_MIN + random.nextFloat() * DEFAULT_LIGHTNESS_RANGE;
        return hslToRgb(hue, saturation, lightness);
    }

    private static float[] rgbToHsl(Color color) {
        float r = color.getRed() / (float) COLOR_MAX_VALUE;
        float g = color.getGreen() / (float) COLOR_MAX_VALUE;
        float b = color.getBlue() / (float) COLOR_MAX_VALUE;

        float max = Math.max(Math.max(r, g), b);
        float min = Math.min(Math.min(r, g), b);

        float h = 0;
        float l = (max + min) / 2f;
        float s = (max == min)
            ? 0
            : (l > LIGHTNESS_THRESHOLD
                ? (max - min) / (2 - max - min)
                : (max - min) / (max + min));

        if (Math.abs(max - min) >= EPSILON) {           // max != min
            if (Math.abs(max - r) < EPSILON) {          // max == r
                h = (g - b) / (max - min) + (g < b ? BLEND_ITERATION : 0);
            } else if (Math.abs(max - g) < EPSILON) {   // max == g
                h = (b - r) / (max - min) + 2;
            } else {
                h = (r - g) / (max - min) + HUE_RED_GREEN_ADJUSTMENT;
            }
            h /= BLEND_ITERATION;
        }

        return new float[] {h, s, l};
    }

    private static Color hslToRgb(float h, float s, float l) {
        float r;
        float g;
        float b;

        if (s == 0) {
            r = l;
            g = l;
            b = l;
        } else {
            float q = l < LIGHTNESS_THRESHOLD ? l * (1 + s) : (l + s - l * s);
            float p = 2 * l - q;
            r = hueToRgb(p, q, h + HUE_TO_RGB_ONE_THIRD);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - HUE_TO_RGB_ONE_THIRD);
        }

        return new Color(clamp(r), clamp(g), clamp(b));
    }

    private static float hueToRgb(float p, float q, float t) {
        float adjustedT = t;
        if (adjustedT < RGB_CLAMP_MIN) {
            adjustedT += 1;
        } else if (adjustedT > RGB_CLAMP_MAX) {
            adjustedT -= 1;
        }

        if (adjustedT < HUE_TO_RGB_ONE_SIXTH) {
            return p + (q - p) * BLEND_ITERATION * adjustedT;
        } else if (adjustedT < LIGHTNESS_THRESHOLD) {
            return q;
        } else if (adjustedT < HUE_TO_RGB_TWO_THIRD) {
            return p + (q - p) * (HUE_TO_RGB_TWO_THIRD - adjustedT) * BLEND_ITERATION;
        } else {
            return p;
        }
    }

    private static float clamp(float value) {
        return Math.min(RGB_CLAMP_MAX, Math.max(RGB_CLAMP_MIN, value));
    }

    public static Color blendColors(Color background, Color foreground) {
        float alphaForeground = foreground.getAlpha() / (float) COLOR_MAX_VALUE;
        float alphaBackground = background.getAlpha() / (float) COLOR_MAX_VALUE;
        float alphaOut = alphaForeground + alphaBackground * (1 - alphaForeground);

        int r = (int) ((foreground.getRed() * alphaForeground + background.getRed()
            * alphaBackground * (1 - alphaForeground)) / alphaOut);

        int g = (int) ((foreground.getGreen() * alphaForeground + background.getGreen()
            * alphaBackground * (1 - alphaForeground)) / alphaOut);

        int b = (int) ((foreground.getBlue() * alphaForeground + background.getBlue()
            * alphaBackground * (1 - alphaForeground)) / alphaOut);

        int a = (int) (alphaOut * (float) COLOR_MAX_VALUE);

        return new Color(r, g, b, a);
    }
}
