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
        List<AffineTransformation> affineTransforms = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            affineTransforms.add(generateAffineTransformation());
        }
        return affineTransforms;
    }

    private AffineTransformation generateAffineTransformation() {
        double a, b, c, d, e, f;
        do {
            a = random.nextDouble() * 2 - 1;   // [-1, 1]
            b = random.nextDouble() * 2 - 1;   // [-1, 1]
            c = random.nextDouble() * 5 - 2.5; // [-2.5, 2.5]
            d = random.nextDouble() * 2 - 1;   // [-1, 1]
            e = random.nextDouble() * 2 - 1;   // [-1, 1]
            f = random.nextDouble() * 5 - 2.5; // [-2.5, 2.5]
        } while (!isValidCoefficients(a, b, d, e));

        return new AffineTransformation(a, b, c, d, e, f);
    }

    private static boolean isValidCoefficients(double a, double b, double d, double e) {
        return (a * a + d * d < 1)
            && (b * b + e * e < 1)
            && (a * a + b * b + d * d + e * e < 1 + Math.pow(a * e - b * d, 2));
    }
}
