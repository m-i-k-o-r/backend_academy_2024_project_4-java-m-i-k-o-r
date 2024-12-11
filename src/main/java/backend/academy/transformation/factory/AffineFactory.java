package backend.academy.transformation.factory;

import backend.academy.transformation.AffineTransformation;
import backend.academy.transformation.wrapper.Wrapper;
import backend.academy.util.WrapperUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static backend.academy.util.ColorUtils.generateColor;

public class AffineFactory implements TransformationFactory<AffineTransformation> {
    private final Random random;

    public AffineFactory(Random random) {
        this.random = random;
    }

    public List<Wrapper<AffineTransformation>> wrap(List<AffineTransformation> transformations) {
        List<Wrapper<AffineTransformation>> wrappers = new ArrayList<>();
        for (AffineTransformation transformation : transformations) {
            double weight = random.nextDouble();
            Color color = generateColor(random);
            wrappers.add(new Wrapper<>(transformation, weight, color));
        }
        return WrapperUtils.normalize(wrappers);
    }
}
