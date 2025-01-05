package backend.academy.util;

import backend.academy.model.ImageFormat;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {
    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
        DateTimeFormatter.ofPattern("dd-MMM_HH-mm-ss-SSS");

    public static String createNumberedList(List<String> items) {
        if (items.isEmpty()) {
            return "[]";
        }

        return IntStream.range(0, items.size())
            .mapToObj(i -> String.format("[%d] %s", i + 1, items.get(i)))
            .collect(Collectors.joining(", "));
    }

    public static String generateFilename(ImageFormat format) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        return String.format(
            "fractal_%s.%s",
            timestamp,
            format.name().toLowerCase()
        );
    }

    public static String getDurationTime(long duration) {
        return new DecimalFormat("### ###").format(duration);
    }
}
