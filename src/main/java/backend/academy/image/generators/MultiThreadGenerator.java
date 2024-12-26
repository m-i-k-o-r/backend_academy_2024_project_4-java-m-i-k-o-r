package backend.academy.image.generators;

import backend.academy.image.FractalGenerator;
import backend.academy.image.FractalImage;
import backend.academy.model.Point;
import backend.academy.model.Rect;
import backend.academy.model.SymmetryType;
import backend.academy.transformation.AffineTransformation;
import backend.academy.transformation.Transformation;
import backend.academy.transformation.wrapper.Wrapper;
import backend.academy.util.WrapperUtils;
import java.awt.Color;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import static backend.academy.util.FractalGeneratorUtils.RendererMetrics;
import static backend.academy.util.FractalGeneratorUtils.generateSymmetricPoints;
import static backend.academy.util.FractalGeneratorUtils.renderPoint;

public class MultiThreadGenerator implements FractalGenerator {
    private static final int SEQUENTIAL_THRESHOLD = 1000;

    private final SymmetryType symmetryType;
    private final int threadCount;
    private final ForkJoinPool threadPool;

    private Point center;

    public MultiThreadGenerator(SymmetryType symmetryType, int threadCount) {
        this.symmetryType = symmetryType;
        this.threadCount = threadCount <= 0 ? Runtime.getRuntime().availableProcessors() : threadCount;
        this.threadPool = new ForkJoinPool(this.threadCount);
    }

    @Override
    public FractalImage generate(
        FractalImage canvas,
        Rect world,
        List<Wrapper<AffineTransformation>> affine,
        List<Wrapper<Transformation>> variations,
        int samples,
        int iterPerSample,
        long seed
    ) {
        Instant startTime = Instant.now();
        center = new Point(
            world.x() + world.width() / 2,
            world.y() + world.height() / 2
        );

        try {
            threadPool.invoke(new RenderTask(
                new RenderConfig(
                    canvas, world, affine, variations,
                    samples, iterPerSample,
                    0, samples, symmetryType
                )
            ));
        } finally {
            threadPool.shutdown();
        }

        Instant endTime = Instant.now();
        long totalTimeMs = Duration.between(startTime, endTime).toMillis();

        System.out.println(new RendererMetrics(
            totalTimeMs,
            threadCount,
            Runtime.getRuntime(),
            samples,
            iterPerSample,
            "Multi Thread Generator"
        ));

        return canvas;
    }

    private record RenderConfig(
        FractalImage canvas,
        Rect world,
        List<Wrapper<AffineTransformation>> affine,
        List<Wrapper<Transformation>> variations,
        int samples,
        int iterPerSample,
        int start,
        int end,
        SymmetryType symmetryType
    ) {
    }

    private class RenderTask extends RecursiveAction {
        private final RenderConfig config;

        public RenderTask(RenderConfig config) {
            this.config = config;
        }

        @Override
        protected void compute() {
            if (config.end() - config.start() <= SEQUENTIAL_THRESHOLD) {
                processSamplesSequentially();
                return;
            }

            int mid = config.start() + (config.end() - config.start()) / 2;

            invokeAll(
                new RenderTask(new RenderConfig(
                    config.canvas(), config.world(), config.affine(),
                    config.variations(), config.samples(), config.iterPerSample(),
                    config.start(), mid, config.symmetryType()
                )),
                new RenderTask(new RenderConfig(
                    config.canvas(), config.world(), config.affine(),
                    config.variations(), config.samples(), config.iterPerSample(),
                    mid, config.end(), config.symmetryType()
                ))
            );
        }

        private void processSamplesSequentially() {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            IntStream.range(config.start(), config.end()).forEach(i -> processOneSample(random));
        }

        private void processOneSample(ThreadLocalRandom random) {
            Point point = config.world().randomPoint(random);

            for (int j = 0; j < config.iterPerSample(); j++) {
                point = processOneIteration(point, random);
            }
        }

        private Point processOneIteration(Point point, ThreadLocalRandom random) {
            Wrapper<AffineTransformation> affine = WrapperUtils.choose(random, config.affine());
            point = affine.transformation().apply(point);
            Color transformColor = affine.color();

            Wrapper<Transformation> variation = WrapperUtils.choose(random, config.variations());
            point = variation.transformation().apply(point);

            renderSymmetricPoints(point, transformColor);

            return point;
        }

        private void renderSymmetricPoints(Point point, Color transformColor) {
            generateSymmetricPoints(point, center.x(), center.y(), config.symmetryType())
                .stream()
                .filter(symPoint -> config.world().contains(symPoint))
                .forEach(symPoint -> {
                    renderPoint(
                        config.canvas(),
                        config.world(),
                        symPoint,
                        transformColor
                    );
                });
        }
    }
}
