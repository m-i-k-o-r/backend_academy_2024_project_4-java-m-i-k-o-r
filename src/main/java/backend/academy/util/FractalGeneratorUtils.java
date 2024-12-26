package backend.academy.util;

import backend.academy.image.FractalImage;
import backend.academy.model.Pixel;
import backend.academy.model.Point;
import backend.academy.model.Rect;
import backend.academy.model.SymmetryType;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FractalGeneratorUtils {
    private static final int MAX_COLOR_VALUE = 255;

    public static List<Point> generateSymmetricPoints(
        Point original,
        double centerX,
        double centerY,
        SymmetryType symmetryType
    ) {
        List<Point> points = new ArrayList<>(switch (symmetryType) {
            case NONE -> 1;
            case HORIZONTAL, VERTICAL, RADIAL_2 -> 2;
            case BOTH, RADIAL_4 -> 4;
        });

        points.add(original);

        double dx = original.x() - centerX;
        double dy = original.y() - centerY;

        switch (symmetryType) {
            case HORIZONTAL -> points.add(new Point(original.x(), centerY - dy));
            case VERTICAL -> points.add(new Point(centerX - dx, original.y()));
            case BOTH -> {
                points.add(new Point(centerX - dx, original.y()));
                points.add(new Point(original.x(), centerY - dy));
                points.add(new Point(centerX - dx, centerY - dy));
            }
            case RADIAL_2 -> points.add(new Point(centerX - dx, centerY - dy));
            case RADIAL_4 -> {
                points.add(new Point(centerX - dx, original.y()));
                points.add(new Point(original.x(), centerY - dy));
                points.add(new Point(centerX - dx, centerY - dy));
            }
            default -> {

            }
        }

        return points;
    }

    public static Pixel blendPixels(Pixel currentPixel, Color transformColor) {
        if (currentPixel.hitCount() == 0) {
            return new Pixel(
                transformColor.getRed(),
                transformColor.getGreen(),
                transformColor.getBlue(),
                1
            );
        }

        return new Pixel(
            calculateBlendedColor(currentPixel.r(), transformColor.getRed(), currentPixel.hitCount()),
            calculateBlendedColor(currentPixel.g(), transformColor.getGreen(), currentPixel.hitCount()),
            calculateBlendedColor(currentPixel.b(), transformColor.getBlue(), currentPixel.hitCount()),
            currentPixel.hitCount() + 1
        );
    }

    public static void renderPoint(
        FractalImage canvas,
        Rect world,
        Point point,
        Color transformColor
    ) {
        if (!world.contains(point)) {
            return;
        }

        int x = (int) ((point.x() - world.x()) / world.width() * canvas.width());
        int y = (int) ((point.y() - world.y()) / world.height() * canvas.height());

        if (!canvas.contains(x, y)) {
            return;
        }

        Pixel currentPixel = canvas.pixel(x, y);
        Pixel updatedPixel = blendPixels(currentPixel, transformColor);

        canvas.setPixel(x, y, updatedPixel);
    }

    public static int calculateBlendedColor(int currentColor, int newColor, int hitCount) {
        return Math.min(MAX_COLOR_VALUE, (currentColor * hitCount + newColor) / (hitCount + 1));
    }

    public record RendererMetrics(
        long totalTimeMs,
        int threadCount,
        Runtime runtime,
        int samples,
        int iterPerSample,
        String generatorType
    ) {
        @Override
        public String toString() {
            return String.format("""
                    Rendering Metrics:
                    Generator Type: %s
                    System Configuration:
                        Available processors: %d
                        Max memory: %d MB
                        Free memory: %d MB
                        Total memory: %d MB
                    Execution Details:
                        Thread count: %d
                        Total samples: %d
                        Iterations per sample: %d
                        Total execution time: %d ms
                        Average time per sample: %.2f ms
                    """,
                generatorType,
                runtime.availableProcessors(),
                runtime.maxMemory() / (1024 * 1024),
                runtime.freeMemory() / (1024 * 1024),
                runtime.totalMemory() / (1024 * 1024),
                threadCount,
                samples,
                iterPerSample,
                totalTimeMs,
                (double) totalTimeMs / samples
            );
        }
    }
}
