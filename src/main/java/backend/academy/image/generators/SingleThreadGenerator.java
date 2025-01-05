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
import java.security.SecureRandom;
import java.util.List;
import static backend.academy.util.FractalGeneratorUtils.generateSymmetricPoints;
import static backend.academy.util.FractalGeneratorUtils.renderPoint;

public class SingleThreadGenerator implements FractalGenerator {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final SymmetryType symmetryType;

    public SingleThreadGenerator(SymmetryType symmetryType) {
        this.symmetryType = symmetryType;
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
        SECURE_RANDOM.setSeed(seed);

        Point center = new Point(
            world.x() + world.width() / 2,
            world.y() + world.height() / 2
        );

        for (int i = 0; i < samples; i++) {
            Point point = world.randomPoint(SECURE_RANDOM);

            for (int j = 0; j < iterPerSample; j++) {
                Wrapper<AffineTransformation> a = WrapperUtils.choose(SECURE_RANDOM, affine);
                point = a.transformation().apply(point);
                Color transformColor = a.color();

                Wrapper<Transformation> variation = WrapperUtils.choose(SECURE_RANDOM, variations);
                point = variation.transformation().apply(point);

                List<Point> symmetricPoints = generateSymmetricPoints(point, center.x(), center.y(), symmetryType);
                for (Point symPoint : symmetricPoints) {
                    if (world.contains(symPoint)) {
                        renderPoint(canvas, world, symPoint, transformColor);
                    }
                }
            }
        }
        return canvas;
    }
}
