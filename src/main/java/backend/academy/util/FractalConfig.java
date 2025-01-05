package backend.academy.util;

import backend.academy.effects.GammaCorrectionProcessor;
import backend.academy.effects.ImageProcessor;
import backend.academy.effects.InvertColorsProcessor;
import backend.academy.effects.LogarithmicGammaProcessor;
import backend.academy.model.ImageFormat;
import backend.academy.transformation.ExTransformation;
import backend.academy.transformation.ExponentialTransformation;
import backend.academy.transformation.HeartTransformation;
import backend.academy.transformation.JuliaTransformation;
import backend.academy.transformation.LinearTransformation;
import backend.academy.transformation.SinusoidalTransformation;
import backend.academy.transformation.SphericalTransformation;
import backend.academy.transformation.SwirlTransformation;
import backend.academy.transformation.Transformation;
import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@Getter
@UtilityClass
public class FractalConfig {
    public static final int         DEFAULT_SIZE_WINDOW = 700;

    public static final int         DEFAULT_WIDTH = 1000;
    public static final int         DEFAULT_HEIGHT = 1000;
    public static final int         DEFAULT_SAMPLES = 10;
    public static final int         DEFAULT_ITERATIONS = 1_000_000;
    public static final ImageFormat DEFAULT_FORMAT = ImageFormat.PNG;
    public static final double      DEFAULT_GAMMA_VALUE = 2.2;
    public static final double      DEFAULT_ALPHA_VALUE = 1.0;

    private static final String NAME_COLOR_BLACK =  "Черный";
    private static final String NAME_COLOR_WHITE =  "Белый";
    private static final String NAME_COLOR_GRAY =   "Серый";
    private static final String NAME_COLOR_RED =    "Красный";
    private static final String NAME_COLOR_YELLOW = "Желтый";
    private static final String NAME_COLOR_GREEN =  "Зеленый";
    private static final String NAME_COLOR_BLUE =   "Синий";

    public static final Map<Color, String> DEFAULT_PALETTE_COLORS = Stream.of(
        Map.entry(Color.BLACK,  NAME_COLOR_BLACK),
        Map.entry(Color.GRAY,   NAME_COLOR_GRAY),
        Map.entry(Color.WHITE,  NAME_COLOR_WHITE),
        Map.entry(Color.RED,    NAME_COLOR_RED),
        Map.entry(Color.YELLOW, NAME_COLOR_YELLOW),
        Map.entry(Color.GREEN,  NAME_COLOR_GREEN),
        Map.entry(Color.BLUE,   NAME_COLOR_BLUE)
    ).collect(
        LinkedHashMap::new,
        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
        LinkedHashMap::putAll
    );

    public static final Map<Color, String> DEFAULT_BACKGROUND_COLORS = Stream.of(
        Map.entry(Color.BLACK, NAME_COLOR_BLACK),
        Map.entry(Color.WHITE, NAME_COLOR_WHITE)
    ).collect(
        LinkedHashMap::new,
        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
        LinkedHashMap::putAll
    );

    public static final List<Transformation> DEFAULT_TRANSFORMATIONS = List.of(
        new LinearTransformation(),
        new SinusoidalTransformation(),
        new JuliaTransformation(null),
        new SphericalTransformation(),
        new ExponentialTransformation(),
        new SwirlTransformation(),
        new ExTransformation(),
        new HeartTransformation()
    );

    public static final List<ImageProcessor> DEFAULT_PROCESSORS = List.of(
        new GammaCorrectionProcessor(DEFAULT_GAMMA_VALUE),
        new LogarithmicGammaProcessor(DEFAULT_ALPHA_VALUE),
        new InvertColorsProcessor()
    );
}
