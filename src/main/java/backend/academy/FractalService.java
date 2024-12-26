package backend.academy;

import backend.academy.effects.ImageProcessor;
import backend.academy.effects.NormalizeProcessor;
import backend.academy.image.FractalGenerator;
import backend.academy.image.FractalImage;
import backend.academy.image.ImageUtils;
import backend.academy.image.generators.MultiThreadGenerator;
import backend.academy.image.generators.SingleThreadGenerator;
import backend.academy.model.Rect;
import backend.academy.transformation.AffineTransformation;
import backend.academy.transformation.Transformation;
import backend.academy.transformation.factory.AffineFactory;
import backend.academy.transformation.factory.VariationFactory;
import backend.academy.transformation.generator.AffineGenerator;
import backend.academy.transformation.wrapper.Wrapper;
import backend.academy.ui.ParameterPicker;
import backend.academy.ui.ThreadPicker;
import backend.academy.ui.UiComponent;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import java.awt.Color;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import static backend.academy.util.StringUtils.generateFilename;
import static backend.academy.util.StringUtils.getDurationTime;

public class FractalService {
    private static final Rect WORLD = new Rect(-1, -1, 2, 2);

    public void generate(
        UiComponent<ParameterPicker.Parameters> parameterPicker,
        UiComponent<List<Transformation>> variationsPicker,
        UiComponent<List<ImageProcessor>> processorPicker,
        UiComponent<Color> backgroundColorPicker,
        UiComponent<List<Color>> paletteColorPicker,
        UiComponent<ThreadPicker.Type> threadPicker,
        WindowBasedTextGUI textGUI
    ) throws Exception {
        ParameterPicker.Parameters params = parameterPicker.getSelected();
        ThreadPicker.Type type = threadPicker.getSelected();
        Random random = createSecureRandom(params.seed());

        FractalImage canvas = FractalImage.create(params.width(), params.height());
        List<Color> palette = paletteColorPicker.getSelected();
        List<Wrapper<AffineTransformation>> affineTransforms = generateAffineTransforms(random, palette, params);

        List<Wrapper<Transformation>> variations = getVariations(random, variationsPicker);

        long startTime = System.nanoTime();

        FractalImage fractal = renderFractal(canvas, affineTransforms, variations, params, type);

        processFractalImage(canvas, processorPicker.getSelected());

        String filename = saveFractal(fractal, backgroundColorPicker.getSelected(), params);

        long endTime = System.nanoTime();
        String durationTime = getDurationTime(startTime, endTime);
        notifyAboutCompletion(textGUI, filename, durationTime);
    }

    private SecureRandom createSecureRandom(long seed) throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(seed);
        return secureRandom;
    }

    private List<Wrapper<AffineTransformation>> generateAffineTransforms(
        Random random,
        List<Color> palette,
        ParameterPicker.Parameters params
    ) {
        return new AffineFactory(random, palette).wrap(
            new AffineGenerator(random).generate(params.samples())
        );
    }

    private List<Wrapper<Transformation>> getVariations(
        Random random,
        UiComponent<List<Transformation>> variationsPicker
    ) {
        List<Transformation> selectedVariations = variationsPicker.getSelected();
        if (selectedVariations.isEmpty()) {
            throw new IllegalArgumentException("Выберите хотя бы одну трансформацию");
        }
        return new VariationFactory(random).wrap(selectedVariations);
    }

    private FractalImage renderFractal(
        FractalImage canvas,
        List<Wrapper<AffineTransformation>> affineTransforms,
        List<Wrapper<Transformation>> variations,
        ParameterPicker.Parameters params,
        ThreadPicker.Type type
    ) {
        FractalGenerator generator = type.isMultithreading()
            ? new MultiThreadGenerator(params.symmetry(), type.threadCount())
            : new SingleThreadGenerator(params.symmetry());

        return generator.generate(
            canvas,
            WORLD,
            affineTransforms,
            variations,
            params.samples(),
            params.iterations(),
            params.seed()
        );
    }

    private void processFractalImage(FractalImage canvas, List<ImageProcessor> processors) {
        new NormalizeProcessor().process(canvas);
        for (ImageProcessor processor : processors) {
            processor.process(canvas);
        }
    }

    private String saveFractal(
        FractalImage fractal,
        Color backgroundColor,
        ParameterPicker.Parameters params
    ) throws Exception {
        String filename = generateFilename(params.format());
        ImageUtils.save(fractal, backgroundColor, filename, params.format());
        return filename;
    }

    private void notifyAboutCompletion(WindowBasedTextGUI textGUI, String filename, String durationTime) {
        new MessageDialogBuilder()
            .setTitle("Генерация завершена")
            .setText("Фрактал успешно сгенерирован за " + durationTime + " ms\nФайл сохранен: " + filename)
            .build()
            .showDialog(textGUI);
    }
}
