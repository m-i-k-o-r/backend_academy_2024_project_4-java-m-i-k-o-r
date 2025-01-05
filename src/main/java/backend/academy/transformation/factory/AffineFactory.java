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
    private final List<Color> paletteColors;

    public AffineFactory(Random random, List<Color> paletteColors) {
        this.random = random;
        this.paletteColors = paletteColors;
    }

    public AffineFactory(Random random) {
        this.random = random;
        this.paletteColors = new ArrayList<>();
    }

    public List<Wrapper<AffineTransformation>> wrap(List<AffineTransformation> transformations) {
        List<Wrapper<AffineTransformation>> wrappers = new ArrayList<>(transformations.size());
        for (AffineTransformation transformation : transformations) {
            double weight = random.nextDouble();

            Color color;
            if (paletteColors.isEmpty()) {
                color = generateColor(random);
            } else {
                Color baseColor = paletteColors.get(random.nextInt(paletteColors.size()));
                color = generateColor(random, baseColor);
            }
            wrappers.add(new Wrapper<>(transformation, weight, color));
        }
        return WrapperUtils.normalize(wrappers);
    }
}
