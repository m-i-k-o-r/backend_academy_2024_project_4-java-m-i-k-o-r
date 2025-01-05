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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.awt.Color;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import static backend.academy.util.FractalGeneratorUtils.generateSymmetricPoints;
import static backend.academy.util.FractalGeneratorUtils.renderPoint;

public class MultiThreadGenerator implements FractalGenerator {
    private static final int SEQUENTIAL_THRESHOLD = 1000;

    private final SymmetryType symmetryType;
    private final ForkJoinPool threadPool;

    private Point center;

    public MultiThreadGenerator(SymmetryType symmetryType, int threadCount) {
        this.symmetryType = symmetryType;
        this.threadPool = new ForkJoinPool(
            threadCount <= 0
                ? Runtime.getRuntime().availableProcessors()
                : threadCount
        );
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
        return canvas;
    }

    @SuppressWarnings("RecordComponentNumber")
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

        private RenderTask(RenderConfig config) {
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

        // PREDICTABLE_RANDOM
        @SuppressFBWarnings
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
            Point currentPoint = point;

            Wrapper<AffineTransformation> affine = WrapperUtils.choose(random, config.affine());
            currentPoint = affine.transformation().apply(currentPoint);

            Color transformColor = affine.color();

            Wrapper<Transformation> variation = WrapperUtils.choose(random, config.variations());
            currentPoint = variation.transformation().apply(currentPoint);

            renderSymmetricPoints(currentPoint, transformColor);

            return currentPoint;
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
