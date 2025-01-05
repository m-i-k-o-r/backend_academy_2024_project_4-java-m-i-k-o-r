package backend.academy.transformation.generator;

import backend.academy.transformation.AffineTransformation;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AffineGenerator {
    private Random random;

    public List<AffineTransformation> generate(int iterations) {
        List<AffineTransformation> affineTransforms = new ArrayList<>(iterations);
        for (int i = 0; i < iterations; i++) {
            affineTransforms.add(generateAffineTransformation());
        }
        return affineTransforms;
    }

    private static final double COEFFICIENT_RANGE_MIN = -1.0;
    private static final double COEFFICIENT_RANGE_MAX = 1.0;
    private static final double TRANSLATION_RANGE_MIN = -2.5;
    private static final double TRANSLATION_RANGE_MAX = 2.5;

    private AffineTransformation generateAffineTransformation() {
        double a;
        double b;
        double c;
        double d;
        double e;
        double f;

        do {
            // [-1, 1]
            a = random.nextDouble() * (COEFFICIENT_RANGE_MAX - COEFFICIENT_RANGE_MIN) + COEFFICIENT_RANGE_MIN;

            // [-1, 1]
            b = random.nextDouble() * (COEFFICIENT_RANGE_MAX - COEFFICIENT_RANGE_MIN) + COEFFICIENT_RANGE_MIN;

            // [-2.5, 2.5]
            c = random.nextDouble() * (TRANSLATION_RANGE_MAX - TRANSLATION_RANGE_MIN) + TRANSLATION_RANGE_MIN;

            // [-1, 1]
            d = random.nextDouble() * (COEFFICIENT_RANGE_MAX - COEFFICIENT_RANGE_MIN) + COEFFICIENT_RANGE_MIN;

            // [-1, 1]
            e = random.nextDouble() * (COEFFICIENT_RANGE_MAX - COEFFICIENT_RANGE_MIN) + COEFFICIENT_RANGE_MIN;

            // [-2.5, 2.5]
            f = random.nextDouble() * (TRANSLATION_RANGE_MAX - TRANSLATION_RANGE_MIN) + TRANSLATION_RANGE_MIN;
        } while (!isValidCoefficients(a, b, d, e));

        return new AffineTransformation(a, b, c, d, e, f);
    }

    private static boolean isValidCoefficients(double a, double b, double d, double e) {
        return (a * a + d * d < 1)
            && (b * b + e * e < 1)
            && (a * a + b * b + d * d + e * e < 1 + Math.pow(a * e - b * d, 2));
    }
}
