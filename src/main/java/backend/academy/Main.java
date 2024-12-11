package backend.academy;

import backend.academy.effects.NormalizeProcessor;
import backend.academy.effects.GammaCorrectionProcessor;
import backend.academy.effects.InvertColorsProcessor;
import backend.academy.model.Rect;
import backend.academy.transformation.AffineTransformation;
import backend.academy.transformation.ExponentialTransformation;
import backend.academy.transformation.JuliaTransformation;
import backend.academy.transformation.Transformation;
import backend.academy.transformation.factory.AffineFactory;
import backend.academy.transformation.factory.VariationFactory;
import backend.academy.transformation.generator.AffineGenerator;
import backend.academy.transformation.wrapper.Wrapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Main {
    public static void main(String[] args) throws Exception {
        int width = 800, height = 800, samples = 10, iterations = 1_000_000;
        long seed = 1039847652847L;

        FractalImage canvas = FractalImage.create(width, height);
        Rect world = new Rect(-1, -1, 2, 2);

        Random random = new Random(seed);

        List<Wrapper<Transformation>> variations =
            new VariationFactory(random).wrap(
                List.of(
                    new JuliaTransformation(random),
                    new ExponentialTransformation()
                )
            );

        List<Wrapper<AffineTransformation>> affineTransforms =
            new AffineFactory(random).wrap(
                new AffineGenerator(random).generate(100)
            );

        FractalRenderer renderer = new FractalRenderer();
        canvas = renderer.render(canvas, world, affineTransforms, variations, samples, iterations, seed);

        new NormalizeProcessor().process(canvas);

        new InvertColorsProcessor().process(canvas);
        new GammaCorrectionProcessor(3).process(canvas);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM_HH-mm-ss-SSS"));
        ImageUtils.save(canvas, "fractal_" + timestamp + ".png", "PNG");
    }
}
