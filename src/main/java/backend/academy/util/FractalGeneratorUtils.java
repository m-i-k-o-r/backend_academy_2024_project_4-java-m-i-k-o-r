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
import static backend.academy.util.ColorUtils.COLOR_MAX_VALUE;

@UtilityClass
public class FractalGeneratorUtils {
    private static final int POINT_CAPACITY_2 = 2;
    private static final int POINT_CAPACITY_4 = 4;

    /**
     * Генерирует симметричные точки на основе исходной точки
     *
     * @param original исходная точка
     * @param centerX  X центра симметрии
     * @param centerY  Y центра симметрии
     * @param symmetryType тип симметрии {@link SymmetryType}.
     * @return список симметричных точек
     */
    public static List<Point> generateSymmetricPoints(
        Point original,
        double centerX,
        double centerY,
        SymmetryType symmetryType
    ) {
        if (symmetryType == null) {
            return List.of(original);
        }

        List<Point> points = new ArrayList<>(switch (symmetryType) {
            case HORIZONTAL, VERTICAL, RADIAL_2 -> POINT_CAPACITY_2;
            case BOTH, RADIAL_4 -> POINT_CAPACITY_4;
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
            default -> { }
        }

        return points;
    }

    /**
     * Выполняет смешивание текущего пикселя с цветом преобразования
     *
     * @param currentPixel текущий пиксель {@link Pixel}.
     * @param transformColor цвет преобразования {@link Color}.
     * @return новый пиксель
     */
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

    /**
     * Отображает точку на фрактальном изображении, применяя цвет преобразования
     *
     * @param canvas холст {@link FractalImage}
     * @param world область мира {@link Rect}
     * @param point точка для отображения {@link Point}
     * @param transformColor цвет преобразования {@link Color}
     */
    public static void renderPoint(
        FractalImage canvas,
        Rect world,
        Point point,
        Color transformColor
    ) {
        if (world.contains(point)) {
            int x = (int) ((point.x() - world.x()) / world.width() * canvas.width());
            int y = (int) ((point.y() - world.y()) / world.height() * canvas.height());

            if (canvas.contains(x, y)) {
                Pixel currentPixel = canvas.pixel(x, y);
                Pixel updatedPixel = blendPixels(currentPixel, transformColor);

                canvas.setPixel(x, y, updatedPixel);
            }
        }
    }

    /**
     * Рассчитывает смешанный цвет с учетом количества попаданий
     *
     * @param currentColor текущий цвет (r, g или b)
     * @param newColor новый цвет (r, g или b)
     * @param hitCount количество попаданий для текущего пикселя
     * @return смешанный цвет [0, 255]
     */
    public static int calculateBlendedColor(int currentColor, int newColor, int hitCount) {
        return Math.min(COLOR_MAX_VALUE, (currentColor * hitCount + newColor) / (hitCount + 1));
    }
}
