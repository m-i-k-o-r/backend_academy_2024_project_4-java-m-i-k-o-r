package backend.academy.model;

import backend.academy.effects.ImageProcessor;
import backend.academy.transformation.Transformation;
import java.awt.Color;
import java.util.List;
import static backend.academy.util.FractalConfig.DEFAULT_BACKGROUND_COLORS;
import static backend.academy.util.FractalConfig.DEFAULT_PALETTE_COLORS;
import static backend.academy.util.StringUtils.getDurationTime;

@SuppressWarnings("RecordComponentNumber")
public record Metrics(
    String nameFile,
    long totalTimeMs,
    int threadCount,
    Runtime runtime,
    int samples,
    int iterPerSample,
    String generatorType,
    long seed,
    ImageFormat imageFormat,
    SymmetryType symmetryType,
    List<Transformation> transformations,
    List<ImageProcessor> postProcessors,
    Color backgroundColor,
    List<Color> paletteColors
) {
    private static final int BYTES_IN_MB = 1024 * 1024;

    private static final String EMPTY_STRING = " - ";
    private static final String PATTERN_LIST_ELEMENT = "│     [%d] %s";

    @SuppressWarnings("RegexpSinglelineJava")
    public void print() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        String upperLine =  "╒═══════════════════════════════════════════════════════════════╕";
        String middleLine = "╞══════════════════────────────────┄┄┄┄┄┄┄┄─────┄─┄┄┄┄ ┄┄  ┄   ┄ ";
        String footerLine = "╘═══════════════════════════════════════════════════════════════╛";

        StringBuilder sb = new StringBuilder();

        sb.append(upperLine).append('\n');
        sb.append("│ Метрики генерации фрактала: ").append(nameFile).append('\n');
        sb.append(middleLine).append('\n');

        sb.append(String.format("""
                │ Системная конфигурация:
                │     Доступно потоков: %d
                │     Максимальная память: %d МБ
                │     Свободная память: %d МБ
                │     Всего памяти: %d МБ
                │
                │ Детали выполнения:
                │     Количество потоков: %d
                │     Количество выборок (samples): %d
                │     Итераций на выборку: %d
                │     Общее время выполнения: %s мс
                │     Среднее время на выборку: %.2f мс
                """,
            runtime.availableProcessors(),
            runtime.maxMemory() / BYTES_IN_MB,
            runtime.freeMemory() / BYTES_IN_MB,
            runtime.totalMemory() / BYTES_IN_MB,
            threadCount,
            samples,
            iterPerSample,
            getDurationTime(totalTimeMs),
            (double) totalTimeMs / samples
        ));

        sb.append(middleLine).append('\n');

        sb.append(String.format("""
                │ Параметры генерации:
                │
                │ Seed: %d
                │ Формат изображения: %s
                │ Симметрия: %s
                │ Преобразования:
                %s
                │ Пост-обработчики:
                %s
                │ Цвет фона: %s
                │ Цвета палитры:
                %s
                """,
            seed,
            imageFormat,
            symmetryType != null ? symmetryType.description() : EMPTY_STRING,
            formatList(transformations.stream().map(Transformation::name).toList()),
            formatList(postProcessors.stream().map(ImageProcessor::name).toList()),
            formatColor(backgroundColor),
            formatColor(paletteColors)
        ));

        sb.append(footerLine).append('\n');
        return sb.toString();
    }

    private static <T> String formatList(List<T> list) {
        if (list.isEmpty()) {
            return "│     - Нет элементов";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(String.format(
                PATTERN_LIST_ELEMENT,
                i + 1,
                list.get(i).toString()
            ));

            if (i < list.size() - 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    private static String formatColor(Color color) {
        String nameColor = DEFAULT_BACKGROUND_COLORS.get(color);
        return nameColor != null
            ? "│     " + nameColor
            : EMPTY_STRING;
    }

    private static String formatColor(List<Color> colors) {
        if (colors.isEmpty()) {
            return "│     - Нет заданных цветов";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < colors.size(); i++) {
            String colorName = DEFAULT_PALETTE_COLORS.get(colors.get(i));
            sb.append(String.format(
                PATTERN_LIST_ELEMENT,
                i + 1,
                colorName != null
                    ? colorName
                    : "Неизвестный цвет"
            ));

            if (i < colors.size() - 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }
}
