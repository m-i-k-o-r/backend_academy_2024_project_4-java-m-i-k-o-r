package backend.academy;

import backend.academy.effects.ImageProcessor;
import backend.academy.effects.NormalizeProcessor;
import backend.academy.image.FractalGenerator;
import backend.academy.image.FractalImage;
import backend.academy.image.generators.MultiThreadGenerator;
import backend.academy.image.generators.SingleThreadGenerator;
import backend.academy.model.FractalGenerationContext;
import backend.academy.model.Metrics;
import backend.academy.model.Rect;
import backend.academy.saver.BMPImageSaver;
import backend.academy.saver.ImageSaver;
import backend.academy.saver.JPEGImageSaver;
import backend.academy.saver.PNGImageSaver;
import backend.academy.transformation.AffineTransformation;
import backend.academy.transformation.Transformation;
import backend.academy.transformation.factory.AffineFactory;
import backend.academy.transformation.factory.VariationFactory;
import backend.academy.transformation.generator.AffineGenerator;
import backend.academy.transformation.wrapper.Wrapper;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import static backend.academy.util.StringUtils.generateFilename;
import static backend.academy.util.StringUtils.getDurationTime;

/**
 * Сервис для генерации фракталов
 * <br>
 * Выполняет основную работу по генерации, обработке и сохранению фрактала
 */
public class FractalService {
    private static final Rect WORLD = new Rect(-1, -1, 2, 2);

    /**
     * Генерирует фрактал на основе переданных параметров
     *
     * @param context параметры {@link FractalGenerationContext}
     * @param textGUI граф интерфейс для отображения уведомлений
     * @throws Exception ошибка во время генерации или сохранения
     */
    public void generate(
        FractalGenerationContext context,
        WindowBasedTextGUI textGUI
    ) throws Exception {
        Random random = createSecureRandom(context.parameters().seed());

        FractalImage canvas = FractalImage.create(
            context.parameters().width(),
            context.parameters().height()
        );
        List<Wrapper<AffineTransformation>> affineTransforms = generateAffineTransforms(random, context);
        List<Wrapper<Transformation>> variations = getVariations(random, context);

        Instant startTime = Instant.now();

        FractalImage fractal = renderFractal(canvas, affineTransforms, variations, context);
        processFractalImage(canvas, context);
        String filename = saveFractal(fractal, context);

        Instant endTime = Instant.now();
        long totalTimeMs = Duration.between(startTime, endTime).toMillis();

        notifyAboutCompletionInWindow(textGUI, filename, totalTimeMs);
        notifyAboutCompletionInConsole(filename, totalTimeMs, context);
    }

    private SecureRandom createSecureRandom(long seed) throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(seed);
        return secureRandom;
    }

    /**
     * Создает список преобразований {@link AffineTransformation}
     *
     * @param random ну {@link Random}
     * @param context параметры генерации фрактала {@link FractalGenerationContext}
     * @return список преобразований, завернутых в {@link Wrapper}
     */
    private List<Wrapper<AffineTransformation>> generateAffineTransforms(
        Random random,
        FractalGenerationContext context
    ) {
        return new AffineFactory(random, context.paletteColors()).wrap(
            new AffineGenerator(random).generate(context.parameters().samples())
        );
    }

    /**
     * Получает список вариаций трансформаций
     *
     * @param random ну {@link Random}
     * @param context параметры генерации фрактала {@link FractalGenerationContext}
     * @return список трансформаций, завернутых в {@link Wrapper}.
     * @throws IllegalArgumentException если переданный список трансформаций пуст
     */
    private List<Wrapper<Transformation>> getVariations(
        Random random,
        FractalGenerationContext context
    ) {
        List<Transformation> variations = context.variations();
        if (variations.isEmpty()) {
            throw new IllegalArgumentException("Выберите хотя бы одну трансформацию");
        }
        return new VariationFactory(random).wrap(variations);
    }

    /**
     * Рендерит изображение фрактала
     *
     * @param canvas холст
     * @param affineTransforms список аффинных преобразований
     * @param variations список трансформаций
     * @param context параметры генерации фрактала
     * @return сгенерированное изображение фрактала {@link FractalImage}.
     */
    private FractalImage renderFractal(
        FractalImage canvas,
        List<Wrapper<AffineTransformation>> affineTransforms,
        List<Wrapper<Transformation>> variations,
        FractalGenerationContext context
    ) {
        FractalGenerator generator = context.thread().isMultithreading()
            ? new MultiThreadGenerator(context.symmetry(), context.thread().threadCount())
            : new SingleThreadGenerator(context.symmetry());

        return generator.generate(
            canvas,
            WORLD,
            affineTransforms,
            variations,
            context.parameters().samples(),
            context.parameters().iterations(),
            context.parameters().seed()
        );
    }

    /**
     * Обрабатывает изображение фрактала
     *
     * @param canvas холст
     * @param context параметры генерации фрактала
     */
    private void processFractalImage(FractalImage canvas, FractalGenerationContext context) {
        new NormalizeProcessor().process(canvas);

        List<ImageProcessor> processors = context.processors();
        for (ImageProcessor processor : processors) {
            processor.process(canvas);
        }
    }

    /**
     * Сохраняет изображение фрактала
     *
     * @param fractal изображение
     * @param context параметры генерации фрактала
     * @return имя сохраненного файла
     * @throws Exception ошибка во время сохранения
     */
    private String saveFractal(
        FractalImage fractal,
        FractalGenerationContext context
    ) throws Exception {
        String filename = generateFilename(context.format());

        ImageSaver imageSaver = switch (context.format()) {
            case PNG -> new PNGImageSaver(context.thread().threadCount());
            case JPEG -> new JPEGImageSaver(context.thread().threadCount());
            case BMP -> new BMPImageSaver(context.thread().threadCount());
        };
        imageSaver.save(fractal, context.backgroundColor(), filename);

        return filename;
    }

    /**
     * Показывает сообщение о завершении генерации в графическом интерфейсе
     *
     * @param textGUI интерфейс для отображения сообщений
     * @param filename имя сохраненного файла
     * @param totalTimeMs время выполнения (мс)
     */
    private void notifyAboutCompletionInWindow(WindowBasedTextGUI textGUI, String filename, long totalTimeMs) {
        new MessageDialogBuilder()
            .setTitle("Генерация завершена")
            .setText(
                "Фрактал успешно сгенерирован за "
                    + getDurationTime(totalTimeMs)
                    + " ms\nФайл сохранен: "
                    + filename
            ).build()
            .showDialog(textGUI);
    }

    /**
     * Выводит сообщение о завершении генерации в консоль
     *
     * @param filename имя сохраненного файла
     * @param totalTimeMs время выполнения (мс)
     * @param context параметры генерации фрактала
     */
    private void notifyAboutCompletionInConsole(String filename, long totalTimeMs, FractalGenerationContext context) {
        new Metrics(
            filename,
            totalTimeMs,
            !context.thread().isMultithreading()
                ? 1
                : context.thread().threadCount() > 0
                    ? context.thread().threadCount()
                    : Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime(),
            context.parameters().samples(),
            context.parameters().iterations(),
            !context.thread().isMultithreading()
                ? "Single Thread Generator"
                : "Multi Thread Generator",
            context.parameters().seed(),
            context.format(),
            context.symmetry(),
            context.variations(),
            context.processors(),
            context.backgroundColor(),
            context.paletteColors()
        ).print();
    }
}
